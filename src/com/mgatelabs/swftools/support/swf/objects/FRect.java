/*
    Copyright M-Gate Labs 2007
    Can be edited with permission only.
*/

package com.mgatelabs.swftools.support.swf.objects;

// This class is a basic flash rectangles, could be replaced with the built in rect class.

public class FRect {
    // Variables

    public long xmin;
    public long xmax;
    public long ymin;
    public long ymax;

    // Constructor

    public FRect(long xmin, long xmax, long ymin, long ymax) {
        this.xmin = xmin;
        this.xmax = xmax;
        this.ymin = ymin;
        this.ymax = ymax;
    }

    public long getLeft() {
        return xmin;
    }

    public long getTop() {
        return ymin;
    }

    // Get Width

    public long getWidth() {
        return xmax - xmin;
    }

    // Get Height

    public long getHeight() {
        return ymax - ymin;
    }

    // To String Method

    public String toString() {
        return "Rect: X Min: " + xmin + ":" + "X Max: " + xmax + ": Y Min: " + ymin + ":" + "Y Max: " + ymax;
    }
}