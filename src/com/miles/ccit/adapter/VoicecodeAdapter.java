package com.miles.ccit.adapter;

import java.util.List;
import java.util.Map;

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
import com.miles.ccit.util.AbsToCallActivity;
import com.miles.ccit.util.BaseMapObject;
import com.miles.ccit.util.UnixTime;

public class VoicecodeAdapter extends BaseAdapter {

    private List<BaseMapObject> contactlist;
    private List<BaseMapObject> clist;
    private Context mContext;

    public VoicecodeAdapter(Context contex, List<BaseMapObject> list, List<BaseMapObject> c) {
        this.mContext = contex;
        this.contactlist = list;
        this.clist = c;
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


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final BaseMapObject map = contactlist.get(position);
        View view = null;

        if (clist != null && map.get("number") != null) {
            for (int i = 0; i < clist.size(); i++) {
                if (clist.get(i).get("number").toString().indexOf(map.get("number").toString()) != -1) {
                    map.put("name", clist.get(i).get("name").toString());
                }
            }
        }


        LayoutInflater mInflater = LayoutInflater.from(mContext);
        view = mInflater.inflate(R.layout.listitem_contact, null);
        ((TextView) view.findViewById(R.id.text_name)).setText(map.get("name") == null ? map.get("number").toString() : map.get("name").toString());
        ((TextView) view.findViewById(R.id.text_number)).setText(UnixTime.unixTime2Simplese(map.get("creattime").toString(), "yyyy-MM-dd HH:mm:ss"));
        ImageView img = (ImageView) view.findViewById(R.id.image_head);
        if (map.get("status").toString().equals(AbsToCallActivity.Send_Call)) {
            img.setImageResource(R.drawable.outputphone);
        } else if (map.get("status").toString().equals(AbsToCallActivity.Recv_Call)) {
            img.setImageResource(R.drawable.incomephone);
        } else if (map.get("status").toString().equals(AbsToCallActivity.Recv_Error)) {
            img.setImageResource(R.drawable.incomephoneerror);
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
