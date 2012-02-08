package name.pilgr.android.picat.fragments;

/**
 * Empty base controller
 */
public class CommonController extends BaseController {

    public CommonController(Class supportedAreaClass_, OnActionListener actionListener_) {
        super(supportedAreaClass_, actionListener_);
    }

    @Override
    public void onScrollUp(float distance) {
    }

    @Override
    public void onScrollDown(float distance) {
    }

    @Override
    void onOneFingerDown() {
    }

    @Override
    void onOneFingerUp() {
    }

    @Override
    public void onScrollLeft(float distance) {
    }

    @Override
    public void onScrollRight(float distance) {
    }

    @Override
    public void onSwipeUp(float velocity_) {
    }

    @Override
    public void onSwipeDown(float velocity_) {
    }

    @Override
    public void onSwipeLeft(float velocity_) {
    }

    @Override
    public void onSwipeRight(float velocity_) {
    }

    @Override
    void setActionListener() {

    }

    @Override
    protected void onTwoFingerDown() {
    }

    @Override
    protected void onTwoFingerUp() {
    }


}
