package com.miles.ccit.duomo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;

import com.miles.ccit.util.AbsBaseActivity;
import com.miles.ccit.util.ByteUtil;
import com.miles.ccit.util.MyLog;

public class DebugActivity extends AbsBaseActivity {

    private EditText et_debug;
    private MyBroadcastReciver broad = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(broad_debug_info);
        broad = new MyBroadcastReciver();
        this.registerReceiver(broad, intentFilter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.about, menu);
        return true;
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.bt_left:
                this.finish();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(broad);
    }

    @Override
    public void initView() {
        // TODO Auto-generated method stub
        initBaseView("调试信息");
        Btn_Left.setOnClickListener(this);
        Btn_Right.setVisibility(View.INVISIBLE);
        et_debug = (EditText) findViewById(R.id.edit_debug);
        findViewById(R.id.bt_clear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                et_debug.setText("");
            }
        });
    }

    public class MyBroadcastReciver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            hideProgressDlg();
            String action = intent.getAction();

            if (action.equals(broad_debug_info)) {
                byte[] data = intent.getByteArrayExtra("data");
                int len = ByteUtil.oneByte2oneInt(data[5]);      //获取内容长度，1字节
                byte[] info = new byte[len];
                System.arraycopy(data, 6, info, 0, len);
                try {
                    String content = new String(info, "UTF-8");
                    et_debug.setText(et_debug.getText().toString() + "------------------\r\n" + content + "\r\n");
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }

    }


}
