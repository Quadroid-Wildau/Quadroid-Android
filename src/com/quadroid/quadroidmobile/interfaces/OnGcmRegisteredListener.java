package com.quadroid.quadroidmobile.interfaces;

import com.quadroid.quadroidmobile.util.GCMUtils;

import android.os.AsyncTask;

/**
 * This interface is intended to be used with the GCM registration {@link AsyncTask}
 * in {@link GCMUtils} class.
 * 
 * @author Georg Baumgarten
 * @version 1.0
 *
 */
public interface OnGcmRegisteredListener {

	/**
	 * Called after the GCM registration process
	 * @param registrationId
	 * 			The GCM registration id
	 */
	public void onGcmRegistered(String registrationId);
}
