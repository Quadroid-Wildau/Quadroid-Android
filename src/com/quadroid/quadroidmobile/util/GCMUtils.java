package com.quadroid.quadroidmobile.util;

import java.io.IOException;

import android.content.Context;
import android.os.AsyncTask;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.quadroid.quadroidmobile.R;
import com.quadroid.quadroidmobile.configuration.Configuration;
import com.quadroid.quadroidmobile.interfaces.OnGcmRegisteredListener;

public class GCMUtils {
	
	/**
	 * Gets the current registration ID for application on GCM service.
	 * <p>
	 * If result is empty, the app needs to register.
	 *
	 * @return registration ID, or empty string if there is no existing
	 *         registration ID.
	 */
	public static String getRegistrationId(Context context) {
	    return PreferenceUtils.getString(context, R.string.pref_key_gcm_reg_id, "");
	}
	
	/**
	 * Registers the application with GCM servers asynchronously.
	 * 
	 * @param gcm
	 * 			Instance of {@link GoogleCloudMessaging}
	 * @param listener
	 * 			An {@link OnGcmRegisteredListener} which is called after registration
	 */
	public static void registerInBackground(final GoogleCloudMessaging gcm, final OnGcmRegisteredListener listener) {
	    new AsyncTask<Void, Void, String>() {
	        @Override
	        protected String doInBackground(Void... params) {
	            String regId = "";
	            try {
	                regId = gcm.register(Configuration.SENDER_ID);
	            } catch (IOException ex) {
	                regId = "";
	            }
	            return regId;
	        }

	        @Override
	        protected void onPostExecute(String regId) {
	            if (listener != null) listener.onGcmRegistered(regId);
	        }
	    }.execute(null, null, null);
	}
}
