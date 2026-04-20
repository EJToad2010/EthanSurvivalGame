package src.GameManagement.GameState;
// Import graphics and event handling libraries

import src.Characters.BasicCharacter;
import src.GameManagement.Game;
import src.GameManagement.Mechanics.DayManager;
import src.GameManagement.UI.DialogManager;
import src.GameManagement.UI.GamePanel;
import src.GameManagement.UI.InputHandler;
import src.GameManagement.UI.Button;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

// A singular class that can be called to execute a specific "phase" of the game
// Contains both the logic and graphics of a phase
// Each GameState has support for smaller "steps," which change the flow of logic without changing the entire screen
public class GameState {
    // Store a reference to the running Game instance
    protected Game game;
    protected DayManager dayManager;
    // Store a DialogManager object that can be used by subclasses
    protected DialogManager dialogManager = new DialogManager();
    // Store a boolean that catches if the dialog is throwing out a signal or not
    protected boolean isHandlingSignal = false;
    // Each dialog message can only throw a signal once
    protected boolean isOnSignalCooldown = false;
    // Store an InputHandler object that can be used by subclasses
    protected InputHandler inputHandler = new InputHandler();

    // Store an integer value of the current "step"
    protected int currentStep = 0;

    // Variables related to animations in a panel
    // Each step can have multiple unique scenes, which can have multiple unique frames.
    // The animation tick controls the flow of animation
    protected int animationTick = 0;
    protected boolean isAnimating = false;
    protected int scene = 0;
    protected int frame = 0;

    // Used to handle typing
    protected boolean isTyping = false;
    protected String typedText = "";
    
    // Constructor
    public GameState(Game game, DayManager dayManager){
        this.game = game;
        this.dayManager = dayManager;
    }

    // Getters
    public DialogManager getDialogManager(){
        return dialogManager;
    }

    public InputHandler getInputHandler(){
        return inputHandler;
    }

    public boolean getIsHandlingSignal(){
        return isHandlingSignal;
    }

    // Both of these methods should call once per frame but they handle different tasks
    // Handles all logic of a panel
    public void update(){}
    // Handles all graphics of a panel
    // Redirect to drawStep()
    public void draw(Graphics graphics){
        drawStep(currentStep, graphics);
        if(isHandlingSignal){
            drawSignal(dialogManager.getSignal(),graphics);
        }
    }

    // Move to the next stpe
    protected void nextStep(){
        onExitStep(currentStep);
        onEnterStep(currentStep+1);
        currentStep++;
    }

    // Increment the animationTick
    protected void nextTick(){
        if(isAnimating){
            animationTick++;
        }
    }

    // Set animationTick to 0
    protected void resetTick(){
        animationTick = 0;
    }

    // Increment the frame number
    protected void nextFrame(){
        if(isAnimating){
            onExitFrame(scene, frame);
            frame++;
            onEnterFrame(scene, frame);
            resetTick();
        }
    }

    // Set frame to 0
    protected void resetFrame(){
        onExitFrame(scene, frame);
        frame = 0;
        onEnterFrame(scene, 0);
        resetTick();
    }

    // Set frame to a given number
    protected void setFrame(int frame){
        onExitFrame(scene, this.frame);
        this.frame = frame;
        onEnterFrame(scene, this.frame);
    }

    // Increment the animation scene
    protected void nextScene(){
        if(isAnimating){
            onExitScene(scene);
            scene++;
            onEnterScene(scene);
            resetFrame();
            resetTick();
        }
    }

    // Set scene to 0
    protected void resetScene(){
        onExitScene(scene);
        scene = 0;
        onEnterScene(scene);
        resetFrame();
        resetTick();
    }
    
    // Set a specific step
    protected void setStep(int newStep){
        onExitStep(currentStep);
        onEnterStep(newStep);
        currentStep = newStep;
    }

    // Subclasses must override these for unique functionality
    // Handle the flow of logic between steps
    protected void handleStep(int step, int keyCode){}
    // Draw relevant information of a single step
    protected void drawStep(int step, Graphics graphics){}
    // Used when a GameState makes use of dialog that contains interaction
    // (Remember to set isHandlingSignal to FALSE when done handling)
    protected void handleSignal(String signal, double amount){}
    // Graphical content of a GameState's signal
    // (Remember to set isHandlingSignal to FALSE when done handling)
    protected void drawSignal(String signal, Graphics graphics){}
    // Calls once when a new step is first loaded
    protected void onEnterStep(int step){}
    // Calls once when the previous step exits
    protected void onExitStep(int step){}
    // Calls once when a new scene is first loaded
    protected void onEnterScene(int scene){}
    // Calls once when the previous scene is unloaded
    protected void onExitScene(int scene){}
    // Calls once when a new frame is first loaded
    protected void onEnterFrame(int scene, int frame){}
    // Calls once when the previous frame is unloaded
    protected void onExitFrame(int scene, int frame){}
    // Calls once when panel is first loaded
    public void onEnter(GamePanel panel){}
    // Calls once when panel is unloaded
    public void onExit(GamePanel panel){}
    // Call in drawStep if a panel wants to make use of animations
    protected void drawScene(int scene, Graphics graphics){}
    // Call in drawScene to make use of individual frames
    protected void drawFrame(int scene, int frame, Graphics graphics){}

