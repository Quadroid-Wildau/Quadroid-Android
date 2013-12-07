package com.quadroid.quadroidmobile.configuration;

/**
 * Configuration class that holds some static environment configuration values.
 * 
 * @author Georg Baumgarten
 * 
 */
public class Configuration {
	
	/**
	 * Is the app running in production mode? Set to true for a release which is going to be uploaded to Google Play.
	 */
	public static final boolean IS_PRODUCTION = false;
	
	/**
	 * Root folder on memory card.
	 */
	public static final String ROOT_FOLDER = "Quadroid";
	
	/**
	 * Log folder (part of root folder) on memory card.
	 */
	public static final String LOG_FOLDER = "logs";
	
	/**
	 * GCM Sender ID
     */
	public static final String SENDER_ID = "Your-Sender-ID";
	
	public static final String LOGIN_URL = "http://api.quadroid.com/users/login";
	
	public static final String GCM_SETUP_URL = "http://api.quadroid.com/users/gcmsetup";
}
