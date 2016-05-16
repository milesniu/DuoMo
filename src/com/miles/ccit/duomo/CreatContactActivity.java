package com.miles.ccit.duomo;

import java.util.HashMap;

import com.miles.ccit.duomo.R;
import com.miles.ccit.duomo.R.layout;
import com.miles.ccit.duomo.R.menu;
import com.miles.ccit.net.APICode;
import com.miles.ccit.util.AbsBaseActivity;
import com.miles.ccit.util.BaseMapObject;
import com.miles.ccit.util.O;
import com.miles.ccit.util.SendDataTask;
import com.miles.ccit.util.UnixTime;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.EdgeEffect;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Toast;

public class CreatContactActivity extends AbsBaseActivity {

    private EditText edit_Num;
    private EditText edit_Company;
    private EditText edit_Remarks;
    private EditText edit_IP;
    private EditText edit_SIM;
    private RadioButton radio_wireness, radio_wired, radio_net;
    private BaseMapObject tmp;
    private String havecode = "";
    private String currentcode = "";
    private int altype = -1;
    private LinearLayout linear_netinfo;

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newcontact);
        if (getIntent().getSerializableExtra("contact") != null) {
            tmp = BaseMapObject.HashtoMyself((HashMap<String, Object>) getIntent().getSerializableExtra("contact"));
        } else if (getIntent().getStringExtra("number") != null) {
            havecode = getIntent().getStringExtra("number");
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.newcontact, menu);
        return true;
    }


    @Override
    public void initView() {
        // TODO Auto-generated method stub
        initBaseView("联系人");
        Btn_Right.setBackgroundResource(R.drawable.btsure);
        edit_Num = (EditText) findViewById(R.id.edit_num);
        edit_Company = (EditText) findViewById(R.id.edit_company);
        edit_Remarks = (EditText) findViewById(R.id.edit_remarks);
        edit_IP = (EditText) findViewById(R.id.edit_ip);
        edit_SIM = (EditText) findViewById(R.id.edit_sim);
        radio_wireness = (RadioButton) findViewById(R.id.radio_wireness);
        radio_wired = (RadioButton) findViewById(R.id.radio_wired);
        radio_net = (RadioButton) findViewById(R.id.radio_net);

        radio_wireness.setOnClickListener(this);
        radio_wired.setOnClickListener(this);
        radio_net.setOnClickListener(this);
        linear_netinfo = (LinearLayout) findViewById(R.id.linear_netinfo);
        if (tmp != null) {
//			Btn_Right.setText("修改");
            altype = Integer.parseInt(tmp.get("type").toString());
            edit_Company.setText(tmp.get("name").toString());
            edit_Remarks.setText(tmp.get("remarks").toString());
            currentcode = tmp.get("number").toString();
            if (tmp.get("type").toString().equals("0")) {
                radio_wireness.setChecked(true);
                edit_IP.setVisibility(View.VISIBLE);
                edit_SIM.setVisibility(View.VISIBLE);
                String[] nums = tmp.get("number").toString().split("#");
                edit_Num.setText(nums[0]);
                edit_IP.setText(nums[1]);
                edit_SIM.setText(nums[2]);
            } else if (tmp.get("type").toString().equals("1")) {
                radio_wired.setChecked(true);
                edit_Num.setText(tmp.get("number").toString());
                edit_IP.setVisibility(View.GONE);
                edit_SIM.setVisibility(View.GONE);
            } else if (tmp.get("type").toString().equals("2")) {
                radio_net.setChecked(true);
            }
        } else {
            edit_Num.setText(havecode);
        }

    }

    private void sendContact2Web(int type, String data) {
        new SendDataTask().execute(type == O.WIRENESS ? (APICode.SEND_WirelessContact + "") : (APICode.SEND_WiredContact + ""), data);
    }


    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.bt_left:
                this.finish();
                break;
            case R.id.radio_wireness:
            case R.id.radio_net:
                edit_IP.setVisibility(View.VISIBLE);
                edit_SIM.setVisibility(View.VISIBLE);
                break;
            case R.id.radio_wired:
                edit_IP.setVisibility(View.GONE);
                edit_SIM.setVisibility(View.GONE);
                break;
            case R.id.bt_right:
                String name = edit_Company.getText().toString();
                String number = edit_Num.getText().toString() + (radio_wired.isChecked() ? "" : "#" + edit_IP.getText().toString() + "#" + edit_SIM.getText().toString());
                String type = radio_wireness.isChecked() ? "0" : (radio_wired.isChecked() ? "1" : "2");
                long ret = 0;
                if (name.equals("") || number.equals("")) {
                    Toast.makeText(mContext, "必要信息不能为空。", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (tmp == null) {
                    BaseMapObject contact = new BaseMapObject();
                    contact.put("id", null);
                    contact.put("name", name);
                    contact.put("number", number);
                    contact.put("type", type);
                    contact.put("remarks", edit_Remarks.getText().toString());
                    contact.put("creattime", UnixTime.getStrCurrentUnixTime());
                    ret = contact.InsertObj2DB(mContext, "contact");
                    if (ret == -1) {
                        Toast.makeText(mContext, "号码已经存在，请勿重复添加。", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } else {
                    tmp.put("name", edit_Company.getText().toString());
                    tmp.put("number", edit_Num.getText().toString());
                    tmp.put("type", radio_wireness.isChecked() ? "0" : "1");
                    tmp.put("remarks", edit_Remarks.getText().toString());
                    if (edit_Num.getText().toString().equals(currentcode)) {
                        this.finish();
                        return;
                    }
                    if (tmp.UpdateMyself(mContext, "contact") == -1) {
                        Toast.makeText(mContext, "号码已经存在，请检查。", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                String[] numbers = number.split("#");
                sendContact2Web(Integer.parseInt(type), numbers[0] + "," + name + (radio_wired.isChecked() ? "" : "," + numbers[1] + "," + numbers[2]));

                this.finish();
                break;
        }
    }

}
