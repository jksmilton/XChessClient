/**************************************************************************
 *
 * jcPlayerAI.java - Interface to a computer player
 * by Fran�ois Dominic Laram�e
 *
 * Purpose: This object allows a computer player to play JavaChess.  Its only
 * real job is to query an AI Search Agent for his move.
 *
 * History:
 * 11.06.00 Creation
 * 07.08.00 Association with the search agent
 *
 **************************************************************************/

package com.jksmilton.xchessclient.javachess;

import com.jksmilton.xchessclient.javachess.jcAISearchAgent;

public class jcPlayerAI extends jcPlayer
{
  /************************************************************************
   * DATA MEMBERS
   ***********************************************************************/

  // The search agent in charge of the moves
  jcAISearchAgent Agent;

  /***********************************************************************
   * PUBLIC METHODS
   **********************************************************************/

  // Constructor
  public jcPlayerAI( int whichPlayer, int whichType, jcOpeningBook ref )
  {
    this.SetSide( whichPlayer );
    Agent = jcAISearchAgent.MakeNewAgent( whichType, ref );
  }

  // Attach a search agent to the AI player
  public boolean AttachSearchAgent( jcAISearchAgent theAgent )
  {
    Agent = theAgent;
    return true;
  }

  // Getting a move from the machine
  public jcMove GetMove( jcBoard theBoard )
  {
    return( Agent.PickBestMove( theBoard ) );
  }
}