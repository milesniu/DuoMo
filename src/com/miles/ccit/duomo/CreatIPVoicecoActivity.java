package com.miles.ccit.duomo;

import android.os.Bundle;
import android.view.Menu;

import com.miles.ccit.database.GetData4DB;
import com.miles.ccit.util.AbsToCallActivity;

public class CreatIPVoicecoActivity extends AbsToCallActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_num);
        all = GetData4DB.getObjectListData(mContext, "contact", "type", "0");
        CurrentType = TOIPVOICE;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.input_num, menu);
        return true;
    }

    @Override
    public void initView() {
        // TODO Auto-generated method stub
        super.initView();
        findViewById(R.id.buttoncall).setOnClickListener(this);

    }


}
