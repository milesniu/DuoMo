package com.miles.ccit.main;

import android.Manifest;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.OrientationEventListener;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.miles.ccit.database.GetData4DB;
import com.miles.ccit.duomo.BroadCastctivity;
import com.miles.ccit.duomo.ContactActivity;
import com.miles.ccit.duomo.HostcfgActivity;
import com.miles.ccit.duomo.LoginActivity;
import com.miles.ccit.duomo.NetModelFragmentActivity;
import com.miles.ccit.duomo.R;
import com.miles.ccit.duomo.SettingActivity;
import com.miles.ccit.duomo.SpecialNetFragmentActivity;
import com.miles.ccit.duomo.SpecialVoiceActivity;
import com.miles.ccit.duomo.WiredModelActivity;
import com.miles.ccit.util.AbsBaseActivity;
import com.miles.ccit.util.BaseMapObject;
import com.miles.ccit.util.ByteUtil;
import com.miles.ccit.util.FileUtils;
import com.miles.ccit.util.O;
import com.redfox.ui.AssistantActivity;
import com.redfox.ui.CallActivity;
import com.redfox.ui.CallIncomingActivity;
import com.redfox.ui.CallOutgoingActivity;
import com.redfox.ui.DialerFragment;
import com.redfox.ui.LauncherActivity;
import com.redfox.ui.StatusFragment;
import com.redfox.voip_pro.RedfoxManager;
import com.redfox.voip_pro.RedfoxPreferences;
import com.redfox.voip_pro.RedfoxService;
import com.redfox.voip_pro.RedfoxUtils;

