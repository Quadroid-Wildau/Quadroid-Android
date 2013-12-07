package com.quadroid.quadroidmobile.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * This class provides methods for creating and reading preferences.
 * 
 * @author Georg Baumgarten
 *
 */
public class PreferenceUtils {
	
	private static SharedPreferences preferences;
	private static SharedPreferences.Editor editor;
	
	/**
	 * gets String from preferences
	 * @param context
	 * 			A valid context
	 * @param key
	 * 			Preference key to read
	 * @param defValue - default value if key not exists
	 * 			Default value to return if the preference does not exist
	 * @return 
	 * 		String value
	 */
	public static String getStringFromPreferences(Context context, String key, String defValue) {
		if (preferences == null)
			preferences = PreferenceManager.getDefaultSharedPreferences(context);
		
		return preferences.getString(key, defValue);
	}
	
	/**
	 * puts String to preferences
	 * @param context
	 * 			A valid context
	 * @param key
	 * 			The key which is used to store the preference
	 * @param value
	 * 			The value to store
	 */
	public static void putStringToPreferences(Context context, String key, String value) {
		if (preferences == null)
			preferences = PreferenceManager.getDefaultSharedPreferences(context);
		if (editor == null)
			editor = preferences.edit();
		
		editor.putString(key, value);
		editor.commit();
	}
	
	/**
	 * puts String to preferences
	 * @param context
	 * 				A valid context
	 * @param keyResourceId
	 * 				Resource id of the string to use as key
	 * @param value
	 * 				The value
	 */
	public static void putString(Context context, int keyResourceId, String value) {
		putStringToPreferences(context, context.getString(keyResourceId), value);
	}
	
	/**
	 * 
	 * @param context
	 * 				A valid context
	 * @param keyResourceId
	 * 				Resource id of the string to use as key
	 * @param defaultValue
	 * 				The default value which is returned if the preference was not found
	 * @return
	 * 			The string value
	 */
	public static String getString(Context context, int keyResourceId, String defaultValue) {
		return getStringFromPreferences(context, context.getString(keyResourceId), defaultValue);
	}
	
	/**
	 * Checks if a preference does exist
	 * @param context
	 * 				A valid context
	 * @param key
	 * 				Key to check
	 * @return
	 * 			True if the preference does exist
	 */
	public static boolean hasPreferences(Context context, String key) {
		if (preferences == null)
			preferences = PreferenceManager.getDefaultSharedPreferences(context);
		
		return preferences.contains(key);
	}
	
	/**
	 * Removes Preference by key
	 * @param context
	 * 			A valid context
	 * @param key
	 * 			The key to remove
	 */
	public static void removeFromPreferences(Context context, String key) {
		if (preferences == null)
			preferences = PreferenceManager.getDefaultSharedPreferences(context);
		if (editor == null)
			editor = preferences.edit();
		
		editor.remove(key);
		editor.commit();
	}
	
	/**
	 * Removes Preference by key
	 * @param context
	 * 			A valid context
	 * @param keyResourceId
	 * 			The key to remove
	 */
	public static void removeFromPreferences(Context context, int keyResourceId) {
		removeFromPreferences(context, context.getString(keyResourceId));
	}
}


