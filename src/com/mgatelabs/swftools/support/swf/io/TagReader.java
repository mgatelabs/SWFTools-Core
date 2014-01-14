/*
    Copyright M-Gate Labs 2007
*/

/**
 <p>
 <b>Binary Input Class</b>
 </p>
 <p>
 Used to process flash files at the tag level.
 </p>

 <p>
 Messy Code
 </p>
 */

package com.mgatelabs.swftools.support.swf.io;

import com.mgatelabs.swftools.exploit.control.AppControl;
import com.mgatelabs.swftools.support.swf.objects.*;
import com.mgatelabs.swftools.support.swf.tags.PlaceObject;
import com.mgatelabs.swftools.support.swf.tags.RemoveObject;
import com.mgatelabs.swftools.support.swf.tags.Tag;

import java.io.IOException;
import java.util.Vector;

public class TagReader {

    // Binary Input
    private BitInput bi;
    private BitLibrary bl;

    // Flash Version
    private int version;

    // My Type Reader
    private TypeReader myTypeReader;

    // My Filters
    private FlashFilter myFilter;

    // Quick Reference to the Core Movie File
    private FMovie coreMovie;

    // JPEG Table (Old Flash Movies)
    //private byte [] JPEGTable;

    private TagReaderImage myImageReader;
    private boolean isDebug;
    private int myMaxFrame;

    private int myPercentage;

    public TagReader(TypeReader aTypeReader, BitLibrary bl, BitInput bi, int version, FlashFilter aFilter) {
        myPercentage = 0;

        myMaxFrame = -1;
        this.bl = bl;
        this.bi = bi;
        //this.JPEGTable = null;
        this.myTypeReader = aTypeReader;
        this.version = version;
        this.isDebug = AppControl.getInfo().getDebug();

        myFilter = aFilter;

        if (myFilter.getFilter(myFilter.BITMAP)) {
            myImageReader = new TagReaderImage(isDebug, bi, bl, myTypeReader, myFilter);
        } else {
            myImageReader = null;
        }


        if (isDebug) {
            System.out.println("Tag Reader: Active");
        }
    }

    public void setMaxFrame(int value) {
        myMaxFrame = value;
    }

    public void setFilter(FlashFilter aFilter) {
        myFilter = aFilter;
    }

    public int getComplete() {
        return myPercentage;
    }

    public String parseTags(FMovieInterface myMovie, long longTotalFileLength, int depth) throws IOException {
        // for (int x=0;x<depth;x++)
        // {
        // System.out.print("\t");
        // }
        // System.out.println("ParseTags");

        Tag aTag = null;

        long startByte = 0;
        long endByte = 0;
        int currentFrame = 0;

        if (depth == 0) {
            // Preset the Core Movie
            coreMovie = (FMovie) myMovie;
        }

        //FSound soundBlockHead = null;
        //Vector soundStreamBlocks = null;

        while (bi.bitsRead < longTotalFileLength && currentFrame != myMaxFrame) {
            myPercentage = (int) (((float) bi.bitsRead / longTotalFileLength) * 100);

            aTag = myTypeReader.tag();

            if (isDebug) {
                System.out.println("Byte:" + bi.bitsRead + " " + aTag);
            }

            startByte = bi.bitsRead;
            endByte = startByte + aTag.length;

            switch (aTag.id) {
                case 0: // End
                case 1: // ShowFrame
                    myMovie.addTag(aTag);
                    if (depth == 0) {
                        currentFrame++;
                    }
                    break;
                case 2: // DefineShape
                    if (depth == 0 && myFilter.getFilter(myFilter.SHAPE)) // Otherwise Skip, Bad Format
                    {
                        FShape aShape = parseDefineShape(1);
                        coreMovie = (FMovie) myMovie;
                        coreMovie.add(aShape.getID(), aShape);
                    }
                    break;
                case 4: // PlaceObject
                {
                    PlaceObject aPlaceObject = parsePlaceObject(endByte);
                    aTag.myObject = aPlaceObject;
                    myMovie.addTag(aTag);
                }
                break;
                case 5: // RemoveObject
                {
                    aTag.myObject = parseRemoveObject(1);
                    myMovie.addTag(aTag);
                }
                break;

                case 6:  // Define Bits 1
                    if (myFilter.getFilter(myFilter.BITMAP)) {
                        FBitmap aBitmap = myImageReader.parseBitmap(1, aTag.length);
                        coreMovie = (FMovie) myMovie;
                        coreMovie.add(aBitmap.getID(), aBitmap);
                    }
                    break;

                case 7: // DefineButton
                    //System.out.println("Tag: DefineButton");
                    break;
                case 8: // Define JPEGTable
                {
                    if (myFilter.getFilter(myFilter.BITMAP)) {
                        myImageReader.parseBitmap(0, aTag.length);
                    }
                }
                break;
                case 9: // SetBackgroundColor
                {
                    aTag.myObject = myTypeReader.rgba(1);
                    coreMovie.setBackgroundColor(((FColor) aTag.myObject).toColor());
                    myMovie.addTag(aTag);
                }
                break;
                case 10: // DefineFont
                    //System.out.println("Tag: DefineFont");
                {
                    FFont aFont = parseDefineFont();
                    if (aFont != null) {
                        coreMovie = (FMovie) myMovie;
                        coreMovie.add(aFont.getID(), aFont);
                    }
                }
                break;
                case 11: // DefineText
                {
                    FText aText = parseText(1);
                    if (aText != null) {
                        coreMovie = (FMovie) myMovie;
                        coreMovie.add(aText.getID(), aText);
                    }
                }
                //System.out.println("Tag: DefineFontInfo");
                break;
                case 12: // DoAction
                    //System.out.println("Tag: DoAction");
                    break;
                case 13: // DefineFontInfo
                    //System.out.println("Tag: DefineText");
                    break;
                case 14: // Define Sound
                    if (myFilter.getFilter(myFilter.SOUND)) {
                        FSound aSound = DefineSound(aTag.length);
                        coreMovie = (FMovie) myMovie;
                        coreMovie.add(aSound.getID(), aSound);
                    }
                    break;
                case 17: // DefineButtonSound
                    //System.out.println("Tag: DefineButtonSound");
                    break;

                case 18:
                case 45:

                    // System.out.println("Head");
                    //
                    // if (soundBlockHead != null)
                    // {
                    // 	// Build up current stuff and start new
                    // combineTagHeadAndBlockData(soundBlockHead, soundStreamBlocks);
                    //    soundStreamBlocks.clear();
                    // }
                    // soundBlockHead = DefineSoundHead();
                    // soundStreamBlocks = new Vector();
                    //
                    // coreMovie = (FMovie)myMovie;
                    // coreMovie.addSound(soundBlockHead.getID(), soundBlockHead);
                    //
                    break;
                case 19:
                    // System.out.print("Block:" + aTag.length);
                    //
                    // if (soundStreamBlocks != null)
                    // {
                    // soundStreamBlocks.add(getSoundStreamBlock(aTag.length));
                    // }

                    break;
                case 20:
                    if (myFilter.getFilter(myFilter.BITMAP)) {
                        FLossless aLossless = myImageReader.parseLossless(1, (int) aTag.length);
                        coreMovie = (FMovie) myMovie;
                        coreMovie.add(aLossless.getID(), aLossless);
                    }
                    break;
                case 21: // Define Bits 2
                    if (myFilter.getFilter(myFilter.BITMAP)) {
                        FBitmap aBitmap = myImageReader.parseBitmap(2, aTag.length);
                        coreMovie = (FMovie) myMovie;
                        coreMovie.add(aBitmap.getID(), aBitmap);
                    }
                    break;
                case 22: // DefineShape2
                    if (depth == 0 && myFilter.getFilter(myFilter.SHAPE)) // Otherwise Skip, Bad Format
                    {
                        FShape aShape = parseDefineShape(2);
                        coreMovie = (FMovie) myMovie;
                        coreMovie.add(aShape.getID(), aShape);
                    }
                    break;
                case 23: // DefineButtonCxForm
                    break;
                case 24: // Protect
                    //case 25: // Might Protect
                    break;
                case 26: // Place Object 2
                case 70: // Place Object 3
                {
                    PlaceObject aPlaceObject = parsePlaceObject2(aTag.id == 26 ? 2 : 3);
                    aTag.myObject = aPlaceObject;
                    myMovie.addTag(aTag);
                }
                break;
                case 28: // RemoveObject2
                {
                    aTag.myObject = parseRemoveObject(2);
                    myMovie.addTag(aTag);
                }
                break;
                case 32: // DefineShape3
                    if (depth == 0 && myFilter.getFilter(myFilter.SHAPE)) // Otherwise Skip, Bad Format
                    {
                        FShape aShape = parseDefineShape(3);
                        coreMovie = (FMovie) myMovie;
                        coreMovie.add(aShape.getID(), aShape);
                    }
                    break;
                case 33: // DefineText2
                    FText aText = parseText(2);
                    if (aText != null) {
                        coreMovie = (FMovie) myMovie;
                        coreMovie.add(aText.getID(), aText);
                    }
                    break;
                case 34: // DefineButton2

                    break;
                case 35: // Define Bits 3
                    if (myFilter.getFilter(myFilter.BITMAP)) {
                        FBitmap aBitmap = myImageReader.parseBitmap(3, aTag.length);
                        coreMovie = (FMovie) myMovie;
                        coreMovie.add(aBitmap.getID(), aBitmap);
                    }
                    break;
                case 36:
                    if (myFilter.getFilter(myFilter.BITMAP)) {
                        FLossless aLossless = myImageReader.parseLossless(2, (int) aTag.length);
                        if (aLossless != null) {
                            coreMovie = (FMovie) myMovie;
                            coreMovie.add(aLossless.getID(), aLossless);
                        }
                    }
                    break;
                case 37: // DefineEditText

                    break;
                case 38:// DefineMovie
                    break;
                case 39: // DefineSprite
                    if (myFilter.getFilter(myFilter.SHAPE)) {
                        FSprite aSprite = parseDefineSprite(depth, longTotalFileLength, myMovie.getCore());
                        coreMovie = (FMovie) myMovie;
                        coreMovie.add(aSprite.getID(), aSprite);
                    }
                    break;
                case 43: // FrameLabel

                    break;
                case 46: // DefineMorphShape
                    if (depth == 0 && myFilter.getFilter(myFilter.SHAPE)) {
                        FMorph aMorph = parseMorphShape();
                        coreMovie = (FMovie) myMovie;
                        coreMovie.add(aMorph.getID(), aMorph);
                    }
                    break;
                case 48: // DefineFont2
                    if (myFilter.getFilter(myFilter.FONT)) {
                        FFont aFont = parseDefineFont2(2);
                        if (aFont != null) {
                            coreMovie = (FMovie) myMovie;
                            coreMovie.add(aFont.getID(), aFont);
                        }
                    }
                    break;
                case 56: // ExportAssets

                    break;
                case 57: // ImportAssets

                    break;
                case 58: // EnableDebugger

                    break;
                case 75: // DefineFont3
                    if (myFilter.getFilter(myFilter.FONT)) {
                        FFont aFont = parseDefineFont2(3);
                        if (aFont != null) {
                            coreMovie = (FMovie) myMovie;
                            coreMovie.add(aFont.getID(), aFont);
                        }
                    }
                    break;
                case 83: // DefineShape4
                    if (depth == 0 && myFilter.getFilter(myFilter.SHAPE)) // Otherwise Skip, Bad Format
                    {
                        FShape aShape = parseDefineShape(4);
                        coreMovie = (FMovie) myMovie;
                        coreMovie.add(aShape.getID(), aShape);
                    }
                    break;
                // case 70: // Place Object 3
                // {
                // PlaceObject aPlaceObject = parsePlaceObject2();
                // aTag.myObject = aPlaceObject2;
                // FMovie coreMovie = (FMovie)myMovie;
                // myMovie.addTag(aTag);
                // }
                //break;
                case 1023: // DefineBitsPtr
                    //System.out.println("Tag: DefineBitsPtr");
                    break;

                default:
                    //System.out.println(aTag);
                    break;
            }

            if (aTag.id == 0) // End
            {
                break;
            }

            if (aTag.length < 0) {
                break;
            }

            if (bi.bitsRead < endByte) {
                long skipTotal = (endByte - bi.bitsRead);
                //System.out.println("Skipping: From " + bi.bitsRead + " To " + endByte + " Total " + skipTotal);
                bi.skip(skipTotal);
            } else if (bi.bitsRead > endByte) {
                //for (int x=0;x<depth+1;x++)
                //{
                //System.out.print("\t");
                //}
                System.out.println("Error:Overbyte:" + (bi.bitsRead - endByte) + " " + bi.bitsRead + "\\" + endByte);
                return "Error: OVERBYTE when reading " + aTag + " " + (bi.bitsRead - endByte) + " " + bi.bitsRead + "\\" + endByte;
            }

        }

        return null;
    }

