package com.mgatelabs.swftools.support.swf.objects;

import com.mgatelabs.swftools.support.swf.tags.Tag;

import java.awt.*;
import java.util.Hashtable;
import java.util.Vector;

public class FMovie implements FMovieInterface {
    // The Flash Object Must Contain Information

    Hashtable myObjects;
    Vector myObjectVector;

    Vector myTags;

    private String mySignature;
    private long myVersion;
    private long myFileLength;
    private FRect myRect;
    private float myFrameRate;
    private long myFrameCount;
    private Color myBackgroundColor;

    private boolean compressed;

    private boolean ready;

    // Create A Flash Object

    public FMovie(String aSignature, long version, long fileLength, FRect aRect, float frameRate, long frameCount) {
        myObjects = new Hashtable();

        myObjectVector = new Vector();
        myTags = new Vector();

        // Setup Variables
        this.mySignature = aSignature;
        this.myVersion = version;
        this.myFileLength = fileLength;
        this.myRect = aRect;
        this.myFrameRate = frameRate;
        this.myFrameCount = frameCount;
        this.myBackgroundColor = Color.WHITE;
        this.ready = false;
        compressed = false;
    }

    public Color getBackgroundColor() {
        return myBackgroundColor;
    }

    public void setBackgroundColor(Color aColor) {
        myBackgroundColor = aColor;
    }

    public void clean() {
        myObjects = null;
        myObjectVector = null;
        myTags = null;
    }

    public boolean ready() {
        return ready;
    }

    public void setReady(boolean val) {
        ready = val;
    }

    public FMovie getCore() {
        return this;
    }

    // File Information

    public int getID() {
        return -1;
    }

    // Get basic file information

    public long getFileLength() {
        return myFileLength;
    }

    // Get movie information

    public long getFrameCount() {
        return myFrameCount;
    }

    // Get movie information

    public float getFrameRate() {
        return myFrameRate;
    }

    public void setFrameRate(float rate) {
        myFrameRate = rate;
    }

    // Get movie information

    public FRect getRect() {
        return myRect;
    }

    // Get movie information

    public int getVersion() {
        return (int) myVersion;
    }

    public void setVersion(int version) {
        myVersion = version;
    }

    // Objects

    public void add(int index, Object obj) {
        myObjects.put("" + index, obj);

        myObjectVector.add(obj);
    }

    public Hashtable getObjects() {
        return myObjects;
    }

    public Vector getObjectVector() {
        return myObjectVector;
    }

    // Tags

    public void addTag(Tag tag) {
        myTags.add(tag);
    }

    public Vector getTags() {
        return myTags;
    }

    // Search for a Object

    public Object findObject(int id) {
        Object result = null;
        result = myObjects.get("" + id);
        if (result == null) {
            result = myObjects.get("" + id);
        }
        return result;
    }

    // to String Method

    public String toString() {
        String result = "Flash Object Info\n";
        result += mySignature + "\n";
        result += "Version    : " + myVersion + "\n";
        result += "Length     : " + myFileLength + "\n";
        result += myRect + "\n";
        result += "Frame Rate : " + myFrameRate + "\n";
        result += "Frame Count: " + myFrameCount;
        return result;
    }

}