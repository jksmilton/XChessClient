package com.jksmilton.xchessclient.activities;

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.jksmilton.xchessclient.R;
import com.jksmilton.xchessclient.javachess.jcBoard;

public class NewAiGame extends Activity {

	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_ai_game);
	}

	public void playWhite(View view){
		
		setUpGame(true);
	}
	
	public void playBlack(View view){
		
		setUpGame(false);
	}
	
	private void setUpGame(boolean isWhite){
	
		SharedPreferences sharedPref = this.getSharedPreferences(getString(R.string.user_data), Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPref.edit();
		
		editor.putBoolean(getResources().getString(R.string.jksmilton_solo_side), isWhite);
		editor.commit();		
		
		jcBoard board = new jcBoard();
		
		File save = new File(this.getFilesDir(), getResources().getString(R.string.jksmilton_current_game));
		
		try {
			save.delete();
			
			board.Save(save);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Intent intent = new Intent(this, AiGameActivity.class);
		startActivity(intent);
		
	}

}
