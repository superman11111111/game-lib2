package com.mkypr.gl2.client;

import com.mkypr.gl2.middleware.Constants;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class GamePane extends JLayeredPane {

    Ship[] player1Ships;
    int gridSize = 8;

    BufferedImage bigShip;

    static class Ship {
        int x;
        int y;
        boolean r;
        boolean[] h; // health

        protected Ship(int x, int y, boolean r, boolean[] h) {
            this.x = x;
            this.y = y;
            this.r = r;
            this.h = h;
        }

        public static Ship create(int x, int y, boolean rotation, int length) {
            return new Ship(x, y, rotation, new boolean[length]);
        }
    }

    public GamePane() {
        setFocusable(false);
        setLayout(null);
        setOpaque(true);
        player1Ships = new Ship[] {
                Ship.create(1, 2, true, 4),
                Ship.create(5, 2, false, 2)
        };
        JPanel p = new JPanel();
        p.setFocusable(false);
        FlowLayout flowLayout = new FlowLayout();
        flowLayout.setVgap(0);
        flowLayout.setAlignment(FlowLayout.LEFT);
        p.setLayout(flowLayout);
        JSlider slider = new JSlider();
        slider.setFocusable(false);
        slider.setMaximum(((Dimension) Settings.getValue("maindim")).height / gridSize);
        slider.setMinimum(slider.getMaximum() / 2);
        slider.setPaintTicks(true);
        slider.setSnapToTicks(true);
        JLabel l = new JLabel(String.valueOf(slider.getValue()));
        l.setFocusable(false);
        l.setFont(Constants.smallFont);
        l.setPreferredSize(new Dimension(80, 40));
        slider.addChangeListener(changeEvent -> l.setText(String.valueOf(slider.getValue())));
        p.add(l);
        p.add(slider);
        try {
            Settings.addSetting("blocksize", slider::getValue,  p);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Texture texture = new Texture("/textures/ship.png");
        bigShip = texture.image.getSubimage(4, 4, 64, 64 * 4);

    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setColor(Color.black);
        int pixelPerBlock = (int) Settings.getValue("blocksize");
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                g2d.drawRect(i * pixelPerBlock, j * pixelPerBlock, pixelPerBlock, pixelPerBlock);
            }
        }
        for (Ship s : player1Ships) {
            AffineTransform t = new AffineTransform();
            Image im = bigShip.getScaledInstance(pixelPerBlock, s.h.length * pixelPerBlock, BufferedImage.TYPE_INT_RGB);
            t.translate(s.x * pixelPerBlock, s.y * pixelPerBlock);
            if (s.r) {
                t.translate(0, pixelPerBlock);
                t.rotate(Math.toRadians(270));
            }
            g2d.drawImage(im, t, this);
        }

        g2d.dispose();
    }
}
