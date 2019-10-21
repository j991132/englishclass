package com.social.englishclass;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

public class englishlesson extends AppCompatActivity implements View.OnClickListener {

    private final static int LOADER_ID = 0x001;
    public float f;
    private String TAG = "activity_englishlesson";
    private RecyclerView mRecyclerView;
    private AudioAdapter mAdapter;
    private ImageView mImgAlbumArt;
    private TextView mTxtTitle;
    private ImageButton mBtnPlayPause;
   


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_englishlesson);

// OS가 Marshmallow 이상일 경우 권한체크를 해야 합니다.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1000);
            } else {
                // READ_EXTERNAL_STORAGE 에 대한 권한이 있음.
                getAudioListFromMediaDatabase();
            }
        }
        // OS가 Marshmallow 이전일 경우 권한체크를 하지 않는다.
        else {
            getAudioListFromMediaDatabase();
        }

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        mAdapter = new AudioAdapter(this, null);
        mRecyclerView.setAdapter(mAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);

        mImgAlbumArt = (ImageView) findViewById(R.id.img_albumart);
        mTxtTitle = (TextView) findViewById(R.id.txt_title);
        mBtnPlayPause = (ImageButton) findViewById(R.id.btn_play_pause);
        findViewById(R.id.lin_miniplayer).setOnClickListener(this);
        findViewById(R.id.btn_rewind).setOnClickListener(this);
        mBtnPlayPause.setOnClickListener(this);
        findViewById(R.id.btn_forward).setOnClickListener(this);

        registerBroadcast();
        updateUI();


    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // READ_EXTERNAL_STORAGE 에 대한 권한 획득.
            getAudioListFromMediaDatabase();
        }
    }



    private void getAudioListFromMediaDatabase() {
        getSupportLoaderManager().initLoader(LOADER_ID, null, new LoaderManager.LoaderCallbacks<Cursor>() {
            @Override
            public Loader<Cursor> onCreateLoader(int id, Bundle args) {
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
                String selection = MediaStore.Audio.Media.IS_MUSIC + " = 1";
                String sortOrder = MediaStore.Audio.Media.TITLE + " COLLATE LOCALIZED ASC";
                return new CursorLoader(getApplicationContext(), uri, projection, selection, null, sortOrder);
            }

            @Override
            public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
                mAdapter.swapCursor(data);
                if (data != null && data.getCount() > 0) {
                    while (data.moveToNext()) {
                        Log.i(TAG, "Title:" + data.getString(data.getColumnIndex(MediaStore.Audio.Media.TITLE)));
                    }
                }
            }

            @Override
            public void onLoaderReset(Loader<Cursor> loader) {
                mAdapter.swapCursor(null);
            }
        });
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateUI();
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterBroadcast();
    }

    @Override
    public void onClick(View v) {
        CheckBox btncheckBox = (CheckBox) findViewById(R.id.checkBox);

        if(btncheckBox.isChecked()){
           f= (float)0.5;
 //           Toast.makeText(getApplicationContext(), "재생속도 f 값은   "+f, Toast.LENGTH_SHORT).show();
        }
        else{
             f= (float)1;
 //                      Toast.makeText(getApplicationContext(), "재생속도 f 값은   "+f, Toast.LENGTH_SHORT).show();
        }
        switch (v.getId()) {
            case R.id.lin_miniplayer:
                // 플레이어 화면으로 이동할 코드가 들어갈 예정
                break;
            case R.id.btn_rewind:
                // 이전곡으로 이동
                AudioApplication.getInstance().getServiceInterface().rewind();
                break;
            case R.id.btn_play_pause:
                // 재생 또는 일시정지
                AudioApplication.getInstance().getServiceInterface().togglePlay();
                break;
            case R.id.btn_forward:
                // 다음곡으로 이동
                AudioApplication.getInstance().getServiceInterface().forward();
                break;

        }





    }
        public void registerBroadcast(){
            IntentFilter filter = new IntentFilter();
            filter.addAction(BroadcastActions.PLAY_STATE_CHANGED);
            registerReceiver(mBroadcastReceiver, filter);
        }

        public void unregisterBroadcast(){
            unregisterReceiver(mBroadcastReceiver);
        }

        private void updateUI() {
            if (AudioApplication.getInstance().getServiceInterface().isPlaying()) {
                mBtnPlayPause.setImageResource(R.drawable.pause);
            } else {
                mBtnPlayPause.setImageResource(R.drawable.play);
            }
            AudioAdapter.AudioItem audioItem = AudioApplication.getInstance().getServiceInterface().getAudioItem();
            if (audioItem != null) {
                Uri albumArtUri = ContentUris.withAppendedId(Uri.parse("content://media/external/audio/albumart"), audioItem.mAlbumId);
                Picasso.with(getApplicationContext()).load(albumArtUri).error(R.drawable.empty_albumart).into(mImgAlbumArt);
                mTxtTitle.setText(audioItem.mTitle);
            } else {
                mImgAlbumArt.setImageResource(R.drawable.empty_albumart);
                mTxtTitle.setText("재생중인 음악이 없습니다.");
            }
        }

    }//메인 종료
