package com.mgatelabs.swftools.support.swf.objects;

import java.util.Vector;

public class FText implements FID {
    private int myID;
    private int myVersion;
    private FRect myRect;
    private FMatrix myMatrix;
    private Vector myRecords;

   	/*
       Header        	  	 	   RECORDHEADER        	      Tag ID = 11
   	TextId						UI16								ID for this character     
   	TextBounds				RECT								Bounds of the text     
   	TextMatrix				MATRIX							Matrix for the text     
   	TextGlyphBits			nGlyphBits = UI8				Bits in each glyph index     
   	TextAdvanceBits			nAdvanceBits = UI8			Bits in each advance value     
   	TextRecords				TEXTRECORD[zero or more]	Text records     
   	TextEndOfRecordsFlag	UI8 = 0							Always set to zero   
   	*/

    // Constructor

    public FText(int aVersion, int aID) {
        myVersion = aVersion;
        myID = aID;
        myRect = null;
        myMatrix = null;
    }

    // Get this Font's ID
    public int getID() {
        return myID;
    }

    // Bounds

    public void setRect(FRect aRect) {
        myRect = aRect;
    }

    public FRect getRect() {
        return myRect;
    }

    // Matrix

    public void setMatrix(FMatrix aMatrix) {
        myMatrix = aMatrix;
    }

    public FMatrix getMatrix() {
        return myMatrix;
    }

    // Records

    public void setRecords(Vector aRecordSet) {
        myRecords = aRecordSet;
    }

    public Vector getRecords() {
        return myRecords;
    }
}