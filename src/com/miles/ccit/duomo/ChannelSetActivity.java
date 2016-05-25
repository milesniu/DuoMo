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
import com.miles.ccit.util.ByteUtil;
import com.miles.ccit.util.SendDataTask;
import com.miles.ccit.util.SendNetData;

public class ChannelSetActivity extends AbsBaseActivity {

    private Spinner sp_weixing, sp_duanbo, sp_cduanbo, sp_mobile, sp_wired;

    private String[] array = new String[]{"最低", "低", "高", "次高", "最高"};
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
            case R.id.bt_right:
                new setChannel().execute(APICode.SEND_RECV_ChannelCfg + "", sp_weixing.getSelectedItemPosition() + "", sp_duanbo.getSelectedItemPosition() + "", sp_cduanbo.getSelectedItemPosition() + "", sp_mobile.getSelectedItemPosition() + "", sp_wired.getSelectedItemPosition() + "");
                break;
        }
    }

    private class setChannel extends SendDataTask {
        @Override
        protected void onPostExecute(byte[] result) {
            super.onPostExecute(result);
            hideProgressDlg();
            finish();
        }
    }


    @Override
    public void initView() {
        initBaseView("优先级设置");
        Btn_Left.setOnClickListener(this);
        Btn_Right.setOnClickListener(this);
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
            hideProgressDlg();
            String action = intent.getAction();
            if (action.equals(broad_query_channel)) {
                byte[] data = intent.getByteArrayExtra("data");

                int weixing = ByteUtil.oneByte2oneInt(data[5]);
                int duanbo = ByteUtil.oneByte2oneInt(data[6]);
                int cduanbo = ByteUtil.oneByte2oneInt(data[7]);
                int mobile = ByteUtil.oneByte2oneInt(data[8]);
                int wired = ByteUtil.oneByte2oneInt(data[9]);

                sp_weixing.setSelection(weixing);
                sp_duanbo.setSelection(duanbo);
                sp_cduanbo.setSelection(cduanbo);
                sp_mobile.setSelection(mobile);
                sp_wired.setSelection(wired);

            }

        }

    }


}
