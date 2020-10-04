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
import android.graphics.Color;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.bumptech.glide.signature.StringSignature;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static com.social.englishclass.englishlesson.REQUEST_AUDIO_PERMISSION_CODE;

public class SelectLesson extends AppCompatActivity implements View.OnClickListener {

    private Intent intent;
    private Dialog lesson_dialog, level_dialog, listenandrepeat_dialog, letsread_dialog, readandtalk_dialog;
    public static String lesson, lesson_type, line;
    private ImageButton playbtn, stopplay, stopbtn, startbtn, btn_server, dictionary_btn, extrawork_btn;
    public static final int REQUEST_AUDIO_PERMISSION_CODE = 1;
    boolean isRecording = false;
    private int pause;
    private File beforeFileName, afterFileName, beforesendtest, aftersendtest, exisitFileName, changeFileName;
    private Long duration;
    public static Dialog recordlistdialog, deletedialog;
    private String folder, fname, login_name, token, login_school, login_number;
    private AudioAdapter mAdapter, recordAdapter, serchAdapter;
    public String serchfilename, ext;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;
    private StorageTask mUploadTask;
    private static final int MULTIPLE_PERMISSIONS = 101;
    private TextView recordname_sub_title;


    //멀티 퍼미션 지정
    private String[] permissions = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO, // 녹음
            Manifest.permission.WRITE_EXTERNAL_STORAGE, // 기기, 사진, 미디어, 파일 엑세스 권한

    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_lesson);

        Intent login_intent = getIntent();
        login_school = login_intent.getStringExtra("login_school");
        login_name = login_intent.getStringExtra("login_name");
        Log.e("인텐트 로그인네임 값", "" + login_name);
        login_number = login_intent.getStringExtra("login_number");
        token = login_intent.getStringExtra("token");
        line = login_intent.getStringExtra("line");
        Log.e("인텐트 라인 값", "" + line);

//성장그래프 버튼
        ImageButton teacher_btn = (ImageButton) findViewById(R.id.teacher_btn);
        GlideDrawableImageViewTarget gifImage1 = new GlideDrawableImageViewTarget(teacher_btn);
        Glide.clear(teacher_btn);
        Glide.with(this).load(R.drawable.charticon).signature(new StringSignature(UUID.randomUUID().toString())).into(gifImage1);

//        if (login_name.contains("teacher") || login_name.contains("60830")) {
//        } else {
//            teacher_btn.setVisibility(View.INVISIBLE);
//        }
        TextView login_info = (TextView) findViewById(R.id.login_info);
        if (line.equals("1")) {
            login_info.setText(login_name + " (On Line)");
            login_info.setTextColor(Color.parseColor("green"));
        } else {
            login_info.setText(login_name + " (Off Line)");
            login_info.setTextColor(Color.parseColor("red"));
        }
        //녹음버튼 관련
        startbtn = (ImageButton) findViewById(R.id.btnRecord);
        stopbtn = (ImageButton) findViewById(R.id.btnStop);
        playbtn = (ImageButton) findViewById(R.id.btnPlay);
        stopplay = (ImageButton) findViewById(R.id.StopPlay);
        btn_server = (ImageButton) findViewById(R.id.btn_server);
        dictionary_btn = (ImageButton) findViewById(R.id.dictionary_btn);
        extrawork_btn = (ImageButton) findViewById(R.id.extrawork_btn);
        stopbtn.setEnabled(false);
        playbtn.setEnabled(true);
        stopplay.setEnabled(false);
        playbtn.setOnClickListener(this);
        stopbtn.setOnClickListener(this);
        startbtn.setOnClickListener(this);
        stopplay.setOnClickListener(this);
        btn_server.setOnClickListener(this);
        dictionary_btn.setOnClickListener(this);
        extrawork_btn.setOnClickListener(this);
//녹음버튼 끝
        mStorageRef = FirebaseStorage.getInstance().getReference("uploads");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("uploads");
        registerBroadcast();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkPermissions();
        }
