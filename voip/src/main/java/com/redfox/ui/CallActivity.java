package com.redfox.ui;


import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.redfox.voip_pro.RedfoxManager;
import com.redfox.voip_pro.RedfoxPreferences;
import com.redfox.voip_pro.RedfoxUtils;

import org.linphone.core.LinphoneCall;
import org.linphone.core.LinphoneCall.State;
import org.linphone.core.LinphoneCallParams;
import org.linphone.core.LinphoneCore;
import org.linphone.core.LinphoneCoreListenerBase;
import org.linphone.core.LinphonePlayer;
import org.linphone.mediastream.video.capture.hwconf.AndroidCameraConfiguration;


public class CallActivity extends Activity implements OnClickListener{
	private final static String TAG = "CallActivity";
	private final static int SECONDS_BEFORE_HIDING_CONTROLS = 4000;
	private final static int SECONDS_BEFORE_DENYING_CALL_UPDATE = 30000;
	private static final int PERMISSIONS_REQUEST_CAMERA = 202;
	private static final int PERMISSIONS_ENABLED_CAMERA = 203;

	private static CallActivity instance;

	private Handler mControlsHandler = new Handler();
	private Runnable mControls;
	private ImageView switchCamera;
	private RelativeLayout mActiveCallHeader;
	private ImageView hangUp;
	private LinearLayout callInfo;
	private StatusFragment status;
	private CallVideoFragment videoCallFragment;
	private LinearLayout mControlsLayout;
	private int cameraNumber;
	private CountDownTimer timer;
	private Sensor mProximity;
	private LinphoneCoreListenerBase mListener;

	public static CallActivity instance() {
		return instance;
	}

	public static boolean isInstanciated() {
		return instance != null;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		instance = this;

		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
		setContentView(R.layout.call);

		cameraNumber = AndroidCameraConfiguration.retrieveCameras().length;

		mListener = new LinphoneCoreListenerBase(){
			@Override
			public void callState(LinphoneCore lc, final LinphoneCall call, LinphoneCall.State state, String message) {
				if (RedfoxManager.getLc().getCallsNb() == 0) {
					finish();
					return;
				}

				if (state == State.IncomingReceived) {
					startIncomingCallActivity();
					return;
				}

				if (state == State.Resuming) {
					if(RedfoxPreferences.instance().isVideoEnabled()){
						if(call.getCurrentParamsCopy().getVideoEnabled()){
							showVideoView();
						}
					}
				}

				if (state == State.StreamsRunning) {
					switchVideo(isVideoEnabled(call));
				}

				if (state == State.CallUpdatedByRemote) {
					// If the correspondent proposes video while audio call
					boolean videoEnabled = RedfoxPreferences.instance().isVideoEnabled();
					if (!videoEnabled) {
						acceptCallUpdate(false);
					}

					boolean remoteVideo = call.getRemoteParams().getVideoEnabled();
					boolean localVideo = call.getCurrentParamsCopy().getVideoEnabled();
					boolean autoAcceptCameraPolicy = RedfoxPreferences.instance().shouldAutomaticallyAcceptVideoRequests();
					if (remoteVideo && !localVideo && !autoAcceptCameraPolicy && !RedfoxManager.getLc().isInConference()) {
							showAcceptCallUpdateDialog();
							timer = new CountDownTimer(SECONDS_BEFORE_DENYING_CALL_UPDATE, 1000) {
								public void onTick(long millisUntilFinished) { }
								public void onFinish() {
									//TODO dismiss dialog
									acceptCallUpdate(false);
								}
							}.start();
					}
				}

			}
		};

		if (findViewById(R.id.fragmentContainer) != null) {
			initUI();

			if (savedInstanceState != null) {
				return;
			}

			Fragment callFragment = new CallVideoFragment();
			videoCallFragment = (CallVideoFragment) callFragment;
			displayVideoCall(false);

			if (callFragment != null) {
				callFragment.setArguments(getIntent().getExtras());
				getFragmentManager().beginTransaction().add(R.id.fragmentContainer, callFragment).commitAllowingStateLoss();
			}
		}
	}

