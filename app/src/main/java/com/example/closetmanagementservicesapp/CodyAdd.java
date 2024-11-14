package com.example.closetmanagementservicesapp;

        import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

        import android.annotation.SuppressLint;
        import android.app.ActivityManager;
        import android.app.AlertDialog;
        import android.app.Dialog;
        import android.content.ContentValues;
        import android.content.Context;
        import android.content.DialogInterface;
        import android.content.Intent;
        import android.database.Cursor;
        import android.database.sqlite.SQLiteDatabase;
        import android.graphics.Bitmap;
        import android.graphics.BitmapFactory;
        import android.graphics.Canvas;
        import android.graphics.Color;
        import android.graphics.drawable.BitmapDrawable;
        import android.graphics.drawable.Drawable;
        import android.graphics.drawable.VectorDrawable;
        import android.net.Uri;
        import android.os.Bundle;
        import android.util.Log;
        import android.view.Gravity;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.view.WindowManager;
        import android.widget.ArrayAdapter;
        import android.widget.Button;
        import android.widget.CheckBox;
        import android.widget.CompoundButton;
        import android.widget.EditText;
        import android.widget.FrameLayout;
        import android.widget.GridLayout;
        import android.widget.ImageButton;
        import android.widget.LinearLayout;
        import android.widget.Spinner;
        import android.widget.TextView;
        import android.widget.Toast;

        import androidx.annotation.Nullable;
        import androidx.appcompat.app.AppCompatActivity;
        import androidx.core.content.ContextCompat;

        import com.gun0912.tedpermission.PermissionListener;
        import com.gun0912.tedpermission.TedPermission;

        import java.io.ByteArrayOutputStream;
        import java.io.File;
        import java.io.FileOutputStream;
        import java.io.IOException;
        import java.text.DateFormat;
        import java.text.SimpleDateFormat;
        import java.util.ArrayList;
        import java.util.Date;
        import java.util.List;
        import java.util.Locale;
        import java.util.TimeZone;

public class CodyAdd extends AppCompatActivity {
    private DBHelper dbHelper;
    private SQLiteDatabase db;
    private List<Integer> cod_loc_value;
    private CameraUtil_Cody cameraUtil;
    private ImageLoader_Cody imageLoader;
    private static final int CAMERA_REQUEST_CODE = 1;
    private static int selectedButton = -1;
    private GridLayout gridLayout;

    private int imgCounter = 8001;
    private int tagCounter = 9001;
    private int imgRow = 0;
    private int tagRow = 0;
    private ImageButton[] detailCodIndices;
    private Boolean hasImage = false;

