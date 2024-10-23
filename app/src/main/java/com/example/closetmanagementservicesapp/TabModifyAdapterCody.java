package com.example.closetmanagementservicesapp;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class TabModifyAdapterCody extends BaseAdapter {
    Context context;
    LayoutInflater layoutInflater;
    ArrayList<String> data;
    boolean isVisible;
    DeleteItem deleteItem;


    public TabModifyAdapterCody(Context context, ArrayList<String>data, DeleteItem deleteItem) {
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
        this.data = data;
        this.isVisible = false;
        this.deleteItem = deleteItem;
    }
    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View adapterView = layoutInflater.inflate(R.layout.tab_modify_item_cody, null);

        TextView modifytext = adapterView.findViewById(R.id.modify_closet_Text);
        ImageButton modifyedit = adapterView.findViewById(R.id.modify_closet_edit);
        ImageButton modifydelete = adapterView.findViewById(R.id.modify_closet_delete);

        // 리스트 아이템 데이터 가져오기
        String item = data.get(position);

        // 텍스트뷰에 아이템 이름 설정
        modifytext.setText(item);

        // 수정 버튼 누를 시 아이콘 표시 / 숨기기
        modifyedit.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        modifydelete.setVisibility(isVisible ? View.VISIBLE : View.GONE);

        modifydelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("경고");
                builder.setMessage("선택한 테마에 있는 모든 코디들이 삭제됩니다. \n\n정말 삭제하시겠습니까?");

                builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (deleteItem != null) {
                            DBHelper dbHelper = DBHelper.getInstance(context);
                            SQLiteDatabase db = dbHelper.getWritableDatabase();

                            // 삭제할 항목 가져오기
                            String closetName = data.get(position); // data는 리스트뷰 항목 데이터
                            try {
                                // 참조 테이블(예: Main_Closet, Coordy)에서 먼저 데이터 삭제
                                db.delete("Main_Closet", "c_loc = (SELECT c_loc FROM Closet_Location WHERE c_loc_name = ?)", new String[]{closetName});
                                db.delete("Coordy", "cod_loc = (SELECT cod_loc FROM Coordy_Location WHERE cod_loc_name = ?)", new String[]{closetName});

                                // Closet_Location에서 데이터 삭제
                                int rowsAffected = db.delete("Coordy_Location", "cod_loc_name = ?", new String[]{closetName});

                                if (rowsAffected > 0) {
                                    // 삭제 성공 시 ArrayList에서 해당 항목 삭제
                                    deleteItem.Delete(position);  // 인터페이스 메서드 호출로 리스트뷰에서 삭제
                                    Toast.makeText(context, "삭제되었습니다.", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(context, "삭제할 수 없습니다. 데이터베이스 오류.", Toast.LENGTH_SHORT).show();
                                }

                                // 리스트뷰 갱신
                                notifyDataSetChanged();

                            } catch (Exception e) {
                                // 오류 발생 시 처리
                                e.printStackTrace();
                                Toast.makeText(context, "삭제 중 오류 발생: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            } finally {
                                dialog.dismiss();  // 다이얼로그 닫기
                                db.close();  // 데이터베이스 닫기
                            }
                        }
                        dialog.dismiss();
                    }
                });
                builder.show();
            }
        });

        modifyedit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("이름 수정");

                final EditText input = new EditText(context);
                input.setText(item); // 현재 이름을 EditText에 설정
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
                        String newName = input.getText().toString();
                        if (!newName.isEmpty()) {
                            // 데이터베이스에서 이름 수정
                            DBHelper dbHelper = DBHelper.getInstance(context);
                            SQLiteDatabase db = dbHelper.getWritableDatabase();

                            ContentValues values = new ContentValues();
                            values.put("cod_loc_name", newName);
                            db.update("Coordy_Location", values, "cod_loc_name = ?", new String[]{item});

                            // ArrayList에서 이름 수정
                            data.set(position, newName);
                            notifyDataSetChanged(); // 리스트뷰 갱신
                            Toast.makeText(context, "이름이 수정되었습니다.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "이름을 입력해주세요.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                builder.show();
            }
        });

        return adapterView;
    }

    public void setVisibility(boolean visible) {
        this.isVisible = visible;
        notifyDataSetChanged();
    }
    public interface DeleteItem {
        void Delete(int position);
    }

}
