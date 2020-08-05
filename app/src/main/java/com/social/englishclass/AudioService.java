package com.social.englishclass;


import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

public class AudioService extends Service {
    private final IBinder mBinder = new AudioServiceBinder();
    private ArrayList<Long> mAudioIds = new ArrayList<>();
    private MediaPlayer mMediaPlayer;
    private MediaRecorder mRecorder;
    private static final String LOG_TAG = "AudioRecording";
    private boolean isPrepared;
    private int mCurrentPosition;
    private AudioAdapter.AudioItem mAudioItem;
    private float f;
    private static String mFileName = null;
    int lenSpeech = 0;
    boolean isRecording = false;
    boolean forceStop = false;
    int maxLenSpeech = 16000 * 45;
    byte[] speechData = new byte[maxLenSpeech * 2];
    byte[] speechData2;
    public Thread mRecordThread = null;
    public Thread mPlayThread = null;
    private int mAudioSource = MediaRecorder.AudioSource.MIC;
    private int mSampleRate = 16000;
    private int mChannelCount = AudioFormat.CHANNEL_OUT_MONO;
    private int mAudioFormat = AudioFormat.ENCODING_PCM_16BIT;
    private int mBufferSize = AudioTrack.getMinBufferSize(mSampleRate, mChannelCount, mAudioFormat);
    public AudioTrack mAudioTrack = null;
    public boolean isPlaying = false;
    private String ext;

    public class AudioServiceBinder extends Binder {
        AudioService getService() {
            return AudioService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

//녹음스레드

        File recordpath = new File("/storage/emulated/0/englishclass/record/");
        if (!recordpath.exists()){
            recordpath.mkdirs();
        }
        mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        mFileName += "/englishclass/record/AudioRecording.3gp";

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
                mAudioItem = null;
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
            Log.d("음악파일 위치", mAudioItem.mDataPath);


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
        } catch (Exception e) {
            e.printStackTrace();
        }  // englishlesson 액티비티 종료시 null 에러를 잡기위해 예외문 추가
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

    public void recordname_sub_play(File file){
        try {
            mMediaPlayer.setDataSource(file.getPath());
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.prepareAsync();
        } catch (Exception e) {
            e.printStackTrace();
        }
        mMediaPlayer.start();
    }
    public void pcmplay(){
        String filename = Environment.getExternalStorageDirectory().getAbsolutePath()+"/englishclass/record/KeywordRecording.pcm";
        isPlaying = true;
        playthread(filename);
    }
    public void play(int position) {

        queryAudioItem(position);
        ext = mAudioItem.mDataPath.substring(mAudioItem.mDataPath.length()-3, mAudioItem.mDataPath.length());

        Log.e("선택된 음악파일 확장자", ""+ext);

        stop();
        if(ext.equals("pcm")) {
            Log.e("선택된 음악파일", ""+mAudioItem.mDataPath);
//            if (mMediaPlayer != null) {
//                mMediaPlayer.release();
//                mMediaPlayer=null;

//            }
            isPlaying = true;
            playthread(mAudioItem.mDataPath);
//            a(mAudioItem.mDataPath);
        }else {
//            mPlayThread.interrupt();
            prepare();

            Log.e("선택된 음악파일2", ""+mAudioItem.mDataPath);


        }



        //AudioApplication.getInstance().getServiceInterface().isPlaying();
        //          sendBroadcast(new Intent(BroadcastActions.PLAY_STATE_CHANGED)); // 재생상태 변경 전송

    }

