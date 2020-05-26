package com.social.englishclass;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import static com.social.englishclass.englishlesson.REQUEST_AUDIO_PERMISSION_CODE;

public class SelectLesson extends AppCompatActivity {

    private Intent intent;
    private String login_name, token, login_school;
    private Dialog lesson_dialog, level_dialog;
    public static String lesson, lesson_type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_lesson);

        Intent login_intent = getIntent();
        login_school = login_intent.getStringExtra("login_school");
        login_name = login_intent.getStringExtra("login_name");
        token = login_intent.getStringExtra("token");

        // OS가 Marshmallow 이상일 경우 권한체크를 해야 합니다.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1000);
            } else {
                // READ_EXTERNAL_STORAGE 에 대한 권한이 있음.
//                getAudioListFromMediaDatabase();
            }
        }
        // OS가 Marshmallow 이전일 경우 권한체크를 하지 않는다.
        else {
//            getAudioListFromMediaDatabase();
        }



        ImageButton btn1 = (ImageButton) findViewById(R.id.button1);
        ImageButton btn2 = (ImageButton) findViewById(R.id.button2);
        ImageButton btn3 = (ImageButton) findViewById(R.id.button3);
        ImageButton btn4 = (ImageButton) findViewById(R.id.button4);
        ImageButton btn5 = (ImageButton) findViewById(R.id.button5);
        ImageButton btn6 = (ImageButton) findViewById(R.id.button6);
        ImageButton btn7 = (ImageButton) findViewById(R.id.button7);
        ImageButton btn8 = (ImageButton) findViewById(R.id.button8);
        ImageButton btn9 = (ImageButton) findViewById(R.id.button9);
        ImageButton btn10 = (ImageButton) findViewById(R.id.button10);
        ImageButton btn11 = (ImageButton) findViewById(R.id.button11);
        ImageButton btn12 = (ImageButton) findViewById(R.id.button12);
//        intent = new Intent(this, englishlesson.class);

        View.OnClickListener Listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.button1:
                        lessonDialog("Lesson 1 - What Grade Are You In?");
                        lesson = "1";

