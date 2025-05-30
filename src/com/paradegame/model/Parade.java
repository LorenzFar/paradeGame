package com.paradegame.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the parade in the Parade game.
 * Players add a card to the end of the parade each turn.
 * Cards in the parade might be collected based on game rules when a new card is added.
 */
public class Parade {
    private List<Card> cards;

    /**
     * Constructs a new empty Parade.
     */
    public Parade() {
        this.cards = new ArrayList<>();
    }

     /**
     * Adds a card to the end of the parade.
     *
     * @param card the card to be added
     */
    public void addCard(Card card) {
        cards.add(card);
    }

    /**
     * Determines which cards must be removed from the parade when a card is played.
     * This modifies the parade by removing certain cards that 
     * share the same colour as the played card or have a value less than or equal to it.
     *
     * @param playedCard the card being played
     * @return the list of cards that are removed from the parade as a result of playing this card
     */
    public List<Card> handleCardPlayed(Card playedCard) {
        List<Card> originalParadeCards = cards;
        int originalSize = cards.size();
        int playedValue = playedCard.getValue();

        List<Card> remaining = new ArrayList<>();
        List<Card> removed = new ArrayList<>();

        if (originalSize > playedValue || (playedValue == 0 && originalSize > 0)) {
            for (int pos = 0; pos < originalSize; pos++) {
                if (pos < originalSize - 1 - playedValue) {
                    Card candidate = originalParadeCards.get(pos);
                    if (candidate.getColour() == playedCard.getColour()
                            || candidate.getValue() <= playedCard.getValue()) {
                        removed.add(candidate);
                    } else {
                        remaining.add(candidate);
                    }
                } else {
                    remaining.add(originalParadeCards.get(pos));
                }
            }
        } else {
            remaining.addAll(originalParadeCards);
        }

        // Recreate parade
        cards.clear();
        cards.addAll(remaining);

        return removed;
    }

    /**
     * Simulates playing a card in the parade without modifying the original parade.
     * Creates a temporary parade to determine which cards would be collected.
     *
     * @param playedCard the card being played
     * @return the list of cards that would be collected if the card were played
     */
    public List<Card> simulateCollectedCards(Card playedCard) {
        Parade tempParade = new Parade();
        tempParade.getCards().addAll(this.getCards()); // Copy the parade state
        tempParade.addCard(playedCard);                // Simulate the played card
        return tempParade.handleCardPlayed(playedCard); // Simulate the collection logic
    }

    /**
     * Gets the number of cards currently in the parade.
     *
     * @return the number of cards in the parade
     */
    public int size() {
        return cards.size();
    }

    /**
     * Gets the list of cards currently in the parade.
     *
     * @return the list of cards in the parade
     */
    public List<Card> getCards() {
        return cards;
    }

    /**
     * Returns a string representation of the parade.
     *
     * @return a string representation of the cards in the parade
     */
    @Override
    public String toString() {
        return cards.toString();
    }
}