package com.mkypr.gl2.client;

import com.mkypr.gl2.middleware.Constants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;

public class Client {
    private final static String MENUPANEL = "menu";
    private final static String GAMEPANEL = "game";
    private final static String SETTINGSPANEL = "settings";

    public final static LinkedHashMap<String, JComponent> cards = new LinkedHashMap<>();
    public final static LinkedList<String> navigationHistory = new LinkedList<>(Collections.singleton(MENUPANEL));
    public final static Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

    private static JFrame mainFrame;
    private static JPanel btnPanel;
    private static JPanel cardPanel;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Client::start);
    }

    public static void updateSize() {
        Dimension dim = (Dimension) Settings.getValue("maindim");
        mainFrame.setPreferredSize(dim);
        mainFrame.setBounds((screenSize.width - dim.width) / 2, (screenSize.height - dim.height) / 2, dim.width, dim.height);
        btnPanel.setPreferredSize(new Dimension(dim.width / 5, dim.height / 3));
        for (Component c : btnPanel.getComponents()) {
            c.setMaximumSize(new Dimension(btnPanel.getPreferredSize().width, btnPanel.getPreferredSize().height / btnPanel.getComponents().length));
        }
        btnPanel.setBounds((dim.width - btnPanel.getPreferredSize().width) / 2, (dim.height - btnPanel.getPreferredSize().height) / 2, btnPanel.getPreferredSize().width, btnPanel.getPreferredSize().height);
        cardPanel.setPreferredSize(dim);

        mainFrame.pack();
    }

    public static void updateColor() {
        Color color = (Color) Settings.getValue("maincol");
        mainFrame.getContentPane().setBackground(color);
        btnPanel.setBackground(color);
        for (Component c : btnPanel.getComponents()) {
            c.setBackground(color);
        }
        for (JComponent c : cards.values()) {
            c.setBackground(color);
        }
    }

    public static void navigateTo(String s) {
        CardLayout cl = (CardLayout) (cardPanel.getLayout());
        navigationHistory.add(s);
        cl.show(cardPanel, s);
    }

    public static boolean navigateBack() {
        try {
            CardLayout cl = (CardLayout) (cardPanel.getLayout());
            if (navigationHistory.size() > 1) {
                navigationHistory.removeLast();
                cl.show(cardPanel, navigationHistory.getLast());
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    static void start() {
        Settings.addSetting("maindim", new Dimension(1400, 900));
        Settings.addSetting("maincol", new Color(96, 197, 209));
        Settings.addSetting("keybinds", new Settings.Keybind[]{
                new Settings.Keybind(() -> {
                    System.exit(0);
                    return true;
                }, KeyEvent.VK_Q),
                new Settings.Keybind(Client::navigateBack, KeyEvent.VK_R)
        });

        mainFrame = new JFrame();
        mainFrame.setTitle("GameClient");
        mainFrame.setFocusable(true);
        mainFrame.setResizable(false);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setLocationByPlatform(true);

        LinkedHashMap<String, ActionListener> builder = new LinkedHashMap<>();
        builder.put("games", actionEvent -> {
            navigateTo(GAMEPANEL);
        });
        builder.put("settings", actionEvent -> {
            navigateTo(SETTINGSPANEL);
        });
        builder.put("quit", actionEvent -> System.exit(0));

        JPanel menu = new JPanel();
        menu.setFocusable(false);
        menu.setLayout(null);
        btnPanel = new JPanel();
        btnPanel.setFocusable(false);
        btnPanel.setLayout(new BoxLayout(btnPanel, BoxLayout.Y_AXIS));
        for (String s : builder.keySet()) {
            JButton b = new JButton(s);
            b.setFocusable(false);
            b.setFont(Constants.bigFont);
            b.setAlignmentX(Component.CENTER_ALIGNMENT);
            b.setBorderPainted(false);
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
                    b.setBackground(((Color) Settings.getValue("maincol")).brighter());
                    b.repaint();
                }

                @Override
                public void mouseExited(MouseEvent mouseEvent) {
                    b.setBackground(((Color) Settings.getValue("maincol")));
                    b.repaint();
                }
            });
            btnPanel.add(b);
        }
        menu.add(btnPanel);

        cardPanel = new JPanel(new CardLayout());
        cardPanel.setFocusable(false);
        cards.put(MENUPANEL, menu);
        cards.put(GAMEPANEL, new GamePane());
        cards.put(SETTINGSPANEL, new SettingsPane());
        for (String k : cards.keySet()) cardPanel.add(cards.get(k), k);

        mainFrame.getContentPane().add(cardPanel);

        mainFrame.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent keyEvent) {

            }

            @Override
            public void keyPressed(KeyEvent keyEvent) {
                for (Settings.Keybind keybind : (Settings.Keybind[]) Settings.getValue("keybinds")) {
                    if (keyEvent.getKeyCode() == keybind.key) {
                        try {
                            if (keybind.action.call()) return;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void keyReleased(KeyEvent keyEvent) {

            }
        });

        Client.updateSize();
        Client.updateColor();

        mainFrame.setVisible(true);
    }

}
