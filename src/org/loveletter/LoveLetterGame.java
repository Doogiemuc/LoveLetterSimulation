package org.loveletter;

public class LoveLetterGame {

  public static void main(String[] args) {
      Board board = Board.newBoard(4);
      Log.trace("running ...");
      while (board.nextPlayer()) {
          Log.trace(board.printBoardShort());
      }
      
      Log.trace("done.");      
  }
    
  
}