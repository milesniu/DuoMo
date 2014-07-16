package com.miles.ccit.util;

import java.util.List;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.EditText;
import android.widget.Toast;

import com.miles.ccit.duomo.R;

public class MutiChoiseDlg
{
	private Context mContext;
	private List<BaseMapObject> contactList;
	boolean[] selected;

	public MutiChoiseDlg(Context contex, List<BaseMapObject> contacts)
	{
		this.mContext = contex;
		contactList = contacts;
		selected = new boolean[contactList.size()];
	}

	public String getDlg(final EditText edit)
	{
		Dialog dialog = null;
		Builder builder = new AlertDialog.Builder(mContext);
		builder.setTitle("联系人选择");
		builder.setIcon(R.drawable.ic_launcher);
		DialogInterface.OnMultiChoiceClickListener mutiListener = new DialogInterface.OnMultiChoiceClickListener()
		{

			@Override
			public void onClick(DialogInterface dialogInterface, int which,
					boolean isChecked)
			{
				selected[which] = isChecked;
			}
		};
		String[] arrayc = new String[contactList.size()];
		for (int i = 0; i < contactList.size(); i++)
		{
			arrayc[i] = contactList.get(i).get("name").toString();
		}
		builder.setMultiChoiceItems(arrayc, selected, mutiListener);
		DialogInterface.OnClickListener btnListener = new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialogInterface, int which)
			{
				String selectedStr = "";
				for (int i = 0; i < selected.length; i++)
				{
					if (selected[i] == true)
					{
						selectedStr = selectedStr
								+ contactList.get(i).get("number").toString()+ "," ;
					}
				}
				edit.setText(selectedStr.equals("")?"":selectedStr.subSequence(0, selectedStr.length()-1));
			}
		};
		builder.setPositiveButton("确定", btnListener);
		builder.setNegativeButton("取消", null);
		dialog = builder.create();
		dialog.show();
		return null;
	}
}
