package com.mgatelabs.swftools.support.filters;

import java.io.File;

public class SVGFileFilter extends javax.swing.filechooser.FileFilter {
    public boolean accept(File f) {
        return f.toString().toLowerCase().endsWith(".svg") || f.isDirectory();
    }

    public String getDescription() {
        return "SVG, Scalable Vector Graphics";
    }
}