    // Tag Helpers
       /*
       private void combineTagHeadAndBlockData(FSound target, Vector data)
      {
         int size = 0;
         int [] output = null;
      
         for (int x=0;x<data.size();x++)
         {
            int [] a = (int []) data.get(x);
            size += a.length;
         }
      	
         output = new int[size];
      	
         int y = 0;
         for (int x=0;x<data.size();x++)
         {
            int [] a = (int []) data.get(x);
            for (int z=0;z<a.length;z++)
            {
               output[y] = a[z];
               y++;
            }
         }
         
         target.setData(output);
      }
   	*/

    // Tag Parsers
       /*
       private int [] getSoundStreamBlock(long size) throws IOException
      {
      	
         int [] data = bi.readBytes((int) size);
         return data;
      }
      */

    private FSound DefineSoundHead() throws IOException {
      /*
      struct swf_soundstreamhead {
      swf_tag			f_tag;		// 18 or 45
      unsigned		f_reserved : 4;
      unsigned		f_playback_rate : 2;
      unsigned		f_playback_size : 1;
      unsigned		f_playback_stereo : 1;
      unsigned		f_compression : 4;
      unsigned		f_sound_rate : 2;
      unsigned		f_sound_size : 1;
      unsigned		f_sound_stereo : 1;
      unsigned short		f_sample_size;
      if(f_compression == 2) {
      	signed short	f_latency_seek;
      }
      };
      */
        // This will force a jump

        //int aId				= (int)bl.ui16();

        bl.ub(4);
        bl.ub(2);
        bl.ub(1);
        bl.ub(1);
        int aFormat = (int) bl.ub(4);

        FSound aSound = new FSound(-50, aFormat, 0, false, false, 0, null);
        return aSound;
    }

    private FSound DefineSound(long size) throws IOException {
        int aId = (int) bl.ui16();//readUI16();
        int aFormat = (int) bl.ub(4);//(int)in.readUBits( 4 );
        int aFrequency = (int) bl.ub(2);//(int)in.readUBits( 2 );
        boolean aBits16 = (bl.ub(1) != 0);//in.readUBits(1) != 0;
        boolean aStereo = (bl.ub(1) != 0);//in.readUBits(1) != 0; // 3 Bytes
        long aSampleCount = bl.ui32(); //(int)in.readUI32();

        int[] data = bi.readBytes((int) size - 7);

        FSound aSound = new FSound(aId, aFormat, aFrequency, aBits16, aStereo, aSampleCount, data);
        return aSound;
    }

