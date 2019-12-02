package com.social.englishclass;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btn1 = (Button) findViewById(R.id.button1);
        Button btn2 = (Button) findViewById(R.id.button2);
        Button btn3 = (Button) findViewById(R.id.button3);
        Button btn4 = (Button) findViewById(R.id.button4);
        Button btn5 = (Button) findViewById(R.id.button5);
        Button btn6 = (Button) findViewById(R.id.button6);
        Button btn7 = (Button) findViewById(R.id.button7);
        Button btn8 = (Button) findViewById(R.id.button8);
        Button btn9 = (Button) findViewById(R.id.button9);
        Button btn10 = (Button) findViewById(R.id.button10);
        Button btn11 = (Button) findViewById(R.id.button11);
        Button btn12 = (Button) findViewById(R.id.button12);

        View.OnClickListener Listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.button1:
                        Intent intent = new Intent(MainActivity.this, englishlesson.class);
                        intent.putExtra("lesson", "/storage/emulated/0/englishclass/lesson1");
                        startActivity(intent);
                        break;
                    case R.id.button2:

                        break;
                    case R.id.button3:

                        break;
                    case R.id.button4:

                        break;
                    case R.id.button5:

                        break;
                    case R.id.button6:

                        break;
                    case R.id.button7:

                        break;
                    case R.id.button8:

                        break;
                    case R.id.button9:

                        break;
                    case R.id.button10:

                        break;
                    case R.id.button11:

                        break;
                    case R.id.button12:

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
}
