package com.social.englishclass.ui.main;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.SoundPool;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
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

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.bumptech.glide.signature.StringSignature;
import com.google.gson.Gson;
import com.social.englishclass.AudioApplication;
import com.social.englishclass.R;
import com.social.englishclass.SelectLesson;
import com.social.englishclass.level;

import org.apache.commons.lang.StringEscapeUtils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
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
import java.util.UUID;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;


public class LetsreadFragment extends Fragment {

        private static final String ARG_SECTION_NUMBER = "section_number";
        public static final int REQUEST_AUDIO_PERMISSION_CODE = 1;
        private PageViewModel pageViewModel;
        MediaPlayer mediaPlayer;
        public static String[] filepath = new String[30];
    private String accessKey;
        private String filepath_sound, filepath_picture;
        private static String lt, ln, log_n;
        private Button videostart_btn;
        private Spinner spinner;
        private ImageButton mBtnPlayPause, sendtest_btn;
        ArrayList<String> arrayList;
        private ArrayAdapter<String> arrayAdapter;
        private float f=1;
        private static boolean pause=false;
        private View root = null;
        private static boolean isPrepared ;
        int maxLenSpeech = 16000 * 45;
        private  byte [] speechData = null;
        int lenSpeech = 0;
        boolean isRecording = false;
        public static boolean forceStop = false;
        String result;
        private int pos, index;
        private ImageView letsread_image;
        private Bitmap myBitmap = null;
        private  File imgFile;
    private SoundPool soundPool;
    int soundPlay1,soundPlay2,soundPlay3 ;
    private static int  privite_number;


        public static LetsreadFragment newInstance(int index) {
            ln = SelectLesson.lesson;
            lt = SelectLesson.lesson_type;

            log_n = level.login_number;
            if(!log_n.contains("teacher")) {
                String log_num = log_n.substring(3, 5);
                privite_number = Integer.parseInt(log_num);
            }else{
                privite_number = 10;
            }

            LetsreadFragment fragment = new LetsreadFragment();
            Bundle bundle = new Bundle();
            bundle.putInt(ARG_SECTION_NUMBER, index);
            if(lt.equals("let")) {
                bundle.putString(ln + lt + "sound" + index, "/storage/emulated/0/englishclass/lesson/letsread/l" + ln + "_let_lv1_sound/l" + ln + "_let_p" + index + ".mp3");
                bundle.putString(ln + lt + "picture" + index, "/storage/emulated/0/englishclass/lesson/letsread/l" + ln + "_let_lv1_picture/l" + ln + "_let_p" + index + ".png");
            }else{
                bundle.putString(ln + lt + "sound" + index, "/storage/emulated/0/englishclass/lesson/readandtalk/l" + ln + "_rt1_sound/l" + ln + "_rt_k" + index + ".mp3");
                bundle.putString(ln + lt + "picture" + index, "/storage/emulated/0/englishclass/lesson/letsread/l" + ln + "_rt1_picture/l" + ln + "_rt_k" + index + ".png");
            }
            fragment.setArguments(bundle);
            return fragment;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);


            pageViewModel = ViewModelProviders.of(this).get(PageViewModel.class);

