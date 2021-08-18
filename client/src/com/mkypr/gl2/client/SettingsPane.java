package com.mkypr.gl2.client;

import com.mkypr.gl2.middleware.Constants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class SettingsPane extends JPanel {

    static JComboBox<Settings.Resolution> resolutionBox;

    public SettingsPane() {
        setFocusable(false);
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        setBackground((Color) Settings.getValue("maincol"));

        Settings.Resolution[] resolutions = new Settings.Resolution[]{
                new Settings.Resolution(1900, 1000),
                new Settings.Resolution(1400, 900),
                new Settings.Resolution(1200, 800)
        };
        FlowLayout flowLayout = new FlowLayout();
        flowLayout.setVgap(0);
        flowLayout.setHgap(0);
        flowLayout.setAlignment(FlowLayout.LEFT);
        resolutionBox = new JComboBox<>(resolutions);
        resolutionBox.setFont(Constants.smallFont);
        resolutionBox.setPreferredSize(new Dimension(400, 40));
        Settings.addSetting("resolutions", resolutions, resolutionBox);

        class ColorPicker extends JPanel {
            final JSlider[] sliders = new JSlider[3];
            Color currentColor = (Color) Settings.getValue("maincol");
            @Override
            public void paint(Graphics g) {
                super.paint(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setColor(currentColor);
                g2d.fillRect(0, 20, 40, getPreferredSize().height - 40);
                g2d.dispose();
            }

            public ColorPicker() {
                setFocusable(false);
                setPreferredSize(new Dimension(250, 140));
                setMinimumSize(getPreferredSize());
                setMaximumSize(getPreferredSize());
                for (int i = 0; i < sliders.length; i++) {
                    JPanel p = new JPanel();
                    p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
                    JSlider s = new JSlider(JSlider.VERTICAL, 0, 0xff, 0);
                    JLabel l = new JLabel(String.valueOf(s.getValue()));
                    s.setFocusable(false);
                    s.setPaintTicks(true);
                    s.setSnapToTicks(true);
                    s.setAlignmentX(LEFT_ALIGNMENT);
                    l.setFont(Constants.smallFont);
                    l.setAlignmentX(LEFT_ALIGNMENT);
                    l.setPreferredSize(new Dimension(40, 40));
                    l.setMinimumSize(l.getPreferredSize());
                    l.setMaximumSize(l.getPreferredSize());
                    s.addChangeListener(changeEvent -> {
                        l.setText(String.valueOf(s.getValue()));
                        currentColor = new Color(sliders[0].getValue(), sliders[1].getValue(), sliders[2].getValue());
                        repaint();
                    });
                    s.setPreferredSize(new Dimension(l.getPreferredSize().width, getPreferredSize().height - l.getPreferredSize().height - 8));
                    s.setMinimumSize(s.getPreferredSize());
                    s.setMaximumSize(s.getPreferredSize());
                    p.add(l);
                    p.add(s);
                    add(p);
                    sliders[i] = s;
                }
            }
        }
        ColorPicker colorPicker = new ColorPicker();
        Settings.setComponent("maincol", colorPicker);
        Settings.setDimension("maincol", new Dimension(400, 140));
        Settings.setValueGetter("maincol", () -> colorPicker.currentColor);

        FlowLayout flowLayout1 = new FlowLayout();
        flowLayout1.setVgap(0);
        flowLayout1.setHgap(0);
        flowLayout1.setAlignment(FlowLayout.LEFT);
        for (String k : Settings.allSettings().keySet()) {
            Settings.Setting setting = Settings.allSettings().get(k);
            if (setting.component == null) continue;
            System.out.println(setting.name);
            JPanel settingsItem = new JPanel();
            settingsItem.setFocusable(false);
            settingsItem.setAlignmentX(Component.CENTER_ALIGNMENT);
            settingsItem.setLayout(flowLayout1);

            JLabel nameLabel = new JLabel(setting.name);
            nameLabel.setFocusable(false);
            nameLabel.setFont(Constants.smallFont);
            nameLabel.setPreferredSize(new Dimension(200, 40));
            nameLabel.setMinimumSize(nameLabel.getPreferredSize());
            nameLabel.setMaximumSize(nameLabel.getPreferredSize());

            settingsItem.add(nameLabel);

            settingsItem.setMaximumSize(new Dimension(nameLabel.getMaximumSize().width + setting.dimension.width, setting.dimension.height));

            setting.component.setFocusable(false);
            settingsItem.add(setting.component);
            add(settingsItem);
        }

        JButton applyButton = new JButton("Apply");
        applyButton.setFocusable(false);
        applyButton.setFont(Constants.smallFont);
        applyButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        applyButton.setBorderPainted(false);
        applyButton.setMaximumSize(new Dimension(600, 40));
        applyButton.setBackground(((Color) Settings.getValue("maincol")));
        applyButton.addMouseListener(new MouseListener() {
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
                applyButton.setBackground(((Color) Settings.getValue("maincol")).brighter());
                applyButton.repaint();
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {
                applyButton.setBackground(((Color) Settings.getValue("maincol")));
                applyButton.repaint();
            }
        });
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
            Client.updateColor();
            Client.navigateBack();
        });
        add(applyButton);
    }
}
