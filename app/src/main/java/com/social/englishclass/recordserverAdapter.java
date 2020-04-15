package com.social.englishclass;

import android.content.ContentUris;
import android.content.Context;
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

import com.squareup.picasso.Picasso;

import java.util.List;

public class recordserverAdapter extends RecyclerView.Adapter<recordserverAdapter.AudioViewHolder> {
    private Context mContext;
    private List<Upload> mUploads;

    public recordserverAdapter(Context context, List<Upload> uploads){
        mContext = context;
        mUploads = uploads;
//        super(context, uploads);
    }//recordserverAdapter 끝


    @Override
    public AudioViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.listitem_audio, parent, false);
        return new AudioViewHolder(v);
    }

    @Override
    public void onBindViewHolder( AudioViewHolder holder, int position) {
        Upload uploadCurrent = mUploads.get(position);
        holder. mTxtTitle.setText(uploadCurrent.getName());
    }

    @Override
    public int getItemCount() {
        return mUploads.size();
    }

    public static class AudioItem {
        public long mId; // 오디오 고유 ID
        public long mAlbumId; // 오디오 앨범아트 ID
        public String mTitle; // 타이틀 정보
        public String mArtist; // 아티스트 정보
        public String mAlbum; // 앨범 정보
        public long mDuration; // 재생시간
        public String mDataPath; // 실제 데이터위치
    }

    public class AudioViewHolder extends RecyclerView.ViewHolder {
        private final Uri artworkUri = Uri.parse("content://media/external/audio/albumart");
        private ImageView mImgAlbumArt;
        private TextView mTxtTitle;
        private TextView mTxtSubTitle;
        private TextView mTxtDuration;
        private AudioAdapter.AudioItem mItem;
        private int mPosition;

        private AudioViewHolder(View view) {
            super(view);
            mImgAlbumArt = (ImageView) view.findViewById(R.id.img_albumart);
            mTxtTitle = (TextView) view.findViewById(R.id.txt_title);
            mTxtSubTitle = (TextView) view.findViewById(R.id.txt_sub_title);
            mTxtDuration = (TextView) view.findViewById(R.id.txt_duration);
/*            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    AudioApplication.getInstance().getServiceInterface().setPlayList(getAudioIds()); // 재생목록등록
                    AudioApplication.getInstance().getServiceInterface().play(mPosition); // 선택한 오디오재생
                    try {
                        englishlesson.recordlistdialog.dismiss();  //녹음 리스트 다이얼로그 끄기
                    }catch (Exception e){e.printStackTrace();}


                }
            });

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

        public void setAudioItem(AudioAdapter.AudioItem item, int position) {
            mItem = item;
            mPosition = position;
            mTxtTitle.setText(item.mTitle);
            mTxtSubTitle.setText(item.mArtist + "(" + item.mAlbum + ")");
            mTxtDuration.setText(DateFormat.format("mm:ss", item.mDuration));
            Uri albumArtUri = ContentUris.withAppendedId(artworkUri, item.mAlbumId);
            Picasso.with(itemView.getContext()).load(albumArtUri).error(R.drawable.music).into(mImgAlbumArt);
        }
    }
}
