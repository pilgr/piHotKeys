package name.pilgr.android.picat.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.AdapterView;
import android.widget.GridView;
import name.pilgr.android.picat.ConnectivityManager;
import name.pilgr.android.picat.HotkeysAdapter;
import name.pilgr.android.picat.R;
import name.pilgr.android.picat.model.Application;
import name.pilgr.android.picat.model.Hotkeys;
import name.pilgr.android.picat.model.Key;
import name.pilgr.android.picat.shared.EventSequence;

/**
 * Created by IntelliJ IDEA.
 * User: pilgr
 * Date: 22.07.11
 * Time: 22:10
 * To change this template use File | Settings | File Templates.
 */
public class GridFragment extends Fragment {
    private Hotkeys hotkeys;
    private GridView grid;

    //Adapter for keys grid view
    private HotkeysAdapter hotkeysAdapter;
    private ConnectivityManager _connManager;
    private LayoutAnimationController gridAnimation;

    public GridFragment(ConnectivityManager _conManager, Hotkeys hotkeys_) {
        this.hotkeys = hotkeys_;
        this._connManager = _conManager;
        this.hotkeysAdapter = new HotkeysAdapter();
    }

    public GridFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.grid_fragment, container, false);
        grid = (GridView) v.findViewById(R.id.grid_of_keys);
        grid.setAdapter(hotkeysAdapter);
        hotkeysAdapter.setApplication(hotkeys.getActiveApp());

        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Key key = hotkeysAdapter.getItem(i);
                if (key != null) {
                    EventSequence msg = key.getEventSequence();
                    _connManager.sendMessage(msg);
                }
            }
        });

        gridAnimation
                = AnimationUtils.loadLayoutAnimation(
                getActivity(), R.anim.layout_grid_inverse_fade);
        grid.setLayoutAnimation(gridAnimation);

        return v;
    }

    public void setActiveApp(Application activeApp) {
        hotkeysAdapter.setApplication(activeApp);
        //grid can be null while fragment creating?
        //[10.01.12 Comment to disable animation]
        /*if (grid != null) {
            grid.startLayoutAnimation();
        }*/
    }
}
