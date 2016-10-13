package mediademo.jaguarlandrover.com.mediademo;

import com.google.gson.internal.LinkedTreeMap;

import java.util.ArrayList;

/**
 * Created by aren on 10/11/16.
 */

public class MultimediaListObject {
    private ArrayList<LinkedTreeMap> medias = new ArrayList<LinkedTreeMap>();
    private String root = null;
    public ArrayList<LinkedTreeMap> getMultiMedia() {
        if (medias.size() < 1) {
            ArrayList<LinkedTreeMap> temp = new ArrayList<>();
            LinkedTreeMap temp_tree = new LinkedTreeMap();
            temp_tree.put("displayName", "EMPTY");
            temp.add(temp_tree);
            return temp;
        }
        return medias;
    }
    public void clearData() { this.medias.clear(); }
    public String getRoot() { return this.root; }
    public void addMultimedia(LinkedTreeMap item) {
        if (this.medias.size() == 0 && this.root == null) {
                this.root = (String) item.get("parent");
        } else if (!this.medias.contains(item)) {
            this.medias.add(item);
        }
    }
    private static final mediademo.jaguarlandrover.com.mediademo.MultimediaListObject holder = new mediademo.jaguarlandrover.com.mediademo.MultimediaListObject();
    public static mediademo.jaguarlandrover.com.mediademo.MultimediaListObject getInstance() { return holder; }
}
