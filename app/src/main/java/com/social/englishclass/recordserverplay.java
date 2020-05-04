package com.social.englishclass;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.ArrayList;

public class recordserverplay extends AppCompatActivity implements View.OnClickListener{

    private String filename, ext;
    public static MediaPlayer mMediaplayer;
    private Uri uri, muri;
    private StorageReference mStorageRef;
    private ImageButton mBtnPlayPause;
    private TextView recplay_txt_title;
    private static boolean isPrepared ;
    public static boolean reset;
    private Spinner spinner;
    private float f;
    ArrayList<String> arrayList;
    ArrayAdapter<String> arrayAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recordserverplay);

// 인텐트 값 받기
        Intent intent = getIntent();
        filename = intent.getStringExtra("filename");
        ext = intent.getStringExtra("ext");
//화면 뷰 매칭
       recplay_txt_title = (TextView)findViewById(R.id.recplay_txt_title);
        mBtnPlayPause = (ImageButton) findViewById(R.id.recplay_btn_play_pause);
        mBtnPlayPause.setOnClickListener(this);

        recplay_txt_title.setText(filename);
        speedselect_server();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.recplay_btn_play_pause:
//                togglePlay((float) 1.00);
                // 플레이어 화면으로 이동할 코드가 들어갈 예정

                mMediaplayer = new MediaPlayer();
                mMediaplayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                getaudiourl(filename+ext);

                break;
        }
    }//클릭 끝
// 파이어베이스에서 스트리밍하기
    public void getaudiourl(final String Filename) {
        mStorageRef = FirebaseStorage.getInstance().getReference("uploads");

        // 파이어베이스에서 가져오기
        mStorageRef.child(Filename).getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        muri = uri;
                        Log.e("2  파이어베이스에서 불러온 url  ", "" + muri);
                        try {
                            mMediaplayer.setDataSource(muri.toString());
                            mMediaplayer.prepareAsync();
                            mMediaplayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

                                @Override
                                public void onPrepared(MediaPlayer mp) {
                                    isPrepared = true;
                                    reset = false;
                                    mp.start();
                                    updateUI();

                                    //녹음재생 완료후 정지
                                    mMediaplayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                        @Override
                                        public void onCompletion(MediaPlayer mp) {
                                            if (mMediaplayer != null) {
                                                isPrepared = false;

//                                          mMediaplayer.release();    //객체를 파괴하여 다시 못씀
                                                mMediaplayer.reset();
                                                reset = true;
                                                updateUI();
                                                speedselect_server();
                                            }
                                        }
                                    });

                                }
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("조회 실패2  ", "조회실패2");
                    }
                });
    }
//플레이버튼 ui 업데이트
private void updateUI() {
    if (mMediaplayer.isPlaying()) {

        mBtnPlayPause.setImageResource(R.drawable.pause);
    } else {
        mBtnPlayPause.setImageResource(R.drawable.play);
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

        arrayAdapter = new ArrayAdapter<>(getApplicationContext(),
                android.R.layout.simple_spinner_dropdown_item, arrayList);

        spinner = (Spinner) findViewById(R.id.recplay_spinner);
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

        mMediaplayer.setPlaybackParams((mMediaplayer.getPlaybackParams().setSpeed(a)));
        mMediaplayer.start();

    }
}
}//메인 끝
