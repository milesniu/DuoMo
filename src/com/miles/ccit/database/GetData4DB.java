package com.miles.ccit.database;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import com.miles.ccit.util.BaseMapObject;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class GetData4DB {

    public static List<BaseMapObject> getObjectListData(Context context, String table) {
        return UserDatabase.queryByCondition(context, table, null, null, null);
    }

    public static HashMap<String, BaseMapObject> getObjectHashData(Context context, String table, String key) {
        return UserDatabase.queryByConditionforMap(context, table, null, null, null, key);
    }

    public static List<BaseMapObject> getObjectListData(Context context, String table, String wherename, String value) {
        return UserDatabase.queryByCondition(context, table, wherename + "=?", new String[]{value}, null);
    }

    public static BaseMapObject getObjectByid(Context context, String table, String id) {
        List<BaseMapObject> list = UserDatabase.queryByCondition(context, table, "id=?", new String[]{id}, null);
        if (list != null && list.size() > 0) {
            return list.get(0);
        } else {
            return null;
        }
    }

    public static BaseMapObject getObjectByRowName(Context context, String table, String wherename, String value) {
        List<BaseMapObject> list = UserDatabase.queryByCondition(context, table, wherename + "=?", new String[]{value}, null);
        if (list != null && list.size() > 0) {
            return list.get(0);
        } else {
            return null;
        }
    }

    public static void cleanTable(Context contex, String table) {
        String sql = "DELETE FROM " + table + ";";
        SQLiteDatabase db = UserDatabase.OpenOrCreatDataBase(contex);
        db.execSQL(sql);
        db.close();
    }

    public static List<BaseMapObject> getObjList4LeftJoin(Context contex, String tableleft, String tableright, String wherename) {
        String strSql = "SELECT " + tableleft + ".*," + tableright + ".name" + " FROM " + tableleft + " LEFT JOIN " + tableright + " ON " + tableleft + "." + wherename + "=" + tableright + "." + wherename;
        SQLiteDatabase db = UserDatabase.OpenOrCreatDataBase(contex);
        Cursor cursor = db.rawQuery(strSql, new String[]{});
        List<BaseMapObject> data = new Vector<BaseMapObject>();
        while (cursor.moveToNext()) {
            BaseMapObject rowData = new BaseMapObject();
            String[] columnNames = cursor.getColumnNames();
            for (int i = 0; i < columnNames.length; i++) {
                rowData.put(columnNames[i], cursor.getString(i));
            }
            data.add(rowData);
        }
        cursor.close();
        db.close();
        return data;
    }

    public static List<BaseMapObject> getObjecSet(Context contex, String tableleft, String tableright, String wherename, String groupby) {
        return getObjecSet(contex, tableleft, tableright, wherename, groupby, new String[]{}, new String[]{}, "");
    }


    public static List<BaseMapObject> getObjecSet(Context contex, String tableleft, String tableright, String wherename, String groupby, String[] where, String[] name, String fuhao) {
        String strSql = "SELECT " + tableleft + ".*," + tableright + ".name" + " FROM " + tableleft + " LEFT JOIN " + tableright + " ON " + tableleft + "." + wherename + "=" + tableright + "." + wherename + " WHERE " + tableleft + ".id IN (SELECT MAX(" + tableleft + ".id) FROM " + tableleft + " GROUP BY " + tableleft + "." + groupby + ")";

        for (int i = 0; i < where.length; i++) {
            strSql += (" and " + tableleft + "." + where[i] + fuhao + name[i]);
        }

        SQLiteDatabase db = UserDatabase.OpenOrCreatDataBase(contex);
        Cursor cursor = db.rawQuery(strSql, new String[]{});
        List<BaseMapObject> data = new Vector<BaseMapObject>();
        while (cursor.moveToNext()) {
            BaseMapObject rowData = new BaseMapObject();
            String[] columnNames = cursor.getColumnNames();
            for (int i = 0; i < columnNames.length; i++) {
                rowData.put(columnNames[i], cursor.getString(i));
            }
            data.add(rowData);
        }
        cursor.close();
        db.close();
        return data;
    }

}
