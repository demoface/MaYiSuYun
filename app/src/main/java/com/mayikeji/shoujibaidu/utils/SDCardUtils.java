package com.mayikeji.shoujibaidu.utils;

import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.os.storage.StorageManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;

/**
 * SD卡相关的辅助类
 * 
 * 
 * 
 */
public class SDCardUtils {
	private static String getPath() {
		return SDCardUtils.getSDCardPath() + "/qiangdan/";
	}

	private SDCardUtils() {
		/* cannot be instantiated */
		throw new UnsupportedOperationException("cannot be instantiated");
	}

	/**
	 * 判断SDCard是否可用
	 * 
	 * @return
	 */
	public static boolean isSDCardEnable() {
		return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);

	}

	/**
	 * 获取SD卡路径
	 * 
	 * @return
	 */
	public static String getSDCardPath() {
		return Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator;
	}

	/**
	 * 获取SD卡的剩余容量 单位byte
	 * 
	 * @return
	 */
	public static long getSDCardAllSize() {
		if (isSDCardEnable()) {
			StatFs stat = new StatFs(getSDCardPath());
			// 获取空闲的数据块的数量
			long availableBlocks = (long) stat.getAvailableBlocks() - 4;
			// 获取单个数据块的大小（byte）
			long freeBlocks = stat.getAvailableBlocks();
			return freeBlocks * availableBlocks;
		}
		return 0;
	}

	/**
	 * 获取指定路径所在空间的剩余可用容量字节数，单位byte
	 * 
	 * @param filePath
	 * @return 容量字节 SDCard可用空间，内部存储可用空间
	 */
	public static long getFreeBytes(String filePath) {
		// 如果是sd卡的下的路径，则获取sd卡可用容量
		if (filePath.startsWith(getSDCardPath())) {
			filePath = getSDCardPath();
		} else {// 如果是内部存储的路径，则获取内存存储的可用容量
			filePath = Environment.getDataDirectory().getAbsolutePath();
		}
		StatFs stat = new StatFs(filePath);
		long availableBlocks = (long) stat.getAvailableBlocks() - 4;
		return stat.getBlockSize() * availableBlocks;
	}

	/**
	 * 获取系统存储路径
	 * 
	 * @return
	 */
	public static String getRootDirectoryPath() {
		return Environment.getRootDirectory().getAbsolutePath();
	}

	/**
	 * 获取内置SD卡路径
	 * 
	 * @return
	 */
	public static String getInnerSDCardPath() {
		return Environment.getExternalStorageDirectory().getPath();
	}

	/** 枚举所有挂载点 */
	public static String[] getVolumePaths(Context context) {
		String[] paths = null;
		StorageManager mStorageManager;
		Method mMethodGetPaths = null;
		try {
			mStorageManager = (StorageManager) context.getSystemService(Activity.STORAGE_SERVICE);
			mMethodGetPaths = mStorageManager.getClass().getMethod("getVolumePaths");
			paths = (String[]) mMethodGetPaths.invoke(mStorageManager);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return paths;
	}

	/**
	 * 检查是否挂载
	 */
	public static boolean checkMounted(Context context, String mountPoint) {
		if (mountPoint == null) {
			return false;
		}
		StorageManager storageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
		try {
			Method getVolumeState = storageManager.getClass().getMethod("getVolumeState", String.class);
			String state = (String) getVolumeState.invoke(storageManager, mountPoint);
			return Environment.MEDIA_MOUNTED.equals(state);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public static String copyBigDataToSD(Context cx) throws IOException {
//		Bitmap bitmap=null;
//		String strOutFileName="";
//		bitmap = BitmapFactory.decodeResource(cx.getResources(), R.drawable.ic_launcher);
//		
//		if (isSDCardEnable()) {
//			strOutFileName = getSDCardPath()+"/ic_launcher.png";
//
//		}else{
//			strOutFileName = getInnerSDCardPath()+"/ic_launcher.png";
//		}
//		PictureUtil.saveMyBitmap(bitmap, new File(strOutFileName));
		
		return "http://apprs.ezhayan.com/APP/58-58.png";
	}

	// 写入sd卡
	/**
	 *
	 *            写入sd的文件夹名字
	 * @param fileName
	 *            文件名
	 * @return 是否写入正确
	 */
	public static boolean writeToSD(String fileName, byte[] bytes) {
		return writeToSD(new File(getSDCardPath(),getPath() + fileName),bytes);
	}
	public static boolean writeToSD(File file, byte[] bytes) {
		if(bytes == null){
			return false;
		}
		if (isSDCardEnable()) {
			exists(getPath());

			FileOutputStream fos = null;
			try {
				fos = new FileOutputStream(file);
				fos.write(bytes);
				return true;
			} catch (FileNotFoundException e) {

				e.printStackTrace();
			} catch (IOException e) {

				e.printStackTrace();
			} finally {
				try {
					fos.close();
				} catch (IOException e) {

					e.printStackTrace();
				}
			}
		}
		return false;
	}

	public static void exists(String path){
		File file = new File(path);
		if (!file.exists()) {
			file.mkdirs();
		}
	}
}