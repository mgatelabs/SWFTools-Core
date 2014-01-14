package com.mgatelabs.swftools.support.swf.objects;

public class FMorphLine extends FLine {
    // My Varables
    private FLine lineA, lineB;

    // Constructor

    public FMorphLine(long aStartWidth, long aEndWidth, FColor aStartColor, FColor aEndColor) {
        lineA = new FLine(aStartWidth, aStartColor);
        lineB = new FLine(aEndWidth, aEndColor);
    }

    public FLine morph(float percentage) {
        float wA = lineA.getWidth() / 20.0f;
        float wB = lineB.getWidth() / 20.0f;
        float wAB = wA;
        float lD = 0;

        if (wA > wB) {
            lD = wA - wB;
            wAB = wA - (lD * percentage);
        } else {
            lD = wB - wA;
            wAB = wA + (lD * percentage);
        }

        return new FLine(wAB, lineA.getColor().morphTo(lineB.getColor(), percentage));
    }

    // Get Start Color

    public FLine getStartLine() {
        return lineA;
    }

    // Get End Color

    public FLine getEndLine() {
        return lineB;
    }
    // to String Method

    public String toString() {
        return lineA + " -> " + lineB;
    }

}