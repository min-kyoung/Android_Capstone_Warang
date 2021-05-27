package org.tensorflow.lite.examples.detection;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.wonderkiln.camerakit.CameraKitError;
import com.wonderkiln.camerakit.CameraKitEvent;
import com.wonderkiln.camerakit.CameraKitEventListener;
import com.wonderkiln.camerakit.CameraKitImage;
import com.wonderkiln.camerakit.CameraKitVideo;
import com.wonderkiln.camerakit.CameraView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

//stt tts


public class StudyTTSActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {

    private static final String MODEL_PATH = "model_unquant.tflite";
    private static final boolean QUANT = false;
    private static final String LABEL_PATH = "labels.txt";
    private static final int INPUT_SIZE = 224;

    private Classifier classifier;
    private ImageView correctView;


    private Executor executor = Executors.newSingleThreadExecutor();
    private TextView textName;
    private TextView textEnglish;
    private ImageButton btnDetectObject;
    private ImageView imageViewResult;
    private CameraView cameraView;

    FileOutputStream output = null;
    /* ----------------- stt / tts ---------------------- */

    //stt 말하기
    Intent intent;
    SpeechRecognizer mRecognizer;
    ImageButton speakEnglish;
    ImageButton speakKorean;
    TextView sttResult;
    //TextView answer;
    final int PERMISSION = 1;


    //tts 듣기
    private TextToSpeech tts;
    private ImageButton listenEnglish;
    private ImageButton listenKorean;

    //btn change
    private ImageButton earBtn;
    private ImageButton mouthBtn;
    private LinearLayout listenBtn;
    private LinearLayout speakBtn;


    @SuppressLint("CutPasteId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_studytts);
        cameraView = findViewById(R.id.cameraView);
        imageViewResult = findViewById(R.id.imageViewResult);

        textName = findViewById(R.id.textName);
        textName.setMovementMethod(new ScrollingMovementMethod());

        textEnglish = findViewById(R.id.textEnglish);
        btnDetectObject = findViewById(R.id.btnDetectObject);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);



        //btn change
        earBtn = findViewById(R.id.earBtn);
        mouthBtn = findViewById(R.id.mouthBtn);
        speakBtn = findViewById(R.id.speakBtn);
        listenBtn = findViewById(R.id.listenBtn);
        sttResult = findViewById(R.id.sttResult);

        //listenBtn.setClick
        speakBtn.setVisibility(View.GONE);
        earBtn.setVisibility(View.GONE);
        sttResult.setVisibility(View.GONE);


        mouthBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override public void onClick(View v) {
                mouthBtn.setVisibility(View.GONE);
                listenBtn.setVisibility(View.GONE);

                //
                textName.setVisibility(View.GONE);
                textEnglish.setVisibility(View.GONE);


                earBtn.setVisibility(View.VISIBLE);
                speakBtn.setVisibility(View.VISIBLE);
                sttResult.setVisibility(View.VISIBLE);

            }
        });

        earBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override public void onClick(View v) {
                earBtn.setVisibility(View.GONE);
                speakBtn.setVisibility(View.GONE);
                sttResult.setVisibility(View.GONE);

                mouthBtn.setVisibility(View.VISIBLE);
                listenBtn.setVisibility(View.VISIBLE);
                //textResult.setVisibility(View.VISIBLE);
                textName.setVisibility(View.VISIBLE);
                textEnglish.setVisibility(View.VISIBLE);
            }
        });

        int shortAnimationDuration = getResources().getInteger(
                android.R.integer.config_shortAnimTime);
        correctView = findViewById(R.id.correctView);
        correctView.bringToFront();

        cameraView.addCameraKitListener(new CameraKitEventListener() {
            @Override
            public void onEvent(CameraKitEvent cameraKitEvent) {

            }

            @Override
            public void onError(CameraKitError cameraKitError) {

            }

            @Override
            public void onImage(CameraKitImage cameraKitImage) {

                Bitmap bitmap = cameraKitImage.getBitmap();

                bitmap = Bitmap.createScaledBitmap(bitmap, INPUT_SIZE, INPUT_SIZE, false);

                imageViewResult.setImageBitmap(bitmap); //img 보여주기.

                final List<Classifier.Recognition> results = classifier.recognizeImage(bitmap);

                String str = results.get(0).toString();

                String[] word = str.split(",");

                textName.setText(word[0]); //text 보여주기
                textEnglish.setText(word[1]);

                //찍은 사진 저장
                try {
                    File storageDir = new File(getFilesDir() + "/capture");
                    if (!storageDir.exists()) { //폴더 없으면 생성
                        storageDir.mkdirs();
                    }
                    String filename =  str + ".jpg";

                    File file = new File(storageDir, filename);
                    boolean deleted = file.delete();
                    Log.e("사진 삭제", "delete :" + deleted);

                    try {
                        output = new FileOutputStream(file);
                        BitmapDrawable drawable = (BitmapDrawable) imageViewResult.getDrawable();
                        bitmap = drawable.getBitmap();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, output);

                    }catch (FileNotFoundException e){
                        e.printStackTrace();
                    }finally {
                        try{
                            assert output !=null;
                            output.close();
                        }catch (IOException e){
                            e.printStackTrace();
                        }
                    }
                    Log.e("", "저장 성공!");
                }catch (Exception e) {
                    Log.e("", "저장 실패!");
                }
            }

            @Override
            public void onVideo(CameraKitVideo cameraKitVideo) {

            }
        });




        btnDetectObject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraView.captureImage();
            }
        });

        initTensorFlowAndLoadModel();

        /* ----------------- stt / tts ---------------------- */

        // stt 말하기
        ActivityCompat.requestPermissions(this, new String[]
                {Manifest.permission.INTERNET, Manifest.permission.RECORD_AUDIO}, PERMISSION);
        //sttResult = (TextView) findViewById(R.id.sttResult);
        speakEnglish = (ImageButton) findViewById(R.id.speakEnglish); // RecognizerIntent 객체 생성
        speakKorean = (ImageButton) findViewById(R.id.speakKorean);
        intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getPackageName());

        //answer = (TextView) findViewById(R.id.answer);


        // 버튼을 클릭 이벤트 - 객체에 Context와 listener를 할당한 후 실행
        speakKorean.setOnClickListener(v -> {
            //Toast.makeText(getApplicationContext(),"speakEnglish ImageButton click", Toast.LENGTH_SHORT).show();
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR"); //언어설정
            mRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
            mRecognizer.setRecognitionListener(listener);
            mRecognizer.startListening(intent);
            //textName.setVisibility(View.VISIBLE);

        });

        speakEnglish.setOnClickListener(v -> {
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US");
            mRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
            mRecognizer.setRecognitionListener(listener);
            mRecognizer.startListening(intent);
            //textEnglish.setVisibility(View.VISIBLE);
        });

        //--------------------------------------------------//

        // tts 듣기
        tts = new TextToSpeech(this, (TextToSpeech.OnInitListener) this);
        listenKorean = findViewById(R.id.listenKorean);
        listenEnglish = findViewById(R.id.listenEnglish);

        listenKorean.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override public void onClick(View v) {
                speakKoreanOut();
                //textResult.setVisibility(View.VISIBLE);
                //textName.setVisibility(View.VISIBLE);
            }
        });

        listenEnglish.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override public void onClick(View v) {
                speakEnglishOut();
                //textResult.setVisibility(View.VISIBLE);
                //textEnglish.setVisibility(View.VISIBLE);
            }
        });




    }


    @Override
    protected void onResume() {
        super.onResume();
        cameraView.start();
    }

    @Override
    protected void onPause() {
        cameraView.stop();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                classifier.close();
            }
        });

        //stt , tts

        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
    }

    private void initTensorFlowAndLoadModel() {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    classifier = TensorFlowImageClassifier.create(
                            getAssets(),
                            MODEL_PATH,
                            LABEL_PATH,
                            INPUT_SIZE,
                            QUANT);
                    makeImageButtonVisible();
                } catch (final Exception e) {
                    throw new RuntimeException("Error initializing TensorFlow!", e);
                }
            }
        });
    }

    private void makeImageButtonVisible() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                btnDetectObject.setVisibility(View.VISIBLE);
            }
        });
    }

    //stt
    private RecognitionListener listener = new RecognitionListener() {
        @Override public void onReadyForSpeech(Bundle params) {
            Toast.makeText(getApplicationContext(),"음성인식을 시작합니다.",Toast.LENGTH_SHORT).show();
        }
        @Override public void onBeginningOfSpeech() {}
        @Override public void onRmsChanged(float rmsdB) {}
        @Override public void onBufferReceived(byte[] buffer) {}
        @Override public void onEndOfSpeech() {}
        @Override public void onError(int error) {
            String message; switch (error) {
                case SpeechRecognizer.ERROR_AUDIO:
                    message = "오디오 에러";
                    break;
                case SpeechRecognizer.ERROR_CLIENT:
                    message = "클라이언트 에러";
                    break;
                case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                    message = "퍼미션 없음";
                    break;
                case SpeechRecognizer.ERROR_NETWORK:
                    message = "네트워크 에러";
                    break;
                case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                    message = "네트웍 타임아웃";
                    break;
                case SpeechRecognizer.ERROR_NO_MATCH:
                    message = "찾을 수 없음";
                    break;
                case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                    message = "RECOGNIZER가 바쁨";
                    break;
                case SpeechRecognizer.ERROR_SERVER:
                    message = "서버가 이상함";
                    break;
                case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                    message = "말하는 시간초과";
                    break;
                default:
                    message = "알 수 없는 오류임";
                    break;
            }
            Toast.makeText(getApplicationContext(), "에러가 발생하였습니다. : " + message,
                    Toast.LENGTH_SHORT).show();
        }
        @Override public void onResults(Bundle results) {
            // 말을 하면 ArrayList에 단어를 넣고 textView에 단어를 이어줍니다.
            ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            for(int i = 0; i < matches.size() ; i++){
                sttResult.setText(matches.get(i));
            }

            if((sttResult.getText().toString().equals(textName.getText().toString()))||(sttResult.getText().toString().equals(textEnglish.getText().toString()))){
                Log.e("sttResult.getText() : ", sttResult.getText().toString());

                //answer.setText("정답입니다.");
                correctView.setImageResource(R.drawable.o);

                Animation animationDuration = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.duration);
                correctView.startAnimation(animationDuration);
                correctView.setVisibility(View.GONE);

                if((sttResult.getText().toString()).equals(textName.getText().toString())){
                    textName.setVisibility(View.VISIBLE);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            textName.setVisibility(View.GONE);
                        }
                    },3000);
                    textName.setVisibility(View.VISIBLE);
                }
                if((sttResult.getText().toString()).equals(textEnglish.getText().toString())){
                    textEnglish.setVisibility(View.VISIBLE);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            textEnglish.setVisibility(View.GONE);
                        }
                    },3000);
                    textEnglish.setVisibility(View.VISIBLE);
                }
            }
            else{
                //answer.setText("다시 시도하세요.");
                correctView.setImageResource(R.drawable.x);
                Animation animationDuration = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.duration);
                correctView.startAnimation(animationDuration);
                correctView.setVisibility(View.GONE);
            }
        }
        @Override public void onPartialResults(Bundle partialResults) {}
        @Override public void onEvent(int eventType, Bundle params) {}
    };

    //tts
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP) private void speakKoreanOut() {
        int result = tts.setLanguage(Locale.KOREA);
        CharSequence text = textName.getText();
        //tts.setPitch((float) 0.6);
        tts.setPitch(1.0f);
        //tts.setSpeechRate((float) 0.1);
        tts.setSpeechRate(0.8f);
        tts.speak(text,TextToSpeech.QUEUE_FLUSH,null,"id1");
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP) private void speakEnglishOut() {
        int result = tts.setLanguage(Locale.ENGLISH);
        CharSequence text = textEnglish.getText();
        //tts.setPitch((float) 0.6);
        tts.setPitch(1.0f);
        //tts.setSpeechRate((float) 0.1);
        tts.setSpeechRate(1.0f);
        tts.speak(text,TextToSpeech.QUEUE_FLUSH,null,"id2");
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result;
            if(speakKorean.hasOnClickListeners()) {
                Log.e("speakKorean : ","finish!!");
                result = tts.setLanguage(Locale.KOREA);
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e("TTS", "This Language is not supported");
                }
                else {

                    listenKorean.setEnabled(true);
                    //speakEnglishOut();
                    speakKoreanOut();
                }
            }
            else if(speakEnglish.hasOnClickListeners()){
                Log.e("speakEnglish : ","finish!!");
                result = tts.setLanguage(Locale.ENGLISH);

                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e("TTS", "This Language is not supported");
                }
                else {
                    listenEnglish.setEnabled(true);
                    speakEnglishOut();
                    //speakKoreanOut();
                }
            }

        }
        else {
            Log.e("TTS", "Initilization Failed!");
        }
    }
}
