package com.miles.ccit.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Json数据解析和转换类
 * @author 牛程
 * @version 1.0
 * @since 2013/04/12
 * */
public class JSONUtil
{
	/**
	 * 将map对象转成Json字符串
	 * 
	 * @param data
	 *            待转换的map对象
	 * @return Json字符串
	 */
	@SuppressWarnings("unchecked")
	public static String toJson(Map<String, Object> data)
	{
		Iterator<String> keySet = data.keySet().iterator();
		String key;
		Object value;
		JSONObject jsonObject = new JSONObject();
		try
		{
			while (keySet.hasNext())
			{
				key = keySet.next();
				value = data.get(key);
				if (value instanceof List)
				{
					@SuppressWarnings("unchecked")
					List<Map<String, Object>> list = (List<Map<String, Object>>) value;
					for (Map<String, Object> map : list)
					{
						JSONObject item = new JSONObject();
						Iterator<String> iterator = map.keySet().iterator();
						String subKey;
						Object subValue;
						while (iterator.hasNext())
						{
							subKey = iterator.next();
							subValue = map.get(subKey);
							if(subValue instanceof Map)
							{
								HashMap<String, Object> c2 = (HashMap<String, Object>) subValue;
								Iterator<String> keySetc2 = c2.keySet().iterator();
								String keyc2;
								Object valuec2;
								JSONObject jsonObjectc2 = new JSONObject();
								while(keySetc2.hasNext())
								{
									keyc2 = keySetc2.next();
									valuec2 = c2.get(keyc2);
									jsonObjectc2.put(keyc2, valuec2);
						
								}
								item.put(subKey, jsonObjectc2);
							}
							else
							{
								item.put(subKey, subValue);
							}
							
						}
						jsonObject.accumulate(key, item);
					}
				}
				else if (value instanceof String[])
				{
					// jsonObject.p
					String[] valuearray = (String[]) value;
					JSONArray array = new JSONArray();
					for (String str : valuearray)
					{
						array.put(str);
					}
					jsonObject.put(key, array);
				}
				else if(value instanceof Map)
				{
					HashMap<String, Object> v = (HashMap<String, Object>) value;
					Iterator<String> keySetc = v.keySet().iterator();
					String keyc;
					Object valuec;
					JSONObject jsonObjectc = new JSONObject();
					while(keySetc.hasNext())
					{
						keyc = keySetc.next();
						valuec = v.get(keyc);
						if(valuec instanceof Map)
						{
							HashMap<String, Object> c2 = (HashMap<String, Object>) valuec;
							Iterator<String> keySetc2 = c2.keySet().iterator();
							String keyc2;
							Object valuec2;
							JSONObject jsonObjectc2 = new JSONObject();
							while(keySetc2.hasNext())
							{
								keyc2 = keySetc2.next();
								valuec2 = c2.get(keyc2);
								jsonObjectc2.put(keyc2, valuec2);
					
							}
							jsonObjectc.put(keyc, jsonObjectc2);
						}
						else
						{
							jsonObjectc.put(keyc, valuec);
						}
			
					}
					jsonObject.put(key, jsonObjectc);
				}
				else
				{
					jsonObject.put(key, value);
				}
			}
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
		return jsonObject.toString();

	}

	/**
	 * 根据Json字符串获得一个map对象
	 * 
	 * @param jsonString
	 *            Json字符串
	 * @return Map对象
	 * */
	public static Map<String, Object> getMapFromJson(String jsonString)
	{
		if(jsonString.equals(""))
		{
			return null;
		}
		JSONObject jsonObject;
		try
		{
			String strjason =  jsonString.substring(jsonString.indexOf("{"), jsonString.lastIndexOf("}") + 1);
			jsonObject = new JSONObject(strjason);
			
			Iterator<String> keyIter = jsonObject.keys();
			String key;
			Object value;
			Map<String, Object> valueMap = new HashMap<String, Object>();
			while (keyIter.hasNext())
			{
				key = (String) keyIter.next();
				value = jsonObject.get(key);
				if(value instanceof JSONArray)
				{
					valueMap.put(key, getListFromJson(value.toString()));
				}
				else if(value instanceof JSONObject)
				{
					valueMap.put(key, getMapFromJson(value.toString()));
				}
				else
				{
					valueMap.put(key, value);
				}
			}
			return valueMap;
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 根据Json字符串获得一个map列表对象
	 * 
	 * @param jsonString
	 *            Json字符串
	 * @return Map列表
	 * */
	public static List<Map<String, Object>> getListFromJson(String jsonString)
	{
		List<Map<String, Object>> list = null;
		list = new ArrayList<Map<String, Object>>();
		try
		{
			if (jsonString.indexOf("[") != -1)
			{
				JSONArray jsonArray = new JSONArray(jsonString);
				JSONObject jsonObject;
				for (int i = 0; i < jsonArray.length(); i++)
				{
					jsonObject = jsonArray.getJSONObject(i);
					list.add(getMapFromJson(jsonObject.toString()));
				}
			}
			else
			{
				list.add(getMapFromJson(jsonString));
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}

}
