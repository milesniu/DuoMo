package com.wz.codecs;


class G722 extends CodecBase implements Codec {

	/*
		Acceptable values for bitrate are
		48000, 56000 or 64000
 	 */
	private static final int DEFAULT_BITRATE = 64000;

	G722() {
		CODEC_NAME = "G722 HD Voice";
		CODEC_USER_NAME = "G722";
		CODEC_DESCRIPTION = "64kbit";
		CODEC_NUMBER = 9;
		CODEC_DEFAULT_SETTING = "wlanor3g";
		CODEC_SAMPLE_RATE = 16000;
		CODEC_FRAME_SIZE = 320;		
		super.update();
	}


	void load() {
		try {
			System.loadLibrary("g722_jni");
			super.load();
		} catch (Throwable e) {
			e.printStackTrace();
		}
    
	}  
 
	public native int open(int brate);
	public native int decode(byte encoded[], short lin[], int size);
	public native int encode(short lin[], int offset, byte encoded[], int size);
	public native void close();

	public void init() {
		load();
		if (isLoaded())
			open(DEFAULT_BITRATE);
	}
}
