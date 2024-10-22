package com.example.closetmanagementservicesapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class Settings extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        // 뒤로 가기
        ImageButton btnBack = (ImageButton) findViewById(R.id.btnBack_settings);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // 공지 사항
        Button btnNotice = (Button) findViewById(R.id.btnNotice);
        btnNotice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Settings.this, SettingsNotice.class);
                startActivity(intent);
            }
        });

        // 고객센터
        Button btnService = (Button) findViewById(R.id.btnService);
        btnService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings.this, SettingsService.class);
                startActivity(intent);
            }
        });

        // 버전 정보
        Button btnVersion = (Button) findViewById(R.id.btnVersion);
        btnVersion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast toast = Toast.makeText(getApplicationContext(), "현재 애플리케이션 버전: 1.0.0",Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }
}
