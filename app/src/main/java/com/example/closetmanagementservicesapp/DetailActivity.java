package com.example.closetmanagementservicesapp;

import static android.view.View.GONE;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

// 이미지 버튼을 클릭했을 때 실행되는 java 코드
public class DetailActivity extends AppCompatActivity {
    private DBHelper dbHelper;
    private SQLiteDatabase db;
    private List<Integer> c_loc_value;
    private int c_id;
    private String c_img;
    private boolean isModified = false;
    private ImageLoader_Closet_Modify imageLoader_modify;
    private CameraUtil_Closet_Modify cameraUtil_modify;
    private static final int CAMERA_REQUEST_CODE = 1;
    private boolean justCancle = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // DB OPEN
        dbHelper = MyApplication.getDbHelper();
        db = dbHelper.getWritableDatabase();

        weatherSelect(); // 태그(계절) 함수 호출

        Button detail_Cancle = (Button) findViewById(R.id.detail_Cancle);
        detail_Cancle.setVisibility(GONE);

        ImageButton detail_c_img = (ImageButton) findViewById(R.id.detail_c_img);
        Spinner detail_c_loc = (Spinner) findViewById(R.id.detail_c_loc);
        EditText detail_c_name = (EditText) findViewById(R.id.detail_c_name);
        EditText detail_c_brand = (EditText) findViewById(R.id.detail_c_brand);
        Spinner detail_c_type = (Spinner) findViewById(R.id.detail_c_type);
        EditText detail_c_type_add = (EditText) findViewById(R.id.detail_c_type_add);
        Spinner detail_c_size = (Spinner) findViewById(R.id.detail_c_size);
        EditText detail_c_size_add = (EditText) findViewById(R.id.detail_c_size_add);
        CheckBox weatherSelectspring= findViewById(R.id.weatherSelect_spring);
        CheckBox weatherSelectsummer= findViewById(R.id.weatherSelect_summer);
        CheckBox weatherSelectfall= findViewById(R.id.weatherSelect_fall);
        CheckBox weatherSelectwinter= findViewById(R.id.weatherSelect_winter);
        CheckBox weatherSelectcommunal= findViewById(R.id.weatherSelect_communal);
        EditText detail_c_memo = (EditText) findViewById(R.id.detail_c_memo);
        EditText detail_c_date = (EditText) findViewById(R.id.detail_c_date);
        CheckBox detailModifyButton = (CheckBox) findViewById(R.id.detail_Modify);
        Button deleteButton = findViewById(R.id.detail_delete);

        detail_c_img.setOnClickListener(v -> showImageOptionsDialog());
        detail_c_img.setClickable(false);
        detail_c_loc.setEnabled(false);
        detail_c_loc.setClickable(false);
        detail_c_type.setEnabled(false);
        detail_c_type.setClickable(false);
        detail_c_size.setEnabled(false);
        detail_c_size.setClickable(false);

        Intent intent = getIntent();
        c_id = intent.getIntExtra("c_id", -1);
        c_img = intent.getStringExtra("c_img");
        int c_loc = intent.getIntExtra("c_loc", 1);
        String c_name = intent.getStringExtra("c_name");
        String c_type = intent.getStringExtra("c_type");
        String c_size = intent.getStringExtra("c_size");
        String c_brand = intent.getStringExtra("c_brand");
        int c_tag = intent.getIntExtra("c_tag", 1);
        String c_memo = intent.getStringExtra("c_memo");
        String c_date = intent.getStringExtra("c_date");
        int c_stack = intent.getIntExtra("c_stack", 0);
        String fileName = intent.getStringExtra("c_img_modify");
        Boolean fileUpload = intent.getBooleanExtra("fileUpload", false);

        Bitmap bitmap = BitmapFactory.decodeFile(c_img);
        detail_c_img.setImageBitmap(bitmap);
        detail_c_name.setText(c_name);
        detail_c_brand.setText(c_brand);
        c_tag_reader(c_tag);
        detail_c_memo.setText(c_memo);
        detail_c_date.setText(c_date);

        ImageButton btnBack = (ImageButton) findViewById(R.id.btnBack_detail);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        fillSpinner_location(c_loc);
        fillSpinner_type(c_type);
        fillSpinner_size(c_size);

        cameraUtil_modify = new CameraUtil_Closet_Modify(this, detail_c_img, c_id); //화면, 이미지뷰
        imageLoader_modify = new ImageLoader_Closet_Modify(this, detail_c_img, c_id);

        detailModifyButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (justCancle) {

                    return;
                }

