package com.social.englishclass;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class recordserver extends AppCompatActivity implements View.OnClickListener {
    private RecyclerView mRecyclerView;
    private recordserverAdapter mAdapter;

    private ProgressBar mProgressCircle;
    private DatabaseReference mDatabaseRef;
    private StorageReference mStorageRef;
    private List<Upload> mUploads;
    private ImageButton mBtnPlayPause;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recordserver);
        mBtnPlayPause = (ImageButton) findViewById(R.id.rec_btn_play_pause);
        mBtnPlayPause.setOnClickListener(this);
        mRecyclerView = (RecyclerView) findViewById(R.id.rec_recyclerview);


        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);

//        mProgressCircle = findViewById(R.id.progress_circle);

        mUploads = new ArrayList<>();
        Log.e("입력전 리스트  ", " " + mUploads);

        mDatabaseRef = FirebaseDatabase.getInstance().getReference("uploads");

        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Upload upload = postSnapshot.getValue(Upload.class);
                    mUploads.add(upload);
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


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(recordserver.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();

            }


        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rec_btn_play_pause:
                updateUI();
                // 플레이어 화면으로 이동할 코드가 들어갈 예정
                break;
        }
    }

    private void updateUI() {
        if (recordserverAdapter.mMediaplayer.isPlaying()) {
//        if (AudioApplication.getInstance().getServiceInterface().isPlaying()) {
            mBtnPlayPause.setImageResource(R.drawable.pause);
        } else {
            mBtnPlayPause.setImageResource(R.drawable.play);
        }
    }
}

