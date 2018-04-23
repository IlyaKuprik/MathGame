package com.example.mathgame;

import android.app.Activity;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Rating;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends Activity {

    EditText loginEditText,passwordEditText;

    private static SharedPreferences mSaves;
    private static SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mSaves=getSharedPreferences("mySave", Context.MODE_PRIVATE);

        loginEditText = (EditText)findViewById(R.id.login_editText);
        passwordEditText = (EditText)findViewById(R.id.password_editText);
    }

    public void onRegisterTextViewClick(View view) {
        Intent i = new Intent(this,RegisterActivity.class);
        startActivity(i);
    }

    public void onLoginClick(View view) {
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        final String login = loginEditText.getText().toString();
        if (login.length() >= 6) {
            String password = passwordEditText.getText().toString();
            String url = "https://mathgame-serv.herokuapp.com/login/login.php?login=" + login + "&password=" + password;

            StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    if (response.equals("true")) {
                        Toast.makeText(LoginActivity.this, "Вы успешно вошли", Toast.LENGTH_SHORT).show();
                        editor = mSaves.edit();
                        editor.putString("Login",login);
                        editor.apply();
                        Intent i = new Intent(LoginActivity.this, RatingListActivity.class);
                        startActivity(i);
                    } else if (response.equals("false")) {
                        Toast.makeText(LoginActivity.this, "Пользователь не существует, либо Вы ввесли не верный пароль.", Toast.LENGTH_SHORT).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(LoginActivity.this, "Ошибка. Проверьте подключение к интернету.", Toast.LENGTH_SHORT).show();
                }
            });
            queue.add(request);
        }
        else Toast.makeText(this, "Длина логина должна быть не меньше 6 символов", Toast.LENGTH_SHORT).show();
    }
}
