package org.loveletter.Players;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.loveletter.Board;
import org.loveletter.Card;
import org.loveletter.Log;
import org.loveletter.Player;

/**
 * Player with the best strategy I could think of.
 */
public class Best extends Player {
    /** 
     * Cards that we have seen from <b>other</b> players.
     * May contain null elements for cards of other players that we have not seen yet or do not know anymore!
     * My own cards are not listed!
     */
    List<Card> knownCards;
    
    @Override
    public void reset(Board board, int id, Card firstCard) {
        super.reset(board, id, firstCard);
        this.knownCards  = new ArrayList<Card>(board.players.size());        
        for (int i = 0; i < board.players.size(); i++) {
            knownCards.add(null);
        }        
    }
    
    /**
     * Very clever player :-) Read the code for my secret strategies.
     */
    @Override
    public Card chooseCardtoPlay() {
        //  sort cards so that card2 has the higher value
        if (card1.value > card2.value) {
            Card cc = card2;
            card2   = card1;
            card1   = cc;            
        }
        
        //Always keep princess and countess if possible
        if (card2.value == Card.PRINCESS) return playCard1();
        if (card2.value == Card.COUNTESS) return playCard1();  // card1 is not king or prince. This case has already been handled
        
        //if cards are equal, play any
        if (card1.value == card2.value) return playCard2();
        
        //----- Throw someone out
        
        //Can I throw out another player because we know his card and we can guess with guard?
        if (hasCardValue(Card.GUARD) > 0 && knowAnyCard()) return playValue(Card.GUARD);
        
        //Can I throw out another player because he has something high (or even the princess) and we can force him to drop it with a prince?
        if (hasCardValue(Card.PRINCE) > 0 && highestKnownValue() >= Card.KING) return playValue(Card.PRINCE);
        
        //Can I throw out another player because I have the Baron and know that he has a lower card than my second card.
        if (card1.value == Card.BARON && smallestKnownValue() < card2.value) return playCard1();
        if (card2.value == Card.BARON && smallestKnownValue() < card1.value) return playCard2();        

        // If know that someone has at least a king or more, then let him drop it.
        
        
        //----- KING
        //If we know a higher card, then play the king and exchange.
        if (card2.value == Card.KING && highestKnownValue() > card1.value) return playCard2();  
        if (card2.value == Card.KING && card1.value == Card.PRINCE)  {
            // if we know another player that has a princess, then play the prince and let him discard it :-)
            if (knownCards.contains(Card.PRINCESS)) {
                return playCard1();  // play the prince
            }
        }
        if (card2.value == Card.KING && card1.value == Card.MAID)  {
            // Exchange does not make sense, casue there is no higher card known. 
            return playCard1();  // So playing the maid is always a good idea
        }
        if (card2.value == Card.KING && card1.value == Card.BARON) return playCard2();      // King, since We do not know a higher card for sure (no risk to be thrown out)
        if (card2.value == Card.KING && card1.value == Card.PRIEST) return playCard1();     // priest: look at another players card
        if (card2.value == Card.KING && card1.value == Card.GUARD) return playCard1();      // guard: try to guess

        //----- PRINCE
        if (card2.value == Card.PRINCE && card1.value == Card.MAID) return playCard1();     // maid: be safe
        if (card2.value == Card.PRINCE && card1.value == Card.BARON) return playCard2();    // prince: Do not compare with baron, but let anyone drop his card
        if (card2.value == Card.PRINCE && card1.value == Card.PRIEST) return playCard1();   // priest: look at another card
        if (card2.value == Card.PRINCE && card1.value == Card.GUARD) return playCard1();    // guard: try to guess
        
        //----- MAID
        if (card2.value == Card.MAID) return playCard2();       // always play the maid
        
        //----- BARON
        if (card2.value == Card.BARON) return playCard1();      // play the other card, cause we do not know anyone to compare to
                
        //----- PRIEST
        if (card2.value == Card.PRIEST) return playCard2();     // priest: look at another players hand
        
        //Guard+guard would the only thing left
        
        return playCard2();
    }
    
    /** to guess at whom */
    private int guessPlayerId = -1;

