package org.tensorflow.lite.examples.detection;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.util.Random;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class CardGameActivity extends AppCompatActivity {

    ImageButton[] Picture = new ImageButton[4];
    Button[] Text = new Button[4];
    private String picturebtn_id;
    private String textbtn_id;
    private String btn_id;

    Button start_btn;

    int img_num[] = new int[4];
    int pic_num[] = new int[4];
    String pic_text[] = new String[4];
    String pic_name;
    String pic_result;
    String split_text;
    final Random rand = new Random();

    int check1 =0;
    int check2 =0;
    int num=0;
    Button[] btn = new Button[4];
    Button card_pic;
    Button text_pic;

    Button check_btn;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_cardgame);


        settingPicturebtn();
        loadImgArr();

        start_btn = findViewById(R.id.start_btn);
        check_btn = findViewById(R.id.check_btn);
        start_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                click_restart();
                loadImgArr();
            }
        });

    }


    //버튼 세팅(이미지버튼, 텍스트버튼)
    private void settingPicturebtn(){
        //이미지버튼 id부여, 초기화
        for(int i=0; i <Picture.length; i++){
            picturebtn_id = "picture" + (i+1); //버튼 아이디값 저장
            Picture[i] = findViewById(getResources().getIdentifier(picturebtn_id, "id",getPackageName()));
            textbtn_id = "text" + (i+1);
            Text[i] = findViewById(getResources().getIdentifier(textbtn_id, "id",getPackageName()));
            btn_id = "btn" +(i+1);
            btn[i] = findViewById(getResources().getIdentifier(btn_id,"id",getPackageName()));

        }
    }

    // 저장소에서 4개의 사진,텍스트 불러오기
    private void loadImgArr() {
        try{
            File file = new File(getFilesDir() + "/capture");
            File[] filelist = file.listFiles();
            final String[] text = new String[1];

            //4개 사진 랜덤으로 불러오기
            for(int i=0; i<4; i++) {
                img_num[i]=rand.nextInt(filelist.length);

                for(int j=0;j<i;j++) {
                    if (img_num[i] == (img_num[j])) {
                        i--;
                    }
                }
                Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(filelist[img_num[i]]));
                Picture[i].setImageBitmap(bitmap);
                pic_name = filelist[img_num[i]].getName();
                split_text = pic_name.substring(0,pic_name.length()-4);
                pic_text[i] = split_text;
                btn[i].setText(split_text);

                //불러온 4개의 사진의 텍스트를 랜덤으로 배치
                for(int k=0; k<4; k++){
                    pic_num[k]=rand.nextInt(pic_text.length);

                    for(int j=0;j<k;j++) {
                        if (pic_num[k] == (pic_num[j])) {
                            k--;
                        }
                    }
                    pic_result = pic_text[pic_num[k]];
                    Text[k].setText(pic_result);
                }

                final int finalI = i;

                btn[i].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        check1 ++;
                        if (check1==1){
                            card_pic = findViewById(v.getId());
                            card_pic.setBackgroundResource(R.drawable.border_pic_click);
                            text[0] = btn[finalI].getText().toString();

                            Log.e("tag","사진 선택끝");
                            Log.e("tag",btn[finalI].getText()+"");
                        }

                    }
                });

                Text[i].setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        check2 ++;
                        if (check2==1){
                            text_pic = findViewById(v.getId());
                            text_pic.setBackgroundResource(R.drawable.border_text_click);
                            Log.e("tag","텍스트 선택끝");
                            Log.e("tag",Text[finalI].getText()+"");
                        }

                        if(check1==1 && check2 ==1){
                            if(text[0].equals(Text[finalI].getText())){
                                Log.e("tag","일치");
                                check_btn.setBackgroundResource(R.drawable.bear_match);
                                Text[finalI].setBackgroundResource(R.drawable.match_pic);
                                Text[finalI].setTextColor(Color.parseColor("#00ff0000"));
                                num++;

                            }else{
                                Log.e("tag","불일치");
                                check_btn.setBackgroundResource(R.drawable.bear_mismatch);
                                card_pic.setBackgroundResource(R.drawable.border_pic);
                                text_pic.setBackgroundResource(R.drawable.border_text);

                            }
                            check1=0;
                            check2=0;
                            if(num==4){
                                Log.e("tag","게임 끝");
                                AlertDialog.Builder builder = new AlertDialog.Builder(CardGameActivity.this);
                                builder.setTitle("게임 클리어").setMessage("축하합니다");
                                builder.setPositiveButton("확인", new DialogInterface.OnClickListener(){
                                    @Override
                                    public void onClick(DialogInterface dialog, int id)
                                    {
                                        click_restart();
                                        loadImgArr();
                                    }
                                });
                                AlertDialog alertDialog = builder.create();
                                alertDialog.show();
                            }

                        }

                    }
                });
            }

        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "파일 불러오기 실패", Toast.LENGTH_SHORT).show();
        }

    }


    public void click_restart(){
        for(int i=0; i<4; i++){
            Picture[i].setBackgroundResource(R.drawable.border_pic);
            btn[i].setBackgroundResource(R.drawable.border_pic);
            Text[i].setBackgroundResource(R.drawable.border_text);
            Text[i].setTextColor(Color.parseColor("#000000"));
            num=0;
            check1=0;
            check2=0;
            check_btn.setBackgroundResource(R.drawable.bear);
        }

    }





}

