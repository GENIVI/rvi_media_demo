package mediademo.jaguarlandrover.com.mediademo;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.google.gson.internal.LinkedTreeMap;

import java.util.ArrayList;

/**
 * Created by aren on 10/11/16.
 */

public class MultimediaAdapter extends BaseAdapter {
    private ArrayList<LinkedTreeMap> data;
    private Activity activity;
    private static LayoutInflater inflater = null;

    public MultimediaAdapter(Activity active, ArrayList<LinkedTreeMap> objects) {
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
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
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
        LinkedTreeMap song = data.get(position);
        //get views
        TextView info = (TextView) view_id.findViewById(android.R.id.text1);
        //get information
        //setup info
        info.setText((String) song.get("displayName"));

        return view_id;
    }

    public void clearData() {
        //data.clear();
        MultimediaListObject.getInstance().clearData();
    }

    public void update() {
        data = MultimediaListObject.getInstance().getMultiMedia();
    }
}
