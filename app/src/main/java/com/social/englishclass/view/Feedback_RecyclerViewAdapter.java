package com.social.englishclass.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.social.englishclass.ChatDTO;
import com.social.englishclass.R;

import java.util.List;

public class Feedback_RecyclerViewAdapter extends RecyclerView.Adapter<Feedback_RecyclerViewAdapter.ViewHolder> {

    private static Context mContext;
    private List<ChatDTO> mChatDTO;

    public Feedback_RecyclerViewAdapter(Context context, List<ChatDTO> chatdto){
        mContext = context;
        mChatDTO = chatdto;
    }//Feedback_RecyclerViewAdapter 끝


    @Override
    public Feedback_RecyclerViewAdapter.ViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.feedback_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder( Feedback_RecyclerViewAdapter.ViewHolder holder, int position) {

        ChatDTO chatDTOCurrent = mChatDTO.get(position);
        holder.mFeedback_text.setText(chatDTOCurrent.getUserName() + " : " + chatDTOCurrent.getMessage());

    }

    @Override
    public int getItemCount() {
        return mChatDTO.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView mFeedback_text;

        private ViewHolder(final View view){
            super(view);

            mFeedback_text = (TextView) view.findViewById(R.id.feedback_text);
        }
    }
}
