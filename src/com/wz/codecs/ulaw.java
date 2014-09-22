package com.wz.codecs;


class ulaw extends CodecBase implements Codec {
	ulaw() {
		CODEC_NAME = "PCMU";
		CODEC_USER_NAME = "PCMU";
		CODEC_DESCRIPTION = "64kbit";
		CODEC_NUMBER = 0;
		CODEC_DEFAULT_SETTING = "wlanor3g";

		load();
	}

	public void init() {
		G711.init();
	}
    
	public int decode(byte enc[], short lin[], int frames) {
		G711.ulaw2linear(enc, lin, frames);

		return frames;
	}

	public int encode(short lin[], int offset, byte enc[], int frames) {
		G711.linear2ulaw(lin, offset, enc, frames);

		return frames;
	}

	public void close() {
	}
}
