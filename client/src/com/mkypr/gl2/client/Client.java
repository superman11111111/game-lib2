package com.mkypr.gl2.client;

import com.mkypr.gl2.middleware.Constants;

import javax.swing.*;
import java.awt.*;
import java.awt.desktop.SystemSleepEvent;
import java.awt.event.*;
import java.security.Key;
import java.util.*;

public class Client {

    final static String MENUPANEL = "menu";
    final static String GAMEPANEL = "game";
    final static String SETTINGSPANEL = "settings";

    final static LinkedHashMap<String, ActionListener> builder = new LinkedHashMap<>();
    final static LinkedList<String> navigationHistory = new LinkedList<>(Collections.singleton(MENUPANEL));

    static JFrame mainFrame;
    static JPanel buttons;
    static JPanel cards;
    static Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    static JPanel menu;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Client::start);
    }

    public static void updateSize(Dimension dim) {
        mainFrame.setPreferredSize(dim);
        mainFrame.setBounds((screenSize.width - dim.width) / 2, (screenSize.height - dim.height) / 2, dim.width, dim.height);
        buttons.setPreferredSize(new Dimension(dim.width / 5, dim.height / 3));
        for (Component c : buttons.getComponents()) {
            c.setMaximumSize(new Dimension(buttons.getPreferredSize().width, buttons.getPreferredSize().height / builder.size()));
        }
        buttons.setBounds((dim.width - buttons.getPreferredSize().width) / 2, (dim.height - buttons.getPreferredSize().height) / 2, buttons.getPreferredSize().width, buttons.getPreferredSize().height);
        cards.setPreferredSize(dim);

        mainFrame.pack();
        mainFrame.repaint();
    }

    public static void updateColor() {
        mainFrame.getContentPane().setBackground(Constants.mainColor);
        menu.setBackground(Constants.mainColor);
        buttons.setBackground(Constants.mainColor);
        for (Component c : buttons.getComponents()) {
            c.setBackground(Constants.mainColor);
        }
        mainFrame.repaint();
    }

    public static void navigateTo(String s) {
        CardLayout cl = (CardLayout) (cards.getLayout());
        navigationHistory.add(s);
        cl.show(cards, s);
    }

    public static void navigateBack() {
        CardLayout cl = (CardLayout) (cards.getLayout());
        navigationHistory.removeLast();
        cl.show(cards, navigationHistory.getLast());
    }

    static void start() {
        mainFrame = new JFrame();
        mainFrame.setTitle("GameClient");
        mainFrame.setFocusable(true);
        mainFrame.setResizable(false);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setLocationByPlatform(true);

        builder.put("games", actionEvent -> {
            System.out.println("Clicked games");
            navigateTo(GAMEPANEL);
        });
        builder.put("settings", actionEvent -> {
            System.out.println("Clicked settings");
            navigateTo(SETTINGSPANEL);
        });
        builder.put("quit", actionEvent -> System.exit(0));

        menu = new JPanel();
        menu.setFocusable(false);
        menu.setLayout(null);
//        menu.setBackground(Constants.mainColor);
        buttons = new JPanel();
        buttons.setFocusable(false);
//        buttons.setBackground(Constants.mainColor);
        buttons.setLayout(new BoxLayout(buttons, BoxLayout.Y_AXIS));
        for (String s : builder.keySet()) {
            JButton b = new JButton(s);
            b.setFocusable(false);
            b.setFont(Constants.bigFont);
            b.setAlignmentX(Component.CENTER_ALIGNMENT);
            b.setBorderPainted(false);
//            b.setBackground(Constants.mainColor);
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
        }

        menu.add(buttons);

        cards = new JPanel(new CardLayout());
        cards.setFocusable(false);
        cards.add(menu, MENUPANEL);
        cards.add(new GamePane(), GAMEPANEL);
        cards.add(new SettingsPane(), SETTINGSPANEL);

        mainFrame.getContentPane().add(cards);

        mainFrame.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent keyEvent) {

            }

            @Override
            public void keyPressed(KeyEvent keyEvent) {
                if (keyEvent.getKeyCode() == KeyEvent.VK_Q) {
                    System.exit(0);
                } else if (keyEvent.getKeyCode() == KeyEvent.VK_R) {
                    navigateBack();
                }
            }

            @Override
            public void keyReleased(KeyEvent keyEvent) {

            }
        });

//        mainFrame.pack();
        updateSize(new Dimension(1400, 900));
        updateColor();

        mainFrame.setVisible(true);
    }

}
