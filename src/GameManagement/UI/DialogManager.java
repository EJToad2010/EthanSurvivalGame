package src.GameManagement.UI;
import java.util.ArrayList;

import src.GameManagement.Mechanics.ActionResult;

import java.awt.*;
// A dialog is a series of text displayed in order every time the user presses ENTER
// A dedicated dialog box is created at the bottom of the screen
// Rework DialogManager to use an ArrayList of ActionResult instead of String
public class DialogManager {
    // The list of each line in a dialog sequence
    // Use ActionResult object so that classes can flexibly implement multiple messages and signals
    private ActionResult dialogSequence;
    private int dialogIndex = 0;
    // false if dialog has not been completed yet, true if it has
    private boolean isActive;

    // Constructors
    public DialogManager(){
        dialogSequence = new ActionResult();
        isActive = false;
    }

    // Getters
    public boolean getIsActive(){
        return isActive;
    }

    public ActionResult getDialogSequence(){
        return dialogSequence;
    }

    public int getDialogIndex(){
        return dialogIndex;
    }

    public int size(){
        return dialogSequence.getMessages().size();
    }

    // Return the message of the current dialogIndex
    // Return blank if none found
    public String getMessage(){
        return dialogSequence.getMessage(dialogIndex);
    }

    // Return the signal of the current dialogIndex
    // Return blank if none found
    public String getSignal(){
        return dialogSequence.getSignal(dialogIndex);
    }

    // Return the amount of the current dialogIndex
    // Return 0 if none found
    public double getAmount(){
        return dialogSequence.getAmount(dialogIndex);
    }

    public void setIsActive(boolean isActive){
        this.isActive = isActive;
    }

    // Manipulating elements
    public void add(String line){
        dialogSequence.add(line);
        isActive = true;
    }

    public void add(String line, String signal){
        dialogSequence.add(line, signal);
        isActive = true;
    }

    public void add(String line, String signal, double amount){
        dialogSequence.add(line, signal, amount);
        isActive = true;
    }

    // Since an ActionResult can contain multiple messages and signals, add each one
    // Messages and signals must have the same length to work
    public void add(ActionResult actionResult){
        ArrayList<String> amessages = actionResult.getMessages();
        ArrayList<String> asignals = actionResult.getSignals();
        ArrayList<Double> aamounts = actionResult.getAmounts();
        for(int i = 0; i < amessages.size(); i++){
            dialogSequence.add(amessages.get(i), asignals.get(i), aamounts.get(i));
        }
        isActive = true;
    }

    public void clear(){
        dialogSequence.clear();
        dialogIndex = 0;
        isActive = false;
    }

    // Advance dialogIndex
    public void nextLine(){
        if(!isActive){
            return;
        }
        dialogIndex++;
        if(dialogIndex >= dialogSequence.getMessages().size()){
            isActive = false;
        }
    }

    // Draw the dialog box
    public void drawDialogBox(Graphics graphics){
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 490, 1280, 220);
        graphics.setColor(Color.BLACK);
        graphics.fillRect(0, 500, 1280, 220);
    }

    public void draw(Graphics graphics){
        if(!isActive || dialogSequence.getMessages().size() == 0){
            return;
        }
        drawDialogBox(graphics);
        UIManager.setTextColor(graphics, Color.WHITE);
        UIManager.setFontSize(32);
        UIManager.refreshText(graphics);
        UIManager.drawCenteredStringInBox(graphics, dialogSequence.getMessage(dialogIndex), 0, 500, 1280, 220);
        // Display Press ENTER to continue
        UIManager.setTextColor(graphics, Color.GRAY);
        UIManager.setFontSize(25);
        UIManager.refreshText(graphics);
        UIManager.drawCenteredStringInBox(graphics, "(Press ENTER to continue)", 0, 680, 1280, 20);
    }
}
