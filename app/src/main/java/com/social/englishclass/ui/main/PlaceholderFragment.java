package com.social.englishclass.ui.main;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.social.englishclass.R;

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
    private Button videostart_btn;

    public static PlaceholderFragment newInstance(int index) {
        PlaceholderFragment fragment = new PlaceholderFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
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
            filepath = getArguments().getString("l1lv1");
        }
        pageViewModel.setIndex(index);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        int pos = getArguments().getInt(ARG_SECTION_NUMBER);
        View root = null;
        root = inflater.inflate(R.layout.fragment_level, container, false);
//        if(pos==1)root = inflater.inflate(R.layout.fragment_level, container, false);
//        else if(pos==2)root = inflater.inflate(R.layout.fragment_level, container, false);
//        else if(pos==3)root = inflater.inflate(R.layout.fragment_level, container, false);
//        else if(pos==4)root = inflater.inflate(R.layout.fragment_level, container, false);
//        else if(pos==5)root = inflater.inflate(R.layout.fragment_level, container, false);
        surfaceView = root.findViewById(R.id.surfaceView);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        videostart_btn = (Button) root.findViewById(R.id.videostart_btn);
        videostart_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.start();
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

//       SurfaceView sur = new SurfaceView(root.getContext());

    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.e("MyTag","surfaceCreated");

        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
        } else {
            mediaPlayer.reset();
        }

        try {

            String path = "/storage/emulated/0/englishclass/lesson1/l1lv1.mp4";
            mediaPlayer.setDataSource(filepath);

            //mediaPlayer.setVolume(0, 0); //볼륨 제거
            mediaPlayer.setDisplay(surfaceHolder); // 화면 호출
            mediaPlayer.prepare(); // 비디오 load 준비

            //mediaPlayer.setOnCompletionListener(completionListener); // 비디오 재생 완료 리스너

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
            mediaPlayer.release();
        }
    }
}