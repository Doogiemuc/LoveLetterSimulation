package org.loveletter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Board with cardstack and players
 * (Java singleton)
 */
public class Board {
    public static final int NUM_CARDS = 16;

    /** 
     * stack of cards, where cards are drawn from. In LoveLetter the last card in the stack
     * is not draw anymore. (It is used in some special situations of the last play.)
     * Element at index 0 is the topmost card!
     */
    public static List<Card>   cardstack; 
    
    /** players at the table */
    public static List<Player> players;
    
    /** index of player who is just his turn */
    int currentPlayerIdx = 0;
    
    /** how many turns have been played */
    int turn = 0;

    private static Board instance = null;
    
    public static Board newBoard(int numPlayers) {
        instance = new Board(numPlayers);
        return instance;
    }
        
    private Board(int numPlayers) {
        //----- setup cardstack
        cardstack = new ArrayList<Card>(Card.Name_EN.length);
        for (int i = 0; i < Card.Name_EN.length; i++) {
            for (int j = 0; j < Card.NumCardsOfValue[i]; j++) {
                Card card = new Card(i+1, Card.Name_EN[i]);
                cardstack.add(card);
            }
        }
        Collections.shuffle(cardstack, new Random(System.currentTimeMillis())); 
        
        //----- setup players and deal cards
        Board.players = new ArrayList<Player>(numPlayers);
        for (int i = 0; i < numPlayers; i++) {
            Card firstCard = cardstack.remove(0);
            Player player = new RandomPlayer(i, firstCard);
            players.add(player);
        }
        this.turn = 0;
    }

    /**
     * let the next player draw a card and play one card
     * @return false when game is finished (only one card left in the stack or only one player left)
     */
    public boolean nextPlayer() {
        if (isGameFinished()) return false;        
        this.turn++;
        
        //----- currentPlayer draws a card 
        Card topCard = cardstack.remove(0);
        Player currentPlayer = players.get(currentPlayerIdx);
        currentPlayer.drawCard(topCard);
        
        Log.traceAppend(turn+": "+currentPlayer.toString()+" draws "+topCard);
        // If player has to play the countess, then do it.
        // Otherwise let the player chooose a card he wants to play
        if (!currentPlayer.mustPlayCountess()) {
            Card chosenCard = currentPlayer.chooseCardtoPlay();
            Log.traceAppend(" plays "+chosenCard);
            handleCard(chosenCard);
        }
        Log.traceFlush();
        
        //----- check if game is finished or advance to next player that is still in the game
        if (isGameFinished()) return false;
        do {
            currentPlayerIdx = (currentPlayerIdx + 1) % getNumPlayers();
        } while (!players.get(currentPlayerIdx).inGame);
  
        return true; 
    }
    
    /**
     * currentPlayer has played card. Handle the consequences for the board.
     * @param currentPlayer
     * @param card
     */
    public void handleCard(Card card) {
        Player currentPlayer = players.get(currentPlayerIdx);
        int    otherId       = -1;
        Player otherPlayer   = null;
        
        if (card.value == Card.GUARD  ||
            card.value == Card.PRIEST ||
            card.value == Card.BARON  ||
            card.value == Card.PRINCE ||
            card.value == Card.KING ) 
        {
            otherId = currentPlayer.getOtherPlayerFor(card.value);
            otherPlayer = players.get(otherId);
            if (otherPlayer.isGuarded()) return;  // if otherPlayer has played countess, then he is safe
        }
        
        switch (card.value) {
        case Card.GUARD: // Gues a card
            int guessedValue = currentPlayer.guessCardValue();  // returns guessed card value of otherPlayer
            Log.traceAppend(" guesses "+guessedValue+" at "+otherPlayer);
            if (otherPlayer.hasCardValue(guessedValue)) {
                Log.traceAppend(" => CORRECT!");
                otherPlayer.setInGame(false);  // card guessed correctly
            }
            break;
            
        case Card.PRIEST: // look at other player's card
            currentPlayer.otherPlayerHasCard(otherId, otherPlayer.card1.value);
            Log.traceAppend(" and sees "+otherPlayer);
            break;
            
        case Card.BARON: // compare card values
            // both players now know each others card
            currentPlayer.otherPlayerHasCard(otherId, otherPlayer.card1.value);
            otherPlayer.otherPlayerHasCard(currentPlayerIdx, currentPlayer.card1.value);
            if (currentPlayer.card1.value > otherPlayer.card1.value) {
                otherPlayer.setInGame(false);
                Log.traceAppend(" and throws out "+otherPlayer);
            } else if (currentPlayer.card1.value < otherPlayer.card1.value) {
                currentPlayer.setInGame(false);
                Log.traceAppend(" looses against "+otherPlayer);
            } else {
              Log.traceAppend(" and both have the same card");
            }
            // When card value is equal, then no one is out.
            break;
            
        case Card.MAID: 
            // current player is safe as long as the maid is open in front of him
            Log.traceAppend(" and is save.");
            break;
            
        case Card.PRINCE:  // other player must discard his card and draw a new one
            Log.traceAppend(": "+otherPlayer);
            Card topCard = cardstack.remove(0);
            otherPlayer.drawCard(topCard);
            otherPlayer.playCard1();  // Discard card, without effect.
            Log.traceAppend(" must discard his card and draws "+topCard);
            break;
            
        case Card.KING:  // change cards with otherPlayer
            Card myCard = currentPlayer.card1;
            currentPlayer.card1 = otherPlayer.card1;
            otherPlayer.card1 = myCard;
            Log.traceAppend(" and exchanges cards with "+otherPlayer);
            break;
            
        case Card.COUNTESS:  
            // Countess must be played if player also has king or prince
            // This case is handled in Player.mustPlayCountess() !!
            Log.traceAppend(" voluntarily.");
            break;
        
        case Card.PRINCESS: // if princess is played, this player is out of the game
            currentPlayer.setInGame(false);
            Log.traceAppend(" must play the princess and is out.");
            break;
        }
        
    }
    
    public static int getNumPlayers() {
        return players.size();
    }
    
    public static int getNumPlayerStillInGame() {
        int num = 0;
        for (Player player : players) {
            if (player.inGame) num++;
        }
        return num;
    }
    
    /**
     * check whether the game is finished
     * @return true when the game is finished
     */
    public boolean isGameFinished() {
        if (cardstack.size() <= 1) return true;  // last card has been drawn (none or one card left in the stack)
        if (getNumPlayerStillInGame() < 2) return true; // one player has won
        return false;
    }
    
    public String printBoardShort() {
        StringBuffer buf = new StringBuffer();
        for (Card card : cardstack) {
            buf.append(card.value);
        }
        buf.append(" ");
        for (Player player : players) {
            buf.append(player.card1.value);
            if (player.card2 != null) {
                buf.append(player.card2.value);
            }
            buf.append("(");
            for (Card card : player.playedCards) {
                buf.append(card.value);
            }
            buf.append(")");
        }
        return buf.toString();
    }
}