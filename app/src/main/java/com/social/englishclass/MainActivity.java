package com.social.englishclass;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

public class MainActivity extends AppCompatActivity{

    private Intent intent;
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private int line=0;
    private StorageReference mStorageRef;
    private String downfile;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final EditText login_school = (EditText)findViewById(R.id.login_school);
        final EditText login_name = (EditText)findViewById(R.id.login_name);

        Button login_btn= (Button) findViewById(R.id.login_btn);
        Button textdown_btn=(Button)findViewById(R.id.textdown_btn);
        RadioButton online_rbtn = (RadioButton)findViewById(R.id.online_rbtn);
        RadioButton offline_rbtn = (RadioButton)findViewById(R.id.offline_rbtn);
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

                        if(schoolID.equals("")||userdata.userID==null) {
                            Toast.makeText(getApplicationContext(), "학교명 또는 아이디를 입력하세요", Toast.LENGTH_LONG).show();
                        }else {
                            if(line == 0) {
                                Toast.makeText(getApplicationContext(), "온라인 또는 오프라인을 선택하세요", Toast.LENGTH_LONG).show();
                            }else if (line == 1){
                                firebaseDatabase.getReference("users").child(schoolID + userdata.userID).setValue(userdata);
//                                intent = new Intent(MainActivity.this, englishlesson.class);
                                intent = new Intent(MainActivity.this, SelectLesson.class);
                                intent.putExtra("login_school", schoolID);
                                intent.putExtra("login_name", schoolID + userdata.userID);
                                intent.putExtra("token", userdata.fcmToken);
                                startActivity(intent);
                                finish();
                            }else {
                                intent = new Intent(MainActivity.this, SelectLesson.class);
                                intent.putExtra("login_school", schoolID);
                                intent.putExtra("login_name", schoolID + userdata.userID);
                                intent.putExtra("token", userdata.fcmToken);
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
                        gettextdata("englishclass.zip");
//                        CheckTypesTask ziptask = new CheckTypesTask();
//                       ziptask.execute();
//                        String zipFile = Environment.getExternalStorageDirectory() + "/englishclass/englishclass.zip";  //zip 파일이 있는 위치 정의
//                        String unzipLocation = Environment.getExternalStorageDirectory() + "/englishclass/";  //unzip 하고자 하는 위치
//                        Decompress d =  new Decompress(zipFile, unzipLocation);
//                        d.execute();
                        break;

                }
            }
        };
        login_btn.setOnClickListener(Listener);
        online_rbtn.setOnClickListener(Listener);
        offline_rbtn.setOnClickListener(Listener);
        textdown_btn.setOnClickListener(Listener);

    }
//textbook 데이터 다운받기
    private void gettextdata(String Filename){


        mStorageRef = FirebaseStorage.getInstance().getReference("textbook").child(Filename);
//        pathReference = mStorageRef.child(Filename);
        try{
            File path = new File("/storage/emulated/0/englishclass/");
            final File file = new File(path, Filename);
            try{
                if (path.exists()){
                    Toast.makeText(getApplicationContext(), "이미 기기안에 englishclass 폴더가 존재합니다. 폴더를 삭제하고 다시 다운받아 주세요. ", Toast.LENGTH_LONG).show();
                }else {
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
                            downfile = "/storage/emulated/0/englishclass/" + Filename;
                            Log.e("다운로드받은 녹음파일 ", "" + downfile);
                            String zipFile = Environment.getExternalStorageDirectory() + "/englishclass/englishclass.zip";  //zip 파일이 있는 위치 정의
                            String unzipLocation = Environment.getExternalStorageDirectory() + "/englishclass/";  //unzip 하고자 하는 위치
                            Decompress d =  new Decompress(zipFile, unzipLocation);
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
            }catch (IOException e){
                e.printStackTrace();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private class Decompress extends AsyncTask<Void, Integer, Integer>{
        private String _zipFile;  //저장된 zip 파일 위치
        private String _location; //압출을 풀 위치
//        private long total_len, total_installed_len;
        private int per;
        ProgressDialog asyncDialog = new ProgressDialog(
                MainActivity.this);



        public  Decompress(String zipFile, String location) {
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

            try  {
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
                    if(ze.isDirectory()) {

                        _dirChecker(ze.getName());
                    } else {
                        FileOutputStream fout = new FileOutputStream(_location + ze.getName());
                        BufferedInputStream in = new BufferedInputStream(zin);  //이렇게 지정하지 않고 unzip을 수행하면 속도가 매우 느려집니다.
                        BufferedOutputStream out = new BufferedOutputStream(fout);
                        byte b[] = new byte[1024];
                        int n;
                        while ((n = in.read(b,0,1024)) >= 0) {
                            out.write(b,0,n);
                        }
                        out.close();
                        zin.closeEntry();
                        fout.close();
                    }

                }
                zin.close();
            } catch(Exception e) {
                Log.e("Decompress", "unzip", e);
            }
            return per;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            asyncDialog.setProgress( per);
//            asyncDialog.setMessage("압축 푸는 중 " + ((int) per) + "% ...");
        }



        @Override
        protected void onPostExecute(Integer integer) {
            //다이얼로그를 없앰
            asyncDialog.dismiss();
            Toast.makeText(getApplicationContext(), Integer.toString(integer) + " total sum",
                    Toast.LENGTH_SHORT).show();

            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://"+ Environment.getExternalStorageDirectory() + "/englishclass/lesson1keyword/sound/art.mp3")));


        }
        //변수 location에 저장된 directory의 폴더를 만듭니다.
        private void _dirChecker(String dir) {
            File f = new File(_location + dir);

            if(!f.isDirectory()) {
                f.mkdirs();
            }
        }

    }













    }


