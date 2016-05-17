package com.miles.ccit.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.miles.ccit.database.GetData4DB;
import com.miles.ccit.database.UserDatabase;
import com.miles.ccit.duomo.R;

public abstract class AbsBaseActivity extends Activity implements OnClickListener {

    public Context mContext = this;
    public View LayoutTitle;
    public Button Btn_Left;
    public Button Btn_Right;
    public LinearLayout linear_Select;
    public TextView text_left;
    public TextView text_right;
    public Button Btn_Delete;
    public Button Btn_Canle;
    public LinearLayout linear_Del;


    public static final int RECVFROM = 1;
    public static final int SENDTO = 2;
    public static final int SENDERROR = 3;
    public static final int SENDSUCCESS = 2;
    public static final int SENDNOW = 4;

    public static final String broad_login_Action = "cn.broadcast.login";
    public static final String broad_recvtextmsg_Action = "cn.broadcast.recvtextmsg";
    public static final String broad_Email_Action = "cn.broadcast.recvemail";
    public static final String broad_recvback_Action = "cn.broadcast.recvbackmsg";
    public static final String broad_recvvoicecode_Action = "cn.broadcast.recvvoicecode";
    public static final String broad_broadcast_Action = "cn.broadcast.broadcast";
    public static final String broad_specialvoice_Action = "cn.broadcast.specialvoice";
    public static final String broad_backchangepwd_Action = "cn.broadcast.backchangepwd";
    public static final String broad_wiredvoice_Action = "cn.broadcast.wiredvoice";
    public static final String broad_interaput_Action = "cn.broadcast.interaput";
    public static final String broad_fileprogress_Action = "cn.broadcast.fileprogress";
    public static final String broad_filefinish_Action = "cn.broadcast.filefinish";
    public static final String broad_fileresult_Action = "cn.broadcast.fileresult";
    public static final String broad_usimout_Action = "cn.broadcast.usimout";
    public static final String broad_broadcastresult_Action = "cn.broadcast.broadcastresult";
    public static final String broad_recvcodedirc_Action = "cn.broadcast.recvcodedirc";
    public static final String broad_backemailresult_Action = "cn.broadcast.backemailresult";
    public static final String broad_backlocation_Action = "cn.broadcast.backlocation";
    public static final String broad_debug_info = "cn.broadcast.debug.info";
    public static final String broad_config_host = "cn.broadcast.host.config";
    public static final String broad_query_channel = "cn.broadcast.query.channel";


    public abstract void initView();

    public ImageView img_Empty;
    public ProgressDialog pdialog;
    public static String title = "多模系统";
    public static String message = "正在努力传输数据···";

    public void initBaseView(String titlename) {
        LayoutTitle = (View) findViewById(R.id.include_layout);
        if (LayoutTitle.findViewById(R.id.title_text) != null) {
            ((TextView) LayoutTitle.findViewById(R.id.title_text)).setText(titlename);
        }
        Btn_Left = (Button) LayoutTitle.findViewById(R.id.bt_left);
        Btn_Right = (Button) LayoutTitle.findViewById(R.id.bt_right);
        Btn_Left.setOnClickListener(this);
        Btn_Right.setOnClickListener(this);
        img_Empty = (ImageView) findViewById(R.id.image_empty);

    }

