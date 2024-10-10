package com.example.closetmanagementservicesapp;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TabSort_Closet extends AppCompatActivity {
    private DBHelper dbHelper;
    private SQLiteDatabase db;
    private ArrayList<Integer> sort_c_id = new ArrayList<>();
    private Context context;
    private TabSortCallback callback;
    private BottomSheetDialog bottomSheetDialog;
    private HashMap<Integer, Boolean> checkboxStates;

    public TabSort_Closet(Context context, BottomSheetDialog bottomSheetDialog, TabSortCallback callback, HashMap<Integer, Boolean> checkboxStates) {
        this.context = context;
        this.callback = callback;
        this.bottomSheetDialog = bottomSheetDialog;
        this.checkboxStates = checkboxStates;
    }

    public void sortApply(View view) {
        dbHelper = MyApplication.getDbHelper();
        db = dbHelper.getWritableDatabase();

        clothesSelect(view);
        initializeCheckBoxes(view);
        weatherSelect(view);
        weatherButtonBase(view);


        Button tag_refresh = (Button) view.findViewById(R.id.tag_refresh);

        CheckBox clothesSelect_all = (CheckBox) view.findViewById(R.id.clothesSelect_all);
        CheckBox clothesSelect_all_clothes = (CheckBox) view.findViewById(R.id.clothesSelect_all_clothes);
        CheckBox clothesSelect_all_access = (CheckBox) view.findViewById(R.id.clothesSelect_all_access);
        CheckBox clothesSelect_all_etc = (CheckBox) view.findViewById(R.id.clothesSelect_all_etc);
        CheckBox clothesSelect_top = (CheckBox) view.findViewById(R.id.clothesSelect_top);
        CheckBox clothesSelect_bottom = (CheckBox) view.findViewById(R.id.clothesSelect_bottom);
        CheckBox clothesSelect_outer = (CheckBox) view.findViewById(R.id.clothesSelect_outer);
        CheckBox clothesSelect_shoes = (CheckBox) view.findViewById(R.id.clothesSelect_shoes);
        CheckBox clothesSelect_under = (CheckBox) view.findViewById(R.id.clothesSelect_under);
        CheckBox clothesSelect_socks = (CheckBox) view.findViewById(R.id.clothesSelect_socks);
        CheckBox clothesSelect_hat = (CheckBox) view.findViewById(R.id.clothesSelect_hat);
        CheckBox clothesSelect_access = (CheckBox) view.findViewById(R.id.clothesSelect_access);
        CheckBox clothesSelect_bag = (CheckBox) view.findViewById(R.id.clothesSelect_bag);
        CheckBox clothesSelect_set = (CheckBox) view.findViewById(R.id.clothesSelect_set);
        CheckBox clothesSelect_etc = (CheckBox) view.findViewById(R.id.clothesSelect_etc);
        CheckBox clothesSelect_input = (CheckBox) view.findViewById(R.id.clothesSelect_input);

        CheckBox Spring = (CheckBox) view.findViewById(R.id.weatherSelect_spring);
        CheckBox Summer = (CheckBox) view.findViewById(R.id.weatherSelect_summer);
        CheckBox Fall = (CheckBox) view.findViewById(R.id.weatherSelect_fall);
        CheckBox Winter = (CheckBox) view.findViewById(R.id.weatherSelect_winter);
        CheckBox Com = (CheckBox) view.findViewById(R.id.weatherSelect_communal);

        CheckBox sortSelect_name = (CheckBox) view.findViewById(R.id.sortSelect_name);
        CheckBox sortSelect_asc = (CheckBox) view.findViewById(R.id.sortSelect_asc);

        Button sort_apply = (Button) view.findViewById(R.id.sort_apply);

        sort_apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<String> clothesArgsList = new ArrayList<>();
                ArrayList<String> clothesInputArgsList = new ArrayList<>();
                Set<String> tagList = new HashSet<>();

                // 의류 카테고리 체크박스 상태 확인
                if (clothesSelect_top.isChecked()) { clothesArgsList.add("상의"); }
                if (clothesSelect_bottom.isChecked()) { clothesArgsList.add("하의"); }
                if (clothesSelect_outer.isChecked()) { clothesArgsList.add("아우터"); }
                if (clothesSelect_shoes.isChecked()) { clothesArgsList.add("신발"); }
                if (clothesSelect_under.isChecked()) { clothesArgsList.add("속옷"); }
                if (clothesSelect_socks.isChecked()) { clothesArgsList.add("양말"); }

                // 악세사리 카테고리 체크박스 상태 확인
                if (clothesSelect_hat.isChecked()) { clothesArgsList.add("모자"); }
                if (clothesSelect_access.isChecked()) { clothesArgsList.add("악세사리"); }
                if (clothesSelect_bag.isChecked()) { clothesArgsList.add("가방"); }

                // 기타 카테고리 체크박스 상태 확인
                if (clothesSelect_set.isChecked()) { clothesArgsList.add("세트"); }
                if (clothesSelect_etc.isChecked()) { clothesArgsList.add("기타"); }

                if (clothesSelect_input.isChecked()) {
                    if (!clothesSelect_top.isChecked()) { clothesInputArgsList.add("상의"); }
                    if (!clothesSelect_bottom.isChecked()) { clothesInputArgsList.add("하의"); }
                    if (!clothesSelect_outer.isChecked()) { clothesInputArgsList.add("아우터"); }
                    if (!clothesSelect_shoes.isChecked()) { clothesInputArgsList.add("신발"); }
                    if (!clothesSelect_under.isChecked()) { clothesInputArgsList.add("속옷"); }
                    if (!clothesSelect_socks.isChecked()) { clothesInputArgsList.add("양말"); }
                    if (!clothesSelect_hat.isChecked()) { clothesInputArgsList.add("모자"); }
                    if (!clothesSelect_access.isChecked()) { clothesInputArgsList.add("악세사리"); }
                    if (!clothesSelect_bag.isChecked()) { clothesInputArgsList.add("가방"); }
                    if (!clothesSelect_set.isChecked()) { clothesInputArgsList.add("세트"); }
                    if (!clothesSelect_etc.isChecked()) { clothesInputArgsList.add("기타"); }
                }

                // 정확한 검색 체크박스 상태 확인
                if (Com.isChecked()) { tagList.addAll(Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15")); }
                else {
                    if (Spring.isChecked()) { tagList.addAll(Arrays.asList("1", "5", "6", "7", "11", "12", "13", "15")); }
                    if (Summer.isChecked()) { tagList.addAll(Arrays.asList("2", "5", "8", "9", "11", "12", "14", "15")); }
                    if (Fall.isChecked()) { tagList.addAll(Arrays.asList("3", "6", "8", "10", "11", "13", "14", "15")); }
                    if (Winter.isChecked()) { tagList.addAll(Arrays.asList("4", "7", "9", "10", "12", "13", "14", "15")); }
                }
                ArrayList<String> tagArgs = new ArrayList<>(tagList);


                String orderBy = "";
                if (sortSelect_name.isChecked()) {
                    orderBy = "c_name";
                } else if (!sortSelect_name.isChecked()){
                    orderBy = "c_date";
                }

                if (sortSelect_asc.isChecked()) {
                    orderBy += " DESC";
                } else if (!sortSelect_asc.isChecked()) {
                    orderBy += " ASC";
                }

                if (!clothesArgsList.isEmpty() && !tagArgs.isEmpty()) {
                    StringBuilder clothes_builder = new StringBuilder();
                    if  (!clothesSelect_input.isChecked()) {
                        clothes_builder.append("c_type IN (");
                        for (int i = 0; i < clothesArgsList.size(); i++) {
                            clothes_builder.append("?");
                            if (i < clothesArgsList.size() - 1) {
                                clothes_builder.append(", ");
                            }
                        }
                        clothes_builder.append(") AND c_tag IN (");
                    } else {
                        clothes_builder.append("(c_type IN (");
                        for (int i = 0; i < clothesArgsList.size(); i++) {
                            clothes_builder.append("?");
                            if (i < clothesArgsList.size() - 1) {
                                clothes_builder.append(", ");
                            }
                        }
                        clothes_builder.append(") OR c_type NOT IN (");

                        for (int i = 0; i < clothesInputArgsList.size(); i++) {
                            clothes_builder.append("?");
                            if (i < clothesInputArgsList.size() - 1) {
                                clothes_builder.append(", ");
                            }
                        }
                        clothes_builder.append(")) AND c_tag IN (");
                    }

                    for (int i = 0; i < tagArgs.size(); i++) {
                        clothes_builder.append("?");
                        if (i < tagArgs.size() - 1) {
                            clothes_builder.append(", ");
                        }
                    }
                    clothes_builder.append(")");

                    ArrayList<String> args = new ArrayList<>(clothesArgsList);
                    args.addAll(clothesInputArgsList);
                    args.addAll(tagArgs);

                    Cursor cursor = db.query("Main_Closet", new String[]{"c_id"}, clothes_builder.toString(), args.toArray(new String[0]), null, null, orderBy);

                    if (cursor != null && cursor.moveToFirst()) {
                        sort_c_id.clear();

                        do {
                            int c_id = cursor.getInt(cursor.getColumnIndexOrThrow("c_id"));
                            sort_c_id.add(c_id);
                        } while (cursor.moveToNext());
                        cursor.close();
                    }
                }

                if (callback != null) {
                    callback.onSortResult(sort_c_id, orderBy);
                }
                bottomSheetDialog.dismiss();
            }
        });

        tag_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clothesSelect_all.setChecked(true);
                Com.setChecked(true);
                sortSelect_name.setChecked(false);
                sortSelect_asc.setChecked(false);
            }
        });
    }

    private void initializeCheckBoxes(View rootView) {
        List<CheckBox> checkBoxList = new ArrayList<>();
        findAllCheckBoxes(rootView, checkBoxList);

        for (CheckBox checkBox : checkBoxList) {
            int id = checkBox.getId();
            if (checkboxStates.containsKey(id)) {
                checkBox.setChecked(checkboxStates.get(id));
            }
        }
    }

    public HashMap<Integer, Boolean> getCheckboxStates(View rootView) {
        List<CheckBox> checkBoxList = new ArrayList<>();
        findAllCheckBoxes(rootView, checkBoxList);

        HashMap<Integer, Boolean> currentStates = new HashMap<>();

        for (CheckBox checkBox : checkBoxList) {
            int id = checkBox.getId();
            currentStates.put(id, checkBox.isChecked());
        }

        return currentStates;
    }

    private void findAllCheckBoxes(View view, List<CheckBox> checkBoxList) {
        if (view instanceof CheckBox) {
            checkBoxList.add((CheckBox) view);
        } else if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            for (int i = 0; i < group.getChildCount(); i++) {
                View child = group.getChildAt(i);
                findAllCheckBoxes(child, checkBoxList);
            }
        }
    }

    // 옷 선택
    protected void clothesSelect(View view) {
        CheckBox clothesSelect_all = (CheckBox) view.findViewById(R.id.clothesSelect_all);
        CheckBox clothesSelect_all_clothes = (CheckBox) view.findViewById(R.id.clothesSelect_all_clothes);
        CheckBox clothesSelect_all_access = (CheckBox) view.findViewById(R.id.clothesSelect_all_access);
        CheckBox clothesSelect_all_etc = (CheckBox) view.findViewById(R.id.clothesSelect_all_etc);
        CheckBox clothesSelect_top = (CheckBox) view.findViewById(R.id.clothesSelect_top);
        CheckBox clothesSelect_bottom = (CheckBox) view.findViewById(R.id.clothesSelect_bottom);
        CheckBox clothesSelect_outer = (CheckBox) view.findViewById(R.id.clothesSelect_outer);
        CheckBox clothesSelect_shoes = (CheckBox) view.findViewById(R.id.clothesSelect_shoes);
        CheckBox clothesSelect_under = (CheckBox) view.findViewById(R.id.clothesSelect_under);
        CheckBox clothesSelect_socks = (CheckBox) view.findViewById(R.id.clothesSelect_socks);
        CheckBox clothesSelect_hat = (CheckBox) view.findViewById(R.id.clothesSelect_hat);
        CheckBox clothesSelect_access = (CheckBox) view.findViewById(R.id.clothesSelect_access);
        CheckBox clothesSelect_bag = (CheckBox) view.findViewById(R.id.clothesSelect_bag);
        CheckBox clothesSelect_set = (CheckBox) view.findViewById(R.id.clothesSelect_set);
        CheckBox clothesSelect_etc = (CheckBox) view.findViewById(R.id.clothesSelect_etc);
        CheckBox clothesSelect_input = (CheckBox) view.findViewById(R.id.clothesSelect_input);

        clothesSelect_top.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (clothesSelect_top.isChecked() && clothesSelect_bottom.isChecked()
                            && clothesSelect_outer.isChecked() && clothesSelect_shoes.isChecked()
                            && clothesSelect_under.isChecked() && clothesSelect_socks.isChecked()) {
                        clothesSelect_all_clothes.setChecked(true);
                    }
                } else {
                    if (clothesSelect_all_clothes.isChecked())
                    {
                        clothesSelect_all_clothes.setChecked(false);
                        clothesSelect_top.setChecked(false);
                        clothesSelect_bottom.setChecked(true);
                        clothesSelect_outer.setChecked(true);
                        clothesSelect_shoes.setChecked(true);
                        clothesSelect_under.setChecked(true);
                        clothesSelect_socks.setChecked(true);
                    }
                }
            }

        });

        clothesSelect_bottom.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (clothesSelect_top.isChecked() && clothesSelect_bottom.isChecked()
                            && clothesSelect_outer.isChecked() && clothesSelect_shoes.isChecked()
                            && clothesSelect_under.isChecked() && clothesSelect_socks.isChecked()) {
                        clothesSelect_all_clothes.setChecked(true);
                    }
                } else {
                    if (clothesSelect_all_clothes.isChecked()) {
                        clothesSelect_all_clothes.setChecked(false);
                        clothesSelect_top.setChecked(true);
                        clothesSelect_bottom.setChecked(false);
                        clothesSelect_outer.setChecked(true);
                        clothesSelect_shoes.setChecked(true);
                        clothesSelect_under.setChecked(true);
                        clothesSelect_socks.setChecked(true);
                    }
                }
            }

        });

        clothesSelect_outer.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (clothesSelect_top.isChecked() && clothesSelect_bottom.isChecked()
                            && clothesSelect_outer.isChecked() && clothesSelect_shoes.isChecked()
                            && clothesSelect_under.isChecked() && clothesSelect_socks.isChecked()) {
                        clothesSelect_all_clothes.setChecked(true);
                    }
                } else {
                    if (clothesSelect_all_clothes.isChecked()) {
                        clothesSelect_all_clothes.setChecked(false);
                        clothesSelect_top.setChecked(true);
                        clothesSelect_bottom.setChecked(true);
                        clothesSelect_outer.setChecked(false);
                        clothesSelect_shoes.setChecked(true);
                        clothesSelect_under.setChecked(true);
                        clothesSelect_socks.setChecked(true);
                    }
                }
            }
        });

        clothesSelect_shoes.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (clothesSelect_top.isChecked() && clothesSelect_bottom.isChecked()
                            && clothesSelect_outer.isChecked() && clothesSelect_shoes.isChecked()
                            && clothesSelect_under.isChecked() && clothesSelect_socks.isChecked()) {
                        clothesSelect_all_clothes.setChecked(true);
                    }
                } else {
                    if (clothesSelect_all_clothes.isChecked()) {
                        clothesSelect_all_clothes.setChecked(false);
                        clothesSelect_top.setChecked(true);
                        clothesSelect_bottom.setChecked(true);
                        clothesSelect_outer.setChecked(true);
                        clothesSelect_shoes.setChecked(false);
                        clothesSelect_under.setChecked(true);
                        clothesSelect_socks.setChecked(true);
                    }
                }
            }

        });

        clothesSelect_under.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (clothesSelect_top.isChecked() && clothesSelect_bottom.isChecked()
                            && clothesSelect_outer.isChecked() && clothesSelect_shoes.isChecked()
                            && clothesSelect_under.isChecked() && clothesSelect_socks.isChecked()) {
                        clothesSelect_all_clothes.setChecked(true);
                    }
                } else {
                    if (clothesSelect_all_clothes.isChecked()) {
                        clothesSelect_all_clothes.setChecked(false);
                        clothesSelect_top.setChecked(true);
                        clothesSelect_bottom.setChecked(true);
                        clothesSelect_outer.setChecked(true);
                        clothesSelect_shoes.setChecked(true);
                        clothesSelect_under.setChecked(false);
                        clothesSelect_socks.setChecked(true);
                    }
                }
            }

        });

        clothesSelect_socks.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (clothesSelect_top.isChecked() && clothesSelect_bottom.isChecked()
                            && clothesSelect_outer.isChecked() && clothesSelect_shoes.isChecked()
                            && clothesSelect_under.isChecked() && clothesSelect_socks.isChecked()) {
                        clothesSelect_all_clothes.setChecked(true);
                    }
                } else {
                    if (clothesSelect_all_clothes.isChecked()) {
                        clothesSelect_all_clothes.setChecked(false);
                        clothesSelect_top.setChecked(true);
                        clothesSelect_bottom.setChecked(true);
                        clothesSelect_outer.setChecked(true);
                        clothesSelect_shoes.setChecked(true);
                        clothesSelect_under.setChecked(true);
                        clothesSelect_socks.setChecked(false);
                    }
                }
            }
        });

        clothesSelect_hat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (clothesSelect_hat.isChecked() && clothesSelect_access.isChecked()
                            && clothesSelect_bag.isChecked()) {
                        clothesSelect_all_access.setChecked(true);
                    }
                } else {
                    if (clothesSelect_all_access.isChecked()) {
                        clothesSelect_all_access.setChecked(false);
                        clothesSelect_hat.setChecked(false);
                        clothesSelect_access.setChecked(true);
                        clothesSelect_bag.setChecked(true);
                    }
                }
            }
        });

        clothesSelect_access.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (clothesSelect_hat.isChecked() && clothesSelect_access.isChecked()
                            && clothesSelect_bag.isChecked()) {
                        clothesSelect_all_access.setChecked(true);
                    }
                } else {
                    if (clothesSelect_all_access.isChecked()) {
                        clothesSelect_all_access.setChecked(false);
                        clothesSelect_hat.setChecked(true);
                        clothesSelect_access.setChecked(false);
                        clothesSelect_bag.setChecked(true);
                    }
                }
            }

        });

        clothesSelect_bag.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (clothesSelect_hat.isChecked() && clothesSelect_access.isChecked()
                            && clothesSelect_bag.isChecked()) {
                        clothesSelect_all_access.setChecked(true);
                    }
                } else {
                    if (clothesSelect_all_access.isChecked()) {
                        clothesSelect_all_access.setChecked(false);
                        clothesSelect_hat.setChecked(true);
                        clothesSelect_access.setChecked(true);
                        clothesSelect_bag.setChecked(false);
                    }
                }
            }
        });

        clothesSelect_set.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (clothesSelect_set.isChecked() && clothesSelect_etc.isChecked()
                            && clothesSelect_input.isChecked()) {
                        clothesSelect_all_etc.setChecked(true);
                    }
                } else {
                    if (clothesSelect_all_etc.isChecked()) {
                        clothesSelect_all_etc.setChecked(false);
                        clothesSelect_set.setChecked(false);
                        clothesSelect_etc.setChecked(true);
                        clothesSelect_input.setChecked(true);
                    }
                }
            }
        });

        clothesSelect_etc.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (clothesSelect_set.isChecked() && clothesSelect_etc.isChecked()
                            && clothesSelect_input.isChecked()) {
                        clothesSelect_all_etc.setChecked(true);
                    }
                } else {
                    if (clothesSelect_all_etc.isChecked()) {
                        clothesSelect_all_etc.setChecked(false);
                        clothesSelect_set.setChecked(true);
                        clothesSelect_etc.setChecked(false);
                        clothesSelect_input.setChecked(true);
                    }
                }
            }
        });

        clothesSelect_input.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (clothesSelect_set.isChecked() && clothesSelect_etc.isChecked()
                            && clothesSelect_input.isChecked()) {
                        clothesSelect_all_etc.setChecked(true);
                    }
                } else {
                    if (clothesSelect_all_etc.isChecked()) {
                        clothesSelect_all_etc.setChecked(false);
                        clothesSelect_set.setChecked(true);
                        clothesSelect_etc.setChecked(true);
                        clothesSelect_input.setChecked(false);
                    }
                }
            }
        });

        clothesSelect_all_clothes.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    clothesSelect_top.setChecked(true);
                    clothesSelect_bottom.setChecked(true);
                    clothesSelect_outer.setChecked(true);
                    clothesSelect_shoes.setChecked(true);
                    clothesSelect_under.setChecked(true);
                    clothesSelect_socks.setChecked(true);
                    if (clothesSelect_all_clothes.isChecked() && clothesSelect_all_access.isChecked()
                            && clothesSelect_all_etc.isChecked()) {
                        clothesSelect_all.setChecked(true);
                    }
                } else {
                    if (!clothesSelect_top.isChecked() || !clothesSelect_bottom.isChecked()
                            || !clothesSelect_outer.isChecked() || !clothesSelect_shoes.isChecked()
                            || !clothesSelect_under.isChecked() || !clothesSelect_socks.isChecked()) {
                        clothesSelect_all_access.setChecked(false);
                        clothesSelect_all.setChecked(false);
                        clothesSelect_all_clothes.setChecked(false);
                        clothesSelect_all_access.setChecked(false);
                        clothesSelect_all_etc.setChecked(false);

                    } else {
                        clothesSelect_top.setChecked(false);
                        clothesSelect_bottom.setChecked(false);
                        clothesSelect_outer.setChecked(false);
                        clothesSelect_shoes.setChecked(false);
                        clothesSelect_under.setChecked(false);
                        clothesSelect_socks.setChecked(false);
                        if(clothesSelect_all.isChecked())
                        {
                            clothesSelect_all.setChecked(false);
                        }
                    }
                }
            }
        });

        clothesSelect_all_access.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    clothesSelect_hat.setChecked(true);
                    clothesSelect_access.setChecked(true);
                    clothesSelect_bag.setChecked(true);
                    if (clothesSelect_all_clothes.isChecked() && clothesSelect_all_access.isChecked()
                            && clothesSelect_all_etc.isChecked()) {
                        clothesSelect_all.setChecked(true);
                    }
                } else {
                    if (!clothesSelect_hat.isChecked() || !clothesSelect_access.isChecked()
                            || !clothesSelect_bag.isChecked()) {
                        clothesSelect_all_access.setChecked(false);
                        clothesSelect_all.setChecked(false);
                        clothesSelect_all_clothes.setChecked(false);
                        clothesSelect_all_access.setChecked(false);
                        clothesSelect_all_etc.setChecked(false);
                    } else {
                        clothesSelect_hat.setChecked(false);
                        clothesSelect_access.setChecked(false);
                        clothesSelect_bag.setChecked(false);
                        if(clothesSelect_all.isChecked())
                        {
                            clothesSelect_all.setChecked(false);
                        }
                    }
                }
            }
        });

        clothesSelect_all_etc.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    clothesSelect_set.setChecked(true);
                    clothesSelect_etc.setChecked(true);
                    clothesSelect_input.setChecked(true);
                    if (clothesSelect_all_clothes.isChecked() && clothesSelect_all_access.isChecked()
                            && clothesSelect_all_etc.isChecked()) {
                        clothesSelect_all.setChecked(true);
                    }
                } else {
                    if ( !clothesSelect_set.isChecked() || !clothesSelect_etc.isChecked()
                            || !clothesSelect_input.isChecked()) {
                        clothesSelect_all_etc.setChecked(false);
                        clothesSelect_all.setChecked(false);
                        clothesSelect_all_clothes.setChecked(false);
                        clothesSelect_all_access.setChecked(false);
                        clothesSelect_all_etc.setChecked(false);
                    } else {
                        clothesSelect_set.setChecked(false);
                        clothesSelect_etc.setChecked(false);
                        clothesSelect_input.setChecked(false);
                        if(clothesSelect_all.isChecked())
                        {
                            clothesSelect_all.setChecked(false);
                        }
                    }
                }
            }
        });

        // 정렬 버튼(옷 종류 - 모두 선택)
        clothesSelect_all.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    clothesSelect_all.setChecked(true);
                    clothesSelect_all_clothes.setChecked(true);
                    clothesSelect_all_access.setChecked(true);
                    clothesSelect_all_etc.setChecked(true);
                } else {
                    if (!clothesSelect_all_clothes.isChecked() || !clothesSelect_all_access.isChecked()
                            || !clothesSelect_all_etc.isChecked()) {
                        clothesSelect_all.setChecked(false);
                    } else {
                        clothesSelect_all_clothes.setChecked(false);
                        clothesSelect_all_access.setChecked(false);
                        clothesSelect_all_etc.setChecked(false);
                    }
                }
            }
        });
    }


    // 계절 선택
    public void weatherSelect(View view) {
        // 정렬 버튼 (계절)
        CheckBox weatherSelect_spring = (CheckBox) view.findViewById(R.id.weatherSelect_spring);
        CheckBox weatherSelect_summer = (CheckBox) view.findViewById(R.id.weatherSelect_summer);
        CheckBox weatherSelect_fall = (CheckBox) view.findViewById(R.id.weatherSelect_fall);
        CheckBox weatherSelect_winter = (CheckBox) view.findViewById(R.id.weatherSelect_winter);
        CheckBox weatherSelect_communal = (CheckBox) view.findViewById(R.id.weatherSelect_communal);

        weatherSelect_spring.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (weatherSelect_summer.isChecked() && weatherSelect_fall.isChecked()
                            && weatherSelect_winter.isChecked()) {
                        weatherSelect_communal.setChecked(true);
                    }
                } else {
                    if(weatherSelect_communal.isChecked())
                    {
                        weatherSelect_communal.setChecked(false);
                        weatherSelect_spring.setChecked(true);
                        weatherSelect_summer.setChecked(false);
                        weatherSelect_fall.setChecked(false);
                        weatherSelect_winter.setChecked(false);
                    }
                }
                weatherButtonBase(view);
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
                    if(weatherSelect_communal.isChecked())
                    {
                        weatherSelect_communal.setChecked(false);
                        weatherSelect_spring.setChecked(false);
                        weatherSelect_summer.setChecked(true);
                        weatherSelect_fall.setChecked(false);
                        weatherSelect_winter.setChecked(false);
                    }
                }
                weatherButtonBase(view);
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
                    if(weatherSelect_communal.isChecked())
                    {
                        weatherSelect_communal.setChecked(false);
                        weatherSelect_spring.setChecked(false);
                        weatherSelect_summer.setChecked(false);
                        weatherSelect_fall.setChecked(true);
                        weatherSelect_winter.setChecked(false);
                    }
                }
                weatherButtonBase(view);
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
                    if(weatherSelect_communal.isChecked())
                    {
                        weatherSelect_communal.setChecked(false);
                        weatherSelect_spring.setChecked(false);
                        weatherSelect_summer.setChecked(false);
                        weatherSelect_fall.setChecked(false);
                        weatherSelect_winter.setChecked(true);
                    }
                }
                weatherButtonBase(view);
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
                    weatherButtonBase(view);
                }
            }
        });

        // 정렬 버튼 (하단 정렬)
        CheckBox sortSelect_name = (CheckBox) view.findViewById(R.id.sortSelect_name);
        CheckBox sortSelect_asc = (CheckBox) view.findViewById(R.id.sortSelect_asc);

        sortSelect_name.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(sortSelect_name.isChecked()) {
                    sortSelect_name.setBackgroundResource(R.drawable.left_rounded_on);
                    sortSelect_name.setText("이름순정렬");
                } else {
                    sortSelect_name.setBackgroundResource(R.drawable.left_rounded_off);
                    sortSelect_name.setText("날짜순정렬");
                }
            }
        });
        sortSelect_asc.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(sortSelect_asc.isChecked()) {
                    sortSelect_asc.setBackgroundResource(R.drawable.right_rounded_on);
                    sortSelect_asc.setText("↓ 내림차순");
                } else {
                    sortSelect_asc.setBackgroundResource(R.drawable.right_rounded_off);
                    sortSelect_asc.setText("↑ 오름차순");
                }
            }
        });
    }

    // 계절 버튼 클릭 시 색상 변환
    private void weatherButtonBase (View view) {
        CheckBox weatherSelect_spring = (CheckBox) view.findViewById(R.id.weatherSelect_spring);
        CheckBox weatherSelect_summer = (CheckBox) view.findViewById(R.id.weatherSelect_summer);
        CheckBox weatherSelect_fall = (CheckBox) view.findViewById(R.id.weatherSelect_fall);
        CheckBox weatherSelect_winter = (CheckBox) view.findViewById(R.id.weatherSelect_winter);
        CheckBox weatherSelect_communal = (CheckBox) view.findViewById(R.id.weatherSelect_communal);
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
}
