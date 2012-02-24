package name.pilgr.android.picat;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v4.app.FragmentManager;
import android.text.SpannableString;
import android.text.util.Linkify;
import android.view.*;
import android.widget.TextView;
import android.widget.Toast;
import com.bugsense.trace.BugSenseHandler;
import name.pilgr.android.picat.fragments.*;
import name.pilgr.android.picat.model.Application;
import name.pilgr.android.picat.model.Hotkeys;
import name.pilgr.android.picat.utils.Analytics;
import name.pilgr.android.picat.utils.Log;
import name.pilgr.android.picat.utils.PrivateKeys;
import net.robotmedia.billing.BillingController;
import net.robotmedia.billing.BillingRequest;
import net.robotmedia.billing.helper.AbstractBillingObserver;
import net.robotmedia.billing.model.Transaction;

//TODO Показывать рефреш-иконку при старте 4-ки
//TODO Поправить все менюшки
public class PiCatActivity extends ActionBarActivity implements OnConnectionActionPerformedListener {

    private FragmentManager fragmentManager = getSupportFragmentManager();
    private PowerManager.WakeLock wakeLock;
    private Hotkeys hotkeys;

    private ConnectionController connectionController;
    private OsLevelController touchController;
    private HardButtonsController hardButtonsController;
    private GridFragment gridFragment;
    private ConnectionFragment connectionFragment;
    private IHardButtonPressed hardButtonListener = null;

    private ConnectivityManager connManager;
    private PinInputFragment pinFragment;
    private String prevProcName = "";
    private static final String FRAGMENT_PIN = "pin";
    private static final String FRAGMENT_GRID = "grid";

    public static final int DIALOG_RATE_APP = 2;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d("Creating PiCat activity...");
        super.onCreate(savedInstanceState);
        BugSenseHandler.setup(this, PrivateKeys.BUGSENSE_API_KEY);

        //requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.main);

