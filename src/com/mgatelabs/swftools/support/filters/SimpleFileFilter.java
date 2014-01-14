package com.mgatelabs.swftools.support.filters;

import java.io.File;
import java.io.FileFilter;

public class SimpleFileFilter implements FileFilter {
    private String endString;

    // File Filter that can filter any file type

    public SimpleFileFilter(String aEnding) {
        endString = aEnding;
    }

    public boolean accept(File f) {
        return f.toString().toLowerCase().endsWith(endString);
    }

}