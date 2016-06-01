package org.loveletter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Board with cardstack and players
 * (Java singleton)
 */
public class Board {

    /** 
     * Sorted stack of cards, where cards are drawn from. In LoveLetter the last card in the stack
     * is not draw anymore. (It is used in some special situations of the last play.)
     * Element at index 0 is the topmost card that will be drawn next!
     */
    public List<Card> cardstack;
    
    /** backup of cardStack at start of game */
    private List<Card> initialStack = null;
    
    /** players at the table */
    public List<Player> players;
    
    /** keep track of already played cards for each player */
    public Map<Player, List<Card>> playedCards;
    
    /** index of player who is just his turn */
    int currentPlayerId = 0;
    
    /** how many turns have been played */
    int turn = 0;

    /** statistics about this game. Will be filled, when game is finished */
    GameStats gameStats = null;    
    

    /** 
     * start a new game
     * @param players the competing players at the table 
     */
    public Board(List<Player> initPlayers) {
        this.playedCards  = new HashMap<Player, List<Card>>();
       
        //----- setup cardstack and shuffle
        cardstack = new ArrayList<Card>();
        for (int i = Card.GUARD; i <= Card.PRINCESS; i++) {
            for (int j = 0; j < Card.NumCardsOfValue[i]; j++) {
                Card card = new Card(i);
                cardstack.add(card);
            }
        }
        Collections.shuffle(cardstack); 
        initialStack = new ArrayList<Card>(cardstack);
        
        //----- seat players and deal cards to players
        players = new ArrayList<Player>(initPlayers);  // make copy
        for (int i = 0; i < players.size(); i++) {
            Card firstCard = cardstack.remove(0);
            players.get(i).reset(this, i, firstCard);
            playedCards.put(players.get(i), new ArrayList<Card>());
        }
    }

    /**
     * let the next player draw a card and play one of his two cards
     * @return false when game is finished (only one card left in the stack or only one player left)
     */
    public boolean nextPlayer() {
        assert(!isGameFinished());        
        turn++;
        
        //----- currentPlayer draws a card 
        Card topCard = cardstack.remove(0);
        Player currentPlayer = players.get(currentPlayerId);
        currentPlayer.drawCard(topCard);
        
        Log.traceAppend(currentPlayer.toString()+" draws "+topCard);
        
        //----- If player has to play the countess, then do it.
        Card chosenCard = null;
        if ( currentPlayer.card1.value == Card.COUNTESS && 
                (currentPlayer.card2.value == Card.KING || currentPlayer.card2.value == Card.PRINCE)) {
                Log.traceAppend(" must play countess.");
                chosenCard = currentPlayer.playCard1();
                }
        else if ( currentPlayer.card2.value == Card.COUNTESS && 
                (currentPlayer.card1.value == Card.KING || currentPlayer.card1.value == Card.PRINCE)) {
                Log.traceAppend(" and must play countess.");
                chosenCard = currentPlayer.playCard2();
        }

        //----- otherwise let the player choose a card he wants to play
        if (chosenCard == null) {
            chosenCard = currentPlayer.chooseCardtoPlay();
            if (chosenCard.equals(topCard)) {
                Log.traceAppend(" plays it");
            } else {
                Log.traceAppend(" plays "+chosenCard);
            }
            handleCard(chosenCard);
        }
        this.playedCards.get(currentPlayer).add(chosenCard);
        
        //----- inform all players about the played card
        for (Player p : players) {
            p.cardPlayed(currentPlayer, chosenCard);
        }
        Log.traceFlush();
        
        //----- check if game is finished
        if (isGameFinished()) {
            this.gameStats = new GameStats(initialStack, players, turn, playedCards);            
            return false;
        }
        
        //----- advance to next player that is still in the game
        do {
            currentPlayerId = (currentPlayerId + 1) % players.size();
        } while (!players.get(currentPlayerId).inGame);
  
        return true; 
    }
    
