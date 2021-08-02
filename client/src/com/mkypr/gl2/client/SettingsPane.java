package com.mkypr.gl2.client;

import com.mkypr.gl2.middleware.Constants;

import javax.swing.*;
import java.awt.*;

public class SettingsPane extends JPanel {

    static JComboBox<Settings.Resolution> resolutionBox;

    public SettingsPane() {
        setFocusable(false);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground((Color) Settings.getValue("maincol"));

        Settings.addSetting("resolutions", new Settings.Resolution[]{
                new Settings.Resolution(1900, 1080),
                new Settings.Resolution(1400, 900),
                new Settings.Resolution(1200, 800)
        });

        FlowLayout flowLayout = new FlowLayout();
        flowLayout.setVgap(0);
        flowLayout.setHgap(0);
        flowLayout.setAlignment(FlowLayout.LEFT);
        Settings.Resolution[] resolutions = (Settings.Resolution[]) Settings.getValue("resolutions");
        resolutionBox = new JComboBox<>(resolutions);
        resolutionBox.setFont(Constants.smallFont);
        resolutionBox.setPreferredSize(new Dimension(400, 40));
        Settings.setComponent("resolutions", resolutionBox);
//        Settings.setDimension("resolutions", new Dimension(resolutionBox.getPreferredSize().width, resolutionBox.getPreferredSize().height * resolutions.length));

        for (String k : Settings.allSettings().keySet()) {
            Settings.Setting setting = Settings.allSettings().get(k);
            if (setting.component == null) continue;
            JPanel settingsItem = new JPanel();
            settingsItem.setFocusable(false);
            settingsItem.setAlignmentX(Component.CENTER_ALIGNMENT);
            settingsItem.setLayout(flowLayout);

            JLabel nameLabel = new JLabel(setting.name);
            nameLabel.setFocusable(false);
            nameLabel.setFont(Constants.smallFont);
            nameLabel.setMaximumSize(new Dimension(300, 40));
            nameLabel.setMinimumSize(nameLabel.getMaximumSize());
            nameLabel.setPreferredSize(nameLabel.getMaximumSize());
            settingsItem.add(nameLabel);

            settingsItem.setMaximumSize(new Dimension(nameLabel.getMaximumSize().width + setting.dimension.width, setting.dimension.height));

            setting.component.setFocusable(false);
            settingsItem.add(setting.component);
            add(settingsItem);
        }

        JButton applyButton = new JButton("Apply");
        applyButton.setFocusable(false);
        applyButton.setFont(Constants.smallFont);
        applyButton.setPreferredSize(new Dimension(250, 40));
        applyButton.addActionListener(actionEvent -> {
            Settings.setValue("maindim", resolutionBox.getSelectedItem());
            for (String k : Settings.allSettings().keySet()) {
                Settings.Setting setting = Settings.allSettings().get(k);
                if (setting.component != null && setting.valueGetter != null) {
                    try {
                        Settings.setValue(k, setting.valueGetter.call());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            Client.updateSize();
            Client.navigateBack();
        });
        add(applyButton);
    }
}
