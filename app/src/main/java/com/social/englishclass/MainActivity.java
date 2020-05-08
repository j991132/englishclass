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

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class MainActivity extends AppCompatActivity{

    private Intent intent;
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final EditText login_name = (EditText)findViewById(R.id.login_name);
        Button login_btn= (Button) findViewById(R.id.login_btn);

        intent = new Intent(MainActivity.this, SelectLesson.class);

        View.OnClickListener Listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.login_btn:
                        UserData userdata = new UserData();
                        userdata.userID = login_name.getText().toString().trim();
                        userdata.fcmToken = FirebaseInstanceId.getInstance().getToken();
                        firebaseDatabase.getReference("users").child(userdata.userID).setValue(userdata);
                        intent.putExtra("login_name", userdata.userID);
                        intent.putExtra("token", userdata.fcmToken);
                        startActivity(intent);
                        break;

                }
            }
        };
        login_btn.setOnClickListener(Listener);



    }
}
