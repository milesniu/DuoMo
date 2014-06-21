package com.miles.ccit.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import com.miles.ccit.util.OverAllData;

public class SocketConnection
{
	private volatile Socket socket;

	private boolean isNetworkConnect = false; // 网络是否已连接
	private static String host;
	private static int port;
	static InputStream inStream = null;
	static OutputStream outStream = null;

	private static SocketConnection socketConnection = null;
	private static Timer heartTimer = null;

	private static Thread receiveThread = null;
	private final ReentrantLock lock = new ReentrantLock();
	private String result;
	private final ConcurrentHashMap<Byte, Object> recMsgMap = new ConcurrentHashMap<Byte, Object>();   
	
	private SocketConnection()
	{
		// Properties conf = new Properties();
		try
		{
			// conf.load(SocketConnection.class.getResourceAsStream("test.conf"));
			// this.timeout = Integer.valueOf(conf.getProperty("timeout"));
			// init(conf.getProperty("ip"),Integer.valueOf(conf.getProperty("port")));
			init(OverAllData.Ipaddress, OverAllData.Port);

		}
		catch (IOException e)
		{
			// log.fatal("socket初始化异常!",e);
			throw new RuntimeException("socket初始化异常,请检查配置参数");
		}
	}

	public Socket getsocket()
	{
		return socket;
	}

	/**
	 * 单态模式
	 */
	public static SocketConnection getInstance()
	{
		if (socketConnection == null)
		{
			synchronized (SocketConnection.class)
			{
				if (socketConnection == null)
				{
					socketConnection = new SocketConnection();
					return socketConnection;
				}
			}
		}
		return socketConnection;
	}

	private void init(String host, int port) throws IOException
	{
		InetSocketAddress addr = new InetSocketAddress(host, port);
		socket = new Socket();
		synchronized (this)
		{
			System.out.println("【准备与" + addr + "建立连接】");
			socket.connect(addr, OverAllData.networktimeout);
			System.out.println("【与" + addr + "连接已建立】");
			inStream = socket.getInputStream();
			outStream = socket.getOutputStream();
			socket.setTcpNoDelay(true); // 数据不作缓冲，立即发送
			socket.setSoLinger(true, 0); // socket关闭时，立即释放资源
			socket.setKeepAlive(true);
			socket.setTrafficClass(0x04 | 0x10); // 高可靠性和最小延迟传输
			isNetworkConnect = true;
			receiveThread = new Thread(new ReceiveWorker());
			receiveThread.start();
			SocketConnection.host = host;
			SocketConnection.port = port;

		}
	}

	/**
	 * 重连与目标IP建立重连
	 */
	public void reConnectToCTCC()
	{
		new Thread(new Runnable()
		{
			public void run()
			{
				System.out.println("重新建立与" + host + ":" + port + "的连接");
				// 清理工作，中断计时器，中断接收线程，恢复初始变量
				heartTimer.cancel();
				// isLaunchHeartcheck = false;
				isNetworkConnect = false;
				receiveThread.interrupt();
				try
				{
					socket.close();
				}
				catch (IOException e1)
				{
				}
				// ----------------
				synchronized (this)
				{
					for (;;)
					{
						try
						{
							Thread.currentThread();
							Thread.sleep(1000 * 1);
							init(host, port);
							launchHeartcheck();
							this.notifyAll();
							break;
						}
						catch (IOException e)
						{

						}
						catch (InterruptedException e)
						{

						}
					}
				}
			}
		}).start();
	}

