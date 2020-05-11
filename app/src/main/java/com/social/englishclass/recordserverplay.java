package com.social.englishclass;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class recordserverplay extends AppCompatActivity implements View.OnClickListener{

    private String filename, ext, stress, accent, speed, pronunciation, login_name, login_name_fcm, login_name_teacher, send_token, login_school;
    public static MediaPlayer mMediaplayer;
    private Uri uri, muri;
    private StorageReference mStorageRef;
    private ImageButton mBtnPlayPause;
    private Button save_btn;
    private TextView recplay_txt_title, text_stress, text_accent, text_speed, text_pronunciation;
    private static boolean isPrepared ;
    public static boolean reset;
    public static boolean pause=false;
    private Spinner spinner,  umd_spiner;
    private float f;
    private EditText comment;
    ArrayList<String> arrayList;
    ArrayAdapter<String> arrayAdapter;
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();
    private ListView chat_view;
    private static final String FCM_MESSAGE_URL = "https://fcm.googleapis.com/fcm/send";
    private static final String SERVER_KEY = "AAAAPWMRRUI:APA91bGaqqUJuVclBGOynB4TiyWFDyVFqGs1l_blyaxaHI7oaUKecEgXG5bx5WQ7B4Nq22kwFwf4fH3YfzHccdt4Sy2ux2Yx-DvBmEYgKRmefBVOhWzVsensa_zIe5pOVVCeymi3D5DK";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recordserverplay);

// 인텐트 값 받기
        Intent intent = getIntent();
        login_school = intent.getStringExtra("login_school");
        login_name = intent.getStringExtra("login_name");
        filename = intent.getStringExtra("filename");
        ext = intent.getStringExtra("ext");
//화면 뷰 매칭
       recplay_txt_title = (TextView)findViewById(R.id.recplay_txt_title);
        mBtnPlayPause = (ImageButton) findViewById(R.id.recplay_btn_play_pause);
        mBtnPlayPause.setOnClickListener(this);
        save_btn = (Button)findViewById(R.id.save_btn);
        save_btn.setOnClickListener(this);
        comment = (EditText)findViewById(R.id.comment);
        recplay_txt_title.setText(filename);
        speedselect_server();
        mMediaplayer = new MediaPlayer();
        mMediaplayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        chat_view = (ListView)findViewById(R.id.chat_view);
        text_stress = (TextView)findViewById(R.id.text_stress);
        text_accent = (TextView)findViewById(R.id.text_accent);
        text_speed = (TextView)findViewById(R.id.text_speed);
        text_pronunciation = (TextView)findViewById(R.id.text_pronunciation);
        LinearLayout test_layout1 = (LinearLayout) findViewById(R.id.t_test1);
        LinearLayout test_layout2 = (LinearLayout) findViewById(R.id.t_test2);
        if(!login_name.equals("teacher")){
            test_layout1.setVisibility(View.GONE);
            test_layout2.setVisibility(View.GONE);
        }
        up_mid_down_select("eng_stress");
        up_mid_down_select("eng_accent");
        up_mid_down_select("eng_speed");
        up_mid_down_select("eng_pronunciation");
        openChat();
//보이는 뷰에 업로드했던 로그인아이디 가져오기
        getLoginId();
    }//메인 끝

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.recplay_btn_play_pause:
//                togglePlay((float) 1.00);
                // 플레이어 화면으로 이동할 코드가 들어갈 예정
                if (mMediaplayer.isPlaying() && pause == false) {
                    Log.e("정지  ", "" + pause);
                    mMediaplayer.pause();
                    pause = true;
                    updateUI();
                }else if(pause == true){
                    Log.e("재생  ", "" + pause);
                    play(f);
                    pause = false;
                    updateUI();
                }
                else {
                    Log.e("파이어베이스 불러오기  ", "");
                    getaudiourl(filename + ext);
                }
                break;
// 피드백 저장버튼 클릭시
            case R.id.save_btn:
                if(comment.getText().toString().equals(""))

                    return;
                sendPostToFCM(comment.getText().toString());
                Log.e("코멘트 메세지  ", ""+comment.getText().toString());
//4종류 평가 전송
                umd_test umd = new umd_test(stress, accent, speed, pronunciation);
                databaseReference.child("umd_test").child(filename).push().setValue(umd);
//이름과 코멘트 챗 전송
                ChatDTO chat = new ChatDTO(login_name, comment.getText().toString());
                databaseReference.child("chat").child(filename).push().setValue(chat);


                comment.setText("");




                break;
        }
    }//클릭 끝
