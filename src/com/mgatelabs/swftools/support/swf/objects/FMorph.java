/*
    Copyright M-Gate Labs 2007
    Can be edited with permission only.
*/

// Class for Shapes that Change Shape

package com.mgatelabs.swftools.support.swf.objects;

// Need Vector Support

import java.util.Vector;

public class FMorph {
    private int myFormat;
    private int myShapeID;
    private FRect myStartRect;
    private FRect myEndRect;

    private FStyle myStartStyle;
    private FStyle myEndStyle;
    private Vector myRenderData;

    // Constructor

    public FMorph(int aID, FRect startRect, FRect endRect) {
        myShapeID = aID;
        myStartRect = startRect;
        myEndRect = endRect;
        myStartStyle = new FStyle();
        myEndStyle = new FStyle();
        myRenderData = null;
    }

    // Clean out Flash Information

    public void clean() {
        myStartStyle.clean();
        myEndStyle.clean();
        myStartStyle = null;
        myEndStyle = null;
    }

    // Is this Format 1, 2 or 3

    public int getFormat() {
        return myFormat;
    }

    // Get this shapes ID

    public int getID() {
        return myShapeID;
    }

    // Get My Rect

    public FRect getStartRect() {
        return myStartRect;
    }

    // Get My Rect

    public FRect getEndRect() {
        return myEndRect;
    }

    // Get my Shape With Style

    public FStyle getStartStyle() {
        return myStartStyle;
    }

    public FStyle getEndStyle() {
        return myEndStyle;
    }

    // Get Render Information

    public Vector getRenderData() {
        return myRenderData;
    }

    // Set the Render Data

    public void setRenderData(Vector val) {
        myRenderData = val;
    }
}