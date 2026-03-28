package src.GameManagement.UI;
import java.util.ArrayList;
import java.awt.*;
// A dialog is a series of text displayed in order every time the user presses ENTER
// A dedicated dialog box is created at the bottom of the screen
public class DialogManager {
    // The list of each line in a dialog sequence
    private ArrayList<String> dialogSequence;
    private int dialogIndex = 0;
    // false if dialog has not been completed yet, true if it has
    private boolean isActive;

    // Constructors
    public DialogManager(ArrayList<String> dialogSequence){
        this.dialogSequence = dialogSequence;
        isActive = true;
    }
    public DialogManager(){
        dialogSequence = new ArrayList<String>();
        isActive = false;
    }

    public boolean getIsActive(){
        return isActive;
    }

    public void add(String line){
        dialogSequence.add(line);
        isActive = true;
    }

    // Advance dialogIndex
    public void nextLine(){
        if(!isActive){
            return;
        }
        dialogIndex++;
        if(dialogIndex >= dialogSequence.size()){
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
        if(!isActive){
            return;
        }
        drawDialogBox(graphics);
        UIManager.setTextColor(graphics, Color.WHITE);
        UIManager.setFontSize(32);
        UIManager.refreshText(graphics);
        UIManager.drawCenteredStringInBox(graphics, dialogSequence.get(dialogIndex), 0, 500, 1280, 220);
        // Display Press ENTER to continue
        if(dialogIndex == 0){
            UIManager.setTextColor(graphics, Color.GRAY);
            UIManager.setFontSize(25);
            UIManager.refreshText(graphics);
            UIManager.drawCenteredStringInBox(graphics, "(Press ENTER to continue)", 0, 680, 1280, 20);
        }
    }
}
