package com.nan.likedemo;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.PathInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.util.Stack;

public class LikeFrameLayout extends FrameLayout {

    public static final String TAG = LikeFrameLayout.class.getSimpleName();
    private static final int MAX_LIKE_IMAGE_VIEW_NUM = 5;
    private GestureDetector mGestureDetector;
    private Stack<ImageView> mLikeImageViewStack;

    public LikeFrameLayout(@NonNull Context context) {
        this(context, null);
    }

    public LikeFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LikeFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);

    }

    private void init(Context context) {
        mGestureDetector = new GestureDetector(context, new OnDoubleClickGestureListener());
    }

    private void initLikeImageViewStack() {
        if (mLikeImageViewStack == null) {
            mLikeImageViewStack = new Stack<>();
            for (int i = 0; i < MAX_LIKE_IMAGE_VIEW_NUM; i++) {
                ImageView likeImageView = new ImageView(getContext());
                likeImageView.setImageResource(R.drawable.mz_praise_red);
                mLikeImageViewStack.push(likeImageView);
            }
        }
    }

    private void startLikeAnimation(int x, int y) {

        initLikeImageViewStack();

        //取出栈的元素
        if (mLikeImageViewStack.empty()) {
            Log.e(TAG, "栈已经为空了，MAX_LIKE_IMAGE_VIEW_NUM需要增加");
            return;
        }
        final ImageView likeImageView = mLikeImageViewStack.pop();
//        final ImageView likeImageView = new ImageView(getContext());

        // 添加到布局当中
        addLikeImageView(x, y, likeImageView);

        // 创建动画
        AnimatorSet set = createLikeAnimatorSet(likeImageView);

        // 开始动画
        set.start();

        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                // 动画结束进行移除操作
                removeView(likeImageView);
                // 重新添加到栈当中
                likeImageView.setScaleX(1.0F);
                likeImageView.setScaleY(1.0F);
                likeImageView.setAlpha(1.0F);
                mLikeImageViewStack.push(likeImageView);
            }
        });
    }

    private void addLikeImageView(int x, int y, ImageView view) {
        view.setImageResource(R.drawable.mz_praise_red);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        );

        //位置需要根据图片宽高微调/边框微调
        params.leftMargin = x;
        params.topMargin = y;
        addView(view, params);
    }

    @SuppressLint("NewApi")
    private AnimatorSet createLikeAnimatorSet(final ImageView view) {
        AnimatorSet set = new AnimatorSet();

        PropertyValuesHolder holderX1 = PropertyValuesHolder.ofFloat("scaleX", 0.0F, 1.4F);
        PropertyValuesHolder holderY1 = PropertyValuesHolder.ofFloat("scaleY", 0.0F, 1.4F);
        ValueAnimator scare1 = ObjectAnimator.ofPropertyValuesHolder(view, holderX1, holderY1);
        scare1.setDuration(160L);
        scare1.setInterpolator(new PathInterpolator(0.16F, 0.0F, 0.2F, 1.0F));

        PropertyValuesHolder holderX2 = PropertyValuesHolder.ofFloat("scaleX", 1.4F, 0.9F);
        PropertyValuesHolder holderY2 = PropertyValuesHolder.ofFloat("scaleY", 1.4F, 0.9F);
        ValueAnimator scare2 = ObjectAnimator.ofPropertyValuesHolder(view, holderX2, holderY2);
        scare2.setDuration(160L);
        scare2.setInterpolator(new PathInterpolator(0.16F, 0.0F, 0.2F, 1.0F));

        PropertyValuesHolder holderX3 = PropertyValuesHolder.ofFloat("scaleX", 0.9F, 1.0F);
        PropertyValuesHolder holderY3 = PropertyValuesHolder.ofFloat("scaleY", 0.9F, 1.0F);
        ValueAnimator scare3 = ObjectAnimator.ofPropertyValuesHolder(view, holderX3, holderY3);
        scare3.setDuration(240L);
        scare3.setInterpolator(new PathInterpolator(0.16F, 0.0F, 0.2F, 1.0F));

        final ObjectAnimator alpha = ObjectAnimator.ofInt(view, "alpha", 255, 0);
        alpha.setDuration(480L);
        //alpha.setStartDelay(560L);
        alpha.setInterpolator(new PathInterpolator(0.33F, 0.0F, 0.67F, 1.0F));

        set.playSequentially(scare1, scare2, scare3, alpha);

        return set;
    }

    public class OnDoubleClickGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            // 双击
            startLikeAnimation((int) e.getX(), (int) e.getY());
            return super.onDoubleTap(e);
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            // 单击
            return super.onSingleTapConfirmed(e);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mGestureDetector.onTouchEvent(event);
        return true;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        // 清空，防止内存泄漏
        if (mLikeImageViewStack != null) {
            mLikeImageViewStack.clear();
            mLikeImageViewStack = null;
        }
    }
}
