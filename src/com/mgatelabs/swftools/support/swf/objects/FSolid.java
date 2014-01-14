/*
    Copyright M-Gate Labs 2007
    Can be edited with permission only.
*/

package com.mgatelabs.swftools.support.swf.objects;

// Solid Fill Class
public class FSolid extends FFill {

    // My Color
    private FColor myColor;

    // Constructor
    public FSolid(FColor aColor) {
        super(SOLID);
        myColor = aColor;
    }

    // Get my Color
    public FColor getColor() {
        return myColor;
    }

    // Set Color isn't needed, but could be added.
}