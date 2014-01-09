package com.quadroid.quadroidmobile;

import com.quadroid.quadroidmobile.util.LogUtil;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager.WakeLock;
import android.support.v4.content.WakefulBroadcastReceiver;

/**
 * This {@link BroadcastReceiver} will be instantiated by Android OS, as soon as
 * there is an incoming Quadroid GCM message.
 * The receiver will automatically create a {@link WakeLock} and then passes the work
 * to {@link GCMIntentService}.
 * 
 * @author Georg Baumgarten
 * @version 1.0
 *
 */
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
