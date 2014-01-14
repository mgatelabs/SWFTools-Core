/*
    Copyright M-Gate Labs 2007
*/

/**
 <p>
 <b>Tag</b>
 </p>
 <p>
 Generic Tag Information
 </p>
 */


package com.mgatelabs.swftools.support.swf.tags;

public class Tag {
    public int id;
    public long length;
    public Object myObject;

    public Tag(int id, long length) {
        this.id = id;
        this.length = length;
        myObject = null;
    }

    public int getID() {
        return id;
    }

    public Object getObject() {
        return myObject;
    }

    public String toString() {
        return getTageName(id) + " : Bytes " + length;
    }

    public static String getTageName(int id) {
        switch (id) {
            case 0:
                return ("End");

            case 1: // ShowFrame
                return ("ShowFrame");

            case 2: // DefineShape
                return ("DefineShape");

            case 4: // PlaceObject
                return ("PlaceObject");

            case 5: // RemoveObject
                return ("RemoveObject");

            case 6: // DefineBits
                return ("DefineBits");

            case 7: // DefineButton
                return ("DefineButton");

            case 8: // JPEGTable
                return ("JPEG Table");

            case 9: // SetBackgroundColor 
                return ("SetBackgroundColor");

            case 10: // DefineFont 
                return ("DefineFont");

            case 11: // DefineText 
                return ("DefineText");

            case 12: // DoAction 
                return ("DoAction");

            case 13: // DefineFontInfo 
                return ("DefineFontInfo");

            case 14: // DefineText 
                return ("DefineSound");

            case 17: // DefineButtonSound
                return ("DefineButtonSound");

            case 20: // DefineBitsLossless 
                return "DefineBitsLossless ";

            case 21: // DefineBits2
                return ("DefineBits2");

            case 22: // DefineShape2
                return ("DefineShape2");

            case 23: // DefineButtonCxForm
                return ("DefineButtonCxForm");

            case 24: // Protect
            case 25: // Might Protect
                return ("Protect");

            case 26: // PlaceObject2
                return ("PlaceObject2");

            case 28: // RemoveObject2
                return ("RemoveObject2");

            case 32: // DefineShape3
                return ("DefineShape3");

            case 33: // DefineText2
                return ("DefineText2");

            case 34: // DefineButton2
                return ("DefineButton2");
            case 35: // DefineBitsJPEG3
                return ("DefineBitsJPEG3");
            case 36: // DefineBitsLossless2
                return "DefineBitsLossless2";

            // Flash 4

            case 37: // DefineEditText
                return ("DefineEditText");

            case 38: // DefineMovie
                return ("DefineMovie");

            case 39: // DefineSprite
                return ("DefineSprite");

            case 43: // FrameLabel 
                return ("FrameLabel");

            case 46: // DefineMorphShape 
                return ("DefineMorph Shape");

            case 48: // DefineFont2
                return ("DefineFont2");

            // Flash 5

            case 50: // DefineCommandObject
                return ("DefineCommandObject");

            case 51: // CharacterSet
                return ("CharacterSet");

            case 52: // ExternalFont
                return ("ExternalFont");

            case 56: // ExportAssets
                return ("ExportAssets");

            case 57: // ImportAssets
                return ("ImportAssets");

            case 58: // EnableDebugger
                return ("EnableDebugger");

            // Flash 6

            case 59: // DoInitAction
                return "DoInitAction";
            case 60: // DefineVideoStream
                return "DefineVideoStream";
            case 61: // VideoFrame
                return "VideoFrame";
            case 62: // DefineFontInfo2
                return "DefineFontInfo2";
            case 63: // DebugID
                return "DebugID";
            case 64: // ProtectDebug2
                return "ProtectDebug2";

            // Flash 7

            case 65: // ScriptLimits
                return "ScriptLimits";
            case 66: // SetTabIndex
                return "SetTabIndex";

            // Flash 8

            case 69: // FileAttributes
                return "FileAttributes";
            case 70: // EnableDebugger
                return ("PlaceObject3");
            case 71: // Import2
                return ("Import2");
            case 73: // DefineFontAlignZones
                return "DefineFontAlignZones";
            case 74: // CSMTextSettings
                return "CSMTextSettings";
            case 75: // DefineFont2
                return ("DefineFont3");
            case 77:
                return "Metadata";
            case 78:
                return "DefineScalingGrid";

            case 83: // DefineShape4
                return ("DefineShape4");
            case 84: // DefineMorphShape2
                return ("DefineMorphShape2");

            case 86: // DefineSceneAndFrameData
                return "DefineSceneAndFrameData";
            case 87: // DefineBinaryData
                return "DefineBinaryData";
            case 88: // DefineFontName
                return "DefineFontName";

            // My Own Flash Tag
            case 666: // DefineFontName
                return "Hidden Text";

            case 1023: // DefineBitsPtr
                return ("DefineBitsPtr");

            default:
                return "Unknown ID: " + id;
        }
    }

    // Test if an ID has an object ID
    public static boolean getObjectable(int id) {
        switch (id) {
            case 2: // DefineShape
            case 6: // DefineBits
            case 7: // DefineButton
            case 10: // DefineFont
            case 11: // DefineText
            case 13: // DefineFontInfo
            case 14: // DefineText
            case 17: // DefineButtonSound
            case 20: // DefineBitsLossless 
            case 21: // DefineBits2
            case 22: // DefineShape2
            case 23: // DefineButtonCxForm
            case 32: // DefineShape3
            case 33: // DefineText2
            case 34: // DefineButton2utton2");
            case 35: // Define Bits 3
            case 36: // DefineBitsLossless2
            case 37: // DefineEditText;
            case 38: // DefineMovieie");
            case 39: // DefineSpritete");
            case 46: // DefineMorphShape Shape");
            case 48: // DefineFont2
                //case 70:
            case 83: // EnableDebugger
            case 84: // DefineMorphShape2

            case 1023: // DefineBitsPtr
                //         return("DefineBitsPtr");

                return true;
            default:
                return false;
            //       return "Unknown ID: " + id;
        }
    }

}