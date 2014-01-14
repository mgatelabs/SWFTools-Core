/*
    Copyright M-Gate Labs 2007
    Can be edited with permission only.
*/
   
   /*
       It Works, that is all I can say about it.
   */

package com.mgatelabs.swftools.support.swf.objects;

public class FSound implements FID {
    private int myId;// = in.readUI16();
    private int myFormat;//      = (int)in.readUBits( 4 );
    private int myFrequency;//   = (int)in.readUBits( 2 );
    private boolean myBits16;//  = in.readUBits(1) != 0;
    private boolean myStereo;//  = in.readUBits(1) != 0;
    private long mySampleCount;// = (int)in.readUI32();
    private int[] myData;

    public static final int RAW = 0;
    public static final int ADPCM = 1;
    public static final int MP3 = 2;
    public static final int Uncompressed = 3;
    public static final int Nellymoser = 4;

    public FSound(int aId, int aFormat, int aFrequency, boolean isBits16, boolean isStereo, long aSampleCount, int[] aData) {
        // Setup everything
        myId = aId;
        myFormat = aFormat;
        myFrequency = aFrequency;
        myBits16 = isBits16;
        myStereo = isStereo;
        mySampleCount = aSampleCount;
        myData = aData;
    }

    public String getFormatString() {
        String val = "";

        switch (myFormat) {
            case 0:
                val = "RAW";
                break;
            case 1:
                val = "ADPCM";
                break;
            case 2:
                val = "MP3";
                break;
            case 3:
                val = "Uncompressed";
                break;
            case 4:
                val = "Nellymoser";
                break;
        }

        return val;
    }

    public int getFormat() {
        return myFormat;
    }

    public int getID() {
        return myId;
    }

    public int[] getData() {
        return myData;
    }

    public void setData(int[] dat) {
        myData = dat;
    }
}