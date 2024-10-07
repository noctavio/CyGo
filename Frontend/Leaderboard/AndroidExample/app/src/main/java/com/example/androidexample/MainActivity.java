package com.example.androidexample;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button LeaderboardBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LeaderboardBtn = findViewById(R.id.LeaderboardBtn);

        LeaderboardBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.LeaderboardBtn) {
            Intent intent = new Intent(MainActivity.this, Leaderboard.class);
            startActivity(intent);
        }
    }
}