    private RemoveObject parseRemoveObject(int format) throws IOException {
        RemoveObject ro = null;

        if (format == 1) {
             /*
         	RemoveObject      
         	Field      	     Type      	     Comment     
         	Header      	  RECORDHEADER      Tag ID = 5     
         	CharacterId      UI16      	     ID of character to remove     
         	Depth      	     UI16      	     Depth of character   
         	*/

            ro = new RemoveObject((int) bl.ui16(), (int) bl.ui16());
        } else if (format == 2) {
         	/*
         	RemoveObject2
         	Field        	  Type        	     Comment      
         	Header      	  RECORDHEADER      Tag ID = 28     
         	Depth      	     UI16      	     Depth of character  
         	*/

            ro = new RemoveObject((int) bl.ui16());
        }

        return ro;
    }

    public PlaceObject parsePlaceObject(long endByte) throws IOException {
      /*
      	Header        	      RECORDHEADER        	      Tag ID = 4      
      	CharacterId      	     UI16      	     ID of character to place     
      	Depth      	     UI16      	     Depth of character     
      	Matrix      	     MATRIX      	     Transform matrix data     
      	ColorTransform (optional)      	     CXFORM      	     Color transform data      
      */
        PlaceObject myPlaceObject = new PlaceObject();

        // Set Flags
        myPlaceObject.setPlaceFlagHasMatrix();
        myPlaceObject.setPlaceFlagHasCharacter();

        // Read ID
        myPlaceObject.setID((int) bl.ui16());

        // Set Depth
        myPlaceObject.setDepth((int) bl.ui16());

        // Set Matrix
        myPlaceObject.setMatrix(myTypeReader.matrix());

        // 1.18 Color Transofrm Removed / Has Issues

        //ColorTransform		If PlaceFlagHasColorTransform = 1 CXFORM		Color transform data

        //if (bi.bitsRead < endByte)
        //{
        //myPlaceObject.setPlaceFlagHasColorTransform();
        //myPlaceObject.setCXFORM(myTypeReader.cxform(true));
        //}

        return myPlaceObject;
    }


    public PlaceObject parsePlaceObject2(int placeVersion) throws IOException {
      /*
            Header        	      		  RECORDHEADER      Tag ID = 26      
      		PlaceFlagHasClipActions      UB[1]      	     Has Clip Actions (added to Flash 5)     
      		PlaceFlagReserved      	     UB[1]      	     Reserved Flags     
      		PlaceFlagHasName      	     UB[1]      	     Has name     
      		PlaceFlagHasRatio      	     UB[1]      	     Has ratio     
      		PlaceFlagHasColorTransform   UB[1]      	     Has color transform     
      		PlaceFlagHasMatrix      	  UB[1]      	     Has matrix     
      		PlaceFlagHasCharacter        UB[1]      	     Places a character     
      		PlaceFlagMove      	        UB[1]      	     Defines a character to be moved     
      		Depth      	                 UI16      	     Depth of character     
      		CharacterId      	     If PlaceFlagHasCharacter = 1 UI16      	     ID of character to place     
      		Matrix      	     If PlaceFlagHasMatrix = 1 MATRIX      	     Transform matrix data     
      		ColorTransform      	     If PlaceFlagHasColorTransform = 1 CXFORM      	     Color transform data     
      		Ratio      	     If PlaceFlagHasRatio = 1 UI16      	         
      		Name      	     If PlaceFlagHasName = 1 STRING      	     Name of character     
      		ClipActions      	     If PlaceFlagHasClipActions = 1 ClipActions      	     Clip Actions Data   
      */

        PlaceObject myPlaceObject2 = new PlaceObject();

        // Version > 8
        int f_place_reserved = 0;// : 5;
        boolean f_place_bitmap_caching = false;// : 1;
        boolean f_place_blend_mode = false;// : 1;
        boolean f_place_filters = false;// : 1;

        if (placeVersion == 3) {
            f_place_reserved = (int) bl.ub(5);
            f_place_bitmap_caching = bl.ub(1) == 1;
            f_place_blend_mode = bl.ub(1) == 1;
            f_place_filters = bl.ub(1) == 1;
        }

        int flags = (int) bl.ui8();

        myPlaceObject2.setDepth((int) bl.ui16());

        // Sort Flags

        if ((flags & 64) == 64) {
            //System.out.println("Clip Action");
            myPlaceObject2.setPlaceFlagHasClipActions();
        }

        if ((flags & 128) == 128) {
            //System.out.println("Reserved");
            myPlaceObject2.setPlaceFlagReserved();
        }

        if ((flags & 32) == 32) {
            myPlaceObject2.setPlaceFlagHasName();
        }

        if ((flags & 16) == 16) {
            myPlaceObject2.setPlaceFlagHasRatio();
        }

        if ((flags & 8) == 8) {
            myPlaceObject2.setPlaceFlagHasColorTransform();
        }

        if ((flags & 4) == 4) {
            myPlaceObject2.setPlaceFlagHasMatrix();
        }

        if ((flags & 2) == 2) {
            myPlaceObject2.setPlaceFlagHasCharacter();
        }

        if ((flags & 1) == 1) {
            myPlaceObject2.setPlaceFlagMove();
        }

        //CharacterId   If PlaceFlagHasCharacter = 1 UI16   ID of character to place
        if (myPlaceObject2.getPlaceFlagHasCharacter()) {
            myPlaceObject2.setID((int) bl.ui16());
        }

        //Matrix      	     If PlaceFlagHasMatrix = 1 MATRIX      	     Transform matrix data
        if (myPlaceObject2.getPlaceFlagHasMatrix()) {
            myPlaceObject2.setMatrix(myTypeReader.matrix());
        }

        //ColorTransform		If PlaceFlagHasColorTransform = 1 CXFORM		Color transform data
        if (myPlaceObject2.getPlaceFlagHasColorTransform()) {
            myPlaceObject2.setCXFORM(myTypeReader.cxform(true));
        }

        //Ratio		If PlaceFlagHasRatio = 1 UI16
        if (myPlaceObject2.getPlaceFlagHasRatio()) {
            myPlaceObject2.setRatio((int) bl.ui16());
        }

        //Name		If PlaceFlagHasName = 1 STRING      	     Name of character
        if (myPlaceObject2.getPlaceFlagHasName()) {
            myPlaceObject2.setName(myTypeReader.string());
        }

        //ClipActions		If PlaceFlagHasClipActions = 1 ClipActions      	     Clip Actions Data
        if (myPlaceObject2.getPlaceFlagHasClipActions()) {
            myPlaceObject2.setClipDepth((int) bl.ui16());
        }

        return myPlaceObject2;
    }

    private FSprite parseDefineSprite(int depth, long longTotalFileLength, FMovie master) throws IOException {
      	/*
      		Header        	      RECORDHEADER        	      Tag ID = 39      
      		Sprite ID      	     UI16      	     ID of sprite     
      		FrameCount      	     UI16      	     Number of frames in Sprite.     
      		ControlTags      	     TAG[one or more]      	     A series of tags   
      	*/

        FSprite myFSprite = new FSprite((int) bl.ui16(), bl.ui16(), master);
        parseTags(myFSprite, longTotalFileLength, depth + 1);
        return myFSprite;
    }

