package com.miles.ccit.duomo;

import java.util.List;
import java.util.Map;

import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import com.miles.ccit.adapter.MySpinnerAdapter;
import com.miles.ccit.util.AbsBaseActivity;
import com.miles.ccit.util.AbsCreatCodeActivity;
import com.miles.ccit.util.JSONUtil;
import com.miles.ccit.util.PickTimeDlg;

public class CreatcodeSkyActivity extends AbsCreatCodeActivity
{

	private List<Map<String,Object>> skycode;
	private List<Map<String,Object>> skycolor;
	private Spinner sp_Code;
	private Spinner sp_Color;
	private EditText edit_time;
	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_creatcode_sky);
		Map<String,Object> data = JSONUtil.getMapFromJson(getJunbiaoCode());
		skycode = (List<Map<String,Object>>)data.get("skycode");
		skycolor = (List<Map<String,Object>>)data.get("jbcolor");
//		addMore = getLayoutInflater().inflate(R.layout.addmore, null);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.creatcode_sky, menu);
		return true;
	}

	

	@Override
	public void onClick(View v)
	{
		// TODO Auto-generated method stub
		super.onClick(v);
		switch (v.getId())
		{
		case R.id.bt_left:
			this.finish();
			break;
		case R.id.edit_time:
			new PickTimeDlg(mContext, edit_time);
			break;
		}
	}

	@Override
	public void initView()
	{
		// TODO Auto-generated method stub
		initBaseView("空中目标");
		Btn_Left.setOnClickListener(this);
		Btn_Right.setBackgroundResource(R.drawable.sendmail);
		Btn_Right.setOnClickListener(this);
		sp_Code = (Spinner)findViewById(R.id.sp_jbnum);
		sp_Color = (Spinner)findViewById(R.id.sp_jbcolor);
		list_Content = (ListView)findViewById(R.id.list_content);
		edit_time = (EditText)findViewById(R.id.edit_time);
		edit_time.setInputType(InputType.TYPE_NULL);
		edit_time.setOnClickListener(this);
		sp_Code.setAdapter(new MySpinnerAdapter(mContext, skycode));
		sp_Color .setAdapter(new MySpinnerAdapter(mContext, skycolor));
		super.initView();
		
		
		
	}

	@Override
	protected void goAddOption()
	{
		// TODO Auto-generated method stub
		
	}

}
