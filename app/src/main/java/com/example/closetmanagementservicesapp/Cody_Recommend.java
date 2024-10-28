package com.example.closetmanagementservicesapp;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Cody_Recommend extends AppCompatActivity implements WeatherDataCallback {
    // DB
    private DBHelper dbHelper;
    private SQLiteDatabase db;

    // GPS 및 날씨
    static String sky = "";
    static String sky_state = "";
    static float temp = 0;

    // 랜덤 속성
    static Integer[] c_id_cod_rec1 = {0, 0, 0, 0};
    static Integer[] c_id_cod_rec2 = {0, 0, 0, 0};

    static String[] c_img_cod_rec1 = {"", "", "", ""};
    static String[] c_img_cod_rec2 = {"", "", "", ""};



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.codyrecommend);

        // DB OPEN
        dbHelper = MyApplication.getDbHelper();
        db = dbHelper.getWritableDatabase();

        ImageButton cod_rec1_top = (ImageButton) findViewById(R.id.cod_rec1_top);
        ImageButton cod_rec1_bottom = (ImageButton) findViewById(R.id.cod_rec1_bottom);
        ImageButton cod_rec1_outer = (ImageButton) findViewById(R.id.cod_rec1_outer);
        ImageButton cod_rec1_shoes = (ImageButton) findViewById(R.id.cod_rec1_shoes);

        ImageButton cod_rec2_top = (ImageButton) findViewById(R.id.cod_rec2_top);
        ImageButton cod_rec2_bottom = (ImageButton) findViewById(R.id.cod_rec2_bottom);
        ImageButton cod_rec2_outer = (ImageButton) findViewById(R.id.cod_rec2_outer);
        ImageButton cod_rec2_shoes = (ImageButton) findViewById(R.id.cod_rec2_shoes);

        ImageButton btnBack = (ImageButton) findViewById(R.id.btnBack_rec);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Intent intent = getIntent();
        int rec_click = intent.getIntExtra("rec_click", 0);

        if (rec_click == 1) {
            ArrayList<String> tagArgs = getTagArgs();
            ArrayList<Integer> cIdList = new ArrayList<>();
            ArrayList<String> cImageList = new ArrayList<>();

            // 상의 랜덤 추천
            StringBuilder tags_builder = new StringBuilder();
            tags_builder.append("c_type = '상의' AND c_tag IN (");
            for (int i = 0; i < tagArgs.size(); i++) {
                tags_builder.append("?");
                if (i < tagArgs.size() - 1) {
                    tags_builder.append(", ");
                }
            }
            tags_builder.append(")");

            Cursor cursor_top = db.query("Main_Closet", new String[]{"c_id", "c_img"}, tags_builder.toString(), tagArgs.toArray(new String[0]), null, null, null);
            if (cursor_top.moveToFirst()) {
                do {
                    int codId = cursor_top.getInt(cursor_top.getColumnIndex("c_id"));
                    String codName = cursor_top.getString(cursor_top.getColumnIndex("c_img"));
                    cIdList.add(codId);
                    cImageList.add(codName);
                } while (cursor_top.moveToNext());
            }
            cursor_top.close();

            if (!cIdList.isEmpty()) {
                Random rand = new Random();
                int randIndex1 = rand.nextInt(cIdList.size());
                int randIndex2 = rand.nextInt(cIdList.size());
                c_id_cod_rec1[0] = cIdList.get(randIndex1);
                c_id_cod_rec2[0] = cIdList.get(randIndex2);
                c_img_cod_rec1[0] = cImageList.get(randIndex1);
                c_img_cod_rec2[0] = cImageList.get(randIndex2);
            }

            // 하의 랜덤 추천
            tags_builder = new StringBuilder();
            cIdList.clear();
            cImageList.clear();
            tags_builder.append("c_type = '하의' AND c_tag IN (");
            for (int i = 0; i < tagArgs.size(); i++) {
                tags_builder.append("?");
                if (i < tagArgs.size() - 1) {
                    tags_builder.append(", ");
                }
            }
            tags_builder.append(")");

            Cursor cursor_bottom = db.query("Main_Closet", new String[]{"c_id", "c_img"}, tags_builder.toString(), tagArgs.toArray(new String[0]), null, null, null);
            if (cursor_bottom.moveToFirst()) {
                do {
                    int codId = cursor_bottom.getInt(cursor_bottom.getColumnIndex("c_id"));
                    String codName = cursor_bottom.getString(cursor_bottom.getColumnIndex("c_img"));
                    cIdList.add(codId);
                    cImageList.add(codName);
                } while (cursor_bottom.moveToNext());
            }
            cursor_bottom.close();

            if (!cIdList.isEmpty()) {
                Random rand = new Random();
                int randIndex1 = rand.nextInt(cIdList.size());
                int randIndex2 = rand.nextInt(cIdList.size());
                c_id_cod_rec1[1] = cIdList.get(randIndex1);
                c_id_cod_rec2[1] = cIdList.get(randIndex2);
                c_img_cod_rec1[1] = cImageList.get(randIndex1);
                c_img_cod_rec2[1] = cImageList.get(randIndex2);
            }

            // 외투 랜덤 추천
            tags_builder = new StringBuilder();
            cIdList.clear();
            cImageList.clear();
            tags_builder.append("c_type = '외투' AND c_tag IN (");
            for (int i = 0; i < tagArgs.size(); i++) {
                tags_builder.append("?");
                if (i < tagArgs.size() - 1) {
                    tags_builder.append(", ");
                }
            }
            tags_builder.append(")");

            Cursor cursor_outer = db.query("Main_Closet", new String[]{"c_id", "c_img"}, tags_builder.toString(), tagArgs.toArray(new String[0]), null, null, null);
            if (cursor_outer.moveToFirst()) {
                do {
                    int codId = cursor_outer.getInt(cursor_outer.getColumnIndex("c_id"));
                    String codName = cursor_outer.getString(cursor_outer.getColumnIndex("c_img"));
                    cIdList.add(codId);
                    cImageList.add(codName);
                } while (cursor_outer.moveToNext());
            }
            cursor_outer.close();

            if (!cIdList.isEmpty()) {
                Random rand = new Random();
                int randIndex1 = rand.nextInt(cIdList.size());
                int randIndex2 = rand.nextInt(cIdList.size());
                c_id_cod_rec1[2] = cIdList.get(randIndex1);
                c_id_cod_rec2[2] = cIdList.get(randIndex2);
                c_img_cod_rec1[2] = cImageList.get(randIndex1);
                c_img_cod_rec2[2] = cImageList.get(randIndex2);
            }


            // 신발 랜덤 추천
            tags_builder = new StringBuilder();
            cIdList.clear();
            cImageList.clear();
            tags_builder.append("c_type = '신발' AND c_tag IN (");
            for (int i = 0; i < tagArgs.size(); i++) {
                tags_builder.append("?");
                if (i < tagArgs.size() - 1) {
                    tags_builder.append(", ");
                }
            }
            tags_builder.append(")");

            Cursor cursor_shoes = db.query("Main_Closet", new String[]{"c_id", "c_img"}, tags_builder.toString(), tagArgs.toArray(new String[0]), null, null, null);
            if (cursor_shoes.moveToFirst()) {
                do {
                    int codId = cursor_shoes.getInt(cursor_shoes.getColumnIndex("c_id"));
                    String codName = cursor_shoes.getString(cursor_shoes.getColumnIndex("c_img"));
                    cIdList.add(codId);
                    cImageList.add(codName);
                } while (cursor_shoes.moveToNext());
            }
            cursor_shoes.close();

            if (!cIdList.isEmpty()) {
                Random rand = new Random();
                int randIndex1 = rand.nextInt(cIdList.size());
                int randIndex2 = rand.nextInt(cIdList.size());
                c_id_cod_rec1[3] = cIdList.get(randIndex1);
                c_id_cod_rec2[3] = cIdList.get(randIndex2);
                c_img_cod_rec1[3] = cImageList.get(randIndex1);
                c_img_cod_rec2[3] = cImageList.get(randIndex2);
            }

            Bitmap bitmap_rec1_top = BitmapFactory.decodeFile(c_img_cod_rec1[0]);
            Bitmap bitmap_rec1_bottom = BitmapFactory.decodeFile(c_img_cod_rec1[1]);
            Bitmap bitmap_rec1_outer = BitmapFactory.decodeFile(c_img_cod_rec1[2]);
            Bitmap bitmap_rec1_shoes = BitmapFactory.decodeFile(c_img_cod_rec1[3]);

            Bitmap bitmap_rec2_top = BitmapFactory.decodeFile(c_img_cod_rec2[0]);
            Bitmap bitmap_rec2_bottom = BitmapFactory.decodeFile(c_img_cod_rec2[1]);
            Bitmap bitmap_rec2_outer = BitmapFactory.decodeFile(c_img_cod_rec2[2]);
            Bitmap bitmap_rec2_shoes = BitmapFactory.decodeFile(c_img_cod_rec2[3]);

            cod_rec1_top.setImageBitmap(bitmap_rec1_top);
            cod_rec1_bottom.setImageBitmap(bitmap_rec1_bottom);
            cod_rec1_outer.setImageBitmap(bitmap_rec1_outer);
            cod_rec1_shoes.setImageBitmap(bitmap_rec1_shoes);

            cod_rec2_top.setImageBitmap(bitmap_rec2_top);
            cod_rec2_bottom.setImageBitmap(bitmap_rec2_bottom);
            cod_rec2_outer.setImageBitmap(bitmap_rec2_outer);
            cod_rec2_shoes.setImageBitmap(bitmap_rec2_shoes);


            cod_rec1_top.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new Thread(() -> {
                        Cursor detailCursor = db.query("Main_Closet", null, "c_id = ?", new String[]{String.valueOf(c_id_cod_rec1[0])}, null, null, null);

                        if(detailCursor !=null && detailCursor.moveToFirst()) {
                            Intent intent = new Intent(Cody_Recommend.this, DetailActivity.class);
                            intent.putExtra("c_id", detailCursor.getInt(detailCursor.getColumnIndexOrThrow("c_id")));
                            intent.putExtra("c_img", detailCursor.getString(detailCursor.getColumnIndexOrThrow("c_img")));
                            intent.putExtra("c_loc", detailCursor.getInt(detailCursor.getColumnIndexOrThrow("c_loc")));
                            intent.putExtra("c_name", detailCursor.getString(detailCursor.getColumnIndexOrThrow("c_name")));
                            intent.putExtra("c_type", detailCursor.getString(detailCursor.getColumnIndexOrThrow("c_type")));
                            intent.putExtra("c_size", detailCursor.getString(detailCursor.getColumnIndexOrThrow("c_size")));
                            intent.putExtra("c_brand", detailCursor.getString(detailCursor.getColumnIndexOrThrow("c_brand")));
                            intent.putExtra("c_tag", detailCursor.getInt(detailCursor.getColumnIndexOrThrow("c_tag")));
                            intent.putExtra("c_memo", detailCursor.getString(detailCursor.getColumnIndexOrThrow("c_memo")));
                            intent.putExtra("c_date", detailCursor.getString(detailCursor.getColumnIndexOrThrow("c_date")));
                            intent.putExtra("c_stack", detailCursor.getInt(detailCursor.getColumnIndexOrThrow("c_stack")));

                            // 커서 닫기 및 인텐트 실행은 UI 스레드에서 실행
                            runOnUiThread(() -> {
                                startActivity(intent);
                                detailCursor.close(); // 사용 후 커서 닫기
                            });
                        } else if (detailCursor != null) {
                            detailCursor.close();
                        }
                    }).start();
                }
            });

            cod_rec1_bottom.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new Thread(() -> {
                        Cursor detailCursor = db.query("Main_Closet", null, "c_id = ?", new String[]{String.valueOf(c_id_cod_rec1[1])}, null, null, null);

                        if(detailCursor !=null && detailCursor.moveToFirst()) {
                            Intent intent = new Intent(Cody_Recommend.this, DetailActivity.class);
                            intent.putExtra("c_id", detailCursor.getInt(detailCursor.getColumnIndexOrThrow("c_id")));
                            intent.putExtra("c_img", detailCursor.getString(detailCursor.getColumnIndexOrThrow("c_img")));
                            intent.putExtra("c_loc", detailCursor.getInt(detailCursor.getColumnIndexOrThrow("c_loc")));
                            intent.putExtra("c_name", detailCursor.getString(detailCursor.getColumnIndexOrThrow("c_name")));
                            intent.putExtra("c_type", detailCursor.getString(detailCursor.getColumnIndexOrThrow("c_type")));
                            intent.putExtra("c_size", detailCursor.getString(detailCursor.getColumnIndexOrThrow("c_size")));
                            intent.putExtra("c_brand", detailCursor.getString(detailCursor.getColumnIndexOrThrow("c_brand")));
                            intent.putExtra("c_tag", detailCursor.getInt(detailCursor.getColumnIndexOrThrow("c_tag")));
                            intent.putExtra("c_memo", detailCursor.getString(detailCursor.getColumnIndexOrThrow("c_memo")));
                            intent.putExtra("c_date", detailCursor.getString(detailCursor.getColumnIndexOrThrow("c_date")));
                            intent.putExtra("c_stack", detailCursor.getInt(detailCursor.getColumnIndexOrThrow("c_stack")));

                            // 커서 닫기 및 인텐트 실행은 UI 스레드에서 실행
                            runOnUiThread(() -> {
                                startActivity(intent);
                                detailCursor.close(); // 사용 후 커서 닫기
                            });
                        } else if (detailCursor != null) {
                            detailCursor.close();
                        }
                    }).start();
                }
            });

            cod_rec1_outer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new Thread(() -> {
                        Cursor detailCursor = db.query("Main_Closet", null, "c_id = ?", new String[]{String.valueOf(c_id_cod_rec1[2])}, null, null, null);

                        if(detailCursor !=null && detailCursor.moveToFirst()) {
                            Intent intent = new Intent(Cody_Recommend.this, DetailActivity.class);
                            intent.putExtra("c_id", detailCursor.getInt(detailCursor.getColumnIndexOrThrow("c_id")));
                            intent.putExtra("c_img", detailCursor.getString(detailCursor.getColumnIndexOrThrow("c_img")));
                            intent.putExtra("c_loc", detailCursor.getInt(detailCursor.getColumnIndexOrThrow("c_loc")));
                            intent.putExtra("c_name", detailCursor.getString(detailCursor.getColumnIndexOrThrow("c_name")));
                            intent.putExtra("c_type", detailCursor.getString(detailCursor.getColumnIndexOrThrow("c_type")));
                            intent.putExtra("c_size", detailCursor.getString(detailCursor.getColumnIndexOrThrow("c_size")));
                            intent.putExtra("c_brand", detailCursor.getString(detailCursor.getColumnIndexOrThrow("c_brand")));
                            intent.putExtra("c_tag", detailCursor.getInt(detailCursor.getColumnIndexOrThrow("c_tag")));
                            intent.putExtra("c_memo", detailCursor.getString(detailCursor.getColumnIndexOrThrow("c_memo")));
                            intent.putExtra("c_date", detailCursor.getString(detailCursor.getColumnIndexOrThrow("c_date")));
                            intent.putExtra("c_stack", detailCursor.getInt(detailCursor.getColumnIndexOrThrow("c_stack")));

                            // 커서 닫기 및 인텐트 실행은 UI 스레드에서 실행
                            runOnUiThread(() -> {
                                startActivity(intent);
                                detailCursor.close(); // 사용 후 커서 닫기
                            });
                        } else if (detailCursor != null) {
                            detailCursor.close();
                        }
                    }).start();
                }
            });

            cod_rec1_shoes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new Thread(() -> {
                        Cursor detailCursor = db.query("Main_Closet", null, "c_id = ?", new String[]{String.valueOf(c_id_cod_rec1[3])}, null, null, null);

                        if(detailCursor !=null && detailCursor.moveToFirst()) {
                            Intent intent = new Intent(Cody_Recommend.this, DetailActivity.class);
                            intent.putExtra("c_id", detailCursor.getInt(detailCursor.getColumnIndexOrThrow("c_id")));
                            intent.putExtra("c_img", detailCursor.getString(detailCursor.getColumnIndexOrThrow("c_img")));
                            intent.putExtra("c_loc", detailCursor.getInt(detailCursor.getColumnIndexOrThrow("c_loc")));
                            intent.putExtra("c_name", detailCursor.getString(detailCursor.getColumnIndexOrThrow("c_name")));
                            intent.putExtra("c_type", detailCursor.getString(detailCursor.getColumnIndexOrThrow("c_type")));
                            intent.putExtra("c_size", detailCursor.getString(detailCursor.getColumnIndexOrThrow("c_size")));
                            intent.putExtra("c_brand", detailCursor.getString(detailCursor.getColumnIndexOrThrow("c_brand")));
                            intent.putExtra("c_tag", detailCursor.getInt(detailCursor.getColumnIndexOrThrow("c_tag")));
                            intent.putExtra("c_memo", detailCursor.getString(detailCursor.getColumnIndexOrThrow("c_memo")));
                            intent.putExtra("c_date", detailCursor.getString(detailCursor.getColumnIndexOrThrow("c_date")));
                            intent.putExtra("c_stack", detailCursor.getInt(detailCursor.getColumnIndexOrThrow("c_stack")));

                            // 커서 닫기 및 인텐트 실행은 UI 스레드에서 실행
                            runOnUiThread(() -> {
                                startActivity(intent);
                                detailCursor.close(); // 사용 후 커서 닫기
                            });
                        } else if (detailCursor != null) {
                            detailCursor.close();
                        }
                    }).start();
                }
            });

            cod_rec2_top.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new Thread(() -> {
                        Cursor detailCursor = db.query("Main_Closet", null, "c_id = ?", new String[]{String.valueOf(c_id_cod_rec2[0])}, null, null, null);

                        if(detailCursor !=null && detailCursor.moveToFirst()) {
                            Intent intent = new Intent(Cody_Recommend.this, DetailActivity.class);
                            intent.putExtra("c_id", detailCursor.getInt(detailCursor.getColumnIndexOrThrow("c_id")));
                            intent.putExtra("c_img", detailCursor.getString(detailCursor.getColumnIndexOrThrow("c_img")));
                            intent.putExtra("c_loc", detailCursor.getInt(detailCursor.getColumnIndexOrThrow("c_loc")));
                            intent.putExtra("c_name", detailCursor.getString(detailCursor.getColumnIndexOrThrow("c_name")));
                            intent.putExtra("c_type", detailCursor.getString(detailCursor.getColumnIndexOrThrow("c_type")));
                            intent.putExtra("c_size", detailCursor.getString(detailCursor.getColumnIndexOrThrow("c_size")));
                            intent.putExtra("c_brand", detailCursor.getString(detailCursor.getColumnIndexOrThrow("c_brand")));
                            intent.putExtra("c_tag", detailCursor.getInt(detailCursor.getColumnIndexOrThrow("c_tag")));
                            intent.putExtra("c_memo", detailCursor.getString(detailCursor.getColumnIndexOrThrow("c_memo")));
                            intent.putExtra("c_date", detailCursor.getString(detailCursor.getColumnIndexOrThrow("c_date")));
                            intent.putExtra("c_stack", detailCursor.getInt(detailCursor.getColumnIndexOrThrow("c_stack")));

                            // 커서 닫기 및 인텐트 실행은 UI 스레드에서 실행
                            runOnUiThread(() -> {
                                startActivity(intent);
                                detailCursor.close(); // 사용 후 커서 닫기
                            });
                        } else if (detailCursor != null) {
                            detailCursor.close();
                        }
                    }).start();
                }
            });

            cod_rec2_bottom.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new Thread(() -> {
                        Cursor detailCursor = db.query("Main_Closet", null, "c_id = ?", new String[]{String.valueOf(c_id_cod_rec2[1])}, null, null, null);

                        if(detailCursor !=null && detailCursor.moveToFirst()) {
                            Intent intent = new Intent(Cody_Recommend.this, DetailActivity.class);
                            intent.putExtra("c_id", detailCursor.getInt(detailCursor.getColumnIndexOrThrow("c_id")));
                            intent.putExtra("c_img", detailCursor.getString(detailCursor.getColumnIndexOrThrow("c_img")));
                            intent.putExtra("c_loc", detailCursor.getInt(detailCursor.getColumnIndexOrThrow("c_loc")));
                            intent.putExtra("c_name", detailCursor.getString(detailCursor.getColumnIndexOrThrow("c_name")));
                            intent.putExtra("c_type", detailCursor.getString(detailCursor.getColumnIndexOrThrow("c_type")));
                            intent.putExtra("c_size", detailCursor.getString(detailCursor.getColumnIndexOrThrow("c_size")));
                            intent.putExtra("c_brand", detailCursor.getString(detailCursor.getColumnIndexOrThrow("c_brand")));
                            intent.putExtra("c_tag", detailCursor.getInt(detailCursor.getColumnIndexOrThrow("c_tag")));
                            intent.putExtra("c_memo", detailCursor.getString(detailCursor.getColumnIndexOrThrow("c_memo")));
                            intent.putExtra("c_date", detailCursor.getString(detailCursor.getColumnIndexOrThrow("c_date")));
                            intent.putExtra("c_stack", detailCursor.getInt(detailCursor.getColumnIndexOrThrow("c_stack")));

                            // 커서 닫기 및 인텐트 실행은 UI 스레드에서 실행
                            runOnUiThread(() -> {
                                startActivity(intent);
                                detailCursor.close(); // 사용 후 커서 닫기
                            });
                        } else if (detailCursor != null) {
                            detailCursor.close();
                        }
                    }).start();
                }
            });

            cod_rec2_outer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new Thread(() -> {
                        Cursor detailCursor = db.query("Main_Closet", null, "c_id = ?", new String[]{String.valueOf(c_id_cod_rec2[2])}, null, null, null);

                        if(detailCursor !=null && detailCursor.moveToFirst()) {
                            Intent intent = new Intent(Cody_Recommend.this, DetailActivity.class);
                            intent.putExtra("c_id", detailCursor.getInt(detailCursor.getColumnIndexOrThrow("c_id")));
                            intent.putExtra("c_img", detailCursor.getString(detailCursor.getColumnIndexOrThrow("c_img")));
                            intent.putExtra("c_loc", detailCursor.getInt(detailCursor.getColumnIndexOrThrow("c_loc")));
                            intent.putExtra("c_name", detailCursor.getString(detailCursor.getColumnIndexOrThrow("c_name")));
                            intent.putExtra("c_type", detailCursor.getString(detailCursor.getColumnIndexOrThrow("c_type")));
                            intent.putExtra("c_size", detailCursor.getString(detailCursor.getColumnIndexOrThrow("c_size")));
                            intent.putExtra("c_brand", detailCursor.getString(detailCursor.getColumnIndexOrThrow("c_brand")));
                            intent.putExtra("c_tag", detailCursor.getInt(detailCursor.getColumnIndexOrThrow("c_tag")));
                            intent.putExtra("c_memo", detailCursor.getString(detailCursor.getColumnIndexOrThrow("c_memo")));
                            intent.putExtra("c_date", detailCursor.getString(detailCursor.getColumnIndexOrThrow("c_date")));
                            intent.putExtra("c_stack", detailCursor.getInt(detailCursor.getColumnIndexOrThrow("c_stack")));

                            // 커서 닫기 및 인텐트 실행은 UI 스레드에서 실행
                            runOnUiThread(() -> {
                                startActivity(intent);
                                detailCursor.close(); // 사용 후 커서 닫기
                            });
                        } else if (detailCursor != null) {
                            detailCursor.close();
                        }
                    }).start();
                }
            });

            cod_rec2_shoes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new Thread(() -> {
                        Cursor detailCursor = db.query("Main_Closet", null, "c_id = ?", new String[]{String.valueOf(c_id_cod_rec2[3])}, null, null, null);

                        if(detailCursor !=null && detailCursor.moveToFirst()) {
                            Intent intent = new Intent(Cody_Recommend.this, DetailActivity.class);
                            intent.putExtra("c_id", detailCursor.getInt(detailCursor.getColumnIndexOrThrow("c_id")));
                            intent.putExtra("c_img", detailCursor.getString(detailCursor.getColumnIndexOrThrow("c_img")));
                            intent.putExtra("c_loc", detailCursor.getInt(detailCursor.getColumnIndexOrThrow("c_loc")));
                            intent.putExtra("c_name", detailCursor.getString(detailCursor.getColumnIndexOrThrow("c_name")));
                            intent.putExtra("c_type", detailCursor.getString(detailCursor.getColumnIndexOrThrow("c_type")));
                            intent.putExtra("c_size", detailCursor.getString(detailCursor.getColumnIndexOrThrow("c_size")));
                            intent.putExtra("c_brand", detailCursor.getString(detailCursor.getColumnIndexOrThrow("c_brand")));
                            intent.putExtra("c_tag", detailCursor.getInt(detailCursor.getColumnIndexOrThrow("c_tag")));
                            intent.putExtra("c_memo", detailCursor.getString(detailCursor.getColumnIndexOrThrow("c_memo")));
                            intent.putExtra("c_date", detailCursor.getString(detailCursor.getColumnIndexOrThrow("c_date")));
                            intent.putExtra("c_stack", detailCursor.getInt(detailCursor.getColumnIndexOrThrow("c_stack")));

                            // 커서 닫기 및 인텐트 실행은 UI 스레드에서 실행
                            runOnUiThread(() -> {
                                startActivity(intent);
                                detailCursor.close(); // 사용 후 커서 닫기
                            });
                        } else if (detailCursor != null) {
                            detailCursor.close();
                        }
                    }).start();
                }
            });
        }
    }

    @Override
    public void onWeatherDataResult(String sky_date, String sky_state_date, float temp_date) {
        sky = sky_date;
        sky_state = sky_state_date;
        temp = temp_date;
        Log.d("MainActivity", "날씨 상태: " + sky + ", 강수 상태: " + sky_state + ", 온도: " + temp);
    }

    private ArrayList<String> getTagArgs() {
        ArrayList<String> tagList = new ArrayList<>();

        if (sky.equals("맑음") && sky_state.equals("없음")) {
            temp -= 0.0;
        } else if ((!sky.equals("맑음") && sky_state.equals("없음")) || (sky.equals("맑음") && (sky_state.equals("비") || sky_state.equals("소나기")))) {
            temp -= 0.5;
        } else if ((!sky.equals("맑음") && sky_state.equals("소나기"))) {
            temp -= 1.0;
        } else if ((!sky.equals("맑음") && sky_state.equals("비")) || (sky.equals("맑음") && (sky_state.equals("비/눈") || sky_state.equals("눈")))) {
            temp -= 1.5;
        } else if ((!sky.equals("맑음") && (sky_state.equals("비/눈") || sky_state.equals("눈")))) {
            temp -= 2.0;
        }

        if (temp <= -10.0) {
            tagList.add("4");
        } else if (temp >= -9.9 && temp <= 0.0) {
            tagList.addAll(Arrays.asList("4", "7", "10", "13", "15"));
        } else if (temp >= 0.1 && temp <= 15.0) {
            tagList.addAll(Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15"));
        } else if (temp >= 15.1 && temp <= 25.0) {
            tagList.addAll(Arrays.asList("1", "2", "3", "5", "6", "8", "11", "15"));
        } else if (temp >= 25.1) {
            tagList.addAll(Arrays.asList("2", "3", "5", "6", "8", "11"));
        }

        return tagList;
    }
}
