package com.miles.ccit.duomo;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.miles.ccit.util.AbsCreatCodeActivity;
import com.miles.ccit.util.MyLog;
import com.miles.ccit.util.PickTimeDlg;

import java.util.HashMap;

public class CreatBDdataActivity extends AbsCreatCodeActivity
{

	private EditText edit_card;
	private EditText edit_time;
	private HashMap<String, Object> options = new HashMap<String, Object>();
	private String contact;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_creat_bddata);
		contact = getIntent().getStringExtra("contact");
		initBaseView("北斗报文");
		Btn_Left.setOnClickListener(this);
		Btn_Right.setBackgroundResource(R.drawable.sendmail);
		Btn_Right.setOnClickListener(this);
		
		edit_card=(EditText)findViewById(R.id.edit_bdcard);
		edit_time=(EditText)findViewById(R.id.edit_time);
		edit_time.setInputType(InputType.TYPE_NULL);
		edit_time.setOnClickListener(this);
		findViewById(R.id.bt_addoption).setOnClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.creat_bddata, menu);
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
			if(edit_card.getText().toString().equals(""))
			{
				MyLog.showToast(mContext, "请输入北斗入网卡号");
				return;
			}
//			else if(edit_time.getText().toString().equals(""))
//			{
//				MyLog.showToast(mContext, "选择开始时间");
//				return;
//			}
            if (edit_card.getText().toString().getBytes().length > 15)
            {
                MyLog.showToast(mContext, "北斗入网卡号不能超过15个字节");
                return;
            }
			try
            {
                Double.parseDouble(options.get("lat").toString());
                Double.parseDouble(options.get("lng").toString());
            }
            catch (Exception e)
            {
                MyLog.showToast(mContext, "请选择位置坐标");
                return;
            }
			sendCodedirc(contact, composeSendData());
			this.finish();
			break;
		case R.id.edit_time:
			new PickTimeDlg(mContext, edit_time);
			break;
		case R.id.bt_addoption:
			startActivityForResult(new Intent(mContext, CreatOptionActivity.class).putExtra("typecode", CreatOptionActivity.BDDATA), CreatOptionActivity.BDDATA);
			break;
		}
	}

	private String composeSendData()
	{
		double lat = Double.parseDouble(options.get("lat").toString());
		double lng = Double.parseDouble(options.get("lng").toString());
		String P72x = lng<0?"1":"0";
		String P73x = lng<0?((lng*-1)+""):(lng+"");
		P73x = P73x.substring(0, 3)+"."+P73x.substring(4, 6)+"."+P73x.substring(6, 8)+"."+P73x.substring(8);
		String P74x = lat<0?"1":"0";
		String P75x = lat<0?((lat*-1)+""):(lat+"");
		P75x = P75x.substring(0, 2)+"."+P75x.substring(3, 5)+"."+P75x.substring(5, 7)+"."+P75x.substring(7);
		
		
		
		String data = "P1=0110&P2="+edit_card.getText().toString()+
				"&P3="+edit_time.getText().toString()+
				"&P4="+P72x+
				"&P5="+P73x+
				"&P6="+P74x+
				"&P7="+P75x;
		
		return data;
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == CreatOptionActivity.BDDATA&&data!=null)
		{
			options=(HashMap<String, Object>)data.getSerializableExtra("option");
			double lat = Double.parseDouble(options.get("lat").toString());
			double lng = Double.parseDouble(options.get("lng").toString());
			
			((TextView)findViewById(R.id.edit_latlng)).setText(lat+","+lng);
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
