package com.miles.ccit.adapter;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.widget.ArrayAdapter;

import com.miles.ccit.duomo.R;

public class MySpinnerAdapter extends ArrayAdapter<String>
{

	public MySpinnerAdapter(Context context, List<Map<String,Object>> data)
	{
		
		super(context,  R.layout.simple_spinner_item, getArray(data));
		// TODO Auto-generated constructor stub
		this.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	}
	
	public static String[] getArray(List<Map<String,Object>> data)
	{
		String[] arraystr = new String[data.size()];
		for (int i = 0; i < data.size(); i++)
		{
			arraystr[i] = data.get(i).get("name") + "";
		}
		return arraystr;
	}

}
