/*
  Copyright M-Gate Labs 2007
  Can be edited with permission only.
*/

package com.mgatelabs.swftools.support.swf.objects;

import java.util.Vector;

public class FStyle {
    private Vector myLineStyles;
    private Vector myFillStyles;
    int NfillBits;
    int NumLineBits;
    private Vector myShapeRecords;

    public FStyle() {
        // Default Everything
        myLineStyles = new Vector();
        myFillStyles = new Vector();
        myShapeRecords = new Vector();
        NfillBits = 0;
        NumLineBits = 0;
    }

    public void clean() {
        // Clean up, Remove Useless Information
        myLineStyles.clear();
        myFillStyles.clear();
        myShapeRecords.clear();

        myLineStyles = null;
        myFillStyles = null;
        myShapeRecords = null;
    }

    public void setNfillBits(int value) {
        NfillBits = value;
    }

    public void setNumLineBits(int value) {
        NumLineBits = value;
    }

    public int getNfillBits() {
        return NfillBits;
    }

    public int getNumLineBits() {
        return NumLineBits;
    }

    // Get my Line Styles

    public Vector getLineStyles() {
        return myLineStyles;
    }

    // Get my Fill Styles

    public Vector getFillStyles() {
        return myFillStyles;
    }

    // Get my Shape Records

    public Vector getShapeRecords() {
        return myShapeRecords;
    }

    public void setShapeRecords(Vector vec) {
        myShapeRecords = null;
        myShapeRecords = vec;
    }
}