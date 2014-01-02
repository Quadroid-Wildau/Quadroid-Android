package com.quadroid.quadroidmobile.gcm;

import com.quadroid.quadroidmobile.util.LogUtil;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

public class GCMWakefulBroadcastReceiver extends WakefulBroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		LogUtil.debug(getClass(), "Got GCM notification");
		// Explicitly specify that GcmIntentService will handle the intent.
        ComponentName comp = new ComponentName(context.getPackageName(),
                GCMIntentService.class.getName());
        
        intent.setComponent(comp);
        
        // Start the service, keeping the device awake while it is launching.
        startWakefulService(context, intent);
        
        setResultCode(Activity.RESULT_OK);
	}
}