    public void play() {


    if (isPrepared) {
        Log.e("선택된 음악파일1", ""+mAudioItem.mDataPath);
        mMediaPlayer.start();




}
        sendBroadcast(new Intent(BroadcastActions.PLAY_STATE_CHANGED)); // 재생상태 변경 전송
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
    public void record3gp() {
        initAudioRecorder();
        mRecorder.start();
    }


    public void record() {

   /*
        //영어발음평가 전송용 녹음파일 만들기
        if (isRecording) {
            forceStop = true;
        } else {
            try {
                new Thread(new Runnable() {
                    public void run() {

                        try {
                            recordSpeech();

                        } catch (RuntimeException e) {

                            return;
                        }

                        Thread threadRecog = new Thread(new Runnable() {
                            public void run() {
                                result = sendDataAndGetResult();
                            }
                        });
                        threadRecog.start();
                        try {
                            threadRecog.join(20000);
                            if (threadRecog.isAlive()) {
                                threadRecog.interrupt();

                            } else {

                            }
                        } catch (InterruptedException e) {

                        }

                    }
                }).start();
            } catch (Throwable t) {
//                textResult.setText("ERROR: " + t.toString());
                forceStop = false;
                isRecording = false;
            }
        }
        */
//        initAudioRecorder();
//        if (mRecordThread.isAlive()) {
//            mRecordThread.interrupt();
//            Log.e("스레드 인터럽트", "중단");
//        }
//        Log.e("스레드 상태1", ""+mRecordThread.getState());
        recordtthread();
        Log.e("스레드 상태2", ""+mRecordThread.getState());
//        if(mRecordThread.getState() == Thread.State.NEW){
//        mRecordThread.start();}

//        mRecorder.start();


        Toast.makeText(getApplicationContext(), "녹음 시작", Toast.LENGTH_LONG).show();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void recordpause() {
        mRecorder.pause();
        Toast.makeText(getApplicationContext(), "녹음 일시정지", Toast.LENGTH_LONG).show();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void recordresume() {
        mRecorder.resume();
        Toast.makeText(getApplicationContext(), "녹음 재개", Toast.LENGTH_LONG).show();
    }
    public void recrecordstop() {
        try {
            isRecording = false;
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;

        } catch (Exception e) {
            e.printStackTrace();
        }


 //       Toast.makeText(getApplicationContext(), "Recording Stopped", Toast.LENGTH_LONG).show();
    }
    public void recordstop() {
        forceStop = true;

//        isRecording = false;
//        mRecorder.stop();
//        mRecorder.release();
//        mRecorder = null;

        Toast.makeText(getApplicationContext(), "Recording Stopped", Toast.LENGTH_LONG).show();
    }

    public void recordplay(String fname) {
        mMediaPlayer = new MediaPlayer();
        try {
            mMediaPlayer.setDataSource(fname);
            mMediaPlayer.prepare();
            mMediaPlayer.start();
            Toast.makeText(getApplicationContext(), "Recording Started Playing", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
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
        if (mMediaPlayer != null) {
            //           mMediaPlayer.release();    //객체를 파괴하여 다시 못씀
            mMediaPlayer.reset();        //객체를 처음으로 되돌려 다시 쓸 수 있음
//            mMediaPlayer = null;
            Toast.makeText(getApplicationContext(), "Playing Audio Stopped", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "Playing Audio Stopped", Toast.LENGTH_SHORT).show();
        }
    }

    public void deletedialog(int position) {
        queryAudioItem(position);

        Intent intent = new Intent(BroadcastActions.DELETE_DIALOG);
        intent.putExtra("filenamevalue", mAudioItem.mTitle);
        intent.putExtra("filepathvalue", mAudioItem.mDataPath);
        sendBroadcast(intent);
        Log.e("다이얼로그 출력시 타이틀 정보", " " + mAudioItem.mTitle);
        Log.e("다이얼로그 출력시 파일경로 정보", " " + mAudioItem.mDataPath);

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
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mRecorder.setOutputFile(mFileName);
        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }

    }



        public void recordSpeech() throws RuntimeException {
            try {
                int bufferSize = AudioRecord.getMinBufferSize(
                        16000, // sampling frequency
                        AudioFormat.CHANNEL_IN_MONO,
                        AudioFormat.ENCODING_PCM_16BIT);
                AudioRecord audio = new AudioRecord(
                        MediaRecorder.AudioSource.VOICE_RECOGNITION,
                        16000, // sampling frequency
                        AudioFormat.CHANNEL_IN_MONO,
                        AudioFormat.ENCODING_PCM_16BIT,
                        bufferSize);
                lenSpeech = 0;
                if (audio.getState() != AudioRecord.STATE_INITIALIZED) {
                    throw new RuntimeException("ERROR: Failed to initialize audio device. Allow app to access microphone");
                }
                else {
                    short [] inBuffer = new short [bufferSize];
//                    speechData2 = new byte[inBuffer.length*2];
                    forceStop = false;
                    isRecording = true;
                    audio.startRecording();
//                    try {
//                        String filename = Environment.getExternalStorageDirectory().getAbsolutePath()+"/englishclass/record/AudioRecording.pcm";
//                        OutputStream  os  = new FileOutputStream(filename);
                    while (!forceStop) {
                        int ret = audio.read(inBuffer, 0, bufferSize);

                        for (int i = 0; i < ret ; i++ ) {
                            if (lenSpeech >= maxLenSpeech) {
                                forceStop = true;
                                break;
                            }
                            speechData[lenSpeech*2] = (byte)(inBuffer[i] & 0x00FF);
                            speechData[lenSpeech*2+1] = (byte)((inBuffer[i] & 0xFF00) >> 8);
//                            speechData2[lenSpeech*2] = (byte)(inBuffer[i] & 0x00FF);
//                            speechData2[lenSpeech*2+1] = (byte)((inBuffer[i] & 0xFF00) >> 8);
                            lenSpeech++;
                        }
//                        os.write(speechData);
                    }
//                        os.close();
//                    } catch (FileNotFoundException e) {
//                        e.printStackTrace();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
                    String  audioContents = Base64.encodeToString(
                            speechData, 0, lenSpeech*2, Base64.NO_WRAP);
                    File savefolder = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/englishclass/record");
                    if (!savefolder.exists()){
                        savefolder.mkdir();
                    }
                    String sendtestfileuri = Environment.getExternalStorageDirectory().getAbsolutePath()+"/englishclass/record/sendtest.txt";
                    String filename = Environment.getExternalStorageDirectory().getAbsolutePath()+"/englishclass/record/AudioRecording.pcm";


                    try {
                        OutputStream  os  = new FileOutputStream(filename);
// 말한길이만큼 파일에 써준다
                        os.write(speechData,0,lenSpeech*2);
                        os.close();

                        BufferedWriter buf = new BufferedWriter(new FileWriter(sendtestfileuri, true));

                        buf.append(audioContents); // 파일 쓰기
                        buf.newLine(); // 개행
                        buf.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    audio.stop();
                    audio.release();
                    isRecording = false;
                }
            } catch(Throwable t) {
                throw new RuntimeException(t.toString());
            }
        }
//        녹음 스레드
    public void recordtthread(){
        mRecordThread = new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    recordSpeech();

                } catch (RuntimeException e) {

                    return;
                }

            }
        });
        mRecordThread.start();
    }
    //오디오트랙 재생 스레드
    public void playthread(final String mFile){

        new Thread(new Runnable() {
            @Override
            public void run() {
                mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, mSampleRate, mChannelCount, mAudioFormat, mBufferSize, AudioTrack.MODE_STREAM);

                byte[] writeData = new byte[mBufferSize];
                FileInputStream fis = null;
                try {
                    fis = new FileInputStream(mFile);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                DataInputStream dis = new DataInputStream(fis);
                mAudioTrack.play();

                while (isPlaying) {
                    try {
                        int ret = dis.read(writeData, 0, mBufferSize);
                        Log.e("오디오트랙", "ret"+ret);
                        if (ret <= 0) {
                            Log.e("오디오트랙", "ret2"+ret);
                            Log.e("오디오트랙", "재생중");
//                            (englishlesson.class).runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
                            isPlaying = false;

//                                }
//                            });

                            break;
                        }
                        mAudioTrack.write(writeData, 0, ret);
                        Log.e("오디오트랙", "ret3"+ret);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
                Log.e("오디오트랙", "와일문 종료");

                mAudioTrack.stop();
                mAudioTrack.release();
                mAudioTrack = null;
                Log.e("오디오트랙", "널");

                try {
                    dis.close();
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    }

