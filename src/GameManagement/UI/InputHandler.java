package src.GameManagement.UI;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

// Stores several Button objects, handle the key inputs needed to select a button
public class InputHandler {
    // Attributes
    private ArrayList<Button> buttons = new ArrayList<>();
    private int selectedIndex = 0;

    // Return the ArrayList of buttons
    public ArrayList<Button> getButtons(){
        return buttons;
    }

    public void addButton(Button b){
        buttons.add(b);
    }

    // Return the x of the left edge of each button
    public int[] getButtonsX(){
        int[] output = new int[buttons.size()];
        for(int i = 0; i < buttons.size(); i++){
            output[i] = buttons.get(i).getX() - buttons.get(i).getHorizontalGap();
        }
        return output;
    }
    // Return the y of the top edge of each button
    public int[] getButtonsY(){
        int[] output = new int[buttons.size()];
        for(int i = 0; i < buttons.size(); i++){
            output[i] = buttons.get(i).getY() - buttons.get(i).getVerticalGap();
        }
        return output;
    }

    public void clear(){
        buttons.clear();
        selectedIndex = 0;
    }

    // Use the arrow keys to navigate between options,
    // press ENTER to send an option
    public int keyPressed(int keyCode){
        if(keyCode == KeyEvent.VK_LEFT || keyCode == KeyEvent.VK_A){
            selectedIndex--;
            if(selectedIndex < 0){
                selectedIndex = buttons.size() - 1;
            }
            while(!buttons.get(selectedIndex).getIsSelectable()){
                selectedIndex--;
                if(selectedIndex < 0){
                    selectedIndex = buttons.size() - 1;
                }
            }
        }
        if(keyCode == KeyEvent.VK_RIGHT || keyCode == KeyEvent.VK_D){
            selectedIndex++;
            if(selectedIndex >= buttons.size()){
                selectedIndex = 0;
            }
            while(!buttons.get(selectedIndex).getIsSelectable()){
                selectedIndex++;
                if(selectedIndex >= buttons.size()){
                    selectedIndex = 0;
                }
            }
        }
        if(keyCode == KeyEvent.VK_ENTER){
            return buttons.get(selectedIndex).getID();
        }
        return -1;
    }

    public void draw(Graphics g){
        for(int i = 0; i < buttons.size(); i++){
            buttons.get(i).draw(g, i==selectedIndex);
        }
    }

    // Space all buttons evenly on the same vertical axis.
    // Calculated by obtaining the bounds of all buttons
    public void spaceButtons(Graphics g, int fontSize, int width, int y){
        if(buttons.isEmpty()){
            return;
        }
        String combinedButtonText = "";
        for(Button button : buttons){
            combinedButtonText += button.getText();
        }

        // Find ideal text size to fit width
        // If entered text size > width, decrease text size until all buttons fit
        int finalFontSize = fontSize;
        while(true){
            if(finalFontSize < 1){
                break;
            }
            FontMetrics fm = g.getFontMetrics(UIManager.getFont(finalFontSize));
            int candidateTextWidth = fm.stringWidth(combinedButtonText);
            if(candidateTextWidth > width * 0.75){
                finalFontSize--;
            } else{
                break;
            }
        }

        // Obtain total width of all buttons
        int totalButtonWidth = 0;
        for(Button button : buttons){
            button.setTextSize(finalFontSize);
            int[] bounds = button.getBounds(g, finalFontSize);
            totalButtonWidth += bounds[0];
        }
        int spacing;
        if(buttons.size() == 1){
            spacing = 0;
        }else{
            spacing = (width - totalButtonWidth) / (buttons.size() - 1);
        }
        int totalSpacing = spacing * (buttons.size() - 1);
        int totalWidth = totalButtonWidth + totalSpacing;
        int startX = (1280 - totalWidth) / 2;

        for(Button button : buttons){
            button.setPosition(startX, y);
            button.setTextSize(finalFontSize);
            startX += button.getBounds(g, finalFontSize)[0] + spacing;
        }
    }
}
