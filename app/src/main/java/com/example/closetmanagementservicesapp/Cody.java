package com.example.closetmanagementservicesapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.Button;

import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.core.util.Pair;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.view.View;
import android.widget.Toast;


import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.navigation.NavigationBarView;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TimeZone;

public class Cody extends AppCompatActivity implements WeatherDataCallback {
    // DB
    private DBHelper dbHelper;
    private SQLiteDatabase db;

    // 스피너 및 정렬
    private static List<Integer> cod_loc_value = new ArrayList<>();
    private static int selected_Cody_LocId = 1;
    private static ArrayList<Integer> st_sort_cod_id = null;
    private static String orderBy_cody_set = null;
    private static String search_cod_name = null;
    private HashMap<Integer, Boolean> checkboxStates = new HashMap<>();

    // GPS 및 날씨
    private GpsHelper gpsHelper;
    private ExcelReader excelReader;
    String x = "60", y = "127";
    static String sky = "";
    static String sky_state = "";
    static float temp = 0;

    // UI / UX
    TextView weatherTextView,timeNow;
    ImageView imageViewIcon;
    private GridLayout gridLayout;
    private int imgCounter = 3001;
    private int tagCounter = 4001;
    private int imgViewCounter = 5001;
    private int imgRow = 0;
    private int tagRow = 0;
    private int Call = 0;
    private List<Integer> imgCounterList;
    private List<Integer> imgViewCounterList;

