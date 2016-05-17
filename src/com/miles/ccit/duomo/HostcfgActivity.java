package com.miles.ccit.duomo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.miles.ccit.net.APICode;
import com.miles.ccit.util.AbsBaseActivity;
import com.miles.ccit.util.SendDataTask;

public class HostcfgActivity extends AbsBaseActivity {

    private EditText etWifiName, etWifiPwd, etChannel, etLan, etWan1, etWan2, etWan3, etRout1, etRout2, etRout3;
    private CheckBox ckbWan1, ckbWan2, ckbWan3;
    private MyBroadcastReciver broad = null;

    private boolean ifSendData = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hostcfg);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_left:
                this.finish();
                break;
            case R.id.bt_right:
                String data = "name=" + etWifiName.getText().toString() + "," +
                        "pwd=" + etWifiPwd.getText().toString() + "," +
                        "channel=" + etChannel.getText().toString() + "," +
                        "lan=" + etLan.getText().toString() + "," +
                        "wan1=" + etWan1.getText().toString() + "," +
                        "wan2=" + etWan2.getText().toString() + ",wan3=" + etWan3.getText().toString() + ",route=" + etRout1.getText().toString() + ",route=" + etRout2.getText().toString();

                showprogressdialog();
                ifSendData = true;
                new SendDataTask().execute(APICode.SEND_RECV_HostCfg + "", data);
                break;
        }
    }


    @Override
    public void initView() {
        initBaseView("主机配置");
        Btn_Left.setOnClickListener(this);
        Btn_Right.setOnClickListener(this);
        Btn_Right.setVisibility(View.VISIBLE);
        Btn_Right.setBackgroundResource(R.drawable.btsure);
        etWifiName = (EditText) findViewById(R.id.edit_wifiname);
        etWifiPwd = (EditText) findViewById(R.id.edit_wifipwd);
        etChannel = (EditText) findViewById(R.id.wifichannel);
        etLan = (EditText) findViewById(R.id.edit_lan);
        etWan1 = (EditText) findViewById(R.id.edit_wanl1);
        etWan2 = (EditText) findViewById(R.id.edit_wanl2);
        etWan3 = (EditText) findViewById(R.id.edit_wanl3);
        etRout1 = (EditText) findViewById(R.id.edit_rout1);
        etRout2 = (EditText) findViewById(R.id.edit_rout2);
        etRout3 = (EditText) findViewById(R.id.edit_rout3);

        ckbWan1 = (CheckBox) findViewById(R.id.ckb_wanl1);
        ckbWan2 = (CheckBox) findViewById(R.id.ckb_wanl2);
        ckbWan3 = (CheckBox) findViewById(R.id.ckb_wanl3);
        ckbWan1.setOnCheckedChangeListener(ckbListener);
        ckbWan2.setOnCheckedChangeListener(ckbListener);
        ckbWan3.setOnCheckedChangeListener(ckbListener);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(broad_config_host);
        broad = new MyBroadcastReciver();
        this.registerReceiver(broad, intentFilter);

        showprogressdialog();
        new SendDataTask().execute(APICode.SEND_QueryHost + "");


    }

    public class MyBroadcastReciver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            hideProgressDlg();
            if (ifSendData) {
                finish();
                return;
            }
            String data = intent.getStringExtra("data");
            String[] cfgdatas = data.split(",");
            etWifiName.setText(cfgdatas[0].split("=")[1]);
            etWifiPwd.setText(cfgdatas[1].split("=")[1]);
            etChannel.setText(cfgdatas[2].split("=")[1]);
            etLan.setText(cfgdatas[3].split("=")[1]);
            String wan1 = cfgdatas[4].split("=")[1];
            etWan1.setText(wan1);
            if (wan1.equals("dhcp")) {
                ckbWan1.setChecked(true);
            }
            String wan2 = cfgdatas[5].split("=")[1];
            etWan2.setText(wan2);
            if (wan2.equals("dhcp")) {
                ckbWan2.setChecked(true);
            }
            String wan3 = cfgdatas[6].split("=")[1];
            etWan3.setText(wan3);
            if (wan3.equals("dhcp")) {
                ckbWan3.setChecked(true);
            }
            etRout1.setText(cfgdatas.length > 7 ? cfgdatas[7].split("=")[1] : "");
            etRout2.setText(cfgdatas.length > 8 ? cfgdatas[8].split("=")[1] : "");
            etRout3.setText(cfgdatas.length > 9 ? cfgdatas[9].split("=")[1] : "");
        }

    }


    private CompoundButton.OnCheckedChangeListener ckbListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            if (b) {
                switch (compoundButton.getId()) {
                    case R.id.ckb_wanl1:
                        etWan1.setText("dhcp");
                        etWan1.setEnabled(false);
                        break;
                    case R.id.ckb_wanl2:
                        etWan2.setText("dhcp");
                        etWan2.setEnabled(false);
                        break;
                    case R.id.ckb_wanl3:
                        etWan3.setText("dhcp");
                        etWan3.setEnabled(false);
                        break;
                }
            } else {
                switch (compoundButton.getId()) {
                    case R.id.ckb_wanl1:
                        etWan1.setText("");
                        etWan1.setEnabled(true);
                        break;
                    case R.id.ckb_wanl2:
                        etWan2.setText("");
                        etWan2.setEnabled(true);
                        break;
                    case R.id.ckb_wanl3:
                        etWan3.setText("");
                        etWan3.setEnabled(true);
                        break;
                }
            }
        }
    };

}
