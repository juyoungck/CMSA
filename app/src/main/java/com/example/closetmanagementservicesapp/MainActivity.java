package com.example.closetmanagementservicesapp;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.view.View;


import com.google.android.material.bottomsheet.BottomSheetDialog;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {

    private DBHelper dbHelper;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 하단 등록 버튼 이동
        Button btnAdd = (Button) findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AddMenu.class);
                startActivity(intent);
            }
        });

        // 설정 탭 이동
        ImageButton btnSettings = (ImageButton) findViewById(R.id.btnSettings);
        btnSettings.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Settings.class);
                startActivity(intent);
            }
        });

        // 정렬 버튼 창
        ImageButton btnSort = (ImageButton) findViewById(R.id.btnSort);
        btnSort.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(MainActivity.this);
                View view1 = LayoutInflater.from(MainActivity.this).inflate(R.layout.tab_sort, null);

                bottomSheetDialog.setContentView(view1);
                bottomSheetDialog.show();

                // 정렬 버튼 닫기
                Button tabsortClose = view1.findViewById(R.id.tabsortClose);
                tabsortClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        bottomSheetDialog.dismiss();
                    }
                });

                // 정렬 기능 호출 (TabSort 클래스)
                TabSort tabsort = new TabSort();

                // 정렬 (옷 종류)
                tabsort.clothesSelect(view1);

                // 정렬 (날씨)
                tabsort.weatherSelect(view1);
            }
        });

        // MyApplication 클래스에서 DBHelper를 가져오는 코드
        dbHelper = MyApplication.getDbHelper();

        // db 파일을 읽기/쓰기가 가능한 형식으로 연다
        db = dbHelper.getWritableDatabase();


        // 기본 옷장 위치 추가
        basicLocation();

        // 임의 데이터 출력을 위한 메서드
        displayData();

        // Spinner 값 출력 (TEST)
        fillSpinner();


    }

    /* 임의 데이터 입력 (데이터 입력 기능 추가로 주석처리)
    private void insertDummyData() {
        // values 생성 후 Closet_Location 테이블에 임의 값 입력
        ContentValues values = new ContentValues();
        values.put("c_loc", 1);
        values.put("c_loc_name", "옷장1");
        values.put("c_loc_date", "20240723");
        db.insert("Closet_Location", null, values);
        values.put("c_loc", 2);
        values.put("c_loc_name", "옷장2");
        values.put("c_loc_date", "20240806");
        db.insert("Closet_Location", null, values);
        values.put("c_loc", 3);
        values.put("c_loc_name", "봄 옷장");
        values.put("c_loc_date", "20240806");
        db.insert("Closet_Location", null, values);

        // 입력할 값 배열로 정렬
        int[] imageResources = {R.drawable.image1, R.drawable.image2, R.drawable.image3, R.drawable.image4};
        String[] names = {"라운드 반팔티", "청바지", "후드집업", "니트셔츠"};
        String[] types = {"1", "2", "1", "1"};
        String[] sizes = {"100(XL)", "100(XL)", null, null};
        String[] brands = {"MUSINSA", null, null, null};
        String[] memos = {"조금 낡음", null, null, "버릴 예정"};

        // for 문으로 배열 끝까지 Main_Closet 테이블에 임의값 입력
        for (int i = 0; i < 4; i++) {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), imageResources[i]);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            byte[] imageBytes = outputStream.toByteArray();

            values = new ContentValues();
            values.put("c_id", i + 1);
            values.put("c_loc", 1);
            values.put("c_img", imageBytes);
            values.put("c_name", names[i]);
            values.put("c_type", types[i]);
            values.put("c_size", sizes[i]);
            values.put("c_brand", brands[i]);
            values.put("c_tag", 1);
            values.put("c_memo", memos[i]);
            values.put("c_date", "20240723");
            values.put("c_stack", 0);
            db.insert("Main_Closet", null, values);
        }
    }
     */

    // 임의 데이터 출력, 추후 출력 코드 작성 시 아래와 비슷하게 작성할 예정
    private void displayData() {
        // Main_Closet 테이블의 모든 값을 불러옴
        Cursor cursor = db.query("Main_Closet", null, null, null, null, null, null);

        // 커서 위치 유효성 검사 후 문제가 없으면 해당 코드 실행
        if (cursor != null && cursor.moveToFirst()) {
            int count = cursor.getCount();

            for (int i = 0; i < count; i++) {
                String c_name = cursor.getString(cursor.getColumnIndexOrThrow("c_name"));
                byte[] c_img = cursor.getBlob(cursor.getColumnIndexOrThrow("c_img"));

                Bitmap bitmap = BitmapFactory.decodeByteArray(c_img, 0, c_img.length);

                // getResources().getIdentifier() 메서드를 사용해서 xml 파일의 clothTag1, clothTag2 등의 id 값들을 차례대로 불러옴
                @SuppressLint("DiscouragedApi") int textViewId = getResources().getIdentifier("clothTag" + (i + 1), "id", getPackageName());
                @SuppressLint("DiscouragedApi") int imageButtonId = getResources().getIdentifier("clothImgbtn" + (i + 1), "id", getPackageName());

                TextView textView = findViewById(textViewId);
                ImageButton imageButton = findViewById(imageButtonId);

                // 유효성 검사 후 문제가 없으면 해당 코드 실행 (현재 오류 발생 중, 추후 수정)
                if (textView != null && imageButton != null) {
                    textView.setText(c_name);
                    imageButton.setImageBitmap(bitmap);

                    // 이미지 버튼을 클릭하면 해당하는 컬럼의 모든 데이터를 볼 수 있는 탭으로 이동한다. (DetailActivity.java와 activity_detail.xml 파일 참고)
                    imageButton.setOnClickListener(view -> {
                        Intent intent = new Intent(MainActivity.this, DetailActivity.class);

                        intent.putExtra("c_img", cursor.getBlob(cursor.getColumnIndexOrThrow("c_img")));
                        intent.putExtra("c_loc", cursor.getInt(cursor.getColumnIndexOrThrow("c_loc")));
                        intent.putExtra("c_name", cursor.getString(cursor.getColumnIndexOrThrow("c_name")));
                        intent.putExtra("c_type", cursor.getString(cursor.getColumnIndexOrThrow("c_type")));
                        intent.putExtra("c_size", cursor.getString(cursor.getColumnIndexOrThrow("c_size")));
                        intent.putExtra("c_brand", cursor.getString(cursor.getColumnIndexOrThrow("c_brand")));
                        intent.putExtra("c_tag", cursor.getInt(cursor.getColumnIndexOrThrow("c_tag")));
                        intent.putExtra("c_memo", cursor.getString(cursor.getColumnIndexOrThrow("c_memo")));
                        intent.putExtra("c_date", cursor.getString(cursor.getColumnIndexOrThrow("c_date")));
                        intent.putExtra("c_stack", cursor.getInt(cursor.getColumnIndexOrThrow("c_stack")));
                        startActivity(intent);
                    });
                }

                if (!cursor.moveToNext()) {
                    break;
                }
            }
        }

        if (cursor != null) {
            cursor.close();
        }
    }

    private void fillSpinner() {
        Spinner spinner = findViewById(R.id.main_c_loc);

        List<String> locations = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT c_loc_name FROM Closet_Location ORDER BY c_loc ASC", null);

        while (cursor.moveToNext()) {
            locations.add(cursor.getString(cursor.getColumnIndex("c_loc_name")));
        }
        cursor.close();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, locations);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }


    // MainActivity가 종료 될 때 호출 되는 메서드
    @Override
    protected void onDestroy() {
        super.onDestroy();

        // MyApplication 클래스에서 앱이 정상적으로 종료될 때 db 파일을 닫는 코드가 있지만
        // 정상적으로 종료되지 않았을 때는 db 파일이 닫히지 앋기 때문에 이 곳에도 코드를 추가함
        if (db != null && db.isOpen()) {
            db.close();
        }
    }

    private void basicLocation() {
        ContentValues values = new ContentValues();
        values.put("c_loc", 1);
        values.put("c_loc_name", "옷장 1");
        values.put("c_loc_date", getToday());
        db.insert("Closet_Location", null, values);
    }

    private String getToday() {
        DateFormat Today = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.KOREAN);
        TimeZone KoreaTime = TimeZone.getTimeZone("Asia/Seoul");
        Today.setTimeZone(KoreaTime);

        Date date = new Date();

        return Today.format(date);
    }

}
