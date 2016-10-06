package adb.wifi.woaiwhz.wifiadbandroid.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Property;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;

import adb.wifi.woaiwhz.wifiadbandroid.R;
import adb.wifi.woaiwhz.wifiadbandroid.base.CircularRevealDrawable;
import adb.wifi.woaiwhz.wifiadbandroid.base.OnTouchInterceptor;
import adb.wifi.woaiwhz.wifiadbandroid.base.WiFiModule;
import adb.wifi.woaiwhz.wifiadbandroid.bean.State;
import adb.wifi.woaiwhz.wifiadbandroid.presenter.MainPresenter;

public class MainActivity extends AppCompatActivity
        implements MainPresenter.MainView,View.OnClickListener,CompoundButton.OnCheckedChangeListener{
    private View mSplashContainer;
    private SwitchCompat mSplashSwitch;
    private View mRevealHolderView;
    private View mMaskView;
    private View mLoading;
    private TextView mIpValue;
    private ImageButton mCenterButton;
    private View mIpContainer;
    private SwitchCompat mToolbarSwitch;
    private MainPresenter mPresenter;
    private CircularRevealDrawable mRevelDrawable;
    private Toolbar mToolbar;


    private SwitchCompat mCurrentSwitch;
    private SwitchCompat mHidingSwitch;
    private AnimatorSet mWifiReadyAnimate;
    private AnimatorSet mWifiUnreadyAnimate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSplashContainer = $(R.id.splash_container);
        mSplashSwitch = $(R.id.splash_switch_wifi);
        mRevealHolderView = $(R.id.reveal_layout);
        mMaskView = $(R.id.mask);
        mCenterButton = $(R.id.center_button);
        mLoading = $(R.id.loading);
        mIpValue = $(R.id.ip_value);
        mIpContainer = $(R.id.ip_layout);
        mToolbarSwitch = $(R.id.switch_real);
        mToolbar = $(R.id.toolbar);

        init();
    }

    private void init(){
        setSupportActionBar(mToolbar);

        mRevelDrawable = new CircularRevealDrawable();
        mRevelDrawable.putColor(State.PORT_READY,R.color.port_ready_primary);
        mRevelDrawable.putColor(State.PORT_UNREADY,R.color.port_unready_primary);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mRevealHolderView.setBackground(mRevelDrawable);
        }else {
            mRevealHolderView.setBackgroundDrawable(mRevelDrawable);
        }

        mMaskView.setOnTouchListener(new OnTouchInterceptor());
        mCenterButton.setOnClickListener(this);

        mSplashSwitch.setOnCheckedChangeListener(this);
        mToolbarSwitch.setOnCheckedChangeListener(this);
        mSplashSwitch.setOnClickListener(this);
        mToolbarSwitch.setOnClickListener(this);
        mCurrentSwitch = mSplashSwitch;
        mHidingSwitch = mToolbarSwitch;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if (!isAnimateReady() && hasFocus) {
            initAnimate();
            mPresenter.onStart();
        }
    }

    // TODO: 2016/10/6 尝试去掉 splashSwitch 和 toolbarSwitch 的区分，只有 current 和 hiding
    private void initAnimate(){
        final int toolbarBottom = mToolbar.getBottom();
        final int splashBottom = mSplashContainer.getBottom();
        final float noScale = getResources().getInteger(R.integer.switch_no_scale);
        final float maxScale = getResources().getInteger(R.integer.switch_max_scale);
        final float bigSwitchX = mSplashSwitch.getX();
        final float bigSwitchY = mSplashSwitch.getY();
        final float smallSwitchX = mToolbarSwitch.getX();
        final float smallSwitchY = mToolbarSwitch.getY();
        final long duration = 600L;
        final TimeInterpolator interpolator = new AccelerateDecelerateInterpolator();

        mWifiReadyAnimate = new AnimatorSet();
        mWifiReadyAnimate.play(ObjectAnimator.ofFloat(mSplashSwitch,scaleProperty,maxScale,noScale))
                .with(ObjectAnimator.ofFloat(mSplashSwitch,xProperty,bigSwitchX,smallSwitchX))
                .with(ObjectAnimator.ofFloat(mSplashSwitch,yProperty,bigSwitchY,smallSwitchY))
                .with(ObjectAnimator.ofInt(mSplashContainer,bottomProperty,splashBottom,toolbarBottom));

        mWifiReadyAnimate.setDuration(duration)
                .setInterpolator(interpolator);

        mWifiReadyAnimate.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                mRevealHolderView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mToolbar.setVisibility(View.VISIBLE);
                mSplashContainer.setVisibility(View.GONE);
                mSplashContainer.setBottom(splashBottom);
                mSplashSwitch.setScaleX(maxScale);
                mSplashSwitch.setScaleY(maxScale);
                mSplashSwitch.setX(bigSwitchX);
                mSplashSwitch.setY(bigSwitchY);

                mCurrentSwitch = mToolbarSwitch;
                mHidingSwitch = mSplashSwitch;
            }
        });

        mWifiUnreadyAnimate = new AnimatorSet();
        mWifiUnreadyAnimate
                .play(ObjectAnimator.ofFloat(mToolbarSwitch,scaleProperty,noScale,maxScale))
                .with(ObjectAnimator.ofFloat(mToolbarSwitch,xProperty,smallSwitchX,bigSwitchX))
                .with(ObjectAnimator.ofFloat(mToolbarSwitch,yProperty,smallSwitchY,bigSwitchY))
                .with(ObjectAnimator.ofInt(mToolbar,bottomProperty,toolbarBottom,splashBottom));

        mWifiUnreadyAnimate.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mSplashContainer.setVisibility(View.VISIBLE);
                mRevealHolderView.setVisibility(View.GONE);
                mToolbar.setVisibility(View.GONE);
                mToolbar.setBottom(toolbarBottom);
                mToolbarSwitch.setScaleX(noScale);
                mToolbarSwitch.setScaleY(noScale);
                mToolbarSwitch.setX(smallSwitchX);
                mToolbarSwitch.setY(smallSwitchY);

                mCurrentSwitch = mSplashSwitch;
                mHidingSwitch = mToolbarSwitch;

                Snackbar.make(mRevealHolderView,R.string.wifi_no_ready,Snackbar.LENGTH_SHORT).show();
            }
        });

        mWifiUnreadyAnimate.setDuration(duration)
                .setInterpolator(interpolator);

        mToolbar.setVisibility(View.GONE);
    }

    // TODO: 2016/9/25 改成静态类
    private static Property<View,Float> xProperty = new Property<View, Float>(Float.class,"xProperty") {
        @Override
        public Float get(View object) {
            return object.getX();
        }

        @Override
        public void set(View object, Float value) {
            object.setX(value);
        }
    };

    private static Property<View,Float> yProperty = new Property<View, Float>(Float.class,"yProperty") {
        @Override
        public Float get(View object) {
            return object.getY();
        }

        @Override
        public void set(View object, Float value) {
            object.setY(value);
        }
    };

    private static Property<View,Integer> bottomProperty = new Property<View, Integer>(Integer.class,"bottomProperty") {
        @Override
        public Integer get(View object) {
            return object.getBottom();
        }

        @Override
        public void set(View object, Integer value) {
            object.setBottom(value);
        }
    };


    private static Property<View,Float> scaleProperty = new Property<View, Float>(Float.class,"scaleProperty") {
        @Override
        public Float get(View object) {
            return object.getScaleX();
        }

        @Override
        public void set(View object, Float value) {
            object.setScaleX(value);
            object.setScaleY(value);
        }
    };


    @Override
    protected void onStart() {
        super.onStart();

        if(mPresenter == null){
            mPresenter = new MainPresenter(this);
        }

        if(isAnimateReady()) {
            mPresenter.onStart();
        }
    }

    @SuppressWarnings("unchecked")
    private <T extends View> T $(@IdRes int id){
        return (T) findViewById(id);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mPresenter.onStop();
    }

    @Override
    public void pageLoading(boolean show) {
        if(show){
            showMask(true);
        }else {
            hideMask();
        }
    }

    private void hideMask(){
        mMaskView.setVisibility(View.GONE);
    }

    private void showMask(boolean withLoading){
        mMaskView.setVisibility(View.VISIBLE);

        if(withLoading){
            mLoading.setVisibility(View.VISIBLE);
        }else{
            mLoading.setVisibility(View.GONE);
        }
    }

    @Override
    public void onWifiUnready() {
        mCurrentSwitch.setChecked(false);
        mIpContainer.setVisibility(View.GONE);
        mIpValue.setText(null);
        startSplashAnimate(false);
        hideMask();
    }


    private void onWifiReady() {
        mCurrentSwitch.setChecked(true);
        startSplashAnimate(true);
    }

    @Override
    public void onPortReady(String ip) {
        onWifiReady();
        mIpValue.setText(ip);
        showIpContainer();

        mRevelDrawable.changeState(State.PORT_READY);
    }

    private void showIpContainer(){
        mIpContainer.setAlpha(0f);
        mIpContainer.setVisibility(View.VISIBLE);

        ViewCompat.animate(mIpContainer)
                .alpha(1f)
                .setDuration(300)
                .start();
    }

    @Override
    public void onPortUnready() {
        onWifiReady();

        hideIpContainer();
        mRevelDrawable.changeState(State.PORT_UNREADY);
    }

    private void hideIpContainer(){
        ViewCompat.animate(mIpContainer)
                .alpha(0f)
                .setDuration(300)
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        mIpContainer.setVisibility(View.GONE);
                    }
                }).start();
    }

    @Override
    public void onActionFail(@NonNull String message) {
        Snackbar.make(mRevealHolderView,message,Snackbar.LENGTH_SHORT).show();
        // TODO: 2016/9/28 dismiss loading
    }

    @Override
    public void onClick(View v) {
        if(v == mCenterButton){
            mPresenter.toggle();
        }else if(v == mCurrentSwitch){
            final boolean isChecked = mCurrentSwitch.isChecked();
            WiFiModule.getInstance().enable(isChecked);
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton v, final boolean isChecked) {
        if(v != mCurrentSwitch){
            return;
        }

        mHidingSwitch.setChecked(isChecked);
    }



    private boolean isAnimateReady(){
        return mWifiUnreadyAnimate != null && mWifiReadyAnimate != null;
    }

    private void startSplashAnimate(boolean ready){
        if(!isAnimateReady()){
            return;
        }

        if(mWifiReadyAnimate.isRunning()){
            mWifiReadyAnimate.cancel();
        }else if(mWifiUnreadyAnimate.isRunning()){
            mWifiUnreadyAnimate.cancel();
        }

        if(ready){
            mWifiReadyAnimate.start();
        }else {
            mWifiUnreadyAnimate.start();
        }
    }
}
