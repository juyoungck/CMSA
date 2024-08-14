package com.example.closetmanagementservicesapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SettingsNotice extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_notice);

        ImageButton btnBack_notice = (ImageButton) findViewById(R.id.btnBack_notice);
        btnBack_notice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingsNotice.this, Settings.class);
                startActivity(intent);
                finish();
            }
        });
    }
}