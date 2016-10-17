package mediademo.jaguarlandrover.com.mediademo;

import android.util.Log;

import com.google.gson.internal.LinkedTreeMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by aren on 10/11/16.
 */

public class MultimediaListObject {
    // private ArrayList<LinkedTreeMap> medias = new ArrayList<LinkedTreeMap>();
    private LinkedTreeMap<String, LinkedTreeMap> medias = new LinkedTreeMap<>();
    private String root = null;
    private String TAG = "MMListObject";

    public LinkedTreeMap<String, LinkedTreeMap> getMultiMedia(String parent) {
        if (parent == null) {
            if (this.medias.keySet().size() > 0) {
                return this.medias;
            } else {
               return emptyMedias();
            }
        } else {
            LinkedTreeMap found_parent = findParent(parent, this.medias);
            if (found_parent != null) {
                return (LinkedTreeMap<String, LinkedTreeMap>) found_parent.get("children");
            } else {
                return emptyMedias();
            }
        }
    }
    private LinkedTreeMap<String, LinkedTreeMap> emptyMedias() {
        LinkedTreeMap<String, LinkedTreeMap> temp = new LinkedTreeMap<>();
        LinkedTreeMap<String, String> tempo = new LinkedTreeMap();
        tempo.put("parent", "Empty");
        tempo.put("path", "Empty");
        tempo.put("type", "Empty");
        temp.put("Empty", tempo);
        return temp;
    }
    public void clearData() { this.medias.clear(); }
    public String getRoot() { return this.root; }
    public void addMultimedia(LinkedTreeMap item) {
        if (this.medias.size() == 0 && this.root == null) {
                this.root = (String) item.get("parent");
        } else {
            Object parent = item.get("parent");
            if (findParent(parent, this.medias) != null) {
                LinkedTreeMap container = this.medias.get(parent);
                if (container.get("path") == item.get("path")) {
                    return;
                }
                Object children = container.get("children");
                if (children == null) {
                    LinkedTreeMap<String, LinkedTreeMap> temp = new LinkedTreeMap<>();
                    temp.put((String) item.get("path"), item);
                    container.put("children", temp);
                } else if (children.getClass() == LinkedTreeMap.class) {
                    if (((LinkedTreeMap) children).containsKey(item.get("path"))) {
                        return;
                    } else {
                        ((LinkedTreeMap) children).put(item.get("path"), item);
                    }
                }
            } else {
                if (parent != null) {
                    this.medias.put(item.get("path").toString(), item);
                }
            }
        }
        Log.d(TAG, this.medias.toString());
    }
    private LinkedTreeMap findParent(Object parent, LinkedTreeMap<String, LinkedTreeMap> mediaMap) {
        if (mediaMap == null || parent == null) {
            return null;
        }
        if (mediaMap.keySet().contains(parent.toString())) {
            return this.medias.get(parent.toString());
        } else {
            Iterator iter = mediaMap.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry pair = (Map.Entry)iter.next();
                LinkedTreeMap match = findParent(parent, ((LinkedTreeMap<String, LinkedTreeMap>) pair.getValue()).get("children"));
                if (match != null) {
                    return match;
                }
            }
        }
        return null;
    }
    private static final mediademo.jaguarlandrover.com.mediademo.MultimediaListObject holder = new mediademo.jaguarlandrover.com.mediademo.MultimediaListObject();
    public static mediademo.jaguarlandrover.com.mediademo.MultimediaListObject getInstance() { return holder; }
}
