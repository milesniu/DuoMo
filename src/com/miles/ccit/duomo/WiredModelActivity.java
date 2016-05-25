package com.miles.ccit.duomo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.miles.ccit.adapter.WiredModelAdapter;
import com.miles.ccit.database.GetData4DB;
import com.miles.ccit.net.APICode;
import com.miles.ccit.util.AbsBaseActivity;
import com.miles.ccit.util.AbsCreatActivity;
import com.miles.ccit.util.AbsToCallActivity;
import com.miles.ccit.util.BaseMapObject;
import com.miles.ccit.util.FileUtils;
import com.miles.ccit.util.MyLog;
import com.miles.ccit.util.SendDataTask;

import java.util.Collections;
import java.util.List;
import java.util.Vector;

public class WiredModelActivity extends AbsBaseActivity {

    private ListView list_Content;
    private List<BaseMapObject> wiredlist = new Vector<BaseMapObject>();
    private WiredModelAdapter adapter;
    private String selectCode = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wired_model);

    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        new SendDataTask().execute(APICode.SEND_BackModel + "");
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.wired_model, menu);
        return true;
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.bt_left:
                this.finish();
                break;
            case R.id.bt_right:
                if (LoginActivity.isLogin) {
                    selectType(null);
                } else {
                    MyLog.showToast(mContext, "请登录后再执行该操作。");
                }
                break;

            case R.id.bt_sure:
                confirmDlg("删除记录", "wiredrecord", null, wiredlist, adapter);

                break;
            case R.id.bt_canle:
                for (BaseMapObject tmp : wiredlist) {
                    tmp.put("exp1", null);
                    tmp.put("exp2", null);
                }
                linear_Del.setVisibility(View.GONE);
                break;
            case R.id.btn_recvfile:
                startActivity(new Intent(mContext, FileStatusActivity.class));
                break;
        }
    }

    private void refreshList() {
        Collections.reverse(wiredlist);
        if (wiredlist == null || wiredlist.size() < 1) {
            showEmpty();
            return;
        } else {
            hideEmpty();
            adapter = new WiredModelAdapter(mContext, wiredlist);
            list_Content.setAdapter(adapter);
        }
        list_Content.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {

            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
                // TODO Auto-generated method stub
                menu.setHeaderTitle("有线模式");
                menu.add(0, 0, 0, "删除该条记录");
                menu.add(0, 1, 1, "批量删除");
                menu.add(0, 2, 2, "取消");
            }
        });
        list_Content.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                // TODO Auto-generated method stub
                if (wiredlist.get(arg2).get("sendtype").toString().equals("1")) {
                    String path = wiredlist.get(arg2).get("filepath").toString();
                    AbsCreatActivity.showFile(mContext, AbsCreatActivity.getFileName(path), path);
                } else {
                    selectCode = wiredlist.get(arg2).get("number").toString();
                    selectType(selectCode);
//                    AbsToCallActivity.CurrentType = AbsToCallActivity.TOCALLWIREDVOICE;
//                    AbsToCallActivity.insertWiredRecord(mContext, wiredlist.get(arg2).get("number").toString(), null);
                }

            }
        });
    }

    public void selectType(final String number) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setIcon(R.drawable.ic_launcher);
        builder.setTitle("选择拨打方式");
        //    指定下拉列表的显示数据
        final String[] cities = {"有线语音", "有线文件"};
        //    设置一个下拉的列表选择项
        builder.setItems(cities, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    AbsToCallActivity.CurrentType = AbsToCallActivity.TOCALLWIREDVOICE;
                    if (number == null) {
                        startActivity(new Intent(mContext, CreatWiredActivity.class));
                    } else {
                        AbsToCallActivity.insertWiredRecord(mContext, number, null);
                    }
                } else if (which == 1) {
                    AbsToCallActivity.CurrentType = AbsToCallActivity.TOCALLWIREDFILE;
                    if (number == null) {
                        startActivity(new Intent(mContext, CreatWiredActivity.class));
                    } else {
//                        Toast.makeText(mContext, "选择文件", Toast.LENGTH_SHORT).show();
//                        AbsToCallActivity.insertWiredRecord(mContext, number, null);
                        selectFile();
                    }

                }

            }
        });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 0:
                if (resultCode == RESULT_OK) {
                    // Get the Uri of the selected file
                    Uri uri = data.getData();
                    String path = FileUtils.getPath(this, uri);
//                    String name = AbsCreatActivity.getFileName(path);
                    startActivity(new Intent(mContext, FileStatusActivity.class).putExtra("path", path).putExtra("code", selectCode));
                    MyLog.showToast(mContext, path);
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
        int ListItem = (int) info.position;
        switch (item.getItemId()) {
            case 0:
                confirmDlg("删除记录", "wiredrecord", wiredlist.get(ListItem), wiredlist, adapter);
                break;
            case 1:
                for (BaseMapObject tmp : wiredlist) {
                    tmp.put("exp1", "0");
                }
                adapter.notifyDataSetChanged();
                linear_Del.setVisibility(View.VISIBLE);
                break;
            case 2:
                break;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public void initView() {
        // TODO Auto-generated method stub
        initBaseView("有线模式");
        Btn_Right.setBackgroundResource(R.drawable.creatcall);
        Btn_Left.setOnClickListener(this);
        Btn_Right.setOnClickListener(this);
        Btn_Delete = (Button) findViewById(R.id.bt_sure);
        Btn_Canle = (Button) findViewById(R.id.bt_canle);
        Btn_Delete.setOnClickListener(this);
        Btn_Canle.setOnClickListener(this);
        findViewById(R.id.btn_recvfile).setOnClickListener(this);
        list_Content = (ListView) findViewById(R.id.listView_content);
        linear_Del = (LinearLayout) findViewById(R.id.linear_del);
        wiredlist.clear();
        wiredlist = GetData4DB.getObjList4LeftJoin(mContext, "wiredrecord", "contact", "number");
        refreshList();
    }

}
