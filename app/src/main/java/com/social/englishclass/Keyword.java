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
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.AnimatedImageDrawable;
import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.media.SoundPool;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.bumptech.glide.signature.StringSignature;
import com.google.gson.Gson;
import com.social.englishclass.ui.main.PlaceholderFragment;

import org.apache.commons.lang.StringEscapeUtils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Keyword extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private KeywordAdapter mAdapter;
    private final static int LOADER_ID = 0x001;
    private String folder, lesson, login_name, login_number, accessKey;
    int maxLenSpeech = 16000 * 45;
    private  byte [] speechData = null;
    int lenSpeech = 0;
    boolean isRecording = false;
    boolean forceStop = false;
    String result;
    private ImageButton sendtest_btn;
    private SoundPool soundPool;
    int soundPlay1,soundPlay2,soundPlay3, privite_number ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_keyword);

        Intent intent = getIntent();
        lesson = intent.getStringExtra("lesson");
        login_name = intent.getStringExtra("login_name");
        login_number = intent.getStringExtra("login_number");


        if(!login_number.contains("teacher")) {
            String log_num = login_number.substring(3, 5);
            privite_number = Integer.parseInt(log_num);
            Log.e("로그인 넘버",""+privite_number);
        }else{
            privite_number = 10;
        }

