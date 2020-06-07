package com.social.englishclass;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.text.SimpleDateFormat;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static com.social.englishclass.englishlesson.REQUEST_AUDIO_PERMISSION_CODE;

public class SelectLesson extends AppCompatActivity implements View.OnClickListener {

    private Intent intent;
    private Dialog lesson_dialog, level_dialog, listenandrepeat_dialog, letsread_dialog;
    public static String lesson, lesson_type;
    private Button startbtn, stopbtn, playbtn, stopplay, btn_server;
    public static final int REQUEST_AUDIO_PERMISSION_CODE = 1;
    boolean isRecording = false;
    private int pause;
    private File beforeFileName, afterFileName, beforesendtest, aftersendtest, exisitFileName;
    private Long duration;
    public static Dialog recordlistdialog, deletedialog;
    private String folder, fname, login_name, token, login_school;
    private AudioAdapter mAdapter, recordAdapter, serchAdapter;
    public String serchfilename, ext;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;
    private StorageTask mUploadTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_lesson);

        Intent login_intent = getIntent();
        login_school = login_intent.getStringExtra("login_school");
        login_name = login_intent.getStringExtra("login_name");
        token = login_intent.getStringExtra("token");

        //녹음버튼 관련
        startbtn = (Button) findViewById(R.id.btnRecord);
        stopbtn = (Button) findViewById(R.id.btnStop);
        playbtn = (Button) findViewById(R.id.btnPlay);
        stopplay = (Button) findViewById(R.id.StopPlay);
        btn_server = (Button) findViewById(R.id.btn_server);
        stopbtn.setEnabled(false);
        playbtn.setEnabled(true);
        stopplay.setEnabled(false);
        playbtn.setOnClickListener(this);
        stopbtn.setOnClickListener(this);
        startbtn.setOnClickListener(this);
        stopplay.setOnClickListener(this);
        btn_server.setOnClickListener(this);
//녹음버튼 끝
        mStorageRef = FirebaseStorage.getInstance().getReference("uploads");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("uploads");
        registerBroadcast();

        // OS가 Marshmallow 이상일 경우 권한체크를 해야 합니다.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1000);
            } else {
                // READ_EXTERNAL_STORAGE 에 대한 권한이 있음.
//                getAudioListFromMediaDatabase();
            }
        }
        // OS가 Marshmallow 이전일 경우 권한체크를 하지 않는다.
        else {
//            getAudioListFromMediaDatabase();
        }

        if (CheckPermissions()) {} else {

            RequestPermissions();
        }

        ImageButton btn1 = (ImageButton) findViewById(R.id.button1);
        ImageButton btn2 = (ImageButton) findViewById(R.id.button2);
        ImageButton btn3 = (ImageButton) findViewById(R.id.button3);
        ImageButton btn4 = (ImageButton) findViewById(R.id.button4);
        ImageButton btn5 = (ImageButton) findViewById(R.id.button5);
        ImageButton btn6 = (ImageButton) findViewById(R.id.button6);
        ImageButton btn7 = (ImageButton) findViewById(R.id.button7);
        ImageButton btn8 = (ImageButton) findViewById(R.id.button8);
        ImageButton btn9 = (ImageButton) findViewById(R.id.button9);
        ImageButton btn10 = (ImageButton) findViewById(R.id.button10);
        ImageButton btn11 = (ImageButton) findViewById(R.id.button11);
        ImageButton btn12 = (ImageButton) findViewById(R.id.button12);
//        intent = new Intent(this, englishlesson.class);

        View.OnClickListener Listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.button1:
                        lessonDialog("Lesson 1 - What Grade Are You In?");
                        lesson = "1";

