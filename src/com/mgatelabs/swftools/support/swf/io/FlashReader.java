/*
    Copyright M-Gate Labs 2007
*/

/**
 <p>
 <b>Flash Reader</b>
 </p>
 <p>
 Used to parse the movie information before the tags begin.
 </p>
 */

package com.mgatelabs.swftools.support.swf.io;

import com.mgatelabs.swftools.support.swf.objects.FMovie;
import com.mgatelabs.swftools.support.swf.objects.FRect;

import java.io.File;
import java.io.FileInputStream;

public class FlashReader {
    private BitInput bi;
    private BitLibrary bl;
    private boolean open;
    private TagReader myTagReader;
    private TypeReader myTypeReader;
    private FlashFilter myFilter;
    private String myError;
    private int myMaxFrame;

    // Sets up the Reader and All the necessary helpers
    public FlashReader(File target, FlashFilter aFilter) {
        myError = null;
        myMaxFrame = -1;

        try {
            myFilter = aFilter;
            bi = new BitInput(new FileInputStream(target));
            bl = new BitLibrary(bi);
            open = true;
        } catch (Exception e) {
            open = false;
        }
    }

    public void setMaxFrame(int value) {
        myMaxFrame = value;
    }

    // Test if Ready

    public boolean ready() {
        return open;
    }

    // Close the Reader

    public void close() {
        try {
            open = false;
            bi.close();
            bi = null;
            bl = null;
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    // Parse the pre-selected SWF File

    public FMovie parse() throws Exception {
        // Test if the File is uncompressed;
        String signature = (char) bl.ui8() + "" + (char) bl.ui8() + (char) bl.ui8();

        // Empty Movie Object
        FMovie myObject = null;

        // Get the Flash Movie Version
        int version = (int) bl.ui8();

        // Get the Flash Files Size (Uncompressed)
        long uncompressedSize = bl.ui32();

        // Test if its a Compressed Movie Files
        if (signature.equals("CWS")) {
            // USE Compression NOW!
            bi.useCompressedStream();
        }

        // Create a Type Reader
        myTypeReader = new TypeReader(bl, bi, version);

        // Movie Dimensions
        FRect movieRect = myTypeReader.rect();

        // Movie Frame Rate
        float frameRate = (bl.ui16() / 256.0f);

        // Movie Frame Count
        long frameCount = bl.ui16();

        // Create my Movie Object
        myObject = new FMovie(signature, version, uncompressedSize, movieRect, frameRate, frameCount);

        // Create my Tag Reader
        myTagReader = new TagReader(myTypeReader, bl, bi, myObject.getVersion(), myFilter);

        if (myMaxFrame != -1) {
            myTagReader.setMaxFrame(myMaxFrame);
        }

        // Parse the Available Tags
        String tagResult = myTagReader.parseTags(myObject, myObject.getFileLength(), 0);

        if (tagResult != null) {
            myError = tagResult;
            return null;
        }

        return myObject;
    }

    public String getError() {
        return myError;
    }

    public int getComplete() {
        if (myTagReader != null) {
            return myTagReader.getComplete();
        }
        return 0;
    }

}

	/*
    OLD CODE
	
	// Test if its a Compressed Movie Files
         if (signature.equals("CWS"))
         {
            //int version = (int)bl.ui8();
            //long uncompressedSize = bl.ui32();
         	// USE Compression NOW!
            bi.useCompressedStream();
            
            //myTypeReader = new TypeReader(bl, bi, version);
            //FRect movieRect = myTypeReader.rect();
            //float frameRate = (bl.ui16() / 256.0f);
            //long frameCount = bl.ui16();
            //myObject = new FMovie(signature, version, uncompressedSize, movieRect, frameRate, frameCount);
         }
         else
         {
            //int version = (int)bl.ui8();
           // long uncompressedSize = bl.ui32();
            //myTypeReader = new TypeReader(bl, bi, version);
            	
           // myObject = new FMovie(signature, version, uncompressedSize, myTypeReader.rect(), (bl.ui16() / 256.0f), bl.ui16());
         }
	
	*/
