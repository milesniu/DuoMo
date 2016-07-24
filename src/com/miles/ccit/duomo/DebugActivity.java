package com.miles.ccit.duomo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.method.ScrollingMovementMethod;
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
        sp = PreferenceManager.getDefaultSharedPreferences(mContext);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(broad_debug_show_info);
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
                sp.edit().putString("debugInfo", "").commit();

            }
        });

        String data = sp.getString("debugInfo", "").replace(",", "\r\n");
        et_debug.setText(data);
        et_debug.setMovementMethod(ScrollingMovementMethod.getInstance());
        et_debug.setSelection(et_debug.length());


    }

    public class MyBroadcastReciver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            hideProgressDlg();
            String action = intent.getAction();

            if (action.equals(broad_debug_show_info)) {

                String data = sp.getString("debugInfo", "").replace(",", "\r\n");
                et_debug.setText(data);
                et_debug.setSelection(et_debug.length());

            }
        }

    }


}
