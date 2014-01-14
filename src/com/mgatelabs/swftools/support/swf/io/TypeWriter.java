/*
    Copyright M-Gate Labs 2007
*/

/**
 <p>
 <b>Flash Type Reader</b>
 </p>
 <p>
 Used to write flash files at the type level.  <b>Example</b> Strings, Rects, Gradients...
 </p>
 */

package com.mgatelabs.swftools.support.swf.io;

import com.mgatelabs.swftools.support.swf.objects.FMatrix;
import com.mgatelabs.swftools.support.swf.objects.FRect;
import com.mgatelabs.swftools.support.swf.tags.PlaceObject;

import java.io.IOException;

public class TypeWriter {
    private BitOutput bo;
    private int version;

    // Initilize
    public TypeWriter(BitOutput bo, int version) {
        this.bo = bo;
        this.version = version;
    }

    // Write a String
    public void string(String data) throws IOException {
        for (int x = 0; x < data.length(); x++) {
            bo.writeByte(data.charAt(x));
        }
        bo.writeByte(0);
    }

    // Write a Rect
    public void rect(FRect aRect) throws IOException {
        int minBits = 1;
        int temp = 0;

        temp = bo.determineMinimumBitCount(aRect.xmin);
        if (temp > minBits) {
            minBits = temp;
        }
        temp = bo.determineMinimumBitCount(aRect.ymin);
        if (temp > minBits) {
            minBits = temp;
        }
        temp = bo.determineMinimumBitCount(aRect.xmax);
        if (temp > minBits) {
            minBits = temp;
        }
        temp = bo.determineMinimumBitCount(aRect.ymax);
        if (temp > minBits) {
            minBits = temp;
        }

        bo.writeBits(minBits, 5);

        //System.out.println("MinBits: " + minBits);

        bo.writeBits(aRect.xmin, minBits);
        bo.writeBits(aRect.xmax, minBits);
        bo.writeBits(aRect.ymin, minBits);
        bo.writeBits(aRect.ymax, minBits);
    }

    // Other Rect, Determine Bits
    public long _rect(FRect aRect) {
        int minBits = 1;
        int temp = 0;

        temp = bo.determineMinimumBitCount(aRect.xmin);
        if (temp > minBits) {
            minBits = temp;
        }
        temp = bo.determineMinimumBitCount(aRect.ymin);
        if (temp > minBits) {
            minBits = temp;
        }
        temp = bo.determineMinimumBitCount(aRect.xmax);
        if (temp > minBits) {
            minBits = temp;
        }
        temp = bo.determineMinimumBitCount(aRect.ymax);
        if (temp > minBits) {
            minBits = temp;
        }

        temp = 5 + (4 * minBits);
        minBits = temp / 8;
        if (temp % 8 != 0) {
            minBits++;
        }
        return minBits;
    }

    // Write a Tag
    public void tag(int id, byte[] data) throws IOException {
        long temp = 0;
        temp = id;
        temp = temp << 6;

        if (data.length > 62) {
            temp = temp | 63;
            bo.ui16(temp);
            bo.ui32(data.length);
        } else {
            temp = temp | data.length;
            bo.ui16(temp);
        }

        bo.writeBytes(data);
    }

