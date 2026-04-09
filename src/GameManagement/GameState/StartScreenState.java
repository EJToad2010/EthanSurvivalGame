package src.GameManagement.GameState;

import src.GameManagement.Game;
import src.GameManagement.Mechanics.DayManager;
import src.GameManagement.UI.GamePanel;
import src.GameManagement.UI.ImageManager;
import src.GameManagement.UI.UIManager;


import javax.swing.*;
import java.awt.*;

public class StartScreenState extends GameState{
    private JLabel welcomeMessage;
    private Image castle;
    // Define the buttons that will go into the start screen and constants for each button ID
    private final int START = 0;
    private final int OPTIONS = 1;

    public StartScreenState(Game g, DayManager dayManager){
        super(g, dayManager);
    }
    public void update(){}

    // Override
    protected void handleStep(int step, int keyCode){
        int input = inputHandler.keyPressed(keyCode);
        if(input == START){
            game.setCurrentGameState(new IntroScreenState(game, dayManager));
        } else if(input == OPTIONS){
            setStep(-1);
        }
    }

    protected void drawStep(int step, Graphics graphics){
        inputHandler.spaceButtons(graphics, 50, 800, 400);
        inputHandler.draw(graphics);
        graphics.drawImage(castle, (1280 - castle.getWidth(null))/2 - 40, 100, null);
        if(step == -1){
            UIManager.setTextColor(graphics, Color.GRAY);
            UIManager.setFontSize(28);
            UIManager.refreshText(graphics);
            UIManager.drawCenteredStringInBox(graphics, "Options are not implemented yet.", 0, 600, 1280, 100);
            UIManager.setTextColor(graphics, Color.BLACK);
        }
        if(step == 0){
            UIManager.setTextColor(graphics, Color.GRAY);
            UIManager.setFontSize(25);
            UIManager.refreshText(graphics);
            UIManager.drawCenteredStringInBox(graphics, "Use the left and right arrow keys to move between options. Press ENTER to select.", 0, 600, 1280, 100);
            UIManager.setTextColor(graphics, Color.BLACK);
        }
    }

    // Calls once when panel is first loaded
    public void onEnter(GamePanel panel){
        panel.setBackground(new Color(25, 85, 145));
        // Setup welcomeMessage
        welcomeMessage = UIManager.createCenteredLabel("Welcome to Ethan's Survival Game!", 0, 100, 1280, 100);
        welcomeMessage.setFont(UIManager.getFont(60));
        panel.add(welcomeMessage);
        panel.repaint();
        // Setup inputHandler
        inputHandler = createOptions(new String[]{"Start", "Options"}, new int[]{START, OPTIONS});
        panel.repaint();
        // Setup castle image
        castle = ImageManager.loadImage("src/Images/castle.png");
    }
    // Calls once when panel is unloaded
    public void onExit(GamePanel panel){
        panel.setBackground(new Color(0, 0, 0));
        panel.remove(welcomeMessage);
    }
}
