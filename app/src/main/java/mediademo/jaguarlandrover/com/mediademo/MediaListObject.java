package mediademo.jaguarlandrover.com.mediademo;

import com.google.gson.internal.LinkedTreeMap;

import java.util.ArrayList;

/**
 * Created by aren on 10/6/16.
 */

public class MediaListObject {
    private ArrayList<LinkedTreeMap> songs = new ArrayList<LinkedTreeMap>();
    public ArrayList<LinkedTreeMap> getSongs() { return songs; }
    public void setSongs(ArrayList<LinkedTreeMap> songs) { this.songs = songs; }
    private static final MediaListObject holder = new MediaListObject();
    public static MediaListObject getInstance() { return holder; }
}
