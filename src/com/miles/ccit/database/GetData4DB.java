package com.miles.ccit.database;

import java.util.List;
import java.util.Map;

import android.content.Context;

public class GetData4DB
{

	public static List<Map<String, String>> getObjectListData(Context context,String table)
	{
		return  UserDatabase.queryByCondition(context, table,null,null, null);
	}
	
	public static Map<String,String> getObjectByid(Context context,String table,String id)
	{
		List<Map<String, String>> list = UserDatabase.queryByCondition(context, table, "id=?", new String[]{id},null);
		if(list!=null && list.size()>0)
		{
			return list.get(0);
		}
		else
		{
			return null;
		}
	}
	
}
