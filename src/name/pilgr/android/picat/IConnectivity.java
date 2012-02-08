package name.pilgr.android.picat;

import com.esotericsoftware.kryonet.EndPoint;

public interface IConnectivity {
    EndPoint getEndpoint();

    boolean establishConnection();

    boolean isConnected();

    void closeConnection();

    void setUncaughtExceptionHand(Thread.UncaughtExceptionHandler handler);
}
