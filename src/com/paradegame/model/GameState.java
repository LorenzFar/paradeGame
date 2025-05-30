package com.paradegame.model;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.paradegame.util.Config;

/**
 * Represents the state of the Parade game.
 */
public class GameState {
    private final List<Player> players;
    private final Deck deck;
    private final Parade parade;
    private int currentPlayerIndex;
    private int lastRoundIndex = 0;
    private boolean lastRound;

    /**
     * Constructs a new game state with the given players.
     * Initialises the deck, parade, deals initial hands to players and sets the first player.
     *
     * @param players The list of players participating in the game.
     */
    public GameState(List<Player> players) {
        this.players = players;
        this.deck = new Deck();
        this.parade = new Parade();
        initialiseParade();
        dealInitialHands();
        this.currentPlayerIndex = 0;
    }

    /**
     * Initialises the parade by drawing a number of cards from the decks,
     * as specified in the configuration settings.
     */
    private void initialiseParade() {
        int paradeSize = Config.getInt("initialParadeSize", 6);
        for (int i = 0; i < paradeSize; i++) {
            Card card = deck.draw();
            if (card != null) {
                parade.addCard(card);
            }
        }
    }

    /**
     * Deals the initial hands to all players based on the configuration settings.
     */
    private void dealInitialHands() {
        int handSize = Config.getInt("initialHandSize", 5);
        for (Player p : players) {
            for (int i = 0; i < handSize; i++) {
                Card card = deck.draw();
                if (card != null) {
                    p.addToHand(card);
                }
            }
        }
    }

    /**
     * Checks if the game is in the last round.
     *
     * @return {@code true} if the game is in the last round, otherwise {@code false}.
     */
    public boolean isLastRound() {
        return lastRound;
    }


    /**
     * Checks if the last round should begin (all colors collected, deck empty, or already triggered).
     * Increments the lastRoundIndex and sets the lastRound flag if the conditions are met.
     */
    public void checkLastRound() {
        if (players.get(currentPlayerIndex).getCollected().stream().map(Card::getColour).collect(Collectors.toSet())
                .size() >= 6 || deck.size() <= 1 || lastRoundIndex > 0) {
            lastRoundIndex++;
            lastRound = true;
        }
    }

    /**
     * Gets the index of the last round.
     *
     * @return The index representing the last round state.
     */
    public int getlastRoundIndex() {
        return lastRoundIndex;
    }

    /**
     * Determines if the current player is in the discard phase based on the number of cards in their hand.
     *
     * @return {@code true} if the current player is in the discard phase, otherwise {@code false}.
     */
    public boolean isDiscardPhase() {
        if (players.get(currentPlayerIndex).getHand().size() <= 4
                && players.get(currentPlayerIndex).getHand().size() > 2) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Determines if the game is over. 
     * The game ends when the last round has been reached,
     * which occurs after every player has taken their turn in the final round.
     *
     * @return {@code true} if the game has ended, otherwise {@code false}.
     */
    public boolean isGameOver() {
        return lastRoundIndex == players.size() + 1;
    }

    /**
     * Moves the game to the next turn by updating the current player index.
     */
    public void nextTurn() {
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
    }

    /**
     * Calculates the scores for all players at the end of the game.
     * Scores are determined based on the collected cards of each player.
     * Players with the majority in a colour flip those cards, making each worth 1 point.
     * In a 2-player game, a majority requires at least a 2-card lead.
     * Scores are the sum of all collected card values.
     *
     * @return A map of players and their corresponding scores.
     */
    public Map<Player, Integer> calculateScores() {
        Map<Player, Integer> scores = new HashMap<>();
        Map<Colour, Map<Player, Integer>> colourCounts = new EnumMap<>(Colour.class);

        // For each colour, count number of cards that each player has
        for (Colour colour : Colour.values()) {
            colourCounts.put(colour, new HashMap<>());
            for (Player player : players) {
                long count = player.getCollected().stream()
                        .filter(card -> card.getColour() == colour)
                        .count();
                colourCounts.get(colour).put(player, (int) count);
            }
        }

        // Flip cards for player with greatest number of each colour
        if (players.size() > 2) {
            // Regular rules
            for (Colour colour : Colour.values()) {
                Map<Player, Integer> counts = colourCounts.get(colour);
                int max = counts.values().stream().max(Integer::compare).orElse(0);
                for (Player player : players) {
                    int count = counts.get(player);
                    if (count == max) {
                        // Set all cards of this colour's values' to 1
                        for (Card card : player.getCollected()) {
                            if (card.getColour() == colour) {
                                card.setValue(1);
                                card.setFlipped(true);
                            }
                        }
                    }
                }
            }
        } else {
            // 2 player rules
            for (Colour colour : Colour.values()) {
                Map<Player, Integer> counts = colourCounts.get(colour);
                Player p1 = players.get(0);
                Player p2 = players.get(1);
                if (counts.get(p1) - counts.get(p2) > 1) {
                    for (Card card : p1.getCollected()) {
                        ;
                        if (card.getColour() == colour) {
                            card.setValue(1);
                            card.setFlipped(true);
                        }
                    }
                } else if (counts.get(p2) - counts.get(p1) > 1) {
                    for (Card card : p2.getCollected()) {
                        if (card.getColour() == colour) {
                            card.setValue(1);
                            card.setFlipped(true);
                        }
                    }
                }
            }
        }

        for (Player p : players) {
            int sum = p.getCollected().stream()
                    .mapToInt(Card::getValue)
                    .sum();
            scores.put(p, scores.getOrDefault(p, 0) + sum);
        }
        return scores;
    }

    /**
     * Gets the list of players in the game.
     *
     * @return The list of players.
     */
    public List<Player> getPlayers() {
        return players;
    }

    /**
     * Gets the current player whose turn it is.
     *
     * @return The current player.
     */
    public Player getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }

    /**
     * Gets the deck of cards in the game.
     *
     * @return The deck of cards.
     */
    public Deck getDeck() {
        return deck;
    }

    /**
     * Gets the parade of cards in the game.
     *
     * @return The parade instance.
     */
    public Parade getParade() {
        return parade;
    }
}
