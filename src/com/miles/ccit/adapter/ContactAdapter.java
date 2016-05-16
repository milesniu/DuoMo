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
import com.miles.ccit.util.BaseMapObject;
import com.miles.ccit.util.UnixTime;

public class ContactAdapter extends BaseAdapter {

    private List<BaseMapObject> contactlist;
    private Context mContext;
    private String showtitle;
    private String showinfo;
    private String showretitle;

    public ContactAdapter(Context contex, List<BaseMapObject> list, String title, String retitle, String info) {
        this.mContext = contex;
        this.contactlist = list;
        this.showtitle = title;
        this.showinfo = info;
        this.showretitle = retitle;
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

        LayoutInflater mInflater = LayoutInflater.from(mContext);
        view = mInflater.inflate(R.layout.listitem_contact, null);
        ((TextView) view.findViewById(R.id.text_name)).setText(map.get(showtitle) == null ? map.get(showretitle).toString() : map.get(showtitle).toString());

        String numbers = null;
        if (showinfo.equals("number"))       //通讯录号码显示
        {
            numbers = map.get(showinfo).toString();
        } else if (showinfo.equals("creattime")) {
            numbers = UnixTime.unixTime2Simplese(map.get(showinfo).toString(), "yyyy-MM-dd HH:mm:ss");
        }

        ((TextView) view.findViewById(R.id.text_number)).setText(numbers);
        ImageView img = (ImageView) view.findViewById(R.id.image_head);
        if (map.get("type").toString().equals("0")) {
            img.setImageResource(R.drawable.bt_wuxian_h);
        } else if (map.get("type").toString().equals("1")) {
            img.setImageResource(R.drawable.bt_youxian_h);
        } else if (map.get("type").toString().equals("2")) {
            img.setImageResource(R.drawable.bt_wangluo_h);
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
