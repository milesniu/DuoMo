package com.miles.ccit.duomo;

import java.util.Map;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

import com.miles.ccit.duomo.R;
import com.miles.ccit.net.APICode;
import com.miles.ccit.util.SendDataTask;

public class SpecialNetFragmentActivity extends FragmentActivity 
{
	// 定义FragmentTabHost对象
		public static FragmentTabHost mTabHost;
		public static FragmentManager fm;

		public Map<String, Object> resultData;

		// 定义一个布局
		private LayoutInflater layoutInflater;

		// 定义数组来存放Fragment界面
		private Class fragmentArray[] =
		{ ShortMsgFragment.class, VoicecodeFragment.class, EmailFragment.class, CodeDirectFragment.class,SendsetFragment.class};

		// 定义数组来存放按钮图片
		private int mImageViewArray[] =
		{ R.drawable.tab_msg_btn, R.drawable.tab_voice_btn, R.drawable.tab_mail_btn, R.drawable.tab_direct_btn,R.drawable.tab_set_btn};

		// Tab选项卡的文字
		private String mTextviewArray[] =
		{ "短消息", "声码话", "邮件", "代码指挥","发送设置"};


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_special_net);
		initView();
		fm = getSupportFragmentManager();
	}
	
	

	@Override
	protected void onDestroy()
	{
		// TODO Auto-generated method stub
		new SendDataTask().execute(APICode.SEND_BackModel+"");
		super.onDestroy();
	}



	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.special_net, menu);
		return true;
	}

	/**
	 * 初始化组件
	 */
	private void initView()
	{
		// 实例化布局对象
		layoutInflater = LayoutInflater.from(this);

		// 实例化TabHost对象，得到TabHost
		mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
		mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);

		// 得到fragment的个数
		int count = fragmentArray.length;

		for (int i = 0; i < count; i++)
		{
			// 为每一个Tab按钮设置图标、文字和内容
			TabSpec tabSpec = mTabHost.newTabSpec(mTextviewArray[i]).setIndicator(getTabItemView(i));
			// 将Tab按钮添加进Tab选项卡中
			mTabHost.addTab(tabSpec, fragmentArray[i], null);
			// 设置Tab按钮的背景
			mTabHost.getTabWidget().getChildAt(i).setBackgroundResource(R.drawable.selector_tab_background);
		
		}
	}
	
	/**
	 * 给Tab按钮设置图标和文字
	 */
	private View getTabItemView(int index)
	{
		View view = layoutInflater.inflate(R.layout.tab_item_view, null);

		ImageView imageView = (ImageView) view.findViewById(R.id.imageview);
		imageView.setImageResource(mImageViewArray[index]);

		TextView textView = (TextView) view.findViewById(R.id.textview);
		textView.setText(mTextviewArray[index]);
		return view;
	}

	
}
