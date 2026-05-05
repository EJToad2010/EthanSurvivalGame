package src.GameManagement.UI;

import javax.swing.*;
import java.awt.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;

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
    public static BufferedImage rotateImage(BufferedImage image, double degrees) {
        double radians = Math.toRadians(degrees);
        double centerX = image.getWidth() / 2.0;
        double centerY = image.getHeight() / 2.0;

        AffineTransform tx = AffineTransform.getRotateInstance(radians, centerX, centerY);
        AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
        return op.filter(image, null);
    }
}
