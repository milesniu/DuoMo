package com.wz.codecs;


class alaw extends CodecBase implements Codec {
	alaw() {
		CODEC_NAME = "PCMA";
		CODEC_USER_NAME = "PCMA";
		CODEC_DESCRIPTION = "64kbit";
		CODEC_NUMBER = 8;
		CODEC_DEFAULT_SETTING = "wlanor3g";

		load();
	}

	public void init() {
		G711.init();
	}
    
	public int decode(byte enc[], short lin[], int frames) {
		G711.alaw2linear(enc, lin, frames);

		return frames;
	}

	public int encode(short lin[], int offset, byte enc[], int frames) {
		G711.linear2alaw(lin, offset, enc, frames);

		return frames;
	}

	public void close() {
	}
}
