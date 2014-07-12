package com.miles.ccit.util;

import com.miles.ccit.net.APICode;

import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public abstract class AbsEmailCodeActivity extends AbsBaseActivity
{

	public Button Btn_Send;
	public EditText edit_inputContact;
	public Button Btn_addContact;



	@Override
	protected void onDestroy()
	{
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	


	@Override
	public void initView()
	{
		// TODO Auto-generated method stub
		
	}
	
	public static String getFileName(String path)
	{
		String[] filearr = path.split("/");
		return filearr[filearr.length - 1];
	}
	
	/**
	 * 获取文件后缀名
	 * 
	 * @param fileName
	 * @return 文件后缀名
	 */
	public static String getFileType(String fileName)
	{
		if (fileName != null)
		{
			int typeIndex = fileName.lastIndexOf(".");
			if (typeIndex != -1)
			{
				String fileType = fileName.substring(typeIndex + 1).toLowerCase();
				return fileType;
			}
		}
		return "";
	}

	/**
	 * 根据后缀名判断是否是图片文件
	 * 
	 * @param type
	 * @return 是否是图片结果true or false
	 */
	public static boolean isImage(String type)
	{
		if (type != null && (type.equals("jpg") || type.equals("gif") || type.equals("png") || type.equals("jpeg") || type.equals("bmp") || type.equals("wbmp") || type.equals("ico") || type.equals("jpe")))
		{
			return true;
		}
		return false;
	}


	public void sendEmail(String contact,String cscontact,String subject,String conent,String fjpath)
	{
		if (contact.equals(""))
		{
			Toast.makeText(mContext, "联系人不能为空...", 0).show();
			return;
		}
		else if(subject.equals(""))
		{
			Toast.makeText(mContext, "主题不能为空...", 0).show();
			return;
		}
		else if(conent.equals(""))
		{
			Toast.makeText(mContext, "邮件内容不能为空...", 0).show();
			return;
		}
		else
		{
			if (contact.indexOf(",") == -1)
			{
				long ret = insertEmail(contact, cscontact,subject, conent, fjpath);
//				MsgRecorderutil.insertTextmsg(mContext, contact, edit_inputMsg
//						.getText().toString());
				SendEmailtoNet(new long[]{ret}, new String[]{contact}, cscontact, subject, conent, fjpath);
			}
			else
			{
				String[] tmparray = contact.split(",");
				long[] arrayid = new long[tmparray.length];
				for (int i = 0; i < tmparray.length; i++)
				{
					long ret = insertEmail(tmparray[i], cscontact,subject, conent, fjpath);
					arrayid[i] = ret;
					
//					MsgRecorderutil.insertTextmsg(mContext, tmparray[i],
//							edit_inputMsg.getText().toString());
				}
				SendEmailtoNet(arrayid, tmparray, cscontact, subject, conent, fjpath);
				
			}
		}
	}
	
	public void SendEmailtoNet(long[] id,String[] contact,String cscontact,String subject,String conent,String fjpath)
	{
		String desCon = "";
		for(int i=0;i<id.length;i++)
		{
			desCon+=(contact[i]+","+id[i]+",");
		}
		desCon = desCon.substring(0, desCon.length()-1);
		new SendDataTask().execute(APICode.SEND_Email+"",OverAllData.Account,desCon,cscontact,subject,conent,fjpath);
		
	}
	
//	public static void sendEmailtoNet(long[] id,String[] contact,String msgcontent)
//	{
//		String desCon = "";
//		for(int i=0;i<id.length;i++)
//		{
//			desCon+=(contact[i]+","+id[i]+",");
//		}
//		desCon = desCon.substring(0, desCon.length()-1);
//		new SendDataTask().execute(APICode.SEND_Email+"",OverAllData.Account,desCon,msgcontent);
//		
//	}
	
	public long insertEmail(String contact,String cscontact,String subject,String conent,String fjpath)
	{
		BaseMapObject email = new BaseMapObject();
		email.put("id", null);
		email.put("number", contact);
		email.put("csnumber", cscontact);
		email.put("sendtype", "2");	//发送
		email.put("subject", subject);	
		email.put("mailcontent", conent);	//语音
		email.put("haveattachments", fjpath==null?"0":"1");
		email.put("attachmentsname", fjpath==null?" ":getFileName(fjpath));
		email.put("attachmentspath", fjpath==null?" ":fjpath);
		email.put("creattime", UnixTime.getStrCurrentUnixTime());
		email.put("priority", OverAllData.Priority);
		email.put("acknowledgemen", OverAllData.Acknowledgemen);
		return email.InsertObj2DB(mContext, "emailmsg");
	}


}
