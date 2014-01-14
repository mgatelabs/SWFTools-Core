/*
  Copyright M-Gate Labs 2007
  Can be edited with permission only.
*/

package com.mgatelabs.swftools.support.swf.objects;

// Solid Fill Class
public class FMorphSolid extends FFill {

    // My Color
    private FColor myAColor;
    private FColor myBColor;

    // Constructor
    public FMorphSolid(FColor aColor, FColor bColor) {
        super(SOLID);
        myAColor = aColor;
        myBColor = bColor;
    }

    public FFill morph(float per) {
        return new FSolid(myAColor.morphTo(myBColor, per));
    }

    // Get my Color
    public FColor getColorA() {
        return myAColor;
    }

    // Get my Color
    public FColor getColorB() {
        return myBColor;
    }
}