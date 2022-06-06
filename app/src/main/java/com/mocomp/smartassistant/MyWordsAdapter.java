package com.mocomp.smartassistant;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MyWordsAdapter extends RecyclerView.Adapter<MyWordsAdapter.itemHolder> {

    List<Word> myWords;
    Context mContext;
    private Cursor mCursor;
    boolean flag=false,mBooleanIsPressed = false;
    private final Handler handler = new Handler();
    int Id;
    long colId;

    public MyWordsAdapter(Context mContext, List<Word> myWords,Cursor mCursor) {
        this.mContext = mContext;
        this.myWords = myWords;
        this.mCursor = mCursor;
    }

    @NonNull
    @Override
    public itemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new itemHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.my_words_item, parent, false));
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull itemHolder holder, @SuppressLint("RecyclerView") int position) {
        if (!mCursor.moveToPosition(position)) {
            return;
        }

//      Word list = myWords.get(position);

        String eng = mCursor.getString(mCursor.getColumnIndex(GroceryContract.GroceryEntry.COLUMN_ENGLISH));
        String ar = mCursor.getString(mCursor.getColumnIndex(GroceryContract.GroceryEntry.COLUMN_ARABIC));
        long id = mCursor.getLong(mCursor.getColumnIndex(GroceryContract.GroceryEntry._ID));

        holder.engWord.setText(eng);
        holder.arWord.setText(ar);
        holder.arWord.setVisibility(View.INVISIBLE);
        //holder.arCard.setVisibility(View.INVISIBLE);
        holder.itemView.setTag(id);

        holder.engWord.setOnLongClickListener(v -> {
            holder.arWord.setVisibility(View.VISIBLE);
            //holder.arCard.setVisibility(View.VISIBLE);
            flag=true;
            return false;
        });
        holder.translateCard.setOnLongClickListener(v -> {
            holder.arWord.setVisibility(View.VISIBLE);
            //holder.arCard.setVisibility(View.VISIBLE);
            flag=true;
            return false;
        });
        holder.arWord.setOnLongClickListener(v -> {
            holder.arWord.setVisibility(View.VISIBLE);
            //holder.arCard.setVisibility(View.VISIBLE);
            flag=true;
            return false;
        });


//        holder.engWord.setOnTouchListener((v, event) -> {
//            if(event.getAction() == MotionEvent.ACTION_DOWN) {
//                // Execute your Runnable after 1000 milliseconds = 1 second.
//                handler.postDelayed(holder.runnable, 1000);
//                mBooleanIsPressed = true;
//                Id=position;
//                colId=id;
//            }
//            if(event.getAction() == MotionEvent.ACTION_UP) {
//                if(mBooleanIsPressed) {
//                    mBooleanIsPressed = false;
//                    handler.removeCallbacks(holder.runnable);
//                }
//            }
//            return false;
//        });

        holder.translateCard.setOnClickListener(v ->{
            if (flag){
                holder.arWord.setVisibility(View.INVISIBLE);
                //holder.arCard.setVisibility(View.INVISIBLE);
                flag=false;
            }
        });

        holder.engWord.setOnClickListener(v ->{
            if (flag){
                holder.arWord.setVisibility(View.INVISIBLE);
                //holder.arCard.setVisibility(View.INVISIBLE);
                flag=false;
            }else {
                Intent intent = new Intent("custom-message");
                intent.putExtra("word",eng);
                intent.putExtra("flag","sound");
                LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
            }

        });

        holder.edit.setOnClickListener(v ->{
            Intent intent = new Intent("custom-message");
            intent.putExtra("id", position);
            intent.putExtra("colId", id);
            intent.putExtra("flag","edit");
            LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
        });
    }

    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }

    public class itemHolder extends RecyclerView.ViewHolder {
        TextView engWord,arWord;
        CardView translateCard,arCard;
        ImageView edit;
        public itemHolder(@NonNull View itemView) {
            super(itemView);
            arCard = itemView.findViewById(R.id.arCard);
            edit = itemView.findViewById(R.id.edit);
            engWord = itemView.findViewById(R.id.engWord);
            arWord = itemView.findViewById(R.id.arWord);
            translateCard = itemView.findViewById(R.id.translateCard);

        }

        private final Runnable runnable = new Runnable() {
            public void run() {
                if(mBooleanIsPressed == true){
                    Log.d("TAG", "long click");
                    Intent intent = new Intent("custom-message");
                    intent.putExtra("id", Id);
                    intent.putExtra("colId", colId);
                    intent.putExtra("flag","edit");
                    LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
                }

            }
        };

    }
    public void swapCursor(Cursor newCursor) {
        if (mCursor != null) {
            mCursor.close();
        }

        mCursor = newCursor;

        if (newCursor != null) {
            notifyDataSetChanged();
        }
    }
}
