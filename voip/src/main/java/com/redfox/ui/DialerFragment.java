package com.redfox.ui;


import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.redfox.voip_pro.ContactsManager;
import com.redfox.voip_pro.RedfoxManager;
import com.redfox.voip_pro.RedfoxService;


public class DialerFragment extends Fragment {
	private static DialerFragment instance;

	private EditText mAddress;
	private ImageView mCall;
	private Button mRemove, mQuit;

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		instance = this;
        View view = inflater.inflate(R.layout.dialer, container, false);

		mAddress = (EditText) view.findViewById(R.id.address);

		mCall = (ImageView) view.findViewById(R.id.call);
		mCall.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					if (!RedfoxManager.getInstance().acceptCallIfIncomingPending()) {
						if (mAddress.getText().length() > 0) {
							RedfoxManager.getInstance().newOutgoingCall(mAddress.getText().toString(), mAddress.getText().toString());
						}
					}
				} catch (Exception e) {
					RedfoxManager.getInstance().terminateCall();
				}
			}
		});

		mCall.setImageResource(R.drawable.call_audio_start);

		mRemove = (Button) view.findViewById(R.id.remove);
		mRemove.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				MainActivity.instance().removeAccount();
			}
		});

		mQuit = (Button) view.findViewById(R.id.quit);
		mQuit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				MainActivity.instance().quit();
			}
		});

		return view;
    }

	/**
	 * @return null if not ready yet
	 */
	public static DialerFragment instance() {
		return instance;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		if (MainActivity.isInstanciated()) {
			MainActivity.instance().selectMenu(MainActivity.FragmentsAvailable.DIALER);
			MainActivity.instance().updateDialerFragment(this);
			MainActivity.instance().showStatusBar();
		}

	}
	
	public void newOutgoingCall(String numberOrSipAddress) {
		RedfoxManager.getInstance().newOutgoingCall(mAddress.getText().toString(), mAddress.getText().toString());
	}
	
	public void newOutgoingCall(Intent intent) {
		if (intent != null && intent.getData() != null) {
			String scheme = intent.getData().getScheme();
			if (scheme.startsWith("imto")) {
				mAddress.setText("sip:" + intent.getData().getLastPathSegment());
			} else if (scheme.startsWith("call") || scheme.startsWith("sip")) {
				mAddress.setText(intent.getData().getSchemeSpecificPart());
			} else {
				Uri contactUri = intent.getData();
				String address = ContactsManager.getAddressOrNumberForAndroidContact(RedfoxService.instance().getContentResolver(), contactUri);
				if(address != null) {
					mAddress.setText(address);
				} else {
					mAddress.setText(intent.getData().getSchemeSpecificPart());
				}
			}

			intent.setData(null);

			RedfoxManager.getInstance().newOutgoingCall(mAddress.getText().toString(), mAddress.getText().toString());
		}
	}
}
