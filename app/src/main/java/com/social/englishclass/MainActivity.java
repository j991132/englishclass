package com.social.englishclass;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;

import org.apache.commons.lang.StringEscapeUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

public class MainActivity extends AppCompatActivity {

    private Intent intent;
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private int line = 0;
    private StorageReference mStorageRef;
    private String downfile, howtouse;
    private int textbook_num;
    private File outputFile; //파일명까지 포함한 경로
    private ProgressDialog progressBar;
    private String fileURL;
    static final int PERMISSION_REQUEST_CODE = 1;
    private int howtopagenum = 1;
    String[] PERMISSIONS = {"android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE"};

    private boolean hasPermissions(String[] permissions) {
        int res = 0;
        //스트링 배열에 있는 퍼미션들의 허가 상태 여부 확인
        for (String perms : permissions) {
            res = checkCallingOrSelfPermission(perms);
            if (!(res == PackageManager.PERMISSION_GRANTED)) {
                //퍼미션 허가 안된 경우
                return false;
            }

        }
        //퍼미션이 허가된 경우
        return true;
    }

    private void requestNecessaryPermissions(String[] permissions) {
        //마시멜로( API 23 )이상에서 런타임 퍼미션(Runtime Permission) 요청
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, PERMISSION_REQUEST_CODE);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!hasPermissions(PERMISSIONS)) { //퍼미션 허가를 했었는지 여부를 확인
            requestNecessaryPermissions(PERMISSIONS);//퍼미션 허가안되어 있다면 사용자에게 요청
        } else {
            //이미 사용자에게 퍼미션 허가를 받음.
        }

        progressBar = new ProgressDialog(MainActivity.this);
        progressBar.setMessage("다운로드중");
        progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressBar.setIndeterminate(true);
        progressBar.setCancelable(false);

        final EditText login_school = (EditText) findViewById(R.id.login_school);
        final EditText login_name = (EditText) findViewById(R.id.login_name);

        Button login_btn = (Button) findViewById(R.id.login_btn);
        Button textdown_btn = (Button) findViewById(R.id.textdown_btn);
        Button howtouse_btn = (Button) findViewById(R.id.howtouse_btn);
        RadioButton online_rbtn = (RadioButton) findViewById(R.id.online_rbtn);
        RadioButton offline_rbtn = (RadioButton) findViewById(R.id.offline_rbtn);
//테스트중 넘어가기
//        intent = new Intent(MainActivity.this, englishlesson.class);
//        intent = new Intent(MainActivity.this, visualtest.class);
//        intent = new Intent(MainActivity.this, SelectLesson.class);
//        startActivity(intent);

        View.OnClickListener Listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.login_btn:

                        UserData userdata = new UserData();
                        String schoolID = login_school.getText().toString().trim();
                        userdata.schoolID = schoolID;
                        userdata.userID = login_name.getText().toString().trim();
                        userdata.fcmToken = FirebaseInstanceId.getInstance().getToken();

                        if (schoolID.equals("") || userdata.userID == null) {
                            Toast.makeText(getApplicationContext(), "학교명 또는 아이디를 입력하세요", Toast.LENGTH_LONG).show();
                        } else {
                            if (line == 0) {
                                Toast.makeText(getApplicationContext(), "온라인 또는 오프라인을 선택하세요", Toast.LENGTH_LONG).show();
                            } else if (line == 1) {
                                firebaseDatabase.getReference("users").child(schoolID + userdata.userID).setValue(userdata);
//                                intent = new Intent(MainActivity.this, englishlesson.class);
                                intent = new Intent(MainActivity.this, SelectLesson.class);
                                intent.putExtra("login_school", schoolID);
                                intent.putExtra("login_name", schoolID + userdata.userID);
                                intent.putExtra("login_number", userdata.userID);
                                intent.putExtra("token", userdata.fcmToken);
                                intent.putExtra("line", "1");
                                startActivity(intent);
                                finish();
                            } else {
                                intent = new Intent(MainActivity.this, SelectLesson.class);
                                intent.putExtra("login_school", schoolID);
                                intent.putExtra("login_name", schoolID + userdata.userID);
                                intent.putExtra("token", userdata.fcmToken);
                                intent.putExtra("line", "2");
                                startActivity(intent);
                                finish();
                            }
                        }
                        break;
                    case R.id.online_rbtn:
                        line = 1;
                        break;
                    case R.id.offline_rbtn:
                        line = 2;
                        break;
                    case R.id.textdown_btn:
