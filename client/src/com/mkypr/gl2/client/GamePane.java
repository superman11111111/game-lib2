package com.mkypr.gl2.client;

import com.mkypr.gl2.middleware.Constants;

import javax.swing.*;
import java.awt.*;

public class GamePane extends JLayeredPane {
    public GamePane() {
        setFocusable(false);
        setLayout(null);
        setBorder(BorderFactory.createLineBorder(Color.black, 15));
        setBackground(Constants.mainColor);
    }
}
