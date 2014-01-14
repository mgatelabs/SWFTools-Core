/*
    Copyright M-Gate Labs 2007
*/

/**
 <p>
 <b>Flash Type Reader</b>
 </p>
 <p>
 Used to read flash files at the type level.  <b>Example</b> Strings, Rects, Gradients...
 </p>
 */

package com.mgatelabs.swftools.support.swf.io;

import com.mgatelabs.swftools.exploit.control.AppControl;
import com.mgatelabs.swftools.support.swf.objects.*;
import com.mgatelabs.swftools.support.swf.tags.Tag;

import java.io.IOException;
import java.util.Vector;

public class TypeReader {
    private BitLibrary bl;
    private BitInput bi;
    private int version;

    private boolean isDebug;

    public TypeReader(BitLibrary bl, BitInput bi, int version) {
        this.bl = bl;
        this.bi = bi;
        this.version = version;
        this.isDebug = AppControl.getInfo().getDebug();
        if (isDebug) {
            System.out.println("Type Reader: Active");
        }
    }

    // String

    public String string() throws IOException {
        String result = "";
        int temp = (int) bl.ui8();
        while (temp != 0) {
            result += "" + (char) temp;
            temp = (int) bl.ui8();
        }
        return result;
    }

    public String string(int length) throws IOException {
        String result = "";
        int temp = 0;
        for (int x = 0; x < length; x++) {
            temp = (int) bl.ui8();
            result += "" + (char) temp;
        }
        return result;
    }

    // Flash Object Types /////////////////////////////////////////////////////


    public FRect rect() throws IOException {
        int nBits = (int) bl.ub(5);
        //System.out.println("MinBits: " + nBits);
        //System.out.println("Rect Bits : " + nBits);
        return new FRect(bl.sb(nBits), bl.sb(nBits), bl.sb(nBits), bl.sb(nBits));
    }

    // Tag Information
    public Tag tag() throws IOException {
        // Fast Way
        long temp = bl.ui16();

        int id = (int) (temp >> 6);
        long length = temp & 0x3f;

        if (length == 0x3f) {
            length = bl.ui32();
        }
        return new Tag(id, length);
    }

    // Line Style
    public FLine linestyle(int sFormat, int format) throws IOException {
          /*
             Width        	  UI16         	                      Width of line in twips
      		Color      	     RGB (Shape1 or Shape2); RGBA (Shape3) Color value including alpha channel information for Shape3s  
      	*/
        if (format == 4) {
             /*
         	if(f_tag == DefineShape4)
         	{
         		unsigned short twips	f_width;
         		
         		unsigned		f_start_cap_style : 2;
         		unsigned		f_join_style : 2;
         		unsigned		f_has_fill : 1;
         		unsigned		f_no_hscale : 1;
         		unsigned		f_no_vscale : 1;
         		unsigned		f_pixel_hinting : 1;
         		unsigned		f_reserved : 5;
         		unsigned		f_no_close : 1;
         		unsigned		f_end_cap_style : 2;
         		if(f_join_style == 2)
         		{
         			unsigned short fixed	f_miter_limit_factor;
         		}
         		if(f_has_fill)
         		{
         			swf_fill_style		f_fill_style;
         		}
         		else
         		{
         			swf_rgba		f_rgba;
         		}
         	}
         	*/
            // 2 Bytes
            int f_width = (int) bl.ui16();
            // 1 Byte
            int f_start_cap_style = (int) bl.ub(2);
            int f_join_style = (int) bl.ub(2);
            int f_has_fill = (int) bl.ub(1);
            int f_no_hscale = (int) bl.ub(1);
            int f_no_vscale = (int) bl.ub(1);
            int f_pixel_hinting = (int) bl.ub(1);
            // 1 Byte
            int f_reserved = (int) bl.ub(5);
            int f_no_close = (int) bl.ub(1);
            int f_end_cap_style = (int) bl.ub(2);

            FColor aColor = null;
            FFill aFill = null;
            float myMiterLimit = 0;
            if (f_join_style == 2) {
                myMiterLimit = bl.fb(16);
                //unsigned short fixed	f_miter_limit_factor;
            }
            if (f_has_fill == 1) {
                aFill = fillstyle(sFormat, format);
            } else {
                aColor = rgba(format);
            }

            return new FLine(f_width, f_start_cap_style, f_join_style, f_end_cap_style, f_no_close == 1, f_no_hscale == 1, f_no_vscale == 1, f_pixel_hinting == 1, myMiterLimit, aFill, aColor);

        } else {
            return new FLine(bl.ui16(), rgba(format));
        }
    }

