package com.example.android.wiseking;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.FrameLayout;

import androidx.annotation.AttrRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StyleRes;


public class StartButton extends FrameLayout {
    private View waveView;
    private Button startButton;

    public StartButton(@NonNull Context context) {
        super(context);
        setupViews();
    }

    public StartButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setupViews();
    }

    public StartButton(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setupViews();
    }

    public StartButton(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setupViews();
    }

    public void setupViews() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_start_button, this, true);
        waveView = findViewById(R.id.view_startButton);
        startButton = (Button) findViewById(R.id.button_startButton);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (!isInEditMode()) {
            ScaleAnimation scaleAnimation = new ScaleAnimation(1, 1.1f, 1, 1.1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            scaleAnimation.setDuration(400);
            scaleAnimation.setRepeatCount(Animation.INFINITE);
            scaleAnimation.setRepeatMode(Animation.REVERSE);
            waveView.startAnimation(scaleAnimation);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int size = getContext().getResources().getDimensionPixelSize(R.dimen.size_startButton);
        super.onMeasure(MeasureSpec.makeMeasureSpec(size, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(size, MeasureSpec.EXACTLY));
    }

    public void setOnStartButtonClickListener(OnClickListener onClickListener) {
        startButton.setOnClickListener(onClickListener);
    }

}
