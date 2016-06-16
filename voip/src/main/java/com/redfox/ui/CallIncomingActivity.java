package com.redfox.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.redfox.voip_pro.RedfoxManager;
import com.redfox.voip_pro.RedfoxPreferences;
import com.redfox.voip_pro.RedfoxService;
import com.redfox.voip_pro.RedfoxUtils;

import org.linphone.core.LinphoneAddress;
import org.linphone.core.LinphoneCall;
import org.linphone.core.LinphoneCall.State;
import org.linphone.core.LinphoneCallParams;
import org.linphone.core.LinphoneCore;
import org.linphone.core.LinphoneCoreListenerBase;

import java.util.List;

public class CallIncomingActivity extends Activity {

	private static CallIncomingActivity instance;

	private TextView number;
	private ImageView accept, decline;
	private LinphoneCall mCall;
	private LinphoneCoreListenerBase mListener;
	private LinearLayout acceptUnlock;
	private LinearLayout declineUnlock;
	private boolean isActive;
	private float answerX;
	private float declineX;

	public static CallIncomingActivity instance() {
		return instance;
	}

	public static boolean isInstanciated() {
		return instance != null;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(com.redfox.ui.R.layout.call_incoming);

		number = (TextView) findViewById(com.redfox.ui.R.id.contact_number);

		// set this flag so this activity will stay in front of the keyguard
		int flags = WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON;
		getWindow().addFlags(flags);

		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
			isActive = pm.isInteractive();
		} else {
			isActive = pm.isScreenOn();
		}

		final int screenWidth = getResources().getDisplayMetrics().widthPixels;

		accept = (ImageView) findViewById(com.redfox.ui.R.id.accept);
		decline = (ImageView) findViewById(com.redfox.ui.R.id.decline);
		accept.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(isActive) {
					answer();
				} else {
					decline.setVisibility(View.GONE);
					acceptUnlock.setVisibility(View.VISIBLE);
				}
			}
		});

		if(!isActive) {
			accept.setOnTouchListener(new View.OnTouchListener() {
				@Override
				public boolean onTouch(View view, MotionEvent motionEvent) {
					float curX;
					switch (motionEvent.getAction()) {
						case MotionEvent.ACTION_DOWN:
							acceptUnlock.setVisibility(View.VISIBLE);
							decline.setVisibility(View.GONE);
							answerX = motionEvent.getX();
							break;
						case MotionEvent.ACTION_MOVE:
							curX = motionEvent.getX();
							if((answerX - curX) >= 0)
								view.scrollBy((int) (answerX - curX), view.getScrollY());
							answerX = curX;
							if (curX < screenWidth/4) {
								answer();
								return true;
							}
							break;
						case MotionEvent.ACTION_UP:
							view.scrollTo(0, view.getScrollY());
							decline.setVisibility(View.VISIBLE);
							acceptUnlock.setVisibility(View.GONE);
							break;
					}
					return true;
				}
			});

			decline.setOnTouchListener(new View.OnTouchListener() {
				@Override
				public boolean onTouch(View view, MotionEvent motionEvent) {
					float curX;
					switch (motionEvent.getAction()) {
						case MotionEvent.ACTION_DOWN:
							declineUnlock.setVisibility(View.VISIBLE);
							accept.setVisibility(View.GONE);
							declineX = motionEvent.getX();
							break;
						case MotionEvent.ACTION_MOVE:
							curX = motionEvent.getX();
							view.scrollBy((int) (declineX - curX), view.getScrollY());
							declineX = curX;
							if (curX > (screenWidth/2)){
								decline();
								return true;
							}
							break;
						case MotionEvent.ACTION_UP:
							view.scrollTo(0, view.getScrollY());
							accept.setVisibility(View.VISIBLE);
							declineUnlock.setVisibility(View.GONE);
							break;

					}
					return true;
				}
			});
		}

		decline.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(isActive) {
					decline();
				} else {
					accept.setVisibility(View.GONE);
					acceptUnlock.setVisibility(View.VISIBLE);
				}
			}
		});




		mListener = new LinphoneCoreListenerBase(){
			@Override
			public void callState(LinphoneCore lc, LinphoneCall call, State state, String message) {
				if (call == mCall && State.CallEnd == state) {
					finish();
				}
				if (state == State.StreamsRunning) {
					// The following should not be needed except some devices need it (e.g. Galaxy S).
					RedfoxManager.getLc().enableSpeaker(RedfoxManager.getLc().isSpeakerEnabled());
				}
			}
		};


		super.onCreate(savedInstanceState);
		instance = this;
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
				if (State.IncomingReceived == call.getState()) {
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
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (RedfoxManager.isInstanciated() && (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_HOME)) {
			RedfoxManager.getLc().terminateCall(mCall);
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}

	private void decline() {
		RedfoxManager.getLc().terminateCall(mCall);
		finish();
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

	private void answer() {
		LinphoneCallParams params = RedfoxManager.getLc().createCallParams(mCall);

		boolean isLowBandwidthConnection = !RedfoxUtils.isHighBandwidthConnection(RedfoxService.instance().getApplicationContext());

		if (params != null) {
			params.enableLowBandwidth(isLowBandwidthConnection);
		}

		if (params == null || !RedfoxManager.getInstance().acceptCallWithParams(mCall, params)) {
			// the above method takes care of Samsung Galaxy S
			Toast.makeText(this, com.redfox.ui.R.string.couldnt_accept_call, Toast.LENGTH_LONG).show();
		} else {
//			if (!MainActivity.isInstanciated()) {
//				return;
//			}
			final LinphoneCallParams remoteParams = mCall.getRemoteParams();
			if (remoteParams != null && remoteParams.getVideoEnabled() && RedfoxPreferences.instance().shouldAutomaticallyAcceptVideoRequests()) {
				startVideoActivity(mCall);
			} else {
				startIncallActivity(mCall);
			}
			finish();
		}
	}
}