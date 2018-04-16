package com.example.mathgame;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

public class LevelsActivity extends AppCompatActivity {

    RecyclerView rv;

    private static SharedPreferences mSaves;

    public static int COMPLETED_LEVELS[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_levels);
        mSaves=getSharedPreferences("mySave", Context.MODE_PRIVATE);
        loadArray();

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        rv = (RecyclerView)findViewById(R.id.my_recycler_view);
        ContentAdapter adapter = new ContentAdapter();
        rv.setAdapter(adapter);
        rv.setHasFixedSize(true);
        rv.setLayoutManager(new GridLayoutManager(getApplicationContext(), 4));
    }

    public static void loadArray(){
        COMPLETED_LEVELS = new int[MainActivity.SIZE];
        for (int i = 0; i < MainActivity.SIZE; i++) {
            if (mSaves.contains(String.valueOf(i))){
                COMPLETED_LEVELS[i] = mSaves.getInt(String.valueOf(i),0);
            }
            else COMPLETED_LEVELS[i] = 0;
        }
    }


    public static class ContentAdapter extends RecyclerView.Adapter<ViewHolder> {
        private static final int LENGTH = 40;

        public ContentAdapter() {
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()), parent);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            holder.button.setText(String.valueOf(position));
            holder.button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loadArray();
                    if(COMPLETED_LEVELS[position] == 1 || position == 0) {
                        Intent i = new Intent(v.getContext(), PlayActivity.class);
                        i.putExtra("id", position);
                        i.putExtra("numOfQuestions", 5);
                        if(position > 19){
                            i.putExtra("timeForLevel", 50000);
                        }
                        else{
                            i.putExtra("timeForLevel", 30000);
                        }

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
}
