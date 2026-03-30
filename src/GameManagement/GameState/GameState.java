package src.GameManagement.GameState;
// Import graphics and event handling libraries

import src.GameManagement.Game;
import src.GameManagement.Mechanics.DayManager;
import src.GameManagement.UI.GamePanel;

import java.awt.*;

// A singular class that can be called to execute a specific "phase" of the game
// Contains both the logic and graphics of a phase
// Each GameState has support for smaller "steps," which change the flow of logic without changing the entire screen
public class GameState {
    // Store a reference to the running Game instance
    protected Game game;
    protected DayManager dayManager;

    // Store an integer value of the current "step"
    protected int currentStep = 0;

    // Used to handle typing
    protected boolean isTyping = false;
    protected String typedText = "";
    
    // Constructor
    public GameState(Game game, DayManager dayManager){
        this.game = game;
        this.dayManager = dayManager;
    }

    // Both of these methods should call once per frame but they handle different tasks
    // Handles all logic of a panel
    public void update(){}
    // Handles all graphics of a panel
    // Redirect to drawStep()
    public void draw(Graphics graphics){
        drawStep(currentStep, graphics);
    }

    // Move to the next stpe
    protected void nextStep(){
        currentStep++;
    }

    // Set a specific step
    protected void setStep(int newStep){
        currentStep = newStep;
    }

    // Subclasses must override these
    protected void handleStep(int step, int keyCode){}
    protected void drawStep(int step, Graphics graphics){}
    // Calls once when panel is first loaded
    public void onEnter(GamePanel panel){}
    // Calls once when panel is unloaded
    public void onExit(GamePanel panel){}

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
        if(Character.isLetterOrDigit(c) || c == ' '){
            typedText += c;
        }
        // Backspace removes the last letter
        if(c == '\b'){
            if(typedText.length() > 0){
                typedText = typedText.substring(0, typedText.length() - 1);
            }
        }
    }
}
