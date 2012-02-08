package name.pilgr.android.picat.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.Fragment;
import name.pilgr.android.picat.ConnectivityManager;
import name.pilgr.android.picat.PiCatActivity;
import name.pilgr.android.picat.R;
import name.pilgr.android.picat.model.Application;
import name.pilgr.android.picat.model.Command;
import name.pilgr.android.picat.model.Hotkeys;
import name.pilgr.android.picat.model.Key;
import name.pilgr.android.picat.utils.Log;

public class HardButtonsController extends Fragment {

    private ConnectivityManager connManager;
    private Hotkeys hotkeys;
    private BroadcastReceiver screenEventReceiver;

    public HardButtonsController() {
    }

    public HardButtonsController(ConnectivityManager _connManager, Hotkeys hotkeys_) {
        this.connManager = _connManager;
        this.hotkeys = hotkeys_;
    }

    @Override
    public void onStart() {
        super.onStart();
        ((PiCatActivity) getActivity()).setOnHardButtonPressedListener(new IHardButtonPressed() {
            @Override
            public void onVolumeUpPressed() {
                sendVolumeUp();
            }

            @Override
            public void onVolumeDownPressed() {
                sendVolumeDown();
            }
        });
    }

    private void sendVolumeDown() {
        sendButtonIfSupported(R.id.hotbtn_vol_down);
    }

    private void sendVolumeUp() {
        sendButtonIfSupported(R.id.hotbtn_vol_up);
    }


    private void sendButtonIfSupported(int btnId) {
        Application app = hotkeys.getActiveApp();
        if (app != null) {
            Key key = app.buttons.get(btnId);
            //If current app support hot buttons (see hotkeys.xml)
            if (key != null) {
                connManager.sendMessage(key.getEventSequence());
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        //Unregister listener
        ((PiCatActivity) getActivity()).setOnHardButtonPressedListener(null);
    }

}
