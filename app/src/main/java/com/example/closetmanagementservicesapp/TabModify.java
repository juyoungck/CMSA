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

public class TabModify {

    ListView listView;
    ArrayList<String> itemData;
    TabModifyAdapter tabModifyAdapter;
    private DBHelper dbHelper;

    // Context를 받아 DBHelper 인스턴스 초기화
    public TabModify(Context context) {
        dbHelper = DBHelper.getInstance(context);
    }

    public TabModify() {
    }

    // 데이터베이스에서 옷장 데이터를 불러와 리스트뷰에 추가
    public void loadClosetData() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT c_loc_name FROM Closet_Location", null);

        if (cursor != null && cursor.moveToFirst()) {
            itemData.clear();  // 기존 데이터를 모두 비움
            do {
                String closetName = cursor.getString(0);  // c_loc_name을 가져옴
                itemData.add(closetName);  // 리스트에 추가
            } while (cursor.moveToNext());
            cursor.close();
        }

        // 리스트뷰 갱신
        tabModifyAdapter.notifyDataSetChanged();
    }

    // 수정 버튼 메서드에서 리스트 초기화와 데이터 로드 호출 추가
    public void modifyButton(View modifyView) {
        Button modify_Add = modifyView.findViewById(R.id.modify_Add);
        CheckBox modify_Modify = modifyView.findViewById(R.id.modify_Modify);

        // 리스트뷰 초기화
        listView = modifyView.findViewById(R.id.list_item);
        itemData = new ArrayList<>();

        // 어댑터 설정
        tabModifyAdapter = new TabModifyAdapter(modifyView.getContext(), itemData, new TabModifyAdapter.DeleteItem() {
            @Override
            public void Delete(int position) {
                deleteClosetItem(position);  // 삭제 메서드 호출
            }
        });

        listView.setAdapter(tabModifyAdapter);

        // 리스트뷰에 데이터 로드
        loadClosetData();

        // 추가 버튼 클릭시 다이얼로그
        modify_Add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    AlertDialog.Builder builder = new AlertDialog.Builder(modifyView.getContext());
                    builder.setTitle("옷장 추가");

                    final EditText input = new EditText(modifyView.getContext());
                    input.setHint("옷장 이름을 입력해주세요.");
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
                                addClosetItem(closetName, modifyView);  // 옷장 추가 메서드 호출
                            } else {
                                Toast.makeText(modifyView.getContext(), "옷장 이름을 입력해주세요.", Toast.LENGTH_SHORT).show();
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
                tabModifyAdapter.setVisibility(isChecked);
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
    private void addClosetItem(String closetName, View modifyView) {
        // c_loc 계산
        int cId = 0;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT MAX(c_loc) FROM Closet_Location", null);

        if (cursor != null && cursor.moveToFirst()) {
            cId = cursor.getInt(0);  // 가장 큰 c_loc 값 가져오기
            cId++;  // 새로운 c_loc 값은 기존 값에 1을 더함
            cursor.close();
        }

        // 현재 날짜 가져오기
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        // DB에 값 삽입
        SQLiteDatabase writableDb = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("c_loc", cId);
        values.put("c_loc_name", closetName);
        values.put("c_loc_date", currentDate);

        long newRowId = writableDb.insert("Closet_Location", null, values);

        if (newRowId != -1) {
            itemData.add(closetName);  // 아이템 리스트에 추가
            tabModifyAdapter.notifyDataSetChanged();  // 리스트뷰 갱신
        } else {
            Toast.makeText(modifyView.getContext(), "DB 저장에 실패했습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    // 옷장 삭제 메서드
    private void deleteClosetItem(int position) {
        String closetName = itemData.get(position);

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Closet_Location 테이블에서 데이터 삭제
        db.delete("Closet_Location", "c_loc_name = ?", new String[]{closetName});

        // 리스트에서 삭제
        itemData.remove(position);
        tabModifyAdapter.notifyDataSetChanged();  // 리스트뷰 갱신
    }
}

