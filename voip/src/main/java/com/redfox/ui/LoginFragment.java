package com.redfox.ui;


import android.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import org.linphone.core.LinphoneAddress;


public class LoginFragment extends Fragment implements OnClickListener, TextWatcher {
	private EditText login, password, domain;
	private RadioGroup transports;
	private Button apply;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.assistant_login, container, false);
		
		login = (EditText) view.findViewById(R.id.assistant_username);
		login.addTextChangedListener(this);
		password = (EditText) view.findViewById(R.id.assistant_password);
		password.addTextChangedListener(this);
		domain = (EditText) view.findViewById(R.id.assistant_domain);
		domain.addTextChangedListener(this);
		transports = (RadioGroup) view.findViewById(R.id.assistant_transports);
		apply = (Button) view.findViewById(R.id.assistant_apply);
		apply.setEnabled(false);
		apply.setOnClickListener(this);
		
		return view;
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		
		if (id == R.id.assistant_apply) {
			if (login.getText() == null || login.length() == 0 || password.getText() == null || password.length() == 0 || domain.getText() == null || domain.length() == 0) {
				Toast.makeText(getActivity(), getString(R.string.first_launch_no_login_password), Toast.LENGTH_LONG).show();
				return;
			}

			LinphoneAddress.TransportType transport;
			if(transports.getCheckedRadioButtonId() == R.id.transport_udp){
				transport = LinphoneAddress.TransportType.LinphoneTransportUdp;
			} else {
				if(transports.getCheckedRadioButtonId() == R.id.transport_tcp){
					transport = LinphoneAddress.TransportType.LinphoneTransportTcp;
				} else {
					transport = LinphoneAddress.TransportType.LinphoneTransportTls;
				}
			}

			AssistantActivity.instance().genericLogIn(login.getText().toString(), password.getText().toString(), login.getText().toString(), domain.getText().toString(), transport);
			AssistantActivity.instance().finish();
		}
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		apply.setEnabled(!login.getText().toString().isEmpty() && !password.getText().toString().isEmpty() && !domain.getText().toString().isEmpty());
	}

	@Override
	public void afterTextChanged(Editable s) {

	}
}
