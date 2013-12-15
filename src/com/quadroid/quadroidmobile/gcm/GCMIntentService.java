package com.quadroid.quadroidmobile.gcm;

import org.json.JSONObject;

import android.app.IntentService;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.quadroid.quadroidmobile.R;
import com.quadroid.quadroidmobile.configuration.Configuration;
import com.quadroid.quadroidmobile.util.BitmapUtils;
import com.quadroid.quadroidmobile.util.NotificationUtil;
import com.quadroid.quadroidmobile.util.PreferenceUtils;

public class GCMIntentService extends IntentService {
	
	//used for caching
	private String latitude, longitude, time;
	private int id;
	
	public GCMIntentService(String name) {
		super("QuadroidGCMIntentService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Bundle extras = intent.getExtras();
		
		if (!extras.isEmpty()) {
			//Message contains payload
			
			int lmId = extras.getInt("lmAlarmId", -1);
			
			if (lmId >= 0) {
				AQuery aq = new AQuery(getApplicationContext());
				
				String url = Configuration.LANDMARK_ALARM_URL + 
								lmId + 
								"?access_token=" + 
								PreferenceUtils.getString(getApplicationContext(), R.string.pref_key_login_token, "");
				
				aq.ajax(url, JSONObject.class, mLandmarkCallback);
			}
		}
	}

	private AjaxCallback<JSONObject> mLandmarkCallback = new AjaxCallback<JSONObject>() {
		@Override
		public void callback(String url, JSONObject object, AjaxStatus status) {
			try {
				if (object != null && object.has("image_url")) {
					String imageUrl = object.getString("image_url");
					latitude = object.getString("latitiude");
					longitude = object.getString("longitude");
					time = object.getString("detection_date");
					id = object.getInt("id");
					
					AQuery aq = new AQuery(getApplicationContext());
					aq.ajax(imageUrl, Bitmap.class, mBitmapDownloadCallback);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};
	
	private AjaxCallback<Bitmap> mBitmapDownloadCallback = new AjaxCallback<Bitmap>() {
		@Override
		public void callback(String url, Bitmap object, AjaxStatus status) {
			Bitmap editableBitmap = object.copy(object.getConfig(), true);
			editableBitmap = BitmapUtils.drawTextOnBitmap(
								editableBitmap, 
								"Lat: " + latitude + ", Long: " + longitude + ", Time: " + time, 10, 10);
			
			String filepath = BitmapUtils.saveImageToMemoryCard(editableBitmap, id);
			
			if (filepath != null) {
				NotificationUtil.showNotification(getApplicationContext(), filepath);
			}
		}
	};
	
}
