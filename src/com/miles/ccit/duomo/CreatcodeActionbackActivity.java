package com.miles.ccit.duomo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;

import com.miles.ccit.adapter.MySpinnerAdapter;
import com.miles.ccit.util.AbsCreatCodeActivity;
import com.miles.ccit.util.MyLog;
import com.miles.ccit.util.PickTimeDlg;

public class CreatcodeActionbackActivity extends AbsCreatCodeActivity
{
	Spinner sp_type;
	EditText edit_name;
	EditText edit_time;
	EditText edit_descption;
	private List<Map<String, Object>> typecode = new Vector<Map<String,Object>>();
	private String contact;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_creatcode_actionback);
		contact = getIntent().getStringExtra("contact");
		ComposeTypecode();
		initBaseView("行动命令反馈");
		Btn_Left.setOnClickListener(this);
		Btn_Right.setBackgroundResource(R.drawable.sendmail);
		Btn_Right.setOnClickListener(this);
		sp_type = (Spinner) findViewById(R.id.sp_type);
		edit_name = (EditText)findViewById(R.id.edit_name);
		edit_time = (EditText)findViewById(R.id.edit_time);
		edit_time.setInputType(InputType.TYPE_NULL);
		edit_time.setOnClickListener(this);
		edit_descption=(EditText)findViewById(R.id.edit_descrption);
		sp_type.setAdapter(new MySpinnerAdapter(mContext, typecode));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.creatcode_actionback, menu);
		return true;
	}
	
	private void ComposeTypecode()
	{
		Map<String, Object> m0 = new HashMap<String, Object>();
		m0.put("code", "xxxx");
		m0.put("name", "选择反馈类型");
		
		Map<String, Object> m1 = new HashMap<String, Object>();
		m1.put("code", "0000");
		m1.put("name", "待执行");

		Map<String, Object> m2 = new HashMap<String, Object>();
		m2.put("code", "0001");
		m2.put("name", "已执行");

		Map<String, Object> m3 = new HashMap<String, Object>();
		m3.put("code", "0010");
		m3.put("name", "不能执行");

		Map<String, Object> m4 = new HashMap<String, Object>();
		m4.put("code", "0011");
		m4.put("name", "执行完毕");

		typecode.add(m0);
		typecode.add(m1);
		typecode.add(m2);
		typecode.add(m3);
		typecode.add(m4);

	}
	
	private String composeSendData()
	{
		String P4 = edit_descption.getText().toString();
		String data = "P1=0010&P2="+edit_name.getText().toString()+
				"&P3="+typecode.get(sp_type.getSelectedItemPosition()).get("code").toString()+
				(P4.equals("")?"":("&P4="+P4))+
						"&P5="+edit_time.getText().toString();
			
		return data;
		
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
		case R.id.edit_time:
			new PickTimeDlg(mContext, edit_time);
			break;
		case R.id.bt_right:
			if(sp_type.getSelectedItemPosition()==0)
			{
				MyLog.showToast(mContext, "请选择反馈类型");
				return;
			}else if(edit_name.getText().toString().equals(""))
			{
				MyLog.showToast(mContext, "请选择反馈名称");
				return;
			}
			else if(edit_time.getText().toString().equals(""))
			{
				MyLog.showToast(mContext, "请输入反馈时间");
				return;
			}
			
			sendCodedirc(contact, composeSendData());
			this.finish();
			break;
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
