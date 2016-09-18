package adb.wifi.woaiwhz.wifiadbandroid.base;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.util.SimpleArrayMap;

/**
 * Created by huazhou.whz on 2016/9/17.
 */
public class CircularRevealDrawable extends Drawable implements ValueAnimator.AnimatorUpdateListener,ValueAnimator.AnimatorListener{
    private static final int EOF_COLOR = Integer.MIN_VALUE;

    private Paint mPaint;
    private RectF mReact;
    private PointF mCenterPoint;
    private ValueAnimator mAnimator;
    private float mCurrentRadius;
    private float mMaxRadius;

    private int mInitColor;
    private int mColorWifiUnReady;
    private int mColorPortUnReady;
    private int mColorPortReady;

    private int mCurrentColor;
    private int mNextColor;

    public CircularRevealDrawable(@NonNull SimpleArrayMap<Integer,Integer> stateColorMap) {
        super();
        mPaint = new Paint();

        mInitColor = stateColorMap.get(State.INIT);
        mColorWifiUnReady = stateColorMap.get(State.WIFI_UNREADY);
        mColorPortReady = stateColorMap.get(State.PORT_READY);
        mColorPortUnReady = stateColorMap.get(State.PORT_UNREADY);

        mCurrentColor = mInitColor;
        mNextColor = EOF_COLOR;
    }

    @Override
    public void setBounds(int left, int top, int right, int bottom) {
        super.setBounds(left, top, right, bottom);

        mReact = new RectF(left,top,right,bottom);
        float centerX = (left + right) >> 1;
        float centerY = (top + bottom) >> 1;

        mCenterPoint = new PointF(centerX,centerY);
        mCurrentRadius = mMaxRadius = (float) Math.sqrt(Math.pow(centerX,2) + Math.pow(centerY,2));

        if(mAnimator != null && mAnimator.isRunning()){
            mAnimator.cancel();
        }

        mAnimator = ValueAnimator.ofFloat(0, mMaxRadius);
        mAnimator.setDuration(500);
        mAnimator.addUpdateListener(this);
        mAnimator.addListener(this);
        if(mNextColor != EOF_COLOR){
            mAnimator.start();
        }
    }

    public void changeState(@State.STATE int state){
        switch (state){
            case State.WIFI_UNREADY:
                mNextColor = mColorWifiUnReady;
                break;

            case State.PORT_UNREADY:
                mNextColor = mColorPortUnReady;
                break;

            case State.PORT_READY:
                mNextColor = mColorPortReady;
                break;

            default:
                break;
        }

        if(mAnimator != null) {
            if(mAnimator.isRunning()){
                mAnimator.cancel();
            }

            mAnimator.start();
        }
    }

    @Override
    public void draw(Canvas canvas) {
        mPaint.setColor(mCurrentColor);
        canvas.drawRect(mReact,mPaint);

        if(mNextColor != EOF_COLOR){
            mPaint.setColor(mNextColor);
            canvas.drawCircle(mCenterPoint.x, mCenterPoint.y, mCurrentRadius, mPaint);
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
        return 0;
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        mCurrentRadius = (Float) animation.getAnimatedValue();
//        Log.i("test","radius:" + mCurrentRadius);
        invalidateSelf();
    }

    @Override
    public void onAnimationStart(Animator animation) {}

    @Override
    public void onAnimationEnd(Animator animation) {
        mCurrentColor = mNextColor;
        mNextColor = EOF_COLOR;
    }

    @Override
    public void onAnimationCancel(Animator animation) {}

    @Override
    public void onAnimationRepeat(Animator animation) {}
}
