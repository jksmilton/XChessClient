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
import android.widget.Toast;
import android.widget.ExpandableListView.OnChildClickListener;

import com.jksmilton.xchessclient.R;
import com.jksmilton.xchessclient.model.ChessUser;
import com.jksmilton.xchessclient.model.Game;
import com.jksmilton.xchessclient.model.PostURLAccessor;

public class GameHandler implements OnChildClickListener{

	private boolean pending;
	private FragmentActivity parent;
	private ChessUser player;
	private Game game;
	
	public GameHandler(FragmentActivity caller, ChessUser user){
		parent = caller;
		player = user;
	}
	
	public String getHandle(){
		
		if(game.getWhite().equals(player.getHandle())){
			return game.getBlack();
		} else {
			return game.getWhite();
		}
		
	}
	
	@Override
	public boolean onChildClick(ExpandableListView listView, View v, int group,
			int child, long notSure) {
		
		ExpandableListAdapter adapter = listView.getExpandableListAdapter();
		
		game = (Game) adapter.getChild(group, child);
		String groupType = adapter.getGroup(group).toString();
		
		if(groupType.equals("Games")){
			pending = false;
			
		} else {
			pending=true;
			
			ReplyGameRequest reqDialog = new ReplyGameRequest();
			reqDialog.set(this);
			reqDialog.show(parent.getSupportFragmentManager(), "Game Request Accepter Dialog");
			
		}
		
		return true;
	}

	public void handleResponse(boolean accepted){
		
		String url = parent.getResources().getString(R.string.jksmilton_acceptGameReq) + player.getXauth() + "/" + game.getId() + "/" + accepted + "/" + parent.getResources().getString(R.string.appID);
		
		PostInteractionToServer responder = new PostInteractionToServer();
		responder.execute(url);
		
	}
	private class PostInteractionToServer extends PostURLAccessor{

		
		
		@Override
		protected void onPostExecute(Object result) {
			
			Toast.makeText(parent, result.toString(), Toast.LENGTH_SHORT).show();
			Log.d("From game accept", result.toString());
			
		}
		
	}
	public static class ReplyGameRequest extends DialogFragment {
		
		private GameHandler creator;
		
		public void set (GameHandler caller){
			creator = caller;
		}
		
		public Dialog onCreateDialog(Bundle savedInstanceState) {
	        // Use the Builder class for convenient dialog construction
	        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	        builder.setMessage("Accept game request from " + creator.getHandle() + "?")
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
	
}
