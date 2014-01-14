package com.mgatelabs.swftools.support.swf.objects;

public class FTextRecord {
    /*
    Field							Type										Comment
    TextRecordType				UB [1] = 1								1 if text stage change record
    TextReserved				UB [3]									Reserved � always 0
    TextHasFont					HasFont = UB [1]						1 if text font specified
    TextHasColor				HasColor = UB [1]						1 if text color specified
    TextHasYOffset				HasYOffset = UB [1]					1 if Y offset specified
    TextHasXOffset				HasXOffset = UB [1]					1 if X offset specified
    TextFontID					if hasFont UI16						Font ID for following text
    TextColor					If hasColor RGB
    If this record is part	of a DefineText2 tag then RGBA	Font color for following text
    TextXOffset					If hasXOffset SI16					Font X offset for following text
    TextYOffset					If hasYOffset SI16					Font Y offset for following text
    TextHeight					If hasFont UI16						Font height for following text
    */
    private int myVersion;
    private FFont myFont;
    private FColor myColor;
    private boolean hasOffsetX;
    private boolean hasOffsetY;
    private int myOffsetX;
    private int myOffsetY;
    private int myHeight;

    public FTextRecord(int aVersion) {
        myVersion = aVersion;
        myFont = null;
        myColor = null;
        myOffsetX = 0;
        myOffsetY = 0;
        myHeight = 0;
        hasOffsetX = false;
        hasOffsetY = false;
    }

    // Font

    public FFont getFont() {
        return myFont;
    }

    public void setFont(FFont aFont) {
        myFont = aFont;
    }

    // Color

    public FColor getColor() {
        return myColor;
    }

    public void setColor(FColor aColor) {
        myColor = aColor;
    }

    // Offset X

    public int getOffsetX() {
        return myOffsetX;
    }

    public boolean hasOffsetX() {
        return hasOffsetX;
    }

    public void setOffsetX(int aOffset) {
        myOffsetX = aOffset;
        hasOffsetX = true;
    }

    // Offset Y

    public int getOffsetY() {
        return myOffsetY;
    }

    public void setOffsetY(int aOffset) {
        myOffsetY = aOffset;
        hasOffsetY = true;
    }

    public boolean hasOffsetY() {
        return hasOffsetY;
    }

    // Height

    public int getHeight() {
        return myHeight;
    }

    public void setHeight(int aHeight) {
        myHeight = aHeight;
    }

}