//                        intent.putExtra("login_school", login_school);
//                        intent.putExtra("login_name", login_name);
//                        intent.putExtra("token", token);
//                        intent.putExtra("lesson", "/storage/emulated/0/englishclass/lesson1");
//                        startActivity(intent);
                        break;
                    case R.id.button2:
                        lessonDialog("Lesson 2 - Do You Know Anything About Hanok?");
                        lesson = "2";
                        break;
                    case R.id.button3:
                        lessonDialog("Lesson 3 - When Is Earth Day?");
                        lesson = "3";
                        break;
                    case R.id.button4:
                        lessonDialog("Lesson 4 - How Much Are There Pants?");
                        lesson = "4";

                        break;
                    case R.id.button5:
                        lessonDialog("Lesson 5 - What's Wrong?");
                        lesson = "5";
                        break;
                    case R.id.button6:
                        lessonDialog("Lesson 6 - I'm Going to Go on a Trip");
                        lesson = "6";
                        break;
                    case R.id.button7:
                        lessonDialog("Lesson 7 - You Should Wear a Helmet");
                        lesson = "7";
                        break;
                    case R.id.button8:
                        lessonDialog("Lesson 8 - How Can I Get to the Museum?");
                        lesson = "8";
                        break;
                    case R.id.button9:
                        lessonDialog("Lesson 9 - How Often Do You Exercise?");
                        lesson = "9";
                        break;
                    case R.id.button10:
                        lessonDialog("Lesson 10 - Emily Is Faster than Yuna");
                        lesson = "10";
                        break;
                    case R.id.button11:
                        lessonDialog("Lesson 11 - Why Are You Happy?");
                        lesson = "11";
                        break;
                    case R.id.button12:
                        lessonDialog("Lesson 12 - Would You Like to Come to My Graduation?");
                        lesson = "12";
                        break;
                }
            }
        };
        btn1.setOnClickListener(Listener);
        btn2.setOnClickListener(Listener);
        btn3.setOnClickListener(Listener);
        btn4.setOnClickListener(Listener);
        btn5.setOnClickListener(Listener);
        btn6.setOnClickListener(Listener);
        btn7.setOnClickListener(Listener);
        btn8.setOnClickListener(Listener);
        btn9.setOnClickListener(Listener);
        btn10.setOnClickListener(Listener);
        btn11.setOnClickListener(Listener);
        btn12.setOnClickListener(Listener);


    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // READ_EXTERNAL_STORAGE 에 대한 권한 획득.
