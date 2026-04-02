package src.GameManagement.GameState;

import java.awt.*;

import src.GameManagement.Game;
import src.GameManagement.Mechanics.DayManager;
import src.GameManagement.UI.GamePanel;

public class IntroScreenState extends GameState{
    // Used to control the flow of text displayed on screen
    int ticksPassed = 0;
    
    public IntroScreenState(Game g, DayManager dayManager){
        super(g, dayManager);
        dialogManager.add("You have been hired to defeat the enemies attacking your kingdom.");
        dialogManager.add("Train skilled fighters and survive as long as possible!");
        dialogManager.add("Make skilled decisions. They will have a major impact on your success.");
        dialogManager.add("Good luck on your journey...");
        isAnimating = true;
        setFrame(10);
    }

    // Handle tick when applicable
    public void update(){
        if(currentStep == 0){
            isAnimating = true;
            nextTick();
            if(animationTick % 5 == 0){
                frame--;
            }
            if(frame < 0){
                resetTick();
                currentStep++;
            }
        } else if(currentStep == 2){
            isAnimating = true;
            nextTick();
            if(animationTick % 5 == 0){
                frame++;
            }
            if(frame > 10){
                dayManager.nextPhase();
            }
        } else{
            isAnimating = false;
        }
    }

    // Update dialog after every time ENTER is pressed
    protected void handleStep(int step, int keyCode){
        if(step == 1){
            runDialog(keyCode, true);
        }
    }

    // Draw dialog
    protected void drawStep(int step, Graphics graphics){
        if(step == 1){
            dialogManager.draw(graphics);
        } else{
            drawScene(scene, graphics);
        }
    }

    // Call in drawStep if a panel wants to make use of animations
    protected void drawScene(int scene, Graphics graphics){
        drawFrame(scene, frame, graphics);
    }
    // Call in drawScene to make use of individual frames
    protected void drawFrame(int scene, int frame, Graphics graphics){
        // Transition animation
        for(int i = 10-frame; i < 10; i++){
            graphics.setColor(Color.BLACK);
            graphics.fillRect(0, i*72, 1280, 72);
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
