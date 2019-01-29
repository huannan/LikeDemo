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

import java.util.Random;
import java.util.Stack;

public class PraiseFrameLayout extends FrameLayout {

    public static final String TAG = PraiseFrameLayout.class.getSimpleName();
    private static final int PRAISE_IMAGE_MAX_NUM = 5;
    // 随机的旋转角度数组
    private static final float[] PRAISE_IMAGE_ROTATION = {-30, -20, 0, 20, 30};
    // 是否需要旋转角度
    private static boolean PRAISE_IMAGE_IS_NEED_ROTATION = false;
    // 触摸点相对于图片宽高的百分比
    private static final float PRAISE_IMAGE_PERCENT_X = 0.5F;
    private static final float PRAISE_IMAGE_PERCENT_Y = 0.8F;
    private GestureDetector mGestureDetector;
    private Stack<ImageView> mPraiseImageViewStack;
    private Random mPraiseRandom;

    public PraiseFrameLayout(@NonNull Context context) {
        this(context, null);
    }

    public PraiseFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PraiseFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);

    }

    private void init(Context context) {
        mGestureDetector = new GestureDetector(context, new OnDoubleClickGestureListener());
        mPraiseRandom = new Random();
    }

    private void initPraiseImageViewStack() {
        if (mPraiseImageViewStack == null) {
            mPraiseImageViewStack = new Stack<>();
            for (int i = 0; i < PRAISE_IMAGE_MAX_NUM; i++) {
                ImageView view = new ImageView(getContext());
                view.setImageResource(R.drawable.mz_praise_red);
                mPraiseImageViewStack.push(view);
            }
        }
    }

    private void startLikeAnimation(int x, int y) {

//        initPraiseImageViewStack();

        //取出栈的元素
//        if (mPraiseImageViewStack.empty()) {
//            Log.e(TAG, "栈已经为空了，MAX_LIKE_IMAGE_VIEW_NUM需要增加");
//            return;
//        }
//        final ImageView praiseImageView = mPraiseImageViewStack.pop();
        final ImageView praiseImageView = new ImageView(getContext());

        // 添加到布局当中
        addPraiseImageView(x, y, praiseImageView);

        // 创建动画
        AnimatorSet set = createPraiseAnimatorSet(praiseImageView);

        // 开始动画
        set.start();

        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                // 动画结束进行移除操作
                removeView(praiseImageView);
                // 重新添加到栈当中
//                praiseImageView.setScaleX(1.0F);
//                praiseImageView.setScaleY(1.0F);
//                praiseImageView.setAlpha(1.0F);
//                mPraiseImageViewStack.push(praiseImageView);
            }
        });
    }

    private void addPraiseImageView(int x, int y, ImageView view) {
        view.setImageResource(R.drawable.mz_praise_red);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        );

        // 获取图片宽高
        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        int width = view.getMeasuredWidth();
        int height = view.getMeasuredHeight();

        //位置需要根据图片宽高微调/边框微调
        params.leftMargin = (int) (x - width * PRAISE_IMAGE_PERCENT_X);
        params.topMargin = (int) (y - height * PRAISE_IMAGE_PERCENT_Y);
        if (params.leftMargin < 0) {
            params.leftMargin = 0;
        }
        if (params.leftMargin > Utils.getDisplayWidth() - width) {
            params.leftMargin = Utils.getDisplayWidth() - width;
        }
        if (params.topMargin < 0) {
            params.topMargin = 0;
        }
        if (params.topMargin > Utils.getDisplayHeight() - height) {
            params.topMargin = Utils.getDisplayHeight() - height;
        }

        addView(view, params);
    }

    @SuppressLint("NewApi")
    private AnimatorSet createPraiseAnimatorSet(final ImageView view) {
        AnimatorSet set = new AnimatorSet();

        ObjectAnimator rotation = ObjectAnimator.ofFloat(view, "rotation", PRAISE_IMAGE_ROTATION[mPraiseRandom.nextInt(4)]);
        rotation.setDuration(0);

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
        alpha.setInterpolator(new PathInterpolator(0.33F, 0.0F, 0.67F, 1.0F));

        if (PRAISE_IMAGE_IS_NEED_ROTATION) {
            set.playSequentially(rotation, scare1, scare2, scare3, alpha);
        } else {
            set.playSequentially(scare1, scare2, scare3, alpha);
        }

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
//        if (mPraiseImageViewStack != null) {
//            mPraiseImageViewStack.clear();
//            mPraiseImageViewStack = null;
//        }
    }
}
