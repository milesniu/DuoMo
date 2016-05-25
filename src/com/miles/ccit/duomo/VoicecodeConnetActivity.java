package com.miles.ccit.duomo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.miles.ccit.database.GetData4DB;
import com.miles.ccit.net.APICode;
import com.miles.ccit.util.AbsCreatActivity;
import com.miles.ccit.util.AbsToCallActivity;
import com.miles.ccit.util.AbsVoiceActivity;
import com.miles.ccit.util.BaseMapObject;
import com.miles.ccit.util.FileUtils;
import com.miles.ccit.util.MyLog;
import com.miles.ccit.util.O;
import com.miles.ccit.util.SendDataTask;

public class VoicecodeConnetActivity extends AbsVoiceActivity {


    private Button Btn_DisConnet;
    private Button Btn_Talk;
    private Button Btn_Speaker;
    private TextView text_Num;
    String code = "";
    private Button Btn_KeyBord;
    private LinearLayout linear_Keybord;
    private Button Btn_SendFile;
    private Button Btn_RecvFile;
    private int type;
    private String filepath = null;
    private MyBroadcastReciver broad = null;
    private LinearLayout linear_File;
    private LinearLayout linear_talk;
    MediaPlayer player;
    private boolean isspeaker = false;
//	AudioManager audioManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voicecode_connet);
        code = getIntent().getStringExtra("code");
        type = getIntent().getIntExtra("type", 0);
        filepath = getIntent().getStringExtra("filepath");
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(broad_interaput_Action);
        broad = new MyBroadcastReciver();
        this.registerReceiver(broad, intentFilter);
        startRTPSpeak();
        speakinCall();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.voicecode_connet, menu);
        return true;
    }

    private void palyMusic(int resid) {
        try {
            if (player != null && player.isPlaying()) {
                player.stop();
                player.release();
            }
            player = MediaPlayer.create(mContext, resid);

            player.stop();
            player.prepare();
            player.start();
            player.setOnCompletionListener(new OnCompletionListener() {

                @Override
                public void onCompletion(MediaPlayer mp) {
                    // TODO Auto-generated method stub
                    player.release();
                    player = null;
                    VoicecodeConnetActivity.this.finish();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.bt_disconnet:
                new SendDataTask().execute(APICode.SEND_NormalInteraput + "", O.Account);
                this.finish();
                break;
            case R.id.bt_keybrod:
                if (linear_Keybord.getVisibility() == View.GONE) {
                    linear_Keybord.setVisibility(View.VISIBLE);
                } else {
                    linear_Keybord.setVisibility(View.GONE);
                }
                break;
            case R.id.bt_sendfile:
                selectFile();
                break;
            case R.id.bt_recvfile:
                startActivity(new Intent(mContext, FileStatusActivity.class).putExtra("code", code));

//			Toast.makeText(mContext, "接收", 0).show();
                break;
            case R.id.bt_speaker:
                if (isspeaker) {
                    Btn_Speaker.setBackgroundResource(R.drawable.mianti);
                    speakinCall();
                } else {
                    Btn_Speaker.setBackgroundResource(R.drawable.tingtong);
                    speakinSpeaker();

                }
                isspeaker = !isspeaker;
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 0:
                if (resultCode == RESULT_OK) {
                    // Get the Uri of the selected file
                    Uri uri = data.getData();
                    String path = FileUtils.getPath(this, uri);
                    String name = AbsCreatActivity.getFileName(path);
                    startActivity(new Intent(mContext, FileStatusActivity.class).putExtra("path", path).putExtra("code", code));
                    MyLog.showToast(mContext, path);
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public void initView() {
        // TODO Auto-generated method stub
        Btn_DisConnet = (Button) findViewById(R.id.bt_disconnet);
        Btn_KeyBord = (Button) findViewById(R.id.bt_keybrod);
        linear_talk = (LinearLayout) findViewById(R.id.linear_talk);
        Btn_Talk = (Button) findViewById(R.id.bt_talkpress);
        linear_Keybord = (LinearLayout) findViewById(R.id.linear_inputpanle);
        Btn_SendFile = (Button) findViewById(R.id.bt_sendfile);
        Btn_RecvFile = (Button) findViewById(R.id.bt_recvfile);
        Btn_Speaker = (Button) findViewById(R.id.bt_speaker);
        Btn_SendFile.setOnClickListener(this);
        Btn_RecvFile.setOnClickListener(this);
        Btn_Speaker.setOnClickListener(this);
        linear_File = (LinearLayout) findViewById(R.id.linear_file);
        Btn_DisConnet.setOnClickListener(this);
        Btn_KeyBord.setOnClickListener(this);
        text_Num = (TextView) findViewById(R.id.text_number);

        if (type == AbsToCallActivity.TOCALLVOICE) {
            linear_File.setVisibility(View.GONE);
            linear_talk.setVisibility(View.VISIBLE);

        } else if (type == AbsToCallActivity.TOCALLWIREDVOICE) {
            linear_File.setVisibility(View.GONE);
            linear_talk.setVisibility(View.GONE);

        } else if (type == AbsToCallActivity.TOCALLWIREDFILE) {
            linear_File.setVisibility(View.VISIBLE);
            linear_talk.setVisibility(View.GONE);
        }
        if (code == null || code.equals("")) {
            this.finish();
        } else {
            BaseMapObject map = GetData4DB.getObjectByRowName(mContext, "contact", "number", code);
            if (map != null && map.get("name") != null) {
                text_Num.setText(map.get("name").toString() + "\r\n" + code);
            } else {
                text_Num.setText(code);
            }
        }
        Btn_Talk.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        findViewById(R.id.voice_hint_layout).setVisibility(View.VISIBLE);
                        ((AnimationDrawable) ((ImageView) findViewById(R.id.voice_hint_flash)).getDrawable()).start();
                        ((TextView) findViewById(R.id.voiceHintText)).setText("松开手指,停止讲话");

                        SendTalktoNet(true);
                        break;
                    case MotionEvent.ACTION_UP:
                        findViewById(R.id.voice_hint_layout).setVisibility(View.GONE);
                        ((TextView) findViewById(R.id.voiceHintText)).setText("松开手指发送");
                        SendTalktoNet(false);
                        break;
                }
                return false;
            }
        });
    }

    public void SendTalktoNet(boolean connet) {
        new SendDataTask().execute(APICode.SEND_TalkVoiceCode + "", O.Account, connet ? "1" : "0");
    }


    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        this.unregisterReceiver(broad);
        super.onDestroy();
    }


    public class MyBroadcastReciver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
//			hideProgressDlg();
            String action = intent.getAction();
            if (action.equals(broad_interaput_Action)) {
                stopRTPSpeak();
                palyMusic(R.raw.cutdowm);
//				VoicecodeConnetActivity.this.finish();
            }
        }

    }

}
