package com.redfox.ui;


import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.redfox.voip_pro.RedfoxManager;
import com.redfox.voip_pro.RedfoxService;

import org.linphone.core.LinphoneCall;
import org.linphone.core.LinphoneCore;
import org.linphone.core.LinphoneCore.RegistrationState;
import org.linphone.core.LinphoneCoreListenerBase;
import org.linphone.core.LinphoneProxyConfig;

import java.util.Timer;
import java.util.TimerTask;


public class StatusFragment extends Fragment {
	private Handler mHandler = new Handler();
	private Handler refreshHandler = new Handler();
	private TextView statusText;
	private ImageView statusLed;
	private Runnable mCallQualityUpdater;
	private boolean isInCall, isAttached = false;
	private Timer mTimer;
	private TimerTask mTask;
	private LinphoneCoreListenerBase mListener;
	private int mDisplayedQuality = -1;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.status, container, false);
		
		statusText = (TextView) view.findViewById(R.id.status_text);
		statusLed = (ImageView) view.findViewById(R.id.status_led);
		
		mListener = new LinphoneCoreListenerBase(){
			@Override
			public void registrationState(final LinphoneCore lc, final LinphoneProxyConfig proxy, final LinphoneCore.RegistrationState state, String smessage) {
				if (!isAttached || !RedfoxService.isReady()) {
					return;
				}

				if(lc.getProxyConfigList() == null){
					statusLed.setImageResource(R.drawable.led_disconnected);
					statusText.setText(getString(R.string.no_account));
				} else {
					statusLed.setVisibility(View.VISIBLE);
				}

				if (lc.getDefaultProxyConfig() != null && lc.getDefaultProxyConfig().equals(proxy)) {
					statusLed.setImageResource(getStatusIconResource(state, true));
					statusText.setText(getStatusIconText(state));
				} else if(lc.getDefaultProxyConfig() == null) {
					statusLed.setImageResource(getStatusIconResource(state, true));
					statusText.setText(getStatusIconText(state));
				}

				try {
					statusText.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							lc.refreshRegisters();
						}
					});
				} catch (IllegalStateException ise) {}
			}
		};

		LinphoneCore lc = RedfoxManager.getLcIfManagerNotDestroyedOrNull();
		if (lc != null) {
			lc.addListener(mListener);
			LinphoneProxyConfig lpc = lc.getDefaultProxyConfig();
			if (lpc != null) {
				mListener.registrationState(lc, lpc, lpc.getState(), null);
			}
		}

        return view;
    }

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		isAttached = true;

		if (activity instanceof MainActivity) {
			((MainActivity) activity).updateStatusFragment(this);
			isInCall = false;
		} else if (activity instanceof CallActivity) {
			((CallActivity) activity).updateStatusFragment(this);
			isInCall = true;
		} else if (activity instanceof AssistantActivity) {
//			((AssistantActivity) activity).updateStatusFragment(this);
			isInCall = false;
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		isAttached = false;
	}

	private int getStatusIconResource(LinphoneCore.RegistrationState state, boolean isDefaultAccount) {
		try {
			LinphoneCore lc = RedfoxManager.getLcIfManagerNotDestroyedOrNull();
			boolean defaultAccountConnected = (isDefaultAccount && lc != null && lc.getDefaultProxyConfig() != null && lc.getDefaultProxyConfig().isRegistered()) || !isDefaultAccount;
			if (state == RegistrationState.RegistrationOk && defaultAccountConnected) {
				return R.drawable.led_connected;
			} else if (state == RegistrationState.RegistrationProgress) {
				return R.drawable.led_inprogress;
			} else if (state == RegistrationState.RegistrationFailed) {
				return R.drawable.led_error;
			} else {
				return R.drawable.led_disconnected;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return R.drawable.led_disconnected;
	}

	private String getStatusIconText(LinphoneCore.RegistrationState state) {
		Context context = getActivity();
		if (!isAttached && MainActivity.isInstanciated())
			context = MainActivity.instance();
		else if (!isAttached && RedfoxService.isReady())
			context = RedfoxService.instance();
		
		try {
			if (state == RegistrationState.RegistrationOk && RedfoxManager.getLcIfManagerNotDestroyedOrNull().getDefaultProxyConfig().isRegistered()) {
				return context.getString(R.string.status_connected);
			} else if (state == RegistrationState.RegistrationProgress) {
				return context.getString(R.string.status_in_progress);
			} else if (state == RegistrationState.RegistrationFailed) {
				return context.getString(R.string.status_error);
			} else {
				return context.getString(R.string.status_not_connected);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return context.getString(R.string.status_not_connected);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		LinphoneCore lc = RedfoxManager.getLcIfManagerNotDestroyedOrNull();
		if(lc != null) {
			LinphoneCall call = lc.getCurrentCall();
			if (isInCall && (call != null || lc.getConferenceSize() > 1 || lc.getCallsNb() > 0)) {
				// We are obviously connected
				if(lc.getDefaultProxyConfig() == null){
					statusLed.setImageResource(R.drawable.led_disconnected);
					statusText.setText(getString(R.string.no_account));
				} else {
					statusLed.setImageResource(getStatusIconResource(lc.getDefaultProxyConfig().getState(),true));
					statusText.setText(getStatusIconText(lc.getDefaultProxyConfig().getState()));
				}
			}
		} else {
			statusText.setVisibility(View.VISIBLE);
		}
	}
	
	@Override
	public void onPause() {
		super.onPause();
		
		if (mCallQualityUpdater != null) {
			refreshHandler.removeCallbacks(mCallQualityUpdater);
			mCallQualityUpdater = null;
		}
	}
	
	@Override
	public void onDestroy() {
		LinphoneCore lc = RedfoxManager.getLcIfManagerNotDestroyedOrNull();
		if (lc != null) {
			lc.removeListener(mListener);
		}
		
		super.onDestroy();
	}
}
