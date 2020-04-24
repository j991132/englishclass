package com.social.englishclass;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
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
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class englishlesson extends AppCompatActivity implements View.OnClickListener {

    private final static int LOADER_ID = 0x001;
    private Spinner spinner;
    private float f;
    ArrayList<String> arrayList;
    ArrayAdapter<String> arrayAdapter;
    private int pause;
    private String TAG = "activity_englishlesson";
    private RecyclerView mRecyclerView;
    private AudioAdapter mAdapter, recordAdapter, serchAdapter;
    private ImageView mImgAlbumArt;
    private TextView mTxtTitle;
    private ImageButton mBtnPlayPause;
    private Button startbtn, stopbtn, playbtn, stopplay, btn_server;
    private String folder, fname;
    public  String serchfilename;
    private File beforeFileName, afterFileName, beforesendtest, aftersendtest;
    public static final int REQUEST_AUDIO_PERMISSION_CODE = 1;
    boolean isRecording = false;
    private Long duration;
    public static Dialog recordlistdialog, deletedialog;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;
    private StorageTask mUploadTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_englishlesson);

        Intent intent = getIntent();
        folder = intent.getStringExtra("lesson");

        mStorageRef = FirebaseStorage.getInstance().getReference("uploads");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("uploads");

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
        btn_server = (Button) findViewById(R.id.btn_server);
        stopbtn.setEnabled(false);
        playbtn.setEnabled(true);
        stopplay.setEnabled(false);
        playbtn.setOnClickListener(this);
        stopbtn.setOnClickListener(this);
        startbtn.setOnClickListener(this);
        stopplay.setOnClickListener(this);
        btn_server.setOnClickListener(this);
//녹음버튼 끝
        mImgAlbumArt = (ImageView) findViewById(R.id.img_albumart);
        mTxtTitle = (TextView) findViewById(R.id.txt_title);
        mBtnPlayPause = (ImageButton) findViewById(R.id.btn_play_pause);
        findViewById(R.id.lin_miniplayer).setOnClickListener(this);
        findViewById(R.id.btn_rewind).setOnClickListener(this);
        mBtnPlayPause.setOnClickListener(this);
        findViewById(R.id.btn_forward).setOnClickListener(this);

        speedselect();
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
            if (intent.getAction().equals("deletedialog")){
                String filenamevalue = intent.getStringExtra("filenamevalue");

                Uri filepathvalue = Uri.parse("file:/"+intent.getStringExtra("filepathvalue"));
                Log.e("다이얼로그 출력시 Uri 정보", " "+ filepathvalue);
                deletedialog(filenamevalue, filepathvalue);
            }
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

    @RequiresApi(api = Build.VERSION_CODES.N)
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
                    if (isRecording == false) {

                        stopbtn.setEnabled(true);
                        startbtn.setEnabled(true);
                        playbtn.setEnabled(true);
                        stopplay.setEnabled(false);
                        AudioApplication.getInstance().getServiceInterface().record();
                        isRecording = true;
                        pause = 0;
                        startbtn.setText("일시정지");
                    }else if(isRecording == true && pause == 0 ){
                        AudioApplication.getInstance().getServiceInterface().recordpause();

                        startbtn.setText("녹음시작");
                    }else if (isRecording == true && pause == 1){
                        AudioApplication.getInstance().getServiceInterface().recordresume();
                        pause = 0;
                        startbtn.setText("일시정지");
                    }
                } else {

                    RequestPermissions();
                }
                break;
            case R.id.btnStop:
                // 녹음 중지
                stopbtn.setEnabled(false);
                startbtn.setEnabled(true);
                playbtn.setEnabled(true);
                stopplay.setEnabled(false);
                isRecording = false;
                startbtn.setText("녹음시작");
                AudioApplication.getInstance().getServiceInterface().recordstop();
                recordname();

                break;
            case R.id.btnPlay:
                // 녹음 재생
                stopbtn.setEnabled(false);
                startbtn.setEnabled(true);
//                playbtn.setEnabled(false);
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
                AudioApplication.getInstance().getServiceInterface().recordstopplay();
//                mAdapter.swapCursor(null);
                break;

            case R.id.btn_server:
 /*               // 파이어베이스에서 가져오기
                mStorageRef.child("1_2020년 04월 16일.3gp").getMetadata()
                        .addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
                            @Override
                            public void onSuccess(StorageMetadata storageMetadata) {
                                String name = String.valueOf(storageMetadata.getName());
                                Log.e("파이어베이스에서 불러온 이름  ", name);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e("조회 실패  ", "조회실패");
                            }
                        });

  */
