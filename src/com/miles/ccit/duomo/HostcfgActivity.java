package com.miles.ccit.duomo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.miles.ccit.net.APICode;
import com.miles.ccit.util.AbsBaseActivity;
import com.miles.ccit.util.SendDataTask;

import java.util.List;
import java.util.Vector;

public class HostcfgActivity extends AbsBaseActivity {

    private EditText etWifiName, etWifiPwd, etChannel, etLan, etedit_yanma, etWan1, etYanma1, etWangguan1, etWan2, etYanma2, etWangguan2, etWan3, etYanma3, etWangguan3, etWangguanLan;
    private CheckBox ckbWan1, ckbWan2, ckbWan3, ckbLan;
    private MyBroadcastReciver broad = null;
    private LinearLayout linear_wan1, linear_wan2, linear_wan3, linear_lan;
    private Button btnAddRoute;
    private LinearLayout linear_route;
    private boolean ifSendData = false;
    //    List<RouteItem> routeItem = new Vector<RouteItem>();
//    List<View> routeView = new Vector<View>();

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

                String route = "";
                for (int i = 0; i < linear_route.getChildCount(); i++) {
                    View c = linear_route.getChildAt(i);
                    route += ",route=" + new RouteItem((EditText) c.findViewById(R.id.edit_pote), (EditText) c.findViewById(R.id.edit_taddr), (EditText) c.findViewById(R.id.edit_jaddr)).toString() + ",";
                }

                route = route.length() > 1 ? route.substring(0, route.length() - 1) : "";

                String lan = ckbLan.isChecked() ? "dhcp" : etLan.getText().toString() + "&" + etedit_yanma.getText().toString() + (TextUtils.isEmpty(etWangguanLan.getText().toString())?"":("&" + etWangguanLan.getText().toString()));
                String wan1 = ckbWan1.isChecked() ? "dhcp" : etWan1.getText().toString() + "&" + etYanma1.getText().toString()  + (TextUtils.isEmpty(etWangguan1.getText().toString())?"":("&" + etWangguan1.getText().toString()));
                String wan2 = ckbWan2.isChecked() ? "dhcp" : etWan2.getText().toString() + "&" + etYanma2.getText().toString() + (TextUtils.isEmpty(etWangguan2.getText().toString())?"":("&" + etWangguan2.getText().toString()));
                String wan3 = ckbWan3.isChecked() ? "dhcp" : etWan3.getText().toString() + "&" + etYanma3.getText().toString() + (TextUtils.isEmpty(etWangguan3.getText().toString())?"":("&" + etWangguan3.getText().toString()));

                String data = "name=" + etWifiName.getText().toString() + "," +
                        "pwd=" + etWifiPwd.getText().toString() + "," +
                        "channel=" + etChannel.getText().toString() + "," +
                        "lan=" + lan + "," +
                        "wan1=" + wan1 + "," +
                        "wan2=" + wan2 + "," +
                        "wan3=" + wan3 +
                        route;
                showprogressdialog();
                ifSendData = true;
                new SendDataTask().execute(APICode.SEND_RECV_HostCfg + "", data);
                Toast.makeText(mContext, "参数发送完成", Toast.LENGTH_SHORT).show();
                hideProgressDlg();
//                finish();
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
        etedit_yanma = (EditText) findViewById(R.id.edit_yanma);
        linear_route = (LinearLayout) findViewById(R.id.linear_route);
        etWan1 = (EditText) findViewById(R.id.edit_wanl1);
        etYanma1 = (EditText) findViewById(R.id.edit_wan1yanma);
        etWangguan1 = (EditText) findViewById(R.id.edit_wanl1wangguan);
        linear_wan1 = (LinearLayout) findViewById(R.id.linear_input_wan1);
        linear_lan = (LinearLayout) findViewById(R.id.linear_lan);
        ckbLan = (CheckBox) findViewById(R.id.ckb_lan);
        etWangguanLan = (EditText) findViewById(R.id.edit_lanwangguan);


        etWan2 = (EditText) findViewById(R.id.edit_wanl2);
        etYanma2 = (EditText) findViewById(R.id.edit_wan2yanma);
        etWangguan2 = (EditText) findViewById(R.id.edit_wanl2wangguan);
        linear_wan2 = (LinearLayout) findViewById(R.id.linear_input_wan2);

        etWan3 = (EditText) findViewById(R.id.edit_wanl3);
        etYanma3 = (EditText) findViewById(R.id.edit_wan3yanma);
        etWangguan3 = (EditText) findViewById(R.id.edit_wanl3wangguan);
        linear_wan3 = (LinearLayout) findViewById(R.id.linear_input_wan3);

//        etRout1 = (EditText) findViewById(R.id.edit_rout1);
//        etRout2 = (EditText) findViewById(R.id.edit_rout2);
//        etRout3 = (EditText) findViewById(R.id.edit_rout3);

        ckbWan1 = (CheckBox) findViewById(R.id.ckb_wanl1);
        ckbWan2 = (CheckBox) findViewById(R.id.ckb_wanl2);
        ckbWan3 = (CheckBox) findViewById(R.id.ckb_wanl3);
        ckbWan1.setOnCheckedChangeListener(ckbListener);
        ckbWan2.setOnCheckedChangeListener(ckbListener);
        ckbWan3.setOnCheckedChangeListener(ckbListener);
        ckbLan.setOnCheckedChangeListener(ckbListener);

        btnAddRoute = (Button) findViewById(R.id.bt_addroute);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(broad_config_host);
        broad = new MyBroadcastReciver();
        this.registerReceiver(broad, intentFilter);

