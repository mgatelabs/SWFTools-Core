/*
    Copyright M-Gate Labs 2007
    Can be edited with permission only.
*/
   
   /*
       Tinting Information, Not used, Skipped in renderer
   */

package com.mgatelabs.swftools.support.swf.objects;

public class CXFORMALPHA {
       /*
           HasAddTerms        	hasAdd = UB[1]        	      Has color addition values if equal to 1
   		HasMultTerms      	hasMult = UB[1]      	      Has color multiply values if equal to 1     
   		Nbits      	     		nBits = UB[4]      	     		Bits in each value field     
   		RedMultTerm      	   If hasMult SB[nBits]      	   Red multiply value     
   		GreenMultTerm      	If hasMult SB[nBits]      	   Green multiply value     
   		BlueMultTerm      	If hasMult SB[nBits]      	   Blue multiply value
   		AlphaMultTerm      	If hasMult SB[nBits]      	   Alpha multiply value    
   		RedAddTerm      	   If hasAdd SB[nBits]      	   Red addition value     
   		GreenAddTerm      	If hasAdd SB[nBits]      	   Green addition value    
   		BlueAddTerm      	   If hasAdd SB[nBits]      	   Blue addition value
   		AlphaAddTerm      	If hasAdd SB[nBits]      	   Alpha addition value
   	 */

    private int terms[];
    private boolean termFlags[];

    public CXFORMALPHA(boolean add, boolean multi, boolean alpha) {
        terms = new int[8];
        termFlags = new boolean[3];
        termFlags[0] = alpha;
        termFlags[1] = add;
        termFlags[2] = multi;
    }

    public boolean hasAlpha() {
        return termFlags[0];
    }

    public boolean HasAddTerms() {
        return termFlags[1];
    }

    public boolean HasMultTerms() {
        return termFlags[2];
    }

    public void setAdd(int index, int value) {
        terms[index] = value;
    }

    public void setMulti(int index, int value) {
        terms[index + 4] = value;
    }

    public int getAdd(int index) {
        return terms[index];
    }

    public int getMulti(int index) {
        return terms[index + 4];
    }
}