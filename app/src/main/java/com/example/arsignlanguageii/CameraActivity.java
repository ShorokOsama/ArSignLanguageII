package com.example.arsignlanguageii;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import net.jpountz.lz4.LZ4Compressor;
import net.jpountz.lz4.LZ4Factory;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.arsignlanguageii.ml.ArabicAlphabetSL;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvException;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.Base64;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CameraActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {

    JavaCameraView javaCameraView;
    private Mat mRgba;
    TextView output, serverStatus, expLetter;
    ToggleButton toggle;
    Button speechBtn, clearBtn;
    boolean alpha = false;

    // Alphabet Variables
    int x = 0;
    int SIZE = 200, averageSteps = 20, nClasses = 28, cycle = 0;
    float threshold = 0.50f;
    String expectedLetter, confidence, currentText;
    float sum;
    float[] modelOutput;
    // Rolling Average
    float[][] predictions = new float[nClasses][averageSteps];
    float[] average = new float[nClasses];


    // Words Variables
    boolean serverRunning = false;
    int y = 0;
    Bitmap[] vid = new Bitmap[60];
    Bitmap aoi, finalFrame;
    int V_SIZE = 224;
    int FRAMES_NO = 60;
    int wordIndex;

//    private final String URL = "http://192.168.1.2:5000";
    private String URL;
    private String msg = "None";
    private String responseString, clss;
    private OkHttpClient okHttpClient;
    private String encodedVid = "[";
    private TextToSpeech mTTS;

    byte[] frame, frame_compressed, sizeData, finalData;
    LZ4Factory factory;
    LZ4Compressor compressor;


    String[] ar_alphabet = {"ا", "ب", "ت", "ث", "ج", "ح","خ", "د","ذ", "ر", "ز", "س",
            "ش", "ص","ض", "ط", "ظ","ع", "غ", "ف", "ق","ك","ل", "م","ن", "ه", "و","ي"};

    String[] words = ListActivity.words;

    String[] arWords = ListActivity.arWords;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        Log.d("APII-category", String.valueOf(ListActivity.cat));

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1);

        javaCameraView = findViewById(R.id.javaCamView);

        javaCameraView.setCameraIndex(1);
        javaCameraView.enableView();
        javaCameraView.setCvCameraViewListener(this);

        expLetter = findViewById(R.id.exp_letter);
        output = findViewById(R.id.text_output);
        serverStatus = findViewById(R.id.server_status);

        URL = setURL();

        clearBtn = findViewById(R.id.back_btn);
        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                output.setText("_");
            }
        });

        speechBtn = findViewById(R.id.speech_btn);
        speechBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = output.getText().toString().substring(0, output.getText().length()-1);
                Log.d("TextToSpeech", text);
                mTTS.speak(text, TextToSpeech.QUEUE_FLUSH, null, "id");
                output.setText("_");
            }
        });

        toggle = findViewById(R.id.toggleButton);
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    alpha = true;
                    output.setText("_");
                    expLetter.setText("_");

                } else {
                    alpha = false;
                }
            }
        });


        // creating a client
        okHttpClient = new OkHttpClient();

        // building a request
        Request request = new Request.Builder().url(URL).build();

        // making call asynchronously
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            // called if server is unreachable
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("APII", e.getMessage());
                        Toast.makeText(CameraActivity.this, "server down", Toast.LENGTH_SHORT).show();
                        serverStatus.setText("error connecting to the server");
                    }
                });
            }

            @Override
            // called if we get a
            // response from the server
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                serverRunning = true;
                responseString = response.body().string();
                Log.d("FlaskAPI", responseString);
                parseJson(responseString);
                Log.d("FlaskAPI", msg);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        serverStatus.setText(msg);
