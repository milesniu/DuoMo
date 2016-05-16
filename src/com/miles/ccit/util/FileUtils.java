package com.miles.ccit.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;

public class FileUtils
{
//	private String SDPATH;

//	public String getSDPATH()
//	{
//		return SDPATH;
//	}

	public FileUtils()
	{
//		SDPATH = Environment.getExternalStorageDirectory() + "/";
	}

	
	public static void setMapData2SD(BaseMapObject obj)
	{
		ObjectOutputStream objOutput = null;
		try
		{
			if (obj != null)
			{
				objOutput = new ObjectOutputStream(new FileOutputStream(O.loginPath));
				objOutput.writeObject(obj);
			}

		} catch (Exception e)
		{
			e.printStackTrace();
		} finally
		{
			try
			{
				if (objOutput != null)
				{
					objOutput.close();
				}
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	
	public static BaseMapObject getMapData4SD()
	{
		ObjectInputStream objInput = null;
		BaseMapObject tmp = null;
		// List<PushMessage> outmsglist = null;
		try
		{
			objInput = new ObjectInputStream(new FileInputStream(O.loginPath));
			tmp =  (BaseMapObject) objInput.readObject();
		} catch (Exception e)
		{
			e.printStackTrace();
		} finally
		{

			try
			{
				if (objInput != null)
					objInput.close();
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		if(tmp==null)
		{
			tmp = new BaseMapObject();
			tmp.put("count", 1);
		}
		return tmp;

	}

	 public static String getPath(Context context, Uri uri) {
		 
	        if ("content".equalsIgnoreCase(uri.getScheme())) {
	            String[] projection = { "_data" };
	            Cursor cursor = null;
	 
	            try {
	                cursor = context.getContentResolver().query(uri, projection,null, null, null);
	                int column_index = cursor.getColumnIndexOrThrow("_data");
	                if (cursor.moveToFirst()) {
	                    return cursor.getString(column_index);
	                }
	            } catch (Exception e) {
	                // Eat it
	            }
	        }
	 
	        else if ("file".equalsIgnoreCase(uri.getScheme())) {
	            return uri.getPath();
	        }
	 
	        return null;
	    }
	
	public void RecursionDeleteFile(File file)
	{
		
		if (file.isFile())
		{
			file.delete();
			return;
		}
		if (file.isDirectory())
		{
			File[] childFile = file.listFiles();
			if (childFile == null || childFile.length == 0)
			{
				file.delete();
				return;
			}
			for (File f : childFile)
			{
				RecursionDeleteFile(f);
			}
			file.delete();
		}
	}


	/**
	 * 在SD卡上创建文件
	 * 
	 * @throws IOException
	 */
	public File creatSDFile(String fileName) throws IOException
	{
		File file = new File(fileName);
		file.createNewFile();
		return file;
	}

	/**
	 * 在SD卡上创建目录
	 * 
	 * @param dirName
	 */
	public File creatSDDir(String dirName)
	{
		File dir = new File(dirName);
		dir.mkdirs();
		return dir;
	}

	public static void saveMyBitmap(String bitName, Bitmap mBitmap)
	{
		File f = new File(O.SDCardRoot + bitName);
		try
		{
			f.createNewFile();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			// DebugMessage.put("在保存图片时出错：" + e.toString());
		}
		FileOutputStream fOut = null;
		try
		{
			fOut = new FileOutputStream(f);
		} catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}

		try
		{
			mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
			fOut.flush();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		try
		{
			fOut.close();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * 判断SD卡上的文件夹是否存在
	 */
	public boolean isFileExist(String fileName)
	{
		File file = new File(fileName);
		return file.exists();
	}

	/**
	 * 将一个InputStream里面的数据写入到SD卡中
	 */
	public File writeToSDCard(String path, String fileName, InputStream input)
	{
		File file = null;
		OutputStream output = null;
		try
		{
			creatSDDir(path);
			file = creatSDFile(path + fileName);
			output = new FileOutputStream(file);
			byte[] buffer = new byte[4 * 1024];
			int len = 0;
			while ((len = input.read(buffer)) != -1)
			{
				output.write(buffer, 0, len);
			}
			output.flush();
		} catch (Exception e)
		{
			e.printStackTrace();
		} finally
		{
			try
			{
				output.close();
			} catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		return file;
	}

}
