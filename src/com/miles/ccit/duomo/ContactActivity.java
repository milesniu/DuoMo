package com.miles.ccit.duomo;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnCreateContextMenuListener;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.miles.ccit.adapter.ContactAdapter;
import com.miles.ccit.database.GetData4DB;
import com.miles.ccit.database.UserDatabase;
import com.miles.ccit.duomo.R;
import com.miles.ccit.util.AbsBaseActivity;
import com.miles.ccit.util.BaseMapObject;
import com.miles.ccit.util.OverAllData;

public class ContactActivity extends AbsBaseActivity {

    List<BaseMapObject> wireness = new Vector<BaseMapObject>();
    List<BaseMapObject> wired = new Vector<BaseMapObject>();
    List<BaseMapObject> net = new Vector<BaseMapObject>();


    private static final int WIRENESS = 0;
    private static final int WIRED = 1;
    private static final int NET = 2;

    private ContactAdapter adapter;

    private ListView list_Content;
    private int isWireness = WIRENESS;


    private ImageView img_wuxian;
    private ImageView img_youxian;
    private ImageView img_wangluo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.contact, menu);
        return true;
    }


    @Override
    public void initView() {
        // TODO Auto-generated method stub
        initBaseView("通讯录");
//		Btn_Left.setText("返回");
//		Btn_Right.setText("新建");
        Btn_Right.setBackgroundResource(R.drawable.btaddcontact);
        Btn_Left.setOnClickListener(this);
        Btn_Right.setOnClickListener(this);
        Btn_Delete = (Button) findViewById(R.id.bt_sure);
        Btn_Canle = (Button) findViewById(R.id.bt_canle);

        img_youxian = (ImageView) findViewById(R.id.img_youxian);
        img_wuxian = (ImageView) findViewById(R.id.img_wuxian);
        img_wangluo = (ImageView) findViewById(R.id.img_wangluo);


        Btn_Delete.setOnClickListener(this);
        Btn_Canle.setOnClickListener(this);
        findViewById(R.id.linear_wuxian).setOnClickListener(this);
        findViewById(R.id.linear_youxian).setOnClickListener(this);
        findViewById(R.id.linear_wangluo).setOnClickListener(this);
        list_Content = (ListView) findViewById(R.id.listView_content);
        linear_Del = (LinearLayout) findViewById(R.id.linear_del);
        //添加数据进listview
        List<BaseMapObject> contactList = GetData4DB.getObjectListData(mContext, "contact");
        wireness.clear();
        wired.clear();
        net.clear();
        for (BaseMapObject item : contactList) {
            if (item.get("type").toString().equals("0")) {
                wireness.add(item);
            } else if (item.get("type").toString().equals("1")) {
                wired.add(item);
            } else if (item.get("type").toString().equals("2")) {
                net.add(item);
            }

        }
        refreshList(WIRENESS);
        list_Content.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                // TODO Auto-generated method stub
                startActivity(new Intent(mContext, CreatContactActivity.class).putExtra("contact", getCurrentList().get(arg2)));

            }
        });
        list_Content.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {

            @Override
            public void onCreateContextMenu(ContextMenu menu, View v,
                                            ContextMenuInfo menuInfo) {
                // TODO Auto-generated method stub
                menu.setHeaderTitle("联系人");
                menu.add(0, 0, 0, "删除该联系人");
                menu.add(0, 1, 1, "批量删除");
                menu.add(0, 2, 2, "修改联系人");
                menu.add(0, 3, 3, "取消");
            }
        });

    }

    private List<BaseMapObject> getCurrentList() {
        switch (isWireness) {
            case WIRENESS:
                return wireness;
            case WIRED:
                return wired;
            case NET:
                return net;
        }
        return null;
    }

    private void refreshTabStatus(int index) {
        switch (index) {
            case WIRENESS:
                img_wuxian.setImageResource(R.drawable.bt_wuxian_h);
                img_youxian.setImageResource(R.drawable.bt_youxian_n);
                img_wangluo.setImageResource(R.drawable.bt_wangluo_n);
                break;
            case WIRED:
                img_wuxian.setImageResource(R.drawable.bt_wuxian_n);
                img_youxian.setImageResource(R.drawable.bt_youxian_h);
                img_wangluo.setImageResource(R.drawable.bt_wangluo_n);
                break;
            case NET:
                img_wuxian.setImageResource(R.drawable.bt_wuxian_n);
                img_youxian.setImageResource(R.drawable.bt_youxian_n);
                img_wangluo.setImageResource(R.drawable.bt_wangluo_h);
                break;

        }
    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
        int ListItem = (int) info.position;
        switch (item.getItemId()) {
            case 0:
//			BaseMapObject selectItem =;
                confirmDlg("删除联系人", "contact", getCurrentList().get(ListItem), getCurrentList(), adapter);
                break;
            case 1:
                for (BaseMapObject tmp : getCurrentList()) {
                    tmp.put("exp1", "0");
                }
                adapter.notifyDataSetChanged();
                linear_Del.setVisibility(View.VISIBLE);
                break;
            case 2:
                startActivity(new Intent(mContext, CreatContactActivity.class).putExtra("contact", getCurrentList().get(ListItem)));
                break;
            case 3:
                break;
        }
        return super.onContextItemSelected(item);
    }


    private void refreshList(int type) {
        switch (type) {
            case WIRENESS:
                if (wireness == null || wireness.size() < 1) {
                    showEmpty();

                } else {
                    hideEmpty();

                }
                adapter = new ContactAdapter(mContext, wireness, "name", "name", "number");
                list_Content.setAdapter(adapter);
                break;
            case WIRED:
                if (wired == null || wired.size() < 1) {
                    showEmpty();
                } else {
                    hideEmpty();

                }
                adapter = new ContactAdapter(mContext, wired, "name", "name", "number");
                list_Content.setAdapter(adapter);
                break;
            case NET:
                if (net == null || net.size() < 1) {
                    showEmpty();
                } else {
                    hideEmpty();

                }
                adapter = new ContactAdapter(mContext, net, "name", "name", "number");
                list_Content.setAdapter(adapter);
                break;
        }
        isWireness = type;
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.bt_left:
                this.finish();
                break;
            case R.id.bt_right:
                startActivity(new Intent(this, CreatContactActivity.class));
                break;
            case R.id.linear_wuxian:
                refreshList(WIRENESS);
                refreshTabStatus(WIRENESS);
                break;
            case R.id.linear_youxian:
                refreshList(WIRED);

                refreshTabStatus(WIRED);
                break;
            case R.id.linear_wangluo:
                refreshList(NET);

                refreshTabStatus(NET);
                break;
            case R.id.bt_sure:
                confirmDlg("删除联系人", "contact", null, getCurrentList(), adapter);
                break;
            case R.id.bt_canle:
                for (BaseMapObject tmp : getCurrentList()) {
                    tmp.put("exp1", null);
                    tmp.put("exp2", null);
                }
                linear_Del.setVisibility(View.GONE);
                break;
        }
    }

}
