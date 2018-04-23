package com.example.mathgame;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class RegisterActivity extends AppCompatActivity {

    EditText loginEditText, passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        loginEditText = (EditText) findViewById(R.id.login_editText_);
        passwordEditText = (EditText) findViewById(R.id.password_editText_);
    }

    public void onLoginTextViewClick(View view) {
        onBackPressed();
    }

    public void onRegisterClick(View view) {
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        final String login = loginEditText.getText().toString();
        if (login.length() >= 6 && login.length() <=12) {
            String password = passwordEditText.getText().toString();
            String url = "https://mathgame-serv.herokuapp.com/login/register.php?login=" + login + "&password=" + password;

            StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    if (response.equals("true")) {
                        Toast.makeText(RegisterActivity.this, "Вы успешно зарегестрировались, войдите,чтобы продолжить", Toast.LENGTH_SHORT).show();
                        sendRecord(getApplicationContext(),login);
                        onBackPressed();
                    } else if (response.equals("false")) {
                        Toast.makeText(RegisterActivity.this, "Логин уже зарегестрирован", Toast.LENGTH_SHORT).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(RegisterActivity.this, "Ошибка. Проверьте подключение к интернету.", Toast.LENGTH_SHORT).show();
                }
            });
            queue.add(request);
        } else
            Toast.makeText(this, "Длина логина должна быть не менее 6 символов и не более 12 символов", Toast.LENGTH_SHORT).show();
    }

    public void sendRecord(Context context,String login){
        int level = 0;
        int record = 0;
        SharedPreferences mSaves = getSharedPreferences("mySave",MODE_PRIVATE);
        if (mSaves.contains("SCORE")) level = mSaves.getInt("SCORE",0);
        if (mSaves.contains("MAX_NUMBER")) record = mSaves.getInt("MAX_NUMBER",0);

        Log.d("SEND_TAG","" + level + " " + record);

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
}
