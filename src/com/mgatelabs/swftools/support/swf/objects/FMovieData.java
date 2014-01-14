/*
    Copyright M-Gate Labs 2007
    Can be edited with permission only.
*/

package com.mgatelabs.swftools.support.swf.objects;

// Import in the frame parser

import com.mgatelabs.swftools.support.swf.tools.FrameParser;

import java.awt.*;
import java.util.Vector;

// Need color information
// Need vector information

//import flash.tools.*;

public class FMovieData {
    // Holds information abou the current frame for a movie object.

    // Variables
    private int myFrame;
    private int myInstruction;
    private FMovieInterface myMovie;
    private Vector myDisplayList;
    private Color backgroundColor;

    // Constructor

    public FMovieData(FMovieInterface aMovie) {
        myMovie = aMovie;
        myFrame = 0;
        myInstruction = 0;
        myDisplayList = new Vector(10);
        backgroundColor = null;
    }

    // Get the movies background color

    public Color getBackgroundColor() {
        return backgroundColor;
    }

    // Set the background color

    public void setBackgroundColor(Color aColor) {
        backgroundColor = aColor;
    }

    // Get my movie

    public FMovieInterface getMovie() {
        return myMovie;
    }

    // Get the display list

    public Vector getDisplayList() {
        return myDisplayList;
    }

    // Frame Stuff

    // Get the current flame number

    public int getFrame() {
        return myFrame;
    }

    // Get frame count

    public long getFrameCount() {
        return myMovie.getFrameCount();
    }

    // Move to the next frame

    public void nextFrame() {
        if (myFrame == myMovie.getFrameCount()) {
            resetFrame();
        }
        myInstruction = FrameParser.nextFrame(this, myInstruction, myMovie.getCore());
        myFrame++;
    }

    // Reset to start frame

    public void resetFrame() {
        // Reset Count
        //myDisplayList.clear();

        for (int x = 0; x < myDisplayList.size(); x++) {
            //System.out.println("Checking " + x);
            FDisplayListItem dli = (FDisplayListItem) myDisplayList.get(x);
            if (dli != null && dli.getInsertFrame() != 0) {
                //System.out.println(dli.getInsertFrame());
                myDisplayList.setElementAt(null, x);
            }
        }

        myFrame = 0;
        myInstruction = 0;
    }
}