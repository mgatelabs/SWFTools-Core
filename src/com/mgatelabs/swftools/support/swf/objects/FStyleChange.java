/*
    Copyright M-Gate Labs 2007
    Can be edited with permission only.
*/

package com.mgatelabs.swftools.support.swf.objects;

import java.util.Vector;

public class FStyleChange {
       /*
         TypeFlag        	      UB[1] = 0        	      Non-edge record flag
   	  StateNewStyles      	     NewStyles = UB[1]      	     New styles flag. Used by DefineShape2 and DefineShape3 only.     
   	  StateLineStyle      	     LineStyle = UB[1]      	     Line style change flag     
   	  StateFillStyle1      	     FillStyle1 = UB[1]      	     Fill style 1 change flag     
   	  StateFillStyle0      	     FillStyle0 = UB[1]      	     Fill style 0 change flag     
   	  StateMoveTo      	     MoveTo = UB[1]      	     Move to flag     
   	  MoveBits      	     If moveTo nMoveBits = UB[5]      	     Move bit count     
   	  MoveDeltaX      	     If moveTo SB[nMoveBits]      	     Delta X value     
   	  MoveDeltaY      	     If moveTo SB[nMoveBits]      	     Delta Y value     
   	  FillStyle0      	     If fillStyle0 UB[nFillBits]      	     Fill 0 Style     
   	  FillStyle1      	     If fillStyle1 UB[nFillBits]      	     Fill 1 Style     
   	  LineStyle      	     If lineStyle UB[nLineBits]      	     Line Style     
   	  FillStyles      	     If newStyles FillStyleArray      	     Array of new fill styles     
   	  LineStyles      	     If newStyles LineStyleArray      	     Array of new line styles     
   	  NumFillBits      	     If newStyles NfillBits = UB[4]      	     Number of fill index bits for new styles     
   	  NumLineBits      	     If newStyles NlineBits = UB[4]      	     Number of line index bits for new styles
   	*/

    private boolean isNewStyles;
    private boolean isLineStyle;
    private boolean isFillStyle1;
    private boolean isFillStyle0;
    private boolean isMoveTo;

    private long MoveDeltaX;
    private long MoveDeltaY;
    private int FillStyle0;
    private int FillStyle1;
    private int LineStyle;
    private Vector FillStyleArray;
    private Vector LineStyleArray;

    public FStyleChange() {
        // Default Everything
        isNewStyles = false;
        isLineStyle = false;
        isFillStyle1 = false;
        isFillStyle0 = false;
        isMoveTo = false;

        MoveDeltaX = 0;
        MoveDeltaY = 0;

        FillStyle0 = 0;
        FillStyle1 = 0;
        LineStyle = 0;

        FillStyleArray = new Vector();
        LineStyleArray = new Vector();
    }

    public void setMoveDeltaX(long aValue) {
        isMoveTo = true;
        MoveDeltaX = aValue;
    }

    public void setMoveDeltaY(long aValue) {
        isMoveTo = true;
        MoveDeltaY = aValue;
    }

    public void setFillStyle0(int aValue) {
        isFillStyle0 = true;
        FillStyle0 = aValue;
    }

    public void setFillStyle1(int aValue) {
        isFillStyle1 = true;
        FillStyle1 = aValue;
    }

    public void setLineStyle(int aValue) {
        isLineStyle = true;
        LineStyle = aValue;
    }

    public int getFillStyle0() {
        return FillStyle0;
    }

    public int getFillStyle1() {
        return FillStyle1;
    }

    public int getLineStyle() {
        return LineStyle;
    }

    public long getMoveDeltaX() {
        return MoveDeltaX;
    }

    public long getMoveDeltaY() {
        return MoveDeltaY;
    }

    public Vector getFillStyleArray() {
        isNewStyles = true;
        return FillStyleArray;
    }

    public Vector getLineStyleArray() {
        isNewStyles = true;
        return LineStyleArray;
    }

    public boolean isNewStyles() {
        return isNewStyles;
    }

    public boolean isLineStyle() {
        return isLineStyle;
    }

    public boolean isFillStyle1() {
        return isFillStyle1;
    }

    public boolean isFillStyle0() {
        return isFillStyle0;
    }

    public boolean isMoveTo() {
        return isMoveTo;
    }
}