    public static String getassetsCode(Context conte, String filename) {
        try {
            InputStreamReader inputReader = new InputStreamReader(conte.getResources().getAssets().open(filename));
            BufferedReader bufReader = new BufferedReader(inputReader);
            String line = "";
            String Result = "";
            while ((line = bufReader.readLine()) != null)
                Result += line;
            return Result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void delList(List<BaseMapObject> list, String table, BaseAdapter adapter) {
        Iterator<BaseMapObject> iter = list.iterator();
        List<String> Idlist = new Vector<String>();
        while (iter.hasNext()) {
            BaseMapObject s = iter.next();
            if (s.get("exp2") != null && s.get("exp2").toString().equals("1")) {
                Idlist.add(s.get("id").toString());
                iter.remove();
            }
        }

        UserDatabase.DelListObj(mContext, table, "id", Idlist);

        for (BaseMapObject tmp : list) {
            tmp.put("exp1", null);
            tmp.put("exp2", null);
        }
        adapter.notifyDataSetChanged();
        linear_Del.setVisibility(View.GONE);
    }


    public void candel(List<BaseMapObject> list) {
        for (BaseMapObject tmp : list) {
            tmp.put("exp1", null);
            tmp.put("exp2", null);
        }
        linear_Del.setVisibility(View.GONE);
    }

    public void deloneItem(BaseMapObject oneitem, String tables, List<BaseMapObject> list, BaseAdapter adapter) {
        long ret = BaseMapObject.DelObj4DB(mContext, tables, "id", oneitem.get("id").toString());
        if (ret != -1) {
            list.remove(oneitem);
            adapter.notifyDataSetChanged();
        }
    }

    public void confirmDlg(String title, final String table, final BaseMapObject oneitem, final List<BaseMapObject> list, final BaseAdapter adapter) {
        AlertDialog.Builder builder = new Builder(mContext);
        builder.setMessage("确定删除所选信息吗？");
        builder.setTitle(title);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                if (oneitem != null) {
                    deloneItem(oneitem, table, list, adapter);
                } else {
                    delList(list, table, adapter);
                }
            }
        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                candel(list);
            }
        });
        builder.show();
    }


    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }


    public void checkContact(byte[] data) {

        HashMap<String, BaseMapObject> contacthash = GetData4DB.getObjectHashData(this, "contact", "number");

        try {
            int cursor = 5;
            int contactlenth = ByteUtil.byte2Int(new byte[]
                    {data[cursor + 1], data[cursor + 2]});

            cursor += 2;

            byte[] bytenamebyte = new byte[contactlenth];
            System.arraycopy(data, ++cursor, bytenamebyte, 0, contactlenth);
            String strname = new String(bytenamebyte, "UTF-8");

            cursor += contactlenth;

            int wiredlen = ByteUtil.byte2Int(new byte[]
                    {data[cursor], data[cursor + 1]});


            byte[] bytewiredname = new byte[wiredlen];
            System.arraycopy(data, cursor + 2, bytewiredname, 0, wiredlen);
            String strwiredname = new String(bytewiredname, "UTF-8");

            cursor += 2;

            String[] arraycon = strname.split(",");
            for (int j = 0; j <= (arraycon.length / 2); j += 4) {
                String num = arraycon[j] + "#" + arraycon[j + 2] + "#" + arraycon[j + 3];
                String name = arraycon[j + 1];
                BaseMapObject item = contacthash.get(num);
                if (item == null) {
                    // 直接添加
                    BaseMapObject contact = new BaseMapObject();
                    contact.put("id", null);
                    contact.put("name", name);
                    contact.put("number", num);
                    contact.put("type", "0");// 默认加为无线侧
                    contact.put("remarks", "");
                    contact.put("creattime", UnixTime.getStrCurrentUnixTime());
                    contact.InsertObj2DB(mContext, "contact");

                } else if (num.equals(item.get("number").toString()) && name.equals(item.get("name").toString())) {
                    // 直接返回
                } else {
                    // 更新
                    item.put("name", name);
                    item.UpdateObj2DBbyId(mContext, "contact");

                }
                MyLog.SystemOut(name);
            }

            String[] arrayWired = strwiredname.split(",");
            for (int j = 0; j <= (arrayWired.length / 2); j += 2) {
                String num = arrayWired[j];
                String name = arrayWired[j + 1];
                BaseMapObject item = contacthash.get(num);
                if (item == null) {
                    // 直接添加
                    BaseMapObject contact = new BaseMapObject();
                    contact.put("id", null);
                    contact.put("name", name);
                    contact.put("number", num);
                    contact.put("type", "1");// 添加为有线侧
                    contact.put("remarks", "");
                    contact.put("creattime", UnixTime.getStrCurrentUnixTime());
                    contact.InsertObj2DB(mContext, "contact");

                } else if (num.equals(item.get("number").toString()) && name.equals(item.get("name").toString())) {
                    // 直接返回
                } else {
                    // 更新
                    item.put("name", name);
                    item.UpdateObj2DBbyId(mContext, "contact");

                }
                MyLog.SystemOut(name);
            }

            MyLog.SystemOut(strname);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public void showEmpty() {

        if (img_Empty != null) {
            img_Empty.setVisibility(View.VISIBLE);
        }
    }

    public void hideEmpty() {

        if (img_Empty != null) {
            img_Empty.setVisibility(View.GONE);
        }
    }

    public void showprogressdialog() {
        if (pdialog == null || !pdialog.isShowing()) {
            pdialog = ProgressDialog.show(this, title, message);
            pdialog.setIcon(R.drawable.ic_launcher);
            pdialog.setCancelable(true);
        }
    }

    public void hideProgressDlg() {
        if (pdialog != null && pdialog.isShowing()) {
            pdialog.dismiss();
        }
    }

    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        initView();
        super.onStart();
    }

    public void changeSiwtchLeft() {
        linear_Select.setBackgroundResource(R.drawable.selectleft);
        text_left.setTextColor(getResources().getColor(R.color.white));
        text_right.setTextColor(getResources().getColor(R.color.black));

    }

    public void changeSiwtchRight() {
        linear_Select.setBackgroundResource(R.drawable.selectright);
        text_left.setTextColor(getResources().getColor(R.color.black));
        text_right.setTextColor(getResources().getColor(R.color.white));

    }

    public void initSwitchBaseView(String leftname, String rightname) {
        LayoutTitle = (View) findViewById(R.id.include_layout);
        linear_Select = (LinearLayout) findViewById(R.id.linear_select);
        text_left = (TextView) findViewById(R.id.text_left);
        text_right = (TextView) findViewById(R.id.text_right);
        text_left.setText(leftname);
        text_right.setText(rightname);
        Btn_Left = (Button) LayoutTitle.findViewById(R.id.bt_left);
        Btn_Right = (Button) LayoutTitle.findViewById(R.id.bt_right);
        img_Empty = (ImageView) findViewById(R.id.image_empty);
        Btn_Left.setOnClickListener(this);
        Btn_Right.setOnClickListener(this);
        text_left.setOnClickListener(this);
        text_right.setOnClickListener(this);
    }

}
