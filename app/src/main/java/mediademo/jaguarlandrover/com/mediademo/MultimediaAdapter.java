package mediademo.jaguarlandrover.com.mediademo;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.google.gson.internal.LinkedTreeMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by aren on 10/11/16.
 */

public class MultimediaAdapter extends BaseAdapter {
    private LinkedTreeMap<String, LinkedTreeMap> data;
    private Activity activity;
    private static LayoutInflater inflater = null;
    private String TAG = "MMAdapter";

    public MultimediaAdapter(Activity active, LinkedTreeMap<String, LinkedTreeMap> objects) {
        activity = active;
        data = objects;
        inflater = (LayoutInflater) active.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        if(data != null) {
            return data.size();
        }else{
            return 0;
        }
    }

    @Override
    public Object getItem(int position) {
        LinkedTreeMap[] temp = data.values().toArray(new LinkedTreeMap[data.values().size()]);
        return temp[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public String getRoot() {
        LinkedTreeMap item = (LinkedTreeMap) getItem(0);
        Object parent = item.get("parent");
        if (parent != null) {
            return (String) parent;
        } else {
            return null;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view_id = convertView;
        if (convertView == null) {
            view_id = inflater.inflate(android.R.layout.simple_list_item_1, null);
            TextView info = (TextView) view_id.findViewById(android.R.id.text1);
            Typeface font = Typeface.createFromAsset(activity.getAssets(), "fontawesome-webfont.ttf");
            info.setTypeface(font);
        }
        LinkedTreeMap song = (LinkedTreeMap) getItem(position);
        //get views
        TextView info = (TextView) view_id.findViewById(android.R.id.text1);
        //get information
        //setup info
        info.setText((String) song.get("displayName"));

        return view_id;
    }

    public void update(String path) {
        data = MultimediaListObject.getInstance().getMultiMedia(path);
        notifyDataSetChanged();
    }
}
