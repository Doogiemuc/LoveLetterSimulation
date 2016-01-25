package org.loveletter;

import java.util.ArrayList;
import java.util.List;

/**
 * Simulate Love Letter card game
 *
 */
public class LoveLetterGame {
    public static final int NUM_GAMES = 1;
    public static final int NUM_PLAYERS = 4;
    
    public static void main(String[] args) {
        Log.trace("running ...");

        List<Player> players = new ArrayList<Player>();
        players.add(new HigherCardPlayer());
        players.add(new RandomPlayer());
        players.add(new RandomPlayer());
        players.add(new RandomPlayer());
        
        for (int i = 0; i < NUM_GAMES; i++) {
            Board board = Board.newBoard(players);
            Log.trace(board.getBoardShort());
            while (board.nextPlayer()) {
                Log.trace(board.getBoardShort());
            }
            Log.trace(board.getBoardShort());
            Log.info(i+": "+board.gameStats.toString());
            
        }
    }

}