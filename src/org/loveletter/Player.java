package org.loveletter;

import java.util.Random;
import java.util.Set;

/**
 * Base class for a love letter player.
 */
public abstract  class Player {

    /** position of this player at the table */
    int id;
    
    /** The (first) card that this player has in hand */
    Card card1;
    
    /** the card that was just drawn. Card2 is null when its not this player's turn. */
    Card card2;

    /** is this player still in the game? */
    boolean inGame;
    
    /** is this player currently guarded, because he has played a guard */
    boolean isGuarded;
    
    /** overall number of players in the game (including myself) */
    int numPlayers;
    
    /** random number generator */
    Random rand = new Random();
    
    /** id and firstCard need to be initialized later when using this constructor */
    public Player() {
        this.card2 = null;
        this.inGame = true;
        this.isGuarded = false;
    }
    
    public Player(int id, Card firstCard, int numPlayers) {
        this();
        this.id = id;
        this.card1 = firstCard;
        this.card2 = null;
        this.numPlayers = numPlayers;
    }
    
    /** reset this player for a new game */
    public void reset(int id, Card firstCard, int numPlayers) {
        this.id = id;
        this.card1 = firstCard;
        this.card2 = null;
        this.inGame = true;
        this.isGuarded = false;
        this.numPlayers = numPlayers;
    }
    
    /** is player still in game */
    public void setInGame(boolean inGame) {
        this.inGame = inGame;
    }

    /** drawn card will be set as card2 */
    public void drawCard(Card card) {
        this.card2 = card;
    }

    /** 
     * check if player has a card of this value 
     * @return number of card (1 or 2) if he has it, 0 otherwise
     */
    public int hasCardValue(int val) {
        if (card1.value == val) return 1;
        if (card2 != null && card2.value == val) return 2;
        return 0;
    }

    protected Card playCard1() {
        Card chosenCard = this.card1;
        card1 = card2;
        card2 = null;
        isGuarded = (chosenCard.value == Card.MAID);
        return chosenCard;
    }

    protected Card playCard2() {
        Card chosenCard = this.card2;
        card2 = null;
        isGuarded = (chosenCard.value == Card.MAID);
        return chosenCard;
    }

    /**
     * IF player has countess and also king or prince,
     * THEN he must play the countess.
     * This method will check and also play the countess if necessary.
     * If the player had the countess , then {@link #chooseCardtoPlay()} will not be called anymore.
     * @return the countess if it must be played, otherwise null
     */
    public Card mustPlayCountess() {
        if ( card1.value == Card.COUNTESS && 
            (card2.value == Card.KING || card2.value == Card.PRINCE)) {
            Log.traceAppend(" must play countess.");
            return playCard1();
            }
        if ( card2.value == Card.COUNTESS && 
            (card1.value == Card.KING || card1.value == Card.PRINCE)) {
            Log.traceAppend(" and must play countess.");
            return playCard2();
        }
        return null;
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
    public abstract void otherPlayerHasCard(int id, Card card);

    /**
     * A player has played a card. 
     * Will also be called for own cards.
     * @param id id of the player (can be my own id) 
     * @param card card value that was played
     */
    public abstract void cardPlayed(int id, Card card);

    @Override
    public String toString() {
        return "P"+id+"["+card1+"]";
    }

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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + id;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Player other = (Player) obj;
        if (id != other.id)
            return false;
        return true;
    }

    

}