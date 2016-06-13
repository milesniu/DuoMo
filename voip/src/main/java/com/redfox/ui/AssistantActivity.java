package com.redfox.ui;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ImageView;

import com.redfox.voip_pro.RedfoxManager;
import com.redfox.voip_pro.RedfoxPreferences;

import org.linphone.core.LinphoneAddress;
import org.linphone.core.LinphoneCore;
import org.linphone.core.LinphoneCore.RegistrationState;
import org.linphone.core.LinphoneCoreFactory;
import org.linphone.core.LinphoneCoreListenerBase;
import org.linphone.core.LinphoneProxyConfig;


public class AssistantActivity extends Activity  {
private static AssistantActivity instance;
	private Fragment fragment;
	private RedfoxPreferences mPrefs;
	private boolean accountCreated = false, newAccount = false;
	private LinphoneCoreListenerBase mListener;
	private LinphoneAddress address;
	private Dialog dialog;
	private static final int PERMISSIONS_REQUEST_RECORD_AUDIO = 201;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (getResources().getBoolean(R.bool.orientation_portrait_only)) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}

		setContentView(R.layout.assistant);

		displayLoginGeneric();

        mPrefs = RedfoxPreferences.instance();
        
        mListener = new LinphoneCoreListenerBase(){
        	@Override
        	public void registrationState(LinphoneCore lc, LinphoneProxyConfig cfg, LinphoneCore.RegistrationState state, String smessage) {
				if(accountCreated && !newAccount){
					if(address != null && address.asString().equals(cfg.getAddress().asString()) ) {
						if (state == RegistrationState.RegistrationFailed) {

							if (dialog == null || !dialog.isShowing()) {
								dialog = createErrorDialog(cfg, smessage);
								dialog.show();
							}
						}
					}
				}
			}
		};
		instance = this;
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		LinphoneCore lc = RedfoxManager.getLcIfManagerNotDestroyedOrNull();
		if (lc != null) {
			lc.addListener(mListener);
		}
	}
	
	@Override
	protected void onPause() {
		LinphoneCore lc = RedfoxManager.getLcIfManagerNotDestroyedOrNull();
		if (lc != null) {
			lc.removeListener(mListener);
		}

		super.onPause();
	}
	
	public static AssistantActivity instance() {
		return instance;
	}


	private void changeFragment(Fragment newFragment) {
		FragmentTransaction transaction = getFragmentManager().beginTransaction();
		transaction.replace(R.id.fragment_container, newFragment);
		transaction.commitAllowingStateLoss();
	}

//	public void checkAndRequestAudioPermission() {
//		if (getPackageManager().checkPermission(Manifest.permission.RECORD_AUDIO, getPackageName()) != PackageManager.PERMISSION_GRANTED) {
//			if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO)) {
//				ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, PERMISSIONS_REQUEST_RECORD_AUDIO);
//			}
//		}
//	}

//	@Override
//	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//		success();
//	}

	private void logIn(String username, String password, String displayName, String domain, LinphoneAddress.TransportType transport, boolean sendEcCalibrationResult) {
        saveCreatedAccount(username, password, displayName, domain, transport);
	}

	public void checkAccount(String username, String password, String displayName, String domain) {
		saveCreatedAccount(username, password, displayName, domain, null);
	}

	public void genericLogIn(String username, String password, String displayName, String domain, LinphoneAddress.TransportType transport) {
		if(accountCreated) {
			retryLogin(username, password, displayName, domain, transport);
		} else {
			logIn(username, password, displayName, domain, transport, false);
		}
	}

	public void displayLoginGeneric() {
		fragment = new LoginFragment();
		changeFragment(fragment);
	}

	public void retryLogin(String username, String password, String displayName, String domain, LinphoneAddress.TransportType transport) {
		accountCreated = false;
		saveCreatedAccount(username, password, displayName, domain, transport);
	}

	public void saveCreatedAccount(String username, String password, String displayName, String domain, LinphoneAddress.TransportType transport) {
		if (accountCreated)
			return;

		if(username.startsWith("sip:")) {
			username = username.substring(4);
		}

		if (username.contains("@"))
			username = username.split("@")[0];

		if(domain.startsWith("sip:")) {
			domain = domain.substring(4);
		}

		String identity = "sip:" + username + "@" + domain;
		try {
			address = LinphoneCoreFactory.instance().createLinphoneAddress(identity);
		} catch (Exception e) {
			e.printStackTrace();
		}

		if(address != null && displayName != null && !displayName.equals("")){
			address.setDisplayName(displayName);
		}

		RedfoxPreferences.AccountBuilder builder = new RedfoxPreferences.AccountBuilder(RedfoxManager.getLc())
		.setUsername(username)
		.setDomain(domain)
		.setDisplayName(displayName)
		.setPassword(password);

		String forcedProxy = username + "@" + domain;
		if (!TextUtils.isEmpty(forcedProxy)) {
			builder.setProxy(forcedProxy)
			.setOutboundProxyEnabled(true)
			.setAvpfRRInterval(5);
		}

		if(transport != null) {
			builder.setTransport(transport);
		}

		try {
			builder.saveNewAccount();
			accountCreated = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Dialog createErrorDialog(LinphoneProxyConfig proxy, String message){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		if(message.equals("Forbidden")) {
			message = getString(R.string.assistant_error_bad_credentials);
		}
		builder.setMessage(message)
				.setTitle(proxy.getState().toString())
				.setPositiveButton(getString(R.string.continue_text), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						success();
					}
				})
				.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						RedfoxManager.getLc().removeProxyConfig(RedfoxManager.getLc().getDefaultProxyConfig());
						RedfoxPreferences.instance().resetDefaultProxyConfig();
						RedfoxManager.getLc().refreshRegisters();
						dialog.dismiss();
					}
				});
		return builder.show();
	}
	
	public void success() {
		if(MainActivity.instance() != null) {
			MainActivity.instance().isNewProxyConfig();
			setResult(Activity.RESULT_OK);
		}
		finish();
	}
}
