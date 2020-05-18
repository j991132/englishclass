package com.social.englishclass;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static androidx.fragment.app.FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentPagerAdapter;
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
import android.widget.Toast;

import com.google.firebase.database.annotations.Nullable;
import com.social.englishclass.ui.main.SectionsPagerAdapter;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class level extends AppCompatActivity implements View.OnClickListener {


private String current_lv;
    private Button startbtn, stopbtn, playbtn, stopplay, btn_server;
    public static final int REQUEST_AUDIO_PERMISSION_CODE = 1;
    boolean isRecording = false;
    private int pause;
    private File beforeFileName, afterFileName, beforesendtest, aftersendtest, exisitFileName;

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level);
//현재 보여줄 탭 번호 받기
        Intent intent = getIntent();
        current_lv = intent.getStringExtra("lv_num");
//뷰매칭
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

//탭 화면구성
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setOffscreenPageLimit(5); //탭은 보통 3개까지 로드 되고 죽지만 제한을 5개로 늘려준다
        viewPager.setAdapter(sectionsPagerAdapter);
        viewPager.setCurrentItem(Integer.parseInt(current_lv));  // 현재 보여줄 탭 세팅
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);





//        FloatingActionButton fab = findViewById(R.id.fab);

//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
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
                        AudioApplication.getInstance().getServiceInterface().record();
                        isRecording = true;
                        pause = 0;
                        startbtn.setText("일시정지");
                    }else if(isRecording == true && pause == 0 ){
                        AudioApplication.getInstance().getServiceInterface().recordpause();

                        startbtn.setText("녹음시작");
                    }else if (isRecording == true && pause == 1){
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
                AudioApplication.getInstance().getServiceInterface().recordstop();
                recordname();

                break;
            case R.id.btnPlay:
                // 녹음 재생
                stopbtn.setEnabled(false);
                startbtn.setEnabled(true);
//                playbtn.setEnabled(false);
                stopplay.setEnabled(true);

                recordlistdialog();
                updateUI();
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
                intent.putExtra("login_school",login_school);
                intent.putExtra("login_name",login_name);
                startActivity(intent);
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
                beforeFileName = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/englishclass/record", "AudioRecording.pcm");
//                beforesendtest = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/englishclass/record", "sendtest.txt");
                Log.d("이전파일이름", String.valueOf(beforeFileName));
                afterFileName = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/englishclass/record", FileName +"_"+time+".wav");
                exisitFileName = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/englishclass/record", FileName +"_"+time+".pcm");
//                aftersendtest = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/englishclass/record", FileName +"_"+time+".txt");
                Log.d("수정된파일이름", String.valueOf(afterFileName));

                if (afterFileName.exists()){
//                    Log.e("저장되어 있는 파일이름      ", String.valueOf(afterFileName));
//                    afterFileName.mkdirs();
//                    afterFileName.delete();
//                    metadata(String.valueOf(beforeFileName));
                    beforeFileName.renameTo(exisitFileName);
                    Log.e("재생시간",String.valueOf( duration));
                    try {
                        rawToWave(exisitFileName, afterFileName);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
//                      wavtomp3(new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/englishclass/record", "1.wav"));
                    wavtomp3(afterFileName);
//                    beforeFileName.renameTo(afterFileName);
//                    beforesendtest.renameTo(aftersendtest);
//                    updatadata(FileName+"_"+time);
//                    Log.e("삭제된 파일이름      ", String.valueOf(afterFileName));
                }else {

                    Log.e("재생시간", String.valueOf(duration));
//pcm to wav
                    try {
                        rawToWave(beforeFileName, afterFileName);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
//                    wavtomp3(new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/englishclass/record", "1.wav"));
//wav to mp3
                    wavtomp3(afterFileName);
//                    beforeFileName.renameTo(afterFileName);
                    beforesendtest.renameTo(aftersendtest);
                    fname = String.valueOf(afterFileName);
//                    metadata(String.valueOf(afterFileName));


//                metadata(fname);
                    ContentValues values = new ContentValues();
                    String mp3ext = afterFileName.getName().toString().substring(0,afterFileName.getName().toString().lastIndexOf("."))+".mp3";
                    String mp3extpath = afterFileName.getPath().toString().substring(0,afterFileName.getPath().toString().lastIndexOf("."))+".mp3";
                    File mp3 = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/englishclass/record", mp3ext);
                    Log.e("mp3 확장자 얻기 위한 이름   ", ""+mp3ext);
                    values.put(MediaStore.Audio.Media.DISPLAY_NAME, mp3.getName());
                    values.put(MediaStore.Audio.Media.TITLE, FileName+"_"+time);

//                    values.put(MediaStore.Audio.Media.DURATION, duration);
                    Log.e("녹음 중지 시 저장되는 이름   ", afterFileName.getName());
                    values.put(MediaStore.Audio.Media.DATA, mp3.getPath());
                    Log.e("녹음 중지 시 저장되는 경로   ", afterFileName.getPath());

                    values.put(MediaStore.Audio.Media.MIME_TYPE, "audio/*");



//                Uri uri = MediaStore.Audio.Media.getContentUriForPath(afterFileName.getPath());
                    getContentResolver().insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values);
//                getApplicationContext().getContentResolver().notifyChange(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null);
//                    getContentResolver().update(Settings.System.CONTENT_URI, values, null, null);
                }


                if (beforeFileName.renameTo(afterFileName))
                { Toast.makeText(getApplicationContext(), "success!" + FileName + beforeFileName, Toast.LENGTH_SHORT).show();}
                else
                {  Toast.makeText(getApplicationContext(), "faile" + FileName + beforeFileName, Toast.LENGTH_SHORT).show();}

//                getContentResolver().notifyChange( Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "/englishclass/record")), null);
//                getContentResolver().notifyChange( MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null);
//                sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.fromFile(Environment.getExternalStorageDirectory())));
//                MediaScannerConnection.scanFile(getApplicationContext(), new String[]{new File(Environment.getExternalStorageDirectory(), "englishclass/record").getPath()}, null, null);


//                MediaStore.Audio.Media._ID,
//                        MediaStore.Audio.Media.TITLE,
//                        MediaStore.Audio.Media.ARTIST,
//                        MediaStore.Audio.Media.ALBUM,
//                        MediaStore.Audio.Media.ALBUM_ID,
//                        MediaStore.Audio.Media.DURATION,
//                        MediaStore.Audio.Media.DATA

/*                ContentValues values = new ContentValues();
                values.put(MediaStore.Audio.Media.DISPLAY_NAME, afterFileName.getName());
                values.put(MediaStore.Audio.Media.TITLE, FileName);

                values.put(MediaStore.Audio.Media.DURATION, duration);
                Log.e("녹음 중지 시 저장되는 이름   ", afterFileName.getName());
                values.put(MediaStore.Audio.Media.DATA, afterFileName.getPath());
                Log.e("녹음 중지 시 저장되는 경로   ", afterFileName.getPath());

                values.put(MediaStore.Audio.Media.MIME_TYPE, "audio/*");



//                Uri uri = MediaStore.Audio.Media.getContentUriForPath(afterFileName.getPath());
                getContentResolver().insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values);
//                getApplicationContext().getContentResolver().notifyChange(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null);
//                    getContentResolver().update(Settings.System.CONTENT_URI, values, null, null);
*/
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

}