package com.quadroid.quadroidmobile;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.quadroid.quadroidmobile.configuration.Configuration;
import com.quadroid.quadroidmobile.interfaces.OnGcmRegisteredListener;
import com.quadroid.quadroidmobile.util.GCMUtils;
import com.quadroid.quadroidmobile.util.LogUtil;
import com.quadroid.quadroidmobile.util.NotificationUtil;
import com.quadroid.quadroidmobile.util.PreferenceUtils;

/**
 * 
 * @author Georg Baumgarten
 * 
 * This {@link Activity} is used for login. It basically just supports two actions: Login and logout.
 * When the user logs in, the app conenct to Quadroid server and gets a login token. If this was
 * successful, it connected to the GCM servers and gets the device registration id.
 * If this is also successful, the device registration id gets stored on the Quadroid server.
 * Both, token and registration id are stored as app preferences.
 * <p>
 * On logout, only the saved preferences which hold login token and GCM registration id are deleted.
 *
 */
public class LoginActivity extends Activity implements OnGcmRegisteredListener {

	private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1337;
		
	private Button bt_login, bt_logout;
	private EditText edit_username, edit_password;
	private TextView tv_must_login;
	
	private ProgressDialog mProgressDialog;
	
	private GoogleCloudMessaging mGcm;
	
//***************************************************************************************************************
//	Activity related
//***************************************************************************************************************
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		findViews();
		
		configureViews();
		
		NotificationUtil.showNotification(this, Environment.getExternalStorageDirectory().getPath() + "/a.jpg");
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		checkPlayServices();
		configureViews();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
//***************************************************************************************************************
//	View Listeners
//***************************************************************************************************************
	
	/**
	 * This method is called when the user click the login button
	 * @param v
	 */
	public void onLoginClick(View v) {
		String username = edit_username.getText().toString().trim();
		String password = edit_password.getText().toString().trim();
		
		if (username.equals("")) username = "georg@wonderweblabs.com";
		if (password.equals("")) password = "password";
		
		mProgressDialog = getProgressDialog();
		mProgressDialog.show();
		
		LogUtil.debug(getClass(), "Starting Login Process...");
		Ion.with(this, Configuration.LOGIN_URL)
		.addHeader("Accept", "application/vnd.quadroid-server-v1+json")
		.setLogging("Quadroid", Log.DEBUG)
		.setBodyParameter("grant_type", "password")
		.setBodyParameter("email", username)
		.setBodyParameter("password", password)
		.setBodyParameter("client_id", Configuration.CLIENT_ID)
		.setBodyParameter("client_secret", Configuration.CLIENT_SECRET)
		.asJsonObject()
		.setCallback(new FutureCallback<JsonObject>() {
			@Override
			public void onCompleted(Exception e, JsonObject object) {
				//Debug output
				if (object != null)
					LogUtil.debug(getClass(), "Received JSON: " + object.toString());
				
				//Check if the JSON contains all necessary data
				if (object != null && object.has("access_token")) {
					
					//Extract and save login token
					String loginToken = object.get("access_token").getAsString();
					LogUtil.debug(getClass(), "Access Token: " + loginToken);
					PreferenceUtils.putString(LoginActivity.this, R.string.pref_key_login_token, loginToken);
					
					//Get GCM instance and cached registration id
					mGcm = GoogleCloudMessaging.getInstance(LoginActivity.this);
					String regId = GCMUtils.getRegistrationId(LoginActivity.this);
					LogUtil.debug(getClass(), "Cached GCM Registration ID: " + regId);
					
					//register as new GCM device if cached registration id is empty
					if (regId.equals("")) {
						GCMUtils.registerInBackground(mGcm, LoginActivity.this);
					}
				} else {
					//print error if JSON does not contain necessary data or is empty
					String error = String.format(
											getString(R.string.error_login), 
													  e == null ? "No access_token_found" : e.getMessage());
					Toast.makeText(LoginActivity.this, error, Toast.LENGTH_SHORT).show();
					e.printStackTrace();
					cancelProgressDialog();
				}
				LogUtil.debug(getClass(), "Login Process finished");
			}
		});
	}
	
