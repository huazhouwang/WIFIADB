package adb.wifi.woaiwhz.wifiadbandroid.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.animation.TypeEvaluator;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Property;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;

import adb.wifi.woaiwhz.wifiadbandroid.R;
import adb.wifi.woaiwhz.wifiadbandroid.base.CircularRevealDrawable;
import adb.wifi.woaiwhz.wifiadbandroid.base.OnTouchInterceptor;
import adb.wifi.woaiwhz.wifiadbandroid.base.WiFiModule;
import adb.wifi.woaiwhz.wifiadbandroid.bean.AlphaProperty;
import adb.wifi.woaiwhz.wifiadbandroid.bean.BottomProperty;
import adb.wifi.woaiwhz.wifiadbandroid.bean.ColorProperty;
import adb.wifi.woaiwhz.wifiadbandroid.bean.ScaleProperty;
import adb.wifi.woaiwhz.wifiadbandroid.bean.State;
import adb.wifi.woaiwhz.wifiadbandroid.bean.XProperty;
import adb.wifi.woaiwhz.wifiadbandroid.bean.YProperty;
import adb.wifi.woaiwhz.wifiadbandroid.presenter.MainPresenter;

public class MainActivity extends AppCompatActivity
        implements MainPresenter.MainView,View.OnClickListener,CompoundButton.OnCheckedChangeListener,PopupMenu.OnMenuItemClickListener{
    private View mSplashContainer;
    private View mRevealHolderView;
    private View mMaskView;
    private View mLoading;
    private TextView mIpValue;
    private ImageButton mCenterButton;
    private View mIpContainer;
    private Toolbar mToolbar;
    private SwitchCompat mSplashSwitch;
    private SwitchCompat mToolbarSwitch;
    private View mIconPortReady;
    private View mIconPortUnready;
    private View mShowMenu;

    private PopupMenu mPopupMenu;
    private CircularRevealDrawable mRevelDrawable;
    private MainPresenter mPresenter;
    private SwitchCompat mCurrentSwitch;
    private SwitchCompat mHidingSwitch;
    private AnimatorSet mWifiReadyAnimate;
    private AnimatorSet mWifiUnreadyAnimate;

    private AnimatorSet mPortReadyAnimate;
    private AnimatorSet mPortUnreadyAnimate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSplashContainer = $(R.id.splash_container);
        mSplashSwitch = $(R.id.splash_switch_wifi);
        mRevealHolderView = $(R.id.reveal_layout);
        mMaskView = $(R.id.mask);
        mCenterButton = $(R.id.center_button);
        mIconPortReady = $(R.id.ic_port_ready);
        mIconPortUnready = $(R.id.ic_port_unready);
        mLoading = $(R.id.loading);
        mIpValue = $(R.id.ip_value);
        mIpContainer = $(R.id.ip_layout);
        mToolbarSwitch = $(R.id.switch_real);
        mToolbar = $(R.id.toolbar);
        mShowMenu = $(R.id.main_menu);

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
        mShowMenu.setOnClickListener(this);

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

    private void initAnimate(){
        initWifiStateAnimate();
        initPortStateAnimate();
    }

    private void initPortStateAnimate(){
        final GradientDrawable centerDrawable = (GradientDrawable) mCenterButton.getDrawable();
        final int portReadyAccent = ResourcesCompat.getColor(getResources(),R.color.port_ready_accent,null);
        final int portUnreadyAccent = ResourcesCompat.getColor(getResources(),R.color.port_unready_accent,null);
        final Property<GradientDrawable,Integer> colorProperty = new ColorProperty(Integer.class,"colorProperty");
        final Property<View,Float> alphaProperty = new AlphaProperty(Float.class,"alphaProperty");
        final Interpolator interpolator = new AccelerateDecelerateInterpolator();
        final TypeEvaluator typeEvaluator = new ArgbEvaluator();

        final ObjectAnimator readyCenterButtonColor = ObjectAnimator.ofInt(centerDrawable,colorProperty,portUnreadyAccent,portReadyAccent);
        readyCenterButtonColor.setEvaluator(typeEvaluator);

        mPortReadyAnimate = new AnimatorSet();
        mPortReadyAnimate.setInterpolator(interpolator);

        AnimatorSet iconPortReadyAnimateSet = new AnimatorSet();
        iconPortReadyAnimateSet.play(ObjectAnimator.ofFloat(mIconPortUnready,alphaProperty,0f))
                .before(ObjectAnimator.ofFloat(mIconPortReady,alphaProperty,1f));

        mPortReadyAnimate.play(readyCenterButtonColor)
                .with(ObjectAnimator.ofFloat(mIpContainer,alphaProperty,1f))
                .with(iconPortReadyAnimateSet);

        mPortReadyAnimate.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                mRevelDrawable.changeState(State.PORT_READY);
            }
        });

        final ObjectAnimator unreadyCenterButton = ObjectAnimator.ofInt(centerDrawable,colorProperty,portReadyAccent,portUnreadyAccent);
        unreadyCenterButton.setEvaluator(typeEvaluator);

        AnimatorSet iconPortUnreadyAnimateSet = new AnimatorSet();
        iconPortUnreadyAnimateSet.play(ObjectAnimator.ofFloat(mIconPortReady,alphaProperty,0f))
                .before(ObjectAnimator.ofFloat(mIconPortUnready,alphaProperty,1f));

        mPortUnreadyAnimate = new AnimatorSet();
        mPortUnreadyAnimate.setInterpolator(interpolator);
        mPortUnreadyAnimate.play(unreadyCenterButton)
                .with(ObjectAnimator.ofFloat(mIpContainer,alphaProperty,0f))
                .with(iconPortUnreadyAnimateSet);


        mPortUnreadyAnimate.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                mRevelDrawable.changeState(State.PORT_UNREADY);
            }
        });
    }

    private void initWifiStateAnimate(){
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
        final Property<View,Float> xProperty = new XProperty(Float.class,"xProperty");
        final Property<View,Float> yProperty = new YProperty(Float.class,"yProperty");
        final Property<View,Integer> bottomProperty = new BottomProperty(Integer.class,"bottomProperty");
        final Property<View,Float> scaleProperty = new ScaleProperty(Float.class,"scaleProperty");

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
        wifiStateAnimate(false);
    }

    private void onWifiReady() {
        mCurrentSwitch.setChecked(true);
        wifiStateAnimate(true);
    }

    @Override
    public void onPortReady(String ip) {
        onWifiReady();
        mIpValue.setText(ip);
        portStateAnimate(true);
    }

    @Override
    public void onPortUnready() {
        onWifiReady();
        portStateAnimate(false);
    }

    private void portStateAnimate(boolean ready){
        if(!isAnimateReady()){
            return;
        }

        if(mPortReadyAnimate.isRunning()){
            mPortReadyAnimate.cancel();
        }

        if(mPortUnreadyAnimate.isRunning()){
            mPortUnreadyAnimate.cancel();
        }

        if(ready){
            mPortReadyAnimate.start();
        }else {
            mPortUnreadyAnimate.start();
        }

    }

    @Override
    public void onActionFail(@NonNull String message) {
        Snackbar.make(mRevealHolderView,message,Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        if(v == mCenterButton){
            mPresenter.toggle();
        }else if(v == mCurrentSwitch){
            final boolean isChecked = mCurrentSwitch.isChecked();
            WiFiModule.getInstance().enable(isChecked);
        }else if(v == mShowMenu){
            if(mPopupMenu == null){
                mPopupMenu = new PopupMenu(this,mShowMenu);
                mPopupMenu.getMenuInflater().inflate(R.menu.menu_main,mPopupMenu.getMenu());
                mPopupMenu.setOnMenuItemClickListener(this);
            }

            mPopupMenu.show();
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
        //只需判断一个就好
        return mWifiUnreadyAnimate != null;
    }

    private void wifiStateAnimate(boolean ready){
        if(!isAnimateReady()){
            return;
        }

        if(mWifiReadyAnimate.isRunning()){
            mWifiReadyAnimate.cancel();
        }

        if(mWifiUnreadyAnimate.isRunning()){
            mWifiUnreadyAnimate.cancel();
        }

        if(ready){
            mWifiReadyAnimate.start();
        }else {
            mWifiUnreadyAnimate.start();
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        final int id = item.getItemId();

        switch (id){
            case R.id.get_start:
                browse("https://github.com/Sausure/WIFIADB");
                return true;

            case R.id.about:
                browse("https://github.com/Sausure/WIFIADB/tree/master/WIFIADBAndroid");
                return true;

            case R.id.get_intellij_plugin:
                browse("https://github.com/Sausure/WIFIADB/tree/master/WIFIADBIntelliJPlugin");
                return true;

            default:
                return false;

        }
    }

    private void browse(String url){
        if(TextUtils.isEmpty(url)){
            return;
        }

        final Uri uri = Uri.parse(url);
        final Intent intent = new Intent(Intent.ACTION_VIEW,uri);
        startActivity(intent);
    }
}
