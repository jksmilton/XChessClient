package com.jksmilton.xchessclient.listhandlers;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.jksmilton.xchessclient.R;
import com.jksmilton.xchessclient.exceptions.MoveException;
import com.jksmilton.xchessclient.javachess.jcBoard;
import com.jksmilton.xchessclient.javachess.jcMove;
import com.jksmilton.xchessclient.javachess.jcPlayerHuman;
import com.jksmilton.xchessclient.model.PostURLAccessor;
import com.jksmilton.xchessclient.model.TileAdapter;
import com.jksmilton.xchessclient.model.TranscriptAdapter;

public class TileClickHandler implements OnItemClickListener, CreatesPawnPromotion {

	private int previousPosition = -1;
	private String player;
	private jcBoard board;
	private FragmentActivity activity;
	private TileAdapter gridAdapter;
	private jcMove move;
	private String playerKey;
	private long game;
	
	public TileClickHandler(boolean isWhite, jcBoard theBoard, FragmentActivity parent, String key, long crnt){
		
		if(isWhite){
			player="white";
		} else {
			player="black";
		}
		
		board = theBoard;
		activity = parent;
		playerKey = key;
		game = crnt;
		
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		
		gridAdapter = (TileAdapter) parent.getAdapter();
		
		String piece = (String) gridAdapter.getItem(position);
		
		if((player.equals("white") && board.GetCurrentPlayer() == 1) || (player.equals("black") && board.GetCurrentPlayer() == 0)){
			
			Toast.makeText(view.getContext(), "Not your turn", Toast.LENGTH_SHORT).show();
			
		} else if(piece.startsWith(player)){
			
			previousPosition = position;
			gridAdapter.setSelected(position);
			gridAdapter.notifyDataSetChanged();
			
			
		} else if(previousPosition >= 0){
			
			move = getMove(previousPosition, position);
			piece = (String) gridAdapter.getItem(previousPosition);
			if(piece.endsWith("pawn") && position < 8){
				
				PromotePawn pawnDialog = new PromotePawn();
				pawnDialog.set(move, this);
				pawnDialog.show(activity.getSupportFragmentManager(), "Pawn Promotion Dialog");
				
			} else {
				
				sendMove(move);
				
			}
			
		}
		
	}
	
	private jcMove getMove(int start, int end){
		
		jcMove move = new jcMove();
		move.MoveType = jcMove.MOVE_NORMAL;
		if(player.equals("black")){
			
			start = 63 - start;
			end = 63 - end;
			
		}
		
		move.DestinationSquare = end;
		move.SourceSquare = start;
		
		return move;
		
	}

	public void sendMove(jcMove move){
		
		MoveChecker moveChecker = new MoveChecker();
		this.move = move;
		moveChecker.execute(move);
		
	}
	private String convertMove(int square){
		
		int[] tile = TileAdapter.convertDimToCoord(square);
		
		
		
		tile[1] += 1;
		
		String move = "";
		
		switch(tile[0]){
		case 0: move+= "A";break;
		case 1: move+= "B";break;
		case 2: move+= "C";break;
		case 3: move+= "D";break;
		case 4: move+= "E";break;
		case 5: move+= "F";break;
		case 6: move+= "G";break;
		case 7: move+= "H";
		}
		
		move+= tile[1];
		
		return move;
	}
	
	private String convertPromote(int type){
		
		switch(type){
		case jcMove.MOVE_PROMOTION_QUEEN: return "Q";
		case jcMove.MOVE_PROMOTION_KNIGHT: return "K";
		case jcMove.MOVE_PROMOTION_BISHOP: return "B";
		case jcMove.MOVE_PROMOTION_ROOK: return "R";
		default: return "NA";
		}
		
	}
	public static class PromotePawn extends DialogFragment {
		
		private jcMove move;
		private CreatesPawnPromotion toSend;
		
		public void set(jcMove crntMove, CreatesPawnPromotion parent){
			
			move = crntMove;
			toSend = parent;
			
		}
		
