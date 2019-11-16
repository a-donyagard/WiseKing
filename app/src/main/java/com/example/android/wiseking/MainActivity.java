package com.example.android.wiseking;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
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
import android.widget.Toast;

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
    private SharedPreferences coinsSharedPref;

    private IabHelper iabHelper;
    private static final String RSA_KEY = "MIHNMA0GCSqGSIb3DQEBAQUAA4G7ADCBtwKBrwC9gD8M7qUTrwRc1YrjFtNz8gcjxFJvJ6QH+oji3/rr1sfy+k0gYtXzmyQN9YEGmO8mA2xXwowPOt8lGT24D/e1Km2GaJvDFQIo/yInnHyQuihhmXU8twRmVkUPktlj0u7gnN0l+c6wH6sxOGQ+r6VL2vvtg0QrQkzYY5Mtt6Auoopfsaz7i41Ow9A5poLnUOU2LkejkfMrlygFeIO2XDGFVJAsU23bu4ZuWjmaoFkCAwEAAQ==\n";
    private static final int PURCHASE_REQUEST_CODE = 1001;

    private TextView coinsCountGetStartedTextView;
    private TextView coinsCountInGameTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        iabHelper = new IabHelper(this, RSA_KEY);
        iabHelper.startSetup(this);
        coinsSharedPref = getSharedPreferences("coins_shp", MODE_PRIVATE);

        mediaPlayer = MediaPlayer.create(this, R.raw.background_music);
        questionManager = new QuestionManager(this);
        pointsManager = new PointsManager(this);
        setupViews();
    }

    private void setupViews() {
        Button buttonPurchaseCoins = (Button) findViewById(R.id.button_main_purchaseCoins);
        buttonPurchaseCoins.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PurchaseCoinDialog dialog = new PurchaseCoinDialog();
                dialog.setOnProductSelected(new PurchaseCoinDialog.OnProductSelected() {
                    @Override
                    public void onProductSelected(int productId) {
                        switch (productId) {
                            case PurchaseCoinDialog.TEN_COINS:
                                iabHelper.launchPurchaseFlow(MainActivity.this, "ten_coin", PURCHASE_REQUEST_CODE,
                                        MainActivity.this);
                                break;
                            case PurchaseCoinDialog.FIFTY_COINS:
                                iabHelper.launchPurchaseFlow(MainActivity.this, "fifty_coin", PURCHASE_REQUEST_CODE,
                                        MainActivity.this);
                                break;
                            case PurchaseCoinDialog.HUNDRED_COINS:
                                iabHelper.launchPurchaseFlow(MainActivity.this, "hundred_coin", PURCHASE_REQUEST_CODE,
                                        MainActivity.this);
                                break;
                        }
                    }
                });
                dialog.show(getSupportFragmentManager(), null);
            }
        });
        Button useCoinsButton = (Button) findViewById(R.id.button_main_useCoin);
        useCoinsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getCoinsCount() >= 10) {
                    saveCoins(getCoinsCount() - 10);
                    switch (currentQuestion.getAnswer()) {
                        case 0:
                            option0Button.performClick();
                            break;
                        case 1:
                            option1Button.performClick();
                            break;
                        case 2:
                            option3Button.performClick();
                            break;
                        case 3:
                            option3Button.performClick();
                            break;
                    }
                } else {
                    Toast.makeText(MainActivity.this, "سکه های شما کافی نیست", Toast.LENGTH_SHORT).show();
                }
            }
        });

        coinsCountGetStartedTextView = (TextView) findViewById(R.id.tv_main_getStartCoinsCount);
        coinsCountInGameTextView = (TextView) findViewById(R.id.tv_main_inGameCoinsCount);
        updateCoinsUi();

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
        iabHelper.queryInventoryAsync(this);
    }

    @Override
    public void onIabPurchaseFinished(IabResult result, Purchase info) {
        if (result.isSuccess() && info != null) {
            iabHelper.consumeAsync(info, this);
        }
    }

    @Override
    public void onQueryInventoryFinished(IabResult result, Inventory inv) {

    }

    @Override
    public void onConsumeFinished(Purchase purchase, IabResult result) {
        if (result.isSuccess() && purchase != null) {
            if (purchase.getSku().equalsIgnoreCase("ten_coin")) {
                saveCoins(getCoinsCount() + 10);
            } else if (purchase.getSku().equalsIgnoreCase("fifty_coin")) {
                saveCoins(getCoinsCount() + 50);
            } else if (purchase.getSku().equalsIgnoreCase("hundred_coin")) {
                saveCoins(getCoinsCount() + 100);
            }
            updateCoinsUi();
        }
    }

    public void updateCoinsUi() {
        String coinsCount = String.valueOf(getCoinsCount());
        coinsCountGetStartedTextView.setText("تعداد سکه ها: " + coinsCount);
        coinsCountInGameTextView.setText(coinsCount);
    }

    public void saveCoins(int coins) {
        SharedPreferences.Editor editor = coinsSharedPref.edit();
        editor.putInt("coins", coins);
        editor.apply();
    }

    public int getCoinsCount() {
        return coinsSharedPref.getInt("coins", 50);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PURCHASE_REQUEST_CODE) {
            iabHelper.handleActivityResult(requestCode, resultCode, data);
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
