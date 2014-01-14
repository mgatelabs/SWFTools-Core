/*
    Copyright M-Gate Labs 2007
    Can be edited with permission only.
*/
   
   /*
       Render Information
   */

package com.mgatelabs.swftools.support.swf.objects;

public class FDisplayListItem {
    // My Object
    private Object myObject;

    // My ID
    private int myID;

    // My Ratio
    private int myRatio;

    // My Name
    private String myName;

    // My Matrix
    private FMatrix myMatrix;

    // My Stuff
    private CXFORMALPHA myCXFORMALPHA;

    // Am I a Clip Layer
    private boolean clipLayer;

    // How far do I clip
    private int clipDepth;

    // Am i Visible?
    private boolean visible;

    // When do I come from?
    private int insertedAtFrame;
    // Constructor

    public FDisplayListItem(Object obj, int id, String name, int frame) {
        insertedAtFrame = frame;
        myObject = obj;
        myID = id;
        myName = name;
        myMatrix = null;
        myCXFORMALPHA = null;
        clipLayer = false;
        clipDepth = 0;
        visible = true;
    }

    public int getInsertFrame() {
        return insertedAtFrame;
    }

    public void toggleVisible() {
        visible = !visible;
    }

    public boolean isVisible() {
        return visible;
    }

    public CXFORMALPHA getCXFORMALPHA() {
        return myCXFORMALPHA;
    }

    public void setCXFORMALPHA(CXFORMALPHA aCXFORMALPHA) {
        myCXFORMALPHA = aCXFORMALPHA;
    }

    public void setClipDepth(int dep) {
        clipDepth = dep;
    }

    public int getClipDepth() {
        return clipDepth;
    }

    public void setClipLayer(boolean clip) {
        clipLayer = clip;
    }

    public boolean getClipLayer() {
        return clipLayer;
    }

    public void setObject(Object ob) {
        myObject = ob;
    }

    public Object getObject() {
        return myObject;
    }

    public FMatrix getMatrix() {
        return myMatrix;
    }

    public void setMatrix(FMatrix aMatrix) {
        myMatrix = aMatrix;
    }

    public String getName() {
        return myName;
    }

    public int getID() {
        return myID;
    }

    public void setRatio(int val) {
        myRatio = val;
    }

    public int getRatio() {
        return myRatio;
    }

    public String getQuickInfo() {
        String result = "";

        if (!visible) {
            result += ", Hidden";
        }

        if (clipLayer) {
            result += ", Clip to " + (clipDepth + 1);
        }

        return result;
    }
}