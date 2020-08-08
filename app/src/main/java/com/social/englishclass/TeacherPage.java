package com.social.englishclass;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TeacherPage extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView mRecyclerView;
    private TeacherPageAdapter mAdapter;


    private DatabaseReference mDatabaseRef;
    private StorageReference mStorageRef;
    private List mUsers;
    private ImageButton search_loginid_btn;
    private EditText search_loginid_text;
    public static String login_name, login_school, search;
    String filename;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_page);

        Intent intent = getIntent();
        login_school = intent.getStringExtra("login_school");
        login_name = intent.getStringExtra("login_name");

        search_loginid_btn = (ImageButton) findViewById(R.id.search_loginid_btn);
        search_loginid_btn.setOnClickListener(this);
        search_loginid_text = (EditText) findViewById(R.id.search_loginid_text);
        mRecyclerView = (RecyclerView) findViewById(R.id.teacherpage_recyclerview);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        mRecyclerView.setLayoutManager(layoutManager);

        progressDialog = new ProgressDialog(TeacherPage.this);
        progressDialog.setMessage("서버에서 학생 목록을 불러오는 중입니다...\n잠시만 기다려주세요");
        progressDialog.show();
        mUsers = new ArrayList<>();

        mDatabaseRef = FirebaseDatabase.getInstance().getReference("users");
        load_login_list();


    }//온크리에이트 끝

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.search_loginid_btn:
                search = search_loginid_text.getText().toString().trim();
                if(search.equals("")){
                    load_login_list();
                }else {
                    search_server();
                }
                break;
        }
    }

    //해당 차일드의 스트링값을 모두 가져와서 포함된 것만 검색
    private void search_server(){
        mUsers.clear();
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Boolean found;

                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    String searchname = ds.getKey();
                    found = searchname.contains(search);
                    Log.e("검색어 포함 결과", searchname+"/"+found);

                    if(found){

                        mUsers.add(ds.getKey());

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

    private void load_login_list(){
        mDatabaseRef.addValueEventListener(new ValueEventListener() {


            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                mUsers.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {


                    mUsers.add(postSnapshot.getKey());
                    Log.e("mUsers 에 추가됨 ", " " + postSnapshot.getKey());
                }


                mAdapter = new TeacherPageAdapter(TeacherPage.this, mUsers);

                mRecyclerView.setAdapter(mAdapter);

                try {
                    progressDialog.dismiss();
                } catch (Exception e) {
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(TeacherPage.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();

            }


        });
    }
    @Override
    protected void onDestroy () {
        super.onDestroy();
        progressDialog = null;
        finish();

    }
}
