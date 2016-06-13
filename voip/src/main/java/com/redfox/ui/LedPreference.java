package com.redfox.ui;


import android.content.Context;
import android.preference.Preference;
import android.view.View;
import android.widget.ImageView;


public class LedPreference extends Preference
{
	private int ledDrawable;
	
	public LedPreference(Context context) {
        super(context);
        ledDrawable = R.drawable.led_disconnected;
        this.setWidgetLayoutResource(R.layout.preference_led);
    }

    @Override
    protected void onBindView(final View view) {
        super.onBindView(view);

        final ImageView imageView = (ImageView) view.findViewById(R.id.led);
        if (imageView != null) {
            imageView.setImageResource(ledDrawable);
        }
    }

    public void setLed(int led) {
    	ledDrawable = led;
        notifyChanged();
    }
}
