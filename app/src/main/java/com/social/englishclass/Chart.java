package com.social.englishclass;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Chart extends AppCompatActivity {

    private String login_id;
    private DatabaseReference mDatabaseRef;
    private List<umd_test> mumd_test;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        Intent intent = getIntent();
        login_id = intent.getStringExtra("login_id");

        progressDialog = new ProgressDialog(Chart.this);
        progressDialog.setMessage("서버에서 평가목록을 불러오는 중입니다...\n잠시만 기다려주세요");
        progressDialog.show();

        mumd_test = new ArrayList<>();

        mDatabaseRef = FirebaseDatabase.getInstance().getReference("umd_test");
        mDatabaseRef.addValueEventListener(new ValueEventListener() {


            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Boolean found;
                mumd_test.clear();
//                int i =0;
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    String searchname = postSnapshot.getKey();
                    found = searchname.contains(login_id);

                    if(found){
                        umd_test umdTest = postSnapshot.getValue(umd_test.class);
                        mumd_test.add(umdTest);
                        umd_test umd_data = mumd_test.get(0);
                        Log.e("mumd_test 에 추가됨 ", "searchname"+searchname+"  값 "+umd_data.getaccent());

                    }


//                    i++;
                }
//                for (int i=0; i<mumd_test.size();i++) {
//                Log.e("umd_data 에 추가됨 ", " "+mumd_test.get(0));
//                    umd_test umd_data = mumd_test.get(0);
//                    Log.e("umd_data 에 추가됨 ", " "+umd_data.);
//                }


                //시간역순 정렬
//                Collections.sort(mUploads, sortByTotalCall);

                try {
                    progressDialog.dismiss();
                } catch (Exception e) {
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Chart.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();

            }


        });


    }
}
