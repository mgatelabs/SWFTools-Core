/*
    Copyright M-Gate Labs 2007
    Can be edited with permission only.
*/

package com.mgatelabs.swftools.support.swf.objects;

public class FTexture extends FFill {
      /*
          BitmapID        	      If type = 0x40 or 0x41 UI16         	      ID of bitmap character for fill
     		BitmapMatrix      	   if type = 0x40 or 0x41 MATRIX      	     Matrix for bitmap fill   
   	*/

    // My Varables
    private int myBitmapID;
    private FImage myBitmap;
    private FMatrix myMatrix;


    // Constructor
    public FTexture(int style, int bitmapID, FMatrix matrix) {
        super(style);
        myBitmapID = bitmapID;
        myMatrix = matrix;

        myMatrix.setScaleX(myMatrix.getScaleX() / 20.0f);
        myMatrix.setScaleY(myMatrix.getScaleY() / 20.0f);

        myBitmap = null;
    }

    // Get my Texture Matrix
    public FMatrix getMatrix() {
        return myMatrix;
    }

    // Get The ID of My Bitmap
    public int getBitmapID() {
        return myBitmapID;
    }

    // Set Bitmap
    public void setBitmap(FImage aBitmap) {
        myBitmap = aBitmap;
    }

    // Get Bitmap
    public FImage getBitmap() {
        return myBitmap;
    }

    public String toString() {
        return "Texture: (" + myBitmapID + ") " + myBitmap + " Matrix: " + myMatrix;
    }
}