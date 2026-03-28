package src.GameManagement.UI;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

// Stores several Button objects, handle the key inputs needed to select a button
public class InputHandler {
    // Attributes
    private ArrayList<Button> buttons = new ArrayList<>();
    private int selectedIndex = 0;

    public void addButton(Button b){
        buttons.add(b);
    }

    public void clear(){
        buttons.clear();
        selectedIndex = 0;
    }

    // Use the arrow keys to navigate between options,
    // press ENTER to send an option
    public int keyPressed(int keyCode){
        if(keyCode == KeyEvent.VK_LEFT){
            selectedIndex--;
            if(selectedIndex < 0){
                selectedIndex = buttons.size() - 1;
            }
        }
        if(keyCode == KeyEvent.VK_RIGHT){
            selectedIndex++;
            if(selectedIndex >= buttons.size()){
                selectedIndex = 0;
            }
        }
        if(keyCode == KeyEvent.VK_ENTER){
            return buttons.get(selectedIndex).getID();
        }
        return -1;
    }

    public void draw(Graphics g, int fontSize){
        for(int i = 0; i < buttons.size(); i++){
            buttons.get(i).draw(g, i==selectedIndex, fontSize);
        }
    }

    // Space all buttons evenly on the same vertical axis.
    // Calculated by obtaining the bounds of all buttons
    public void spaceButtons(Graphics g, int fontSize, int width, int y){
        if(buttons.size() <= 1){
            return;
        }
        // Obtain total width of all buttons
        int totalButtonWidth = 0;
        for(Button button : buttons){
            int[] bounds = button.getBounds(g, fontSize);
            totalButtonWidth += bounds[0];
        }
        int spacing = (width - totalButtonWidth) / (buttons.size() - 1);
        int totalSpacing = spacing * (buttons.size() - 1);
        int totalWidth = totalButtonWidth + totalSpacing;
        int startX = (1280 - totalWidth) / 2;
        for(Button button : buttons){
            button.setPosition(startX, y);
            startX += button.getBounds(g, fontSize)[0] + spacing;
        }
    }
}
