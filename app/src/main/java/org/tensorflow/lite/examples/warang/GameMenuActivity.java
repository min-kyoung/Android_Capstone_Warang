package org.tensorflow.lite.examples.warang;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class GameMenuActivity extends AppCompatActivity {
    private Animation animation;
    ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_game);


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


    public void onClick_card(View view) {//카드게임
        //Toast.makeText(getApplicationContext(), "카드 학습을 시작합니다.", Toast.LENGTH_LONG).show();

        Intent intent = new Intent(getApplicationContext(), CardGameActivity.class);
        startActivity(intent);
    }

    public void onClick_treasure(View view) {//보물찾기
        //Toast.makeText(getApplicationContext(), "카드 학습을 시작합니다.", Toast.LENGTH_LONG).show();

        Intent intent = new Intent(getApplicationContext(), DetectorActivity.class);
        startActivity(intent);

    }

}