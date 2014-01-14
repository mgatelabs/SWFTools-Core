/*
    Copyright M-Gate Labs 2007
*/

/**
 <p>
 <b>Frame Parser</b>
 </p>
 <p>
 Used to advance a movie object's frame.
 </p>
 */


package com.mgatelabs.swftools.support.swf.tools;

// Get flash Objects

import com.mgatelabs.swftools.support.swf.objects.*;
import com.mgatelabs.swftools.support.swf.tags.PlaceObject;
import com.mgatelabs.swftools.support.swf.tags.RemoveObject;
import com.mgatelabs.swftools.support.swf.tags.Tag;

import java.util.Vector;

// Get flash Tags
// Need Vector support

// The Frame Parser

public class FrameParser {

    public static int nextFrame(FMovieData movieData, int tagLocation, FMovie core) {
        // Get DisplayList
        Vector displayList = movieData.getDisplayList();
        FMovieInterface movie = movieData.getMovie();
        Vector tags = movie.getTags();

        //System.out.println("Tag Location: " + tagLocation);

        if (tags.size() == 0) {
            System.out.println("No Tags");
            return 0;
        }

        boolean keepGoing = true;
        int endCount = 0;
        int targetDepth = 0;

        while (keepGoing) {
            if (tagLocation == tags.size()) {
                // Need to Forcefully Stop
                tagLocation = 0;
                return 0;
            }

            Tag tag = (Tag) tags.get(tagLocation);
            //System.out.println("Tag " + tag.getID());
            //System.out.println(tag);
            tagLocation++;

            if (tag.getID() == 0) // Stop // Then we gotta loop
            {
                if (endCount == 1) {
                    tagLocation = 0;     // Reset the Count;
                    //displayList.clear(); // Clear the display, time to start over again
                    return 0;
                } else {
                    endCount++;
                    tagLocation = 0;     // Reset the Count;

                    // for (int x=0;x<displayList.size();x++)
                    // {
                    // System.out.println("Checking " + x);
                    // FDisplayListItem dli = (FDisplayListItem)displayList.get(x);
                    // if (dli != null && dli.getInsertFrame() != 0)
                    // {
                    // System.out.println(dli.getInsertFrame());
                    // displayList.setElementAt(null, x);
                    // }
                    // }
                    displayList.clear(); // Clear the display, time to start over again
                }
            } else if (tag.getID() == 1) // Show Frame
            {
                // Update Frames and Such
                // Go through frames and make movies move up a frame
                for (int x = 0; x < displayList.size(); x++) {
                    FDisplayListItem di = (FDisplayListItem) displayList.get(x);
                    if (di == null) {

                    } else if (di.getObject() instanceof FMovieData) {
                        ((FMovieData) di.getObject()).nextFrame();
                    }
                }

                keepGoing = false;
            } else if (tag.getID() == 9) // Set Background Color
            {
                movieData.setBackgroundColor(((FColor) tag.getObject()).toColor());
            } else if (tag.getID() == 70 || tag.getID() == 26 || tag.getID() == 4) // Place Object 2
            {
                // Going to mess with the display list;
                PlaceObject po2 = (PlaceObject) tag.getObject();
                FDisplayListItem aItem = null;

                targetDepth = po2.getDepth() - 1;

                if (!po2.getPlaceFlagMove() && po2.getPlaceFlagHasCharacter()) {//PlaceFlagMove = 0 and PlaceFlagHasCharacter = 1
                    //A new character (with ID of CharacterId) is placed on the display list at the
                    //specified Depth. Other fields set the attributes of this new character.
                    //System.out.println("Add Char " + po2.getID() + " Depth " + targetDepth );

                    String name = null;
                    Object t = core.findObject(po2.getID());
                    if (t != null) {
                        if (t instanceof FMovieInterface) {
                            t = (Object) new FMovieData((FMovieInterface) t);
                        }

                        if (targetDepth < displayList.size() && displayList.get(targetDepth) != null && ((FDisplayListItem) displayList.get(targetDepth)).getInsertFrame() == movieData.getFrame()) {
                            aItem = (FDisplayListItem) displayList.get(targetDepth);
                        } else {
                            aItem = new FDisplayListItem(t, po2.getID(), name, movieData.getFrame());
                            insertDataIntoVector(targetDepth, displayList, aItem);
                        }
                    } else {
                        //System.out.println("Object Not Found");
                        continue;
                    }
                } else if (po2.getPlaceFlagMove() && !po2.getPlaceFlagHasCharacter()) {    //PlaceFlagMove = 1 and PlaceFlagHasCharacter = 0
                    //The character at the specified Depth is modified. Other fields modify the
                    //attributes of this character. Because there can be only one character at any
                    //given depth, no CharacterId is required.
                    //System.out.println("Edit Depth" + targetDepth );

                    if (targetDepth >= displayList.size()) {
                        //System.out.println("Missing Item");
                        continue;
                    }

                    aItem = (FDisplayListItem) displayList.get(targetDepth);

                    if (aItem == null) {
                        //System.out.println("Tryign to modify null item");
                        continue;
                    }

                } else if (po2.getPlaceFlagMove() && po2.getPlaceFlagHasCharacter()) {
                    //PlaceFlagMove = 1 and PlaceFlagHasCharacter = 1
                    //The character at the specified Depth is removed, and a new character (with ID
                    //of CharacterId) is placed at that depth. Other fields set the attributes of this new character.
                    //System.out.println("Exchange Char " + po2.getID() + " Depth " + targetDepth );

                    String name = null;

                    Object t = core.findObject(po2.getID());

                    if (t instanceof FMovieInterface) {
                        t = (Object) new FMovieData((FMovieInterface) t);
                    }

                    if (targetDepth < displayList.size()) {
                        aItem = (FDisplayListItem) displayList.get(targetDepth);
                        if (aItem == null) {
                            aItem = new FDisplayListItem(t, po2.getID(), name, movieData.getFrame());
                            insertDataIntoVector(targetDepth, displayList, aItem);
                        } else {
                            aItem.setObject(t);
                        }
                    } else {
                        aItem = new FDisplayListItem(t, po2.getID(), name, movieData.getFrame());
                        insertDataIntoVector(targetDepth, displayList, aItem);
                    }
                }

                if (aItem != null) {
                    if (po2.getPlaceFlagHasMatrix()) {
                        //System.out.println("Setting Matrix");
                        aItem.setMatrix(po2.getMatrix());
                    }

                    if (po2.getPlaceFlagHasClipActions()) {
                        aItem.setClipLayer(true);
                        aItem.setClipDepth(po2.getClipDepth() - 1);
                    } else {
                        //aItem.setClipLayer(false);
                    }

                    if (po2.getPlaceFlagHasRatio()) {
                        aItem.setRatio(po2.getRatio());
                    }

                    if (po2.getPlaceFlagHasColorTransform()) {
                        aItem.setCXFORMALPHA(po2.getCXFORM());
                    }
                }
            } else if (tag.getID() == 28) // Remove Object 2
            {
                RemoveObject ro = (RemoveObject) tag.getObject();
                targetDepth = ro.getDepth() - 1;
                //System.out.println("Remove Depth " + (ro.getDepth() -1));
                if (targetDepth < displayList.size()) {
                    displayList.setElementAt(null, ro.getDepth() - 1);
                }
            }


        }

        return tagLocation;
    }

    // Used to get around errors involving the display list
    // Objects may be missing from the tag library so they are missing in the movie
    public static void insertDataIntoVector(int depth, Vector displayList, FDisplayListItem aItem) {
        if (depth == displayList.size()) {
            displayList.insertElementAt(aItem, depth);
        } else if (depth > displayList.size()) {
            while (depth > displayList.size()) {
                displayList.add(null);
            }
            displayList.insertElementAt(aItem, depth);
        } else if (depth < displayList.size()) {
            displayList.setElementAt(aItem, depth);
        }
    }

}