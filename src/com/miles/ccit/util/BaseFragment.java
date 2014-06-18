package com.miles.ccit.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.miles.ccit.duomo.R;

public class BaseFragment extends Fragment {
	public ProgressDialog pdialog;
	public boolean isHead = false;
	public static String title = "多模系统";
	public static String message = "系统正在为您努力加载···";
	public View LayoutTitle;
	public Button Btn_Left;
	public Button Btn_Right;
	public ListView listview;

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

	public void initBaseView(View view, String titlename) {
		listview = (ListView) view.findViewById(R.id.listView_content);

		LayoutTitle = (View) view.findViewById(R.id.include_layout);
		if (LayoutTitle.findViewById(R.id.title_text) != null) {
			((TextView) LayoutTitle.findViewById(R.id.title_text))
					.setText(titlename);
		}
		Btn_Left = (Button) LayoutTitle.findViewById(R.id.bt_left);
		Btn_Right = (Button) LayoutTitle.findViewById(R.id.bt_right);
		Btn_Left.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				getActivity().finish();
			}
		});
	}

}
