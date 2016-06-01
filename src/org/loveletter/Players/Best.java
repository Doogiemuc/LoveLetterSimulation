package org.loveletter.Players;

import java.util.Set;

import org.loveletter.Card;
import org.loveletter.Log;
import org.loveletter.Player;

/**
 * Player with the best strategy I could think of.
 */
public class Best extends Player {
    
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
            if (knownCards.values().contains(Card.PRINCESS)) {
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
    
    @Override
    public Player getPlayerFor(int cardValue, Set<Player> availablePlayers) {
        switch (cardValue) {
        case Card.GUARD:  // guess other player with the highest card that we know of (if any within availablePlayerIds)
            Player p = getPlayerWithHighestCard(availablePlayers);
            if (p != null && availablePlayers.contains(p)) {
                Log.traceAppend(" of course correctly");  // ... guesses ...  :-)
                return p;
            }
            // otherwise return any available other player
            return getRandomPlayer(availablePlayers);
            
        case Card.PRIEST:  // look at another players card
            // choose a player who's card we do not know yet
            for (Player lookAtId : availablePlayers) {
                if (knownCards.get(lookAtId) == null) return lookAtId;
            }
            return getRandomPlayer(availablePlayers);
            
        case Card.BARON:   // compare cards
            // If we know someone with just a lower card than ours, then compare with him
            int val = 0;
            Player compareWith = null;
            for (Player player : knownCards.keySet()) {
                Card knownCard = knownCards.get(player);
                if (availablePlayers.contains(player) &&  
                    knownCard != null && knownCard.value > val && knownCard.value < card1.value) 
                {
                    val = knownCard.value;
                    compareWith = player;
                }
            }
            //TODO: it might make sense to compare with player that has the same card value!
            if (compareWith != null) return compareWith;            
            return getRandomPlayer(availablePlayers);
            
        case Card.PRINCE:  // discard card: choose the player with the highest card we know of
            Player discard = getPlayerWithHighestCard(availablePlayers);
            if (discard != null) return discard;
            return getRandomPlayer(availablePlayers);
            
        case Card.KING:    // exchange cards with another player that has a high card (if we know any)
            Player exchangeWith = getPlayerWithHighestCard(availablePlayers);
            if (exchangeWith != null) return exchangeWith;
            return getRandomPlayer(availablePlayers);
            
        default:
            throw new RuntimeException("Unknown card value");
        }
    }

    /**
     * If we know the card of another player, then guess it.
     * Otherwise guess a card value that has not yet been played a lot.
     * @return guessed card value of other player
     */
    @Override
    public int guessCardValue(Player p) {
        //----- if we know a players card, then guess it and throw him out
        Card knownCard = knownCards.get(p);
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

    @Override
    public String toString() {
        StringBuffer buf = new StringBuffer();        
        if (Log.logTRACE) {
        	buf.append("[");
        	buf.append(card1);
        	buf.append(",");
        	for (Card card : knownCards.values()) {
        		buf.append(card.value);
        	}
        	buf.append("]");
        }
        return super.toString() + buf.toString();
    }
}