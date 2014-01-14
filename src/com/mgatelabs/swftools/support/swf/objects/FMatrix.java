/*
    Copyright M-Gate Labs 2007
    Can be edited with permission only.
*/
   
   /*
   
   Could be replaced with the built in affine transform class, but not going to at this time.
   
   */

package com.mgatelabs.swftools.support.swf.objects;

import java.awt.geom.AffineTransform;

public class FMatrix {
       /*
             HasScale        	  hasScale = UB[1]        	      	Has scale values if equal to 1
     		NScaleBits      	  If hasScale nScaleBits = UB[5]    Bits in each scale value field     
     		ScaleX      	     If hasScale FB[nScaleBits]      	X scale value     
     		ScaleY      	     If hasScale FB[nScaleBits]      	Y scale value     
     		HasRotate      	  hasRotate = UB[1]      	     		Has rotate and skew values if equal to 1     
     		NRotateBits      	  If hasRotate nRotateBits = UB[5]  Bits in each rotate value field     
     		RotateSkew0      	  If hasRotate FB[nRotateBits]    	First rotate and skew value     
    		RotateSkew1      	  If hasRotate FB[nRotateBits]     	Second rotate and skew value     
    		NTranslateBits      nTranslateBits = UB[5]      	   Bits in each translate value field     
     		TranslateX      	  SB[nTranslateBits]      	     		X translate value in twips     
     		TranslateY      	  SB[nTranslateBits]      	     		Y translate value in twips     
   	
   		The ScaleX, ScaleY, RotateSkew0 and RotateSkew1 fields are stored as 16.16 fixed-point values. The TranslateX and TranslateY values are stored as signed values in twips.
   
   		The MATRIX record is optimized for common cases such as matrix that performs a translation only. In this case the HasScale and HasRotate flags are zero, and the matrix only contains the TranslateX and TranslateY fields. 
   	
   		For any coordinates (x, y), the transformed coordinates (x', y') are calculated as follows:
   
    			x' = x * ScaleX + y * RotateSkew1 + TranslateX
    			y' = x * RotateSkew0 + y * ScaleY + TranslateY
   	*/

    private float myScaleX;
    private float myScaleY;
    private float myRotateSkew0;
    private float myRotateSkew1;
    private long myTranslateX;
    private long myTranslateY;
    private AffineTransform savedTransform;

    public FMatrix() {
        myScaleX = 1;
        myScaleY = 1;
        myRotateSkew0 = 0;
        myRotateSkew1 = 0;
        myTranslateX = 0;
        myTranslateY = 0;
        savedTransform = null;
    }

    public FMatrix(float sx, float sy, float s0, float s1, long tx, long ty) {
        myScaleX = sx;
        myScaleY = sy;
        myRotateSkew0 = s0;
        myRotateSkew1 = s1;
        myTranslateX = tx;
        myTranslateY = ty;
        savedTransform = null;
    }

    public FMatrix morphTo(FMatrix b, float per) {
        FMatrix c = new FMatrix();

        // Scale X
        float r1 = myScaleX;
        float r2 = b.getScaleX();
        float r = 0;

        if (r1 > r2) {
            r = r1 - r2;
            r = (r1 - (r * per));
        } else {
            r = r2 - r1;
            r = (r1 + (r * per));
        }
        c.setScaleX(r);

        // Scale X
        r1 = myScaleY;
        r2 = b.getScaleY();
        //float r = 0;

        if (r1 > r2) {
            r = r1 - r2;
            r = (r1 - (r * per));
        } else {
            r = r2 - r1;
            r = (r1 + (r * per));
        }
        c.setScaleY(r);

        // Rotate Skew 0
        r1 = myRotateSkew0;
        r2 = b.getRotateSkew0();
        //float r = 0;

        if (r1 > r2) {
            r = r1 - r2;
            r = (r1 - (r * per));
        } else {
            r = r2 - r1;
            r = (r1 + (r * per));
        }
        c.setRotateSkew0(r);

        // Rotate Skew 1
        r1 = myRotateSkew1;
        r2 = b.getRotateSkew1();
        //float r = 0;

        if (r1 > r2) {
            r = r1 - r2;
            r = (r1 - (r * per));
        } else {
            r = r2 - r1;
            r = (r1 + (r * per));
        }
        c.setRotateSkew1(r);

        // Translate X
        long x1 = myTranslateX;
        long x2 = b.getTranslateX();
        long x = 0;

        if (x1 > x2) {
            x = x1 - x2;
            x = (long) (x1 - (x * per));
        } else {
            x = x2 - x1;
            x = (long) (x1 + (x * per));
        }
        c.setTranslateX(x);

        // Translate Y
        x1 = myTranslateY;
        x2 = b.getTranslateY();
        x = 0;

        if (x1 > x2) {
            x = x1 - x2;
            x = (long) (x1 - (x * per));
        } else {
            x = x2 - x1;
            x = (long) (x1 + (x * per));
        }
        c.setTranslateY(x);

        return c;
    }

    // This is the one function that is used the most.
    public AffineTransform getTransform() {
        if (savedTransform == null) {
            savedTransform = new AffineTransform(myScaleX, myRotateSkew0, myRotateSkew1, myScaleY, myTranslateX / 20.0f, myTranslateY / 20.0f);
        }
        return savedTransform;
    }

    public float getScaleX() {
        return myScaleX;
    }

    public float getScaleY() {
        return myScaleY;
    }

    public float getRotateSkew0() {
        return myRotateSkew0;
    }

    public float getRotateSkew1() {
        return myRotateSkew1;
    }

    public long getTranslateX() {
        return myTranslateX;
    }

    public long getTranslateY() {
        return myTranslateY;
    }

    public void setScaleX(float value) {
        myScaleX = value;
        savedTransform = null;
    }

    public void setScaleY(float value) {
        myScaleY = value;
        savedTransform = null;
    }

    public void setRotateSkew0(float value) {
        //System.out.println("Skew 1 " + value);
        myRotateSkew0 = value;
        savedTransform = null;
    }

    public void setRotateSkew1(float value) {
        //System.out.println("Skew 0 " + value);
        myRotateSkew1 = value;
        savedTransform = null;
    }

    public void setTranslateX(long value) {
        myTranslateX = value;
        savedTransform = null;
    }

    public void setTranslateY(long value) {
        myTranslateY = value;
        savedTransform = null;
    }

    public String toString() {
        return "SX: " + myScaleX + " SY: " + myScaleY + " R0: " + myRotateSkew0 + " R1: " + myRotateSkew1 + " TX: " + (myTranslateX / 20.0f) + " TY: " + (myTranslateY / 20.0f);
    }

    // Check if it has Scale
    public boolean hasScale() {
        if (myScaleX <= 0.99f || myScaleX >= 1.01f) {
            return true;
        }

        if (myScaleY <= 0.999f || myScaleY >= 1.001f) {
            return true;
        }

        return false;
    }

    // Check if it has Scale
    public boolean hasSkew() {
        if (myRotateSkew1 <= -0.001f || myRotateSkew1 >= 0.001f) {
            return true;
        }

        if (myRotateSkew0 <= -0.001f || myRotateSkew0 >= 0.001f) {
            return true;
        }

        return false;
    }

    // Check if it has Translate
    public boolean hasTranslate() {
        if (myTranslateX != 0 || myTranslateY != 0) {
            return true;
        }

        return false;
    }
}