    private FShape parseDefineShape(int format) throws IOException {
      	/*
      		Read Shape Information
      	   ShapeID        UI16         	  ID for this character      
      		ShapeBounds    RECT      	     Bounds of the shape     
      		Shapes      	ShapeWithStyle   Shape information  
      	*/

        //System.out.println("parseDefineShape");
        //System.out.println("Creating Objects");

        int sid = (int) bl.ui16();
        FRect myRect = myTypeReader.rect();
        bi.align();

        FShape myFShape = new FShape(format, sid, myRect);
        FStyle myStyle = myFShape.getStyle();

        // Define Shape 4 Extra Tags
        if (format == 4) {
         	/*
         	if(has_strokes)
         	{
         		swf_rect			f_stroke_rect;
         		if(is_morph)
         		{
         			swf_rect			f_stroke_rect_morph;
         		}
         		unsigned			f_define_shape_reserved : 6;
         		unsigned			f_define_shape_non_scaling_strokes : 1;
         		unsigned			f_define_shape_scaling_strokes : 1;
         	}
         	*/
            FRect strokeRect = myTypeReader.rect();
            bi.align();
            myFShape.setStrokeRect(strokeRect);
            // Not a Morph
            int reserved = (int) bl.ub(6);
            boolean sns = bl.ub(1) == 1;
            boolean ss = bl.ub(1) == 1;
            myFShape.setShapeNonScales(sns);
            myFShape.setShapeScales(ss);
        }
      	
      	/*
      		Read Shape With Style
      		FillStyles        	  FillStyleArray         	   Array of fill styles      
      		LineStyles      	     LineStyleArray      	      Array of line styles     
      		NumFillBits      	     NfillBits = UB[4]      	   Number of fill index bits     
      		NumLineBits      	     NlineBits = UB[4]      	   Number of line index bits     
      		ShapeRecords      	  ShapeRecord[one or more]    Shape records - see below  
      	*/

        parseFillStyleArray(myStyle.getFillStyles(), format, format);
        parseLineStyleArray(myStyle.getLineStyles(), format, format);
        parseShapeRecord(myStyle.getShapeRecords(), format);

        //bi.align();

        return myFShape;
    }

    // parse Fill Style Arrays
    private void parseFillStyleArray(Vector myStyles, int sFormat, int format) throws IOException {
      	/*
      		FillStyleCount        	      			count = UI8         	      		Count of fill styles      
      		FillStyleCountExtended      	     		If count = 0xFF count = UI16     Extended count of fill Styles.
      		Supported only for Shape2 and Shape3.     
      		FillStyles      	     						FillStyle[count]      	     		Array of fill styles 
      	*/

        // Read Count
        int count = (int) bl.ui8();
        //System.out.println("parseFillStyleArray");
        //System.out.println("Count = " + count);

        if (format > 1 && count == 0xFF) {
            count = (int) bl.ui16();
        }

        //System.out.println("Found Fills " + count);

        Object last = null;

        // Loop Through Line Styles
        for (int x = 0; x < count; x++) {
            Object o = myTypeReader.fillstyle(sFormat, format);
            if (o == null && last != null) {
                System.out.println(last);
            }
            last = o;
            //System.out.println("Texture: " + o);

            if (myFilter.getFilter(myFilter.BITMAP) && o instanceof FTexture) {
                FTexture tex = (FTexture) o;
                //System.out.println("Loading: " + tex);
                Object tO = coreMovie.getObjects().get("" + tex.getBitmapID());
                if (tO instanceof FImage) {
                    tex.setBitmap((FImage) tO);
                }
            }

            myStyles.add(o);
        }
    }

    // parse Line Style Arrays
    private void parseLineStyleArray(Vector myStyles, int sFormat, int format) throws IOException {
      	/*
      		LineStyleCount        	count = UI8         	         Count of line styles      
      		LineStyleCountExtended  If count = 0xFF count = UI16  Extended count of line Styles.     
      		LineStyles      	     	LineStyle[count]      	      Array of line styles  
      	*/

        // Read Count
        int count = (int) bl.ui8();
        //System.out.println("parseLineStyleArray");
        //System.out.println("Count = " + count);

        // Check and Read Larger Count
        if (sFormat > 1 && count == 0xFF) {
            count = (int) bl.ui16();
        }

        // Loop Through Line Styles
        for (int x = 0; x < count; x++) {
            FLine ls = myTypeReader.linestyle(sFormat, format);
            //System.out.println("New Line Style " + ls);
            myStyles.add(ls);
        }
    }

    // 1.18 Code

    private void parseTextRecord(int aFormat, int nGlyphBits, int nAdvanceBits, Vector aTextRecord) throws IOException {
      /*
      Field							Type										Comment
      
      TextRecordType				UB [1]									0 = glyph record, 1 = text style record   
           
      TextRecordType				UB [1] = 1								1 if text stage change record     
      TextReserved				UB [3]									Reserved � always 0     
      TextHasFont					HasFont = UB [1]						1 if text font specified     
      TextHasColor				HasColor = UB [1]						1 if text color specified     
      TextHasYOffset				HasYOffset = UB [1]					1 if Y offset specified     
      TextHasXOffset				HasXOffset = UB [1]					1 if X offset specified     
      TextFontID					if hasFont UI16						Font ID for following text     
      TextColor					If hasColor RGB
      If this record is part	of a DefineText2 tag then RGBA	Font color for following text     
      TextXOffset					If hasXOffset SI16					Font X offset for following text     
      TextYOffset					If hasYOffset SI16					Font Y offset for following text     
      TextHeight					If hasFont UI16						Font height for following text     
      
      TextRecordType				UB [1] = 0								0 if text glyph record      
      TextGlyphCount				nGlyphs = UB [7]						Number of glyphs in record     
      TextGlyphEntries			GLYPHENTRY[nGlyphs]					Glyph entry � see below 
      */
        int recordType = 0;

        while (true) {
            // 1.18 Alignment Added
            bi.align();

            // Read the Record Type
            recordType = (int) bl.ub(1);

            switch (recordType) {
                case 0: {
                    int glyphCount = (int) bl.ub(7);
                    if (glyphCount == 0) {
                        return;
                    } else {
                        for (int x = 0; x < glyphCount; x++) {
                            aTextRecord.add(new FTextGlyph((int) bl.ub(nGlyphBits), (int) bl.sb(nAdvanceBits)));
                        }
                    }
                }
                break;
                case 1: {
                  /*
                  TextReserved				UB [3]									Reserved � always 0     
                  TextHasFont					HasFont = UB [1]						1 if text font specified     
                  TextHasColor				HasColor = UB [1]						1 if text color specified     
                  TextHasYOffset				HasYOffset = UB [1]					1 if Y offset specified     
                  TextHasXOffset				HasXOffset = UB [1]					1 if X offset specified     
                  TextFontID					if hasFont UI16						Font ID for following text     
                  TextColor					If hasColor RGB
                  If this record is part	of a DefineText2 tag then RGBA	Font color for following text     
                  TextXOffset					If hasXOffset SI16					Font X offset for following text     
                  TextYOffset					If hasYOffset SI16					Font Y offset for following text     
                  TextHeight					If hasFont UI16						Font height for following text
                  */
                    FTextRecord aRecord = new FTextRecord(aFormat);
                    int reserved = (int) bl.ub(3);
                    int hasFont = (int) bl.ub(1);
                    int hasColor = (int) bl.ub(1);
                    int hasOffsetY = (int) bl.ub(1);
                    int hasOffsetX = (int) bl.ub(1);

                    aTextRecord.add(aRecord);

                    if (hasFont == 1) {
                        //TextFontID	if hasFont UI16
                        int fontID = (int) bl.ui16();
                        aRecord.setFont((FFont) coreMovie.findObject(fontID));
                    }

                    if (hasColor == 1) {
                        FColor aColor = myTypeReader.rgba(aFormat == 1 ? 1 : 3);
                        aRecord.setColor(aColor);
                    }

                    if (hasOffsetX == 1) {
                        int offset = (int) bl.si16();
                        aRecord.setOffsetX(offset);
                    }

                    if (hasOffsetY == 1) {
                        int offset = (int) bl.si16();
                        aRecord.setOffsetY(offset);
                    }

                    if (hasFont == 1) {
                        int height = (int) bl.ui16();
                        aRecord.setHeight(height);
                    }

                }
                break;
            }
        }

    }

