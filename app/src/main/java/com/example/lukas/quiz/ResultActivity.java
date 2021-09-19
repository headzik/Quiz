package com.example.lukas.quiz;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        Intent intent = getIntent();

        int score = intent.getIntExtra("SCORE", 0);
        int pointsAvailable = intent.getIntExtra("POINTS_AVAILABLE", 0);

        TextView scoreTextView = new TextView(this);
        scoreTextView.setText(score + "/" + pointsAvailable);

        scoreTextView.setTextSize(30);

        Button backButton = new Button(this);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startAgain();
            }
        });
        backButton.setText("Start again");

        RelativeLayout.LayoutParams bbLayoutParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        bbLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);

        RelativeLayout.LayoutParams stvLayoutParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        stvLayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        stvLayoutParams.addRule(RelativeLayout.ABOVE, R.id.radio_group_id);

        RelativeLayout layout = (RelativeLayout) findViewById(R.id.activity_result);

        layout.addView(scoreTextView, stvLayoutParams);
        layout.addView(backButton, bbLayoutParams);
    }

    public void startAgain() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

}