//                getAudioListFromMediaDatabase();
        }
        switch (requestCode) {
            case REQUEST_AUDIO_PERMISSION_CODE:
                if (grantResults.length > 0) {
                    boolean permissionToRecord = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean permissionToStore = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (permissionToRecord && permissionToStore) {
                        Toast.makeText(getApplicationContext(), "Permission Granted", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Permission Denied", Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }

    //Let's Read 다이얼로그
    private  void letsreadDialog(String level_num){
        letsread_dialog = new Dialog(this);
        letsread_dialog.setContentView(R.layout.letsread_dialog);

        ImageButton level1_btn = (ImageButton) letsread_dialog.findViewById(R.id.let_level1_btn);
        ImageButton level2_btn = (ImageButton) letsread_dialog.findViewById(R.id.let_level2_btn);
        ImageButton level3_btn = (ImageButton) letsread_dialog.findViewById(R.id.let_level3_btn);


        TextView letsreaddialog_text = (TextView) letsread_dialog.findViewById(R.id.letsreaddialog_text);
        letsreaddialog_text.setText(level_num);

        View.OnClickListener letsreaddialog_listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(v.getContext(), level.class);
                switch (v.getId()) {
                    case R.id.let_level1_btn:

                        intent.putExtra("lv_num", "0");
                        intent.putExtra("login_school", login_school);
                        intent.putExtra("login_name", login_name);
                        intent.putExtra("token", token);
                        intent.putExtra("lesson", lesson);
                        intent.putExtra("lesson_type", lesson_type);
                        startActivity(intent);

                        letsread_dialog.dismiss();
                        break;
                    case R.id.let_level2_btn:
                        intent.putExtra("lv_num", "1");
                        intent.putExtra("login_school", login_school);
                        intent.putExtra("login_name", login_name);
                        intent.putExtra("token", token);
                        intent.putExtra("lesson", lesson);
                        intent.putExtra("lesson_type", lesson_type);
                        startActivity(intent);
                        letsread_dialog.dismiss();
                        break;
                    case R.id.let_level3_btn:
                        intent.putExtra("lv_num", "2");
                        intent.putExtra("login_school", login_school);
                        intent.putExtra("login_name", login_name);
                        intent.putExtra("token", token);
                        intent.putExtra("lesson", lesson);
                        intent.putExtra("lesson_type", lesson_type);
                        startActivity(intent);
                        letsread_dialog.dismiss();
                        break;


                }
            }

        };
        level1_btn.setOnClickListener(letsreaddialog_listener);
        level2_btn.setOnClickListener(letsreaddialog_listener);
        level3_btn.setOnClickListener(letsreaddialog_listener);



        letsread_dialog.show();
    }
//레벨 다이얼로그
    private  void levelDialog(String level_num){
        level_dialog = new Dialog(this);
        level_dialog.setContentView(R.layout.level_dialog);

        ImageButton level1_btn = (ImageButton) level_dialog.findViewById(R.id.level1_btn);
        ImageButton level2_btn = (ImageButton) level_dialog.findViewById(R.id.level2_btn);
        ImageButton level3_btn = (ImageButton) level_dialog.findViewById(R.id.level3_btn);
        ImageButton level4_btn = (ImageButton) level_dialog.findViewById(R.id.level4_btn);

        TextView leveldialog_text = (TextView) level_dialog.findViewById(R.id.leveldialog_text);
        leveldialog_text.setText(level_num);

        View.OnClickListener leveldialog_listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(v.getContext(), level.class);
                switch (v.getId()) {
                    case R.id.level1_btn:

                        intent.putExtra("lv_num", "0");
                        intent.putExtra("login_school", login_school);
                        intent.putExtra("login_name", login_name);
                        intent.putExtra("token", token);
                        intent.putExtra("lesson", lesson);
                        intent.putExtra("lesson_type", lesson_type);
                        intent.putExtra("level_text", level_num);
                        startActivity(intent);
//                        lesson_dialog.dismiss();
                        level_dialog.dismiss();
                        break;
                    case R.id.level2_btn:
                        intent.putExtra("lv_num", "1");
                        intent.putExtra("login_school", login_school);
                        intent.putExtra("login_name", login_name);
                        intent.putExtra("token", token);
                        intent.putExtra("lesson", lesson);
                        intent.putExtra("lesson_type", lesson_type);
                        intent.putExtra("level_text", level_num);
                        startActivity(intent);
//                        lesson_dialog.dismiss();
                        level_dialog.dismiss();
                        break;
                    case R.id.level3_btn:
                        intent.putExtra("lv_num", "2");
                        intent.putExtra("login_school", login_school);
                        intent.putExtra("login_name", login_name);
                        intent.putExtra("token", token);
                        intent.putExtra("lesson", lesson);
                        intent.putExtra("lesson_type", lesson_type);
                        intent.putExtra("level_text", level_num);
                        startActivity(intent);
//                        lesson_dialog.dismiss();
                        level_dialog.dismiss();
                        break;
                    case R.id.level4_btn:
                        intent.putExtra("lv_num", "3");
                        intent.putExtra("login_school", login_school);
                        intent.putExtra("login_name", login_name);
                        intent.putExtra("token", token);
                        intent.putExtra("lesson", lesson);
                        intent.putExtra("lesson_type", lesson_type);
                        intent.putExtra("level_text", level_num);
                        startActivity(intent);
//                        lesson_dialog.dismiss();
                        level_dialog.dismiss();
                        break;

                }
            }

        };
        level1_btn.setOnClickListener(leveldialog_listener);
        level2_btn.setOnClickListener(leveldialog_listener);
        level3_btn.setOnClickListener(leveldialog_listener);
        level4_btn.setOnClickListener(leveldialog_listener);


        level_dialog.show();
    }