//                        Log.d("FlaskAPII", msg);
                        serverStatus.setText("متصل..");
                    }
                });
            }
        });


        mTTS = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status == TextToSpeech.SUCCESS) {
                    int result = mTTS.setLanguage(Locale.forLanguageTag("ar"));
                    if (result == TextToSpeech.LANG_NOT_SUPPORTED
                            || result == TextToSpeech.LANG_MISSING_DATA) {
                        Log.e("TTS", "language not supported");
                    } else {
                        speechBtn.setEnabled(true);
                    }
                } else{
                    Log.e("TTS", "Iniliazation failed");
                }

                Log.d("TextToSpeech arabic", mTTS.isLanguageAvailable(Locale.forLanguageTag("ar"))+"");

            }
        });

        //----------------------Compression----------------------
        factory = LZ4Factory.fastestInstance();
        compressor = factory.fastCompressor();



    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        mRgba = new Mat();
    }

    @Override
    public void onCameraViewStopped() {
        mRgba.release();
    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        mRgba = inputFrame.rgba();
        Core.flip(mRgba, mRgba, 1);

        if(!alpha){
            // Words Recognition
            if(serverRunning){
                aoi = convertMatToBitMap(mRgba);
                vid[y] = Bitmap.createScaledBitmap(aoi, V_SIZE, V_SIZE, true);
                finalFrame = Bitmap.createScaledBitmap(aoi, V_SIZE, V_SIZE, true);


                y++;
                y = y % FRAMES_NO;

                //----------------------Compression----------------------
                frame = convertBitmapToByteArray(finalFrame);
                //Log.d("APII - f size before", frame.length+"");
                sizeData = ByteBuffer.allocate(4).putInt(frame.length).array();
                frame_compressed = compressor.compress(frame);

                finalData = new byte[frame_compressed.length + sizeData.length];
                System.arraycopy(sizeData, 0, finalData, 0, sizeData.length);
                System.arraycopy(frame_compressed, 0, finalData, sizeData.length, frame_compressed.length);


                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    String encodedFrameString = Base64.getEncoder().encodeToString(finalData);
                    encodedVid += "\"" + encodedFrameString + "\",";
                }


                if (y == 0) {

                    long allStartTime = System.currentTimeMillis();

                    encodedVid = encodedVid.substring(0, encodedVid.length() - 1);
                    encodedVid += "]";

                    Log.d("APII", encodedVid);

                    String json = String.format("{\"frames\":%s}", encodedVid);
                    Log.d("StringSize-APII", json.getBytes().length/1024.0/1024.0+"");
                    encodedVid = "[";


                    RequestBody body = RequestBody.create(
                            MediaType.parse("application/json"), json);

                    Request request = new Request.Builder()
                            .url(URL)
                            .post(body)
                            .build();

                    Call call = okHttpClient.newCall(request);

                    try {
                        long startTime = System.currentTimeMillis();
                        Response predResponse = call.execute();
                        long estimatedTime = System.currentTimeMillis() - startTime;
                        Log.d("APII - TIMEEexecution", (estimatedTime/1000)+" seconds");

                        Log.d("APII code", predResponse.code() + "");

                        String predResponseString = predResponse.body().string();
                        clss = parseJsonPrediction(predResponseString);
                        Log.d("APII-resonse", predResponseString);

                    } catch (IOException e) {

                        Log.d("APII-resonse", "no response");
                        e.printStackTrace();
                    }

                    wordIndex = Arrays.asList(words).indexOf(clss);


                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            currentText = output.getText().toString().substring(0, output.getText().length()-1);
                            if(wordIndex>=0){
                                output.setText( currentText + arWords[wordIndex] + " _");
                                mTTS.speak(arWords[wordIndex],TextToSpeech.QUEUE_FLUSH, null, "id");
                            }

//                            expLetter.setText("_");
//                        output.setText(arWords[wordIndex]);
//                        Log.d("TextToSpeech", clss+"");

                        }
                    });

                    long allEstimatedTime = System.currentTimeMillis() - allStartTime;
                    Log.d("TIMEE - all execution", (allEstimatedTime/1000)+" seconds");

                }
            }


        }else{
            // Alphabet Recognition

            x++;
            x = x % 30;
            Rect rect = new Rect(100, 150, 600, 600);
            Mat cropped = new Mat(mRgba, rect);
            Bitmap aoi = convertMatToBitMap(cropped);

            String[] expected = getExpectedLetter(aoi);

            //  average prediction

            float[] avMax = getMax(average);
            String avExpectedLetter = ar_alphabet[(int)avMax[0]];
            String avConfidence = avMax[1]+"";

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    expLetter.setText(String.format("%s  %s \t %s  %s",expected[0],expected[1], avExpectedLetter, avConfidence));
                }
            });

            Imgproc.rectangle(mRgba, new Point(rect.x, rect.y),
                    new Point(rect.x + rect.width, rect.y + rect.height),
                    new Scalar(0,0,0), 5);


            if(Float.parseFloat(avConfidence) > threshold && x == 0){
                currentText = output.getText().toString().substring(0, output.getText().length()-1);

                output.setText( currentText + avExpectedLetter + "_");

            }

        }

        return mRgba;
    }

    float[] getMax(float[] arr){
        int max = 0;
        for(int i = 0; i < arr.length; i++){
            if(arr[i] > arr[max]) max = i;
        }
        return new float []{max, Float.parseFloat(String.format("%.2f", arr[max]))};
    }


    public String[] getExpectedLetter(Bitmap img){
        try {

            Locale.setDefault(Locale.ENGLISH);

            ArabicAlphabetSL model = ArabicAlphabetSL.newInstance(this);

            // Creates inputs for reference.
            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, SIZE, SIZE, 3}, DataType.FLOAT32);

            Bitmap input=Bitmap.createScaledBitmap(img,SIZE,SIZE,true);

            ByteBuffer byteBuffer = imgToByteBuffer(input);


            inputFeature0.loadBuffer(byteBuffer);

            // Runs model inference and gets result.
            ArabicAlphabetSL.Outputs outputs = model.process(inputFeature0);
            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

            modelOutput =  outputFeature0.getFloatArray();
            float[] max = getMax(modelOutput);
            expectedLetter = ar_alphabet[(int)max[0]];
            confidence = max[1]+"";

            // Releases model resources if no longer used.
            model.close();
        } catch (IOException e) {
            Log.d("ModelException", e.getMessage());
        }



        // rolling prediction update
        for(int i = 0; i < nClasses; i++){
            predictions[i][cycle] = modelOutput[i];
            sum = 0;
            for(int m = 0; m < predictions[i].length; m++) sum += predictions[i][m];
            average[i] = sum/averageSteps;
        }
        cycle = (cycle+1) % averageSteps;

