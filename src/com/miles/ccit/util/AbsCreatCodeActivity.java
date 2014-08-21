package com.miles.ccit.util;

import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.miles.ccit.duomo.R;
import com.miles.ccit.net.APICode;

public abstract class AbsCreatCodeActivity extends AbsBaseActivity
{
	public Button Btn_addOption;
	public ListView list_Content;
	
	protected abstract void goAddOption();
	
	@Override
	public void onClick(View arg0)
	{
		// TODO Auto-generated method stub
		if(arg0 == Btn_addOption)
		{
			goAddOption();
		}
	}
	@Override
	public void initView()
	{
		// TODO Auto-generated method stub
		list_Content = (ListView)findViewById(R.id.list_content);
		Btn_addOption = (Button)findViewById(R.id.bt_addoption);
	}
	
	public void sendCodedirc(String contact,String conent)
	{
		if (contact.equals(""))
		{
			Toast.makeText(mContext, "联系人不能为空...", 0).show();
			return;
		}
		else
		{
			if (contact.indexOf(",") == -1)
			{
				long ret = insertCodeDirc(contact, conent);
				SendCodedirctoNet(new long[]{ret}, new String[]{contact}, conent);
			}
			else
			{
				String[] tmparray = contact.split(",");
				long[] arrayid = new long[tmparray.length];
				for (int i = 0; i < tmparray.length; i++)
				{
					long ret = insertCodeDirc(tmparray[i], conent);
					arrayid[i] = ret;
				}
				SendCodedirctoNet(arrayid, tmparray, conent);
				
			}
		}
	}
	
	public void SendCodedirctoNet(long[] id,String[] contact,String conent)
	{
		String desCon = "";
		for(int i=0;i<id.length;i++)
		{
			desCon+=(contact[i]+","+id[i]+",");
		}
		desCon = desCon.substring(0, desCon.length()-1);
		MyLog.LogV("codedirc", conent);
		new SendDataTask().execute(APICode.SEND_CodeDirec+"",OverAllData.Account,desCon,conent);
		
	}
	

	public long insertCodeDirc(String contact,String conent)
	{
		BaseMapObject email = new BaseMapObject();
		email.put("id", null);
		email.put("number", contact);
		email.put("sendtype", "2");	//1,收，2,发，3,草稿
		email.put("codetype", "0");	//不记录军标类型
		email.put("codecontent", conent);
		email.put("priority", OverAllData.Priority);
		email.put("acknowledgemen", OverAllData.Acknowledgemen);
		email.put("creattime", UnixTime.getStrCurrentUnixTime());
		return email.InsertObj2DB(mContext, "codedirect");
	}
	
	
	
}