//                        gettextdata("englishclass.zip");
//                        CheckTypesTask ziptask = new CheckTypesTask();
//                       ziptask.execute();
//                        String zipFile = Environment.getExternalStorageDirectory() + "/englishclass/englishclass.zip";  //zip 파일이 있는 위치 정의
//                        String unzipLocation = Environment.getExternalStorageDirectory() + "/englishclass/";  //unzip 하고자 하는 위치
//                        Decompress d =  new Decompress(zipFile, unzipLocation);
//                        d.execute();
// 다이얼로그 띄우기
                        final Dialog textbookdialog = new Dialog(MainActivity.this);
                        textbookdialog.setContentView(R.layout.textbookdown_dialog);

                        Button textbook1_btn = (Button) textbookdialog.findViewById(R.id.textbook1_btn);
                        Button textbook2_btn = (Button) textbookdialog.findViewById(R.id.textbook2_btn);


                        textbook1_btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
//                                String zipFile = Environment.getExternalStorageDirectory() + "/englishclass/englishclass1.zip";  //zip 파일이 있는 위치 정의
//                                String unzipLocation = Environment.getExternalStorageDirectory() + "/englishclass/";  //unzip 하고자 하는 위치
//                                Decompress d = new Decompress(zipFile, unzipLocation);
//                                d.execute();
                                textbook_num = 1;
                                gettextdata("englishclass1.zip");
                                textbookdialog.dismiss();
                            }
                        });
                        textbook2_btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                textbook_num = 2;
                                gettextdata("englishclass2.zip");
                                textbookdialog.dismiss();
                            }
                        });
                        textbookdialog.show();
                        break;
                    case R.id.howtouse_btn:
                        final Dialog howtousedialog = new Dialog(MainActivity.this, android.R.style.Theme_NoTitleBar_OverlayActionModes);
                        howtousedialog.setContentView(R.layout.howtouse_dialog);
                        howtousedialog.getWindow().setBackgroundDrawableResource(R.color.translucent_black);
                        ImageButton howtouse_pre_btn = (ImageButton) howtousedialog.findViewById(R.id.howtouse_pre_btn);
                        ImageButton howtouse_next_btn = (ImageButton) howtousedialog.findViewById(R.id.howtouse_next_btn);
                        ImageButton howtouse_cancle_btn = (ImageButton) howtousedialog.findViewById(R.id.howtouse_cancle_btn);
                        ImageView howtouse_image = (ImageView) howtousedialog.findViewById(R.id.howtouse_image);
                        howtopagenum = 1;
                        howtouse_image.setImageResource(R.drawable.howtouse1);
                        howtouse_image.setScaleType(ImageView.ScaleType.FIT_XY);
                        howtouse_pre_btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (howtopagenum == 1) {
                                    Toast.makeText(getApplicationContext(), "사용 방법 첫 화면 입니다.", Toast.LENGTH_LONG).show();
                                } else {
                                    howtopagenum = howtopagenum - 1;
                                    howtouse = "howtouse" + howtopagenum;
                                    int lid = MainActivity.this.getResources().getIdentifier(howtouse, "drawable", MainActivity.this.getPackageName());
                                    ((ImageView) howtousedialog.findViewById(R.id.howtouse_image)).setImageResource(lid);
                                }

                            }
                        });
                        howtouse_next_btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (howtopagenum == 9) {
                                    Toast.makeText(getApplicationContext(), "사용 방법 마지막 화면 입니다.", Toast.LENGTH_LONG).show();
                                } else {
                                    howtopagenum = howtopagenum + 1;
                                    howtouse = "howtouse" + howtopagenum;
                                    int lid = MainActivity.this.getResources().getIdentifier(howtouse, "drawable", MainActivity.this.getPackageName());
                                    ((ImageView) howtousedialog.findViewById(R.id.howtouse_image)).setImageResource(lid);
                                }

                            }
                        });
                        howtouse_cancle_btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                howtopagenum = 1;
                                howtousedialog.dismiss();
                            }
                        });
                        howtousedialog.show();
                        break;

                }
            }
        };
        login_btn.setOnClickListener(Listener);
        online_rbtn.setOnClickListener(Listener);
        offline_rbtn.setOnClickListener(Listener);
        textdown_btn.setOnClickListener(Listener);
        howtouse_btn.setOnClickListener(Listener);

    }
