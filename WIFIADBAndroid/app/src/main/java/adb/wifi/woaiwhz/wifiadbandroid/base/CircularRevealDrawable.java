package adb.wifi.woaiwhz.wifiadbandroid.base;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.res.ResourcesCompat;

import adb.wifi.woaiwhz.wifiadbandroid.MyApp;
import adb.wifi.woaiwhz.wifiadbandroid.R;

/**
 * Created by huazhou.whz on 2016/9/17.
 */
public class CircularRevealDrawable extends Drawable implements ValueAnimator.AnimatorUpdateListener,ValueAnimator.AnimatorListener{
    private static final int EOF_COLOR = Integer.MIN_VALUE;
    private static final long DURATION = 600L;

    private final Paint mPaint;
    private RectF mReact;
    private PointF mCenterPoint;
    private ValueAnimator mAnimator;
    private float mCurrentRadius;

    private final int mWhiteColor;
    private final int mColorPortUnReady;
    private final int mColorPortReady;

    private int mCurrentColor;
    private int mNextColor;

    public CircularRevealDrawable() {
        super();
        mPaint = new Paint();

        final Resources resources = MyApp.getContext().getResources();
        mWhiteColor = ResourcesCompat.getColor(resources,android.R.color.white,null);
        mColorPortReady = ResourcesCompat.getColor(resources, R.color.port_ready_primary,null);
        mColorPortUnReady = ResourcesCompat.getColor(resources,R.color.port_unready_primary,null);

        mCurrentColor = EOF_COLOR;
        mNextColor = EOF_COLOR;
    }

    @Override
    public void setBounds(int left, int top, int right, int bottom) {
        super.setBounds(left, top, right, bottom);

        mReact = new RectF(left,top,right,bottom);
        float centerX = (left + right) >> 1;
        float centerY = (top + bottom) >> 1;

        mCenterPoint = new PointF(centerX,centerY);
        final float maxRadius = (float) Math.hypot(centerX,centerY);

        if(mAnimator != null && mAnimator.isRunning()){
            mAnimator.cancel();
        }

        mAnimator = ValueAnimator.ofFloat(0, maxRadius);
        mAnimator.setDuration(DURATION);
        mAnimator.addUpdateListener(this);
        mAnimator.addListener(this);

        if(canReveal()){
            mAnimator.start();
        }
    }

    private boolean canReveal(){
        return mNextColor != EOF_COLOR && mNextColor != mCurrentColor;
    }

    private boolean canDraw(){
        return mCurrentColor != EOF_COLOR;
    }

    public void changeState(@State.STATE int state){
        if(mCurrentColor == EOF_COLOR){
            mCurrentColor = getColor(state);
            invalidateSelf();
        }else {
            mNextColor = getColor(state);

            if(mAnimator != null && canReveal()) {
                if(mAnimator.isRunning()){
                    mAnimator.cancel();
                }
                mAnimator.start();
            }
        }
    }

    private int getColor(@State.STATE int state){
        switch (state){
            case State.PORT_UNREADY:
                return mColorPortUnReady;

            case State.PORT_READY:
                return mColorPortReady;

            default:
                return EOF_COLOR;
        }
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        if(canDraw()) {
            mPaint.setColor(mCurrentColor);
            canvas.drawRect(mReact, mPaint);

            if (canReveal()) {
                mPaint.setColor(mNextColor);
                canvas.drawCircle(mCenterPoint.x, mCenterPoint.y, mCurrentRadius, mPaint);
            }
        }else {
            mPaint.setColor(mWhiteColor);
            canvas.drawRect(mReact,mPaint);
        }
    }

    @Override
    public void setAlpha(int alpha) {
        mPaint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
        mPaint.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.UNKNOWN;
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        mCurrentRadius = (Float) animation.getAnimatedValue();
        invalidateSelf();
    }

    @Override
    public void onAnimationStart(Animator animation) {}

    @Override
    public void onAnimationEnd(Animator animation) {
        mCurrentColor = mNextColor;
    }

    @Override
    public void onAnimationCancel(Animator animation) {}

    @Override
    public void onAnimationRepeat(Animator animation) {}
}
