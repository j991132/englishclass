package com.social.englishclass;


import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import java.io.IOException;
import java.util.ArrayList;

public class AudioService extends Service {
    private final IBinder mBinder = new AudioServiceBinder();
    private ArrayList<Long> mAudioIds = new ArrayList<>();
    private MediaPlayer mMediaPlayer;
    private MediaRecorder mRecorder ;
    private static final String LOG_TAG = "AudioRecording" ;
    private boolean isPrepared;
    private int mCurrentPosition;
    private AudioAdapter.AudioItem mAudioItem;
    private float f;
    private static String mFileName = null ;


    public class AudioServiceBinder extends Binder {
        AudioService getService() {
            return AudioService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        mFileName += "/englishclass/record/AudioRecording.3gp" ;

 //      Intent intent = getIntent();
//    String filename = (String)intent.getExtras().get("filename");

        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                isPrepared = true;
                mp.start();
             sendBroadcast(new Intent(BroadcastActions.PREPARED)); // prepared 전송
                sendBroadcast(new Intent(BroadcastActions.PLAY_STATE_CHANGED)); // 재생상태 변경 전송
            }
        });
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                isPrepared = false;
                sendBroadcast(new Intent(BroadcastActions.PLAY_STATE_CHANGED)); // 재생상태 변경 전송
            }
        });
        mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                isPrepared = false;
                sendBroadcast(new Intent(BroadcastActions.PLAY_STATE_CHANGED)); // 재생상태 변경 전송
                return false;
            }
        });
        mMediaPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
            @Override
            public void onSeekComplete(MediaPlayer mp) {

            }
        });
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    private void queryAudioItem(int position) {
        mCurrentPosition = position;
        long audioId = mAudioIds.get(position);
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection = new String[]{
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.ALBUM_ID,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.DATA
        };
        String selection = MediaStore.Audio.Media._ID + " = ?";
        String[] selectionArgs = {String.valueOf(audioId)};
        Cursor cursor = getContentResolver().query(uri, projection, selection, selectionArgs, null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                mAudioItem = AudioAdapter.AudioItem.bindCursor(cursor);
            }
            cursor.close();
        }
    }

    private void prepare() {
        try {
            Log.d("음악파일 위치",mAudioItem.mDataPath );


            mMediaPlayer.setDataSource(mAudioItem.mDataPath);
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.prepareAsync();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        try {


        mMediaPlayer.stop();
        mMediaPlayer.reset();
        }catch (Exception e){e.printStackTrace();}  // englishlesson 액티비티 종료시 null 에러를 잡기위해 예외문 추가
    }

    public void setPlayList(ArrayList<Long> audioIds) {
        if (!mAudioIds.equals(audioIds)) {
            mAudioIds.clear();
            mAudioIds.addAll(audioIds);
        }
    }
    public void clearPlayList() {

            mAudioIds.clear();

    }
    public void play(int position) {

        queryAudioItem(position);
        stop();
        prepare();

        //AudioApplication.getInstance().getServiceInterface().isPlaying();
  //          sendBroadcast(new Intent(BroadcastActions.PLAY_STATE_CHANGED)); // 재생상태 변경 전송

    }
    public void play() {



        if (isPrepared) {

            mMediaPlayer.start();
            sendBroadcast(new Intent(BroadcastActions.PLAY_STATE_CHANGED)); // 재생상태 변경 전송
        }
    }
    public void play2(float a) {



        if (isPrepared) {



            mMediaPlayer.setPlaybackParams((mMediaPlayer.getPlaybackParams().setSpeed(a)));
            mMediaPlayer.start();
            sendBroadcast(new Intent(BroadcastActions.PLAY_STATE_CHANGED)); // 재생상태 변경 전송
        }
    }

    public void pause() {
        if (isPrepared) {
            mMediaPlayer.pause();
            sendBroadcast(new Intent(BroadcastActions.PLAY_STATE_CHANGED)); // 재생상태 변경 전송
        }
    }



    public void forward() {
        if (mAudioIds.size() - 1 > mCurrentPosition) {
            mCurrentPosition++; // 다음 포지션으로 이동.
        } else {
            mCurrentPosition = 0; // 처음 포지션으로 이동.
        }
        play(mCurrentPosition);
    }

    public void rewind() {
        if (mCurrentPosition > 0) {
            mCurrentPosition--; // 이전 포지션으로 이동.
        } else {
            mCurrentPosition = mAudioIds.size() - 1; // 마지막 포지션으로 이동.
        }
        play(mCurrentPosition);
    }

    public void record() {
        initAudioRecorder();
        mRecorder .start();
        Toast.makeText(getApplicationContext(), "녹음 시작" , Toast. LENGTH_LONG ).show();
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void recordpause() {
        mRecorder .pause();
        Toast.makeText(getApplicationContext(), "녹음 일시정지" , Toast. LENGTH_LONG ).show();
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void recordresume() {
        mRecorder .resume();
        Toast.makeText(getApplicationContext(), "녹음 재개" , Toast. LENGTH_LONG ).show();
    }

    public void recordstop() {
        mRecorder .stop();

        mRecorder .release();
        mRecorder = null ;

        Toast.makeText(getApplicationContext(), "Recording Stopped" , Toast. LENGTH_LONG ).show();
    }

    public void recordplay(String fname) {
        mMediaPlayer = new MediaPlayer();
        try {
            mMediaPlayer .setDataSource( fname );
            mMediaPlayer .prepare();
            mMediaPlayer .start();
            Toast.makeText(getApplicationContext(), "Recording Started Playing" , Toast. LENGTH_LONG ).show();
        } catch (IOException e) {
            Log.e( LOG_TAG , "prepare() failed" );
        }
//녹음재생 완료후 정지
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                recordstopplay();
            }
        });
    }

    public void recordstopplay() {
        if(mMediaPlayer !=null) {
 //           mMediaPlayer.release();    //객체를 파괴하여 다시 못씀
            mMediaPlayer.reset();        //객체를 처음으로 되돌려 다시 쓸 수 있음
//            mMediaPlayer = null;
            Toast.makeText(getApplicationContext(), "Playing Audio Stopped", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(getApplicationContext(), "Playing Audio Stopped", Toast.LENGTH_SHORT).show();
        }
    }

    public void deletedialog(int position){
        queryAudioItem(position);

        Intent intent = new Intent(BroadcastActions.DELETE_DIALOG);
        intent .putExtra("filenamevalue",mAudioItem.mTitle );
        sendBroadcast(intent);
        Log.e("다이얼로그 출력시 타이틀 정보", " "+mAudioItem.mTitle);
    }

    public AudioAdapter.AudioItem getAudioItem() {
        return mAudioItem;
    }

    public boolean isPlaying() {
        if (mMediaPlayer != null) {
            return mMediaPlayer.isPlaying();
        }
        return false;
    }
    public void initAudioRecorder() {
        mRecorder = new MediaRecorder();
        mRecorder .setAudioSource(MediaRecorder.AudioSource. MIC );
        mRecorder .setOutputFormat(MediaRecorder.OutputFormat. THREE_GPP );
        mRecorder .setAudioEncoder(MediaRecorder.AudioEncoder. AMR_NB );
        mRecorder .setOutputFile( mFileName );
        try {
            mRecorder .prepare();
        } catch (IOException e) {
            Log.e( LOG_TAG , "prepare() failed" );
        }
    }
}
