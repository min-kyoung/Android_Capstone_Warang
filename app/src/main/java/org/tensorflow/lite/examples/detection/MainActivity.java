package org.tensorflow.lite.examples.detection;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private Animation animation;
    ImageView mImageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_main);

 //       ActionBar actionBar = getSupportActionBar();
//        actionBar.hide();

        ImageView iv = findViewById(R.id.bear);
        mImageView =(ImageView) findViewById(R.id.cloud);

        final Animation animationLeft = AnimationUtils.loadAnimation(this,R.anim.translate_left);
        mImageView.startAnimation(animationLeft);


        final AnimationDrawable drawable =
                (AnimationDrawable) iv.getBackground();
        if (drawable.isRunning()) { // 동작중일 경우
            drawable.stop();  // 멈추기
        } else { // 멈춰있는 경우
            drawable.start(); // 애니메이션 동작 개시
        }

    }

    public void onClick_study(View view) {
        ImageButton go_study = (ImageButton)findViewById(R.id.go_study);

        go_study.setEnabled(false);
        Intent intent = new Intent(getApplicationContext(), StudyMenuActivity.class);
        startActivity(intent);


    }

    public void onClick_game(View view) {
        Intent intent = new Intent(getApplicationContext(), GameMenuActivity.class);
        startActivity(intent);

    }

    public void onClick_exercise(View view) {

        Intent intent = new Intent(getApplicationContext(), ExerciseActivity.class);
        startActivity(intent);
    }


}

