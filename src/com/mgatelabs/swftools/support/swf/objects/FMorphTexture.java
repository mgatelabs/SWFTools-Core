/*
  Copyright M-Gate Labs 2007
  Can be edited with permission only.
*/

// Doesn't Work, Not Implemented!!!!!!

package com.mgatelabs.swftools.support.swf.objects;

public class FMorphTexture extends FFill {
      /*
          BitmapID        	      If type = 0x40 or 0x41 UI16         	      ID of bitmap character for fill
     		BitmapMatrix      	   if type = 0x40 or 0x41 MATRIX      	     Matrix for bitmap fill   
   	*/

    // My Varables
    private int myBitmapID;
    private FMatrix myStartMatrix;
    private FMatrix myEndMatrix;

    // Constructor
    public FMorphTexture(int bitmapID, FMatrix startMatrix, FMatrix endMatrix) {
        super(4);
        myBitmapID = bitmapID;
        myStartMatrix = startMatrix;
        myEndMatrix = endMatrix;
    }

    // Get my Texture Matrix
    public FMatrix getStartMatrix() {
        return myStartMatrix;
    }

    // Get my Texture Matrix
    public FMatrix getEndMatrix() {
        return myEndMatrix;
    }

    // Get The ID of My Bitmap
    public int getBitmapID() {
        return myBitmapID;
    }
}