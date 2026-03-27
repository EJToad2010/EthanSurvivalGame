package src.GameManagement.UI;

import javax.swing.*;

import src.GameManagement.Game;

import java.awt.*;
import java.awt.event.*;

// Class that calls the graphical functions of subclasses and handles user input.
public class GamePanel extends JPanel {
    // Store a reference to the game
    private Game game;
    public GamePanel(Game game){
        this.game = game;

        // Settings
        setPreferredSize(new Dimension(1280, 720));
        setBackground(new Color(0, 0, 0));
        setFocusable(true);
        setLayout(null);
    }

    public void initKeyInputs(){
        // User input
        addKeyListener(new KeyListener() {
            // Overriding methods?
            public void keyPressed(KeyEvent event){
                int keyCode = event.getKeyCode();
                game.getCurrentGameState().keyPressed(keyCode);
            }

            public void keyTyped(KeyEvent event){}

            public void keyReleased(KeyEvent event){}
        });
    }
    public void setGame(Game newGame){
        game = newGame;
    }

    // Overrided method from JPanel
    protected void paintComponent(Graphics graphics){
        super.paintComponent(graphics);

        // Draw current state
        game.getCurrentGameState().draw(graphics);
    }
}
