package com.paradegame.controller;

import java.util.*;
import java.util.stream.*;
import com.paradegame.model.*;
import com.paradegame.view.*;
import com.paradegame.ai.*;

/**
 * The GameController class manages the flow of the game, including the game state, 
 * player actions, and determining win conditions at the end of the game.
 */
public class GameController {
    private GameState gameState;
    private ConsoleView consoleView;
    private InputHandler inputHandler;
    private CardDisplayer cardDisplayer;
    private Map<Integer, List<Card>> beforeDiscardCollected = new HashMap<>();

    /**
     * Constructs a new GameController with the specified number of human and AI players.
     *
     * @param numHumans the number of human players
     * @param aiEnabled {@code true} if AI is enabled, {@code false} otherwise
     */
    public GameController(int numHumans, boolean aiEnabled) {
        this.consoleView = new ConsoleView();
        this.inputHandler = new InputHandler(consoleView);
        this.cardDisplayer = new CardDisplayer();

        List<Player> players = new ArrayList<>();
        for (int i = 1; i <= numHumans; i++) {
            players.add(new Player(i, "Player " + Integer.toString(i)));
        }

        if (aiEnabled) {
            String difficulty = inputHandler.promptForDifficulty();
            int numAI = inputHandler.promptForNumAI(numHumans);
            for (int i = 1; i <= numAI; i++) {
                players.add(createAIPlayer(i + numHumans, "Bot " + i, difficulty));
            }
        }
        this.gameState = new GameState(players);
    }

    /**
     * Starts the game and runs the main game loop.
     * The game progresses through each player's turn, performing actions 
     * such as playing cards, collecting cards, and discarding cards during the discard phase.
     * The game ends when the game over condition is met, and the scores are calculated.
     */
    public void startGame() {
        while (!gameState.isGameOver()) {
            if (gameState.isDiscardPhase()) {
                break;
            }
            consoleView.displayGameState(gameState);
            Player currentPlayer = gameState.getCurrentPlayer();

            Card playedCard;
            List<Card> collectedCards = new ArrayList<>();

            if (currentPlayer instanceof AIPlayer) {
                playedCard = ((AIPlayer) currentPlayer).chooseCard(gameState.getParade());
                System.out.println(currentPlayer.getName() + " played: " + playedCard);
            } else {
                // Prompt the player to choose a card
                int cardIndex = inputHandler.promptPlayerForCard(currentPlayer, gameState, cardDisplayer);
                playedCard = currentPlayer.getHand().get(cardIndex);
            }

            currentPlayer.removeFromHand(playedCard);
            gameState.getParade().addCard(playedCard);
            collectedCards = gameState.getParade().handleCardPlayed(playedCard);
            currentPlayer.addCollected(collectedCards);
            Card newCard = null;

            gameState.checkLastRound();
            if (!gameState.isLastRound() || (gameState.getlastRoundIndex() == 1 && !gameState.getDeck().isEmpty())) {
                newCard = gameState.getDeck().draw();
                currentPlayer.addToHand(newCard);
            }

            consoleView.displayTurnSummary(gameState, playedCard, collectedCards, newCard);

            inputHandler.promptForNextTurn();

            // Move to the next player
            gameState.nextTurn();
        }

        // Take a snapshot of collected cards before discard phase starts
        for (Player player : gameState.getPlayers()) {
            List<Card> copied = new ArrayList<>();
            for (Card card : player.getCollected()) {
                copied.add(new Card(card.getColour(), card.getValue())); // deep copy
            }
            beforeDiscardCollected.put(player.getId(), copied);
        }

        // Discard phase
        while (gameState.isDiscardPhase()) {
            Player currentPlayer = gameState.getCurrentPlayer();

            // Display snapshot version of game state
            consoleView.displayGameState(gameState, beforeDiscardCollected);

            int cardIndex;

            if (currentPlayer instanceof AIPlayer) {
                int[] discards = ((AIPlayer) currentPlayer).chooseDiscards(currentPlayer.getHand(),
                        gameState.getPlayers());

                int index1 = Math.max(discards[0], discards[1]);
                int index2 = Math.min(discards[0], discards[1]);

                Card discard1 = currentPlayer.getHand().get(index1);
                Card discard2 = currentPlayer.getHand().get(index2);

                currentPlayer.removeFromHand(discard1);
                currentPlayer.removeFromHand(discard2);

                currentPlayer.addCollected(currentPlayer.getHand());
                currentPlayer.getHand().clear();

            } else {
                cardIndex = inputHandler.promptPlayerForCard(currentPlayer, gameState, cardDisplayer);
                Card discardedCard = currentPlayer.getHand().get(cardIndex);
                currentPlayer.removeFromHand(discardedCard);

                // Keep showing pre-discard snapshot
                consoleView.displayGameState(gameState, beforeDiscardCollected);

                cardIndex = inputHandler.promptPlayerForCard(currentPlayer, gameState, cardDisplayer);
                discardedCard = currentPlayer.getHand().get(cardIndex);
                currentPlayer.removeFromHand(discardedCard);

                currentPlayer.addCollected(currentPlayer.getHand());
                currentPlayer.getHand().clear();
            }

            gameState.nextTurn();
        }

        Map<Player, Integer> scores = gameState.calculateScores();

        // Display winner
        consoleView.displayWinner(gameState, getWinner(scores), scores);
    }

     /**
     * Creates an AI player based on the specified difficulty level.
     *
     * @param id         the id of the AI player
     * @param name       the name of the AI player
     * @param difficulty the difficulty level of the AI ("easy", "medium", or "hard")
     * @return the created AI player
     */
    private AIPlayer createAIPlayer(int id, String name, String difficulty) {
        switch (difficulty) {
            case "easy":
                return new EasyAI(id, name);
            case "medium":
                return new MediumAI(id, name);
            case "hard":
                return new HardAI(id, name);
            default:
                // Should never reach here due to prior validation
                return new MediumAI(id, name);
        }
    }

    /**
     * Determines the winner of the game based on the calculated scores. If multiple players have the same score,
     * the winner is chosen based on the least number of collected cards.
     *
     * @param scores a map of players and their respective scores
     * @return the player with the lowest score, or the one with the fewest collected cards in case of a tie
     */
    public Player getWinner(Map<Player, Integer> scores) {
        int minScore = Collections.min(scores.values());

        // Add players with score equal to the lowest score to a list
        List<Player> winners = scores.entrySet().stream()
                .filter(e -> e.getValue() == minScore)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        // If multiple winners, check for player with least number of cards
        if (winners.size() > 1) {
            Player minCardsWinner = winners.get(0);
            int minCards = minCardsWinner.getCollected().size();
            for (Player winner : winners) {
                if (winner.getCollected().size() < minCards) {
                    minCardsWinner = winner;
                    minCards = winner.getCollected().size();
                }
            }
            // P.S. It's probably impossible for two players to have the same score and same number of cards
            return minCardsWinner;
        }
        return winners.get(0);
    }

    /**
     * Gets the current game state.
     *
     * @return the current game state
     */
    public GameState getGameState() {
        return gameState;
    }

    /**
     * Gets the console view used to display game information.
     *
     * @return the console view
     */
    public ConsoleView getConsoleView() {
        return consoleView;
    }
}