/*
        // OS가 Marshmallow 이상일 경우 권한체크를 해야 합니다.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1000);
            } else {
                // READ_EXTERNAL_STORAGE 에 대한 권한이 있음.
//                getAudioListFromMediaDatabase();
                if (CheckPermissions()) {} else {

                    RequestPermissions();
                }
            }
        }
        // OS가 Marshmallow 이전일 경우 권한체크를 하지 않는다.
        else {
//            getAudioListFromMediaDatabase();
        }
*/


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
                    case R.id.teacher_btn:
                        if(login_name.contains("teacher") || login_name.contains("박준원") ) {
                            intent = new Intent(SelectLesson.this, TeacherPage.class);
                        }else{
                            intent = new Intent(SelectLesson.this, Chart.class);
                            intent.putExtra("login_id", login_name);
                        }
                        startActivity(intent);
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
        teacher_btn.setOnClickListener(Listener);

    }

    private boolean checkPermissions() {
        int result;
        List<String> permissionList = new ArrayList<>();
        for (String pm : permissions) {
            result = ContextCompat.checkSelfPermission(this, pm);
            if (result != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(pm);
            }
        }
        if (!permissionList.isEmpty()) {
            ActivityCompat.requestPermissions(this, permissionList.toArray(new String[permissionList.size()]), MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MULTIPLE_PERMISSIONS: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++) {
                        if (permissions[i].equals(this.permissions[i])) {
                            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                                showToast_PermissionDeny();
                            }
                        }
                    }
                } else {
                    showToast_PermissionDeny();
                }
                return;
            }
        }

    }

    private void showToast_PermissionDeny() {
        Toast.makeText(this, "권한 요청에 동의 해주셔야 이용 가능합니다. 설정에서 권한 허용 하시기 바랍니다.", Toast.LENGTH_SHORT).show();
        finish();
    }

    /*
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
 */
