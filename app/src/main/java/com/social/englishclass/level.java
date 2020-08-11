package com.social.englishclass;

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
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static androidx.fragment.app.FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.social.englishclass.ui.main.LetsreadFragment;
import com.social.englishclass.ui.main.PlaceholderFragment;
import com.social.englishclass.ui.main.SectionsPagerAdapter;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class level extends AppCompatActivity implements View.OnClickListener {


    private String current_lv;
    public static String lesson_type, lesson;
    private ImageButton playbtn, stopplay, stopbtn, startbtn, btn_server, dictionary_btn, extrawork_btn;
    public static final int REQUEST_AUDIO_PERMISSION_CODE = 1;
    boolean isRecording = false;
    private int pause, index;
    private File beforeFileName, afterFileName, changeFileName;
    private Long duration;
    public static Dialog recordlistdialog, deletedialog;
    private String folder, fname, login_name, token, login_school, filepath, line;
    private AudioAdapter mAdapter, recordAdapter, serchAdapter;
    public String serchfilename, ext;
    public static String  level_text;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;
    private StorageTask mUploadTask;
    public  ViewPager viewPager;
    public static MediaPlayer mediaPlayer;
    private static boolean isPrepared ;
    private TextView recordname_sub_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level);
//현재 보여줄 탭 번호 받기
        Intent intent = getIntent();
        login_school = intent.getStringExtra("login_school");
        login_name = intent.getStringExtra("login_name");
        current_lv = intent.getStringExtra("lv_num");
        lesson = intent.getStringExtra("lesson");
        lesson_type = intent.getStringExtra("lesson_type");
        level_text = intent.getStringExtra("level_text");
        line = intent.getStringExtra("line");
//뷰매칭
        TextView level_title = (TextView)findViewById(R.id.level_text);
        level_title.setText(level_text);
//녹음버튼 관련
        startbtn = (ImageButton) findViewById(R.id.btnRecord);
        stopbtn = (ImageButton) findViewById(R.id.btnStop);
        playbtn = (ImageButton) findViewById(R.id.btnPlay);
        stopplay = (ImageButton) findViewById(R.id.StopPlay);
        btn_server = (ImageButton) findViewById(R.id.btn_server);
        dictionary_btn = (ImageButton) findViewById(R.id.dictionary_btn);
        extrawork_btn = (ImageButton)findViewById(R.id.extrawork_btn);
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
//탭 화면구성
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        viewPager = findViewById(R.id.view_pager);
        Log.e("레슨 타입  ", "lesson_type  "+lesson_type);
        if(lesson_type.equals("let")) {
            Log.e("if 문 안에 레슨타입  ", "lesson_type  " + lesson_type);
            File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/englishclass/lesson/letsread/l" + lesson + "_let_lv1_picture");
            File[] files = file.listFiles();
            if(files != null) {
                int count = files.length;
                Log.e("if 문 안에 카운트 수  ", "count  " + count);
                viewPager.setOffscreenPageLimit(count);
            }
        }else if(lesson_type.equals("rt1")){
            File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/englishclass/lesson/readandtalk/l" + lesson + "_rt1_picture");
            File[] files = file.listFiles();
            if(files != null) {
                int count = files.length;
                viewPager.setOffscreenPageLimit(count);
            }
        }else{
            Log.e("if 문 조건 실패  ", "lesson_type  "+lesson_type);
            viewPager.setOffscreenPageLimit(4); //탭은 보통 3개까지 로드 되고 죽지만 제한을 4개로 늘려준다

        }

        viewPager.setAdapter(sectionsPagerAdapter);
        viewPager.setCurrentItem(Integer.parseInt(current_lv));  // 현재 보여줄 탭 세팅
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

//        medissetting();
//        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//            @Override
//            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//
//                medissetting();
//            }
//
//            @Override
//            public void onPageSelected(int position) {
//
//                medissetting();
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int state) {
//
//            }
//        });

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


//        FloatingActionButton fab = findViewById(R.id.fab);

//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // READ_EXTERNAL_STORAGE 에 대한 권한 획득.
//            getAudioListFromMediaDatabase();
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
//                Log.e("현재 뷰페이퍼 번호", "번호"+ viewPager.getCurrentItem());
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
                ProgressDialog progressDialog = new ProgressDialog(level.this);
                progressDialog.setMessage("서버에서 녹음파일 목록을 불러오는 중입니다...\n잠시만 기다려주세요.");
                progressDialog.show();

                Thread thread = new Thread(new Runnable() {

                    @Override
                    public void run() {
                        // 3초가 지나면 다이얼로그 닫기
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

// 녹음서버 목록 보여주는 엑티비티 띄우기
                if (line.equals("1")) {
                    Intent intent = new Intent(this, recordserver.class);
                    intent.putExtra("login_school", login_school);
                    intent.putExtra("login_name", login_name);
                    startActivity(intent);
//                    finish();
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
                String FileName = login_name+"_"+edit.getText().toString();
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
                }catch (Exception e){}

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
                        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + Environment.getExternalStorageDirectory() + "/englishclass/record/" +afterFileName.getName() )));

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
                    Log.e("녹음 중지 시 저장되는 이름   ",changeFileName.getName());
                    values.put(MediaStore.Audio.Media.DATA, rec.getPath());
                    Log.e("녹음 중지 시 저장되는 경로   ", changeFileName.getPath());
                    values.put(MediaStore.Audio.Media.MIME_TYPE, "audio/*");

                    getContentResolver().insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values);
                    try {
                        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + Environment.getExternalStorageDirectory() + "/englishclass/record/" +preFileName.getName() )));

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

    //녹음파일 롱클릭시 전송, 삭제 다이얼로그 띄우기
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
        Button btn_change_name = (Button) deletedialog.findViewById(R.id.change_name_btn);
        btn_send_test.setVisibility(View.GONE);
        Button deletebtn = (Button) deletedialog.findViewById(R.id.deletebtn);
        Button deletecanclebtn = (Button) deletedialog.findViewById(R.id.deletecanclebtn);
        Log.e("지워질 파일이름   ", filenamevalue);
        Log.e("레벨 액티비티의 딜리트 다이얼로그", "실행됨");
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
            if (lesson_type.equals("let") || lesson_type.equals("rt1")){

                filepath = LetsreadFragment.filepath[viewPager.getCurrentItem()];
            }else{
                filepath = PlaceholderFragment.filepatharr[viewPager.getCurrentItem()];
//                filepath = "";
            }
            Log.e("업로드시 얻어지는 파일패스   ", "" + filepath);
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

                            Upload upload = new Upload(FileName, taskSnapshot.getUploadSessionUri().toString(), login_name, filepath);
                            String uploadId = mDatabaseRef.push().getKey();
                            mDatabaseRef.child(FileName).setValue(upload);
                            progressDialog.dismiss();
                            try {
                                deletedialog.dismiss();
                            } catch (Exception e) {
                            }

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
    public void recording_btn() {
        GlideDrawableImageViewTarget gifImage = new GlideDrawableImageViewTarget(startbtn);
        Glide.with(this).load(R.drawable.recording2).into(gifImage);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterBroadcast();
        PlaceholderFragment.forceStop = true;
        LetsreadFragment.forceStop = true;

       isRecording = false;
        AudioApplication.getInstance().getServiceInterface().stop();
        AudioApplication.getInstance().getServiceInterface().recrecordstop();
        AudioApplication.getInstance().getServiceInterface().clearPlayList();

        finish();

    }

}