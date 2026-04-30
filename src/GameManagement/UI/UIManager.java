package src.GameManagement.UI;

import java.util.ArrayList;
import javax.swing.*;
import java.awt.*;

// A static class that handles common tasks related to UI Text display
public class UIManager {
    // Store the Font to use across all text
    private static Font font = new Font("Monospaced", Font.PLAIN, 24);

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
    // Draw a piece of text, centered around an invisible rectangle with defined bounds
    public static void drawCenteredStringInBox(Graphics graphics, String text, int x, int y, int width, int height){
        ArrayList<String> lines = wrapText(text, graphics, width);
        int startY = y;

        // Setup fontmetrics
        FontMetrics fontMetrics = graphics.getFontMetrics(font);
        int textHeight = fontMetrics.getHeight();

        // Offset if lines exceed height provided
        if(textHeight * lines.size() > height){
            startY -= textHeight * (lines.size() - 1);
        }
        for(int i = 0; i < lines.size(); i++){
            int textWidth = fontMetrics.stringWidth(lines.get(i));
            int drawX = x + (width-textWidth) / 2;
            int drawY = startY + (height - textHeight) / 2;
            graphics.drawString(lines.get(i), drawX, drawY+i*textHeight);
        }
    }
    // Draw a piece of text with centered width and top aligned y
    public static void drawTopAlignedStringInBox(Graphics graphics, String text, int x, int y, int width, int height){
        ArrayList<String> lines = wrapText(text, graphics, width);
        int startY = y;

        // Setup fontmetrics
        FontMetrics fontMetrics = graphics.getFontMetrics(font);
        int textHeight = fontMetrics.getHeight();

        for(int i = 0; i < lines.size(); i++){
            int textWidth = fontMetrics.stringWidth(lines.get(i));
            int drawX = x + (width-textWidth) / 2;
            graphics.drawString(lines.get(i), drawX, startY+i*textHeight);
        }
    }
    // Draw several lines of text, centered around an invisible rectangle with defined bounds
    public static void drawCeneterdStringInBoxSeparateLines(Graphics graphics, String[] text, int x, int y, int width, int height){
        for(String line : text){
            ArrayList<String> lines = wrapText(line, graphics, width);
            for(int i = 0; i < lines.size(); i++){
                FontMetrics fontMetrics = graphics.getFontMetrics(font);
                int textWidth = fontMetrics.stringWidth(lines.get(i));
                int textHeight = fontMetrics.getHeight();

                int drawX = x + (width-textWidth) / 2;
                int drawY = y + (height - textHeight) / 2;
                graphics.drawString(lines.get(i), drawX, drawY+i*textHeight);
            }
        }
    }
    
    // Wrap text around lines so it doesn't overflow
    public static ArrayList<String> wrapText(String text, Graphics graphics, int width){
        FontMetrics fontMetrics = graphics.getFontMetrics(font);
        ArrayList<String> lines = new ArrayList<String>();
        String[] words = text.split(" ");
        String currentLine = "";
        for(String word : words){
            // Create a temporary testLine that attempts to add another word to currentLine
            String testLine = "";
            if(currentLine.isEmpty()){
                testLine = word;
            } else{
                testLine = currentLine + " " + word;
            }
            // Test width of testLine to see if it overflows
            int textWidth = fontMetrics.stringWidth(testLine);
            if(textWidth > width){
                // Add currentLine to the lines ArrayList
                // Set next currentLine to the word that overflowed
                lines.add(currentLine);
                currentLine = word;
            } else{
                currentLine = testLine;
            }
        }
        // Add last line
        if (!currentLine.isEmpty()) {
            lines.add(currentLine);
        }
        return lines;
    }
    // Find the maximum font size that fits entirely within a defined width
    // If setAuto is true, automatically set the UIManager font to the size calculated
    // If false, revert back to the original size
    // If wrapLines is true, attempt to wrap each individual word on a new line to further maximize font size
    public static int findMaxFontSize(String text, Graphics graphics, int width, int height, boolean setAuto, boolean wrapLines){
        String change;
        int originalSize = font.getSize();
        // If the original size already fits, do nothing
        FontMetrics fm = graphics.getFontMetrics(font);
        String[] words;
        if(wrapLines && text.indexOf(" ") != -1){
            words = text.split(" ");
        } else{
            words = new String[0];
        }
        int checkSize = 1;
        while(checkSize > 0){
            setFontSize(checkSize);
            refreshText(graphics);
            fm = graphics.getFontMetrics(font);
            if(wrapLines && words.length > 0){
                boolean hasExceeded = false;
                for(String word : words){
                    if(fm.stringWidth(word) >= width || fm.getHeight() > height){
                        hasExceeded = true;
                        break;
                    }
                }
                if(hasExceeded){
                    checkSize -=1;
                    break;
                }
            } else{
                if(fm.stringWidth(text) >= width || fm.getHeight() > height){
                        checkSize -=1;
                        break;
                }
            }
            checkSize++;
        }
        if(!setAuto){
            setFontSize(originalSize);
            refreshText(graphics);
        }
        return checkSize;
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
