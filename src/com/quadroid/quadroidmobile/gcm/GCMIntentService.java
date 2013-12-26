package com.quadroid.quadroidmobile.gcm;

import android.app.IntentService;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.quadroid.quadroidmobile.R;
import com.quadroid.quadroidmobile.configuration.Configuration;
import com.quadroid.quadroidmobile.util.BitmapUtils;
import com.quadroid.quadroidmobile.util.NotificationUtil;
import com.quadroid.quadroidmobile.util.PreferenceUtils;

public class GCMIntentService extends IntentService {
	
	private Intent intent;
	
	public GCMIntentService(String name) {
		super("QuadroidGCMIntentService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		this.intent = intent;
		
		Bundle extras = intent.getExtras();
		
		if (!extras.isEmpty()) {
			//Message contains payload
			
			int lmId = extras.getInt("lmAlarmId", -1);
			
			if (lmId >= 0) {
				final String loginToken = PreferenceUtils.getString(getApplicationContext(), R.string.pref_key_login_token, "");
				
				Ion.with(getApplicationContext(), Configuration.LANDMARK_ALARM_URL)
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
			if (object != null && object.has("image_url")) {
				final String imageUrl = object.get("image_url").getAsString();
				final String latitude = object.get("latitude").getAsString();
				final String longitude = object.get("longitude").getAsString();
				final String date = object.get("detection_date").getAsString();
				final int id = object.get("id").getAsInt();
				
				Ion.with(getApplicationContext())
				.load(imageUrl)
				.asBitmap()
				.setCallback(new CustomBitmapCallback(latitude, longitude, date, id));
			} else {
				Toast.makeText(getApplicationContext(), R.string.error_lm_alarm_ivalid, Toast.LENGTH_SHORT).show();
				GCMWakefulIBroadcastReceiver.completeWakefulIntent(intent);
			}
		}
	};
	
	private class CustomBitmapCallback implements FutureCallback<Bitmap> {

		//used for caching
		private String latitude, longitude, date;
		private int id;
		
		public CustomBitmapCallback(String latitude, String longitude, String date, int id) {
			this.date = date;
			this.latitude = latitude;
			this.longitude = longitude;
			this.id = id;
		}
		
		@Override
		public void onCompleted(Exception e, Bitmap bitmap) {
			if (bitmap != null) {
				Bitmap editableBitmap = bitmap.copy(bitmap.getConfig(), true);
				editableBitmap = BitmapUtils.drawTextOnBitmap(
						editableBitmap, 
						"Lat: " + latitude + ", Long: " + longitude + ", Time: " + date, 10, 10);
				
				String filepath = BitmapUtils.saveImageToMemoryCard(editableBitmap, id);
				if (filepath != null) {
					NotificationUtil.showNotification(getApplicationContext(), filepath);
				}
			}
			GCMWakefulIBroadcastReceiver.completeWakefulIntent(intent);
		}
		
	}	
}
