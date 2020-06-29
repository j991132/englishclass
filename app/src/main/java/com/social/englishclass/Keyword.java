package com.social.englishclass;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.social.englishclass.ui.main.PlaceholderFragment;

import org.apache.commons.lang.StringEscapeUtils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class Keyword extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private KeywordAdapter mAdapter;
    private final static int LOADER_ID = 0x001;
    private String folder, lesson;
    int maxLenSpeech = 16000 * 45;
    private  byte [] speechData = null;
    int lenSpeech = 0;
    boolean isRecording = false;
    boolean forceStop = false;
    String result;
    private ImageButton sendtest_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_keyword);

        Intent intent = getIntent();
        lesson = intent.getStringExtra("lesson");
        folder = "/storage/emulated/0/englishclass/lesson"+lesson+"keyword/sound";
        // OS가 Marshmallow 이상일 경우 권한체크를 해야 합니다.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1000);
            } else {
                // READ_EXTERNAL_STORAGE 에 대한 권한이 있음.
                getAudioListFromMediaDatabase();
            }
        }
        // OS가 Marshmallow 이전일 경우 권한체크를 하지 않는다.
        else {
            getAudioListFromMediaDatabase();
        }

        sendtest_btn = (ImageButton)findViewById(R.id.keyword_sendtest_btn);
        sendtest_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isRecording) {
                    forceStop = true;
                    sendtest_btn.setImageResource(R.drawable.mic_normal);
                    CheckTypesTask task = new CheckTypesTask();
                    task.execute();

                } else {
                    sendtest_btn.setImageResource(R.drawable.mic_rec);
                    try {
                        new Thread(new Runnable() {
                            public void run() {

                                try {
                                    recordSpeech();

                                } catch (RuntimeException e) {

                                    return;
                                }

                            }
                        }).start();
                    } catch (Throwable t) {
//                    textResult.setText("ERROR: " + t.toString());
                        forceStop = false;
                        isRecording = false;
//                        speechData = null;
                    }
                }
            }
        });

        mRecyclerView = (RecyclerView) findViewById(R.id.keyword_recyclerview);
        mAdapter = new KeywordAdapter(this, null);


        mRecyclerView.setAdapter(mAdapter);
        GridLayoutManager layoutManager = new GridLayoutManager(this,2);
        mRecyclerView.setLayoutManager(layoutManager);


    }//온크리에이트 끝

    public void getAudioListFromMediaDatabase() {
        getSupportLoaderManager().initLoader(LOADER_ID, null, new LoaderManager.LoaderCallbacks<Cursor>() {
            @Override
            public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                Log.d("겟리스트메서드", "메서드실생됨");
                Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
//               String folder = "/storage/emulated/0/Music";
                String[] projection = new String[]{
                        MediaStore.Audio.Media._ID,
                        MediaStore.Audio.Media.TITLE,
                        MediaStore.Audio.Media.DATA
                };
//쿼리를 위한 조건을 담는 부분 ? 한개당 1개의 아규먼트가 적용된다.
//해당폴더는 검색하고 하위폴더는 제외하는 내용
                String selection = MediaStore.Audio.Media.DATA + " LIKE ? AND " + MediaStore.Audio.Media.DATA + " NOT LIKE ? ";
// 원래는 미디어 ismusic 값이 1인 것(음악파일)은 모두 검색하는 조건이 들어갔었다
//                String selection = MediaStore.Audio.Media.IS_MUSIC + " = 1";
                String[] selectionArgs = new String[]{
                        "%" + folder + "%",
                        "%" + folder + "/%/%"
                };
                Log.d("겟리스트", "폴더" + selectionArgs);
                String sortOrder = MediaStore.Audio.Media.TITLE + " COLLATE LOCALIZED ASC";
                //               return new CursorLoader(getApplicationContext(), uri, projection, selection, null, sortOrder);
//검색 쿼리가 들어있는 내장파일 커서로더.java 를 호출한다.
                return new CursorLoader(getApplicationContext(), uri, projection, selection, selectionArgs, sortOrder);
            }

            @Override
            public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
                mAdapter.swapCursor(data);
                Log.d("커서데이터", "커서데이터" + data);
                if (data != null && data.getCount() > 0) {
                    while (data.moveToNext()) {
                        Log.i("태그", "Title:" + data.getString(data.getColumnIndex(MediaStore.Audio.Media.TITLE)));
                    }
                }
            }

            @Override
            public void onLoaderReset(Loader<Cursor> loader) {
                mAdapter.swapCursor(null);
            }
        });
    }
    public void recordSpeech() throws RuntimeException {
        try {
            speechData = new byte [maxLenSpeech * 2];
            int bufferSize = AudioRecord.getMinBufferSize(
                    16000, // sampling frequency
                    AudioFormat.CHANNEL_IN_MONO,
                    AudioFormat.ENCODING_PCM_16BIT);
            AudioRecord audio = new AudioRecord(
                    MediaRecorder.AudioSource.VOICE_RECOGNITION,
                    16000, // sampling frequency
                    AudioFormat.CHANNEL_IN_MONO,
                    AudioFormat.ENCODING_PCM_16BIT,
                    bufferSize);
            lenSpeech = 0;
            if (audio.getState() != AudioRecord.STATE_INITIALIZED) {
                throw new RuntimeException("ERROR: Failed to initialize audio device. Allow app to access microphone");
            }
            else {
                short [] inBuffer = new short [bufferSize];
                forceStop = false;
                isRecording = true;
                audio.startRecording();
                while (!forceStop) {
                    int ret = audio.read(inBuffer, 0, bufferSize);
                    for (int i = 0; i < ret ; i++ ) {
                        if (lenSpeech >= maxLenSpeech) {
                            forceStop = true;
                            break;
                        }
                        speechData[lenSpeech*2] = (byte)(inBuffer[i] & 0x00FF);
                        speechData[lenSpeech*2+1] = (byte)((inBuffer[i] & 0xFF00) >> 8);
                        lenSpeech++;
                    }
                }
                audio.stop();
                audio.release();
                isRecording = false;
            }
        } catch(Throwable t) {
            throw new RuntimeException(t.toString());
        }
    }
    public String sendDataAndGetResult () {
        String openApiURL = "http://aiopen.etri.re.kr:8000/WiseASR/Pronunciation";
        String accessKey = "68c063de-3739-4796-ba10-5c6c3152d760";
//        String accessKey = editID.getText().toString().trim();
        String languageCode = "english";
        String audioContents;

        Gson gson = new Gson();

//                languageCode = "english";
//                openApiURL = "http://aiopen.etri.re.kr:8000/WiseASR/Pronunciation";


        Map<String, Object> request = new HashMap<>();
        Map<String, String> argument = new HashMap<>();

        audioContents = Base64.encodeToString(
                speechData, 0, lenSpeech*2, Base64.NO_WRAP);

        argument.put("language_code", languageCode);
        argument.put("audio", audioContents);

        request.put("access_key", accessKey);
        request.put("argument", argument);
//        speechData = null;
        URL url;
        Integer responseCode;
        String responBody;
        try {
            url = new URL(openApiURL);
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setRequestMethod("POST");
            con.setDoOutput(true);

            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.write(gson.toJson(request).getBytes("UTF-8"));
            wr.flush();
            wr.close();
            Log.e("보내기  ", ""+wr  );
            responseCode = con.getResponseCode();
            if ( responseCode == 200 ) {
                InputStream is = new BufferedInputStream(con.getInputStream());
                responBody = readStream(is);
                Log.e("받기  ", ""+responBody  );
                return responBody;

            }

            else
                return "ERROR: " + Integer.toString(responseCode);
        } catch (Throwable t) {
            return "ERROR: " + t.toString();
        }
    }
    public static String readStream(InputStream in) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader r = new BufferedReader(new InputStreamReader(in),1000);
        for (String line = r.readLine(); line != null; line =r.readLine()){
            sb.append(line);
        }
        in.close();
        return sb.toString();
    }

    public void send() {
        Thread threadRecog = new Thread(new Runnable() {
            public void run() {

                result = sendDataAndGetResult();

            }
        });
        threadRecog.start();
        try {
            threadRecog.join(20000);
            if (threadRecog.isAlive()) {
                threadRecog.interrupt();
//                        SendMessage("No response from server for 20 secs", 4);
                Toast.makeText(this, "서버응답 시간이 초과되었습니다.(20초)", Toast.LENGTH_LONG).show();
            } else {

            }
        }catch(InterruptedException e){
//                    SendMessage("Interrupted", 4);
        }
    }
    private class CheckTypesTask extends AsyncTask<String, Integer, String> {

        ProgressDialog asyncDialog = new ProgressDialog(
                Keyword.this);




        @Override
        protected void onPreExecute() {
            asyncDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            asyncDialog.setMessage("평가중 입니다..");

            // show dialog
            asyncDialog.show();
            super.onPreExecute();
        }

        protected String doInBackground(String... params) {
            send();
            return result;
        }

        @Override
        protected void onPostExecute(String re) {
            asyncDialog.dismiss();
            super.onPostExecute(re);
            //영어평가 결과 다이얼로그 띄우기
            final Dialog sendtestdialog = new Dialog(Keyword.this);
            sendtestdialog.setContentView(R.layout.sendtestdialog);
            TextView textResult = (TextView) sendtestdialog.findViewById(R.id.textresult);
            TextView scoreResult = (TextView) sendtestdialog.findViewById(R.id.scoreresult);
            Button test_cancle = (Button)sendtestdialog.findViewById(R.id.test_cancle);
            ImageView imageresult = (ImageView)sendtestdialog.findViewById(R.id.imageresult);

            test_cancle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendtestdialog.dismiss();
                }
            });

            String r = StringEscapeUtils.unescapeJava(result);
            String target1 = "\"recognized\":\"";
            int target1num = r.indexOf(target1);
            String target2 = "\",\"score\":";
            int target2num = r.indexOf(target2);
            Log.e("평가결과 다이얼로그   ", ""+ r);
            String text = r.substring(target1num+14,target2num);
            String score = r.substring(target2num+10, target2num+13);
            Log.e("스코어", "점수는    "+score);
            float s = Math.round(Float.parseFloat(score)*100/5);
            Log.e("평가결과 다이얼로그   ", ""+ r);
            Log.e("평가결과 텍스트   ", ""+ text);
            Log.e("평가결과 점수   ", ""+ score);
            if(!text.equals("")){
                textResult.setText(text);
            }else{textResult.setText("녹음된 문장이 없습니다.");}

            scoreResult.setText(Float.toString(s)+"%");
            if(s<50){
                imageresult.setImageResource(R.drawable.star1);
            }else if(50<=s && s<70 ){
                imageresult.setImageResource(R.drawable.star2);
            }else {imageresult.setImageResource(R.drawable.star3);}

            sendtestdialog.show();
            speechData = null;
        }
    }
}//메인 끝
