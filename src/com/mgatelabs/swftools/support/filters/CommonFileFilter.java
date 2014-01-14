package com.mgatelabs.swftools.support.filters;

import java.io.File;

public class CommonFileFilter extends javax.swing.filechooser.FileFilter {
    private String endString;
    private String description;

    // File Filter that can filter any file type

    public CommonFileFilter(String aEnding, String aDescription) {
        endString = aEnding;
        description = aDescription;
    }

    public void update(String aEnding, String aDescription) {
        endString = aEnding;
        description = aDescription;
    }

    public boolean accept(File f) {
        return f.toString().toLowerCase().endsWith(endString) || f.isDirectory();
    }

    public String getDescription() {
        return description;
    }
}