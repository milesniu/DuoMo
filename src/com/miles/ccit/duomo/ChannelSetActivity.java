package com.miles.ccit.duomo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Spinner;

import com.miles.ccit.adapter.MySpinnerAdapter;
import com.miles.ccit.net.APICode;
import com.miles.ccit.util.AbsBaseActivity;
import com.miles.ccit.util.SendDataTask;

public class ChannelSetActivity extends AbsBaseActivity {

    private Spinner sp_weixing, sp_duanbo, sp_cduanbo, sp_mobile, sp_wired;

    private String[] array = new String[]{"最低", "底", "高", "次高", "最高"};
    private MySpinnerAdapter adapter;
    private MyBroadcastReciver broad = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channelset);
        adapter = new MySpinnerAdapter(ChannelSetActivity.this, array);
        showprogressdialog();
        new SendDataTask().execute(APICode.SEND_QueryChannel + "");
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_left:
                this.finish();
                break;
        }
    }

    @Override
    public void initView() {
        initBaseView("优先级设置");
        Btn_Left.setOnClickListener(this);
        Btn_Right.setVisibility(View.VISIBLE);
        Btn_Right.setBackgroundResource(R.drawable.btsure);
        sp_weixing = (Spinner) findViewById(R.id.sp_wx);
        sp_duanbo = (Spinner) findViewById(R.id.sp_db);
        sp_cduanbo = (Spinner) findViewById(R.id.sp_cdb);
        sp_mobile = (Spinner) findViewById(R.id.sp_mobile);
        sp_wired = (Spinner) findViewById(R.id.sp_wired);

        sp_weixing.setAdapter(adapter);
        sp_duanbo.setAdapter(adapter);
        sp_cduanbo.setAdapter(adapter);
        sp_mobile.setAdapter(adapter);
        sp_wired.setAdapter(adapter);


        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(broad_query_channel);
        broad = new MyBroadcastReciver();
        this.registerReceiver(broad, intentFilter);
    }

    public class MyBroadcastReciver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {


        }

    }


}