//Read and Talk 다이얼로그
    private void readandtalkDialog() {
        readandtalk_dialog = new Dialog(this);
        readandtalk_dialog.setContentView(R.layout.readandtalk_dialog);
        ImageButton readandtalk1_btn = (ImageButton) readandtalk_dialog.findViewById(R.id.readandtalk1_btn);
        ImageButton readandtalk2_btn = (ImageButton) readandtalk_dialog.findViewById(R.id.readandtalk2_btn);
        ImageButton readandtalk3_btn = (ImageButton) readandtalk_dialog.findViewById(R.id.readandtalk3_btn);
        TextView letsreaddialog_text = (TextView) readandtalk_dialog.findViewById(R.id.letsreaddialog_text);
        letsreaddialog_text.setText("Lesson " + lesson + " Read and Talk");

        View.OnClickListener listenandrepeat_listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.readandtalk1_btn:
                        //액티비티 이동
                        intent = new Intent(v.getContext(), level.class);
                        lesson_type = "rt1";
                        intent.putExtra("lv_num", "0");
                        intent.putExtra("login_school", login_school);
                        intent.putExtra("login_name", login_name);
                        intent.putExtra("token", token);
                        intent.putExtra("lesson", lesson);
                        intent.putExtra("lesson_type", lesson_type);
                        intent.putExtra("login_number", login_number);
                        intent.putExtra("line", line);
                        startActivity(intent);
                        readandtalk_dialog.dismiss();
                        break;
                    case R.id.readandtalk2_btn:
                        //엑티비티 이동
                        intent = new Intent(v.getContext(), Dialogflow.class);
                        intent.putExtra("lv_num", "0");
                        intent.putExtra("login_school", login_school);
                        intent.putExtra("login_name", login_name);
                        intent.putExtra("token", token);
                        intent.putExtra("lesson", lesson);
                        lesson_type = "rt2";
                        intent.putExtra("lesson_type", lesson_type);
                        intent.putExtra("line", line);
                        startActivity(intent);
                        readandtalk_dialog.dismiss();
                        break;

                    case R.id.readandtalk3_btn:
                        //엑티비티 이동
                        intent = new Intent(v.getContext(), SpeakingTest.class);
                        intent.putExtra("login_school", login_school);
                        intent.putExtra("login_name", login_name);
                        intent.putExtra("lesson", lesson);
                        lesson_type = "rt3";
                        intent.putExtra("lesson_type", lesson_type);
                        intent.putExtra("line", line);
                        startActivity(intent);
                        readandtalk_dialog.dismiss();
                        break;
                }
            }
        };
        readandtalk1_btn.setOnClickListener(listenandrepeat_listener);
        readandtalk2_btn.setOnClickListener(listenandrepeat_listener);
        readandtalk3_btn.setOnClickListener(listenandrepeat_listener);
        readandtalk_dialog.show();
    }

    //Let's Read 다이얼로그
    private void letsreadDialog(String level_num) {
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
                        intent.putExtra("login_number", login_number);
                        intent.putExtra("line", line);
                        startActivity(intent);

                        letsread_dialog.dismiss();
                        break;
                    case R.id.let_level2_btn:
                        intent.putExtra("lv_num", "0");
                        intent.putExtra("login_school", login_school);
                        intent.putExtra("login_name", login_name);
                        intent.putExtra("token", token);
                        intent.putExtra("lesson", lesson);
                        intent.putExtra("lesson_type", "let_lv2");
                        intent.putExtra("login_number", login_number);
                        intent.putExtra("line", line);
                        startActivity(intent);
                        letsread_dialog.dismiss();
                        break;
                    case R.id.let_level3_btn:
                        intent.putExtra("lv_num", "1");
                        intent.putExtra("login_school", login_school);
                        intent.putExtra("login_name", login_name);
                        intent.putExtra("token", token);
                        intent.putExtra("lesson", lesson);
                        intent.putExtra("lesson_type", "let_lv2");
                        intent.putExtra("login_number", login_number);
                        intent.putExtra("line", line);
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
    private void levelDialog(String level_num) {
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
                        intent.putExtra("login_number", login_number);
                        intent.putExtra("line", line);
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
                        intent.putExtra("login_number", login_number);
                        intent.putExtra("line", line);
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
                        intent.putExtra("login_number", login_number);
                        intent.putExtra("line", line);
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
                        intent.putExtra("login_number", login_number);
                        intent.putExtra("line", line);
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
                        intent.putExtra("login_number", login_number);
                        startActivity(intent);
                        lesson_dialog.dismiss();
                        break;
                    case R.id.lookandlisten_btn:
                        levelDialog("Lesson " + lesson + " - Look And Listen");
                        lesson_type = "ll";
                        break;
                    case R.id.lookandsay_btn:
                        levelDialog("Lesson " + lesson + " - Look And Say");
                        lesson_type = "ls";
                        break;
                    case R.id.listenandrepeat_btn:
                        listenandrepeatDialog();
                        break;
                    case R.id.readandtalk_btn:
                        readandtalkDialog();
                        break;
                    case R.id.letsread_btn:
                        letsreadDialog("Lesson " + lesson + " - Let's Read");
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

    private void listenandrepeatDialog() {
        listenandrepeat_dialog = new Dialog(this);
        listenandrepeat_dialog.setContentView(R.layout.listenandrepeat_dialog);
        ImageButton listenandrepeat1_btn = (ImageButton) listenandrepeat_dialog.findViewById(R.id.listenandrepeat1_btn);
        ImageButton listenandrepeat2_btn = (ImageButton) listenandrepeat_dialog.findViewById(R.id.listenandrepeat2_btn);
        TextView listenandrepeat_text = (TextView) listenandrepeat_dialog.findViewById(R.id.listenandrepeat_text);
        listenandrepeat_text.setText("Lesson " + lesson + " Listen and Repeat");

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
                        recording_btn();
//                        startbtn.setText("일시정지");
                    } else if (isRecording == true && pause == 0) {
                        AudioApplication.getInstance().getServiceInterface().recordpause();
                        pause = 1;
                        startbtn.setImageResource(R.drawable.record_btn);
//                        startbtn.setText("녹음시작");
                    } else if (isRecording == true && pause == 1) {
                        AudioApplication.getInstance().getServiceInterface().recordresume();
                        pause = 0;
                        recording_btn();
//                        startbtn.setText("일시정지");
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
                startbtn.setImageResource(R.drawable.record_btn);
//                startbtn.setText("녹음시작");
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
 /*
                ProgressDialog progressDialog = new ProgressDialog(SelectLesson.this);
                progressDialog.setMessage("서버에서 녹음파일 목록을 불러오는 중입니다...\n잠시만 기다려주세요.");
                progressDialog.show();

                Thread thread = new Thread(new Runnable() {

                    @Override
                    public void run() {
//                         3초가 지나면 다이얼로그 닫기
                        TimerTask task = new TimerTask(){
                            @Override
                            public void run() {
                                progressDialog.dismiss();

                            }
                        };

                        Timer timer = new Timer();
                        timer.schedule(task, 2000);
                    }
                });
                thread.start();
*/

                if (line.equals("1")) {
// 녹음서버 목록 보여주는 엑티비티 띄우기
                    Intent intent = new Intent(this, recordserver.class);
                    intent.putExtra("login_school", login_school);
                    intent.putExtra("login_name", login_name);
                    startActivity(intent);
                }else {
                    Toast.makeText(getApplicationContext(), "인터넷에 로그인 되어있지 않습니다.\n다시 로그인 하여 주세요", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.dictionary_btn:

                Intent intent_dic = new Intent(Intent.ACTION_VIEW, Uri.parse("https://en.dict.naver.com/#/main"));
                startActivity(intent_dic);
                break;
            case R.id.extrawork_btn:
                Intent intent_extrawork = new Intent(Intent.ACTION_VIEW, Uri.parse("https://sites.google.com/view/textbookshadowinglab/0420-4%EA%B5%90%EC%8B%9C-%EC%98%81%EC%96%B4"));
                startActivity(intent_extrawork);
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
        recordname.setCancelable(false);
        Button okbtn = (Button) recordname.findViewById(R.id.ok);
        Button canclebtn = (Button) recordname.findViewById(R.id.cancle);
        final EditText edit = (EditText) recordname.findViewById(R.id.edittext);
        //확인버튼
        okbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String FileName = login_name + "_" + edit.getText().toString();
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


//                if (beforeFileName.renameTo(afterFileName)) {
//                    Toast.makeText(getApplicationContext(), "success!" + FileName + beforeFileName, Toast.LENGTH_SHORT).show();
//                } else {
//                    Toast.makeText(getApplicationContext(), "faile" + FileName + beforeFileName, Toast.LENGTH_SHORT).show();
//                }


//                afterFileName.delete();
                recordname.dismiss();

//이름변경 확인버튼시 서브다이얼로그 시작
                recordname_sub(afterFileName);

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

    public void recordname_sub(File file) {
        //이름변경 확인버튼시 서브다이얼로그 시작
        final Dialog recordname_sub = new Dialog(this);
        recordname_sub.setContentView(R.layout.recordname_sub);
        recordname_sub.setCancelable(false);

        recordname_sub_title = (TextView) recordname_sub.findViewById(R.id.recordname_sub_title);
        Button recordname_sub_play = (Button) recordname_sub.findViewById(R.id.recordname_sub_play);
        Button recordname_sub_change = (Button) recordname_sub.findViewById(R.id.recordname_sub_change);
        Button recordname_sub_send = (Button) recordname_sub.findViewById(R.id.recordname_sub_send);
        ImageButton recordname_sub_cancle_btn = (ImageButton) recordname_sub.findViewById(R.id.recordname_sub_cancle_btn);

        recordname_sub_title.setText("현재 파일 :  " + file.getName());

//버튼처리
        recordname_sub_cancle_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    AudioApplication.getInstance().getServiceInterface().recordstopplay();
                } catch (Exception e) {
                }

                recordname_sub.dismiss();
            }
        }); //취소버튼 끝
        recordname_sub_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AudioApplication.getInstance().getServiceInterface().recordname_sub_play(file);
            }
        });
        recordname_sub_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recordname_sub_changename();

            }
        });
        recordname_sub_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Uri fileuri = Uri.fromFile(afterFileName);
                Log.e("파일패스에서 얻어지는 uri   ", "" + fileuri);
                uploadfile(afterFileName.getName().toString().substring(0, afterFileName.getName().toString().lastIndexOf(".")), fileuri);
            }
        });

        recordname_sub.show();
    }

    public void recordname_sub_changename() {
        //다이얼로그생성
        final Dialog recordname_sub_changename = new Dialog(this);
        recordname_sub_changename.setContentView(R.layout.recordname);
        recordname_sub_changename.setCancelable(false);
        Button okbtn = (Button) recordname_sub_changename.findViewById(R.id.ok);
        Button canclebtn = (Button) recordname_sub_changename.findViewById(R.id.cancle);
        final EditText edit = (EditText) recordname_sub_changename.findViewById(R.id.edittext);
        //확인버튼
        okbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String FileName = login_name + "_" + edit.getText().toString();
//파일명에 날짜시간 넣기
                SimpleDateFormat format = new SimpleDateFormat("yyyy년 MM월 dd일");
                String time = format.format(System.currentTimeMillis());

                Log.d("이전파일이름", String.valueOf(afterFileName));
                File newFileName = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/englishclass/record", FileName + "_" + time + ".3gp");

                Log.d("수정된파일이름", String.valueOf(newFileName));

                if (newFileName.exists()) {

                    metadata(String.valueOf(newFileName));
//                    beforeFileName.renameTo(exisitFileName);
                    afterFileName.renameTo(newFileName);
                    Log.e("재생시간", String.valueOf(duration));
                    updatadata(FileName + "_" + time);

                } else {

                    Log.e("재생시간", String.valueOf(duration));

                    afterFileName.renameTo(newFileName);
                    metadata(String.valueOf(newFileName));

                    ContentValues values = new ContentValues();

                    String recext = newFileName.getName().toString().substring(0, newFileName.getName().toString().lastIndexOf(".")) + ".3gp";
                    String recextpath = newFileName.getPath().toString().substring(0, newFileName.getPath().toString().lastIndexOf(".")) + ".3gp";
                    File rec = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/englishclass/record", recext);
                    Log.e("mp3 확장자 얻기 위한 이름   ", "" + recext);
                    values.put(MediaStore.Audio.Media.DISPLAY_NAME, rec.getName());
                    values.put(MediaStore.Audio.Media.TITLE, FileName + "_" + time);
                    values.put(MediaStore.Audio.Media.DURATION, duration);
                    Log.e("녹음 중지 시 저장되는 이름   ", newFileName.getName());
                    values.put(MediaStore.Audio.Media.DATA, rec.getPath());
                    Log.e("녹음 중지 시 저장되는 경로   ", newFileName.getPath());
                    values.put(MediaStore.Audio.Media.MIME_TYPE, "audio/*");

                    getContentResolver().insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values);
                    try {
                        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + Environment.getExternalStorageDirectory() + "/englishclass/record/" + afterFileName.getName())));

                    } catch (Exception e) {
                        Log.e("브로드캐스트 저장소 갱신", "오류", e);
                    }
                }

                Toast.makeText(getApplicationContext(), "이름이 변경되었습니다." + newFileName.getName(), Toast.LENGTH_SHORT).show();
                recordname_sub_title.setText("현재 파일 :  " + newFileName.getName());
                afterFileName = newFileName;

                recordname_sub_changename.dismiss();

            }
        }); //ok버튼 끝

        //취소버튼
        canclebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recordname_sub_changename.dismiss();
            }
        }); //취소버튼 끝
        recordname_sub_changename.show();
    }

    public void deletedialog_changename(String filename) {

        //다이얼로그생성
        final Dialog deletedialog_changename = new Dialog(this);
        deletedialog_changename.setContentView(R.layout.recordname);
        deletedialog_changename.setCancelable(false);
        Button okbtn = (Button) deletedialog_changename.findViewById(R.id.ok);
        Button canclebtn = (Button) deletedialog_changename.findViewById(R.id.cancle);
        final EditText edit = (EditText) deletedialog_changename.findViewById(R.id.edittext);
        //확인버튼
        okbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String FileName = login_name + "_" + edit.getText().toString();
//파일명에 날짜시간 넣기
                SimpleDateFormat format = new SimpleDateFormat("yyyy년 MM월 dd일");
                String time = format.format(System.currentTimeMillis());
                File preFileName = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/englishclass/record", filename + ".3gp");
                Log.d("이전파일이름", String.valueOf(afterFileName));
                changeFileName = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/englishclass/record", FileName + "_" + time + ".3gp");

                Log.d("수정된파일이름", String.valueOf(changeFileName));

                if (changeFileName.exists()) {

                    metadata(String.valueOf(changeFileName));
//                    beforeFileName.renameTo(exisitFileName);
                    preFileName.renameTo(changeFileName);
                    Log.e("재생시간", String.valueOf(duration));
                    updatadata(FileName + "_" + time);

                } else {

                    Log.e("재생시간", String.valueOf(duration));

                    preFileName.renameTo(changeFileName);
                    metadata(String.valueOf(changeFileName));

                    ContentValues values = new ContentValues();

                    String recext = changeFileName.getName().toString().substring(0, changeFileName.getName().toString().lastIndexOf(".")) + ".3gp";
                    String recextpath = changeFileName.getPath().toString().substring(0, changeFileName.getPath().toString().lastIndexOf(".")) + ".3gp";
                    File rec = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/englishclass/record", recext);
                    Log.e("mp3 확장자 얻기 위한 이름   ", "" + recext);
                    values.put(MediaStore.Audio.Media.DISPLAY_NAME, rec.getName());
                    values.put(MediaStore.Audio.Media.TITLE, FileName + "_" + time);
                    values.put(MediaStore.Audio.Media.DURATION, duration);
                    Log.e("녹음 중지 시 저장되는 이름   ", changeFileName.getName());
                    values.put(MediaStore.Audio.Media.DATA, rec.getPath());
                    Log.e("녹음 중지 시 저장되는 경로   ", changeFileName.getPath());
                    values.put(MediaStore.Audio.Media.MIME_TYPE, "audio/*");

                    getContentResolver().insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values);
                    try {
                        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + Environment.getExternalStorageDirectory() + "/englishclass/record/" + preFileName.getName())));

                    } catch (Exception e) {
                        Log.e("브로드캐스트 저장소 갱신", "오류", e);
                    }
                }

                Toast.makeText(getApplicationContext(), "이름이 변경되었습니다." + changeFileName.getName(), Toast.LENGTH_SHORT).show();


                deletedialog_changename.dismiss();
                deletedialog.dismiss();
                deletedialog(changeFileName.getName().toString().substring(0, changeFileName.getName().toString().lastIndexOf(".")), Uri.parse(changeFileName.getPath()));
            }
        }); //ok버튼 끝

        //취소버튼
        canclebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deletedialog_changename.dismiss();
            }
        }); //취소버튼 끝
        deletedialog_changename.show();
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
                serchfilename = serchname.getText().toString().trim();
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

    //녹음파일 롱클릭시 다이얼로그 띄우기
    public void deletedialog(final String filenamevalue, final Uri filepathvalue) {
        Log.e("롱클릭시 넘겨진 파일이름   ", "" + filenamevalue);
        Log.e("롱클릭시 넘겨진 파일패스   ", "" + filepathvalue.toString());
        //다이얼로그생성
        deletedialog = new Dialog(this);
        deletedialog.setContentView(R.layout.delete);
        TextView deletedialogtitle = (TextView) deletedialog.findViewById(R.id.deleltedialogtitle);
        deletedialogtitle.setText("선택된 파일  : " + filenamevalue);
        ext = filepathvalue.toString().substring(filepathvalue.toString().lastIndexOf("."));
        Log.e("롱클릭시 넘겨진자   ", "" + ext);
//        folder = "/storage/emulated/0/englishclass/record";
        Button btn_send_firebase = (Button) deletedialog.findViewById(R.id.btn_send_firebase);
        Button btn_change_name = (Button) deletedialog.findViewById(R.id.change_name_btn);
        Button btn_send_test = (Button) deletedialog.findViewById(R.id.btn_send_test);
        btn_send_test.setVisibility(View.GONE);
        Button deletebtn = (Button) deletedialog.findViewById(R.id.deletebtn);
        Button deletecanclebtn = (Button) deletedialog.findViewById(R.id.deletecanclebtn);
        Log.e("지워질 파일이름   ", filenamevalue);
        Log.e("딜리트 다이얼로그 실행됨   ", "딜리트");
//파이어베이스 업로드
        btn_send_firebase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (line.equals("1")) {
                    File deletefile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/englishclass/record", filenamevalue + ext);
                    Uri fileuri = Uri.fromFile(deletefile);
                    Log.e("파일패스에서 얻어지는 uri   ", "" + fileuri);
                    uploadfile(filenamevalue, fileuri);
                }else {
                    Toast.makeText(getApplicationContext(), "인터넷에 로그인 되어있지 않습니다.\n다시 로그인 하여 주세요", Toast.LENGTH_SHORT).show();
                }



            }
        }); //파이어베이스 업로드 끝
