package com.example.mathgame;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Rating;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class MainActivity extends AppCompatActivity {

    public static int SIZE;

    static TextView scoreTV;
    static SharedPreferences mSaves;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        SIZE = 40;
        scoreTV = (TextView)findViewById(R.id.scoreTextView);
        mSaves = getSharedPreferences("mySave", Context.MODE_PRIVATE);
    }

    public void onStartButtonClick(View view) {
        Intent i = new Intent(this,LevelsActivity.class);
        startActivity(i);
    }

    public static void updateScore(){
        int record = 0;
        if (mSaves.contains("MAX_NUMBER")){
            record = mSaves.getInt("MAX_NUMBER",0);
        }
        scoreTV.setText(String.valueOf(record));
    }

    public void onInfinityModeButtonClick(View view) {
        Intent i = new Intent(this,InfinityPlayModeActivity.class);
        startActivity(i);
    }

    public void onRatingButtonClickListener(View view) {
        if (!mSaves.contains("Login")){
            Intent i = new Intent(this,LoginActivity.class);
            startActivity(i);
        }
        else {
            Intent i = new Intent(this,RatingListActivity.class);
            startActivity(i);
        }
    }

    public static void sendRecord(Context context){
        int level = 0;
        int record = 0;
        String login = mSaves.getString("Login","");
        if (mSaves.contains("SCORE")) level = mSaves.getInt("SCORE",0);
        if (mSaves.contains("MAX_NUMBER")) record = mSaves.getInt("MAX_NUMBER",0);

        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "https://mathgame-serv.herokuapp.com/addscore/add-score.php?login=" + login +
                "&record=" + record +
                "&levels=" + level;

        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        queue.add(request);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mSaves.contains("Login")) {
            TextView tv = (TextView)findViewById(R.id.nameTextView);
            tv.setText("Здравствуйте, \n" + mSaves.getString("Login",""));
            sendRecord(getApplicationContext());
        }
        updateScore();
        sendRecord(getApplicationContext());
    }
}
