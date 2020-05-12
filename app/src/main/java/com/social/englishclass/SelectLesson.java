package com.social.englishclass;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class SelectLesson extends AppCompatActivity {

    private Intent intent;
    private String login_name, token, login_school;
    private Dialog lesson_dialog, level_dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_lesson);

        Intent login_intent = getIntent();
        login_school = login_intent.getStringExtra("login_school");
        login_name = login_intent.getStringExtra("login_name");
        token = login_intent.getStringExtra("token");

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
        intent = new Intent(this, englishlesson.class);

        View.OnClickListener Listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.button1:
                        intent.putExtra("login_school", login_school);
                        intent.putExtra("login_name", login_name);
                        intent.putExtra("token", token);
                        intent.putExtra("lesson", "/storage/emulated/0/englishclass/lesson1");
                        startActivity(intent);
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
//레벨 다이얼로그
    private  void levelDialog(){
        level_dialog = new Dialog(this);

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

                        break;
                    case R.id.lookandsay_btn:

                        break;
                    case R.id.listenandrepeat_btn:

                        break;
                    case R.id.letsread_btn:

                        break;
                }
            }

        };
        lookandlisten_btn.setOnClickListener(lessondialog_listener);
        lookandsay_btn.setOnClickListener(lessondialog_listener);
        listenandrepeat_btn.setOnClickListener(lessondialog_listener);
        letsread_btn.setOnClickListener(lessondialog_listener);
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

