package com.mkypr.gl2.client;

import com.mkypr.gl2.middleware.Constants;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedHashMap;

public class SettingsPane extends JPanel {

    static class Resolution extends Dimension {
        public Resolution(int width, int height) {
            super(width, height);
        }

        @Override
        public String toString() {
            return String.format("%dx%d", width, height);
        }
    }

    static class Keybind {
        String action;
        String k;

        public Keybind(String action, String k) {
            this.action = action;
            this.k = k;
        }
    }

    static Resolution[] resolutions = new Resolution[]{
            new Resolution(1900, 1080),
            new Resolution(1400, 900),
            new Resolution(1200, 800)
    };
    static JComboBox<Dimension> resolutionBox = new JComboBox<>(resolutions);
    static Keybind[] keybinds = new Keybind[]{
            new Keybind("Quit", "Q"),
            new Keybind("Navigate Back", "R")
    };


    public SettingsPane() {
        setFocusable(false);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(Constants.mainColor);

        LinkedHashMap<String, JComponent> settingsBuilder = new LinkedHashMap<>();
        FlowLayout flowLayout = new FlowLayout();
        flowLayout.setVgap(0);
        resolutionBox = new JComboBox<>(resolutions);
        resolutionBox.setFont(Constants.smallFont);
        resolutionBox.setPreferredSize(new Dimension(400, 40));
        settingsBuilder.put("Resolution", resolutionBox);

        for (String k : settingsBuilder.keySet()) {
            JLabel label = new JLabel(k + ": ");
            label.setFocusable(false);
            label.setFont(Constants.smallFont);
            label.setPreferredSize(new Dimension(400, 40));
            JComponent comp = settingsBuilder.get(k);
            comp.setFocusable(false);
            JPanel settingsItem = new JPanel();
            settingsItem.setFocusable(false);
            settingsItem.setAlignmentX(Component.CENTER_ALIGNMENT);
            settingsItem.setLayout(flowLayout);
            settingsItem.setMaximumSize(new Dimension(label.getPreferredSize().width + comp.getPreferredSize().width + 40, comp.getPreferredSize().height));
            settingsItem.add(label);
            settingsItem.add(comp);

            add(settingsItem);
        }

        JButton applyButton = new JButton("Apply");
        applyButton.setFocusable(false);
        applyButton.setFont(Constants.smallFont);
        applyButton.setPreferredSize(new Dimension(250, 40));
        applyButton.addActionListener(actionEvent -> {
            System.out.println("Updating settings!");
            setBackground(Constants.mainColor);
            Client.updateSize((Dimension) resolutionBox.getSelectedItem());
        });
        add(applyButton);
    }
}
