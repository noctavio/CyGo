package com.example.androidexample;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

public class PreLogin extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.prelogin);

        // Find the banner view
        ImageView banner = findViewById(R.id.banner);

        // Load the combined animation from XML
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.move_right);

        // Start the animation
        banner.startAnimation(animation);
    }
}


