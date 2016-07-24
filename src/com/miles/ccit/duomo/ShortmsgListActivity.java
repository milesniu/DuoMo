package com.miles.ccit.duomo;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnCreateContextMenuListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.miles.ccit.database.GetData4DB;
import com.miles.ccit.net.APICode;
import com.miles.ccit.util.AbsCreatCodeActivity;
import com.miles.ccit.util.AbsMsgRecorderActivity;
import com.miles.ccit.util.BaseMapObject;
import com.miles.ccit.util.MyLog;
import com.miles.ccit.util.O;
import com.miles.ccit.util.SendNetData;
import com.miles.ccit.util.UnixTime;

import java.util.HashMap;
import java.util.List;
import java.util.Vector;

@SuppressLint("ViewHolder")
public class ShortmsgListActivity extends AbsMsgRecorderActivity {

    private BaseMapObject map = null;
    private List<BaseMapObject> shortList = new Vector<BaseMapObject>();
    private ListView list_Content;
    private MediaPlayer mp;
    private MessageListAdapter adapter;

    private MyBroadcastReciver broad = null;
    public static String number = null;
    public static boolean isTop = false;

    public ToggleButton tBtn_Trans;

    private int Type = -1;

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shortmsg_list);
        Type = getIntent().getIntExtra("type", -1);
        if (getIntent().getSerializableExtra("item") != null) {
            map = BaseMapObject.HashtoMyself((HashMap<String, Object>) getIntent().getSerializableExtra("item"));
            number = map.get("number").toString();
        }
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(broad_recvtextmsg_Action);
        intentFilter.addAction(broad_encrypt_Action);

        broad = new MyBroadcastReciver();
        this.registerReceiver(broad, intentFilter);

    }

    @Override
    protected void onPause() {
        isTop = false;
        super.onPause();
    }

    @Override
    protected void onStop() {
        isTop = false;
        super.onStop();
    }

    @Override
    protected void onResume() {
        isTop = true;
        super.onResume();
    }

    @Override
    public boolean isDestroyed() {
        number = null;
        if (broad != null) {
            this.unregisterReceiver(broad);
        }
        return super.isDestroyed();
    }

    public class MyBroadcastReciver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // hideProgressDlg();
            String action = intent.getAction();

            if (action.equals(broad_recvtextmsg_Action) || action.equals(broad_recvtextmsg_Action)) {
                if (intent.getSerializableExtra("data") == null) {
                    return;
                } else {
                    BaseMapObject broadmap = BaseMapObject.HashtoMyself((HashMap<String, Object>) getIntent().getSerializableExtra("item"));
                    if (broadmap.get("number").toString().equals(map.get("number").toString())) {
                        refreshList();
                    }
                }
            } else if (action.equals(broad_encrypt_Action)) {
                if (intent.getStringExtra("data") == null) {
                    return;
                } else {
                    refreshList();
                    BaseMapObject lastobj = shortList.get(shortList.size() - 1);
                    new SendNetData().execute(APICode.SEND_NET_Encrypt_ShortTextMsg + "", O.LOCALIP + "," + lastobj.get("id").toString(), lastobj.get("number").toString(), intent.getStringExtra("data"));
//                    Toast.makeText(mContext, "加密数据已返回:" + intent.getStringExtra("data"), Toast.LENGTH_SHORT).show();
                }
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.shortmsg_list, menu);
        return true;
    }

    @Override
    public void onClick(View v) {
        stopMediaplayer();
        switch (v.getId()) {
            case R.id.bt_left:
                this.finish();
                break;
            case R.id.bt_addcontact:
                break;
            case R.id.bt_swicthvoice:
                if (LoginActivity.isLogin) {
                    switchVoice();
                } else {
                    MyLog.showToast(mContext, "请登录后再执行该操作。");
                }
                break;
            case R.id.bt_send:
                if (LoginActivity.isLogin) {
                    String num = map.get("number").toString();
                    if (num.indexOf(".") != -1 || num.indexOf("@") != -1) {
                        Type = O.NET;
                    } else {
                        Type = O.WIRENESS;
                    }
                    if (num.indexOf("@") == -1) {
                        sendTextmsg(num, Type, tBtn_Trans.isChecked());
                    } else {
                        sendTextmsg(num.split("@")[1], num.split("@")[0], Type, tBtn_Trans.isChecked());
                    }
                    edit_inputMsg.setText("");
                    refreshList();
                } else {
                    MyLog.showToast(mContext, "请登录后再执行该操作。");
                }
                break;
            case R.id.bt_sure:
                confirmDlg("删除记录", "shortmsg", null, shortList, adapter);
                break;
            case R.id.bt_canle:
                for (BaseMapObject tmp : shortList) {
                    tmp.put("exp1", null);
                    tmp.put("exp2", null);
                }
                linear_Del.setVisibility(View.GONE);
                break;
        }
    }

    private void refreshList() {
        shortList = GetData4DB.getObjectListData(mContext, "shortmsg", "number", map.get("number").toString());

        if (shortList == null || shortList.size() < 1) {
            showEmpty();
            return;
        }
        hideEmpty();
        adapter = new MessageListAdapter(mContext, shortList, list_Content);
        list_Content.setAdapter(adapter);

        list_Content.setSelection(shortList.size() - 1);
        list_Content.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {

            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
                AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
                int ListItem = (int) info.position;
                HashMap<String, Object> item = shortList.get(ListItem);

                menu.setHeaderTitle("短消息");
                menu.add(0, 0, 0, "删除该信息");
                menu.add(0, 1, 1, "批量删除");
                if (LoginActivity.isLogin && item.get("sendtype").toString().equals(AbsCreatCodeActivity.SENDERROR + "")) {
                    menu.add(0, 2, 2, "重新发送");
                }
                if(item.get("msgtype").toString().equals("0"))        //消息主题是文字,可以拷贝
                {
                    menu.add(0, 3, 3, "拷贝");
                }
                menu.add(0, 4, 4, "取消");
            }
        });

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
        int ListItem = (int) info.position;
        BaseMapObject msgitem = shortList.get(ListItem);
        switch (item.getItemId()) {
            case 0:
                confirmDlg("删除记录", "shortmsg", msgitem, shortList, adapter);
                break;
            case 1:
                for (BaseMapObject tmp : shortList) {
                    tmp.put("exp1", "0");
                }
                adapter.notifyDataSetChanged();
                linear_Del.setVisibility(View.VISIBLE);
                break;
            case 2:
                msgitem.put("sendtype", AbsCreatCodeActivity.SENDNOW + "");
                msgitem.UpdateMyself(mContext, "shortmsg");
                adapter.notifyDataSetChanged();
                sendTextMsgtoNet(new long[]{Long.parseLong(msgitem.get("id") + "")}, new String[]{msgitem.get("number") + ""}, msgitem.get("msgcontent") + "", (Integer.parseInt(msgitem.get("exp2").toString())), tBtn_Trans.isChecked());
                break;
            case 3:
                ClipboardManager cmb = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                cmb.setText(msgitem.get("msgcontent").toString());
                break;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public void initView() {
        initBaseView(map.get("name") == null ? map.get("number").toString() : map.get("name").toString());
        isTop = true;
        // Btn_Left.setText("返回");
        Btn_Right.setVisibility(View.INVISIBLE);
        edit_inputMsg = (EditText) findViewById(R.id.edit_inputmsg);
        Btn_switchVoice = (Button) findViewById(R.id.bt_swicthvoice);
        tBtn_Trans = (ToggleButton) findViewById(R.id.toggle_trans);
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
        Btn_Talk.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                stopMediaplayer();
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        talkTouchDown(map.get("number").toString());

                        break;
                    case MotionEvent.ACTION_UP:
                        String num = map.get("number").toString();
                        if (num.indexOf("@") == -1) {
                            talkTouchUp(event, Type, num, null);
                        } else {
                            talkTouchUp(event, Type, num.split("@")[1], num.split("@")[0]);
                        }

                        refreshList();
                        break;
                }
                return false;
            }
        });
        refreshList();
        if (Type == O.NET && map.get("number").toString().indexOf("@") == -1) {
            tBtn_Trans.setVisibility(View.VISIBLE);
        } else {
            tBtn_Trans.setVisibility(View.GONE);
        }

    }

    @Override
    protected void onDestroy() {
        number = null;
        stopMediaplayer();
        unregisterReceiver(broad);
        super.onDestroy();
    }

    private void stopMediaplayer() {
        if (mp != null && mp.isPlaying()) {
            mp.stop();
            mp.release();
            mp = null;
        }
    }

    @SuppressLint("ViewHolder")
    private class MessageListAdapter extends BaseAdapter {

        private List<BaseMapObject> items;
        private Context context;
        private ListView adapterList;

        public MessageListAdapter(Context context, List<BaseMapObject> items, ListView adapterList) {
            this.context = context;
            this.items = items;
            this.adapterList = adapterList;
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public Object getItem(int position) {
            return items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @SuppressLint("ViewHolder")
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final BaseMapObject message = items.get(position);

            View talkView = null;// LayoutInflater.from(ChatActivity.this).inflate(R.layout.child,
            // null);

            switch (Integer.parseInt(message.get("sendtype").toString())) {
                case AbsMsgRecorderActivity.RECVFROM:
                    talkView = LayoutInflater.from(mContext).inflate(R.layout.incometalk, null);
                    break;
                case AbsMsgRecorderActivity.SENDTO:
                    talkView = LayoutInflater.from(mContext).inflate(R.layout.outcometalk, null);
                    break;
                case AbsMsgRecorderActivity.SENDERROR:
                    talkView = LayoutInflater.from(mContext).inflate(R.layout.outcometalk, null);
                    break;
                case AbsMsgRecorderActivity.SENDNOW:
                    talkView = LayoutInflater.from(mContext).inflate(R.layout.outcometalk, null);
                    talkView.findViewById(R.id.progressBar_sendmsg).setVisibility(View.VISIBLE);
                    break;
            }

            TextView text = (TextView) talkView.findViewById(R.id.textcontent);
            ImageView img = (ImageView) talkView.findViewById(R.id.imageView_fail);
            CheckBox checkDel = (CheckBox) talkView.findViewById(R.id.check_del);
            if (message.get("exp1") != null && message.get("exp1").toString().equals("0")) {
                checkDel.setVisibility(View.VISIBLE);
            } else {
                checkDel.setVisibility(View.GONE);
            }
            checkDel.setOnCheckedChangeListener(new OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        message.put("exp2", "1");
                    } else {
                        message.put("exp2", null);
                    }
                }
            });

            if (img != null && message.get("sendtype").toString().equals(AbsMsgRecorderActivity.SENDERROR + "")) {
                img.setVisibility(View.VISIBLE);
            } else if (img != null) {
                img.setVisibility(View.GONE);
            }

            if (message.get("msgcontent") == null || message.get("msgcontent").equals("null")) {
                text.setText("无效信息");
            } else {
                if (message.get("msgtype").toString().equals("0")) {

                    String msgshow = message.get("msgcontent").toString() + "\r\n" + UnixTime.unixTime2Simplese(message.get("creattime").toString(), "MM-dd HH:mm");
                    String num = message.get("number").toString();
                    if (num.indexOf("@") != -1) {
                        msgshow = num.split("@")[2] + ":\r\n" + msgshow;
                    }
                    text.setText(msgshow);
                } else if (message.get("msgtype").toString().equals("1")) {
                    text.setText("(((" + "\r\n" + UnixTime.unixTime2Simplese(message.get("creattime").toString(), "MM-dd HH:mm"));

                    text.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            try {
                                stopMediaplayer();

                                mp = new MediaPlayer();
                                mp.setDataSource(message.get("msgcontent").toString());
                                mp.prepare();
                                mp.start();

                            } catch (Exception e) {
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
