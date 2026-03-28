package src.GameManagement.GameState;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import src.GameManagement.Game;
import src.GameManagement.UI.Button;
import src.GameManagement.UI.DialogManager;
import src.GameManagement.UI.GamePanel;

public class IntroScreenState extends GameState{
    // Used to control the flow of text displayed on screen
    int ticksPassed = 0;
    private DialogManager introDialog = new DialogManager();
    
    public IntroScreenState(Game g){
        super(g);
        introDialog.add("You have been hired to defeat the enemies attacking your kingdom.");
        introDialog.add("Train skilled fighters and survive as long as possible!");
        introDialog.add("Make skilled decisions. They will have a major impact on your success.");
        introDialog.add("Good luck on your journey...");
    }

    // Go to the next step around once per second
    public void update(){
        if(super.currentStep < 4){
            ticksPassed++;
            if(ticksPassed % 60 == 0){
                nextStep();
            }
        }
    }

    // Update dialog after every time ENTER is pressed
    protected void handleStep(int step, int keyCode){
        // If dialog is still active, wait until the next line is reached
        if(introDialog.getIsActive()){
            if(keyCode == KeyEvent.VK_ENTER){
                introDialog.nextLine();
            }
        }
        // If dialog is over, wait for the user to advance to the next screen
    }

    // Draw dialog
    protected void drawStep(int step, Graphics graphics){
        introDialog.draw(graphics);
    }

    // Calls once when panel is first loaded
    public void onEnter(GamePanel panel){
        panel.setBackground(new Color(60, 67, 84));
        panel.repaint();
    }
    // Calls once when panel is unloaded
    public void onExit(GamePanel panel){
        panel.setBackground(new Color(0, 0, 0));
    }
}
