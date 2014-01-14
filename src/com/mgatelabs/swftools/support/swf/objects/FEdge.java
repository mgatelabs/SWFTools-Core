/*
    Copyright M-Gate Labs 2007
    Can be edited with permission only.
*/

/**
 A Sigle Line/Edge/Curve
 */

package com.mgatelabs.swftools.support.swf.objects;

public class FEdge {
    // Possible Styles
    public static final int CURVED = 0;
    public static final int STRAIGHT = 1;

    // My Style
    private int myStyle;

    // Straight Edge
    private long myDeltaX;
    private long myDeltaY;

    // Curved Edge
    private long myControlDeltaX;
    private long myControlDeltaY;
    private long myAnchorDeltaX;
    private long myAnchorDeltaY;

    private int myID;

   	/*
       Straight Edge Record
   	NumBits						NBits = UB[4] + 2							Number of bits per value      
   	GeneralLineFlag			LineFlag = UB[1]							General Line equals 1, Vert/Horz Line equals 0     
   	DeltaX						If lineFlag = 1 SB[nBits]				X delta     
   	DeltaY						If lineFlag = 1 SB[nBits]				Y delta     
   	VertLineFlag				If lineFlag = 0 vertFlag = SB[1]		Vertical Line equals 1, Horizontal Line equals 0     
   	DeltaX						If vertFlag = 0 SB[nBits]				X delta     
   	DeltaY						If vertFlag = 1 SB[nBits]				Y delta
   	
   	Curved Edge Record
   	NumBits						NBits = UB[4] + 2							Number of bits per value      
   	ControlDeltaX				SB[nBits]									X control point change     
   	ControlDeltaY				SB[nBits]									Y control point change     
   	AnchorDeltaX				SB[nBits]									X anchor point change     
   	AnchorDeltaY				SB[nBits]									Y anchor point change 
   	*/

    // Constructor
    public FEdge(int style) {
        myID = -1;
        // Set my Style
        this.myStyle = style;

        // Setup Straight Edge
        myDeltaX = 0;
        myDeltaY = 0;

        // Setup Curved Edge
        myControlDeltaX = 0;
        myControlDeltaY = 0;
        myAnchorDeltaX = 0;
        myAnchorDeltaY = 0;
    }

    public FEdge(int style, int id) {
        myID = id;
        // Set my Style
        this.myStyle = style;

        // Setup Straight Edge
        myDeltaX = 0;
        myDeltaY = 0;

        // Setup Curved Edge
        myControlDeltaX = 0;
        myControlDeltaY = 0;
        myAnchorDeltaX = 0;
        myAnchorDeltaY = 0;
    }

    public int getID() {
        return myID;
    }

    // Get My Style
    public int getStyle() {
        return myStyle;
    }

    // Straight Functions
    public void setDeltaX(long aValue) {
        myDeltaX = aValue;
    }

    public void setDeltaY(long aValue) {
        myDeltaY = aValue;
    }

    public long getDeltaX() {
        return myDeltaX;
    }

    public long getDeltaY() {
        return myDeltaY;
    }

    // Curved Functions

    //Control
    public void setControlDeltaX(long aValue) {
        myControlDeltaX = aValue;
    }

    public void setControlDeltaY(long aValue) {
        myControlDeltaY = aValue;
    }

    public long getControlDeltaX() {
        return myControlDeltaX;
    }

    public long getControlDeltaY() {
        return myControlDeltaY;
    }

    // Anchor
    public void setAnchorDeltaX(long aValue) {
        myAnchorDeltaX = aValue;
    }

    public void setAnchorDeltaY(long aValue) {
        myAnchorDeltaY = aValue;
    }

    public long getAnchorDeltaX() {
        return myAnchorDeltaX;
    }

    public long getAnchorDeltaY() {
        return myAnchorDeltaY;
    }
}