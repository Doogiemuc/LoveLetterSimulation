package org.loveletter;

import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * Base class for a love letter player.
 */
public abstract  class Player {

    /** Board this player is currently playing on */
    public Board board;
    
    /** position of this player at the table */
    public int id;    
    
    /** The (first) card that this player has in hand */
    public Card card1;
    
    /** the card that was just drawn. Card2 is null when its not this player's turn. */
    public Card card2;

    /** is this player still in the game? */
    public boolean inGame;
    
    /** is this player currently guarded, because he has played a guard */
    public boolean isGuarded;
        
    /** random number generator */
    public Random rand = new Random();
    
    /** Initialize player, need to call reset before play can happen */
    public Player() {
    }
        
    /** reset this player for a new game 
     * @param board TODO*/
    public void reset(Board board, int id, Card firstCard) {
    	this.board = board;
        this.id = id;
        this.card1 = firstCard;
        this.card2 = null;
        this.inGame = true;
        this.isGuarded = false;
    }
    
    /** is player still in game */
    public void setInGame(boolean inGame) {
        this.inGame = inGame;
    }
    
    /** drawn card will be set as card2 */
    public void drawCard(Card card) {
    	if (card1 == null)
    		card1 = card;
    	else 
    		card2 = card;
    }

    /** 
     * check if player has a card of this value 
     * @return number of card (1 or 2) if he has it, 0 otherwise
     */
    public int hasCardValue(int val) {
        if (card1 != null && card1.value == val) return 1;
        if (card2 != null && card2.value == val) return 2;
        return 0;
    }

    protected Card playCard1() {
        Card chosenCard = this.card1;
        card1 = card2;
        card2 = null;
        return chosenCard;
    }

    protected Card playCard2() {
        Card chosenCard = this.card2;
        card2 = null;
        return chosenCard;
    }
    
    //----- implement these methods in sublcasses! -----------
    
    /**
     * Which card do you want to play?
     * @return either card1 or card2
     */
    public abstract Card chooseCardtoPlay();

    
    /**
     * Get id of player for cards where current player needs to choose a player. Normaly another player is chosen,
     * but for <b>prince</b> the player may choose himself.
     * @param cardValue either guard, priest, baron, prince or king
     * @param availablePlayers list of players that can be chosen from, ie. players that are still in the game
     *        and that are not currently protected. For prince, this list contains the player himself. 
     *        This list may be empty!
     * @return the chosen player or -1 if list was empty
     */
    public abstract int getPlayerFor(int cardValue, Set<Integer> availablePlayerIds);
  /*  implement like this:
          
        switch (cardValue) {
        case Card.GUARD:
            break;
        case Card.PRIEST:
            break;
        case Card.BARON:
            break;
        case Card.PRINCE:
            break;
        case Card.KING:
            break;
        default:
            Log.error("Wrong card Value in getOtherPlayerFor: "+cardValue);
            break;
        }
  
    */
    
    /**
     * Guard: guess another player's card
     * This method will be called after player has chosen to play a guard.
     * @return value to guess (2-8). Guessing a Guard(1) is not allowed
     */
    public abstract int guessCardValue();
    
    
    
    /**
     * Let this player know the card of another player.
     * Will only be called with other player's id.
     * @param id id of another player
     * @param card card of this other player
     */
    public void otherPlayerHasCard(int id, Card card) {};

    /**
     * A player has played a card. 
     * Will also be called for own cards.
     * @param id id of the player (can be my own id) 
     * @param card card value that was played
     */
    public void cardPlayed(int id, Card card) {};

    @Override
    public String toString() {
    	String details = "";
    	if (Log.logTRACE) {
    		if (card1 != null) details += "["+card1+"]";
    	}
        return this.getClass().getSimpleName() + details;
    }

    
    // Helper Methods
    
    /**
     * get a random element from the set. This helper method can be used by Player subclasses if no other information is available
     * for choosing a player.
     * @param availablePlayerIds set of ids to choose from (may contain own id)
     * @return the chosen id or -1 if availablePlayerIds was empty
     */
    public int getRandomPlayerId(Set<Integer> availablePlayerIds) {
        int countTo = rand.nextInt(availablePlayerIds.size()); 
        int counter = 0;
        for(Integer id : availablePlayerIds)
        {
            if (counter == countTo) return id;
            counter++;
        }
        throw new RuntimeException("Not able to find random player.");
    }
    
    /**
     * get the unseen number of cards of the given type
     * @param the card to be counted
     * @return the number of cards of this type not seen
     */
    public int getCardsLeft(int card) {    	
    	int left = Card.NumCardsOfValue[card]; 
    	if (card1.value == card)
    		left--;
    	if (card2 != null && card2.value == card)
    		left--;
    	for (List<Card> l : board.playedCards)
    		for (Card c : l)
    			if (c.value == card)
    				left--;
    	return left;
    }
    
    public Card playValue(int value) {
        if (card1.value == value) return playCard1();
        if (card2.value == value) return playCard2();
        throw new RuntimeException("cannot playValue"+value);
    }
  
}