    // Gradient
    public Vector gradient(int type, int sFormat, int format) throws IOException {
      	/*
      		if(tag == DefineShape4)
      		{
      			unsigned		f_spread_mode : 2;
      			unsigned		f_interpolation_mode : 2;
      			unsigned		f_count : 4;
      		}
      		else
      		{
      			unsigned		f_pad : 4;
      			unsigned		f_count : 4;
      		}
      		NumGradients        	      nGrads = UI8         	      1 to 8      
      		GradientRecords      	     GRADRECORD [nGradients]      	     Gradient records - see below  
      	*/

        int f_spread_mode = 0;
        int f_interpolation_mode = 0;
        int f_count = 0;
        if (sFormat == 4) {
            f_spread_mode = (int) bl.ub(2);
            f_interpolation_mode = (int) bl.ub(2);
            f_count = (int) bl.ub(4);
        } else {
            bl.ub(4);
            f_count = (int) bl.ub(4);
        }
        //int count = (int)bl.ui8() ;

        //System.out.println("Grad Records: " + count);

        Vector myVector = new Vector(f_count);

        for (int x = 0; x < f_count; x++) {
            myVector.add(gradrecord(format));
        }

        if (type == 0x13) {
            bl.ui16();
        }

        return myVector;
    }

    // GradRecord
    public FGradientRecord gradrecord(int format) throws IOException {
        return new FGradientRecord((int) bl.ui8(), rgba(format));
    }

    // FillStyle
    public FFill fillstyle(int sFormat, int format) throws IOException {
      	/*
      		type = UI8
      		0x00 = solid fill
      		0x10 = linear gradient fill
      		0x12 = radial gradient fill
      		0x40 = tiled bitmap fill
      		0x41 = clipped bitmap fill
      		
      		Color        	     If type = 0x00 RGBA (if Shape3);
      								  RGB (if Shape1 or Shape2)      	     Solid fill color with transparency information     
      	   GradientMatrix      If type = 0x10 or 0x12 MATRIX      	  Matrix for gradient fill     
      	   Gradient      	     If type = 0x10 or 0x12 GRADIENT        Gradient fill     
      	   BitmapID      	     If type = 0x40 or 0x41 UI16      	     ID of bitmap character for fill     
      	   BitmapMatrix        if type = 0x40 or 0x41 MATRIX      	     Matrix for bitmap fill  
      	*/

        int type = (int) bl.ui8();

        //	System.out.println(type);

        switch (type) {
            case 0x00: // solid fill
                return new FSolid(rgba(format));
            case 0x10: // linear gradient fill
                return new FGradient(format, FFill.LGRADIENT, matrix(), gradient(type, sFormat, format));
            case 0x12: // radial gradient fill
                return new FGradient(format, FFill.RGRADIENT, matrix(), gradient(type, sFormat, format));
            case 0x13: // focal gradient fill
                return new FGradient(format, FFill.FGRADIENT, matrix(), gradient(type, sFormat, format));
            case 0x40: // tiled bitmap fill
                return new FTexture(FFill.TBITMAP, (int) bl.ui16(), matrix());
            case 0x41: // clipped bitmap fill
            case 0x42: // clipped bitmap fill
            case 0x43: // clipped bitmap fill
            case 0x44: // clipped bitmap fill
                return new FTexture(FFill.CBITMAP, (int) bl.ui16(), matrix());
            default:

                System.out.println(type);
        }
        return null;
    }

    // RGBA
    public FColor rgba(int format) throws IOException {
      	/*
      		Red        	     UI8        	  Red color value      
      		Green      	     UI8      	     Green color value     
      		Blue      	     UI8      	     Blue color value     
      		Alpha      	     UI8      	     Transparency color value 
      	*/
        return new FColor((int) bl.ui8(), (int) bl.ui8(), (int) bl.ui8(), (int) ((format < 3) ? 255 : bl.ui8()));
    }

