package com.social.englishclass.ui.main;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.gson.Gson;
import com.social.englishclass.AudioApplication;
import com.social.englishclass.R;
import com.social.englishclass.SelectLesson;
import com.social.englishclass.englishlesson;
import com.social.englishclass.level;

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlaceholderFragment extends Fragment implements SurfaceHolder.Callback {

    private static final String ARG_SECTION_NUMBER = "section_number";

    private PageViewModel pageViewModel;
    private SurfaceView surfaceView;
    SurfaceHolder surfaceHolder;
    MediaPlayer mediaPlayer;
    public  String filepath;
    public static String[] filepatharr = new String[5];
    private static String lt;
    private Button videostart_btn;
    private Spinner spinner;
    private ImageButton mBtnPlayPause, sendtest_btn;
    ArrayList<String> arrayList;
    private  ArrayAdapter<String> arrayAdapter;
    private float f=1;
    private static boolean pause=false;
    private View root = null;
    private static boolean isPrepared ;
    int maxLenSpeech = 16000 * 45;
    private  byte [] speechData = null;
    int lenSpeech = 0;
    boolean isRecording = false;
    boolean forceStop = false;
    String result;
    private int pos;


    public static PlaceholderFragment newInstance(int index) {
        String ln = SelectLesson.lesson;
        lt = level.lesson_type;
        PlaceholderFragment fragment = new PlaceholderFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        if(lt.equals("let_lv2")){
            bundle.putString(ln+lt+"lv"+index, "/storage/emulated/0/englishclass/lesson/letsread/l"+ln+"_let/l"+ln+"_let_"+"lv"+index+".mp4");
        }else{
            bundle.putString(ln+lt+"lv"+index, "/storage/emulated/0/englishclass/lesson/l"+ln+"_"+lt+"_"+"lv"+index+".mp4");}
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        pageViewModel = ViewModelProviders.of(this).get(PageViewModel.class);
        int index = 1;
        if (getArguments() != null) {
            index = getArguments().getInt(ARG_SECTION_NUMBER);
            Log.e("MyTag","인덱스 번호  : " +index);
            String ln = SelectLesson.lesson;
            filepath = getArguments().getString(ln+lt+"lv"+index);
            filepatharr[index] = filepath;
            Log.e("MyTag","파일패스  : " +filepath);
//            switch (index){
//                case 1:
//                    filepath = getArguments().getString(ln+lt+"lv1");
//                    break;
//                case 2:
//                    filepath = getArguments().getString(ln+lt+"lv2");
//                    break;
//                case 3:
//                    filepath = getArguments().getString(ln+lt+"lv3");
//                    break;
//                case 4:
//                    filepath = getArguments().getString(ln+lt+"lv4");
//                    break;
//            }

        }
        pageViewModel.setIndex(index);

    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        pos = getArguments().getInt(ARG_SECTION_NUMBER);
        Log.e("MyTag","pos 번호  : " +pos);
//        View root = null;
        root = inflater.inflate(R.layout.fragment_level, container, false);
//        if(pos==1){root = inflater.inflate(R.layout.fragment_level, container, false);}
//        else if(pos==2){root = inflater.inflate(R.layout.fragment_level, container, false);}
//        else if(pos==3){root = inflater.inflate(R.layout.fragment_level, container, false);}
//        else if(pos==4){root = inflater.inflate(R.layout.fragment_level, container, false);}
//        else if(pos==5){root = inflater.inflate(R.layout.fragment_level, container, false);}
        surfaceView = root.findViewById(R.id.surfaceView);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);

        speedselect_server();

        mBtnPlayPause = (ImageButton) root.findViewById(R.id.videoplay_btn_play_pause);
        sendtest_btn = (ImageButton)root.findViewById(R.id.sendtest_btn);
        mBtnPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer.isPlaying()) {
                    Log.e("정지  ", "" + pause);
                    mediaPlayer.pause();
                    pause = true;
                    updateUI();
                    Log.e("정지2  ", "" + pause);
                }else {

                    play(f);
                    pause = false;
                    updateUI();
                    Log.e("재생  ", "" + pause);
                }
//                else {
//                    Log.e("파이어베이스 불러오기  ", "");
//                    mediaPlayer.start();
//                    updateUI();
//                    getaudiourl(filename + ext);
//                }
            }
        });
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

        final TextView textView = root.findViewById(R.id.section_label);
        textView.setText(level.level_text);