//textbook 데이터 다운받기 http 주소 이용
/*
private void gettextdata(String Filename){
    File path1 = new File("/storage/emulated/0/englishclass/lesson1keyword/");
    File path2 = new File("/storage/emulated/0/englishclass/lesson7keyword/");
    if (path1.exists() && textbook_num == 1) {
        Toast.makeText(getApplicationContext(), "이미 기기안에 1학기 자료폴더가 존재합니다. 다시 받으시려면 englishclass 폴더를 삭제하고 다운받아주세요.", Toast.LENGTH_LONG).show();
    } else if (path2.exists() && textbook_num == 2) {
        Toast.makeText(getApplicationContext(), "이미 기기안에 2학기 자료폴더가 존재합니다. 다시 받으시려면 englishclass 폴더를 삭제하고 다운받아주세요.", Toast.LENGTH_LONG).show();
    }else {

        if (textbook_num == 1) {
            fileURL = "https://docs.google.com/uc?export=download&id=1Gi2N0koFI9Vb86v-qco4y54aJ6NdbCIj";

        } else if (textbook_num == 2) {
//            fileURL = "https://docs.google.com/uc?export=download&id=1S5XOnvaq55hRDFp0sM6OonmpwZ3aVgDx";

              fileURL = "http://iyh.icees.kr/boardCnts/fileDown.do?fileSeq=ba8000d6c16c069d999289c7bad8401f";
        }
        downfile = Filename;
        final DownloadFilesTask downloadTask = new DownloadFilesTask(MainActivity.this);
        downloadTask.execute(fileURL);
    }
}

 */

    //파이어베이스 스토리지에서 textbook 데이터 다운받기
    private void gettextdata(String Filename) {


        mStorageRef = FirebaseStorage.getInstance().getReference("textbook").child(Filename);
//        pathReference = mStorageRef.child(Filename);
        try {
            File path = new File("/storage/emulated/0/englishclass/");
            File path1 = new File("/storage/emulated/0/englishclass/lesson1keyword/");
            File path2 = new File("/storage/emulated/0/englishclass/lesson7keyword/");
            final File file = new File(path, Filename);
            if (textbook_num == 1) {
                final String fileURL = "https://docs.google.com/uc?export=download&id=1Gi2N0koFI9Vb86v-qco4y54aJ6NdbCIj";
            } else if (textbook_num == 2) {
                final String fileURL = "https://docs.google.com/uc?export=download&id=1S5XOnvaq55hRDFp0sM6OonmpwZ3aVgDx";
            }
            try {
                if (path1.exists() && textbook_num == 1) {
                    Toast.makeText(getApplicationContext(), "이미 기기안에 1학기 자료폴더가 존재합니다. 다시 받으시려면 englishclass 폴더를 삭제하고 다운받아주세요.", Toast.LENGTH_LONG).show();
                } else if (path2.exists() && textbook_num == 2) {
                    Toast.makeText(getApplicationContext(), "이미 기기안에 2학기 자료폴더가 존재합니다. 다시 받으시려면 englishclass 폴더를 삭제하고 다운받아주세요.", Toast.LENGTH_LONG).show();
                } else {
                    //다운로드 진행 Dialog 보이기
                    final ProgressDialog progressDialog = new ProgressDialog(this);
                    progressDialog.setTitle("다운로드중...");
//                    progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                    progressDialog.setCancelable(false);
                    progressDialog.show();

                    path.mkdirs();
                    file.createNewFile();
                    final FileDownloadTask fileDownloadTask = mStorageRef.getFile(file);
//                downfile = "/storage/emulated/0/englishclass/record/"+Filename;
                    fileDownloadTask.addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            Log.e("다운로드받은 녹음파일 ", "경로입력전");
//                            downfile = "/storage/emulated/0/englishclass/" + Filename;
                            Log.e("다운로드받은 녹음파일 ", "" + downfile);
                            String zipFile = Environment.getExternalStorageDirectory() + "/englishclass/" + Filename;  //zip 파일이 있는 위치 정의
                            String unzipLocation = Environment.getExternalStorageDirectory() + "/englishclass/";  //unzip 하고자 하는 위치
                            Decompress d = new Decompress(zipFile, unzipLocation);
                            d.execute();
                            progressDialog.dismiss();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("다운로드실패 ", "");
                            progressDialog.dismiss();
                        }
                    }).addOnProgressListener(new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull FileDownloadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                            //dialog에 진행률을 퍼센트로 출력해 준다
                            progressDialog.setMessage("Downloaded " + ((int) progress) + "% ...");
                        }
                    });
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class Decompress extends AsyncTask<Void, Integer, Integer> {
        private String _zipFile;  //저장된 zip 파일 위치
        private String _location; //압출을 풀 위치
        //        private long total_len, total_installed_len;
        private int per;
        ProgressDialog asyncDialog = new ProgressDialog(
                MainActivity.this);


        public Decompress(String zipFile, String location) {
            _zipFile = zipFile;
            _location = location;
//            total_len = _zipFile.length();
//            total_installed_len = 0;
            _dirChecker(""); //폴더를 만들기 위한 함수로 아래에 정의 되어 있습니다.

        }


        @Override
        protected void onPreExecute() {
            asyncDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            asyncDialog.setMessage("교과서 파일을 설치중입니다...");
            asyncDialog.setCancelable(false);
            // show dialog
            asyncDialog.show();
            super.onPreExecute();
        }

        @Override
        protected Integer doInBackground(Void... voids) {

            try {
                ZipFile zip = new ZipFile(_zipFile);
                asyncDialog.setMax(zip.size());
                FileInputStream fin = new FileInputStream(_zipFile);
                ZipInputStream zin = new ZipInputStream(fin);
                ZipEntry ze = null;
                while ((ze = zin.getNextEntry()) != null) {
                    Log.e("Decompress", "Unzipping " + ze.getName());   //압축이 풀리면서 logcat으로 zip 안에 있던 파일 들을 볼 수 있습니다.
//                    total_installed_len +=ze.getCompressedSize();
                    per++;
                    publishProgress(per);
//                    per = (int)((total_installed_len/total_len)*100);
//                    Log.e("압축해제 율", "total_installed_len " + total_installed_len);
//                    Log.e("압축해제 율", "total_len " + total_len);
                    Log.e("압축해제 율", "Unzipping " + per);
                    if (ze.isDirectory()) {

                        _dirChecker(ze.getName());
                    } else {
                        FileOutputStream fout = new FileOutputStream(_location + ze.getName());
                        BufferedInputStream in = new BufferedInputStream(zin);  //이렇게 지정하지 않고 unzip을 수행하면 속도가 매우 느려집니다.
                        BufferedOutputStream out = new BufferedOutputStream(fout);
                        byte b[] = new byte[1024];
                        int n;
                        while ((n = in.read(b, 0, 1024)) >= 0) {
                            out.write(b, 0, n);
                        }
                        out.close();
                        zin.closeEntry();
                        fout.close();
                    }
                    try {
                        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + Environment.getExternalStorageDirectory() + "/englishclass/" + ze.getName())));

                    } catch (Exception e) {
                        Log.e("브로드캐스트 저장소 갱신", "오류", e);
                    }
                }
                zin.close();
            } catch (Exception e) {
                Log.e("Decompress", "unzip", e);
            }
            return per;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            asyncDialog.setProgress(per);
