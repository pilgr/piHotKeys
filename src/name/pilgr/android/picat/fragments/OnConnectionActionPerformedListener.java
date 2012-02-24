package name.pilgr.android.picat.fragments;

/**
 * Public interface for handling actions in controller
 */
public interface OnConnectionActionPerformedListener {
    /**
     * Tels activity that PIN inserted
     *
     * @param flag - true - if PIN correct, else - false
     */
    public void onPinInserted(boolean flag);

    /**
     * If connection absence
     */
    public void onConnectionAbsence();

    /**
     * If device is connected
     */
    public void onConnected();

    /**
     * If disconnection occurred
     */
    public void onDisconnect();
}
