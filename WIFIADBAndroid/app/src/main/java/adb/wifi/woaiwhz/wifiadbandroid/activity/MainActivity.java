package adb.wifi.woaiwhz.wifiadbandroid.activity;

import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
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
    private View mNodeLayout;
    private View mMaskView;
    private View mLoading;
    private TextView mIpValue;
    private Button mButton;
    private View mIpContainer;
    private SwitchCompat mSwitch;
    private MainPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNodeLayout = $(R.id.node_root);
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
//            mMaskView.setBackgroundResource(R.color.mask_background);
            mLoading.setVisibility(View.VISIBLE);
        }else{
//            mMaskView.setBackgroundResource(android.R.color.transparent);
            mLoading.setVisibility(View.GONE);
        }
    }

    @Override
    public void onWifiNoReady() {
        mNodeLayout.setBackgroundResource(R.color.wifi_no_ready_primary);
        mSwitch.setChecked(false);
        mButton.setEnabled(false);
        mButton.setClickable(false);

        GradientDrawable drawable = (GradientDrawable) mButton.getBackground();
        drawable.setColor(getResources().getColor(R.color.wifi_no_ready_accent));
        mButton.setText(R.string.port_no_ready);
        mIpContainer.setVisibility(View.GONE);
        mIpValue.setText(null);

        Toast.makeText(this, R.string.wifi_no_ready,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onWifiReady() {
        mSwitch.setChecked(true);
        mButton.setEnabled(true);
        mButton.setClickable(true);
        mButton.setText(R.string.port_no_ready);
        mIpContainer.setVisibility(View.GONE);
        mIpValue.setText(null);
    }

    @Override
    public void onPortReady(String ip) {
        mIpValue.setText(ip);
        mIpContainer.setVisibility(View.VISIBLE);
        mButton.setText(R.string.ready);
        GradientDrawable drawable = (GradientDrawable) mButton.getBackground();
        drawable.setColor(getResources().getColor(R.color.port_ready_accent));
//        mButton.setBackgroundResource(R.color.port_ready_accent);
        mNodeLayout.setBackgroundResource(R.color.port_ready_primary);
    }

    @Override
    public void onPortNoReady() {
        mIpContainer.setVisibility(View.GONE);
        mIpValue.setText(null);
        mButton.setText(R.string.port_no_ready);
        mNodeLayout.setBackgroundResource(R.color.port_no_ready_primary);
        GradientDrawable drawable = (GradientDrawable) mButton.getBackground();
        drawable.setColor(getResources().getColor(R.color.port_no_ready_accent));
//        mButton.setBackgroundResource(R.color.port_no_ready_accent);
    }

    private void toggleWifiSwitch(boolean isChecked){
        WiFiModule.getInstance().enable(isChecked);
    }


    private void togglePortState() {
        mPresenter.togglePortState();
    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();

        switch (id){
            case R.id.monitor_button:
                togglePortState();
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
