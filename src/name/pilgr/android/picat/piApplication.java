package name.pilgr.android.picat;

import name.pilgr.android.picat.utils.PrivateKeys;
import net.robotmedia.billing.BillingController;

public class piApplication extends android.app.Application {
    public final static String ITEM_ID_DONATE_1 = "name.pilgr.android.picat.donate1";
    public final static String ITEM_ID_DONATE_3 = "name.pilgr.android.picat.donate3";
    public final static String ITEM_ID_DONATE_5 = "name.pilgr.android.picat.donate5";
    private ConnectivityManager connectivityManager;

    @Override
    public void onCreate() {
        super.onCreate();
        BillingController.setDebug(true);
        BillingController.setConfiguration(new BillingController.IConfiguration() {

            @Override
            public byte[] getObfuscationSalt() {
                return new byte[]{31, -92, -116, -44, 66, -33, 122, -110, -107, -96, -78, 77, 125, 112, 5, 63, 57, 115, 48, -111};
            }

            @Override
            public String getPublicKey() {
                return PrivateKeys.MARKET_IN_APP_PURCHASE_KEY;
            }
        });
    }

    public ConnectivityManager getConnectivityManager() {
        return connectivityManager;
    }

    public void setConnectivityManager(ConnectivityManager manager) {
        connectivityManager = manager;
    }

}