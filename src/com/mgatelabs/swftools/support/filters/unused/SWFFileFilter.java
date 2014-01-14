package com.mgatelabs.swftools.support.filters;

import java.io.File;

public class SWFFileFilter extends javax.swing.filechooser.FileFilter {
    public boolean accept(File f) {
        return f.toString().toLowerCase().endsWith(".swf") || f.isDirectory();
    }

    public String getDescription() {
        return "SWF, Macromedia Flash Files";
    }
}