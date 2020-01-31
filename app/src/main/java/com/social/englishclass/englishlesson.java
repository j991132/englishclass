package com.social.englishclass;

import android.Manifest;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaScannerConnection;
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
import android.widget.EditText;
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

import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class englishlesson extends AppCompatActivity implements View.OnClickListener {

    private final static int LOADER_ID = 0x001;
    private Spinner spinner;
    private float f;
    ArrayList<String> arrayList;
    ArrayAdapter<String> arrayAdapter;
    private int a = 2;
    private String TAG = "activity_englishlesson";
    private RecyclerView mRecyclerView;
    private AudioAdapter mAdapter;
    private ImageView mImgAlbumArt;
    private TextView mTxtTitle;
    private ImageButton mBtnPlayPause;
    private Button startbtn, stopbtn, playbtn, stopplay;
    private String folder, fname;
    private File beforeFileName, afterFileName;
    public static final int REQUEST_AUDIO_PERMISSION_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_englishlesson);

        Intent intent = getIntent();
        folder = intent.getStringExtra("lesson");

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
        startbtn = (Button) findViewById(R.id.btnRecord);
        stopbtn = (Button) findViewById(R.id.btnStop);
        playbtn = (Button) findViewById(R.id.btnPlay);
        stopplay = (Button) findViewById(R.id.StopPlay);
        stopbtn.setEnabled(false);
        playbtn.setEnabled(false);
        stopplay.setEnabled(false);
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
//스피너 선택버튼 만들기
        arrayList = new ArrayList<>();
        arrayList.add("재생속도");
        arrayList.add("0.5배속");
        arrayList.add("0.75배속");
        arrayList.add("1배속");
        arrayList.add("1.25배속");
        arrayList.add("1.5배속");

        arrayAdapter = new ArrayAdapter<>(getApplicationContext(),
                android.R.layout.simple_spinner_dropdown_item, arrayList);

        spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override

            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {


                switch (arrayList.get(i)) {
                    case "0.5배속":
                        f = (float) 0.5;
                        AudioApplication.getInstance().getServiceInterface().play2(f);
                        break;
                    case "0.75배속":
                        f = (float) 0.75;
                        AudioApplication.getInstance().getServiceInterface().play2(f);
                        break;
                    case "1배속":
                        f = (float) 1;
                        AudioApplication.getInstance().getServiceInterface().play2(f);
                        break;
                    case "1.25배속":
                        f = (float) 1.25;
                        AudioApplication.getInstance().getServiceInterface().play2(f);
                        break;
                    case "1.5배속":
                        f = (float) 1.5;
                        AudioApplication.getInstance().getServiceInterface().play2(f);
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


    public void getAudioListFromMediaDatabase() {
        getSupportLoaderManager().initLoader(LOADER_ID, null, new LoaderManager.LoaderCallbacks<Cursor>() {
            @Override
            public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                Log.d("겟리스트메서드", "메서드실생됨");
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
                String selection = MediaStore.Audio.Media.DATA + " LIKE ? AND " + MediaStore.Audio.Media.DATA + " NOT LIKE ? ";
// 원래는 미디어 ismusic 값이 1인 것(음악파일)은 모두 검색하는 조건이 들어갔었다
//                String selection = MediaStore.Audio.Media.IS_MUSIC + " = 1";
                String[] selectionArgs = new String[]{
                        "%" + folder + "%",
                        "%" + folder + "/%/%"
                };
                Log.d("겟리스트", "폴더" + selectionArgs);
                String sortOrder = MediaStore.Audio.Media.TITLE + " COLLATE LOCALIZED ASC";
                //               return new CursorLoader(getApplicationContext(), uri, projection, selection, null, sortOrder);
//검색 쿼리가 들어있는 내장파일 커서로더.java 를 호출한다.
                return new CursorLoader(getApplicationContext(), uri, projection, selection, selectionArgs, sortOrder);
            }

            @Override
            public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
                mAdapter.swapCursor(data);
                Log.d("커서데이터", "커서데이터" + data);
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
    protected void onPause() {
        super.onPause();
//        mTxtTitle.setText("재생중인 음악이 없습니다.");


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterBroadcast();
        AudioApplication.getInstance().getServiceInterface().stop();
        AudioApplication.getInstance().getServiceInterface().clearPlayList();
        finish();

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

                    stopbtn.setEnabled(true);
                    startbtn.setEnabled(false);
                    playbtn.setEnabled(false);
                    stopplay.setEnabled(false);
                    AudioApplication.getInstance().getServiceInterface().record();
                } else {

                    RequestPermissions();
                }
                break;
            case R.id.btnStop:
                // 녹음 중지
                stopbtn.setEnabled(false);
                startbtn.setEnabled(true);
                playbtn.setEnabled(true);
                stopplay.setEnabled(true);
                AudioApplication.getInstance().getServiceInterface().recordstop();
                recordname();
                break;
            case R.id.btnPlay:
                // 녹음 재생
                stopbtn.setEnabled(false);
                startbtn.setEnabled(true);
                playbtn.setEnabled(false);
                stopplay.setEnabled(true);

                recordlistdialog();
                updateUI();
//                AudioApplication.getInstance().getServiceInterface().recordplay(fname);
                break;
            case R.id.StopPlay:
                // 녹음 재생중지
                stopbtn.setEnabled(false);
                startbtn.setEnabled(true);
                playbtn.setEnabled(true);
                stopplay.setEnabled(false);
                a = a + 1;
                Log.d("로더 아이디", "로더아이디" + a);
                AudioApplication.getInstance().getServiceInterface().recordstopplay();
                break;
        }


    }


    public boolean CheckPermissions() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), RECORD_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
    }

    private void RequestPermissions() {
        ActivityCompat.requestPermissions(englishlesson.this, new String[]{RECORD_AUDIO, WRITE_EXTERNAL_STORAGE}, REQUEST_AUDIO_PERMISSION_CODE);
    }

    public void registerBroadcast() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(BroadcastActions.PLAY_STATE_CHANGED);
        registerReceiver(mBroadcastReceiver, filter);
    }

    public void unregisterBroadcast() {
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
            Picasso.with(getApplicationContext()).load(albumArtUri).error(R.drawable.music).into(mImgAlbumArt);
            mTxtTitle.setText(audioItem.mTitle);
        } else {
            mImgAlbumArt.setImageResource(R.drawable.music);
            mTxtTitle.setText("재생중인 음악이 없습니다.");
        }
    }

    //녹음중지 버튼 시 이름바꿔저장 매서드 실행
    public void recordname() {
        //다이얼로그생성
        final Dialog recordname = new Dialog(this);
        recordname.setContentView(R.layout.recordname);
        Button okbtn = (Button) recordname.findViewById(R.id.ok);
        Button canclebtn = (Button) recordname.findViewById(R.id.cancle);
        final EditText edit = (EditText) recordname.findViewById(R.id.edittext);
        //확인버튼
        okbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String FileName = edit.getText().toString();

                beforeFileName = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/englishclass/record", "AudioRecording.3gp");
                Log.d("이전파일이름", String.valueOf(beforeFileName));
                afterFileName = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/englishclass/record", FileName + ".3gp");
                Log.d("수정된파일이름", String.valueOf(afterFileName));
                beforeFileName.renameTo(afterFileName);
                Log.d("이름바꾸기", String.valueOf(beforeFileName));
                fname = String.valueOf(afterFileName);
                if (beforeFileName.renameTo(afterFileName))
                    Toast.makeText(getApplicationContext(), "success!" + FileName + beforeFileName, Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(getApplicationContext(), "faile" + FileName + beforeFileName, Toast.LENGTH_SHORT).show();

//                getContentResolver().notifyChange( Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "/englishclass/record")), null);
//                getContentResolver().notifyChange( MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null);
//                sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.fromFile(Environment.getExternalStorageDirectory())));
//                MediaScannerConnection.scanFile(getApplicationContext(), new String[]{new File(Environment.getExternalStorageDirectory(), "englishclass/record").getPath()}, null, null);


//                MediaStore.Audio.Media._ID,
//                        MediaStore.Audio.Media.TITLE,
//                        MediaStore.Audio.Media.ARTIST,
//                        MediaStore.Audio.Media.ALBUM,
//                        MediaStore.Audio.Media.ALBUM_ID,
//                        MediaStore.Audio.Media.DURATION,
//                        MediaStore.Audio.Media.DATA

                ContentValues values = new ContentValues();
                values.put(MediaStore.Audio.Media.DISPLAY_NAME, afterFileName.getName());
                values.put(MediaStore.Audio.Media.DATA, afterFileName.getPath());
                values.put(MediaStore.Audio.Media.MIME_TYPE, "audio/*");

//                Uri uri = MediaStore.Audio.Media.getContentUriForPath(afterFileName.getPath());
                getContentResolver().insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values);

                recordname.dismiss();
            }
        }); //ok버튼 끝

        //취소버튼
        canclebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recordname.dismiss();
            }
        }); //취소버튼 끝
        recordname.show();
    }

    //녹음재생 버튼 시 녹음파일 목록 다이얼로그 띄우기
    public void recordlistdialog() {

        //다이얼로그생성
        final Dialog recordlistdialog = new Dialog(this);
        recordlistdialog.setContentView(R.layout.recordlist);
        folder = "/storage/emulated/0/englishclass/record";

        getAudioListFromMediaDatabase2();

        RecyclerView recordRecyclerView = (RecyclerView) recordlistdialog.findViewById(R.id.recordrecyclerview);
//   AudioAdapter  recordAdapter = new AudioAdapter(this, null);
        mAdapter = new AudioAdapter(this, null);   //어댑터를 새로지정하면 못읽는다. null 값이라 그런가?

        recordRecyclerView.setAdapter(mAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recordRecyclerView.setLayoutManager(layoutManager);


//            recordlistdialog.dismiss();

        Log.e("다이얼로그 쇼 전에 커서데이터", "커서데이터");
        recordlistdialog.show();
    }

    public void getAudioListFromMediaDatabase2() {
        long currentTime = System.currentTimeMillis();
        int lid = (int) currentTime;
        Log.e("getAudioList2   로더 아이디", "로더아이디" + currentTime);
//        getSupportLoaderManager().restartLoader(0, null, this);
        getSupportLoaderManager().restartLoader(lid, null, new LoaderManager.LoaderCallbacks<Cursor>() {
            @Override
            public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                Log.e("겟리스트메서드", "메서드실생됨1");
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
                String selection = MediaStore.Audio.Media.DATA + " LIKE ? AND " + MediaStore.Audio.Media.DATA + " NOT LIKE ? ";
// 원래는 미디어 ismusic 값이 1인 것(음악파일)은 모두 검색하는 조건이 들어갔었다
//                String selection = MediaStore.Audio.Media.IS_MUSIC + " = 1";

                String[] selectionArgs = new String[]{
                        "%" + folder + "%",
                        "%" + folder + "/%/%"
                };
                Log.e("겟리스트", "폴더" + folder);
                String sortOrder = MediaStore.Audio.Media.TITLE + " COLLATE LOCALIZED ASC";
                //               return new CursorLoader(getApplicationContext(), uri, projection, selection, null, sortOrder);
//검색 쿼리가 들어있는 내장파일 커서로더.java 를 호출한다.

                return new CursorLoader(getApplicationContext(), uri, projection, selection, selectionArgs, sortOrder);
            }

            @Override
            public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
                mAdapter.swapCursor(data);

                Log.e("커서데이터 두번째", "커서데이터" + data.getCount());
                if (data != null && data.getCount() > 0) {
                    while (data.moveToNext()) {
                        Log.e(TAG, "Title:" + data.getString(data.getColumnIndex(MediaStore.Audio.Media.TITLE)));
                    }
                }
//               mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onLoaderReset(Loader<Cursor> loader) {
                mAdapter.swapCursor(null);
                Log.e("로더", "리셋");
            }
        });
    }

}//메인 종료
