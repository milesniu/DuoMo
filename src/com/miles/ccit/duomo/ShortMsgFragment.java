package com.miles.ccit.duomo;

import java.util.List;
import java.util.Vector;

import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnCreateContextMenuListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.miles.ccit.adapter.MsgorMailSetAdapter;
import com.miles.ccit.database.GetData4DB;
import com.miles.ccit.duomo.R;
import com.miles.ccit.util.AbsBaseFragment;
import com.miles.ccit.util.BaseMapObject;
import com.miles.ccit.util.MyLog;

public class ShortMsgFragment extends AbsBaseFragment
{

	private MsgorMailSetAdapter adapter;
	private List<BaseMapObject> msgList = new Vector<BaseMapObject>();
	public Button Btn_Delete;
	public Button Btn_Canle;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_shortmsg, null);
		initView(view);	
		return view;
	}

	
	
	@Override
	public void onResume()
	{
		// TODO Auto-generated method stub
		refreshList();
		super.onResume();
	}



	private void refreshList()
	{
		msgList = GetData4DB.getObjecSet(getActivity(), "shortmsg", "contact", "number", "number");
		
		if (msgList == null || msgList.size()<1)
		{
			showEmpty();
//			Toast.makeText(getActivity(), "暂无消息记录...", 0).show();
			return;
		}
		hideEmpty();
		adapter = new MsgorMailSetAdapter(getActivity(), msgList,"shortmsg");
		listview.setAdapter(adapter);
		listview.setOnItemClickListener(new OnItemClickListener()
		{

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
			{
				// TODO Auto-generated method stub
				getActivity().startActivity(new Intent(getActivity(), ShortmsgListActivity.class).putExtra("item", msgList.get(arg2)));

			}
		});
		
		
		listview.setOnCreateContextMenuListener(new OnCreateContextMenuListener()
		{
			
			@Override
			public void onCreateContextMenu(ContextMenu menu, View v,
					ContextMenuInfo menuInfo)
			{
				// TODO Auto-generated method stub
				menu.setHeaderTitle("短消息");
				menu.add(0, 0, 0, "删除该短信组");
				menu.add(0, 1, 1, "批量删除短信组");
				menu.add(0, 3, 3, "取消");
			}
		});
	}

	
	@Override
	public boolean onContextItemSelected(MenuItem item)
	{
		// TODO Auto-generated method stub
		AdapterContextMenuInfo info = (AdapterContextMenuInfo)item.getMenuInfo();
		int ListItem = (int)info.position;
		switch(item.getItemId())
		{
		case 0:
			confirmDlg("删除记录", "shortmsg", "number",msgList.get(ListItem), msgList, adapter);
//			
//			BaseMapObject selectItem = msgList.get(ListItem);
//			long ret = BaseMapObject.DelObj4DB(getActivity(), "shortmsg", "number",selectItem.get("number").toString());
//			if(ret != -1)
//			{
//				msgList.remove(ListItem);
//				adapter.notifyDataSetChanged();
//			}
			break;
		case 1:
			for(BaseMapObject tmp:msgList)
			{
				tmp.put("exp1", "0");
			}
			adapter.notifyDataSetChanged();
			linear_Del.setVisibility(View.VISIBLE);
			break;
		case 3:
			break;
		}
		return super.onContextItemSelected(item);
	}



	@Override
	public void initView(View view)
	{
		// TODO Auto-generated method stub
		initBaseView(view, "短消息");
//		Btn_Left.setText("返回");
//		Btn_Right.setText("新建");
		Btn_Right.setBackgroundResource(R.drawable.creatmsg);
		linear_Del = (LinearLayout)view.findViewById(R.id.linear_del);
		Btn_Delete = (Button)view.findViewById(R.id.bt_sure);
		Btn_Canle = (Button)view.findViewById(R.id.bt_canle);
		Btn_Delete.setOnClickListener(this);
		Btn_Canle.setOnClickListener(this);
		refreshList();
	}



	@Override
	public void onClick(View v)
	{
		// TODO Auto-generated method stub
		switch (v.getId())
		{
		case R.id.bt_left:
			getActivity().finish();			
			break;
		case R.id.bt_right:
			if(LoginActivity.isLogin)
			{
				startActivity(new Intent(getActivity(), CreatShortmsgActivity.class));
			}
			else
			{
				MyLog.showToast(getActivity(), "请登录后再执行该操作...");
			}
			break;
		case R.id.bt_sure:
			confirmDlg("删除记录", "shortmsg", "number",null, msgList, adapter);
//			
//			Iterator<BaseMapObject> iter = msgList.iterator();  
//			List<String> Idlist = new Vector<String>();
//			while(iter.hasNext())
//			{  
//			    BaseMapObject s = iter.next();  
//			    if(s.get("exp2")!=null &&s.get("exp2").toString().equals("1"))
//			    {  
//			    	Idlist.add(s.get("number").toString());
//			        iter.remove();
//			    }  
//			}  
//		
//			UserDatabase.DelListObj(getActivity(),"shortmsg", "number", Idlist);
//			
//			for(BaseMapObject tmp:msgList)
//			{
//				tmp.put("exp1", null);
//				tmp.put("exp2", null);
//			}
//			adapter.notifyDataSetChanged();
//			linear_Del.setVisibility(View.GONE);
			break;
		case R.id.bt_canle:
			for(BaseMapObject tmp:msgList)
			{
				tmp.put("exp1", null);
				tmp.put("exp2", null);
			}
			linear_Del.setVisibility(View.GONE);
			break;
		default:
			break;
		}
	}

	
}