// 녹음서버 목록 보여주는 엑티비티 띄우기
                Intent intent = new Intent(this, recordserver.class);
                startActivity(intent);
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
        filter.addAction(BroadcastActions.DELETE_DIALOG);
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
            speedselect();
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
//파일명에 날짜시간 넣기
                SimpleDateFormat format = new SimpleDateFormat("yyyy년 MM월 dd일");
                String time = format.format(System.currentTimeMillis());
                beforeFileName = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/englishclass/record", "AudioRecording.pcm");
                beforesendtest = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/englishclass/record", "sendtest.txt");
                Log.d("이전파일이름", String.valueOf(beforeFileName));
                afterFileName = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/englishclass/record", FileName +"_"+time+".pcm");
                aftersendtest = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/englishclass/record", FileName +"_"+time+".txt");
                Log.d("수정된파일이름", String.valueOf(afterFileName));

                if (afterFileName.exists()){
//                    Log.e("저장되어 있는 파일이름      ", String.valueOf(afterFileName));
//                    afterFileName.mkdirs();
//                    afterFileName.delete();
//                    metadata(String.valueOf(beforeFileName));
                    Log.e("재생시간",String.valueOf( duration));
                    beforeFileName.renameTo(afterFileName);
//                    beforesendtest.renameTo(aftersendtest);
//                    updatadata(FileName+"_"+time);
//                    Log.e("삭제된 파일이름      ", String.valueOf(afterFileName));
                }else {

                    Log.e("재생시간", String.valueOf(duration));
                    beforeFileName.renameTo(afterFileName);
//                    beforesendtest.renameTo(aftersendtest);
                    fname = String.valueOf(afterFileName);
//                    metadata(String.valueOf(afterFileName));

//                metadata(fname);
                    ContentValues values = new ContentValues();
                    values.put(MediaStore.Audio.Media.DISPLAY_NAME, afterFileName.getName());
                    values.put(MediaStore.Audio.Media.TITLE, FileName+"_"+time);

//                    values.put(MediaStore.Audio.Media.DURATION, duration);
                    Log.e("녹음 중지 시 저장되는 이름   ", afterFileName.getName());
                    values.put(MediaStore.Audio.Media.DATA, afterFileName.getPath());
                    Log.e("녹음 중지 시 저장되는 경로   ", afterFileName.getPath());

                    values.put(MediaStore.Audio.Media.MIME_TYPE, "audio/*");



//                Uri uri = MediaStore.Audio.Media.getContentUriForPath(afterFileName.getPath());
                    getContentResolver().insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values);
//                getApplicationContext().getContentResolver().notifyChange(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null);
//                    getContentResolver().update(Settings.System.CONTENT_URI, values, null, null);
                }

                if (beforeFileName.renameTo(afterFileName))
                { Toast.makeText(getApplicationContext(), "success!" + FileName + beforeFileName, Toast.LENGTH_SHORT).show();}
                else
                {  Toast.makeText(getApplicationContext(), "faile" + FileName + beforeFileName, Toast.LENGTH_SHORT).show();}

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

/*                ContentValues values = new ContentValues();
                values.put(MediaStore.Audio.Media.DISPLAY_NAME, afterFileName.getName());
                values.put(MediaStore.Audio.Media.TITLE, FileName);

                values.put(MediaStore.Audio.Media.DURATION, duration);
                Log.e("녹음 중지 시 저장되는 이름   ", afterFileName.getName());
                values.put(MediaStore.Audio.Media.DATA, afterFileName.getPath());
                Log.e("녹음 중지 시 저장되는 경로   ", afterFileName.getPath());

                values.put(MediaStore.Audio.Media.MIME_TYPE, "audio/*");



//                Uri uri = MediaStore.Audio.Media.getContentUriForPath(afterFileName.getPath());
                getContentResolver().insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values);
//                getApplicationContext().getContentResolver().notifyChange(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null);
//                    getContentResolver().update(Settings.System.CONTENT_URI, values, null, null);
*/
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
        recordlistdialog = new Dialog(this);
        recordlistdialog.setContentView(R.layout.recordlist);
        folder = "/storage/emulated/0/englishclass/record";
        EditText serchname = (EditText) recordlistdialog.findViewById(R.id.serchtext);

        Button serchbtn = (Button) recordlistdialog.findViewById(R.id.serchbtn);

        getAudioListFromMediaDatabase2();

       RecyclerView recordRecyclerView = (RecyclerView) recordlistdialog.findViewById(R.id.recordrecyclerview);
       recordAdapter = new AudioAdapter(this, null);