//파이어베이스에서 뷰에 해당하는 업로드시 저장된 로그인아이디 가져오기
    private void getLoginId(){
        firebaseDatabase.getReference("uploads")
                .child(filename)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        final Upload upload = dataSnapshot.getValue(Upload.class);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    login_name_fcm = upload.getUserId();
                                    Log.e("파이어베이스 저장된 로그인아이디  ", ""+login_name_fcm);
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }
 //파이어베이스에서 선생님의 로그인아이디 가져오기
    private void getTeacherLoginId(){
        firebaseDatabase.getReference("users")
                .child(login_school+"teacher")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        final UserData userTeacherData = dataSnapshot.getValue(UserData.class);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    if (userTeacherData != null) {
                                        login_name_teacher = userTeacherData.fcmToken;
                                        Log.e("파이어베이스 저장된 선생님 토큰  ", ""+login_name_teacher);
                                    }
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }
//푸쉬 알림 보내기
    private  void  sendPostToFCM(final String msg){
        getTeacherLoginId();

            firebaseDatabase.getReference("users")
                    .child(login_name_fcm)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            final UserData userData = dataSnapshot.getValue(UserData.class);
                            if(login_name.equals(login_name_fcm)){
                                send_token = login_name_teacher;
                            }else{
                                send_token = userData.fcmToken;
                            }
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        // FMC 메시지 생성 start
                                        JSONObject root = new JSONObject();
                                        JSONObject notification = new JSONObject();
                                        notification.put("body", msg);
                                        Log.e("전송하는 메세지  ", ""+notification+msg);

                                        notification.put("title", getString(R.string.app_name));
                                        root.put("notification", notification);

                                        root.put("to", send_token);
                                            Log.e("로그인 아이디,  저장된 아이디,put", "//"+login_name+"//"+login_name_fcm+"//"+send_token);

                                        // FMC 메시지 생성 end

                                        URL Url = new URL(FCM_MESSAGE_URL);
                                        HttpURLConnection conn = (HttpURLConnection) Url.openConnection();
                                        conn.setRequestMethod("POST");
                                        conn.setDoOutput(true);
                                        conn.setDoInput(true);
                                        conn.addRequestProperty("Authorization", "key=" + SERVER_KEY);
                                        conn.setRequestProperty("Accept", "application/json");
                                        conn.setRequestProperty("Content-type", "application/json");
                                        OutputStream os = conn.getOutputStream();
                                        os.write(root.toString().getBytes("utf-8"));
                                        os.flush();
                                        conn.getResponseCode();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }).start();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
        }

//메세지 추가 메서드
private void addMessage(DataSnapshot dataSnapshot, ArrayAdapter<String> adapter) {
    ChatDTO chatDTO = dataSnapshot.getValue(ChatDTO.class);
    adapter.add(chatDTO.getUserName() + " : " + chatDTO.getMessage());
}
//메세지 삭제 메서드
private void removeMessage(DataSnapshot dataSnapshot, ArrayAdapter<String> adapter) {
    ChatDTO chatDTO = dataSnapshot.getValue(ChatDTO.class);
    adapter.remove(chatDTO.getUserName() + " : " + chatDTO.getMessage());
}
    //평가 셋팅 메서드
    private void add_umd_test(DataSnapshot dataSnapshot) {
        umd_test umd_data = dataSnapshot.getValue(umd_test.class);
        text_stress.setText(umd_data.getstress());
        text_accent.setText(umd_data.getaccent());
        text_speed.setText(umd_data.getspeed());
        text_pronunciation.setText(umd_data.getpronunciation());

    }
//파이어 DB 에서 챗 내용 가져오기
private void openChat() {
    // 리스트 어댑터 생성 및 세팅
    final ArrayAdapter<String> adapter

            = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1);
    chat_view.setAdapter(adapter);

    // 데이터 받아오기 및 어댑터 데이터 추가 및 삭제 등..리스너 관리
    databaseReference.child("chat").child(filename).addChildEventListener(new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            addMessage(dataSnapshot, adapter);

            Log.e("LOG", "s:"+s);
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
            removeMessage(dataSnapshot, adapter);
        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }



        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    });
// 상중하 평가 불러오기
databaseReference.child("umd_test").child(filename).addChildEventListener(new ChildEventListener() {
    @Override
    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
        add_umd_test(dataSnapshot);
        Log.e("LOG", "s:"+s);
    }

    @Override
    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
        add_umd_test(dataSnapshot);
    }

    @Override
    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

    }

    @Override
    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {

    }
});
}
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
//상중하 평가 스피너 버튼
    private void up_mid_down_select(final String spiner_name){
        int resID = getResources().getIdentifier(spiner_name,"id", "com.social.englishclass");
        final ArrayList<String> umd_arrayList;
        ArrayAdapter<String> umd_arrayAdapter;
        umd_arrayList = new ArrayList<>();
        umd_arrayList.add("평가선택");
        umd_arrayList.add("상");
        umd_arrayList.add("중");
        umd_arrayList.add("하");

        umd_arrayAdapter = new ArrayAdapter<>(getApplicationContext(),android.R.layout.simple_spinner_dropdown_item, umd_arrayList);

        umd_spiner = (Spinner)findViewById(resID);
        umd_spiner.setAdapter(umd_arrayAdapter);
        umd_spiner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (umd_arrayList.get(i)) {
                    case "상":
switch (spiner_name){
    case "eng_stress":
        stress = "상";
        break;
    case "eng_accent":
        accent = "상";
        break;
    case "eng_speed":
        speed = "상";
        break;
    case "eng_pronunciation":
        pronunciation = "상";
        break;
}
                        break;
                    case "중":
                        switch (spiner_name){
                            case "eng_stress":
                                stress = "중";
                                break;
                            case "eng_accent":
                                accent = "중";
                                break;
                            case "eng_speed":
                                speed = "중";
                                break;
                            case "eng_pronunciation":
                                pronunciation = "중";
                                break;
                        }
                        break;
                    case "하":
                        switch (spiner_name){
                            case "eng_stress":
                                stress = "하";
                                break;
                            case "eng_accent":
                                accent = "하";
                                break;
                            case "eng_speed":
                                speed = "하";
                                break;
                            case "eng_pronunciation":
                                pronunciation = "하";
                                break;
                        }
                        break;

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
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
