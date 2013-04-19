package com.jksmilton.xchessclient.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.jksmilton.xchessclient.R;

public class LoginCallbackActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login_callback);
		
		Intent creationIntent = getIntent();
		
		String data = creationIntent.getDataString();
		
		data = data.substring(9);
		
		SharedPreferences sharedPref = this.getSharedPreferences(getString(R.string.user_data), MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPref.edit();
		
		editor.putString(getResources().getString(R.string.userjson), data);
		editor.commit();
		
		Log.d("Callback", data);
		Intent mainIntent = new Intent(this, MainActivity.class);
				
		startActivity(mainIntent);
		
	}

	

}
