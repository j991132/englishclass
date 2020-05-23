package com.social.englishclass;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class MainActivity extends AppCompatActivity{

    private Intent intent;
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private int line=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final EditText login_school = (EditText)findViewById(R.id.login_school);
        final EditText login_name = (EditText)findViewById(R.id.login_name);
        Button login_btn= (Button) findViewById(R.id.login_btn);
        RadioButton online_rbtn = (RadioButton)findViewById(R.id.online_rbtn);
        RadioButton offline_rbtn = (RadioButton)findViewById(R.id.offline_rbtn);
//테스트중 넘어가기
//        intent = new Intent(MainActivity.this, englishlesson.class);
//        intent = new Intent(MainActivity.this, visualtest.class);
//        intent = new Intent(MainActivity.this, SelectLesson.class);
//        startActivity(intent);

        View.OnClickListener Listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.login_btn:

                        UserData userdata = new UserData();
                        String schoolID = login_school.getText().toString().trim();
                        userdata.schoolID = schoolID;
                        userdata.userID = login_name.getText().toString().trim();
                        userdata.fcmToken = FirebaseInstanceId.getInstance().getToken();

                        if(schoolID.equals("")||userdata.userID==null) {
                            Toast.makeText(getApplicationContext(), "학교명 또는 아이디를 입력하세요", Toast.LENGTH_LONG).show();
                        }else {
                            if(line == 0) {
                                Toast.makeText(getApplicationContext(), "온라인 또는 오프라인을 선택하세요", Toast.LENGTH_LONG).show();
                            }else {
                                firebaseDatabase.getReference("users").child(schoolID + userdata.userID).setValue(userdata);
//                                intent = new Intent(MainActivity.this, englishlesson.class);
                                intent = new Intent(MainActivity.this, SelectLesson.class);
                                intent.putExtra("login_school", schoolID);
                                intent.putExtra("login_name", schoolID + userdata.userID);
                                intent.putExtra("token", userdata.fcmToken);
                                startActivity(intent);
                            }
                        }
                        break;
                    case R.id.online_rbtn:
                        line = 1;
                        break;
                    case R.id.offline_rbtn:
                        line = 2;
                        break;

                }
            }
        };
        login_btn.setOnClickListener(Listener);
        online_rbtn.setOnClickListener(Listener);
        offline_rbtn.setOnClickListener(Listener);


    }
}
