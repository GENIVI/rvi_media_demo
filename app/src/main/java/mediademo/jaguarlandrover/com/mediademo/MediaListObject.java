package mediademo.jaguarlandrover.com.mediademo;

import com.google.gson.internal.LinkedTreeMap;

import java.util.ArrayList;

/**
 * Created by aren on 10/6/16.
 */

public class MediaListObject {
    private ArrayList<LinkedTreeMap> songs = new ArrayList<LinkedTreeMap>();
    public ArrayList<LinkedTreeMap> getSongs() {
        if (songs.size() < 1) {
            ArrayList<LinkedTreeMap> temp = new ArrayList<>();
            LinkedTreeMap temp_tree = new LinkedTreeMap();
            temp_tree.put("displayName", "EMPTY");
            temp_tree.put("album", "EMPTY");
            temp_tree.put("artist", "EMPTY");
            temp_tree.put("container", "EMPTY");
            temp.add(temp_tree);
            return temp;
        }
        return songs;
    }
    public void setSongs(ArrayList<LinkedTreeMap> songs) { this.songs = songs; }
    public void remove(Integer position) { this.songs.remove(position); }
    private static final MediaListObject holder = new MediaListObject();
    public static MediaListObject getInstance() { return holder; }
}
