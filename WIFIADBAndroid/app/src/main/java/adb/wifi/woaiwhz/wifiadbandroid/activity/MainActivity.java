package adb.wifi.woaiwhz.wifiadbandroid.activity;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v4.util.SimpleArrayMap;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import adb.wifi.woaiwhz.wifiadbandroid.R;
import adb.wifi.woaiwhz.wifiadbandroid.base.CircularRevealDrawable;
import adb.wifi.woaiwhz.wifiadbandroid.base.OnTouchInterceptor;
import adb.wifi.woaiwhz.wifiadbandroid.base.State;
import adb.wifi.woaiwhz.wifiadbandroid.base.WiFiModule;
import adb.wifi.woaiwhz.wifiadbandroid.presenter.MainPresenter;

public class MainActivity extends AppCompatActivity
        implements MainPresenter.MainView,View.OnClickListener,CompoundButton.OnCheckedChangeListener{
    private View mNodeLayout;
    private View mMaskView;
    private View mLoading;
    private TextView mIpValue;
    private ImageButton mButton;
    private View mIpContainer;
    private SwitchCompat mSwitch;
    private MainPresenter mPresenter;
    private CircularRevealDrawable mRevelDrawable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNodeLayout = $(R.id.reveal_holder);
        mMaskView = $(R.id.mask);
        mButton = $(R.id.monitor_button);
        mLoading = $(R.id.loading);
        mIpValue = $(R.id.ip_value);
        mIpContainer = $(R.id.ip_layout);
        mSwitch = $(R.id.wifi_switch);

        mMaskView.setOnTouchListener(new OnTouchInterceptor());
        mButton.setOnClickListener(this);
        mSwitch.setOnCheckedChangeListener(this);

        Toolbar toolbar = $(R.id.tool_bar);
        setSupportActionBar(toolbar);

        SimpleArrayMap<Integer,Integer> map = new SimpleArrayMap<>();
        map.put(State.INIT,getResources().getColor(android.R.color.black));
        map.put(State.WIFI_UNREADY,getResources().getColor(R.color.wifi_unready_primary));
        map.put(State.PORT_UNREADY,getResources().getColor(R.color.port_unready_primary));
        map.put(State.PORT_READY,getResources().getColor(R.color.port_ready_primary));
        mRevelDrawable = new CircularRevealDrawable(map);
        mNodeLayout.setBackgroundDrawable(mRevelDrawable);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(mPresenter == null){
            mPresenter = new MainPresenter(this);
        }

        mPresenter.check();
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
        mSwitch.setChecked(false);
        disableButton();

        mIpContainer.setVisibility(View.GONE);
        mIpValue.setText(null);

        mRevelDrawable.changeState(State.WIFI_UNREADY);
        Toast.makeText(this, R.string.wifi_no_ready,Toast.LENGTH_SHORT).show();
    }

    private void disableButton(){
        mButton.setEnabled(false);
        mButton.setClickable(false);
        mButton.setActivated(false);
    }

    @Override
    public void onWifiReady() {
        mSwitch.setChecked(true);
        mButton.setClickable(true);
        mIpContainer.setVisibility(View.GONE);
        mIpValue.setText(null);
    }

    @Override
    public void onPortReady(String ip) {
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
        mButton.setEnabled(true);
        mButton.setActivated(true);
    }

    @Override
    public void onPortUnready() {
        mIpContainer.setVisibility(View.GONE);
        mIpValue.setText(null);

        enableButton();
        mRevelDrawable.changeState(State.PORT_UNREADY);
    }

    private void enableButton(){
        mButton.setEnabled(true);
        mButton.setActivated(false);
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
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(buttonView == mSwitch) {
            WiFiModule.getInstance().enable(isChecked);
        }
    }
}
