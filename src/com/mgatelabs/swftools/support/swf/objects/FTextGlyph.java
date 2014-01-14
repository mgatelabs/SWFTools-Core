package com.mgatelabs.swftools.support.swf.objects;

public class FTextGlyph {
   /*
   TextGlyphIndex			UB [nGlyphBits]		Glyph index into current font      
   TextGlyphAdvance		SB [nAdvanceBits]		X advance value for glyph  
   */

    private int myGlyphIndex;
    private int myGlyphAdvance;

    public FTextGlyph(int aIndex, int aAdvance) {
        myGlyphIndex = aIndex;
        myGlyphAdvance = aAdvance;
    }

    public int getIndex() {
        return myGlyphIndex;
    }

    public int getAdvance() {
        return myGlyphAdvance;
    }

    public String toString() {
        return myGlyphIndex + ":" + myGlyphAdvance;
    }
}