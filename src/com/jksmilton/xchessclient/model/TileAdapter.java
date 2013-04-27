package com.jksmilton.xchessclient.model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.jksmilton.xchessclient.R;

public class TileAdapter extends BaseAdapter {

	private String[] pieces;
	private int selected = -1;
	private LayoutInflater inflater;
	

	public TileAdapter(Context c, String[] board){
		inflater = LayoutInflater.from(c);
		pieces = board;
	}
	
	public void movePiece(int start, int end){
		
		pieces[end] = pieces[start];
		pieces[start] = new String("");
		
	}
	
	public void setSelected(int select){
		
		selected = select;
		
	}
	
	@Override
	public int getCount() {
		return 64;
	}

	@Override
	public Object getItem(int arg0) {
		
		return pieces[arg0];
	}

	@Override
	public long getItemId(int arg0) {
		
		return arg0;
	}

	public static int convertCoordToDim(int x, int y){
		
		int toReturn = 0;
		
		toReturn+= 8 * (7 - y);
		
		toReturn += x;
		
		return toReturn;
		
	}
	
	public static int[] convertDimToCoord(int pos){
		
		int[] coords = new int[2];
		
		coords[1] = pos % 8;
		
		coords[0] = (int) Math.floor((double) pos / 8.0) + 1;
		
		coords[0] = 8 - coords[0];
		
		return coords;
		
	}
	
	@Override
	public View getView(int position, View v, ViewGroup vg) {
		View returning = v;
		if(v==null){
			
			returning= inflater.inflate(R.layout.tile, vg, false);
			
		}
		
		ImageView square = (ImageView) returning.findViewById(R.id.square);
		
		int[] pos = TileAdapter.convertDimToCoord(position);
		
		//Log.d("Creating view at", "x: " + pos[1] + "; y: " + pos[0]);
		
		if(selected == position){
			
			square.setImageResource(R.drawable.selected_square);
			
		} else if((pos[0]+pos[1]) % 2  == 1){
			square.setImageResource(R.drawable.white_square);
		} else{
			square.setImageResource(R.drawable.black_square);
		}
		
		ImageView image = (ImageView) returning.findViewById(R.id.piece);
		
		image.setVisibility(View.VISIBLE);
		
		if(pieces[position].equals("black_rook")){
			
			image.setImageResource(R.drawable.black_rook);
			
		} else if(pieces[position].equals("black_queen")){
			
			image.setImageResource(R.drawable.black_queen);
			
		} else if(pieces[position].equals("black_king")){
			
			image.setImageResource(R.drawable.black_king);
			
		} else if(pieces[position].equals("black_knight")){
			
			image.setImageResource(R.drawable.black_knight);
			
		} else if(pieces[position].equals("black_bishop")){
			
			image.setImageResource(R.drawable.black_bishop);
			
		} else if(pieces[position].equals("black_pawn")){
			
			image.setImageResource(R.drawable.black_pawn);
			
		} else if(pieces[position].equals("white_rook")){
			
			image.setImageResource(R.drawable.white_rook);
			
		} else if(pieces[position].equals("white_queen")){
			
			image.setImageResource(R.drawable.white_queen);
			
		} else if(pieces[position].equals("white_king")){
			
			image.setImageResource(R.drawable.white_king);
			
		} else if(pieces[position].equals("white_bishop")){
			
			image.setImageResource(R.drawable.white_bishop);
			
		} else if(pieces[position].equals("white_pawn")){
			
			image.setImageResource(R.drawable.white_pawn);
			
		} else if(pieces[position].equals("white_knight")){
			
			image.setImageResource(R.drawable.white_knight);
			
		} else {
			
			image.setVisibility(View.INVISIBLE);
			
		}
		
		
		return returning;
	}

}