//        getApplicationContext().getContentResolver().notifyChange(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null);
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
//        mic_help = (ImageView)findViewById(R.id.mic_help_image);
//        mic_help();
        howtouse_keyword_dialog();
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
//                    sendtest_btn.setImageResource(R.drawable.mic_rec);
                    mic_rec_stop();
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

        //효과음 설정
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();

            soundPool = new SoundPool.Builder().setAudioAttributes(audioAttributes).setMaxStreams(3).build();
        }else{
            //롤리팝 이하 버전일 경우
            // new SoundPool(1번,2번,3번)
            // 1번 - 음악 파일 갯수
            // 2번 - 스트림 타입
            // 3번 - 음질
            soundPool = new SoundPool(3, AudioManager.STREAM_MUSIC,0); }
            soundPlay1 = soundPool.load(this, R.raw.wawawa, 1);
            soundPlay2 = soundPool.load(this, R.raw.clapping2, 1);
            soundPlay3 = soundPool.load(this, R.raw.clapping1, 1);
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

                try {
                    Log.e("녹음 트라이", "트라이 시작");
                    String filename = Environment.getExternalStorageDirectory().getAbsolutePath()+"/englishclass/record/KeywordRecording.pcm";
                    OutputStream  os  = new FileOutputStream(filename);
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

                //AI 녹음시 파일생성

// 말한길이만큼 파일에 써준다
                    os.write(speechData,0,lenSpeech*2);
                    os.close();
                    Log.e("녹음 트라이 파일쓰기 끝", "파일쓰기 끝");
                    } catch (FileNotFoundException e) {
                    Log.e("녹음 트라이 파일익셉션", "파일익셉션");
                        e.printStackTrace();
                    } catch (IOException e) {
                    Log.e("녹음 트라이 IO익셉션", " IO익셉션");
                        e.printStackTrace();
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
        if(!login_number.contains("teacher")) {
            if (privite_number % 26 == 1) {
                accessKey = "e91b9924-679e-49ca-9f90-71704d3ce8b0";
            } else if (privite_number % 26 == 2) {
                accessKey = "fcea3de7-d5d2-4441-8160-a46764c090ed";
            } else if (privite_number % 26 == 3) {
                accessKey = "0fe29a05-1d6a-4f79-b253-d24b673f6bc7";
            } else if (privite_number % 26 == 4) {
                accessKey = "a19c4cb4-8f43-4532-bf34-405058350d59";
            }else if (privite_number % 26 == 5) {
                accessKey = "626179b2-bb88-41d2-af39-3deb30e54abb";
            }else if (privite_number % 26 == 6) {
                accessKey = "1424d226-6627-4a10-8503-580304ec72e7";
            }else if (privite_number % 26 == 7) {
                accessKey = "1105b048-80f8-477d-8103-96afff61b4bc";
            }else if (privite_number % 26 == 8) {
                accessKey = "801e22fd-32fc-425b-9bf6-e9ecd7b22edd";
            }else if (privite_number % 26 == 9) {
                accessKey = "c8012dd7-4fad-473a-863b-7b53aba80156";
            } else if (privite_number % 26 == 10) {
                accessKey = "1d4e8f38-3505-493e-b8cc-07a2006dda44";
            }else if (privite_number % 26 == 11) {
                accessKey = "047aa397-520f-4ca4-81f8-40f7fdd08d0b";
            }else if (privite_number % 26 == 12) {
                accessKey = "a4bc378b-235d-4f2d-a994-726833fdca04";
            }else if (privite_number % 26 == 13) {
                accessKey = "3183dfba-9bfc-40af-9851-9bf8ea200841";
            }else if (privite_number % 26 == 14) {
                accessKey = "9546deba-d607-470b-a600-d9e6f5375feb";
            }else if (privite_number % 26 == 15) {
                accessKey = "86afd2ba-50a3-48a1-a15f-f043e7834399";
            }else if (privite_number % 26 == 16) {
                accessKey = "89b4d631-ca8d-4d2a-9af5-c0125b4e0da4";
            }else if (privite_number % 26 == 17) {
                accessKey = "f96bbb2e-939a-415f-9e47-ce6d7b28b8e4";
            }else if (privite_number % 26 == 18) {
                accessKey = "2fa3dee2-4b4d-4bb8-bec0-0f125fe76a17";
            }else if (privite_number % 26 == 19) {
                accessKey = "e23edebf-24ab-4de4-a060-b1deabb5d8ff";
            }else if (privite_number % 26 == 20) {
                accessKey = "789d4f44-044f-4903-82f9-3263b1842a30";
            }else if (privite_number % 26 == 21) {
                accessKey = "3666d7c4-1613-4d3a-a111-75c1e7222b3b";
            }else if (privite_number % 26 == 22) {
                accessKey = "65e371cf-9f6b-4fb1-bbcb-392841998d29";
            }else if (privite_number % 26 == 23) {
                accessKey = "ffc00b48-a4b2-4528-b59b-16caf1d915c9";
            }else if (privite_number % 26 == 24) {
                accessKey = "893e13c3-8194-43d5-b616-238bd0d259ad";
            }else if (privite_number % 26 == 25) {
                accessKey = "853e4dcb-7fe5-4c01-9d2a-52e27cd0dab2";
            }else {
                accessKey = "68c063de-3739-4796-ba10-5c6c3152d760";
            }
        }else{
            accessKey = "68c063de-3739-4796-ba10-5c6c3152d760";
        }
        Log.e("서버키", ""+accessKey);
        Log.e("개인번호 나머지 값", "  "+(privite_number % 26));
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
//                Toast.makeText(this, "서버응답 시간이 초과되었습니다.(20초)", Toast.LENGTH_LONG).show();
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
            sendtestdialog.setCancelable(false);
            TextView textResult = (TextView) sendtestdialog.findViewById(R.id.textresult);
            TextView scoreResult = (TextView) sendtestdialog.findViewById(R.id.scoreresult);
            Button test_cancle = (Button)sendtestdialog.findViewById(R.id.test_cancle);
            Button myrec_btn = (Button)sendtestdialog.findViewById(R.id.myrec_btn);
            ImageView imageresult = (ImageView)sendtestdialog.findViewById(R.id.imageresult);

            test_cancle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendtestdialog.dismiss();
                }
            });
            myrec_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AudioApplication.getInstance().getServiceInterface().pcmplay();
                }
            });

            String r = StringEscapeUtils.unescapeJava(result);
            String target1 = "\"recognized\":\"";
            int target1num = r.indexOf(target1);
            String target2 = "\",\"score\":";
            int target2num = r.indexOf(target2);
            Log.e("평가결과 다이얼로그   ", ""+ r);
            if(!r.contains("ERROR")) {
                String text = r.substring(target1num + 14, target2num);
                String score = r.substring(target2num + 10, target2num + 13);
                Log.e("스코어", "점수는    " + score);
                float s = Math.round(Float.parseFloat(score) * 100 / 5);
                Log.e("평가결과 다이얼로그   ", "" + r);
                Log.e("평가결과 텍스트   ", "" + text);
                Log.e("평가결과 점수   ", "" + score);
                if (!text.equals("")) {
                    textResult.setText(text);
                } else {
                    textResult.setText("녹음된 문장이 없습니다.");
                    imageresult.setVisibility(View.INVISIBLE);
                }

                scoreResult.setText(Float.toString(s) + "%");
                if (s < 50 && s>0) {
//                    soundPlay = soundPool.load(sendtestdialog.getContext(), R.raw.wawawa, 1);
                    soundPool.play(soundPlay1, 1f,1f,0,0,1f);
                    imageresult.setImageResource(R.drawable.star1);
                } else if (50 <= s && s < 70) {
//                    soundPlay = soundPool.load(sendtestdialog.getContext(), R.raw.clapping2, 1);
                    soundPool.play(soundPlay2, 1f,1f,0,0,1f);
                    imageresult.setImageResource(R.drawable.star2);
                } else if(70 <= s){
//                    soundPlay = soundPool.load(sendtestdialog.getContext(), R.raw.clapping1, 1);
                    soundPool.play(soundPlay3, 1f,1f,0,0,1f);
                    imageresult.setImageResource(R.drawable.star3);
                }
            }else{
            textResult.setText("인터넷 연결이 불안정합니다. 밖으로 빠져 나갔다가 다시 시도해 보세요");}
            sendtestdialog.show();
            speechData = null;
        }
    }

    public void mic_rec_stop() {
        GlideDrawableImageViewTarget gifImage = new GlideDrawableImageViewTarget(sendtest_btn);
        Glide.clear(sendtest_btn);
        Glide.with(this).load(R.drawable.mic_rec_stop).signature(new StringSignature(UUID.randomUUID().toString())).into(gifImage);
    }
    public void howtouse_keyword_dialog(){
        final Dialog howtouse_keyword_dialog = new Dialog(Keyword.this);
        howtouse_keyword_dialog.setContentView(R.layout.howtouse_keyword);
        howtouse_keyword_dialog.setCancelable(false);
        howtouse_keyword_dialog.getWindow().setBackgroundDrawableResource(R.color.translucent_black);

        ImageButton howtouse_cancle_btn = (ImageButton) howtouse_keyword_dialog.findViewById(R.id.howtouse_cancle_btn);
        ImageView keyword_picture_image = (ImageView) howtouse_keyword_dialog.findViewById(R.id.keyword_picture_image);
        ImageView ai_test_image = (ImageView) howtouse_keyword_dialog.findViewById(R.id.ai_test_image);


        GlideDrawableImageViewTarget gifImage2 = new GlideDrawableImageViewTarget(ai_test_image);
        Glide.clear(ai_test_image);
        Glide.with(this).load(R.drawable.mic_help).signature(new StringSignature(UUID.randomUUID().toString())).into(gifImage2);

        GlideDrawableImageViewTarget gifImage1 = new GlideDrawableImageViewTarget(keyword_picture_image);
        Glide.clear(keyword_picture_image);
        Glide.with(this).load(R.drawable.wordclick).signature(new StringSignature(UUID.randomUUID().toString())).into(gifImage1);

        howtouse_cancle_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                howtouse_keyword_dialog.dismiss();
            }
        });
        howtouse_keyword_dialog.show();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        forceStop = true;
        isRecording = false;
        finish();

    }
}//메인 끝