        showprogressdialog();
        new SendDataTask().execute(APICode.SEND_QueryHost + "");


    }

    public void addRoute(View v) {
        addRoute(v, null);
    }

    public void addRoute(View v, String[] data) {
        View item = getLayoutInflater().inflate(R.layout.item_route, null);
        linear_route.addView(item);

        EditText etPort = (EditText) item.findViewById(R.id.edit_pote);
        EditText etTar = (EditText) item.findViewById(R.id.edit_taddr);
        EditText etJum = (EditText) item.findViewById(R.id.edit_jaddr);
        Button btDel = (Button) item.findViewById(R.id.bt_del);
        btDel.setTag(item);
        btDel.findViewById(R.id.bt_del).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                View tag = (View) v.getTag();
                linear_route.removeView(tag);
            }
        });

        if (data != null) {
            etPort.setText(data[0]);
            etTar.setText(data[1]);
            etJum.setText(data[2]);
        }

    }


    class RouteItem {
        EditText etPort;
        EditText etTargetAdd;
        EditText etJumpAdd;
        Button btnDel;


        public RouteItem(EditText etPort, EditText etTargetAdd, EditText etJumpAdd) {
            this.etPort = etPort;
            this.etTargetAdd = etTargetAdd;
            this.etJumpAdd = etJumpAdd;
        }

        public String toString() {
            return getEtPort() + "&" + getEtTargetAdd() + "&" + getEtJumpAdd();
        }

        public String getEtPort() {
            return etPort == null ? "" : etPort.getText().toString();
        }

        public void setEtPort(String etPort) {
            this.etPort.setText(etPort);
        }

        public String getEtTargetAdd() {
            return etTargetAdd == null ? "" : etTargetAdd.getText().toString();
        }

        public void setEtTargetAdd(String etTargetAdd) {
            this.etTargetAdd.setText(etTargetAdd);
        }

        public String getEtJumpAdd() {
            return etJumpAdd == null ? "" : etJumpAdd.getText().toString();
        }

        public void setEtJumpAdd(String etJumpAdd) {
            this.etJumpAdd.setText(etJumpAdd);
        }


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

            String lanStr[] = cfgdatas[3].split("=")[1].split("&");
            etLan.setText(lanStr[0]);
            etedit_yanma.setText(lanStr[1]);
            if (lanStr.length > 2) {
                etWangguanLan.setText(lanStr[2]);
            } else {
                etWangguanLan.setText("");
            }


            if (cfgdatas[4].split("=")[1].equals("dhcp")) {
                ckbWan1.setChecked(true);
            } else {
                String[] wan1 = cfgdatas[4].split("=")[1].split("&");
                etWan1.setText(wan1[0]);
                etYanma1.setText(wan1[1]);
                if (wan1.length > 2)
                    etWangguan1.setText(wan1[2]);
            }


            if (cfgdatas[5].split("=")[1].equals("dhcp")) {
                ckbWan2.setChecked(true);
            } else {
                String[] wan2 = cfgdatas[5].split("=")[1].split("&");
                etWan2.setText(wan2[0]);
                etYanma2.setText(wan2[1]);
                if (wan2.length > 2)
                    etWangguan2.setText(wan2[2]);
            }

            if (cfgdatas[6].split("=")[1].equals("dhcp")) {
                ckbWan3.setChecked(true);
            } else {
                String[] wan3 = cfgdatas[6].split("=")[1].split("&");
                etWan3.setText(wan3[0]);
                etYanma3.setText(wan3[1]);
                if (wan3.length > 2)
                    etWangguan3.setText(wan3[2]);
            }


            if (cfgdatas.length > 7) {
                for (int i = 7; i < cfgdatas.length; i++) {
                    String[] rous = cfgdatas[i].split("=")[1].split("&");
                    addRoute(null, rous);
                }
            }

//            etRout1.setText(cfgdatas.length > 7 ? : "");
//            etRout2.setText(cfgdatas.length > 8 ? cfgdatas[8].split("=")[1] : "");
//            etRout3.setText(cfgdatas.length > 9 ? cfgdatas[9].split("=")[1] : "");
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
                        linear_wan1.setVisibility(View.GONE);
                        break;
                    case R.id.ckb_wanl2:
                        etWan2.setText("dhcp");
                        etWan2.setEnabled(false);
                        linear_wan2.setVisibility(View.GONE);
                        break;

                    case R.id.ckb_wanl3:
                        etWan3.setText("dhcp");
                        etWan3.setEnabled(false);
                        linear_wan3.setVisibility(View.GONE);
                        break;
                    case R.id.ckb_lan:
                        etLan.setText("dhcp");
                        etLan.setEnabled(false);
                        linear_lan.setVisibility(View.GONE);
                        break;
                }
            } else {
                switch (compoundButton.getId()) {
                    case R.id.ckb_wanl1:
                        etWan1.setEnabled(true);
                        etWan1.setText("");
                        etYanma1.setText("");
                        etWangguan1.setText("");
                        linear_wan1.setVisibility(View.VISIBLE);
                        break;
                    case R.id.ckb_wanl2:
                        etWan2.setEnabled(true);
                        etWan2.setText("");
                        etYanma2.setText("");
                        etWangguan2.setText("");
                        linear_wan2.setVisibility(View.VISIBLE);
                        break;
                    case R.id.ckb_wanl3:
                        etWan3.setEnabled(true);
                        etWan3.setText("");
                        etYanma3.setText("");
                        etWangguan3.setText("");
                        linear_wan3.setVisibility(View.VISIBLE);
                        break;
                    case R.id.ckb_lan:
                        etLan.setEnabled(true);
                        etLan.setText("");
                        etWangguanLan.setText("");
                        etedit_yanma.setText("");
                        linear_lan.setVisibility(View.VISIBLE);
                        break;
                }
            }


        }
    };

}
