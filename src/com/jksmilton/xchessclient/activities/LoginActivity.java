package com.jksmilton.xchessclient.activities;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources.NotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.jksmilton.xchessclient.R;

public class LoginActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
	}

	/** Called when the user clicks the Login button */
	public void startLogin(View view) {
	    
		SharedPreferences sharedPref = this.getSharedPreferences(getString(R.string.user_data), Context.MODE_PRIVATE);
		
		String notFound = getResources().getString(R.string.key_default);
		String key = sharedPref.getString(getString(R.string.key), notFound);
				
		ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        
        if (networkInfo != null && networkInfo.isConnected() && !key.equals(notFound)) {
            
        	String url = getResources().getString(R.string.login_key) + key + "/" + getResources().getString(R.string.appID);
            Boolean withKey = true;
        	new CallLoginURLs().execute(url, withKey, this);
        	
        } else if(networkInfo != null && networkInfo.isConnected()){
        	
        	String url = getResources().getString(R.string.login_no_key) + getResources().getString(R.string.appID);
            Boolean withKey = false;
        	new CallLoginURLs().execute(url, withKey, this);
        	
        } else {
        	
        	Toast.makeText(this, getResources().getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
        	Log.d("Login Connection", "No connection found");

        }

        
		
	}
	

	
	
	private class CallLoginURLs extends AsyncTask {
	
		private boolean withKey;
		private Activity parentActivity;
		
		public String readIt(InputStream stream) throws IOException, UnsupportedEncodingException {
		    Reader reader = null;
		    reader = new InputStreamReader(stream, "UTF-8");        
		    boolean ready = true;
		    String output = "";
		    while(ready){
		    	
		    	int read = reader.read();
		    	
		    	if(read < 0)
		    		ready = false;
		    	else
		    		output+= (char) read;
		    	
		    }
		    
		    return output;
		}


		@Override
		protected Object doInBackground(Object... params) {
			InputStream is = null;
			String returnStr = "Fail";
			withKey = (Boolean) params[1];
			parentActivity = (Activity) params[2];
			try {
		        URL url = new URL((String) params[0]);
		        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		        conn.setReadTimeout(10000 /* milliseconds */);
		        conn.setConnectTimeout(15000 /* milliseconds */);
		        conn.setRequestMethod("GET");
		        conn.setDoInput(true);
		        // Starts the query
		        conn.connect();
		        int response = conn.getResponseCode();
		        Log.d("Login without key", "The response is: " + response);
		        is = conn.getInputStream();

		        // Convert the InputStream into a string
		        returnStr = readIt(is);
		        
		       
		        
		    // Makes sure that the InputStream is closed after the app is
		    // finished using it.
		    } catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				returnStr = "malformed URL";
			} catch (NotFoundException e) {
				// TODO Auto-generated catch block
				returnStr = "not found";
			} catch (ProtocolException e) {
				// TODO Auto-generated catch block
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
            
			if(!((String) result).startsWith("http")){
				
				Log.d("Login json", (String) result);
				
				Intent startmain = new Intent(parentActivity, MainActivity.class);
		        
		        startmain.putExtra(getResources().getString(R.string.userjson), (String) result);
		        
		        parentActivity.startActivity(startmain);
				
			} else {
				
				Uri loginUrl = Uri.parse((String) result);
		        
		        Intent webIntent = new Intent(Intent.ACTION_VIEW, loginUrl);
		   
		        parentActivity.startActivity(webIntent);
		        
				
			}
			
       }

		
	}
}
