package com.paradegame.ai;

import com.paradegame.model.*;
import java.util.*;

/**
 * A Medium Version of AI player that selects cards to play and discard with a moderately strategic approach.
 * 
 * The AI selects cards to play based on simulating the collection of cards and choosing the card 
 * that results in the second lowest total value. If there is only one card, it selects the best card. 
 * 
 * When discarding, the AI simulates the effect of discarding two cards and chooses the two cards 
 * that would minimise the total value of the collected cards, taking flipped cards into account.
 */
public class MediumAI extends AIPlayer {

    /**
     * Constructs a new MediumAI player with the given id and name.
     *
     * @param id   the id of the player
     * @param name the name of the AI player
     */
    public MediumAI(int id, String name) {
        super(id, name);
    }

    /**
     * Chooses a card to play from the AI's hand based on the total values of the collected cards.
     * 
     * This method simulates the cards the AI would collect for each card in hand and selects the card that 
     * results in the second least total value. If there is only one card, the best card is selected.
     *
     * @param parade the current parade of cards
     * @return the card chosen by the AI to play
     */
    @Override // choose card that gives the second least value
    public Card chooseCard(Parade parade) {
        List<Card> hand = getHand();
        int[] totalValues = new int[hand.size()];

        // Calculate total value of simulated card collections for each card
        for (int i = 0; i < hand.size(); i++) {
            List<Card> simulated = parade.simulateCollectedCards(hand.get(i));
            int sum = 0;
            for (Card c : simulated) {
                sum += c.getValue();
            }
            totalValues[i] = sum;
        }

        // Sort hand and corresponding total values by increasing total value
        for (int i = 0; i < hand.size(); i++) {
            for (int j = i + 1; j < hand.size(); j++) {
                if (totalValues[i] > totalValues[j]) {
                    int tempVal = totalValues[i];
                    totalValues[i] = totalValues[j];
                    totalValues[j] = tempVal;

                    Card tempCard = hand.get(i);
                    hand.set(i, hand.get(j));
                    hand.set(j, tempCard);
                }
            }
        }

        // Return second best card (lowest total value is best), or best if only 1
        return hand.size() > 1 ? hand.get(1) : hand.get(0);
    }

    /**
     * Chooses two cards to discard from the AI's hand to minimise the total value of the collected cards.
     * 
     * The method simulates the collection of cards after discarding two cards and calculates 
     * a score based on the total value of the collected cards, considering flipped cards.
     * 
     * The two cards that would result in the lowest score are selected for discarding. 
     * The cards in opponents' hands are not considered, only the cards that are already collected by all players.
     *
     * @param hand the list of cards in the player's hand
     * @param allPlayers the list of all players in the game
     * @return an array of two integers, each representing the index of a card to discard from the hand
     */
    @Override
    public int[] chooseDiscards(List<Card> hand, List<Player> allPlayers) {
        int[] bestDiscards = {0, 1};
        int minScore = Integer.MAX_VALUE;

        for (int i = 0; i < hand.size(); i++) {
            for (int j = i + 1; j < hand.size(); j++) {
                // Create a simulated collected set for this AI
                List<Card> simulatedCollected = new ArrayList<>(getCollected());

                // Remove i and j temporarily and add rest of hand
                for (int k = 0; k < hand.size(); k++) {
                    if (k != i && k != j) {
                        simulatedCollected.add(hand.get(k));
                    }
                }

                // Prepare colour counts for all players
                Map<Player, Map<Colour, Integer>> allCounts = new HashMap<>();
                for (Player p : allPlayers) {
                    Map<Colour, Integer> countMap = new HashMap<>();
                    for (Card c : p.getCollected()) {
                        countMap.put(c.getColour(), countMap.getOrDefault(c.getColour(), 0) + 1);
                    }
                    allCounts.put(p, countMap);
                }

                // Include simulatedCollected into this AI's count map
                Map<Colour, Integer> myCounts = new HashMap<>();
                for (Card c : simulatedCollected) {
                    myCounts.put(c.getColour(), myCounts.getOrDefault(c.getColour(), 0) + 1);
                }
                allCounts.put(this, myCounts);

                // Determine flips
                Set<Colour> flipped = new HashSet<>();
                for (Colour c : Colour.values()) {
                    int myCount = allCounts.get(this).getOrDefault(c, 0);
                    boolean othersHaveMore = false;

                    for (Player p : allPlayers) {
                        if (p == this) continue;
                        int theirCount = allCounts.get(p).getOrDefault(c, 0);
                        if (theirCount > myCount) {
                            othersHaveMore = true;
                            break;
                        }
                    }

                    if (!othersHaveMore) {
                        flipped.add(c); // this AI has equal or most
                    }
                }

                // Score this simulated discard
                int total = 0;
                for (Card c : simulatedCollected) {
                    if (flipped.contains(c.getColour())) {
                        total += 1;
                    } else {
                        total += c.getValue();
                    }
                }

                if (total < minScore) {
                    minScore = total;
                    bestDiscards[0] = i;
                    bestDiscards[1] = j;
                }
            }
        }

        return bestDiscards;
    }
}