                if (isChecked) {
                    detail_Cancle.setVisibility(View.VISIBLE);
                    detailModifyButton.setText("저장");
                    detailModifyButton.setBackgroundColor(Color.parseColor("#ff6e6e"));
                    isModified = true;
                    detail_c_img.setClickable(true);
                    detail_c_loc.setEnabled(true);
                    detail_c_loc.setClickable(true);
                    detail_c_name.setEnabled(true);
                    detail_c_brand.setEnabled(true);
                    detail_c_type.setEnabled(true);
                    detail_c_type.setClickable(true);
                    detail_c_type_add.setEnabled(true);
                    detail_c_size.setEnabled(true);
                    detail_c_size.setClickable(true);
                    detail_c_size_add.setEnabled(true);
                    weatherSelectspring.setEnabled(true);
                    weatherSelectsummer.setEnabled(true);
                    weatherSelectfall.setEnabled(true);
                    weatherSelectwinter.setEnabled(true);
                    weatherSelectcommunal.setEnabled(true);
                    detail_c_memo.setEnabled(true);

                    detail_Cancle.setOnClickListener(v -> {
                        justCancle = true; // 플래그 설정
                        detailModifyButton.setChecked(false);   // 체크 해제 (리스너 호출됨)
                        detail_Cancle.setVisibility(View.GONE);
                        detailModifyButton.setText("수정");
                        detailModifyButton.setBackgroundColor(Color.parseColor("#e9ecef"));

                        detail_c_img.setClickable(false);
                        detail_c_loc.setEnabled(false);
                        detail_c_loc.setClickable(false);
                        detail_c_name.setEnabled(false);
                        detail_c_brand.setEnabled(false);
                        detail_c_type.setEnabled(false);
                        detail_c_type.setClickable(false);
                        detail_c_type_add.setEnabled(false);
                        detail_c_size.setEnabled(false);
                        detail_c_size.setClickable(false);
                        detail_c_size_add.setEnabled(false);
                        weatherSelectspring.setEnabled(false);
                        weatherSelectsummer.setEnabled(false);
                        weatherSelectfall.setEnabled(false);
                        weatherSelectwinter.setEnabled(false);
                        weatherSelectcommunal.setEnabled(false);
                        detail_c_memo.setEnabled(false);
                        justCancle = false; // 플래그 해제
                    });

                } else {
                    if (detail_c_name.equals("")) {
                        Toast.makeText(getApplicationContext(), "옷 이름이 등록되지 않았습니다.", Toast.LENGTH_SHORT).show();
                    } else if (detail_c_type.equals("직접입력") /*&& detail_c_type_add.equals("")*/) {
                        Toast.makeText(getApplicationContext(), "옷 종류가 등록되지 않았습니다.", Toast.LENGTH_SHORT).show();
                    } else {
                        // 두 번째 클릭: 다이얼로그 표시
                        AlertDialog.Builder builder = new AlertDialog.Builder(DetailActivity.this);
                        builder.setMessage("옷을 저장하겠습니까?")
                                .setPositiveButton("예", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {

                                        int selectedLocIndex = ((Spinner) findViewById(R.id.detail_c_loc)).getSelectedItemPosition();
                                        Integer c_loc = c_loc_value.get(selectedLocIndex);
                                        String c_name = detail_c_name.getText().toString();
                                        String c_type = detail_c_type.getSelectedItem().toString();
                                        String c_type_add = detail_c_type_add.getText().toString();
                                        String c_size = detail_c_size.getSelectedItem().toString();
                                        String c_size_add = detail_c_size_add.getText().toString();
                                        String c_brand = detail_c_brand.getText().toString();
                                        String c_memo = detail_c_memo.getText().toString();

                                        db.beginTransaction();
                                        try {
                                            ContentValues values = new ContentValues();
                                            if (fileUpload) {
                                                String oldFileName = "image_modify" + c_id + ".png";
                                                String newFileName = "image_" + c_id + ".png";

                                                File dir = new File(DetailActivity.this.getFilesDir(), "images");
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
                                                        c_img = newFile.getAbsolutePath();
                                                        values.put("c_img", c_img);
                                                    }
                                                } catch (IOException e) {
                                                    Log.e("FileRename", "파일 이름 변경 중 오류 발생", e);
                                                }
                                            }
                                            values.put("c_loc", c_loc);
                                            values.put("c_name", c_name);

                                            if (c_type.equals("직접입력")) {
                                                values.put("c_type", c_type_add);
                                            } else {
                                                values.put("c_type", c_type);
                                            }

                                            if (c_size.equals("직접입력")) {
                                                values.put("c_size", c_size_add);
                                            } else if (c_size.equals("선택안함")) {
                                                values.put("c_size", "");
                                            } else {
                                                values.put("c_size", c_size);
                                            }
                                            values.put("c_brand", c_brand);
                                            values.put("c_tag", getTag());
                                            values.put("c_memo", c_memo);
                                            db.update("Main_Closet", values, "c_id = ?", new String[]{String.valueOf(c_id)});

                                            db.setTransactionSuccessful();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        } finally {
                                            db.endTransaction();
                                        }

                                        Toast.makeText(getApplicationContext(), "옷 수정이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(DetailActivity.this, MainActivity.class);
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
                }
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(DetailActivity.this);
                builder.setMessage("옷을 삭제하시겠습니까?")
                        .setPositiveButton("예", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                db.beginTransaction();
                                try {
                                    db.delete("Main_Closet", "c_id = ?", new String[]{String.valueOf(c_id)});
                                    String delete_filename = "/data/user/0/com.example.closetmanagementservicesapp/files/images/image_" + c_id + ".png";
                                    File deleteFile = new File(delete_filename);
                                    if (deleteFile.delete()) {
                                        Log.d("img_deleted", deleteFile.getName());
                                    }

                                    db.setTransactionSuccessful();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                } finally {
                                    db.endTransaction();
                                }

                                Toast.makeText(getApplicationContext(), "옷 삭제가 완료되었습니다.", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(DetailActivity.this, MainActivity.class);
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
            bitmap = BitmapFactory.decodeFile(imagePath);
            detail_c_img.setImageBitmap(bitmap);
            isModified = false;
            detailModifyButton.performClick();
        }
    }

    private void fillSpinner_location(int selected_loc) {
        Spinner c_loc = findViewById(R.id.detail_c_loc);

        List<String> locations = new ArrayList<>();
        c_loc_value = new ArrayList<>();    // c_loc 값을 저장할 리스트 초기화
        Cursor cursor = db.rawQuery("SELECT c_loc, c_loc_name FROM Closet_Location ORDER BY c_loc ASC", null);

        while (cursor.moveToNext()) {
            locations.add(cursor.getString(cursor.getColumnIndex("c_loc_name")));
            c_loc_value.add(cursor.getInt(cursor.getColumnIndex("c_loc")));
        }
        cursor.close();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_title, locations);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown);
        c_loc.setAdapter(adapter);

        int position = c_loc_value.indexOf(selected_loc);

        if (position >= 0) {
            c_loc.setSelection(position);
        }
    }

