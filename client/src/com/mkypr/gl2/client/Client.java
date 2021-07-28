package com.mkypr.gl2.client;

import com.mkypr.gl2.middleware.Constants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class Client {

    final static String MENUPANEL = "menu";
    final static String GAMEPANEL = "game";
    final static String SETTINGSPANEL = "settings";

    static JFrame mainFrame;
    static JPanel cards;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Client::start);
    }

    static void start() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension windowDim = new Dimension(1400, 900);
        mainFrame = new JFrame();
        mainFrame.setTitle("GameClient");
        mainFrame.setFocusable(true);
        mainFrame.setPreferredSize(windowDim);
        mainFrame.setResizable(false);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setLocationByPlatform(true);
        mainFrame.getContentPane().setBackground(Constants.mainColor);

        mainFrame.setBounds((screenSize.width - windowDim.width) / 2, (screenSize.height - windowDim.height) / 2, windowDim.width, windowDim.height);

        LinkedHashMap<String, ActionListener> builder = new LinkedHashMap<>();
        builder.put("games", actionEvent -> {
            System.out.println("Clicked games");
            CardLayout cl = (CardLayout)(cards.getLayout());
            cl.show(cards, GAMEPANEL);
        });
        builder.put("settings", actionEvent -> {
            System.out.println("Clicked settings");
            CardLayout cl = (CardLayout)(cards.getLayout());
            cl.show(cards, SETTINGSPANEL);
        });
        builder.put("quit", actionEvent -> System.exit(0));

        JPanel buttons = new JPanel();
        buttons.setFocusable(false);
        buttons.setBorder(BorderFactory.createLineBorder(Color.red));
        buttons.setBackground(Constants.mainColor);
        buttons.setLayout(new BoxLayout(buttons, BoxLayout.Y_AXIS));
        buttons.setPreferredSize(new Dimension(windowDim.width / 5, windowDim.height / 3));
        buttons.add(Box.createRigidArea(new Dimension(0, windowDim.height / 4)));
        for (String s : builder.keySet()) {
            JButton b = new JButton(s);
            b.setFocusable(false);
            b.setFont(Constants.bigFont);
            b.setMaximumSize(new Dimension(buttons.getPreferredSize().width, buttons.getPreferredSize().height / builder.size()));
            b.setAlignmentX(Component.CENTER_ALIGNMENT);
            b.setBorderPainted(false);
            b.setBackground(Constants.mainColor);
            b.addActionListener(builder.get(s));
            b.addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent mouseEvent) {

                }

                @Override
                public void mousePressed(MouseEvent mouseEvent) {

                }

                @Override
                public void mouseReleased(MouseEvent mouseEvent) {

                }

                @Override
                public void mouseEntered(MouseEvent mouseEvent) {
                    b.setBackground(Constants.mainColor.brighter());
                    b.repaint();
                }

                @Override
                public void mouseExited(MouseEvent mouseEvent) {
                    b.setBackground(Constants.mainColor);
                    b.repaint();
                }
            });
            buttons.add(b);
            buttons.add(Box.createRigidArea(new Dimension(0, 10)));
        }

        cards = new JPanel(new CardLayout());
        cards.setFocusable(false);
        cards.setPreferredSize(windowDim);
        cards.add(buttons, MENUPANEL);
        cards.add(new GamePane(), GAMEPANEL);
        cards.add(new SettingsPane(), SETTINGSPANEL);

        mainFrame.getContentPane().add(cards);

        mainFrame.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent keyEvent) {

            }

            @Override
            public void keyPressed(KeyEvent keyEvent) {
                System.out.println(keyEvent.getKeyCode());
                if (keyEvent.getKeyCode() == KeyEvent.VK_Q) {
                    System.exit(0);
                }
            }

            @Override
            public void keyReleased(KeyEvent keyEvent) {

            }
        });

        mainFrame.pack();
        mainFrame.setVisible(true);
    }

}
