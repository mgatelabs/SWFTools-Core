/*
    Copyright M-Gate Labs 2007
    Can be edited with permission only.
*/

package com.mgatelabs.swftools.support.swf.objects;

// Flash Gradient Record
public class FGradientRecord {
       /*
           Ratio        	UI8         	           Ratio value
     		Color      	   RGB  (Shape1 or Shape2)	Color of gradient
   							RGBA (Shape3)      	     
   	*/

    // My Variables
    private int myRatio;
    private FColor myColor;

    // Constructor
    public FGradientRecord(int ratio, FColor aColor) {
        myRatio = ratio;
        myColor = aColor;
    }

    // Get My Radio
    public int getRatio() {
        return myRatio;
    }

    // Get My Color
    public FColor getColor() {
        return myColor;
    }
}