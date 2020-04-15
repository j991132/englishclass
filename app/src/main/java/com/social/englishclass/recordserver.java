package com.social.englishclass;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class recordserver extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private recordserverAdapter mAdapter;

    private ProgressBar mProgressCircle;

    private StorageReference mStorageRef;
    private List<Upload> mUploads;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_englishlesson);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);



        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);

//        mProgressCircle = findViewById(R.id.progress_circle);

        mUploads = new ArrayList<>();

        mStorageRef = FirebaseStorage.getInstance().getReference("uploads");

        // 파이어베이스에서 가져오기
        mStorageRef.child("12020년 04월 02일.3gp").getMetadata()
                .addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
                    @Override
                    public void onSuccess(StorageMetadata storageMetadata) {
                        String name = String.valueOf(storageMetadata.getName());
                        Log.e("2  파이어베이스에서 불러온 이름  ", name);
                        Upload upload = new Upload();
                        upload.setName(name);
//                        Upload upload = new Upload(name);
                        mUploads.add(upload);
                        Log.e("리스트  ", " "+mUploads);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("조회 실패  ", "조회실패");
                    }
                });


        mAdapter = new recordserverAdapter(this, mUploads);

        mRecyclerView.setAdapter(mAdapter);
//                mProgressCircle.setVisibility(View.INVISIBLE);


            }



}
