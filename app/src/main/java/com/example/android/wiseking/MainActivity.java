package com.example.android.wiseking;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.android.wiseking.util.IabHelper;
import com.example.android.wiseking.util.IabResult;
import com.example.android.wiseking.util.Inventory;
import com.example.android.wiseking.util.Purchase;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,
        IabHelper.QueryInventoryFinishedListener,
        IabHelper.OnIabPurchaseFinishedListener,
        IabHelper.OnIabSetupFinishedListener,
        IabHelper.OnConsumeFinishedListener {

    private FrameLayout getStartDialog;
    private QuestionManager questionManager;
    private TextView questionTextView;
    private Button option0Button;
    private Button option1Button;
    private Button option2Button;
    private Button option3Button;
    private TextView pointTextView;
    private TextView remainingTimeTextView;
    private Question currentQuestion;
    private int point;
    private Timer timer;
    private int remainingTime = 34000;
    private MediaPlayer mediaPlayer;
    private PointsManager pointsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mediaPlayer = MediaPlayer.create(this, R.raw.background_music);
        questionManager = new QuestionManager(this);
        pointsManager = new PointsManager(this);
        setupViews();
    }

    private void setupViews() {
        getStartDialog = (FrameLayout) findViewById(R.id.frameLayout_main_getStartDialog);
        questionTextView = (TextView) findViewById(R.id.textView_main_question);
        pointTextView = (TextView) findViewById(R.id.textView_main_point);
        remainingTimeTextView = (TextView) findViewById(R.id.textView_main_remainingTime);
        Button exitButton = (Button) findViewById(R.id.button_main_exit);
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        option0Button = (Button) findViewById(R.id.button_main_answer_0);
        option0Button.setTag(0);
        option0Button.setOnClickListener(this);

        option1Button = (Button) findViewById(R.id.button_main_answer_1);
        option1Button.setTag(1);
        option1Button.setOnClickListener(this);

        option2Button = (Button) findViewById(R.id.button_main_answer_2);
        option2Button.setTag(2);
        option2Button.setOnClickListener(this);

        option3Button = (Button) findViewById(R.id.button_main_answer_3);
        option3Button.setTag(3);
        option3Button.setOnClickListener(this);

        final StartButton startButton = (StartButton) findViewById(R.id.startButton_main);
        startButton.setOnStartButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlphaAnimation alphaAnimation = new AlphaAnimation(1f, 0f);
                alphaAnimation.setDuration(300);
                alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        getStartDialog.setVisibility(View.GONE);
                        onGameStarted();
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });

                getStartDialog.startAnimation(alphaAnimation);
            }
        });
    }

    private void setPoint(int point) {
        this.point = point;
        pointTextView.setText(String.valueOf(point));
    }

    private void loadNewQuestion() {
        currentQuestion = questionManager.getQuestion();
        questionTextView.setText(currentQuestion.getQuestion());
        option0Button.setText(currentQuestion.getOptions().get(0));
        option1Button.setText(currentQuestion.getOptions().get(1));
        option2Button.setText(currentQuestion.getOptions().get(2));
        option3Button.setText(currentQuestion.getOptions().get(3));
    }

    private void onGameStarted() {
        setPoint(0);
        mediaPlayer.start();
        remainingTimeTextView.setText(formatTime(remainingTime));
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        remainingTime -= 1000;
                        if (remainingTime < 20000) {
                            if (remainingTime < 10000) {
                                if (remainingTime <= 0) {
                                    onGameFinished();
                                }
                                remainingTimeTextView.setTextColor(ContextCompat.getColor(MainActivity.this, android.R.color.holo_red_light));
                            } else {
                                remainingTimeTextView.setTextColor(ContextCompat.getColor(MainActivity.this, android.R.color.holo_orange_light));
                            }
                        } else {
                            remainingTimeTextView.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.green));
                        }
                        remainingTimeTextView.setText(formatTime(remainingTime));
                    }
                });
            }
        }, 0, 1000);
        loadNewQuestion();

    }

    private boolean isShowingSelectedOptionResult = false;

    @Override
    public void onClick(final View view) {
        if (!isShowingSelectedOptionResult) {
            int selectedOption = (int) view.getTag();
            if (selectedOption == currentQuestion.getAnswer()) {
                view.setBackgroundResource(R.drawable.shape_correct_option);
                setPoint(point + 10);
            } else {
                view.setBackgroundResource(R.drawable.shape_wrong_option);
                switch (currentQuestion.getAnswer()) {
                    case 0:
                        option0Button.setBackgroundResource(R.drawable.shape_correct_option);
                        break;
                    case 1:
                        option1Button.setBackgroundResource(R.drawable.shape_correct_option);
                        break;
                    case 2:
                        option2Button.setBackgroundResource(R.drawable.shape_correct_option);
                        break;
                    case 3:
                        option3Button.setBackgroundResource(R.drawable.shape_correct_option);
                        break;
                }
            }
            isShowingSelectedOptionResult = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    loadNewQuestion();
                    view.setBackgroundResource(R.drawable.background_option_button);
                    option0Button.setBackgroundResource(R.drawable.background_option_button);
                    option1Button.setBackgroundResource(R.drawable.background_option_button);
                    option2Button.setBackgroundResource(R.drawable.background_option_button);
                    option3Button.setBackgroundResource(R.drawable.background_option_button);
                    isShowingSelectedOptionResult = false;
                }
            }, 500);
        }

    }

    private String formatTime(long duration) {
        int seconds = (int) (duration / 1000);
        int minutes = seconds / 60;
        seconds %= 60;
        return String.format(Locale.ENGLISH, "%02d", minutes) + ":" + String.format(Locale.ENGLISH, "%02d", seconds);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timer.cancel();
        mediaPlayer.release();
    }

    private void onGameFinished() {
        pointsManager.savePoint(point);
        timer.cancel();
        mediaPlayer.seekTo(0);
        mediaPlayer.pause();
        showGameResult();
    }

    public void showGameResult() {
        final FrameLayout frameLayout = (FrameLayout) findViewById(R.id.frameLayout_gameResultDialog);
        frameLayout.setVisibility(View.VISIBLE);
        AlphaAnimation alphaAnimation = new AlphaAnimation(0f, 1f);
        alphaAnimation.setDuration(300);
        frameLayout.startAnimation(alphaAnimation);

        TextView resultMessageTextView = (TextView) findViewById(R.id.textView_gameResultDialog_message);
        TextView resultPointTextView = (TextView) findViewById(R.id.textView_gameResult_point);
        TextView bestRecordTextView = (TextView) findViewById(R.id.textView_gameResultDialog_bestRecord);

        Button exitButton = (Button) findViewById(R.id.button_gameResultDialog_exit);
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        StartButton startButton = (StartButton) findViewById(R.id.startButton_gameResultDialog);
        startButton.setOnStartButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                frameLayout.setVisibility(View.GONE);
                onGameStarted();
            }
        });

        if (point > 80) {
            resultMessageTextView.setText("فوق العاده بود! ایول :)");
        } else if (point > 50) {
            resultMessageTextView.setText("خوب بود! آفرین");
        } else if (point > 30) {
            resultMessageTextView.setText("بد نبود");
        } else if (point >= 0) {
            resultMessageTextView.setText("خوب نبود! تلاشتو بیشتر کن");
        }

        resultPointTextView.setText(String.valueOf(point));
        bestRecordTextView.setText("بهترین رکورد شما: " + pointsManager.getBestRecord() + " امتیاز");
    }

    @Override
    public void onIabSetupFinished(IabResult result) {

    }

    @Override
    public void onIabPurchaseFinished(IabResult result, Purchase info) {

    }

    @Override
    public void onQueryInventoryFinished(IabResult result, Inventory inv) {

    }

    @Override
    public void onConsumeFinished(Purchase purchase, IabResult result) {

    }
}
