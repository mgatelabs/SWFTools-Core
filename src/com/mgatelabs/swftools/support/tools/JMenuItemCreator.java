package com.mgatelabs.swftools.support.tools;

import javax.swing.*;
import java.awt.event.ActionListener;

public class JMenuItemCreator {
    ActionListener myActionListener;

    public JMenuItemCreator(ActionListener myList) {
        myActionListener = myList;
    }

    public JMenuItem build(String name) {
        JMenuItem item = new JMenuItem(name);
        item.addActionListener(myActionListener);
        return item;
    }
}