//                        intent.putExtra("login_school", login_school);
//                        intent.putExtra("login_name", login_name);
//                        intent.putExtra("token", token);
//                        intent.putExtra("lesson", "/storage/emulated/0/englishclass/lesson1");
//                        startActivity(intent);
                        break;
                    case R.id.button2:
                        intent.putExtra("login_school", login_school);
                        intent.putExtra("login_name", login_name);
                        intent.putExtra("token", token);
                        intent.putExtra("lesson", "/storage/emulated/0/englishclass/lesson2");
                        startActivity(intent);
                        break;
                    case R.id.button3:
                        intent.putExtra("login_school", login_school);
                        intent.putExtra("login_name", login_name);
                        intent.putExtra("token", token);
                        intent.putExtra("lesson", "/storage/emulated/0/englishclass/lesson3");
                        startActivity(intent);
                        break;
                    case R.id.button4:
                        intent.putExtra("login_school", login_school);
                        intent.putExtra("login_name", login_name);
                        intent.putExtra("token", token);
                        intent.putExtra("lesson", "/storage/emulated/0/englishclass/lesson4");
                        startActivity(intent);
                        break;
                    case R.id.button5:
                        intent.putExtra("login_school", login_school);
                        intent.putExtra("login_name", login_name);
                        intent.putExtra("token", token);
                        intent.putExtra("lesson", "/storage/emulated/0/englishclass/lesson5");
                        startActivity(intent);
                        break;
                    case R.id.button6:
                        intent.putExtra("login_school", login_school);
                        intent.putExtra("login_name", login_name);
                        intent.putExtra("token", token);
                        intent.putExtra("lesson", "/storage/emulated/0/englishclass/lesson6");
                        startActivity(intent);
                        break;
                    case R.id.button7:
                        intent.putExtra("login_school", login_school);
                        intent.putExtra("login_name", login_name);
                        intent.putExtra("token", token);
                        intent.putExtra("lesson", "/storage/emulated/0/englishclass/lesson7");
                        startActivity(intent);
                        break;
                    case R.id.button8:
                        intent.putExtra("login_school", login_school);
                        intent.putExtra("login_name", login_name);
                        intent.putExtra("token", token);
                        intent.putExtra("lesson", "/storage/emulated/0/englishclass/lesson8");
                        startActivity(intent);
                        break;
                    case R.id.button9:
                        intent.putExtra("login_school", login_school);
                        intent.putExtra("login_name", login_name);
                        intent.putExtra("token", token);
                        intent.putExtra("lesson", "/storage/emulated/0/englishclass/lesson9");
                        startActivity(intent);
                        break;
                    case R.id.button10:
                        intent.putExtra("login_school", login_school);
                        intent.putExtra("login_name", login_name);
                        intent.putExtra("token", token);
                        intent.putExtra("lesson", "/storage/emulated/0/englishclass/lesson10");
                        startActivity(intent);
                        break;
                    case R.id.button11:
                        intent.putExtra("login_school", login_school);
                        intent.putExtra("login_name", login_name);
                        intent.putExtra("token", token);
                        intent.putExtra("lesson", "/storage/emulated/0/englishclass/lesson11");
                        startActivity(intent);
                        break;
                    case R.id.button12:
                        intent.putExtra("login_school", login_school);
                        intent.putExtra("login_name", login_name);
                        intent.putExtra("token", token);
                        intent.putExtra("lesson", "/storage/emulated/0/englishclass/lesson12");
                        startActivity(intent);
                        break;
                }
            }
        };
        btn1.setOnClickListener(Listener);
        btn2.setOnClickListener(Listener);
        btn3.setOnClickListener(Listener);
        btn4.setOnClickListener(Listener);
        btn5.setOnClickListener(Listener);
        btn6.setOnClickListener(Listener);
        btn7.setOnClickListener(Listener);
        btn8.setOnClickListener(Listener);
        btn9.setOnClickListener(Listener);
        btn10.setOnClickListener(Listener);
        btn11.setOnClickListener(Listener);
        btn12.setOnClickListener(Listener);


    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // READ_EXTERNAL_STORAGE 에 대한 권한 획득.
