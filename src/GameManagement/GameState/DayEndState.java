package src.GameManagement.GameState;

import java.awt.Color;
import java.awt.Graphics;

import src.GameManagement.Game;
import src.GameManagement.Mechanics.DayManager;
import src.GameManagement.UI.GamePanel;
import src.Teams.PlayerTeam;

public class DayEndState extends GameState {
    PlayerTeam playerTeam;
    public DayEndState(Game g, DayManager dayManager){
        super(g, dayManager);
        dialogManager.add("Your characters are exhausted after a long day of battle.");
        dialogManager.add("They have decided to stay here for the night...");
        isAnimating = false;
        playerTeam = g.getGameData().getPlayerTeamObj();
        currentStep = 0;
    }

    // Handles all logic of a panel
    public void update(){
        if(currentStep == 1){
            nextTick();
            if(animationTick % 5 == 0){
                frame++;
                if(frame > 10){
                    System.out.println("anim end");
                    dayManager.nextPhase();
                }
            }
        }
    }

    // Handle the flow of logic between steps
    protected void handleStep(int step, int keyCode){
        if(step == 0){
            runDialog(keyCode, true);
        }
    }

    // Draw relevant information of a single step
    protected void drawStep(int step, Graphics graphics){
        playerTeam.drawPlayerTeam(graphics, 300, 300, 680);
        if(step == 0){
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
        for(int i = 0; i < frame; i++){
            graphics.setColor(Color.BLACK);
            graphics.fillRect(0, i*72, 1280, 72);
        }
    }

    // Calls once when panel is first loaded
    public void onEnter(GamePanel panel){
        panel.setBackground(new Color(28, 47, 74));
        panel.repaint();
    }
    // Calls once when panel is unloaded
    public void onExit(GamePanel panel){
        panel.setBackground(new Color(0, 0, 0));
    }
}