import org.linphone.core.LinphoneAuthInfo;
import org.linphone.core.LinphoneCall;
import org.linphone.core.LinphoneCore;
import org.linphone.core.LinphoneCoreListenerBase;
import org.linphone.core.LinphoneProxyConfig;
import org.linphone.core.Reason;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class IndexActivity extends AbsBaseActivity {

    //	public static boolean result = false;
    private MyBroadcastReciver broad = null;
    private BaseMapObject checkCount = null;

    private TextView tvUnreadSpecise;
    private TextView tvUnreadNetmodel;

    public static int underspecise = 0;
    public static int undernet = 0;


    public enum FragmentsAvailable {
        UNKNOW,
        DIALER,
        EMPTY,
        ACCOUNT_SETTINGS,
        SETTINGS,
    }

    private static final String TAG = "IndexActivity";
    private static final int SETTINGS_ACTIVITY = 123;
    private static final int FIRST_LOGIN_ACTIVITY = 101;
    private static final int REMOTE_PROVISIONING_LOGIN_ACTIVITY = 102;
    private static final int CALL_ACTIVITY = 19;
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 200;
    private static final int PERMISSIONS_REQUEST_RECORD_AUDIO = 201;
    private static final int PERMISSIONS_REQUEST_RECORD_AUDIO_INCOMING_CALL = 203;

    private static IndexActivity instance;

    private StatusFragment statusFragment;
    private RelativeLayout mTopBar;
    private ImageView cancel;
    private FragmentsAvailable currentFragment, nextFragment;
    private List<FragmentsAvailable> fragmentsHistory;
    private Fragment dialerFragment;
    private Fragment.SavedState dialerSavedState;
    private boolean newProxyConfig;
    private boolean isAnimationDisabled = true, emptyFragment = false, permissionAsked = false;
    private OrientationEventListener mOrientationHelper;
    private LinphoneCoreListenerBase mListener;


    static final boolean isInstanciated() {
        return instance != null;
    }

    public static final IndexActivity instance() {
        if (instance != null)
            return instance;
        throw new RuntimeException("IndexActivity not instantiated yet");
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if (!RedfoxManager.isInstanciated()) {
            Log.e(TAG, "No service running: avoid crash by starting the launch " + this.getClass().getName());
            finish();
            startActivity(getIntent().setClass(this, LauncherActivity.class));
            return;
        }


        setContentView(R.layout.activity_index);
        sp = PreferenceManager.getDefaultSharedPreferences(mContext);
        checkCount = FileUtils.getMapData4SD();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(broad_usimout_Action);
        intentFilter.addAction(broad_decryption_Action);
        intentFilter.addAction(broad_debug_info);
        broad = new MyBroadcastReciver();
        this.registerReceiver(broad, intentFilter);
        sp = PreferenceManager.getDefaultSharedPreferences(mContext);
        O.isEncrypt = sp.getBoolean("jiami", false);


        instance = this;

        fragmentsHistory = new ArrayList<FragmentsAvailable>();
        currentFragment = nextFragment = FragmentsAvailable.DIALER;
        fragmentsHistory.add(currentFragment);
        if (savedInstanceState == null) {
            if (findViewById(com.redfox.ui.R.id.fragmentContainer) != null) {
                dialerFragment = new DialerFragment();
                dialerFragment.setArguments(getIntent().getExtras());
                getFragmentManager().beginTransaction().addToBackStack(dialerFragment.toString());
                getFragmentManager().beginTransaction().add(com.redfox.ui.R.id.fragmentContainer, dialerFragment, currentFragment.toString()).commit();
            }
        }

        mListener = new LinphoneCoreListenerBase() {
            @Override
            public void authInfoRequested(LinphoneCore lc, String realm, String username, String domain) {
                //authInfoPassword = displayWrongPasswordDialog(username, realm, domain);
                //authInfoPassword.show();
            }

            @Override
            public void registrationState(LinphoneCore lc, LinphoneProxyConfig proxy, LinphoneCore.RegistrationState state, String smessage) {
                if (state.equals(LinphoneCore.RegistrationState.RegistrationCleared)) {
                    if (lc != null) {
                        LinphoneAuthInfo authInfo = lc.findAuthInfo(proxy.getIdentity(), proxy.getRealm(), proxy.getDomain());
                        if (authInfo != null)
                            lc.removeAuthInfo(authInfo);
                    }
                }

                if (state.equals(LinphoneCore.RegistrationState.RegistrationFailed) && newProxyConfig) {
                    newProxyConfig = false;
                    if (proxy.getError() == Reason.BadCredentials) {
                    }
                    if (proxy.getError() == Reason.Unauthorized) {
                    }
                    if (proxy.getError() == Reason.IOError) {
                    }
                }
            }

            @Override
            public void callState(LinphoneCore lc, LinphoneCall call, LinphoneCall.State state, String message) {
                if (state == LinphoneCall.State.IncomingReceived) {
                    if (getPackageManager().checkPermission(Manifest.permission.RECORD_AUDIO, getPackageName()) == PackageManager.PERMISSION_GRANTED || RedfoxPreferences.instance().audioPermAsked()) {
                        startActivity(new Intent(IndexActivity.instance(), CallIncomingActivity.class));
                    } else {
//						checkAndRequestPermission(Manifest.permission.RECORD_AUDIO, PERMISSIONS_REQUEST_RECORD_AUDIO_INCOMING_CALL);
                    }
                } else if (state == LinphoneCall.State.OutgoingInit || state == LinphoneCall.State.OutgoingProgress) {
                    if (getPackageManager().checkPermission(Manifest.permission.RECORD_AUDIO, getPackageName()) == PackageManager.PERMISSION_GRANTED || RedfoxPreferences.instance().audioPermAsked()) {
                        startActivity(new Intent(IndexActivity.instance(), CallOutgoingActivity.class));
                    } else {
//						checkAndRequestPermission(Manifest.permission.RECORD_AUDIO, PERMISSIONS_REQUEST_RECORD_AUDIO);
                    }
                } else if (state == LinphoneCall.State.CallEnd || state == LinphoneCall.State.Error || state == LinphoneCall.State.CallReleased) {
                    // Convert LinphoneCore message for internalization
                    if (message != null && call.getErrorInfo().getReason() == Reason.Declined) {
                    } else if (message != null && call.getErrorInfo().getReason() == Reason.NotFound) {
                    } else if (message != null && call.getErrorInfo().getReason() == Reason.Media) {
                    } else if (message != null && state == LinphoneCall.State.Error) {
                    }
                }
            }
        };

        LinphoneCore lc = RedfoxManager.getLcIfManagerNotDestroyedOrNull();
        if (lc != null) {
            lc.addListener(mListener);
        }


    }


    public void isNewProxyConfig() {
        newProxyConfig = true;
    }

    private int mAlwaysChangingPhoneAngle = -1;

    private class LocalOrientationEventListener extends OrientationEventListener {
        public LocalOrientationEventListener(Context context) {
            super(context);
        }

        @Override
        public void onOrientationChanged(final int o) {
            if (o == OrientationEventListener.ORIENTATION_UNKNOWN) {
                return;
            }

            int degrees = 270;
            if (o < 45 || o > 315)
                degrees = 0;
            else if (o < 135)
                degrees = 90;
            else if (o < 225)
                degrees = 180;

            if (mAlwaysChangingPhoneAngle == degrees) {
                return;
            }
            mAlwaysChangingPhoneAngle = degrees;

            org.linphone.mediastream.Log.d("Phone orientation changed to ", degrees);
            int rotation = (360 - degrees) % 360;
            LinphoneCore lc = RedfoxManager.getLcIfManagerNotDestroyedOrNull();
            if (lc != null) {
                lc.setDeviceRotation(rotation);
                LinphoneCall currentCall = lc.getCurrentCall();
                if (currentCall != null && currentCall.cameraEnabled() && currentCall.getCurrentParamsCopy().getVideoEnabled()) {
                    lc.updateCall(currentCall, null);
                }
            }
        }
    }


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

    public void quit() {
        finish();
        stopService(new Intent(Intent.ACTION_MAIN).setClass(this, RedfoxService.class));
    }

    public void removeAccount() {
        RedfoxManager.getLc().clearAuthInfos();
        RedfoxManager.getLc().clearProxyConfigs();

        startActivityForResult(new Intent().setClass(this, AssistantActivity.class), FIRST_LOGIN_ACTIVITY);
    }


    @Override
    protected void onDestroy() {
        unregisterReceiver(broad);

        if (mOrientationHelper != null) {
            mOrientationHelper.disable();
            mOrientationHelper = null;
        }

        LinphoneCore lc = RedfoxManager.getLcIfManagerNotDestroyedOrNull();
        if (lc != null) {
            lc.removeListener(mListener);
        }

        instance = null;
        super.onDestroy();

//        unbindDrawables(findViewById(com.redfox.ui.R.id.topLayout));
        System.gc();
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        Bundle extras = intent.getExtras();
        if (extras != null && extras.getBoolean("Notification", false)) {
            if (RedfoxManager.getLc().getCallsNb() > 0) {
                LinphoneCall call = RedfoxManager.getLc().getCalls()[0];
                if (call.getCurrentParamsCopy().getVideoEnabled()) {
                    startVideoActivity(call);
                } else {
                    startIncallActivity(call);
                }
            }
        } else {
            if (dialerFragment != null) {
                if (extras != null && extras.containsKey("SipUriOrNumber")) {
                    if (getResources().getBoolean(com.redfox.ui.R.bool.automatically_start_intercepted_outgoing_gsm_call)) {
                        ((DialerFragment) dialerFragment).newOutgoingCall(extras.getString("SipUriOrNumber"));
                    }
                } else {
                    ((DialerFragment) dialerFragment).newOutgoingCall(intent);
                }
            }
            if (RedfoxManager.getLc().getCalls().length > 0) {
                LinphoneCall calls[] = RedfoxManager.getLc().getCalls();
                if (calls.length > 0) {
                    LinphoneCall call = calls[0];

                    if (call != null && call.getState() != LinphoneCall.State.IncomingReceived) {
                        if (call.getCurrentParamsCopy().getVideoEnabled()) {
                            //startVideoActivity(call);
                        } else {
                            //startIncallActivity(call);
                        }
                    }
                }

                // If a call is ringing, start incomingcallactivity
                Collection<LinphoneCall.State> incoming = new ArrayList<LinphoneCall.State>();
                incoming.add(LinphoneCall.State.IncomingReceived);
                if (RedfoxUtils.getCallsInState(RedfoxManager.getLc(), incoming).size() > 0) {
                    if (CallActivity.isInstanciated()) {
                        CallActivity.instance().startIncomingCallActivity();
                    } else {
                        if (getPackageManager().checkPermission(Manifest.permission.RECORD_AUDIO, getPackageName()) == PackageManager.PERMISSION_GRANTED || RedfoxPreferences.instance().audioPermAsked()) {
                            startActivity(new Intent(this, CallIncomingActivity.class));
                        } else {
//							checkAndRequestPermission(Manifest.permission.RECORD_AUDIO, PERMISSIONS_REQUEST_RECORD_AUDIO_INCOMING_CALL);
                        }
                    }
                }
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.index, menu);
        return true;
    }


    public class MyBroadcastReciver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            hideProgressDlg();
            String action = intent.getAction();

            if (action.equals(broad_usimout_Action)) {
                findViewById(R.id.linear_title).setBackgroundResource(R.drawable.indextitlenologin);
                return;
            } else if (action.equals(broad_decryption_Action)) {
                if (intent.getStringExtra("data") == null) {
                    return;
                } else {
                    List<BaseMapObject> shortList = GetData4DB.getObjectListData(mContext, "shortmsg");
                    BaseMapObject lastobj = shortList.get(shortList.size() - 1);
                    lastobj.put("msgcontent", intent.getStringExtra("data"));
                    lastobj.UpdateMyself(mContext, "shortmsg");
//                    Toast.makeText(mContext, "解密数据已返回:" + intent.getStringExtra("data"), Toast.LENGTH_SHORT).show();
                }
            } else if (action.equals(broad_debug_info)) {
                byte[] data = intent.getByteArrayExtra("data");
                int len = ByteUtil.oneByte2oneInt(data[5]);      //获取内容长度，1字节
                byte[] info = new byte[len];
                System.arraycopy(data, 6, info, 0, len);
                try {
                    String content = new String(info, "UTF-8");
                    String orgcon = sp.getString("debugInfo", "");

                    String newdata = "";
                    String[] dataary = orgcon.split(",");
                    if (dataary.length >= 20) {
                        for (int i = dataary.length - 20; i < dataary.length; i++) {
                            newdata += dataary[i] + ",";
                        }
                    } else {
                        for (String s : dataary) {
                            newdata += s + ",";
                        }
                    }

                    String debug = newdata + "------------------\r\n" + content + ",";
                    sp.edit().putString("debugInfo", debug).commit();
                    //发送广播,通知debug界面更新
                    intent = new Intent();
                    intent.setAction(AbsBaseActivity.broad_debug_show_info);
                    sendBroadcast(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }

    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub

        if (keyCode == KeyEvent.KEYCODE_BACK)        //Back键实现Home键返回，activity后台压栈
        {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);// 注意
            intent.addCategory(Intent.CATEGORY_HOME);
            this.startActivity(intent);


            boolean isBackgroundModeActive = RedfoxPreferences.instance().isBackgroundModeEnabled();
            if (!isBackgroundModeActive) {
                stopService(new Intent(Intent.ACTION_MAIN).setClass(this, RedfoxService.class));
                finish();
            } else if (RedfoxUtils.onKeyBackGoHome(this, keyCode, event)) {
                return true;
            }

        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.bt_specialnet:
                startActivity(new Intent(mContext, SpecialNetFragmentActivity.class));
                underspecise = 0;
                break;
            case R.id.bt_contact:
                startActivity(new Intent(mContext, ContactActivity.class));
                break;
            case R.id.bt_haveline:
                startActivity(new Intent(mContext, WiredModelActivity.class));
                break;
            case R.id.bt_broadcast:
                startActivity(new Intent(mContext, BroadCastctivity.class));

                break;
            case R.id.bt_specialway:
                startActivity(new Intent(mContext, SpecialVoiceActivity.class));

                break;
            case R.id.bt_setting:
                startActivity(new Intent(mContext, SettingActivity.class));

                break;
            case R.id.bt_about:
                startActivity(new Intent(mContext, HostcfgActivity.class));

                break;
            case R.id.bt_netmodle:
                startActivity(new Intent(mContext, NetModelFragmentActivity.class));
                undernet = 0;
                break;
            case R.id.linear_title:
                if (!LoginActivity.isLogin) {
                    startActivityForResult(new Intent(mContext, LoginActivity.class), 3);
                }

                break;
        }
    }


    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        if (LoginActivity.isLogin) {
            findViewById(R.id.linear_title).setBackgroundResource(R.drawable.loginok8);
            tvUnreadSpecise.setText("" + underspecise);
            tvUnreadNetmodel.setText("" + undernet);
            if (underspecise == 0) {
                tvUnreadSpecise.setVisibility(View.GONE);
            } else {
                tvUnreadSpecise.setVisibility(View.VISIBLE);
            }
            if (undernet == 0) {
                tvUnreadNetmodel.setVisibility(View.GONE);
            } else {
                tvUnreadNetmodel.setVisibility(View.VISIBLE);
            }
        } else {
            findViewById(R.id.linear_title).setBackgroundResource(R.drawable.indextitlenologin);
            tvUnreadNetmodel.setVisibility(View.GONE);
            tvUnreadSpecise.setVisibility(View.GONE);
        }
        if (!RedfoxService.isReady()) {
            startService(new Intent(Intent.ACTION_MAIN).setClass(this, RedfoxService.class));
        }

        RedfoxManager.getInstance().changeStatusToOnline();
        super.onResume();


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void initView() {
        // TODO Auto-generated method stub

        tvUnreadSpecise = (TextView) findViewById(R.id.text_unread_specise);
        tvUnreadNetmodel = (TextView) findViewById(R.id.text_unread_netmodel);


        findViewById(R.id.bt_specialnet).setOnClickListener(this);
        findViewById(R.id.bt_contact).setOnClickListener(this);
        findViewById(R.id.bt_haveline).setOnClickListener(this);
        findViewById(R.id.bt_broadcast).setOnClickListener(this);
        findViewById(R.id.bt_specialway).setOnClickListener(this);
        findViewById(R.id.bt_setting).setOnClickListener(this);
        findViewById(R.id.bt_about).setOnClickListener(this);
        findViewById(R.id.linear_title).setOnClickListener(this);
        findViewById(R.id.bt_netmodle).setOnClickListener(this);
    }


}