    @Override
    public int getPlayerFor(int cardValue, Set<Integer> availablePlayerIds) {
        switch (cardValue) {
        case Card.GUARD:  // guess other player with the highest card that we know of (if any within availablePlayerIds)
            guessPlayerId = getPlayerWithHighestCard(availablePlayerIds);
            if (this.guessPlayerId != -1 && availablePlayerIds.contains(guessPlayerId)) {
                Log.traceAppend(" of course correctly");  // ... guesses ...  :-)
                return this.guessPlayerId;
            }
            // otherwise return any available other player
            guessPlayerId = getRandomPlayerId(availablePlayerIds);
            return guessPlayerId;
            
        case Card.PRIEST:  // look at another players card
            // choose a player who's card we do not know yet
            for (Integer lookAtId : availablePlayerIds) {
                if (knownCards.get(lookAtId) == null) return lookAtId;
            }
            return getRandomPlayerId(availablePlayerIds);
            
        case Card.BARON:   // compare cards
            // If we know someone with just a lower card than ours, then compare with him
            int val = 0;
            int compareWith = -1;
            for (int i = 0; i < knownCards.size(); i++) {
                Card knownCard = knownCards.get(i);
                if (availablePlayerIds.contains(i) &&  
                    knownCard != null && knownCard.value > val && knownCard.value < card1.value) 
                {
                    val = knownCard.value;
                    compareWith = i;
                }
            }
            //TODO: it might make sense to compare with player that has the same card value!
            if (compareWith != -1) return compareWith;            
            return getRandomPlayerId(availablePlayerIds);
            
        case Card.PRINCE:  // discard card: choose the player with the highest card we know of
            int discardId = getPlayerWithHighestCard(availablePlayerIds);
            if (discardId != -1) return discardId;
            return getRandomPlayerId(availablePlayerIds);
            
        case Card.KING:    // exchange cards with another player that has a high card (if we know any)
            int exchangeWithId = getPlayerWithHighestCard(availablePlayerIds);
            if (exchangeWithId != -1) return exchangeWithId;
            return getRandomPlayerId(availablePlayerIds);
            
        default:
            Log.error("Wrong card Value in getOtherPlayerFor: "+cardValue);
            return -1;
        }

    }


    /**
     * @param availablePlayerIds list of player IDs to choose from
     * @return id of another player that has the highest card we know of. Or -1 if we do not know any other cards yet.
     */
    private int getPlayerWithHighestCard(Set<Integer> availablePlayerIds) {
        int maxValue = 0; 
        int otherId = -1;
        for (Integer availableId : availablePlayerIds) {                //Collections.max() is not null save! :-(
            Card knownCard = knownCards.get(availableId);
            if (knownCard != null && knownCard.value > maxValue) {
                maxValue = knownCard.value;
                otherId = availableId;
            }
        }
        return otherId;
    }

    /**
     * If we know the card of another player, then guess it.
     * Otherwise guess a card value that has not yet been played a lot.
     * @return guessed card value of other player
     */
    @Override
    public int guessCardValue() {
        //----- if we know a players card, then guess it and throw him out
        Card knownCard = knownCards.get(guessPlayerId);
        if (knownCard != null) { 
        	if (knownCard.value == Card.GUARD)
        		return -1;
            return knownCard.value;
        }
            
        //----- otherwise guess a card value that is still most available 
    	for (int i = Card.PRINCE; i > Card.GUARD; i--) {
    		if (getCardsLeft(i) == 2)
    				return i;
    	}
    	for (int i = Card.PRINCESS; i > Card.GUARD; i--) {
    		if (getCardsLeft(i) == 1)
    				return i;
    	}
    	
    	//It could be the case that only GUARDS are left -> guess nothing in this case.
    	return -1;
    }

    /** remember cards of other players, when we see them */
    @Override
    public void otherPlayerHasCard(int id, Card card) {
        if (id == this.id) throw new RuntimeException("otherPlayerHasCard should not have been called with my own id");
        knownCards.set(id, card);
    }
    
    public boolean knowAnyCard() {
        for (Card card : knownCards) {
            if (card != null) return true;
        }
        return false;
    }
    
    /**
     * @return the smallest value we know of
     */
    public int smallestKnownValue() {
        int smallest = 999;
        for (int i = 0; i < knownCards.size(); i++) {
            if (knownCards.get(i) != null && knownCards.get(i).value < smallest) {
                smallest = knownCards.get(i).value;
            }
        }
        return smallest;
    }
    
    /**
     * @return the highest value we know of
     */
    public int highestKnownValue() {
        int highest = 0;
        for (int i = 0; i < knownCards.size(); i++) {
            if (knownCards.get(i) != null && knownCards.get(i).value > highest) {
                highest = knownCards.get(i).value;
            }
        }
        return highest;
    }
        
    /**
     * remember the played card and check if we still know the other card.
     */
    @Override
    public void cardPlayed(int id, Card card) {
        
        //----- If other player has played the card we knew, then we do not know his new card yet.
        if (knownCards.get(id) != null && knownCards.get(id).value == card.value) {
            knownCards.set(id, null);
        }
        
        //TODO: deduce new know cards from played cards (especially in endgame)
    }
    
    @Override
    public String toString() {
        StringBuffer buf = new StringBuffer();        
        if (Log.logTRACE) {
        	buf.append("[");
        	buf.append(card1);
        	buf.append(",");
        	for (Card card : knownCards) {
        		buf.append(card != null ? card.value : ".");
        	}
        	buf.append("]");
        }
        return super.toString() + buf.toString();
    }
}