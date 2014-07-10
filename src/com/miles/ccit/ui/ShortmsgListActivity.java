package com.miles.ccit.ui;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.View.OnCreateContextMenuListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.miles.ccit.database.GetData4DB;
import com.miles.ccit.database.UserDatabase;
import com.miles.ccit.duomo.R;
import com.miles.ccit.util.BaseMapObject;
import com.miles.ccit.util.AbsMsgRecorderActivity;
import com.miles.ccit.util.OverAllData;

public class ShortmsgListActivity extends AbsMsgRecorderActivity
{

	private BaseMapObject map = null;
	private List<BaseMapObject> shortList = new Vector<BaseMapObject>();
	private ListView list_Content;
	private MediaPlayer mp;
	private MessageListAdapter adapter;
	private LinearLayout linear_Del;

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shortmsg_list);
		if (getIntent().getSerializableExtra("item") != null)
		{
			map = BaseMapObject.HashtoMyself((HashMap<String, Object>) getIntent().getSerializableExtra("item"));
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
		stopMediaplayer();
		switch (v.getId())
		{
		case R.id.bt_left:
			this.finish();
			break;
		case R.id.bt_addcontact:
			break;
		case R.id.bt_swicthvoice:
			switchVoice();
			break;
		case R.id.bt_send:
			sendTextmsg(map.get("number").toString());
			edit_inputMsg.setText("");
			refreshList();
			break;
		case R.id.bt_sure:
			Iterator<BaseMapObject> iter = shortList.iterator();
			List<String> Idlist = new Vector<String>();
			while (iter.hasNext())
			{
				BaseMapObject s = iter.next();
				if (s.get("exp2") != null && s.get("exp2").toString().equals("1"))
				{
					Idlist.add(s.get("id").toString());
					iter.remove();
				}
			}

			UserDatabase.DelListObj(mContext, "shortmsg", "id", Idlist);

			for (BaseMapObject tmp : shortList)
			{
				tmp.put("exp1", null);
				tmp.put("exp2", null);
			}
			adapter.notifyDataSetChanged();
			linear_Del.setVisibility(View.GONE);
			break;
		case R.id.bt_canle:
			for (BaseMapObject tmp : shortList)
			{
				tmp.put("exp1", null);
				tmp.put("exp2", null);
			}
			linear_Del.setVisibility(View.GONE);
			break;
		}
	}

	private void refreshList()
	{
		shortList = GetData4DB.getObjectListData(mContext, "shortmsg", "number", map.get("number").toString());

		if (shortList == null || shortList.size() < 1)
		{
			showEmpty();
			return;
		}
		hideEmpty();
		adapter = new MessageListAdapter(mContext, shortList, list_Content);
		list_Content.setAdapter(adapter);

		list_Content.setSelection(shortList.size() - 1);
		list_Content.setOnCreateContextMenuListener(new OnCreateContextMenuListener()
		{

			@Override
			public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo)
			{
				// TODO Auto-generated method stub
				menu.setHeaderTitle(OverAllData.TitleName);
				menu.add(0, 0, 0, "删除该信息");
				menu.add(0, 1, 1, "批量删除");
				menu.add(0, 3, 3, "取消");
			}
		});

	}

	@Override
	public boolean onContextItemSelected(MenuItem item)
	{
		// TODO Auto-generated method stub
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		int ListItem = (int) info.position;
		switch (item.getItemId())
		{
		case 0:
			BaseMapObject selectItem = shortList.get(ListItem);
			long ret = BaseMapObject.DelObj4DB(mContext, "shortmsg", "id", selectItem.get("id").toString());
			if (ret != -1)
			{
				shortList.remove(ListItem);
				adapter.notifyDataSetChanged();
			}
			break;
		case 1:
			for (BaseMapObject tmp : shortList)
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
	public void initView()
	{
		// TODO Auto-generated method stub
		initBaseView(map.get("name") == null ? map.get("number").toString() : map.get("name").toString());
		// Btn_Left.setText("返回");
		Btn_Right.setVisibility(View.INVISIBLE);
		edit_inputMsg = (EditText) findViewById(R.id.edit_inputmsg);
		Btn_switchVoice = (Button) findViewById(R.id.bt_swicthvoice);
		Btn_Send = (Button) findViewById(R.id.bt_send);
		Btn_Talk = (Button) findViewById(R.id.bt_talk);
		Btn_switchVoice.setOnClickListener(this);
		Btn_Send.setOnClickListener(this);
		list_Content = (ListView) findViewById(R.id.listView_contect);
		linear_Del = (LinearLayout) findViewById(R.id.linear_del);
		Btn_Delete = (Button) findViewById(R.id.bt_sure);
		Btn_Canle = (Button) findViewById(R.id.bt_canle);
		Btn_Delete.setOnClickListener(this);
		Btn_Canle.setOnClickListener(this);
		Btn_Talk.setOnTouchListener(new OnTouchListener()
		{

			@Override
			public boolean onTouch(View v, MotionEvent event)
			{
				// TODO Auto-generated method stub
				stopMediaplayer();
				switch (event.getAction())
				{
				case MotionEvent.ACTION_DOWN:
					talkTouchDown(map.get("number").toString());

					break;
				case MotionEvent.ACTION_UP:
					talkTouchUp(event);
					refreshList();
					break;
				}
				return false;
			}
		});
		refreshList();
	}

	@Override
	protected void onDestroy()
	{
		// TODO Auto-generated method stub
		stopMediaplayer();
		super.onDestroy();
	}

	private void stopMediaplayer()
	{
		if (mp != null && mp.isPlaying())
		{
			mp.stop();
			mp.release();
			mp = null;
		}
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
			final BaseMapObject message = items.get(position);

			View talkView = null;// LayoutInflater.from(ChatActivity.this).inflate(R.layout.child,
									// null);

			switch (Integer.parseInt(message.get("sendtype").toString()))
			{
			case AbsMsgRecorderActivity.RECVFROM:
				talkView = LayoutInflater.from(mContext).inflate(R.layout.incometalk, null);

				break;
			case AbsMsgRecorderActivity.SENDTO:
				talkView = LayoutInflater.from(mContext).inflate(R.layout.outcometalk, null);

				break;
			case AbsMsgRecorderActivity.SENDERROR:
				break;
			}

			TextView text = (TextView) talkView.findViewById(R.id.textcontent);

			CheckBox checkDel = (CheckBox) talkView.findViewById(R.id.check_del);
			if (message.get("exp1") != null && message.get("exp1").toString().equals("0"))
			{
				checkDel.setVisibility(View.VISIBLE);
			} else
			{
				checkDel.setVisibility(View.GONE);
			}
			checkDel.setOnCheckedChangeListener(new OnCheckedChangeListener()
			{

				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
				{
					// TODO Auto-generated method stub
					if (isChecked)
					{
						message.put("exp2", "1");
					} else
					{
						message.put("exp2", null);
					}
				}
			});

			if (message.get("msgcontent") == null || message.get("msgcontent").equals("null"))
			{
				text.setText("无效信息");
			} else
			{
				if (message.get("msgtype").toString().equals("0"))
				{
					text.setText(message.get("msgcontent").toString());
				} else if (message.get("msgtype").toString().equals("1"))
				{
					text.setText("(((");
					text.setOnClickListener(new OnClickListener()
					{

						@Override
						public void onClick(View v)
						{
							// TODO Auto-generated method stub
							try
							{
								stopMediaplayer();

								mp = new MediaPlayer();
								mp.setDataSource(message.get("msgcontent").toString());
								mp.prepare();
								mp.start();

							} catch (Exception e)
							{
								e.printStackTrace();
							}
						}
					});
				}
			}
			return talkView;

		}

	}

}
