package name.pilgr.android.picat;

import android.graphics.Typeface;
import android.graphics.drawable.TransitionDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import name.pilgr.android.picat.model.Application;
import name.pilgr.android.picat.model.Key;

/**
 * Created by IntelliJ IDEA.
 * User: pilgr
 * Date: 09.06.11
 * Time: 21:02
 * To change this template use File | Settings | File Templates.
 */
public class HotkeysAdapter extends BaseAdapter {
    Application app;
    //private Typeface font;

    public void setApplication(Application app_) {
        app = app_;
        notifyDataSetChanged();
    }

    public int getCount() {
        return app == null ? 0 : app.keys.size();
    }

    public Key getItem(int i) {
        if (app == null) return null;
        try {
            return app.keys.get(i);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    public long getItemId(int i) {
        if (app == null) return 0;
        try {
            return app.keys.get(i).id;
        } catch (IndexOutOfBoundsException e) {
            return 0;
        }
    }

    public View getView(int i, View view, ViewGroup viewGroup) {
        //if (font == null) font = Typeface.createFromAsset(viewGroup.getContext().getAssets(), "HelveticaNeueLight.ttf");

        if (view == null) {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.hotkey, null);
        }

        TextView label = (TextView) view.findViewById(R.id.hotkey_label);
        //label.setTypeface(font);

        //If app was set
        if (app != null) {
            try {
                Key key = app.keys.get(i);
                label.setText(key.label);
                //if app have not full set keys
            } catch (IndexOutOfBoundsException e) {
                label.setText("");
            }
        } else {
            label.setText("");
        }

        return view;
    }

    private View.OnTouchListener transitionListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view_, MotionEvent motionEvent_) {
            TransitionDrawable dr = (TransitionDrawable) view_.getBackground();
            switch (motionEvent_.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    dr.startTransition(500);
                    return true;
                case MotionEvent.ACTION_UP:
                    dr.reverseTransition(500);
                    break;
            }
            return false;
        }
    };

}