        //Keep screen on
        PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "DimScreen");

        //Create the connectivity manager with credentials (user name and type of account)
        createConnManager();

        setOnWindowChangeListeners();

        //Load hotkeys configuration from XML
        try {
            hotkeys = new Hotkeys(this, R.xml.hotkeys);
        } catch (Exception e) {
            Log.e(e.toString());
        }
        // Init needed fragments
        touchController = new OsLevelController(hotkeys);
        hardButtonsController = new HardButtonsController(hotkeys);
        gridFragment = new GridFragment(hotkeys);
        connectionFragment = new ConnectionFragment();
        pinFragment = new PinInputFragment();
        connectionController = new ConnectionController();

        //Add invisible fragments - controllers
        fragmentManager.beginTransaction().
                add(touchController, "touch").
                add(hardButtonsController, "buttons").
                add(connectionController, "connection_controller").
                commit();

        donateBillingHelper();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        //TODO Показывать индикатор поиска сервера
        //getActionBarHelper().setRefreshActionItemState(isWaitResponse());
        hideDonateIconIfAlreadyDonated();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        //We wait response? Then menu item must be not enabled
        //We need do this here due the Honeycomb specific menu
        //getActionBarHelper().setRefreshActionItemState(isWaitResponse());
        hideDonateIconIfAlreadyDonated();
        return super.onPrepareOptionsMenu(menu);
    }

    private void createConnManager() {
        String accountName, accountType;
        Account[] accounts = AccountManager.get(this).getAccounts();
        if (accounts.length != 0) {
            accountName = accounts[0].name;
            accountType = accounts[0].type;
        } else {
            accountName = "Unnamed";
            accountType = "Unnamed";
        }
        connManager = new ConnectivityManager(accountName, accountType);
        PiApplication2 application = (PiApplication2) getApplication();
        application.setConnectivityManager(connManager);
    }

    @Override
    public void onStart() {
        super.onStart();
        Analytics.startSession(this);
        //To hide after finish Donate activity
        hideDonateIconIfAlreadyDonated();
        Log.d("PiCat activity has been started");
    }

    @Override
    protected void onResume() {
        super.onResume();
        wakeLock.acquire();
    }

    @Override
    protected void onPause() {
        super.onPause();
        wakeLock.release();
    }

    @Override
    public void onStop() {
        super.onStop();

        Analytics.trackIsConnected(connManager.isConnected());
        Analytics.endSession(this);
        Log.d("PiCat activity has been stopped");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("Finishing PiCat activity...");
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event_) {
        return touchController.onTouch(event_) || super.dispatchTouchEvent(event_);
    }

    private void setOnWindowChangeListeners() {
        connManager.setOnChangeWindowListener(new ConnectivityManager.OnChangeWindowListener() {
            public void onChange(String procName) {
                processWindowChange(procName);
            }
        });
    }

    private void processWindowChange(String procName) {
        Log.d("Active window changed to: " + procName);
        Analytics.trackReducedProcName(procName);
        //Are really active pc app has been changed?
        if (reallyProcNameChanged(procName)) {
            //If active pc app supported
            if (hotkeys.isSupportedApp(procName)) {
                //activeApp can be null. In this case PC app not supported;
                Application activeApp = hotkeys.getActiveApp();
                if (gridFragment != null) gridFragment.setActiveApp(activeApp);
                CharSequence appName = activeApp == null ? "" : activeApp.name;
                setTitle(appName);
                Analytics.trackSupportedApp(appName.toString());
            }
            //Show only proc name and set a blank fragment
            else {
                setTitle(procName);
                setBlankFragmentInsteadOfActiveApp();
            }
        }
    }

    private boolean reallyProcNameChanged(String procName_) {
        if (procName_.equalsIgnoreCase(prevProcName)) {
            return false;
        } else {
            prevProcName = procName_;
            return true;
        }
    }

    //TODO Сделать нормальную установку фрагмента для неподдерживаемых приложений. Просто за счет null сейчас показывается пустой фрагмент
    private void setBlankFragmentInsteadOfActiveApp() {
        if (gridFragment != null) gridFragment.setActiveApp(null);
    }

    private void showTitlesDialog() {

        String aboutTitle = String.format("About %s", getString(R.string.app_name));
        String versionString = String.format("Version: %s", getString(R.string.version));
        String aboutText = getString(R.string.about);

        final TextView message = new TextView(this);
        final SpannableString s = new SpannableString(aboutText);
        message.setPadding(5, 5, 5, 5);
        message.setText(versionString + "\n\n" + s);
        Linkify.addLinks(message, Linkify.ALL);

        new AlertDialog.Builder(this).
                setTitle(aboutTitle).
                setCancelable(true).
                setIcon(R.drawable.icon).
                setPositiveButton(this.getString(android.R.string.ok), null).
                setView(message).create().show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main, menu);

        // Calling super after populating the menu is necessary here to ensure that the
        // action bar helpers have a chance to handle this event.
        boolean ret = super.onCreateOptionsMenu(menu);
        return ret;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
            case R.id.menu_about:
                showTitlesDialog();
                break;

            case R.id.menu_feedback:
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_EMAIL, new String[]{"aleksey.masny@gmail.com"});
                i.putExtra(Intent.EXTRA_SUBJECT, "piHotKeys v" + getString(R.string.version));
                i.putExtra(Intent.EXTRA_TEXT, "Feel free to ask me any question\n");
                try {
                    startActivity(Intent.createChooser(i, "Send mail..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.menu_donate:
                openDonationActivity();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void openDonationActivity() {
        Intent donateIntent = new Intent(this, DonateActivity.class);
        startActivity(donateIntent);
    }

    private Dialog createDialog(int titleId, int messageId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(titleId).setIcon(android.R.drawable.stat_sys_warning).setMessage(messageId).setCancelable(
                false).setPositiveButton(android.R.string.ok, null);
        return builder.create();
    }

    public void setOnHardButtonPressedListener(IHardButtonPressed listener) {
        hardButtonListener = listener;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.d("key code " + keyCode);
        if (hardButtonListener != null) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_VOLUME_UP:
                    hardButtonListener.onVolumeUpPressed();
                    return true;
                case KeyEvent.KEYCODE_VOLUME_DOWN:
                    hardButtonListener.onVolumeDownPressed();
                    return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * Hide donation icon if user donate us some money
     */
    private void hideDonateIconIfAlreadyDonated() {
        boolean purchased1 = BillingController.isPurchased(this, PiApplication2.ITEM_ID_DONATE_1);
        boolean purchased3 = BillingController.isPurchased(this, PiApplication2.ITEM_ID_DONATE_3);
        boolean purchased5 = BillingController.isPurchased(this, PiApplication2.ITEM_ID_DONATE_5);
        if (purchased1 || purchased3 || purchased5) {
            getActionBarHelper().hideActionItem(R.id.menu_donate, true);
        }
    }

    /**
     * Make help for billing features on start up
     */
    private void donateBillingHelper() {
        //Restore transaction if user already bought our donation nad reinstall app
        BillingController.registerObserver(new AbstractBillingObserver(this) {
            @Override
            public void onBillingChecked(boolean supported) {
                if (supported) {
                    if (!this.isTransactionsRestored()) {
                        BillingController.restoreTransactions(PiCatActivity.this);
                        Log.d("Transaction restored!");
                    }
                }
            }

            @Override
            public void onPurchaseStateChanged(String itemId, Transaction.PurchaseState state) {
            }

            @Override
            public void onRequestPurchaseResponse(String itemId, BillingRequest.ResponseCode response) {
            }
        });
        BillingController.checkBillingSupported(this);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DIALOG_RATE_APP:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.dialog_rate_app_header).setIcon(R.drawable.icon).setMessage(R.string.dialog_rate_app_body).setCancelable(
                        false).setCancelable(true);
                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        new Analytics().trackShowRateDialog(true);
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=name.pilgr.android.picat")));
                    }
                });
                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        new Analytics().trackShowRateDialog(false);
                    }
                });
                return builder.create();
            default:
                return null;
        }
    }

    @Override
    public void onPinInserted(boolean flag) {
        if (flag) {
            fragmentManager.beginTransaction().replace(R.id.pad, pinFragment, FRAGMENT_PIN).commit();
            Log.d("NEW_CODE ---------------------------------------> PIN CORRECT!");
        } else {
            pinFragment.incorrectPin();
            Log.d("NEW_CODE ---------------------------------------> PIN INCORRECT!");
        }
    }

    private static final String FRAGMENT_CONNECTION = "connection_in_progress";

    @Override
    public void onConnectionAbsence() {
        fragmentManager.beginTransaction().replace(R.id.pad, connectionFragment, FRAGMENT_CONNECTION).commitAllowingStateLoss();
        Log.d("NEW_CODE ---------------------------------------> onConnectionAbsence!");
    }

    @Override
    public void onConnected() {
        //Replace by real grid fragment
        if (fragmentManager.findFragmentByTag(FRAGMENT_GRID) == null) {
            fragmentManager.beginTransaction().replace(R.id.pad, gridFragment, FRAGMENT_GRID).commitAllowingStateLoss();
        }
    }

    @Override
    public void onDisconnect() {
        //Replace by real grid fragment
        if (fragmentManager.findFragmentByTag(FRAGMENT_CONNECTION) == null) {
            fragmentManager.beginTransaction().replace(R.id.pad, connectionFragment, FRAGMENT_CONNECTION).commitAllowingStateLoss();
        }
    }
}
