package com.paradegame.view;

import com.paradegame.ai.AIPlayer;
import com.paradegame.model.*;
import java.util.*;

/**
 * The ConsoleView class is responsible for displaying the game state and user prompts to the console.
 * It handles the presentation of various game phases, such as the start of the game, each player's turn, 
 * the discard phase, and the final winner display.
 */
public class ConsoleView {
    private final CardDisplayer cardDisplayer;

    /** Current turn counter, increments each turn. */
    public static int turn = 1;

    /** Initial number of cards in the game deck. */
    private static final int INITIAL_DECK_SIZE = 66;

    /**
     * Constructs a ConsoleView instance, initialising the CardDisplayer.
     */
    public ConsoleView() {
        this.cardDisplayer = new CardDisplayer();
    }

    /**
     * Displays the start screen of the game, including the game title and options to select the game mode.
     */
    public void displayStart() {
        try {
            System.out.println("WELCOME TO...");
            Thread.sleep(750);
            System.out.print(
                    " ____                     _      \n" + //
                            "|  _ \\ __ _ _ __ __ _  __| | ___ \n" + //
                            "| |_) / _` | '__/ _` |/ _` |/ _ \\\n" + //
                            "|  __/ (_| | | | (_| | (_| |  __/\n" + //
                            "|_|   \\__,_|_|  \\__,_|\\__,_|\\___|\n" + //
                            "\n" + //
                            "");
            Thread.sleep(500);
            System.out.println("Select the game mode:");
            System.out.println("1. Versus Humans");
            System.out.println("2. Versus AI");
        } catch (InterruptedException e) {
            System.out.println("pause failed");
        }
    }
    
    /**
     * Displays the game state, including the current turn, parade, deck size, and collected cards.
     * This method will show different information depending on the phase of the game (discard or play).
     * 
     * @param gameState the current state of the game
     */
    public void displayGameState(GameState gameState) {
        if (turn == 1) {
            try {
                Thread.sleep(500);
                System.out.printf("\nInitialising game...\n");
                Thread.sleep(2000);
                System.out.printf("\nSetting up Parade...\n");
                Thread.sleep(1500);
                System.out.printf("\nShuffling deck...\n");
                Thread.sleep(1000);
                System.out.printf("\nDealing hands...\n");
                Thread.sleep(1500);
                System.out.printf("\nLet's Parade!\n");
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                System.out.println("pause failed");
            }
        }
        // Turn display
        try {
            System.out.printf("\n================ TURN %d ===================\n", turn++);
            Thread.sleep(100);
            System.out.println("\n==== PARADE ====");
            System.out.println("Parade: " + gameState.getParade());
            System.out.println("Deck Size: " + (INITIAL_DECK_SIZE - gameState.getDeck().getIndex()));
            System.out.println("================");
            Thread.sleep(100);
        } catch (InterruptedException e) {
            System.out.println("pause failed");
        }

        // Show current player's turn and last turn if applicable
        System.out.println("\n--- " + gameState.getCurrentPlayer().getName().toUpperCase() + "'S TURN ---");
        if (gameState.isLastRound() && !gameState.isDiscardPhase()) {
            System.out.println("\n--- THIS IS YOUR LAST TURN BEFORE DISCARD PHASE ---");
        }

        // Display game state for human players
        if (!(gameState.getCurrentPlayer() instanceof AIPlayer)) {
            // Show current player's hand and collected cards
            System.out.println("\n--- HAND ---");
            cardDisplayer.displayCardsInHand(gameState.getCurrentPlayer().getHand());
            System.out.println("\n--- YOUR COLLECTED CARDS ---");
            displayPlayerCollectedCards(gameState.getCurrentPlayer());

            // Show other players' collected cards
            System.out.println("\n--- OTHER PLAYERS' COLLECTED CARDS ---");
            for (Player player : gameState.getPlayers()) {
                try {
                    if (!player.equals(gameState.getCurrentPlayer())) {
                        System.out.println(player.getName() + ": ");
                        displayPlayerCollectedCards(player);
                        Thread.sleep(100);
                    }
                } catch (InterruptedException e) {
                    System.out.println("pause failed");
                }
            }
            System.out.println("============================================\n");
        } else {
            try {
                System.out.println("\nAI is playing...\n");
                System.out.println("============================================\n");
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                System.out.println("pause failed");
            }
        }
    }

