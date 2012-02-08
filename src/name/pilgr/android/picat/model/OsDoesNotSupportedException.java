package name.pilgr.android.picat.model;

public class OsDoesNotSupportedException extends Exception {

    public OsDoesNotSupportedException(String osName_) {
        super(osName_);
    }
}
