package com.wz.codecs;


class CodecBase {
	protected String CODEC_NAME;
	protected String CODEC_USER_NAME;
	protected int CODEC_NUMBER;
	protected int CODEC_SAMPLE_RATE=8000;		// default for most narrow band codecs
	protected int CODEC_FRAME_SIZE=160;		// default for most narrow band codecs
	protected String CODEC_DESCRIPTION;
	protected String CODEC_DEFAULT_SETTING = "never";

	private boolean loaded = false,failed = false;
	private boolean enabled = false;
	private String value;

	public void update() {
		if (value == null) {
			value = CODEC_DEFAULT_SETTING;
			updateFlags(value);
		}
	}
	
	public String getValue() {
		return value;
	}
	
	void load() {
		update();
		loaded = true;
	}
	
	public int samp_rate() {
		return CODEC_SAMPLE_RATE;
	}
	
	public int frame_size() {
		return CODEC_FRAME_SIZE;
	}

	public boolean isLoaded() {
		return loaded;
	}
    
	public boolean isFailed() {
		return failed;
	}
	
	public void fail() {
		update();
		failed = true;
	}
	
	public void enable(boolean e) {
		enabled = e;
	}

	public boolean isEnabled() {
		return enabled;
	}
	
	public boolean isValid() {
		return true;
	}
		
	public String name() {
		return CODEC_NAME;
	}

	public String key() {
		return CODEC_NAME+"_new";
	}
	
	public String userName() {
		return CODEC_USER_NAME;
	}

	public String getTitle() {
		return CODEC_NAME + " (" + CODEC_DESCRIPTION + ")";
	}

	public int number() {
		return CODEC_NUMBER;
	}

	private void updateFlags(String v) {

		if (v.equals("never")) {
			enabled = false;
		} else {
			enabled = true;
			if (v.equals("wlan")) {
			} else {
			}
			if (v.equals("wlanor3g")) {
			} else {
			}
		}
	}

	public String toString() {
		return "CODEC{ " + CODEC_NUMBER + ": " + getTitle() + "}";
	}
}