//        mAdapter = new AudioAdapter(this, null);   //어댑터를 새로지정하면 못읽는다. null 값이라 그런가?

        recordRecyclerView.setAdapter(recordAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recordRecyclerView.setLayoutManager(layoutManager);

        Log.e("다이얼로그 쇼 전에 커서데이터", "커서데이터");
        Log.e("검색버튼 클릭 시 검색어   ", "검색어 "+serchfilename);
        serchbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText serchname = (EditText) recordlistdialog.findViewById(R.id.serchtext);
                serchfilename = serchname.getText().toString();
                getSerchAudioListFromMediaDatabase();
                RecyclerView serchRecyclerView = (RecyclerView) recordlistdialog.findViewById(R.id.recordrecyclerview);
                serchAdapter = new AudioAdapter(getApplicationContext(), null);
                serchRecyclerView.setAdapter(serchAdapter);
                Log.e("검색버튼 클릭 시 검색어   ", "버튼 안 로그" + serchfilename);
//                recordlistdialog.dismiss();

 //               serchlistdialog();
            }
        });

        Button recordplaycanclebtn = (Button) recordlistdialog.findViewById(R.id.cancle);
        recordplaycanclebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recordlistdialog.dismiss();
            }
        }); //취소버튼 끝

        recordlistdialog.show();
    }
 //녹음파일 검색 시 녹음파일 목록 다이얼로그 띄우기
    public void deletedialog(final String filenamevalue, final Uri filepathvalue) {

        //다이얼로그생성
        deletedialog = new Dialog(this);
        deletedialog.setContentView(R.layout.delete);
        TextView deletedialogtitle = (TextView) deletedialog.findViewById(R.id.deleltedialogtitle);
        deletedialogtitle.setText("선택된 파일  : "+filenamevalue);
//        folder = "/storage/emulated/0/englishclass/record";
        Button  btn_send_firebase = (Button) deletedialog.findViewById(R.id.btn_send_firebase);
        Button  btn_send_test = (Button) deletedialog.findViewById(R.id.btn_send_test);
        Button  deletebtn = (Button)  deletedialog.findViewById(R.id.deletebtn);
        Button  deletecanclebtn = (Button)  deletedialog.findViewById(R.id.deletecanclebtn);
        Log.e("지워질 파일이름   ", filenamevalue);
//파이어베이스 업로드
        btn_send_firebase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File  deletefile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/englishclass/record", filenamevalue+".3gp");
                Uri fileuri = Uri.fromFile(deletefile);
                Log.e("파일패스에서 얻어지는 uri   ", ""+fileuri);
                uploadfile(filenamevalue, fileuri);
            }
        }); //파이어베이스 업로드 끝
//영어발음평가 전송
        btn_send_test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });  //영어발음평가 전송 끝

//녹음파일 삭제
        deletebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                삭제시 지우기
                File  deletefile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/englishclass/record", filenamevalue+".3gp");
               Uri fileuri = Uri.fromFile(deletefile);
                Log.e("파일패스에서 얻어지는 uri   ", ""+fileuri);
                if (deletefile.delete()){
                    deletedata(filenamevalue);
                    Toast.makeText(getApplicationContext(), "녹음파일  " + filenamevalue+".3gp 가 삭제되었습니다." , Toast.LENGTH_SHORT).show();
                    deletedialog.dismiss();
                }
            }
        }); //삭제버튼 끝
//취소버튼
        deletecanclebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deletedialog.dismiss();
            }
        }); //취소버튼 끝


        deletedialog.show();
    }

    public void metadata(String filePath){
        MediaMetadataRetriever mediaMetadataRetriever= new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(filePath);

        duration = Long.valueOf(mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
    }

    public void updatadata(String FileName){
        ContentValues values = new ContentValues();
        String mselection = MediaStore.Audio.Media.TITLE+" LIKE ?";
//        String[] mselectionargs = {"%"+FileName+"%"};
        String[] mselectionargs = {FileName};

        values.put(MediaStore.Audio.Media.DURATION, duration);

//                Uri uri = MediaStore.Audio.Media.getContentUriForPath(afterFileName.getPath());
//        getContentResolver().insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values);
//                getApplicationContext().getContentResolver().notifyChange(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null);
                    getContentResolver().update(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values, mselection, mselectionargs);
//        getContentResolver().delete(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, mselection, mselectionargs);
  }
    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }
    //파이어베이스 업로드
    public void uploadfile(final String FileName, Uri filepathvalue){
        if(filepathvalue !=null) {
            Log.e("업로드시 얻어지는 파일 uri   ", ""+filepathvalue);

            String ext = filepathvalue.toString().substring(filepathvalue.toString().lastIndexOf("."));
            Log.e("업로드시 얻어지는 파일 확장자   ", ""+ext);
//업로드 진행 Dialog 보이기
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("업로드중...");
            progressDialog.show();

//스토리지 지정
            StorageReference filereference = mStorageRef.child(FileName + ext);
            mUploadTask = filereference.putFile(filepathvalue)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Upload upload = new Upload(FileName, taskSnapshot.getUploadSessionUri().toString());
                            String uploadId = mDatabaseRef.push().getKey();
                            mDatabaseRef.child(uploadId).setValue(upload);

                            Toast.makeText(getApplicationContext(), "업로드 완료!", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "업로드 실패!", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            @SuppressWarnings("VisibleForTests")
                            double progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                            //dialog에 진행률을 퍼센트로 출력해 준다
                            progressDialog.setMessage("Uploaded " + ((int) progress) + "% ...");
                        }
                    });
        }else{
            Toast.makeText(getApplicationContext(), "파일을 먼저 선택하세요.", Toast.LENGTH_SHORT).show();
        }

    }

    public void deletedata(String FileName){
        ContentValues values = new ContentValues();
        String mselection = MediaStore.Audio.Media.TITLE+" LIKE ?";
//        String[] mselectionargs = {"%"+FileName+"%"};
        String[] mselectionargs = {FileName};



//                Uri uri = MediaStore.Audio.Media.getContentUriForPath(afterFileName.getPath());
//        getContentResolver().insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values);
//                getApplicationContext().getContentResolver().notifyChange(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null);
//        getContentResolver().update(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values, mselection, mselectionargs);
        getContentResolver().delete(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, mselection, mselectionargs);
    }
