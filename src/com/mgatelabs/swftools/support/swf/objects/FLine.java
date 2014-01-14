/*
    Copyright M-Gate Labs 2007
    Can be edited with permission only.
*/

package com.mgatelabs.swftools.support.swf.objects;

import java.awt.*;

public class FLine {
    // Line Information for a Shape

    // My Varables

    private FColor myColor;
    private long myWidth;
    private BasicStroke myStroke;

    // Version 8

    private int f_start_cap_style;// : 2;
    private int f_join_style;// : 2;
    private boolean f_has_fill;// : 1;
    private boolean f_no_hscale;// : 1;
    private boolean f_no_vscale;// : 1;
    private boolean f_pixel_hinting;// : 1;
    //unsigned f_reserved : 5;
    private boolean f_no_close;// : 1;
    private int f_end_cap_style;// : 2;
    private float f_miter_limit_factor;
    private FFill myFill; // Fill on a Line?
    // Constructor


    public FLine() {
        myColor = null;
        myWidth = 0;
        myStroke = null;
    }

    public FLine morph(float percentage) {
        return null;
    }
      
      /*
          In flash 1 pixel is equal to 20 flash units.
      	So to keep everything correct 20 is applied to the math is a few parts.
      */

    public FLine(long aWidth, FColor aColor) {
        myColor = aColor;
        myWidth = aWidth;
        myStroke = new BasicStroke(myWidth / 20.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
    }

    public FLine(long aWidth, int startStyle, int aJoinStyle, int endStyle, boolean noClose, boolean noHScale, boolean noVScale, boolean pixelHinting, float miterLimit, FFill aFill, FColor aColor) {
        myColor = aColor;
        myWidth = aWidth;

        f_start_cap_style = startStyle;
        f_join_style = aJoinStyle;
        f_has_fill = (myFill != null);
        f_no_hscale = noHScale;
        f_no_vscale = noVScale;
        f_pixel_hinting = pixelHinting;
        f_no_close = noClose;
        f_end_cap_style = endStyle;
        f_miter_limit_factor = miterLimit;
        myFill = aFill; // Fill on a Line?

        int capStyle = 0;
        int joinStyle = 0;

        switch (f_start_cap_style) {
            case 0: // Round cap
                capStyle = BasicStroke.CAP_ROUND;
                break;
            case 1: // No cap
                capStyle = BasicStroke.CAP_BUTT;
                break;
            case 2: // Square cap
                capStyle = BasicStroke.CAP_SQUARE;
                break;
        }

        switch (f_join_style) {
            case 0: // Round cap
                joinStyle = BasicStroke.JOIN_ROUND;
                break;
            case 1: // Bevel cap
                joinStyle = BasicStroke.JOIN_BEVEL;
                break;
            case 2: // Miter cap
                joinStyle = BasicStroke.JOIN_MITER;
                break;
        }

      	
      	/*
          The f_start_cap_style and f_end_cap_style can be:
      		* 0 - Round cap,
      		* 1 - No cap,
      		* 2 - Square cap.
      	The f_join_style can be:
      		* 0 - Round join,
      		* 1 - Bevel join,
      		* 2 - Miter join.
      	Java Styles
      		CAP_BUTT
      		CAP_ROUND
      		CAP_SQUARE
      		JOIN_BEVEL
      		JOIN_MITER
      		JOIN_ROUND
      	*/
        if (joinStyle != 2) {
            myStroke = new BasicStroke(myWidth / 20.0f, capStyle, joinStyle);
        } else {
            myStroke = new BasicStroke(myWidth / 20.0f, capStyle, joinStyle, f_miter_limit_factor);
        }
    }

    public FLine(float aWidth, FColor aColor) {
        myColor = aColor;
        myWidth = (long) (aWidth * 20);
        myStroke = new BasicStroke(aWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
    }

    // Get My Color

    public FColor getColor() {
        return myColor;
    }

    public BasicStroke getStroke() {
        return myStroke;
    }

    // Get My Width

    public long getWidth() {
        return myWidth;
    }

    // to String Method

    public String toString() {
        return "Width " + myWidth + " : " + myColor;
    }

    // Flash 8
    public void setStartCapStyle(int value) {
        f_start_cap_style = value;
    }

    public int getStartCapStyle() {
        return f_start_cap_style;
    }

    public void setHasFill(boolean value) {
        f_has_fill = value;
    }

    public boolean getHasFill() {
        return f_has_fill;
    }

    public void setNoHScale(boolean value) {
        f_no_hscale = value;
    }

    public boolean getNoHScale() {
        return f_no_hscale;
    }

    public void setNoVScale(boolean value) {
        f_no_vscale = value;
    }

    public boolean getNoVScale() {
        return f_no_vscale;
    }

    public void setPixelHinting(boolean value) {
        f_pixel_hinting = value;
    }

    public boolean getPixelHinting() {
        return f_pixel_hinting;
    }

    public void setNoClose(boolean value) {
        f_no_close = value;
    }

    public boolean getNoClose() {
        return f_no_close;
    }

    public void setEndCapStyle(int value) {
        f_end_cap_style = value;
    }

    public int getEndCapStyle() {
        return f_end_cap_style;
    }

    public void setMiterLimit(float value) {
        f_miter_limit_factor = value;
    }

    public float getMiterLimit() {
        return f_miter_limit_factor;
    }

    public void setFill(FFill value) {
        myFill = value;
    }

    public FFill getFill() {
        return myFill;
    }
   	
   	/*
   	int f_join_style : 2;
   	boolean f_has_fill : 1;
   	boolean f_no_hscale : 1;
   	boolean f_no_vscale : 1;
   	boolean f_pixel_hinting : 1;
   	//unsigned f_reserved : 5;
   	boolean f_no_close : 1;
   	int f_end_cap_style : 2;
   	*/
}