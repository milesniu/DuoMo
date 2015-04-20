package com.miles.ccit.util;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;

import com.miles.ccit.adapter.ContactAdapter;
import com.miles.ccit.duomo.CallWaitActivity;
import com.miles.ccit.duomo.CreatContactActivity;
import com.miles.ccit.duomo.R;
import com.miles.ccit.net.APICode;

import java.util.List;
import java.util.Vector;

public abstract class AbsToCallActivity extends AbsBaseActivity
{
	public String strNumber ="";
	private EditText editInputFrom;
	private ListView listview;
	private ContactAdapter adapter;
	public List<BaseMapObject> all;
	public static final int TOCALLVOICE = 0;		//声码话
	public static final int TOCALLWIREDVOICE = 1;	//有线语音
	public static final int TOCALLWIREDFILE = 2;	//有线文件
	public static int  CurrentType = -1;
	public static String Recv_Call = "1";//接听
	public static String Send_Call = "2";//拨打
	public static String Recv_Error = "3";//未接
	

	public List<BaseMapObject> getContact(String code)
	{
		if(code.equals(""))
		{
			return all;
		}
		List<BaseMapObject> have = new Vector<BaseMapObject>();
		for(BaseMapObject item : all)
		{
			if(item.get("number").toString().indexOf(code)!=-1)
			{
				have.add(item);
			}
		}
		return have;
	}
	
