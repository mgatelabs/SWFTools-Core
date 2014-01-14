/*
    Copyright M-Gate Labs 2007
    Can be edited with permission only.
*/

package com.mgatelabs.swftools.support.swf.objects;

// Need Vector Support

import java.util.Vector;

public class FShape implements FID {
    // Static Vars
    public static final int VER1 = 1;
    public static final int VER2 = 2;
    public static final int VER3 = 3;

    // Object Vars
    private int myFormat;
    private int myShapeID;
    private FRect myRect;
    private FRect myStrokeRect;

    // Style & Shape Vars
    private FStyle myStyle;
    private Vector myRenderData;

    private boolean myShapeScales;
    private boolean myShapeNotScales;

    // Constructor

    public FShape(int format, int aID, FRect aRect) {
        myFormat = format;
        myShapeID = aID;
        myRect = aRect;
        myStyle = new FStyle();
        myRenderData = null;
        myStrokeRect = null;

        myShapeScales = false;
        myShapeNotScales = false;
    }

    // Clean out Useless Flash Information

    public void clean() {
        myStyle.clean();
        myStyle = null;
    }

    // Is this Format 1, 2 or 3
    // 4 Not Supported

    public int getFormat() {
        return myFormat;
    }

    // Get this shapes ID

    public int getID() {
        return myShapeID;
    }

    // Get My Rect

    public FRect getRect() {
        return myRect;
    }

    // Get My Rect

    public FRect getStrokeRect() {
        return myStrokeRect;
    }

    // Set My Rect

    public void setStrokeRect(FRect aRect) {
        myStrokeRect = aRect;
    }

    // Get my Shape With Style

    public FStyle getStyle() {
        return myStyle;
    }

    // Get Render Information

    public Vector getRenderData() {
        return myRenderData;
    }

    // Set the Render Data

    public void setRenderData(Vector val) {
        myRenderData = val;
    }

    public void setShapeScales(boolean value) {
        myShapeScales = value;
    }

    public void setShapeNonScales(boolean value) {

        myShapeNotScales = value;
    }

    public boolean getShapeScales() {
        return myShapeScales;
    }

    public boolean getShapeNonScales() {

        return myShapeNotScales;
    }
}