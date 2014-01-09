package com.quadroid.quadroidmobile;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.app.IntentService;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.quadroid.quadroidmobile.configuration.Configuration;
import com.quadroid.quadroidmobile.util.BitmapUtils;
import com.quadroid.quadroidmobile.util.LogUtil;
import com.quadroid.quadroidmobile.util.NotificationUtil;
import com.quadroid.quadroidmobile.util.PreferenceUtils;
import com.quadroid.quadroidmobile.util.StorageUtils;

/**
 * This class is used for processing a new landmark alarm. It connects to Quadroid server, downloads
 * the landmark image and adds the meta information to it, by drawing it directly on the image.
 * Then, a notification will be created.
 * 
 * @author Georg Baumgarten
 * @version 1.0
 *
 */
public class GCMIntentService extends IntentService {
	
	private Intent intent;
	
	public GCMIntentService() {
		super("QuadroidGCMIntentService");
	}
	
	public GCMIntentService(String name) {
		super("QuadroidGCMIntentService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		this.intent = intent;
		
		Bundle extras = intent.getExtras();
		
		if (!extras.isEmpty()) {
			//Message contains payload
			LogUtil.debug(getClass(), "GCM notification has payload");
			
			//Extract landmark alarm ID
			String lmId = extras.getString("lmAlarmId");
			
			LogUtil.debug(getClass(), "Landmark alarm id: " + lmId);
			
			if (!lmId.isEmpty()) {
				//get login token
				final String loginToken = PreferenceUtils.getString(getApplicationContext(), R.string.pref_key_login_token, "");
				
				LogUtil.debug(getClass(), "Downloading Landmark Alarm with Login Token: " + loginToken);
				
				//GET landmark alarm
				Ion.with(getApplicationContext(), String.format(Configuration.LANDMARK_ALARM_URL, lmId))
				.addHeader("Authorization", "Bearer " + loginToken)
				.addHeader("Accept", "application/vnd.quadroid-server-v1+json")
				.asJsonObject()
				.setCallback(mLandmarkCallback);
			}
		}
	}
	
	private FutureCallback<JsonObject> mLandmarkCallback = new FutureCallback<JsonObject>() {		
		@Override
		public void onCompleted(Exception e, JsonObject object) {
			if (e != null) {
				//there was an error, show it
				Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
				e.printStackTrace();
			}
			
			LogUtil.debug(getClass(), "Downloaded Landmark Alarm...");
			
			if (object != null && object.has("landmark_alert")) {
				LogUtil.debug(getClass(), "Landmark Alarm: " + object);
				
				//extract information from JSON
				JsonObject lmAlert = object.getAsJsonObject("landmark_alert");
				final String imageUrl = lmAlert.get("image_path").getAsString();
				final float latitude = lmAlert.get("latitude").getAsFloat();
				final float longitude = lmAlert.get("longitude").getAsFloat();
				final long date = lmAlert.get("detection_date").getAsLong();
				final int id = lmAlert.get("id").getAsInt();
				
				LogUtil.debug(getClass(), "Downloading Landmark Image...");
				
				//Download landmark image
				Ion.with(getApplicationContext())
				.load(imageUrl)
				.asBitmap()
				.setCallback(new CustomBitmapCallback(latitude, longitude, date, id));
			} else {
				//The JSON did not contain the landmark alert, show error
				Toast.makeText(getApplicationContext(), R.string.error_lm_alarm_ivalid, Toast.LENGTH_SHORT).show();
				GCMWakefulBroadcastReceiver.completeWakefulIntent(intent);
			}
		}
	};
	
	private class CustomBitmapCallback implements FutureCallback<Bitmap> {

		//used for caching
		private float latitude, longitude;
		private long date;
		private int id;
		
		public CustomBitmapCallback(float latitude, float longitude, long date, int id) {
			this.date = date;
			this.latitude = latitude;
			this.longitude = longitude;
			this.id = id;
		}
		
		@Override
		public void onCompleted(Exception e, Bitmap bitmap) {
			LogUtil.debug(getClass(), "Downloading Landmark Image finished");
			if (bitmap != null) {
				LogUtil.debug(getClass(), "Landmark Image: " + bitmap.getWidth() + "*" + bitmap.getHeight());
				
				Bitmap editableBitmap = bitmap.copy(bitmap.getConfig(), true);
				
				//format detection time
				Calendar c = Calendar.getInstance();
				c.setTimeInMillis(date*1000);
				SimpleDateFormat sdf = new SimpleDateFormat("EEE yyyy-MM-dd HH:mm:ss");
				
				//create bitmap with text
				editableBitmap = BitmapUtils.drawTextOnBitmap(
						editableBitmap, 
						"Lat: " + latitude + ", Long: " + longitude + ", Time: " + sdf.format(c.getTime()), 10, editableBitmap.getHeight()-10);
				
				LogUtil.debug(getClass(), "Altered Image: " + editableBitmap.getWidth() + "*" + editableBitmap.getHeight());
				
				//save bitmap on filesystem
				String filepath = StorageUtils.saveImageToMemoryCard(getApplicationContext(), editableBitmap, id);
				if (filepath != null) {
					//if it was saved successfully, show notification
					NotificationUtil.showNotification(getApplicationContext(), filepath);
				}
			}
			
			//Notify out Broadcastreceiver that we completed our work, so it can release the wakelock
			GCMWakefulBroadcastReceiver.completeWakefulIntent(intent);
		}
		
	}	
}
