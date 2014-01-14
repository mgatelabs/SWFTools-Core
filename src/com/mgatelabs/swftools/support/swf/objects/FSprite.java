/*
    Copyright M-Gate Labs 2007
    Can be edited with permission only.
*/

package com.mgatelabs.swftools.support.swf.objects;

import com.mgatelabs.swftools.support.swf.tags.Tag;

import java.util.Vector;

public class FSprite implements FMovieInterface {
    //Header        	     RECORDHEADER      Tag ID = 39
    //Sprite ID      	     UI16      	     ID of sprite
    //FrameCount      	     UI16      	     Number of frames in Sprite.
    //ControlTags      	  TAG[one or more]  A series of tags

    private FMovie myMaster;

    private int myID;
    private long myFrameCount;
    private Vector myTags;

    public FSprite(int id, long framecount, FMovie master) {
        // Default Everything
        myMaster = master;
        myID = id;
        myFrameCount = framecount;
        myTags = new Vector();
    }

    // Get the Master Movie

    public FMovie getCore() {
        // Each Sprite knows the master clip
        // Should be a global parameter, but not going to change it yet
        return myMaster;
    }

    // Get My ID

    public int getID() {
        return myID;
    }

    // Get Frame Count

    public long getFrameCount() {
        return myFrameCount;
    }

    // to String Method

    public String toString() {
        return "Sprite: ID:" + myID;
    }

    // Get my Tags

    public Vector getTags() {
        return myTags;
    }

    // Add a Tag

    public void addTag(Tag aTag) {
        myTags.add(aTag);
    }
}