package src.GameManagement.GameState;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;

import javax.swing.*;

import src.Characters.PlayerCharacters.Archer;
import src.Characters.PlayerCharacters.Knight;
import src.Characters.PlayerCharacters.Wizard;
import src.GameManagement.Game;
import src.GameManagement.GameData;
import src.GameManagement.Mechanics.DayManager;
import src.GameManagement.UI.Button;
import src.GameManagement.UI.DialogManager;
import src.GameManagement.UI.GamePanel;
import src.GameManagement.UI.InputHandler;
import src.GameManagement.UI.UIManager;
import src.Teams.PlayerTeam;

// Prompts the user to select Character classes and create names for their Characters
// This state is only called if the user is able to make one or more new Characters
public class CharacterSelectState extends GameState{
    // Attributes
    private JLabel title;
    private int allowedCharacters;
    private int createdCharacters = 0;
    private String selectedClass = "";

    // Constants to keep track of steps
    private final int SCREEN_INFO = 0;
    private final int SELECT_CLASS = 1;
    private final int CONFIRM_CLASS = 2;
    private final int NAME_CHARACTER = 3;
    private final int CREATION_SUCCESS = 4;

    // Define the buttons used to select Character class
    private final int KNIGHT = 0;
    private final int ARCHER = 1;
    private final int WIZARD = 2;

    // Define the buttons used to confirm Character choice
    private final int YES = 0;
    private final int NO = 1;

    // Constructor
    // Fill the dialog and define allowedCharacters here
    public CharacterSelectState(Game g, DayManager dayManager){
        super(g, dayManager);
        GameData data = g.getGameData();
        allowedCharacters = data.getPlayerBattleCapacity() - data.getPlayerTeamArr().size();
        if(allowedCharacters > 1){
            dialogManager.add("You may add " + allowedCharacters + " new Characters to your team!");
        } else{
            dialogManager.add("You may add 1 new Character to your team!");
        }
    }

    // Update dialog after every time ENTER is pressed
    protected void handleStep(int step, int keyCode){
        // Display dialogManager when state is first loaded
        if(step == SCREEN_INFO){
            if(dialogManager.getIsActive()){
                if(keyCode == KeyEvent.VK_ENTER){
                    dialogManager.nextLine();
                    if(!dialogManager.getIsActive()){
                        initInputHandlerCharClass();
                        nextStep();
                    }
                }
            }
        }
        else if(step == SELECT_CLASS){
            // Process the Character class selected
            int input = inputHandler.keyPressed(keyCode);
            if(input == -1){
                return;
            }
            if(input == KNIGHT){
                selectedClass = "Knight";
            }
            else if(input == ARCHER){
                selectedClass = "Archer";
            } else if(input == WIZARD){
                selectedClass = "Wizard";
            }
            if(!selectedClass.isEmpty()){
                initInputHandlerConfirmation();
                nextStep();
            }
        } else if(step == CONFIRM_CLASS){
            // Process the confirmation choice selected
            int input = inputHandler.keyPressed(keyCode);
            if(input == -1){
                return;
            }
            if(input == YES){
                isTyping = true;
                typedText = "";
                nextStep();
            } else if(input == NO){
                selectedClass = "";
                initInputHandlerCharClass();
                setStep(SELECT_CLASS);
            }
        } else if(step == NAME_CHARACTER){
            // Detect an ENTER press (Make sure the name isn't empty)
            if(keyCode == KeyEvent.VK_ENTER && !typedText.isEmpty()){
                isTyping = false;
            }
            // Wait for the user to press ENTER, which disables typing
            if(!isTyping){
                // Add the new Character to the PlayerTeam
                PlayerTeam playerTeam = game.getGameData().getPlayerTeamObj();
                if(selectedClass.equals("Knight")){
                    playerTeam.addCharacter(new Knight(typedText));
                } else if(selectedClass.equals("Archer")){
                    playerTeam.addCharacter(new Archer(typedText));
                } else{
                    playerTeam.addCharacter(new Wizard(typedText));
                }
                createdCharacters++;
                dialogManager.clear();
                dialogManager.add("Congratulations! " + typedText + " was added to your team.");
                nextStep();
            }
        } else if(step == CREATION_SUCCESS){
            // Reuse dialogManager to display congratulations message
            if(dialogManager.getIsActive()){
                if(keyCode == KeyEvent.VK_ENTER){
                    dialogManager.nextLine();
                    if(!dialogManager.getIsActive()){
                        if(createdCharacters == allowedCharacters){
                            dayManager.nextPhase();
                        } else{
                            initInputHandlerCharClass();
                            setStep(SELECT_CLASS);
                        }
                    }
                }
            }
        }
    }

