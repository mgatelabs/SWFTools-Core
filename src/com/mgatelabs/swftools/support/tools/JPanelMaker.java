package com.mgatelabs.swftools.support.tools;

import javax.swing.*;
import java.awt.*;

public class JPanelMaker {

    public static JPanel XPanel(int w, int h) {
        JPanel master = new JPanel();
        master.setLayout(new BoxLayout(master, BoxLayout.X_AXIS));
        setPanelWidth(master, w, h);
        return master;
    }

    public static JPanel YPanel(int w, int h) {
        JPanel master = new JPanel();
        master.setLayout(new BoxLayout(master, BoxLayout.Y_AXIS));
        setPanelWidth(master, w, h);
        return master;
    }

    public static void setPanelWidth(JPanel master, int w, int h) {
        if (w + h == 0) {
            return;
        }
        master.setPreferredSize(new Dimension(w, h));
        master.setMaximumSize(master.getPreferredSize());
        master.setMinimumSize(master.getPreferredSize());
    }

    public static JPanel buildLayoutPanel(Component a, Component b, int fullwidth, int widthpart, int height) {
        return buildLayoutPanel(a, b, fullwidth, widthpart, height, null);
    }

    public static JPanel buildLayoutPanel(Component a, Component b, int fullwidth, int widthpart, int height, Color back) {
        JPanel master = JPanelMaker.XPanel(fullwidth, height); // 630
        JPanel p1 = JPanelMaker.XPanel(widthpart, height);
        JPanel p2 = JPanelMaker.XPanel(fullwidth - widthpart, height);

        if (back != null) {
            master.setBackground(back);
            p1.setBackground(back);
            p2.setBackground(back);
        }

        p1.add(a);

        p2.add(b);
        p2.add(Box.createHorizontalStrut(5));

        master.add(p1);
        master.add(p2);

        master.add(Box.createHorizontalStrut(5));

        return master;
    }

}