/*
    Copyright M-Gate Labs 2007
    Can be edited with permission only.
*/

package com.mgatelabs.swftools.support.swf.objects;

import java.util.Vector;

public class FGradient extends FFill {
      /*
          GradientMatrix        	      If type = 0x10 or 0x12 MATRIX         	      Matrix for gradient fill
     		Gradient      	     If type = 0x10 or 0x12 GRADIENT      	     Gradient fill   
   	*/

    private FMatrix myMatrix;
    private Vector myGradient;
    private float alpha;

    public FGradient(int format, int style, FMatrix matrix, Vector gradient) {
        super(style);
        myMatrix = matrix;
        myGradient = gradient;

        int temp = 0;
        alpha = 0;
        for (int x = 0; x < myGradient.size(); x++) {
            FGradientRecord a = (FGradientRecord) myGradient.get(x);
            temp += a.getColor().getA();
        }
        alpha = (float) temp / myGradient.size();
    }

    public void setAlpha(float aval) {
        alpha = aval;
    }

    public float getAlpha() {
        return alpha;
    }

    public FMatrix getMatrix() {
        return myMatrix;
    }

    public Vector getGradient() {
        return myGradient;
    }
}