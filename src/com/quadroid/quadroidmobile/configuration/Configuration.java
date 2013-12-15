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
	
	public static final String ALARM_FOLDER = "alarms";
	
	/**
	 * Log folder (part of root folder) on memory card.
	 */
	public static final String LOG_FOLDER = "logs";
	
	public static final String CLIENT_ID = "3cb7ada5f62bbb5205d157fda77c34673ed8ccd82cddd7729456353c9ccbaf9b";
	public static final String CLIENT_SECRET = "2cbf071c5d9e65e92077db3fc64cda628ec6c58344b7159689e6cd3a10b5a592";
	
	/**
	 * GCM Sender ID
     */
	public static final String SENDER_ID = "983484157298";
	
	public static final String BASE_URL = "http://quadroid.dev.wonderweblabs.com/";
	public static final String LOGIN_URL = BASE_URL + "oauth/token";
	public static final String GCM_SETUP_URL = BASE_URL + "gcm_device";
	public static final String LANDMARK_ALARM_URL = BASE_URL + "landmark_alerts/";
}
