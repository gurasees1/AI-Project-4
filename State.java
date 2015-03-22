import java.util.*;
import java.lang.*;

public class State
{
 
 /**
  * @author pdgalvin
  * 
  * A State object holds the state of the board, or the state of a
  * hypothetical future board that can be used for the Minimax algorithm.
  * 
  * @param board The first index is height, the second index is distance
  *     from the left edge, each ranging from 0 (outside of
  *     playable area) to the size of the board + 1. The
  *     integer value stored is the color of the circle,
  *     ranging 0 to 3.
  * 
  * @param lastMove A circle representing the last move played by opponent.
  * 
  * 
  */
 
 // Instance variables
 
 private int[][] board;
 private Move lastMove;
 private int size;
 
 /**
  * The colors defined as values
  */
 public static final int UNCOLORED = 0;
 public static final int RED = 1;
 public static final int BLUE = 2;
 public static final int GREEN = 3;
 
 /**
  * Possible scores defined as words
  * 
  * @param FREEMOVE This Minimax algorithm detects if no neighboring
  *      positions are open, and rewards the max/minimizer if
  *      either can get the "free" move
  */
 public static final int FREEMOVE = 5;
 public static final int LOSE = -10;
 
 /**
  * Other
  * 
  * @param ENDGAMETACTIC  Once this many total uncolored circles remain
  *        on the playable board, return FREEMOVE rather
  *        than examining the possible moves
  * 
  * @param MAXDEPTH   The maximum search depth of the Depth Limited
  *        algorithm
  */
 public static final int ENDGAMETACTIC = 6;
 public static final int MAXDEPTH = 4;
   
   
 //Public methods
   
 /**
  * Class constructors
  */
 public State(String input)
 {
  String[] rows = input.split("]");
  this.size = rows.length - 3;
  board = new int[size+2][size+2];
  
  for(int i = 0; i < rows.length-1; i++)
  {
   String row = rows[i];
   row = row.substring(1, row.length());
   for(int j = 0; j < row.length(); j++)
   {
    if(i != rows.length-2)
    {
     board[size+1-i][j] = Integer.parseInt(row.substring(j, j+1));
    } else {
     board[size+1-i][j+1] = Integer.parseInt(row.substring(j, j+1));
    }
   }
  }
  
  String move = rows[rows.length-1];
  if(move.indexOf('(') >= 0)
  {
   move = move.substring(move.indexOf('('));
   move = move.substring(1);
   move = move.substring(0, move.length()-1);
   String[] partsString = move.split(",");
   int[] parts = new int[4];
   for (int i = 0; i < 4; i++)
   {
    parts[i] = Integer.parseInt(partsString[i]);
   }
   lastMove = new Move(parts[0], parts[1], parts[2], parts[3]);
  } else {
   lastMove = null;
  }
 }
 
 /**
  * Constructor for a hypothetical State, after a move has been made
  */
 public State(State prevState, Move newMove)
 {
  this.board = prevState.getBoard();
  this.size = prevState.getSize();
  if(board[newMove.getX()][newMove.getY()] == 0)
  {
   board[newMove.getX()][newMove.getY()] = newMove.getColor();
  } else {
   //throw new RuntimeException("Move attempted at colored circle");
  }
  this.lastMove = newMove;
 }
 
 
 
 /**
  * Accessor methods
  */
 public int getColor(int i, int j)
 {
  return board[i][j];
 }
 
 public int[][] getBoard()
 {
  return board;
 }
 
 public Move getLastMove()
 {
  return lastMove;
 }
 
 public int getSize()
 {
  return size;
 }
 
 
 /**
  * Methods for the Minimax algorithm
  */
 
