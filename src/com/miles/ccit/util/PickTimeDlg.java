package com.miles.ccit.util;

import java.util.Calendar;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.EditText;
import android.widget.TextView;

import com.miles.ccit.duomo.R;
import com.miles.techbloom.wheel.StrericWheelAdapter;
import com.miles.techbloom.wheel.WheelView;

public class PickTimeDlg
{
	private int minYear = 1970; // 最小年份
	private int fontSize = 13; // 字体大小
	private WheelView yearWheel, monthWheel, dayWheel, hourWheel, minuteWheel, secondWheel;
	public static String[] yearContent = null;
	public static String[] monthContent = null;
	public static String[] dayContent = null;
	public static String[] hourContent = null;
	public static String[] minuteContent = null;
	public static String[] secondContent = null;
	private Context mContext;
	public PickTimeDlg(Context context,EditText text)
	{
		this.mContext = context;
		initContent();
		buttonClick(text);
	}
	
	
	private void initContent()
	{
		yearContent = new String[10];
		for (int i = 0; i < 10; i++)
			yearContent[i] = String.valueOf(i + 2013);

		monthContent = new String[12];
		for (int i = 0; i < 12; i++)
		{
			monthContent[i] = String.valueOf(i + 1);
			if (monthContent[i].length() < 2)
			{
				monthContent[i] = "0" + monthContent[i];
			}
		}

		dayContent = new String[31];
		for (int i = 0; i < 31; i++)
		{
			dayContent[i] = String.valueOf(i + 1);
			if (dayContent[i].length() < 2)
			{
				dayContent[i] = "0" + dayContent[i];
			}
		}
		hourContent = new String[24];
		for (int i = 0; i < 24; i++)
		{
			hourContent[i] = String.valueOf(i);
			if (hourContent[i].length() < 2)
			{
				hourContent[i] = "0" + hourContent[i];
			}
		}

		minuteContent = new String[60];
		for (int i = 0; i < 60; i++)
		{
			minuteContent[i] = String.valueOf(i);
			if (minuteContent[i].length() < 2)
			{
				minuteContent[i] = "0" + minuteContent[i];
			}
		}
		secondContent = new String[60];
		for (int i = 0; i < 60; i++)
		{
			secondContent[i] = String.valueOf(i);
			if (secondContent[i].length() < 2)
			{
				secondContent[i] = "0" + secondContent[i];
			}
		}
	}

	private void buttonClick(final EditText v)
	{

		LayoutInflater mInflater = LayoutInflater.from(mContext);
		View view = mInflater.inflate(R.layout.time_picker, null);
		
//		View view = ((LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE)).inflate(R.layout.time_picker, null);

		Calendar calendar = Calendar.getInstance();
		int curYear = calendar.get(Calendar.YEAR);
		int curMonth = calendar.get(Calendar.MONTH) + 1;
		int curDay = calendar.get(Calendar.DAY_OF_MONTH);
		int curHour = calendar.get(Calendar.HOUR_OF_DAY);
		int curMinute = calendar.get(Calendar.MINUTE);
		int curSecond = calendar.get(Calendar.SECOND);

		yearWheel = (WheelView) view.findViewById(R.id.yearwheel);
		monthWheel = (WheelView) view.findViewById(R.id.monthwheel);
		dayWheel = (WheelView) view.findViewById(R.id.daywheel);
		hourWheel = (WheelView) view.findViewById(R.id.hourwheel);
		minuteWheel = (WheelView) view.findViewById(R.id.minutewheel);
		secondWheel = (WheelView) view.findViewById(R.id.secondwheel);

		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		builder.setView(view);

		yearWheel.setAdapter(new StrericWheelAdapter(yearContent));
		yearWheel.setCurrentItem(curYear - 2013);
		yearWheel.setCyclic(true);
		yearWheel.setInterpolator(new AnticipateOvershootInterpolator());

		monthWheel.setAdapter(new StrericWheelAdapter(monthContent));

		monthWheel.setCurrentItem(curMonth - 1);

		monthWheel.setCyclic(true);
		monthWheel.setInterpolator(new AnticipateOvershootInterpolator());

		dayWheel.setAdapter(new StrericWheelAdapter(dayContent));
		dayWheel.setCurrentItem(curDay - 1);
		dayWheel.setCyclic(true);
		dayWheel.setInterpolator(new AnticipateOvershootInterpolator());

		hourWheel.setAdapter(new StrericWheelAdapter(hourContent));
		hourWheel.setCurrentItem(curHour);
		hourWheel.setCyclic(true);
		hourWheel.setInterpolator(new AnticipateOvershootInterpolator());

		minuteWheel.setAdapter(new StrericWheelAdapter(minuteContent));
		minuteWheel.setCurrentItem(curMinute);
		minuteWheel.setCyclic(true);
		minuteWheel.setInterpolator(new AnticipateOvershootInterpolator());

		secondWheel.setAdapter(new StrericWheelAdapter(secondContent));
		secondWheel.setCurrentItem(curSecond);
		secondWheel.setCyclic(true);
		secondWheel.setInterpolator(new AnticipateOvershootInterpolator());

		builder.setTitle("选取时间");
		builder.setPositiveButton("确  定", new DialogInterface.OnClickListener()
		{

			@Override
			public void onClick(DialogInterface dialog, int which)
			{

				StringBuffer sb = new StringBuffer();
				sb.append(yearWheel.getCurrentItemValue()).append("-").append(monthWheel.getCurrentItemValue()).append("-").append(dayWheel.getCurrentItemValue());

				sb.append(" ");
				sb.append(hourWheel.getCurrentItemValue()).append(":").append(minuteWheel.getCurrentItemValue()).append(":").append(secondWheel.getCurrentItemValue());
				v.setText(sb);
				v.setTextColor(mContext.getResources().getColor(R.color.black));
				dialog.cancel();
			}
		});

		builder.show();

	}
}
