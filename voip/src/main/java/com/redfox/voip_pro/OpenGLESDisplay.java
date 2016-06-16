package com.redfox.voip_pro;

public class OpenGLESDisplay {
	public static native void init(int ptr, int width, int height);
    public static native void render(int ptr);
}