 /**
  * For the given State, return the Move with the best or the worst score,
  * depending on if it is the Maximizer's or Minimizer's turn
  */
 public Move getMaxMinMove(int depthCount, boolean isMax)
 {
  // If it's the first move
  if(lastMove == null)
  {
   return new Move(1,1,1,size);
  }
  
  ArrayList<Move> childNodes = constructChildNodes();
  
  // If no neighbor circle is uncolored
  if(childNodes == null)
  {
   // If the remaining uncolored circles on the board is too high,
   // return a dummy Move with only a score to save computation time
   //
   // If this is the original getMaxMinMove call, a dummy Move should
   // NOT be returned, so depthCount must be less than the original
   if(countFreeCircles() > ENDGAMETACTIC && depthCount < MAXDEPTH)
   {
    if(isMax)  // If this depth is a maximizer, then the maximizer will
        // receive the free move, and thus the score is positive
    {
     return new Move(FREEMOVE);
    } else {  // If this depth is minimizer, score is negative
     return new Move(-1*FREEMOVE);
    }
    
   } else {    // If few enough free circles are left, consider
        // the possible "free" moves, and responses
    childNodes = constructFreeMoves();
    
    // If this is the original method call, set a low depthCount so
    // that the computation time doesn't explode from trying tons
    // of Moves
    if(countFreeCircles() > ENDGAMETACTIC)
    {
     depthCount = 2;
    }
   }
  }
  
  // If all moves are losing moves, return a dummy Move with only a score
  // that is positive for the Minimizer and negative for the Maximizer, so
  // that it will be avoided
  //
  // If this is the original getMaxMinMove call, a dummy Move should
  // NOT be returned, so depthCount must be less than the original
  if(childNodes.isEmpty())
  {
   if(depthCount < MAXDEPTH)
   {
    if(isMax)
    {
     return new Move(LOSE); // LOSE is negative
    } else {
     return new Move(-1*LOSE);
    }
   } else {                             // depthCount == MAXDEPTH
    Move losingMove = constructLegalMove();
    return losingMove;
   }
  }
  
  // Look at each allowed Move, get their scores, and choose the max or
  // the min value
  Move bestNode = new Move(100); // Minimizer will never choose 100
  if(isMax)
  {
   bestNode = new Move(-100); // Maximizer will never choose -100
  }
  for(Move node : childNodes)
  {
   node.calcScore(this, depthCount, isMax);
   if(isMax)
   {
    if(node.getScore() > bestNode.getScore())
    {
     bestNode = new Move(node);
    }
   } else {
    if(node.getScore() < bestNode.getScore())
    {
     bestNode = new Move(node);
    }
   }
   
   // To randomize moves in the case that no move is preferred, each
   // considered move has a 10% probability of overriding the previous
   // move.
   if(node.getScore() == bestNode.getScore())
   {
    if(Math.random() < 0.1)
    {
     bestNode = new Move(node);
    }
   }
  }
  
  return bestNode;
 }
 
 /**
  * Returns the potential Moves a player could make that would not
  * immediately lose
  */
 public ArrayList<Move> constructChildNodes()
 {
  Move[] neighborCircles = new Move[6];
  int x = lastMove.getX();
  int y = lastMove.getY();
  int z = lastMove.getZ();
  
  // neighborCircles contains the circles neighboring to the last move,
  // starting with the circle to the upper right and rotating clockwise.
  neighborCircles[0] = new Move(board[x+1][y], x+1, y, z-1);
  neighborCircles[1] = new Move(board[x][y+1], x, y+1, z-1);
  neighborCircles[2] = new Move(board[x-1][y+1], x-1, y+1, z);
  neighborCircles[3] = new Move(board[x-1][y], x-1, y, z+1);
  neighborCircles[4] = new Move(board[x][y-1], x, y-1, z+1);
  neighborCircles[5] = new Move(board[x+1][y-1], x+1, y-1, z);
  
  // Declare variables outside of for loop to prevent repetitive
  // declaration.
  int[] colorsSurrounding = new int[7];
  boolean[] badColors;
  int color1;
  int color2;
  ArrayList<Move> allowedMoves = new ArrayList<Move>();
  Move allowedMove;
  boolean aNeighborIsFree = false;  
  
  for(Move neighbor : neighborCircles)
  {
   if(neighbor.getColor() == 0)
   {
    aNeighborIsFree = true;
    
    x = neighbor.getX();
    y = neighbor.getY();
    z = neighbor.getZ();
    
    // colorsSurrounding is an int[] of the colors around neighbor,
    // in the same rotation order as neighborCircles.
    // The first color is added again at the end to allow a
    // comparison of the last and first colors.
    colorsSurrounding[0] = board[x+1][y];
    colorsSurrounding[1] = board[x][y+1];
    colorsSurrounding[2] = board[x-1][y+1];
    colorsSurrounding[3] = board[x-1][y];
    colorsSurrounding[4] = board[x][y-1];
    colorsSurrounding[5] = board[x+1][y-1];
    colorsSurrounding[6] = colorsSurrounding[0];
    
    // badColors has a boolean element for each color, 1 2 or 3.
    // An element is set to true if setting that color in the
    // position specified by neighbor would be a losing move.
    // Booleans initialize to false. 
    badColors = new boolean[3];
    
    for(int i = 0; i < 6; i++)
    {
     color1 = colorsSurrounding[i];
     color2 = colorsSurrounding[i+1];
     if(color1 != 0 && color2 != 0 && color1 != color2)
     {
      badColors[6-color1-color2-1] = true;
     }
    }
    
    for(int i = 0; i < 3; i++)
    {
     if(!badColors[i])
     {
      allowedMove = new Move(i+1, x, y, z);
      allowedMoves.add(allowedMove);
     }
    }
   }
  }
  
  // Catch the case when no neighbor circle is uncolored
  if(!aNeighborIsFree)
  {
   return null;
  }
  
  return allowedMoves;
 }
 
