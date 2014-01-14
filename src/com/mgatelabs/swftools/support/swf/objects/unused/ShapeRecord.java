package com.mgatelabs.swftools.support.swf.objects;

public abstract class ShapeRecord {
    public static final int CHANGE = 0;
    public static final int STRAIGHT = 0;
    public static final int CURVE = 0;

    public int myType;

    public ShapeRecord(int aType) {
        myType = aType;
    }

    public int getType() {
        return myType;
    }
}