    // MATRIX
    public FMatrix matrix() throws IOException {
      	/*
      		HasScale        	hasScale = UB[1]        	      Has scale values if equal to 1      
      		NScaleBits      	If hasScale nScaleBits = UB[5]   Bits in each scale value field     
           	ScaleX      	   If hasScale FB[nScaleBits]      	X scale value     
           	ScaleY      	   If hasScale FB[nScaleBits]      	Y scale value     
           	HasRotate      	hasRotate = UB[1]      	         Has rotate and skew values if equal to 1     
           	NRotateBits      	If hasRotate nRotateBits = UB[5] Bits in each rotate value field     
           	RotateSkew0      	If hasRotate FB[nRotateBits]     First rotate and skew value     
           	RotateSkew1      	If hasRotate FB[nRotateBits]     Second rotate and skew value     
           	NTranslateBits   	nTranslateBits = UB[5]      	   Bits in each translate value field     
           	TranslateX     	SB[nTranslateBits]      	      X translate value in twips     
           	TranslateY      	SB[nTranslateBits]      	      Y translate value in twips  
      	*/

        FMatrix myMatrix = new FMatrix();
        bi.align();

        int hasScale = (int) bl.ub(1);

        if (hasScale == 1) // hasScale
        {
            int nScaleBits = (int) bl.ub(5);

            //int scaleBits = (int)in.readUBits(5);
            //scaleX = ((double)in.readSBits( scaleBits ))/65536.0;
            //scaleY = ((double)in.readSBits( scaleBits ))/65536.0;

            //myMatrix.setScaleX( sb( nScaleBits )/65536.0f );
            //myMatrix.setScaleY( sb( nScaleBits )/65536.0f );

            myMatrix.setScaleX(bl.fb(nScaleBits));
            myMatrix.setScaleY(bl.fb(nScaleBits));
        }

        int hasRotate = (int) bl.ub(1);

        if (hasRotate == 1) // hasRotate
        {
            int nRotateBits = (int) bl.ub(5);

            //int skewBits  = (int)in.readUBits(5);
            //skew0 = ((double)in.readSBits( skewBits ))/65536.0;
            //skew1 = ((double)in.readSBits( skewBits ))/65536.0;

            myMatrix.setRotateSkew0(bl.fb(nRotateBits));
            myMatrix.setRotateSkew1(bl.fb(nRotateBits));

            //myMatrix.setRotateSkew0( fb( nRotateBits ) );
            //myMatrix.setRotateSkew1( fb( nRotateBits ) );
        }

        int nTranslateBits = (int) bl.ub(5);
        myMatrix.setTranslateX(bl.sb(nTranslateBits));
        myMatrix.setTranslateY(bl.sb(nTranslateBits));

        bi.align();

        return myMatrix;
    }

    public CXFORMALPHA cxform(boolean alpha) throws IOException {
        bi.align();
        boolean add = false;
        boolean mul = false;
        add = (bl.ub(1) == 1); // hasAdd
        mul = (bl.ub(1) == 1); // hasAdd
        CXFORMALPHA myCXFORMALPHA = new CXFORMALPHA(add, mul, alpha);
        int Nbits = (int) bl.ub(4);

        if (mul) {
            for (int x = 0; x < (alpha ? 4 : 3); x++) {
                myCXFORMALPHA.setMulti(x, (int) bl.sb(Nbits));
            }
        }

        if (add) {
            for (int x = 0; x < (alpha ? 4 : 3); x++) {
                myCXFORMALPHA.setAdd(x + 4, (int) bl.sb(Nbits));
            }
        }
        return myCXFORMALPHA;
    }

    // Morph

    // FillStyle
    public FFill morphFillStyle() throws IOException {
      	/*
      		type = UI8
      		0x00 = solid fill
      		0x10 = linear gradient fill
      		0x12 = radial gradient fill
      		0x40 = tiled bitmap fill
      		0x41 = clipped bitmap fill
      		
      		Color        	     If type = 0x00 RGBA (if Shape3);
      								  RGB (if Shape1 or Shape2)      	     Solid fill color with transparency information     
      	   GradientMatrix      If type = 0x10 or 0x12 MATRIX      	  Matrix for gradient fill     
      	   Gradient      	     If type = 0x10 or 0x12 GRADIENT        Gradient fill     
      	   BitmapID      	     If type = 0x40 or 0x41 UI16      	     ID of bitmap character for fill     
      	   BitmapMatrix        if type = 0x40 or 0x41 MATRIX      	     Matrix for bitmap fill  
      	*/

        int type = (int) bl.ui8();
        switch (type) {
            case 0x00: // solid fill
                return new FMorphSolid(rgba(3), rgba(3));
            case 0x10: // linear gradient fill
                return new FMorphGradient(FFill.LGRADIENT, matrix(), matrix(), morphGradient());
            case 0x12: // radial gradient fill
                return new FMorphGradient(FFill.RGRADIENT, matrix(), matrix(), morphGradient());
            //case 0x40: // tiled bitmap fill
            //return new FTexture( FFill.TBITMAP, (int)bl.ui16(), matrix() ) ;
            case 0x41: // clipped bitmap fill
                return new FMorphTexture((int) bl.ui16(), matrix(), matrix());
        }
        return null;
    }

    // Line Style
    public FMorphLine morphLineStyle() throws IOException {
      	/*
      	   Width        	  UI16         	                      Width of line in twips      
      		Color      	     RGB (Shape1 or Shape2); RGBA (Shape3) Color value including alpha channel information for Shape3s  
      	*/
        return new FMorphLine(bl.ui16(), bl.ui16(), rgba(3), rgba(3));
    }

    public Vector morphGradient() throws IOException {
      	/*
      		NumGradients      nGrads = UI8         	      1 to 8      
      		GradientRecords   GRADRECORD [nGradients]      	     Gradient records - see below  
      	*/
        int count = (int) bl.ui8();

        Vector myVector = new Vector(count);

        for (int x = 0; x < count; x++) {
            myVector.add(morphGradRecord());
        }

        return myVector;
    }

    // GradRecord
    public FMorphGradientRecord morphGradRecord() throws IOException {
        return new FMorphGradientRecord((int) bl.ui8(), rgba(3), (int) bl.ui8(), rgba(3));
    }
}