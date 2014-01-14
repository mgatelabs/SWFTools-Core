/*
    Copyright M-Gate Labs 2007
    Can be edited with permission only.
*/
   
   /*
       Bitmap storage
   */

package com.mgatelabs.swftools.support.swf.objects;

// Need Vector Support

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;

public class FBitmap implements FID, FImage {
    public static final int VER1 = 1;
    public static final int VER2 = 2;
    public static final int VER3 = 3;

    private int myID;
    private int myVersion;
    private BufferedImage myImage;

    private int myHeight;
    private int myWidth;

    byte[] myData;

    public FBitmap(int id, byte[] data) {
        myID = id;
        myData = null;
        myVersion = 0;
        try {
            myData = data;
            myImage = ImageIO.read(new ByteArrayInputStream(data));

            myWidth = myImage.getWidth();
            myHeight = myImage.getHeight();
        } catch (Exception e) {
            System.out.println("Data Error: ID:" + myID + " : " + e.getMessage());
            myImage = null;
        }
    }

    public FBitmap(int id, int version, byte[] data, boolean discardData) {
        myID = id;
        myData = null;
        myVersion = version;
        try {
            myData = data;
            myImage = ImageIO.read(new ByteArrayInputStream(data));
            if (discardData) {
                myData = null;
            }
            myWidth = myImage.getWidth();
            myHeight = myImage.getHeight();
        } catch (Exception e) {
            System.out.println("Data Error: ID:" + myID + " : " + e.getMessage());
            myImage = null;
        }
    }

    public FBitmap(int id, byte[] data, byte[] dataA) {
        myID = id;
    }

    public int getHeight() {
        return myHeight;
    }

    public int getWidth() {
        return myWidth;
    }

    public void setVersion(int value) {
        myVersion = value;
    }

    public int getVersion() {
        return myVersion;
    }

    public int getID() {
        return myID;
    }

    public void setData(byte[] bytes) {
        myData = bytes;
    }

    public byte[] getData() {
        return myData;
    }

    public BufferedImage getImage() {
        return myImage;
    }

    public void setImage(BufferedImage aImage) {
        myImage = null;
        myImage = aImage;
    }

    public String toString() {
        if (myImage != null) {
            return "H: " + myImage.getWidth() + " W: " + myImage.getHeight();
        } else {
            return "Null Image";
        }
    }
}