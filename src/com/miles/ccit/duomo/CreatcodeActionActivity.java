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
import com.miles.ccit.util.MyLog;
import com.miles.ccit.util.PickTimeDlg;

public class CreatcodeActionActivity extends AbsCreatCodeActivity
{

	private List<Map<String, Object>> typecode = new Vector<Map<String,Object>>();
	private Spinner sp_Type;
	private EditText edit_actionname;
	private EditText edit_actionnum;
	private EditText edit_targename;
	private EditText edit_targeintro;
	private EditText edit_starttime;
	private EditText edit_steptime;
	

	private List<HashMap<String, Object>> options = new Vector<HashMap<String, Object>>();
	private List<HashMap<String, Object>> options2 = new Vector<HashMap<String, Object>>();
	private String contact;
	private ListView list_Content2;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_creatcode_action);
		ComposeTypecode();

		contact = getIntent().getStringExtra("contact");
		initBaseView("行动命令");
		Btn_Left.setOnClickListener(this);
		Btn_Right.setBackgroundResource(R.drawable.sendmail);
		Btn_Right.setOnClickListener(this);
		sp_Type = (Spinner) findViewById(R.id.sp_actiontype);
		list_Content = (ListView) findViewById(R.id.listView1);
		list_Content2 = (ListView)findViewById(R.id.listView2);
		edit_actionname = (EditText) findViewById(R.id.edit_name);
		edit_actionnum = (EditText) findViewById(R.id.edit_actionnum);
		edit_targename = (EditText) findViewById(R.id.edit_targename);
		edit_targeintro = (EditText) findViewById(R.id.edit_intro);
		edit_starttime = (EditText)findViewById(R.id.edit_time);
		edit_starttime.setInputType(InputType.TYPE_NULL);
		edit_starttime.setOnClickListener(this);
		edit_steptime = (EditText)findViewById(R.id.edit_step);
		sp_Type.setAdapter(new MySpinnerAdapter(mContext, typecode));
		findViewById(R.id.bt_addoption1).setOnClickListener(this);
		findViewById(R.id.bt_addoption2).setOnClickListener(this);
	}

	private void ComposeTypecode()
	{
		Map<String, Object> m0 = new HashMap<String, Object>();
		m0.put("code", "xxxx");
		m0.put("name", "选择行动类型");
		
		Map<String, Object> m1 = new HashMap<String, Object>();
		m1.put("code", "0000");
		m1.put("name", "集结");

		Map<String, Object> m2 = new HashMap<String, Object>();
		m2.put("code", "0001");
		m2.put("name", "疏散");

		Map<String, Object> m3 = new HashMap<String, Object>();
		m3.put("code", "0010");
		m3.put("name", "进攻");

		Map<String, Object> m4 = new HashMap<String, Object>();
		m4.put("code", "0011");
		m4.put("name", "撤退");

		typecode.add(m0);
		typecode.add(m1);
		typecode.add(m2);
		typecode.add(m3);
		typecode.add(m4);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.creatcode_action, menu);
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
		case R.id.edit_time:
			new PickTimeDlg(mContext, edit_starttime);
			break;
		case R.id.bt_addoption1:
			startActivityForResult(new Intent(mContext, CreatOptionActivity.class).putExtra("typecode", CreatOptionActivity.ACTION1), CreatOptionActivity.ACTION1);
			break;
		case R.id.bt_addoption2:
			startActivityForResult(new Intent(mContext, CreatOptionActivity.class).putExtra("typecode", CreatOptionActivity.ACTION2), CreatOptionActivity.ACTION2);
			break;
		case R.id.bt_right:
			if(sp_Type.getSelectedItemPosition()==0)
			{
				MyLog.showToast(mContext, "请选择行动类型");
				return;
			}else if(edit_actionnum.getText().toString().equals(""))
			{
				MyLog.showToast(mContext, "请输入应急队伍编号");
				return;
			}
			else if(edit_starttime.getText().toString().equals(""))
			{
				MyLog.showToast(mContext, "请输入开始执行时间");
				return;
			}
			else if(edit_steptime.getText().toString().equals(""))
			{
				MyLog.showToast(mContext, "请输入时间间隔");
				return;
			}
			
			sendCodedirc(contact, composeSendData());
			this.finish();
			break;
		}
	}

	private String composeSendData()
	{
		String data = "P1=0001&P2="+typecode.get(sp_Type.getSelectedItemPosition()).get("code").toString()+
				(edit_actionname.getText().toString().equals("")?"":("&P3="+edit_actionname.getText().toString()))+
				"&P4="+edit_actionnum.getText().toString()+
				(edit_targename.getText().toString().equals("")?"":("&P5="+edit_targename.getText().toString()))+
				(edit_targeintro.getText().toString().equals("")?"":("&P6="+edit_targeintro.getText().toString()))+
				(options.size()>0?("&P7="+options.size()):"");
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
			option+=("&P72"+(i+1)+"="+P72x+
					"&P73"+(i+1)+"="+P73x+
					"&P74"+(i+1)+"="+P74x+
					"&P75"+(i+1)+"="+P75x);
			
		}
		
		option += ("&P8="+edit_starttime.getText().toString());
		option += ("&P9="+edit_steptime.getText().toString());
		
		option+=(options2.size()>0?("&P10="+options2.size()):"");
		for(int i=0;i<options2.size();i++)
		{
			HashMap<String, Object> item = options2.get(i);
			double lat = Double.parseDouble(item.get("lat").toString());
			double lng = Double.parseDouble(item.get("lng").toString());
			String P72x = lng<0?"1":"0";
			String P73x = lng<0?((lng*-1)+""):(lng+"");
			P73x = P73x.substring(0, 3)+"."+P73x.substring(4, 6)+"."+P73x.substring(6, 8)+"."+P73x.substring(8);
			String P74x = lat<0?"1":"0";
			String P75x = lat<0?((lat*-1)+""):(lat+"");
			P75x = P75x.substring(0, 2)+"."+P75x.substring(3, 5)+"."+P75x.substring(5, 7)+"."+P75x.substring(7);
			option+=("&P102"+(i+1)+"="+P72x+
					"&P103"+(i+1)+"="+P73x+
					"&P104"+(i+1)+"="+P74x+
					"&P105"+(i+1)+"="+P75x);
			
		}
		
		return (data+option);
	}
	
	private void refreshList(ListView list,List<HashMap<String, Object>> data)
	{
		String[] array = new String[data.size()];
		for(int i=0;i<data.size();i++)
		{
			double lat = Double.parseDouble(data.get(i).get("lat")+"");
			double lng = Double.parseDouble(data.get(i).get("lng")+"");
			
			array[i] =lat+", "+lng;
		}
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext, R.layout.listitem_option, R.id.text_name, array);
		list.setAdapter(adapter);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == CreatOptionActivity.ACTION1&&data!=null)
		{
			options.add((HashMap<String, Object>)data.getSerializableExtra("option"));
			refreshList(list_Content,options);
		}
		else if(requestCode == CreatOptionActivity.ACTION2&&data!=null)
		{
			options2.add((HashMap<String, Object>)data.getSerializableExtra("option"));
			refreshList(list_Content2,options2);
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
