package com.quadroid.quadroidmobile.util;

import java.io.File;
import java.io.IOException;

import android.os.Environment;
import android.os.StatFs;

import com.quadroid.quadroidmobile.configuration.Configuration;

/**
 * 
 * Utility class for memory card.
 * 
 * @author Georg Baumgarten
 *
 */
public class StorageUtils {

	/**
	 * Checks if memory is mounted and writeable at the moment.
	 * @return
	 * 		true if it is
	 */
	public static boolean isMemoryCardMounted() {
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			return true;
		}
		return false;
	}
	
	/**
	 * This method creates the log folder if necessary. If the folder already exists,
	 * this method does nothing and only return the path to the folder.
	 * @return
	 * 		The path to the log folder
	 */
	public static String prepareLogFolder() {
		String root = Environment.getExternalStorageDirectory() + 
						File.separator + 
						Configuration.ROOT_FOLDER +
						File.separator;
		
		File log = new File(root + Configuration.LOG_FOLDER);
		
		if (!log.exists())
			log.mkdirs();
		
		return log.getAbsolutePath();
	}
	
	/**
	 * This methods checks how much space is left on the memory card.
	 * @return
	 * 		Space left on memory card in MB.
	 */
	public static long getSpaceLeftInMB() {
		StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
		@SuppressWarnings("deprecation")
		long bytesAvailable = (long)stat.getBlockSize() *(long)stat.getBlockCount();
		return bytesAvailable / 1048576;
	}	
	
	public static File getImageFile(int imageId, boolean overwrite) throws IOException {
		File root = new File(
				Environment.getExternalStorageDirectory() + 
				File.separator + 
				Configuration.ROOT_FOLDER + 
				File.separator +
				Configuration.ALARM_FOLDER);
		
		if (!root.exists())
			root.mkdirs();
		
		File f = new File(
				Environment.getExternalStorageDirectory() + 
				File.separator + 
				Configuration.ROOT_FOLDER + 
				File.separator +
				Configuration.ALARM_FOLDER,
				imageId + ".png");
		
		//if file already exists, you can overwrite
		if (!f.createNewFile() && overwrite) {
			f.delete();
			f.createNewFile();
		}
		return f;
	}
}
