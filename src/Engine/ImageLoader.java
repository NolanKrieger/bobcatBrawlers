package Engine;

import Utils.ImageUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

// contains a bunch of helpful methods for loading images file into the game
public class ImageLoader {

    // loads an image and sets its transparent color to the one defined in the Config class
    public static BufferedImage load(String imageFileName) {
        return ImageLoader.load(imageFileName, Config.TRANSPARENT_COLOR);
    }

    // loads an image and allows the transparent color to be specified
    public static BufferedImage load(String imageFileName, Color transparentColor) {
        try {
            // Primary: path relative to working directory using Config.RESOURCES_PATH
            File primary = new File(Config.RESOURCES_PATH + imageFileName);
            BufferedImage initialImage = null;

            if (primary.exists()) {
                initialImage = ImageIO.read(primary);
            } else {
                // Secondary: some users run the program from the repository root where resources may be under the project folder
                File alt = new File("bobcatBrawlers/" + Config.RESOURCES_PATH + imageFileName);
                if (alt.exists()) {
                    initialImage = ImageIO.read(alt);
                } else {
                    // Tertiary: try loading from the classpath (e.g., inside the jar or resources folder on classpath)
                    try {
                        java.net.URL resource = Thread.currentThread().getContextClassLoader().getResource(Config.RESOURCES_PATH + imageFileName);
                        if (resource != null) initialImage = ImageIO.read(resource);
                    } catch (Exception ex) {
                        // ignore here, handled below
                    }
                }
            }

            if (initialImage == null) {
                throw new IOException("Image file not found in any known location");
            }
            return ImageUtils.transformColorToTransparency(initialImage, transparentColor);
        } catch (IOException e) {
            System.out.println("Unable to find file " + Config.RESOURCES_PATH + imageFileName);
            throw new RuntimeException(e);
        }
    }

    // loads a piece of an image from an image file and sets its transparent color to the one defined in the Config class
    public static BufferedImage loadSubImage(String imageFileName, int x, int y, int width, int height) {
        return ImageLoader.loadSubImage(imageFileName, Config.TRANSPARENT_COLOR, x, y, width, height);
    }

    // loads a piece of an image from an image file and allows the transparent color to be specified
    public static BufferedImage loadSubImage(String imageFileName, Color transparentColor, int x, int y, int width, int height) {
        try {
            BufferedImage initialImage = ImageIO.read(new File(Config.RESOURCES_PATH + imageFileName));
            BufferedImage transparentImage = ImageUtils.transformColorToTransparency(initialImage, transparentColor);
            return transparentImage.getSubimage(x, y, width, height);
        } catch (IOException e) {
            System.out.println("Unable to find file " + Config.RESOURCES_PATH + imageFileName);
            throw new RuntimeException(e);
        }
    }
}
