package com.miles.ccit.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.miles.ccit.duomo.R;

public abstract class AbsBaseFragment extends Fragment implements OnClickListener {
	public ProgressDialog pdialog;
	public boolean isHead = false;
	public static String title = "多模系统";
	public static String message = "系统正在为您努力加载···";
	public View LayoutTitle;
	public Button Btn_Left;
	public Button Btn_Right;
	public ListView listview;
	public LinearLayout linear_Select;
	public TextView text_left;
	public TextView text_right;
	public ImageView img_Empty;

	public void showprogressdialog(Context context) {
		if (pdialog == null || !pdialog.isShowing()) {
			pdialog = ProgressDialog.show(context, title, message);
			pdialog.setIcon(R.drawable.ic_launcher);
			pdialog.setCancelable(true);
		}
	}

	public void hideProgressDlg() {
		if (pdialog != null && pdialog.isShowing()) {
			pdialog.dismiss();
		}
	}

	public abstract void initView(View view);
	
	public void showEmpty()
	{

		if(img_Empty!=null)
		{
			img_Empty.setVisibility(View.VISIBLE);
		}
	}
	
	public void hideEmpty()
	{

		if(img_Empty!=null)
		{
			img_Empty.setVisibility(View.GONE);
		}
	}
	public void initBaseView(View view, String titlename) {
		listview = (ListView) view.findViewById(R.id.listView_content);
		img_Empty = (ImageView)view.findViewById(R.id.image_empty);
		
		LayoutTitle = (View) view.findViewById(R.id.include_layout);
		if (LayoutTitle.findViewById(R.id.title_text) != null) {
			((TextView) LayoutTitle.findViewById(R.id.title_text))
					.setText(titlename);
		}
		Btn_Left = (Button) LayoutTitle.findViewById(R.id.bt_left);
		Btn_Right = (Button) LayoutTitle.findViewById(R.id.bt_right);
		Btn_Left.setOnClickListener(this);
		Btn_Right.setOnClickListener(this);
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
	
	
	public void initSwitchBaseView(View view, String leftname,String rightname) {
		listview = (ListView) view.findViewById(R.id.listView_content);
		img_Empty = (ImageView)view.findViewById(R.id.image_empty);
		
		LayoutTitle = (View) view.findViewById(R.id.include_layout);
		linear_Select = (LinearLayout)view.findViewById(R.id.linear_select);
		text_left = (TextView)view.findViewById(R.id.text_left);
		text_right = (TextView)view.findViewById(R.id.text_right);
		text_left.setText(leftname);
		text_right.setText(rightname);
		Btn_Left = (Button) LayoutTitle.findViewById(R.id.bt_left);
		Btn_Right = (Button) LayoutTitle.findViewById(R.id.bt_right);
		Btn_Left.setOnClickListener(this);
		Btn_Right.setOnClickListener(this);
		text_left.setOnClickListener(this);
		text_right.setOnClickListener(this);
	}

}
