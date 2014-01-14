package com.mgatelabs.swftools.support.tools;

import javax.swing.*;
import java.awt.*;

public class NerdyFrame {
    private JFrame frame;
    private int w, h;
    private boolean autoCenter;

    public NerdyFrame(String title, int w, int h, boolean allowCloseOperation) {
        this.w = w;
        this.h = h;
        frame = new JFrame(title);
        if (allowCloseOperation) {
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        }
        autoCenter = false;
    }

    public void setMenu(JMenuBar theJMenuBar) {
        frame.setJMenuBar(theJMenuBar);
    }

    public void setAutoCenter(boolean value) {
        autoCenter = value;
    }

    public void setResizeable(boolean value) {
        frame.setResizable(value);
    }

    public JFrame getFrame() {
        return frame;
    }

    public void setTitle(String title) {
        frame.setTitle(title);
    }

    public void close() {
        frame.dispose();
    }

    public void setContent(JComponent stuff) {
        frame.getContentPane().add(stuff);
        frame.pack();

        if (w + h > 0) {
            frame.setSize(w, h);
        }

        if (autoCenter) {
            Toolkit tk = Toolkit.getDefaultToolkit();
            Dimension screenSize = tk.getScreenSize();
            frame.setLocation((screenSize.width / 2) - (frame.getWidth() / 2), (screenSize.height / 2) - (frame.getHeight() / 2));
        }

        frame.show();

    }
}