package com.social.englishclass.ui.main;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.social.englishclass.R;

public class Surface extends SurfaceView {
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private MediaPlayer mediaPlayer;

    public Surface(Context context) {
        super(context);
        surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        surfaceHolder = getHolder();
        surfaceHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                if (mediaPlayer == null) {
                    mediaPlayer = new MediaPlayer();
                } else {
                    mediaPlayer.reset();
                }

                try {

                    String path = "/storage/emulated/0/englishclass/lesson1/l1lv1.mp4";
                    mediaPlayer.setDataSource(path);

                    //mediaPlayer.setVolume(0, 0); //볼륨 제거
                    mediaPlayer.setDisplay(surfaceHolder); // 화면 호출
                    mediaPlayer.prepare(); // 비디오 load 준비

                    //mediaPlayer.setOnCompletionListener(completionListener); // 비디오 재생 완료 리스너

                    mediaPlayer.start();

                } catch (Exception e) {
                    Log.e("MyTag","surface view error : " + e.getMessage());
                }

            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                Log.e("MyTag","surface Changed");
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                Log.e("MyTag","surfaceDestroyed");
                if (mediaPlayer != null) {
                    mediaPlayer.release();
                }
            }
        });
    }


}
