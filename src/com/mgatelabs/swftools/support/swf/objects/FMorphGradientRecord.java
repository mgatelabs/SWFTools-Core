/*
  Copyright M-Gate Labs 2007
  Can be edited with permission only.
*/

package com.mgatelabs.swftools.support.swf.objects;

// Flash Gradient Record
public class FMorphGradientRecord {
       /*
           Ratio        	UI8         	           Ratio value
     		Color      	   RGB  (Shape1 or Shape2)	Color of gradient
   							RGBA (Shape3)      	     
   	*/

    // My Variables
    private int myStartRatio;
    private int myEndRatio;
    private FColor myStartColor;
    private FColor myEndColor;

    // Constructor
    public FMorphGradientRecord(int startRatio, FColor startColor, int endRatio, FColor endColor) {
        myStartRatio = startRatio;
        myEndRatio = endRatio;
        myStartColor = startColor;
        myEndColor = endColor;
    }

    // Get My Radio
    public int getStartRatio() {
        return myStartRatio;
    }

    // Get My Radio
    public int getEndRatio() {
        return myEndRatio;
    }

    // Get My Color
    public FColor getStartColor() {
        return myStartColor;
    }

    // Get My Color
    public FColor getEndColor() {
        return myEndColor;
    }
}