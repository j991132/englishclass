package com.social.englishclass;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class ChatAdapter extends BaseAdapter {
    private Context context;
    private int layout;
    private ArrayList chatData;
    private LayoutInflater inflater;
    private int a;
    public ChatAdapter(Context applicationContext, int talklist, ArrayList list, int a){
        this.context = applicationContext;
        this.layout = talklist;
        this.chatData = list;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.a= a;
    }


    @Override
    public int getCount() {
        return chatData.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //첫항목을 그릴때만 inflate 함 다음거부터는 매개변수로 넘겨줌 (느리기때문) : recycle이라고 함
        ViewHolder holder;

        if(convertView == null){
//어떤 레이아웃을 만들어 줄 것인지, 속할 컨테이너, 자식뷰가 될 것인지
            convertView = inflater.inflate(layout, parent, false); //아이디를 가지고 view를 만든다
            holder = new ViewHolder();
            holder.samimage = (ImageView)convertView.findViewById(R.id.samimage);
            holder.my_msg = (TextView)convertView.findViewById(R.id.text_chat);
            holder.text_layout = (LinearLayout)convertView.findViewById(R.id.chat_layout);
            holder.text_parent_layout = (LinearLayout)convertView.findViewById(R.id.chat_parent_layout);

            convertView.setTag(holder);
        }else{
            holder= (ViewHolder) convertView.getTag();
        }
        holder.samimage.setImageResource(R.drawable.sam);
        holder.my_msg.setText(chatData.get(position).toString());

//누군지 판별
        if((position%2)==0){
            Log.e("포지션",""+position);
            holder.text_parent_layout.setHorizontalGravity(Gravity.RIGHT);
//            holder.my_msg.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
            holder.samimage.setVisibility(View.GONE);
            holder.text_layout.setBackgroundResource(R.drawable.mychat);

        }else{
            Log.e("포지션",""+position);
            holder.samimage.setVisibility(View.VISIBLE);
            holder.text_parent_layout.setGravity(Gravity.LEFT);
//            holder.my_msg.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);
            holder.text_layout.setBackgroundResource(R.drawable.yourchat);
        }


        return convertView;
    }

    //뷰홀더패턴
    public class ViewHolder{
        ImageView samimage;
        TextView my_msg;
        LinearLayout text_layout;
        LinearLayout text_parent_layout;
    }
}