//            asyncDialog.setMessage("압축 푸는 중 " + ((int) per) + "% ...");
        }


        @Override
        protected void onPostExecute(Integer integer) {
            //다이얼로그를 없앰
            asyncDialog.dismiss();
            Toast.makeText(getApplicationContext(), Integer.toString(integer) + " total sum",
                    Toast.LENGTH_SHORT).show();
/*
            try {
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + Environment.getExternalStorageDirectory() + "/englishclass/lesson1keyword/sound/art.mp3")));
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + Environment.getExternalStorageDirectory() + "/englishclass/lesson2keyword/sound/animal.mp3")));
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + Environment.getExternalStorageDirectory() + "/englishclass/lesson3keyword/sound/April.mp3")));
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + Environment.getExternalStorageDirectory() + "/englishclass/lesson4keyword/sound/cap.mp3")));
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + Environment.getExternalStorageDirectory() + "/englishclass/lesson5keyword/sound/cold.mp3")));
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + Environment.getExternalStorageDirectory() + "/englishclass/lesson6keyword/sound/boat.mp3")));
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + Environment.getExternalStorageDirectory() + "/englishclass/lesson7keyword/sound/behind.mp3")));
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + Environment.getExternalStorageDirectory() + "/englishclass/lesson8keyword/sound/bank.mp3")));
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + Environment.getExternalStorageDirectory() + "/englishclass/lesson9keyword/sound/bone.mp3")));
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + Environment.getExternalStorageDirectory() + "/englishclass/lesson10keyword/sound/always.mp3")));
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + Environment.getExternalStorageDirectory() + "/englishclass/lesson11keyword/sound/ate.mp3")));
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + Environment.getExternalStorageDirectory() + "/englishclass/lesson12keyword/sound/birthday.mp3")));


            } catch (Exception e) {
                Log.e("브로드캐스트 저장소 갱신", "오류", e);
            }

 */

        }

        //변수 location에 저장된 directory의 폴더를 만듭니다.
        private void _dirChecker(String dir) {
            File f = new File(_location + dir);

            if (!f.isDirectory()) {
                f.mkdirs();
            }
        }

    }

    private class DownloadFilesTask extends AsyncTask<String, String, Long> {

        private Context context;
        private PowerManager.WakeLock mWakeLock;

        public DownloadFilesTask(Context context) {
            this.context = context;
        }


        //파일 다운로드를 시작하기 전에 프로그레스바를 화면에 보여줍니다.
        @Override
        protected void onPreExecute() { //2
            super.onPreExecute();

            //사용자가 다운로드 중 파워 버튼을 누르더라도 CPU가 잠들지 않도록 해서
            //다시 파워버튼 누르면 그동안 다운로드가 진행되고 있게 됩니다.
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, getClass().getName());
            mWakeLock.acquire();

            progressBar.show();
        }


        //파일 다운로드를 진행합니다.
        @Override
        protected Long doInBackground(String... string_url) { //3
            int count;
            long FileSize = -1;
            InputStream input = null;
            OutputStream output = null;
            URLConnection connection = null;

            try {


                URL url = new URL(string_url[0]);
                connection = url.openConnection();
                connection.connect();


                //파일 크기를 가져옴
                FileSize = connection.getContentLength();

                //URL 주소로부터 파일다운로드하기 위한 input stream
                input = new BufferedInputStream(url.openStream(), 8192);

                File path = new File("/storage/emulated/0/englishclass/");

//                path= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                outputFile = new File(path, downfile); //파일명까지 포함함 경로의 File 객체 생성

                // SD카드에 저장하기 위한 Output stream
                output = new FileOutputStream(outputFile);


                byte data[] = new byte[1024];
                long downloadedSize = 0;
                while ((count = input.read(data)) != -1) {
                    //사용자가 BACK 버튼 누르면 취소가능
                    if (isCancelled()) {
                        input.close();
                        return Long.valueOf(-1);
                    }

                    downloadedSize += count;

                    if (FileSize > 0) {
                        float per = ((float) downloadedSize / FileSize) * 100;
                        String str = "Downloaded " + downloadedSize + "KB / " + FileSize + "KB (" + (int) per + "%)";
                        publishProgress("" + (int) ((downloadedSize * 100) / FileSize), str);

                    }

                    //파일에 데이터를 기록합니다.
                    output.write(data, 0, count);
                }
                // Flush output
                output.flush();

                // Close streams
                output.close();
                input.close();


            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            } finally {
                try {
                    if (output != null)
                        output.close();
                    if (input != null)
                        input.close();
                } catch (IOException ignored) {
                }

                mWakeLock.release();

            }
            return FileSize;

        }


        //다운로드 중 프로그레스바 업데이트
        @Override
        protected void onProgressUpdate(String... progress) { //4
            super.onProgressUpdate(progress);

            // if we get here, length is known, now set indeterminate to false
            progressBar.setIndeterminate(false);
            progressBar.setMax(100);
            progressBar.setProgress(Integer.parseInt(progress[0]));
            progressBar.setMessage(progress[1]);
        }

        //파일 다운로드 완료 후
        @Override
        protected void onPostExecute(Long size) { //5
            super.onPostExecute(size);

            progressBar.dismiss();

            if (size > 0) {
                Toast.makeText(getApplicationContext(), "다운로드 완료되었습니다. 파일 크기=" + size.toString(), Toast.LENGTH_LONG).show();

                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                mediaScanIntent.setData(Uri.fromFile(outputFile));
                sendBroadcast(mediaScanIntent);


            } else {
                Toast.makeText(getApplicationContext(), "다운로드 에러", Toast.LENGTH_LONG).show();
            }
            String zipFile = Environment.getExternalStorageDirectory() + "/englishclass/" + downfile;  //zip 파일이 있는 위치 정의
            Log.e("압축해제 파일: ", "" + zipFile);
            String unzipLocation = Environment.getExternalStorageDirectory() + "/englishclass/";  //unzip 하고자 하는 위치
            Decompress d = new Decompress(zipFile, unzipLocation);
            d.execute();
        }


    }


    @Override
    public void onRequestPermissionsResult(int permsRequestCode, String[] permissions, int[] grantResults) {
        switch (permsRequestCode) {

            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {
                    boolean readAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean writeAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                        if (!readAccepted || !writeAccepted) {
                            showDialogforPermission("앱을 실행하려면 퍼미션을 허가하셔야합니다.");
                            return;
                        }
                    }
                }
                break;
        }
    }

    private void showDialogforPermission(String msg) {

        final AlertDialog.Builder myDialog = new AlertDialog.Builder(MainActivity.this);
        myDialog.setTitle("알림");
        myDialog.setMessage(msg);
        myDialog.setCancelable(false);
        myDialog.setPositiveButton("예", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(PERMISSIONS, PERMISSION_REQUEST_CODE);
                }

            }
        });
        myDialog.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                finish();
            }
        });
        myDialog.show();
    }


}


