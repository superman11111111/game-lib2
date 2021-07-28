package com.mkypr.gl2.client;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

public class Texture {
    public BufferedImage image;

    public Texture(String resource) {
        try {
            image = ImageIO.read(Objects.requireNonNull(Client.class.getResourceAsStream(resource)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
