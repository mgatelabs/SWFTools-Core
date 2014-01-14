/*
    Copyright M-Gate Labs 2007
*/

/**
 <p>
 <b>Binary Input Class</b>
 </p>
 <p>
 Used to process flash files at the bit level.
 </p>
 */

// Set Package
package com.mgatelabs.swftools.support.swf.io;

// Program Infomration

import com.mgatelabs.swftools.exploit.control.AppControl;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.InflaterInputStream;

// Java I/O
// Java Compression

public class BitInput {
    // My Input Stream
    private InputStream myStream;
    // Bit Offset
    private int offset;
    // Bit Storage
    private long register;
    // Bytes Read, Misleading
    public long bitsRead;
    // Is com.mgatelabs.swftools.exploit running in debug mode
    private boolean isDebug;

    //private Stack myStreams;

    // Constructure

    public BitInput(InputStream aStream) {
        // Set the Stream
        myStream = aStream;
        //myStreams = new Stack();

        // Reset Variables
        offset = 0;
        register = 0;
        bitsRead = 0;

        // Set debug info
        this.isDebug = AppControl.getInfo().getDebug();
        if (isDebug) {
            System.out.println("Binary Input: Active");
        }
    }

    public void useCompressedStream() {
        //myStreams.push(myStream);
        // Make a new Compressed Stream by wrapping the old
        myStream = new InflaterInputStream(myStream);
    }

    public void zipStream() {
        //myStreams.push(myStream);
        // Make a new Compressed Stream by wrapping the old
        myStream = new InflaterInputStream(myStream);
        //System.out.println("Compres->"+myStream);
    }

    //public void unZipStream()
    //{
    //myStream = (InputStream)myStreams.pop();
    //System.out.println("Uncompres->"+myStream);
    //}

    // Close the Streams
    public void close() throws IOException {
        myStream.close();
    }

    // Read Byte
    public int readByte() throws IOException {
        offset = 0;
        bitsRead++;
        return myStream.read() & 0xFF;
    }

    // Read Multiple Bytes as Ints
    public int[] readBytes(int size) throws IOException {
        offset = 0;
        //bitsRead+=size;
        int[] result = new int[size];
        for (int x = 0; x < size; x++) {
            result[x] = readByte();
        }
        //myStream.read(result,0, size);
        return result;
    }

    // Read Multiple Bytes
    public byte[] readBytes2(int size) throws IOException {
        offset = 0;
        //bitsRead+=size;
        byte[] result = new byte[size];
        for (int x = 0; x < size; x++) {
            result[x] = (byte) readByte();
        }
        return result;
    }

    // Get the Bit Alignment
    // If > 0 then the stream is not aligned at a (bit % 8 == 0)
    public int getAlignment() {
        return offset;
    }

    // Private Bit Reading
    // Just when you need a byte quickly
    private int readInternalByte() throws IOException {
        bitsRead++;
        return myStream.read() & 0xFF;
    }

    // Align Bits to a byte
    // Just throw away the info and everything is fixed.
    public void align() throws IOException {
        // Clear the offset
        offset = 0;
        // Clear the bit register
        register = 0;
    }

    // Read Bits
    public long readBits(int bits, boolean littleendian) throws IOException {
        //System.out.println("Start Read Bits");
        // Clear the Register is not allready
        if (offset == 0) {
            register = 0;
        }

        // Test Storage
        int temp = 0;
        long readBit = 1;
        //long writeBit=1;
        long result = 0;

        // Only read when the bits is greater the amount allready stored
        if (bits > offset) {
            // Keep reading while bits read is less then bits aquired
            while (bits > offset) {
                // Select which mode to use for reading bytes
                if (!littleendian) {
                    temp = readInternalByte();
                    register = (register << 8);
                    register = register | temp;
                    offset += 8;
                } else {
                    temp = readInternalByte();
                    temp = (temp << offset);
                    register = register | temp;
                    offset += 8;
                }
            }
        }

        // Fix & Set the Result Bits
        int startBit = offset - bits;

        if (startBit > 0) {
            result = register >> startBit;
        } else {
            result = register;
        }

        offset = startBit;

        // Fix the Register
        if (offset > 0) {
            readBit = (pow(offset) - 1);
            register = (register & readBit);
        }

        //System.out.println("End Read Bits");
        return result;
    }

    // Dubug Function
    public String printBinary(long bits, int n) {
        long readBit = 1;
        String result = "";
        while (n > 0) {
            if ((bits & readBit) == readBit) {
                result = "1" + result;
            } else {
                result = "0" + result;
            }
            readBit *= 2;
            n--;
        }
        return result;
    }

    // Skip Bytes
    public void skip(long bytes) throws IOException {
        // Align the Bit Stream
        align();
        // Skip Bytes
        myStream.skip(bytes);
        // Update Bytes Read Count
        bitsRead += bytes;
    }

    // User Created Pow Function

    public long pow(int n) {
        long value = 1;
        while (n > 0) {
            value *= 2;
            n--;
        }
        return value;
    }
}

// OLD CODE

//System.out.println("\tRegister : " + printBinary(register, 20));
//System.out.println("\tRequest : " + bits);
//System.out.println("\tOffset : " + offset);
//System.out.println("\tStart : " + startBit);
//System.out.println(": startBit : " + startBit);
//readBit = pow(startBit);

//System.out.println("\tResult : " + result);

// for (int x=startBit;x<offset;x++)
// {
// 	//System.out.println("Read : " + readBit + " : Write " + writeBit + " : Result " + (((register & readBit) == readBit) ? 1 : 0)) ;
// if ((register & readBit) == readBit)
// {
// result = result | writeBit ;
// }
// readBit *=2;
// writeBit *= 2;
// }

//System.out.println("\tFix Mask : " + printBinary(readBit, 20));