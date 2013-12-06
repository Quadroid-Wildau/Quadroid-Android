package com.quadroid.quadroidmobile;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.quadroid.quadroidmobile.util.LogUtil;
import com.quadroid.quadroidmobile.util.PreferenceUtils;

public class LoginActivity extends Activity {

	private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1337;
	
	private static final String LOGIN_URL = "http://api.quadroid.com/users/login";
	
	private Button bt_login, bt_logout;
	private EditText edit_username, edit_password;
	private TextView tv_must_login;
	
	private ProgressDialog mProgressDialog;
	
	private AQuery aq;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		findViews();
		
		configureViews();
		
		aq = new AQuery(this);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		checkPlayServices();
		configureViews();
	}
	
	/**
	 * This method is called when the user click the login button
	 * @param v
	 */
	public void onLoginClick(View v) {
		String username = edit_username.getText().toString().trim();
		String password = edit_password.getText().toString().trim();
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("username", username);
		params.put("password", password);
		
		mProgressDialog = createProgressDialog();
		mProgressDialog.show();
		
		aq.ajax(LOGIN_URL, params, JSONObject.class, new AjaxCallback<JSONObject>() {
			@Override
			public void callback(String url, JSONObject object, AjaxStatus status) {
				if (status.getCode() == 200) {
					// Everything is fine, handle result
					
					try {
						String loginToken = object.getString("login_token");
						PreferenceUtils.putString(LoginActivity.this, R.string.pref_key_login_token, loginToken);
					} catch (JSONException e) {
						e.printStackTrace();
					}
					
				} else {
					cancelProgressDialog();
					Toast.makeText(LoginActivity.this, "HTTP Error " + status.getCode() + ": " + status.getError(), Toast.LENGTH_LONG).show();
				}
			}
		});
	}
	
	public void onLogoutClick(View v) {
		
	}
	
	private ProgressDialog createProgressDialog() {
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
	            LogUtil.debug(getClass(), "Google Play Services cannot be installed. Exiting!");
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
}
