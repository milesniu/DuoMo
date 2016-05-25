package com.miles.ccit.duomo;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
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
import com.miles.ccit.util.AbsBaseActivity;
import com.miles.ccit.util.AbsBaseFragment;
import com.miles.ccit.util.BaseMapObject;
import com.miles.ccit.util.MyLog;
import com.miles.ccit.util.O;

import java.util.Collections;
import java.util.List;
import java.util.Vector;

public class NetShortMsgFragment extends AbsBaseFragment {

    private MsgorMailSetAdapter adapter;
    private MyBroadcastReciver broad = null;
    private List<BaseMapObject> msgList = new Vector<BaseMapObject>();
    private List<BaseMapObject> contactList = new Vector<BaseMapObject>();

    public Button Btn_Delete;
    public Button Btn_Canle;
    public static boolean isTop = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shortmsg, null);
        initView(view);
        contactList = GetData4DB.getObjectListData(getActivity(), "contact");
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(AbsBaseActivity.broad_recvtextmsg_Action);
        broad = new MyBroadcastReciver();
        getActivity().registerReceiver(broad, intentFilter);
        return view;
    }


    @Override
    public void onResume() {
        isTop = true;
        refreshList();
        super.onResume();
    }


    @Override
    public void onDestroy() {
        getActivity().unregisterReceiver(broad);
        super.onDestroy();
        isTop = false;
    }


    @Override
    public void onPause() {
        super.onPause();
        isTop = false;
    }


    @Override
    public void onStop() {
        super.onStop();
        isTop = false;
    }


    public class MyBroadcastReciver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
//			hideProgressDlg();
            String action = intent.getAction();

            if (action.equals(AbsBaseActivity.broad_recvtextmsg_Action)) {
                refreshList();
            }
        }

    }


    private void refreshList() {
        /**
         * exp1 用作删除状态变更
         * exp2 用作区分网络模式和专网模式
         * */

        msgList = GetData4DB.getObjecSet(getActivity(), "shortmsg", "contact", "number", "number", new String[]{"exp2"}, new String[]{"2"}, "=");

        Collections.reverse(msgList);

        if (msgList == null || msgList.size() < 1) {
            showEmpty();
//			Toast.makeText(getActivity(), "暂无消息记录...", 0).show();
            return;
        }
        hideEmpty();
        adapter = new MsgorMailSetAdapter(getActivity(), msgList, contactList,"shortmsg");
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                getActivity().startActivity(new Intent(getActivity(), ShortmsgListActivity.class).putExtra("item", msgList.get(arg2)).putExtra("type", O.NET));

            }
        });


        listview.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {

            @Override
            public void onCreateContextMenu(ContextMenu menu, View v,
                                            ContextMenuInfo menuInfo) {
                menu.setHeaderTitle("短消息");
                menu.add(0, 0, 0, "删除该短信组");
                menu.add(0, 1, 1, "批量删除短信组");
                menu.add(0, 3, 3, "取消");
            }
        });
    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
        int ListItem = (int) info.position;
        switch (item.getItemId()) {
            case 0:
                confirmDlg(false, "删除记录", "shortmsg", "number", msgList.get(ListItem), msgList, adapter);
                break;
            case 1:
                for (BaseMapObject tmp : msgList) {
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
    public void initView(View view) {
        // TODO Auto-generated method stub
        initBaseView(view, "短消息");
        Btn_Right.setBackgroundResource(R.drawable.creatmsg);
        linear_Del = (LinearLayout) view.findViewById(R.id.linear_del);
        Btn_Delete = (Button) view.findViewById(R.id.bt_sure);
        Btn_Canle = (Button) view.findViewById(R.id.bt_canle);
        Btn_Delete.setOnClickListener(this);
        Btn_Canle.setOnClickListener(this);
        refreshList();
    }


    public void selectArray() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setIcon(R.drawable.ic_launcher);
        builder.setTitle("新建方式");
        //    指定下拉列表的显示数据
        final String[] cities = {"单聊", "群聊"};
        //    设置一个下拉的列表选择项
        builder.setItems(cities, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        startActivity(new Intent(getActivity(), CreatShortmsgActivity.class).putExtra("nettype", 0).putExtra("type", O.NET));
                        break;
                    case 1:
                        startActivity(new Intent(getActivity(), CreatShortmsgActivity.class).putExtra("nettype", 1).putExtra("type", O.NET));
                        break;
                }
            }
        });
        builder.show();

    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.bt_left:
                getActivity().finish();
                break;
            case R.id.bt_right:
                if (LoginActivity.isLogin) {
//                    selectArray();
                    startActivity(new Intent(getActivity(), CreatShortmsgActivity.class).putExtra("nettype", 0).putExtra("type", O.NET));

                } else {
                    MyLog.showToast(getActivity(), "请登录后再执行该操作。");
                }
                break;
            case R.id.bt_sure:
                confirmDlg(false, "删除记录", "shortmsg", "number", null, msgList, adapter);

                break;
            case R.id.bt_canle:
                for (BaseMapObject tmp : msgList) {
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
