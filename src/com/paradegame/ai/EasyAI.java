package com.paradegame.ai;

import com.paradegame.model.*;
import java.util.List;

/**
 * An Easy Version of AI player that chooses cards to play and discard based on basic strategies.
 * 
 * The easy AI selects a card by simulating how many cards would be collected if played and chooses the card 
 * with the third least number of collected cards. If fewer than three cards are available, it selects the second 
 * least or the first card if only one is available.
 * 
 * When discarding, it selects the two cards with the highest values.
 */
public class EasyAI extends AIPlayer {

    /**
     * Constructs a new EasyAI player with the given id and name.
     *
     * @param id   the id of the player
     * @param name the name of the AI player
     */
    public EasyAI(int id, String name) {
        super(id, name);
    }

    /**
     * Chooses a card to play from the AI's hand based on how many cards would be collected.
     * 
     * For each card in hand, this method simulates how many cards the AI would collect if that card were played.
     * It sorts the cards from least to most collected and picks the third option.
     * 
     * If the AI has fewer than three cards, it selects the second card, or the first if only one is available.
     *
     * @param parade the current parade of cards
     * @return the card chosen by the AI to play
     */
    @Override
    public Card chooseCard(Parade parade) {
        List<Card> hand = getHand();
        int[] collectSizes = new int[hand.size()];

        // Step 1: Simulate each card and get size of collected cards
        for (int i = 0; i < hand.size(); i++) {
            collectSizes[i] = parade.simulateCollectedCards(hand.get(i)).size();
        }

        // Step 2: Sort both hand and sizes based on size (ascending)
        for (int i = 0; i < hand.size(); i++) {
            for (int j = i + 1; j < hand.size(); j++) {
                if (collectSizes[i] > collectSizes[j]) {
                    // Swap sizes
                    int tempSize = collectSizes[i];
                    collectSizes[i] = collectSizes[j];
                    collectSizes[j] = tempSize;

                    // Swap corresponding cards in hand
                    Card tempCard = hand.get(i);
                    hand.set(i, hand.get(j));
                    hand.set(j, tempCard);
                }
            }
        }

        // Step 3: Return the card that gives the 3rd least number of collected cards
        if (hand.size() >= 3) {
            return hand.get(2);  // 3rd least
        } else if (hand.size() == 2) {
            return hand.get(1);  // 2nd least
        } else {
            return hand.get(0);  // only one option
        }
    }

    /**
     * Chooses two cards with the highest values to discard from the AI's hand.
     *
     * @param hand      the list of cards in the player's hand
     * @param allPlayers the list of all players in the game
     * @return an array of two integers, each representing the index of a card to discard from the hand
     */
    @Override
    public int[] chooseDiscards(List<Card> hand, List<Player> allPlayers) {
        int first = 0;
        int second = 1;

        if (hand.get(second).getValue() > hand.get(first).getValue()) {
            int temp = first;
            first = second;
            second = temp;
        }

        for (int i = 2; i < hand.size(); i++) {
            int value = hand.get(i).getValue();
            if (value > hand.get(first).getValue()) {
                second = first;
                first = i;
            } else if (value > hand.get(second).getValue()) {
                second = i;
            }
        }

        return new int[]{first, second};
    }
}