	private boolean isVideoEnabled(LinphoneCall call) {
		if(call != null){
			return call.getCurrentParamsCopy().getVideoEnabled();
		}
		return false;
	}

	private boolean isTablet() {
		return getResources().getBoolean(R.bool.isTablet);
	}

	private void initUI() {
		//BottonBar
		hangUp = (ImageView) findViewById(R.id.hang_up);
		hangUp.setOnClickListener(this);

		//Active Call
		callInfo = (LinearLayout) findViewById(R.id.active_call_info);

		mActiveCallHeader = (RelativeLayout) findViewById(R.id.active_call);

		switchCamera = (ImageView) findViewById(R.id.switchCamera);
		switchCamera.setOnClickListener(this);

		mControlsLayout = (LinearLayout) findViewById(R.id.menu);

		RedfoxManager.getInstance().changeStatusToOnThePhone();
	}
//
//	public void checkAndRequestPermission(String permission, int result) {
//		if (getPackageManager().checkPermission(permission, getPackageName()) != PackageManager.PERMISSION_GRANTED) {
//			if (!ActivityCompat.shouldShowRequestPermissionRationale(this,permission)){
//				ActivityCompat.requestPermissions(this, new String[]{permission}, result);
//			}
//		}
//	}
//
//	@Override
//	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//		switch (requestCode) {
//			case PERMISSIONS_REQUEST_CAMERA:
//				UIThreadDispatcher.dispatch(new Runnable() {
//					@Override
//					public void run() {
//						acceptCallUpdate(true);
//					}
//				});
//				break;
//			case PERMISSIONS_ENABLED_CAMERA:
//				UIThreadDispatcher.dispatch(new Runnable() {
//					@Override
//					public void run() {
//						enabledOrDisabledVideo(false);
//					}
//				});
//				break;
//		}
//		RedfoxPreferences.instance().neverAskCameraPerm();
//	}

	public void updateStatusFragment(StatusFragment statusFragment) {
		status = statusFragment;
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();

		if (id == R.id.hang_up) {
			hangUp();
		}
		else if (id == R.id.switchCamera) {
			if (videoCallFragment != null) {
				videoCallFragment.switchCamera();
			}
		}
	}

	private void enabledOrDisabledVideo(final boolean isVideoEnabled) {
		final LinphoneCall call = RedfoxManager.getLc().getCurrentCall();
		if (call == null) {
			return;
		}

		if (isVideoEnabled) {
			LinphoneCallParams params = call.getCurrentParamsCopy();
			params.setVideoEnabled(false);
			RedfoxManager.getLc().updateCall(call, params);
		} else {
			if (!call.getRemoteParams().isLowBandwidthEnabled()) {
				RedfoxManager.getInstance().addVideo();
			}
		}
	}

	private void switchVideo(final boolean displayVideo) {
		final LinphoneCall call = RedfoxManager.getLc().getCurrentCall();
		if (call == null) {
			return;
		}

		//Check if the call is not terminated
		if(call.getState() == State.CallEnd || call.getState() == State.CallReleased) return;
		
		if (displayVideo) {
			if (!call.getRemoteParams().isLowBandwidthEnabled()) {
				RedfoxManager.getInstance().addVideo();
				if (videoCallFragment == null || !videoCallFragment.isVisible())
					showVideoView();
			}
		}
	}

	private void showVideoView() {
		videoCallFragment = new CallVideoFragment();

		FragmentTransaction transaction = getFragmentManager().beginTransaction();
		transaction.replace(R.id.fragmentContainer, videoCallFragment);
		try {
			transaction.commitAllowingStateLoss();
		} catch (Exception e) {
		}

		hideStatusBar();
	}

	private void hangUp() {
		LinphoneCore lc = RedfoxManager.getLc();
		LinphoneCall currentCall = lc.getCurrentCall();

		if (currentCall != null) {
			lc.terminateCall(currentCall);
		} else if (lc.isInConference()) {
			lc.terminateConference();
		} else {
			lc.terminateAllCalls();
		}
	}