    private void parseShapeRecord(Vector myShapeRecord, int format) throws IOException {
        // First Things First
        int NumFillBits = (int) bl.ub(4);
        int NumLineBits = (int) bl.ub(4);

        //if (isDebug)
        // {
        //System.out.println("Fill Bits: " + NumFillBits + " Line Bits: " + NumLineBits);
        // }

        //System.out.println("parseShapeRecord");

        while (true) {
            // Read int First Bit
            int type = (int) bl.ub(1);

            //System.out.println("type = " + type);

            if (type == 0) // Exit Or StyleChange
            {
            
            /*
            StateNewStyles        	  NewStyles = UB[1]        	      New styles flag. Used by DefineShape2 and DefineShape3 only.      
            StateLineStyle      	     LineStyle = UB[1]      	     Line style change flag     
            StateFillStyle1      	  FillStyle1 = UB[1]      	     Fill style 1 change flag     
            StateFillStyle0      	  FillStyle0 = UB[1]      	     Fill style 0 change flag     
            StateMoveTo      	        MoveTo = UB[1]      	     Move to flag 
            */

                int count = 0;
                int StateNewStyles = (int) bl.ub(1);
                int StateLineStyle = (int) bl.ub(1);
                int StateFillStyle1 = (int) bl.ub(1);
                int StateFillStyle0 = (int) bl.ub(1);
                int StateMoveTo = (int) bl.ub(1);

                count = StateNewStyles + StateLineStyle + StateFillStyle1 + StateFillStyle0 + StateMoveTo;

                //System.out.println("Count: " + count);
                if (count == 0) {
                    //System.out.println("Break Out");
                    break;
                }
                //System.out.println("Style Change");

                // We have a Style Change
                FStyleChange myStyleChangeRecord = new FStyleChange();
            	/*
            		MoveBits        	     If moveTo nMoveBits = UB[5]        Move bit count      
            		MoveDeltaX      	     If moveTo SB[nMoveBits]      	    Delta X value     
            		MoveDeltaY      	     If moveTo SB[nMoveBits]      	    Delta Y value     
            	*/

                if (StateMoveTo == 1) {
                    int moveBits = (int) bl.ub(5);
                    myStyleChangeRecord.setMoveDeltaX(bl.sb(moveBits));
                    myStyleChangeRecord.setMoveDeltaY(bl.sb(moveBits));
                    //System.out.println("StateMoveTo " + myStyleChangeRecord.getMoveDeltaX() + " : " + myStyleChangeRecord.getMoveDeltaY());
                }

                //	StateFillStyle0      	     If fillStyle0 UB[nFillBits]      	     Fill 0 Style
                if (StateFillStyle0 == 1) {
                    //System.out.println("StateFillStyle0");
                    myStyleChangeRecord.setFillStyle0((int) bl.ub(NumFillBits));
                }
                //	StateFillStyle1      	     If fillStyle1 UB[nFillBits]      	     Fill 1 Style
                if (StateFillStyle1 == 1) {
                    //System.out.println("StateFillStyle1");
                    myStyleChangeRecord.setFillStyle1((int) bl.ub(NumFillBits));
                }
                //	StateLineStyle      	     If lineStyle UB[nLineBits]      	     Line Style
                if (StateLineStyle == 1) {
                    myStyleChangeRecord.setLineStyle((int) bl.ub(NumLineBits));
                    //System.out.println("StateLineStyle " + myStyleChangeRecord.getLineStyle());
                }
            	
            	/*
            		FillStyles      	     If newStyles FillStyleArray      	     Array of new fill styles     
            		LineStyles      	     If newStyles LineStyleArray      	     Array of new line styles     
            		NumFillBits      	     If newStyles NfillBits = UB[4]      	     Number of fill index bits for new styles     
            		NumLineBits      	     If newStyles NlineBits = UB[4]      	     Number of line index bits for new styles   
            	*/

                if (StateNewStyles == 1 && format > 1) {
                    //System.out.println("StateNewStyles");
                    parseFillStyleArray(myStyleChangeRecord.getFillStyleArray(), format, format);
                    parseLineStyleArray(myStyleChangeRecord.getLineStyleArray(), format, format);
                    NumFillBits = (int) bl.ub(4);
                    NumLineBits = (int) bl.ub(4);
                }

                myShapeRecord.add(myStyleChangeRecord);
            } else if (type == 1) // Edge
            {
                type = (int) bl.ub(1);

                //if (isDebug)
                // {
                // System.out.println("Type: " + type);
                //}

                int numBits = (int) bl.ub(4) + 2;
                FEdge myRecord = null;

                if (type == 0) // Curved
                {
                    //System.out.println("Curved Edge");
                    myRecord = new FEdge(FEdge.CURVED);
                    myRecord.setControlDeltaX(bl.sb(numBits));
                    myRecord.setControlDeltaY(bl.sb(numBits));
                    myRecord.setAnchorDeltaX(bl.sb(numBits));
                    myRecord.setAnchorDeltaY(bl.sb(numBits));
                } else if (type == 1) // Straight
                {
                    //System.out.println("Straight Edge");
                    myRecord = new FEdge(FEdge.STRAIGHT);

                    if (bl.ub(1) == 1) //GeneralLineFlag
                    {
                        myRecord.setDeltaX(bl.sb(numBits));
                        myRecord.setDeltaY(bl.sb(numBits));
                    } else {
                        if (bl.ub(1) == 1) // Vert Flag
                        {
                            myRecord.setDeltaY(bl.sb(numBits));
                        } else {
                            myRecord.setDeltaX(bl.sb(numBits));
                        }
                    }
                }

                myShapeRecord.add(myRecord);
            } else {
                IOException ex = new IOException("Read Error:-" + type);
                throw ex;
            }
        }
    }

