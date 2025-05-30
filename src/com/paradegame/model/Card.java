package com.paradegame.model;

import com.paradegame.util.Config;

/**
 * Represents a card with a colour and a value.
 * The card can be flipped and its value can be modified to 1 for score calculation.
 * It also supports colour-coded output for printing.
 */
public class Card implements Comparable<Card> {
    private final Colour colour;
    private int value;
    private boolean isFlipped = false;

    // Coloured prints
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_ORANGE = "\u001B[33m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_GREY = "\u001B[37m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_RED = "\u001B[31m";

    /**
     * Construct a new Card with the specified colour and value.
     * 
     * @param colour the colour of the card
     * @param value the numerical value of the card
     */
    public Card(Colour colour, int value) {
        this.colour = colour;
        this.value = value;
    }

    /**
     * Gets the colour of the card
     * 
     * @return The colour of the card
     */
    public Colour getColour() {
        return colour;
    }

    /**
     * Gets the value of the card
     * 
     * @return The value of the card
     */
    public int getValue() {
        return value;
    }

    /**
     * Sets value of the card.
     * This method is used to change the card's value to 1 during the flipping process,
     * which occurs when calculating scores.
     * 
     * @param value The new value of the card.
     */
    public void setValue(int value) {
        this.value = value;
    }

    /**
     * Sets the flip status of the card
     * 
     * @param flipped {@code true} if the card is flipped, {@code false} otherwise.
     */
    public void setFlipped(boolean flipped) {
        this.isFlipped = flipped;
    }
    
    /**
     * Checks whether the card is flipped.
     *
     * @return {@code true} if the card is flipped, {@code false} otherwise.
     */
    public boolean isFlipped() {
        return isFlipped;
    }

    /**
     * Compares this card to another card based on their values.
     *
     * @param other The other card to compare with.
     * @return A negative integer if this card has a lower value,
     *         zero if both cards have the same value,
     *         or a positive integer if this card has a higher value.
     */
    @Override
    public int compareTo(Card other) {
        return this.value - other.value;
    }

     /**
     * Returns a string representation of the card.
     * If ANSI colors are enabled, the card's colour is displayed with corresponding ANSI codes.
     *
     * @return A formatted string representation of the card.
     */
    @Override
    public String toString() {
        boolean useColors = Config.getBoolean("useAnsiColors", true);
        String label = isFlipped ? "FLIPPED" : colour.toString();
        String output = label + " " + value;

        if (!useColors) {
            return output;
        }

        switch (colour) {
            case BLUE:
                return ANSI_BLUE + output + ANSI_RESET;
            case ORANGE:
                return ANSI_ORANGE + output + ANSI_RESET;
            case GREEN:
                return ANSI_GREEN + output + ANSI_RESET;
            case GREY:
                return ANSI_GREY + output + ANSI_RESET;
            case PURPLE:
                return ANSI_PURPLE + output + ANSI_RESET;
            case RED:
                return ANSI_RED + output + ANSI_RESET;
            default:
                return output;
        }
    }
}
