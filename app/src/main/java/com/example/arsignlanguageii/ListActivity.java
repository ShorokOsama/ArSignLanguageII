package com.example.arsignlanguageii;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.opencv.android.OpenCVLoader;

enum Categories {general, bank, cafe, train, hospital}

public class ListActivity extends AppCompatActivity {
    Button generalBtn,bankBtn,hospitalBtn,cafeBtn,stationBtn;
    static Categories cat;
    static String[] words, arWords;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list2);
        Log.d("OpenCVModule", "OpenCV Loading Status: " + OpenCVLoader.initDebug());

        generalBtn = findViewById(R.id.ar_general_btn);

        generalBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cat = Categories.general;
                words = new String[]{"and", "clear", "college", "communication", "computer", "deaf", "disability",
                        "easy", "goal", "hello", "help", "information", "me", "name", "question", "rehabilitation",
                        "science", "speaking", "team", "university", "we", "what", "yes", "you", "zagazig"};
                arWords = new String[]{"و", "واضح", "كليه", "تواصل", "حاسبات", "صم", "اعاقه",
                        "سهل", "هدف", "مرحبا", "مساعدة", "معلومات", "انا", "اسم", "سؤال", "تأهيل",
                        "علوم", "المتكلمين", "فريق", "جامعة", "نحن", "ماذا", "نعم", "انت", "الزقازيق"};

                Intent intent = new Intent(ListActivity.this, CameraActivity.class);
                startActivity(intent);
            }
        });
        bankBtn = findViewById(R.id.ar_bank_btn);

        bankBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cat = Categories.bank;
                words = new String[]{"Bank", "Bank manager", "Financial benefits", "Form", "How much",
                        "Id card", "Revenues", "Visa", "Withdraw money", "account", "and", "doing",
                        "expenses", "finance", "hello", "help", "loan", "money", "money transfer", "me"};
                arWords = new String[]{"بنك", "مدير البنك", "الفوائد", "استمارة", "كام",
                        "بطاقة", "إرادات", "فيزا", "اسحب", "حساب", "و", "يعمل",
                        "مصروفات", "تمويل", "مرحبا", "مساعدة", "قرض", "فلوس", "حوالة", "انا"};
                
                Intent intent = new Intent( ListActivity.this, CameraActivity.class);
                startActivity(intent);

            }
        });
        hospitalBtn = findViewById(R.id.ar_hospital_btn);
        hospitalBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cat = Categories.hospital;
                words = new String[]{"my", "name", "sick", "tired", "problem", "pressure", "headache", "gauze",
                        "illness", "eyes", "deaf", "corona", "cold", "blood", "Tell me", "Anemia",
                        "Diabetes", "How are you", "Pregnancy", "Injection", "bones", "brain", "burn", "liver",
                        "nerves"};
                arWords = new String[]{"انا", "اسم", "مريض", "تعبان", "مشكلة", "ضغط", "صداع", "شاش",
                        "مرض", "عيون", "اصم", "كورونا", "برد", "دم", "Tell قولي", "انيميا",
                        "سكر", "عامل ايه", "حمل", "حقنة", "عظام", "مخ", "حرق", "كبد",
                        "اعصاب"};

                Intent intent = new Intent( ListActivity.this, CameraActivity.class);
                startActivity(intent);

            }
        });
        cafeBtn = findViewById(R.id.ar_cafe_btn);
        cafeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cat = Categories.cafe;
                words = new String[]{"How much", "Roselle", "anise", "banana", "cinnamon", "berell",
                        "coffee", "cold", "drink", "doing", "hot", "in", "juice", "lemon", "mango", "me",
                        "milk", "nescafe", "or", "orange", "pepsi", "question", "spoon", "tea", "water"};
                arWords = new String[]{"كام", "كركديه", "ينسون", "موز", "قرفة", "بيريل",
                        "قهوة", "بارد", "اشرب", "يعمل", "سخن", "في", "عصير", "لمون", "مانجا", "انا",
                        "لبن", "نسكفية", "او", "برتقال", "بيبسي", "سؤال", "معلقة", "شاي", "مياه"};
                
                Intent intent = new Intent( ListActivity.this, CameraActivity.class);
                startActivity(intent);

            }
        });
        stationBtn = findViewById(R.id.ar_station_btn);
        stationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cat = Categories.train;
                words = new String[]{"question", "Alexandria", "Train", "He will come", "Cairo",
                        "Mansoura", "Tanta", "Damanhur", "zagazig", "Integrated services card",
                        "need", "ticket", "late", "I go", "me"};
                arWords = new String[]{"سؤال", "اسكندرية", "قطار", "هيجي", "القاهرة",
                        "المنصورة", "طنطا", "دمنهور", "الزقازيق", "بطاقة الخدمات المتكاملة",
                        "عايز", "تذكرة", "متأخر", "اروح", "انا"};
                
                Intent intent = new Intent( ListActivity.this, CameraActivity.class);
                startActivity(intent);

            }
        });


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