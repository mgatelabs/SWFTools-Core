package com.mgatelabs.swftools.support.base;

/**
 * Base16.java<br>
 * Written by SlashAndBurn<br>
 * 08/01/2006<br>
 * http://www.3D2Toy.com<br>
 */

// Just make a new Base16 and call the encode or decode methods.
// Also you can call the conversion functions just incase you need
// to convert something else

public class Base16 {

    public byte[] encode(byte[] input) {
        byte output[] = new byte[input.length * 2];
        int len = input.length;
        int z = 0;

        for (int x = 0; x < len; x++) {
            convertToHex(input[x], output, z);
            z += 2;
        }

        return output;
    }

    public byte[] decode(byte[] input) {
        byte output[] = new byte[input.length / 2];
        int len = output.length;
        int z = 0;

        for (int x = 0; x < len; x++) {
            output[x] = convertToNumber(input[z], input[z + 1]);
            z += 2;
        }

        return output;
    }

    // Number -> Base16

    private void convertToHex(byte number, byte[] output, int start) {
        byte numberHigh = (byte) ((number >> 4) & 0x0F);
        byte numberLow = (byte) (number & 0x0F);

        output[start] = getHex(numberHigh);
        output[start + 1] = getHex(numberLow);
    }

    public byte getHex(byte input) {
        if (input < 10) {
            return (byte) ('0' + input);
        } else if (input < 16) {
            return (byte) ('a' + (input - 10));
        } else {
            return (byte) 'Z';
        }
    }

    // Base16 -> Number

    private byte convertToNumber(byte numberHigh, byte numberLow) {
        numberHigh = (byte) (getNumber(numberHigh) << 4);
        numberLow = (byte) (numberHigh | getNumber(numberLow));

        return numberLow;
    }

    public byte getNumber(byte input) {
        if (input >= '0' && input <= '9') {
            return (byte) (input - '0');
        } else if (input >= 'a' && input <= 'f') {
            return (byte) (input - 'a' + 10);
        } else {
            return -1;
        }
    }

}