//이름바꾸기
        btn_change_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                deletedialog_changename(filenamevalue);


            }
        });
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

                            Upload upload = new Upload(FileName, taskSnapshot.getUploadSessionUri().toString(), login_name, "");
                            String uploadId = mDatabaseRef.push().getKey();
                            mDatabaseRef.child(FileName).setValue(upload);
                            progressDialog.dismiss();
                            try {
                                deletedialog.dismiss();
                            } catch (Exception e) {
                            }

//                            Toast.makeText(getApplicationContext(), "업로드 완료!", Toast.LENGTH_SHORT).show();
                            Toast toast =  Toast.makeText(getApplicationContext(), "업로드 완료!", Toast.LENGTH_SHORT);
                            View view = toast.getView();
                            view.setBackgroundColor(Color.RED);
                            TextView toastMessage = (TextView) toast.getView().findViewById(android.R.id.message);
                            toastMessage.setTextColor(Color.WHITE);
                            toast.show();
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

    public void recording_btn() {
        GlideDrawableImageViewTarget gifImage = new GlideDrawableImageViewTarget(startbtn);
        Glide.clear(startbtn);
        Glide.with(this).load(R.drawable.recording2).signature(new StringSignature(UUID.randomUUID().toString())).into(gifImage);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterBroadcast();
        isRecording = false;
        AudioApplication.getInstance().getServiceInterface().stop();
        AudioApplication.getInstance().getServiceInterface().recrecordstop();
        AudioApplication.getInstance().getServiceInterface().clearPlayList();
        finish();

    }
    @Override
    protected void onResume() {
try {
    deletedialog.dismiss();
}catch (Exception e){}
        super.onResume();
    }
}