    private void fillSpinner_type(String selected_type) {
        Spinner detail_c_type_spinner = (Spinner) findViewById(R.id.detail_c_type);  // 옷 종류 호출
        EditText detail_c_type_add = (EditText) findViewById(R.id.detail_c_type_add);   // 옷 종류(직접입력) 호출

        List<String> c_type_array = Arrays.asList(getResources().getStringArray(R.array.c_type_array));

        if (!c_type_array.contains(selected_type)) {
            detail_c_type_add.setText(selected_type);
            selected_type = "직접입력";
        }

        ArrayAdapter<CharSequence> detail_c_type_adapter = ArrayAdapter.createFromResource(this, R.array.c_type_array, R.layout.spinner_title);
        detail_c_type_adapter.setDropDownViewResource(R.layout.spinner_dropdown);
        detail_c_type_spinner.setAdapter(detail_c_type_adapter);

        int position = detail_c_type_adapter.getPosition(selected_type);

        if (position >= 0) {
            detail_c_type_spinner.setSelection(position);

            if (position == 12) {
                detail_c_type_add.setVisibility(View.VISIBLE);
            } else {
                detail_c_type_add.setVisibility(View.INVISIBLE);
            }
        }

        detail_c_type_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String c_type = detail_c_type_spinner.getSelectedItem().toString();

                if (c_type.equals("직접입력")) {
                    detail_c_type_add.setVisibility(View.VISIBLE);
                } else {
                    detail_c_type_add.setVisibility(View.INVISIBLE);
                    detail_c_type_add.setText(""); // 다른 항목이 선택되면 EditText 초기화
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    private void fillSpinner_size(String selected_size) {
        Spinner detail_c_size_spinner = (Spinner) findViewById(R.id.detail_c_size);  // 옷 사이즈 호출
        EditText detail_c_size_add = (EditText) findViewById(R.id.detail_c_size_add);   // 옷 사이즈((직접입력) 호출

        List<String> c_size_array = Arrays.asList(getResources().getStringArray(R.array.c_size_array));

        if (!c_size_array.contains(selected_size)) {
            if (selected_size.equals("")) {
                selected_size = "선택안함";
            } else {
                detail_c_size_add.setText(selected_size);
                selected_size = "직접입력";
            }
        }

        ArrayAdapter<CharSequence> detail_c_size_adapter = ArrayAdapter.createFromResource(this, R.array.c_size_array, R.layout.spinner_title);
        detail_c_size_adapter.setDropDownViewResource(R.layout.spinner_dropdown);
        detail_c_size_spinner.setAdapter(detail_c_size_adapter);

        int position = detail_c_size_adapter.getPosition(selected_size);

        if (position >= 0) {
            detail_c_size_spinner.setSelection(position);

            if (position == 8) {
                detail_c_size_add.setVisibility(View.VISIBLE);
            } else {
                detail_c_size_add.setVisibility(View.INVISIBLE);
            }
        }

        detail_c_size_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String c_size = detail_c_size_spinner.getSelectedItem().toString();

                if (c_size.equals("직접입력")) {
                    detail_c_size_add.setVisibility(View.VISIBLE);
                } else {
                    detail_c_size_add.setVisibility(View.INVISIBLE);
                    detail_c_size_add.setText(""); // 다른 항목이 선택되면 EditText 초기화
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
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
                Toast.makeText(DetailActivity.this, "권한이 거부되었습니다.", Toast.LENGTH_SHORT).show();
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

    private void c_tag_reader(int tagId) {
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
}