            if (getArguments() != null) {
                index = getArguments().getInt(ARG_SECTION_NUMBER);
                Log.e("MyTag","인덱스 번호  : " +index);
                String ln = SelectLesson.lesson;
                filepath_sound = getArguments().getString(ln+lt+"sound"+index);

                filepath[index] = filepath_sound;
                filepath_picture = getArguments().getString(ln+lt+"picture"+index);


            }
            pageViewModel.setIndex(index);
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
            soundPlay1 = soundPool.load(getContext(), R.raw.wawawa, 1);
            soundPlay2 = soundPool.load(getContext(), R.raw.clapping2, 1);
            soundPlay3 = soundPool.load(getContext(), R.raw.clapping1, 1);
        }

        @Override
        public View onCreateView(
                @NonNull LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            pos = getArguments().getInt(ARG_SECTION_NUMBER);
            Log.e("MyTag","pos 번호  : " +pos);

            root = inflater.inflate(R.layout.fragment_letsread, container, false);


            speedselect_server();
            letsread_image = (ImageView) root.findViewById(R.id.letsread_image);
            setimage();
            mBtnPlayPause = (ImageButton) root.findViewById(R.id.audioplay_btn_play_pause);
            sendtest_btn = (ImageButton)root.findViewById(R.id.sendtest_btn);
            mBtnPlayPause.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mediaPlayer !=null && mediaPlayer.isPlaying()) {
                        Log.e("정지  ", "" + pause);
                        mediaPlayer.pause();
                        pause = true;
                        updateUI();
                        Log.e("정지2  ", "" + pause);
                    }else if(mediaPlayer !=null && pause == true) {

                        play(f);
                        pause = false;
                        updateUI();
                        Log.e("재생  ", "" + pause);
                    }
                else {
                        mediaplayerstart();
//                    Log.e("파이어베이스 불러오기  ", "");
//                    mediaPlayer.start();
                    updateUI();
//                    getaudiourl(filename + ext);
                }
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
//                        sendtest_btn.setImageResource(R.drawable.mic_rec);
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
                    }//else 끝

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

    private Bitmap makeBitmapSmall(Bitmap bitmap){
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 4;
            options.inBitmap = bitmap;
            return bitmap;
        }


//이미지 세팅
        private void setimage() {
            if(lt.equals("let")) {
               imgFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/englishclass/lesson/letsread/l" + ln + "_let_lv1_picture/l" + ln + "_let_p" + index + ".png");
            }else{
                imgFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/englishclass/lesson/readandtalk/l" + ln + "_rt1_picture/l" + ln + "_rt_k" + index + ".png");
            }

            myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            makeBitmapSmall(myBitmap);

            if (imgFile.exists()) {
                letsread_image.setImageBitmap(myBitmap);
            }

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



       public void mediaplayerstart(){

           if (mediaPlayer == null ) {
               Log.e("MyTag","렛츠리드 미디어 플레이어 널상태  pos"+pos +mediaPlayer);
               mediaPlayer = new MediaPlayer();
           } else {
               try {
                   Log.e("MyTag","렛츠리드  미디어 플레이어 널이 아닐때  pos"+pos +mediaPlayer);
                   mediaPlayer.reset();
               }catch (Exception e) {
                   Log.e("MyTag","렛츠리드  미디어 플레이어 오류 : pos"+pos + e.getMessage());
               }
           }

           try {

               mediaPlayer.setDataSource(filepath_sound);

               //mediaPlayer.setVolume(0, 0); //볼륨 제거

               mediaPlayer.prepare(); // 비디오 load 준비

               isPrepared = true;

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
                play(f);
//            mediaPlayer.start();

           } catch (Exception e) {
               Log.e("MyTag","surface view error : " + e.getMessage());
           }
       }



        @Override
        public void onResume(){


            super.onResume();

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
        @Override
        public  void onDestroy(){
            myBitmap.recycle();
            super.onDestroy();
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
//            String accessKey = "68c063de-3739-4796-ba10-5c6c3152d760";

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


            Log.e("서버키", ""+accessKey);
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
//                    Toast.makeText(root.getContext(), "서버응답 시간이 초과되었습니다.(20초)", Toast.LENGTH_LONG).show();
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
                    textResult.setText("인터넷 연결이 불안정합니다. 다시 시도해 보세요");}
                sendtestdialog.show();
                speechData = null;
            }
        }
    public void mic_rec_stop() {
        GlideDrawableImageViewTarget gifImage = new GlideDrawableImageViewTarget(sendtest_btn);
        Glide.clear(sendtest_btn);
        Glide.with(root.getContext()).load(R.drawable.mic_rec_stop).signature(new StringSignature(UUID.randomUUID().toString())).into(gifImage);
    }
    }


