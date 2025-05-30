package com.paradegame.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a player in the Parade game.
 * A player has an id, a name, a hand of cards they can play,
 * and a collection of cards they have collected throughout the game.
 */
public class Player {
    private int id;
    private String name;
    private final List<Card> hand = new ArrayList<>();
    private final List<Card> collected = new ArrayList<>();

    /**
     * Constructs a new Player with the specified id and name.
     *
     * @param id   the id of the player
     * @param name the name of the player
     */
    public Player(int id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * Adds a card to the player's hand.
     *
     * @param card the card to be added
     */
    public void addToHand(Card card) {
        hand.add(card);
    }

    /**
     * Gets the player's current hand of cards.
     *
     * @return the list of cards in the player's hand
     */
    public List<Card> getHand() {
        return hand;
    }

    /**
     * Removes a specified card from the player's hand.
     *
     * @param card the card to be removed
     */
    public void removeFromHand(Card card) {
        hand.remove(card);
    }

    /**
     * Adds a list of collected cards to the player's collected pile.
     *
     * @param cards the list of cards to be added to the collected pile
     */
    public void addCollected(List<Card> cards) {
        collected.addAll(cards);
    }

    /**
     * Gets the list of cards the player has collected.
     *
     * @return the list of collected cards
     */
    public List<Card> getCollected() {
        return collected;
    }

    /**
     * Gets the name of the player.
     * 
     * @return the name of the player
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the id of the player.
     * 
     * @return the id of the player
     */
    public int getId(){
        return id;
    }

    /**
     * Returns a string representation of the player.
     *
     * @return A string indicating the player is a human player.
     */
    public String toString() {
        return "Human Player";
    }
}
