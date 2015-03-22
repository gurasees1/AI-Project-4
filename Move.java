public class Move
{
 
 /**
  * @author pdgalvin
  * 
  * A Move object is the same as a node in the Minimax algorithm, but it can
  * also be used to store the previous play, so its name Move is meant to
  * reflect either use (a node being a potential future move).
  * 
  * @param color  Color of the node
  * @param x   The distance from the bottom of the board
  * @param y   The distance from the left side of the board
  * @param z   The distance from the right side of the board
  * @param score  Positive if preferred by maximizer, negative for
  *      minimizer, 0 for indifferent
  */

 // Instance variables
 
 private int color;
 private int x;
 private int y;
 private int z;
 private int score;
 
 /**
  * Constructor for a non-node Move, with only color and coordinates
  */
 public Move(int color, int x, int y, int z)
 {
  this.color = color;
  this.x = x;
  this.y = y;
  this.z = z;
 }
 
 /**
  * Constructor for a dummy score-only Move, to be used when no child nodes
  * exist
  */
 public Move(int score)
 {
  this.score = score;
 }
 
 /**
  * Constructor that takes a Move object and constructs a clone
  */
 public Move(Move move)
 {
  this.color = move.getColor();
  this.x = move.getX();
  this.y = move.getY();
  this.z = move.getZ();
  this.score = move.getScore();
 }
 
 
 // Public methods
 
 /**
  * Calculates the score of this Move by calling getMaxMinMove and taking
  * on the best score found. If this is a frontier node (the maximum search
  * depth has been reached), then score is assigned 0 to indicate that not
  * enough information was found to assign a meaningful score.
  */
 public void calcScore(State board, int depthCount, boolean isMax)
 {
  if (depthCount <= 0)
  {
   this.score = 0;
  } else {  // depthCount > 0
   State newBoard = new State(board, this);
   Move bestMove = newBoard.getMaxMinMove(depthCount-1, !isMax);
   this.score = bestMove.getScore();
  }
 }
 
 public int getX()
 {
  return x;
 }
 
 public int getY()
 {
  return y;
 }
 
 public int getZ()
 {
  return z;
 }
 
 public int getColor()
 {
  return color;
 }
 
 public void setColor(int color)
 {
  this.color = color;
 }
 
 public int getScore()
 {
  return score;
 }
 
 public String toString()
 {
  String str = "(" + color + "," + x + "," + y + "," + z + ")";
  return str;
 }
}