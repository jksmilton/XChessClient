package com.jksmilton.xchessclient.model;

public class Game {

	private Long id;
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getWhite() {
		return white;
	}

	public void setWhite(String white) {
		this.white = white;
	}

	public String getBlack() {
		return black;
	}

	public void setBlack(String black) {
		this.black = black;
	}

	private String white;
	private String black;
	private int turn;
	public int getTurn() {
		return turn;
	}

	public void setTurn(int turn) {
		this.turn = turn;
	}

	public Game(){}
	
	public String toString(){
		
		String vs;
		
		if(turn==1)
			vs = " VS (turn) ";
		else
			vs = " (turn) VS ";
		
		return white + vs + black;
		
	}
	
}
