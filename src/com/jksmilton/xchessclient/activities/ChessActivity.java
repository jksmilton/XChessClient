package com.jksmilton.xchessclient.activities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jksmilton.xchessclient.R;
import com.jksmilton.xchessclient.exceptions.MoveException;
import com.jksmilton.xchessclient.javachess.jcBoard;
import com.jksmilton.xchessclient.javachess.jcMove;
import com.jksmilton.xchessclient.javachess.jcPlayerHuman;
import com.jksmilton.xchessclient.listhandlers.TileClickHandler;
import com.jksmilton.xchessclient.model.PostURLAccessor;
import com.jksmilton.xchessclient.model.TileAdapter;
import com.jksmilton.xchessclient.model.TranscriptAdapter;
import com.jksmilton.xchessclient.model.URLAccessor;

public class ChessActivity extends FragmentActivity {

	public static final String GAME_EXTRA_KEY = "get_game_obj";
	public static final String GAME_PLAYER_COLOUR = "get_player_colour";
	private String[][] pieces = new String[8][8];
	private boolean isWhite = true;
	private String playerKey;
	private long game;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chess);
		
		jcBoard engBoard = new jcBoard();
		// Show the Up button in the action bar.
		setupActionBar();
		engBoard.StartingBoard();
		Intent intent = getIntent();
		
		game = intent.getLongExtra(GAME_EXTRA_KEY, -1);
		isWhite = intent.getBooleanExtra(GAME_PLAYER_COLOUR, false);
		
		SharedPreferences sharedPref = this.getSharedPreferences(getString(R.string.user_data), Context.MODE_PRIVATE);		
		playerKey =  sharedPref.getString(getResources().getString(R.string.key), "");
		
		requestTranscript();
			
	}
	
	private void requestTranscript(){
		
		String url = getResources().getString(R.string.jksmilton_get_game_transcript) + game + "/" + getResources().getString(R.string.appID);
		
		GetTranscript getGame = new GetTranscript();
		
		getGame.execute(url);
		
	}
	

	private void setUpStartBoard(){
		
		String nearSideCol, farSideCol, leftUnique, rightUnique;
		
		if(isWhite){
			nearSideCol= "white_";
			farSideCol = "black_";
			leftUnique = "queen";
			rightUnique = "king";
		} else {
			farSideCol= "white_";
			nearSideCol = "black_";
			leftUnique = "king";
			rightUnique = "queen";
		}
		
		for(int i = 2; i < 6; i++){
			
			for(int j = 0; j<8; j++){
				
				pieces[i][j] = "";
				
			}
			
		}
		
		for (int i = 0; i< 8; i++){
			
			pieces[1][i] = nearSideCol + "pawn";
			pieces[6][i] = farSideCol + "pawn";
			
		}
		
		pieces[0][0] = nearSideCol +"rook";
		pieces[0][1] = nearSideCol +"knight";
		pieces[0][2] = nearSideCol +"bishop";
		
		pieces[0][3] = nearSideCol + leftUnique;
		pieces[0][4] = nearSideCol + rightUnique;
		
		pieces[0][5] = nearSideCol + "bishop";
		pieces[0][6] = nearSideCol +"knight";
		pieces[0][7] = nearSideCol +"rook";
		
		pieces[7][0] = farSideCol +"rook";
		pieces[7][1] = farSideCol +"knight";
		pieces[7][2] = farSideCol +"bishop";
		
		pieces[7][3] = farSideCol + leftUnique;
		pieces[7][4] = farSideCol + rightUnique;
		
		pieces[7][5] = farSideCol + "bishop";
		pieces[7][6] = farSideCol +"knight";
		pieces[7][7] = farSideCol +"rook";
		
		
	}
	
	private String[] convertToDimBoard(){
		String[] dimBoard = new String[64];
		for(int i = 0; i<8; i++){
			for(int j = 0; j < 8; j++){
				
				int dim = TileAdapter.convertCoordToDim(j, i);
				dimBoard[dim] = pieces[i][j];
				
			}
		}
		return dimBoard;
	}

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {

		getActionBar().setDisplayHomeAsUpEnabled(true);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.chess, menu);
		return true;
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
		case R.id.action_refresh: 
			requestTranscript();
			return true;
		case R.id.action_resign :
			PostURLAccessor accessor = new PostURLAccessor() {
				
				@Override
				protected void onPostExecute(Object result) {
					Toast.makeText(ChessActivity.this, result.toString(), Toast.LENGTH_SHORT).show();
					
				}
			};
			
			String url = getResources().getString(R.string.jksmilton_resign_from_game) + playerKey + "/" + game + "/" + getResources().getString(R.string.appID);
			
			accessor.execute(url);
			
		}
		return super.onOptionsItemSelected(item);
	}

	private class GetTranscript extends URLAccessor{

		@Override
		protected void onPostExecute(Object result) {
			
			String moveList = result.toString();
			ListView listView = (ListView) ChessActivity.this.findViewById(R.id.transcript);
			TranscriptAdapter adapter = new TranscriptAdapter(ChessActivity.this, R.layout.child_element, new ArrayList<String>());
			listView.setAdapter(adapter);
			Log.d("recieved transcript", result.toString());
			
			if(moveList.startsWith("[")){
				
				Gson gson = new Gson();
				
				List<String> transcript = Arrays.asList(gson.fromJson(moveList, String[].class));
				Log.d("convert transcript", transcript.toString());
				
				jcBoard engBoard = new jcBoard();
				setUpStartBoard();
				jcMove move = new jcMove();
				
				for(String s : transcript){
					
					adapter.addItem(s);
					
					move = new jcMove();
					move.MoveType = jcMove.MOVE_NORMAL;
					int[] startPos, endPos;
					
					if(s.equals("RESIG") && engBoard.GetCurrentPlayer() == 0){
						
						adapter.addItem("White Resigned");
						break;
						
					} else if(s.equals("RESIG")){
						
						adapter.add("Black Resigned");
						break;
						
					}
					
					String[] sections = s.split(" ");
					startPos = parsePos(sections[0]);
					endPos = parsePos(sections[1]);
					
					move.SourceSquare = TileAdapter.convertCoordToDim(startPos[1], startPos[0]);
					move.DestinationSquare = TileAdapter.convertCoordToDim(endPos[1], endPos[0]);
					
					if(!isWhite){
						
						startPos[0] = 7 - startPos[0];
						startPos[1] = 7 - startPos[1];
						endPos[0] = 7 - endPos[0];
						endPos[1] = 7 - endPos[1];
						
					}
					
					String piece = pieces[startPos[0]][startPos[1]];
					
					if(sections[2].equals("Q")){
						
						move.MoveType = jcMove.MOVE_PROMOTION_QUEEN;
						piece = piece.substring(0, 6) + "queen";
						
					} else if(sections[2].equals("B")){
						
						move.MoveType = jcMove.MOVE_PROMOTION_BISHOP;
						piece = piece.substring(0, 6) + "bishop";
						
					} else if(sections[2].equals("K")){
						
						move.MoveType = jcMove.MOVE_PROMOTION_KNIGHT;
						piece = piece.substring(0, 6) + "knight";
						
					} else if(sections[2].equals("R")){
						
						move.MoveType = jcMove.MOVE_PROMOTION_ROOK;
						piece = piece.substring(0, 6) + "rook";
						
					}
					
					
					
					jcPlayerHuman player = new jcPlayerHuman(engBoard.GetCurrentPlayer());
					
					try {
						move = player.GetMove(engBoard, move, move.MoveType);
					} catch (MoveException e) {
						
						e.printStackTrace();
					}
					
					engBoard.ApplyMove(move);
					
					pieces[endPos[0]][endPos[1]] = piece;
					pieces[startPos[0]][startPos[1]] = "";
					
					Log.d("Applied move from", "start: " + startPos[0] + ";" + startPos[1]);
					Log.d("Applied move to", "end: " + endPos[0] + ";" + endPos[1]);
					
					if((isWhite && engBoard.GetCurrentPlayer() == 0) && move.MoveType == jcMove.MOVE_CASTLING_KINGSIDE){
						
						pieces[0][5] = pieces[0][7];
						pieces[0][7] = "";
						
					} else if((isWhite && engBoard.GetCurrentPlayer() == 0) && move.MoveType == jcMove.MOVE_CASTLING_QUEENSIDE){
						
						pieces[0][3] = pieces[0][0];
						pieces[0][0] = "";
						
					} else if((isWhite && engBoard.GetCurrentPlayer() != 0) && move.MoveType == jcMove.MOVE_CASTLING_KINGSIDE){
						
						pieces[7][5] = pieces[7][7];
						pieces[7][7] = "";
						
					} else if((isWhite && engBoard.GetCurrentPlayer() != 0) && move.MoveType == jcMove.MOVE_CASTLING_QUEENSIDE){
						
						pieces[7][3] = pieces[7][0];
						pieces[7][0] = "";
						
					} else if((!isWhite && engBoard.GetCurrentPlayer() == 0) && move.MoveType == jcMove.MOVE_CASTLING_KINGSIDE){
						
						pieces[7][2] = pieces[7][0];
						pieces[7][0] = "";
						
					} else if((!isWhite && engBoard.GetCurrentPlayer() == 0) && move.MoveType == jcMove.MOVE_CASTLING_QUEENSIDE){
						
						pieces[7][5] = pieces[7][0];
						pieces[7][7] = "";
						
					} else if((!isWhite && engBoard.GetCurrentPlayer() != 0) && move.MoveType == jcMove.MOVE_CASTLING_KINGSIDE){
						
						pieces[0][2] = pieces[0][0];
						pieces[0][0] = "";
						
					} else if((!isWhite && engBoard.GetCurrentPlayer() != 0) && move.MoveType == jcMove.MOVE_CASTLING_QUEENSIDE){
						
						pieces[0][5] = pieces[0][7];
						pieces[0][7] = "";
						
					}
					
				}
				
				if(move.MoveType == jcMove.MOVE_STALEMATE){
					
					adapter.add("STALEMATE");
					
				}
				
				String[] dimBoard = convertToDimBoard();
				
				GridView board = (GridView) findViewById(R.id.chessboard);
				TileAdapter tileAdapter = new TileAdapter(board.getContext(), dimBoard);
				board.setAdapter(tileAdapter);
				
				board.setOnItemClickListener(new TileClickHandler(isWhite, engBoard, ChessActivity.this, playerKey, game));
				
				Log.d("Creating board", "Finished");
				
			}
			
		}
		private int[] parsePos(String move){
			
			int pos[] = {-1,-1};
			char[] chars = move.toCharArray();			
			
			pos[1] = Integer.parseInt(new String("" + chars[1])) - 1;
			
			if(chars[0] == 'A'){
				
				pos[0] = 0; 
				
			} else if(chars[0] == 'B'){
				
				pos[0] = 1;
				
			} else if(chars[0] == 'C'){
				
				pos[0] = 2;
				
			} else if(chars[0] == 'D'){
				
				pos[0] = 3;
				
			} else if(chars[0] == 'E'){
				
				pos[0] = 4;
				
			} else if(chars[0] == 'F'){
				
				pos[0] = 5;
				
			} else if(chars[0] == 'G'){
				
				pos[0] = 6;
				
			} else if(chars[0] == 'H'){
				
				pos[0] = 7;
				
			}
			
			return pos;
			
		}
		
	}
	
}
