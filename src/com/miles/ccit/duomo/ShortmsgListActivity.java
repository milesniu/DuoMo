package com.miles.ccit.duomo;

import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.miles.ccit.database.GetData4DB;
import com.miles.ccit.util.BaseActivity;
import com.miles.ccit.util.BaseMapObject;
import com.miles.ccit.util.UnixTime;

public class ShortmsgListActivity extends BaseActivity
{

	private EditText edit_inputMsg;
	private Button Btn_switchVoice;
	private Button Btn_Send;
	private Button Btn_Talk;
	private BaseMapObject map = null;
	private List<BaseMapObject> shortList = new Vector<BaseMapObject>();
	private ListView list_Content;
	
	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shortmsg_list);
		if(getIntent().getSerializableExtra("item")!=null)
		{
			map = BaseMapObject.HashtoMyself((HashMap<String,Object>)getIntent().getSerializableExtra("item"));
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.shortmsg_list, menu);
		return true;
	}

	@Override
	public void onClick(View v)
	{
		// TODO Auto-generated method stub
		switch(v.getId())
		{
		case R.id.bt_left:
			this.finish();
			break;
		case R.id.bt_addcontact:
			break;
		case R.id.bt_swicthvoice:
			if(edit_inputMsg.getVisibility() == View.VISIBLE)
			{
				edit_inputMsg.setVisibility(View.GONE);
				Btn_Talk.setVisibility(View.VISIBLE);
				Btn_switchVoice.setText("文字");
			}
			else
			{
				edit_inputMsg.setVisibility(View.VISIBLE);
				Btn_Talk.setVisibility(View.GONE);
				Btn_switchVoice.setText("语音");
			}
			break;
		case R.id.bt_send:
			
			if(edit_inputMsg.getText().toString().equals(""))
			{
				Toast.makeText(mContext, "发送内容不能为空...", 0).show();
				return;
			}
			else
			{
				BaseMapObject shortmsg = new BaseMapObject();
				shortmsg.put("id", null);
				shortmsg.put("number",map.get("number").toString());
				shortmsg.put("sendtype", "1");
				shortmsg.put("status", "1");
				shortmsg.put("msgtype", "0");
				shortmsg.put("msgcontent", edit_inputMsg.getText().toString());
				shortmsg.put("creattime",UnixTime.getStrCurrentUnixTime());
				shortmsg.put("priority", "1");
				shortmsg.put("acknowledgemen", "1");
				shortmsg.InsertObj2DB(mContext, "shortmsg");				
			}
			break;
		}
	}

	private void refreshList()
	{
		shortList = GetData4DB.getObjectListData(mContext, "shortmsg", "number", map.get("number").toString());
		
		if (shortList == null)
		{
			Toast.makeText(mContext, "暂无消息记录...", 0).show();
			return;
		}

//		adapter = new ShortMsgSetAdapter(getActivity(), msgList);
		list_Content.setAdapter(new MessageListAdapter(mContext, shortList, list_Content));
//		list_Content.setOnItemClickListener(new OnItemClickListener()
//		{
//
//			@Override
//			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
//			{
//				// TODO Auto-generated method stub
//				getActivity().startActivity(new Intent(getActivity(), ShortmsgListActivity.class).putExtra("item", msgList.get(arg2)));
//
//			}
//		});
	}
	
	@Override
	public void initView()
	{
		// TODO Auto-generated method stub
		initBaseView(map.get("name")==null?map.get("number").toString():map.get("name").toString());
		Btn_Left.setText("返回");
		Btn_Right.setVisibility(View.INVISIBLE);
		edit_inputMsg = (EditText)findViewById(R.id.edit_inputmsg);
		Btn_switchVoice = (Button)findViewById(R.id.bt_swicthvoice);
		Btn_Send = (Button)findViewById(R.id.bt_send);
		Btn_Talk = (Button)findViewById(R.id.bt_talk);
		Btn_switchVoice.setOnClickListener(this);
		Btn_Send.setOnClickListener(this);
		list_Content = (ListView)findViewById(R.id.listView_contect);
		
		refreshList();
		
	}
	
	private class MessageListAdapter extends BaseAdapter
	{

		private List<BaseMapObject> items;
		private Context context;
		private ListView adapterList;
		public MessageListAdapter(Context context, List<BaseMapObject> items, ListView adapterList)
		{
			this.context = context;
			this.items = items;
			this.adapterList = adapterList;
		}

		public void refreshList(List<BaseMapObject> items)
		{
			this.items = items;
			this.notifyDataSetChanged();
			adapterList.setSelection(items.size() - 1);
		}

		@Override
		public int getCount()
		{
			return items.size();
		}

		@Override
		public Object getItem(int position)
		{
			return items.get(position);
		}

		@Override
		public long getItemId(int position)
		{
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			BaseMapObject message = items.get(position);

			View talkView = null;//LayoutInflater.from(ChatActivity.this).inflate(R.layout.child, null);
			
			String userName = "我 ";
			
			switch(Integer.parseInt(message.get("sendtype").toString()))
			{
			case 1:
				talkView = LayoutInflater.from(mContext).inflate(R.layout.outcometalk, null);
				
				break;
			case 2:
				talkView = LayoutInflater.from(mContext).inflate(R.layout.incometalk, null);
				
				break;
			case 3:
				break;
			}
			
			
			TextView text = (TextView)talkView.findViewById(R.id.textcontent);
			
			if(message.get("msgcontent")==null||message.get("msgcontent").equals("null"))
			{
				text.setText("无效信息");
			}
			else
			{
				text.setText(message.get("msgcontent").toString());
			}
			return talkView;
			
		}

	}

}
