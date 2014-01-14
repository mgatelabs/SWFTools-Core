/*
    Copyright M-Gate Labs 2007
*/

/**
 <p>
 <b>Binary Output Class</b>
 </p>
 <p>
 Used to write flash files.
 </p>
 <p>
 Not Complete!  Limited Write Support.
 </p>
 */

package com.mgatelabs.swftools.support.swf.io;

import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.DeflaterOutputStream;

public class BitOutput {
    private OutputStream myStream;
    private long buffer;
    private int bufferSize;
    private long bytesWritten;

    public BitOutput(OutputStream aStream) {
        myStream = aStream;
        buffer = 0;
        bufferSize = 0;
        bytesWritten = 0;
    }

    public void useCompression() throws IOException {
        myStream = new DeflaterOutputStream(myStream);
    }

    public void close() throws IOException {
        myStream.close();
    }

    public void align() throws IOException {
        flushStream();
    }

    public void flushStream() throws IOException {
        if (bufferSize > 0) {
            buffer = (buffer << (8 - bufferSize));
            myStream.write((int) buffer);
            buffer = 0;
            bufferSize = 0;
        }
    }

    public void writeBits(long data, int size) throws IOException {
        long temp = 0;
        while (size > 0) {
            if (size >= 8) {
                temp = data;
                temp = (temp >> (size - 8));
                temp = temp & 0xff;
                size -= 8;
                buffer = buffer << 8;
                bufferSize += 8;
                buffer = buffer | temp;
            } else {
                temp = data;
                temp = (temp >> (size - size));
                temp = temp & 0xff;
                buffer = buffer << size;
                bufferSize += size;
                size = 0;
                buffer = buffer | temp;
            }

            if (bufferSize >= 8) {
                temp = buffer;
                temp = (buffer >> (bufferSize - 8)) & 0xff;
                bufferSize -= 8;
                myStream.write((int) temp);
            }
        }
    }

    public void writeByte(int data) throws IOException {
        flushStream();
        myStream.write(data);
    }

    public void writeBytes(byte[] data) throws IOException {
        flushStream();
        for (int x = 0; x < data.length; x++) {
            myStream.write(data[x]);
        }
    }

    public void ui8(int data) throws IOException {
        writeByte(data & 0xff);
    }

    public void ui16(long data) throws IOException {
        ui8((int) data);
        data = data >> 8;
        ui8((int) data);
    }

    public void ui32(long data) throws IOException {
        ui16(data);
        data = data >> 16;
        ui16(data);
    }

    public long getLongFromFloat(float data) {
        return (long) (data * 65536.0f);
    }

    public int determineMinimumBitCount(long data) {
        long last = -1;
        long temp = 0;
        int count = 0;
        int pos = 1;

        //System.out.println("Min: " + data);

        while (count < 32) {
            temp = data & 1;
            data = data >> 1;
            if (temp != last) {
                pos = count + 1;
                last = temp;
            }
            count++;
        }

        return pos;
    }
}