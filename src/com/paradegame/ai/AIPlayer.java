package com.paradegame.ai;

import java.util.*;
import com.paradegame.model.*;

/**
 * The AIPlayer class represents an computer-controlled player in the game.
 * It extends the Player class and provides abstract methods for AI decision-making.
 * Concrete implementations should define how the AI chooses cards and discards.
 * This class is extended by EasyAI, MediumAI and HardAI class.
 */
public abstract class AIPlayer extends Player {
    /**
     * Constructs a new AIPlayer instance with the given id and name.
     *
     * @param id   the id of the AI player
     * @param name the name of the AI player
     */
    public AIPlayer(int id, String name) {
        super(id, name);
    }

    /**
     * Abstract method for the AI to choose a card to play from its hand.
     * Subclasses must implement their own logic for choosing a card to play.
     *
     * @param parade the current state of the Parade
     * @return the chosen Card to play
     */
    public abstract Card chooseCard(Parade parade);

    /**
     * Abstract method for the AI to choose cards to discard from its hand.
     * Subclasses must implement their own logic for discarding cards.
     *
     * @param hand the list of cards in the AI's hand
     * @param allPlayers the list of all players in the game
     * @return an array of two integers, each representing the index of a card to discard from the hand
     */
    public abstract int[] chooseDiscards(List<Card> hand, List<Player> allPlayers);

    /**
     * Returns a string representation of the AI player.
     * 
     * @return a string indicating that the player is an AI player.
     */
    public String toString() {
        return "AI Player";
    }
}
