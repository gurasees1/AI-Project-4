import java.util.*;

public class pdgalvinPlayer
{
 
 /**
  * @author pdgalvin
  * 
  * This class is made to play the game Atropos for the class Artificial
  * Intelligence at Boston University. It takes in a string representing the
  * state of the board, and uses an intelligent algorithm to compute an
  * appropriate next move to take.
  */

 // Instance variables
 
 
 /**
  * @param MAX, MIN Max and Min nodes defined by booleans, to make method
  *      calls more clear
  */
 public static final boolean MAX = true;
 public static final boolean MIN = false;
 
 /**
  * @param MAXDEPTH The maximum search depth of the Depth Limited
  *      algorithm
  */
 public static final int MAXDEPTH = 4;
 
 
 
 
 public static void main(String[] args)
 {
  
  // Initialize board using constructor that handles an appropriate String
  State board = new State(args[0]);
  
  Move bestMove = board.getMaxMinMove(MAXDEPTH, MAX);
  
  System.out.print(bestMove);
  
  
  /*
  ArrayList<Move> moves = board.constructChildNodes();
  System.out.println(moves.get(2));
  System.out.println(moves.size());
  
  System.out.println(board.getColor(1, 0));
  System.out.println(board.getColor(2, 2));
  
  Move lastMove = board.getLastMove();
  System.out.println(lastMove.toString());
  */
  
  
 }
 

}