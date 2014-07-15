package com.miles.ccit.util;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.miles.ccit.database.UserDatabase;

public class BaseMapObject extends HashMap<String, Object> implements Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6724757568964774261L;

	// 向表中添加一个对象
	@SuppressWarnings("rawtypes")
	public long InsertObj2DB(Context contex, String tables)
	{

		ContentValues values = new ContentValues();

		Set<String> key = this.keySet();
		for (Iterator it = key.iterator(); it.hasNext();)
		{
			String s = (String) it.next();
			values.put(s, (String) this.get(s));
		}

		synchronized (UserDatabase.DataBaseLock)
		{
			SQLiteDatabase db = UserDatabase.OpenOrCreatDataBase(contex);
			long ret = db.insert(tables, null, values);
			db.close();
			return ret;
		}
	}

	public long UpdateMyself(Context contex, String tables)
	{

		ContentValues values = new ContentValues();

		Set<String> key = this.keySet();
		for (Iterator it = key.iterator(); it.hasNext();)
		{
			String s = (String) it.next();
			values.put(s, (String) this.get(s));
		}

		synchronized (UserDatabase.DataBaseLock)
		{
			SQLiteDatabase db = UserDatabase.OpenOrCreatDataBase(contex);
			int ret = -1;
			try
			{
				ret =db.update(tables, values, "id=?", new String[]{ values.get("id").toString() });
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			
			db.close();
			return ret;
		}
	}
	
	public long UpdateMyself(Context contex, String tables,String row)
	{

		ContentValues values = new ContentValues();

		Set<String> key = this.keySet();
		for (@SuppressWarnings("rawtypes")
		Iterator it = key.iterator(); it.hasNext();)
		{
			String s = (String) it.next();
			values.put(s, (String) this.get(s));
		}

		synchronized (UserDatabase.DataBaseLock)
		{
			SQLiteDatabase db = UserDatabase.OpenOrCreatDataBase(contex);
			int ret = db.update(tables, values, row+"=?", new String[]
			{ values.get(row).toString() });
			db.close();
			return ret;
		}
	}

	public static BaseMapObject HashtoMyself(HashMap<String, Object> map)
	{
		BaseMapObject tmp = new BaseMapObject();
		Iterator<Entry<String, Object>> iter = map.entrySet().iterator();
		while (iter.hasNext())
		{
			@SuppressWarnings("rawtypes")
			Map.Entry entry = (Map.Entry) iter.next();
			tmp.put(entry.getKey().toString(), entry.getValue());
		}

		return tmp;
	}

	public static long DelObj4DB(Context contex, String tables, String wherename, String value)
	{
		synchronized (UserDatabase.DataBaseLock)
		{
			SQLiteDatabase db = UserDatabase.OpenOrCreatDataBase(contex);
			int ret = db.delete(tables, wherename + "=" + '"' + value + '"', null); // 删除设备节点
			db.close();
			return ret;
		}
	}

	// 更改对象属性
	public long UpdateObj2DBbyId(Context contex, String tables)
	{

		ContentValues upvalues = new ContentValues();

		Set<String> key = this.keySet();
		for (Iterator it = key.iterator(); it.hasNext();)
		{
			String s = (String) it.next();
			upvalues.put(s, (String) this.get(s));
		}

		synchronized (UserDatabase.DataBaseLock)
		{
			SQLiteDatabase db = UserDatabase.OpenOrCreatDataBase(contex);
			int ret = db.update(tables, upvalues, "id=?", new String[]
			{ this.get("id").toString() });
			db.close();
			return ret;
		}
	}

}
