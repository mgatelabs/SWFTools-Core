/*
    Copyright M-Gate Labs 2007
    Can be edited with permission only.
*/

package com.mgatelabs.swftools.support.swf.objects;

import com.mgatelabs.swftools.support.swf.tags.Tag;

import java.util.Vector;

// Every Movie Object is Based off this Interface

public interface FMovieInterface {
    // Get the Origonal Clip, that contains all of the objects
    public FMovie getCore();

    // General
    public int getID();

    public long getFrameCount();

    public String toString();

    // Animation Tags
    public Vector getTags();

    public void addTag(Tag tag);
}