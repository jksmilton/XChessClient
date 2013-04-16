package com.jksmilton.xchessclient.model;

import java.util.List;

public class ChessUser {

	private String authString;
	private String xauth;
	private String email;
	private String handle;
	private String authSecret;
	private List<String> friends;
	private List<Game> games;
	
	private ChessUser(){}

	public String getAuthString() {
		return authString;
	}

	public void setAuthString(String authString) {
		this.authString = authString;
	}

	public String getXauth() {
		return xauth;
	}

	public void setXauth(String xauth) {
		this.xauth = xauth;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getHandle() {
		return handle;
	}

	public void setHandle(String handle) {
		this.handle = handle;
	}

	public String getAuthSecret() {
		return authSecret;
	}

	public void setAuthSecret(String authSecret) {
		this.authSecret = authSecret;
	}

	public List<String> getFriends() {
		return friends;
	}

	public void setFriends(List<String> friends) {
		this.friends = friends;
	}

	public List<Game> getGames() {
		return games;
	}

	public void setGames(List<Game> games) {
		this.games = games;
	}
	
}