	public void refreshList()
	{
		adapter = new ContactAdapter(mContext, getContact(strNumber),"name","name","number"); 
		listview.setAdapter(adapter);
		listview.setOnItemClickListener(new OnItemClickListener()
		{

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3)
			{
				// TODO Auto-generated method stub
//				toCall(TOCALLVOICE, getContact(strNumber).get(arg2).get("number").toString());
				if(CurrentType==TOCALLVOICE)
				{
					insertVoiceRecord(mContext,getContact(strNumber).get(arg2).get("number").toString());
				}
				else if(CurrentType == TOCALLWIREDVOICE)
				{
					insertWiredRecord(mContext, getContact(strNumber).get(arg2).get("number").toString(), null);
				}
				else if(CurrentType == TOCALLWIREDFILE)
				{
					
				}
			}
		});
	}
	
	public void insertNum(String num)
	{
		strNumber+=num;
		editInputFrom.setText(strNumber);
		refreshList();
	}
	
	public boolean isHaveNum(String code)
	{
		for(BaseMapObject item : all)
		{
			if(item.get("number").toString().equals(code))
			{
				return true;
			}
		}
		return false;
	}
	
	public void delNum()
	{
		if(strNumber.length()<=0)
		{
			return;
		}
		strNumber = strNumber.substring(0, strNumber.length()-1);
		editInputFrom.setText(strNumber);
		refreshList();
	}
	
	@Override
	public void onClick(View v)
	{
		// TODO Auto-generated method stub
		switch(v.getId())
		{
		case R.id.bt_left:
			this.finish();
			break;
		case R.id.button0:
			insertNum("0");
			break;
		case R.id.button1:
			insertNum("1");
			break;
		case R.id.button2:
			insertNum("2");
			break;
		case R.id.button3:
			insertNum("3");
			break;
		case R.id.button4:
			insertNum("4");
			break;
		case R.id.button5:
			insertNum("5");
			break;
		case R.id.button6:
			insertNum("6");
			break;
		case R.id.button7:
			insertNum("7");
			break;
		case R.id.button8:
			insertNum("8");
			break;
		case R.id.button9:
			insertNum("9");
			break;
		case R.id.buttonx:
			insertNum("*");
			break;
		case R.id.buttony:
			insertNum("#");
			break;
		case R.id.buttoncall:
			if(strNumber.equals(""))
			{
				MyLog.showToast(mContext, "请输入有效号码");
				return;
			}
			CurrentType = TOCALLVOICE;
			insertVoiceRecord(mContext,strNumber);
			break;
		case R.id.buttonadd:
			inserContact();
			break;
		case R.id.buttondel:
			delNum();
			break;
		case R.id.buttoncallvoice:
			if(strNumber.equals(""))
			{
				MyLog.showToast(mContext, "请输入有效号码");
				return;
			}
			CurrentType = TOCALLWIREDVOICE;
			insertWiredRecord(mContext,strNumber, null);
			break;
			
			//文件呼叫方式修改，此处不需要呼叫，注释
//		case R.id.buttoncallfile:
////			insertWiredRecord(strNumber, 1);
//			if(strNumber.equals(""))
//			{
//				MyLog.showToast(mContext, "请输入有效号码");
//				return;
//			}
//			CurrentType = TOCALLWIREDFILE;
//			Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//			intent.setType("image/*");
//			intent.addCategory(Intent.CATEGORY_OPENABLE);
//			try
//			{
//				startActivityForResult(Intent.createChooser(intent, "请选择附件"), 0);
//			} catch (android.content.ActivityNotFoundException ex)
//			{
//				Toast.makeText(this, "Please install a File Manager.", Toast.LENGTH_SHORT).show();
//			}
//			break;
			

		}
	}
	
	public static void  insertVoiceRecord(Context contex,String code)
	{
		if(code.equals(""))
		{
			return;
		}
		BaseMapObject record = new BaseMapObject();
		record.put("id",null);
		record.put("number",code);
		record.put("status","2");
		record.put("creattime", UnixTime.getStrCurrentUnixTime());
		record.put("priority", OverAllData.Priority);
		record.put("acknowledgemen", OverAllData.Acknowledgemen);
		
		record.InsertObj2DB(contex, "voicecoderecord");
		toCall(contex,code,null);
	}
	
	public static void insertWiredRecord(Context mcontext,String code,String filepath)
	{
		if(code.equals(""))
		{
			return;
		}
		BaseMapObject record = new BaseMapObject();
		record.put("id",null);
		record.put("number",code);
		record.put("sendtype",filepath==null?"0":"1");//语音0.文件1
		record.put("status","2");//呼入成功/呼出成功/呼入失败/呼出失败(1,2,3,4)
		record.put("filepath",filepath);
		record.put("creattime", UnixTime.getStrCurrentUnixTime());
		
		record.InsertObj2DB(mcontext, "wiredrecord");
		toCall(mcontext,code,filepath);
	}
	
	public static void  sendVoiceStarttoNet(String contact)
	{
		new SendDataTask().execute(APICode.SEND_VoiceCode+"",OverAllData.Account,contact);
	}
	
	public static void  sendWiredStarttoNet(String contact)
	{
		new SendDataTask().execute(APICode.SEND_WiredVoice+"",OverAllData.Account,contact);
	}
	
	public  static void toCall(Context contex,final String code,String filepath)
	{
		if(CurrentType==TOCALLVOICE)
		{
			contex.startActivity(new Intent(contex, CallWaitActivity.class).putExtra("type", TOCALLVOICE).putExtra("code", code));
			sendVoiceStarttoNet(code);
			//			new Timer().schedule(new TimerTask()
//			{
//				
//				@Override
//				public void run()
//				{
//					// TODO Auto-generated method stub
//					sendVoiceStarttoNet(code);
//				}
//			},1000);
			
		}
		else if(CurrentType == TOCALLWIREDVOICE)
		{
			contex.startActivity(new Intent(contex, CallWaitActivity.class).putExtra("type", TOCALLWIREDVOICE).putExtra("code", code));
			sendWiredStarttoNet(code);
			//			new Timer().schedule(new TimerTask()
//			{
//				
//				@Override
//				public void run()
//				{
//					// TODO Auto-generated method stub
//					
//				}
//			},1000);
		}
		else if(CurrentType == TOCALLWIREDFILE)
		{
//			contex.startActivity(new Intent(contex, CallWaitActivity.class).putExtra("type", TOCALLWIREDFILE).putExtra("filepath", filepath).putExtra("code", code));
//			new Timer().schedule(new TimerTask()
//			{
//				@Override
//				public void run()
//				{
//					// TODO Auto-generated method stub
//					sendWiredStarttoNet(code);
//				}
//			},1000);
		}
			
	}
	
	public void inserContact()
	{
		if(strNumber.equals(""))
		{
			return;
		}
		else if(isHaveNum(strNumber))
		{
			MyLog.showToast(mContext, "此号码已存在，不能添加。");
		}
		else
		{
			startActivity(new Intent(mContext, CreatContactActivity.class).putExtra("number", strNumber));
		}
	}

	@Override
	public void initView()
	{
		// TODO Auto-generated method stub
		initBaseView("拨号");
		Btn_Left.setOnClickListener(this);
		Btn_Right.setVisibility(View.INVISIBLE);
		editInputFrom = (EditText)findViewById(R.id.edit_form);
		listview = (ListView)findViewById(R.id.listview_content);
		findViewById(R.id.button0).setOnClickListener(this);
		findViewById(R.id.button1).setOnClickListener(this);
		findViewById(R.id.button2).setOnClickListener(this);
		findViewById(R.id.button3).setOnClickListener(this);
		findViewById(R.id.button4).setOnClickListener(this);
		findViewById(R.id.button5).setOnClickListener(this);
		findViewById(R.id.button6).setOnClickListener(this);
		findViewById(R.id.button7).setOnClickListener(this);
		findViewById(R.id.button8).setOnClickListener(this);
		findViewById(R.id.button9).setOnClickListener(this);
		findViewById(R.id.buttonx).setOnClickListener(this);
		findViewById(R.id.buttony).setOnClickListener(this);
		findViewById(R.id.buttonadd).setOnClickListener(this);
		findViewById(R.id.buttondel).setOnClickListener(this);
		
		refreshList();
		
	}
	
}
