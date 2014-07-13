package com.miles.ccit.util;

import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.miles.ccit.database.GetData4DB;
import com.miles.ccit.duomo.R;
import com.miles.ccit.ui.LoginActivity;
import com.miles.ccit.ui.LoginActivity.MyBroadcastReciver;

public abstract class AbsBaseActivity extends Activity implements OnClickListener
{

	public Context mContext = this;
	public View LayoutTitle;
	public Button Btn_Left;
	public Button Btn_Right;
	public LinearLayout linear_Select;
	public TextView text_left;
	public TextView text_right;
	public Button Btn_Delete;
	public Button Btn_Canle;
	public static final int RECVFROM = 1;
	public static final int SENDTO = 2;
	public static final int SENDERROR = 3;

	public static final String broad_login_Action = "cn.broadcast.login";
	public static final String broad_recvtextmsg_Action = "cn.broadcast.recvtextmsg";
	public static final String broad_Email_Action = "cn.broadcast.recvemail";

	public abstract void initView();

	public ImageView img_Empty;
	public ProgressDialog pdialog;
	public static String title = "Anzer";
	public static String message = "正在努力加载···";

	public void initBaseView(String titlename)
	{
		LayoutTitle = (View) findViewById(R.id.include_layout);
		if (LayoutTitle.findViewById(R.id.title_text) != null)
		{
			((TextView) LayoutTitle.findViewById(R.id.title_text)).setText(titlename);
		}
		Btn_Left = (Button) LayoutTitle.findViewById(R.id.bt_left);
		Btn_Right = (Button) LayoutTitle.findViewById(R.id.bt_right);
		Btn_Left.setOnClickListener(this);
		Btn_Right.setOnClickListener(this);
		img_Empty = (ImageView) findViewById(R.id.image_empty);

	}
	
	
	

	



	@Override
	protected void onDestroy()
	{
		// TODO Auto-generated method stub
		super.onDestroy();
	}




	public void checkContact(byte[] data)
	{

		HashMap<String, BaseMapObject> contacthash = GetData4DB.getObjectHashData(this, "contact", "number");

		try
		{
			int contactlenth = ByteUtil.byte2Int(new byte[]
			{ data[6], data[7] });
			byte[] bytenamebyte = new byte[contactlenth];
			System.arraycopy(data, 8, bytenamebyte, 0, contactlenth);
			String strname = new String(bytenamebyte, "UTF-8");
			String[] arraycon = strname.split(",");
			for (int j = 0; j <= (arraycon.length / 2); j += 2)
			{
				String num = arraycon[j];
				String name = arraycon[j + 1];
				BaseMapObject item = contacthash.get(num);
				if (item == null)
				{
					// 直接添加
					BaseMapObject contact = new BaseMapObject();
					contact.put("id", null);
					contact.put("name", name);
					contact.put("number", num);
					contact.put("type", "0");// 默认加为无线侧
					contact.put("remarks", "");
					contact.put("creattime", UnixTime.getStrCurrentUnixTime());
					contact.InsertObj2DB(mContext, "contact");

				} else if (num.equals(item.get("number").toString()) && name.equals(item.get("name").toString()))
				{
					// 直接返回
				} else
				{
					// 更新
					item.put("name", name);
					item.UpdateObj2DBbyId(mContext, "contact");

				}
				MyLog.SystemOut(name);
			}

			MyLog.SystemOut(strname);
		} catch (Exception e)
		{
			e.printStackTrace();
		}

	}

	
	public void showEmpty()
	{

		if (img_Empty != null)
		{
			img_Empty.setVisibility(View.VISIBLE);
		}
	}

	public void hideEmpty()
	{

		if (img_Empty != null)
		{
			img_Empty.setVisibility(View.GONE);
		}
	}

	public void showprogressdialog()
	{
		if (pdialog == null || !pdialog.isShowing())
		{
			pdialog = ProgressDialog.show(this, title, message);
			pdialog.setIcon(R.drawable.ic_launcher);
			pdialog.setCancelable(true);
		}
	}

	public void hideProgressDlg()
	{
		if (pdialog != null && pdialog.isShowing())
		{
			pdialog.dismiss();
		}
	}

	@Override
	protected void onStart()
	{
		// TODO Auto-generated method stub
		initView();
		super.onStart();
	}

	public void changeSiwtchLeft()
	{
		linear_Select.setBackgroundResource(R.drawable.selectleft);
		text_left.setTextColor(getResources().getColor(R.color.white));
		text_right.setTextColor(getResources().getColor(R.color.black));

	}

	public void changeSiwtchRight()
	{
		linear_Select.setBackgroundResource(R.drawable.selectright);
		text_left.setTextColor(getResources().getColor(R.color.black));
		text_right.setTextColor(getResources().getColor(R.color.white));

	}

	public void initSwitchBaseView(String leftname, String rightname)
	{
		LayoutTitle = (View) findViewById(R.id.include_layout);
		linear_Select = (LinearLayout) findViewById(R.id.linear_select);
		text_left = (TextView) findViewById(R.id.text_left);
		text_right = (TextView) findViewById(R.id.text_right);
		text_left.setText(leftname);
		text_right.setText(rightname);
		Btn_Left = (Button) LayoutTitle.findViewById(R.id.bt_left);
		Btn_Right = (Button) LayoutTitle.findViewById(R.id.bt_right);
		img_Empty = (ImageView) findViewById(R.id.image_empty);
		Btn_Left.setOnClickListener(this);
		Btn_Right.setOnClickListener(this);
		text_left.setOnClickListener(this);
		text_right.setOnClickListener(this);
	}

}