    /**
     * Displays the game state during the discard phase, showing the collected cards 
     * of all players before the discard phase starts. 
     * 
     * This is an overloaded version of {@link #displayGameState(GameState)} that uses a
     * a snapshot of the collected cards to ensure players only see the collected cards 
     * before the discard phase, not the cards being added during it.
     *
     * @param gameState the current state of the game
     * @param beforeDiscardCollected a map of player IDs to their collected cards 
     *                               before the discard phase starts
     */
    public void displayGameState(GameState gameState, Map<Integer, List<Card>> beforeDiscardCollected) {
        // Discard phase display
        System.out.printf("\n============= DISCARD PHASE ================\n");

        // Show current player's turn
        System.out.println("\n--- " + gameState.getCurrentPlayer().getName().toUpperCase() + "'S TURN ---");

        if (!(gameState.getCurrentPlayer() instanceof AIPlayer)) {
            // Show current player's hand and collected cards
            System.out.println("\n--- HAND ---");
            cardDisplayer.displayCardsInHand(gameState.getCurrentPlayer().getHand());
            System.out.println("\n--- YOUR COLLECTED CARDS ---");
            displayCollectedWithFallback(gameState.getCurrentPlayer(), beforeDiscardCollected);

            // Show other players' collected cards
            System.out.println("\n--- OTHER PLAYERS' COLLECTED CARDS ---");
            for (Player player : gameState.getPlayers()) {
                try {
                    if (!player.equals(gameState.getCurrentPlayer())) {
                        System.out.println(player.getName() + ": ");
                        displayCollectedWithFallback(player, beforeDiscardCollected);
                        Thread.sleep(100);
                    }
                } catch (InterruptedException e) {
                    System.out.println("pause failed");
                }
            }
            System.out.println("============================================\n");
        } else {
            try {
                System.out.println("\nAI is playing...\n");
                System.out.println("============================================\n");
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                System.out.println("pause failed");
            }
        }
    }

    /**
     * Displays the collected cards for a given player.
     * 
     * @param player the player whose collected cards are to be displayed
     */
    public void displayPlayerCollectedCards(Player player) {
        if (player.getCollected().isEmpty()) {
            System.out.println("(No cards collected yet)\n");
        } else {
            cardDisplayer.displayCards(player.getCollected(), true);
            System.out.println();
        }
    }

    /**
     * Displays a player's collected cards using the snapshot taken before the discard phase.
     * 
     * This prevents any cards remaining in the player's hand after discarding from being shown as 
     * part of the player's collected cards before the scoring phase.
     * 
     * This avoids revealing which cards have been added to the player's collection 
     * before all players have finished discarding.
     * 
     * @param player the player whose collected cards are to be displayed
     * @param beforeDiscardCollected a map of player IDs to their collected cards before the discard phase
     */
    private void displayCollectedWithFallback(Player player, Map<Integer, List<Card>> beforeDiscardCollected) {
        List<Card> cards = beforeDiscardCollected.get(player.getId());
        if (cards == null || cards.isEmpty()) {
            System.out.println("(No cards collected yet)\n");
        } else {
            cardDisplayer.displayCards(cards, true);
            System.out.println();
        }
    }

    /**
     * Displays the move the current player is about to make, including the played card and any possible collected cards.
     * 
     * @param playedCard the card being played
     * @param possibleCollectedCards a list of cards that can potentially be collected
     */
    public void displayMove(Card playedCard, List<Card> possibleCollectedCards) {
        System.out.println("\nYou're going to play: " + playedCard);
        if (possibleCollectedCards.isEmpty()) {
            System.out.println("No cards collected.");
        } else {
            System.out.print("Possible cards you might collect: ");
            cardDisplayer.displayCards(possibleCollectedCards, false);
        }
        System.out.println();
    }

    /**
     * Displays a prompt indicating that it is the player's turn.
     * 
     * @param playerName the name of the player whose turn it is
     */
    public void displayTurnPrompt(String playerName) {
        try {
            System.out.println(playerName + ", it's your turn!");
            Thread.sleep(500);
        } catch (InterruptedException e) {
            System.out.println("pause failed");
        }
    }

    /**
     * Displays a prompt for the player to choose a card to play.
     * 
     * @param handSize the size of the player's hand
     */
    public void displayPlaySelectionPrompt(int handSize) {
        System.out.print("Choose a card to play (enter index 0-" + (handSize - 1) + "): ");
    }

