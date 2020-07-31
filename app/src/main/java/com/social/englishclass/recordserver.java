package com.social.englishclass;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class recordserver extends AppCompatActivity implements View.OnClickListener {
    private RecyclerView mRecyclerView;
    private recordserverAdapter mAdapter;

    private ProgressBar mProgressCircle;
    private DatabaseReference mDatabaseRef;
    private StorageReference mStorageRef;
    private List<Upload> mUploads;
    private ImageButton mBtnPlayPause, search_server_btn;
    private TextView rec_mTxtTitle;
    private EditText search_server_text;
    public static String login_name, login_school;
    String filename;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recordserver);



        Intent intent = getIntent();
        login_school = intent.getStringExtra("login_school");
        login_name = intent.getStringExtra("login_name");

        mBtnPlayPause = (ImageButton) findViewById(R.id.rec_btn_play_pause);
        mBtnPlayPause.setOnClickListener(this);
        search_server_btn = (ImageButton)findViewById(R.id.search_server_btn);
        mRecyclerView = (RecyclerView) findViewById(R.id.rec_recyclerview);
        search_server_btn.setOnClickListener(this);
        LinearLayout reclayout = (LinearLayout)findViewById(R.id.rec_lin_miniplayer);
        reclayout.setVisibility(View.GONE);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        rec_mTxtTitle = (TextView) findViewById(R.id.rec_txt_title);
        search_server_text = (EditText)findViewById(R.id.search_server_text);
        registerBroadcast();
//        mProgressCircle = findViewById(R.id.progress_circle);



        mUploads = new ArrayList<>();
        Log.e("입력전 리스트  ", " " + mUploads);

        progressDialog = new ProgressDialog(recordserver.this);
        progressDialog.setMessage("서버에서 녹음파일 목록을 불러오는 중입니다...");
        progressDialog.show();
//        Log.e("프로그래스다이얼로그  ", " "+progressDialog);

        mDatabaseRef = FirebaseDatabase.getInstance().getReference("uploads");
        mDatabaseRef.addValueEventListener(new ValueEventListener() {



            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                mUploads.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                    Upload upload = postSnapshot.getValue(Upload.class);
                    mUploads.add(upload);
//시간역순 정렬
                    Collections.sort(mUploads, sortByTotalCall);

                }
 /*
        mStorageRef = FirebaseStorage.getInstance().getReference("uploads");

        // 파이어베이스에서 가져오기
        mStorageRef.child("1_2020년 04월 16일.3gp").getMetadata()
                .addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
                    @Override
                    public void onSuccess(StorageMetadata storageMetadata) {
                        String name = String.valueOf(storageMetadata.getName());
                        Log.e("2  파이어베이스에서 불러온 이름  ", name);
                       Upload upload = new Upload();
                       upload.setName(name);
//                        Upload upload = new Upload(name);
                        Log.e("리스트 전 업로드자료  ", " "+upload.getName());

                        mUploads.add(upload);
                        Log.e("리스트  ", " "+mUploads);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("조회 실패2  ", "조회실패2");
                    }
                });
*/


                mAdapter = new recordserverAdapter(recordserver.this, mUploads);

                mRecyclerView.setAdapter(mAdapter);
//                mProgressCircle.setVisibility(View.INVISIBLE);
                progressDialog.dismiss();
                Log.e("프로그래스다이얼로그 사라짐 ", " "+progressDialog);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(recordserver.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();

            }


        });


    }
    private final static Comparator<Upload> sortByTotalCall= new Comparator<Upload>() {

        @Override

        public int compare(Upload object1, Upload object2) {

            return Collator.getInstance().compare(object2.getName().substring(object2.getName().lastIndexOf("_")+1), object1.getName().substring(object1.getName().lastIndexOf("_")+1));

        }

    };
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterBroadcast();
        progressDialog =null;

        finish();

    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rec_btn_play_pause:
                togglePlay((float) 1.00);
                // 플레이어 화면으로 이동할 코드가 들어갈 예정
                break;
            case R.id.search_server_btn:
                search_server();
                break;
        }
    }

    private void updateUI() {
        if (recordserverAdapter.mMediaplayer.isPlaying()) {

            mBtnPlayPause.setImageResource(R.drawable.pause);
        } else {
            mBtnPlayPause.setImageResource(R.drawable.play);
        }
if(recordserverAdapter.reset ){

    rec_mTxtTitle.setText("재생중인 파일이 없습니다.");
    Log.e("reset 상태  ", " " + recordserverAdapter.reset );
} else{rec_mTxtTitle.setText(filename);}



//            speedselect();

    }

    public void registerBroadcast() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(BroadcastActions.START);
        filter.addAction(BroadcastActions.PLAY_STATE_CHANGED);

        registerReceiver(receiver, filter);
    }
    public void unregisterBroadcast() {
        unregisterReceiver(receiver);
    }
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if(intent.getAction().equals("START")){
                filename = intent.getStringExtra("filename");
                Log.e("재생목록 파일 이름  ", " " +  filename);
                updateUI();
            }

            updateUI();

        }
    };

    public void togglePlay(float a) {
        if (recordserverAdapter.mMediaplayer.isPlaying()) {
            recordserverAdapter.pause();
        } else {

            recordserverAdapter.play(a);
        }
    }
//해당 차일드의 스트링값을 모두 가져와서 포함된 것만 검색
    private void search_server(){
        mUploads.clear();
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Boolean found;
                String search = search_server_text.getText().toString();
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    String searchname = ds.child("name").getValue(String.class);
                    found = searchname.contains(search);
                    Log.e("검색어 포함 결과", searchname+"/"+found);
                    if(found){
                        Upload upload = ds.getValue(Upload.class);
                        mUploads.add(upload);
                        Collections.sort(mUploads, sortByTotalCall);
                    }
                }
//                mAdapter = new recordserverAdapter(recordserver.this, mUploads);

                mRecyclerView.setAdapter(mAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        mDatabaseRef.addListenerForSingleValueEvent(eventListener);
    }
    @Override
    public void onBackPressed()
              {
        finish();
        super.onBackPressed();
    }


}


