package adb.wifi.woaiwhz.wifiadbandroid.activity;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import adb.wifi.woaiwhz.wifiadbandroid.R;
import adb.wifi.woaiwhz.wifiadbandroid.base.OnTouchInterceptor;
import adb.wifi.woaiwhz.wifiadbandroid.base.WiFiModule;
import adb.wifi.woaiwhz.wifiadbandroid.presenter.MainPresenter;

public class MainActivity extends AppCompatActivity
        implements MainPresenter.MainView,View.OnClickListener,CompoundButton.OnCheckedChangeListener{
    private View mMaskView;
    private View mLoading;
    private TextView mIpValue;
    private MainPresenter mPresenter;
    private SwitchCompat mSwitch;
    private Button mButton;
    private View mIpContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMaskView = $(R.id.mask);
        mMaskView.setOnTouchListener(new OnTouchInterceptor());

        mButton = $(R.id.monitor_button);
        mButton.setOnClickListener(this);

        mLoading = $(R.id.loading);
        mIpValue = $(R.id.ip_value);
        mIpContainer = $(R.id.ip_layout);
        mSwitch = $(R.id.wifi_switch);
        mSwitch.setOnCheckedChangeListener(this);
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
    public void pageLoading(boolean display) {
        if(display){
            showMask(true);
        }else {
            hideMask();
        }
    }

    void hideMask(){
        mMaskView.setVisibility(View.GONE);
    }

    void showMask(boolean withLoading){
        mMaskView.setVisibility(View.VISIBLE);

        if(withLoading){
            mLoading.setVisibility(View.VISIBLE);
        }else{
            mLoading.setVisibility(View.GONE);
        }
    }

    @Override
    public void onWifiNoReady() {
        mSwitch.setChecked(false);
        mButton.setEnabled(false);
        mButton.setClickable(false);
        onPortNoReady();

        Toast.makeText(this, R.string.wifi_no_ready,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onWifiReady() {
        mSwitch.setChecked(true);
        mButton.setEnabled(true);
        mButton.setClickable(true);
        onPortNoReady();
    }

    @Override
    public void onPortReady(String ip) {
        mIpValue.setText(ip);
        mIpContainer.setVisibility(View.VISIBLE);
        mButton.setText(R.string.ready);
    }

    @Override
    public void onPortNoReady() {
        mIpContainer.setVisibility(View.GONE);
        mIpValue.setText(null);
        mButton.setText(R.string.no_ready);
    }

    private void toggleWifiSwitch(boolean isChecked){
        WiFiModule.getInstance().enable(isChecked);
    }


    private void toggleMonitorState() {
        mPresenter.togglePortState();
    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();

        switch (id){
            case R.id.monitor_button:
                toggleMonitorState();
                break;

            default:
                break;
        }

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        toggleWifiSwitch(isChecked);
    }
}
