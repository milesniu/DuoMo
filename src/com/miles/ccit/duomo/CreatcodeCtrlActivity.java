package com.miles.ccit.duomo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;

import com.miles.ccit.adapter.MySpinnerAdapter;
import com.miles.ccit.util.AbsCreatCodeActivity;
import com.miles.ccit.util.MyLog;

public class CreatcodeCtrlActivity extends AbsCreatCodeActivity
{

	private List<Map<String,Object>> skytype = new Vector<Map<String,Object>>();
	private List<Map<String,Object>> skytool = new Vector<Map<String,Object>>();
	private Spinner sp_Type;
	private Spinner sp_Tool;
	private EditText edit_descption;
	private String contact;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_creatcode_ctrl);
		ComposeSpinner();
		initBaseView("警报控制");
		contact = getIntent().getStringExtra("contact");
		Btn_Left.setOnClickListener(this);
		Btn_Right.setBackgroundResource(R.drawable.sendmail);
		Btn_Right.setOnClickListener(this);
		sp_Type = (Spinner)findViewById(R.id.sp_type);
		sp_Tool = (Spinner)findViewById(R.id.sp_tool);
		sp_Tool.setAdapter(new MySpinnerAdapter(mContext,skytool));
		sp_Type.setAdapter(new MySpinnerAdapter(mContext, skytype));
		edit_descption = (EditText)findViewById(R.id.edit_descrption);
	}

	private void ComposeSpinner()
	{
		Map<String, Object> m0 = new HashMap<String, Object>();
		m0.put("code", "xxxx");
		m0.put("name", "选择控制类型");
		
		Map<String, Object> m1 = new HashMap<String, Object>();
		m1.put("code", "000");
		m1.put("name", "预警");

		Map<String, Object> m2 = new HashMap<String, Object>();
		m2.put("code", "001");
		m2.put("name", "空袭");

		Map<String, Object> m3 = new HashMap<String, Object>();
		m3.put("code", "010");
		m3.put("name", "解警");

		Map<String, Object> m4 = new HashMap<String, Object>();
		m4.put("code", "011");
		m4.put("name", "消防");
		
		skytype.add(m0);
		skytype.add(m1);
		skytype.add(m2);
		skytype.add(m3);
		skytype.add(m4);
		
		Map<String, Object> t0 = new HashMap<String, Object>();
		t0.put("code", "xxx");
		t0.put("name", "请选择报警方式");

		Map<String, Object> t1 = new HashMap<String, Object>();
		t1.put("code", "000");
		t1.put("name", "文字报警");
		
		Map<String, Object> t2 = new HashMap<String, Object>();
		t2.put("code", "001");
		t2.put("name", "声音报警");
		
		skytool.add(t0);
		skytool.add(t1);
		skytool.add(t2);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.creatcode_ctrl, menu);
		return true;
	}
	
	private String composeSendData()
	{
		String P4 = edit_descption.getText().toString();
		String data = "P1=0101&P2="+skytype.get(sp_Type.getSelectedItemPosition()).get("code")+
				"&P3="+skytool.get(sp_Tool.getSelectedItemPosition()).get("code")+
				(P4.equals("")?"":("&P4="+P4));
			
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
		case R.id.bt_right:
			if(sp_Type.getSelectedItemPosition()==0)
			{
				MyLog.showToast(mContext, "请选择控制类型");
				return;
			}else if(sp_Tool.getSelectedItemPosition()==0)
			{
				MyLog.showToast(mContext, "请选择报警方式");
				return;
			}
            if (edit_descption.getText().toString().getBytes().length > 15)
            {
                MyLog.showToast(mContext, "警报控制说明不能超过15个字节");
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
