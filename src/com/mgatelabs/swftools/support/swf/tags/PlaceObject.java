/*
    Copyright M-Gate Labs 2007
*/

/**
 <p>
 <b>Place Object Tag</b>
 </p>
 <p>
 Used to place objects on the display graph.
 </p>
 */


package com.mgatelabs.swftools.support.swf.tags;

import com.mgatelabs.swftools.support.swf.objects.CXFORMALPHA;
import com.mgatelabs.swftools.support.swf.objects.FMatrix;

public class PlaceObject {
       /*
         Header								RECORDHEADER											Tag ID = 26      
   		PlaceFlagHasClipActions			UB[1]														Has Clip Actions (added to Flash 5)     
   		PlaceFlagReserved					UB[1]														Reserved Flags     
   		PlaceFlagHasName					UB[1]														Has name     
   		PlaceFlagHasRatio					UB[1]														Has ratio     
   		PlaceFlagHasColorTransform		UB[1]														Has color transform     
   		PlaceFlagHasMatrix				UB[1]														Has matrix     
   		PlaceFlagHasCharacter			UB[1]														Places a character     
   		PlaceFlagMove						UB[1]														Defines a character to be moved     
   		Depth									UI16														Depth of character     
   		CharacterId							If PlaceFlagHasCharacter = 1 UI16				ID of character to place     
   		Matrix								If PlaceFlagHasMatrix = 1 MATRIX					Transform matrix data     
   		ColorTransform						If PlaceFlagHasColorTransform = 1 CXFORM		Color transform data     
   		Ratio									If PlaceFlagHasRatio = 1 UI16      	         
   		Name									If PlaceFlagHasName = 1 STRING					Name of character     
   		ClipActions							If PlaceFlagHasClipActions = 1 ClipActions	Clip Actions Data   
      */

    private boolean PlaceFlagHasClipActions;
    private boolean PlaceFlagReserved;
    private boolean PlaceFlagHasName;
    private boolean PlaceFlagHasRatio;
    private boolean PlaceFlagHasColorTransform;
    private boolean PlaceFlagHasMatrix;
    private boolean PlaceFlagHasCharacter;
    private boolean PlaceFlagMove;

    private int myDepth;
    private int myClipDepth;
    private int myID;
    private int myRatio;
    private FMatrix myMatrix;
    private String myName;
    private CXFORMALPHA myCXFORMALPHA;

    // Constructor

    public PlaceObject() {
        PlaceFlagHasClipActions = false;
        PlaceFlagReserved = false;
        PlaceFlagHasName = false;
        PlaceFlagHasRatio = false;
        PlaceFlagHasColorTransform = false;
        PlaceFlagHasMatrix = false;
        PlaceFlagHasCharacter = false;
        PlaceFlagMove = false;

        myDepth = -1;
        myClipDepth = -1;
        myID = 0 - 1;
        myRatio = 0;
        myMatrix = null;
        myName = null;
        myCXFORMALPHA = null;
    }

    // Set Flags

    public void setPlaceFlagHasClipActions() {
        //System.out.println("Clip");
        PlaceFlagHasClipActions = true;
    }

    public void setPlaceFlagReserved() {
        PlaceFlagReserved = true;
    }

    public void setPlaceFlagHasName() {
        PlaceFlagHasName = true;
    }

    public void setPlaceFlagHasRatio() {
        PlaceFlagHasRatio = true;
    }

    public void setPlaceFlagHasColorTransform() {
        PlaceFlagHasColorTransform = true;
    }

    public void setPlaceFlagHasMatrix() {
        PlaceFlagHasMatrix = true;
    }

    public void setPlaceFlagHasCharacter() {
        PlaceFlagHasCharacter = true;
    }

    public void setPlaceFlagMove() {
        PlaceFlagMove = true;
    }

    // Get Flags

    public boolean getPlaceFlagHasClipActions() {
        return PlaceFlagHasClipActions;
    }

    public boolean getPlaceFlagReserved() {
        return PlaceFlagReserved;
    }

    public boolean getPlaceFlagHasName() {
        return PlaceFlagHasName;
    }

    public boolean getPlaceFlagHasRatio() {
        return PlaceFlagHasRatio;
    }

    public boolean getPlaceFlagHasColorTransform() {
        return PlaceFlagHasColorTransform;
    }

    public boolean getPlaceFlagHasMatrix() {
        return PlaceFlagHasMatrix;
    }

    public boolean getPlaceFlagHasCharacter() {
        return PlaceFlagHasCharacter;
    }

    public boolean getPlaceFlagMove() {
        return PlaceFlagMove;
    }

    // Set Vars

    public void setDepth(int aDepth) {
        myDepth = aDepth;
    }

    public void setClipDepth(int aDepth) {
        myClipDepth = aDepth;
    }

    public void setID(int aID) {
        myID = aID;
    }

    public void setRatio(int aRatio) {
        myRatio = aRatio;
    }

    public void setMatrix(FMatrix aMatrix) {
        myMatrix = aMatrix;
    }

    public void setName(String aName) {
        myName = aName;
    }

    public void setCXFORM(CXFORMALPHA aCXFORMALPHA) {
        myCXFORMALPHA = aCXFORMALPHA;
    }

    // Get Vars

    public int getDepth() {
        return myDepth;
    }

    public int getClipDepth() {
        return myClipDepth;
    }

    public int getID() {
        return myID;
    }

    public int getRatio() {
        return myRatio;
    }

    public FMatrix getMatrix() {
        return myMatrix;
    }

    public String getName() {
        return myName;
    }

    public CXFORMALPHA getCXFORM() {
        return myCXFORMALPHA;
    }
}