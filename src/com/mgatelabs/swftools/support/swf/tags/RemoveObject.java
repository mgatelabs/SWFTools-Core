/*
    Copyright M-Gate Labs 2007
*/

/**
 <p>
 <b>Remove Object Tag</b>
 </p>
 <p>
 Used to remove objects from the display graph.
 </p>
 */

package com.mgatelabs.swftools.support.swf.tags;

public class RemoveObject {
    // My Variables

    private int CharacterId;
    private int Depth;

    //CharacterId        	      UI16        	      ID of character to remove
    //Depth      	     UI16      	     Depth of character

    // Constructor
    public RemoveObject(int id, int depth) {
        CharacterId = id;
        Depth = depth;
    }

    // Constructor
    public RemoveObject(int depth) {
        CharacterId = -1;
        Depth = depth;
    }

    // Get ID
    public int getID() {
        return CharacterId;
    }

    // Get Depth
    public int getDepth() {
        return Depth;
    }
}