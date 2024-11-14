package com.example.clubchat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button ClubBtn;
    private Button ClubChatBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ClubBtn = findViewById(R.id.ClubBtn);
        ClubChatBtn = findViewById(R.id.ClubChatBtn);

        ClubBtn.setOnClickListener(this);
        ClubChatBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

       if (view.getId() == R.id.ClubBtn) {
            Intent intent = new Intent(MainActivity.this, Club.class);
            startActivity(intent);
        }
       else if (view.getId() == R.id.ClubChatBtn) {
           Intent intent = new Intent(MainActivity.this, ClubChatActivity.class);
           startActivity(intent);
       }
    }
}

