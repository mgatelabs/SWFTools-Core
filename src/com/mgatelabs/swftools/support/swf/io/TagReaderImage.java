/*
     Copyright M-Gate Labs 2007
 */

/**
 <p>
 <b>Image Tag Reader</b>
 </p>
 <p>
 Used to process image tags.
 </p>
 */

package com.mgatelabs.swftools.support.swf.io;

import com.mgatelabs.swftools.support.swf.objects.FBitmap;
import com.mgatelabs.swftools.support.swf.objects.FColor;
import com.mgatelabs.swftools.support.swf.objects.FLossless;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

public class TagReaderImage {
    private BitInput bi;
    private BitLibrary bl;
    private TypeReader myTypeReader;
    private byte[] JPEGTable;
    private boolean isDebug;
    private FlashFilter myFilters;

    public TagReaderImage(boolean debug, BitInput bi, BitLibrary bl, TypeReader aTypeReader, FlashFilter filters) {
        this.isDebug = debug;
        this.myFilters = filters;
        if (isDebug) {
            System.out.println("Image Reader: Active");
        }

        this.bi = bi;
        this.bl = bl;
        this.myTypeReader = aTypeReader;
        this.JPEGTable = null;
    }
   
   	/*
           This function parses a Bitmap Tag
   	*/

