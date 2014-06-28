package com.miles.ccit.util;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.miles.ccit.duomo.R;

public abstract class AbsBaseActivity extends Activity implements OnClickListener {
	
	public Context mContext = this;
	public View LayoutTitle;
	public Button Btn_Left;
	public Button Btn_Right;
	public LinearLayout linear_Select;
	public TextView text_left;
	public TextView text_right;
	public Button Btn_Delete;
	public Button Btn_Canle;
	public abstract void initView();
	
	public void initBaseView(String titlename)
	{
		LayoutTitle = (View)findViewById(R.id.include_layout);
		if (LayoutTitle.findViewById(R.id.title_text) != null) {
			((TextView) LayoutTitle.findViewById(R.id.title_text))
					.setText(titlename);
		}
		Btn_Left = (Button)LayoutTitle.findViewById(R.id.bt_left);
		Btn_Right = (Button)LayoutTitle.findViewById(R.id.bt_right);
		Btn_Left.setOnClickListener(this);
		Btn_Right.setOnClickListener(this);
	}
	
	
	
	@Override
	protected void onStart()
	{
		// TODO Auto-generated method stub
		initView();
		super.onStart();
	}

	
	public void initSwitchBaseView(String leftname,String rightname)
	{
		LayoutTitle = (View)findViewById(R.id.include_layout);
		linear_Select = (LinearLayout)findViewById(R.id.linear_select);
		text_left = (TextView)findViewById(R.id.text_left);
		text_right = (TextView)findViewById(R.id.text_right);
		text_left.setText(leftname);
		text_right.setText(rightname);
		Btn_Left = (Button)LayoutTitle.findViewById(R.id.bt_left);
		Btn_Right = (Button)LayoutTitle.findViewById(R.id.bt_right);
		Btn_Left.setOnClickListener(this);
		Btn_Right.setOnClickListener(this);
		text_left.setOnClickListener(this);
		text_right.setOnClickListener(this);
	}

}
