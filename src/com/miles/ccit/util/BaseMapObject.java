package com.miles.ccit.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.miles.ccit.database.UserDatabase;

public class BaseMapObject extends HashMap<String, Object>
{
	private static final long serialVersionUID = 1L;
	
	// 向表中添加一个对象
	@SuppressWarnings("rawtypes")
	public long InsertObj2DB(Context contex, String tables)
	{
		
		ContentValues values = new ContentValues();
//		values.put(key, value)
		
		Set<String> key = this.keySet();
        for (Iterator it = key.iterator(); it.hasNext();) 
        {
            String s = (String) it.next();
       		values.put(s, (String) this.get(s));
        }
		
		synchronized (UserDatabase.DataBaseLock)
		{
			SQLiteDatabase db = UserDatabase.OpenOrCreatDataBase(contex);
			long ret =  db.insert(tables, null, values);
			db.close();
			return ret;
		}
	}
	
	public static long DelObj4DB(Context contex, String tables, String id, String wherename)
	{
		synchronized (UserDatabase.DataBaseLock)
		{
			SQLiteDatabase db = UserDatabase.OpenOrCreatDataBase(contex);
			int ret = db.delete(tables, wherename + "=" + '"' + id + '"', null); // 删除设备节点
			db.close();
			return ret;
		}
	}
	
		// 更改对象属性
		public static long UpdateObj2DB(Context contex,  String tables, ContentValues values,String wherename,String[] whereid)
		{
			synchronized (UserDatabase.DataBaseLock)
			{
				SQLiteDatabase db = UserDatabase.OpenOrCreatDataBase(contex);
				int ret = db.update(tables, values, wherename+"=?", whereid);
				db.close();
				return ret;
			}
		}

}
