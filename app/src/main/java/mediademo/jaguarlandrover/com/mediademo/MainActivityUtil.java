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

import java.util.HashMap;

public class MainActivityUtil {
    private final static String TAG = "MediaDemo:MainActivityUtil";

    public static HashMap<Integer, String> initializeViewToServiceIdMap() {
        HashMap<Integer, String> initial = new HashMap<>();

        //initial.put(R.id.playPauseButton, MediaServiceIdentifier.PLAY_PAUSE.value());
        return initial;
    }

    public static HashMap<String, Integer> initializeServiceToViewIdMap() {
        HashMap<String, Integer> initial = new HashMap<>();

        //initial.put(MediaServiceIdentifier.PLAY_PAUSE.value(), R.id.playPauseButton);
        return initial;
    }
}