    private boolean thumbFromCamera = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.codyadd);

        dbHelper = MyApplication.getDbHelper();
        db = dbHelper.getWritableDatabase();

        fillSpinner_cod_location();

        EditText codyadd_title = (EditText) findViewById(R.id.codyadd_title);

        ImageButton thumb = (ImageButton) findViewById(R.id.add_cod_thumb);

        thumb.setOnClickListener(v -> showImageOptionsDialog());

        detailCodIndices = new ImageButton[]{
                findViewById(R.id.detail_cod_index1),
                findViewById(R.id.detail_cod_index2),
                findViewById(R.id.detail_cod_index3),
                findViewById(R.id.detail_cod_index4),
                findViewById(R.id.detail_cod_index5),
                findViewById(R.id.detail_cod_index6),
                findViewById(R.id.detail_cod_index7),
                findViewById(R.id.detail_cod_index8)
        };

        for (int i = 0; i < 8; i++) {
            final int index = i;
            detailCodIndices[i].setOnClickListener(v -> {
                selectedButton = index;
                Dialog dialog = new Dialog(CodyAdd.this);
                View addView = LayoutInflater.from(CodyAdd.this).inflate(R.layout.cody_addmenu, null);

                dialog.setContentView(addView);
                dialog.show();

                WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
                params.width=WindowManager.LayoutParams.WRAP_CONTENT;
                params.height=1200;
                dialog.getWindow().setAttributes(params);

                gridLayout = addView.findViewById(R.id.gl_add);

                List<Integer> imgCounterList = CodyAddImg(imgCounter);

                List<Integer> tagCounterList = CodyAddTag(tagCounter);

                Cursor cursor = db.query("Main_Closet", null, null, null, null, null, null);

                int initialImgCounter = 8001;
                int initialTagCounter = 9001;

                // 커서 위치 유효성 검사 후 문제가 없으면 해당 코드 실행
                if (cursor != null && cursor.moveToFirst()) {
                    int count = cursor.getCount();

                    for (int j = 0; j < count; j++) {
                        String c_name = cursor.getString(cursor.getColumnIndexOrThrow("c_name"));
                        String c_img = cursor.getString(cursor.getColumnIndexOrThrow("c_img"));
                        int c_id = cursor.getInt(cursor.getColumnIndexOrThrow("c_id"));

                        Bitmap bitmap = BitmapFactory.decodeFile(c_img);

                        int imgCounter = initialImgCounter + j; // ImageButton의 ID
                        int tagCounter = initialTagCounter + j; // TextView의 ID

                        ImageButton imageButton = (ImageButton) addView.findViewById(imgCounter);
                        TextView textView = (TextView) addView.findViewById(tagCounter);

                        // 유효성 검사 후 문제가 없으면 해당 코드 실행 (현재 오류 발생 중, 추후 수정)
                        if (textView != null && imageButton != null) {
                            textView.setText(c_name);

                            imageButton.setImageBitmap(bitmap);
                            imageButton.setTag(c_id);

                            if ((imgCounter - 8000) % 3 == 0) {
                                imgRow++;
                                imgCounter++;
                                imgCounterList = CodyAddImg(imgCounter);
                                imgCounter += imgCounterList.size();
                            }

                            if ((tagCounter - 9000) % 3 == 0) {
                                tagRow++;
                                tagCounter++;
                                tagCounterList = CodyAddTag(tagCounter);
                                tagCounter += tagCounterList.size();
                            }

                            int finalI = j;

                            int finalcId = c_id;
                            imageButton.setOnClickListener(view -> {
                                AlertDialog.Builder builder = new AlertDialog.Builder(addView.getContext());
                                builder.setTitle("옷 등록")        // 제목 설정
                                        .setMessage("해당 옷으로 등록하시겠습니까?")        // 메세지 설정
                                        .setCancelable(false)                               // 뒤로 버튼 클릭시 취소 가능 설정
                                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialogInterface, int whichButton) {

                                                Bitmap bitmap = BitmapFactory.decodeFile(c_img);
                                                if (bitmap != null && selectedButton != -1) {
                                                    // 선택된 이미지 버튼에 비트맵 설정
                                                    detailCodIndices[selectedButton].setImageBitmap(bitmap);

                                                    detailCodIndices[selectedButton].setTag(c_id);

                                                } else {
                                                    Toast.makeText(getApplicationContext(), "이미지를 설정할 수 없습니다.", Toast.LENGTH_SHORT).show();
                                                }

                                                Toast.makeText(getApplicationContext(), "옷 등록이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                                                dialogInterface.dismiss();
                                                dialog.dismiss();
                                            }
                                        })
                                        .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int whichButton) {
                                                //원하는 클릭 이벤트를 넣으시면 됩니다.
                                                dialog.dismiss();
                                            }
                                        });
                                builder.show(); // 알림창 띄우기
                            });
                        }
                        cursor.moveToNext();
                    }
                    cursor.close();
                }

                ImageButton addClose = addView.findViewById(R.id.addClose);
                addClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
            });
        }


        cameraUtil = new CameraUtil_Cody(this, thumb); //화면, 이미지뷰
        imageLoader = new ImageLoader_Cody(this, thumb);
        Intent intent = getIntent();
        String fileName = intent.getStringExtra("codyFileName");
        Boolean fileUpload = intent.getBooleanExtra("fileUpload", false);

        if (fileUpload) {
            String imagePath = "/data/user/0/com.example.closetmanagementservicesapp/files/images/" + fileName;
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            thumb.setImageBitmap(bitmap);
        }

        for (int i = 0; i < 8; i++) {
            Drawable drawable = detailCodIndices[i].getDrawable();
            boolean hasImage = drawable instanceof BitmapDrawable && ((BitmapDrawable) drawable).getBitmap() != null;
        }


        ImageButton btnBack = (ImageButton) findViewById(R.id.btnBack_codyadd);
        btnBack.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(CodyAdd.this, Cody.class);
                startActivity(intent);
                finish();
            }
        });

        Button save = (Button) findViewById(R.id.codyadd_post);               // 등록 버튼

        handleIncomingIntent(getIntent());

        // 등록 버튼을 누르면 실행되는 함수
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = getIntent();
                String CodyFileName = intent.getStringExtra("codyFileName");

                Cursor cursor = db.rawQuery("SELECT MAX(cod_id) FROM Coordy", null);
                int codId = 0;

                if (cursor != null && cursor.moveToFirst()) {
                    codId = cursor.getInt(0);
                    codId++;
                    cursor.close();
                }

                String fileNameCheck = "cody_" + codId + ".png";

                int[] cIdArray = new int[8];

                for (int i = 0; i < detailCodIndices.length; i++) {
                    Drawable drawable = detailCodIndices[i].getDrawable();
                    if (drawable instanceof BitmapDrawable && ((BitmapDrawable) drawable).getBitmap() != null) {
                        hasImage = true; // 이미지가 존재함
                        Object tag = detailCodIndices[i].getTag();
                        if (tag != null) {
                            int c_id = (int) tag;
                            cIdArray[i] = c_id; // 해당 위치에 c_id 저장
                        } else {
                            // c_id가 설정되지 않은 경우 처리
                            Toast.makeText(getApplicationContext(), "선택된 옷의 ID를 가져올 수 없습니다.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    } else {
                        cIdArray[i] = -1; // 이미지가 없는 경우 -1 또는 0 등으로 설정
                    }
                }

                // 8개의 이미지 버튼을 체크
                for (int i = 0; i < detailCodIndices.length; i++) {
                    Drawable drawable = detailCodIndices[i].getDrawable();
                    if (drawable instanceof BitmapDrawable && ((BitmapDrawable) drawable).getBitmap() != null) {
                        hasImage = true; // 비트맵이 존재하는 경우
                        break; // 하나라도 이미지가 있으면 반복문 종료
                    }
                }

                int selectedLocIndex = ((Spinner) findViewById(R.id.cody_location)).getSelectedItemPosition();
                Integer cod_loc = cod_loc_value.get(selectedLocIndex);
                String cod_name = codyadd_title.getText().toString();

                ImageButton thumb = (ImageButton) findViewById(R.id.add_cod_thumb);
                Drawable thumbDrawable = thumb.getDrawable();

                if (thumbDrawable == null || !(thumbDrawable instanceof BitmapDrawable) || ((BitmapDrawable) thumbDrawable).getBitmap() == null) {
                    // 썸네일 이미지가 등록되지 않았을 경우 메시지 표시
                    AlertDialog.Builder builder = new AlertDialog.Builder(CodyAdd.this);
                    builder.setTitle("썸네일 등록");
                    builder.setMessage("썸네일 이미지가 등록되지 않았습니다.\n기본 썸네일 이미지를 사용하시겠습니까?");

                    builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            codyRegister(hasImage, cod_name, cIdArray, cod_loc, CodyFileName);
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
                    // 썸네일 이미지가 등록되어 있으면 바로 코디 등록 단계로 이동
                    codyRegister(hasImage, cod_name, cIdArray, cod_loc, CodyFileName);
                }
            }
        });


        weatherSelect(); // 태그(계절) 함수 호출
    }

    private String getToday() {
        DateFormat Today = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.KOREAN);
        TimeZone KoreaTime = TimeZone.getTimeZone("Asia/Seoul");
        Today.setTimeZone(KoreaTime);

        Date date = new Date();

        return Today.format(date);
    }

    protected void weatherSelect() {
        // 정렬 버튼 (계절)
        CheckBox weatherSelect_spring = (CheckBox) findViewById(R.id.weatherSelect_spring);
        CheckBox weatherSelect_summer = (CheckBox) findViewById(R.id.weatherSelect_summer);
        CheckBox weatherSelect_fall = (CheckBox) findViewById(R.id.weatherSelect_fall);
        CheckBox weatherSelect_winter = (CheckBox) findViewById(R.id.weatherSelect_winter);
        CheckBox weatherSelect_communal = (CheckBox) findViewById(R.id.weatherSelect_communal);

        weatherSelect_spring.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (weatherSelect_summer.isChecked() && weatherSelect_fall.isChecked()
                            && weatherSelect_winter.isChecked()) {
                        weatherSelect_communal.setChecked(true);
                    }
                } else {
                    if (weatherSelect_communal.isChecked()) {
                        weatherSelect_communal.setChecked(false);
                        weatherSelect_spring.setChecked(true);
                        weatherSelect_summer.setChecked(false);
                        weatherSelect_fall.setChecked(false);
                        weatherSelect_winter.setChecked(false);
                    }
                }
                weatherButtonBase();
            }
        });

        weatherSelect_summer.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (weatherSelect_spring.isChecked() && weatherSelect_fall.isChecked()
                            && weatherSelect_winter.isChecked()) {
                        weatherSelect_communal.setChecked(true);
                    }
                } else {
                    if (weatherSelect_communal.isChecked()) {
                        weatherSelect_communal.setChecked(false);
                        weatherSelect_spring.setChecked(false);
                        weatherSelect_summer.setChecked(true);
                        weatherSelect_fall.setChecked(false);
                        weatherSelect_winter.setChecked(false);
                    }
                }
                weatherButtonBase();
            }
        });

        weatherSelect_fall.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (weatherSelect_spring.isChecked() && weatherSelect_summer.isChecked()
                            && weatherSelect_winter.isChecked()) {
                        weatherSelect_communal.setChecked(true);
                    }
                } else {
                    if (weatherSelect_communal.isChecked()) {
                        weatherSelect_communal.setChecked(false);
                        weatherSelect_spring.setChecked(false);
                        weatherSelect_summer.setChecked(false);
                        weatherSelect_fall.setChecked(true);
                        weatherSelect_winter.setChecked(false);
                    }
                }
                weatherButtonBase();
            }
        });

        weatherSelect_winter.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (weatherSelect_spring.isChecked() && weatherSelect_summer.isChecked()
                            && weatherSelect_fall.isChecked()) {
                        weatherSelect_communal.setChecked(true);
                    }
                } else {
                    if (weatherSelect_communal.isChecked()) {
                        weatherSelect_communal.setChecked(false);
                        weatherSelect_spring.setChecked(false);
                        weatherSelect_summer.setChecked(false);
                        weatherSelect_fall.setChecked(false);
                        weatherSelect_winter.setChecked(true);
                    }
                }
                weatherButtonBase();
            }
        });

        // 정렬 버튼 (계절 - 전체 선택)
        weatherSelect_communal.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // 전체 선택 버튼이 선택된 경우
                    weatherSelect_spring.setChecked(true);
                    weatherSelect_summer.setChecked(true);
                    weatherSelect_fall.setChecked(true);
                    weatherSelect_winter.setChecked(true);

                } else {
                    if (!weatherSelect_spring.isChecked() || !weatherSelect_summer.isChecked()
                            || !weatherSelect_fall.isChecked() || !weatherSelect_winter.isChecked()) {
                        weatherSelect_communal.setChecked(false);
                    } else {
                        weatherSelect_spring.setChecked(false);
                        weatherSelect_summer.setChecked(false);
                        weatherSelect_fall.setChecked(false);
                        weatherSelect_winter.setChecked(false);
                    }
                    weatherButtonBase();
                }
            }
        });
    }
    private void weatherButtonBase (){
        CheckBox weatherSelect_spring = (CheckBox) findViewById(R.id.weatherSelect_spring);
        CheckBox weatherSelect_summer = (CheckBox) findViewById(R.id.weatherSelect_summer);
        CheckBox weatherSelect_fall = (CheckBox) findViewById(R.id.weatherSelect_fall);
        CheckBox weatherSelect_winter = (CheckBox) findViewById(R.id.weatherSelect_winter);
        CheckBox weatherSelect_communal = (CheckBox) findViewById(R.id.weatherSelect_communal);
        if (weatherSelect_spring.isChecked()) {
            weatherSelect_spring.setBackgroundResource(R.drawable.left_rounded_on);
        } else {
            weatherSelect_spring.setBackgroundResource(R.drawable.left_rounded_off);
        }
        if (weatherSelect_summer.isChecked()) {
            weatherSelect_summer.setBackgroundColor(Color.parseColor("#808080"));
        } else {
            weatherSelect_summer.setBackgroundColor(Color.parseColor("#e9ecef"));
        }
        if (weatherSelect_fall.isChecked()) {
            weatherSelect_fall.setBackgroundColor(Color.parseColor("#808080"));
        } else {
            weatherSelect_fall.setBackgroundColor(Color.parseColor("#e9ecef"));
        }
        if (weatherSelect_winter.isChecked()) {
            weatherSelect_winter.setBackgroundColor(Color.parseColor("#808080"));
        } else {
            weatherSelect_winter.setBackgroundColor(Color.parseColor("#e9ecef"));
        }
        if (weatherSelect_communal.isChecked()) {
            weatherSelect_communal.setBackgroundResource(R.drawable.right_rounded_on);
        } else {
            weatherSelect_communal.setBackgroundResource(R.drawable.right_rounded_off);
        }
    }

    private int getTag() {
        CheckBox Spring = (CheckBox) findViewById(R.id.weatherSelect_spring);
        CheckBox Summer = (CheckBox) findViewById(R.id.weatherSelect_summer);
        CheckBox Fall = (CheckBox) findViewById(R.id.weatherSelect_fall);
        CheckBox Winter = (CheckBox) findViewById(R.id.weatherSelect_winter);
        CheckBox Com = (CheckBox) findViewById(R.id.weatherSelect_communal);

        if (Com.isChecked()) {
            return 15;
        } else if (Spring.isChecked() && !Summer.isChecked() && !Fall.isChecked() && !Winter.isChecked()) {
            return 1;
        } else if (!Spring.isChecked() && Summer.isChecked() && !Fall.isChecked() && !Winter.isChecked()) {
            return 2;
        } else if (!Spring.isChecked() && !Summer.isChecked() && Fall.isChecked() && !Winter.isChecked()) {
            return 3;
        } else if (!Spring.isChecked() && !Summer.isChecked() && !Fall.isChecked() && Winter.isChecked()) {
            return 4;
        } else if (Spring.isChecked() && Summer.isChecked() && !Fall.isChecked() && !Winter.isChecked()) {
            return 5;
        } else if (Spring.isChecked() && !Summer.isChecked() && Fall.isChecked() && !Winter.isChecked()) {
            return 6;
        } else if (Spring.isChecked() && !Summer.isChecked() && !Fall.isChecked() && Winter.isChecked()) {
            return 7;
        } else if (!Spring.isChecked() && Summer.isChecked() && Fall.isChecked() && !Winter.isChecked()) {
            return 8;
        } else if (!Spring.isChecked() && Summer.isChecked() && !Fall.isChecked() && Winter.isChecked()) {
            return 9;
        } else if (!Spring.isChecked() && !Summer.isChecked() && Fall.isChecked() && Winter.isChecked()) {
            return 10;
        } else if (Spring.isChecked() && Summer.isChecked() && Fall.isChecked() && !Winter.isChecked()) {
            return 11;
        } else if (Spring.isChecked() && Summer.isChecked() && !Fall.isChecked() && Winter.isChecked()) {
            return 12;
        } else if (Spring.isChecked() && !Summer.isChecked() && Fall.isChecked() && Winter.isChecked()) {
            return 13;
        } else if (!Spring.isChecked() && Summer.isChecked() && Fall.isChecked() && Winter.isChecked()) {
            return 14;
        }

        return 15;
    }

    private void fillSpinner_cod_location() {
        Spinner cod_loc = findViewById(R.id.cody_location);

        List<String> locations = new ArrayList<>();
        cod_loc_value = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT cod_loc, cod_loc_name FROM Coordy_Location ORDER BY cod_loc ASC", null);

        while (cursor.moveToNext()) {
            locations.add(cursor.getString(cursor.getColumnIndex("cod_loc_name")));
            cod_loc_value.add(cursor.getInt(cursor.getColumnIndex("cod_loc")));
        }
        cursor.close();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_title, locations);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown);
        cod_loc.setAdapter(adapter);
    }

    private void showImageOptionsDialog() {
        String[] options = {"촬영", "파일에서 가져오기"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("옵션 선택")
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            requestCameraPermission();
                        } else if (which == 1) {
                            imageLoader.selectImage();
                        }
                    }
                });
        builder.show();
    }

    private void requestCameraPermission() {
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                cameraUtil.openCameraForResult(CAMERA_REQUEST_CODE);

            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                Toast.makeText(CodyAdd.this, "권한이 거부되었습니다.", Toast.LENGTH_SHORT).show();
            }
        };
        TedPermission.with(this)
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("카메라 권한이 필요합니다. [설정] > [권한]에서 권한을 허용해주세요.")
                .setPermissions(android.Manifest.permission.CAMERA)
                .check();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1) {
            System.out.println("되는듯");
            cameraUtil.handleCameraResult(requestCode, resultCode, data);
            thumbFromCamera = true;
        }
        if(requestCode==2) {
            imageLoader.loadImageFromResult(requestCode, resultCode, data);
            thumbFromCamera = true;
        }
    }

    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        // 새로운 Intent 데이터 처리 (FLAG_ACTIVITY_REORDER_TO_FRONT로 인해 액티비티가 다시 호출될 때)
        setIntent(intent);
    }

    private void handleIncomingIntent(Intent intent) {
        if (intent != null && intent.hasExtra("imageData")) {
            byte[] byteArray = intent.getByteArrayExtra("imageData");
            if (byteArray != null && byteArray.length > 0) {
                Bitmap newBitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);

                ImageButton thumb = (ImageButton) findViewById(R.id.add_cod_thumb);

                if (newBitmap != null && selectedButton != -1) { // selectedButton이 유효한 값일 때만 실행
                    // 선택된 버튼에 이미지를 설정
                    detailCodIndices[selectedButton].setImageBitmap(newBitmap);
                }
                thumb.setImageBitmap(newBitmap);
            }
        }
    }

    public List<Integer> CodyAddImg(int imgCounter){

        List<Integer> imgCounters = new ArrayList<>();

        gridLayout.setRowCount(1000);
        gridLayout.setColumnCount(3);

        for (int col = 0; col < 3; col++) {
            // ImageButton
            ImageButton clothImgbtn = new ImageButton(this);
            clothImgbtn.setBackgroundColor(Color.parseColor("#00ff0000"));
            clothImgbtn.setPadding(150, 150, 150, 150);
            clothImgbtn.setId(imgCounter);

            // GridLayout에 레이아웃 매개변수 설정
            GridLayout.LayoutParams paramsImageButton = new GridLayout.LayoutParams();
            paramsImageButton.width = 275;
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

    public List<Integer> CodyAddTag(int tagCounter) {

        List<Integer> tagCounters = new ArrayList<>();

        gridLayout.setRowCount(1000);
        gridLayout.setColumnCount(3);


        for (int col = 0; col < 3; col++) {

            // TextView 생성 및 설정
            TextView clothTag = new TextView(this);
            clothTag.setBackgroundColor(Color.parseColor("#00ff0000"));
            clothTag.setGravity(Gravity.CENTER);
            clothTag.setId(tagCounter);

            GridLayout.LayoutParams paramsTextView = new GridLayout.LayoutParams();
            paramsTextView.width = 275;
            paramsTextView.height = 90;
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

    private void codyRegister(boolean hasImage, String cod_name, int[] cIdArray, Integer cod_loc, String CodyFileName) {
        if (!hasImage) {
            Toast.makeText(getApplicationContext(), "사진 파일이 등록되지 않았습니다.", Toast.LENGTH_SHORT).show();
        } else if (cod_name.equals("")) {
            Toast.makeText(getApplicationContext(), "코디 이름이 등록되지 않았습니다.", Toast.LENGTH_SHORT).show();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(CodyAdd.this);
            builder.setTitle("코디 등록")
                    .setMessage("해당 설정으로 코디를 등록하시겠습니까?")
                    .setCancelable(false)
                    .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            db.beginTransaction();
                            try {
                                ContentValues values = new ContentValues();

                                values.put("cod_img", "/data/user/0/com.example.closetmanagementservicesapp/files/images/" + CodyFileName);

                                String[] codIndexColumns = {
                                        "cod_index1",
                                        "cod_index2",
                                        "cod_index3",
                                        "cod_index4",
                                        "cod_index5",
                                        "cod_index6",
                                        "cod_index7",
                                        "cod_index8"
                                };

                                for (int i = 0; i < codIndexColumns.length; i++) {
                                    String key = codIndexColumns[i];
                                    if (cIdArray[i] != -1) {
                                        values.put(key, cIdArray[i]);
                                    } else {
                                        values.putNull(key);
                                    }
                                }

                                values.put("cod_loc", cod_loc);
                                values.put("cod_name", cod_name);
                                values.put("cod_date", getToday());
                                values.put("cod_tag", getTag());
                                values.put("cod_stack", 0);

                                db.insert("Coordy", null, values);
                                db.setTransactionSuccessful();
                            } catch (Exception e) {
                                e.printStackTrace();
                            } finally {
                                db.endTransaction();
                            }

                            Toast.makeText(getApplicationContext(), "코디 등록이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(CodyAdd.this, Cody.class);
                            startActivity(intent);
                            finish();
                        }
                    })
                    .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            // 아무 작업도 하지 않음
                        }
                    });

            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }
}


