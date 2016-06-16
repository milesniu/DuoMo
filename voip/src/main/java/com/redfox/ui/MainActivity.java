package com.redfox.ui;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.redfox.voip_pro.RedfoxManager;
import com.redfox.voip_pro.RedfoxPreferences;
import com.redfox.voip_pro.RedfoxService;
import com.redfox.voip_pro.RedfoxUtils;

import org.linphone.core.LinphoneAuthInfo;
import org.linphone.core.LinphoneCall;
import org.linphone.core.LinphoneCall.State;
import org.linphone.core.LinphoneCore;
import org.linphone.core.LinphoneCore.RegistrationState;
import org.linphone.core.LinphoneCoreListenerBase;
import org.linphone.core.LinphoneProxyConfig;
import org.linphone.core.Reason;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;


public class MainActivity extends Activity implements OnClickListener {

    public enum FragmentsAvailable {
        UNKNOW,
        DIALER,
        EMPTY,
        ACCOUNT_SETTINGS,
        SETTINGS,
    }

    private static final String TAG = "MainActivity";
    private static final int SETTINGS_ACTIVITY = 123;
    private static final int FIRST_LOGIN_ACTIVITY = 101;
    private static final int REMOTE_PROVISIONING_LOGIN_ACTIVITY = 102;
    private static final int CALL_ACTIVITY = 19;
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 200;
    private static final int PERMISSIONS_REQUEST_RECORD_AUDIO = 201;
    private static final int PERMISSIONS_REQUEST_RECORD_AUDIO_INCOMING_CALL = 203;

    private static MainActivity instance;

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

    public static final MainActivity instance() {
        if (instance != null)
            return instance;
        throw new RuntimeException("MainActivity not instantiated yet");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //startOrientationSensor();

        if (!RedfoxManager.isInstanciated()) {
            Log.e(TAG, "No service running: avoid crash by starting the launch " + this.getClass().getName());
            finish();
            startActivity(getIntent().setClass(this, LauncherActivity.class));
            return;
        }

        boolean useFirstLoginActivity = getResources().getBoolean(R.bool.display_account_wizard_at_first_start);
        if (useFirstLoginActivity && RedfoxPreferences.instance().isFirstLaunch() || RedfoxManager.getLc().getProxyConfigList().length == 0) {
            if (RedfoxPreferences.instance().getAccountCount() > 0) {
                RedfoxPreferences.instance().firstLaunchSuccessful();
            } else {
                startActivityForResult(new Intent().setClass(this, AssistantActivity.class), FIRST_LOGIN_ACTIVITY);
            }
        }

        setContentView(R.layout.activity_main);
        instance = this;
        fragmentsHistory = new ArrayList<FragmentsAvailable>();

        initButtons();

        currentFragment = nextFragment = FragmentsAvailable.DIALER;
        fragmentsHistory.add(currentFragment);
        if (savedInstanceState == null) {
            if (findViewById(R.id.fragmentContainer) != null) {
                dialerFragment = new DialerFragment();
                dialerFragment.setArguments(getIntent().getExtras());
                getFragmentManager().beginTransaction().addToBackStack(dialerFragment.toString());
                getFragmentManager().beginTransaction().add(R.id.fragmentContainer, dialerFragment, currentFragment.toString()).commit();
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
                if (state.equals(RegistrationState.RegistrationCleared)) {
                    if (lc != null) {
                        LinphoneAuthInfo authInfo = lc.findAuthInfo(proxy.getIdentity(), proxy.getRealm(), proxy.getDomain());
                        if (authInfo != null)
                            lc.removeAuthInfo(authInfo);
                    }
                }

                if (state.equals(RegistrationState.RegistrationFailed) && newProxyConfig) {
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
                if (state == State.IncomingReceived) {
                    if (getPackageManager().checkPermission(Manifest.permission.RECORD_AUDIO, getPackageName()) == PackageManager.PERMISSION_GRANTED || RedfoxPreferences.instance().audioPermAsked()) {
                        startActivity(new Intent(MainActivity.instance(), CallIncomingActivity.class));
                    } else {
//						checkAndRequestPermission(Manifest.permission.RECORD_AUDIO, PERMISSIONS_REQUEST_RECORD_AUDIO_INCOMING_CALL);
                    }
                } else if (state == State.OutgoingInit || state == State.OutgoingProgress) {
                    if (getPackageManager().checkPermission(Manifest.permission.RECORD_AUDIO, getPackageName()) == PackageManager.PERMISSION_GRANTED || RedfoxPreferences.instance().audioPermAsked()) {
//                        startActivity(new Intent(MainActivity.instance(), CallOutgoingActivity.class));
                    } else {
//						checkAndRequestPermission(Manifest.permission.RECORD_AUDIO, PERMISSIONS_REQUEST_RECORD_AUDIO);
                    }
                } else if (state == State.CallEnd || state == State.Error || state == State.CallReleased) {
                    // Convert LinphoneCore message for internalization
                    if (message != null && call.getErrorInfo().getReason() == Reason.Declined) {
                    } else if (message != null && call.getErrorInfo().getReason() == Reason.NotFound) {
                    } else if (message != null && call.getErrorInfo().getReason() == Reason.Media) {
                    } else if (message != null && state == State.Error) {
                    }
                }
            }
        };

        LinphoneCore lc = RedfoxManager.getLcIfManagerNotDestroyedOrNull();
        if (lc != null) {
            lc.addListener(mListener);
        }

    }

    private void initButtons() {
        mTopBar = (RelativeLayout) findViewById(R.id.top_bar);

        cancel = (ImageView) findViewById(R.id.cancel);
        cancel.setOnClickListener(this);
    }

    public void hideStatusBar() {
        findViewById(R.id.status).setVisibility(View.GONE);
    }

    public void showStatusBar() {
        if (statusFragment != null && !statusFragment.isVisible()) {
            statusFragment.getView().setVisibility(View.VISIBLE);
        }
        findViewById(R.id.status).setVisibility(View.VISIBLE);
    }

    public void isNewProxyConfig() {
        newProxyConfig = true;
    }

    private void changeCurrentFragment(FragmentsAvailable newFragmentType, Bundle extras) {
        changeCurrentFragment(newFragmentType, extras, false);
    }

    private void changeCurrentFragment(FragmentsAvailable newFragmentType, Bundle extras, boolean withoutAnimation) {
        if (newFragmentType == currentFragment) {
            return;
        }
        nextFragment = newFragmentType;

        if (currentFragment == FragmentsAvailable.DIALER) {
            try {
                dialerSavedState = getFragmentManager().saveFragmentInstanceState(dialerFragment);
            } catch (Exception e) {
            }
        }

        Fragment newFragment = null;

        switch (newFragmentType) {
            case DIALER:
                newFragment = new DialerFragment();
                if (extras == null) {
                    newFragment.setInitialSavedState(dialerSavedState);
                }
                dialerFragment = newFragment;
                break;
            default:
                break;
        }

        if (newFragment != null) {
            newFragment.setArguments(extras);
            changeFragment(newFragment, newFragmentType, withoutAnimation);
        }
    }

    private void changeFragment(Fragment newFragment, FragmentsAvailable newFragmentType, boolean withoutAnimation) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.addToBackStack(newFragmentType.toString());
        transaction.replace(R.id.fragmentContainer, newFragment, newFragmentType.toString());
        transaction.commitAllowingStateLoss();
        getFragmentManager().executePendingTransactions();

        currentFragment = newFragmentType;
    }

