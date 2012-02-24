package name.pilgr.android.picat;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.TextView;
import name.pilgr.android.picat.actionbar.ActionBarHelper;
import name.pilgr.android.picat.fragments.ConnectionFragment;
import name.pilgr.android.picat.fragments.OnActionPerformedListener;
import name.pilgr.android.picat.fragments.PinInputFragment;
import name.pilgr.android.picat.utils.Analytics;
import name.pilgr.android.picat.utils.Log;

public class ConnectionController extends Fragment {
    private FragmentManager fragmentManager;

    private static final long CONNECTION_TIMEOUT_IN_S = 300;
    private static final String FRAGMENT_CONNECTION = "connection_in_progress";

    private ConnectivityManager connManager;
    private boolean isDestroying = false;
    private OnActionPerformedListener actionPerformedListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragmentManager = getActivity().getSupportFragmentManager();
        piApplication application = (piApplication) getActivity().getApplication();
        connManager = application.getConnectivityManager();
        connManager.activate();

        connManager.setOnConnectionChangeListener(new ConnectivityManager.OnConnectionChangeListener() {
            public void onConnect(String osName_, String osVersion_, String userName_, boolean approvedByUser_, int piServerVersion, ConnectivityManager.ConnType currConnType_) {
                onConnected(currConnType_);
            }

            public void onReconnect() {

            }

            public void onDisconnect() {
                onDisconnected();
            }

            public void onPinFailed() {
                actionPerformedListener.onPinInserted(false);
            }

            public void onEnterPin() {
                actionPerformedListener.onPinInserted(true);
            }
        });

        abReset();

        //Add temporary connection fragment
        if (fragmentManager.findFragmentByTag(FRAGMENT_CONNECTION) == null) {
            actionPerformedListener.onConnectionAbsence();
        }
    }

    private void onConnected(ConnectivityManager.ConnType currConnType_) {
        String conType = currConnType_ == ConnectivityManager.ConnType.USB ? "USB" : "Wi-Fi";
        Log.d("Connected via " + conType);
        abSetRefresh(false);

        if (currConnType_ == ConnectivityManager.ConnType.USB) {
            ((ActionBarActivity) getActivity()).getActionBarHelper().setRefreshActionIcon(R.drawable.action_usb);
        } else {
            ((ActionBarActivity) getActivity()).getActionBarHelper().setRefreshActionIcon(R.drawable.action_wifi);
        }
        actionPerformedListener.onConnected();
        showRateDialogIfNeeded();
        Analytics.trackConnected(conType);
    }

    @Override
    public void onStart() {
        super.onStart();
        isDestroying = false;

        /**
         * It's depends from menu creation.
         * But menu creation invoke before onCreate.
         * So we display progress bar first time by this way
         */
        if (!connManager.isConnected()) {
            abSetRefresh(true);
        }
    }

    private void onDisconnected() {
        Log.d("onDisconnected");
        if (isDestroying) return;
        abReset();
        actionPerformedListener.onDisconnect();
    }

    @Override
    public void onStop() {
        super.onStop();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isDestroying = true;
        //Disconnect
        new Thread() {
            public void run() {
                connManager.deactivate();
            }
        }.start();

    }

    private void abReset() {
        getActivity().setTitle(getResources().getString(R.string.app_name));
        abSetRefresh(true);
    }

    private void abSetRefresh(boolean refresh) {
        ActionBarHelper abHelper = ((ActionBarActivity) getActivity()).getActionBarHelper();
        if (abHelper != null) {
            abHelper.setRefreshActionItemState(refresh);
        }
    }

    private void showRateDialogIfNeeded() {
        final String CONNECTION_COUNTER = "connection-counter";
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        int connCounter = pref.getInt(CONNECTION_COUNTER, 0);
        if (connCounter == 5) {
            getActivity().showDialog(PiCatActivity.DIALOG_RATE_APP);
        }
        pref.edit().putInt(CONNECTION_COUNTER, connCounter + 1).commit();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            actionPerformedListener = (OnActionPerformedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implemented OnActionPerformedListener");
        }
    }
}
