/*
    Copyright M-Gate Labs 2007
    Can be edited with permission only.
*/

package com.mgatelabs.swftools.support.swf.objects;

import java.util.Vector;

public class FMorphGradient extends FFill {
      /*
          GradientMatrix        	      If type = 0x10 or 0x12 MATRIX         	      Matrix for gradient fill
     		Gradient      	     If type = 0x10 or 0x12 GRADIENT      	     Gradient fill   
   	*/

    private FMatrix myStartMatrix;
    private FMatrix myEndMatrix;
    private Vector myGradient;
    private float alpha;

    public FMorphGradient(int style, FMatrix aStartMatrix, FMatrix aEndMatrix, Vector gradient) {
        super(style);
        myStartMatrix = aStartMatrix;
        myEndMatrix = aEndMatrix;
        myGradient = gradient;

        int temp = 0;
        alpha = 0;
        for (int x = 0; x < myGradient.size(); x++) {
            FMorphGradientRecord a = (FMorphGradientRecord) myGradient.get(x);
            temp += a.getStartColor().getA();
        }
        alpha = (float) temp / myGradient.size();
    }

    // Make a Morphed Shape
    public FFill morph(float per) {
        Vector newGradients = new Vector();

        for (int x = 0; x < myGradient.size(); x++) {
            FMorphGradientRecord a = (FMorphGradientRecord) myGradient.get(x);
            int r1 = a.getStartRatio();
            int r2 = a.getEndRatio();
            int r = 0;

            if (r1 > r2) {
                r = r1 - r2;
                r = (int) (r1 - (r * per));
            } else {
                r = r2 - r1;
                r = (int) (r1 + (r * per));
            }

            newGradients.add(new FGradientRecord(r, a.getStartColor().morphTo(a.getEndColor(), per)));
        }

        return new FGradient(3, getStyle(), myStartMatrix.morphTo(myEndMatrix, per), newGradients);
    }

    public void setAlpha(float aval) {
        alpha = aval;
    }

    public float getAlpha() {
        return alpha;
    }

    public FMatrix getStatMatrix() {
        return myStartMatrix;
    }

    public FMatrix getEndMatrix() {
        return myEndMatrix;
    }

    public Vector getGradient() {
        return myGradient;
    }
}