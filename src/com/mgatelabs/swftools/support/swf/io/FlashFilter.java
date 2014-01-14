/*
    Copyright M-Gate Labs 2007
*/

/**
 <p>
 <b>Flash Filter</b>
 </p>
 <p>
 Used to determine what to skip while parsing a flash file.  <b>Ex</b>. Disable sounds, bitmaps, fonts shapes.
 </p>
 */

package com.mgatelabs.swftools.support.swf.io;

public class FlashFilter {

    public static final int SHAPE = 0;
    public static final int SOUND = 1;
    public static final int FONT = 2;
    public static final int BITMAP = 3;
    public static final int BITMAP_DISCARD = 4;

    public static final int MAX_FILTERS = 5;

    private boolean filters[];

    // Default all Filters
    public FlashFilter() {
        filters = new boolean[MAX_FILTERS];

        for (int x = 0; x < filters.length; x++) {
            filters[x] = true;
        }
    }

    // Get a Filter
    public boolean getFilter(int index) {
        if (index >= 0 && index < filters.length) {
            return filters[index];
        } else {
            return false;
        }
    }

    // Set a Filter
    public void setFilter(int index, boolean value) {
        if (index >= 0 && index < filters.length) {
            filters[index] = value;
        }
    }

}