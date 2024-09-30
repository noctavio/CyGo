package com.example.androidexample;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    private TextView messageText;   // define message textview variable
    Button b1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);             // link to Main activity XML
        b1 = (Button) = findViewByID(R.id.buttonMessage);

        b1 = setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Hello World!", Toast.LENGTH_Long).show();
            }
        })

        /* initialize UI elements */
        messageText = findViewById(R.id.main_msg_txt);      // link to message textview in the Main activity XML
        messageText.setText("Hello World");
    }
}