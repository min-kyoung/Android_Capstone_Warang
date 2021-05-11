package org.tensorflow.lite.examples.detection;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SubMenu;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

public class CardActivity extends AppCompatActivity {
    private DrawCanvas drawCanvas;
    private FloatingActionButton fbPen;             //펜 모드 버튼
    private FloatingActionButton fbEraser;          //지우개 모드 버튼
    private FloatingActionButton fbClear;           // 전체지우개 모드 버튼
    private ConstraintLayout canvasContainer;       //캔버스 root view

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_card);

     //   ActionBar actionBar = getSupportActionBar();
      //  actionBar.hide();

        findId();
        canvasContainer.addView(drawCanvas);
        setOnClickListener();
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
}