//레슨 다이얼로그
    private void lessonDialog(String lesson_num) {
        lesson_dialog = new Dialog(this);
        lesson_dialog.setContentView(R.layout.lesson_dialog);

        ImageButton keyword_btn = (ImageButton) lesson_dialog.findViewById(R.id.keyword_btn);
        ImageButton lookandlisten_btn = (ImageButton) lesson_dialog.findViewById(R.id.lookandlisten_btn);
        ImageButton lookandsay_btn = (ImageButton) lesson_dialog.findViewById(R.id.lookandsay_btn);
        ImageButton listenandrepeat_btn = (ImageButton) lesson_dialog.findViewById(R.id.listenandrepeat_btn);
        ImageButton readandtalk_btn = (ImageButton) lesson_dialog.findViewById(R.id.readandtalk_btn);
        ImageButton letsread_btn = (ImageButton) lesson_dialog.findViewById(R.id.letsread_btn);
        TextView lessondialog_text = (TextView) lesson_dialog.findViewById(R.id.lessondialog_text);
        lessondialog_text.setText(lesson_num);

        View.OnClickListener lessondialog_listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.keyword_btn:
                        intent = new Intent(v.getContext(), Keyword.class);
                        intent.putExtra("lesson", lesson);
                        startActivity(intent);
                        lesson_dialog.dismiss();
                        break;
                    case R.id.lookandlisten_btn:
                        levelDialog("Lesson "+lesson+" - Look And Listen");
                        lesson_type = "ll";
                        break;
                    case R.id.lookandsay_btn:
                        levelDialog("Lesson "+lesson+" - Look And Say");
                        lesson_type = "ls";
                        break;
                    case R.id.listenandrepeat_btn:
                        listenandrepeatDialog();
                        break;
                    case R.id.readandtalk_btn:
                        levelDialog("Lesson "+lesson+" - Read AND Talk");
                        lesson_type = "rt";
                        break;
                    case R.id.letsread_btn:
                        letsreadDialog("Lesson "+lesson+" - Let's Read");
                        lesson_type = "let";
                        break;
                }
            }

        };
        keyword_btn.setOnClickListener(lessondialog_listener);
        lookandlisten_btn.setOnClickListener(lessondialog_listener);
        lookandsay_btn.setOnClickListener(lessondialog_listener);
        listenandrepeat_btn.setOnClickListener(lessondialog_listener);
        readandtalk_btn.setOnClickListener(lessondialog_listener);
        letsread_btn.setOnClickListener(lessondialog_listener);

        lesson_dialog.show();
    }//레슨다이얼로그 끝

    private void listenandrepeatDialog(){
        listenandrepeat_dialog = new Dialog(this);
        listenandrepeat_dialog.setContentView(R.layout.listenandrepeat_dialog);
        ImageButton listenandrepeat1_btn = (ImageButton) listenandrepeat_dialog.findViewById(R.id.listenandrepeat1_btn);
        ImageButton listenandrepeat2_btn = (ImageButton) listenandrepeat_dialog.findViewById(R.id.listenandrepeat2_btn);
        TextView listenandrepeat_text = (TextView) listenandrepeat_dialog.findViewById(R.id.listenandrepeat_text);
        listenandrepeat_text.setText("Lesson "+lesson+" Listen and Repeat");

        View.OnClickListener listenandrepeat_listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.listenandrepeat1_btn:
                        levelDialog("Lesson " + lesson + " - Listen And Repeat");
                        lesson_type = "lr1";
                        listenandrepeat_dialog.dismiss();
                        break;
                    case R.id.listenandrepeat2_btn:
                        levelDialog("Lesson " + lesson + " - Listen And Repeat");
                        lesson_type = "lr2";
                        listenandrepeat_dialog.dismiss();
                        break;
                }
            }
        };
        listenandrepeat1_btn.setOnClickListener(listenandrepeat_listener);
        listenandrepeat2_btn.setOnClickListener(listenandrepeat_listener);
        listenandrepeat_dialog.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnRecord:
                // 녹음 시작

//                Log.d( "녹음버튼클릭" , "조건문 이전" );
                if (CheckPermissions()) {
                    if (isRecording == false) {

                        stopbtn.setEnabled(true);
                        startbtn.setEnabled(true);
                        playbtn.setEnabled(true);
                        stopplay.setEnabled(false);
                        AudioApplication.getInstance().getServiceInterface().record3gp();
                        isRecording = true;
                        pause = 0;
                        startbtn.setText("일시정지");
                    } else if (isRecording == true && pause == 0) {
                        AudioApplication.getInstance().getServiceInterface().recordpause();

                        startbtn.setText("녹음시작");
                    } else if (isRecording == true && pause == 1) {
                        AudioApplication.getInstance().getServiceInterface().recordresume();
                        pause = 0;
                        startbtn.setText("일시정지");
                    }
                } else {

                    RequestPermissions();
                }
                break;
            case R.id.btnStop:
                // 녹음 중지
                stopbtn.setEnabled(false);
                startbtn.setEnabled(true);
                playbtn.setEnabled(true);
                stopplay.setEnabled(false);
                isRecording = false;
                startbtn.setText("녹음시작");
                AudioApplication.getInstance().getServiceInterface().recrecordstop();
                recordname();

                break;
            case R.id.btnPlay:
                // 녹음 재생
                stopbtn.setEnabled(false);
                startbtn.setEnabled(true);
//                playbtn.setEnabled(false);
                stopplay.setEnabled(true);

                recordlistdialog();
//                updateUI();
//                AudioApplication.getInstance().getServiceInterface().recordplay(fname);
                break;
            case R.id.StopPlay:
                // 녹음 재생중지
                stopbtn.setEnabled(false);
                startbtn.setEnabled(true);
                playbtn.setEnabled(true);
                stopplay.setEnabled(false);
                AudioApplication.getInstance().getServiceInterface().recordstopplay();
