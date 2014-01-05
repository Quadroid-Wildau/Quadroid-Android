package com.quadroid.quadroidmobile.util;

import java.io.File;

import android.app.Notification;
import android.app.Notification.BigPictureStyle;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Build.VERSION;

import com.quadroid.quadroidmobile.R;

public class NotificationUtil {
	
	private static final int NOTIFICATION_ID = 13564182;
	
	/**
	 * This method will show a typical Android notification on all Android devices up to 4.1. On Android 4.1
	 * and later, a {@link BigPictureStyle} notification is shown with a preview of the landmark alert. When the
	 * user clicks on the notification, he can choose the app to view the landmark alert image
	 * @param context
	 * 			A context
	 * @param filepath
	 * 			Absolute filepath to the preview image
	 */
	public static void showNotification(Context context, String filepath) {
		if (VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			showBigPictureNotification(context, filepath);
		} else {
			showSmallNotification(context, filepath);
		}
	}
	
	private static void showSmallNotification(Context context, String filepath) {
		NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		int icon = R.drawable.logo_notification;
		long when = System.currentTimeMillis();
		@SuppressWarnings("deprecation")
		Notification notification = new Notification(icon, context.getString(R.string.app_name), when);
		Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		
		//configure the intent and pending intent that is fired when the user clicks the notification
		Intent intent = new Intent(Intent.ACTION_VIEW);
	    File file = new File(filepath);
	    intent.setDataAndType(Uri.fromFile(file), "image/*");
	    PendingIntent contentIntent = PendingIntent.getActivity(
	    									context, 
	    									0, 
	    									intent, 
	    									0);
	    
		notification.contentIntent = contentIntent;
		notification.ledARGB = Color.parseColor("#ffff00ff");
		notification.ledOnMS = 500;
		notification.ledOffMS = 5000;
		notification.sound = alarmSound;
		//show notification
		mNotificationManager.notify(NOTIFICATION_ID, notification);
	}
	
	private static void showBigPictureNotification(Context context, String filePath) {
		NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		
		//configure the intent and pending intent that is fired when the user clicks the notification
		Intent intent = new Intent(Intent.ACTION_VIEW);
	    File file = new File(filePath);
	    intent.setDataAndType(Uri.fromFile(file), "image/*");
	    PendingIntent contentIntent = PendingIntent.getActivity(
	    									context, 
	    									0, 
	    									intent, 
	    									0);
	    
	    Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
	    
	    //build notification using android builder
	    Notification.Builder mNotificationBuilder = new Notification.Builder(context)
					.setAutoCancel(false)
					.setSmallIcon(R.drawable.logo_notification)
					.setContentIntent(contentIntent)
					.setContentText(context.getString(R.string.tap_to_view))
					.setContentTitle(context.getString(R.string.new_landmark_alarm))
					.setLights(Color.parseColor("#ffff00ff"), 500, 5000)
					.setSound(alarmSound)
					.addAction(android.R.drawable.ic_menu_view, context.getString(R.string.view), contentIntent);
	    
	    
	   //decode image of landmark alert
	    Bitmap image = BitmapFactory.decodeFile(filePath);
		
	    //create notification
		Notification notification = new Notification.BigPictureStyle(mNotificationBuilder)
											.bigPicture(image)
											.setSummaryText(context.getString(R.string.tap_to_view))
											.build();
		
		//show notification
		mNotificationManager.notify(NOTIFICATION_ID, notification);
	}
}
