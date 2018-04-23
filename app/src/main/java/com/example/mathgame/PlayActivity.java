package com.example.mathgame;

        import android.content.Context;
        import android.content.DialogInterface;
        import android.content.SharedPreferences;
        import android.os.CountDownTimer;
        import android.support.v7.app.AlertDialog;
        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.util.Log;
        import android.view.View;
        import android.view.WindowManager;
        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.TextView;
        import android.widget.Toast;

public class PlayActivity extends AppCompatActivity {
    public static int DIFFICULT;
    public static int OPERATION;
    public static int ANSWER;
    public static int COUNTER;
    public static int ID;
    public static int NUMBER_OF_QUESTION;
    public static long TIME_FOR_LEVEL;

    public static int COMPLETED_LEVELS[];

    TextView timerTextView, questionTextView, counterTextView;
    EditText answer;
    Button apply, exit;

    CountDownTimer timer;

    private static SharedPreferences mSaves;
    private static SharedPreferences.Editor editor;

    public static int getSCORE() {
        int score = 0;
        if (mSaves.contains("SCORE")){
            score = mSaves.getInt("SCORE",0);
        }
        return score;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        setFullScreen();

        mSaves = getSharedPreferences("mySave", Context.MODE_PRIVATE);
        loadArray();
        initViews();
        initValues();
        setupDifficult();

        startTimer(TIME_FOR_LEVEL);
        getQuestion();

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = answer.getText().toString();
                if(answer.getText()!=null) {
                    if (s.equals(String.valueOf(ANSWER))) {
                        COUNTER++;
                        counterTextView.setText(COUNTER + "/" + NUMBER_OF_QUESTION);
                        if (COUNTER >= NUMBER_OF_QUESTION) {
                            finishLevel();
                        } else getQuestion();
                    } else printOnScreen("Неверно!");
                }
                else Toast.makeText(PlayActivity.this,"Неверно",Toast.LENGTH_SHORT).show();
                answer.setText("");
            }
        });
    }

    public void finishLevel(){
        timer.cancel();
        COUNTER = 0;
        saveScore((COMPLETED_LEVELS[ID + 1] == 0 ? 1 : 0));
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(PlayActivity.this);
        dialogBuilder.setTitle("Поздравляем!")
                .setMessage("Вы прошли уровень!")
                .setCancelable(false)
                .setPositiveButton("Далее", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        onBackPressed();
                    }
                });
        AlertDialog alert = dialogBuilder.create();
        alert.show();
        COMPLETED_LEVELS[ID + 1] = 1;
        if(ID == 39)
            printOnScreen("Поздравляем!Вы прошли игру!");
        saveArray();
    }

    private void setFullScreen() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    public void getQuestion(){
        int value1;
        int value2;
        switch (OPERATION){
            case 0:
                value1 = randForPlusAndMinus();
                value2 = randForPlusAndMinus();
                ANSWER = value1 + value2;
                questionTextView.setText(value1 + " + " + value2);
                break;
            case 1:
                value1 = randForPlusAndMinus();
                value2 = randForPlusAndMinus();
                int val = value1;
                value1 = Math.max(value1,value2);
                value2 = Math.min(val,value2)/(DIFFICULT<=3?1:(DIFFICULT-2))*(DIFFICULT<=6?1:DIFFICULT-6);
                ANSWER = value1 - value2;
                questionTextView.setText(value1 + " - " + value2);
                break;
            case 2:
                value1 = randForMultiply();
                value2 = randForMultiply();
                ANSWER = value1;
                questionTextView.setText(value1*value2 + " / " + value2);
                break;
            case 3:
                value1 = randForProduction();
                value2 = randForProduction();
                ANSWER = value1 * value2;
                questionTextView.setText(value1 + " * " + value2);
                break;
        }
    }

    public int randForPlusAndMinus(){
        switch (DIFFICULT){
            case 0: return (int)(Math.random()*9 + 2); // 1..9
            case 1: return (int)(Math.random()*50 + 25); // 25..75
            case 2: return (int)(Math.random()*75 + 75); // 75..150
            case 3: return (int)(Math.random()*200 + 100); //100..300
            case 4: return (int)(Math.random()*150 + 250);// 250..400
            case 5: return (int)(Math.random()*250 + 350);// 350 .. 600
            case 6: return (int)(Math.random()*200 + 400); // 600..800
            case 7: return (int)(Math.random()*150 + 750); //750..900
            case 8: return (int)(Math.random()*200 + 900);//900..1100
            case 9: return (int)(Math.random()*400 + 1100);//1100..1500
            default: return 0;
        }
    }

    public int randForMultiply(){
        switch (DIFFICULT){
            case 0: return (int)(Math.random()*8 + 1);
            case 1: return (int)(Math.random()*8 + 5);
            case 2: return (int)(Math.random()*8 + 5);
            case 3: return (int)(Math.random()*8 + 7);
            case 4: return (int)(Math.random()*8 + 9);
            case 5: return (int)(Math.random()*8 + 9);
            case 6: return (int)(Math.random()*8 + 15);
            case 7: return (int)(Math.random()*8 + 17);
            case 8: return (int)(Math.random()*7 + 20);
            case 9: return (int)(Math.random()*7 + 25);
            default: return 1;
        }
    }

    public int randForProduction(){
        switch (DIFFICULT){
            case 0: return (int)(Math.random()*8 + 1);
            case 1: return (int)(Math.random()*10 + 5);
            case 2: return (int)(Math.random()*10 + 8);
            case 3: return (int)(Math.random()*10 + 10);
            case 4: return (int)(Math.random()*10 + 10);
            case 5: return (int)(Math.random()*10 + 12);
            case 6: return (int)(Math.random()*10 + 15);
            case 7: return (int)(Math.random()*10 + 17);
            case 8: return (int)(Math.random()*10 + 21);
            case 9: return (int)(Math.random()*10 + 27);
            default: return 1;
        }
    }

    public void initViews(){
        questionTextView = (TextView)findViewById(R.id.task_textview);
        counterTextView = (TextView)findViewById(R.id.counter);
        timerTextView = (TextView) findViewById(R.id.timer_textview);
        answer =(EditText)findViewById(R.id.answer_editText);
        apply = (Button)findViewById(R.id.apply_answer_button);
        exit = (Button)findViewById(R.id.exit);
    }

    public void initValues() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            ID = extras.getInt("id");
            NUMBER_OF_QUESTION = extras.getInt("numOfQuestions");
            TIME_FOR_LEVEL = extras.getInt("timeForLevel");
        }
        else {
            ID = 0;
            NUMBER_OF_QUESTION = 5;
            TIME_FOR_LEVEL = 5000;
        }
    }

    private void setupDifficult() {
        DIFFICULT = ID / 4;
        OPERATION = ID % 4;
    }

    public void startTimer(long milliseconds){
        timer =  new CountDownTimer(milliseconds, 1000) {
            public void onTick(long millisUntilFinished) {
                timerTextView.setText("" + millisUntilFinished / 1000);
            }
            public void onFinish() {
                timerTextView.setText("0");
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(PlayActivity.this);
                dialogBuilder.setTitle("Время истекло")
                        .setMessage("Повторить попытку?")
                        .setCancelable(false)
                        .setPositiveButton("Повторить", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                COUNTER = 0;
                                counterTextView.setText("0/" + NUMBER_OF_QUESTION);
                                startTimer(TIME_FOR_LEVEL);
                                getQuestion();
                                dialog.cancel();
                            }
                        })
                        .setNegativeButton("В меню", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                                onBackPressed();
                            }
                        });
                AlertDialog alert = dialogBuilder.create();
                alert.show();
            }
        }.start();
    }

    public static void saveArray(){
        editor=mSaves.edit();
        for (int i = 0; i < MainActivity.SIZE+1; i++) {
            editor.putInt(String.valueOf(i),COMPLETED_LEVELS[i]);
            Log.e("TAG",String.valueOf(COMPLETED_LEVELS[i]));
        }
        editor.apply();
    }

    public static void loadArray(){
        COMPLETED_LEVELS = new int[MainActivity.SIZE + 1];
        for (int i = 0; i < MainActivity.SIZE + 1; i++) {
            if (mSaves.contains(String.valueOf(i))){
                COMPLETED_LEVELS[i] = mSaves.getInt(String.valueOf(i),0);
            }
            else COMPLETED_LEVELS[i] = 0;
        }
    }

    private void printOnScreen(String s){
        Toast.makeText(this, s,
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        timer.cancel();
        COUNTER = 0;
        LevelsActivity.refreshAdapter();
    }

    public void saveScore(int score){
        editor=mSaves.edit();
        if (mSaves.contains("SCORE")){
            score += mSaves.getInt("SCORE",0);
        }
        editor.putInt("SCORE",score);
        editor.apply();
    }
}


