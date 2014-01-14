package com.mgatelabs.swftools.support.base;

    /*
    Base 64 Encoder
    Written by SlashAndBurn
    http://www.3D2Toy.com
    */

public class Base64 {
    // The possible output characters
    private String myNotes;

    // Used for encode/decode process
    private int tempBits[];
    private boolean isPadded;
    private String myExt;

    public Base64() {
        this.isPadded = true;

        // Characters
        myNotes = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=";
        myExt = "+/=";
        tempBits = new int[4];
    }

    public Base64(boolean pad) {
        this.isPadded = pad;

        // Characters
        myNotes = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=";
        myExt = "+/=";

        tempBits = new int[4];
    }

    // ext is used to change the ending characters, can be used to make it url safe.
    // Pass ext null to use the default encoding

    public Base64(boolean pad, String ext) {
        this.isPadded = pad;

        if (ext == null) {
            ext = "+/=";
        } else if (ext.length() != 3) {
            System.out.println("Base 64: Error with input, defaulting ext");
            ext = "+/=";
        }

        myExt = ext;

        // Characters
        myNotes = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789" + ext;

        //System.out.println(myNotes.charAt(64));

        tempBits = new int[4];
    }

    public boolean verify(String input) {
        char a = 0;

        for (int x = 0; x < input.length(); x++) {
            a = input.charAt(x);
            if ((a >= 'a' && a <= 'z')) {

            } else if ((a >= 'A' && a <= 'Z')) {

            } else if ((a >= '0' && a <= '9')) {

            } else if ((myExt.indexOf("" + a) >= 0)) {

            } else {
                return false;
            }
        }
        return true;
    }

    // Force your own ending char set
    public Base64(String notes) {
        this.isPadded = notes.length() == 65;

        // Characters
        myNotes = notes;

        tempBits = new int[4];
    }

    // Encode Data
    public String encode(byte[] data) {
        // This speeds up the entire process
        StringBuffer sb = new StringBuffer((data.length / 3) + 1);
        int x = 0;

        while (x < data.length) {
            if (x + 2 < data.length) {
                sb.append(encodeBits(data, x, 3));
            } else {
                sb.append(encodeBits(data, x, data.length - x));
            }
            x += 3;
        }
        // Return the encoded string
        return sb.toString();
    }

    private String encodeBits(byte[] data, int start, int length) {
        String result = "";
        int tempA, tempB;

        tempBits[0] = data[start] & 252; // 11111100
        tempBits[0] = tempBits[0] >> 2;

        tempA = data[start] & 3; // 00000011
        tempA = tempA << 4;
        tempB = 0;
        if (length > 1) {
            tempB = data[start + 1] & 240; // 11110000
            tempB = tempB >> 4;
        }
        tempBits[1] = tempA | tempB;

        if (length > 1) {
            tempA = data[start + 1] & 15; // 00001111
            tempA = tempA << 2;
            tempB = 0;
            if (length > 2) {
                tempB = data[start + 2] & 192; // 11000000
                tempB = tempB >> 6;
            }
            tempBits[2] = tempA | tempB;

            if (length > 2) {
                tempBits[3] = data[start + 2] & 63;
            }
        }

        result += myNotes.charAt(tempBits[0]);
        result += myNotes.charAt(tempBits[1]);

        if (length > 1) {
            result += myNotes.charAt(tempBits[2]);
            if (length > 2) {
                result += myNotes.charAt(tempBits[3]);
            } else if (isPadded) {
                result += myNotes.charAt(64);
            }
        } else if (isPadded) {
            result += myNotes.charAt(64) + "" + myNotes.charAt(64);
        }

        return result;
    }

    // Decode Data
    public byte[] decode(String input) {
        // Determine the final size
        int size = input.length();
        if (input.charAt(size - 2) == myNotes.charAt(64)) {
            size -= 2;
        } else if (input.charAt(size - 1) == myNotes.charAt(64)) {
            size -= 1;
        }

        int endSize = ((size / 4)) * 3;
        switch (size % 4) {
            case 0:

                break;
            case 1:

                break;
            case 2:
                endSize += 1;
                break;
            case 3:
                endSize += 2;
                break;
        }
        byte[] data = new byte[endSize];

        int x = 0;
        int y = 0;

        while (x < size) {
            if (x + 3 < size) {
                decodeBits(input, x, 4, data, y);
            } else {
                decodeBits(input, x, size - x, data, y);
            }

            y += 3;
            x += 4;
        }

        return data;
    }

    private void decodeBits(String input, int start, int length, byte[] output, int position) {
        int tempA = 0, tempB = 0;

        for (int x = 0; x < length; x++) {
            tempBits[x] = myNotes.indexOf((int) input.charAt(start + x));
        }

        // byte 1
        tempA = tempBits[0] & 63; // 00111111
        tempB = tempBits[1] & 48; // 00110000
        tempA = tempA << 2;
        tempB = tempB >> 4;
        output[position] = (byte) (tempA | tempB);

        // byte 2
        if (length > 2) {
            tempA = tempBits[1] & 15; // 00001111
            tempB = tempBits[2] & 60; // 00111100
            tempA = tempA << 4;
            tempB = tempB >> 2;
            output[position + 1] = (byte) (tempA | tempB);
        }

        // byte 3
        if (length > 3) {
            tempA = tempBits[2] & 3;  // 00000011
            tempB = tempBits[3] & 63; // 00111111
            tempA = tempA << 6;
            //tempB = tempB >> 2;
            output[position + 2] = (byte) (tempA | tempB);
        }

    }

}