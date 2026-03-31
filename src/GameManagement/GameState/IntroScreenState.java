package src.GameManagement.GameState;

import java.awt.*;
import java.awt.event.KeyEvent;

import javax.imageio.ImageIO;

import src.GameManagement.Game;
import src.GameManagement.Mechanics.DayManager;
import src.GameManagement.UI.GamePanel;

public class IntroScreenState extends GameState{
    // Used to control the flow of text displayed on screen
    int ticksPassed = 0;
    // Enter screen start off black, with bars disappearing from top to bottom
    int blackBarsVisible = 10;
    
    public IntroScreenState(Game g, DayManager dayManager){
        super(g, dayManager);
        dialogManager.add("You have been hired to defeat the enemies attacking your kingdom.");
        dialogManager.add("Train skilled fighters and survive as long as possible!");
        dialogManager.add("Make skilled decisions. They will have a major impact on your success.");
        dialogManager.add("Good luck on your journey...");
    }

    public void update(){
        if(super.currentStep == 0){
            // Transition animation
            ticksPassed++;
            if(ticksPassed % 5 == 0){
                blackBarsVisible--;
            }
            if(blackBarsVisible < 0){
                ticksPassed = 0;
                super.currentStep++;
            }
        } else if(super.currentStep == 2){
            // Transition animation
            ticksPassed++;
            if(ticksPassed % 5 == 0){
                blackBarsVisible++;
            }
            if(blackBarsVisible > 10){
                dayManager.nextPhase();
            }
        }
    }

    // Update dialog after every time ENTER is pressed
    protected void handleStep(int step, int keyCode){
        if(step == 1){
            // If dialog is still active, wait until the next line is reached
            if(dialogManager.getIsActive()){
                if(keyCode == KeyEvent.VK_ENTER){
                    dialogManager.nextLine();
                    if(!dialogManager.getIsActive()){
                        nextStep();
                    }
                }
            }
        }
    }

    // Draw dialog
    protected void drawStep(int step, Graphics graphics){
        if(step == 1){
            dialogManager.draw(graphics);
        } else{
            // Transition animation
            for(int i = 10-blackBarsVisible; i < 10; i++){
                graphics.setColor(Color.BLACK);
                graphics.fillRect(0, i*72, 1280, 72);
            }
        }
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
