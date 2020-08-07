package com.social.englishclass;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TeacherPageAdapter extends RecyclerView.Adapter<TeacherPageAdapter.TeacherViewHolder> {

    private static Context mContext;
    private List mUsers;

    public TeacherPageAdapter(Context context, List users){
        mContext = context;
        mUsers = users;
//        super(context, uploads);
    }//Adapter 끝

    @NonNull
    @Override
    public TeacherPageAdapter.TeacherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.teacherpage_item, parent, false);
        return new TeacherViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull TeacherPageAdapter.TeacherViewHolder holder, int position) {
        holder. mTxtTitle.setText(mUsers.get(position).toString());
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class TeacherViewHolder extends RecyclerView.ViewHolder {

        private TextView mTxtTitle;

        private int mPosition;

        private TeacherViewHolder(final View view) {
            super(view);

            mTxtTitle = (TextView) view.findViewById(R.id.teacherpage_text);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    String login_id = mTxtTitle.getText().toString();
//리사이클뷰 어댑터안의 뷰에서 인텐트 전달
                    Intent intent = new Intent(view.getContext(), Chart.class);
                    intent.putExtra("login_id", login_id);
                    mContext.startActivity(intent);
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

    }}