    public FBitmap parseBitmap(int version, long size) throws IOException {
        // Store the Size of the Block
        int sizeOfBlock = (int) size;
        // Store the Size of the Data
        int sizeOfData = sizeOfBlock;
        // Store the Size fo the Alpha
        int sizeOfAlpha = 0;
        // Store the Offset to the Alpha
        int offset_to_alpha = 0;

        // Version 0, JPEGTable
        if (version == 0) {
            // Make a JPEGTable Object
            JPEGTable = new byte[sizeOfBlock];
            // Loop Through and Read the Table
            for (int x = 0; x < sizeOfBlock; x++) {
                JPEGTable[x] = (byte) bl.ui8();
            }
            // Return Nothing
            return null;
        } else {
            // Get the ID of the Image
            int id = (int) bl.ui16();
            // Minus the ID from the Size of the Block
            sizeOfBlock -= 2;
            // Set the Size of the Data
            sizeOfData = sizeOfBlock;

            // Version 3 -- Alpha
            if (version == 3) {
                // Read the Offset to the Alpha Value
                offset_to_alpha = (int) bl.ui32();
                // Update the Block Size
                sizeOfBlock -= 2;
                // Update the Size of the Data
                sizeOfData = offset_to_alpha;
                // Update the Size of the Alpha
                sizeOfAlpha = sizeOfBlock - offset_to_alpha - 2;
            }
            // Make a Byte Array to store the Bits
            byte[] bitTable = null;


            if (version == 1) {// Version 1, JPEGTable + Data, With some Edits
                // JPEG Tables are funny with the extra stream info.
                // Have to compensate
                if (JPEGTable.length > 0) {
                    bitTable = new byte[(sizeOfData - 2) + (JPEGTable.length - 2)];

                    // Skip the last two bytes
                    for (int x = 0; x < (JPEGTable.length - 2); x++) {
                        // Copy the JPEGTable Over to the Data
                        bitTable[x] = JPEGTable[x];
                    }
                    // Skip the first two bytes
                    bi.skip(2);
                    // Read the Data
                    for (int x = 0; x < (sizeOfData - 2); x++) {
                        // Read the bits
                        bitTable[x + (JPEGTable.length - 2)] = (byte) bl.ui8();
                    }
                } else {
                    bitTable = new byte[sizeOfData];
                    for (int x = 0; x < sizeOfData; x++) {
                        bitTable[x] = (byte) bl.ui8();
                    }
                }

                sizeOfData = bitTable.length;
            } else if (version == 2 || version == 3) {// Version 2 & 3 Include their own JPEGTables
                bitTable = new byte[sizeOfData];
                // Offset Variable
                int offsetX = 0;
                //  Read the Bytes
                for (int x = 0; x < sizeOfData; x++) {
                    bitTable[x] = (byte) bl.ui8();
                }

            	/*
               int trueStart = 0;
            	// Locate a Good Start, Flash Files Must Start With FF F8
               for (int x = 1; x < 10 && x < sizeOfData; x++)
               {
                  if ((int)(bitTable[x-1] & 0xFF) == 0xFF && (int)(bitTable[x] & 0xFF) == 0xD8)
                  {
                     trueStart = x-1;
                  }
               }
               if (trueStart > 0)
               {
                  byte [] temp = new byte[sizeOfData-trueStart];
                  for (int x=0;x<temp.length;x++)
                  {
                     temp[x] = bitTable[x+trueStart];
                  }
                  bitTable = null;
                  bitTable = temp;
                  temp = null;
               }
               */
            }

            int trueStart = 0;
            // Locate a Good Start, Flash Files Must Start With FF F8
            for (int x = 1; x < 10 && x < sizeOfData; x++) {
                if ((int) (bitTable[x - 1] & 0xFF) == 0xFF && (int) (bitTable[x] & 0xFF) == 0xD8) {
                    trueStart = x - 1;
                }
            }
            if (trueStart > 0) {
                byte[] temp = new byte[sizeOfData - trueStart];
                for (int x = 0; x < temp.length; x++) {
                    temp[x] = bitTable[x + trueStart];
                }
                bitTable = null;
                bitTable = temp;
                temp = null;
            }

            // Extra Data Check
            int removePosition = 0;
            do {
                removePosition = 0;
                for (int x = 4; x < bitTable.length - 4; x++) {

                    if (
                            ((bitTable[x] & 0xFF) == 0xFF) &&
                                    ((bitTable[x + 1] & 0xFF) == 0xd9) &&
                                    ((bitTable[x + 2] & 0xFF) == 0xFF) &&
                                    ((bitTable[x + 3] & 0xFF) == 0xd8)
                            ) {
                        removePosition = x;
                        break;
                    }
                }

                if (removePosition > 0) {
                    byte[] temp = new byte[bitTable.length - 4];

                    for (int x = 0; x < removePosition; x++) {
                        temp[x] = bitTable[x];
                    }
                    int i = removePosition;
                    for (int x = removePosition + 4; x < bitTable.length; x++) {
                        temp[i] = bitTable[x];
                        i++;
                    }

                    bitTable = null;
                    bitTable = temp;
                    temp = null;
                }
            }
            while (removePosition > 0);

            // Make a New Bitmap
            FBitmap aBitmap = new FBitmap(id, version, bitTable, myFilters.getFilter(FlashFilter.BITMAP_DISCARD));
            // Set the Bitmaps Version
            aBitmap.setVersion(version);

            if (version == 3 && aBitmap.getImage() != null) // End Fixes
            {
                BufferedImage origImage = aBitmap.getImage();

                aBitmap.setData(null);

                int w = aBitmap.getWidth();
                int h = aBitmap.getHeight();

                BufferedImage lImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2D = lImage.createGraphics(); // Context for buffered image
                // Clear image with transparent alpha by drawing a rectangle
                Rectangle2D.Double rect = new Rectangle2D.Double(0, 0, 1, 1);

                //aLossless.setImage(lImage);

                //byte [] myDataBlock = bl.uiBytes(sizeOfAlpha);

                byte[] myDataBlock = bl.uiBytes(sizeOfAlpha);

                ByteArrayInputStream bai = new ByteArrayInputStream(myDataBlock);
                BitInput aBi = new BitInput(bai);
                BitLibrary aBl = new BitLibrary(aBi);

                aBi.zipStream();

                int s, r, g, b, col;
                //int [] bits = new int[4];
                //int alpha = 255;
                Color aColor = null;

                //System.out.println("Image Size: " + w + ":" + h + "\n");

                for (int y = 0; y < h; y++) {
                    for (int x = 0; x < w; x++) {
                        col = origImage.getRGB(x, y);

                        s = (int) aBi.readBits(8, false);

                        r = ((col >> 16) & 0xff);
                        g = ((col >> 8) & 0xff);
                        b = ((col) & 0xff);

                        aColor = new Color(r, g, b);
                        g2D.setPaint(aColor);
                        rect.setRect(x, y, 1, 1);
                        g2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, s / 255.0f));
                        //g2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha / 255.0f )); //
                        g2D.fill(rect);
                    }
                }
                aBitmap.setImage(lImage);
            }
            // Return the Bitmap
            return aBitmap;
        }
    }

   	/*
       public FBitmap parseBitmap(int version, long size)  throws IOException
      {
      	// Store the Size of the Block
         int sizeOfBlock = (int)size;
         // Store the Size of the Data
         int sizeOfData = sizeOfBlock;
         // Store the Size fo the Alpha
         int sizeOfAlpha = 0;
      	// Store the Offset to the Alpha
         int offset_to_alpha = 0;
      	
      	// Version 0, JPEGTable
         if (version == 0)
         {
         	// Make a JPEGTable Object
            JPEGTable = new byte[sizeOfBlock];
            // Loop Through and Read the Table
            for (int x=0;x<sizeOfBlock;x++)
            {
               JPEGTable[x] = (byte)bl.ui8();
            }
            // Return Nothing
            return null;
         }
         else
         {
         	// Get the ID of the Image
            int id = (int)bl.ui16();
            // Minus the ID from the Size of the Block
            sizeOfBlock -= 2;
            // Set the Size of the Data
            sizeOfData  = sizeOfBlock;
            
         	// Version 3 -- Alpha
            if (version == 3)
            {
            	// Read the Offset to the Alpha Value
               offset_to_alpha = (int)bl.ui32();
               // Update the Block Size
               sizeOfBlock -= 2;
               // Update the Size of the Data
               sizeOfData = offset_to_alpha;
               // Update the Size of the Alpha
               sizeOfAlpha = sizeOfBlock - sizeOfData;
            }
            // Make a Byte Array to store the Bits
            byte [] bitTable = null;
            
         	
            if (version == 1)
            {// Version 1, JPEGTable + Data, With some Edits
            	// JPEG Tables are funny with the extra stream info.
            	// Have to compensate
               bitTable = new byte[(sizeOfData-2) + (JPEGTable.length-2)];
            	
            	// Skip the last two bytes
               for (int x=0;x<(JPEGTable.length-2);x++)
               {
               	// Copy the JPEGTable Over to the Data
                  bitTable[x] = JPEGTable[x];
               }
               // Skip the first two bytes
               bi.skip(2);
               // Read the Data
               for (int x=0;x<(sizeOfData-2);x++)
               {
               	// Read the bits
                  bitTable[x+(JPEGTable.length-2)] = (byte)bl.ui8();
               }
            }
            else if (version == 2 || version == 3)
            {// Version 2 & 3 Include their own JPEGTables 
               bitTable = new byte[sizeOfData];
               // Offset Variable
               int offsetX = 0;
            	//  Read the Bytes
               for (int x=0;x<sizeOfData;x++)
               {
                  bitTable[x] = (byte)bl.ui8();
               }
            	
            	// This Part is Used to Remove Erronious Data
               int []startbit = new int [4];
               // Read the Data From the Byte Array Allread in Memory
               for (int x=0;x<4;x++)
               {
               	// Read the Values & FF then to make them Unsigned
                  startbit[x] = bitTable[x] & 0xFF;
               }
            	
            	// The Amount of Data to Skip
               int skipAmount = 0;
            	
            	// First Test Removes the Extra Start & End Blocks
               if ( startbit[1] != 0xd8 )
               {
               	// Skip 4 Bytes
                  skipAmount = 4;
               }
            	
            	// If Skipping then Do It
               if (skipAmount > 0)
               {
                  bitTable = Arrays.copyOfRange(bitTable, skipAmount, bitTable.length-1);
               }  
            	
            	// Find Bad Splits, the Bane of JPG Data, But Java Bitmap Rendering Doesn't Care, but Windows & Firefox Do!
            	// ff d9 ff d8
               int splitPosition = 0;
            	
               for (int x=4;x<bitTable.length-4;x++)
               {
                  if ( (bitTable[x] & 0xFF) == 0xFF)
                  {
                     startbit[1] = bitTable[x+1] & 0xFF;
                     startbit[2] = bitTable[x+2] & 0xFF;
                     startbit[3] = bitTable[x+3] & 0xFF;
                     // Find a Extra Start Byte
                     if (startbit[1] == 0xd9 && startbit[2] == 0xff && startbit[3] == 0xd8)
                     {
                        splitPosition = x;
                        break;
                     }
                  }
               }
            	
            	// If the Split if not 0 then Split them
               if (splitPosition > 0)
               {
               	// Make a New Array to Store the Data
                  byte [] newTable = new byte[bitTable.length-4];
                  int y=0;
                  for (int x=0;x<bitTable.length;x++)
                  {
                  	// Copy & Skip the Bad Data
                     if (x < splitPosition || x > (splitPosition + 3) )
                     {
                        newTable[y] = bitTable[x];
                        y++;
                     }
                  }
                  bitTable = null;
                  bitTable = newTable;
                  newTable = null;
               }
            }
         	// Make a New Bitmap
            FBitmap aBitmap = new FBitmap( id, version, bitTable , myFilters.getFilter(FlashFilter.BITMAP_DISCARD) );
            // Set the Bitmaps Version
            aBitmap.setVersion(version);
         	// Return the Bitmap
            return aBitmap; 
         }
      }
   	*/
   	
   	/*
   		This function parses a Lossless Image Tag
   	*/

    public FLossless parseLossless(int version, int blockSize) throws IOException {
        int id = (int) bl.ui16();
        int format = (int) bl.ui8();
        int width = (int) bl.ui16();
        int height = (int) bl.ui16();

        blockSize -= 7;

        //System.out.println(version+":"+id+":"+format+":"+width+":"+height);

        if (format == 3) {
            FLossless aLossless = new FLossless(version, id, format, width, height);

            // Create image with RGB and alpha channel
            BufferedImage aImage = new BufferedImage(width, height, version == 2 ? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB);
            Graphics2D g2D = aImage.createGraphics(); // Context for buffered image
            // Set best alpha interpolation quality
            g2D.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);

            // Clear image with transparent alpha by drawing a rectangle
            g2D.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR, 0.0f));
            Rectangle2D.Double rect = new Rectangle2D.Double(0, 0, width, height);
            g2D.fill(rect);

            aLossless.setImage(aImage);

            int colorMapCount = (int) bl.ui8();
            blockSize -= 1;

            byte[] myDataBlock = bl.uiBytes(blockSize);

            ByteArrayInputStream bai = new ByteArrayInputStream(myDataBlock);
            BitInput aBi = new BitInput(bai);
            BitLibrary aBl = new BitLibrary(aBi);
            TypeReader aTypeReader = new TypeReader(aBl, aBi, 3);

            aBi.zipStream();

            FColor[] myColors = new FColor[colorMapCount];
            FColor aColor;
            //System.out.println("colors:"+ colorMapCount);

            for (int x = 0; x < colorMapCount; x++) {
                myColors[x] = aTypeReader.rgba(version == 1 ? 1 : 3);
                //System.out.print(myColors[x]);
            }

            int skipSize = 4 - ((int) aBi.bitsRead & 3);
            //System.out.print(myColors[x]);
            aBi.skip(skipSize);

            skipSize = 4 - (width & 3);

            int index = 0;
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    index = (int) aBl.ui8();
                    //index--;
                    if (index >= 0 && index < colorMapCount) {
                        aColor = myColors[index];
                        g2D.setPaint(aColor.toColor());
                        rect.setRect(x, y, 1, 1);
                        g2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, aColor.getA() / 255.0f)); //
                        g2D.fill(rect);
                    }
                }

                if ((width & 3) != 0) {
                    aBi.skip(skipSize);
                }
            }

            return aLossless;
        } else if (format == 4) {
            FLossless aLossless = new FLossless(version, id, format, width, height);
            BufferedImage aImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2D = aImage.createGraphics(); // Context for buffered image
            // Clear image with transparent alpha by drawing a rectangle
            Rectangle2D.Double rect = new Rectangle2D.Double(0, 0, width, height);

            aLossless.setImage(aImage);

            byte[] myDataBlock = bl.uiBytes(blockSize);

            ByteArrayInputStream bai = new ByteArrayInputStream(myDataBlock);
            BitInput aBi = new BitInput(bai);
            BitLibrary aBl = new BitLibrary(aBi);

            aBi.zipStream();

            int s, r, g, b;
            //int [] bits = new int[4];
            //int alpha = 255;
            Color aColor = null;
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    s = (int) aBi.readBits(1, false);
                    r = (int) aBi.readBits(5, false);
                    g = (int) aBi.readBits(5, false);
                    b = (int) aBi.readBits(5, false);
                    aColor = new Color((int) ((r / 31.0f) * 255), (int) ((g / 31.0f) * 255), (int) ((b / 31.0f) * 255));
                    g2D.setPaint(aColor);
                    rect.setRect(x, y, 1, 1);
                    //g2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha / 255.0f )); //
                    g2D.fill(rect);
                }
            }
            return aLossless;
        } else if (format == 5) {
            FLossless aLossless = new FLossless(version, id, format, width, height);
            // Create image with RGB and alpha channel
            BufferedImage aImage = new BufferedImage(width, height, (version == 2) ? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB);
            Graphics2D g2D = aImage.createGraphics(); // Context for buffered image
            // Set best alpha interpolation quality
            g2D.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
            // Clear image with transparent alpha by drawing a rectangle
            Rectangle2D.Double rect = new Rectangle2D.Double(0, 0, width, height);
            if (version == 2) {
                g2D.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR, 0.0f));
                g2D.fill(rect);
            }
            aLossless.setImage(aImage);

            byte[] myDataBlock = bl.uiBytes(blockSize);

            ByteArrayInputStream bai = new ByteArrayInputStream(myDataBlock);
            BitInput aBi = new BitInput(bai);
            BitLibrary aBl = new BitLibrary(aBi);

            aBi.zipStream();

            int[] bits = new int[4];
            int alpha = 255;
            Color aColor = null;
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    for (int z = 0; z < 4; z++) {
                        bits[z] = (int) aBl.ui8();
                    }

                    if (version == 2) {
                        alpha = bits[0];
                    }
                    aColor = new Color(bits[1], bits[2], bits[3]);

                    g2D.setPaint(aColor);
                    rect.setRect(x, y, 1, 1);
                    g2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha / 255.0f)); //
                    g2D.fill(rect);
                }
            }

            return aLossless;
        }
        return null;
    }

}