    /**
     * Displays a prompt for the player to choose a card to discard.
     * 
     * @param handSize the size of the player's hand
     */
    public void displayDiscardSelectionPrompt(int handSize) {
        if (handSize == 4) {
            System.out.print("Choose a card to discard (enter index 0-" + (handSize - 1) + "): ");
        } else {
            System.out.print("Choose another card to discard (enter index 0-" + (handSize - 1) + "): ");
        }
    }

    /**
     * Displays a confirmation prompt for the player to confirm playing a specific card.
     * 
     * @param playedCard the card the player is about to play
     */
    public void displayPlayConfirmationPrompt(Card playedCard) {
        System.out.print("Are you sure you want to play " + playedCard + "? (Y/N): ");
    }

     /**
     * Displays a confirmation prompt for the player to confirm discarding a specific card.
     * 
     * @param playedCard the card the player is about to discard
     */
    public void displayDiscardConfirmationPrompt(Card playedCard) {
        System.out.print("Are you sure you want to discard " + playedCard + "? (Y/N): ");
    }

    /**
     * Displays an error message for invalid input.
     * 
     * @param type the type of invalid input ("index", "confirmation")
     * @param handSize the size of the player's hand
     */
    public void displayInvalidInputMessage(String type, int handSize) {
        if ("index".equalsIgnoreCase(type)) {
            System.out.println("Invalid input. Please enter a number between 0 and " + (handSize - 1) + ".");
        } else if ("confirmation".equalsIgnoreCase(type)) {
            System.out.println("Invalid input. Please enter 'Y' to confirm or 'N' to cancel.");
        } else {
            System.out.println("Invalid input. Please try again.");
        }
    }

    /**
     * Displays a message indicating that the card selection was cancelled.
     */
    public void displayCancelMessage() {
        System.out.println("Card selection cancelled. Please choose another card.\n");
    }

    /**
     * Displays a prompt for the player to proceed to the next turn.
     */
    public void displayNextTurnPrompt() {
        System.out.print("Ready for the next turn? Press Enter to continue.");
    }

     /**
     * Displays a summary of the current turn, including the played card, collected cards, and drawn card.
     * 
     * @param gameState the current state of the game
     * @param playedCard the card played during the turn
     * @param collectedCards the cards collected during the turn
     * @param newCard the new card drawn during the turn
     */
    public void displayTurnSummary(GameState gameState, Card playedCard, List<Card> collectedCards, Card newCard) {
        System.out.println("\n=== TURN SUMMARY ===");
        System.out.println("Played: " + playedCard);

        System.out.print("Collected: ");

        // Display collected cards
        if (collectedCards.isEmpty()) {
            System.out.println("None for this turn");
        } else {
            cardDisplayer.displayCards(collectedCards, false);
        }

        // Display drawn card
        if (gameState.getCurrentPlayer() instanceof AIPlayer) {
            System.out.println("Drawn Card: No peeking at the AI's cards!");
        } else if (newCard != null) {
            System.out.println("Drawn Card: " + newCard);
        } else {
            System.out.println("Drawn Card: None for last round");
        }

        System.out.println("====================\n");
    }

    /**
     * Displays the winner and the final game results, including collected cards and scores.
     * 
     * @param gameState the final state of the game, containing player information.
     * @param winner the player who won the game.
     * @param scores a map associating each player with their final score.
     */
    public void displayWinner(GameState gameState, Player winner, Map<Player, Integer> scores) {
        try {
            Thread.sleep(2000);
            System.out.println("\n================= GAME OVER ================");
            Thread.sleep(500);

            System.out.println("\n--- FINAL CARDS COLLECTED ---");
            for (Player player : gameState.getPlayers()) {
                System.out.println(player.getName() + ":");
                cardDisplayer.displayCards(player.getCollected(), true);
                Thread.sleep(100);
            }

            Thread.sleep(1000);
            System.out.println("\nCALCULATING WINNER(S)...");
            Thread.sleep(3000);

            System.out.println("\n--- FINAL SCORES ---");
            for (Player player : gameState.getPlayers()) {
                System.out.println(player.getName() + ": " + scores.get(player));
                Thread.sleep(500);
            }

            Thread.sleep(750);
            System.out.println("\n----- CONGRATULATIONS WINNER(S) -----");
            System.out.println(winner.getName() + "!");

            System.out.println("\n============================================\n");
        } catch (InterruptedException e) {
            System.out.println("pause failed");
        }
    }
}