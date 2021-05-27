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
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class StudyMenuActivity extends AppCompatActivity {
    private Animation animation;
    ImageView mImageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_menu);

      //  ActionBar actionBar = getSupportActionBar();
     //   actionBar.hide();

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

    public void onClick(View view) {
        ImageButton arButton  = (ImageButton)findViewById(R.id.btn_call_unity);;

        Toast.makeText(getApplicationContext(), "낱말카드 AR학습을 시작합니다.", Toast.LENGTH_LONG).show();

        //arButton.setEnabled(false);
        Intent intent = new Intent(getApplicationContext(), UnityPlayerActivity.class);
        startActivity(intent);

        //유니티 플레이어 액티비티 실행
    }

    // Tensorflow - Detector 액티비티 실행
    public void onClickTensorflow(View view) {
        ImageButton arButton  = (ImageButton)findViewById(R.id.btn_call_tensorflow);;

        Toast.makeText(getApplicationContext(), "딥러닝 학습을 시작합니다.", Toast.LENGTH_LONG).show();

        //arButton.setEnabled(false);
        Intent intent = new Intent(getApplicationContext(), StudyTTSActivity.class);
        startActivity(intent);
    }
}

