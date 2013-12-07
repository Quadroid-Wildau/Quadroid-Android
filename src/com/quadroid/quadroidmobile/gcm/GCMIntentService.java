package com.quadroid.quadroidmobile.gcm;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;

import com.androidquery.AQuery;

public class GCMIntentService extends IntentService {

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
				
				//load image
			}
		}
		
	}

	
}