	public void onLogoutClick(View v) {
		PreferenceUtils.removeFromPreferences(this, R.string.pref_key_gcm_reg_id);
		PreferenceUtils.removeFromPreferences(this, R.string.pref_key_login_token);
		configureViews();
	}

//***************************************************************************************************************
//	Helpers
//***************************************************************************************************************
	
	private ProgressDialog getProgressDialog() {
		ProgressDialog dialog = new ProgressDialog(this);
		dialog.setIndeterminate(true);
		dialog.setCancelable(false);
		dialog.setMessage(getString(R.string.login) + "...");
		return dialog;
	}
	
	private void cancelProgressDialog() {
		try {
			mProgressDialog.dismiss();
		} catch (Exception e) {}
	}
	
	/**
	 * Get references to all views
	 */
	private void findViews() {
		bt_login = (Button) findViewById(R.id.bt_login);
		bt_logout = (Button) findViewById(R.id.bt_logout);
		edit_username = (EditText) findViewById(R.id.edit_username);
		edit_password = (EditText) findViewById(R.id.edit_password);
		tv_must_login = (TextView) findViewById(R.id.tv_must_login);
	}
	
	/**
	 * Configure views based on if user is logged or not.
	 */
	private void configureViews() {
		if (isLoggedIn()) {
			tv_must_login.setVisibility(View.INVISIBLE);
			bt_login.setEnabled(false);
			bt_logout.setEnabled(true);
			bt_login.setText(R.string.logged_in);
			bt_logout.setText(R.string.logout);
		} else {
			tv_must_login.setVisibility(View.VISIBLE);
			bt_login.setEnabled(true);
			bt_logout.setEnabled(false);
			bt_login.setText(R.string.login);
			bt_logout.setText(R.string.logged_out);
		}
	}
	
	/**
	 * Check the device to make sure it has the Google Play Services APK. If
	 * it doesn't, display a dialog that allows users to download the APK from
	 * the Google Play Store or enable it in the device's system settings.
	 */
	private boolean checkPlayServices() {
	    int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
	    if (resultCode != ConnectionResult.SUCCESS) {
	        if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
	            GooglePlayServicesUtil.getErrorDialog(resultCode, this,
	                    PLAY_SERVICES_RESOLUTION_REQUEST).show();
	        } else {
	            LogUtil.debug(getClass(), "Google Play Services unrecoverable error. Exiting!");
	            finish();
	        }
	        return false;
	    }
	    return true;
	}
	
	/**
	 * Checks if user is logged in or not
	 * @return
	 * 		true if he is logged in
	 */
	private boolean isLoggedIn() {
		String token = PreferenceUtils.getString(this, R.string.pref_key_login_token, "");
		return !token.equals("");
	}
	

//***************************************************************************************************************
//	Callbacks
//***************************************************************************************************************
	
	@Override
	public void onGcmRegistered(String registrationId) {
		PreferenceUtils.putString(this, R.string.pref_key_gcm_reg_id, registrationId);
		
		if (!registrationId.equals("")) {
			//Now we got both, login token and GCM registration Id, so we can send it to our backend
			String loginToken = PreferenceUtils.getString(this, R.string.pref_key_login_token, "");
			
			Ion.with(this, Configuration.GCM_SETUP_URL)
			.addHeader("Accept", "application/vnd.quadroid-server-v1+json")
			.addHeader("Authorization", "Bearer " + loginToken)
			.setBodyParameter("gcm_device[registration_id]", registrationId)
			.asJsonObject()
			.setCallback(new FutureCallback<JsonObject>() {
				@Override
				public void onCompleted(Exception e, JsonObject object) {
					//Debug output
					if (object != null)
						LogUtil.debug(getClass(), "Received JSON: " + object.toString());
					
					//No errors? Great!
					if (e == null) {
						cancelProgressDialog();
					} else {
						//Show error if there was one
						Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
						cancelProgressDialog();
					}
				}
			});
		} else {
			//There went something wrong while registering the device
			
			//Hide the progress dialog
			cancelProgressDialog();
			
			//Remove login
			PreferenceUtils.removeFromPreferences(this, R.string.pref_key_login_token);
			
			//Reconfigure views
			configureViews();
			
			//Show error
			Toast.makeText(LoginActivity.this, R.string.error_registering_gcm, Toast.LENGTH_LONG).show();
		}
	}
}
