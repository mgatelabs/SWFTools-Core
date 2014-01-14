package com.mgatelabs.swftools.support.swf.objects;

import java.awt.image.BufferedImage;

public class FLossless implements FID, FImage {
    int myID;
    int myWidth;
    int myHeight;
    int myFormat;
    int myVersion;
    BufferedImage myImage;

    public FLossless(int version, int id, int format, int width, int height) {
        myVersion = version;
        myID = id;
        myFormat = format;
        myWidth = width;
        myHeight = height;
        myImage = null;
    }

    public FLossless(FBitmap b) {
        myVersion = 3;
        myID = b.getID();
        myFormat = 5;
        myWidth = b.getWidth();
        myHeight = b.getWidth();
        myImage = b.getImage();
    }

    public int getID() {
        return myID;
    }

    public int getVersion() {
        return myVersion;
    }

    public int getFormat() {
        return myFormat;
    }

    public int getWidth() {
        return myWidth;
    }

    public int getHeight() {
        return myHeight;
    }

    public BufferedImage getImage() {
        return myImage;
    }

    public void setImage(BufferedImage aImage) {
        myImage = aImage;
    }
}