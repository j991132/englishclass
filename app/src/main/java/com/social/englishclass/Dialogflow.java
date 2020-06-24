package com.social.englishclass;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

import ai.api.AIListener;
import ai.api.android.AIConfiguration;
import ai.api.android.AIService;
import ai.api.model.AIError;
import ai.api.model.AIResponse;
import ai.api.model.Result;

public class Dialogflow extends AppCompatActivity implements AIListener {

    AIService aiService;
    TextView t;
    TextToSpeech textToSpeech;
    private ListView chat_view;
    ArrayAdapter<String> adapter;
    private ListView chatbot_view;
    private LinearLayout chat_layout;
    private TextView text_chat;
    ArrayList list = new ArrayList();
    int a=0;
    private ChatAdapter adapter1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialogflow);
        t= (TextView) findViewById(R.id.textView);
        chatbot_view = (ListView)findViewById(R.id.chatbot_view);

        adapter1 = new ChatAdapter(getApplicationContext(), R.layout.chatitem, list, a);

//        adapter = new ArrayAdapter<String>(this, R.layout.chatitem, R.id.text_chat);
        chatbot_view.setAdapter(adapter1);

        chat_layout = (LinearLayout)findViewById(R.id.chat_layout);
        text_chat = (TextView)findViewById(R.id.text_chat);



        int permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO);

        if (permission != PackageManager.PERMISSION_GRANTED) {

            makeRequest();
        }

        final AIConfiguration config = new AIConfiguration("d3f41084aac04fafaa76b5edefb3b60d",
                AIConfiguration.SupportedLanguages.English,
                AIConfiguration.RecognitionEngine.System);
        aiService = AIService.getService(this, config);
        aiService.setListener(this);
        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR){
                    Log.e("TTS 상태 에러아님",""+status);
                    textToSpeech.setLanguage(Locale.US);
                }
            }
        });

    }
    protected void makeRequest() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.RECORD_AUDIO},
                101);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 101: {

                if (grantResults.length == 0
                        || grantResults[0] !=
                        PackageManager.PERMISSION_GRANTED) {


                } else {

                }
                return;
            }
        }
    }

    public void buttonClicked(View view){
        aiService.startListening();
        Log.e("버튼 누름","");
    }
    @SuppressLint("ResourceType")
    @Override
    public void onResult(AIResponse result) {
        Result result1=result.getResult();

//        t.setText("Query "+result1.getResolvedQuery()+" action: "+result1.getAction()+"풀필먼트  "+result1.getFulfillment().getSpeech() );

        if(!result1.getFulfillment().getSpeech().equals("Sorry, can you say that again?")) {

            list.add(result1.getResolvedQuery());
            adapter1.notifyDataSetChanged();
//            adapter.add(result1.getResolvedQuery());
            a=1;

            list.add(result1.getFulfillment().getSpeech());
            adapter1.notifyDataSetChanged();
//            adapter.add(result1.getFulfillment().getSpeech());
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            textToSpeech.speak(result1.getFulfillment().getSpeech(), TextToSpeech.QUEUE_FLUSH, null, null);
        }else{
            textToSpeech.speak(result1.getFulfillment().getSpeech(), TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    @Override
    public void onError(AIError error) {

    }

    @Override
    public void onAudioLevel(float level) {

    }

    @Override
    public void onListeningStarted() {

    }

    @Override
    public void onListeningCanceled() {

    }

    @Override
    public void onListeningFinished() {

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // TTS 객체가 남아있다면 실행을 중지하고 메모리에서 제거한다.
        if(textToSpeech != null){
            textToSpeech.stop();
            textToSpeech.shutdown();
            textToSpeech = null;
        }
    }
}
