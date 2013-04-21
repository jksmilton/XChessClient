package com.jksmilton.xchessclient.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.GridView;

import com.jksmilton.xchessclient.R;
import com.jksmilton.xchessclient.model.TileAdapter;

public class ChessActivity extends Activity {

	String[][] pieces = new String[8][8];
	boolean isWhite = true;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chess);
		// Show the Up button in the action bar.
		setupActionBar();
		
		String[] dimBoard = setUpStartBoard();
		
		GridView board = (GridView) findViewById(R.id.chessboard);
		TileAdapter tileAdapter = new TileAdapter(board.getContext(), dimBoard, isWhite);
		board.setAdapter(tileAdapter);
		
		Log.d("Creating board", "Finished");
			
	}
	
	private String[] setUpStartBoard(){
		
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
		}
		return super.onOptionsItemSelected(item);
	}

}
