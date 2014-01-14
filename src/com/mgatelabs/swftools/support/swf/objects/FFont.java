/*
    Copyright M-Gate Labs 2007
    Can be edited with permission only.
*/

package com.mgatelabs.swftools.support.swf.objects;

////////////////////////////////////////////////////////////////////////////

import java.util.Vector;

////////////////////////////////////////////////////////////////////////////

public class FFont implements FID {
    private int myFontType;
    private int myGlyphCount;
    private boolean myFlags[];
    private String myName;
    private long[] myGlyphOffsetTable;
    private int[] myCodeOffsets;
    private Vector[] myGlyphs;
    private int myID;

    private int myFontAscent;
    private int myFontDescent;
    private int myFontLeading;
    private int myFontAdvanceTable[];
    private FRect myFontBoundsTable[];
    private int myFontKerningCount;

    ////////////////////////////////////////////////////////////////////////////

    public static final int FontFlagsHasLayout = 0;
    public static final int FontFlagsShiftJIS = 1;
    public static final int FontFlagsUnicode = 2;
    public static final int FontFlagsAnsi = 3;
    public static final int FontFlagsWideOffsets = 4;
    public static final int FontFlagsWideCodes = 5;
    public static final int FontFlagsItalic = 6;
    public static final int FontFlagsBold = 7;

    ////////////////////////////////////////////////////////////////////////////

    public FFont(int type, int aID) {
        myFontType = type;
        myID = aID;

        myGlyphOffsetTable = null;
        myCodeOffsets = null;
        myGlyphs = null;

        if (type == 2) {
            myFlags = new boolean[8];
        } else {
            myFlags = null;
        }
    }

    ////////////////////////////////////////////////////////////////////////////

    public int getFormat() {
        return myFontType;
    }

    public String getName() {
        return myName;
    }

    public void setName(String aName) {
        myName = aName;
    }

    ////////////////////////////////////////////////////////////////////////////

    public void setGlyphCount(int count) {
        myGlyphCount = count;
    }

    public int getGlyphCount() {
        return myGlyphCount;
    }

    ////////////////////////////////////////////////////////////////////////////

    public void setGlyphOffsetTable(long[] offsets) {
        myGlyphOffsetTable = offsets;
    }

    public long[] getGlyphOffsetTable() {
        return myGlyphOffsetTable;
    }

    ////////////////////////////////////////////////////////////////////////////

    public void setCodeOffsets(int[] offsets) {
        myCodeOffsets = offsets;
    }

    public int[] getCodeOffsets() {
        return myCodeOffsets;
    }

    ////////////////////////////////////////////////////////////////////////////

    public void setGlyphs(Vector[] glyphs) {
        myGlyphs = glyphs;
    }

    public Vector[] getGlyphs() {
        return myGlyphs;
    }

    ////////////////////////////////////////////////////////////////////////////

    public boolean getFlag(int flag) {
        if (myFlags != null && flag >= 0 && flag <= 7) {
            return myFlags[flag];
        } else {
            return false;
        }
    }

    public void setFlag(int flag, boolean value) {
        if (myFlags != null && flag >= 0 && flag <= 7) {
            myFlags[flag] = value;
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    // Layout

    public int getFontAscent() {
        return myFontAscent;
    }

    public void setFontAscent(int value) {
        myFontAscent = value;
    }

    ////////////////////////////////////////////////////////////////////////////

    public int getFontDescent() {
        return myFontDescent;
    }

    public void setFontDescent(int value) {
        myFontDescent = value;
    }

    ////////////////////////////////////////////////////////////////////////////

    public int getFontLeading() {
        return myFontLeading;
    }

    public void setFontLeading(int value) {
        myFontLeading = value;
    }

    ////////////////////////////////////////////////////////////////////////////

    public int getFontKerningCount() {
        return myFontKerningCount;
    }

    public void setFontKerningCount(int value) {
        myFontKerningCount = value;
    }

    ////////////////////////////////////////////////////////////////////////////

    public int[] getFontAdvanceTable() {
        return myFontAdvanceTable;
    }

    public void setFontAdvanceTable(int[] value) {
        myFontAdvanceTable = value;
    }

    ////////////////////////////////////////////////////////////////////////////

    public FRect[] getFontBoundsTable() {
        return myFontBoundsTable;
    }

    public void setFontBoundsTable(FRect[] value) {
        myFontBoundsTable = value;
    }

    ////////////////////////////////////////////////////////////////////////////

    public int getID() {
        return myID;
    }

    public String toString() {
        return "F" + myFontType + ": ID:" + myID + " " + myName + "(" + myGlyphCount + ")";
    }

}