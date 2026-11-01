package src.GameManagement.GameState;

import java.awt.Color;
import java.awt.Graphics;

import src.GameManagement.Game;
import src.GameManagement.Mechanics.DayManager;
import src.GameManagement.UI.GamePanel;
import src.GameManagement.UI.UIManager;
import src.Teams.PlayerTeam;

public class DayEndState extends GameState {
    // Add nighttime ambush?

    // Constants used to define each major step
    private final int ENTER_AREA = 0;
    private final int SELECT_ACTION = 1;
    private final int PERFORM_REST = 2;
    private final int SELECT_ITEM = 3;
    private final int SELECT_ITEM_TARGET = 4;
    private final int USE_ITEM = 5;
    private final int PERFORM_ACTION = 6;

    // Possible rest locations to encounter
    private String[] restLocations = new String[]{"Abandoned Campsite", "Mountain Cave", "Riverside"};
    // Adjust the relative probability of each location being chosen
    private Double[] locationProbabilities = new Double[]{2.0, 1.0, 1.0};
    // Every rest location has two unique actions associated with them
    private String[] locationAction1 = new String[]{"Forage", "Explore", "Fish"};
    private String[] locationAction2 = new String[]{"Stoke Fire", "Reinforce", "Collect Water"};
    // Generic actions available for every location
    private String[] genericActions = new String[]{"Rest", "Use item"};

    // How many total actions player can make
    private int totalActions;
    private int actionsLeft;

    private String chosenLocation;
    private int chosenLocationIndex;

    private int chosenActionNumber;
    private String chosenAction;

    // UI Variables
    private int buttonFontSize = 40;

    PlayerTeam playerTeam;
    public DayEndState(Game g, DayManager dayManager){
        super(g, dayManager);
        dialogManager.add("Your characters are exhausted after a long day of battle.");
        isAnimating = false;
        playerTeam = g.getGameData().getPlayerTeamObj();
        currentStep = 0;
        totalActions = playerTeam.getAliveCharacters().size();
        actionsLeft = totalActions;

        chosenLocationIndex = (int)(Math.random() * restLocations.length);
        chosenLocation = restLocations[chosenLocationIndex];
        if(chosenLocation.equals("Abandoned Campsite")){
            dialogManager.add("They have found an abandoned campsite used by previous travelers.");
        } else if(chosenLocation.equals("Mountain Cave")){
            dialogManager.add("They have found a secluded, sheltered cave inside of a mountain.");
        } else if(chosenLocation.equals("Riverside")){
            dialogManager.add("They have found an open-air site near a fertile river.");
        } else{
            // Should not happen
            System.out.println("ERROR: Proper rest location not found.");
            dialogManager.add("They have found a random resting place.");
        }
    }

    // Handles all logic of a panel
    public void update(){
        // Temp
        if(currentStep == 2){
            isAnimating = true;
            nextTick();
            if(animationTick % 5 == 0){
                frame++;
            }
            if(frame > 10){
                System.out.println("anim end");
                dayManager.nextPhase();
            }
        }
    }

    // Handle the flow of logic between steps
    protected void handleStep(int step, int keyCode){
        // Handle any dialog that exists
        int exitCode = runDialog(keyCode, false);
        if(exitCode == -1 && (dialogManager.getIsActive() || dialogManager.size() == 0)){
            return;
        }
        if(exitCode == 0){
            handleDialogEndedLogic(step);
        }

        // Handle any inputHandler actions that exist
        if(!inputHandler.getButtons().isEmpty() && !dialogManager.getIsActive() && exitCode == -1){
            int input = inputHandler.keyPressed(keyCode);
            if(input == -1){
                return;
            }

            if(step == SELECT_ACTION){
                chosenActionNumber = input;
                chosenAction = inputHandler.getButtons().get(input).getText();

                inputHandler.clear();
                // Temp
                nextStep();
            }
        }
    }

    protected void handleDialogEndedLogic(int step){
        if(step == ENTER_AREA){
            nextStep();
        }
    }

    // Calls once when a new step is first loaded
    protected void onEnterStep(int step){
        if(step == SELECT_ACTION){
            inputHandler = createOptions(new String[]{genericActions[0], genericActions[1], locationAction1[chosenLocationIndex], locationAction2[chosenLocationIndex]});
        }
    }
    // Calls once when the previous step exits
    protected void onExitStep(int step){}

    // Draw relevant information of a single step
    protected void drawStep(int step, Graphics graphics){
        playerTeam.drawPlayerTeam(graphics, 300, 300, 680);
        if(step == ENTER_AREA){
        } else if(step == SELECT_ACTION){
            dialogManager.drawDialogBox(graphics);
            inputHandler.spaceButtons(graphics, buttonFontSize, 1100, 550);
            // Prompt the user to select an action for their Character
            UIManager.setTextColor(graphics, Color.WHITE);
            UIManager.setFontSize(40);
            UIManager.refreshText(graphics);
            UIManager.drawCenteredStringInBox(graphics, "Select an action to perform.", 0, 650, 1280, 100);
        } else{
            drawScene(scene, graphics);
        }

        dialogManager.draw(graphics);
        if(inputHandler.getButtons().size() > 0 && !dialogManager.getIsActive()){
            inputHandler.draw(graphics);
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
        playerTeam.resetSelectedCharacterIndex();
    }
    // Calls once when panel is unloaded
    public void onExit(GamePanel panel){
        panel.setBackground(new Color(0, 0, 0));
    }
}
