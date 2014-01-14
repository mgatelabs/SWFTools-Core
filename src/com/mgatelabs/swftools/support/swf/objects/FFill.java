/*
    Copyright M-Gate Labs 2007
    Can be edited with permission only.
*/

package com.mgatelabs.swftools.support.swf.objects;

public abstract class FFill {
    // My possible fill styles

    public static final int SOLID = 0;
    public static final int LGRADIENT = 1;
    public static final int RGRADIENT = 2;
    public static final int FGRADIENT = 5;
    public static final int TBITMAP = 3;
    public static final int CBITMAP = 4;

    // My ID
    private int myID;

    // Every fill could possibly have a morph
    public FFill morph(float per) {
        return null;
    }

    // Constructor
    public FFill(int aID) {
        myID = aID;
    }

    // Get My Style
    public int getStyle() {
        return myID;
    }
}