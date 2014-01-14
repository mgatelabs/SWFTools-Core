/*
    Copyright M-Gate Labs 2007
*/

/**
 <p>
 <b>Block Input/Output Class</b>
 </p>
 <p>
 Used to read and write flash files at the TAG level.  Useful for editing flash content.
 </p>
 */

package com.mgatelabs.swftools.support.swf.io;

import com.mgatelabs.swftools.support.swf.objects.FMovie;
import com.mgatelabs.swftools.support.swf.objects.FRect;
import com.mgatelabs.swftools.support.swf.tags.TBlock;
import com.mgatelabs.swftools.support.swf.tags.Tag;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Vector;

public class BlockIO {

    private BitInput bi;
    private BitOutput bo;
    private BitLibrary bl;
    private TypeReader myTypeReader;
    private TypeWriter myTypeWriter;

    public Object[] readFlash(File target) throws Exception {
        try {
            bi = new BitInput(new FileInputStream(target));
            bl = new BitLibrary(bi);

        } catch (Exception e) {
            return null;
        }

        String signature = (char) bl.ui8() + "" + (char) bl.ui8() + (char) bl.ui8();

        FMovie myObject = null;

        long uncompressedSize = 0;

        if (signature.equals("CWS")) {
            int version = (int) bl.ui8();
            uncompressedSize = bl.ui32();
            bi.useCompressedStream();

            myTypeReader = new TypeReader(bl, bi, version);

            FRect movieRect = myTypeReader.rect();
            float frameRate = (bl.ui16() / 256.0f);
            long frameCount = bl.ui16();

            myObject = new FMovie(signature, version, uncompressedSize, movieRect, frameRate, frameCount);
        } else {
            int version = (int) bl.ui8();
            uncompressedSize = bl.ui32();
            myTypeReader = new TypeReader(bl, bi, version);

            myObject = new FMovie(signature, version, uncompressedSize, myTypeReader.rect(), (bl.ui16() / 256.0f), bl.ui16());
        }

        // Read Blocks

        Object[] objects = new Object[2];
        objects[0] = myObject;
        objects[1] = readBlocks(uncompressedSize);

        bi.close();
        bi = null;
        bl = null;

        return objects;
    }

    ////////////////////////////////////////////////////////////////////////////

    public Vector readBlocks(long size) throws Exception {
        Tag aTag = null;
        TBlock tblock = null;
        Vector results = new Vector();
        long startByte = 0;
        long endByte = 0;

        while (bi.bitsRead < size) {
            aTag = myTypeReader.tag();

            startByte = bi.bitsRead;
            endByte = startByte + aTag.length;

            tblock = new TBlock(aTag.id, aTag.length);

            byte[] data = bi.readBytes2((int) aTag.length);
            tblock.setData(data);

            results.add(tblock);
        }

        return results;
    }

    ////////////////////////////////////////////////////////////////////////////

    public boolean writeFlash(File target, FMovie aMovie, Vector data, boolean compress) throws Exception {
        try {
            bo = new BitOutput(new FileOutputStream(target));
            myTypeWriter = new TypeWriter(bo, aMovie.getVersion());
        } catch (Exception e) {
            return false;
        }

        // Write Signiture
        if (compress) {
            bo.writeByte('C');
            bo.writeByte('W');
            bo.writeByte('S');
        } else {
            bo.writeByte('F');
            bo.writeByte('W');
            bo.writeByte('S');
        }
        // Version
        bo.ui8(aMovie.getVersion());
        // Size
        bo.ui32(determineFlashSize(aMovie, data));

        if (compress) {
            bo.useCompression();
        }

        myTypeWriter.rect(aMovie.getRect());

        bo.ui16((long) (aMovie.getFrameRate() * 256));
        bo.ui16(aMovie.getFrameCount());

        for (int x = 0; x < data.size(); x++) {
            TBlock aBlock = (TBlock) data.get(x);
            myTypeWriter.tag(aBlock.getId(), aBlock.getData());
        }

        bo.close();

        return true;
    }

    ////////////////////////////////////////////////////////////////////////////

    /**
     * Flash Movies are picky about their file size field, so it must be known beforehand.
     */

    public long determineFlashSize(FMovie aMovie, Vector blocks) {
        long size = 3;
        size += 1;
        size += 4;
        size += myTypeWriter._rect(aMovie.getRect());
        size += 2;
        size += 2;

        for (int x = 0; x < blocks.size(); x++) {
            TBlock aBlock = (TBlock) blocks.get(x);
            size += aBlock.getData().length + (aBlock.getData().length > 62 ? 6 : 2);
        }

        return size;
    }

    ////////////////////////////////////////////////////////////////////////////

    public TBlock readBlock(File target, FMovie aMovie) throws Exception {
        try {
            bi = new BitInput(new FileInputStream(target));
            bl = new BitLibrary(bi);

            myTypeReader = new TypeReader(bl, bi, aMovie.getVersion());
        } catch (Exception e) {
            return null;
        }

        Tag aTag = myTypeReader.tag();

        TBlock tblock = new TBlock(aTag.id, aTag.length);

        byte[] data = bi.readBytes2((int) aTag.length);
        tblock.setData(data);

        bi.close();

        return tblock;
    }

    ////////////////////////////////////////////////////////////////////////////

    public boolean writeBlock(File target, FMovie aMovie, TBlock aBlock) throws Exception {
        try {
            bo = new BitOutput(new FileOutputStream(target));
            myTypeWriter = new TypeWriter(bo, aMovie.getVersion());
        } catch (Exception e) {
            return false;
        }

        myTypeWriter.tag(aBlock.getId(), aBlock.getData());

        bo.close();

        return true;
    }

    ////////////////////////////////////////////////////////////////////////////

    public boolean writeData(File target, FMovie aMovie, TBlock aBlock) throws Exception {
        try {
            bo = new BitOutput(new FileOutputStream(target));
            myTypeWriter = new TypeWriter(bo, aMovie.getVersion());
        } catch (Exception e) {
            return false;
        }

        bo.writeBytes(aBlock.getData());

        bo.close();

        return true;
    }

    ////////////////////////////////////////////////////////////////////////////
}