    @SuppressLint("SimpleDateFormat")
    private String secondsToDisplayableString(int secs) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        cal.set(0, 0, 0, 0, 0, secs);
        return dateFormat.format(cal.getTime());
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.cancel) {
            hideTopBar();
            displayDialer();
        }
    }

    public void hideTopBar() {
        mTopBar.setVisibility(View.GONE);
    }

    public void selectMenu(FragmentsAvailable menuToSelect) {
        currentFragment = menuToSelect;

        switch (menuToSelect) {
            case SETTINGS:
            case ACCOUNT_SETTINGS:
                mTopBar.setVisibility(View.VISIBLE);
                break;
        }
    }

    public void updateDialerFragment(DialerFragment fragment) {
        dialerFragment = fragment;
        // Hack to maintain soft input flags
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    public void updateStatusFragment(StatusFragment fragment) {
        statusFragment = fragment;
    }

    public void displayDialer() {
        changeCurrentFragment(FragmentsAvailable.DIALER, null);
    }

    public void displayAccountSettings(int accountNumber) {
        Bundle bundle = new Bundle();
        bundle.putInt("Account", accountNumber);
        changeCurrentFragment(FragmentsAvailable.ACCOUNT_SETTINGS, bundle);
        //settings.setSelected(true);
    }

    public StatusFragment getStatusFragment() {
        return statusFragment;
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

    private synchronized void startOrientationSensor() {
        if (mOrientationHelper == null) {
            mOrientationHelper = new LocalOrientationEventListener(this);
        }
        mOrientationHelper.enable();
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

    public FragmentsAvailable getCurrentFragment() {
        return currentFragment;
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!RedfoxService.isReady()) {
            startService(new Intent(Intent.ACTION_MAIN).setClass(this, RedfoxService.class));
        }

        RedfoxManager.getInstance().changeStatusToOnline();
    }

    @Override
    protected void onDestroy() {
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

        unbindDrawables(findViewById(R.id.topLayout));
        System.gc();
    }

    private void unbindDrawables(View view) {
        if (view != null && view.getBackground() != null) {
            view.getBackground().setCallback(null);
        }
        if (view instanceof ViewGroup && !(view instanceof AdapterView)) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                unbindDrawables(((ViewGroup) view).getChildAt(i));
            }
            ((ViewGroup) view).removeAllViews();
        }
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
                    if (getResources().getBoolean(R.bool.automatically_start_intercepted_outgoing_gsm_call)) {
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
                Collection<State> incoming = new ArrayList<State>();
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

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (currentFragment == FragmentsAvailable.DIALER) {
                boolean isBackgroundModeActive = RedfoxPreferences.instance().isBackgroundModeEnabled();
                if (!isBackgroundModeActive) {
                    stopService(new Intent(Intent.ACTION_MAIN).setClass(this, RedfoxService.class));
                    finish();
                } else if (RedfoxUtils.onKeyBackGoHome(this, keyCode, event)) {
                    return true;
                }
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    public void displaySettings() {
        changeCurrentFragment(FragmentsAvailable.SETTINGS, null);
    }
}