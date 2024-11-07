package com.example.pre_game;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button PreGameBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PreGameBtn = findViewById(R.id.PreGameBtn);
        PreGameBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.PreGameBtn) {
            Intent intent = new Intent(MainActivity.this, PreGameActivity.class);
            startActivity(intent);
        }
    }
}