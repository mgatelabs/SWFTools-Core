/*
    Copyright M-Gate Labs 2007
    Can be edited with permission only.
*/
   
   /*
       A Flash Color, Basically a RGBA holder.  Color could have been used in its place, except
   	it doesn't store Alpha.
   */

package com.mgatelabs.swftools.support.swf.objects;

import java.awt.*;

// Flash Color Class, aka RGB || RGBA

public class FColor {

    // My Variables

    private int myRed;
    private int myGreen;
    private int myBlue;
    private int myAlpha;

    // Constructor

    public FColor(int red, int green, int blue, int alpha) {
        myRed = red;
        myGreen = green;
        myBlue = blue;
        myAlpha = alpha;
    }

    // Return Red

    public int getR() {
        return myRed;
    }

    // Return Green

    public int getG() {
        return myGreen;
    }

    // Return Blue

    public int getB() {
        return myBlue;
    }

    // Return Alpha

    public int getA() {
        return myAlpha;
    }

    // Get a Color Object

    public Color toColor() {
        return new Color(myRed, myGreen, myBlue);
    }

    // Get a Inverse Color Object
    // Why not :)

    public Color toInverseColor() {
        return new Color(255 - myRed, 255 - myGreen, 255 - myBlue);
    }

    // to String Mehod

    public String toString() {
        return "FColor(" + myRed + "," + myGreen + "," + myBlue + "," + myAlpha + ")";
    }

    // Colors can morph
    public FColor morphTo(FColor b, float per) {
        float s[] = {myRed, myGreen, myBlue, myAlpha};
        float e[] = {b.getR(), b.getG(), b.getB(), b.getA()};

        for (int x = 0; x < 4; x++) {
            float i = s[x];
            float I = e[x];
            float iI = 0;
            if (i > I) {
                iI = i - I;
                iI = i - (iI * per);
            } else {
                iI = I - i;
                iI = i + (iI * per);
            }
            s[x] = iI;
            //System.out.println("S " + s[x]);
        }

        return new FColor((int) s[0], (int) s[1], (int) s[2], (int) s[3]);
    }
}