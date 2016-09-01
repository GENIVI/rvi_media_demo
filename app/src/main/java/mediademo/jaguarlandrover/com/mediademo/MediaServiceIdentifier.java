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
    PLAY("PLAY"),
    PAUSE("PAUSE"),
    NEXT("NEXT"),
    SUBSCRIBE("subscribe"),
    UNSUBSCRIBE("unsubscribe"),
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
            case "PlayPause": return PLAY_PAUSE;
            case "Play":      return PLAY;
            case "Pause":     return PAUSE;
            case "Next":      return NEXT;
        }
        return NONE;
    }
}