//                mAdapter.swapCursor(null);
                break;

            case R.id.btn_server:
 /*               // 파이어베이스에서 가져오기
                mStorageRef.child("1_2020년 04월 16일.3gp").getMetadata()
                        .addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
                            @Override
                            public void onSuccess(StorageMetadata storageMetadata) {
                                String name = String.valueOf(storageMetadata.getName());
                                Log.e("파이어베이스에서 불러온 이름  ", name);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e("조회 실패  ", "조회실패");
                            }
                        });

  */
// 녹음서버 목록 보여주는 엑티비티 띄우기
                Intent intent = new Intent(this, recordserver.class);
                intent.putExtra("login_school", login_school);
                intent.putExtra("login_name", login_name);
                startActivity(intent);
                finish();
                break;
        }
    }
    public boolean CheckPermissions() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), RECORD_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
    }

    private void RequestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{RECORD_AUDIO, WRITE_EXTERNAL_STORAGE}, REQUEST_AUDIO_PERMISSION_CODE);
    }
    //녹음중지 버튼 시 이름바꿔저장 매서드 실행
    public void recordname() {
        //다이얼로그생성
        final Dialog recordname = new Dialog(this);
        recordname.setContentView(R.layout.recordname);
        Button okbtn = (Button) recordname.findViewById(R.id.ok);
        Button canclebtn = (Button) recordname.findViewById(R.id.cancle);
        final EditText edit = (EditText) recordname.findViewById(R.id.edittext);
        //확인버튼
        okbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String FileName = edit.getText().toString();
//파일명에 날짜시간 넣기
                SimpleDateFormat format = new SimpleDateFormat("yyyy년 MM월 dd일");
                String time = format.format(System.currentTimeMillis());
                beforeFileName = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/englishclass/record", "AudioRecording.3gp");
//                beforesendtest = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/englishclass/record", "sendtest.txt");
                Log.d("이전파일이름", String.valueOf(beforeFileName));
                afterFileName = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/englishclass/record", FileName + "_" + time + ".3gp");
//                exisitFileName = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/englishclass/record", FileName +"_"+time+".pcm");
//                aftersendtest = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/englishclass/record", FileName +"_"+time+".txt");
                Log.d("수정된파일이름", String.valueOf(afterFileName));

                if (afterFileName.exists()) {
//                    Log.e("저장되어 있는 파일이름      ", String.valueOf(afterFileName));
//                    afterFileName.mkdirs();
//                    afterFileName.delete();
                    metadata(String.valueOf(beforeFileName));
//                    beforeFileName.renameTo(exisitFileName);
                    beforeFileName.renameTo(afterFileName);
                    Log.e("재생시간", String.valueOf(duration));
//                    try {
//                        rawToWave(exisitFileName, afterFileName);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }

//                    wavtomp3(afterFileName);
//                    beforeFileName.renameTo(afterFileName);
//                    beforesendtest.renameTo(aftersendtest);
                    updatadata(FileName + "_" + time);
//                    Log.e("삭제된 파일이름      ", String.valueOf(afterFileName));
                } else {

                    Log.e("재생시간", String.valueOf(duration));
//pcm to wav
//                    try {
//                        rawToWave(beforeFileName, afterFileName);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                    wavtomp3(new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/englishclass/record", "1.wav"));
//wav to mp3
//                    wavtomp3(afterFileName);
                    beforeFileName.renameTo(afterFileName);
//                    beforesendtest.renameTo(aftersendtest);
//                    fname = String.valueOf(afterFileName);
                    metadata(String.valueOf(afterFileName));


//                metadata(fname);
                    ContentValues values = new ContentValues();
//                    String mp3ext = afterFileName.getName().toString().substring(0,afterFileName.getName().toString().lastIndexOf("."))+".mp3";
//                    String mp3extpath = afterFileName.getPath().toString().substring(0,afterFileName.getPath().toString().lastIndexOf("."))+".mp3";
//                    File mp3 = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/englishclass/record", mp3ext);
//                    Log.e("mp3 확장자 얻기 위한 이름   ", ""+mp3ext);
//                    values.put(MediaStore.Audio.Media.DISPLAY_NAME, mp3.getName());
                    String recext = afterFileName.getName().toString().substring(0, afterFileName.getName().toString().lastIndexOf(".")) + ".3gp";
                    String recextpath = afterFileName.getPath().toString().substring(0, afterFileName.getPath().toString().lastIndexOf(".")) + ".3gp";
                    File rec = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/englishclass/record", recext);
                    Log.e("mp3 확장자 얻기 위한 이름   ", "" + recext);
                    values.put(MediaStore.Audio.Media.DISPLAY_NAME, rec.getName());


                    values.put(MediaStore.Audio.Media.TITLE, FileName + "_" + time);

                    values.put(MediaStore.Audio.Media.DURATION, duration);
                    Log.e("녹음 중지 시 저장되는 이름   ", afterFileName.getName());
                    values.put(MediaStore.Audio.Media.DATA, rec.getPath());
                    Log.e("녹음 중지 시 저장되는 경로   ", afterFileName.getPath());

                    values.put(MediaStore.Audio.Media.MIME_TYPE, "audio/*");


//                Uri uri = MediaStore.Audio.Media.getContentUriForPath(afterFileName.getPath());
                    getContentResolver().insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values);
//                getApplicationContext().getContentResolver().notifyChange(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null);
//                    getContentResolver().update(Settings.System.CONTENT_URI, values, null, null);
                }


                if (beforeFileName.renameTo(afterFileName)) {
                    Toast.makeText(getApplicationContext(), "success!" + FileName + beforeFileName, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "faile" + FileName + beforeFileName, Toast.LENGTH_SHORT).show();
                }


