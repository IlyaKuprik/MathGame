package com.example.mathgame;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;

public class LevelsActivity extends AppCompatActivity {

    static RecyclerView rv;

    private static SharedPreferences mSaves;

    public static ArrayList<Integer> levelsArr;

    static ContentAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_levels);
        mSaves=getSharedPreferences("mySave", Context.MODE_PRIVATE);
        loadArray();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        rv = (RecyclerView)findViewById(R.id.my_recycler_view);
        adapter = new ContentAdapter();
        rv.setAdapter(adapter);
        rv.setHasFixedSize(true);
        rv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshAdapter();
            }
        });
        rv.setLayoutManager(new GridLayoutManager(getApplicationContext(), 4));
    }

    public static void loadArray(){
        levelsArr = new ArrayList<>();
        levelsArr.add(1);
        for (int i = 1; i < MainActivity.SIZE; i++) {
            if (mSaves.contains(String.valueOf(i))){
                levelsArr.add(mSaves.getInt(String.valueOf(i),0));
            }
            else levelsArr.add(0);
        }
    }


    public static class ContentAdapter extends RecyclerView.Adapter<ViewHolder> {
        private static final int LENGTH = 40;

        ViewHolder v;

        public ContentAdapter() {

        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            v = new ViewHolder(LayoutInflater.from(parent.getContext()), parent);
            return v;
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            holder.button.setText(String.valueOf(position+1));
            loadArray();
            if(levelsArr.get(position) == 0 && position !=0) {
                Drawable back = holder.button.getBackground();
                back.setAlpha(100);
                Log.e("TA_G"," " + position);
                holder.button.setBackgroundDrawable(back);
            }
            else {
                Drawable back = holder.button.getBackground();
                back.setAlpha(255);
                holder.button.setBackgroundDrawable(back);
            }
            holder.button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loadArray();
                    if(levelsArr.get(position) == 1 || position == 0) {
                        Intent i = new Intent(v.getContext(), PlayActivity.class);
                        i.putExtra("id", position);
                        i.putExtra("numOfQuestions", 5);
                        if(position > 7 && position < 16)
                            i.putExtra("timeForLevel", 35000);
                        else if(position > 15 && position < 24)
                            i.putExtra("timeForLevel", 40000);
                        else if(position > 23 && position < 32)
                            i.putExtra("timeForLevel", 45000);
                        else if(position > 31 && position < 40)
                            i.putExtra("timeForLevel", 50000);
                        else
                            i.putExtra("timeForLevel", 30000);
                        v.getContext().startActivity(i);
                    }
                    else Toast.makeText(v.getContext(), "Уровень недоступен", Toast.LENGTH_SHORT).show();
                }
            });

        }

        @Override
        public int getItemCount() {
            return LENGTH;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        Button button;

        public ViewHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.level_item, parent, false));
            button = (Button)itemView.findViewById(R.id.button);

        }
    }

    public static void refreshAdapter(){
        rv.setAdapter(null);
        rv.setAdapter(new LevelsActivity.ContentAdapter());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        MainActivity.updateScore();
    }
}
