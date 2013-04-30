/*************************************************************************
 * jcHistoryTable - A heuristic used to pick an order of evaluation for moves
 *
 * The history heuristic is an extension of the old "killer move" system: if
 * a move has caused a lot of cutoffs recently, it will be tried early in the
 * hope that it will do so again.
 *
 * Using the history table is a gamble.  We could do without it entirely,
 * compute "successor positions" for each possible moves, look them up in
 * the transposition table and hope to get a cutoff this way, which would
 * insure fast cutoffs whenever possible.  On the other hand, HistoryTable
 * has no knowledge of the contents of the transposition table, so it may
 * cause a deep search of several moves even though another one would result
 * in an immediate cutoff...  However, History requires far less memory
 * and computation than creating a ton of successor jcBoard objects, so we
 * hope that, on average, it will still be more efficient overall.
 *
 * History
 * 14.08.00 Creation
 ************************************************************************/

package com.jksmilton.xchessclient.javachess;
import java.util.Collections;
import java.util.Comparator;

public class jcHistoryTable
{
  /***********************************************************************
   * DATA MEMBERS
   **********************************************************************/

  // the table itself; a separate set of cutoff counters exists for each
  // side
  int History[][][];
  int CurrentHistory[][];

  // This is a singleton class; the same history can be shared by two AI's
  private static jcHistoryTable theInstance;

  // A comparator, used to sort the moves


  /***********************************************************************
   * STATIC BLOCK
   ***********************************************************************/
  static
  {
    theInstance = new jcHistoryTable();
  }

  /***********************************************************************
   *  jcMoveComparator - Inner class used in sorting moves
   **********************************************************************/
   class jcMoveComparator implements Comparator<jcMove>
   {
     public int compare( jcMove mov1, jcMove mov2 )
     {
       
    	 if(CurrentHistory[mov2.SourceSquare][mov2.DestinationSquare] > CurrentHistory[mov1.SourceSquare][mov2.DestinationSquare])
    		 return 1;
    	 else if(CurrentHistory[mov2.SourceSquare][mov2.DestinationSquare] < CurrentHistory[mov1.SourceSquare][mov2.DestinationSquare])
    		 return -1;
    	 else
    		 return 0;
    	
     }
   }

   
   public boolean SortMoveList( jcMoveListGenerator theList, int movingPlayer )
   {
     // Which history will we use?
     CurrentHistory = History[ movingPlayer ];

     // Arrays can't sort a dynamic array like jcMoveListGenerator's ArrayList
     // member, so we have to use an intermediate.  Annoying and not too clean,
     // but it works...
     Collections.sort( theList.GetMoveList(), new jcMoveComparator() );
     
     return true;
   }

   
  /************************************************************************
   * PUBLIC METHODS
   ***********************************************************************/

  // Accessor
  public static jcHistoryTable GetInstance()
  {
    return theInstance;
  }

  // Sort a list of moves, using the Java "Arrays" class as a helper


  // History table compilation
  public boolean AddCount( int whichPlayer, jcMove mov )
  {
    History[ whichPlayer ][ mov.SourceSquare ][ mov.DestinationSquare ]++;
    return true;
  }


  // public boolean Forget
  // Once in a while, we must erase the history table to avoid ordering
  // moves according to the results of very old searches
  public boolean Forget()
  {
    for( int i = 0; i < 2; i++ )
      for( int j = 0; j < 64; j++ )
        for( int k = 0; k < 64; k++ )
          History[ i ][ j ][ k ] = 0;
    return true;
  }

  /************************************************************************
   * PRIVATE METHODS
   ***********************************************************************/
  private jcHistoryTable()
  {
    History = new int[ 2 ][ 64 ][ 64 ];
  }
}