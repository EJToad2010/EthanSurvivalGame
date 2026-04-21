package src.GameManagement.UI;

import javax.swing.*;

import src.GameManagement.Game;

// Stores the JFrame object (game window)
public class GameFrame {
    public GameFrame(){
        System.out.println(getClass().getResource("/"));
        JFrame frame = new JFrame("Ethan's Survival Game");
        GamePanel panel = new GamePanel(null);
        Game game = new Game(panel);
        panel.setGame(game);
        panel.initKeyInputs();

        // Java Swing methods I learned to use
        frame.add(panel);
        frame.pack();
        frame.setIconImage(ImageManager.loadImage("src/Images/icon.png"));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setVisible(true);
        panel.requestFocusInWindow();

        game.run();
    }
}
