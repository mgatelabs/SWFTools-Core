/*
    Copyright M-Gate Labs 2007
*/

/**
 <p>
 <b>Flash Decompressor</b>
 </p>
 <p>
 Used to quickly Decompress flash/svg files.
 </p>
 <p>
 Compression of flash/svg files should be added.
 </p>
 */

package com.mgatelabs.swftools.support.swf.io;

import java.io.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.Inflater;

public class FlashCompression {
    public static boolean uncompressSVGZ(File svgz, File svg) {
        try {
            GZIPInputStream gzip = new GZIPInputStream(new BufferedInputStream(new FileInputStream(svgz)));
            FileOutputStream fos = new FileOutputStream(svg);
            BufferedOutputStream bos = new BufferedOutputStream(fos);

            while (gzip.available() > 0) {
                bos.write(gzip.read());
            }
            bos.flush();
            bos.close();
            gzip.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean uncompressSWF(File source, File destination) {
        try {
            // Create a decompresser
            Inflater decompresser = new Inflater();

            // File Access Time
            //FileInputStream fis = new FileInputStream(source);
            FileInputStream fis = new FileInputStream(source);
            BitInput bi = new BitInput(fis);
            BitLibrary bl = new BitLibrary(bi);

            if (!(bl.ui8() == 'C' && bl.ui8() == 'W' && bl.ui8() == 'S')) {
                return false;
            }


            int version = (int) bl.ui8();
            System.out.println("Version: " + version);
            long uncompressedSize = bl.ui32();
            System.out.println("File Size: " + uncompressedSize);

            //Now I just have uncompressed Data
            byte[] input = new byte[(int) (source.length() - 8)];
            fis.read(input);
            fis.close();

            byte[] output = new byte[(int) (uncompressedSize - 8)];

            decompresser.setInput(input);

            int resultLength = decompresser.inflate(output);

            System.out.println("Difference = " + (resultLength - (int) (uncompressedSize - 8)));

            //Output File
            FileOutputStream fos = new FileOutputStream(destination);
            fos.write('F');
            fos.write('W');
            fos.write('S');
            fos.write(version);

            for (int x = 0; x < 4; x++) {
                fos.write((int) (uncompressedSize & 0x0ff));
                uncompressedSize = uncompressedSize >> 8;
            }

            fos.write(output);
            fos.close();
            return true;
        } catch (Exception e) {

        }
        return false;
    }

}