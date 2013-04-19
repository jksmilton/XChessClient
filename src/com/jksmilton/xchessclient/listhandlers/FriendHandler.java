package com.jksmilton.xchessclient.listhandlers;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.Toast;

import com.jksmilton.xchessclient.R;
import com.jksmilton.xchessclient.model.PostURLAccessor;

public class FriendHandler implements OnChildClickListener{

	private FragmentActivity parent;
	private String handle, user;
	private boolean pending;
	
	public FriendHandler(FragmentActivity caller, String user){
		
		parent = caller;
		this.user = user;
	}
	
	public String getHandle(){return handle;}
	
	public boolean onChildClick(ExpandableListView listView, View view, int i,
			int j, long notSure) {
		
		ExpandableListAdapter adapter = listView.getExpandableListAdapter();
		
		handle = adapter.getChild(i, j).toString();
		String group = adapter.getGroup(i).toString();

		if(group.equals("Friends")){
			
			pending = false;
			
			RequestGame gameReqDialog = new RequestGame();
			gameReqDialog.set(this);
			gameReqDialog.show(parent.getSupportFragmentManager(), "Game Request With Friend");
			
		} else {
			pending = true;
			
			ReplyFriendRequest reqDialog = new ReplyFriendRequest();
			reqDialog.set(this);
			reqDialog.show(parent.getSupportFragmentManager(), "Friend Request Response");
			
		}
		
		return true;
	}
	
	public void handleResponse(boolean accept){
		PostInteractionToServer postURL = new PostInteractionToServer();
		if(pending){
			String url = parent.getResources().getString(R.string.jksmilton_friendAcceptURL) + user + "/" + handle + "/" + accept + "/" + parent.getResources().getString(R.string.appID);
			
			
			postURL.execute(url);
		} else {
			
			String url = parent.getResources().getString(R.string.jksmilton_requestGameFriend) + user + "/" + handle + "/" + parent.getResources().getString(R.string.appID);
			
			postURL.execute(url);
			
		}
	}
	
	private class PostInteractionToServer extends PostURLAccessor{

		@Override
		protected void onPostExecute(Object result) {
			
			Toast.makeText(parent, result.toString(), Toast.LENGTH_SHORT).show();
			Log.d("From friend accept", result.toString());
			
			
		}
		
	}

	public static class ReplyFriendRequest extends DialogFragment {
		
		private FriendHandler creator;
		
		public void set (FriendHandler caller){
			creator = caller;
		}
		
		public Dialog onCreateDialog(Bundle savedInstanceState) {
	        // Use the Builder class for convenient dialog construction
	        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	        builder.setMessage("Accept friend request from " + creator.getHandle() + "?")
	               .setPositiveButton("Accept", new DialogInterface.OnClickListener() {
	                   public void onClick(DialogInterface dialog, int id) {

	                	   creator.handleResponse(true);
	                	   
	                   }
	               })
	               .setNegativeButton("Reject", new DialogInterface.OnClickListener() {
	                   public void onClick(DialogInterface dialog, int id) {

	                	   creator.handleResponse(false);
	                	   
	                   }
	               });
	        // Create the AlertDialog object and return it
	        return builder.create();
	    }

		
	}
	
	public static class RequestGame extends DialogFragment {
		private FriendHandler creator;
		
		public void set (FriendHandler caller){
			creator = caller;
		}
		
		public Dialog onCreateDialog(Bundle savedInstanceState) {
	        // Use the Builder class for convenient dialog construction
	        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	        builder.setMessage("Request game with " + creator.getHandle() + "?")
	               .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
	                   public void onClick(DialogInterface dialog, int id) {

	                	   creator.handleResponse(true);
	                	   
	                   }
	               })
	               .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
	                   public void onClick(DialogInterface dialog, int id) {

	                	   
	                	   
	                   }
	               });
	        // Create the AlertDialog object and return it
	        return builder.create();
	    }
	}
	
}
