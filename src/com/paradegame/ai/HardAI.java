package com.paradegame.ai;

import com.paradegame.model.*;
import java.util.*;

/**
 * A Hard Version of AI player that chooses cards to play and discards based on complex strategies.
 *
 * The AI chooses which card to play by simulating the cards it would collect
 * and selects the one that results in the lowest total value of the collected cards.
 *
 * For discarding, it evaluates all pairs of cards and chooses the pair that leads
 * to the lowest score, estimating flipped colours and how each opponent
 * might add two cards of every colour to their collection after discarding.
 */
public class HardAI extends AIPlayer {

    /**
     * Constructs a new HardAI player with the given id and name.
     *
     * @param id   the id of the player
     * @param name the name of the AI player
     */
    public HardAI(int id, String name) {
        super(id, name);
    }

    /**
     * Chooses a card to play from the AI's hand based on the total values of the collected cards.
     * 
     * This method simulates the cards the AI would collect for each card in hand and selects the card that 
     * results in the least total value.
     *
     * @param parade the current parade of cards
     * @return the card chosen by the AI to play
     */
    @Override 
    public Card chooseCard(Parade parade) {
        List<Card> hand = getHand();
        Card bestCard = hand.get(0);
        int minValue = Integer.MAX_VALUE;

        for (Card card : hand) {
            List<Card> simulated = parade.simulateCollectedCards(card);
            int totalValue = 0;
            for (Card c : simulated) {
                totalValue += c.getValue();
            }

            if (totalValue < minValue) {
                minValue = totalValue;
                bestCard = card;
            }
        }

        return bestCard;
    }

    /**
     * Chooses two cards to discard from the AI's hand to minimise the total value of the collected cards.
     * 
     * The method simulates the collection of cards after discarding two cards and calculates 
     * a score based on the total value of the collected cards, considering flipped cards and 
     * estimating the potential cards that might be added to the opponents' collections from their hands.
     * 
     * The AI estimates how each opponent might add two cards of every colour to their collection after discarding.
     * 
     * The two cards that would result in the lowest score are selected for discarding.
     *
     * @param hand      the list of cards in the player's hand
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

                // Estimate how other players might add 2 cards to each colour
                for (Player other : allPlayers) {
                    if (other == this) continue;
                    Map<Colour, Integer> estimate = allCounts.get(other);
                    for (Colour c : Colour.values()) {
                        estimate.put(c, estimate.getOrDefault(c, 0) + 2);
                    }
                }

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