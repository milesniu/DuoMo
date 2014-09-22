package com.wz.media;

import java.io.IOException;
import java.net.SocketException;

import com.wz.net.RtpPacket;
import com.wz.net.RtpSocket;
import com.wz.net.UserDatagramSocket;
import com.wz.codecs.Codecs;

import android.content.ContentResolver;
import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.ToneGenerator;
import android.os.Build;
import android.os.SystemClock;
import android.view.KeyEvent;

/**
 * RtpStreamReceiver is a generic stream receiver. It receives packets from RTP
 * and writes them into an OutputStream.
 */
public class RtpStreamReceiver extends Thread {

	/** Whether working in debug mode. */
	public static boolean DEBUG = true;

	/** Payload type */
	Codecs.Map p_type;

	static String codec = "";

	/** Size of the read buffer */
	public static final int BUFFER_SIZE = 1024;

	/** Maximum blocking time, spent waiting for reading new bytes [milliseconds] */
	public static final int SO_TIMEOUT = 1000;

	/** The RtpSocket */
	RtpSocket rtp_socket = null;

	/** Whether it is running */
	boolean running;
	AudioManager am;
	ContentResolver cr;
	public static int speakermode = -1;
	public static boolean bluetoothmode;
	
	public static Context mContext = null;
	
	//test
	CallRecorder mRec = new CallRecorder(null, 8000);
	/**
	 * Constructs a RtpStreamReceiver.
	 * 
	 * @param output_stream
	 *            the stream sink
	 * @param socket
	 *            the local receiver SipdroidSocket
	 */
	public RtpStreamReceiver(Context context, UserDatagramSocket socket, Codecs.Map payload_type) {
		init(socket);
		p_type = payload_type;
		mContext = context;
	}

	/** Inits the RtpStreamReceiver */
	private void init(UserDatagramSocket socket) {
		if (socket != null)
			rtp_socket = new RtpSocket(socket);
	}

	/** Whether is running */
	public boolean isRunning() {
		return running;
	}

	/** Stops running */
	public void halt() {
		running = false;
	}
	
	void bluetooth() {
		speaker(AudioManager.MODE_IN_CALL);
		enableBluetooth(true);
		RtpStreamSender.micgain = 0;
	}
	
	static boolean was_enabled;
	
	static void enableBluetooth(boolean mode) {
		if (isBluetoothAvailable()) {
			if (mode) was_enabled = true;
			Bluetooth.enable(mode);
		}
	}
	
	void cleanupBluetooth() {
		if (was_enabled && Integer.parseInt(Build.VERSION.SDK) == 8) {
			enableBluetooth(false);
			try {
				sleep(3000);
			} catch (InterruptedException e) {
			}
		}
	}
	
	public static boolean isBluetoothAvailable() {
		if (!isBluetoothSupported())
			return false;
		return Bluetooth.isAvailable();
	}
	
	public static boolean isBluetoothSupported() {
		if (Integer.parseInt(Build.VERSION.SDK) < 8)
			return false;
		return Bluetooth.isSupported();
	}
	
	public int speaker(int mode) {
		int old = speakermode;
		if (mode == old)
			return old;
		enableBluetooth(false);
		setMode(speakermode = mode);
		setCodec();
		return old;
	}

	public static void setMode(int mode) {
		AudioManager am = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
		am.setSpeakerphoneOn(mode == AudioManager.MODE_NORMAL);
	}
	
	static ToneGenerator ringbackPlayer;
	static int oldvol = -1;
	
	static int stream() {
		return speakermode == AudioManager.MODE_IN_CALL?AudioManager.STREAM_VOICE_CALL:AudioManager.STREAM_MUSIC;
	}
	
	double smin = 200,s;
	public static int nearend;
	
	void calc(short[] lin,int off,int len) {
		int i,j;
		double sm = 30000,r;
		
		for (i = 0; i < len; i += 5) {
			j = lin[i+off];
			s = 0.03*Math.abs(j) + 0.97*s;
			if (s < sm) sm = s;
			if (s > smin) nearend = 6000*mu/5;
			else if (nearend > 0) nearend--;
		}
		for (i = 0; i < len; i++) {
			j = lin[i+off];
			if (j > 6550)
				lin[i+off] = 6550*5;
			else if (j < -6550)
				lin[i+off] = -6550*5;
			else
				lin[i+off] = (short)(j*5);
		}
		r = (double)len/(100000*mu);
		if (sm > 2*smin || sm < smin/2)
			smin = sm*r + smin*(1-r);
	}
	
	void calc2(short[] lin,int off,int len) {
		int i,j;
		
		for (i = 0; i < len; i++) {
			j = lin[i+off];
			if (j > 16350)
				lin[i+off] = 16350<<1;
			else if (j < -16350)
				lin[i+off] = -16350<<1;
			else
				lin[i+off] = (short)(j<<1);
		}
	}

	static long down_time;
	static float gain,ogain;
	
