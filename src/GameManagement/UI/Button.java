package src.GameManagement.UI;
import java.awt.*;
import java.awt.geom.*;
import src.GameManagement.UI.UIManager;

// A label with a text box that represents an option that can be selected during gameplay
// Each button has a nuumerical "ID" value that is handled by the InputManager to distinguish different objects
public class Button {
    // Attributes
    private String text;
    private int x;
    private int y;
    private int id;

    // Constants
    private final int HORIZONTAL_GAP = 20;
    private final int VERTICAL_GAP = 10;

    // Constructor requires all parameters
    public Button(String text, int x, int y, int id){
        this.text = text;
        this.x = x;
        this.y = y;
        this.id = id;
    }

    public void setPosition(int x, int y){
        this.x = x;
        this.y = y;
    }

    // If selected, button has a yellow background instead of white
    public void draw(Graphics graphics, boolean selected, int fontSize){
        // Get FontMetrics
        UIManager.setFontSize(fontSize);
        UIManager.refreshText(graphics);
        FontMetrics fontMetrics = graphics.getFontMetrics(UIManager.getFont());
        // Obtain the width and height of the text
        int textWidth = fontMetrics.stringWidth(text);
        int textHeight = fontMetrics.getHeight();

        // Draw the box around the button
        Color bgColor;
        if(selected){
            bgColor = Color.YELLOW;
        } else{
            bgColor = Color.WHITE;
        }
        graphics.setColor(bgColor);
        graphics.fillRoundRect(x-HORIZONTAL_GAP, y-VERTICAL_GAP, textWidth+(HORIZONTAL_GAP*2), textHeight+(VERTICAL_GAP*2), fontSize, fontSize);

        // Draw the text
        UIManager.setTextColor(graphics, Color.BLACK);
        UIManager.refreshText(graphics);
        UIManager.drawCenteredStringInBox(graphics, text, x, y+fontMetrics.getAscent(), textWidth, textHeight);
    }

    public int getID(){
        return id;
    }
}
