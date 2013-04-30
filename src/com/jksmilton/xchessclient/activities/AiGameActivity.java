package com.jksmilton.xchessclient.activities;

import java.io.File;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources.NotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.jksmilton.xchessclient.R;
import com.jksmilton.xchessclient.exceptions.MoveException;
import com.jksmilton.xchessclient.javachess.jcAISearchAgent;
import com.jksmilton.xchessclient.javachess.jcBoard;
import com.jksmilton.xchessclient.javachess.jcMove;
import com.jksmilton.xchessclient.javachess.jcOpeningBook;
import com.jksmilton.xchessclient.javachess.jcPlayer;
import com.jksmilton.xchessclient.javachess.jcPlayerAI;
import com.jksmilton.xchessclient.javachess.jcPlayerHuman;
import com.jksmilton.xchessclient.listhandlers.CreatesPawnPromotion;
import com.jksmilton.xchessclient.listhandlers.TileClickHandler.PromotePawn;
import com.jksmilton.xchessclient.model.TileAdapter;

public class AiGameActivity extends FragmentActivity {

	jcBoard board = new jcBoard();
	jcPlayer[] players = new jcPlayer[2];
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chess_solo);
		
		jcOpeningBook book = new jcOpeningBook();
		
		try {
			
			book.Load(getResources().openRawResource(R.raw.openings));
			board.Load(new File(getFilesDir(), getString(R.string.jksmilton_current_game)));
			
		} catch (NotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		SharedPreferences sharedPref = this.getSharedPreferences(getString(R.string.user_data), Context.MODE_PRIVATE);
		boolean isWhite = sharedPref.getBoolean(getString(R.string.jksmilton_solo_side), true);
		
		int i = 1;
		if(isWhite){
			i = 0;
			
		}

		players[i] = new jcPlayerHuman(i);
		players[1-i] = new jcPlayerAI(1-i, jcAISearchAgent.AISEARCH_MTDF, book);
		if((isWhite && board.GetCurrentPlayer() == 1) || (!isWhite && board.GetCurrentPlayer() == 0)){
			
			jcMove move = players[board.GetCurrentPlayer()].GetMove(board);
			board.ApplyMove(move);
			
		}
		
		
		TileAdapter tileAdapter = new TileAdapter(this, getBoard(isWhite));
		
		GridView grid =  (GridView) findViewById(R.id.chessboard);
		grid.setAdapter(tileAdapter);
		grid.setOnItemClickListener(new ClickHandler(isWhite));
	
		TextView info = (TextView) findViewById(R.id.info);
		info.setText("Your turn");
		
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_new_game:
			Intent intent = new Intent(this, NewAiGame.class);
			startActivity(intent);
		}
		return true;
	}

	private String[] getBoard(boolean isWhite){
		
		String[] boardStr = new String[64];
		int i = 0;
		if(!isWhite){
			i = 63;
		}
		
		for(int square= 0; square< 64; square++){
			
			int piece = board.FindWhitePiece(i);
			
			if(piece == jcBoard.EMPTY_SQUARE)
				piece = board.FindBlackPiece(i);
			
			switch(piece){
			case jcBoard.BLACK_BISHOP:{
				boardStr[square] = "black_bishop";
			}
			break;
			
			case jcBoard.BLACK_KING:{
				boardStr[square] = "black_king";
			}
			break;
			
			case jcBoard.BLACK_KNIGHT:{
				boardStr[square] = "black_knight";
			}
			break;
			
			case jcBoard.BLACK_PAWN:{
				boardStr[square] = "black_pawn";
			}
			break;
			
			case jcBoard.BLACK_QUEEN:{
				boardStr[square] = "black_queen";
			}
			break;
			
			case jcBoard.BLACK_ROOK:{
				boardStr[square] = "black_rook";
			}
			break;
			
			case jcBoard.WHITE_PAWN:{
				boardStr[square] = "white_pawn";
			}
			break;
			
			case jcBoard.WHITE_BISHOP:{
				boardStr[square] = "white_bishop";
			}
			break;
			
			case jcBoard.WHITE_KING:{
				boardStr[square] = "white_king";
			}
			break;
			
			case jcBoard.WHITE_KNIGHT:{
				boardStr[square] = "white_knight";
			}
			break;
			
			case jcBoard.WHITE_QUEEN:{
				boardStr[square] = "white_queen";
			}
			break;
			
			case jcBoard.WHITE_ROOK:{
				boardStr[square] = "white_rook";
			}
			break;
			
			default:
				boardStr[square] = "";
			
			}
			
			if(isWhite)
				i ++;
			else
				i= i-1;
			
		}
		
		return boardStr;
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.ai_game, menu);
		return true;
	}

	private class ClickHandler implements OnItemClickListener, CreatesPawnPromotion {

		private String player;
		private int previousPosition = -1;
		private TileAdapter gridAdapter;
		
		public ClickHandler(boolean isWhite){
			
			if(isWhite){
				player= "white";
			} else {
				player = "black";
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
		
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long notSure) {
			
			gridAdapter = (TileAdapter) parent.getAdapter();			
			String piece = (String) gridAdapter.getItem(position);
			
			if((player.equals("white") && board.GetCurrentPlayer() == 1) || (player.equals("black") && board.GetCurrentPlayer() == 0)){
				
				Toast.makeText(view.getContext(), "Not your turn", Toast.LENGTH_SHORT).show();
				
			} else if(piece.startsWith(player)){
				
				previousPosition = position;
				gridAdapter.setSelected(position);
				gridAdapter.notifyDataSetChanged();
				
				
			} else if(previousPosition >= 0){
				
				jcMove move = getMove(previousPosition, position);
				piece = (String) gridAdapter.getItem(previousPosition);
				if(piece.endsWith("pawn") && position < 8){
					
					PromotePawn pawnDialog = new PromotePawn();
					pawnDialog.set(move, this);
					pawnDialog.show(AiGameActivity.this.getSupportFragmentManager(), "Pawn Promotion Dialog");
					
				} else {
					
					sendMove(move);
					
				}
			
			}
		}
		
		public void sendMove(jcMove move){
			
			EngineRunner runner = new EngineRunner();
			runner.execute(move);
			
		}
		
		private class AIEngineRunner extends AsyncTask<Object, Object, jcMove>{

			
			@Override
			protected jcMove doInBackground(Object... arg0) {

				jcMove move = players[board.GetCurrentPlayer()].GetMove(board);
				
				return move;
				
			}
			
			@Override
			protected void onPostExecute(jcMove result){
				board.ApplyMove(result);
				int start = result.SourceSquare, end = result.DestinationSquare;
				if(result.MoveType == jcMove.MOVE_RESIGN){
					((TextView)findViewById(R.id.info)).setText("AI Resigns");
				} else {
					if(!player.equals("white")){
						start = 63-start;
						end = 63- end;
					}
					
					gridAdapter.setSelected(-1);
					gridAdapter.movePiece(start, end);
					
					if(result.MoveType > jcMove.NO_PROMOTION_MASK){
						gridAdapter.promotePawn(result.MoveType, end);
					}
					
					gridAdapter.notifyDataSetChanged();
					((TextView)findViewById(R.id.info)).setText("Your turn");
				}
				
				
				if(result.MoveType == jcMove.MOVE_CASTLING_KINGSIDE || result.MoveType == jcMove.MOVE_CASTLING_QUEENSIDE){
					board.ClearExtraKings(1 - board.GetCurrentPlayer());
					
					gridAdapter.setBoard(getBoard(player.equals("white")));
					
				}
				
				
			}
			
		}
		
	
		
		
		private class EngineRunner extends AsyncTask<jcMove, Object, Boolean>{

			private jcMove playerMove;
			
			@Override
			protected Boolean doInBackground(jcMove... arg0) {

				jcMove move = arg0[0];
				jcPlayerHuman player = (jcPlayerHuman) players[board.GetCurrentPlayer()];
				
				
				
				try {
					move = player.GetMove(board, move, move.MoveType);
					board.ApplyMove(move);
					
					playerMove = move;
					
					
					
				} catch (MoveException e) {
					
					Log.d("Move error","" + e.getType());
					
					switch(e.getType()){
					case MoveException.inCheck : {
						AiGameActivity.this.runOnUiThread(new Runnable() {
							
							@Override
							public void run() {
								Toast.makeText(AiGameActivity.this, "Move leaves you in check", Toast.LENGTH_SHORT).show();
								
							}
						});
						
					}break;
					default : 
						AiGameActivity.this.runOnUiThread(new Runnable() {
							
							@Override
							public void run() {
								
								Toast.makeText(AiGameActivity.this, "Move is not legal", Toast.LENGTH_SHORT).show();
								
							}
						});
					}
					return false;
				}
				return true;
			}
			
			@Override
			protected void onPostExecute(Boolean result){
				
				if(result){
					
					int start = playerMove.SourceSquare, end = playerMove.DestinationSquare;
					
					if(!player.equals("white")){
						start = 63-start;
						end = 63- end;
					} 
					gridAdapter.setSelected(-1);
					gridAdapter.movePiece(start, end);
					gridAdapter.notifyDataSetChanged();
					
					if(playerMove.MoveType == jcMove.MOVE_CASTLING_KINGSIDE || playerMove.MoveType == jcMove.MOVE_CASTLING_QUEENSIDE){
						board.ClearExtraKings(1 - board.GetCurrentPlayer());
						
						gridAdapter.setBoard(getBoard(player.equals("white")));
						
					}
					
					AIEngineRunner runner = new AIEngineRunner();
					runner.execute(new Object());
					((TextView)findViewById(R.id.info)).setText("AI Deliberating");
				}
			}
			
		}
	
		
	}
	
	
	
	protected void onStop(){
		super.onStop();
		
		File save = new File(this.getFilesDir(), getResources().getString(R.string.jksmilton_current_game));
		
		save.delete();
		
		try {
			
			board.Save(save);
		} catch (NotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	
}
