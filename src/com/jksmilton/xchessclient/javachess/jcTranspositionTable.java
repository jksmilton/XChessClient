/*****************************************************************************
 * jcTranspositionTable - Alphabeta's memory
 * by F.D. Laramee
 *
 * Purpose:
 * There are many ways to transpose (i.e., achieve the same position)
 * via different move sequences in chess and in most other 2-player games.
 * This object allows the AISearchAgent to save its search results, so that
 * transpositions will not have to be searched again.
 *
 * Notes:
 * As it is currently implemented, the transposition table is exclusive to its
 * AI player.  Therefore, if the machine is to play against itself (for
 * example, to test new versions of an evaluation function against an established
 * one), there will be two instances of the table active.  For some types of
 * evaluation functions, it would be easy to share a singleton table between
 * two AI players by adding a flag to each entry to identify from whose
 * perspective the evaluation was performed; if we have evaluated from Black's
 * perspective and need the results from White's, we could simply change the
 * sign of the evaluation, and voila.  However, since my evaluation function is
 * NOT entirely symmetrical (i.e., material value depends on the number of pawns
 * owned by the *winning* side, not necessarily the *moving* side), this might
 * introduce errors in the search process.  Memory being dirt cheap these days,
 * this isn't much of an issue.
 *
 * History
 * 14.08.00 Creation
 ****************************************************************************/

package com.jksmilton.xchessclient.javachess;

import com.jksmilton.xchessclient.javachess.jcBoard;

/***************************************************************************
 * A small internal class containing an AB value for a given position, and
 * a "hash lock" signature used to identify collisions between board positions
 * with the same basic hashing values.
 *
 * Note that there is no need to store the actual move leading to this value,
 * for two reasons: first, by the time we check on the transposition table, the
 * move has already been applied; second, our version of alphabeta only handles
 * moves themselves at the top level of the search, so this information would
 * be passed to non one!
 ***************************************************************************/
class jcTranspositionEntry
{
  // Data fields, beginning with the actual value of the board and whether this
  // value represents an accurate evaluation or only a boundary
  public int theEvalType;
  public int theEval;

  // This value was obtained through a search to what depth?  0 means that
  // it was obtained during quiescence search (which is always effectively
  // of infinite depth but only within the quiescence domain; full-width
  // search of depth 1 is still more valuable than whatever Qsearch result)
  public int theDepth;

  // Board position signature, used to detect collisions
  public long theLock;

  // What this entry stored so long ago that it may no longer be useful?
  // Without this, the table will slowly become clogged with old, deep search
  // results for positions with no chance of happening again, and new positions
  // (specifically the 0-depth quiescence search positions) will never be
  // stored!
  public int timeStamp;

  public static final int NULL_ENTRY = -1;

  // construction
  jcTranspositionEntry()
  {
    theEvalType = NULL_ENTRY;
  }
}


public class jcTranspositionTable
{
  /***************************************************************************
   * DATA MEMBERS
   **************************************************************************/

  // The size of a transposition table, in entries
  private static final int TABLE_SIZE = 131072;

  // Data
  private jcTranspositionEntry Table[];

  /**************************************************************************
   * PUBLIC METHODS
   *************************************************************************/

  // Construction
  public jcTranspositionTable()
  {
    Table = new jcTranspositionEntry[ TABLE_SIZE ];
    for ( int i = 0; i < TABLE_SIZE; i++ )
    {
      Table[ i ] = new jcTranspositionEntry();
    }
  }

  // boolean LookupBoard( jcBoard theBoard, jcMove theMove )
  // Verify whether there is a stored evaluation for a given board.
  // If so, return TRUE and copy the appropriate values into the
  // output parameter
  public boolean LookupBoard( jcBoard theBoard, jcMove theMove )
  {
    // Find the board's hash position in Table
    int key = Math.abs( theBoard.HashKey() % TABLE_SIZE );
    jcTranspositionEntry entry = Table[ key ];

    // If the entry is an empty placeholder, we don't have a match
    if ( entry.theEvalType == -1 ) // jcTranspositionEntry.NULL_ENTRY )
      return false;

    // Check for a hashing collision!
    if ( entry.theLock != theBoard.HashLock() )
      return false;

    // Now, we know that we have a match!  Copy it into the output parameter
    // and return
    theMove.MoveEvaluation = entry.theEval;
    theMove.MoveEvaluationType = entry.theEvalType;
    theMove.SearchDepth = entry.theDepth;
    return true;
  }

  // public StoreBoard( theBoard, eval, evalType, depth, timeStamp )
  // Store a good evaluation found through alphabeta for a certain board position
  public boolean StoreBoard( jcBoard theBoard, int eval, int evalType, int depth, int timeStamp )
  {
    int key = Math.abs( theBoard.HashKey() % TABLE_SIZE );

    // Would we erase a more useful (i.e., higher) position if we stored this
    // one?  If so, don't bother!
    if ( ( Table[ key ].theEvalType != jcTranspositionEntry.NULL_ENTRY ) &&
         ( Table[ key ].theDepth > depth ) &&
         ( Table[ key ].timeStamp >= timeStamp ) )
      return true;

    // And now, do the actual work
    Table[ key ].theLock = theBoard.HashLock();
    Table[ key ].theEval = eval;
    Table[ key ].theDepth = depth;
    Table[ key ].theEvalType = evalType;
    Table[ key ].timeStamp = timeStamp;
    return true;
  }
}
