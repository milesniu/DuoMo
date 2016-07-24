package com.miles.ccit.adapter;

import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.miles.ccit.duomo.R;
import com.miles.ccit.util.AbsCreatActivity;
import com.miles.ccit.util.AbsToCallActivity;
import com.miles.ccit.util.BaseMapObject;
import com.miles.ccit.util.UnixTime;

public class WiredModelAdapter extends BaseAdapter {

    private List<BaseMapObject> contactlist;
    private Context mContext;

    public WiredModelAdapter(Context contex, List<BaseMapObject> list) {
        this.mContext = contex;
        this.contactlist = list;
    }

    @Override
    public int getCount() {
        return contactlist.size();
    }

    @Override
    public Object getItem(int position) {
        return contactlist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }


    @SuppressLint("ViewHolder")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final BaseMapObject map = contactlist.get(position);
        View view = null;

        LayoutInflater mInflater = LayoutInflater.from(mContext);
        view = mInflater.inflate(R.layout.listitem_contact, null);
        ((TextView) view.findViewById(R.id.text_name)).setText(map.get("name") == null ? map.get("number").toString() : map.get("name").toString());
        ((TextView) view.findViewById(R.id.text_number)).setText(UnixTime.unixTime2Simplese(map.get("creattime").toString(), "yyyy-MM-dd HH:mm:ss") + "\r\n" + map.get("filepath").toString());
        ImageView img = (ImageView) view.findViewById(R.id.image_head);
        if (map.get("sendtype").toString().equals("0"))        //语音
        {
            if (map.get("status").toString().equals(AbsToCallActivity.Send_Call)) {
                img.setImageResource(R.drawable.outputphone);
            } else if (map.get("status").toString().equals(AbsToCallActivity.Recv_Call)) {
                img.setImageResource(R.drawable.incomephone);
            } else if (map.get("status").toString().equals(AbsToCallActivity.Recv_Error)) {
                img.setImageResource(R.drawable.incomephoneerror);
            }
        } else if (map.get("sendtype").toString().equals("1"))    //文件
        {
            if (map.get("status").toString().equals(AbsCreatActivity.SENDSUCCESS + "")) {
                img.setImageResource(R.drawable.outfile);
            } else if (map.get("status").toString().equals(AbsCreatActivity.RECVFROM + "")) {
                img.setImageResource(R.drawable.infile);
            } else if (map.get("status").toString().equals(AbsCreatActivity.SENDERROR + "")) {
                img.setImageResource(R.drawable.outfilefaild);
            }
        }
        CheckBox checkDel = (CheckBox) view.findViewById(R.id.check_del);
        if (map.get("exp1") != null && map.get("exp1").toString().equals("0")) {
            checkDel.setVisibility(View.VISIBLE);
        } else {
            checkDel.setVisibility(View.INVISIBLE);
        }
        checkDel.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    map.put("exp2", "1");
                } else {
                    map.put("exp2", null);
                }
            }
        });
        return view;
    }

}
