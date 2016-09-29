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
import android.widget.Toast;

import adb.wifi.woaiwhz.wifiadbandroid.MyApp;
import adb.wifi.woaiwhz.wifiadbandroid.R;
import adb.wifi.woaiwhz.wifiadbandroid.base.CircularRevealDrawable;
import adb.wifi.woaiwhz.wifiadbandroid.base.OnTouchInterceptor;
import adb.wifi.woaiwhz.wifiadbandroid.bean.State;
import adb.wifi.woaiwhz.wifiadbandroid.base.WiFiModule;
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
    private SwitchCompat mSwitch;
    private MainPresenter mPresenter;
    private CircularRevealDrawable mRevelDrawable;
    private Toolbar mToolbar;

    private AnimatorSet mAnimate2WifiReady;
    private AnimatorSet mAnimate2WifiUnReady;

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
        mSwitch = $(R.id.switch_real);

        mMaskView.setOnTouchListener(new OnTouchInterceptor());
        mCenterButton.setOnClickListener(this);
        mSplashSwitch.setOnCheckedChangeListener(this);

        mToolbar = $(R.id.toolbar);
        setSupportActionBar(mToolbar);

        mRevelDrawable = new CircularRevealDrawable();
        mRevelDrawable.putColor(State.PORT_READY,R.color.port_ready_primary);
        mRevelDrawable.putColor(State.PORT_UNREADY,R.color.port_unready_primary);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mRevealHolderView.setBackground(mRevelDrawable);
        }else {
            mRevealHolderView.setBackgroundDrawable(mRevelDrawable);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if (!isAnimateReady() && hasFocus) {
            initAnimate();
            mPresenter.onStart();
        }
    }

    private void initAnimate(){
        final int toolbarBottom = mToolbar.getBottom();
        final int splashBottom = mSplashContainer.getBottom();
        final float noScale = getResources().getInteger(R.integer.switch_no_scale);
        final float maxScale = getResources().getInteger(R.integer.switch_max_scale);
        final float bigSwitchX = mSplashSwitch.getX();
        final float bigSwitchY = mSplashSwitch.getY();
        final float smallSwitchX = mSwitch.getX();
        final float smallSwitchY = mSwitch.getY();
        final long duration = 600L;
        final TimeInterpolator interpolator = new AccelerateDecelerateInterpolator();

        mAnimate2WifiReady = new AnimatorSet();
        mAnimate2WifiReady.play(ObjectAnimator.ofFloat(mSplashSwitch,scaleProperty,maxScale,noScale))
                .with(ObjectAnimator.ofFloat(mSplashSwitch,xProperty,bigSwitchX,smallSwitchX))
                .with(ObjectAnimator.ofFloat(mSplashSwitch,yProperty,bigSwitchY,smallSwitchY))
                .with(ObjectAnimator.ofInt(mSplashContainer,bottomProperty,splashBottom,toolbarBottom));

        mAnimate2WifiReady.setDuration(duration)
                .setInterpolator(interpolator);

        mAnimate2WifiReady.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                mRevealHolderView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mToolbar.setAlpha(1f);
                mSplashContainer.setAlpha(0f);
            }
        });

        mAnimate2WifiUnReady = new AnimatorSet();
        mAnimate2WifiUnReady
                .play(ObjectAnimator.ofFloat(mSplashSwitch,scaleProperty,noScale,maxScale))
                .with(ObjectAnimator.ofFloat(mSplashSwitch,xProperty,smallSwitchX,bigSwitchX))
                .with(ObjectAnimator.ofFloat(mSplashSwitch,yProperty,smallSwitchY,bigSwitchY))
                .with(ObjectAnimator.ofInt(mSplashContainer,bottomProperty,toolbarBottom,splashBottom));

        mAnimate2WifiUnReady.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationStart(Animator animation) {
                mSplashContainer.setAlpha(1f);
                mToolbar.setAlpha(0f);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mRevealHolderView.setVisibility(View.GONE);
                Toast.makeText(MyApp.getContext(), R.string.wifi_no_ready,Toast.LENGTH_SHORT).show();
            }
        });

        mAnimate2WifiUnReady.setDuration(duration)
                .setInterpolator(interpolator);

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
        mSplashSwitch.setChecked(false);
        mIpContainer.setVisibility(View.GONE);
        mIpValue.setText(null);
        startSplashAnimate(false);
    }


    private void onWifiReady() {
        mSplashSwitch.setChecked(true);
        startSplashAnimate(true);
    }

    @Override
    public void onPortReady(String ip) {
        onWifiReady();
        mIpValue.setText(ip);

        mIpContainer.setAlpha(0f);
        mIpContainer.setVisibility(View.VISIBLE);
        ViewCompat.animate(mIpContainer)
                .alpha(1f)
                .setDuration(300)
                .start();

        mRevelDrawable.changeState(State.PORT_READY);
    }

    @Override
    public void onPortUnready() {
        onWifiReady();

        if(mIpContainer.getVisibility() == View.VISIBLE){
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

        mRevelDrawable.changeState(State.PORT_UNREADY);
    }

    @Override
    public void onActionFail(@NonNull String message) {
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
        // TODO: 2016/9/28 dismiss loading
    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();

        switch (id){
            case R.id.center_button:
                mPresenter.toggle();
                break;

            case R.id.splash_switch_wifi:
                final boolean isChecked = mSplashSwitch.isChecked();
                WiFiModule.getInstance().enable(isChecked);
                mSwitch.setChecked(isChecked);
                break;

            default:
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {
        final int id = buttonView.getId();

        switch (id){
            case R.id.splash_switch_wifi:
                mSwitch.setChecked(isChecked);
                break;

            default:
                break;
        }
    }



    private boolean isAnimateReady(){
        return mAnimate2WifiUnReady != null && mAnimate2WifiReady != null;
    }

    private void startSplashAnimate(boolean ready){
        if(!isAnimateReady()){
            return;
        }

        if(mAnimate2WifiReady.isRunning()){
            mAnimate2WifiReady.cancel();
        }else if(mAnimate2WifiUnReady.isRunning()){
            mAnimate2WifiUnReady.cancel();
        }

        if(ready){
            mAnimate2WifiReady.start();
        }else {
            mAnimate2WifiUnReady.start();
        }
    }
}
