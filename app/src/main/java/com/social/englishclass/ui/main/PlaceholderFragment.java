package com.social.englishclass.ui.main;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.social.englishclass.R;
import com.social.englishclass.SelectLesson;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlaceholderFragment extends Fragment implements SurfaceHolder.Callback {

    private static final String ARG_SECTION_NUMBER = "section_number";

    private PageViewModel pageViewModel;
    private SurfaceView surfaceView;
    SurfaceHolder surfaceHolder;
    MediaPlayer mediaPlayer;
    private String filepath;
    private static String lt;
    private Button videostart_btn;
    private Spinner spinner;
    private ImageButton mBtnPlayPause;
    ArrayList<String> arrayList;
    private  ArrayAdapter<String> arrayAdapter;
    private float f;
    private static boolean pause=false;
    private View root = null;
    private static boolean isPrepared ;

    public static PlaceholderFragment newInstance(int index) {
        String ln = SelectLesson.lesson;
        lt = SelectLesson.lesson_type;
        PlaceholderFragment fragment = new PlaceholderFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        bundle.putString(ln+lt+"lv"+index, "/storage/emulated/0/englishclass/lesson/l"+ln+"_"+lt+"_"+"lv"+index+".mp4");
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        pageViewModel = ViewModelProviders.of(this).get(PageViewModel.class);
        int index = 1;
        if (getArguments() != null) {
            index = getArguments().getInt(ARG_SECTION_NUMBER);
            Log.e("MyTag","인덱스 번호  : " +index);
            String ln = SelectLesson.lesson;
            switch (index){
                case 1:
                    filepath = getArguments().getString(ln+lt+"lv1");
                    break;
                case 2:
                    filepath = getArguments().getString(ln+lt+"lv2");
                    break;
                case 3:
                    filepath = getArguments().getString(ln+lt+"lv3");
                    break;
                case 4:
                    filepath = getArguments().getString(ln+lt+"lv4");
                    break;
                case 5:
                    filepath = getArguments().getString(ln+lt+"lv5");
                    break;
            }

        }
        pageViewModel.setIndex(index);

    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        int pos = getArguments().getInt(ARG_SECTION_NUMBER);
        Log.e("MyTag","pos 번호  : " +pos);
//        View root = null;
        root = inflater.inflate(R.layout.fragment_level, container, false);
//        if(pos==1)root = inflater.inflate(R.layout.fragment_level, container, false);
//        else if(pos==2)root = inflater.inflate(R.layout.fragment_level, container, false);
//        else if(pos==3)root = inflater.inflate(R.layout.fragment_level, container, false);
//        else if(pos==4)root = inflater.inflate(R.layout.fragment_level, container, false);
//        else if(pos==5)root = inflater.inflate(R.layout.fragment_level, container, false);
        surfaceView = root.findViewById(R.id.surfaceView);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        speedselect_server();
        mBtnPlayPause = (ImageButton) root.findViewById(R.id.videoplay_btn_play_pause);

        mBtnPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer.isPlaying() && pause == false) {
                    Log.e("정지  ", "" + pause);
                    mediaPlayer.pause();
                    pause = true;
                    updateUI();
                }else if(pause == true){
                    Log.e("재생  ", "" + pause);
                    play(f);
                    pause = false;
                    updateUI();
                }
                else {
                    Log.e("파이어베이스 불러오기  ", "");
                    mediaPlayer.start();
                    updateUI();
//                    getaudiourl(filename + ext);
                }
            }
        });

        final TextView textView = root.findViewById(R.id.section_label);
        pageViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;



    }
    //플레이버튼 ui 업데이트
    private void updateUI() {
        if (mediaPlayer.isPlaying()) {

            mBtnPlayPause.setImageResource(R.drawable.pause);
        } else {
            mBtnPlayPause.setImageResource(R.drawable.play);
        }
    }

    //스피너 선택버튼 만들기
    private void speedselect_server() {
        arrayList = new ArrayList<>();
        arrayList.add("재생속도");
        arrayList.add("0.5배속");
        arrayList.add("0.75배속");
        arrayList.add("1배속");
        arrayList.add("1.25배속");
        arrayList.add("1.5배속");

        arrayAdapter = new ArrayAdapter<>(root.getContext(),
                android.R.layout.simple_spinner_dropdown_item, arrayList);

        spinner = (Spinner) root.findViewById(R.id.videoplay_spinner);
        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override

            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {


                switch (arrayList.get(i)) {
                    case "0.5배속":
                        f = (float) 0.5;
                        play(f);
                        break;
                    case "0.75배속":
                        f = (float) 0.75;
                        play(f);
                        break;
                    case "1배속":
                        f = (float) 1;
                        play(f);
                        break;
                    case "1.25배속":
                        f = (float) 1.25;
                        play(f);
                        break;
                    case "1.5배속":
                        f = (float) 1.5;
                        play(f);
                        break;

                }
                //               Toast.makeText(getApplicationContext(),arrayList.get(i)+"가 선택되었습니다. f값은 " + f, Toast.LENGTH_SHORT).show();


            }

            @Override

            public void onNothingSelected(AdapterView<?> adapterView) {

            }

        });
    }
    //재생속도 변경
    public void play(float a) {


        if (isPrepared) {

            mediaPlayer.setPlaybackParams((mediaPlayer.getPlaybackParams().setSpeed(a)));
            mediaPlayer.start();

        }
    }

//서피스뷰 동작
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.e("MyTag","surfaceCreated");

        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
        } else {
            mediaPlayer.reset();
        }

        try {


            mediaPlayer.setDataSource(filepath);

            //mediaPlayer.setVolume(0, 0); //볼륨 제거
            mediaPlayer.setDisplay(surfaceHolder); // 화면 호출
            mediaPlayer.prepare(); // 비디오 load 준비
            isPrepared = true;
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    if (mediaPlayer != null) {
                        isPrepared = false;


                        mediaPlayer.reset();

                        updateUI();
                        speedselect_server();
                    }

                }
            }); // 비디오 재생 완료 리스너

//            mediaPlayer.start();

        } catch (Exception e) {
            Log.e("MyTag","surface view error : " + e.getMessage());
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

        Log.d("MyTag","surfaceChanged");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        Log.e("MyTag","surfaceDestroyed");
        if (mediaPlayer != null) {
            isPrepared = false;
            mediaPlayer.release();
        }
    }
}