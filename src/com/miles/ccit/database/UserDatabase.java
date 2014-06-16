package com.miles.ccit.database;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class UserDatabase
{
	public static Object DataBaseLock = new Object();	//同步锁

	/** 创建一个DatabaseHelper对象，然后将该对象返回 */
	public static SQLiteDatabase OpenOrCreatDataBase(Context contex)
	{
		synchronized (DataBaseLock)
		{
			// 创建一个DatabaseHelper对象
			DatabaseHelper dbHelper = new DatabaseHelper(contex);
			// 只有调用了DatabaseHelper对象的getReadableDatabase()方法，或者是getWritableDatabase()方法之后，才会创建，或打开一个数据库
			return dbHelper.getReadableDatabase();
		}
	}

	public static List<Map<String, String>> queryByCondition(Context context, String tableName, String condition, String[] selectionArgs,String order)
	{
		synchronized (DataBaseLock)
		{
			DatabaseHelper dbHelper = new DatabaseHelper(context);
			List<Map<String, String>> data = new ArrayList<Map<String, String>>();
			SQLiteDatabase db = dbHelper.getReadableDatabase();
			Cursor cursor = db.query(tableName, null, condition, selectionArgs, null, null, order);
			while (cursor.moveToNext())
			{
				Map<String, String> rowData = new HashMap<String, String>();
				String[] columnNames = cursor.getColumnNames();
				for (int i = 0; i < columnNames.length; i++)
				{
					rowData.put(columnNames[i], cursor.getString(i));
				}
				data.add(rowData);
			}
			cursor.close();
			db.close();
			return data;
		}
	}
}