    /**
     * currentPlayer has played card. Handle the consequences for the board.
     * @param currentPlayer
     * @param card
     */
    public void handleCard(Card card) {
        Player currentPlayer = players.get(currentPlayerId);
        Player otherPlayer   = null;
        
        // Remove old MAID effect
        currentPlayer.isGuarded = false;
        
        //----- choose otherPlayer for cards where that is necessary
        if (card.value == Card.GUARD  ||
            card.value == Card.PRIEST ||
            card.value == Card.BARON  ||
            card.value == Card.PRINCE ||   // for prince player may choose himself to discard his own card 
            card.value == Card.KING ) 
        {
            // create Set of available player IDs to choose from
            Set<Player> availablePlayers = new HashSet<Player>();
            for (Player player : players) {
                if (player.inGame &&                        // other player must still be in the game 
                    !player.isGuarded &&                    // and must not be guarded
                    (card.value == Card.PRINCE || player != currentPlayer)  )   // and must not choose himself, unless for the prince (discarding own card is allowed) 
                {
                    availablePlayers.add(player);
                }
            }
            // when there is no other player to choose from, then discard players card
            if (availablePlayers.isEmpty()) {
                Log.traceAppend(" without effect.");
                return;
            }
            // let Player decide whom to choose
            otherPlayer= currentPlayer.getPlayerFor(card.value, availablePlayers);
            assert(availablePlayers.contains(otherPlayer));
        }
        
        switch (card.value) {
        case Card.GUARD: // Try to guess  card of other player
            int guessedValue = currentPlayer.guessCardValue(otherPlayer);
            assert(guessedValue != Card.GUARD);
            Log.traceAppend(" guesses "+guessedValue+" at "+otherPlayer);
            if (otherPlayer.hasCardValue(guessedValue) > 0) {
                Log.traceAppend(" => CORRECT!");
                otherPlayer.setInGame(false);  // card guessed correctly
            }
            break;
            
        case Card.PRIEST: // look at other player's card
            currentPlayer.otherPlayerHasCard(otherPlayer, otherPlayer.card1);
            Log.traceAppend(" and sees "+otherPlayer);
            break;
            
        case Card.BARON: // compare card values
            // both players now know each others card
            currentPlayer.otherPlayerHasCard(otherPlayer, otherPlayer.card1);
            otherPlayer.otherPlayerHasCard(currentPlayer, currentPlayer.card1);
            if (currentPlayer.card1.value > otherPlayer.card1.value) {
                otherPlayer.setInGame(false);
                Log.traceAppend(" and throws out "+otherPlayer+ " in comparison");
            } else if (currentPlayer.card1.value < otherPlayer.card1.value) {
                currentPlayer.setInGame(false);
                Log.traceAppend(" looses against "+otherPlayer + " in comparison");
            } else {
              Log.traceAppend(" and has the same card as "+otherPlayer);
            }
            // When card value is equal, then no one is out.
            break;
            
        case Card.MAID: 
            // current player is safe as long as the maid is open in front of him
        	currentPlayer.isGuarded = true;
            Log.traceAppend(" and is save.");
            break;
            
        case Card.PRINCE:  // discard card and draw a new one (player may have chosen other player or himself) 
            Log.traceAppend(": "+otherPlayer);
            Card discarded = otherPlayer.playCard1();  // Discard card, without effect on other players
            playedCards.get(otherPlayer).add(discarded);
            if (discarded.value == Card.PRINCESS) {
                otherPlayer.setInGame(false);
                Log.traceAppend(" has to discard the princess and is OUT.");
            } else {
            	Card topCard = cardstack.remove(0);
                otherPlayer.drawCard(topCard);
                Log.traceAppend(" discards his card and draws "+topCard);
            }
            break;
            
        case Card.KING:  // change cards with otherPlayer
            Log.traceAppend(" and exchanges cards with "+otherPlayer);            
            Card myCard = currentPlayer.card1;
            currentPlayer.card1 = otherPlayer.card1;
            otherPlayer.card1 = myCard;
            currentPlayer.otherPlayerHasCard(otherPlayer, otherPlayer.card1);
            otherPlayer.otherPlayerHasCard(currentPlayer, currentPlayer.card1);
            break;
            
        case Card.COUNTESS:  
            // Countess must be played if player also has king or prince
            // This case is handled already when selecting the card to play
            Log.traceAppend(" voluntarily.");
            break;
        
        case Card.PRINCESS: // if princess is played, this player is out of the game
            currentPlayer.setInGame(false);
            Log.traceAppend(" must play the princess and is out.");
            break;
        }
        
    }

    /**
     * Number of players that are not out yet.
     * @return number of players that are still in the game
     */
    public int getNumPlayerStillInGame() {
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
        if (cardstack.size() <= 1) return true;         // last card has been drawn (none or one card left in the stack)
        if (getNumPlayerStillInGame() < 2) return true; // one player has won
        return false;
    }
    
    /**
     * return a short one line representation of the current state of the game.
     * @return a string
     */
    public String getBoardShort() {
        StringBuffer buf = new StringBuffer();
        buf.append(Log.padLeftaligned(turn+":", 5));
        for (Card card : cardstack) {
            buf.append(card.value);
        }
        buf.append(" ");
        for (Player player : players) {
            buf.append("(");
            buf.append(player.inGame ? " " : "X");
            if (player.card1 != null)
            	buf.append(player.card1.value);
            if (player.card2 != null)
                buf.append(player.card2.value);
            buf.append(")");
            for (Card card : this.playedCards.get(player))
                buf.append(card.value);
            buf.append(" ");
        }
        return buf.toString();
    }
}