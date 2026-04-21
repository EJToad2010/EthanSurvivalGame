package src.GameManagement.UI;

import javax.swing.*;
import java.awt.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

// Handle storing and positioning images as a static class
public class ImageManager {
    public static BufferedImage loadImage(String path){
        try{
            BufferedImage output = ImageIO.read(new File(path));
            return output;
        } catch(IOException e){
            System.out.println("The image " + path + " failed to load.");
            try{
                return ImageIO.read(new File("src/Images/null.png"));
            } catch(IOException ex){
                return null;
            }
        }
    }
    public static ImageIcon loadIcon(String path){
        return new ImageIcon(ImageManager.loadImage(path));
    }
}
