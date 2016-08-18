package jlr.demo.media.rvi.rvi_media_demo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MyFragment extends Fragment {

    public static Fragment newInstance(RviMediaDemo context, int pos, float scale) {
        Bundle b = new Bundle();
        b.putInt("pos", pos);
        b.putFloat("scale", scale);
        return Fragment.instantiate(context, MyFragment.class.getName(), b);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (container == null) {
            return null;
        }

        LinearLayout l = (LinearLayout)
                inflater.inflate(R.layout.carousel, container, false);

        int pos = this.getArguments().getInt("pos");
        TextView tv = (TextView) l.findViewById(R.id.text);
        StringBuilder msg = new StringBuilder(25);
        msg.append("Position = ");
        msg.append(pos);
        tv.setText(msg);

        MyLinearLayout root = (MyLinearLayout) l.findViewById(R.id.root);
        float scale = this.getArguments().getFloat("scale");
        root.setScaleBoth(scale);

        return l;
    }
}