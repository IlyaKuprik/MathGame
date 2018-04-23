package com.example.mathgame;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class RatingListActivity extends AppCompatActivity {

    static RecyclerView rv;

    static ContentAdapter adapter;

    static ArrayList<RatingObject> ratingList;

    private static SharedPreferences mSaves;
    private static SharedPreferences.Editor editor;

    static RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating_list);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        rv = (RecyclerView)findViewById(R.id.my_recycler_view2);
        getRatingList(getApplicationContext());
        mSaves = getSharedPreferences("mySave", Context.MODE_PRIVATE);
        adapter = new ContentAdapter();
        rv.setAdapter(adapter);
        rv.setHasFixedSize(true);
        rv.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
    }

    public static void saveArray(int score){
        editor=mSaves.edit();
        for (int i = 0; i < MainActivity.SIZE+1; i++) {
            editor.putInt(String.valueOf(i),score>=0?1:0);
            score--;
        }
        editor.apply();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        queue.cancelAll(0);
    }

    public static void getRatingList(final Context context){
        ratingList = new ArrayList<>();
        queue = Volley.newRequestQueue(context);
        String url = "https://mathgame-serv.herokuapp.com/getscore/get-score.php";
        final Gson gson = new Gson();

        JsonArrayRequest request = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject jsonObject = (JSONObject) response.get(i);
                        RatingObject obj = gson.fromJson(jsonObject.toString(),RatingObject.class);
                        ratingList.add(obj);
                        if(obj.login.equals(mSaves.getString("Login",""))){
                            editor = mSaves.edit();
                            saveValues(obj);
                            editor.apply();
                            Log.d("TAG_FOR_RECORD", String.valueOf(mSaves.getInt("MAX_NUMBER",0)));
                        }
                    }
                }
                catch (JSONException e){
                    e.printStackTrace();
                }
                sortList();
                adapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "Ошибка. Проверьте подключение у интернету",Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(request);
    }

    public static void sortList(){
        Collections.sort(ratingList,new Comparator<RatingObject>() {
            @Override
            public int compare(RatingObject o1, RatingObject o2) {
                if (o1.record + o1.levels > o2.record + o2.levels) return -1;
                else  if (o1.record + o1.levels < o2.record + o2.levels) return 1;
                else return 0;
            }
        });
    }

    private static void saveValues(RatingObject obj) {
        mSaves.edit().putInt("MAX_NUMBER",obj.record).apply();
        mSaves.edit().putInt("SCORE",obj.levels).apply();
        saveArray(obj.levels);
    }

    public static class ContentAdapter extends RecyclerView.Adapter<ViewHolder> {
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
            Log.d("TAG__", String.valueOf(ratingList.size()));
            for (int i = 0; i < ratingList.size(); i++) {
                Log.d("TAG__", String.valueOf(i));
                holder.login.setText(String.valueOf(ratingList.get(position).login));
                holder.record.setText(String.valueOf(ratingList.get(position).record));
                holder.levels.setText(String.valueOf(ratingList.get(position).levels));
            }
        }

        @Override
        public int getItemCount() {
            return ratingList.size();
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

            TextView login, record, levels;

        public ViewHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.rating_item, parent, false));
            login = (TextView)itemView.findViewById(R.id.login_name);
            record = (TextView)itemView.findViewById(R.id.rating);
            levels = (TextView)itemView.findViewById(R.id.level);
        }
    }

    public static class RatingObject{
        String login;
        int record;
        int levels;
        public RatingObject(String login, int rating, int levels){
            this.login = login;
            this.record = rating;
            this.levels = levels;
        }
    }

}
