package com.mgatelabs.swftools.support.plugins;

public interface Plugin {
    public static final int CONVERSION = 1;
    public static final int BLOCK = 3;
    public static final int DISPLAY = 5;

    public String getName();

    public int getType();

}