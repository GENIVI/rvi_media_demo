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

import android.content.Context;
import android.content.res.Resources;
import android.provider.MediaStore;

import com.google.gson.JsonElement;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class MainActivityUtil {
    private final static String TAG = "MediaDemo:MainActivityUtil";

    public static List<String> getServices = Arrays.asList(
            "GETMUTEATTRIBUTE",
            "GETSHUFFLEATTRIBUTE",
            "GETREPEATATTRIBUTE",
            "GETVOLUMEATTRIBUTE",
            "GETPLAYBACKSTATUSATTRIBUTE",
            "GETPOSITIONATTRIBUTE",
            "GETDURATIONATTRIBUTE",
            "GETCURRENTPLAYQUEUE",
            "GETCURRENTTRACKATTRIBUTE");

    public static HashMap<Integer, String> initializeViewToServiceIdMap() {
        HashMap<Integer, String> initial = new HashMap<>();

        initial.put(R.id.playPauseButton, MediaServiceIdentifier.PLAY_PAUSE.value());
        initial.put(R.id.skipNext, MediaServiceIdentifier.NEXT.value());
        initial.put(R.id.skipPrevious, MediaServiceIdentifier.PREVIOUS.value());
        initial.put(R.id.repeat, MediaServiceIdentifier.REPEAT.value());
        initial.put(R.id.shuffle, MediaServiceIdentifier.SHUFFLE.value());
        initial.put(R.id.playList, MediaServiceIdentifier.REPEAT.value());
        initial.put(R.id.volume, MediaServiceIdentifier.MUTE.value());
        return initial;
    }

    public static HashMap<String, Integer> initializeServiceToViewIdMap() {
        HashMap<String, Integer> initial = new HashMap<>();

        initial.put(MediaServiceIdentifier.PLAY_PAUSE.value(), R.id.playPauseButton);
        initial.put(MediaServiceIdentifier.NEXT.value(), R.id.skipNext);
        initial.put(MediaServiceIdentifier.PREVIOUS.value(),R.id.skipPrevious);
        initial.put(MediaServiceIdentifier.REPEAT.value(), R.id.repeat);
        initial.put(MediaServiceIdentifier.SHUFFLE.value(), R.id.shuffle);
        initial.put(MediaServiceIdentifier.GETPLAYLIST.value(), R.id.playList);
        initial.put(MediaServiceIdentifier.GETMUTE.value(), R.id.volume);
        return initial;
    }

    public static HashMap<Integer, Integer> initializeButtonImages() {
        HashMap<Integer, Integer> offImages = new HashMap<>();

        offImages.put(R.id.playPauseButton, R.string.icon_play);
        offImages.put(R.id.skipNext, R.string.icon_skip_forward);
        offImages.put(R.id.skipPrevious, R.string.icon_skip_back);
        offImages.put(R.id.repeat, R.string.icon_repeat);
        offImages.put(R.id.shuffle, R.string.icon_shuffle);
        offImages.put(R.id.playList, R.string.icon_playlist);
        offImages.put(R.id.volume, R.string.icon_volume);
        return offImages;
    }

    public static HashMap<Integer, Boolean> initializeButtonState() {
        HashMap<Integer, Boolean> states = new HashMap<>();

        states.put(R.id.playPauseButton, false);
        states.put(R.id.skipNext, false);
        states.put(R.id.skipPrevious, false);
        states.put(R.id.repeat, false);
        states.put(R.id.shuffle, false);
        states.put(R.id.playList, false);
        states.put(R.id.volume, false);
        return states;
    }

    public static HashMap<String, Integer> initializeSignaltoViewId(Context context) {
        HashMap<String, Integer> signals = new HashMap<>();

        signals.put(context.getString(R.string.play_signal), R.id.playPauseButton);
        signals.put(context.getString(R.string.next_signal), R.id.skipNext);
        signals.put(context.getString(R.string.previous_signal), R.id.skipPrevious);
        signals.put(context.getString(R.string.shuffle_signal), R.id.shuffle);
        signals.put(context.getString(R.string.shuffle_change), R.id.shuffle);
        signals.put(context.getString(R.string.repeat_signal), R.id.repeat);
        signals.put(context.getString(R.string.mute_signal), R.id.volume);
        return signals;
    }

    public static HashMap<String, Integer> initializeSignaltoInvokable() {
        HashMap<String, Integer> signals = new HashMap<>();

        signals.put(MediaServiceIdentifier.PLAY_PAUSE.value(), R.string.play_signal);
        signals.put(MediaServiceIdentifier.SHUFFLE.value(), R.string.shuffle_signal);
        signals.put(MediaServiceIdentifier.REPEAT.value(), R.string.repeat_signal);
        signals.put(MediaServiceIdentifier.NEXT.value(), R.string.playlist_signal);
        signals.put(MediaServiceIdentifier.PREVIOUS.value(), R.string.playlist_signal);
        return signals;
    }

    public static HashMap<String, ParamGetter> initializeSignalToValue(Context context) {
        HashMap<String, ParamGetter> signals = new HashMap<>();

        signals.put(context.getString(R.string.play_signal),
                new ParamGetter() {
            @Override
            public Boolean getParam(Object param) { return (boolean) param; }
            });
        signals.put(context.getString(R.string.shuffle_signal),
                new ParamGetter() {
            @Override
            public Boolean getParam(Object param) { return (1 == ((Double) param).intValue()); }
            });
        signals.put(context.getString(R.string.repeat_signal),
                new ParamGetter() {
            @Override
            public Boolean getParam(Object doe) {
                return (1 == ((Double) doe).intValue());
            }
        });
        signals.put(context.getString(R.string.shuffle_change),
                new ParamGetter() {
                    @Override
                    public Boolean getParam(Object doe) {
                        return (1 == ((Double) doe).intValue());
                    }
                });
        return signals;
    }

    public interface ParamGetter {
        Boolean getParam(Object doe);
    }
}