// 녹음파일 리스트 어답터
    public void getAudioListFromMediaDatabase2() {
        long currentTime = System.currentTimeMillis();
        int lid = (int) currentTime;
        Log.e("getAudioList2   로더 아이디", "로더아이디" + currentTime);
//        getSupportLoaderManager().restartLoader(0, null, this);
        getSupportLoaderManager().restartLoader(lid, null, new LoaderManager.LoaderCallbacks<Cursor>() {
            @Override
            public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                Log.e("겟리스트메서드", "메서드실행됨"  );
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
                Log.e("겟리스트", "폴더" + selectionArgs );
                String sortOrder = MediaStore.Audio.Media.TITLE + " COLLATE LOCALIZED ASC";
                //               return new CursorLoader(getApplicationContext(), uri, projection, selection, null, sortOrder);
//검색 쿼리가 들어있는 내장파일 커서로더.java 를 호출한다.
                return new CursorLoader(getApplicationContext(), uri, projection, selection, selectionArgs, sortOrder);
            }

            @Override
            public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
                recordAdapter.swapCursor(data);
                Log.e("커서데이터", "커서데이터" + data );
                if (data != null && data.getCount() > 0) {
                    while (data.moveToNext()) {
                        Log.e(TAG, "Title:" + data.getString(data.getColumnIndex(MediaStore.Audio.Media.TITLE)));
                    }
                }
            }

            @Override
            public void onLoaderReset(Loader<Cursor> loader) {
                recordAdapter.swapCursor(null);
            }
        });
    }
// 녹음파일 검색 어답터
    public void getSerchAudioListFromMediaDatabase() {
        long currentTime = System.currentTimeMillis();
        int lid = (int) currentTime;
        getSupportLoaderManager().restartLoader(lid, null, new LoaderManager.LoaderCallbacks<Cursor>() {
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
//쿼리를 위한 조건을 담는 부분 ? 한개당 1개의 아규먼트가 적용된다.
//해당폴더는 검색하고 하위폴더는 제외하는 내용
                String selection = MediaStore.Audio.Media.DATA + " LIKE ? AND " + MediaStore.Audio.Media.TITLE + " LIKE ? ";
// 원래는 미디어 ismusic 값이 1인 것(음악파일)은 모두 검색하는 조건이 들어갔었다
//                String selection = MediaStore.Audio.Media.IS_MUSIC + " = 1";
                String[] selectionArgs = new String[]{
                        "%" + folder + "%",
                        "%" + serchfilename + "%"
                };

                String sortOrder = MediaStore.Audio.Media.TITLE + " COLLATE LOCALIZED ASC";

//검색 쿼리가 들어있는 내장파일 커서로더.java 를 호출한다.
                return new CursorLoader(getApplicationContext(), uri, projection, selection, selectionArgs, sortOrder);
            }

            @Override
            public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
                serchAdapter.swapCursor(data);

                if (data != null && data.getCount() > 0) {
                    while (data.moveToNext()) {
                        Log.e(TAG, "Title:" + data.getString(data.getColumnIndex(MediaStore.Audio.Media.TITLE)));
                    }
                }
            }

            @Override
            public void onLoaderReset(Loader<Cursor> loader) {
                serchAdapter.swapCursor(null);
            }
        });
    }


    //스피너 선택버튼 만들기
    public void speedselect() {
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
    }
}//메인 종료
