package com.paradegame.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.paradegame.util.Config;

/**
 * Represents a deck of cards used in the Parade game.
 * The deck consists of cards of different colours and values,
 * which are shuffled upon initialisation.
 */
public class Deck {
    private final List<Card> cards;
    private int index;

    /**
     * Constructs a new deck of cards.
     * The number of cards per colour is retrieved from the configuration.
     * The deck is shuffled after creation.
     */
    public Deck() {
        cards = new ArrayList<>();
        int cardsPerColor = Config.getInt("cardsPerColor", 11);

        for (Colour colour : Colour.values()) {
            for (int value = 0; value < cardsPerColor; value++) {
                cards.add(new Card(colour, value));
            }
        }

        Collections.shuffle(cards);
        index = 0;
    }

    /**
     * Draws the next card from the deck.
     *
     * @return The next card in the deck, or {@code null} if the deck is empty.
     */
    public Card draw() {
        if (index >= cards.size()){
            return null;
        }
        return cards.get(index++);
    }

    /**
     * Checks if the deck is empty.
     *
     * @return {@code true} if there are no more cards left to draw, {@code false} otherwise.
     */
    public boolean isEmpty() {
        return index >= cards.size();
    }

    /**
     * Gets the current index of the deck (number of drawn cards).
     *
     * @return The current draw index.
     */
    public int getIndex() {
        return index;
    }

     /**
     * Gets the number of remaining cards in the deck.
     *
     * @return The number of cards left in the deck.
     */
    public int size() {
        return cards.size() - index;
    }
}
