package com.example.closetmanagementservicesapp;

import static android.view.View.GONE;
import static androidx.coordinatorlayout.widget.CoordinatorLayout.Behavior.getTag;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class DetailCody extends AppCompatActivity {

    private DBHelper dbHelper;
    private SQLiteDatabase db;

    private ImageLoader_Cody_Modify imageLoader_modify;
    private CameraUtil_Cody_Modify cameraUtil_modify;
    private static final int CAMERA_REQUEST_CODE = 1;
    private Button[] detailButtons = new Button[8];
    private int[] cIdValues = new int[8];
    private Bitmap thumbnailBitmap;
    private Bitmap[] bitmaps = new Bitmap[8];
    private int selectedButton = -1;
    private List<Integer> cod_loc_value;
    private int cod_id;
    private String cod_img;
    private int imgCounter = 8001;
    private int tagCounter = 9001;
    private int imgRow = 0;
    private int tagRow = 0;
    private GridLayout gridLayout;

    private boolean justCancle = false;
    private boolean isModified = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cody_detail);

        // DB OPEN
        dbHelper = MyApplication.getDbHelper();
        db = dbHelper.getWritableDatabase();

        weatherSelect();

        Button detail_Cancle = (Button) findViewById(R.id.detail_Cancle);
        detail_Cancle.setVisibility(GONE);

        // 수정 체크박스 초기화
        CheckBox detail_Modify = (CheckBox) findViewById(R.id.detail_Modify);
        Button detail_Delete = (Button) findViewById(R.id.detail_Delete);

        ImageButton detail_cod_img = (ImageButton) findViewById(R.id.detail_cod_img);
        Spinner detail_cod_loc = (Spinner) findViewById(R.id.detail_cod_loc);
        EditText detail_cod_name = (EditText) findViewById(R.id.detail_cod_name);
        EditText detail_cod_date = (EditText) findViewById(R.id.detail_cod_date);
        CheckBox weatherSelectspring= findViewById(R.id.weatherSelect_spring);
        CheckBox weatherSelectsummer= findViewById(R.id.weatherSelect_summer);
        CheckBox weatherSelectfall= findViewById(R.id.weatherSelect_fall);
        CheckBox weatherSelectwinter= findViewById(R.id.weatherSelect_winter);
        CheckBox weatherSelectcommunal= findViewById(R.id.weatherSelect_communal);

        // 8개의 Button 초기화
        detailButtons[0] = findViewById(R.id.detail_cod_top);
        detailButtons[1] = findViewById(R.id.detail_cod_bottom);
        detailButtons[2] = findViewById(R.id.detail_cod_shoes);
        detailButtons[3] = findViewById(R.id.detail_cod_outer);
        detailButtons[4] = findViewById(R.id.detail_cod_underwear);
        detailButtons[5] = findViewById(R.id.detail_cod_socks);
        detailButtons[6] = findViewById(R.id.detail_cod_accessory);
        detailButtons[7] = findViewById(R.id.detail_cod_bag);

        detail_cod_img.setOnClickListener(v -> showImageOptionsDialog());
        detail_cod_img.setClickable(false);
        detail_cod_loc.setClickable(false);
        detail_cod_loc.setEnabled(false);
        detail_cod_name.setClickable(false);
        detail_cod_name.setEnabled(false);


        // Intent에서 전달받은 데이터 가져오기
        Intent intent = getIntent();
        cod_id = intent.getIntExtra("cod_id", -1);
        cod_img = intent.getStringExtra("cod_img");
        int cod_loc = intent.getIntExtra("cod_loc", 1);
        String cod_name = intent.getStringExtra("cod_name");
        int cod_tag = intent.getIntExtra("cod_tag", 1);
        int cod_stack = intent.getIntExtra("cod_stack", 0);
        String cod_date = intent.getStringExtra("cod_date");
        String fileName = intent.getStringExtra("cod_img_modify");
        Boolean fileUpload = intent.getBooleanExtra("fileUpload", false);
        detail_cod_name.setText(cod_name);
        detail_cod_date.setText(cod_date);
        cod_tag_reader(cod_tag);

        fillSpinner_cod_location(cod_loc);

        cameraUtil_modify = new CameraUtil_Cody_Modify(this, detail_cod_img, cod_id); //화면, 이미지뷰
        imageLoader_modify = new ImageLoader_Cody_Modify(this, detail_cod_img, cod_id);

        if (cod_id != -1) {
            // 데이터베이스 초기화
            DBHelper dbHelper = DBHelper.getInstance(this);
            SQLiteDatabase db = dbHelper.getReadableDatabase();

            // Coordy 테이블에서 cod_id에 해당하는 데이터 조회
            Cursor cursor = db.query("Coordy", null, "cod_id = ?", new String[]{String.valueOf(cod_id)}, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                String cod_img = cursor.getString(cursor.getColumnIndexOrThrow("cod_img"));
                if (cod_img != null) {
                    File imgFile = new File(cod_img);
                    if (imgFile.exists()) {
                    thumbnailBitmap = BitmapFactory.decodeFile(cod_img);
                    detail_cod_img.setImageBitmap(thumbnailBitmap);
                } else {
                    detail_cod_img.setImageResource(R.drawable.baseline_add_box_24);
                }
            } else {
                    detail_cod_img.setImageResource(R.drawable.baseline_add_box_24);
                }

            // cod_index1 ~ cod_index8 가져오기
            for (int i = 0; i < bitmaps.length; i++) {
                String codIndexColumn = "cod_index" + (i + 1);
                int c_id = cursor.getInt(cursor.getColumnIndexOrThrow(codIndexColumn));

                cIdValues[i] = c_id;

                if (c_id != 0) {
                    // Main_Closet 테이블에서 c_id에 해당하는 c_img 조회
                    Cursor cCursor = db.query("Main_Closet", null, "c_id = ?", new String[]{String.valueOf(c_id)}, null, null, null);
                    if (cCursor != null && cCursor.moveToFirst()) {
                        String c_img = cCursor.getString(cCursor.getColumnIndexOrThrow("c_img"));
                        if (c_img != null) {
                            bitmaps[i] = BitmapFactory.decodeFile(c_img);
                        }
                        cCursor.close();
                    }
                }
            }
            cursor.close();
        }


            // 각 버튼에 이미지 설정 및 클릭 리스너 추가
            for (int i = 0; i < detailButtons.length; i++) {
                if (bitmaps[i] != null) {
                    Drawable drawable = new BitmapDrawable(getResources(), bitmaps[i]);
                    detailButtons[i].setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null);
                }

                final int index = i;

                detailButtons[i].setOnClickListener(view -> {
                    if (detail_Modify.isChecked()) {
                        // 수정 체크박스가 체크되어 있으면 다이얼로그 표시
                        selectedButton = index;
                        showImageSelectionDialog();
                    } else {
                        // 기존 동작 수행
                        int c_id = cIdValues[index];

                        if (c_id != 0) {
                            new Thread(() -> {
                                // Main_Closet 테이블에서 c_id로 데이터 조회
                                DBHelper dbHelper1 = DBHelper.getInstance(this);
                                SQLiteDatabase db1 = dbHelper1.getReadableDatabase();
                                Cursor detailCursor = db1.query("Main_Closet", null, "c_id = ?", new String[]{String.valueOf(c_id)}, null, null, null);

                                if (detailCursor != null && detailCursor.moveToFirst()) {
                                    // 데이터 가져오기
                                    int c_id_value = detailCursor.getInt(detailCursor.getColumnIndexOrThrow("c_id"));
                                    String c_img = detailCursor.getString(detailCursor.getColumnIndexOrThrow("c_img"));
                                    int c_loc = detailCursor.getInt(detailCursor.getColumnIndexOrThrow("c_loc"));
                                    String c_name = detailCursor.getString(detailCursor.getColumnIndexOrThrow("c_name"));
                                    String c_type = detailCursor.getString(detailCursor.getColumnIndexOrThrow("c_type"));
                                    String c_size = detailCursor.getString(detailCursor.getColumnIndexOrThrow("c_size"));
                                    String c_brand = detailCursor.getString(detailCursor.getColumnIndexOrThrow("c_brand"));
                                    int c_tag = detailCursor.getInt(detailCursor.getColumnIndexOrThrow("c_tag"));
                                    String c_memo = detailCursor.getString(detailCursor.getColumnIndexOrThrow("c_memo"));
                                    String c_date = detailCursor.getString(detailCursor.getColumnIndexOrThrow("c_date"));
                                    int c_stack = detailCursor.getInt(detailCursor.getColumnIndexOrThrow("c_stack"));

                                    detailCursor.close(); // 커서 닫기
                                    db1.close(); // 데이터베이스 닫기

                                    // Intent 생성 및 데이터 추가
                                    Intent detailIntent = new Intent(DetailCody.this, DetailActivity.class);
                                    detailIntent.putExtra("c_id", c_id_value);
                                    detailIntent.putExtra("c_img", c_img);
                                    detailIntent.putExtra("c_loc", c_loc);
                                    detailIntent.putExtra("c_name", c_name);
                                    detailIntent.putExtra("c_type", c_type);
                                    detailIntent.putExtra("c_size", c_size);
                                    detailIntent.putExtra("c_brand", c_brand);
                                    detailIntent.putExtra("c_tag", c_tag);
                                    detailIntent.putExtra("c_memo", c_memo);
                                    detailIntent.putExtra("c_date", c_date);
                                    detailIntent.putExtra("c_stack", c_stack);

                                    // UI 스레드에서 인텐트 실행
                                    runOnUiThread(() -> {
                                        startActivity(detailIntent);
                                    });
                                } else if (detailCursor != null) {
                                    detailCursor.close(); // 커서 닫기
                                    db1.close(); // 데이터베이스 닫기
                                }
                            }).start();
                        }
                    }
                });
            }
        }


        // 수정 체크박스의 상태 변화에 따른 동작 설정
        detail_Modify.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (justCancle) {

                    return;
                }

                if (isChecked) {
                    detail_Cancle.setVisibility(View.VISIBLE);
                    detail_Modify.setText("저장");
                    detail_Modify.setBackgroundColor(Color.parseColor("#ff6e6e"));
                    detail_cod_img.setClickable(true);
                    detail_cod_loc.setClickable(true);
                    detail_cod_loc.setEnabled(true);
                    detail_cod_name.setClickable(true);
                    detail_cod_name.setEnabled(true);
                    weatherSelectspring.setEnabled(true);
                    weatherSelectsummer.setEnabled(true);
                    weatherSelectfall.setEnabled(true);
                    weatherSelectwinter.setEnabled(true);
                    weatherSelectcommunal.setEnabled(true);

                    detail_Cancle.setOnClickListener(v -> {
                        justCancle = true; // 플래그 설정
                        detail_Modify.setChecked(false);   // 체크 해제 (리스너 호출됨)
                        detail_Cancle.setVisibility(View.GONE);
                        detail_Modify.setText("수정");
                        detail_Modify.setBackgroundColor(Color.parseColor("#e9ecef"));

                        detail_cod_img.setClickable(false);
                        detail_cod_loc.setClickable(false);
                        detail_cod_loc.setEnabled(false);
                        detail_cod_name.setClickable(false);
                        detail_cod_name.setEnabled(false);
                        weatherSelectspring.setEnabled(false);
                        weatherSelectsummer.setEnabled(false);
                        weatherSelectfall.setEnabled(false);
                        weatherSelectwinter.setEnabled(false);
                        weatherSelectcommunal.setEnabled(false);

                        justCancle = false; // 플래그 해제
                    });


                } else {
                    if (detail_cod_name.equals("")) {
                        Toast.makeText(getApplicationContext(), "코디 이름이 등록되지 않았습니다.", Toast.LENGTH_SHORT).show();
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(DetailCody.this);
                        builder.setMessage("코디를 저장하겠습니까?")
                                .setPositiveButton("예", (dialog, id) -> {

                                    int selectedLocIndex = ((Spinner) findViewById(R.id.detail_cod_loc)).getSelectedItemPosition();
                                    Integer cod_loc = cod_loc_value.get(selectedLocIndex);
                                    String cod_name = detail_cod_name.getText().toString();

                                    db.beginTransaction();
                                    try {
                                        ContentValues values = new ContentValues();
                                        for (int i = 0; i < cIdValues.length; i++) {
                                            String codIndexColumn = "cod_index" + (i + 1);
                                            values.put(codIndexColumn, cIdValues[i]);
                                        }

                                        if (fileUpload) {
                                            String oldFileName = "cody_modify" + cod_id + ".png";
                                            String newFileName = "cody_" + cod_id + ".png";

                                            File dir = new File(DetailCody.this.getFilesDir(), "images");
                                            File oldFile = new File(dir, oldFileName);
                                            File newFile = new File(dir, newFileName);

                                            try {
                                                // 1. 새 파일이 이미 존재하는지 확인하고, 존재하면 삭제
                                                if (newFile.exists()) {
                                                    if (newFile.delete()) { }
                                                }

                                                if (oldFile.exists()) {
                                                    Files.copy(oldFile.toPath(), newFile.toPath());

                                                    // 3. 복사가 완료되면 원본 파일 삭제
                                                    if (oldFile.delete()) { }

                                                    // 4. DB에 c_img 경로 업데이트
                                                    cod_img = newFile.getAbsolutePath();
                                                    values.put("cod_img", cod_img);
                                                }
                                            } catch (IOException e) {
                                                Log.e("FileRename", "파일 이름 변경 중 오류 발생", e);
                                            }
                                        }
                                        values.put("cod_loc", cod_loc);
                                        values.put("cod_name", cod_name);
                                        values.put("cod_tag", getTag());

                                        db.update("Coordy", values, "cod_id = ?", new String[]{String.valueOf(cod_id)});
                                        db.setTransactionSuccessful();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    } finally {
                                        db.endTransaction();
                                    }

                                    Toast.makeText(getApplicationContext(), "코디 수정이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                                    Intent modifyIntent = new Intent(DetailCody.this, Cody.class);
                                    startActivity(modifyIntent);
                                    finish();
                                })
                                .setNegativeButton("아니오", (dialog, id) -> {
                                    dialog.dismiss();
                                });
                        builder.create().show();
                    }
                }
            }
        });

        detail_Delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(DetailCody.this);
                builder.setMessage("옷을 삭제하시겠습니까?")
                        .setPositiveButton("예", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                db.beginTransaction();
                                try {
                                    db.delete("Coordy", "cod_id = ?", new String[]{String.valueOf(cod_id)});
                                    String delete_filename = "/data/user/0/com.example.closetmanagementservicesapp/files/images/cody_" + cod_id + ".png";

                                    db.setTransactionSuccessful();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                } finally {
                                    db.endTransaction();
                                }

                                Toast.makeText(getApplicationContext(), "옷 삭제가 완료되었습니다.", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(DetailCody.this, Cody.class);
                                startActivity(intent);
                                finish();
                            }

                        })
                        .setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });
                builder.create().show();
            }
        });

        if (fileUpload) {
            String imagePath = "/data/user/0/com.example.closetmanagementservicesapp/files/images/" + fileName;
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            detail_cod_img.setImageBitmap(bitmap);
            isModified = false;
            detail_Modify.performClick();
        }


        ImageButton btnBack = findViewById(R.id.btnBack_detail);
        btnBack.setOnClickListener(view -> {
            Intent backIntent = new Intent(DetailCody.this, Cody.class);
            startActivity(backIntent);
            finish();
        });
    }

    private void fillSpinner_cod_location(int selected_loc) {
        Spinner c_loc = findViewById(R.id.detail_cod_loc);

        List<String> locations = new ArrayList<>();
        cod_loc_value = new ArrayList<>();    // c_loc 값을 저장할 리스트 초기화
        Cursor cursor = db.rawQuery("SELECT cod_loc, cod_loc_name FROM Coordy_Location ORDER BY cod_loc ASC", null);

        while (cursor.moveToNext()) {
            locations.add(cursor.getString(cursor.getColumnIndex("cod_loc_name")));
            cod_loc_value.add(cursor.getInt(cursor.getColumnIndex("cod_loc")));
        }
        cursor.close();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_title, locations);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown);
        c_loc.setAdapter(adapter);

        int position = cod_loc_value.indexOf(selected_loc);

        if (position >= 0) {
            c_loc.setSelection(position);
        }

    }

    // 이미지 선택 다이얼로그를 표시하는 메서드
    private void showImageSelectionDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("이미지 옵션 선택")
                .setItems(new CharSequence[]{"이미지 제거", "이미지 변경"}, (dialog, which) -> {
                    if (which == 0) {
                        Drawable defaultDrawable = getResources().getDrawable(R.drawable.baseline_add_box_24);
                        detailButtons[selectedButton].setCompoundDrawablesWithIntrinsicBounds(null, defaultDrawable, null, null);
                        cIdValues[selectedButton] = 0;
                        Toast.makeText(DetailCody.this, "옷이 제거되었습니다.", Toast.LENGTH_SHORT).show();
                    } else if (which == 1) {
                        Dialog selectDialog = new Dialog(DetailCody.this);
                        View addView = LayoutInflater.from(DetailCody.this).inflate(R.layout.cody_addmenu, null);

                        selectDialog.setContentView(addView);
                        selectDialog.show();

                        WindowManager.LayoutParams params = selectDialog.getWindow().getAttributes();
                        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
                        params.height = 1200;
                        selectDialog.getWindow().setAttributes(params);

                        gridLayout = addView.findViewById(R.id.gl_add);

                        List<Integer> imgCounterList = CodyAddImg(imgCounter);

                        List<Integer> tagCounterList = CodyAddTag(tagCounter);

                        Cursor cursor = db.query("Main_Closet", null, null, null, null, null, null);

                        int initialImgCounter = 8001;
                        int initialTagCounter = 9001;

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
                                }

                                int finalI = j;

                                int finalcId = c_id;

                                // Set OnClickListener for imageButton
                                imageButton.setOnClickListener(v -> {
                                    AlertDialog.Builder Codybuilder = new AlertDialog.Builder(DetailCody.this);
                                    Codybuilder.setTitle("옷 등록")
                                            .setMessage("해당 옷으로 등록하시겠습니까?")
                                            .setCancelable(false)
                                            .setPositiveButton("확인", (dialogInterface, whichButton) -> {
                                                Bitmap selectedBitmap = BitmapFactory.decodeFile(c_img);
                                                if (selectedBitmap != null && selectedButton != -1) {
                                                    Drawable selectedDrawable = new BitmapDrawable(getResources(), selectedBitmap);
                                                    detailButtons[selectedButton].setCompoundDrawablesWithIntrinsicBounds(null, selectedDrawable, null, null);
                                                    cIdValues[selectedButton] = c_id;
                                                } else {
                                                    Toast.makeText(getApplicationContext(), "이미지를 설정할 수 없습니다.", Toast.LENGTH_SHORT).show();
                                                }
                                                Toast.makeText(getApplicationContext(), "옷 등록이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                                                dialogInterface.dismiss();
                                                selectDialog.dismiss();
                                            })
                                            .setNegativeButton("취소", (dialogInterface, whichButton) -> {
                                                // Do nothing
                                            });
                                    Codybuilder.show();
                                });
                                cursor.moveToNext();
                            }
                        }
                        cursor.close();
                        ImageButton addClose = addView.findViewById(R.id.addClose);
                        addClose.setOnClickListener(v -> {
                            selectDialog.dismiss();  // 다이얼로그 닫기
                        });
                    }
                })
                .setNegativeButton("취소", (dialog, which) -> {
                    dialog.dismiss();
                });
        builder.show();
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
                            imageLoader_modify.selectImage();
                        }
                    }
                });
        builder.show();
    }

    private void requestCameraPermission() {
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                cameraUtil_modify.openCameraForResult(CAMERA_REQUEST_CODE);
            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                Toast.makeText(DetailCody.this, "권한이 거부되었습니다.", Toast.LENGTH_SHORT).show();
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
            cameraUtil_modify.handleCameraResult(requestCode, resultCode, data);
        }
        if(requestCode==2) {
            imageLoader_modify.loadImageFromResult(requestCode, resultCode, data);
        }
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

    private void cod_tag_reader(int tagId) {
        CheckBox Spring = (CheckBox) findViewById(R.id.weatherSelect_spring);
        CheckBox Summer = (CheckBox) findViewById(R.id.weatherSelect_summer);
        CheckBox Fall = (CheckBox) findViewById(R.id.weatherSelect_fall);
        CheckBox Winter = (CheckBox) findViewById(R.id.weatherSelect_winter);
        CheckBox Com = (CheckBox) findViewById(R.id.weatherSelect_communal);

        switch (tagId) {
            case 1:
                Spring.setChecked(true);
                break;
            case 2:
                Summer.setChecked(true);
                break;
            case 3:
                Fall.setChecked(true);
                break;
            case 4:
                Winter.setChecked(true);
                break;
            case 5:
                Spring.setChecked(true);
                Summer.setChecked(true);
                break;
            case 6:
                Spring.setChecked(true);
                Fall.setChecked(true);
                break;
            case 7:
                Spring.setChecked(true);
                Winter.setChecked(true);
                break;
            case 8:
                Summer.setChecked(true);
                Fall.setChecked(true);
                break;
            case 9:
                Summer.setChecked(true);
                Winter.setChecked(true);
                break;
            case 10:
                Fall.setChecked(true);
                Winter.setChecked(true);
                break;
            case 11:
                Spring.setChecked(true);
                Summer.setChecked(true);
                Fall.setChecked(true);
                break;
            case 12:
                Spring.setChecked(true);
                Summer.setChecked(true);
                Winter.setChecked(true);
                break;
            case 13:
                Spring.setChecked(true);
                Fall.setChecked(true);
                Winter.setChecked(true);
                break;
            case 14:
                Summer.setChecked(true);
                Fall.setChecked(true);
                Winter.setChecked(true);
                break;
            case 15:
                Com.setChecked(true);
                break;
            default:
                // 아무 것도 체크되지 않음 (모두 false)
                break;
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
}

