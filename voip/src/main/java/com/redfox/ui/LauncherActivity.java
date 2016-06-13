package com.redfox.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.redfox.voip_pro.RedfoxManager;
import com.redfox.voip_pro.RedfoxService;

import org.linphone.core.LinphoneCore;
import org.linphone.core.PayloadType;

import static android.content.Intent.ACTION_MAIN;

public class LauncherActivity extends Activity {

    private Handler mHandler;
    private ServiceWaitThread mThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.launch_screen);

        mHandler = new Handler();

        if (RedfoxService.isReady()) {
            onServiceReady();
        } else {
            startService(new Intent(ACTION_MAIN).setClass(this, RedfoxService.class));
            mThread = new ServiceWaitThread();
            mThread.start();
        }
    }

    protected void onServiceReady() {
        final Class<? extends Activity> classToStart;
        classToStart = MainActivity.class;

        LinphoneCore lc = RedfoxManager.getLcIfManagerNotDestroyedOrNull();
        lc.enableVideo(true, true);
        for (final PayloadType pt : lc.getVideoCodecs()) {
            if (pt.getMime().equals("H264")) {
                try {
                    lc.enablePayloadType(pt, true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else {
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
                startActivity(new Intent().setClass(LauncherActivity.this, classToStart).setData(getIntent().getData()));
                finish();
            }
        }, 1000);
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