//        pageViewModel.getText().observe(this, new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });
        return root;



    }
    //플레이버튼 ui 업데이트
    private void updateUI() {
        if (mediaPlayer!=null) {
            if (mediaPlayer.isPlaying()) {

                mBtnPlayPause.setImageResource(R.drawable.pause);
            } else {
                mBtnPlayPause.setImageResource(R.drawable.play);
            }
        }
    }

    //스피너 선택버튼 만들기
    private void speedselect_server() {
        arrayList = new ArrayList<>();
        arrayList.add("재생속도");
        arrayList.add("0.5배속");
        arrayList.add("0.75배속");
        arrayList.add("1배속");
        arrayList.add("1.25배속");
        arrayList.add("1.5배속");

        arrayAdapter = new ArrayAdapter<>(root.getContext(),
                android.R.layout.simple_spinner_dropdown_item, arrayList);

        spinner = (Spinner) root.findViewById(R.id.videoplay_spinner);
        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override

            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {


                switch (arrayList.get(i)) {
                    case "0.5배속":
                        f = (float) 0.5;
                        play(f);
                        break;
                    case "0.75배속":
                        f = (float) 0.75;
                        play(f);
                        break;
                    case "1배속":
                        f = (float) 1;
                        play(f);
                        break;
                    case "1.25배속":
                        f = (float) 1.25;
                        play(f);
                        break;
                    case "1.5배속":
                        f = (float) 1.5;
                        play(f);
                        break;

                }
                //               Toast.makeText(getApplicationContext(),arrayList.get(i)+"가 선택되었습니다. f값은 " + f, Toast.LENGTH_SHORT).show();


            }

            @Override

            public void onNothingSelected(AdapterView<?> adapterView) {

            }

        });
    }
    //재생속도 변경
    public void play(float a) {


        if (isPrepared) {

            mediaPlayer.setPlaybackParams((mediaPlayer.getPlaybackParams().setSpeed(a)));
            mediaPlayer.start();

        }
    }

    //서피스뷰 동작
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.e("MyTag","surfaceCreated");

        if (mediaPlayer == null ) {
            Log.e("MyTag","서피스 크리에이트 미디어 플레이어 널상태  pos"+pos +mediaPlayer);
            mediaPlayer = new MediaPlayer();
        } else {
            try {
                Log.e("MyTag","서피스 크리에이트 미디어 플레이어 널이 아닐때  pos"+pos +mediaPlayer);
                mediaPlayer.reset();
            }catch (Exception e) {
                Log.e("MyTag","서피스 크리에이트 미디어 플레이어 오류 : pos"+pos + e.getMessage());
            }
        }

        try {


            mediaPlayer.setDataSource(filepath);

            //mediaPlayer.setVolume(0, 0); //볼륨 제거
            mediaPlayer.setDisplay(surfaceHolder); // 화면 호출
            mediaPlayer.prepare(); // 비디오 load 준비
            mediaPlayer.seekTo(100);   // 비디오 첫화면 설정 1초 장면
            isPrepared = true;
            Log.e("MyTag","크리에이트_이즈프리페어드  "+isPrepared);
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    if (mediaPlayer != null) {
                        isPrepared = false;


                        mediaPlayer.reset();

                        updateUI();
                        speedselect_server();
                    }

                }
            }); // 비디오 재생 완료 리스너

//            mediaPlayer.start();

        } catch (Exception e) {
            Log.e("MyTag","surface view error : " + e.getMessage());
        }

    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
//        isPrepared =false;

//        이거 왜 여기다가 위에꺼넣으면 미디어 속도조절이 안되나
        Log.e("MyTag","surfaceChanged");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        Log.e("MyTag","surfaceDestroyed   pos:  "+pos);
        if (mediaPlayer != null) {
            isPrepared = false;
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
            Log.e("MyTag","서피스 디스트로이_미디어상태  "+mediaPlayer);
        }mediaPlayer = null;
    }

    @Override
    public void onResume(){
        if (mediaPlayer == null ) {
            Log.e("MyTag","온 리섬 미디어 플레이어 널상태  pos"+pos +mediaPlayer);
            mediaPlayer = new MediaPlayer();
        } else {
            Log.e("MyTag","온 리섬 미디어 플레이어 널이 아닐때  pos"+pos +mediaPlayer);
//            try {
            mediaPlayer.reset();
//            }catch (Exception e) {
            Log.e("MyTag","온 리섬 미디어 플레이어 오류 : pos"+pos);
//                Log.e("MyTag","온 리섬 미디어 플레이어 오류 : pos"+pos  + e.getMessage());
//            }

        }

        try {


            mediaPlayer.setDataSource(filepath);

            //mediaPlayer.setVolume(0, 0); //볼륨 제거
            mediaPlayer.setDisplay(surfaceHolder); // 화면 호출
            mediaPlayer.prepare(); // 비디오 load 준비
            mediaPlayer.seekTo(100);   // 비디오 첫화면 설정 1초 장면
            isPrepared = true;
            Log.e("MyTag","크리에이트_이즈프리페어드  "+isPrepared);
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    if (mediaPlayer != null) {
                        isPrepared = false;


                        mediaPlayer.reset();

                        updateUI();
                        speedselect_server();
                    }

                }
            }); // 비디오 재생 완료 리스너

//            mediaPlayer.start();

        } catch (Exception e) {
            Log.e("MyTag","surface view error : " + e.getMessage());
        }
        super.onResume();
        Log.e("MyTag","파일패스  "+filepath);
        Log.e("MyTag","on Resume  "+mediaPlayer);
    }

    @Override
    public void onPause() {
        if (mediaPlayer !=null){
            mediaPlayer.stop();
            updateUI();
            mediaPlayer.release();
            mediaPlayer = null;
//            mediaPlayer.reset();
        }mediaPlayer = null;
        super.onPause();
//
        Log.e("MyTag","on Pause  "+mediaPlayer);


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
                    OutputStream os  = new FileOutputStream(filename);
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
                Toast.makeText(root.getContext(), "서버응답 시간이 초과되었습니다.(20초)", Toast.LENGTH_LONG).show();
            } else {

            }
        }catch(InterruptedException e){
//                    SendMessage("Interrupted", 4);
        }
    }
    private class CheckTypesTask extends AsyncTask<String, Integer, String> {

        ProgressDialog asyncDialog = new ProgressDialog(
                root.getContext());




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
            final Dialog sendtestdialog = new Dialog(root.getContext());
            sendtestdialog.setContentView(R.layout.sendtestdialog);
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
}