	public void displayVideoCall(boolean display){
		if(display) {
			showStatusBar();
			mControlsLayout.setVisibility(View.VISIBLE);
			mActiveCallHeader.setVisibility(View.VISIBLE);
			callInfo.setVisibility(View.VISIBLE);
			if (cameraNumber > 1) {
				switchCamera.setVisibility(View.VISIBLE);
			}
		} else {
			hideStatusBar();
			mControlsLayout.setVisibility(View.GONE);
			mActiveCallHeader.setVisibility(View.GONE);
			switchCamera.setVisibility(View.GONE);
		}
	}


	public void displayVideoCallControlsIfHidden() {
		if (mControlsLayout != null) {
			if (mControlsLayout.getVisibility() != View.VISIBLE) {
					displayVideoCall(true);
			}
			resetControlsHidingCallBack();
		}
	}

	public void resetControlsHidingCallBack() {
		if (mControlsHandler != null && mControls != null) {
			mControlsHandler.removeCallbacks(mControls);
		}
		mControls = null;

		if (isVideoEnabled(RedfoxManager.getLc().getCurrentCall()) && mControlsHandler != null) {
			mControlsHandler.postDelayed(mControls = new Runnable() {
				public void run() {
						displayVideoCall(false);
				}
			}, SECONDS_BEFORE_HIDING_CONTROLS);
		}
	}

	public void acceptCallUpdate(boolean accept) {
		if (timer != null) {
			timer.cancel();
		}

		LinphoneCall call = RedfoxManager.getLc().getCurrentCall();
		if (call == null) {
			return;
		}

		LinphoneCallParams params = call.getCurrentParamsCopy();
		if (accept) {
			params.setVideoEnabled(true);
			RedfoxManager.getLc().enableVideo(true, true);
		}

		try {
			RedfoxManager.getLc().acceptCallUpdate(call, params);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void startIncomingCallActivity() {
		startActivity(new Intent(this, CallIncomingActivity.class));
	}

	public void hideStatusBar() {
		if (isTablet()) {
			return;
		}

		findViewById(R.id.status).setVisibility(View.GONE);
		findViewById(R.id.fragmentContainer).setPadding(0, 0, 0, 0);
	}

	public void showStatusBar() {
		if (isTablet()) {
			return;
		}

		if (status != null && !status.isVisible()) {
			// Hack to ensure statusFragment is visible after coming back to
			// dialer from chat
			status.getView().setVisibility(View.VISIBLE);
		}
		findViewById(R.id.status).setVisibility(View.VISIBLE);
		//findViewById(R.id.fragmentContainer).setPadding(0, RedfoxUtils.pixelsToDpi(getResources(), 40), 0, 0);
	}


	private void showAcceptCallUpdateDialog() {
		final Dialog dialog = new Dialog(this);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		Drawable d = new ColorDrawable(getResources().getColor(R.color.colorC));
		d.setAlpha(200);
		dialog.setContentView(R.layout.dialog);
		dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
		dialog.getWindow().setBackgroundDrawable(d);

		TextView customText = (TextView) dialog.findViewById(R.id.customText);
		customText.setText(getResources().getString(R.string.add_video_dialog));
		Button delete = (Button) dialog.findViewById(R.id.delete_button);
		delete.setText(R.string.accept);
		Button cancel = (Button) dialog.findViewById(R.id.cancel);
		cancel.setText(R.string.decline);

		delete.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				if (getPackageManager().checkPermission(Manifest.permission.CAMERA, getPackageName()) == PackageManager.PERMISSION_GRANTED || RedfoxPreferences.instance().cameraPermAsked()) {
					CallActivity.instance().acceptCallUpdate(true);
				} else {
//					checkAndRequestPermission(Manifest.permission.CAMERA, PERMISSIONS_REQUEST_CAMERA);
				}

				dialog.dismiss();
			}
		});

