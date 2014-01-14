/*
    Copyright M-Gate Labs 2007
*/

/**
 <p>
 <b>Binary Input Library Class</b>
 </p>
 <p>
 Used to process flash files at the object level.  <b>Example</b> rects, floats, ints.
 </p>
 */

package com.mgatelabs.swftools.support.swf.io;

import com.mgatelabs.swftools.exploit.control.AppControl;

import java.io.IOException;

public class BitLibrary {
    // Class Operations

    private BitInput myBitInput;
    private boolean isDebug;

    public BitLibrary(BitInput aBitInput) {
        myBitInput = aBitInput;
        this.isDebug = AppControl.getInfo().getDebug();
        if (isDebug) {
            System.out.println("Bit Library: Active");
        }
    }

    // Flash Basic Types

    // SB(n), Read Signed Bits
    public long sb(int n) throws IOException {
        long result = myBitInput.readBits(n, false);

        if ((result & (1L << (n - 1))) != 0) {
            result |= (-1L << n);
        }
        return result;
    }

    // UB(n), Read Un-Signed Bits
    public long ub(int n) throws IOException {
        return myBitInput.readBits(n, false);
    }

    // SB(n), Read Signed Bits
    public long sbl(int n) throws IOException {
        long result = myBitInput.readBits(n, true);

        if ((result & (1L << (n - 1))) != 0) {
            result |= (-1L << n);
        }
        return result;

        //return fixSignedNumber( myBitInput.readBits( n , true) , n ) ;
    }

    // UB(n), Read Un-Signed Bits
    public long ubl(int n) throws IOException {
        return myBitInput.readBits(n, true);
    }

    // SB(n), Fixed Bits
    public float fb(int n) throws IOException {
        return sb(n) / 65536.0f;
    }

    // Byte Operations

    // Read Signed Integer - 8 Bit
    public long si8() throws IOException {
        return (byte) ui8();
    }

    // Read Signed Integer - 15 Bit
    public long si16() throws IOException {
        return (short) ui16();
    }

    // Read Signed Integer - 32 Bit
    public long si32() throws IOException {
        return (int) ui32();
    }

    // Read N amount of 8 Bit Numbers (Signed)
    public long[] si8(int n) throws IOException {
        long[] myArray = new long[n];
        for (int x = 0; x < n; x++) {
            myArray[x] = si8();
        }
        return myArray;
    }

    // Read N amount of 16 Bit Numbers (Signed)
    public long[] si16(int n) throws IOException {
        long[] myArray = new long[n];
        for (int x = 0; x < n; x++) {
            myArray[x] = si16();
        }
        return myArray;
    }

    // Read N amount of 32 Bit Numbers (Signed)
    public long[] si32(int n) throws IOException {
        long[] myArray = new long[n];
        for (int x = 0; x < n; x++) {
            myArray[x] = si32();
        }
        return myArray;
    }

    // Read Un-Signed Integer - 8 Bit
    public long ui8() throws IOException {
        return myBitInput.readByte();
    }

    // Read Un-Signed Integer - 16 Bit
    public long ui16() throws IOException {
        long temp = 0;
        long result = 0;
        // Byte 1
        result += myBitInput.readByte();
        // Byte 2
        temp = myBitInput.readByte();
        temp = temp << 8;
        result += temp;
        return result;
    }

    // Read Un-Signed Integer - 32 Bit
    public long ui32() throws IOException {
        long temp = 0;
        long result = 0;
        // Byte 1
        result += myBitInput.readByte();
        // Byte 2
        temp = myBitInput.readByte();
        temp = temp << 8;
        result += temp;
        // Byte 3
        temp = myBitInput.readByte();
        temp = temp << 16;
        result += temp;
        // Byte 4
        temp = myBitInput.readByte();
        temp = temp << 24;
        result += temp;
        return result;
    }

    // Read N amount of 8 Bit Numbers (Unsigned)
    public byte[] uiBytes(int n) throws IOException {
        byte[] myArray = new byte[n];
        for (int x = 0; x < n; x++) {
            myArray[x] = (byte) ui8();
        }
        return myArray;
    }

    // Read N amount of 8 Bit Numbers (Unsigned)
    public long[] ui8(int n) throws IOException {
        long[] myArray = new long[n];
        for (int x = 0; x < n; x++) {
            myArray[x] = ui8();
        }
        return myArray;
    }

    // Read N amount of 16 Bit Numbers
    public long[] ui16(int n) throws IOException {
        long[] myArray = new long[n];
        for (int x = 0; x < n; x++) {
            myArray[x] = ui16();
        }
        return myArray;
    }

    // Read N amount of 32 Bit Numbers
    public long[] ui32(int n) throws IOException {
        long[] myArray = new long[n];
        for (int x = 0; x < n; x++) {
            myArray[x] = ui32();
        }
        return myArray;
    }

    // Fixed
    public float fixed() throws IOException {
        return Float.intBitsToFloat((int) si32());
        //return ui32() / (float)myBitInput.pow(16);
    }
   
   	/*
       // Create Mask // Not Used
       private long produceMask(int bits)
      {
         long result = 0;
         long bitPosition = 1 ;
         
         for ( int x = 0 ; x < bits ; x++ )
         {
            result = result | bitPosition;
            bitPosition *= 2;
         }
         
         return result;
      }
   	*/

   	/*
       // Nor, Will prouce a negative version of a number;
   	// Not Used
       private long nor(long input, int bits)
      {
         long result = 0 ;
         long bitPosition = 1 ;
         
         for ( int x = 0 ; x < bits ; x++ )
         {
            if ( ( input & bitPosition ) == 0 )
            {
               result = result | bitPosition;
            }
            bitPosition *= 2;
         }
         
         return result;
      }
   	*/
}