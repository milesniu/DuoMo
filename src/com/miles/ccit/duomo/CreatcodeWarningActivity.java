package com.miles.ccit.duomo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import com.miles.ccit.adapter.MySpinnerAdapter;
import com.miles.ccit.util.AbsCreatCodeActivity;
import com.miles.ccit.util.MyLog;

public class CreatcodeWarningActivity extends AbsCreatCodeActivity
{

	private List<Map<String,Object>> skytype = new Vector<Map<String,Object>>();
	private List<Map<String,Object>> skyrect = new Vector<Map<String,Object>>();
	private Spinner sp_Type;
	private Spinner sp_Rect;
	private EditText edit_descption;
	private List<HashMap<String, Object>> options = new Vector<HashMap<String, Object>>();
	private String contact;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_creatcode_warning);
		contact = getIntent().getStringExtra("contact");
		ComposeSpinner();
		initBaseView("威胁报警");
		Btn_Left.setOnClickListener(this);
		Btn_Right.setBackgroundResource(R.drawable.sendmail);
		Btn_Right.setOnClickListener(this);
		sp_Type = (Spinner)findViewById(R.id.sp_type);
		sp_Rect = (Spinner)findViewById(R.id.sp_rect);
		list_Content = (ListView)findViewById(R.id.list_content);
		edit_descption = (EditText)findViewById(R.id.edit_descption);
		sp_Type.setAdapter(new MySpinnerAdapter(mContext, skytype));
		sp_Rect .setAdapter(new MySpinnerAdapter(mContext, skyrect));
		findViewById(R.id.bt_addoption).setOnClickListener(this);
	}
	
	private void ComposeSpinner()
	{
		Map<String, Object> m0 = new HashMap<String, Object>();
		m0.put("code", "xxxx");
		m0.put("name", "选择威胁报警类型");
		
		Map<String, Object> m1 = new HashMap<String, Object>();
		m1.put("code", "0000");
		m1.put("name", "规避区");

		Map<String, Object> m2 = new HashMap<String, Object>();
		m2.put("code", "0001");
		m2.put("name", "化学污染区");

		Map<String, Object> m3 = new HashMap<String, Object>();
		m3.put("code", "0010");
		m3.put("name", "核污染区");

		
		skytype.add(m0);
		skytype.add(m1);
		skytype.add(m2);
		skytype.add(m3);
		
		Map<String, Object> t0 = new HashMap<String, Object>();
		t0.put("code", "xxx");
		t0.put("name", "请选择威胁区形状");

		Map<String, Object> t1 = new HashMap<String, Object>();
		t1.put("code", "000");
		t1.put("name", "图形");
		
		Map<String, Object> t2 = new HashMap<String, Object>();
		t2.put("code", "001");
		t2.put("name", "椭圆");
		
		Map<String, Object> t3 = new HashMap<String, Object>();
		t3.put("code", "010");
		t3.put("name", "正方形");
		
		Map<String, Object> t4 = new HashMap<String, Object>();
		t4.put("code", "011");
		t4.put("name", "长方形");
		
		Map<String, Object> t5 = new HashMap<String, Object>();
		t5.put("code", "100");
		t5.put("name", "多边形");
		
		skyrect.add(t0);
		skyrect.add(t1);
		skyrect.add(t2);
		skyrect.add(t3);
		skyrect.add(t4);
		skyrect.add(t5);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.creatcode_warning, menu);
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
			if(sp_Type.getSelectedItemPosition()==0)
			{
				MyLog.showToast(mContext, "请选择威胁报警类型");
				return;
			}
			else if(sp_Rect.getSelectedItemPosition()==0)
			{
				MyLog.showToast(mContext, "请选择威胁区形状");
				return;
			}
			sendCodedirc(contact, composeSendData());
			this.finish();
			break;
		case R.id.bt_addoption:
			startActivityForResult(new Intent(mContext, CreatOptionActivity.class).putExtra("typecode", CreatOptionActivity.WARINNING), CreatOptionActivity.WARINNING);
			break;
		}
	}

		private String composeSendData()
		{
			String data = "P1=0100&P2="+skytype.get(sp_Type.getSelectedItemPosition()).get("code").toString()+
					"&P3="+skyrect.get(sp_Rect.getSelectedItemPosition()).get("code").toString()+
					(edit_descption.getText().toString().equals("")?"":("&P4="+edit_descption.getText().toString()))+
					(options.size()>0?("&P5="+options.size()):"");
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
				option+=("&P52"+(i+1)+"="+P72x+
						"&P53"+(i+1)+"="+P73x+
						"&P54"+(i+1)+"="+P74x+
						"&P55"+(i+1)+"="+P75x);
				
			}
			return (data+option);
		}
		
		@SuppressWarnings("unchecked")
		@Override
		protected void onActivityResult(int requestCode, int resultCode, Intent data)
		{
			// TODO Auto-generated method stub
			super.onActivityResult(requestCode, resultCode, data);
			if(requestCode == CreatOptionActivity.WARINNING&&data!=null)
			{
				options.add((HashMap<String, Object>)data.getSerializableExtra("option"));
			}
			refreshList();
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
