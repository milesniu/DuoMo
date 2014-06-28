package com.miles.ccit.util;

import java.util.List;
import java.util.Vector;

import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.miles.ccit.adapter.ContactAdapter;
import com.miles.ccit.database.GetData4DB;
import com.miles.ccit.duomo.R;
import com.miles.ccit.ui.CreatContactActivity;

public abstract class AbsInputNumActivity extends AbsBaseActivity
{
	private String strNumber ="";
	private EditText editInputFrom;
	private ListView listview;
	private ContactAdapter adapter;
	private List<BaseMapObject> all;

	public List<BaseMapObject> getContact(String code)
	{
		if(code.equals(""))
		{
			return new Vector<BaseMapObject>();
		}
		List<BaseMapObject> have = new Vector<BaseMapObject>();
		for(BaseMapObject item : all)
		{
			if(item.get("number").toString().indexOf(code)!=-1)
			{
				have.add(item);
			}
		}
		return have;
	}
	
	public void refreshList()
	{
		adapter = new ContactAdapter(mContext, getContact(strNumber),"name","name","number"); 
		listview.setAdapter(adapter);
	}
	
	public void inserNum(String num)
	{
		strNumber+=num;
		editInputFrom.setText(strNumber);
		refreshList();
	}
	
	public boolean isHaveNum(String code)
	{
		for(BaseMapObject item : all)
		{
			if(item.get("number").toString().equals(code))
			{
				return true;
			}
		}
		return false;
	}
	
	public void delNum()
	{
		if(strNumber.length()<=0)
		{
			return;
		}
		strNumber = strNumber.substring(0, strNumber.length()-1);
		editInputFrom.setText(strNumber);
		refreshList();
	}
	
	@Override
	public void onClick(View v)
	{
		// TODO Auto-generated method stub
		switch(v.getId())
		{
		case R.id.bt_left:
			this.finish();
			break;
		case R.id.button0:
			inserNum("0");
			break;
		case R.id.button1:
			inserNum("1");
			break;
		case R.id.button2:
			inserNum("2");
			break;
		case R.id.button3:
			inserNum("3");
			break;
		case R.id.button4:
			inserNum("4");
			break;
		case R.id.button5:
			inserNum("5");
			break;
		case R.id.button6:
			inserNum("6");
			break;
		case R.id.button7:
			inserNum("7");
			break;
		case R.id.button8:
			inserNum("8");
			break;
		case R.id.button9:
			inserNum("9");
			break;
		case R.id.buttonx:
			inserNum("*");
			break;
		case R.id.buttony:
			inserNum("#");
			break;
		case R.id.buttoncall:
			insertRecord();
			break;
		case R.id.buttonadd:
			inserContact();
			break;
		case R.id.buttondel:
			delNum();
			break;

		}
	}
	
	public void insertRecord()
	{
		BaseMapObject record = new BaseMapObject();
		record.put("id",null);
		record.put("number",strNumber);
		record.put("status","2");
		record.put("creattime", UnixTime.getStrCurrentUnixTime());
		record.put("priority", "1");
		record.put("acknowledgemen", "1");
		record.InsertObj2DB(mContext, "voicecoderecord");
	}
	
	public void inserContact()
	{
		if(strNumber.equals(""))
		{
			return;
		}
		else if(isHaveNum(strNumber))
		{
			MyLog.showToast(mContext, "此号码已存在，不能添加...");
		}
		else
		{
			startActivity(new Intent(mContext, CreatContactActivity.class).putExtra("number", strNumber));
		}
	}

	@Override
	public void initView()
	{
		// TODO Auto-generated method stub
		initBaseView("拨号");
		Btn_Left.setText("返回");
		Btn_Left.setOnClickListener(this);
		Btn_Right.setVisibility(View.INVISIBLE);
		editInputFrom = (EditText)findViewById(R.id.edit_form);
		listview = (ListView)findViewById(R.id.listview_content);
		findViewById(R.id.button0).setOnClickListener(this);
		findViewById(R.id.button1).setOnClickListener(this);
		findViewById(R.id.button2).setOnClickListener(this);
		findViewById(R.id.button3).setOnClickListener(this);
		findViewById(R.id.button4).setOnClickListener(this);
		findViewById(R.id.button5).setOnClickListener(this);
		findViewById(R.id.button6).setOnClickListener(this);
		findViewById(R.id.button7).setOnClickListener(this);
		findViewById(R.id.button8).setOnClickListener(this);
		findViewById(R.id.button9).setOnClickListener(this);
		findViewById(R.id.buttonx).setOnClickListener(this);
		findViewById(R.id.buttony).setOnClickListener(this);
		findViewById(R.id.buttoncall).setOnClickListener(this);
		findViewById(R.id.buttonadd).setOnClickListener(this);
		findViewById(R.id.buttondel).setOnClickListener(this);
		
		
		all = GetData4DB.getObjectListData(mContext, "contact", "type", "0");
		
	}
	
}
