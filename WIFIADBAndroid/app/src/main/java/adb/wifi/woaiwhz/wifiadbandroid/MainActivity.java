package adb.wifi.woaiwhz.wifiadbandroid;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    public static final int LISTEN_2_PORT_FAIL = 1;
    public static final int LISTEN_2_PORT_SUCCESS = 1 << 1;


    private Handler mHandler;
    private PortMonitor mMonitor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mHandler = new Handler();
        mMonitor = new PortMonitor(mHandler);
    }

    public void listenInPort(View view) {
        mMonitor.start();
    }
}