//                afterFileName.delete();
                recordname.dismiss();
            }
        }); //ok버튼 끝

        //취소버튼
        canclebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recordname.dismiss();
            }
        }); //취소버튼 끝
        recordname.show();
    }

    public void metadata(String filePath) {
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(filePath);

        duration = Long.valueOf(mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
    }

    public void updatadata(String FileName) {
        ContentValues values = new ContentValues();
        String mselection = MediaStore.Audio.Media.TITLE + " LIKE ?";
//        String[] mselectionargs = {"%"+FileName+"%"};
        String[] mselectionargs = {FileName};

        values.put(MediaStore.Audio.Media.DURATION, duration);

        getContentResolver().update(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values, mselection, mselectionargs);

    }

    //녹음재생 버튼 시 녹음파일 목록 다이얼로그 띄우기
    public void recordlistdialog() {

        //다이얼로그생성
        recordlistdialog = new Dialog(this);
        recordlistdialog.setContentView(R.layout.recordlist);
        folder = "/storage/emulated/0/englishclass/record";
        EditText serchname = (EditText) recordlistdialog.findViewById(R.id.serchtext);

        Button serchbtn = (Button) recordlistdialog.findViewById(R.id.serchbtn);

        getAudioListFromMediaDatabase2();

        RecyclerView recordRecyclerView = (RecyclerView) recordlistdialog.findViewById(R.id.recordrecyclerview);
        recordAdapter = new AudioAdapter(this, null);
//        mAdapter = new AudioAdapter(this, null);   //어댑터를 새로지정하면 못읽는다. null 값이라 그런가?

        recordRecyclerView.setAdapter(recordAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recordRecyclerView.setLayoutManager(layoutManager);

        Log.e("다이얼로그 쇼 전에 커서데이터", "커서데이터");
        Log.e("검색버튼 클릭 시 검색어   ", "검색어 " + serchfilename);
        serchbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText serchname = (EditText) recordlistdialog.findViewById(R.id.serchtext);
                serchfilename = serchname.getText().toString();
                getSerchAudioListFromMediaDatabase();
                RecyclerView serchRecyclerView = (RecyclerView) recordlistdialog.findViewById(R.id.recordrecyclerview);
                serchAdapter = new AudioAdapter(getApplicationContext(), null);
                serchRecyclerView.setAdapter(serchAdapter);
                Log.e("검색버튼 클릭 시 검색어   ", "버튼 안 로그" + serchfilename);
//                recordlistdialog.dismiss();

                //               serchlistdialog();
            }
        });

        Button recordplaycanclebtn = (Button) recordlistdialog.findViewById(R.id.cancle);
        recordplaycanclebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recordlistdialog.dismiss();
            }
        }); //취소버튼 끝

        recordlistdialog.show();
    }

    // 녹음파일 리스트 어답터
    public void getAudioListFromMediaDatabase2() {
        long currentTime = System.currentTimeMillis();
        int lid = (int) currentTime;
        Log.e("getAudioList2   로더 아이디", "로더아이디" + currentTime);
//        getSupportLoaderManager().restartLoader(0, null, this);
        getSupportLoaderManager().restartLoader(lid, null, new LoaderManager.LoaderCallbacks<Cursor>() {
            @Override
            public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                Log.e("겟리스트메서드", "메서드실행됨");
                Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
//               String folder = "/storage/emulated/0/Music";
                String[] projection = new String[]{
                        MediaStore.Audio.Media._ID,
                        MediaStore.Audio.Media.TITLE,
                        MediaStore.Audio.Media.ARTIST,
                        MediaStore.Audio.Media.ALBUM,
                        MediaStore.Audio.Media.ALBUM_ID,
                        MediaStore.Audio.Media.DURATION,
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
                Log.e("겟리스트", "폴더" + selectionArgs);
                String sortOrder = MediaStore.Audio.Media.TITLE + " COLLATE LOCALIZED ASC";
                //               return new CursorLoader(getApplicationContext(), uri, projection, selection, null, sortOrder);
//검색 쿼리가 들어있는 내장파일 커서로더.java 를 호출한다.
                return new CursorLoader(getApplicationContext(), uri, projection, selection, selectionArgs, sortOrder);
            }

            @Override
            public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
                recordAdapter.swapCursor(data);
                Log.e("커서데이터", "커서데이터" + data);
                if (data != null && data.getCount() > 0) {
                    while (data.moveToNext()) {
                        Log.e("태그", "Title:" + data.getString(data.getColumnIndex(MediaStore.Audio.Media.TITLE)));
                    }
                }
            }

            @Override
            public void onLoaderReset(Loader<Cursor> loader) {
                recordAdapter.swapCursor(null);
            }
        });
    }

    // 녹음파일 검색 어답터
    public void getSerchAudioListFromMediaDatabase() {
        long currentTime = System.currentTimeMillis();
        int lid = (int) currentTime;
        getSupportLoaderManager().restartLoader(lid, null, new LoaderManager.LoaderCallbacks<Cursor>() {
            @Override
            public Loader<Cursor> onCreateLoader(int id, Bundle args) {

                Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

                String[] projection = new String[]{
                        MediaStore.Audio.Media._ID,
                        MediaStore.Audio.Media.TITLE,
                        MediaStore.Audio.Media.ARTIST,
                        MediaStore.Audio.Media.ALBUM,
                        MediaStore.Audio.Media.ALBUM_ID,
                        MediaStore.Audio.Media.DURATION,
                        MediaStore.Audio.Media.DATA
                };
//쿼리를 위한 조건을 담는 부분 ? 한개당 1개의 아규먼트가 적용된다.
//해당폴더는 검색하고 하위폴더는 제외하는 내용
                String selection = MediaStore.Audio.Media.DATA + " LIKE ? AND " + MediaStore.Audio.Media.TITLE + " LIKE ? ";
// 원래는 미디어 ismusic 값이 1인 것(음악파일)은 모두 검색하는 조건이 들어갔었다
//                String selection = MediaStore.Audio.Media.IS_MUSIC + " = 1";
                String[] selectionArgs = new String[]{
                        "%" + folder + "%",
                        "%" + serchfilename + "%"
                };

                String sortOrder = MediaStore.Audio.Media.TITLE + " COLLATE LOCALIZED ASC";

//검색 쿼리가 들어있는 내장파일 커서로더.java 를 호출한다.
                return new CursorLoader(getApplicationContext(), uri, projection, selection, selectionArgs, sortOrder);
            }

            @Override
            public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
                serchAdapter.swapCursor(data);

                if (data != null && data.getCount() > 0) {
                    while (data.moveToNext()) {
                        Log.e("태그", "Title:" + data.getString(data.getColumnIndex(MediaStore.Audio.Media.TITLE)));
                    }
                }
            }

            @Override
            public void onLoaderReset(Loader<Cursor> loader) {
                serchAdapter.swapCursor(null);
            }
        });
    }

    //녹음파일 검색 시 녹음파일 목록 다이얼로그 띄우기
    public void deletedialog(final String filenamevalue, final Uri filepathvalue) {

        //다이얼로그생성
        deletedialog = new Dialog(this);
        deletedialog.setContentView(R.layout.delete);
        TextView deletedialogtitle = (TextView) deletedialog.findViewById(R.id.deleltedialogtitle);
        deletedialogtitle.setText("선택된 파일  : " + filenamevalue);
        ext = filepathvalue.toString().substring(filepathvalue.toString().lastIndexOf("."));
        Log.e("롱클릭시 넘겨진자   ", "" + ext);
//        folder = "/storage/emulated/0/englishclass/record";
        Button btn_send_firebase = (Button) deletedialog.findViewById(R.id.btn_send_firebase);
        Button btn_send_test = (Button) deletedialog.findViewById(R.id.btn_send_test);
        btn_send_test.setVisibility(View.GONE);
        Button deletebtn = (Button) deletedialog.findViewById(R.id.deletebtn);
        Button deletecanclebtn = (Button) deletedialog.findViewById(R.id.deletecanclebtn);
        Log.e("지워질 파일이름   ", filenamevalue);
//파이어베이스 업로드
        btn_send_firebase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File deletefile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/englishclass/record", filenamevalue + ext);
                Uri fileuri = Uri.fromFile(deletefile);
                Log.e("파일패스에서 얻어지는 uri   ", "" + fileuri);
                uploadfile(filenamevalue, fileuri);
            }
        }); //파이어베이스 업로드 끝
