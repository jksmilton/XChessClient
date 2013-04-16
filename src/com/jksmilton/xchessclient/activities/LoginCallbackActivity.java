package com.jksmilton.xchessclient.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.jksmilton.xchessclient.R;

public class LoginCallbackActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login_callback);
		
		Intent creationIntent = getIntent();
		
		String data = creationIntent.getDataString();
		
		data = data.substring(9);
		
		Intent mainIntent = new Intent(this, MainActivity.class);
		
		mainIntent.putExtra(getResources().getString(R.string.userjson), data);
		
		startActivity(mainIntent);
		
	}

	

}
