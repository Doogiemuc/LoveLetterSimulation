package org.loveletter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Player with the best strategy I could think of.
 */
public class BestPlayer extends Player {
    /** 
     * Cards that we have seen from <b>other</b> players.
     * May contain null elements for cards of other players that we have not seen yet or do not know anymore!
     * My own cards are not listed!
     */
    List<Card> knownCards;
    
    /** cards that were already played. Including my own played cards. Highest index is played last. */
    List<List<Card>> playedCards;

    @Override
    public void reset(int id, Card firstCard, int numPlayers) {
        super.reset(id, firstCard, numPlayers);
        this.knownCards  = new ArrayList<Card>(numPlayers);
        this.playedCards = new ArrayList<List<Card>>(numPlayers);
        for (int i = 0; i < numPlayers; i++) {
            knownCards.add(null);
            playedCards.add(new ArrayList<Card>());
        }
        
    }
    
    /**
     * Very clever player :-) Read the code for my secret strategies.
     */
    @Override
    public Card chooseCardtoPlay() {
        //  sort cards so that card2 has the higher value
        if (card1.value > card2.value) {
            int val     = card2.value;
            card2.value = card1.value;
            card1.value = val;
            
        }
        
        //1. Always keep princess and countess
        if (card2.value == Card.PRINCESS) return playCard1();
        if (card2.value == Card.COUNTESS) return playCard1();  // card1 is not king or prince. This case has already been handled
        
        //if cards are equal, play any
        if (card1.value == card2.value) playCard2();
        
        //Can I throw out another player because we know his card and we can guess with guard?
        if (hasCardValue(Card.GUARD) && knowAnyCard()) return play(Card.GUARD);
        //Can I throw out another player because he has the princess and we can force him to drop it with a prince?
        if (knownCards.contains(Card.PRINCESS) && hasCardValue(Card.PRINCE)) return play(Card.PRINCE);
        //Can I throw out another player because I know a lower card and have the baron for comparing
        if ()
        
        //Can I throw out another player 50:50 chance?  TODO
        
        //4. Can I get a higher card (late in the game)?
        
        //----- KING
        if (card2.value == Card.KING && card1.value == Card.PRINCE)  {
            // if we know another player that has a princess, then play the prince and let him discard it :-)
            if (knownCards.contains(Card.PRINCESS)) {
                return playCard1();  // play the prince
            }
        }
        if (card2.value == Card.KING && card1.value == Card.MAID)  {
            // if we know the princess then exchange it
            if (knownCards.contains(Card.PRINCESS) || knownCards.contains(Card.COUNTESS)) {
                return playCard2();  // play the prince
            }
            
            //MAYBE: deliberately keep the maid, especially during endgame
            return playCard1();  // playing the maid is always a good idea
        }
        if (card2.value == Card.KING && card1.value == Card.BARON)  {
            // exchange cards if we know a higher one
            
            // compare cards if we know a lower one    //TODO: what first?  Throw someone out or get a higher card?  can throw him out later?
            
            // otherwise play king (no risk to be thrown out)
        }    
        if (card2.value == Card.KING && card1.value == Card.PRIEST)  {
            // exchange cards if we know a higher one
            
            // otherwise look at another player
        }
        if (card2.value == Card.KING && card1.value == Card.GUARD)  {
            // if we know a card for sure, then play the guard and guess correctly
            
            // if we know that someone has the princess or the countess, then exchange
            
            // if we know 50:50 (only two chances) also play the guard and try to guess
            
            // 
        }
        
        if (card2.value == Card.PRINCE && card1.value == Card.MAID)  {
            if (knownCards.contains(Card.PRINCESS)) { 
                return playCard2();  // play the prince
            }
            return playCard1();  // be safe
        }
        if (card2.value == Card.PRINCE && card1.value == Card.BARON)  {
            
        }
        
        //TODO:  ... further strategies
        
        return playCard2();
    }
    
    /** to guess at whom */
    private int guessPlayerId = -1;

    @Override
    public int getPlayerFor(int cardValue, Set<Integer> availablePlayerIds) {
        switch (cardValue) {
        case Card.GUARD:  // guess other player with the highest card that we know of (if any witin availablePlayerIds)
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
        if (guessPlayerId != -1) {
            Card knownCard = knownCards.get(guessPlayerId);
            if (knownCard == null) throw new RuntimeException("tried to guess but found null."); 
            return knownCard.value;
        }
            
        //----- otherwise guess a card value that is still most available 
        int[] numLeft = new int[Card.NumCardsOfValue.length];
        System.arraycopy(Card.NumCardsOfValue, 0, numLeft, 0, Card.NumCardsOfValue.length);
        for (List<Card> playedCardsOfPlayer : this.playedCards) {
            for (Card card : playedCardsOfPlayer) {
                numLeft[card.value]--;
            }
        }
        int maxValue   = 0;    // which value,
        int maxNumLeft = 0;    // has the most number of cards left
        for (int value = 1; value < numLeft.length; value++) {  // value=0 GUARD must not be guessed!
            if (numLeft[value] >= maxNumLeft) {
                maxValue = value;
            }
        }
        return maxValue;
    }

    /** remember cards of other players, when we see them */
    @Override
    public void otherPlayerHasCard(int id, Card card) {
        if (id == this.id) throw new RuntimeException("otherPlayerHasCArd should not have been called with my own id");
        knownCards.set(id, card);
    }
    
    public boolean knowAnyCard() {
        for (Card card : knownCards) {
            if (card != null) return true;
        }
        return false;
    }
    
    private Card play(int value) {
        if (card1.value == value) return playCard1();
        if (card2.value == value) return playCard2();
        throw new RuntimeException("cannot play "+value);
    }
    
    @Override
    public void cardPlayed(int id, Card card) {
        //----- remember all played cards
        if (this.playedCards.get(id) == null) {
            this.playedCards.set(id, new ArrayList<Card>());
        }        
        this.playedCards.get(id).add(card);
        
        //----- If other player has played the card we knew, then we do not know his new card yet.
        if (knownCards.get(id) != null && knownCards.get(id).value == card.value) {
            knownCards.set(id, null);
        }
        
        //TODO: deduce new know cards from played cards (especially in endgame)
    }
    
    @Override
    public String toString() {
        return "Best"+id+"["+card1+"]";
    }
}