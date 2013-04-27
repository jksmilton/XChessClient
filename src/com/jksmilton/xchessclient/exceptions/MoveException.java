package com.jksmilton.xchessclient.exceptions;

public class MoveException extends Exception {

	private static final long serialVersionUID = 1L;

	public static final int illegalStartSquare = 0;
	public static final int illegalEndSquare = 1;
	public static final int inCheck = 2;
	public static final int selfCapture = 3;
	public static final int illegalMoveType = 4;
	public static final int failedToParse = 5;
	private int exceptionType;
	
	public MoveException(int type){
		
		exceptionType = type;
		
	}
	
	public int getType(){
		
		return exceptionType;
	}
	
}
