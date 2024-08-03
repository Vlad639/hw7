package com.github.javarar.animal.faces;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.util.function.Consumer;

/**
 * Создаёт на основе исходного новое чёрно-белое изображение и сохраняет его с тем же именем в заданной директории
 */
public class BlackWhiteHandler implements Consumer<File> {

    private final String targetPath;

    public BlackWhiteHandler(String targetPath) {
        this.targetPath = targetPath;
    }

    @Override
    public void accept(File file) {
        try {
            BufferedImage sourceImage = ImageIO.read(new FileInputStream(file));
            int width = sourceImage.getWidth();
            int height = sourceImage.getHeight();
            BufferedImage blackWhiteImage = new BufferedImage(width, height, sourceImage.getType());

            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    Color color = new Color(sourceImage.getRGB(x, y));
                    int red = color.getRed();
                    int green = color.getGreen();
                    int blue = color.getBlue();

                    int blackWhiteCanal = (int) (red * 0.299 + green * 0.587 + blue * 0.114);

                    red = blackWhiteCanal;
                    green = blackWhiteCanal;
                    blue = blackWhiteCanal;

                    Color blackWhiteColor = new Color(red, green, blue);
                    blackWhiteImage.setRGB(x, y, blackWhiteColor.getRGB());
                }
            }

            File result = new File(targetPath + File.separator + file.getName());
            ImageIO.write(blackWhiteImage, "jpg", result);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
