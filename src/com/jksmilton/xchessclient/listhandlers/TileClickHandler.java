package com.jksmilton.xchessclient.listhandlers;

import javachess.jcBoard;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

public class TileClickHandler implements OnItemClickListener {

	private int[] previousPosition = {-1,-1};
	private String player;
	private jcBoard board;
	
	public TileClickHandler(boolean isWhite, jcBoard theBoard){
		
		if(isWhite){
			player="white";
		} else {
			player="black";
		}
		
		board = theBoard;
		
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		
		
		
	}

	
	
}
