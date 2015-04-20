package com.miles.ccit.duomo;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.miles.ccit.util.AbsCreatCodeActivity;
import com.miles.ccit.util.MyLog;

import java.util.HashMap;

public class CreatcodeApplyresActivity extends AbsCreatCodeActivity
{

    private EditText edit_code;
    private EditText edit_name;
    private EditText edit_count;
    private EditText edit_unit;
    private EditText edit_address;
    private HashMap<String, Object> options = new HashMap<String, Object>();
    private String contact;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creatcode_applyres);
        contact = getIntent().getStringExtra("contact");
        initBaseView("资源申请");
        Btn_Left.setOnClickListener(this);
        Btn_Right.setBackgroundResource(R.drawable.sendmail);
        Btn_Right.setOnClickListener(this);

        edit_code = (EditText) findViewById(R.id.edit_code);
        edit_name = (EditText) findViewById(R.id.edit_name);
        edit_count = (EditText) findViewById(R.id.edit_count);
        edit_unit = (EditText) findViewById(R.id.edit_unit);
        edit_address = (EditText) findViewById(R.id.edit_address);
        findViewById(R.id.bt_addoption).setOnClickListener(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.creatcode_applyres, menu);
        return true;
    }

    @Override
    public void onClick(View v)
    {
        // TODO Auto-generated method stub
        switch (v.getId())
        {
            case R.id.bt_left:
                this.finish();
                break;
            case R.id.bt_right:
                if (edit_code.getText().toString().equals(""))
                {
                    MyLog.showToast(mContext, "请输入应急队伍编号");
                    return;
                } else if (Integer.parseInt(edit_code.getText().toString()) > 1000)
                {
                    MyLog.showToast(mContext, "应急队伍编号应在0~1000之间");
                    return;
                } else if (edit_name.getText().toString().equals(""))
                {
                    MyLog.showToast(mContext, "请输入物资名称");
                    return;
                } else if (edit_name.getText().toString().getBytes().length > 9)
                {
                    MyLog.showToast(mContext, "物资名称不能超过9个字节");
                    return;
                } else if (edit_count.getText().toString().equals(""))
                {
                    MyLog.showToast(mContext, "请输入物资数量");
                    return;
                } else if (Integer.parseInt(edit_count.getText().toString()) > 1000)
                {
                    MyLog.showToast(mContext, "物资数量应在0~1000之间");
                    return;
                } else if (edit_unit.getText().toString().equals(""))
                {
                    MyLog.showToast(mContext, "请输入物资单位");
                    return;
                } else if (edit_unit.getText().toString().getBytes().length > 9)
                {
                    MyLog.showToast(mContext, "物资单位不能超过9个字节");
                    return;
                } else if (edit_address.getText().toString().getBytes().length > 9)
                {
                    MyLog.showToast(mContext, "受领地地名不能超过9个字节");
                    return;
                }


                try
                {
                    double lat = Double.parseDouble(options.get("lat").toString());
                    double lng = Double.parseDouble(options.get("lng").toString());
                } catch (Exception e)
                {
                    MyLog.showToast(mContext, "请选择经纬度坐标");
                    return;
                }
                sendCodedirc(contact, composeSendData());
                this.finish();
                break;
            case R.id.bt_addoption:
                startActivityForResult(new Intent(mContext, CreatOptionActivity.class).putExtra("typecode", CreatOptionActivity.APPLY), CreatOptionActivity.APPLY);
                break;
        }
    }

    private String composeSendData()
    {
        double lat = Double.parseDouble(options.get("lat").toString());
        double lng = Double.parseDouble(options.get("lng").toString());
        String P72x = lng < 0 ? "1" : "0";
        String P73x = lng < 0 ? ((lng * -1) + "") : (lng + "");
        P73x = P73x.substring(0, 3) + "." + P73x.substring(4, 6) + "." + P73x.substring(6, 8) + "." + P73x.substring(8);
        String P74x = lat < 0 ? "1" : "0";
        String P75x = lat < 0 ? ((lat * -1) + "") : (lat + "");
        P75x = P75x.substring(0, 2) + "." + P75x.substring(3, 5) + "." + P75x.substring(5, 7) + "." + P75x.substring(7);


        String data = "P1=0011&P2=" + edit_code.getText().toString() +
                "&P3=" + edit_name.getText().toString() +
                "&P4=" + edit_count.getText().toString() +
                "&P5=" + edit_unit.getText().toString() +
                (edit_address.getText().toString().equals("") ? "" : ("&P6=" + edit_address.getText().toString())) +
                "&P7=" + P72x +
                "&P8=" + P73x +
                "&P9=" + P74x +
                "&P10=" + P75x;

        return data;

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CreatOptionActivity.APPLY && data != null)
        {
            options = (HashMap<String, Object>) data.getSerializableExtra("option");
            double lat = Double.parseDouble(options.get("lat").toString());
            double lng = Double.parseDouble(options.get("lng").toString());
//			String P72x = lng<0?"1":"0";
//			String P73x = lng<0?((lng*-1)+""):(lng+"");
//			P73x = P73x.substring(0, 3)+"."+P73x.substring(4, 6)+"."+P73x.substring(6, 8)+"."+P73x.substring(8);
//			String P74x = lat<0?"1":"0";
//			String P75x = lat<0?((lat*-1)+""):(lat+"");
//			P75x = P75x.substring(0, 2)+"."+P75x.substring(3, 5)+"."+P75x.substring(5, 7)+"."+P75x.substring(7);

            ((TextView) findViewById(R.id.edit_latlng)).setText(lat + "," + lng);
        }

    }

    @Override
    public void initView()
    {
        // TODO Auto-generated method stub
    }

    @Override
    protected void goAddOption()
    {
        // TODO Auto-generated method stub

    }

}