	public static void adjust(int keyCode,boolean down) {
        AudioManager mAudioManager = (AudioManager) mContext.getSystemService(
                Context.AUDIO_SERVICE);
        
		if (RtpStreamReceiver.speakermode == AudioManager.MODE_NORMAL)
			if (down ^ mAudioManager.getStreamVolume(stream()) == 0)
				mAudioManager.setStreamMute(stream(), down);
		if (down && down_time == 0)
			down_time = SystemClock.elapsedRealtime();
		if (!down ^ RtpStreamReceiver.speakermode != AudioManager.MODE_NORMAL)
			if (SystemClock.elapsedRealtime()-down_time < 500) {
				if (!down)
					down_time = 0;
				if (ogain > 1)
					if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
						if (gain != ogain) {
							gain = ogain;
							return;
						}
						if (mAudioManager.getStreamVolume(stream()) ==
							mAudioManager.getStreamMaxVolume(stream())) return;
						gain = ogain/2;
					} else {
						if (gain == ogain) {
							gain = ogain/2;
							return;
						}
						if (mAudioManager.getStreamVolume(stream()) == 0) return;
						gain = ogain;
					}
		        mAudioManager.adjustStreamVolume(
		                    stream(),
		                    keyCode == KeyEvent.KEYCODE_VOLUME_UP
		                            ? AudioManager.ADJUST_RAISE
		                            : AudioManager.ADJUST_LOWER,
		                    AudioManager.FLAG_SHOW_UI);
			}
		if (!down)
			down_time = 0;
	}
	
	public static float good, late, lost, loss, loss2;
	double avgheadroom,devheadroom;
	int avgcnt;
	public static int timeout;
	int seq;
	
	void empty() {
		try {
			rtp_socket.getDatagramSocket().setSoTimeout(1);
			for (;;)
				rtp_socket.receive(rtp_packet);
		} catch (SocketException e2) {
			e2.printStackTrace();
		} catch (IOException e) {
		}
		try {
			rtp_socket.getDatagramSocket().setSoTimeout(SO_TIMEOUT);
		} catch (SocketException e2) {
			e2.printStackTrace();
		}
		seq = 0;
	}
	
	RtpPacket rtp_packet;
	AudioTrack track;
	int maxjitter,minjitter,minjitteradjust;
	int cnt,cnt2,user,luser,luser2,lserver;
	public static int jitter,mu;
	
	void setCodec() {
		synchronized (this) {
			AudioTrack oldtrack;
			
			p_type.codec.init();
			codec = p_type.codec.getTitle();
			mu = p_type.codec.samp_rate()/8000;
			maxjitter = AudioTrack.getMinBufferSize(p_type.codec.samp_rate(), 
					AudioFormat.CHANNEL_CONFIGURATION_MONO, 
					AudioFormat.ENCODING_PCM_16BIT);
			if (maxjitter < 2*2*BUFFER_SIZE*6*mu)
				maxjitter = 2*2*BUFFER_SIZE*6*mu;
			oldtrack = track;
			track = new AudioTrack(stream(), p_type.codec.samp_rate(), AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_16BIT,
					maxjitter*2, AudioTrack.MODE_STREAM);
			maxjitter /= 2*2;
			minjitter = minjitteradjust = 500*mu;
			jitter = 875*mu;
			devheadroom = Math.pow(jitter/5, 2);
			timeout = 1;
			luser = luser2 = -8000*mu;
			cnt = cnt2 = user = lserver = 0;
			if (oldtrack != null) {
				oldtrack.stop();
				oldtrack.release();
			}
		}
	}
	
	void write(short a[],int b,int c) {
		synchronized (this) {
			user += track.write(a,b,c);
		}
	}
	
	void newjitter(boolean inc) {
		 if (good == 0 || lost/good > 0.01)
			 return;
		 int newjitter = (int)Math.sqrt(devheadroom)*5 + (inc?minjitteradjust:0);
		 if (newjitter < minjitter)
			 newjitter = minjitter;
		 if (newjitter > maxjitter)
			 newjitter = maxjitter;
		 if (!inc && (Math.abs(jitter-newjitter) < minjitteradjust || newjitter >= jitter))
			 return;
		 if (inc && newjitter <= jitter)
			 return;
		 jitter = newjitter;
		 late = 0;
		 avgcnt = 0;
		 luser2 = user;
	}
	
	boolean keepon;
	
	/** Runs it in a new Thread. */
	public void run() {

		if (rtp_socket == null) {
			if (DEBUG)
				println("ERROR: RTP socket is null");
			return;
		}

		byte[] buffer = new byte[BUFFER_SIZE+12];
		rtp_packet = new RtpPacket(buffer, 0);

		if (DEBUG)
			println("Reading blocks of max " + buffer.length + " bytes");

		running = true;

		android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_AUDIO);
		am = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);

		am.setVibrateSetting(AudioManager.VIBRATE_TYPE_RINGER,AudioManager.VIBRATE_SETTING_OFF);
		am.setVibrateSetting(AudioManager.VIBRATE_TYPE_NOTIFICATION,AudioManager.VIBRATE_SETTING_OFF);
		if (oldvol == -1) oldvol = am.getStreamVolume(AudioManager.STREAM_MUSIC);
		setCodec();
		short lin[] = new short[BUFFER_SIZE];
		short lin2[] = new short[BUFFER_SIZE];
		int server, headroom, todo, len = 0, m = 1, expseq, getseq, vm = 1, gap, gseq;
		track.play();
		System.gc();
		empty();

		while (running) {
			try {
				rtp_socket.receive(rtp_packet);
				if (timeout != 0) {
					track.pause();
					for (int i = maxjitter*4; i > 0; i -= BUFFER_SIZE)
						write(lin2,0,i>BUFFER_SIZE?BUFFER_SIZE:i);
					cnt += maxjitter*2;
					track.play();
					empty();
				}
				timeout = 0;
			} catch (IOException e) {
				rtp_socket.getDatagramSocket().disconnect();
			}
			if (running && timeout == 0) {		
				 gseq = rtp_packet.getSequenceNumber();
				 if (seq == gseq) {
					 m++;
					 continue;
				 }
				 gap = (gseq - seq) & 0xff;
				 if (gap > 240)
					 continue;
				 server = track.getPlaybackHeadPosition();
				 headroom = user-server;
				 
				 if (headroom > 2*jitter)
					 cnt += len;
				 else
					 cnt = 0;
				 
				 if (lserver == server)
					 cnt2++;
				 else
					 cnt2 = 0;

				 if (cnt <= 500*mu || cnt2 >= 2 || headroom - jitter < len ||
						 p_type.codec.number() != 8 || p_type.codec.number() != 0) {
					 if (rtp_packet.getPayloadType() != p_type.number && p_type.change(rtp_packet.getPayloadType())) {
						 setCodec();
						 codec = p_type.codec.getTitle();
					 }
					 len = p_type.codec.decode(buffer, lin, rtp_packet.getPayloadLength());

					 // Call recording: Save incoming.
					 // Data is in buffer lin, from 0 to len.
					 //if (mRec != null)
					 //	 mRec.writeIncoming(lin, 0, len);
					 
//		 			 if (speakermode == AudioManager.MODE_NORMAL)
//		 				 calc2(lin,0,len);
//		 			 else if (gain > 1)
//		 			 	 calc2(lin,0,len);
				 }
				 
				 if (cnt == 0)
					 avgheadroom = avgheadroom * 0.99 + (double)headroom * 0.01;
				 if (avgcnt++ > 300)
					 devheadroom = devheadroom * 0.999 + Math.pow(Math.abs(headroom - avgheadroom),2) * 0.001;

				 if (headroom < 250*mu) { 
	 				 late++;
	 				 avgcnt += 10;
	 				 if (avgcnt > 400)
	 					 newjitter(true);
					 todo = jitter - headroom;
					 write(lin2,0,todo>BUFFER_SIZE?BUFFER_SIZE:todo);
				 }

				 if (cnt > 500*mu && cnt2 < 2) {
					 todo = headroom - jitter;
					 if (todo < len)
						 write(lin,todo,len-todo);
				 } else
					 write(lin,0,len);
				 				 
				 if (seq != 0) {
					 getseq = gseq&0xff;
					 expseq = ++seq&0xff;
					 if (m == RtpStreamSender.m) vm = m;
					 gap = (getseq - expseq) & 0xff;
					 if (gap > 0) {
						 if (gap > 100) gap = 1;
						 loss += gap;
						 lost += gap;
						 good += gap - 1;
						 loss2++;
					 } else {
						 if (m < vm) {
							 loss++;
							 loss2++;
						 }
					 }
					 good++;
					 if (good > 110) {
						 good *= 0.99;
						 lost *= 0.99;
						 loss *= 0.99;
						 loss2 *= 0.99;
						 late *= 0.99;
					 }
				 }
				 m = 1;
				 seq = gseq;

				 lserver = server;
			}
		}

		track.stop();
		track.release();
		am.setStreamVolume(AudioManager.STREAM_MUSIC,oldvol,0);
		am.setStreamVolume(AudioManager.STREAM_MUSIC,oldvol,0);
		oldvol = -1;
		p_type.codec.close();
		rtp_socket.close();
		rtp_socket = null;
		codec = "";

		if (DEBUG)
			println("rtp receiver terminated");

		cleanupBluetooth();
	}

	/** Debug output */
	private static void println(String str) {
		System.out.println("RtpStreamReceiver: " + str);
	}

	public static int byte2int(byte b) { // return (b>=0)? b : -((b^0xFF)+1);
		// return (b>=0)? b : b+0x100;
		return (b + 0x100) % 0x100;
	}

	public static int byte2int(byte b1, byte b2) {
		return (((b1 + 0x100) % 0x100) << 8) + (b2 + 0x100) % 0x100;
	}

	public static String getCodec() {
		return codec;
	}
}
