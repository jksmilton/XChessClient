/***************************************************************************
 * jcAISearchAgentMTDF - A sophisticated search agent
 *
 * Purpose: A (mostly) state-of-the-art search agent, implementing advanced
 * techniques like the iterative-deepening MTDF search algorithm, transposition
 * table, opening book and history table.
 *
 * History:
 * 05.10.00 Completed initial version
 **************************************************************************/

package com.jksmilton.xchessclient.javachess;

import com.jksmilton.xchessclient.javachess.jcAISearchAgent;
import com.jksmilton.xchessclient.javachess.jcBoard;
import com.jksmilton.xchessclient.javachess.jcOpeningBook;

public class jcAISearchAgentMTDF extends jcAISearchAgent
{
  // A reference to the game's opening book
  private jcOpeningBook Openings;

  // A measure of the effort we are willing to expend on search
  private static final int MaxSearchSize = 50000;

  // Construction
  public jcAISearchAgentMTDF( jcOpeningBook ref )
  {
    super();
    Openings = ref;
  }

  /****************************************************************************
   * PUBLIC METHODS
   ***************************************************************************/

  // Move selection: An iterative-deepening paradigm calling MTD(f) repeatedly
  public jcMove PickBestMove( jcBoard theBoard )
  {
    // First things first: look in the Opening Book, and if it contains a
    // move for this position, don't search anything
    MoveCounter++;
    jcMove Mov = null;
    Mov = Openings.Query( theBoard );
    if ( Mov != null )
      return Mov;

    // Store the identity of the moving side, so that we can tell Evaluator
    // from whose perspective we need to evaluate positions
    FromWhosePerspective = theBoard.GetCurrentPlayer();

    // Should we erase the history table?
    if ( ( Rnd.nextInt() % 6 ) == 2 )
      HistoryTable.Forget();

    // Begin search.  The search's maximum depth is determined on the fly,
    // according to how much effort has been spent; if it's possible to search
    // to depth 8 in 5 seconds, then by all means, do it!
    int bestGuess = 0;
    int iterdepth = 1;

    while( true )
    {
      // Searching to depth 1 is not very effective, so we begin at 2
      iterdepth++;

      // Compute efficiency statistics
      NumRegularNodes = 0; NumQuiescenceNodes = 0;
      NumRegularTTHits = 0; NumQuiescenceTTHits = 0;
      NumRegularCutoffs = 0; NumQuiescenceCutoffs = 0;

      // Look for a move at the current depth
      Mov = MTDF( theBoard, bestGuess, iterdepth );
      bestGuess = Mov.MoveEvaluation;

      // Feedback!
      System.out.print( "Iteration of depth " + iterdepth + "; best move = " );
      Mov.Print();
      System.out.print( "  --> Transposition Table hits for regular nodes: " );
      System.out.println( NumRegularTTHits + " of " + NumRegularNodes );
      System.out.print( "  --> Transposition Table hits for quiescence nodes: " );
      System.out.println( NumQuiescenceTTHits + " of " + NumQuiescenceNodes );
      System.out.println( "  --> Number of cutoffs for regular nodes: " + NumRegularCutoffs );
      System.out.println( "  --> Number of cutoffs in quiescence search: " + NumQuiescenceCutoffs );

      // Get out if we have searched deep enough
      if ( ( NumRegularNodes + NumQuiescenceNodes ) > MaxSearchSize )
        break;
      if ( iterdepth >= 2 )
        break;
    }

    return Mov;
  }

  /***************************************************************************
   * PRIVATE METHODS
   **************************************************************************/

  // private jcMove MTDF
  // Use the MTDF algorithm to find a good move.  MTDF repeatedly calls
  // alphabeta with a zero-width search window, which creates very many quick
  // cutoffs.  If alphabeta fails low, the next call will place the search
  // window lower; in a sense, MTDF is a sort of binary search mechanism into
  // the minimax space.
  private jcMove MTDF( jcBoard theBoard, int target, int depth )
  {
    int beta;
    jcMove Mov;
    int currentEstimate = target;
    int upperbound = ALPHABETA_MAXVAL;
    int lowerbound = ALPHABETA_MINVAL;

    // This is the trick: make repeated calls to alphabeta, zeroing in on the
    // actual minimax value of theBoard by narrowing the bounds
    do {
      if ( currentEstimate == lowerbound )
        beta = currentEstimate + 1;
      else
        beta = currentEstimate;

      Mov = UnrolledAlphabeta( theBoard, depth, beta - 1, beta );
      currentEstimate = Mov.MoveEvaluation;

      if ( currentEstimate < beta )
        upperbound = currentEstimate;
      else
        lowerbound = currentEstimate;

    } while ( lowerbound < upperbound );

    return Mov;
  }

