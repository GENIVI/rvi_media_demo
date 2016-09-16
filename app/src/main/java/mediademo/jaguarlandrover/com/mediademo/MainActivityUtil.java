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
        offImages.put(R.id.playListButton, R.drawable.ic_playlist_play_black_24dp);
        return offImages;
    }

    public static HashMap<Integer, Integer> initializeButtonOnImagesMap() {
        HashMap<Integer, Integer> onImages = new HashMap<>();

        onImages.put(R.id.playPauseButton, R.drawable.ic_pause_black_24dp);
        onImages.put(R.id.skipNext, R.drawable.ic_skip_next_black_24dp);
        onImages.put(R.id.skipPrevious, R.drawable.ic_skip_previous_black_24dp);
        onImages.put(R.id.repeat, R.drawable.ic_repeat_white_24dp);
        onImages.put(R.id.shuffle, R.drawable.ic_shuffle_white_24dp);
        onImages.put(R.id.playListButton, R.drawable.ic_playlist_play_black_24dp);
        return onImages;
    }

    public static HashMap<Integer, Boolean> initializeButtonState() {
        HashMap<Integer, Boolean> states = new HashMap<>();

        states.put(R.id.playPauseButton, false);
        states.put(R.id.skipNext, false);
        states.put(R.id.skipPrevious, false);
        states.put(R.id.repeat, false);
        states.put(R.id.shuffle, false);
        states.put(R.id.playListButton, false);
        return states;
    }

    public static HashMap<Integer, Integer> initializeSignaltoViewId() {
        HashMap<Integer, Integer> signals = new HashMap<>();

        signals.put(R.string.play_signal, R.id.playPauseButton);
        signals.put(R.string.pause_signal, R.id.playPauseButton);
        signals.put(R.string.next_signal, R.id.skipNext);
        signals.put(R.string.previous_signal, R.id.skipPrevious);
        signals.put(R.string.shuffle_signal, R.id.shuffle);
        signals.put(R.string.repeat_signal, R.id.repeat);
        return signals;
    }
}