	/**
	 * 发送命令并接受响应
	 * 
	 * @param requestMsg
	 * @return
	 * @throws SocketTimeoutException
	 * @throws IOException
	 */
	public String readReqMsg(byte[] requestMsg, boolean needBack)
			throws IOException
	{
		if (requestMsg == null)
		{
			return null;
		}
		if (!isNetworkConnect)
		{
			synchronized (this)
			{
				try
				{
					this.wait(1000 * 5); // 等待5秒，如果网络还没有恢复，抛出IO流异常
					if (!isNetworkConnect)
					{
						throw new IOException("网络连接中断！");
					}
				}
				catch (InterruptedException e)
				{

				}
			}
		}
		outStream = socket.getOutputStream();
		outStream.write(requestMsg);
		outStream.flush();
		
		return "ok";
		
//		if (!needBack)
//		{
//			return "ok";
//		}
//		{
//			Condition msglock = lock.newCondition(); // 消息锁
//			// 注册等待接收消息
//			recMsgMap.put(requestMsg[4], msglock);
//			try
//			{
//				lock.lock();
//				msglock.await(OverAllData.networktimeout, TimeUnit.MILLISECONDS);
//			}
//			catch (InterruptedException e)
//			{
//
//			}
//			finally
//			{
//				lock.unlock();
//			}
//			Object respMsg = recMsgMap.remove(msgNo); // 响应信息
//			if (respMsg != null && (respMsg != msglock))
//			{
//				// 已经接收到消息，注销等待，成功返回消息
//				return (String) respMsg;
//			}
//			else
//			{
//
//				throw new SocketTimeoutException(msgNo + " 超时，未收到响应消息");
//			}
//		}
	}

	/**
	 * 心跳包检测
	 */
	public void launchHeartcheck()
	{
		if (SocketConnection.getInstance().getsocket() == null)
			throw new IllegalStateException("socket is not 	established!");
		heartTimer = new Timer();
		heartTimer.schedule(new TimerTask()
		{
			public void run()
			{
				SimpleDateFormat dateformate = new SimpleDateFormat("yyyyMMddHHmmss");
				String msgDateTime = dateformate.format(new Date());
				System.out.println("心跳检测包 -> IVR " + msgDateTime);
				int reconnCounter = 1;
				while (true)
				{
					try
					{
						byte[] heart = new byte[]{(byte)0x55,(byte)0xaa,(byte)0x01,(byte)0x01};
						result = SocketConnection.getInstance().readReqMsg(heart, false);
					}
					catch (IOException e)
					{
						// log.error("IO流异常",e);
						reconnCounter++;
					}
					if (result != null)
					{
						System.out.println("心跳响应包 <- IVR " + result);
						reconnCounter = 1;
						break;
					}
					else
					{
						reconnCounter++;
					}
					if (reconnCounter > 3)
					{// 重连次数已达三次，判定网络连接中断，重新建立连接。连接未被建立时不释放锁
						reConnectToCTCC();
						break;
					}
				}
			}
		}, 1000, OverAllData.HeartbeatTime);
	}
	
	public void finalize()
	{
		if (socket != null)
		{
			try
			{
				socket.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}

	// 消息接收线程
	private class ReceiveWorker implements Runnable
	{
		String intStr = null;

		public void run()
		{
			while (!Thread.interrupted())
			{
				try
				{
					byte[] heart = new byte[512];
					if (inStream.read(heart) == -1)
					{
						// log.warn("读到流未尾，对方已关闭流!");
						reConnectToCTCC();// 读到流未尾，对方已关闭流
						return;
					}
					// byte[] tmp = new byte[4];
					// tmp = headBytes;
					// String tempStr = new String(tmp).trim();
					System.out.println("接收到消息：" + heart);
					// if (tempStr == null || tempStr.equals(""))
					// {
					// // log.error("received message is null");
					// continue;
					// }
					// intStr = new String(tmp);
					// int totalLength = Integer.parseInt(intStr);
					// // ----------------
					// byte[] msgBytes = new byte[totalLength - 4];
					// inStream.read(msgBytes);
					// String resultMsg = new String(headBytes)+ new
					// String(msgBytes);
					// // 抽出消息ID
					// String msgNo = resultMsg.substring(8, 8 + 24);
					// Condition msglock = (Condition) recMsgMap.get(msgNo);
					// if (msglock == null)
					// {
					// // log.warn(msgNo+"序号可能已被注销！响应消息丢弃");
					// recMsgMap.remove(msgNo);
					// continue;
					// }
					// recMsgMap.put(msgNo, resultMsg);
					try
					{
						lock.lock();
						// msglock.signalAll();
					}
					finally
					{
						lock.unlock();
					}
				}
				catch (SocketException e)
				{
					// log.error("服务端关闭socket",e);
					reConnectToCTCC();
				}
				catch (IOException e)
				{
					// log.error("接收线程读取响应数据时发生IO流异常",e);
				}
				catch (NumberFormatException e)
				{
					// log.error("收到没良心包，String转int异常，异常字符:"+intStr);
				}
			}
		}
	}

}
