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
    private boolean isSelectable = true;

    // Constants
    private final int HORIZONTAL_GAP = 20;
    private final int VERTICAL_GAP = 10;

    // Constructor requires all parameters except isSelectable, which is true by default
    public Button(String text, int x, int y, int id){
        this.text = text;
        this.x = x;
        this.y = y;
        this.id = id;
    }

    public Button(String text, int x, int y, int id, boolean isSelectable){
        this.text = text;
        this.x = x;
        this.y = y;
        this.id = id;
        this.isSelectable = isSelectable;
    }

    public boolean getIsSelectable(){
        return isSelectable;
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
        // Draw the box around the button
        Color bgColor;
        if(selected){
            bgColor = Color.YELLOW;
        } else{
            bgColor = Color.WHITE;
        }
        if(!isSelectable){
            bgColor = Color.GRAY;
        }
        graphics.setColor(bgColor);
        int[] cornerCoords = getCornerCoords(graphics, fontSize);
        graphics.fillRoundRect(cornerCoords[0], cornerCoords[1], cornerCoords[2], cornerCoords[3], fontSize, fontSize);

        // Draw the text
        UIManager.setTextColor(graphics, Color.BLACK);
        UIManager.refreshText(graphics);
        int[] bounds = getBounds(graphics, fontSize);
        UIManager.drawCenteredStringInBox(graphics, text, x, y+fontMetrics.getAscent(), bounds[0], bounds[1]);
    }

    // Return the width and height of a button
    public int[] getBounds(Graphics graphics, int fontSize){
        UIManager.setFontSize(fontSize);
        FontMetrics fontMetrics = graphics.getFontMetrics(UIManager.getFont());
        int textWidth = fontMetrics.stringWidth(text);
        int textHeight = fontMetrics.getHeight();
        return new int[]{textWidth, textHeight};
    }

    // Return the coordinates of the top left corner and bottom right corner of a button
    // [x1, y1, x2, y2]
    public int[] getCornerCoords(Graphics graphics, int fontSize){
        int[] bounds = getBounds(graphics, fontSize);
        return new int[]{x-HORIZONTAL_GAP, y-VERTICAL_GAP, bounds[0] + (HORIZONTAL_GAP * 2), bounds[1]+(VERTICAL_GAP * 2)};
    }

    public int getID(){
        return id;
    }
}
