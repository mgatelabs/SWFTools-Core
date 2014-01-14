/*
    Copyright M-Gate Labs 2007
*/

/**
 <p>
 <b>Flash Tag Block</b>
 </p>
 <p>
 Used to hold a Flash Tag Block.  Each swf file is made up of many blocks.
 </p>
 */

package com.mgatelabs.swftools.support.swf.tags;

import java.util.Vector;

public class TBlock {

    private int myId;
    private long mySize;
    private byte[] myData;
    private Vector myVector;

    public TBlock(int id, long size) {
        myId = id;
        mySize = size;
        myData = null;
        myVector = null;
    }

    public int getId() {
        return myId;
    }

    public long getSize() {
        return mySize;
    }

    public byte[] getData() {
        return myData;
    }

    public Vector getVector() {
        return myVector;
    }

    public void setData(byte[] data) {
        myData = data;
    }

    public void setVector(Vector data) {
        myVector = data;
    }

    public String toString() {
        String result = Tag.getTageName(myId) + ",";

        if (Tag.getObjectable(myId)) {
            result += "ID: " + getDataID(this);
        } else {
            result += "Size: " + mySize;
        }

        return result;
    }

    public static boolean setDataID(TBlock aBlock, int id) {
        if (aBlock.getData().length >= 2) {
            byte[] data = aBlock.getData();

            data[1] = (byte) ((id >> 8) & 0xff);
            data[0] = (byte) (id & 0xff);

            return true;
        }

        return false;
    }


    public static int getDataID(TBlock aBlock) {
        if (aBlock.getData().length >= 2) {
            byte[] data = aBlock.getData();
            int result = 0;

            result = 0xff & data[1];
            result = result << 8;
            result = result | (0xff & data[0]);

            return result;
        }
        return -1;
    }

}