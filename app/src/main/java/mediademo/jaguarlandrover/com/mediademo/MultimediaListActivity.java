package mediademo.jaguarlandrover.com.mediademo;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.gson.internal.LinkedTreeMap;

/**
 * Created by aren on 10/11/16.
 */

public class MultimediaListActivity extends AppCompatActivity {

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    private MultimediaAdapter adapter;
    private String TAG = "MMListActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.playlist);

        Window window = this.getWindow();

        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        // finally change the color
        window.setStatusBarColor(this.getResources().getColor(R.color.colorPrimaryDark));
        Toolbar settingsToolbar = (Toolbar) findViewById(R.id.settings_toolbar);
        settingsToolbar.setTitle("Multimedia");
        settingsToolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_material);
        ListView media_list = (ListView) findViewById(android.R.id.list);
        adapter = new MultimediaAdapter(this, MultimediaListObject.getInstance().getMultiMedia());
        CustomOnClick navigator = new CustomOnClick(null, this, adapter);
        settingsToolbar.setNavigationOnClickListener(navigator);
        media_list.setAdapter(adapter);
        media_list.setOnItemClickListener(new CustomOnItemClick(navigator, adapter, this));
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                adapter.update();
                adapter.notifyDataSetChanged();
                handler.postDelayed(this, 1 * 1000);
            }
        }, 2 * 1000);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("MultimediaList Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.settings_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.main_activity_item:
                finish();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }
}

class CustomOnClick implements View.OnClickListener {
    private String parent = null;
    private Activity runner = null;
    private MultimediaAdapter adapter = null;

    public CustomOnClick(String parent, Activity container, MultimediaAdapter adapter) {
        this.parent = parent;
        this.runner = container;
        this.adapter = adapter;
    }

    public void setParent(String container) {
        this.parent = container;
    }

    @Override
    public void onClick(View v) {
        if (this.parent != null) {
            this.adapter.clearData();
            MediaManager.invokeService(MediaServiceIdentifier.GETMEDIACHILD.value(), this.parent);
        } else {
            MultimediaListObject.getInstance().clearData();
            this.runner.finish();
        }
    }
}

class CustomOnItemClick implements AdapterView.OnItemClickListener {
    private CustomOnClick observer = null;
    private MultimediaAdapter adapter = null;
    private Activity activity = null;

    public CustomOnItemClick(CustomOnClick watcher, MultimediaAdapter adapt, Activity active) {
        observer = watcher;
        adapter = adapt;
        activity = active;
    }
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        LinkedTreeMap container = (LinkedTreeMap) adapter.getItem(position);
        String path = (String) container.get("path");
        setParent((String) container.get("parent"));
        String type = (String) container.get("type");
        if (type.equalsIgnoreCase("container")) {
            adapter.clearData();
            if (path != null) {
                MediaManager.invokeService(MediaServiceIdentifier.GETMEDIACHILD.value(), path);
            }
        } else if (type.equalsIgnoreCase("music")){
            String name = (String) container.get("displayName");
            MediaManager.invokeService(MediaServiceIdentifier.ENQUEUE.value(), path);
            String msg = name + " added to playqueue!";
            Toast connectedToast = Toast.makeText(activity.getApplicationContext(), msg, Toast.LENGTH_SHORT);
            connectedToast.show();
        }
    }

    public void setParent(String container) {
        observer.setParent(container);
    }
}