    // Bitmaps
   	/*
       private FBitmap parseBitmap(int version, long size)  throws IOException
      {
         int sizeOfBlock = (int)size;
         
         int sizeOfData = sizeOfBlock;
         
         int sizeOfAlpha = 0;
      	
         int offset_to_alpha = 0;
      	
         if (version == 0)
         {
            JPEGTable = new byte[sizeOfBlock];
            for (int x=0;x<sizeOfBlock;x++)
            {
               JPEGTable[x] = (byte)bl.ui8();
            }
            return null;
         }
         else
         {
         	
         	
         	// unsigned short		f_image_id;
   //          	if(f_tag == DefineBitsJPEG3) {
   //          			// sizeof(f_encoding) + sizeof(f_image_data)/
   //          			unsigned long	f_offset_to_alpha;
   //          		}
   //          		if(f_tag != DefineBitsJPEG) {
   //          			// when DefineBitsJPEG, use JPEGTables instead/
   //          			unsigned char	f_encoding_tables[<variable size>];
   //          		}
   //          		unsigned char		f_image_data[<variable size>];
   //          		if(f_tag == DefineBitsJPEG3) {
   //          			unsigned char	f_alpha[<variable size>];
   //          		}
   //          	};
         	
         
            int id = (int)bl.ui16();
            sizeOfBlock -= 2;
            sizeOfData  = sizeOfBlock;
            
            if (version == 3)
            {
               offset_to_alpha = (byte)bl.ui16();
               sizeOfBlock -= 2;
               sizeOfData = offset_to_alpha;
               sizeOfAlpha = sizeOfBlock - sizeOfData;
            }
            
            byte [] bitTable = null;
            
            if (version == 1)
            {
            	// JPEG Tables are funny with the extra stream info.
            	// Have to compensate
               bitTable = new byte[(sizeOfData-2) + (JPEGTable.length-2)];
            	
            	// Skip the last two bytes
               for (int x=0;x<(JPEGTable.length-2);x++)
               {
                  bitTable[x] = JPEGTable[x];
               }
               // Skip the first two bytes
               bi.skip(2);
               for (int x=0;x<(sizeOfData-2);x++)
               {
                  bitTable[x+(JPEGTable.length-2)] = (byte)bl.ui8();
               }
            }
            else if (version == 2 || version == 3)
            {
               bitTable = new byte[sizeOfData];
               int offsetX = 0;
            	
               for (int x=0;x<sizeOfData;x++)
               {
                  bitTable[x] = (byte)bl.ui8();
               }
            	
               int []startbit = new int [4];
               
               for (int x=0;x<4;x++)
               {
                  startbit[x] = bitTable[x] & 0xFF;
                  //System.out.println(startbit[x]);
               }
            	
               int skipAmount = 0;
            	
               if ( startbit[1] != 0xd8 )
               {
                  skipAmount = 4;
               }
            	
               if (skipAmount > 0)
               {
                  bitTable = Arrays.copyOfRange(bitTable, skipAmount, bitTable.length-1);
               }  
            	
            	// Find Bad Splits
            	// ff d9 ff d8
            	
               int splitPosition = 0;
            	
               for (int x=4;x<bitTable.length-4;x++)
               {
                  if ( (bitTable[x] & 0xFF) == 0xFF)
                  {
                     startbit[1] = bitTable[x+1] & 0xFF;
                     startbit[2] = bitTable[x+2] & 0xFF;
                     startbit[3] = bitTable[x+3] & 0xFF;
                     if (startbit[1] == 0xd9 && startbit[2] == 0xff && startbit[3] == 0xd8)
                     {
                        splitPosition = x;
                        break;
                     }
                  }
               }
            	
               if (splitPosition > 0)
               {
                  byte [] newTable = new byte[bitTable.length-4];
                  int y=0;
                  for (int x=0;x<bitTable.length;x++)
                  {
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
            	
            	/*
            	// Encoding Table      	
               for (int x=0;x<size2-4;x++)
               {
                  bitTable[x] = (byte)bl.ui8();
                  if (x > 8)
                  {
                     if ((0xFF & bitTable[x]) == 0xD9 && (0xFF & bitTable[x-1]) == 0xFF)
                     {
                     // Found End of Table
                        offsetX = x - 1;
                        break;
                     }
                     else
                     {
                        offsetX = x;
                     }
                  }
                  else
                  {
                     offsetX = x;
                  }
               }
            	
            	//Skip 2, The Start Bytes
               bi.skip(2);
            	
            	// Data
               // for (int x=0;x<(size2-4)-offsetX;x++)
   //                {
   //                   bitTable[offsetX+x] = (byte)bl.ui8();
   //                }
               
            }
         
            FBitmap aBitmap = new FBitmap( id, bitTable );
            aBitmap.setVersion(version);
         	
            return aBitmap; 
         }
      }
   	*/

    // Morphing

    private FMorph parseMorphShape() throws IOException {
        int id = (int) bl.ui16();
        FRect a = myTypeReader.rect();
        bi.align();
        FRect b = myTypeReader.rect();
        //bi.align();
        FMorph myFMorph = new FMorph(id, a, b);
        //return null;
        //
        long shape1Length = bl.ui32();
        //shape1Length += bi.bitsRead;

        //System.out.println("Shape 2 Start : " + shape1Length);

        parseMorphFillStyleArray(myFMorph.getStartStyle().getFillStyles());
        parseMorphLineStyleArray(myFMorph.getStartStyle().getLineStyles());

        parseMorphRecord(myFMorph.getStartStyle().getShapeRecords(), -1);

        bi.align();

        parseMorphRecord(myFMorph.getEndStyle().getShapeRecords(), -1);

        return myFMorph;
    }

    // parse Fill Style Arrays
    private void parseMorphFillStyleArray(Vector myStyles) throws IOException {
      	/*
      		FillStyleCount        	      			count = UI8         	      		Count of fill styles      
      		FillStyleCountExtended      	     		If count = 0xFF count = UI16     Extended count of fill Styles.
      		Supported only for Shape2 and Shape3.     
      		FillStyles      	     						FillStyle[count]      	     		Array of fill styles 
      	*/

        // Read Count
        int count = (int) bl.ui8();
        //System.out.println("parseFillStyleArray");
        //System.out.println("Count = " + count);

        // Check and Read Larger Count
        if (count == 0xFF) {
            count = (int) bl.ui16();
        }

        //System.out.println("Found Fills " + count);

        // Loop Through Line Styles
        for (int x = 0; x < count; x++) {
            myStyles.add(myTypeReader.morphFillStyle());
        }
    }

    // parse Line Style Arrays
    private void parseMorphLineStyleArray(Vector myStyles) throws IOException {
      	/*
      		LineStyleCount        	count = UI8         	         Count of line styles      
      		LineStyleCountExtended  If count = 0xFF count = UI16  Extended count of line Styles.     
      		LineStyles      	     	LineStyle[count]      	      Array of line styles  
      	*/

        // Read Count
        int count = (int) bl.ui8();
        //System.out.println("parseLineStyleArray");
        //System.out.println("Count = " + count);

        // Check and Read Larger Count
        if (count == 0xFF) {
            count = (int) bl.ui16();
        }

        // Loop Through Line Styles
        for (int x = 0; x < count; x++) {
            //System.out.println("New Line Style " + ls);
            myStyles.add(myTypeReader.morphLineStyle());
        }
    }

