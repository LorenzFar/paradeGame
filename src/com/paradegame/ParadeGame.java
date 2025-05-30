package com.paradegame;

import com.paradegame.controller.*;
import com.paradegame.view.*;

/**
 * The ParadeGame class serves as the entry point for starting the parade game.
 * This class sets up the game by initialising the the ConsoleView and InputHandler.
 * It prompts the player for game mode and number of human players, 
 * and then delegating control to the GameController to begin gameplay.
 */
public class ParadeGame {
    /**
     * The main method is the entry point to the ParadeGame application.
     * It initialises the ConsoleView, InputHandler, GameController, prompts for
     * the game mode (AI or human players), and the number of human players, 
     * then starts the game using the GameController.
     * 
     * @param args command-line arguments (not used in this case)
     */
    public static void main(String[] args) {
        ConsoleView console = new ConsoleView();
        InputHandler input = new InputHandler(console);

        boolean aiEnabled = input.promptForGameMode();
        int numHumans = input.promptForHumans(aiEnabled);

        GameController gameController = new GameController(numHumans, aiEnabled);
        gameController.startGame();
    }
}