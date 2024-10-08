package com.example.closetmanagementservicesapp;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;


class ImageLoader_Closet_Modify {
    private DBHelper dbHelper;
    private SQLiteDatabase db;
    private Activity activity;
    private ImageView imageView;
    private String savedImagePath = "";
    private int c_id;

    public ImageLoader_Closet_Modify(Activity activity, ImageView imageView, int c_id) {
        this.activity = activity;
        this.imageView = imageView;
        this.c_id = c_id;
    }

    // 이미지를 선택하는 메서드
    public void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        activity.startActivityForResult(intent, 2);
    }

    // onActivityResult에서 호출되는 메서드
    public void loadImageFromResult(int requestCode, int resultCode, Intent data) {
        // DB OPEN
        dbHelper = MyApplication.getDbHelper();
        db = dbHelper.getWritableDatabase();

        if (requestCode == 2 && resultCode == Activity.RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            if (selectedImageUri != null) {
                try {
                    ContentResolver resolver = activity.getContentResolver();
                    InputStream inputStream = resolver.openInputStream(selectedImageUri);
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    inputStream.close();

                    Bitmap resizedBitmap = resizeBitmap(bitmap, 300, 300);

                    String fileName = "image_modify" + c_id + ".png";

                    // 이미지 저장
                    savedImagePath = saveImageInternalStorage(resizedBitmap, fileName);

                    loadImageFromStorage(savedImagePath);

                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);

                    imageView.setImageBitmap(bitmap);
                    Toast.makeText(activity.getApplicationContext(), "이미지를 성공적으로 로드했습니다.", Toast.LENGTH_LONG).show();

                    Cursor cursor = db.rawQuery("SELECT * FROM Main_Closet WHERE c_id = " + c_id, null);
                    if (cursor != null && cursor.moveToFirst()) {
                        Intent intent = new Intent(activity, DetailActivity.class);
                        intent.putExtra("c_id", cursor.getInt(cursor.getColumnIndexOrThrow("c_id")));
                        intent.putExtra("c_img", cursor.getString(cursor.getColumnIndexOrThrow("c_img")));
                        intent.putExtra("c_loc", cursor.getInt(cursor.getColumnIndexOrThrow("c_loc")));
                        intent.putExtra("c_name", cursor.getString(cursor.getColumnIndexOrThrow("c_name")));
                        intent.putExtra("c_type", cursor.getString(cursor.getColumnIndexOrThrow("c_type")));
                        intent.putExtra("c_size", cursor.getString(cursor.getColumnIndexOrThrow("c_size")));
                        intent.putExtra("c_brand", cursor.getString(cursor.getColumnIndexOrThrow("c_brand")));
                        intent.putExtra("c_tag", cursor.getInt(cursor.getColumnIndexOrThrow("c_tag")));
                        intent.putExtra("c_memo", cursor.getString(cursor.getColumnIndexOrThrow("c_memo")));
                        intent.putExtra("c_date", cursor.getString(cursor.getColumnIndexOrThrow("c_date")));
                        intent.putExtra("c_stack", cursor.getInt(cursor.getColumnIndexOrThrow("c_stack")));
                        intent.putExtra("c_img_modify", fileName);
                        intent.putExtra("fileUpload", true);

                        activity.startActivity(intent);
                        cursor.close();
                    }
                } catch (Exception e) {
                    Toast.makeText(activity.getApplicationContext(), "이미지 로드에 실패했습니다.", Toast.LENGTH_LONG).show();
                    Log.e("ImageLoader", "Error loading image", e);
                }
            }
        }
    }

    private String saveImageInternalStorage(Bitmap bitmap, String fileName) {
        File directory = new File(activity.getFilesDir(), "images");
        if (!directory.exists()) {
            directory.mkdirs();
        }

        File file = new File(directory, fileName);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            Log.d("File Save Path", file.getAbsolutePath());

            // 저장된 파일의 경로 반환
            return file.getAbsolutePath();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(activity, "이미지 저장에 실패했습니다.", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    private void loadImageFromStorage(String path) {
        if (path != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(path);
            imageView.setImageBitmap(bitmap);
        } else {
            Toast.makeText(activity, "이미지를 불러오는데 실패했습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    private Bitmap resizeBitmap(Bitmap originalBitmap, int width, int height) {
        return Bitmap.createScaledBitmap(originalBitmap, width, height, true);
    }
}