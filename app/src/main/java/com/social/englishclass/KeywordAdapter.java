package com.social.englishclass;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class KeywordAdapter  extends CursorRecyclerViewAdapter<RecyclerView.ViewHolder> {
    public KeywordAdapter(Context context, Cursor cursor) {
        super(context, cursor);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, Cursor cursor) {
        KeywordItem audioItem = KeywordItem.bindCursor(cursor);
        ((AudioViewHolder) viewHolder).setAudioItem(audioItem, cursor.getPosition());


    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.keyword_item, parent, false);
        return new AudioViewHolder(v);
    }
    public static class KeywordItem {
        public long mId; // 오디오 고유 ID
        public String mTitle; // 타이틀 정보
        public String mDataPath; // 실제 데이터위치
        public long mAlbumId; // 오디오 앨범아트 ID

        public static KeywordItem bindCursor(Cursor cursor) {
            KeywordItem audioItem = new KeywordItem();
            audioItem.mId = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.AudioColumns._ID));
            audioItem.mTitle = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.TITLE));
            audioItem.mDataPath = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DATA));
            return audioItem;
        }
    }

    public ArrayList<Long> getAudioIds() {
        int count = getItemCount();
        Log.d("리사이클뷰 아이탬 갯수", "       " + count );
        ArrayList<Long> audioIds = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            audioIds.add(getItemId(i));
        }
        return audioIds;
    }
    private class AudioViewHolder extends RecyclerView.ViewHolder {
        private final Uri artworkUri = Uri.parse("/storage/emulated/0/englishclass/lesson1keyword/picture");
//        private final Uri artworkUri = Uri.parse("content://media/external/audio/albumart");
        private TextView mTxtTitle;
        private ImageView mImageView;
        private KeywordItem mItem;
        private int mPosition;

        private AudioViewHolder(View view) {
            super(view);

            mTxtTitle = (TextView) view.findViewById(R.id.keyword_text);
            mImageView = (ImageView)view.findViewById(R.id.keyword_image);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    AudioApplication.getInstance().getServiceInterface().setPlayList(getAudioIds()); // 재생목록등록
                    AudioApplication.getInstance().getServiceInterface().play(mPosition); // 선택한 오디오재생
//                    AudioApplication.getInstance().getServiceInterface().deletedialog(mPosition);

                }
            });


        }

        public void setAudioItem(KeywordItem item, int position) {
            mItem = item;
            mPosition = position;
            mTxtTitle.setText(item.mTitle);
            Uri albumArtUri = ContentUris.withAppendedId(artworkUri, item.mAlbumId);
            Picasso.with(itemView.getContext()).load( Uri.parse("/storage/emulated/0/englishclass/lesson1keyword/picture/grade.jpg")).error(R.drawable.music).into(mImageView);
//            Picasso.with(itemView.getContext()).load(albumArtUri).error(R.drawable.music).into(mImageView);

        }
    }
}
