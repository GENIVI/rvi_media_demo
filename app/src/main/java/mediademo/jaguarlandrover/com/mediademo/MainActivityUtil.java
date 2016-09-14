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

import android.widget.ImageButton;

import java.util.HashMap;

public class MainActivityUtil {
    private final static String TAG = "MediaDemo:MainActivityUtil";

    public static HashMap<Integer, String> initializeViewToServiceIdMap() {
        HashMap<Integer, String> initial = new HashMap<>();

        initial.put(R.id.playPauseButton, MediaServiceIdentifier.PLAY_PAUSE.value());
        initial.put(R.id.skipNext, MediaServiceIdentifier.NEXT.value());
        initial.put(R.id.skipPrevious, MediaServiceIdentifier.PREVIOUS.value());
        initial.put(R.id.repeat, MediaServiceIdentifier.REPEAT.value());
        initial.put(R.id.shuffle, MediaServiceIdentifier.SHUFFLE.value());
        initial.put(R.id.playList, MediaServiceIdentifier.REPEAT.value());
        return initial;
    }

    public static HashMap<String, Integer> initializeServiceToViewIdMap() {
        HashMap<String, Integer> initial = new HashMap<>();

        initial.put(MediaServiceIdentifier.PLAY_PAUSE.value(), R.id.playPauseButton);
        initial.put(MediaServiceIdentifier.NEXT.value(), R.id.skipNext);
        initial.put(MediaServiceIdentifier.PREVIOUS.value(),R.id.skipPrevious);
        initial.put(MediaServiceIdentifier.REPEAT.value(), R.id.repeat);
        initial.put(MediaServiceIdentifier.SHUFFLE.value(), R.id.shuffle);
        initial.put(MediaServiceIdentifier.REPEAT.value(), R.id.playList);
        return initial;
    }

    public static HashMap<Integer, Integer> initializeButtonOffImagesMap() {
        HashMap<Integer, Integer> offImages = new HashMap<>();

        offImages.put(R.id.playPauseButton, R.drawable.ic_play_arrow_black_24dp);
        offImages.put(R.id.skipNext, R.drawable.ic_skip_next_black_24dp);
        offImages.put(R.id.skipPrevious, R.drawable.ic_skip_previous_black_24dp);
        offImages.put(R.id.repeat, R.drawable.ic_repeat_black_24dp);
        offImages.put(R.id.shuffle, R.drawable.ic_shuffle_black_24dp);
        offImages.put(R.id.playList, R.drawable.ic_playlist_play_black_24dp);
    }
}
