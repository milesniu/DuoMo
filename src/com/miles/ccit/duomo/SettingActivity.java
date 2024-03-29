package com.miles.ccit.duomo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ToggleButton;

import com.miles.ccit.net.APICode;
import com.miles.ccit.net.SocketConnection;
import com.miles.ccit.util.AbsBaseActivity;
import com.miles.ccit.util.BaseMapObject;
import com.miles.ccit.util.MyLog;
import com.miles.ccit.util.O;
import com.miles.ccit.util.SendDataTask;
import com.redfox.voip_pro.RedfoxManager;

public class SettingActivity extends AbsBaseActivity {

    Button Btn_Singout;
    ToggleButton TBtn_HealthCheck, TBtn_Encrpty, TBtn_3G, TBtn_ZKEncrpyt;
    public static BaseMapObject LoactionInfo = new BaseMapObject();
    private MyBroadcastReciver broad = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        sp = PreferenceManager.getDefaultSharedPreferences(mContext);
        Btn_Singout = (Button) findViewById(R.id.bt_singout);
        TBtn_HealthCheck = (ToggleButton) findViewById(R.id.toggleButton1);
        TBtn_Encrpty = (ToggleButton) findViewById(R.id.toggle_jiami);
        TBtn_3G = (ToggleButton) findViewById(R.id.toggle_3g);
        TBtn_ZKEncrpyt = (ToggleButton) findViewById(R.id.toggle_zkencrpyt);
        TBtn_HealthCheck.setVisibility(View.GONE);
        if (SocketConnection.isHealthCheck) {
            TBtn_HealthCheck.setChecked(true);
        } else {
            TBtn_HealthCheck.setChecked(false);
        }

        O.isEncrypt = sp.getBoolean("jiami", false);


        TBtn_Encrpty.setChecked(O.isEncrypt);
        TBtn_Encrpty.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                O.isEncrypt = b;
                sp.edit().putBoolean("jiami", b).commit();
            }
        });

        TBtn_3G.setChecked(sp.getBoolean("3g", false));
        TBtn_3G.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sp.edit().putBoolean("3g", isChecked).commit();

                new SendDataTask().execute(APICode.SEND_3G + "", isChecked + "");

            }
        });

        TBtn_ZKEncrpyt.setChecked(sp.getBoolean("zkjiami", false));
        TBtn_ZKEncrpyt.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sp.edit().putBoolean("zkjiami", isChecked).commit();

                new SendDataTask().execute(APICode.SEND_ZKJIAMI + "", isChecked + "");

            }
        });

        TBtn_HealthCheck.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SocketConnection.isHealthCheck = isChecked;
            }
        });
        if (LoginActivity.isLogin) {
            Btn_Singout.setVisibility(View.VISIBLE);
            Btn_Singout.setOnClickListener(this);
            TBtn_HealthCheck.setVisibility(View.VISIBLE);
        } else {
            Btn_Singout.setVisibility(View.GONE);
        }

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(broad_backlocation_Action);
        broad = new MyBroadcastReciver();
        this.registerReceiver(broad, intentFilter);

        showprogressdialog();
        new SendDataTask().execute(APICode.SEND_Location + "");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.setting, menu);
        return true;
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.bt_left:
                this.finish();
                break;
            case R.id.rela_changepws:
                startActivity(new Intent(mContext, ChangePwdActivity.class));
                break;
            case R.id.rela_checklocation:
                startActivity(new Intent(mContext, HostLocationActivity.class));
                break;
            case R.id.rela_checkversion:
                startActivity(new Intent(mContext, HostVersionActivity.class));
                break;
            case R.id.rela_checkstatus:
                startActivity(new Intent(mContext, HostStatusActivity.class));
                break;
            case R.id.rela_syssetting:
                startActivity(new Intent(mContext, SystemCfgActivity.class));
                break;
            case R.id.rela_sendsetting:
                startActivity(new Intent(mContext, SendCfgActivity.class));
                break;
            case R.id.rela_debug:
                startActivity(new Intent(mContext, DebugActivity.class));
                break;
            case R.id.rela_channelset:
                startActivity(new Intent(mContext, ChannelSetActivity.class));
                break;
            case R.id.bt_singout:
                MyLog.showToast(mContext, "您已成功注销！");
                new SendDataTask().execute(APICode.SEND_Logout + "");
                SocketConnection.getInstance().canleSocket();
                RedfoxManager.getLc().clearAuthInfos();
                RedfoxManager.getLc().clearProxyConfigs();
                Btn_Singout.setVisibility(View.GONE);
                break;

        }
    }

    @Override
    public void initView() {
        // TODO Auto-generated method stub
        initBaseView("设置");
        Btn_Left.setOnClickListener(this);
        Btn_Right.setVisibility(View.INVISIBLE);
        findViewById(R.id.rela_sendsetting).setOnClickListener(this);
        findViewById(R.id.rela_syssetting).setOnClickListener(this);
        findViewById(R.id.rela_checkversion).setOnClickListener(this);
        findViewById(R.id.rela_checklocation).setOnClickListener(this);
        findViewById(R.id.rela_checkstatus).setOnClickListener(this);
        findViewById(R.id.rela_changepws).setOnClickListener(this);
        findViewById(R.id.rela_debug).setOnClickListener(this);
        findViewById(R.id.rela_channelset).setOnClickListener(this);
    }


    public class MyBroadcastReciver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            hideProgressDlg();
        }

    }

}
