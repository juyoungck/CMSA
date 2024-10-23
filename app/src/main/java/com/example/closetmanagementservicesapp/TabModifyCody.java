package com.example.closetmanagementservicesapp;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TabModifyCody {

    ListView listView;
    ArrayList<String> itemData;
    TabModifyAdapterCody tabModifyAdapterCody;
    private DBHelper dbHelper;

    // Context를 받아 DBHelper 인스턴스 초기화
    public TabModifyCody(Context context) {
        dbHelper = DBHelper.getInstance(context);
    }

    public TabModifyCody() {
    }

    // 데이터베이스에서 옷장 데이터를 불러와 리스트뷰에 추가
    public void loadCoordyData() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT cod_loc_name FROM Coordy_Location", null);

        if (cursor != null && cursor.moveToFirst()) {
            itemData.clear();  // 기존 데이터를 모두 비움
            do {
                String closetName = cursor.getString(0);  // cod_loc_name을 가져옴
                itemData.add(closetName);  // 리스트에 추가
            } while (cursor.moveToNext());
            cursor.close();
        }

        // 리스트뷰 갱신
        tabModifyAdapterCody.notifyDataSetChanged();
    }

    // 수정 버튼 메서드에서 리스트 초기화와 데이터 로드 호출 추가
    public void modifyButton(View modifyView) {
        Button modify_Add = modifyView.findViewById(R.id.modify_Add);
        CheckBox modify_Modify = modifyView.findViewById(R.id.modify_Modify);

        // 리스트뷰 초기화
        listView = modifyView.findViewById(R.id.list_item);
        itemData = new ArrayList<>();

        // 어댑터 설정
        tabModifyAdapterCody = new TabModifyAdapterCody(modifyView.getContext(), itemData, new TabModifyAdapterCody.DeleteItem() {
            @Override
            public void Delete(int position) {
                deleteCoordyItem(position);  // 삭제 메서드 호출
            }
        });

        listView.setAdapter(tabModifyAdapterCody);

        // 리스트뷰에 데이터 로드
        loadCoordyData();

        // 추가 버튼 클릭시 다이얼로그
        modify_Add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    AlertDialog.Builder builder = new AlertDialog.Builder(modifyView.getContext());
                    builder.setTitle("테마 추가");

                    final EditText input = new EditText(modifyView.getContext());
                    input.setHint("코디 테마 이름을 입력해주세요.");
                    builder.setView(input);

                    builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String closetName = input.getText().toString();
                            if (!closetName.isEmpty()) {
                                addCoordyItem(closetName, modifyView);  // 옷장 추가 메서드 호출
                            } else {
                                Toast.makeText(modifyView.getContext(), "코디 테마 이름을 입력해주세요.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    builder.show();
                } catch (Exception e) {
                    e.printStackTrace();  // 로그에 오류 메시지 출력
                    Toast.makeText(modifyView.getContext(), "오류가 발생했습니다: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });

        // 수정 버튼 동작
        modify_Modify.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                tabModifyAdapterCody.setVisibility(isChecked);
                if (modify_Modify.isChecked()) {
                    modify_Modify.setBackgroundColor(Color.parseColor("#ff6e6e"));
                    modify_Modify.setText("취소");
                    modify_Add.setEnabled(false);
                } else {
                    modify_Modify.setBackgroundColor(Color.parseColor("#e9ecef"));
                    modify_Modify.setText("수정");
                    modify_Add.setEnabled(true);
                }
            }
        });
    }

    // 옷장 추가 메서드
    private void addCoordyItem(String closetName, View modifyView) {
        // cod_loc 계산
        int cId = 0;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT MAX(cod_loc) FROM Coordy_Location", null);

        if (cursor != null && cursor.moveToFirst()) {
            cId = cursor.getInt(0);  // 가장 큰 cod_loc 값 가져오기
            cId++;  // 새로운 cod_loc 값은 기존 값에 1을 더함
            cursor.close();
        }

        // 현재 날짜 가져오기
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        // DB에 값 삽입
        SQLiteDatabase writableDb = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("cod_loc", cId);
        values.put("cod_loc_name", closetName);
        values.put("cod_loc_date", currentDate);

        long newRowId = writableDb.insert("Coordy_Location", null, values);

        if (newRowId != -1) {
            itemData.add(closetName);  // 아이템 리스트에 추가
            tabModifyAdapterCody.notifyDataSetChanged();  // 리스트뷰 갱신
        } else {
            Toast.makeText(modifyView.getContext(), "DB 저장에 실패했습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    // 옷장 삭제 메서드
    private void deleteCoordyItem(int position) {
        String closetName = itemData.get(position);

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Coordy_Location 테이블에서 데이터 삭제
        db.delete("Coordy_Location", "cod_loc_name = ?", new String[]{closetName});

        // 리스트에서 삭제
        itemData.remove(position);
        tabModifyAdapterCody.notifyDataSetChanged();  // 리스트뷰 갱신
    }
}