//                getAudioListFromMediaDatabase();
        }
        switch (requestCode) {
            case REQUEST_AUDIO_PERMISSION_CODE:
                if (grantResults.length > 0) {
                    boolean permissionToRecord = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean permissionToStore = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (permissionToRecord && permissionToStore) {
                        Toast.makeText(getApplicationContext(), "Permission Granted", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Permission Denied", Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }

//레벨 다이얼로그
    private  void levelDialog(String level_num){
        level_dialog = new Dialog(this);
        level_dialog.setContentView(R.layout.level_dialog);

        ImageButton level1_btn = (ImageButton) level_dialog.findViewById(R.id.level1_btn);
        ImageButton level2_btn = (ImageButton) level_dialog.findViewById(R.id.level2_btn);
        ImageButton level3_btn = (ImageButton) level_dialog.findViewById(R.id.level3_btn);
        ImageButton level4_btn = (ImageButton) level_dialog.findViewById(R.id.level4_btn);

        TextView leveldialog_text = (TextView) level_dialog.findViewById(R.id.leveldialog_text);
        leveldialog_text.setText(level_num);

        View.OnClickListener leveldialog_listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(v.getContext(), level.class);
                switch (v.getId()) {
                    case R.id.level1_btn:

                        intent.putExtra("lv_num", "0");
                        intent.putExtra("login_school", login_school);
                        intent.putExtra("login_name", login_name);
                        intent.putExtra("token", token);
                        startActivity(intent);
                        lesson_dialog.dismiss();
                        break;
                    case R.id.level2_btn:
                        intent.putExtra("lv_num", "1");
                        startActivity(intent);
                        lesson_dialog.dismiss();
                        break;
                    case R.id.level3_btn:
                        intent.putExtra("lv_num", "2");
                        startActivity(intent);
                        lesson_dialog.dismiss();
                        break;
                    case R.id.level4_btn:
                        intent.putExtra("lv_num", "3");
                        startActivity(intent);
                        lesson_dialog.dismiss();
                        break;

                }
            }

        };
        level1_btn.setOnClickListener(leveldialog_listener);
        level2_btn.setOnClickListener(leveldialog_listener);
        level3_btn.setOnClickListener(leveldialog_listener);
        level4_btn.setOnClickListener(leveldialog_listener);


        level_dialog.show();
    }
//레슨 다이얼로그
    private void lessonDialog(String lesson_num) {
        lesson_dialog = new Dialog(this);
        lesson_dialog.setContentView(R.layout.lesson_dialog);

        ImageButton lookandlisten_btn = (ImageButton) lesson_dialog.findViewById(R.id.lookandlisten_btn);
        ImageButton lookandsay_btn = (ImageButton) lesson_dialog.findViewById(R.id.lookandsay_btn);
        ImageButton listenandrepeat_btn = (ImageButton) lesson_dialog.findViewById(R.id.listenandrepeat_btn);
        ImageButton letsread_btn = (ImageButton) lesson_dialog.findViewById(R.id.letsread_btn);
        TextView lessondialog_text = (TextView) lesson_dialog.findViewById(R.id.lessondialog_text);
        lessondialog_text.setText(lesson_num);

        View.OnClickListener lessondialog_listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.lookandlisten_btn:
                        levelDialog("Lesson "+lesson+" - Look And Listen");
                        lesson_type = "ll";
                        break;
                    case R.id.lookandsay_btn:
                        levelDialog("Lesson "+lesson+" - Look And Say");
                        lesson_type = "ls";
                        break;
                    case R.id.listenandrepeat_btn:
                        levelDialog("Lesson "+lesson+" - Listen And Repeat");
                        lesson_type = "lr";
                        break;
                    case R.id.letsread_btn:
                        levelDialog("Lesson "+lesson+" - Let's Read");
                        lesson_type = "let";
                        break;
                }
            }

        };
        lookandlisten_btn.setOnClickListener(lessondialog_listener);
        lookandsay_btn.setOnClickListener(lessondialog_listener);
        listenandrepeat_btn.setOnClickListener(lessondialog_listener);
        letsread_btn.setOnClickListener(lessondialog_listener);

        lesson_dialog.show();
    }//레슨다이얼로그 끝
        private void testResolver () {
            String folder = "englishclass/record";

            Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
//               String folder = "/storage/emulated/0/Music";
            String[] projection = new String[]{
                    MediaStore.Audio.Media._ID,
                    MediaStore.Audio.Media.TITLE,
                    MediaStore.Audio.Media.ARTIST,
                    MediaStore.Audio.Media.ALBUM,
                    MediaStore.Audio.Media.ALBUM_ID,
                    MediaStore.Audio.Media.DURATION,
                    MediaStore.Audio.Media.DATA
            };
//쿼리를 위한 조건을 담는 부분 ? 한개당 1개의 아규먼트가 적용된다.
//해당폴더는 검색하고 하위폴더는 제외하는 내용
//        String selection = MediaStore.Audio.Media.DATA + " LIKE ? AND " + MediaStore.Audio.Media.DATA + " NOT LIKE ? ";
            String selection = MediaStore.Audio.Media.DATA + " LIKE ? ";
// 원래는 미디어 ismusic 값이 1인 것(음악파일)은 모두 검색하는 조건이 들어갔었다
//                String selection = MediaStore.Audio.Media.IS_MUSIC + " = 1";

            String[] selectionArgs = new String[]{
                    "%" + folder + "%",
//                "%" + folder + "/%/%"
            };
            Log.e("겟리스트", "폴더" + folder);
            String sortOrder = MediaStore.Audio.Media.TITLE + " COLLATE LOCALIZED ASC";

            Cursor cur = getContentResolver().query(uri, projection, selection, selectionArgs, sortOrder);
            while (cur.moveToNext()) {
                Log.e("폴더안 파일 타이틀", "Title:" + cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.TITLE)));
            }
            Log.e("count", cur.getCount() + "");

            cur.close();
        }
    }

