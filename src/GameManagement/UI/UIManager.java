package src.GameManagement.UI;

import javax.swing.*;
import java.awt.*;

// A static class that handles common tasks related to UI Text display
public class UIManager {
    // Store the Font to use across all text
    private static Font font = new Font("Dialog", Font.PLAIN, 24);

    public static Font getFont(){
        return font;
    }

    public static Font getFont(int fontSize){
        return new Font(font.getName(), font.getStyle(), fontSize);
    }
    public static void setFontSize(int fontSize){
        font = new Font(font.getName(), font.getStyle(), fontSize);
    }

    // Methods that use the Graphics class
    public static void setTextColor(Graphics graphics, Color color){
        graphics.setColor(color);
    }
    public static void refreshText(Graphics graphics){
        graphics.setFont(font);
    }
    public static void drawString(Graphics graphics, String text, int x, int y){
        graphics.drawString(text, x, y);
    }
    public static void drawCenteredStringInBox(Graphics graphics, String text, int x, int y, int width, int height){
        FontMetrics fontMetrics = graphics.getFontMetrics(font);
        int textWidth = fontMetrics.stringWidth(text);
        int textHeight = fontMetrics.getHeight();

        int drawX = x + (width-textWidth) / 2;
        int drawY = y + (height - textHeight) / 2;
        
        graphics.drawString(text, drawX, drawY);
    }

    // Methods that use JLabels
    public static JLabel createCenteredLabel(String text, int x, int y, int width, int height) {
        JLabel label = new JLabel(text);
        label.setForeground(Color.WHITE);
        label.setLocation(x, y);
        label.setSize(width, height);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        return label;
    }
    public static JLabel createBoxLabel(String text, int x, int y, int width, int height) {
        JLabel label = new JLabel(text);
        label.setBounds(x, y, width, height);
        label.setOpaque(true);
        label.setBackground(Color.GRAY);
        label.setForeground(Color.RED);
        label.setHorizontalAlignment(SwingConstants.CENTER);

        return label;
    }
}
