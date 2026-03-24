package src.GameManagement.GameState;
// Import graphics and event handling libraries
import javax.swing.*;

import src.GameManagement.Game;

import java.awt.*;
import java.awt.event.*;

// A singular class that can be called to execute a specific "phase" of the game
// Contains both the logic and graphics of a phase
public class GameState {
    // Store a reference to the running Game instance
    protected Game game;
    
    // Constructor
    public GameState(Game game){
        this.game = game;
    }

    // Both of these methods should call once per frame but they handle different tasks
    // Handles all logic of a panel
    public void update(){}
    // Handles all graphics of a panel
    public void draw(Graphics graphics){}
}