  // private jcMove UnrolledAlphabeta
  // The standard alphabeta, with the top level "unrolled" so that it can
  // return a jcMove structure instead of a mere minimax value
  // See jcAISearchAgent.Alphabeta for detailed comments on this code
  private jcMove UnrolledAlphabeta( jcBoard theBoard, int depth, int alpha,
                                    int beta )
  {
    jcMove BestMov = new jcMove();

    jcMoveListGenerator movegen = new jcMoveListGenerator();
    movegen.ComputeLegalMoves( theBoard );
    HistoryTable.SortMoveList(movegen, theBoard.GetCurrentPlayer());

    jcBoard newBoard = new jcBoard();
    int bestSoFar;

    bestSoFar = ALPHABETA_MINVAL;
    int currentAlpha = alpha;
    jcMove mov;

    // Loop on the successors
    while( ( mov = movegen.Next() ) != null )
    {
      // Compute a board position resulting from the current successor
      newBoard.Clone( theBoard );
      newBoard.ApplyMove( mov );

      // And search it in turn
      int movScore = AlphaBeta( MINNODE, newBoard, depth - 1, currentAlpha, beta );

      // Ignore illegal moves in the alphabeta evaluation
      if ( movScore == ALPHABETA_ILLEGAL )
        continue;
      currentAlpha = Math.max( currentAlpha, movScore );

      // Is the current successor better than the previous best?
      if ( movScore > bestSoFar )
      {
        BestMov.Copy( mov );
        bestSoFar = movScore;
        BestMov.MoveEvaluation = bestSoFar;

        // Can we cutoff now?
        if ( bestSoFar >= beta )
        {
          TransTable.StoreBoard( theBoard, bestSoFar, jcMove.EVALTYPE_UPPERBOUND, depth, MoveCounter );

          // Add this move's efficiency in the HistoryTable
          HistoryTable.AddCount( theBoard.GetCurrentPlayer(), mov );
          return BestMov;
        }
      }
    }

    // Test for checkmate or stalemate
    if ( bestSoFar <= ALPHABETA_GIVEUP )
    {
      newBoard.Clone( theBoard );
      jcMoveListGenerator secondary = new jcMoveListGenerator();
      newBoard.SwitchSides();
      if ( secondary.ComputeLegalMoves( newBoard ) )
      {
        // Then, we are not in check and may continue our efforts.
    	  HistoryTable.SortMoveList(movegen, theBoard.GetCurrentPlayer());
        movegen.ResetIterator();
        BestMov.MoveType = jcMove.MOVE_STALEMATE;
        BestMov.MovingPiece = jcBoard.KING + theBoard.GetCurrentPlayer();
        while( ( mov = movegen.Next() ) != null )
        {
          newBoard.Clone( theBoard );
          newBoard.ApplyMove( mov );
          if ( secondary.ComputeLegalMoves( newBoard ) )
          {
            BestMov.MoveType = jcMove.MOVE_RESIGN;
          }
        }
      }
      else
      {
        // We're in check and our best hope is GIVEUP or worse, so either we are
        // already checkmated or will be soon, without hope of escape
        BestMov.MovingPiece = jcBoard.KING + theBoard.GetCurrentPlayer();
        BestMov.MoveType = jcMove.MOVE_RESIGN;
      }
    }

    // If we haven't returned yet, we have found an accurate minimax score
    // for a position which is neither a checkmate nor a stalemate
    TransTable.StoreBoard( theBoard, bestSoFar, jcMove.EVALTYPE_ACCURATE, depth, MoveCounter );

    return BestMov;
  }
}