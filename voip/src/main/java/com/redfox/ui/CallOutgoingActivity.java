package com.redfox.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.redfox.voip_pro.RedfoxManager;
import com.redfox.voip_pro.RedfoxPreferences;
import com.redfox.voip_pro.RedfoxUtils;

import org.linphone.core.LinphoneAddress;
import org.linphone.core.LinphoneCall;
import org.linphone.core.LinphoneCall.State;
import org.linphone.core.LinphoneCallParams;
import org.linphone.core.LinphoneCore;
import org.linphone.core.LinphoneCoreListenerBase;

import java.util.List;

public class CallOutgoingActivity extends Activity implements OnClickListener {

    private static CallOutgoingActivity instance;

    private TextView name, number;
    private ImageView hangUp;
    private LinphoneCall mCall;
    private LinphoneCoreListenerBase mListener;

    public static CallOutgoingActivity instance() {
        return instance;
    }

    public static boolean isInstanciated() {
        return instance != null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getResources().getBoolean(com.redfox.ui.R.bool.orientation_portrait_only)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(com.redfox.ui.R.layout.call_outgoing);

        name = (TextView) findViewById(com.redfox.ui.R.id.contact_name);
        number = (TextView) findViewById(com.redfox.ui.R.id.contact_number);

        // set this flag so this activity will stay in front of the keyguard
        int flags = WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON;
        getWindow().addFlags(flags);

        hangUp = (ImageView) findViewById(com.redfox.ui.R.id.outgoing_hang_up);
        hangUp.setOnClickListener(this);

        mListener = new LinphoneCoreListenerBase() {
            @Override
            public void callState(LinphoneCore lc, LinphoneCall call, LinphoneCall.State state, String message) {
                if (RedfoxManager.getLc().getCallsNb() == 0) {
                    Toast.makeText(CallOutgoingActivity.this, "对方已离线...", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }
                if (call == mCall && State.CallEnd == state) {
                    finish();
                }

                if (call == mCall && (State.Connected == state)) {
//                    if (!MainActivity.isInstanciated()) {
//                        return;
//                    }
                    final LinphoneCallParams remoteParams = mCall.getRemoteParams();
                    if (remoteParams != null && remoteParams.getVideoEnabled() && RedfoxPreferences.instance().shouldAutomaticallyAcceptVideoRequests()) {
                        startVideoActivity(mCall);
                    } else {
                        startIncallActivity(mCall);
                    }
                    finish();
                    return;
                }
            }
        };

        super.onCreate(savedInstanceState);
        instance = this;
    }


    private static final int CALL_ACTIVITY = 19;

    public void startVideoActivity(LinphoneCall currentCall) {
        Intent intent = new Intent(this, CallActivity.class);
        intent.putExtra("VideoEnabled", true);
        startActivityForResult(intent, CALL_ACTIVITY);
    }


    public void startIncallActivity(LinphoneCall currentCall) {
        Intent intent = new Intent(this, CallActivity.class);
        intent.putExtra("VideoEnabled", false);
        startActivityForResult(intent, CALL_ACTIVITY);
    }

    @Override
    protected void onResume() {
        super.onResume();
        instance = this;
        LinphoneCore lc = RedfoxManager.getLcIfManagerNotDestroyedOrNull();
        if (lc != null) {
            lc.addListener(mListener);
        }

        // Only one call ringing at a time is allowed
        if (RedfoxManager.getLcIfManagerNotDestroyedOrNull() != null) {
            List<LinphoneCall> calls = RedfoxUtils.getLinphoneCalls(RedfoxManager.getLc());
            for (LinphoneCall call : calls) {
                if (State.OutgoingInit == call.getState() || State.OutgoingProgress == call.getState() || State.OutgoingRinging == call.getState() || State.OutgoingEarlyMedia == call.getState()) {
                    mCall = call;
                    break;
                }
            }
        }
        if (mCall == null) {
            finish();
            return;
        }

        LinphoneAddress address = mCall.getRemoteAddress();
        number.setText(address.asStringUriOnly());
    }

    @Override
    protected void onPause() {
        LinphoneCore lc = RedfoxManager.getLcIfManagerNotDestroyedOrNull();
        if (lc != null) {
            lc.removeListener(mListener);
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        instance = null;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == com.redfox.ui.R.id.outgoing_hang_up) {
            decline();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (RedfoxManager.isInstanciated() && (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_HOME)) {
            RedfoxManager.getLc().terminateCall(mCall);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    private void decline() {
        RedfoxManager.getLc().terminateCall(mCall);
    }
}
