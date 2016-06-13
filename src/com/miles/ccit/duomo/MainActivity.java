package com.miles.ccit.duomo;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import com.miles.ccit.main.IndexActivity;
import com.miles.ccit.net.ComposeData;
import com.miles.ccit.net.SocketClient;
import com.miles.ccit.service.HeartbeatService;
import com.miles.ccit.util.AbsBaseActivity;
import com.miles.ccit.util.FileUtils;
import com.miles.ccit.util.O;
import com.redfox.voip_pro.RedfoxManager;
import com.redfox.voip_pro.RedfoxService;

import org.linphone.core.LinphoneCore;
import org.linphone.core.PayloadType;

import static android.content.Intent.ACTION_MAIN;

public class MainActivity extends AbsBaseActivity {


    private Handler mHandler;
    private ServiceWaitThread mThread;


    /**
     * 文件目录的准备
     */
    private void PrePareFile() {
        FileUtils fileutil = new FileUtils();
        // 主目录
        if (!fileutil.isFileExist(O.SDCardRoot)) {
            fileutil.creatSDDir(O.SDCardRoot);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PrePareFile();
        startService(new Intent(mContext, HeartbeatService.class));


        //IP视频模块的服务
        mHandler = new Handler();
        if (RedfoxService.isReady()) {
            onServiceReady();
        } else {
            startService(new Intent(ACTION_MAIN).setClass(this, RedfoxService.class));
            mThread = new ServiceWaitThread();
            mThread.start();
        }

    }

    byte[] red = null;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public void initView() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub

    }

    protected void onServiceReady() {
        final Class<? extends Activity> classToStart;
        classToStart = IndexActivity.class;

        LinphoneCore lc = RedfoxManager.getLcIfManagerNotDestroyedOrNull();
        lc.enableVideo(true, true);
        for (final PayloadType pt : lc.getVideoCodecs()) {
            if (pt.getMime().equals("H264")) {
                try {
                    lc.enablePayloadType(pt, true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    lc.enablePayloadType(pt, false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        lc.setVideoPolicy(true, lc.getVideoAutoAcceptPolicy());
        lc.setVideoPolicy(lc.getVideoAutoInitiatePolicy(), true);

        RedfoxService.instance().setActivityToLaunchOnIncomingReceived(classToStart);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent().setClass(MainActivity.this, classToStart).setData(getIntent().getData()));
                finish();
            }
        }, 2000);
    }


    private class ServiceWaitThread extends Thread {
        public void run() {
            while (!RedfoxService.isReady()) {
                try {
                    sleep(30);
                } catch (InterruptedException e) {
                    throw new RuntimeException("waiting thread sleep() has been interrupted");
                }
            }
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    onServiceReady();
                }
            });
            mThread = null;
        }
    }

}
