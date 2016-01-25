package org.loveletter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Player with the best strategy I could think of.
 */
public class BestPlayer extends Player {
    /** 
     * Cards that we have seen from other players.
     * May contain null elements for cards of other players that we have not seen yet or do not know anymore!
     */
    List<Integer> cardValueOfOtherPlayer;
    
    /** cards that were already played. Including my own played cards. Highest index is played last. */
    List<Card> playedCards;

    public BestPlayer() {
        this.cardValueOfOtherPlayer = new ArrayList<Integer>();
        this.playedCards = new ArrayList<Card>();
    }
    
    @Override
    public void reset(int id, Card firstCard) {
        super.reset(id, firstCard);
        this.cardValueOfOtherPlayer = new ArrayList<Integer>();
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
        if (card2.value == Card.PRINCESS) return playCard1();
        if (card2.value == Card.COUNTESS) return playCard1();  // card1 is not king or prince. This case has already been handled
        if (card2.value == Card.KING && card1.value == Card.PRINCE)  {
            // if we know another player that has a princess, then play the prince and let him drop it :-)
            if (cardValueOfOtherPlayer.contains(Card.PRINCESS)) {
                return playCard1();  // play the prince
            }
        }
        if (card2.value == Card.KING && card1.value == Card.MAID)  {
            return playCard1();  // playing the maid is always a good idea
        }
               
        //TODO:  ... further strategies
        
        return playCard2();
    }
    
    private int guessPlayerId = -1;

    @Override
    public int getPlayerFor(int cardValue, Set<Integer> availablePlayerIds) {
        switch (cardValue) {
        case Card.GUARD:  // guess the highest card of other player that we know of (if any)
            int maxValue = 0;
            for (int id = 0; id < cardValueOfOtherPlayer.size(); id++) {
                if (cardValueOfOtherPlayer.get(id) > maxValue) {
                    Log.traceAppend(" of course correctly ");  // guesses ...  :-)
                    maxValue = cardValueOfOtherPlayer.get(id);
                    this.guessPlayerId = id;
                    
                }
            }
            // otherwise return any available other player
            return getRandomPlayerId(availablePlayerIds);
            
        case Card.PRIEST:  // look at another players card
            // choose a player who's card we do not know yet
            for (int i = 0; i < cardValueOfOtherPlayer.size(); i++) {
                if (i != this.id && cardValueOfOtherPlayer.get(i) == null) return i;
            }
            return getRandomPlayerId(availablePlayerIds);
            
        case Card.BARON:
            return 0;
            
        case Card.PRINCE:  // choose the player with the highest card we know of
            
            return 0;
            
        case Card.KING:
            return 0;
            
        default:
            Log.error("Wrong card Value in getOtherPlayerFor: "+cardValue);
            return -1;
        }

    }

    /**
     * If we know the card of another player, then guess it.
     * Otherwise guess a card value that has not yet been played a lot.
     * @return guessed card value of other player
     */
    @Override
    public int guessCardValue() {
        //return the known value if we know it.
        Integer knownId = cardValueOfOtherPlayer.get(guessPlayerId);  // may be null
        if (knownId != null) return knownId.intValue();
        
        //otherwise guess a card value that is still available at lot. 
        int[] numLeft = new int[Card.NumCardsOfValue.length];
        System.arraycopy(Card.NumCardsOfValue, 0, numLeft, 0, Card.NumCardsOfValue.length);
        for (Card card : this.playedCards) {
            numLeft[card.value]--;
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
    public void otherPlayerHasCard(int id, int value) {
        cardValueOfOtherPlayer.set(id, value);
    }

    @Override
    public void cardPlayed(int id, Card card) {
        this.playedCards.add(card);
    }

}
