package com.miles.ccit.duomo;

import android.os.Bundle;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.miles.ccit.database.GetData4DB;
import com.miles.ccit.util.AbsMsgRecorderActivity;
import com.miles.ccit.util.MutiChoiseDlg;
import com.miles.ccit.util.MyLog;
import com.miles.ccit.util.O;

public class CreatShortmsgActivity extends AbsMsgRecorderActivity {
    private EditText edit_inputContact;
    private Button Btn_addContact;
    private LinearLayout linearLayout_Groupname;

    private int nettype = -1;
    private int type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creat_shortmsg);
        nettype = getIntent().getIntExtra("nettype", -1);
        type = getIntent().getIntExtra("type", -1);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.creat_shortmsg, menu);
        return true;
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.bt_left:
                this.finish();
                break;
            case R.id.bt_addcontact:
                new MutiChoiseDlg(mContext, GetData4DB.getObjectListData(mContext, "contact", "type", "0")).getDlg(edit_inputContact);
                break;
            case R.id.bt_swicthvoice:
                switchVoice();
                break;
            case R.id.bt_send:
                if (!checkContactNum(edit_inputContact.getText().toString())) {
                    MyLog.showToast(mContext, "输入号码有误,请检查。");
                    return;
                }
                if (edit_inputContact.getText().toString().equals("")) {
                    return;
                }
                setStrContatc(edit_inputContact.getText().toString());
                sendTextmsg(edit_inputContact.getText().toString(),type);
                this.finish();
                break;
        }
    }

    public boolean checkContactNum(String contact) {
        for (int i = 0; i < contact.length(); i++) {
            char c = contact.charAt(i);
            if (c > 60) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void initView() {
        // TODO Auto-generated method stub
        initBaseView("新建消息");
        // Btn_Left.setText("返回");
        Btn_Right.setVisibility(View.INVISIBLE);

        edit_inputContact = (EditText) findViewById(R.id.edit_concotact);
        edit_inputMsg = (EditText) findViewById(R.id.edit_inputmsg);
        Btn_switchVoice = (Button) findViewById(R.id.bt_swicthvoice);
        Btn_addContact = (Button) findViewById(R.id.bt_addcontact);
        linearLayout_Groupname = (LinearLayout) findViewById(R.id.linear_groupname);
        Btn_Send = (Button) findViewById(R.id.bt_send);
        Btn_Talk = (Button) findViewById(R.id.bt_talk);
        Btn_addContact.setOnClickListener(this);
        Btn_switchVoice.setOnClickListener(this);
        Btn_Send.setOnClickListener(this);
        Btn_Talk.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        talkTouchDown(edit_inputContact.getText().toString());
                        break;
                    case MotionEvent.ACTION_UP:
                        if (edit_inputContact.getText().toString().equals("")) {
                            return false;
                        }
                        talkTouchUp(event);
                        CreatShortmsgActivity.this.finish();
                        break;
                }
                return false;
            }
        });
        if (nettype == 1) {
            linearLayout_Groupname.setVisibility(View.VISIBLE);
        } else {
            linearLayout_Groupname.setVisibility(View.GONE);
        }
    }

}
