package com.paradegame.view;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.paradegame.model.*;

/**
 * The CardDisplayer class is responsible for displaying cards in two formats.
 * A linear format that can be grouped by colour and sorted or shown in original order, and
 * a format for displaying a player's hand with each card indexed.
 */
public class CardDisplayer {
    /**
     * Displays cards in a linear format, either grouped by colour (and sorted by value) 
     * or in original order.
     * 
     * When groupByColour is true, the cards are grouped by their colour and
     * within each group, cards are sorted by value.
     * When false, cards appear in their original order.
     *
     * @param cards          the list of cards to be display
     * @param groupByColour {@code true} for grouped/sorted display, 
     *                      {@code false} for original order
     */
    public void displayCards(List<Card> cards, boolean groupByColour) {
        System.out.print("[ ");

        if (groupByColour) {
            displayGroupedAndSortedCards(cards);
        } else {
            printCardsInLine(cards);
        }

        System.out.println(" ]");
    }

    /**
     * Displays the cards in hand, printing each card with its index.
     * Each card is displayed on a new line.
     *
     * @param cards  the list of cards in the player's hand
     */
    public void displayCardsInHand(List<Card> cards) {
        for (int i = 0; i < cards.size(); i++) {
            Card card = cards.get(i);
            System.out.println(i + ": " + card);
        }
    }

     /**
     * Prints the cards in a single line, separated by commas.
     *
     * @param cards the list of cards to be printed
     */
    private void printCardsInLine(List<Card> cards) {
        for (int i = 0; i < cards.size(); i++) {
            System.out.print(cards.get(i));
            if (i < cards.size() - 1) {
                System.out.print(", ");
            }
        }
    }

    /**
     * Displays the cards grouped by their colour and sorted by value within each group.
     *
     * @param cards the list of cards to be grouped and sorted
     */
    private void displayGroupedAndSortedCards(List<Card> cards) {
        Map<Colour, List<Card>> groupedCards = groupCardsByColour(cards);
        sortCardsInGroups(groupedCards);

        boolean isFirstColourGroup = true;

        for (Colour colour : groupedCards.keySet()) {
            List<Card> cardsInGroup = groupedCards.get(colour);

            // Add a comma between groups (but not before the first card)
            if (!isFirstColourGroup) {
                System.out.print(", ");
            }
            isFirstColourGroup = false;

            printCardsInLine(cardsInGroup);
        }
    }

    /**
     * Groups the cards by their color.
     *
     * @param cards  the list of cards to be grouped
     * @return       a map where the keys are colours, and the values are lists of cards of that color
     */
    private Map<Colour, List<Card>> groupCardsByColour(List<Card> cards) {
        Map<Colour, List<Card>> groupedCards = new TreeMap<>();

        for (Card card : cards) {
            Colour colour = card.getColour();

            // If the color key doesn't exist in the map, create a new empty list
            if (!groupedCards.containsKey(colour)) {
                groupedCards.put(colour, new ArrayList<>());
            }

            groupedCards.get(colour).add(card);
        }

        return groupedCards;
    }
    
    /**
     * Sorts the cards within each colour group by their value in ascending order.
     *
     * @param groupedCards a map where the keys are colours, and the values are lists of cards of that color
     */
    private void sortCardsInGroups(Map<Colour, List<Card>> groupedCards) {
        for (List<Card> cardsInGroup : groupedCards.values()) {
            Collections.sort(cardsInGroup);
        }
    }
}
