package com.miles.ccit.duomo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import com.miles.ccit.adapter.MySpinnerAdapter;
import com.miles.ccit.util.AbsCreatCodeActivity;
import com.miles.ccit.util.JSONUtil;
import com.miles.ccit.util.MyLog;
import com.miles.ccit.util.PickTimeDlg;

public class CreatcodeOtherActivity extends AbsCreatCodeActivity
{
	
	private List<Map<String,Object>> skycolor;
	private Spinner sp_Color;
	private EditText edit_code;
	private EditText edit_name;
	private List<HashMap<String, Object>> options = new Vector<HashMap<String, Object>>();
	private String contact;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_creatcode_other);
		
		Map<String,Object> data = JSONUtil.getMapFromJson(getassetsCode(mContext,"junbiaocode.txt"));
		skycolor = (List<Map<String,Object>>)data.get("jbcolor");
		contact = getIntent().getStringExtra("contact");
		initBaseView("其他目标");
		Btn_Left.setOnClickListener(this);
		Btn_Right.setBackgroundResource(R.drawable.sendmail);
		Btn_Right.setOnClickListener(this);
		sp_Color = (Spinner)findViewById(R.id.sp_jbcolor);
		list_Content = (ListView)findViewById(R.id.list_content);
		edit_code = (EditText)findViewById(R.id.edit_jbnum);
		edit_name = (EditText)findViewById(R.id.edit_name);
		sp_Color .setAdapter(new MySpinnerAdapter(mContext, skycolor));
		findViewById(R.id.bt_addoption).setOnClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.creatcode_other, menu);
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
			if(sp_Color.getSelectedItemPosition()==0)
			{
				MyLog.showToast(mContext, "请选择军标颜色");
				return;
			}
			
			sendCodedirc(contact, composeSendData());
			this.finish();
			break;
		case R.id.bt_addoption:
			startActivityForResult(new Intent(mContext, CreatOptionActivity.class).putExtra("typecode", CreatOptionActivity.OTHERCODE), CreatOptionActivity.OTHERCODE);
			break;
		}
	}


	private String composeSendData()
	{
		String data = "P1=0000&P2=011&P3="+edit_code.getText().toString()+(edit_name.getText().toString().equals("")?"":("&P4="+edit_name.getText().toString()))+"&P5="+skycolor.get(sp_Color.getSelectedItemPosition()).get("code").toString()+(options.size()>0?("&P6="+options.size()):"");
		String option = "";
		for(int i=0;i<options.size();i++)
		{
			HashMap<String, Object> item = options.get(i);
			double lat = Double.parseDouble(item.get("lat").toString());
			double lng = Double.parseDouble(item.get("lng").toString());
			String P72x = lng<0?"1":"0";
			String P73x = lng<0?((lng*-1)+""):(lng+"");
			P73x = P73x.substring(0, 3)+"."+P73x.substring(4, 6)+"."+P73x.substring(6, 8)+"."+P73x.substring(8);
			String P74x = lat<0?"1":"0";
			String P75x = lat<0?((lat*-1)+""):(lat+"");
			P75x = P75x.substring(0, 2)+"."+P75x.substring(3, 5)+"."+P75x.substring(5, 7)+"."+P75x.substring(7);
			option+=("&P62"+(i+1)+"="+P72x+
					"&P63"+(i+1)+"="+P73x+
					"&P64"+(i+1)+"="+P74x+
					"&P65"+(i+1)+"="+P75x);
			
		}
		return (data+option);
	}
	
	private void refreshList()
	{
		String[] array = new String[options.size()];
		for(int i=0;i<options.size();i++)
		{
			double lat = Double.parseDouble(options.get(i).get("lat")+"");
			double lng = Double.parseDouble(options.get(i).get("lng")+"");
			
			array[i] =lat+", "+lng;
		}
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext, R.layout.listitem_option, R.id.text_name, array);
		list_Content.setAdapter(adapter);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == CreatOptionActivity.OTHERCODE&&data!=null)
		{
			options.add((HashMap<String, Object>)data.getSerializableExtra("option"));
		}
		refreshList();
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
