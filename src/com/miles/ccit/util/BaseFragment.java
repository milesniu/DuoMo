package com.miles.ccit.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.View;

import com.miles.ccit.duomo.R;

public class BaseFragment extends Fragment
{
	public ProgressDialog pdialog;
	public boolean isHead = false;
	public static String title = "多模系统";
	public static String message = "系统正在为您努力加载···";
	public View LayoutTitle;
	
	public void showprogressdialog(Context context)
	{
		if (pdialog == null || !pdialog.isShowing())
		{
			pdialog = ProgressDialog.show(context, title, message);
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
	
}
