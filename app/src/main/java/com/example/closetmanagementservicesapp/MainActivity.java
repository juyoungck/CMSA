package com.example.closetmanagementservicesapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
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
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.Button;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.view.View;
import android.widget.Toast;


import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.LabelVisibilityMode;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.navigation.NavigationBarView;

import java.io.IOException;
import java.sql.Array;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {
    private DBHelper dbHelper;
    private SQLiteDatabase db;
    private static List<Integer> c_loc_value = new ArrayList<>();

    String date = "", time = "";
    String x = "55", y = "127";

    TextView weatherTextView,timeNow;
    ImageView imageViewIcon;

    private GpsHelper gpsHelper;
    private ExcelReader excelReader;

    private GridLayout gridLayout;
    private int imgCounter = 1001;
    private int tagCounter = 2001;
    private int imgRow = 0;
    private int tagRow = 0;

    private static Boolean BasicLocationLoad = true;
    private static boolean isSpinnerValueChanged = false;
    private static int selected_Clothes_LocId = 1;
    private static Boolean Clothes_FilterDataLoad = false;
    private static Boolean ClickSearchView = false;
    private static ArrayList<Integer> st_sort_c_id = null;
    private static String orderBy_Clothes_set = null;
    private static String search_c_name = null;

    private HashMap<Integer, Boolean> checkboxStates = new HashMap<>();
    private static boolean isAppStarted = false;


    BottomNavigationView bottomNavigationView;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!isAppStarted) {
            clearCheckboxStatesPreferences(); // 앱이 처음 시작되었으므로 정렬 설정 초기화
            isAppStarted = true; // 앱이 시작되었음을 표시
        }

        loadCheckboxStates();

        // DB OPEN
        dbHelper = MyApplication.getDbHelper();
        db = dbHelper.getWritableDatabase();

        gridLayout = findViewById(R.id.gl_main);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        // 중앙 이미지 레이아웃과 이미지 호출
        List<Integer> imgCounterList = ItemAddImg(imgCounter);

        // 중앙 태그 레이아웃과 태그 호출
        List<Integer> tagCounterList = ItemAddTag(tagCounter);

        // 기본 옷장, 코디 위치 추가
        basicLocation(BasicLocationLoad);

        // 옷장 위치 스피너 출력
        fillSpinner_c_loc();
        Spinner_Selected();

        if (ClickSearchView) {

        }

        if (!Clothes_FilterDataLoad) {
            displayDataCloset();
        } else if (Clothes_FilterDataLoad) {
            filterDataByQuery(st_sort_c_id, orderBy_Clothes_set, search_c_name, selected_Clothes_LocId);
        }

        if (isSpinnerValueChanged) {
            Clothes_FilterDataLoad = true;
        }

        bottomNavigationView.setLabelVisibilityMode(NavigationBarView.LABEL_VISIBILITY_LABELED);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                bottomNavigationView.setItemBackgroundResource(android.R.color.transparent);

                if (item.isChecked()) {
                    return false;
                }

                item.setCheckable(false);
                item.setChecked(true);

                if (itemId == R.id.btnCody) {
                    bottomNavigationView.setItemBackgroundResource(android.R.color.transparent);
                    startActivity(new Intent(MainActivity.this, Cody.class));
                    overridePendingTransition(0, 0);

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
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(MainActivity.this);
                View tabView = LayoutInflater.from(MainActivity.this).inflate(R.layout.tab_sort_closet, null);

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
                TabSort_Closet tabsort = new TabSort_Closet(getApplicationContext(), bottomSheetDialog, new TabSortCallback() {
                    @Override
                    public void onSortResult(ArrayList<Integer> sort_c_id, String orderBy) {
                        // 데이터 출력을 위한 메서드
                        st_sort_c_id = sort_c_id;
                        orderBy_Clothes_set = orderBy;
                        Clothes_FilterDataLoad = true;
                        filterDataByQuery(st_sort_c_id, orderBy_Clothes_set, search_c_name, selected_Clothes_LocId);
                    }
                }, checkboxStates);

                // 정렬 확인 버튼
                tabsort.sortApply(tabView);

                bottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        // TabSort_Closet에서 체크박스 상태 가져와서 저장
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
                Dialog dialog = new Dialog(MainActivity.this);
                View modifyView = LayoutInflater.from(MainActivity.this).inflate(R.layout.tab_modify, null);

                dialog.setContentView(modifyView);
                dialog.show();

                WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
                params.width=WindowManager.LayoutParams.WRAP_CONTENT;
                params.height=1200;
                dialog.getWindow().setAttributes(params);

                ImageButton tabmodifyClose = modifyView.findViewById(R.id.tabmodifyClose);
                tabmodifyClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                TabModify tabModify = new TabModify(MainActivity.this);

                tabModify.modifyButton(modifyView);

                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        recreate();  // 액티비티 재생성
                    }
                });
            }
        });

        //날씨, gps 코드
        /*long now = System.currentTimeMillis();
        Date mDate = new Date(now);*/

        SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyyMMdd", Locale.KOREA);
        SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("HH", Locale.KOREA);
        SimpleDateFormat simpleDateFormat3 = new SimpleDateFormat("HH:mm", Locale.KOREA);
        TimeZone KoreaTime = TimeZone.getTimeZone("Asia/Seoul");
        simpleDateFormat1.setTimeZone(KoreaTime);
        simpleDateFormat2.setTimeZone(KoreaTime);
        simpleDateFormat3.setTimeZone(KoreaTime);
        Date date = new Date();
        Log.d("시간",simpleDateFormat1.format(date));
        Log.d("시간",simpleDateFormat2.format(date));
        Log.d("시간",simpleDateFormat3.format(date));

        String getDate = simpleDateFormat1.format(date);
        String getTime =  simpleDateFormat2.format(date)+ "00";

        gpsHelper = new GpsHelper(this);
        excelReader = new ExcelReader(this);

        gpsHelper.initializeGps();

        final Button textview_address = findViewById(R.id.refresh);

        ImageButton ShowLocationButton = findViewById(R.id.btnLocation);

        timeNow = findViewById(R.id.timeNow);
        morningAfternoon ma = new morningAfternoon(simpleDateFormat3.format(date));
        String abc = ma.asd();
        timeNow.setText(abc);

        ShowLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                try {
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
                    textview_address.setText(local[b]+" "+local[a]);
                    String[] gridCoordinates = excelReader.readExcel(localName);
                    String x = gridCoordinates[0];
                    String y = gridCoordinates[1];
                    if (x=="0"&&y=="0"){
                        Toast.makeText(MainActivity.this, "등록된 위치가 없어 위치와 함께 문의 바랍니다", Toast.LENGTH_LONG).show();
                        x = "55";
                        y = "127";
                    }
                    System.out.println("격자값 x: " + x + ", y: " + y);
                    //날씨 재동기화
                    weatherTextView = findViewById(R.id.weatherDegree);
                    imageViewIcon = findViewById(R.id.btnWeather);
                    WeatherData wd = new WeatherData(weatherTextView,imageViewIcon, null);
                    wd.fetchWeather(getDate, getTime, x, y);  // 비동기적으로 날씨 데이터를 가져옴

                    //스테틱으로 값 유지
                    GPSImpo.printingGPS = local[b];
                    GPSImpo.printingGPS2 = local[a];
                    GPSImpo.weatherX = x;
                    GPSImpo.weatherY = y;
                }
                catch (Exception e){
                    Toast.makeText(MainActivity.this, "오류가 발생했습니다.", Toast.LENGTH_LONG).show();
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
        wd.fetchWeather(getDate, getTime, x, y);  // 비동기적으로 날씨 데이터를 가져옴
        SearchView searchView = findViewById(R.id.btnSearch);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // 검색어가 제출되면 실행될 코드 (제출 후 엔터 시)
                search_c_name = query;
                Clothes_FilterDataLoad = true;
                filterDataByQuery(st_sort_c_id, orderBy_Clothes_set, query, selected_Clothes_LocId);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // 검색어가 변경될 때마다 필터링된 데이터를 보여주는 메소드 호출
                Log.d("SearchView", newText);
                search_c_name = newText;
                Clothes_FilterDataLoad = true;
                filterDataByQuery(st_sort_c_id, orderBy_Clothes_set, newText, selected_Clothes_LocId);
                return false;
            }
        });
    }

    protected void onResume() {
        super.onResume();
        // SharedPreferences에서 체크박스 상태 로드
        loadCheckboxStates();
    }

    // 임의 데이터 출력, 추후 출력 코드 작성 시 아래와 비슷하게 작성할 예정
    private void displayDataCloset() {
        // Main_Closet 테이블의 모든 값을 불러옴
        Cursor cursor = db.query("Main_Closet", null, null, null, null, null, null);

        int initialImgCounter = 1001;
        int initialTagCounter = 2001;

        // 커서 위치 유효성 검사 후 문제가 없으면 해당 코드 실행
        if (cursor != null && cursor.moveToFirst()) {
            int count = cursor.getCount();

            GridLayout gridLayout = findViewById(R.id.gl_main);

            for (int i = 0; i < count; i++) {
                String c_name = cursor.getString(cursor.getColumnIndexOrThrow("c_name"));
                String c_img = cursor.getString(cursor.getColumnIndexOrThrow("c_img"));
                Bitmap bitmap = BitmapFactory.decodeFile(c_img);

                int imgCounter = initialImgCounter + i; // ImageButton의 ID
                int tagCounter = initialTagCounter + i; // TextView의 ID

                ImageButton imageButton = (ImageButton) findViewById(imgCounter);
                TextView textView = (TextView) findViewById(tagCounter);

                // 유효성 검사 후 문제가 없으면 해당 코드 실행 (현재 오류 발생 중, 추후 수정)
                if (textView != null && imageButton != null) {
                    textView.setText(c_name);
                    imageButton.setImageBitmap(bitmap);

                    if ((imgCounter - 1000) % 3 == 0) {
                        imgRow++;
                        imgCounter++;
                        List<Integer> imgCounterList = ItemAddImg(imgCounter);
                        imgCounter += imgCounterList.size();
                    }

                    if ((tagCounter - 2000) % 3 == 0) {
                        tagRow++;
                        tagCounter++;
                        List<Integer> tagCounterList = ItemAddTag(tagCounter);
                        tagCounter += tagCounterList.size();
                    }

                    int finalI = i;

                    // 이미지 버튼을 클릭하면 해당하는 컬럼의 모든 데이터를 볼 수 있는 탭으로 이동한다. (DetailActivity.java와 activity_detail.xml 파일 참고)
                    imageButton.setOnClickListener(view -> {
                        new Thread(() -> {
                            Cursor detailCursor = db.query("Main_Closet", null, null, null, null, null, null);
                            if (detailCursor != null && detailCursor.moveToPosition(finalI)) {
                                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
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
                                detailCursor.close(); // 커서가 유효하지 않을 경우에도 닫기
                            }
                        }).start();
                    });
                }
                cursor.moveToNext();
            }
            cursor.close();
        }
    }

    // 옷장 위치 스피너 출력
    private void fillSpinner_c_loc() {
        Spinner c_loc_spinner = findViewById(R.id.main_c_loc);

        List<String> locations = new ArrayList<>();
        c_loc_value.clear();

        Cursor cursor = db.rawQuery("SELECT c_loc, c_loc_name FROM Closet_Location ORDER BY c_loc ASC", null);
        int selectedPosition = -1;

        while (cursor.moveToNext()) {
            locations.add(cursor.getString(cursor.getColumnIndex("c_loc_name")));
            c_loc_value.add(cursor.getInt(cursor.getColumnIndex("c_loc")));

            if (cursor.getInt((cursor.getColumnIndex("c_loc"))) == selected_Clothes_LocId) {
                selectedPosition = c_loc_value.size() - 1;
            }
        }
        cursor.close();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_title, locations);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown);
        c_loc_spinner.setAdapter(adapter);

        if (selectedPosition != -1) {
            c_loc_spinner.setSelection(selectedPosition);
        }
    }

    private void Spinner_Selected() {
        Spinner c_loc_spinner = findViewById(R.id.main_c_loc);

        c_loc_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                selected_Clothes_LocId = c_loc_value.get(position);
                isSpinnerValueChanged = true;
                filterDataByQuery(st_sort_c_id, orderBy_Clothes_set, search_c_name, selected_Clothes_LocId);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
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

    private void basicLocation(boolean basicLocationLoad) {
        if (basicLocationLoad) {
            ContentValues values = new ContentValues();
            values.put("c_loc", 1);
            values.put("c_loc_name", "전체");
            values.put("c_loc_date", getToday());
            db.insert("Closet_Location", null, values);

            values = new ContentValues();
            values.put("cod_loc", 1);
            values.put("cod_loc_name", "기본");
            values.put("cod_loc_date", getToday());
            db.insert("Coordy_Location", null, values);

            BasicLocationLoad = false;
        }
    }

    private String getToday() {
        DateFormat Today = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.KOREAN);
        TimeZone KoreaTime = TimeZone.getTimeZone("Asia/Seoul");
        Today.setTimeZone(KoreaTime);

        Date date = new Date();

        return Today.format(date);
    }

    //위치 권한 코드
    public void onRequestPermissionsResult(int permsRequestCode, @NonNull String[] permissions, @NonNull int[] grandResults) {
        super.onRequestPermissionsResult(permsRequestCode, permissions, grandResults);
        gpsHelper.checkRunTimePermission();
    }

    private List<Integer> ItemAddImg(int imgCounter){

        List<Integer> imgCounters = new ArrayList<>();

        GridLayout gridLayout = findViewById(R.id.gl_main);
        gridLayout.setRowCount(50);
        gridLayout.setColumnCount(3);


            for (int col = 0; col < 3; col++) {
                // ImageButton
                ImageButton clothImgbtn = new ImageButton(this);
                clothImgbtn.setBackgroundColor(Color.parseColor("#00ff0000"));
                clothImgbtn.setPadding(150, 150, 150, 150);
                clothImgbtn.setId(imgCounter);

                // GridLayout에 레이아웃 매개변수 설정
                GridLayout.LayoutParams paramsImageButton = new GridLayout.LayoutParams();
                paramsImageButton.width = 300;
                paramsImageButton.height = 300;
                paramsImageButton.setMargins(30, 45, 30, 0);
                paramsImageButton.rowSpec = GridLayout.spec(imgRow * 2);
                paramsImageButton.columnSpec = GridLayout.spec(col);
                clothImgbtn.setLayoutParams(paramsImageButton);

                // GridLayout에 뷰 추가
                gridLayout.addView(clothImgbtn);
                imgCounters.add(imgCounter);
                imgCounter++;
            }


        return imgCounters;
    }

    private List<Integer> ItemAddTag(int tagCounter) {

        List<Integer> tagCounters = new ArrayList<>();

        GridLayout gridLayout = findViewById(R.id.gl_main);
        gridLayout.setRowCount(50);
        gridLayout.setColumnCount(3);


            for (int col = 0; col < 3; col++) {

                // TextView 생성 및 설정
                TextView clothTag = new TextView(this);
                clothTag.setBackgroundColor(Color.parseColor("#00ff0000"));
                clothTag.setGravity(Gravity.CENTER);
                float dpValue = 16;
                float fixedTextSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, getResources().getDisplayMetrics());
                clothTag.setTextSize(TypedValue.COMPLEX_UNIT_PX, fixedTextSize);
                clothTag.setId(tagCounter);

                GridLayout.LayoutParams paramsTextView = new GridLayout.LayoutParams();
                paramsTextView.width = 300;
                paramsTextView.height = 75;
                paramsTextView.setMargins(30, 0, 30, 0);
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

    // 쿼리로 데이터를 필터링하는 함수
    private void filterDataByQuery(ArrayList<Integer> sort_c_id, String orderBy, String search_c_name, int selectedLocId) {
        // 검색어가 없으면 전체 데이터를 다시 불러옴
        String selection = null;
        String[] selectionArgs = null;
        StringBuilder c_id_builder = new StringBuilder();

        if (selectedLocId > 1) {
            c_id_builder.append("c_loc = ?");  // c_loc 값 필터링
            if ((search_c_name != null && !search_c_name.isEmpty()) || (sort_c_id != null && !sort_c_id.isEmpty())) {
                c_id_builder.append(" AND ");
            }
        }

        if (search_c_name != null && !search_c_name.isEmpty()) {
            if (sort_c_id != null && !sort_c_id.isEmpty()) {
                selectionArgs = new String[sort_c_id.size() + 1 + (selectedLocId > 1 ? 1 : 0)];

                c_id_builder.append("c_id IN (");
                for (int i = 0; i < sort_c_id.size(); i++) {
                    c_id_builder.append("?");
                    if (i < sort_c_id.size() - 1) {
                        c_id_builder.append(", ");
                    }
                }
                c_id_builder.append(") AND c_name LIKE ?");

                if (selectedLocId > 1) {
                    selectionArgs[0] = String.valueOf(selectedLocId);
                    for (int i = 0; i < sort_c_id.size(); i++) {
                        selectionArgs[i + 1] = String.valueOf(sort_c_id.get(i));
                    }
                    selectionArgs[sort_c_id.size() + 1] = "%" + search_c_name + "%";
                } else {
                    for (int i = 0; i < sort_c_id.size(); i++) {
                        selectionArgs[i] = String.valueOf(sort_c_id.get(i));
                    }

                    selectionArgs[sort_c_id.size()] = "%" + search_c_name + "%";
                }
                selection = c_id_builder.toString();
            } else {
                selection = "c_name LIKE ?";
                if (selectedLocId > 1) {
                    selection = "c_loc = ? AND " + selection;
                    selectionArgs = new String[]{String.valueOf(selectedLocId), "%" + search_c_name + "%"};
                } else {
                    selectionArgs = new String[]{"%" + search_c_name + "%"};
                }
            }
        } else if (sort_c_id != null && !sort_c_id.isEmpty()) {
            selectionArgs = new String[sort_c_id.size() + (selectedLocId > 1 ? 1 : 0)];

            c_id_builder.append("c_id IN (");
            for (int i = 0; i < sort_c_id.size(); i++) {
                c_id_builder.append("?");
                if (i < sort_c_id.size() - 1) {
                    c_id_builder.append(", ");
                }
            }
            c_id_builder.append(")");

            selection = c_id_builder.toString();

            if (selectedLocId > 1) {
                selectionArgs[0] = String.valueOf(selectedLocId);
                for (int i = 0; i < sort_c_id.size(); i++) {
                    selectionArgs[i + 1] = String.valueOf(sort_c_id.get(i));
                }
            } else {
                for (int i = 0; i < sort_c_id.size(); i++) {
                    selectionArgs[i] = String.valueOf(sort_c_id.get(i));
                }
            }
        } else if (selectedLocId > 1) {
            selection = "c_loc = ?";
            selectionArgs = new String[]{String.valueOf(selectedLocId)};
        }

        Log.d("filterdata", String.valueOf(selection));
        Log.d("filterdata", Arrays.toString(selectionArgs));
        // Main_Closet 테이블에서 필터링된 데이터를 가져옴
        Cursor cursor = db.query("Main_Closet", null, selection, selectionArgs, null, null, orderBy);

        ArrayList<Integer> filter_c_id = new ArrayList<>();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                int c_id = cursor.getInt(cursor.getColumnIndexOrThrow("c_id"));
                filter_c_id.add(c_id);
            } while (cursor.moveToNext());
        }

        // 기존에 표시된 데이터를 지우고 새로운 데이터를 보여줌
        displayFilteredData(cursor, filter_c_id);
    }

    // 필터링된 데이터를 GridLayout에 표시하는 함수
    private void displayFilteredData(Cursor cursor, ArrayList<Integer> filter_c_id) {
        // 초기 ImageButton 및 TextView ID 설정
        int initialImgCounter = 1001;
        int initialTagCounter = 2001;
        int imgRow = 0; // 이미지의 행을 관리하는 변수
        int tagRow = 0; // 텍스트의 행을 관리하는 변수

        // GridLayout에서 이전에 추가된 항목들을 모두 제거
        GridLayout gridLayout = findViewById(R.id.gl_main);
        gridLayout.removeAllViews();

        // 커서 유효성 확인 후 데이터 처리
        if (cursor != null && cursor.moveToFirst()) {
            int count = cursor.getCount();

            for (int i = 0; i < count; i++) {
                // 데이터베이스에서 c_name과 c_img 값 가져오기
                String c_name = cursor.getString(cursor.getColumnIndexOrThrow("c_name"));
                String c_img = cursor.getString(cursor.getColumnIndexOrThrow("c_img"));

                Bitmap bitmap = BitmapFactory.decodeFile(c_img);

                // ImageButton 및 TextView ID 설정
                int imgCounter = initialImgCounter + i;
                int tagCounter = initialTagCounter + i;

                // ImageButton 및 TextView 생성
                ImageButton imageButton = new ImageButton(this);
                imageButton.setId(imgCounter);
                imageButton.setImageBitmap(bitmap);

                TextView textView = new TextView(this);
                textView.setId(tagCounter);
                textView.setGravity(Gravity.CENTER);
                float dpValue = 16;
                float fixedTextSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, getResources().getDisplayMetrics());
                textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, fixedTextSize);
                textView.setText(c_name);

                // GridLayout에 레이아웃 매개변수 설정
                GridLayout.LayoutParams paramsImageButton = new GridLayout.LayoutParams();
                GridLayout.LayoutParams paramsTextView = new GridLayout.LayoutParams();

                paramsImageButton.width = 300; // 버튼의 너비 설정
                paramsImageButton.height = 300; // 버튼의 높이 설정
                paramsImageButton.setMargins(30, 45, 30, 0); // 여백 설정
                paramsImageButton.rowSpec = GridLayout.spec(imgRow * 2); // 2행에 이미지 버튼 위치
                paramsImageButton.columnSpec = GridLayout.spec(i % 3); // 3개의 열로 정렬

                paramsTextView.width = 300; // 텍스트뷰의 너비 설정
                paramsTextView.height = 75; // 텍스트뷰의 높이 설정
                paramsTextView.setMargins(30, 0, 30, 0); // 여백 설정
                paramsTextView.rowSpec = GridLayout.spec(tagRow * 2 + 1); // 2행 + 1에 텍스트 위치
                paramsTextView.columnSpec = GridLayout.spec(i % 3); // 3개의 열로 정렬

                // GridLayout에 뷰 추가
                gridLayout.addView(imageButton, paramsImageButton);
                gridLayout.addView(textView, paramsTextView);

                // 이미지 버튼 클릭 시 상세 정보를 볼 수 있도록 클릭 리스너 설정
                int finalI = i; // 람다 표현식 안에서 i를 사용할 수 있도록 final 변수로 변경

                StringBuilder filter_builder = new StringBuilder();
                filter_builder.append("c_id IN (");

                for (int j = 0; j < filter_c_id.size();j++) {
                    filter_builder.append("?");
                    if (j < filter_c_id.size() - 1) {
                        filter_builder.append(", ");
                    }
                }
                filter_builder.append(")");

                String selection = filter_builder.toString();
                String[] selectionArgs = new String[filter_c_id.size()];

                for (int j = 0; j < filter_c_id.size(); j++) {
                    selectionArgs[j] = String.valueOf(filter_c_id.get(j));
                }

                Log.d("clothes_builder", String.valueOf(i));
                imageButton.setOnClickListener(view -> {
                    new Thread(() -> {
                        Cursor detailCursor = db.query("Main_Closet", null, selection, selectionArgs, null, null, null);
                        if (detailCursor != null && detailCursor.moveToPosition(finalI)) {
                            Intent intent = new Intent(MainActivity.this, DetailActivity.class);
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

                            runOnUiThread(() -> {
                                startActivity(intent);
                                detailCursor.close();
                            });
                        } else if (detailCursor != null) {
                            detailCursor.close();
                        }
                    }).start();
                });

                // 행(row) 계산: 3개의 항목이 추가될 때마다 다음 행으로 이동
                if ((imgCounter - 1000) % 3 == 0) {
                    imgRow++;
                    tagRow++;
                }

                // 커서 이동
                cursor.moveToNext();
            }
            cursor.close();
        }
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
}

