package com.quadroid.quadroidmobile.util;

import java.io.File;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Build.VERSION;

import com.quadroid.quadroidmobile.R;

public class NotificationUtil {
	
	private static final int NOTIFICATION_ID = 13564182;
	
	public static void showNotification(Context context, String filepath) {
		if (VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			showBigPictureNotification(context, filepath);
		} else {
			showSmallNotification(context, filepath);
		}
	}
	
	private static void showSmallNotification(Context context, String filepath) {
		NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		int icon = R.drawable.ic_launcher;
		long when = System.currentTimeMillis();
		@SuppressWarnings("deprecation")
		Notification notification = new Notification(icon, context.getString(R.string.app_name), when);
		
		Intent intent = new Intent(Intent.ACTION_VIEW);
	    File file = new File(filepath);
	    intent.setDataAndType(Uri.fromFile(file), "image/*");
	    PendingIntent contentIntent = PendingIntent.getActivity(
	    									context, 
	    									0, 
	    									intent, 
	    									0);
	    
		notification.contentIntent = contentIntent;
		
		mNotificationManager.notify(NOTIFICATION_ID, notification);
	}
	
	private static void showBigPictureNotification(Context context, String filePath) {
		NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		
		Intent intent = new Intent(Intent.ACTION_VIEW);
	    File file = new File(filePath);
	    intent.setDataAndType(Uri.fromFile(file), "image/*");
	    PendingIntent contentIntent = PendingIntent.getActivity(
	    									context, 
	    									0, 
	    									intent, 
	    									0);
	    
	    Notification.Builder mNotificationBuilder = new Notification.Builder(context)
					.setAutoCancel(false)
					.setSmallIcon(R.drawable.ic_launcher)
					.setContentIntent(contentIntent)
					.setContentText(context.getString(R.string.tap_to_view))
					.setContentTitle(context.getString(R.string.new_landmark_alarm))
					.addAction(android.R.drawable.ic_menu_view, context.getString(R.string.view), contentIntent);
	    
	    
	    
	    BitmapFactory.Options opts = new BitmapFactory.Options();
	    opts.inSampleSize = 3;
	    LogUtil.debug("Decoding image: " + filePath);
	    Bitmap image = BitmapFactory.decodeFile(filePath, opts);
	    LogUtil.debug("Image decoded. Size: " + image.getWidth() + "*" + image.getHeight());
		
		Notification notification = new Notification.BigPictureStyle(mNotificationBuilder)
											.bigPicture(image)
											.setSummaryText(context.getString(R.string.tap_to_view))
											.build();
		
		mNotificationManager.notify(NOTIFICATION_ID, notification);
	}
}