		public Dialog onCreateDialog(Bundle savedInstanceState) {
		    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		    builder.setTitle(R.string.jksmilton_promotion);
		    
		    builder.setItems(R.array.promotion_choice, new DialogInterface.OnClickListener() {
		               public void onClick(DialogInterface dialog, int which) {
		               // The 'which' argument contains the index position
		               // of the selected item
		            	   switch(which){
		            	   case 0 : move.MoveType = jcMove.MOVE_PROMOTION_QUEEN; break;
		            	   case 1 : move.MoveType = jcMove.MOVE_PROMOTION_BISHOP; break;
		            	   case 2 : move.MoveType = jcMove.MOVE_PROMOTION_KNIGHT; break;
		            	   case 3 : move.MoveType = jcMove.MOVE_PROMOTION_ROOK; break;
		            	   default : ;
		            	   }
		            	   
		            	   toSend.sendMove(move);
		           }
		    });
		    return builder.create();
		}
		
	}
	
	private class MoveChecker extends AsyncTask<jcMove, Object, jcMove> {

		@Override
		protected jcMove doInBackground(jcMove... params) {

									
			jcPlayerHuman player = new jcPlayerHuman(board.GetCurrentPlayer());
			
			try {
				
				move = player.GetMove(board, move, move.MoveType);
								
			} catch (MoveException e) {
				
				Log.d("Move error","" + e.getType());
				
				switch(e.getType()){
				case MoveException.inCheck : {
					activity.runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							Toast.makeText(activity, "Move leaves you in check", Toast.LENGTH_SHORT).show();
							
						}
					});
					
				}break;
				default : 
					activity.runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							
							Toast.makeText(activity, "Move is not legal", Toast.LENGTH_SHORT).show();
							Log.d("illegal move", "start: " + move.SourceSquare + "; end: " + move.DestinationSquare);
						}
					});
					
				}
				
				move.MoveType = jcMove.NULL_MOVE;
				
			}
			
			return move;
		}
		
		@Override
        protected void onPostExecute(jcMove result){
			
			PostMove urlPostMove = new PostMove();
			
			String start = convertMove(result.SourceSquare), end = convertMove(result.DestinationSquare), promotion = convertPromote(result.MoveType);
			
			String url = activity.getResources().getString(R.string.jksmilton_submit_move) + playerKey + "/" + game
					+ "/" + start + "/" + end + "/" + promotion + "/"
					+ activity.getResources().getString(R.string.appID);
			
			if(result.MoveType != jcMove.NULL_MOVE){
				
				urlPostMove.execute(url);
				
			} 
			
		}
	

		
		
	}
	
	private class PostMove extends PostURLAccessor {

		
		@Override
		protected void onPostExecute(Object result) {
			
			Toast.makeText(activity, (String) result, Toast.LENGTH_SHORT).show();
			String moveStr = convertMove(move.SourceSquare) + " " + convertMove(move.DestinationSquare) + " " + convertPromote(move.MoveType);
			
			if(result.equals("Success")){
				
				int start, end;
				start = move.SourceSquare;
				end = move.DestinationSquare;
				
				if(!player.equals("white")){
					
					start = 63- start;
					end = 63 - end;
					
				} 
				
				board.ApplyMove(move);
				gridAdapter.movePiece(start, end);
				
				if(move.MoveType > jcMove.NO_PROMOTION_MASK){
					gridAdapter.promotePawn(move.MoveType, end);
				}
				
				if(move.MoveType == jcMove.MOVE_CASTLING_KINGSIDE){
					board.ClearExtraKings(1 - board.GetCurrentPlayer());
					
					if(player.equals("white")){
						
						gridAdapter.movePiece(63, 63 - 2);
						
					} else {
						
						gridAdapter.movePiece(63-7, 63-5);
						
					}
					
				} else if (move.MoveType == jcMove.MOVE_CASTLING_QUEENSIDE) {
					board.ClearExtraKings(1 - board.GetCurrentPlayer());
					
					if(player.equals("white")){
						
						gridAdapter.movePiece(63 - 7, 63 - 4);
						
					} else {
						
						gridAdapter.movePiece(63, 63 - 3);
						
					}
					
				}
				
				gridAdapter.setSelected(-1);
				previousPosition = -1;
				gridAdapter.notifyDataSetChanged();
				TranscriptAdapter adapter = (TranscriptAdapter) ((ListView) activity.findViewById(R.id.transcript)).getAdapter(); 
				
				adapter.addItem(moveStr);
				
			}
			
		}
		
		
	}
	
}
