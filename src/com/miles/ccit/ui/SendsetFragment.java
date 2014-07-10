package com.miles.ccit.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import com.miles.ccit.database.GetData4DB;
import com.miles.ccit.duomo.R;
import com.miles.ccit.util.AbsBaseFragment;
import com.miles.ccit.util.BaseMapObject;
import com.miles.ccit.util.HexSwapString;
import com.miles.ccit.util.MyLog;

public class SendsetFragment extends AbsBaseFragment
{

	private byte[] byteconfig = new byte[]{(byte)0x00,(byte)0x00};
	
	private RadioButton radio_need;
	private RadioButton radio_noneed;
	private RadioButton radio_high;
	private RadioButton radio_medium;
	private RadioButton radio_low;
	private BaseMapObject read4DB;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_sendset, null);
		
		initView(view);
		return view;
	}


	@Override
	public void initView(View view)
	{
		// TODO Auto-generated method stub
		initBaseView(view, "发送设置");
		Btn_Right.setBackgroundResource(R.drawable.btsure);
		Btn_Right.setOnClickListener(this);
		radio_need = (RadioButton)view.findViewById(R.id.radio_needack);
		radio_noneed = (RadioButton)view.findViewById(R.id.radionotneedack);
		radio_high = (RadioButton)view.findViewById(R.id.radio_hight);
		radio_medium = (RadioButton)view.findViewById(R.id.radio_middle);
		radio_low = (RadioButton)view.findViewById(R.id.radio_low);
		
		read4DB = GetData4DB.getObjectByRowName(getActivity(), "systeminto", "key", "sendcfg");
		if(read4DB!=null)
		{
			byteconfig = HexSwapString.HexString2Bytes(read4DB.get("value").toString());
			switch(byteconfig[0])
			{
			case 0x00:
				radio_high.setChecked(true);
				break;
			case 0x01:
				radio_medium.setChecked(true);
				break;
			case 0x02:
				radio_low.setChecked(true);
				break;
			}
			switch(byteconfig[1])
			{
			case 0x00:
				radio_noneed.setChecked(true);
				break;
			case 0x01:
				radio_need.setChecked(true);
				break;
			}
		}
		
		
	}


	@Override
	public void onClick(View v)
	{
		// TODO Auto-generated method stub
		switch (v.getId())
		{
		case R.id.bt_left:
			getActivity().finish();			
			break;
		
		case R.id.bt_right:
			if(radio_high.isChecked())
			{
				byteconfig[0] = (byte)0x00;
			}else if(radio_medium.isChecked())
			{
				byteconfig[0] = (byte)0x01;
			}
			else if(radio_low.isChecked())
			{
				byteconfig[0] = (byte)0x02;
			}
			
			if(radio_need.isChecked())
			{
				byteconfig[1] = (byte)0x01;
			}else if(radio_noneed.isChecked())
			{
				byteconfig[1] = (byte)0x00;
			}
			if(read4DB!=null)
			{
				read4DB.put("key", "sendcfg");
				read4DB.put("value", HexSwapString.encode(byteconfig));
				read4DB.UpdateMyself(getActivity(), "systeminto","key");
			}
			else
			{
				BaseMapObject config = new BaseMapObject();
				config.put("key", "sendcfg");
				config.put("value", HexSwapString.encode(byteconfig));
				config.InsertObj2DB(getActivity(), "systeminto");
				
			}
			MyLog.showToast(getActivity(), "保存成功");
//			startActivity(new Intent(getActivity(), CreatCodedirecActivity.class));
			break;
		default:
			break;
		}
	}

	
}
