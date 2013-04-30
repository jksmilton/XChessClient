/****************************************************************************
 * jcOpeningBook - A hash table of well-known positions and moves
 *
 * Chess programs are notoriously bad at deciding what to do with complicated
 * positions, so everyone "cheats" by giving them a library of opening positions
 * taken from the ECO or something like that.  This one is very primitive and
 * contains very little, but it gets the job done.
 *
 * History:
 * 19.09.00 Creation
 *
 ****************************************************************************/

package com.jksmilton.xchessclient.javachess;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StreamTokenizer;


/****************************************************************************
 * PRIVATE class jcOpeningBookEntry
 * A signature for a board position, and the best moves for White and Black
 * in that position.
 ***************************************************************************/

class jcOpeningBookEntry
{
  // A signature for the board position stored in the entry
  int theLock;

  // Moves
  jcMove WhiteMove;
  jcMove BlackMove;

  // A sentinel indicating that a move is invalid
  public static final int NO_MOVE = -1;

  // Construction
  jcOpeningBookEntry()
  {
    theLock = 0;
    WhiteMove = new jcMove();
    WhiteMove.MoveType = NO_MOVE;
    BlackMove = new jcMove();
    BlackMove.MoveType = NO_MOVE;
  }
}


/*****************************************************************************
 * PUBLIC class jcOpeningBook
 * A hash table containing a certain number of slots for well-known positions
 ****************************************************************************/

public class jcOpeningBook
{
  // The hash table itself
  private static final int TABLE_SIZE = 1024;
  private jcOpeningBookEntry Table[];

  // Construction
  public jcOpeningBook()
  {
    Table = new jcOpeningBookEntry[ TABLE_SIZE ];
    for ( int i = 0; i < TABLE_SIZE; i++ )
    {
      Table[ i ] = new jcOpeningBookEntry();
    }
  }

  // public jcMove Query
  // Querying the table for a ready-made move to play.  Return null if there
  // is none
  public jcMove Query( jcBoard theBoard )
  {
    // First, look for a match in the table
    int key = Math.abs( theBoard.HashKey() % TABLE_SIZE );
    int lock = theBoard.HashLock();

    // If the hash lock doesn't match the one for our position, get out
    if ( Table[ key ].theLock != lock )
      return null;

    // If there is an entry for this board in the table, verify that it
    // contains a move for the current side
    if ( theBoard.GetCurrentPlayer() == jcPlayer.SIDE_BLACK )
    {
      if ( Table[ key ].BlackMove.MoveType != jcOpeningBookEntry.NO_MOVE )
        return Table[ key ].BlackMove;
    }
    else
    {
      if ( Table[ key ].WhiteMove.MoveType != jcOpeningBookEntry.NO_MOVE )
        return Table[ key ].WhiteMove;
    }

    // If we haven't found anything useful, quit
    return null;
  }

  // Loading the table from a file
  public boolean Load( InputStream stream ) throws Exception
  {
    // Open the file as a Java tokenizer
    InputStreamReader reader = new InputStreamReader(stream);
    BufferedReader fr = new BufferedReader( reader );
    StreamTokenizer tok = new StreamTokenizer( fr );
    tok.eolIsSignificant( false );
    tok.lowerCaseMode( false );

    // Create a game board on which to "play" the opening sequences stored in
    // the book, so that we know which position to associate with which move
    jcBoard board = new jcBoard();
    jcMove mov = new jcMove();
    jcMoveListGenerator successors = new jcMoveListGenerator();

    // How many lines of play do we have in the book?
    tok.nextToken();
    int numLines = (int) tok.nval;

    for( int wak = 0; wak < numLines; wak++ )
    {
      // Begin the line of play with a clean board
      board.StartingBoard();

      // Load the continuation
      while( true )
      {
        successors.ComputeLegalMoves( board );

        // Is the token an end-of-continuation marker?
        // If so, go on to the next continuation
        if( ( tok.nextToken() == StreamTokenizer.TT_WORD ) && ( tok.sval.equalsIgnoreCase( "END" ) ) )
        {
          break;
        }

        if ( tok.ttype == StreamTokenizer.TT_EOL )
          tok.nextToken();

        // If not, gather the source and destination squares of the next move
        int source = (int) tok.nval;
        tok.nextToken();
        int destination = (int) tok.nval;

        // Make a jcMove structure out of the source and destination squares;
        // this determines whether there is a capture involved, a castling, etc.
        mov = successors.FindMoveForSquares( source, destination );

        // And now, store the move in the table
        StoreMove( board, mov );

        // Finally, apply the move and get ready for the next one
        board.ApplyMove( mov );

      }
    }

    fr.close();
    return true;
  }


  // private StoreMove( jcBoard, jcMov )
  private boolean StoreMove( jcBoard theBoard, jcMove theMove )
  {
    // Where should we store this data?
    int key = Math.abs( theBoard.HashKey() % TABLE_SIZE );
    int lock = theBoard.HashLock();

    // Is there already an entry for a different board position where we
    // want to put this?  If so, mark it deleted
    if ( Table[ key ].theLock != lock )
    {
      Table[ key ].BlackMove.MoveType = jcOpeningBookEntry.NO_MOVE;
      Table[ key ].WhiteMove.MoveType = jcOpeningBookEntry.NO_MOVE;
    }

    // And store the new move
    Table[ key ].theLock = lock;
    if ( theBoard.GetCurrentPlayer() == jcPlayer.SIDE_BLACK )
    {
      Table[ key ].BlackMove.Copy( theMove );
    }
    else
    {
      Table[ key ].WhiteMove.Copy( theMove );
    }

    return true;
  }
}