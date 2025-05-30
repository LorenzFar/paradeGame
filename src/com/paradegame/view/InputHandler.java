package com.paradegame.view;

import java.util.*;
import com.paradegame.model.*;

/**
 * The InputHandler class is responsible for handling user input during the game.
 * It prompts the user for various choices, such as the game mode, number of players,
 * AI difficulty, and card selection. It also ensures that the input is valid before proceeding.
 */
public class InputHandler {
    private final Scanner scanner;
    private final ConsoleView consoleView;

    /**
     * Constructs an InputHandler instance, initialising the scanner and consoleView.
     * 
     * @param consoleView the console view to be used for displaying prompts and game state
     */
    public InputHandler(ConsoleView consoleView) {
        this.scanner = new Scanner(System.in);
        this.consoleView = consoleView;
    }

    /**
     * Prompts the player to choose the game mode (versus humans or versus AI).
     * 
     * @return {@code true} if AI mode is selected, {@code false} if human mode is selected
     */
    public boolean promptForGameMode() {
        consoleView.displayStart();
        int choice = 1;
        boolean validInput = false;
        while (!validInput) {
            System.out.print("Enter your choice (1 or 2): ");
            String choiceStr = scanner.nextLine();
            if (choiceStr.equals("1") || choiceStr.equals("2")) {
                choice = Integer.parseInt(choiceStr);
                validInput = true;
            }
        }
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            System.out.println("pause failed");
        }
        return choice == 2 ? true : false;
    }

    /**
     * Prompts the user to enter the number of human players.
     * 
     * @param aiEnabled {@code true} if AI is enabled, restricting human players to 5.
     * @return the number of human players
     */
    public int promptForHumans(boolean aiEnabled) {
        int numPlayers = 1;
        boolean validInput = false;
        while (!validInput) {
            if (aiEnabled) {
                System.out.print("Enter number of human players (1-5): ");
            } else {
                System.out.print("Enter number of players (2-6): ");
            }
            try {
                numPlayers = Integer.parseInt(scanner.nextLine());
                if (aiEnabled) {
                    if (numPlayers > 0 && numPlayers <= 5) {
                        validInput = true;
                    }
                } else {
                    if (numPlayers > 1 && numPlayers <= 6) {
                        validInput = true;
                    }
                }
            } catch (Exception e) {
                if (aiEnabled) {
                    System.out.print("Invalid number. Please enter between 1-5 players: ");
                } else {
                    System.out.print("Invalid number. Please enter between 2-6 players: ");
                }
            }
        }
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            System.out.println("pause failed");
        }
        return numPlayers;
    }

    /**
     * Prompts the user to select the AI difficulty level.
     * 
     * @return the selected difficulty level (Easy, Medium, or Hard)
     */
    public String promptForDifficulty() {
        String difficulty = "bruh";
        while (!isValidDifficulty(difficulty)) {
            System.out.print("Enter AI difficulty (Easy, Medium or Hard): ");
            difficulty = scanner.nextLine();
        }
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            System.out.println("pause failed");
        }
        return difficulty;
    }

     /**
     * Prompts the user to enter the number of AI players.
     * 
     * @param numHumans the number of human players that was already selected
     * @return the number of AI players
     */
    public int promptForNumAI(int numHumans) {
        if (numHumans == 5) {
            System.out.println("1 AI player automatically selected.");
            return 1;
        }
        int numAI = 0;
        boolean validInput = false;
        while (!validInput) {
            System.out.printf("Enter number of AI players (1-%d): ", 6 - numHumans);
            try {
                numAI = Integer.parseInt(scanner.nextLine());
                if (numAI > 0 && numAI < 7 - numHumans) {
                    validInput = true;
                }
            } catch (Exception e) {
                // Catch NumberFormatException and continue loop
            }
        }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            System.out.println("pause failed");
        }
        return numAI;
    }

     /**
     * Prompts the player to select a card to play or discard.
     * 
     * @param player the player who is making the selection
     * @param gameState the current state of the game
     * @param cardDisplayer the CardDisplayer used to display cards
     * @return the index of the selected card
     */
    public int promptPlayerForCard(Player player, GameState gameState, CardDisplayer cardDisplayer) {
        int cardIndex = -1;
        boolean confirmSelection;

        consoleView.displayTurnPrompt(player.getName());

        do {
            // Get a valid card index
            cardIndex = getValidCard(player, gameState.isDiscardPhase());

            // Simulate and display possible collected cards
            Card selectedCard = player.getHand().get(cardIndex);

            if (!gameState.isDiscardPhase()) {
                List<Card> possibleCollectedCards = gameState.getParade().simulateCollectedCards(selectedCard);
                consoleView.displayMove(selectedCard, possibleCollectedCards);
            }

            // Confirm the card selection
            confirmSelection = confirmCardSelection(selectedCard, gameState.isDiscardPhase());
        } while (!confirmSelection);

        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            System.out.println("pause failed");
        }

        return cardIndex; // Return the confirmed card index
    }

    /**
     * Gets a valid card index from the player's hand.
     * 
     * @param player the player selecting the card.
     * @param isDiscardPhase {@code true} if the player is discarding a card, {@code false} otherwise.
     * @return the valid card index
     */
    private int getValidCard(Player player, boolean isDiscardPhase) {
        int cardIndex = -1;
        boolean validInput = false;

        while (!validInput) {
            try {
                if (!isDiscardPhase) {
                    consoleView.displayPlaySelectionPrompt(player.getHand().size());
                } else {
                    consoleView.displayDiscardSelectionPrompt(player.getHand().size());
                }
                cardIndex = scanner.nextInt();

                if (cardIndex >= 0 && cardIndex < player.getHand().size()) {
                    validInput = true; // Exit loop if input is valid
                } else {
                    consoleView.displayInvalidInputMessage("index", player.getHand().size());
                }
            } catch (InputMismatchException e) {
                consoleView.displayInvalidInputMessage("index", player.getHand().size());
                scanner.nextLine(); // Clear invalid input
            }
        }
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            System.out.println("pause failed");
        }
        return cardIndex;
    }

    /**
     * Prompts the player to confirm the card selection.
     * 
     * @param playedCard the card selected by the player
     * @param isDiscardPhase {@code true} if discarding, {@code false} if playing.
     * @return {@code true} if the selection is confirmed, {@code false} otherwise.
     */
    private boolean confirmCardSelection(Card playedCard, boolean isDiscardPhase) {
        while (true) {
            if (!isDiscardPhase) {
                consoleView.displayPlayConfirmationPrompt(playedCard);
            } else {
                consoleView.displayDiscardConfirmationPrompt(playedCard);
            }
            String confirmation = scanner.next().trim().toLowerCase();
            scanner.nextLine(); // Consumes the leftover /n

            if ("y".equals(confirmation)) {
                return true; // Confirm move
            } else if ("n".equals(confirmation)) {
                consoleView.displayCancelMessage();
                return false; // Cancel move
            } else {
                consoleView.displayInvalidInputMessage("confirmation", 0);
            }
        }
    }

    /**
     * Prompts the user to proceed to the next turn.
     */
    public void promptForNextTurn() {
        consoleView.displayNextTurnPrompt();
        scanner.nextLine(); // Wait for user to press enter
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            System.out.println("pause failed");
        }
    }

    /**
     * Checks if the entered difficulty level is valid.
     *
     * @param difficulty The difficulty level entered by the user.
     * @return {@code true} if valid (Easy, Medium, or Hard), {@code false} otherwise.
     */
    private boolean isValidDifficulty(String difficulty) {
        switch (difficulty.toLowerCase()) {
            case "easy":
            case "e":
            case "medium":
            case "m":
            case "hard":
            case "h":
                return true;
            default:
                return false;
        }
    }
}
