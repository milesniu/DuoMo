package com.miles.ccit.duomo;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import com.miles.ccit.adapter.MySpinnerAdapter;
import com.miles.ccit.util.AbsCreatCodeActivity;
import com.miles.ccit.util.JSONUtil;
import com.miles.ccit.util.MyLog;
import com.miles.ccit.util.PickTimeDlg;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class CreatcodeSkyActivity extends AbsCreatCodeActivity
{

    private List<Map<String, Object>> skycode;
    private List<Map<String, Object>> skycolor;
    private Spinner sp_Code;
    private Spinner sp_Color;
    private EditText edit_time;
    private EditText edit_name;
    private List<HashMap<String, Object>> options = new Vector<HashMap<String, Object>>();
    private String contact;

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creatcode_sky);
        Map<String, Object> data = JSONUtil.getMapFromJson(getassetsCode(mContext, "junbiaocode.txt"));
        skycode = (List<Map<String, Object>>) data.get("skycode");
        skycolor = (List<Map<String, Object>>) data.get("jbcolor");
        contact = getIntent().getStringExtra("contact");
        initBaseView("空中目标");
        Btn_Left.setOnClickListener(this);
        Btn_Right.setBackgroundResource(R.drawable.sendmail);
        Btn_Right.setOnClickListener(this);
        sp_Code = (Spinner) findViewById(R.id.sp_jbnum);
        sp_Color = (Spinner) findViewById(R.id.sp_jbcolor);
        list_Content = (ListView) findViewById(R.id.list_content);
        edit_time = (EditText) findViewById(R.id.edit_time);
        edit_time.setInputType(InputType.TYPE_NULL);
        edit_time.setOnClickListener(this);
        edit_name = (EditText) findViewById(R.id.edit_name);
        sp_Code.setAdapter(new MySpinnerAdapter(mContext, skycode));
        sp_Color.setAdapter(new MySpinnerAdapter(mContext, skycolor));
        findViewById(R.id.bt_addoption).setOnClickListener(this);
//		addMore = getLayoutInflater().inflate(R.layout.addmore, null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.creatcode_sky, menu);
        return true;
    }


    @Override
    public void onClick(View v)
    {
        // TODO Auto-generated method stub
        super.onClick(v);
        switch (v.getId())
        {
            case R.id.bt_left:
                this.finish();
                break;
            case R.id.bt_right:
                if (sp_Code.getSelectedItemPosition() == 0)
                {
                    MyLog.showToast(mContext, "请选择军标编号");
                    return;
                } else if (sp_Color.getSelectedItemPosition() == 0)
                {
                    MyLog.showToast(mContext, "请选择军标颜色");
                    return;
                } else if (edit_time.getText().toString().equals(""))
                {
                    MyLog.showToast(mContext, "请选择时间戳");
                    return;
                }
                String jbname = edit_name.getText().toString();
                if (jbname.getBytes().length > 15)
                {
                    MyLog.showToast(mContext, "军标名称不能超过15个字节");
                    return;
                }
                sendCodedirc(contact, composeSendData());
                this.finish();
                break;
            case R.id.edit_time:
                new PickTimeDlg(mContext, edit_time);
                break;
            case R.id.bt_addoption:
                startActivityForResult(new Intent(mContext, CreatOptionActivity.class).putExtra("typecode", CreatOptionActivity.SKYCODE), CreatOptionActivity.SKYCODE);
                break;
        }
    }

    private String composeSendData()
    {
        String data = "P1=0000&P2=000&P3=" + skycode.get(sp_Code.getSelectedItemPosition()).get("code").toString() + (edit_name.getText().toString().equals("") ? "" : ("&P4=" + edit_name.getText().toString())) + "&P5=" + skycolor.get(sp_Color.getSelectedItemPosition()).get("code").toString() + "&P6=" + edit_time.getText().toString() + (options.size() > 0 ? ("&P7=" + options.size()) : "");
        String option = "";
        for (int i = 0; i < options.size(); i++)
        {
            HashMap<String, Object> item = options.get(i);
            double lat = Double.parseDouble(item.get("lat").toString());
            double lng = Double.parseDouble(item.get("lng").toString());
            String P72x = lng < 0 ? "1" : "0";
            String P73x = lng < 0 ? ((lng * -1) + "") : (lng + "");
            P73x = P73x.substring(0, 3) + "." + P73x.substring(4, 6) + "." + P73x.substring(6, 8) + "." + P73x.substring(8);
            String P74x = lat < 0 ? "1" : "0";
            String P75x = lat < 0 ? ((lat * -1) + "") : (lat + "");
            P75x = P75x.substring(0, 2) + "." + P75x.substring(3, 5) + "." + P75x.substring(5, 7) + "." + P75x.substring(7);
            String P76x = item.get("height").toString();
            String P77x = item.get("lane").toString();
            String P78x = item.get("speed").toString();
            String P79x = item.get("jiaci").toString();
            String P710x = item.get("bum").toString();
            String P711x = item.get("step").toString();
            option += ("&P72" + (i + 1) + "=" + P72x +
                    "&P73" + (i + 1) + "=" + P73x +
                    "&P74" + (i + 1) + "=" + P74x +
                    "&P75" + (i + 1) + "=" + P75x +
                    (P76x.equals("") ? "" : ("&P76" + (i + 1) + "=" + P76x)) +
                    (P77x.equals("") ? "" : ("&P77" + (i + 1) + "=" + P77x)) +
                    (P78x.equals("") ? "" : ("&P78" + (i + 1) + "=" + P78x)) +
                    (P79x.equals("") ? "" : ("&P79" + (i + 1) + "=" + P79x)) +
                    (P710x.equals("") ? "" : ("&P710" + (i + 1) + "=" + P710x)) +
                    (P711x.equals("") ? "" : ("&P711" + (i + 1) + "=" + P711x)));

        }
        return (data + option);
    }

    private void refreshList()
    {
        String[] array = new String[options.size()];
        for (int i = 0; i < options.size(); i++)
        {
            double lat = Double.parseDouble(options.get(i).get("lat") + "");
            double lng = Double.parseDouble(options.get(i).get("lng") + "");

            array[i] = lat + ", " + lng;
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext, R.layout.listitem_option, R.id.text_name, array);
        list_Content.setAdapter(adapter);
    }


    @SuppressWarnings("unchecked")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CreatOptionActivity.SKYCODE && data != null)
        {
            options.add((HashMap<String, Object>) data.getSerializableExtra("option"));
        }
        refreshList();
    }

    @Override
    public void initView()
    {
        // TODO Auto-generated method stub

        super.initView();


    }

    @Override
    protected void goAddOption()
    {
        // TODO Auto-generated method stub

    }

}
