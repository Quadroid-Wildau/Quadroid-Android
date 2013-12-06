package com.quadroid.quadroidmobile.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.util.Log;

import com.quadroid.quadroidmobile.configuration.Configuration;

/**
 * 
 * This class is more or less a wrapper class for Androids default logging mechanism.
 * It provides some basic functionality for writing logs to memory card. Debug logs are only
 * written if the app doesn't run in production mode.
 * 
 * @author Georg Baumgarten
 *
 */
public class LogUtil {
	
	private static final String TAG = "Quadroid";

	/**
	 * Write a debug message using Androids {@link Log} class to the adb console.
	 * This only work if the app does not in production mode. See {@link Configuration}.
	 * @param message
	 * 			The debug message
	 */
	public static void debug(String message) {
		if (!Configuration.IS_PRODUCTION)
			Log.d(TAG, message);
	}
	
	/**
	 * Write a debug message using Androids {@link Log} class to the adb console.
	 * This only work if the app does not in production mode. See {@link Configuration}.
	 * @param clz
	 * 			The class from where the debug log is written
	 * @param message
	 * 			The debug message
	 */
	public static void debug(Class<?> clz, String message) {
		if (!Configuration.IS_PRODUCTION)
			Log.d(TAG, "[" + clz.getSimpleName() + "] - " + message);
	}
	
	/**
	 * Writes an Exception Stacktrace into a logfile on memory card.
	 * @param t
	 * 			The Exception
	 */
	public static void writeStacktraceToSD(Throwable t) {
		if (StorageUtils.isMemoryCardMounted()) {
			try {
				String stackTrace = getStackTrace(t);
				BufferedWriter out = new BufferedWriter(
									 	new FileWriter(
									 		StorageUtils.prepareLogFolder() + 
									 		File.separator + 
									 		Configuration.LOG_FOLDER + 
									 		File.separator + 
									 		"log.txt", true));
				out.write("\n");
				out.write("#####################\n");
				out.write(now("yyyy.MM.dd 'at' hh:mm:ss \n"));
				out.write("---------------------\n");
				out.write(stackTrace + "\n");
				out.write("#####################\n");
				out.write("\n");
				out.flush();
				out.close();
			} catch (Exception e) {e.printStackTrace();}
		}
	}
	
	/**
	 * Writes a custom log message into a log file on memory card.
	 * @param log
	 * 			The log as string
	 */
	public static void writeLogToSD(String log) {
		if (StorageUtils.isMemoryCardMounted()) {
			try {
				BufferedWriter out = new BufferedWriter(
									 	new FileWriter(
									 		StorageUtils.prepareLogFolder() + 
									 		File.separator + 
									 		Configuration.LOG_FOLDER + 
									 		File.separator + 
									 		"log.txt", true));
				out.write("\n");
				out.write("#####################\n");
				out.write(now("yyyy.MM.dd 'at' hh:mm:ss \n"));
				out.write("---------------------\n");
				out.write(log + "\n");
				out.write("#####################\n");
				out.write("\n");
				out.flush();
				out.close();
			} catch (Exception e) {e.printStackTrace();}
		}
	}
	
	/**
	 * Writes an Exception Stacktrace into a logfile on memory card.
	 * @param t
	 * 			The Exception
	 * @param additionalInfo
	 * 			Additional information you can provide
	 */
	public static void writeStacktraceToSD(Throwable t, String[] additionalInfo) {
		if (StorageUtils.isMemoryCardMounted()) {
			try {
				String stackTrace = getStackTrace(t);
				BufferedWriter out = new BufferedWriter(
									 	new FileWriter(
									 		StorageUtils.prepareLogFolder() + 
									 		File.separator + 
									 		Configuration.LOG_FOLDER + 
									 		File.separator + 
									 		"log.txt", true));
				out.write("\n");
				out.write("#####################\n");
				out.write(now("yyyy.MM.dd 'at' hh:mm:ss \n"));
				out.write("---------------------\n");
				out.write("Additional information\n");
				
				for (int i = 0; i < additionalInfo.length; i++) {
					out.write(additionalInfo[i] + "\n");
				}
				
				out.write(stackTrace + "\n");
				out.write("#####################\n");
				out.write("\n");
				out.flush();
				out.close();
			} catch (Exception e) {e.printStackTrace();}
		}
	}
	
	private static String getStackTrace(Throwable t) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw, true);
        t.printStackTrace(pw);
        pw.flush();
        sw.flush();
        try {
        	pw.close();
			sw.close();
		} catch (IOException e) {}
        return sw.toString();
    }

	
	private static String now(String dateFormat) {
	    Calendar cal = Calendar.getInstance();
	    SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
	    return sdf.format(cal.getTime());
    }
}
