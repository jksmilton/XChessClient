package com.jksmilton.xchessclient.model;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import android.content.res.Resources.NotFoundException;
import android.util.Log;

public abstract class PostURLAccessor extends URLAccessor {

	@Override
	protected Object doInBackground(Object... params){
		
		InputStream is = null;
		String returnStr = "Fail";
		
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
	        Log.d("Acessing ", (String) params[0]);
	        Log.d("The response is: ", ""+ response);
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
	
	

}