    private void parseMorphRecord(Vector myShapeRecord, long length) throws IOException {
        //long endLength = bi.bitsRead + length;

        //System.out.println("End " + length);

        int NumFillBits = 0;
        int NumLineBits = 0;
        int edgeCounter = 0;

        //if (length != -1)
        //{
        // First Things First
        NumFillBits = (int) bl.ub(4);
        NumLineBits = (int) bl.ub(4);

        //System.out.println("parseShapeRecord");
        //}

        while (1 == 1) {
            // System.out.println("Bits" + bi.bitsRead);
            //if (length != -1)
            //{
            // if (bi.bitsRead == length)
            // {
            // return;
            // }
            // }

            // Read int First Bit
            int type = (int) bl.ub(1);
            //System.out.println("Type: " + type);
            //System.out.println("type = " + type);

            if (type == 0) // Exit Or StyleChange
            {
            
            /*
            StateNewStyles        	  NewStyles = UB[1]          New styles flag. Used by DefineShape2 and DefineShape3 only.      
            StateLineStyle      	     LineStyle = UB[1]      	  Line style change flag     
            StateFillStyle1      	  FillStyle1 = UB[1]      	  Fill style 1 change flag     
            StateFillStyle0      	  FillStyle0 = UB[1]      	  Fill style 0 change flag     
            StateMoveTo      	        MoveTo = UB[1]      	     Move to flag 
            */

                int count = 0;
                int StateNewStyles = (int) bl.ub(1);
                int StateLineStyle = (int) bl.ub(1);
                int StateFillStyle1 = (int) bl.ub(1);
                int StateFillStyle0 = (int) bl.ub(1);
                int StateMoveTo = (int) bl.ub(1);

                count = StateNewStyles + StateLineStyle + StateFillStyle1 + StateFillStyle0 + StateMoveTo;

                //System.out.println("Count: " + count);
                if (count == 0) {
                    //System.out.println("Break Out");
                    break;
                }
                //System.out.println("Style Change");

                // We have a Style Change
                FStyleChange myStyleChangeRecord = new FStyleChange();
            	/*
            		MoveBits        	     If moveTo nMoveBits = UB[5]        Move bit count      
            		MoveDeltaX      	     If moveTo SB[nMoveBits]      	    Delta X value     
            		MoveDeltaY      	     If moveTo SB[nMoveBits]      	    Delta Y value     
            	*/

                if (StateMoveTo == 1) {
                    int moveBits = (int) bl.ub(5);
                    myStyleChangeRecord.setMoveDeltaX(bl.sb(moveBits));
                    myStyleChangeRecord.setMoveDeltaY(bl.sb(moveBits));
                    //System.out.println("StateMoveTo " + myStyleChangeRecord.getMoveDeltaX() + " : " + myStyleChangeRecord.getMoveDeltaY());
                }

                //	StateFillStyle0      	     If fillStyle0 UB[nFillBits]      	     Fill 0 Style
                if (StateFillStyle0 == 1) {
                    //System.out.println("StateFillStyle0");
                    myStyleChangeRecord.setFillStyle0((int) bl.ub(NumFillBits));
                }
                //	StateFillStyle1      	     If fillStyle1 UB[nFillBits]      	     Fill 1 Style
                if (StateFillStyle1 == 1) {
                    //System.out.println("StateFillStyle1");
                    myStyleChangeRecord.setFillStyle1((int) bl.ub(NumFillBits));
                }
                //	StateLineStyle      	     If lineStyle UB[nLineBits]      	     Line Style
                if (StateLineStyle == 1) {
                    myStyleChangeRecord.setLineStyle((int) bl.ub(NumLineBits));
                    //System.out.println("StateLineStyle " + myStyleChangeRecord.getLineStyle());
                }
            	
            	/*
            		FillStyles      	     If newStyles FillStyleArray      	     Array of new fill styles     
            		LineStyles      	     If newStyles LineStyleArray      	     Array of new line styles     
            		NumFillBits      	     If newStyles NfillBits = UB[4]      	     Number of fill index bits for new styles     
            		NumLineBits      	     If newStyles NlineBits = UB[4]      	     Number of line index bits for new styles   
            	*/
                if (StateNewStyles == 1) {
                    //System.out.println("StateNewStyles");
                    parseMorphFillStyleArray(myStyleChangeRecord.getFillStyleArray());
                    parseMorphLineStyleArray(myStyleChangeRecord.getLineStyleArray());
                    NumFillBits = (int) bl.ub(4);
                    NumLineBits = (int) bl.ub(4);
                }

                myShapeRecord.add(myStyleChangeRecord);
            } else if (type == 1) // Edge
            {
                type = (int) bl.ub(1);
                int numBits = (int) bl.ub(4) + 2;
                FEdge myRecord = null;

                if (type == 0) // Curved
                {
                    //System.out.println("Curved Edge");
                    myRecord = new FEdge(FEdge.CURVED, edgeCounter++);
                    myRecord.setControlDeltaX(bl.sb(numBits));
                    myRecord.setControlDeltaY(bl.sb(numBits));
                    myRecord.setAnchorDeltaX(bl.sb(numBits));
                    myRecord.setAnchorDeltaY(bl.sb(numBits));
                } else if (type == 1) // Straight
                {
                    //System.out.println("Straight Edge");
                    myRecord = new FEdge(FEdge.STRAIGHT, edgeCounter++);

                    if (bl.ub(1) == 1) //GeneralLineFlag
                    {
                        myRecord.setDeltaX(bl.sb(numBits));
                        myRecord.setDeltaY(bl.sb(numBits));
                    } else {
                        if (bl.ub(1) == 1) // Vert Flag
                        {
                            myRecord.setDeltaY(bl.sb(numBits));
                        } else {
                            myRecord.setDeltaX(bl.sb(numBits));
                        }
                    }
                }

                //System.out.println("Line " + myRecord.getDeltaX() + ":" + myRecord.getDeltaY());

                myShapeRecord.add(myRecord);
            }
        }

        //System.out.println("Count " + edgeCounter + " " + myShapeRecord.size() );
    }

    public FFont parseDefineFont() throws IOException {
        // Skip Header
        //Header 	RECORDHEADER 	Tag ID = 48

        //FontId 	UI16 	Font ID for this information
        FFont aFont = new FFont(1, (int) bl.ui16());

        long glyOffTable = bi.bitsRead;

        int offsetToStartOfGlyphs = (int) bl.ui16();
        int glyphCount = offsetToStartOfGlyphs / 2;

        long[] glyphOffsetTable = new long[glyphCount];
        glyphOffsetTable[0] = offsetToStartOfGlyphs;
        for (int x = 1; x < glyphCount; x++) {
            glyphOffsetTable[x] = (int) bl.ui16();
        }

        //bi.skip( offsetToStartOfGlyphs - 2 );

        Vector[] glyphVector = new Vector[glyphCount];

        for (int x = 0; x < glyphCount; x++) {
            bi.align();

            if ((bi.bitsRead - glyOffTable) != glyphOffsetTable[x]) {
                if (isDebug) {
                    System.out.println("Overbyte in Glyphs");
                }
                IOException ieo = new IOException("Glyph Error:- Overbyte " + ((bi.bitsRead - glyOffTable) - glyphOffsetTable[x]));
                throw ieo;
            }

            Vector glyph = new Vector();
            parseShapeRecord(glyph, 3);
            glyphVector[x] = glyph;
        }

        aFont.setGlyphs(glyphVector);

        return aFont;
    }