/*
//영어발음평가 전송
        btn_send_test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                deletedialog.dismiss();



                String  sendtestfile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/englishclass/record/"+ filenamevalue+".txt";
                Log.e("영어평가전송버튼 누를 때 선택된 파일   ", ""+ sendtestfile);
                sendtest(sendtestfile);
                englishlesson.CheckTypesTask task = new englishlesson.CheckTypesTask();
                task.execute();
//                sendtestThread();


            }
        });  //영어발음평가 전송 끝
*/
//녹음파일 삭제
        deletebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                삭제시 지우기
                File deletefile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/englishclass/record", filenamevalue + ext);
                Uri fileuri = Uri.fromFile(deletefile);
                Log.e("파일패스에서 얻어지는 uri   ", "" + fileuri);
                if (deletefile.delete()) {
                    deletedata(filenamevalue);
                    Toast.makeText(getApplicationContext(), "녹음파일  " + filenamevalue + ext + " 가 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                    deletedialog.dismiss();
                }
            }
        }); //삭제버튼 끝
//취소버튼
        deletecanclebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deletedialog.dismiss();
            }
        }); //취소버튼 끝


        deletedialog.show();
    }

    //파이어베이스 업로드
    public void uploadfile(final String FileName, Uri filepathvalue) {
        if (filepathvalue != null) {
            Log.e("업로드시 얻어지는 파일 uri   ", "" + filepathvalue);

            String ext = filepathvalue.toString().substring(filepathvalue.toString().lastIndexOf("."));
            Log.e("업로드시 얻어지는 파일 확장자   ", "" + ext);
//업로드 진행 Dialog 보이기
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("업로드중...");
            progressDialog.show();

//스토리지 지정
            StorageReference filereference = mStorageRef.child(FileName + ext);
            mUploadTask = filereference.putFile(filepathvalue)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            Upload upload = new Upload(FileName, taskSnapshot.getUploadSessionUri().toString(), login_name);
                            String uploadId = mDatabaseRef.push().getKey();
                            mDatabaseRef.child(FileName).setValue(upload);
                            progressDialog.dismiss();
                            deletedialog.dismiss();
                            Toast.makeText(getApplicationContext(), "업로드 완료!", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "업로드 실패!", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            @SuppressWarnings("VisibleForTests")
                            double progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                            //dialog에 진행률을 퍼센트로 출력해 준다
                            progressDialog.setMessage("Uploaded " + ((int) progress) + "% ...");
                        }
                    });
        } else {
            Toast.makeText(getApplicationContext(), "파일을 먼저 선택하세요.", Toast.LENGTH_SHORT).show();
        }

    }

    public void deletedata(String FileName) {
        ContentValues values = new ContentValues();
        String mselection = MediaStore.Audio.Media.TITLE + " LIKE ?";
//        String[] mselectionargs = {"%"+FileName+"%"};
        String[] mselectionargs = {FileName};
        getContentResolver().delete(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, mselection, mselectionargs);
    }

    public void registerBroadcast() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(BroadcastActions.PLAY_STATE_CHANGED);
        filter.addAction(BroadcastActions.DELETE_DIALOG);
        registerReceiver(mBroadcastReceiver, filter);
    }

    public void unregisterBroadcast() {
        unregisterReceiver(mBroadcastReceiver);
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("deletedialog")) {
                String filenamevalue = intent.getStringExtra("filenamevalue");

                Uri filepathvalue = Uri.parse("file:/" + intent.getStringExtra("filepathvalue"));
                Log.e("다이얼로그 출력시 Uri 정보", " " + filepathvalue);
                deletedialog(filenamevalue, filepathvalue);
            }
//            updateUI();
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterBroadcast();
        AudioApplication.getInstance().getServiceInterface().stop();
        AudioApplication.getInstance().getServiceInterface().clearPlayList();
        finish();

    }
    }

