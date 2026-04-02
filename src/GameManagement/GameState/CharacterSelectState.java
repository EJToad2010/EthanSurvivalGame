package src.GameManagement.GameState;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.KeyEvent;

import javax.swing.*;

import src.Characters.PlayerCharacters.Archer;
import src.Characters.PlayerCharacters.Knight;
import src.Characters.PlayerCharacters.Wizard;
import src.GameManagement.Game;
import src.GameManagement.GameData;
import src.GameManagement.Mechanics.DayManager;
import src.GameManagement.UI.GamePanel;
import src.GameManagement.UI.ImageManager;
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
    private Image knight;
    private Image archer;
    private Image wizard;
    private Image selectedImage;

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
    }

    // Handle the flow of logic between steps
    protected void handleStep(int step, int keyCode){
        // Display dialogManager when state is first loaded
        if(step == SCREEN_INFO){
            runDialog(keyCode, true);
        }
        else if(step == SELECT_CLASS){
            // Process the Character class selected
            int input = inputHandler.keyPressed(keyCode);
            if(input == -1){
                return;
            }
            // Update selectedClass to reflect user's choice
            if(input == KNIGHT){
                selectedClass = "Knight";
            }
            else if(input == ARCHER){
                selectedClass = "Archer";
            } else if(input == WIZARD){
                selectedClass = "Wizard";
            }
            nextStep();
        } else if(step == CONFIRM_CLASS){
            // Process the confirmation choice selected
            int input = inputHandler.keyPressed(keyCode);
            if(input == -1){
                return;
            }
            if(input == YES){
                nextStep();
            } else if(input == NO){
                setStep(SELECT_CLASS);
            }
        } else if(step == NAME_CHARACTER){
            // Detect an ENTER press (Make sure the name isn't empty)
            if(keyCode == KeyEvent.VK_ENTER && !typedText.isEmpty()){
                isTyping = false;
            }
            // Wait for the user to press ENTER, which disables typing
            if(!isTyping){             
                nextStep();
            }
        } else if(step == CREATION_SUCCESS){
            // Reuse dialogManager to display congratulations message
            int exitStatus = runDialog(keyCode, false);
            if(exitStatus == 0){
                if(createdCharacters == allowedCharacters){
                    dayManager.nextPhase();
                } else{
                    setStep(SELECT_CLASS);
                }
            }
        }
    }

    // Calls once when a new step is first loaded
    protected void onEnterStep(int step){
        if(step == SCREEN_INFO){
            // Figure out how many new Characters can be made
            GameData data = game.getGameData();
            allowedCharacters = data.getPlayerBattleCapacity() - data.getPlayerTeamArr().size();
            if(allowedCharacters > 1){
                dialogManager.add("You may add " + allowedCharacters + " new Characters to your team!");
            } else{
                dialogManager.add("You may add 1 new Character to your team!");
            }
        }
        else if(step == SELECT_CLASS){
            // Initialize the buttons used to select a character class
            initInputHandlerCharClass();
            selectedClass = "";
        } else if(step == CONFIRM_CLASS){
            // Initialize the buttons used to select YES or NO
            initInputHandlerConfirmation();
        } else if(step == NAME_CHARACTER){
            // Reset typing vars
            isTyping = true;
            typedText = "";
        }
    }

    // Calls once when the previous step exits
    protected void onExitStep(int step){
        if(step == SELECT_CLASS){
            // Determine the selectedImage
            if(selectedClass.equals("Knight")){
                selectedImage = knight;
            } else if(selectedClass.equals("Archer")){
                selectedImage = archer;
            } else{
                selectedImage = wizard;
            }
        } else if(step == NAME_CHARACTER){
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
            // Prepare the dialog for the next step
            dialogManager.clear();
            dialogManager.add("Congratulations! " + typedText + " was added to your team.");
            typedText = "";
        }
    }

    // Draw relevant information of a single step
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
        if(step == SELECT_CLASS){
            // Prompt the user to select a Character class
            UIManager.setTextColor(graphics, Color.WHITE);
            UIManager.setFontSize(40);
            UIManager.refreshText(graphics);
            UIManager.drawCenteredStringInBox(graphics, "Select a Character class.", 0, 650, 1280, 100);
            // Draw inputHandler buttons
            inputHandler.spaceButtons(graphics, 40, 800, 450);
            inputHandler.draw(graphics, 40);
            // Draw each corresponding Character class above buttons
            int[] buttonsX = inputHandler.getButtonsX();
            int[] buttonsY = inputHandler.getButtonsY();
            graphics.drawImage(knight, buttonsX[KNIGHT], buttonsY[KNIGHT] - 180, null);
            graphics.drawImage(archer, buttonsX[ARCHER], buttonsY[ARCHER] - 180, null);
            graphics.drawImage(wizard, buttonsX[WIZARD], buttonsY[WIZARD] - 180, null);
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
            // Draw the selected Character class on the left side of the screen
            int leftX = inputHandler.getButtonsX()[0];
            graphics.drawImage(selectedImage, leftX-40, 260, null);
        } else if(step == NAME_CHARACTER){
            dialogManager.drawDialogBox(graphics);
            // Prompt the user to create a name for their Character
            UIManager.setTextColor(graphics, Color.WHITE);
            UIManager.setFontSize(40);
            UIManager.refreshText(graphics);
            UIManager.drawCenteredStringInBox(graphics, "Provide a name for your " + selectedClass + ".", 0, 200, 1280, 100);
            graphics.drawImage(selectedImage, 1280/2 - 80, 260, null);
            UIManager.setTextColor(graphics, Color.WHITE);
            UIManager.setFontSize(32);
            UIManager.refreshText(graphics);
            UIManager.drawCenteredStringInBox(graphics, typedText + "_", 0, 500, 1280, 220);
            // Display the Press ENTER to confirm message
            UIManager.setTextColor(graphics, Color.GRAY);
            UIManager.setFontSize(25);
            UIManager.refreshText(graphics);
            UIManager.drawCenteredStringInBox(graphics, "(Press ENTER to confirm)", 0, 680, 1280, 20);
        } else if(step == CREATION_SUCCESS){
            graphics.drawImage(selectedImage, 1280/2 - 80, 260, null);
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
        // Setup Character images
        knight = ImageManager.loadImage("src/Images/knight.png");
        archer = ImageManager.loadImage("src/Images/archer.png");
        wizard = ImageManager.loadImage("src/Images/wizard.png");
        onEnterStep(SCREEN_INFO);
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
