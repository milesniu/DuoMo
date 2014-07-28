package com.miles.ccit.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

//DatabaseHelper作为一个访问SQLite的助手类，提供两个方面的功能，
//第一，getReadableDatabase(),getWritableDatabase()可以获得SQLiteDatabse对象，通过该对象可以对数据库进行操作
//第二，提供了onCreate()和onUpgrade()两个回调函数，允许我们在创建和升级数据库时，进行自己的操作

public class DatabaseHelper extends SQLiteOpenHelper
{

	private static final int VERSION = 1; 					// 数据库版本/2014/06/16
	private static final String DATABASE_NAME = "DuoMo.db"; // 数据库名称

	// 在SQLiteOepnHelper的子类当中，必须有该构造函数
	public DatabaseHelper(Context context, String name, CursorFactory factory, int version)
	{
		// 必须通过super调用父类当中的构造函数
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}

	public DatabaseHelper(Context context)
	{
		this(context, DATABASE_NAME, VERSION);
	}

	public DatabaseHelper(Context context, String name, int version)
	{
		this(context, DATABASE_NAME, null, version);
	}

	// 该函数是在第一次创建数据库的时候执行,实际上是在第一次得到SQLiteDatabse对象的时候，才会调用这个方法
	@Override
	public void onCreate(SQLiteDatabase db)
	{
		// TODO Auto-generated method stub
		CtreteTables(db);
		initData(db);
		Log.v("Jason", "Create Database");
//		CtreteTables(db);
//		initData(db);
	}

	private void CtreteTables(SQLiteDatabase db)
	{
		// execSQL函数用于执行SQL语句,创建表
		db.execSQL("CREATE TABLE shortmsg(id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,number TEXT NOT NULL,sendtype INTEGER NOT NULL,status INTEGER NOT NULL,msgtype INTEGER NOT NULL,msgcontent TEXT NOT NULL,creattime TEXT NOT NULL,priority INTEGER NOT NULL,acknowledgemen INTEGER NOT NULL,exp1 TEXT,exp2 TEXT)");
		db.execSQL("CREATE TABLE emailmsg(id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,sendtype INTEGER NOT NULL,number TEXT NOT NULL,csnumber TEXT NOT NULL,subject TEXT NOT NULL,mailcontent TEXT NOT NULL,haveattachments INTEGER NOT NULL,attachmentsname TEXT,attachmentspath TEXT,creattime TEXT NOT NULL,priority INTEGER NOT NULL,acknowledgemen INTEGER NOT NULL,exp1 TEXT,exp2 TEXT)");
		db.execSQL("CREATE TABLE voicecoderecord(id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,number TEXT NOT NULL,status INTEGER NOT NULL,creattime TEXT NOT NULL,priority INTEGER NOT NULL,acknowledgemen INTEGER NOT NULL,exp1 TEXT,exp2 TEXT)");
		db.execSQL("CREATE TABLE codedirect(id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,sendtype INTEGER NOT NULL,number TEXT NOT NULL,codetype INTEGER NOT NULL,codecontent TEXT NOT NULL,creattime TEXT NOT NULL,priority INTEGER NOT NULL,acknowledgemen INTEGER NOT NULL,exp1 TEXT,exp2 TEXT)");
		db.execSQL("CREATE TABLE contact(id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,name TEXT NOT NULL,number TEXT UNIQUE NOT NULL,type INTEGER NOT NULL,remarks TEXT NOT NULL,creattime TEXT NOT NULL,exp1 TEXT,exp2 TEXT)");
		db.execSQL("CREATE TABLE wiredrecord(id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,number TEXT NOT NULL,sendtype INTEGER NOT NULL,status INTEGER NOT NULL,filepath TEXT,creattime TEXT NOT NULL,exp1 TEXT,exp2 TEXT)");
		db.execSQL("CREATE TABLE broadcastrecode(id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,frequency TEXT NOT NULL,filepath TEXT NOT NULL,creattime TEXT NOT NULL,exp1 TEXT,exp2 TEXT)");
		db.execSQL("CREATE TABLE specialway(id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,frequency TEXT NOT NULL,creattime TEXT NOT NULL,exp1 TEXT,exp2 TEXT)");
		db.execSQL("CREATE TABLE systeminto(key TEXT NOT NULL,value TEXT NOT NULL)");
		
	}

	// 初始化密码表中数据，
	private void initData(SQLiteDatabase db)
	{
//		String Id = null;
//		ContentValues values = new ContentValues();
//		values.put("floorid", Id);
//		values.put("myfloorname", "0");
//		values.put("floorname", "一楼");
//		db.insert("floors", null, values);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		// TODO Auto-generated method stub
		if(newVersion > oldVersion)
		{
			//版本升级后的操作
		}
	}
}