//        Log.d("AverageArray", Arrays.toString(average));

        return new String[]{expectedLetter, confidence};
    }

    public String parseJsonPrediction(String jsonResponse) {
        String predictedClass = "Prediction Failed";
        try {
            JSONObject jsonObject = new JSONObject(jsonResponse);
            predictedClass = jsonObject.getString("class_name");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return predictedClass;
    }

    public void parseJson(String jsonResponse) {
        try {
            JSONObject jsonObject = new JSONObject(jsonResponse);
            msg = jsonObject.getString("message");
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private static Bitmap convertMatToBitMap(Mat input){
        Bitmap bmp = null;
        Mat rgb = new Mat();
        Imgproc.cvtColor(input, rgb, Imgproc.COLOR_BGR2RGB);

        try {
            bmp = Bitmap.createBitmap(rgb.cols(), rgb.rows(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(rgb, bmp);
        }
        catch (CvException e){
            Log.d("Exception",e.getMessage());
        }
        return bmp;
    }


    private ByteBuffer imgToByteBuffer(Bitmap image) {
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect( 4 * SIZE * SIZE * 3);
        byteBuffer.order(ByteOrder.nativeOrder());

        int[] intValues = new int[SIZE * SIZE];
        image.getPixels(intValues, 0, image.getWidth(), 0, 0, image.getWidth(), image.getHeight());
        int pixel = 0;
        //iterate over each pixel and extract R, G, and B values. Add those values individually to the byte buffer.
        for(int i = 0; i < SIZE; i ++){
            for(int j = 0; j < SIZE; j++){
                int val = intValues[pixel++]; // RGB
                byteBuffer.putFloat(((val >> 16) & 0xFF) * (1.f / 255));
                byteBuffer.putFloat(((val >> 8) & 0xFF) * (1.f / 255));
                byteBuffer.putFloat((val & 0xFF) * (1.f / 255));
            }
        }

        return byteBuffer;
    }

    public byte[] convertBitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream(bitmap.getWidth() * bitmap.getHeight());
//        bitmap.compress(Bitmap.CompressFormat.PNG, 100, buffer);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, buffer);
        return buffer.toByteArray();
    }

    public String setURL(){
        String address = MainActivity.IPAddress;
        String url = address + "/" + String.valueOf(ListActivity.cat);

        Log.d("FlaskAPII-URL", url);
        return url;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocas) {
        super.onWindowFocusChanged(hasFocas);
        View decorView = getWindow().getDecorView();
        if (hasFocas) {
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    }
}