    // Call every time a key is pressed
    public void keyPressed(int keyCode){
        handleStep(currentStep, keyCode);
    }

    // Call every time a key is tyoed
    public void keyTyped(char c){
        if(!isTyping){
            return;
        }
        // Only allow letters, numbers, or SPACE
        // Character limit of 20
        if((Character.isLetterOrDigit(c) || c == ' ') && typedText.length() <20){
            typedText += c;
        }
        // Backspace removes the last letter
        if(c == '\b'){
            if(typedText.length() > 0){
                typedText = typedText.substring(0, typedText.length() - 1);
            }
        }
    }

    public void runDialogSignalChecks(){
        if(!dialogManager.getIsActive()){
            return;
        }
        if(isHandlingSignal){
            handleSignal(dialogManager.getSignal(), dialogManager.getAmount());
            return;
        }
        if(!dialogManager.getSignal().equals("") && !isHandlingSignal && !isOnSignalCooldown){
            System.out.println("Handling signal " +dialogManager.getSignal());
            isHandlingSignal = true;
            handleSignal(dialogManager.getSignal(), dialogManager.getAmount());
        }
    }

    public int runEmptyDialogCheck(){
        if(!dialogManager.getIsActive()){
            return -1;
        }
        if(isHandlingSignal){
            return -1;
        }
        if(dialogManager.getMessage().equals("")){
            System.out.println("Empty dialog skipped");
            dialogManager.nextLine();
            isOnSignalCooldown = false;
        }
        if(!dialogManager.getIsActive()){
            System.out.println("Dialog concluded");
            isHandlingSignal = false;
            return 0;
        }
        return -1;
    }

    // Automatically process every dialog line, moving on every time the user presses ENTER
    // If the end of the dialog is reached, moveOn dictates whether or not it should move to the next step
    // Return 0 if end was reached, -1 if not
    // (Call inside handleStep to access keyCode)
    public int runDialog(int keyCode, boolean moveOn){
        if(dialogManager.size() < 1){
            return -1;
        }
        if(!dialogManager.getIsActive()){
            return -1;
        }
        if(!(keyCode == KeyEvent.VK_ENTER) && !dialogManager.getMessage().equals("")){
            return -1;
        }
        dialogManager.nextLine();
        isOnSignalCooldown = false;
        if(dialogManager.getIsActive()){
            return -1;
        }
        // Dialog finished
        if(moveOn){
            nextStep();
        }
        System.out.println("Dialog concluded");
        isHandlingSignal = false;
        return 0;
    }

    // Automatically create an InputHandler containing the given labels for each Button
    public InputHandler createOptions(String[] options){
        InputHandler output = new InputHandler();
        for(int i = 0; i < options.length; i++){
            output.addButton(new Button(options[i], 0, 0, i));
        }
        return output;
    }

    // Automatically create an InputHandler containing the given labels for each Button, and given ids for each
    // options and ids must be the same length
    public InputHandler createOptions(String[] options, int[] ids){
        InputHandler output = new InputHandler();
        for(int i = 0; i < options.length; i++){
            output.addButton(new Button(options[i], 0, 0, ids[i]));
        }
        return output;
    }

    // Automatically create an InputHandler containing an option for each Character in a given team
    public InputHandler createCharacterOptions(ArrayList<? extends BasicCharacter> team){
        InputHandler output = new InputHandler();
        for(int i = 0; i < team.size(); i++){
            BasicCharacter character = team.get(i);
            Button characterButton = new Button(character.getName(), 0, 0, i);
            output.addButton(characterButton);
        }
        return output;
    }

    // Automatically create an InputHandler containing an option for each Character in a given team
    // Keep in mind that some Characters cannot be selected due to various reasons
    public InputHandler createCharacterOptions(ArrayList<? extends BasicCharacter> team, ArrayList<? extends BasicCharacter> unavailableCharacters){
        InputHandler output = new InputHandler();
        for(int i = 0; i < team.size(); i++){
            BasicCharacter character = team.get(i);
            Button characterButton;
            boolean isUnavailable = false;
            for(BasicCharacter unavailable : unavailableCharacters){
                if(character == unavailable){
                    isUnavailable = true;
                    break;
                }
            }        
            characterButton = new Button(character.getName(), 0, 0, i, !isUnavailable);
            output.addButton(characterButton);
        }
        return output;
    }
}