    public FFont parseDefineFont2(int format) throws IOException {
        // Skip Header
        //Header 	RECORDHEADER 	Tag ID = 48

        //FontId 	UI16 	Font ID for this information
        FFont aFont = new FFont(format, (int) bl.ui16());

        //0 FontFlagsHasLayout* 		UB[1] 	Has font metrics/layout information
        //1 FontFlagsShiftJIS 			UB[1] 	ShiftJIS encoding
        //2 FontFlagsUnicode 			UB[1] 	Unicode encoding
        //3 FontFlagsAnsi 				UB[1] 	ANSI encoding
        //4 FontFlagsWideOffsets 		UB[1] 	If 1, uses 32 bit offsets
        //5 FontFlagsWideCodes 			UB[1] 	If 1, font uses 16-bit codes, otherwise font uses 8 bit codes
        //6 FontFlagsItalic 				UB[1] 	Italic Font
        //7 FontFlagsBold 				UB[1] 	Bold Font

        for (int x = 0; x < 8; x++) {
            aFont.setFlag(x, ((int) bl.ub(1) == 1));
            //System.out.println("Flag("+x+"): " + aFont.getFlag(x));
        }

        //FontFlagsReserved 	UB[8] 	Reserved Flags
        bi.skip(1);

        //FontNameLen 	UI8 	Length of name
        int fontNameLength = (int) bl.ui8();
        //System.out.println("Name Length: " + fontNameLength);

        //FontName 	UI8[NameLen] 	Name of Font
        aFont.setName(myTypeReader.string(fontNameLength));

        //System.out.println("Font Name: " + fontName);


        //FontGlyphCount 	UI16 	Count of Glyphs in font � nGlyphs
        int glyphCount = (int) bl.ui16();

        aFont.setGlyphCount(glyphCount);

        long glyOffTable = bi.bitsRead;

        //FontOffsetTable 	If FontFlagsWideOffsets is 1, UI32[nGlyphs] else UI16[nGlyphs]
        //FontCodeOffset		If FontFlagsWideOffsets is 1, UI32 else UI16 Byte count from start of the FontOffsetTable to start of FontCodeTable
        long[] glyphOffsetTable = new long[glyphCount];
        long fontCodeOffset = 0;

        if (aFont.getFlag(FFont.FontFlagsWideOffsets)) {
            for (int x = 0; x < glyphCount; x++) {
                glyphOffsetTable[x] = bl.ui32();
                //System.out.println("Glyph Offset(" + x + "):- " + glyphOffsetTable[x]);
            }
            fontCodeOffset = bl.ui32();
        } else {
            for (int x = 0; x < glyphCount; x++) {
                glyphOffsetTable[x] = bl.ui16();
                //System.out.println("Glyph Offset(" + x + "):- " + glyphOffsetTable[x]);
            }
            fontCodeOffset = bl.ui16();
        }
        aFont.setGlyphOffsetTable(glyphOffsetTable);

        //FontShapeTable 	SHAPE[nGlyphs]
        Vector[] glyphVector = new Vector[glyphCount];

        for (int x = 0; x < glyphCount; x++) {
            bi.align();

            if ((bi.bitsRead - glyOffTable) != glyphOffsetTable[x]) {
                if (isDebug) {
                    System.out.println("Overbyte in Glyphs");
                }
                IOException ieo = new IOException("Glyph Error:- Overbyte " + ((bi.bitsRead - glyOffTable) - glyphOffsetTable[x]));
                throw ieo;
            }

            Vector glyph = new Vector();
            parseShapeRecord(glyph, 3);
            glyphVector[x] = glyph;
        }

        aFont.setGlyphs(glyphVector);

        //FontCodeTable 	If FontFlagsWideCodes is 1, UI16[nGlyphs] else UI8[nGlyphs]
      
      /*
      	FontCodeTable        	      If FontFlagsWideCodes is 1, UI16 [nGlyphs] else UI8 [nGlyphs]        	           
      	
      	FontAscent      	     			If FontFlagsHasLayout is 1, SI16 � Font ascender height      	         
      	FontDescent      	     			If FontFlagsHasLayout is 1, SI16 � Font descender height      	         
      	FontLeading      	     			If FontFlagsHasLayout is 1, SI16 � Font leading height      	         
      	FontAdvance Table      	     	If FontFlagsHasLayout is 1, SI16 [nGlyphs]      	         
      	FontBoundsTable      	     	If FontFlagsHasLayout is 1, RECT [nGlyphs]      	     Not used in Flash Player 4     
      	FontKerningCount      	     	If FontFlagsHasLayout is 1, nCount = UI16      	     Not used in Flash Player 4     
      	FontKerningTable      	     	If FontFlagsHasLayout is 1, KERNINGRECORD[nCount]      	     Not used in Flash Player 4  
      */

        int[] codeOffsets = new int[glyphCount];

        for (int x = 0; x < glyphCount; x++) {
            codeOffsets[x] = (aFont.getFlag(FFont.FontFlagsWideCodes) ? (int) bl.ui16() : (int) bl.ui8());
            //if (isDebug)
            //	{
            //System.out.println("Font Offset("+x+"):" + codeOffsets[x]);
            //}
        }

        aFont.setCodeOffsets(codeOffsets);
        codeOffsets = null;

        if (aFont.getFlag(FFont.FontFlagsHasLayout)) {
            //FontAscent 	If FontFlagsHasLayout is 1,
            //SI16 - Font ascender height
            aFont.setFontAscent((int) bl.si16());

            //FontDescent 	If FontFlagsHasLayout is 1,
            //SI16 - Font decender height
            aFont.setFontDescent((int) bl.si16());

            //FontLeading 	If FontFlagsHasLayout is 1,
            //SI16 - Font leading height
            aFont.setFontLeading((int) bl.si16());

            //FontAdvanceTable 	If FontFlagsHasLayout is 1,
            //SI16[nGlyphs]
            int[] advanceTable = new int[glyphCount];

            for (int x = 0; x < glyphCount; x++) {
                advanceTable[x] = (int) bl.si16();
            }

            aFont.setFontAdvanceTable(advanceTable);

            //FontBoundsTable 	If FontFlagsHasLayout = 1,
            //RECT[nGlyphs]
            FRect[] boundTable = new FRect[glyphCount];

            for (int x = 0; x < glyphCount; x++) {
                boundTable[x] = myTypeReader.rect();
                bi.align();
                //System.out.println(boundTable[x]);
            }

            aFont.setFontBoundsTable(boundTable);

            //FontKerningCount 	If FontFlagsHasLayout is 1,
            //UI16 - Font kerning table count
            aFont.setFontKerningCount((int) bl.si16());
        }

        return aFont;
    }

    // 1.18 Code

    private FText parseText(int version) throws IOException {
      	/*
      	Header					RECORDHEADER        	      Tag ID = 11      
      	TextId					UI16								ID for this character     
      	TextBounds				RECT								Bounds of the text     
      	TextMatrix				MATRIX							Matrix for the text     
      	TextGlyphBits			nGlyphBits = UI8				Bits in each glyph index     
      	TextAdvanceBits		nAdvanceBits = UI8			Bits in each advance value     
      	TextRecords				TEXTRECORD[zero or more]	Text records     
      	TextEndOfRecordsFlag	UI8 = 0							Always set to zero   
      	*/

        FText aText = new FText(version, (int) bl.ui16());

        aText.setRect(myTypeReader.rect());

        aText.setMatrix(myTypeReader.matrix());

        int nGlyphBits = (int) bl.ui8();

        int nAdvanceBits = (int) bl.ui8();

        Vector myRecords = new Vector();

        parseTextRecord(version, nGlyphBits, nAdvanceBits, myRecords);

        aText.setRecords(myRecords);

        return aText;
    }
      
   	/*
       private FLossless parseLossless(int version, int blockSize) throws IOException
      {
         int id = (int)bl.ui16();
         int format = (int)bl.ui8();
         int width = (int)bl.ui16();
         int height = (int)bl.ui16();
         
         blockSize -= 7;
      	
         //System.out.println(version+":"+id+":"+format+":"+width+":"+height);
      	
         if (format == 3)
         {
            FLossless aLossless = new FLossless(version, id, format, width, height);
         	
         	// Create image with RGB and alpha channel
            BufferedImage aImage = new BufferedImage(width, height, version == 2 ? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB);
            Graphics2D g2D = aImage.createGraphics(); // Context for buffered image
            // Set best alpha interpolation quality
            g2D.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
         
         	  // Clear image with transparent alpha by drawing a rectangle
            g2D.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR, 0.0f));
            Rectangle2D.Double rect = new Rectangle2D.Double(0,0,width,height); 
            g2D.fill(rect);
         
            aLossless.setImage(aImage);
         	
            int colorMapCount = (int)bl.ui8();
            blockSize -= 1;
         	
            byte [] myDataBlock = bl.uiBytes(blockSize);
         	
            ByteArrayInputStream bai = new ByteArrayInputStream(myDataBlock);
            BitInput aBi = new BitInput(bai);
            BitLibrary aBl = new BitLibrary(aBi);
            TypeReader aTypeReader = new TypeReader(aBl, aBi, 3);
         	
            aBi.zipStream();
         	
            FColor [] myColors = new FColor[colorMapCount];
            FColor aColor;
            //System.out.println("colors:"+ colorMapCount);
         	
            for (int x=0;x<colorMapCount;x++)
            {
               myColors[x] = aTypeReader.rgba( version == 1 ? 1 : 3 );
               //System.out.print(myColors[x]);
            }
            
            int skipSize = 4 - ((int)aBi.bitsRead &  3);
         	//System.out.print(myColors[x]);
            aBi.skip(skipSize);
         	
         	skipSize = 4 - (width &  3);
         	
            int index = 0;
            for (int y=0;y<height;y++)
            {
               for (int x=0;x<width;x++)
               {
                  index = (int)aBl.ui8();
                  //index--;
                  if (index >=0 && index < colorMapCount)
                  {
                     aColor = myColors[index];
                     g2D.setPaint(aColor.toColor());
                     rect.setRect(x, y, 1, 1);  
                     g2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, aColor.getA() / 255.0f )); // 
                     g2D.fill(rect);
                  }
               }
               
               if ( (width & 3) != 0)
               {
                  aBi.skip(skipSize);
               }
            }
         	
            return aLossless;
         }
         else if (format == 4)
         {
         
         }
         else if (format == 5)
         {
         
         }
         return null;
      }
   	*/
}