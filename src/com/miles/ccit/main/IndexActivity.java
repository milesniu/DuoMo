package com.miles.ccit.main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import com.miles.ccit.duomo.AboutActivity;
import com.miles.ccit.duomo.BroadCastctivity;
import com.miles.ccit.duomo.ContactActivity;
import com.miles.ccit.duomo.HostcfgActivity;
import com.miles.ccit.duomo.LoginActivity;
import com.miles.ccit.duomo.NetModelFragmentActivity;
import com.miles.ccit.duomo.R;
import com.miles.ccit.duomo.SettingActivity;
import com.miles.ccit.duomo.SpecialNetFragmentActivity;
import com.miles.ccit.duomo.SpecialVoiceActivity;
import com.miles.ccit.duomo.WiredModelActivity;
import com.miles.ccit.util.AbsBaseActivity;
import com.miles.ccit.util.BaseMapObject;
import com.miles.ccit.util.FileUtils;

public class IndexActivity extends AbsBaseActivity {

    //	public static boolean result = false;
    private MyBroadcastReciver broad = null;
    private BaseMapObject checkCount = null;

    private TextView tvUnreadSpecise;
    private TextView tvUnreadNetmodel;

    public static int underspecise = 0;
    public static int undernet = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);
        checkCount = FileUtils.getMapData4SD();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(broad_usimout_Action);
        broad = new MyBroadcastReciver();
        this.registerReceiver(broad, intentFilter);
//		new SendDataTask().execute(APICode.SEND_Login+"","123456","abdc123");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.index, menu);
        return true;
    }


    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        unregisterReceiver(broad);
        super.onDestroy();

    }

    public class MyBroadcastReciver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            hideProgressDlg();
            String action = intent.getAction();

            if (action.equals(broad_usimout_Action)) {
                findViewById(R.id.linear_title).setBackgroundResource(R.drawable.indextitlenologin);
                return;
            }
        }

    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub

        if (keyCode == KeyEvent.KEYCODE_BACK)        //Back键实现Home键返回，activity后台压栈
        {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);// 注意
            intent.addCategory(Intent.CATEGORY_HOME);
            this.startActivity(intent);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.bt_specialnet:
                startActivity(new Intent(mContext, SpecialNetFragmentActivity.class));
                underspecise = 0;
                break;
            case R.id.bt_contact:
                startActivity(new Intent(mContext, ContactActivity.class));
                break;
            case R.id.bt_haveline:
                startActivity(new Intent(mContext, WiredModelActivity.class));
                break;
            case R.id.bt_broadcast:
                startActivity(new Intent(mContext, BroadCastctivity.class));

                break;
            case R.id.bt_specialway:
                startActivity(new Intent(mContext, SpecialVoiceActivity.class));

                break;
            case R.id.bt_setting:
                startActivity(new Intent(mContext, SettingActivity.class));

                break;
            case R.id.bt_about:
                startActivity(new Intent(mContext, HostcfgActivity.class));

                break;
            case R.id.bt_netmodle:
                startActivity(new Intent(mContext, NetModelFragmentActivity.class));
                undernet = 0;
                break;
            case R.id.linear_title:
                if (!LoginActivity.isLogin) {
                    startActivityForResult(new Intent(mContext, LoginActivity.class), 3);
                }

                break;
        }
    }


    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        if (LoginActivity.isLogin) {
            findViewById(R.id.linear_title).setBackgroundResource(R.drawable.loginok8);
            tvUnreadSpecise.setText("" + underspecise);
            tvUnreadNetmodel.setText("" + undernet);
            if (underspecise == 0) {
                tvUnreadSpecise.setVisibility(View.GONE);
            } else {
                tvUnreadSpecise.setVisibility(View.VISIBLE);
            }
            if (undernet == 0) {
                tvUnreadNetmodel.setVisibility(View.GONE);
            } else {
                tvUnreadNetmodel.setVisibility(View.VISIBLE);
            }
        } else {
            findViewById(R.id.linear_title).setBackgroundResource(R.drawable.indextitlenologin);
            tvUnreadNetmodel.setVisibility(View.GONE);
            tvUnreadSpecise.setVisibility(View.GONE);
        }
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void initView() {
        // TODO Auto-generated method stub

        tvUnreadSpecise = (TextView) findViewById(R.id.text_unread_specise);
        tvUnreadNetmodel = (TextView) findViewById(R.id.text_unread_netmodel);


        findViewById(R.id.bt_specialnet).setOnClickListener(this);
        findViewById(R.id.bt_contact).setOnClickListener(this);
        findViewById(R.id.bt_haveline).setOnClickListener(this);
        findViewById(R.id.bt_broadcast).setOnClickListener(this);
        findViewById(R.id.bt_specialway).setOnClickListener(this);
        findViewById(R.id.bt_setting).setOnClickListener(this);
        findViewById(R.id.bt_about).setOnClickListener(this);
        findViewById(R.id.linear_title).setOnClickListener(this);
        findViewById(R.id.bt_netmodle).setOnClickListener(this);
    }

}
