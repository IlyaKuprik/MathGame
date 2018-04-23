package com.example.mathgame;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class InfinityPlayModeActivity extends AppCompatActivity {

    TextView timerTextView, questionTextView, counterTextView;
    EditText answer;
    Button apply, exit;

    public static long TIME;
    public static int ANSWER;
    public static int COUNTER;
    public static int MAX_NUMBER;

    MyAsyncTask task;

    private static SharedPreferences mSaves;
    private static SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        initViews();
        mSaves=getSharedPreferences("mySave", Context.MODE_PRIVATE);

        if (mSaves.contains("MAX_NUMBER"))
            MAX_NUMBER = mSaves.getInt("MAX_NUMBER",0);
        else MAX_NUMBER = 0;

        counterTextView.setText("0");
        TIME = 15;
        startTimer();
        getQuestion();

        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (answer.getText() != null) {
                    if (String.valueOf(ANSWER).equals(answer.getText().toString())) {
                        COUNTER++;
                        TIME += 5;//изменить в зависимости от сложности
                        counterTextView.setText(String.valueOf(COUNTER));
                        getQuestion();
                    }
                    else Toast.makeText(InfinityPlayModeActivity.this,"Неверно",Toast.LENGTH_SHORT).show();
                }
                else Toast.makeText(InfinityPlayModeActivity.this, "Пожалуйста, введите ответ", Toast.LENGTH_SHORT).show();
                answer.setText("");
            }
        });

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void getQuestion() {
        int operation = randOperation();
        int value1;
        int value2;
        switch (operation){
            case 0:
                value1 =(int)(Math.random()*15*(COUNTER + 1) +(int)(Math.random()*5*(COUNTER)));
                value2 =(int)(Math.random()*10*(COUNTER + 1) +(int)(Math.random()*5*(COUNTER)));
                questionTextView.setText(value1 + " + " + value2);
                ANSWER = value1 + value2;
                break;
            case 1:
                value1 =(int)(Math.random()*15*(COUNTER + 1) + 15);
                value2 =(int)(Math.random()*25*(COUNTER + 1) + 15);
                questionTextView.setText(Math.max(value1,value2) + " - " + Math.min(value1,value2));
                ANSWER = Math.max(value1,value2) - Math.min(value1,value2);
                break;
            case 2:
                value1 =(int)(Math.random()*3*((COUNTER+2)/2) + 1);
                value2 =(int)(Math.random()*3*((COUNTER+2)/2) + 1);
                questionTextView.setText(value1*value2 + " / " + value1);
                ANSWER = value2;
                break;
            case 3:
                value1 =(int)(Math.random()*3*((COUNTER+2)/2) + 1 + (COUNTER/4));
                value2 =(int)(Math.random()*3*((COUNTER+2)/2) + 1 + (COUNTER/4));
                questionTextView.setText(value1 + " * " + value2);
                ANSWER = value2*value1;
                break;
        }

    }

    public int randOperation(){
        return (int)(Math.random()*4);
    }

    public void startTimer(){
        task = new MyAsyncTask();
        task.execute();
    }

    public void initViews(){
        questionTextView = (TextView)findViewById(R.id.task_textview);
        counterTextView = (TextView)findViewById(R.id.counter);
        timerTextView = (TextView) findViewById(R.id.timer_textview);
        answer =(EditText)findViewById(R.id.answer_editText);
        apply = (Button)findViewById(R.id.apply_answer_button);
        exit = (Button)findViewById(R.id.exit);
    }

    public void showAlert(){
        saveMaxNumber(COUNTER>MAX_NUMBER?COUNTER:MAX_NUMBER);
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(InfinityPlayModeActivity.this);
        dialogBuilder.setTitle("Время истекло")
                .setMessage("Закончить или повторить?")
                .setCancelable(false)
                .setPositiveButton("Закончить", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        onBackPressed();
                    }
                })
                .setNegativeButton("Повторить", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        TIME = 15;
                        saveMaxNumber(COUNTER>MAX_NUMBER?COUNTER:MAX_NUMBER);
                        startTimer();
                        getQuestion();
                        COUNTER = 0;
                        counterTextView.setText("0");
                    }
                });
        AlertDialog alert = dialogBuilder.create();
        alert.show();
    }

    public void saveMaxNumber(int value){
        editor = mSaves.edit();
        editor.putInt("MAX_NUMBER",value);
        editor.apply();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        TIME = 0;
        task.cancel(true);
        saveMaxNumber(COUNTER>MAX_NUMBER?COUNTER:MAX_NUMBER);
        MainActivity.updateScore();
        COUNTER = 0;
    }

    private class MyAsyncTask extends AsyncTask<Void,Integer,Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            while (TIME > 0) {
                TIME--;
                publishProgress((int)TIME);
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            showAlert();
            saveMaxNumber(COUNTER>MAX_NUMBER?COUNTER:MAX_NUMBER);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            timerTextView.setText(Integer.toString(values[0]));
        }
    }
}
