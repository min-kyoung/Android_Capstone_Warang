package org.tensorflow.lite.examples.warang;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

public class ExerciseActivity extends AppCompatActivity {
    private DrawCanvas drawCanvas;
    private FloatingActionButton fbPen;             //펜 모드 버튼
    private FloatingActionButton fbEraser;          //지우개 모드 버튼
    private FloatingActionButton fbClear;           // 전체지우개 모드 버튼
    private ConstraintLayout canvasContainer;       //캔버스 root view
    private ImageButton preBtn;
    private ImageButton nextBtn;
    private ImageButton imgBtn;
    private TextView txt;
    private int i = 0;
    String str ;
    String split_text ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_card);

        preBtn = findViewById(R.id.preBtn);
        nextBtn = findViewById(R.id.nextBtn);
        imgBtn = findViewById(R.id.picture1);
        txt = findViewById(R.id.txt);


        //   ActionBar actionBar = getSupportActionBar();
        //  actionBar.hide();

        findId();
        canvasContainer.addView(drawCanvas);
        setOnClickListener();


        printStr();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    // View Id를 셋팅
    private void findId() {
        canvasContainer = findViewById(R.id.lo_canvas);
        fbPen = findViewById(R.id.fb_pen);
        fbEraser = findViewById(R.id.fb_eraser);
        fbClear = findViewById(R.id.fb_clear);
        drawCanvas = new DrawCanvas(this);
    }

    // OnClickListener Setting
    private void setOnClickListener() {
        fbPen.setOnClickListener((v)->{
            drawCanvas.changeTool(DrawCanvas.MODE_PEN);
        });

        fbEraser.setOnClickListener((v)->{
            drawCanvas.changeTool(DrawCanvas.MODE_ERASER);
        });

        fbClear.setOnClickListener((v)->{
            drawCanvas.invalidate();
            drawCanvas.init();
        });

    }

    // Pen을 표현할 class입니다.
    class Pen {
        public static final int STATE_START = 0;        //펜의 상태(움직임 시작)
        public static final int STATE_MOVE = 1;         //펜의 상태(움직이는 중)
        float x, y;                                     //펜의 좌표
        int moveStatus;                                 //현재 움직임 여부
        int color;                                      //펜 색
        int size;                                       //펜 두께

        public Pen(float x, float y, int moveStatus, int color, int size) {
            this.x = x;
            this.y = y;
            this.moveStatus = moveStatus;
            this.color = color;
            this.size = size;
        }

        // 현재 pen의 상태가 움직이는 상태인지 반환합니다.
        public boolean isMove() {
            return moveStatus == STATE_MOVE;
        }
    }

    // 그림이 그려질 canvas view
    class DrawCanvas extends View {
        public static final int MODE_PEN = 1;                     //모드 (펜)
        public static final int MODE_ERASER = 0;                  //모드 (지우개)
        final int PEN_SIZE = 10;                                   //펜 사이즈
        final int ERASER_SIZE = 50;                               //지우개 사이즈

        ArrayList<Pen> drawCommandList;                           //그리기 경로가 기록된 리스트
        Paint paint;                                              //펜
        Bitmap loadDrawImage;                                     //호출된 이전 그림
        int color;                                                //현재 펜 색상
        int size;                                                 //현재 펜 크기

        public DrawCanvas(Context context) {
            super(context);
            init();
        }

        public DrawCanvas(Context context, @Nullable AttributeSet attrs) {
            super(context, attrs);
            init();
        }

        public DrawCanvas(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
            init();
        }

        // 그리기에 필요한 요소를 초기화 합니다.
        private void init() {
            paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            drawCommandList = new ArrayList<>();
            loadDrawImage = null;
            color = Color.BLACK;
            size = PEN_SIZE;
        }

        // Tool type을 (펜 or 지우개)로 변경합니다.
        private void changeTool(int toolMode) {
            if (toolMode == MODE_PEN) {
                this.color = Color.BLACK;
                size = PEN_SIZE;
            } else if(toolMode == MODE_ERASER) {
                this.color = Color.WHITE;
                size = ERASER_SIZE;
            }
            paint.setColor(color);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            canvas.drawColor(Color.WHITE);

            if (loadDrawImage != null) {
                canvas.drawBitmap(loadDrawImage, 0, 0, null);
            }

            for (int i = 0; i < drawCommandList.size(); i++) {
                Pen p = drawCommandList.get(i);
                paint.setColor(p.color);
                paint.setStrokeWidth(p.size);

                if (p.isMove()) {
                    Pen prevP = drawCommandList.get(i - 1);
                    canvas.drawLine(prevP.x, prevP.y, p.x, p.y, paint);
                }
            }
        }

        @Override
        public boolean onTouchEvent(MotionEvent e) {
            int action = e.getAction();
            int state = action == MotionEvent.ACTION_DOWN ? Pen.STATE_START : Pen.STATE_MOVE;
            drawCommandList.add(new Pen(e.getX(), e.getY(), state, color, size));
            invalidate();
            return true;
        }
    }
    private void printStr(){

    /*
    label = new String[]{"양배추,cabbage", "사과,apple", "사람,person", "비행기,airplane",
            "라쿤,raccoon", "고양이,cat", "원숭이,monkey", "새,bird", "컵,cup",
            "안경,glasses","자동차,car", "신발,shoes", "모자,hat", "모니터,monitor",
            "마우스,mouse", "키보드,keyboard"};
    */


        //randomTxt.setText(label[randomLabel]);

        try{
            //int randomLabel = (int) (Math.random() * 16);

            File file = new File(getFilesDir() + "/capture");
            File[] filelist = file.listFiles();

            i=0;
            //assert filelist != null;
            //txt.setText(filelist[i].getName());


            str = filelist[i].getName();
            split_text = str.substring(0,str.length()-4);
            txt.setText(split_text);

            imgBtn.bringToFront();
            Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(filelist[i]));
            imgBtn.setImageBitmap(bitmap);


            if(i > 0 || i <filelist.length-1){
                preBtn.setEnabled(true);
            }
            else {
                preBtn.setEnabled(false);
            }

            if(i<filelist.length-2){
                nextBtn.setEnabled(true);
            }
            else {
                nextBtn.setEnabled(false);
            }
            preBtn.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                @Override public void onClick(View v) {
                    //preBtn.setEnabled(true);
                    if(i>0 && i<filelist.length){
                        //preBtn.setEnabled(true);
                        i--;
                        str = filelist[i].getName();
                        split_text = str.substring(0,str.length()-4);
                        txt.setText(split_text);

                        Bitmap bitmap = null;
                        try {
                            bitmap = BitmapFactory.decodeStream(new FileInputStream(filelist[i]));
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        imgBtn.bringToFront();
                        imgBtn.setImageBitmap(bitmap);


                    }
                    else {
                        //preBtn.setEnabled(false);
                    }

                }
            });

            nextBtn.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                @Override public void onClick(View v) {
                    //nextBtn.setEnabled(true);
                    if(i<filelist.length-1){
                        //nextBtn.setEnabled(true);
                        i++;
                        str = filelist[i].getName();
                        split_text = str.substring(0,str.length()-4);
                        txt.setText(split_text);

                        Bitmap bitmap = null;
                        try {
                            bitmap = BitmapFactory.decodeStream(new FileInputStream(filelist[i]));
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        imgBtn.bringToFront();
                        imgBtn.setImageBitmap(bitmap);


                    }
                    else {
                       // nextBtn.setEnabled(false);
                    }

                }
            });

        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "파일 불러오기 실패", Toast.LENGTH_SHORT).show();
        }

    }
}