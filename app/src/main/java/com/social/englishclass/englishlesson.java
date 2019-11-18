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
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class englishlesson extends AppCompatActivity implements View.OnClickListener {

    private final static int LOADER_ID = 0x001;
    private Spinner spinner;
    private float f;
    ArrayList<String> arrayList;
    ArrayAdapter<String> arrayAdapter;

    private String TAG = "activity_englishlesson";
    private RecyclerView mRecyclerView;
    private AudioAdapter mAdapter;
    private ImageView mImgAlbumArt;
    private TextView mTxtTitle;
    private ImageButton mBtnPlayPause;
    private Button startbtn , stopbtn , playbtn, stopplay  ;
    public static final int REQUEST_AUDIO_PERMISSION_CODE = 1 ;

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
//녹음버튼 관련
        startbtn = (Button)findViewById(R.id. btnRecord );
        stopbtn = (Button)findViewById(R.id. btnStop );
        playbtn = (Button)findViewById(R.id. btnPlay );
        stopplay = (Button)findViewById(R.id.StopPlay);
        stopbtn .setEnabled( false );
        playbtn .setEnabled( false );
        stopplay .setEnabled( false );
        playbtn.setOnClickListener(this);
        stopbtn.setOnClickListener(this);
        startbtn.setOnClickListener(this);
        stopplay.setOnClickListener(this);
//녹음버튼 끝
        mImgAlbumArt = (ImageView) findViewById(R.id.img_albumart);
        mTxtTitle = (TextView) findViewById(R.id.txt_title);
        mBtnPlayPause = (ImageButton) findViewById(R.id.btn_play_pause);
        findViewById(R.id.lin_miniplayer).setOnClickListener(this);
        findViewById(R.id.btn_rewind).setOnClickListener(this);
        mBtnPlayPause.setOnClickListener(this);
        findViewById(R.id.btn_forward).setOnClickListener(this);
        arrayList = new ArrayList<>();
        arrayList.add("1배속");
        arrayList.add("0.5배속");
        arrayList.add("0.75배속");
        arrayList.add("1.25배속");
        arrayList.add("1.5배속");

        arrayAdapter = new ArrayAdapter<>(getApplicationContext(),
                android.R.layout.simple_spinner_dropdown_item,
                arrayList);

        spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override

            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {


                switch (arrayList.get(i)) {
                    case "0.5배속":
                        f = (float) 0.5;
                        break;
                    case "0.75배속":
                        f = (float) 0.75;
                        break;
                    case "1배속":
                        f = (float) 1;
                        break;
                    case "1.25배속":
                        f = (float) 1.25;
                        break;
                    case "1.5배속":
                        f = (float) 1.5;
                        break;

                }
 //               Toast.makeText(getApplicationContext(),arrayList.get(i)+"가 선택되었습니다. f값은 " + f, Toast.LENGTH_SHORT).show();



            }

            @Override

            public void onNothingSelected(AdapterView<?> adapterView) {

            }

        });



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
        switch (requestCode) {
            case REQUEST_AUDIO_PERMISSION_CODE :
                if (grantResults. length > 0 ) {
                    boolean permissionToRecord = grantResults[ 0 ] == PackageManager.  PERMISSION_GRANTED ;
                    boolean permissionToStore = grantResults[ 1 ] == PackageManager.  PERMISSION_GRANTED ;
                    if (permissionToRecord && permissionToStore) {
                        Toast.makeText(getApplicationContext(), "Permission Granted" , Toast. LENGTH_LONG ).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Permission Denied" ,Toast. LENGTH_LONG ).show();
                    }
                }
                break ;
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
//        btncheckBox = (CheckBox) findViewById(R.id.checkBox);


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
                AudioApplication.getInstance().getServiceInterface().togglePlay(f);


                break;
            case R.id.btn_forward:
                // 다음곡으로 이동
                AudioApplication.getInstance().getServiceInterface().forward();
                break;

            case R.id.btnRecord:
                // 녹음 시작

//                Log.d( "녹음버튼클릭" , "조건문 이전" );
                if (CheckPermissions()) {

                    stopbtn .setEnabled( true );
                    startbtn .setEnabled( false );
                    playbtn .setEnabled( false );
                    stopplay .setEnabled( false );
                    AudioApplication.getInstance().getServiceInterface().record();
                }
                else
                {

                    RequestPermissions();
                }
                break;
            case R.id.btnStop:
                // 녹음 중지
                stopbtn .setEnabled( false );
                startbtn .setEnabled( true );
                playbtn .setEnabled( true );
                stopplay .setEnabled( true );
                AudioApplication.getInstance().getServiceInterface().recordstop();
                break;
            case R.id.btnPlay:
                // 녹음 재생
                stopbtn .setEnabled( false );
                startbtn .setEnabled( true );
                playbtn .setEnabled( false );
                stopplay .setEnabled( true );
                AudioApplication.getInstance().getServiceInterface().recordplay();
                break;
            case R.id.StopPlay:
                // 녹음 재생중지
                stopbtn .setEnabled( false );
                startbtn .setEnabled( true );
                playbtn .setEnabled( true );
                stopplay .setEnabled( false );
                AudioApplication.getInstance().getServiceInterface().recordstopplay();
                break;
        }





    }


    public boolean CheckPermissions() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE );
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), RECORD_AUDIO );
        return result == PackageManager.  PERMISSION_GRANTED && result1 == PackageManager.  PERMISSION_GRANTED ;
    }
    private void RequestPermissions() {
        ActivityCompat.requestPermissions(englishlesson. this , new String[]{ RECORD_AUDIO , WRITE_EXTERNAL_STORAGE }, REQUEST_AUDIO_PERMISSION_CODE );
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
