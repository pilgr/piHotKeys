package name.pilgr.android.picat;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.bugsense.trace.BugSenseHandler;
import name.pilgr.android.picat.utils.Analytics;
import name.pilgr.android.picat.utils.PrivateKeys;
import net.robotmedia.billing.BillingController;
import net.robotmedia.billing.BillingRequest;
import net.robotmedia.billing.helper.AbstractBillingObserver;
import net.robotmedia.billing.model.Transaction;

public class DonateActivity extends ActionBarActivity {
    private static final int DIALOG_BILLING_NOT_SUPPORTED_ID = 2;
    private static final String TAG = "DonateActivity";

    private AbstractBillingObserver mBillingObserver;

    private Button btnDonate1, btnDonate3, btnDonate5;

    private Dialog createDialog(int titleId, int messageId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(titleId).setIcon(android.R.drawable.stat_sys_warning).setMessage(messageId).setCancelable(
                false).setPositiveButton(android.R.string.ok, null);
        return builder.create();
    }

    public void onBillingChecked(boolean supported) {
        if (supported) {
            restoreTransactions();
        } else {
            showDialog(DIALOG_BILLING_NOT_SUPPORTED_ID);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //To handle stack traces of unhandled exceptions
        BugSenseHandler.setup(this, PrivateKeys.BUGSENSE_API_KEY);

        setContentView(R.layout.donate);
        setupWidgets();

        mBillingObserver = new AbstractBillingObserver(this) {

            @Override
            public void onBillingChecked(boolean supported) {
                DonateActivity.this.onBillingChecked(supported);
            }

            @Override
            public void onPurchaseStateChanged(String itemId, Transaction.PurchaseState state) {
                DonateActivity.this.onPurchaseStateChanged(itemId, state);
            }

            @Override
            public void onRequestPurchaseResponse(String itemId, BillingRequest.ResponseCode response) {
                DonateActivity.this.onRequestPurchaseResponse(itemId, response);
            }
        };

        BillingController.registerObserver(mBillingObserver);
        BillingController.checkBillingSupported(this);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DIALOG_BILLING_NOT_SUPPORTED_ID:
                return createDialog(R.string.billing_not_supported_title, R.string.billing_not_supported_message);
            default:
                return null;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Analytics.startSession(this);
        Analytics.trackOpenDonateActivity();
    }

    @Override
    public void onStop() {
        super.onStop();
        Analytics.endSession(this);
    }

    @Override
    protected void onDestroy() {
        BillingController.unregisterObserver(mBillingObserver);
        super.onDestroy();
    }

    /**
     * Restores previous transactions, if any. This happens if the application
     * has just been installed or the user wiped data. We do not want to do this
     * on every startup, rather, we want to do only when the database needs to
     * be initialized.
     */
    private void restoreTransactions() {
        if (!mBillingObserver.isTransactionsRestored()) {
            BillingController.restoreTransactions(this);
            //Toast.makeText(this, R.string.restoring_transactions, Toast.LENGTH_LONG).show();
        }
    }

    public void onPurchaseStateChanged(String itemId, Transaction.PurchaseState state) {
        Log.i(TAG, "onPurchaseStateChanged() itemId: " + itemId + " state = " + state);
        Analytics.trackPurchaseStateChanges(itemId, state.toString());
        if (state == Transaction.PurchaseState.PURCHASED) {
            finish();
        }
    }

    public void onRequestPurchaseResponse(String itemId, BillingRequest.ResponseCode response) {
    }

    private void setupWidgets() {
        Typeface font = Typeface.createFromAsset(this.getAssets(), "HelveticaNeueLight.ttf");

        btnDonate1 = (Button) findViewById(R.id.btn_donate1);
        btnDonate3 = (Button) findViewById(R.id.btn_donate3);
        btnDonate5 = (Button) findViewById(R.id.btn_donate5);

        ((TextView) findViewById(R.id.lbl_donate)).setTypeface(font);
        ((TextView) findViewById(R.id.lbl_donate_message)).setTypeface(font);

        btnDonate1.setTypeface(font);
        btnDonate3.setTypeface(font);
        btnDonate5.setTypeface(font);

        btnDonate1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Analytics.trackClickDonateButton("$1");
                BillingController.requestPurchase(DonateActivity.this, PiApplication2.ITEM_ID_DONATE_1, true /* confirm */);
            }
        });

        btnDonate3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Analytics.trackClickDonateButton("$3");
                BillingController.requestPurchase(DonateActivity.this, PiApplication2.ITEM_ID_DONATE_3, true /* confirm */);
            }
        });

        btnDonate5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Analytics.trackClickDonateButton("$5");
                BillingController.requestPurchase(DonateActivity.this, PiApplication2.ITEM_ID_DONATE_5, true /* confirm */);
            }
        });
    }

}
