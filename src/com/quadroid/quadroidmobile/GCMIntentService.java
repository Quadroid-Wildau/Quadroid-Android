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
			
			String lmId = extras.getString("lmAlarmId");
			
			LogUtil.debug(getClass(), "Landmark alarm id: " + lmId);
			
			if (!lmId.equals("")) {
				final String loginToken = PreferenceUtils.getString(getApplicationContext(), R.string.pref_key_login_token, "");
				
				LogUtil.debug(getClass(), "Downloading Landmark Alarm with Login Token: " + loginToken);
				
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
				Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
				e.printStackTrace();
			}
			
			LogUtil.debug(getClass(), "Downloaded Landmark Alarm...");
			
			if (object != null && object.has("landmark_alert")) {
				LogUtil.debug(getClass(), "Landmark Alarm: " + object);
				
				JsonObject lmAlert = object.getAsJsonObject("landmark_alert");
				final String imageUrl = lmAlert.get("image_path").getAsString();
				final float latitude = lmAlert.get("latitude").getAsFloat();
				final float longitude = lmAlert.get("longitude").getAsFloat();
				final long date = lmAlert.get("detection_date").getAsLong();
				final int id = lmAlert.get("id").getAsInt();
				
				LogUtil.debug(getClass(), "Downloading Landmark Image...");
				Ion.with(getApplicationContext())
				.load(imageUrl)
				.asBitmap()
				.setCallback(new CustomBitmapCallback(latitude, longitude, date, id));
			} else {
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
				
				Calendar c = Calendar.getInstance();
				c.setTimeInMillis(date*1000);
				SimpleDateFormat sdf = new SimpleDateFormat("EEE yyyy-MM-dd HH:mm:ss");
				
				editableBitmap = BitmapUtils.drawTextOnBitmap(
						editableBitmap, 
						"Lat: " + latitude + ", Long: " + longitude + ", Time: " + sdf.format(c.getTime()), 10, editableBitmap.getHeight()-10);
				
				LogUtil.debug(getClass(), "Altered Image: " + editableBitmap.getWidth() + "*" + editableBitmap.getHeight());
				
				String filepath = BitmapUtils.saveImageToMemoryCard(getApplicationContext(), editableBitmap, id);
				if (filepath != null) {
					NotificationUtil.showNotification(getApplicationContext(), filepath);
				}
			}
			GCMWakefulBroadcastReceiver.completeWakefulIntent(intent);
		}
		
	}	
}
