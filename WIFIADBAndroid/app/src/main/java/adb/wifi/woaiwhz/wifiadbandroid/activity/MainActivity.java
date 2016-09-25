package adb.wifi.woaiwhz.wifiadbandroid.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v4.util.SimpleArrayMap;
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
import adb.wifi.woaiwhz.wifiadbandroid.base.State;
import adb.wifi.woaiwhz.wifiadbandroid.base.WiFiModule;
import adb.wifi.woaiwhz.wifiadbandroid.presenter.MainPresenter;

public class MainActivity extends AppCompatActivity
        implements MainPresenter.MainView,View.OnClickListener,CompoundButton.OnCheckedChangeListener{
    private View mSplashContainer;
    private SwitchCompat mSplashSwitch;

    private View mRevealHolderView;
//    private View mMaskView;
//    private View mLoading;
    private TextView mIpValue;
    private ImageButton mCenterButton;
    private View mIpContainer;
    private SwitchCompat mSwitch;
    private MainPresenter mPresenter;
    private CircularRevealDrawable mRevelDrawable;
    private Toolbar mToolbar;

    private boolean mInitAnimate = false;
    private AnimatorSet mAnimate2WifiReady;
    private AnimatorSet mAnimate2WifiUnReady;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSplashContainer = $(R.id.splash_container);
        mSplashSwitch = $(R.id.splash_switch_wifi);
        mRevealHolderView = $(R.id.reveal_holder);
//        mMaskView = $(R.id.mask);
        mCenterButton = $(R.id.monitor_button);
//        mLoading = $(R.id.loading);
        mIpValue = $(R.id.ip_value);
        mIpContainer = $(R.id.ip_layout);
        mSwitch = $(R.id.switch_real);

//        mMaskView.setOnTouchListener(new OnTouchInterceptor());
        mCenterButton.setOnClickListener(this);
        mSplashSwitch.setOnCheckedChangeListener(this);

        mToolbar = $(R.id.toolbar);
        setSupportActionBar(mToolbar);

        SimpleArrayMap<Integer,Integer> map = new SimpleArrayMap<>();
        map.put(State.INIT,getResources().getColor(android.R.color.black));
        map.put(State.WIFI_UNREADY,getResources().getColor(R.color.wifi_unready_primary));
        map.put(State.PORT_UNREADY,getResources().getColor(R.color.port_unready_primary));
        map.put(State.PORT_READY,getResources().getColor(R.color.port_ready_primary));
        mRevelDrawable = new CircularRevealDrawable(map);
        mRevealHolderView.setBackgroundDrawable(mRevelDrawable);
        // TODO: 2016/9/24
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if (!mInitAnimate && hasFocus) {
            init();
            mInitAnimate = true;
            mPresenter.check();
        }
    }

    private void init(){
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
    protected void onResume() {
        super.onResume();

        if(mPresenter == null){
            mPresenter = new MainPresenter(this);
        }

        if(mInitAnimate) {
            mPresenter.check();
        }
    }

    @SuppressWarnings("unchecked")
    private <T extends View> T $(@IdRes int id){
        return (T) findViewById(id);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.onDestroy();
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
//        mMaskView.setVisibility(View.GONE);
    }

    private void showMask(boolean withLoading){
//        mMaskView.setVisibility(View.VISIBLE);

//        if(withLoading){
//            mLoading.setVisibility(View.VISIBLE);
//        }else{
//            mLoading.setVisibility(View.GONE);
//        }
    }

    @Override
    public void onWifiUnready() {
        if(mSplashSwitch.isChecked()) {
            mSplashSwitch.setChecked(false);
        }
//        disableButton();

        mIpContainer.setVisibility(View.GONE);
        mIpValue.setText(null);

//        mRevelDrawable.changeState(State.WIFI_UNREADY);

        if(mInitAnimate)
        mAnimate2WifiUnReady.start();
    }

    private void disableButton(){
        mCenterButton.setEnabled(false);
        mCenterButton.setClickable(false);
        mCenterButton.setActivated(false);
    }


    private void onWifiReady() {
        if(!mSplashSwitch.isChecked()) {
            mSplashSwitch.setChecked(true);
        }
        mCenterButton.setClickable(true);
        mIpContainer.setVisibility(View.GONE);
        mIpValue.setText(null);
        if(mInitAnimate)
        mAnimate2WifiReady.start();
    }

    @Override
    public void onPortReady(String ip) {
        onWifiReady();
        mIpValue.setText(ip);
        mIpContainer.setVisibility(View.VISIBLE);
        activeButton();
//        mNodeLayout.post(new Runnable() {
//            @Override
//            public void run() {
                mRevelDrawable.changeState(State.PORT_READY);
//            }
//        });

    }

    private void activeButton(){
        mCenterButton.setEnabled(true);
        mCenterButton.setActivated(true);
    }

    @Override
    public void onPortUnready() {
        onWifiReady();
        mIpContainer.setVisibility(View.GONE);
        mIpValue.setText(null);

        enableButton();
        mRevelDrawable.changeState(State.PORT_UNREADY);
    }

    private void enableButton(){
        mCenterButton.setEnabled(true);
        mCenterButton.setActivated(false);
    }

    @Override
    public void onActionFail(@NonNull String message) {
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();

        switch (id){
            case R.id.monitor_button:
                mPresenter.togglePortState();
                break;

            default:
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {
//        if(mSwitch != buttonView){
//            return;
//        }

        if(WiFiModule.getInstance().isEnable() != isChecked) {
            WiFiModule.getInstance().enable(isChecked);
        }
//        if(mSwitch == buttonView) {
////            if (mInitAnimate) {
//                mSplashSwitch.setChecked(isChecked);
////                startSplashAnimate(isChecked);
////            }
//        }

//        if(mSplashSwitch == buttonView){

        mSwitch.setChecked(isChecked);

        if(!isChecked){
            mAnimate2WifiUnReady.start();
        }

//        }

        /*else {
            mSwitch.post(new Runnable() {
                @Override
                public void run() {
                    startSplashAnimate(isChecked);
                }
            });
        }*/
    }

    private void startSplashAnimate(boolean ready){
        if(mAnimate2WifiUnReady == null || mAnimate2WifiReady == null){
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