 /**
  * Returns the number of uncolored circles on the playable board
  */
 public int countFreeCircles()
 {
  int numFreeCircles = 0;
  for(int i = size; i > 0; i--)
  {
   for(int j = 1; j < size-i+2; j++)
   {
    if(board[i][j] == 0)
    {
     numFreeCircles++;
    }
   }
  }
  
  return numFreeCircles;
 }
 
 /**
  * Returns an ArrayList of the possible moves which will not lose, for when
  * a player has a "free" move
  */
 public ArrayList<Move> constructFreeMoves()
 {
  // Declare variables outside of for loop to prevent repetitive
  // declaration.
  int[] colorsSurrounding = new int[7];
  boolean[] badColors;
  int color1;
  int color2;
  Move freeCircle;
  ArrayList<Move> freeCircles = new ArrayList<Move>();
  
  for(int i = size; i > 0; i--)
  {
   for(int j = 1; j < size-i+2; j++)
   {
    if(board[i][j] == 0)
    {
     int x = i;
     int y = j;
     int z = size+2-i-j;
     
     // colorsSurrounding is an int[] of the colors around neighbor,
     // in the same rotation order as neighborCircles.
     // The first color is added again at the end to allow a
     // comparison of the last and first colors.
     colorsSurrounding[0] = board[x+1][y];
     colorsSurrounding[1] = board[x][y+1];
     colorsSurrounding[2] = board[x-1][y+1];
     colorsSurrounding[3] = board[x-1][y];
     colorsSurrounding[4] = board[x][y-1];
     colorsSurrounding[5] = board[x+1][y-1];
     colorsSurrounding[6] = colorsSurrounding[0];
     
     // badColors has a boolean element for each color, 1 2 or 3.
     // An element is set to true if setting that color in the
     // position specified by neighbor would be a losing move.
     // Booleans initialize to false. 
     badColors = new boolean[3];
     
     for(int k = 0; k < 6; k++)
     {
      color1 = colorsSurrounding[k];
      color2 = colorsSurrounding[k+1];
      if(color1 != 0 && color2 != 0 && color1 != color2)
      {
       badColors[6-color1-color2-1] = true;
      }
     }
     
     for(int k = 0; k < 3; k++)
     {
      if(!badColors[k])
      {
       freeCircle = new Move(k+1, x, y, z);
       freeCircles.add(freeCircle);
      }
     }
    }
   }
  }
  
  return freeCircles;
 }
 
 /**
  * Returns some legal move, i.e. any move that is adjacent to lastMove
  * and uncolored. This method is only used when defeat is unavoidable.
  */
 public Move constructLegalMove()
 {
  Move[] neighborCircles = new Move[6];
  int x = lastMove.getX();
  int y = lastMove.getY();
  int z = lastMove.getZ();
  
  // neighborCircles contains the circles neighboring to the last move,
  // starting with the circle to the upper right and rotating clockwise.
  neighborCircles[0] = new Move(board[x+1][y], x+1, y, z-1);
  neighborCircles[1] = new Move(board[x][y+1], x, y+1, z-1);
  neighborCircles[2] = new Move(board[x-1][y+1], x-1, y+1, z);
  neighborCircles[3] = new Move(board[x-1][y], x-1, y, z+1);
  neighborCircles[4] = new Move(board[x][y-1], x, y-1, z+1);
  neighborCircles[5] = new Move(board[x+1][y-1], x+1, y-1, z);
  
  for(Move neighbor : neighborCircles)
  {
   if(neighbor.getColor() == 0)
   {
    neighbor.setColor(1);
    return neighbor;
   }
  }
  
  // If there are no uncolored neighboring circles, search for an uncolored
  // circle anywhere on the board
  for(int i = size; i > 0; i--)
  {
   for(int j = 1; j < size-i+2; j++)
   {
    if(board[i][j] == 0)
    {
     return new Move(1, i, j, size+2-i-j);
    }
   }
  }
  
  return null; // This line should not be reached, it is for the compiler
 }
 
 /**
  * For testing
  */
 
 public static void main(String[] args)
 {
  ArrayList<Move> moves = new ArrayList<Move>();
  System.out.println(moves);
  moves = null;
 }
}