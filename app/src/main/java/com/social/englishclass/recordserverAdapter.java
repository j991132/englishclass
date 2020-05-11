package com.social.englishclass;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.List;

import static java.security.AccessController.getContext;

public class recordserverAdapter extends RecyclerView.Adapter<recordserverAdapter.AudioViewHolder> {
    private static Context mContext;
    private List<Upload> mUploads;
    public static MediaPlayer mMediaplayer;
    public Uri uri, muri;
    private StorageReference mStorageRef;
    private static boolean isPrepared ;
    public static boolean reset;
    private String ext1, ext;

    public recordserverAdapter(Context context, List<Upload> uploads){
        mContext = context;
        mUploads = uploads;
//        super(context, uploads);
    }//recordserverAdapter 끝


    @Override
    public recordserverAdapter.AudioViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.listitem_audio, parent, false);
        return new AudioViewHolder(v);
    }

    @Override
    public void onBindViewHolder( recordserverAdapter.AudioViewHolder holder, int position) {
        Upload uploadCurrent = mUploads.get(position);

        holder. mTxtTitle.setText(uploadCurrent.getName());
        Log.e("바인드된 파일이름   ", ""+mUploads);
        uri = Uri.parse(uploadCurrent.getUrl());
        Log.e("바인드된 uri   ", ""+uri);
        ext1 = uri.toString().substring(uri.toString().lastIndexOf("."));
        ext = ext1.substring(0,4);
        Log.e("리사이클뷰에서 얻어지는 파일 확장자   ", ""+ext);
    }

    @Override
    public int getItemCount() {
        return mUploads.size();
    }

    public static class AudioItem {
//        public long mId; // 오디오 고유 ID
//        public long mAlbumId; // 오디오 앨범아트 ID
        public String mTitle; // 타이틀 정보
//        public String mArtist; // 아티스트 정보
//        public String mAlbum; // 앨범 정보
//        public long mDuration; // 재생시간
//        public String mDataPath; // 실제 데이터위치
    }

    public class AudioViewHolder extends RecyclerView.ViewHolder {
        private final Uri artworkUri = Uri.parse("content://media/external/audio/albumart");


        //        private ImageView mImgAlbumArt;
        private TextView mTxtTitle;

//        private TextView mTxtSubTitle;
//        private TextView mTxtDuration;
        private recordserverAdapter.AudioItem mItem;
        private int mPosition;

        private AudioViewHolder(final View view) {
            super(view);
//            mImgAlbumArt = (ImageView) view.findViewById(R.id.img_albumart);
            mTxtTitle = (TextView) view.findViewById(R.id.txt_title);
//            mTxtSubTitle = (TextView) view.findViewById(R.id.txt_sub_title);
//            mTxtDuration = (TextView) view.findViewById(R.id.txt_duration);
          
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Log.e("파이어베이스에서 불러온 이름  ", mTxtTitle.getText().toString());
                    Log.e("파이어베이스에서 불러온 uri  ", uri.toString());
                    Log.e(" 파일 확장자   ", ""+ext);
                    String file_name_ext = mTxtTitle.getText().toString()+ext;
//리사이클뷰 어댑터안의 뷰에서 인텐트 전달
                    Intent intent = new Intent(view.getContext(), recordserverplay.class);
                    intent.putExtra("login_school", recordserver.login_school);
                    intent.putExtra("login_name", recordserver.login_name);
                    intent.putExtra("filename", mTxtTitle.getText().toString());
                    intent.putExtra("ext", ext);
                    mContext.startActivity(intent);

//                    mMediaplayer = new MediaPlayer();
//                    mMediaplayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

//                    getaudiourl(mTxtTitle.getText().toString());





//                    AudioApplication.getInstance().getServiceInterface().setPlayList(getAudioIds()); // 재생목록등록
//                    AudioApplication.getInstance().getServiceInterface().play(mPosition); // 선택한 오디오재생
//                    try {
//                        englishlesson.recordlistdialog.dismiss();  //녹음 리스트 다이얼로그 끄기
//                    }catch (Exception e){e.printStackTrace();}


                }
            });

/*
            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
//   롱클릭시 이벤트
                    AudioApplication.getInstance().getServiceInterface().setPlayList(getAudioIds()); // 재생목록등록
                    AudioApplication.getInstance().getServiceInterface().deletedialog(mPosition);
                    Log.e("롱클릭 실행됨", "   롱클릭    " );
//                    Log.e("다이얼로그 출력시 Uri 정보", " "+ view.getData());
                    return true;
                }
            });
*/
        }

        public void setAudioItem(AudioItem item, int position) {
            mItem = item;
            mPosition = position;
            mTxtTitle.setText(item.mTitle);
//            mTxtSubTitle.setText(item.mArtist + "(" + item.mAlbum + ")");
//            mTxtDuration.setText(DateFormat.format("mm:ss", item.mDuration));
 //           Uri albumArtUri = ContentUris.withAppendedId(artworkUri, item.mAlbumId);
 //           Picasso.with(itemView.getContext()).load(albumArtUri).error(R.drawable.music).into(mImgAlbumArt);
        }
    }

public void getaudiourl(final String Filename){
    mStorageRef = FirebaseStorage.getInstance().getReference("uploads");

    // 파이어베이스에서 가져오기
    mStorageRef.child(Filename+ext).getDownloadUrl()
            .addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {

                   muri = uri;
                    Log.e("2  파이어베이스에서 불러온 url  ", ""+ muri);
                    try {
                        mMediaplayer.setDataSource(muri.toString());
                        mMediaplayer.prepareAsync();
                        mMediaplayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

                            @Override
                            public void onPrepared(MediaPlayer mp) {
                                isPrepared = true;
                                reset = false;
                                mp.start();

                                Intent intent = new Intent(BroadcastActions.START);
                                intent .putExtra("filename",Filename );

                                mContext.sendBroadcast(intent);
//                                mContext.sendBroadcast(new Intent(BroadcastActions.PLAY_STATE_CHANGED));

                                //녹음재생 완료후 정지
                                mMediaplayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                    @Override
                                    public void onCompletion(MediaPlayer mp) {
                                        if(mMediaplayer !=null) {
                                            isPrepared = false;

//                                          mMediaplayer.release();    //객체를 파괴하여 다시 못씀
                                            mMediaplayer.reset();
                                            reset = true;
                                            Intent intent = new Intent(BroadcastActions.PLAY_STATE_CHANGED);
//                                            intent .putExtra("filename","재생중인 파일이 없습니다." );

                                            mContext.sendBroadcast(intent);

                                        }
                                    }
                                });

                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e("조회 실패2  ", "조회실패2");
                }
            });
}


    public static void pause() {
        if (isPrepared) {
            mMediaplayer.pause();
            mContext.sendBroadcast(new Intent(BroadcastActions.PLAY_STATE_CHANGED)); // 재생상태 변경 전송
        }
    }
    public static void play(float a) {

        if (isPrepared) {

            mMediaplayer.setPlaybackParams((mMediaplayer.getPlaybackParams().setSpeed(a)));
            mMediaplayer.start();
            mContext.sendBroadcast(new Intent(BroadcastActions.PLAY_STATE_CHANGED)); // 재생상태 변경 전송
        }
    }
}
