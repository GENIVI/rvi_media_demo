package mediademo.jaguarlandrover.com.mediademo;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.internal.LinkedTreeMap;

import java.util.ArrayList;

/**
 * Created by aren on 10/3/16.
 */

public class MediaAdapter extends BaseAdapter {
    private ArrayList<LinkedTreeMap> data;
    private Activity activity;
    private static LayoutInflater inflater = null;

    public MediaAdapter(Activity active, ArrayList<LinkedTreeMap> objects) {
        activity = active;
        data = objects;
        inflater = (LayoutInflater) active.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        if (data != null) {
            return data.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view_id = convertView;
        if (convertView == null) {
            view_id = inflater.inflate(R.layout.list_row, null);
            Button more_info = (Button) view_id.findViewById(R.id.remove_button);
            Typeface font = Typeface.createFromAsset(activity.getAssets(), "fontawesome-webfont.ttf");
            more_info.setTypeface(font);
        }
        LinkedTreeMap song = data.get(position);
        //get views
        TextView song_title = (TextView) view_id.findViewById(R.id.song_title);
        TextView artist_name = (TextView) view_id.findViewById(R.id.artist);
        ImageView album_art = (ImageView) view_id.findViewById(R.id.album_art);
        Button ex_icon = (Button) view_id.findViewById(R.id.remove_button);
        //get information
        //setup info
        song_title.setText(song.get("displayName").toString() + " - " + song.get("album").toString());
        artist_name.setText(song.get("artist").toString());
        // album_art.setImageURI(song.get_album_art());
        album_art.setImageResource(R.drawable.ic_music_video_black_24dp);
        ex_icon.setOnClickListener(new RemoveOnClick(position, this));
        return view_id;
    }
}

class RemoveOnClick implements View.OnClickListener {
    private Integer position = null;
    private BaseAdapter parent = null;

    public RemoveOnClick(Integer position, BaseAdapter container) {
        this.position = position;
        this.parent = container;
    }
    @Override
    public void onClick(View v) {
        if (this.position != null) {
            MediaListObject.getInstance().remove(this.position);
            this.parent.notifyDataSetChanged();
        }
    }
}