    // 기타 기능
    BottomNavigationView bottomNavigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cody);

        // DB OPEN
        dbHelper = MyApplication.getDbHelper();
        db = dbHelper.getWritableDatabase();

        // UI 기본값 설정
        Pair<List<Integer>, List<Integer>> counters = ItemCodyImgBtn(imgCounter);
        imgCounterList = counters.first;
        imgViewCounterList = counters.second;
        ItemCodyTag(tagCounter);

        gridLayout = findViewById(R.id.gl_cody);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.btnCody);

        // 코디 테마 스피너 출력
        fillSpinner_cod_loc();
        Spinner_Selected();

        // 코디 데이터 출력
        filterDataByQuery(st_sort_cod_id, orderBy_cody_set, search_cod_name, selected_Cody_LocId);

        bottomNavigationView.setLabelVisibilityMode(NavigationBarView.LABEL_VISIBILITY_LABELED);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if (item.isChecked()) {
                    return false;
                }

                item.setCheckable(false);
                item.setChecked(true);

                if (itemId == R.id.btnCloset) {
                    bottomNavigationView.setItemBackgroundResource(android.R.color.transparent);
                    startActivity(new Intent(Cody.this, MainActivity.class));
                    overridePendingTransition(0, 0);
                    finish();
                    return true;
                }

                return false;
            }
        });

        // 하단 등록 버튼(+) 이동
        ImageButton btnAdd = (ImageButton) findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                LinearLayout Add_menu = (LinearLayout)inflater.inflate(R.layout.addmenu, null);

                Add_menu.setBackgroundColor(Color.parseColor("#99000000"));

                LinearLayout.LayoutParams param = new LinearLayout.LayoutParams
                        (LinearLayout.LayoutParams.FILL_PARENT,ViewGroup.LayoutParams.FILL_PARENT);
                addContentView(Add_menu,param);

                Button BtnAddClothes = (Button) findViewById(R.id.btnAddClothes);
                Button BtnAddCody = (Button) findViewById(R.id.btnAddCodey);
                ImageButton AddMenuClose = (ImageButton) findViewById(R.id.addMenuClose);

                Animation fadeIn = new AlphaAnimation(0, 1);
                fadeIn.setDuration(500);
                Add_menu.startAnimation(fadeIn);

                ObjectAnimator Animation = ObjectAnimator.ofFloat(AddMenuClose, "rotation", 0f, 45f);
                Animation.setDuration(300);
                Animation.start();

                Animation = ObjectAnimator.ofFloat(btnAdd, "rotation", 0f, 45f);
                Animation.setDuration(300);
                Animation.start();

                BtnAddClothes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getApplicationContext(), Post.class);
                        startActivity(intent);
                    }
                });
                BtnAddCody.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getApplicationContext(), CodyAdd.class);
                        startActivity(intent);
                    }
                });
                AddMenuClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ViewGroup parentViewGroup = (ViewGroup) Add_menu.getParent();
                        if(parentViewGroup != null) {
                            Animation fadeIn = new AlphaAnimation(1, 0);
                            fadeIn.setDuration(500);
                            Add_menu.startAnimation(fadeIn);

                            parentViewGroup.removeView(Add_menu);

                            ObjectAnimator Animation = ObjectAnimator.ofFloat(AddMenuClose, "rotation", 45f, 0f);
                            Animation.setDuration(300);
                            Animation.start();

                            Animation = ObjectAnimator.ofFloat(btnAdd, "rotation", 45f, 0f);
                            Animation.setDuration(300);
                            Animation.start();
                        }
                    }
                });
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
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(Cody.this);
                View tabView = LayoutInflater.from(Cody.this).inflate(R.layout.tab_sort_cody, null);

                bottomSheetDialog.setContentView(tabView);
                bottomSheetDialog.show();

                // 정렬 버튼 닫기
                ImageButton tabsortClose = tabView.findViewById(R.id.tabsortClose);
                tabsortClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        bottomSheetDialog.dismiss();
                    }
                });

                // 정렬 기능 호출 (TabSort 클래스)
                TabSort_Cody tabsort = new TabSort_Cody(getApplicationContext(), bottomSheetDialog, new TabSortCallback() {
                    @Override
                    public void onSortResult(ArrayList<Integer> sort_cod_id, String orderBy) {
                        // 데이터 출력을 위한 메서드
                        st_sort_cod_id = sort_cod_id;
                        orderBy_cody_set = orderBy;
                        filterDataByQuery(st_sort_cod_id, orderBy_cody_set, search_cod_name, selected_Cody_LocId);
                    }
                }, checkboxStates);

                tabsort.sortApply(tabView);

                bottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        checkboxStates = tabsort.getCheckboxStates(tabView);
                        saveCheckboxStates();
                    }
                });
            }
        });

        // 수정 버튼
        ImageButton btnModify = (ImageButton) findViewById(R.id.btnModify);
        btnModify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialog = new Dialog(Cody.this);
                View modifyView = LayoutInflater.from(Cody.this).inflate(R.layout.tab_modify_cody, null);

                dialog.setContentView(modifyView);
                dialog.show();

                WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
                params.width = WindowManager.LayoutParams.WRAP_CONTENT;
                params.height = 1200;
                dialog.getWindow().setAttributes(params);

                ImageButton tabmodifyClose = modifyView.findViewById(R.id.tabmodifyClose);
                tabmodifyClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                TabModifyCody tabModifyCody = new TabModifyCody(Cody.this);

                tabModifyCody.modifyButton(modifyView);

                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        recreate();  // 액티비티 재생성
                    }
                });
            }
        });

        // GPS 및 날씨 코드
        gpsHelper = new GpsHelper(this);
        excelReader = new ExcelReader(this);

        gpsHelper.initializeGps();

        final Button textview_address = findViewById(R.id.refresh);
        ImageButton ShowLocationButton = findViewById(R.id.btnLocation);
        timeNow = findViewById(R.id.timeNow);

        // 시간 설정
        morningAfternoon ma = new morningAfternoon(TimeHelper.getFormattedTime());
        String abc = ma.asd();
        timeNow.setText(abc);

        ShowLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                try {
                    updateLocationAndWeather(textview_address);
                }
                catch (Exception e){
                    Toast.makeText(Cody.this, "오류가 발생했습니다.", Toast.LENGTH_LONG).show();
                    e.printStackTrace();  // 오류 로그 출력
                }

            }
        });

        if(GPSImpo.printingGPS != null){
            textview_address.setText(GPSImpo.printingGPS+" "+GPSImpo.printingGPS2);
            x = GPSImpo.weatherX;
            y = GPSImpo.weatherY;
        }

        weatherTextView = findViewById(R.id.weatherDegree);
        imageViewIcon = findViewById(R.id.btnWeather);
        WeatherData wd = new WeatherData(weatherTextView,imageViewIcon, null);
        wd.fetchWeather(TimeHelper.getDate(), TimeHelper.getTime(), x, y);  // 비동기적으로 날씨 데이터를 가져옴


        SearchView searchView = findViewById(R.id.btnSearch);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // 검색어가 제출되면 실행될 코드 (제출 후 엔터 시)
                search_cod_name = query;
                filterDataByQuery(st_sort_cod_id, orderBy_cody_set, query, selected_Cody_LocId);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // 검색어가 변경될 때마다 필터링된 데이터를 보여주는 메소드 호출
                Log.d("SearchView", newText);
                search_cod_name = newText;
                filterDataByQuery(st_sort_cod_id, orderBy_cody_set, newText, selected_Cody_LocId);
                return false;
            }
        });

        // 코디 추천 버튼
        ImageButton cod_rec = (ImageButton) findViewById(R.id.cod_rec);
        cod_rec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialog = new Dialog(Cody.this);
                View recView = LayoutInflater.from(Cody.this).inflate(R.layout.recommend_options, null);

                dialog.setContentView(recView);
                dialog.show();

                WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
                params.width=WindowManager.LayoutParams.WRAP_CONTENT;
                params.height=750;
                dialog.getWindow().setAttributes(params);

                ImageButton btnRecCordy = recView.findViewById(R.id.btnRecCordy);
                btnRecCordy.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ArrayList<String> tagArgs = getTagArgs();

                        StringBuilder tags_builder = new StringBuilder();
                        tags_builder.append("cod_tag IN (");
                        for (int i = 0; i < tagArgs.size(); i++) {
                            tags_builder.append("?");
                            if (i < tagArgs.size() - 1) {
                                tags_builder.append(", ");
                            }
                        }
                        tags_builder.append(")");

                        Log.d("tagArgs", tags_builder.toString());
                        Log.d("tagArgs", String.valueOf(tagArgs));

                        Cursor cursor = db.query("Coordy", new String[]{"cod_id", "cod_name"}, tags_builder.toString(), tagArgs.toArray(new String[0]), null, null, null);
                        ArrayList<Integer> codIdList = new ArrayList<>();
                        ArrayList<String> codNameList = new ArrayList<>();

                        if (cursor.moveToFirst()) {
                            do {
                                int codId = cursor.getInt(cursor.getColumnIndex("cod_id"));
                                String codName = cursor.getString(cursor.getColumnIndex("cod_name"));
                                codIdList.add(codId);
                                codNameList.add(codName);
                            } while (cursor.moveToNext());
                        }
                        cursor.close();

                        if (!codIdList.isEmpty()) {
                            Random rand = new Random();
                            int randIndex = rand.nextInt(codIdList.size());
                            int randCodId = codIdList.get(randIndex);
                            String randCodName = codNameList.get(randIndex);

                            AlertDialog.Builder builder = new AlertDialog.Builder(Cody.this);
                            builder.setTitle("오늘의 추천 코디");
                            builder.setMessage("추천 코디는 '" + randCodName + "' 코디입니다!\n해당 코디를 보러 가시겠습니까?");

                            builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    new Thread(() -> {
                                        Cursor detailCursor = db.query("Coordy", null, "cod_id = ?", new String[]{String.valueOf(randCodId)}, null, null, null);

                                        if(detailCursor !=null && detailCursor.moveToFirst()) {
                                            Intent intent = new Intent(Cody.this, DetailCody.class);
                                            intent.putExtra("cod_id", detailCursor.getInt(detailCursor.getColumnIndexOrThrow("cod_id")));
                                            intent.putExtra("cod_img", detailCursor.getString(detailCursor.getColumnIndexOrThrow("cod_img")));
                                            intent.putExtra("cod_loc", detailCursor.getInt(detailCursor.getColumnIndexOrThrow("cod_loc")));
                                            intent.putExtra("cod_name", detailCursor.getString(detailCursor.getColumnIndexOrThrow("cod_name")));
                                            intent.putExtra("cod_tag", detailCursor.getInt(detailCursor.getColumnIndexOrThrow("cod_tag")));
                                            intent.putExtra("cod_date", detailCursor.getString(detailCursor.getColumnIndexOrThrow("cod_date")));
                                            intent.putExtra("cod_stack", detailCursor.getInt(detailCursor.getColumnIndexOrThrow("cod_stack")));

                                            intent.putExtra("cod_index1", detailCursor.getString(detailCursor.getColumnIndexOrThrow("cod_index1")));
                                            intent.putExtra("cod_index2", detailCursor.getString(detailCursor.getColumnIndexOrThrow("cod_index2")));
                                            intent.putExtra("cod_index3", detailCursor.getString(detailCursor.getColumnIndexOrThrow("cod_index3")));
                                            intent.putExtra("cod_index4", detailCursor.getString(detailCursor.getColumnIndexOrThrow("cod_index4")));
                                            intent.putExtra("cod_index5", detailCursor.getString(detailCursor.getColumnIndexOrThrow("cod_index5")));
                                            intent.putExtra("cod_index6", detailCursor.getString(detailCursor.getColumnIndexOrThrow("cod_index6")));
                                            intent.putExtra("cod_index7", detailCursor.getString(detailCursor.getColumnIndexOrThrow("cod_index7")));
                                            intent.putExtra("cod_index8", detailCursor.getString(detailCursor.getColumnIndexOrThrow("cod_index8")));

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

                            builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            });

                            AlertDialog dialog = builder.create();
                            dialog.show();
                        } else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(Cody.this);
                            builder.setTitle("코디 추천 불가");
                            builder.setMessage("현재 날씨에 적합한 추천 코디가 없습니다.\n더 많은 코디를 등록해 보세요!\n");
                            builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();  // 다이얼로그 닫기
                                }
                            });

                            // 다이얼로그 보여주기
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        }
                    }
                });

                ImageButton btnRandCordy = recView.findViewById(R.id.btnRandCordy);
                btnRandCordy.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(Cody.this);
                        builder.setMessage("현재 날씨에 맞는\n랜덤 코디를 추천 받으시겠습니까?\n\n※ 4가지 항목(상의, 하의, 외투, 신발)만 추천됩니다.")
                                .setPositiveButton("예", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        Intent intent = new Intent(Cody.this, Cody_Recommend.class);
                                        intent.putExtra("rec_click", 1);
                                        startActivity(intent);
                                    }
                                })
                                .setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.dismiss();
                                    }
                                });
                        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialogInterface) {
                                dialog.dismiss(); // Custom Dialog 닫기
                            }
                        });
                        builder.create().show();

                    }
                });
            }
        });
    }

    protected void onResume() {
        super.onResume();
        // SharedPreferences에서 체크박스 상태 로드
        loadCheckboxStates();
    }

    @Override
    public void onWeatherDataResult(String sky_date, String sky_state_date, float temp_date) {
        sky = sky_date;
        sky_state = sky_state_date;
        temp = temp_date;
        Log.d("MainActivity", "날씨 상태: " + sky + ", 강수 상태: " + sky_state + ", 온도: " + temp);
    }


    // 코디 위치 스피너 출력
    private void fillSpinner_cod_loc() {
        Spinner cod_loc_spinner = findViewById(R.id.main_cod_loc);

        List<String> locations = new ArrayList<>();
        cod_loc_value.clear();
        Cursor cursor = db.rawQuery("SELECT cod_loc, cod_loc_name FROM Coordy_Location ORDER BY cod_loc ASC", null);
        int selectedPosition = -1;

        while (cursor.moveToNext()) {
            locations.add(cursor.getString(cursor.getColumnIndex("cod_loc_name")));
            cod_loc_value.add(cursor.getInt(cursor.getColumnIndex("cod_loc")));

            if (cursor.getInt((cursor.getColumnIndex("cod_loc"))) == selected_Cody_LocId) {
                selectedPosition = cod_loc_value.size() - 1;
            }
        }
        cursor.close();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_title, locations);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown);
        cod_loc_spinner.setAdapter(adapter);

        if (selectedPosition != -1) {
            cod_loc_spinner.setSelection(selectedPosition);
        }
    }

    private void Spinner_Selected() {
        Spinner c_loc_spinner = findViewById(R.id.main_cod_loc);

        c_loc_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                selected_Cody_LocId = cod_loc_value.get(position);
                filterDataByQuery(st_sort_cod_id, orderBy_cody_set, search_cod_name, selected_Cody_LocId);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    private Pair<List<Integer>, List<Integer>> ItemCodyImgBtn(int imgCounter){

        List<Integer> imgCounters = new ArrayList<>();
        List<Integer> imgViewCounters = new ArrayList<>();

        GridLayout gridLayout = findViewById(R.id.gl_cody);
        gridLayout.setPadding(0,dpToPx(-28),0,0);
        gridLayout.setRowCount(50);
        gridLayout.setColumnCount(2);

        // 전체 코디 목록
        for (int col = 0; col < 2; col++) {
            FrameLayout frameLayout = new FrameLayout(this);
            GridLayout.LayoutParams frameParams = new GridLayout.LayoutParams();
            frameParams.width = dpToPx(254);
            frameParams.height = dpToPx(285);
            frameParams.setMargins(dpToPx(24), dpToPx(49),  dpToPx(-77), 0);
            frameLayout.setLayoutParams(frameParams);

            GridLayout innerGridLayout = new GridLayout(this);
            innerGridLayout.setRowCount(4);
            innerGridLayout.setColumnCount(2);


            // 내부 이미지 뷰(2X4)
            for (int row = 0; row < 4; row++) {
                for(int gridCol = 0; gridCol < 2; gridCol++) {

                    ImageView gridImgView = new ImageView(this);
                    gridImgView.setBackgroundColor(Color.parseColor("#00ff0000"));
                    gridImgView.setId(imgViewCounter);
                    gridImgView.setScaleType(ImageView.ScaleType.CENTER_CROP);

                    GridLayout.LayoutParams gridImgViewParams = new GridLayout.LayoutParams();
                    gridImgViewParams.width = dpToPx(85);
                    gridImgViewParams.height = dpToPx(61);
                    gridImgViewParams.setMargins(dpToPx(4), dpToPx(4), dpToPx(4), dpToPx(4));
                    gridImgViewParams.rowSpec = GridLayout.spec(row);
                    gridImgViewParams.columnSpec = GridLayout.spec(gridCol);
                    gridImgView.setLayoutParams(gridImgViewParams);

                    innerGridLayout.addView(gridImgView);
                    imgViewCounters.add(imgViewCounter);
                    imgViewCounter++;

                }
            }

            // 대표 이미지(썸네일)
            ImageButton clothImgbtn = new ImageButton(this);
            clothImgbtn.setBackgroundColor(Color.parseColor("#00ff0000"));
            clothImgbtn.setId(imgCounter);
            clothImgbtn.setPadding(0,0,0,0);
            clothImgbtn.setScaleType(ImageView.ScaleType.FIT_XY);

            // GridLayout에 레이아웃 매개변수 설정
            GridLayout.LayoutParams paramsImageButton = new GridLayout.LayoutParams();
            paramsImageButton.width = dpToPx(179);
            paramsImageButton.height = dpToPx(268);
            paramsImageButton.setMargins(dpToPx(4), dpToPx(4), dpToPx(4), dpToPx(4));
            paramsImageButton.rowSpec = GridLayout.spec(imgRow * 2);
            paramsImageButton.columnSpec = GridLayout.spec(col);
            clothImgbtn.setLayoutParams(paramsImageButton);

            frameLayout.addView(innerGridLayout);
            frameLayout.addView(clothImgbtn);
            gridLayout.addView(frameLayout);
            imgCounters.add(imgCounter);
            imgCounter++;
        }

        return new Pair<>(imgCounters, imgViewCounters);
    }

    private List<Integer> ItemCodyTag(int tagCounter) {
        List<Integer> tagCounters = new ArrayList<>();

        GridLayout gridLayout = findViewById(R.id.gl_cody);

        for (int col = 0; col < 2; col++) {
            // TextView 생성 및 설정
            TextView clothTag = new TextView(this);
            clothTag.setBackgroundColor(Color.parseColor("#00ff0000"));
            clothTag.setGravity(Gravity.CENTER);
            float dpValue = 16;
            float fixedTextSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, getResources().getDisplayMetrics());
            clothTag.setTextSize(TypedValue.COMPLEX_UNIT_PX, fixedTextSize);
            clothTag.setId(tagCounter);


            GridLayout.LayoutParams paramsTextView = new GridLayout.LayoutParams();
            paramsTextView.width = dpToPx(179);
            paramsTextView.height = dpToPx(31);
            paramsTextView.setMargins(dpToPx(28), 0, dpToPx(-77), 0);
            paramsTextView.rowSpec = GridLayout.spec(tagRow * 2 + 1);
            paramsTextView.columnSpec = GridLayout.spec(col);
            clothTag.setLayoutParams(paramsTextView);

            // GridLayout에 뷰 추가
            gridLayout.addView(clothTag);
            tagCounters.add(tagCounter);
            tagCounter++;
        }

        return tagCounters;
    }

    private void filterDataByQuery(ArrayList<Integer> sort_cod_id, String orderBy, String search_cod_name, int selectedLocId) {
        String selection = null;
        String[] selectionArgs = null;
        StringBuilder cod_id_builder = new StringBuilder();

        if (selectedLocId > 1) {
            cod_id_builder.append("cod_loc = ?");  // cod_loc 값 필터링
            if ((search_cod_name != null && !search_cod_name.isEmpty()) || (sort_cod_id != null && !sort_cod_id.isEmpty())) {
                cod_id_builder.append(" AND ");
            }
        }

        if (search_cod_name != null && !search_cod_name.isEmpty()) {
            if (sort_cod_id != null && !sort_cod_id.isEmpty()) {
                selectionArgs = new String[sort_cod_id.size() + 1 + (selectedLocId > 1 ? 1 : 0)];
                cod_id_builder.append("cod_id IN (");

                for (int i = 0; i < sort_cod_id.size(); i++) {
                    cod_id_builder.append("?");
                    if (i < sort_cod_id.size() - 1) {
                        cod_id_builder.append(", ");
                    }
                }
                cod_id_builder.append(") AND cod_name LIKE ?");

                if (selectedLocId > 1) {
                    selectionArgs[0] = String.valueOf(selectedLocId);
                    for (int i = 0; i < sort_cod_id.size(); i++) {
                        selectionArgs[i + 1] = String.valueOf(sort_cod_id.get(i));
                    }
                    selectionArgs[sort_cod_id.size() + 1] = "%" + search_cod_name + "%";
                } else {
                    for (int i = 0; i < sort_cod_id.size(); i++) {
                        selectionArgs[i] = String.valueOf(sort_cod_id.get(i));
                    }

                    selectionArgs[sort_cod_id.size()] = "%" + search_cod_name + "%";
                }
                selection = cod_id_builder.toString();
            } else {
                selection = "cod_name LIKE ?";
                if (selectedLocId > 1) {
                    selection = "cod_loc = ? AND " + selection;
                    selectionArgs = new String[]{String.valueOf(selectedLocId), "%" + search_cod_name + "%"};
                } else {
                    selectionArgs = new String[]{"%" + search_cod_name + "%"};
                }
            }
        } else if (sort_cod_id != null && !sort_cod_id.isEmpty()) {
            selectionArgs = new String[sort_cod_id.size() + (selectedLocId > 1 ? 1 : 0)];

            cod_id_builder.append("cod_id IN (");
            for (int i = 0; i < sort_cod_id.size(); i++) {
                cod_id_builder.append("?");
                if (i < sort_cod_id.size() - 1) {
                    cod_id_builder.append(", ");
                }
            }
            cod_id_builder.append(")");

            selection = cod_id_builder.toString();

            if (selectedLocId > 1) {
                selectionArgs[0] = String.valueOf(selectedLocId);
                for (int i = 0; i < sort_cod_id.size(); i++) {
                    selectionArgs[i + 1] = String.valueOf(sort_cod_id.get(i));
                }
            } else {
                for (int i = 0; i < sort_cod_id.size(); i++) {
                    selectionArgs[i] = String.valueOf(sort_cod_id.get(i));
                }
            }
        } else if (selectedLocId > 1) {
            selection = "cod_loc = ?";
            selectionArgs = new String[]{String.valueOf(selectedLocId)};
        }

        Log.d("filterdata", String.valueOf(selection));
        Log.d("filterdata", Arrays.toString(selectionArgs));
        // Coordy 테이블에서 필터링된 데이터를 가져옴
        Cursor cursor = db.query("Coordy", null, selection, selectionArgs, null, null, orderBy);

        ArrayList<Integer> filter_cod_id = new ArrayList<>();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                int cod_id = cursor.getInt(cursor.getColumnIndexOrThrow("cod_id"));
                filter_cod_id.add(cod_id);
            } while (cursor.moveToNext());
        }

        // 기존에 표시된 데이터를 지우고 새로운 데이터를 보여줌
        displayFilteredData(cursor, filter_cod_id);
    }

    private void displayFilteredData(Cursor cursor, ArrayList<Integer> filter_cod_id) {
        int initialImgCounter = 3001;
        int initialTagCounter = 4001;
        int initialImgViewCounter = 5001;
        int imgRow = 0; // 이미지의 행을 관리하는 변수
        int tagRow = 0; // 텍스트의 행을 관리하는 변수


        // GridLayout에서 이전에 추가된 항목들을 모두 제거
        GridLayout gridLayout = findViewById(R.id.gl_cody);
        gridLayout.removeAllViews();

        LinearLayout noneClothes = findViewById(R.id.noneClothes);

        if (cursor == null || cursor.getCount() == 0) {
            noneClothes.setVisibility(View.VISIBLE);
            return;
        } else {
            noneClothes.setVisibility(View.GONE);
        }

        // 커서가 유효하면 해당 코드 실행
        if (cursor != null && cursor.moveToFirst()) {
            int count = cursor.getCount();

            for (int i = 0; i < count; i++) {
                String cod_name = cursor.getString(cursor.getColumnIndexOrThrow("cod_name"));
                String cod_img = cursor.getString(cursor.getColumnIndexOrThrow("cod_img"));
                Bitmap thumbBitmap = BitmapFactory.decodeFile(cod_img);

                StringBuilder filter_builder = new StringBuilder();
                filter_builder.append("cod_id IN (");
                for (int j = 0; j < filter_cod_id.size(); j++) {
                    filter_builder.append("?");
                    if (j < filter_cod_id.size() - 1) {
                        filter_builder.append(", ");
                    }
                }
                filter_builder.append(")");

                String selection = filter_builder.toString();
                String[] selectionArgs = new String[filter_cod_id.size()];

                for (int j = 0; j < filter_cod_id.size(); j++) {
                    selectionArgs[j] = String.valueOf(filter_cod_id.get(j));
                }

                // 각 cod_index에 해당하는 c_img 경로를 가져와 비트맵 생성
                Integer[] cod_indices = new Integer[8];
                for (int idx = 0; idx < 8; idx++) {
                    String columnName = "cod_index" + (idx + 1);
                    int columnIndex = cursor.getColumnIndex(columnName);
                    if (columnIndex != -1 && !cursor.isNull(columnIndex)) {
                        // cod_index 값 가져오기
                        cod_indices[idx] = cursor.getInt(columnIndex);
                    } else {
                        cod_indices[idx] = null;
                    }
                }

                Bitmap[] bitmaps = new Bitmap[8];
                for (int j = 0; j < 8; j++) {
                    if (cod_indices[j] != null) {
                        // Main_Closet에서 c_id로 c_img 경로 가져오기
                        Cursor closetCursor = db.query("Main_Closet", new String[]{"c_img"},
                                "c_id=?", new String[]{String.valueOf(cod_indices[j])}, null, null, null);
                        if (closetCursor != null && closetCursor.moveToFirst()) {
                            String c_img = closetCursor.getString(closetCursor.getColumnIndexOrThrow("c_img"));
                            Bitmap bitmap = BitmapFactory.decodeFile(c_img);
                            bitmaps[j] = bitmap;
                            closetCursor.close();
                        } else {
                            bitmaps[j] = null;
                        }
                    } else {
                        bitmaps[j] = null;
                    }
                }

                int imgCounter = initialImgCounter + i; // ImageButton의 ID
                int tagCounter = initialTagCounter + i; // TextView의 ID
                int imgViewCounter = initialImgViewCounter + i;

                gridLayout.setRowCount(50);
                gridLayout.setColumnCount(2);

                DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
                int screenWidth = displayMetrics.widthPixels;
                int screenHeight = displayMetrics.heightPixels;

                int margin = (int)(screenWidth * 0.05f);
                int imgMargin = (int)(screenWidth * 0.037f);
                int frameWidth = (int)((screenWidth - margin * 3) / 2); // 2개의 열과 마진을 고려하여 너비 계산
                int frameHeight = (int)(screenHeight * 0.4f); // 기존 높이 비율 유지

                FrameLayout frameLayout = new FrameLayout(this);
                GridLayout.LayoutParams frameParams = new GridLayout.LayoutParams();
                frameParams.width = frameWidth;
                frameParams.height = frameHeight;
                frameParams.setMargins(imgMargin, imgMargin * 3, imgMargin, 0); // 좌우 마진 설정
                frameParams.rowSpec = GridLayout.spec(imgRow * 2);
                frameParams.columnSpec = GridLayout.spec(i % 2);
                frameLayout.setLayoutParams(frameParams);

                ImageButton imageButton = new ImageButton(this);
                imageButton.setId(imgCounter);
                imageButton.setScaleType(ImageView.ScaleType.FIT_XY);
                imageButton.setPadding(0, 0, 0, 0);
                imageButton.setImageBitmap(thumbBitmap);
                imageButton.setBackgroundColor(Color.parseColor("#00ff0000"));

                GridLayout innerGridLayout = new GridLayout(this);
                innerGridLayout.setRowCount(4);
                innerGridLayout.setColumnCount(2);

                int totalInnerColumns = 2;
                int totalInnerRows = 4;
                int innerImageViewMargin = (int)(frameWidth * 0.005f);

                int innerImageViewWidth = (int)((frameWidth - innerImageViewMargin * (totalInnerColumns + 1)) / totalInnerColumns * 0.93);
                int innerImageViewHeight = (int)((frameHeight - innerImageViewMargin * (totalInnerRows + 1)) / totalInnerRows * 0.7);

                List<ImageView> innerImageViews = new ArrayList<>();
                for (int row = 0; row < totalInnerRows; row++) {
                    for (int gridCol = 0; gridCol < totalInnerColumns; gridCol++) {
                        ImageView gridImgView = new ImageView(this);
                        gridImgView.setBackgroundColor(Color.parseColor("#00ff0000"));
                        gridImgView.setId(imgViewCounter++);
                        gridImgView.setScaleType(ImageView.ScaleType.CENTER_CROP);

                        GridLayout.LayoutParams gridImgViewParams = new GridLayout.LayoutParams();
                        gridImgViewParams.width = innerImageViewWidth;
                        gridImgViewParams.height = innerImageViewHeight;
                        gridImgViewParams.setMargins(innerImageViewMargin * 5, innerImageViewMargin * 5,
                                innerImageViewMargin * 5, innerImageViewMargin * 5);
                        gridImgViewParams.rowSpec = GridLayout.spec(row);
                        gridImgViewParams.columnSpec = GridLayout.spec(gridCol);
                        gridImgView.setLayoutParams(gridImgViewParams);

                        innerGridLayout.addView(gridImgView);
                        innerImageViews.add(gridImgView);
                    }
                }

                TextView textView = new TextView(this);
                textView.setId(tagCounter);
                textView.setBackgroundColor(Color.parseColor("#00ff0000"));
                textView.setGravity(Gravity.CENTER);
                float dpValue = 16;
                float fixedTextSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, getResources().getDisplayMetrics());
                textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, fixedTextSize);
                textView.setText(cod_name);

                frameLayout.addView(innerGridLayout);
                gridLayout.addView(frameLayout);

                int imageButtonWidth = (int)(screenWidth * 0.41f);
                int imageButtonHeight = (int)(frameHeight * 0.82f);
                int imgBtnLeftMargin = (int)(screenWidth * 0.045f);
                int imgBtnTopMargin = (int)(screenHeight * 0.056f);

                GridLayout.LayoutParams paramsImageButton = new GridLayout.LayoutParams();
                paramsImageButton.width = imageButtonWidth;
                paramsImageButton.height = imageButtonHeight;
                paramsImageButton.setMargins(imgBtnLeftMargin, imgBtnTopMargin, margin / 2, 0);
                paramsImageButton.rowSpec = GridLayout.spec(imgRow * 2);
                paramsImageButton.columnSpec = GridLayout.spec(i % 2);

                GridLayout.LayoutParams paramsTextView = new GridLayout.LayoutParams();
                paramsTextView.width = imageButtonWidth;
                paramsTextView.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                paramsTextView.setMargins(margin, -margin * 2, margin / 2, 0); // 마진을 줄여서 여백 감소
                paramsTextView.rowSpec = GridLayout.spec(tagRow * 2 + 1);
                paramsTextView.columnSpec = GridLayout.spec(i % 2);

                gridLayout.addView(imageButton, paramsImageButton);
                gridLayout.addView(textView, paramsTextView);

                for (int index = 0; index < bitmaps.length && index < innerImageViews.size(); index++) {
                    ImageView imgView = innerImageViews.get(index);
                    if (bitmaps[index] != null) {
                        imgView.setImageBitmap(bitmaps[index]);
                        imgView.setTag(cod_indices[index]);
                    } else {
                        imgView.setImageBitmap(null);
                    }
                }

                // Set OnClickListener for imageButton
                int finalI = i;
                imageButton.setOnClickListener(view -> {
                    new Thread(() -> {
                        Cursor detailCursor = db.query("Coordy", null, selection, selectionArgs, null, null, null);
                        if (detailCursor != null && detailCursor.moveToPosition(finalI)) {
                            Intent intent = new Intent(Cody.this, DetailCody.class);
                            intent.putExtra("cod_id", detailCursor.getInt(detailCursor.getColumnIndexOrThrow("cod_id")));
                            intent.putExtra("cod_img", detailCursor.getString(detailCursor.getColumnIndexOrThrow("cod_img")));
                            intent.putExtra("cod_loc", detailCursor.getInt(detailCursor.getColumnIndexOrThrow("cod_loc")));
                            intent.putExtra("cod_name", detailCursor.getString(detailCursor.getColumnIndexOrThrow("cod_name")));
                            intent.putExtra("cod_tag", detailCursor.getInt(detailCursor.getColumnIndexOrThrow("cod_tag")));
                            intent.putExtra("cod_date", detailCursor.getString(detailCursor.getColumnIndexOrThrow("cod_date")));
                            intent.putExtra("cod_stack", detailCursor.getInt(detailCursor.getColumnIndexOrThrow("cod_stack")));

                            intent.putExtra("cod_index1", detailCursor.getString(detailCursor.getColumnIndexOrThrow("cod_index1")));
                            intent.putExtra("cod_index2", detailCursor.getString(detailCursor.getColumnIndexOrThrow("cod_index2")));
                            intent.putExtra("cod_index3", detailCursor.getString(detailCursor.getColumnIndexOrThrow("cod_index3")));
                            intent.putExtra("cod_index4", detailCursor.getString(detailCursor.getColumnIndexOrThrow("cod_index4")));
                            intent.putExtra("cod_index5", detailCursor.getString(detailCursor.getColumnIndexOrThrow("cod_index5")));
                            intent.putExtra("cod_index6", detailCursor.getString(detailCursor.getColumnIndexOrThrow("cod_index6")));
                            intent.putExtra("cod_index7", detailCursor.getString(detailCursor.getColumnIndexOrThrow("cod_index7")));
                            intent.putExtra("cod_index8", detailCursor.getString(detailCursor.getColumnIndexOrThrow("cod_index8")));

                            runOnUiThread(() -> {
                                startActivity(intent);
                                detailCursor.close();
                            });
                        } else if (detailCursor != null) {
                            detailCursor.close();
                        }
                    }).start();
                });

                // 행(row) 계산: 2개의 항목이 추가될 때마다 다음 행으로 이동
                if ((imgCounter - 3000) % 2 == 0) {
                    imgRow++;
                    tagRow++;
                }

                // 커서 이동
                cursor.moveToNext();
            }
            cursor.close();
        }
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

    private void clearCheckboxStatesPreferences() {
        SharedPreferences sharedPref = getSharedPreferences("CheckboxStates", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.clear();
        editor.apply();
    }

    private void loadCheckboxStates() {
        SharedPreferences sharedPref = getSharedPreferences("CheckboxStates", Context.MODE_PRIVATE);
        Map<String, ?> allEntries = sharedPref.getAll();
        checkboxStates.clear();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            int key = Integer.parseInt(entry.getKey());
            boolean value = (Boolean) entry.getValue();
            checkboxStates.put(key, value);
        }
    }

    private void saveCheckboxStates() {
        SharedPreferences sharedPref = getSharedPreferences("CheckboxStates", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        for (Map.Entry<Integer, Boolean> entry : checkboxStates.entrySet()) {
            editor.putBoolean(String.valueOf(entry.getKey()), entry.getValue());
        }

        editor.apply();
    }
    public void updateLocationAndWeather(TextView textView){

        double[] location = gpsHelper.getCurrentLocation();
        double latitude = location[0];
        double longitude = location[1];

        String address = gpsHelper.getCurrentAddress(latitude, longitude);


        String[] local = address.split(" ");
        String localDong ="";
        int a = 4;
        int b = 3;
        String localTwo;
        if (local[1].indexOf("서울")!= -1) {
            a = 3;
            b = 2;
        }
        else {
            a = 4;
            b = 3;
        }
                    /*if (local[a].indexOf("로")!= -1) {
                        local[a] = local[a].replace("로","");
                        localDong =local[a]+"동";
                    }
                    else {
                        localDong =local[a];
                    }*/
        localTwo = local[a].substring(0, 2);
        String localName = localTwo;
        textView.setText(local[b]+" "+local[a]);
        String[] gridCoordinates = excelReader.readExcel(localName);
        String x = gridCoordinates[0];
        String y = gridCoordinates[1];
        if (x=="0"&&y=="0"){
            Toast.makeText(Cody.this, "등록된 위치가 없어 위치와 함께 문의 바랍니다", Toast.LENGTH_LONG).show();
            x = "60";
            y = "127";
        }
        System.out.println("격자값 x: " + x + ", y: " + y);
        //날씨 재동기화
        weatherTextView = findViewById(R.id.weatherDegree);
        imageViewIcon = findViewById(R.id.btnWeather);
        WeatherData wd = new WeatherData(weatherTextView,imageViewIcon, null);
        wd.fetchWeather(TimeHelper.getDate(), TimeHelper.getTime(), x, y);  // 비동기적으로 날씨 데이터를 가져옴

        //스테틱으로 값 유지
        GPSImpo.printingGPS = local[b];
        GPSImpo.printingGPS2 = local[a];
        GPSImpo.weatherX = x;
        GPSImpo.weatherY = y;

    }

}
