package com.miles.ccit.util;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.miles.ccit.duomo.R;

public class BaseActivity extends Activity {
	
	public Context mContext = this;
	public View LayoutTitle;
	public Button Btn_Left;
	public Button Btn_Right;
	
	public void initBaseView(String titlename)
	{
		LayoutTitle = (View)findViewById(R.id.include_layout);
		if (LayoutTitle.findViewById(R.id.title_text) != null) {
			((TextView) LayoutTitle.findViewById(R.id.title_text))
					.setText(titlename);
		}
		Btn_Left = (Button)LayoutTitle.findViewById(R.id.bt_left);
		Btn_Right = (Button)LayoutTitle.findViewById(R.id.bt_right);
		Btn_Left.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	}

}
