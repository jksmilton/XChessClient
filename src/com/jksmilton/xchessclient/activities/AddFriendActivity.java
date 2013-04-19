package com.jksmilton.xchessclient.activities;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources.NotFoundException;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.jksmilton.xchessclient.R;
import com.jksmilton.xchessclient.model.URLAccessor;

public class AddFriendActivity extends Activity {

	public static final String USER_STRING_RES = "jksmilton.get.user.key";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_friend);
		
		setupActionBar();
		
	}
	
	private void setupActionBar() {

		getActionBar().setDisplayHomeAsUpEnabled(true);

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void attemptAdd(View v){
		
		SharedPreferences sharedPref = this.getSharedPreferences(getString(R.string.user_data), Context.MODE_PRIVATE);
		EditText editText = (EditText) findViewById(R.id.friendHandle);
		String user = sharedPref.getString(getString(R.string.key), "");
		String url = getResources().getString(R.string.jksmilton_addFriendurl) + user + "/" + editText.getText() + "/" + getResources().getString(R.string.appID);
		
		AttemptAdd adder = new AttemptAdd();
		
		adder.execute(url, this);
		
	}
	
	protected class AttemptAdd extends URLAccessor {

		private Activity parentActivity;
		
		@Override
		protected Object doInBackground(Object... params){
			
			InputStream is = null;
			String returnStr = "Fail";
			parentActivity = (Activity) params[1];
			try {
		        URL url = new URL((String) params[0]);
		        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		        conn.setReadTimeout(10000 /* milliseconds */);
		        conn.setConnectTimeout(15000 /* milliseconds */);
		        conn.setRequestMethod("POST");
		        
		        conn.setDoInput(true);
		        // Starts the query
		        conn.connect();
		        int response = conn.getResponseCode();
		        Log.d("Acessing " + params[0], "The response is: " + response);
		        is = conn.getInputStream();

		        // Convert the InputStream into a string
		        returnStr = readIt(is);
		        
		       
		        
		    // Makes sure that the InputStream is closed after the app is
		    // finished using it.
		    } catch (MalformedURLException e) {
				
				returnStr = "malformed URL";
			} catch (NotFoundException e) {
				
				returnStr = "not found";
			} catch (ProtocolException e) {
				
				returnStr = "protocol exception";
			} catch (IOException e) {
				returnStr = "IO Exception: " + e.getMessage();
			} finally {
		        if (is != null) {
		            try {
						is.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		        } 
		    }
			return returnStr;
			
		}
		
		@Override
		protected void onPostExecute(Object result) {

			if(result.equals(getResources().getString(R.string.jksmilton_friendAddSuccess))){
				
				Intent back = new Intent(parentActivity, MainActivity.class);
				
				startActivity(back);
				
			} else {
				
				Toast.makeText(parentActivity, result.toString(), Toast.LENGTH_SHORT).show();
				Log.d("Add Friend", result.toString());
				
			}
			
		}
		
		
		
	}
	

}