    public void matrix(FMatrix aMatrix) throws IOException {
          /*
              HasScale        	hasScale = UB[1]        	      Has scale values if equal to 1
      		NScaleBits      	If hasScale nScaleBits = UB[5]   Bits in each scale value field     
           	ScaleX      	   If hasScale FB[nScaleBits]      	X scale value     
           	ScaleY      	   If hasScale FB[nScaleBits]      	Y scale value     
           	HasRotate      	hasRotate = UB[1]      	         Has rotate and skew values if equal to 1     
           	NRotateBits      	If hasRotate nRotateBits = UB[5] Bits in each rotate value field     
           	RotateSkew0      	If hasRotate FB[nRotateBits]     First rotate and skew value     
           	RotateSkew1      	If hasRotate FB[nRotateBits]     Second rotate and skew value     
           	NTranslateBits   	nTranslateBits = UB[5]      	   Bits in each translate value field     
           	TranslateX     	SB[nTranslateBits]      	      X translate value in twips     
           	TranslateY      	SB[nTranslateBits]      	      Y translate value in twips  
      	*/

        // Align the Bytes First

        bo.align();

        // Scale
        if (aMatrix.hasScale()) {
            // Has Scale
            bo.writeBits(1, 1);

            // Get the Two Long Values
            long sxl = bo.getLongFromFloat(aMatrix.getScaleX());
            long syl = bo.getLongFromFloat(aMatrix.getScaleY());

            // Determine the Largest Size
            int sxs = bo.determineMinimumBitCount(sxl);
            int sys = bo.determineMinimumBitCount(syl);
            int ss = sxs > sys ? sxs : sys;

            // Write the Bit Size
            bo.writeBits(ss, 5);

            // Wite X
            bo.writeBits(sxl, ss);
            // Write Y
            bo.writeBits(syl, ss);
        } else {
            // No Scale
            bo.writeBits(0, 1);
        }

        // Rotate
        if (aMatrix.hasSkew()) {
            // Has Scale
            bo.writeBits(1, 1);

            // Get the Two Long Values
            long s0l = bo.getLongFromFloat(aMatrix.getRotateSkew0());
            long s1l = bo.getLongFromFloat(aMatrix.getRotateSkew1());

            // Determine the Largest Size
            int s0s = bo.determineMinimumBitCount(s0l);
            int s1s = bo.determineMinimumBitCount(s1l);
            int ss = s0s > s1s ? s0s : s1s;

            // Write the Bit Size
            bo.writeBits(ss, 5);

            // Wite Skew 0
            bo.writeBits(s0l, ss);
            // Write Skew 1
            bo.writeBits(s1l, ss);
        } else {
            // No Scale
            bo.writeBits(0, 1);
        }

        // Translate, Always Written
        //if (aMatrix.hasTranslate())
        {
            // Has Scale
            //bo.writeBits(1, 1);

            // Get the Two Long Values
            long sxl = aMatrix.getTranslateX();
            long syl = aMatrix.getTranslateY();

            // Determine the Largest Size
            int sxs = bo.determineMinimumBitCount(sxl);
            int sys = bo.determineMinimumBitCount(syl);
            int ss = sxs > sys ? sxs : sys;

            // Write the Bit Size
            bo.writeBits(ss, 5);

            // Translate X
            bo.writeBits(sxl, ss);
            // Translate Y
            bo.writeBits(syl, ss);
        }

        // Align in the End
        bo.align();
    }

    public void PlaceObject(PlaceObject po, int type) throws IOException {
        if (type == 1) {
            // Write ID
            bo.ui16(po.getID());

            // Write Depth
            bo.ui16(po.getDepth());

            // Write Matrix
            matrix(po.getMatrix());
        } else if (type <= 3) {
            // Add Extra Junk for Version 3
            if (type == 3) {
                bo.ui8(0);
            }

            // Make Flags
            int flag = 0;

            if (po.getPlaceFlagReserved()) {
                flag = flag | 128;
            }

            if (po.getPlaceFlagHasClipActions()) {
                flag = flag | 64;
            }

            if (po.getPlaceFlagHasName()) {
                flag = flag | 32;
            }

            if (po.getPlaceFlagHasRatio()) {
                flag = flag | 16;
            }

            if (po.getPlaceFlagHasColorTransform()) {
                flag = flag | 8;
            }

            if (po.getPlaceFlagHasMatrix()) {
                flag = flag | 4;
            }

            if (po.getPlaceFlagHasCharacter()) {
                flag = flag | 2;
            }

            if (po.getPlaceFlagMove()) {
                flag = flag | 1;
            }

            // Write Flag
            bo.ui8(flag);

            // Write Depth
            bo.ui16(po.getDepth());

            // Write Character
            if (po.getPlaceFlagHasCharacter()) {
                bo.ui16(po.getID());
            }

            // Write Matrix
            if (po.getPlaceFlagHasMatrix()) {
                matrix(po.getMatrix());
            }

            // Skip ColorTransform

            // Ratio
            if (po.getPlaceFlagHasRatio()) {
                bo.ui16(po.getRatio());
            }

            // Name
            if (po.getPlaceFlagHasName()) {
                string(po.getName());
            }

            // Clipping
            if (po.getPlaceFlagHasClipActions()) {
                bo.ui16(po.getClipDepth());
            }
        }
    }

}