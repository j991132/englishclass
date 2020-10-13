package com.social.englishclass.view;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.social.englishclass.ChatDTO;
import com.social.englishclass.R;
import com.social.englishclass.recordserverplay;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Feedback_RecyclerViewAdapter extends RecyclerView.Adapter<Feedback_RecyclerViewAdapter.ViewHolder> {

    private static Context mContext;
    private List<ChatDTO> mChatDTO;
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();
    private String edittext;

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
        if(mChatDTO != null) {
            return mChatDTO.size();
        }else{
            return 0;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView mFeedback_text;
        private ImageButton feedback_edit_btn;
        private String editkey;

        private ViewHolder(final View view){
            super(view);

            mFeedback_text = (TextView) view.findViewById(R.id.feedback_text);
            feedback_edit_btn = (ImageButton)view.findViewById(R.id.feedback_edit_btn);

            feedback_edit_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.e(" 수정하기 버튼 클릭   ", "수정버튼 눌림");

                    Query q = databaseReference.child("chat").child(recordserverplay.filename).orderByChild("message").equalTo(mFeedback_text.getText().toString().substring(mFeedback_text.getText().toString().lastIndexOf(": ")+2));

                    q.addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                            Log.e("쿼리2",""+dataSnapshot.getKey());
                            editkey = dataSnapshot.getKey();
                        }

                        @Override
                        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                        }

                        @Override
                        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                        }

                        @Override
                        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

//다이얼로그 띄우기
                    Dialog feedback_dialog = new Dialog(view.getContext());
                    feedback_dialog.setContentView(R.layout.feedback_edit_dialog);

                    EditText feedback_edit_text = (EditText)feedback_dialog.findViewById(R.id.feedback_edit_text);
                    Button feedback_text_del_btn = (Button)feedback_dialog.findViewById(R.id.feedback_text_del_btn);
                    Button feedback_text_edit_ok = (Button)feedback_dialog.findViewById(R.id.feedback_text_edit_ok);

                    feedback_edit_text.setText(mFeedback_text.getText().toString().substring(mFeedback_text.getText().toString().lastIndexOf(": ")+2));

                    feedback_text_del_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Log.e("   버튼 클릭   ", "삭제버튼 눌림");
                            feedback_dialog.dismiss();
                        }
                    });

                    feedback_text_edit_ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Log.e("   버튼 클릭   ", "수정 완료 버튼 눌림");
                            edittext = feedback_edit_text.getText().toString();
                            Log.e("   로그인 아이디   ", ""+mFeedback_text.getText().toString().substring(0, mFeedback_text.getText().toString().lastIndexOf(" : ")));
                            Log.e("   텍스트 내용   ", ""+edittext);
                            ChatDTO chat = new ChatDTO(mFeedback_text.getText().toString().substring(0, mFeedback_text.getText().toString().lastIndexOf(" : ")), edittext);
//                            databaseReference.child("chat").child(recordserverplay.filename).setValue(chat);
//                            String key = databaseReference.child("chat").child(recordserverplay.filename).getKey();



                            Map<String,Object> childUpdates = new HashMap<>();
                            childUpdates.put(editkey, chat);
//                            Log.e("   키값   ", ""+key);
                            databaseReference.child("chat").child(recordserverplay.filename).updateChildren(childUpdates);
                            feedback_dialog.dismiss();
                        }
                    });
                    feedback_dialog.show();
                }
            });
        }
    }
}
