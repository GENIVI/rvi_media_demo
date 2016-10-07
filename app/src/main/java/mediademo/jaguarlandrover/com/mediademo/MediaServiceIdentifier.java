package mediademo.jaguarlandrover.com.mediademo;/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *                                                                                                                          
*
* Copyright (c) 2016 Jaguar Land Rover.
*
* This program is licensed under the terms and conditions of the
* Mozilla Public License, version 2.0. The full text of the
* Mozilla Public License is at https://www.mozilla.org/MPL/2.0/
*
* Project: MediaDemo
*
* Created by aren on 8/23/16.
*
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

public enum MediaServiceIdentifier {
    PLAY_PAUSE("PLAYPAUSE"),
    GET_PLAY_PAUSE("GETCANPLAYATTRIBUTE"),
    CURRENT_CHANGE("ONCURRENTTRACKATTRIBUTECHANGED"),
    NEXT("NEXT"),
    PREVIOUS("PREVIOUS"),
    SHUFFLE("SETSHUFFLEATTRIBUTE"),
    GETSHUFFLE("GETSHUFFLEATTRIBUTE"),
    REPEAT("SETREPEATATTRIBUTE"),
    GETREPEAT("GETRPEATATTRIBUTE"),
    DURATION("SETDURATIONATTRIBUTE"),
    GETDURATION("GETDURATIONATTRIBUTE"),
    GETPOSITION("GETPOSITIONATTRIBUTE"),
    POSITION_CHANGE(""),
    VOLUME("SETVOLUMEATTRIBUTE"),
    GETVOLUME("GETVOLUMEATTRIBUTE"),
    PLAYLIST("OPENPLAYLIST"),
    GETPLAYLIST("GETCURRENTPLAYQUEUE"),
    GETMULTIMEDIA("DISCOVERMM"),
    SUBSCRIBE("SUBSCRIBE"),
    GETMEDIACHILD("LISTCHILDREN"),
    NONE("none");

    private final String mIdentifier;

    MediaServiceIdentifier(String identifier){
        mIdentifier = identifier;
    }

    public final String value() {
        return mIdentifier;
    }

    public static MediaServiceIdentifier get(String identifier) {
        switch(identifier) {
            case "PlayPause":   return PLAY_PAUSE;
            case "Next":        return NEXT;
            case "Previous":    return PREVIOUS;
            case "Shuffle":     return SHUFFLE;
            case "Repeat":      return REPEAT;
            case "Duration":    return DURATION;
            case "Volume":      return VOLUME;
            case "PlayList":    return PLAYLIST;
            case "Subscribe":   return SUBSCRIBE;
            case "CurrentQueue":return GETPLAYLIST;
        }
        return NONE;
    }
}
