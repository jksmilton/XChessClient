package com.jksmilton.xchessclient.activities;

import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jksmilton.xchessclient.R;
import com.jksmilton.xchessclient.model.ChessUser;
import com.jksmilton.xchessclient.model.PostURLAccessor;

public class CreateGameActivity extends FragmentActivity {
	private ChessUser user;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_game);
		// Show the Up button in the action bar.
		setupActionBar();
		
		SharedPreferences sharedPref = getSharedPreferences(getString(R.string.user_data), Context.MODE_PRIVATE);
		
		String userStr = sharedPref.getString(getResources().getString(R.string.userjson), "");
		String userKey = sharedPref.getString(getResources().getString(R.string.key), "");
		Gson gson = new Gson();
		
		user = gson.fromJson(userStr, ChessUser.class);
		user.setXauth(userKey);
	}

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
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

	public void requestRandomGame(View v){
		
		String url = getResources().getString(R.string.jksmilton_requestRandomgGame) + user.getXauth() + "/" + getResources().getString(R.string.appID);
		
		SendRequest requester = new SendRequest();
		requester.setRandom(true);
		requester.execute(url);
		
	}
	
	public void requestGameWithFriend(View v){
		
		StartGameWithFriend newGameDialog = new StartGameWithFriend();
		
		newGameDialog.set(user.getFriends(), this, user.getXauth());
		
		newGameDialog.show(this.getSupportFragmentManager(), "Add friend from list");
		
	}
	
	public void sendRequest(String url){
		
		SendRequest requester = new SendRequest();
		requester.setRandom(false);
		requester.execute(url);
		
	}
	
	public static class StartGameWithFriend extends DialogFragment {
		
		private String[] listToShow = new String[1];
		private String player;
		private CreateGameActivity parent;
		
		public void set(List<String> friends, CreateGameActivity caller, String user){
			
			listToShow = friends.toArray(listToShow);
			player = user;
			parent = caller;
			
		}
		
		public Dialog onCreateDialog(Bundle savedInstanceState) {
		    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		    builder.setTitle(R.string.jksmilton_pick_friend);
		    
		    builder.setItems(listToShow, new DialogInterface.OnClickListener() {
		               public void onClick(DialogInterface dialog, int which) {
		               // The 'which' argument contains the index position
		               // of the selected item
		            	   String url = parent.getResources().getString(R.string.jksmilton_requestGameFriend) + player + "/" + listToShow[which] + "/" + parent.getResources().getString(R.string.appID);
		            	   parent.sendRequest(url);
		            	   
		           }
		    });
		    return builder.create();
		}
		
	}
	
	private class SendRequest extends PostURLAccessor {

		private boolean isRandom;
		
		public void setRandom(boolean random){
			isRandom = random;
		}
		
		@Override
		protected void onPostExecute(Object result) {
			Log.d("Requesting game ...", result.toString());
			if(!isRandom){
				Toast.makeText(CreateGameActivity.this, result.toString(), Toast.LENGTH_SHORT).show();
				
			} else {
				
				if(result.equals(user.getHandle())){
					
					Toast.makeText(CreateGameActivity.this, "Added to game queue", Toast.LENGTH_SHORT).show();
					
				} else {
					
					Toast.makeText(CreateGameActivity.this, "You have challenged " + result, Toast.LENGTH_SHORT).show();
					
				}
				
			}
			
			Intent backToMain = new Intent(CreateGameActivity.this, MainActivity.class);
			
			CreateGameActivity.this.startActivity(backToMain);
			
		}
		
	}
	
}