		cancel.setOnClickListener(new

		OnClickListener() {
			@Override
			public void onClick (View view){
				if (CallActivity.isInstanciated()) {
					CallActivity.instance().acceptCallUpdate(false);
				}
				dialog.dismiss();
			}
		});
		dialog.show();
	}

	@Override
	protected void onResume() {
		instance = this;
		super.onResume();

		LinphoneCore lc = RedfoxManager.getLcIfManagerNotDestroyedOrNull();
		if (lc != null) {
			lc.addListener(mListener);
		}

		registerCallDurationTimer(null, lc.getCurrentCall());
		handleViewIntent();
	}

	private void registerCallDurationTimer(View v, LinphoneCall call) {
		int callDuration = call.getDuration();
		if (callDuration == 0 && call.getState() != State.StreamsRunning) {
			return;
		}

		Chronometer timer = null;
		if(v == null){
			timer = (Chronometer) findViewById(R.id.current_call_timer);
		}

		if (timer == null) {
			throw new IllegalArgumentException("no callee_duration view found");
		}

		timer.setBase(SystemClock.elapsedRealtime() - 1000 * callDuration);
		timer.start();
	}

	private void handleViewIntent() {
		Intent intent = getIntent();
		if(intent != null && intent.getAction() == "android.intent.action.VIEW") {
			LinphoneCall call = RedfoxManager.getLc().getCurrentCall();
			if(call != null && isVideoEnabled(call)) {
				LinphonePlayer player = call.getPlayer();
				String path = intent.getData().getPath();
				Log.i(TAG, "Openning " + path);
				int openRes = player.open(path, new LinphonePlayer.Listener() {

					@Override
					public void endOfFile(LinphonePlayer player) {
						player.close();
					}
				});
				if(openRes == -1) {
					String message = "Could not open " + path;
					Log.e(TAG, message);
					Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
					return;
				}
				Log.i(TAG, "Start playing");
				if(player.start() == -1) {
					player.close();
					String message = "Could not start playing " + path;
					Log.e(TAG, message);
					Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
				}
			}
		}
	}

	@Override
	protected void onPause() {
		LinphoneCore lc = RedfoxManager.getLcIfManagerNotDestroyedOrNull();
		if (lc != null) {
			lc.removeListener(mListener);
		}

		super.onPause();

		if (mControlsHandler != null && mControls != null) {
			mControlsHandler.removeCallbacks(mControls);
		}
		mControls = null;
	}

	@Override
	protected void onDestroy() {
		RedfoxManager.getInstance().changeStatusToOnline();

		if (mControlsHandler != null && mControls != null) {
			mControlsHandler.removeCallbacks(mControls);
		}
		mControls = null;
		mControlsHandler = null;

		unbindDrawables(findViewById(R.id.topLayout));
		instance = null;
		super.onDestroy();
		System.gc();
	}

	private void unbindDrawables(View view) {
		if (view.getBackground() != null) {
			view.getBackground().setCallback(null);
		}
		if (view instanceof ImageView) {
			view.setOnClickListener(null);
		}
		if (view instanceof ViewGroup && !(view instanceof AdapterView)) {
			for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
				unbindDrawables(((ViewGroup) view).getChildAt(i));
			}
			((ViewGroup) view).removeAllViews();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (RedfoxUtils.onKeyVolumeAdjust(keyCode)) return true;
		if (RedfoxUtils.onKeyBackGoHome(this, keyCode, event)) return true;
		return super.onKeyDown(keyCode, event);
	}

	public void bindVideoFragment(CallVideoFragment fragment) {
		videoCallFragment = fragment;
	}

	public static Boolean isProximitySensorNearby(final SensorEvent event) {
		float threshold = 4.001f; // <= 4 cm is near

		final float distanceInCm = event.values[0];
		final float maxDistance = event.sensor.getMaximumRange();
		Log.d(TAG, "Proximity sensor report [" + distanceInCm + "] , for max range [" + maxDistance+ "]");

		if (maxDistance <= threshold) {
			// Case binary 0/1 and short sensors
			threshold = maxDistance;
		}
		return distanceInCm < threshold;
	}
}