    protected void drawStep(int step, Graphics graphics){
        if(step > SCREEN_INFO){
            // Inform the user how many more Characters they can make
            UIManager.setTextColor(graphics, Color.WHITE);
            UIManager.setFontSize(32);
            UIManager.refreshText(graphics);
            UIManager.drawCenteredStringInBox(graphics, "Remaining Characters: " + (allowedCharacters-createdCharacters), 0, 100, 1280, 100);
        }
        if(step == SCREEN_INFO || step == CREATION_SUCCESS){
            // Draw dialog
            dialogManager.draw(graphics);
        }
        else if(step == SELECT_CLASS){
            // Prompt the user to select a Character class
            UIManager.setTextColor(graphics, Color.WHITE);
            UIManager.setFontSize(40);
            UIManager.refreshText(graphics);
            UIManager.drawCenteredStringInBox(graphics, "Select a Character class.", 0, 650, 1280, 100);
            // Draw inputHandler buttons
            inputHandler.spaceButtons(graphics, 40, 800, 450);
            inputHandler.draw(graphics, 40);
        } else if(step == CONFIRM_CLASS){
            // Prompt the user to confirm their choice
            UIManager.setTextColor(graphics, Color.WHITE);
            UIManager.setFontSize(40);
            UIManager.refreshText(graphics);
            UIManager.drawCenteredStringInBox(graphics, "Are you sure you want to select " + selectedClass + "?", 0, 650, 1280, 100);
            // Draw inputHandler buttons
            inputHandler.spaceButtons(graphics, 40, 800, 575);
            inputHandler.draw(graphics, 40);
            // Draw a box containing relevant info for the Character class
            graphics.setColor(new Color(60, 60, 60));
            graphics.fillRect(390, 140, 720, 420);
            graphics.setColor(new Color(117, 97, 70));
            graphics.fillRect(400, 150, 700, 400);
        } else if(step == NAME_CHARACTER){
            // Prompt the user to create a name for their Character
            UIManager.setTextColor(graphics, Color.WHITE);
            UIManager.setFontSize(40);
            UIManager.refreshText(graphics);
            UIManager.drawCenteredStringInBox(graphics, "Provide a name for your " + selectedClass + ".", 0, 200, 1280, 100);
            // Draw an empty dialog box, where the user will type their name
            dialogManager.drawDialogBox(graphics);
            UIManager.setTextColor(graphics, Color.WHITE);
            UIManager.setFontSize(32);
            UIManager.refreshText(graphics);
            UIManager.drawCenteredStringInBox(graphics, typedText + "_", 0, 500, 1280, 220);
            // Display the Press ENTER to confirm message
            UIManager.setTextColor(graphics, Color.GRAY);
            UIManager.setFontSize(25);
            UIManager.refreshText(graphics);
            UIManager.drawCenteredStringInBox(graphics, "(Press ENTER to confirm)", 0, 680, 1280, 20);
        }
    }

    // Calls once when panel is first loaded
    public void onEnter(GamePanel panel){
        panel.setBackground(new Color(60, 67, 84));
        // Setup title
        title = UIManager.createCenteredLabel("Character Selection", 0, 0, 1280, 100);
        title.setFont(UIManager.getFont(60));
        panel.add(title);
        panel.repaint();
        initInputHandlerCharClass();
    }
    // Calls once when panel is unloaded
    public void onExit(GamePanel panel){
        panel.setBackground(new Color(0, 0, 0));
        panel.remove(title);
    }

    // Reset the inputHandler to include buttons for selecting Character class
    private void initInputHandlerCharClass(){
        // Setup inputHandler for Character classes
        inputHandler = createOptions(new String[]{"Knight", "Archer", "Wizard"}, new int[]{KNIGHT, ARCHER, WIZARD});
    }

    // Reset the inputHandler to include buttons for selecting confirmtaion
    private void initInputHandlerConfirmation(){
        // Setup inputHandler for YES / NO
        inputHandler = createOptions(new String[]{"YES", "NO"}, new int[]{YES, NO});
    }
}
