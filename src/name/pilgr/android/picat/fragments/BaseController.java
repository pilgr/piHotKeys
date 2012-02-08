package name.pilgr.android.picat.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import name.pilgr.android.picat.model.areas.Area;
import name.pilgr.android.picat.utils.CommonGestureListener;

public abstract class BaseController extends Fragment {
    private OnActionListener actionListener;
    private Class supportedAreaClass;
    private GestureDetector gestureDetector;

    public BaseController(Class supportedAreaClass_, OnActionListener actionListener_) {
        supportedAreaClass = supportedAreaClass_;
        actionListener = actionListener_;

        gestureDetector = new GestureDetector(
                new CommonGestureListener(new CommonGestureListener.CustomGestureListener() {

                    public void swipeLeft(float velocity) {
                        onSwipeLeft(velocity);
                    }

                    public void swipeRight(float velocity) {
                        onSwipeRight(velocity);
                    }

                    public void swipeUp(float velocity) {
                        onSwipeUp(velocity);
                    }

                    public void swipeDown(float velocity) {
                        onSwipeDown(velocity);
                    }

                    public void scrollUp(float distance) {
                        onScrollUp(distance);
                    }

                    public void scrollDown(float distance) {
                        onScrollDown(distance);
                    }

                    public void scrollLeft(float distance) {
                        onScrollLeft(distance);
                    }

                    public void scrollRight(float distance) {
                        onScrollRight(distance);
                    }
                }
                ));
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setActionListener();
    }

    public abstract void onScrollUp(float distance);

    public abstract void onScrollDown(float distance);

    abstract void onOneFingerDown();

    abstract void onOneFingerUp();

    public abstract void onScrollLeft(float distance);

    public abstract void onScrollRight(float distance);

    public abstract void onSwipeUp(float velocity_);

    public abstract void onSwipeDown(float velocity_);

    public abstract void onSwipeLeft(float velocity_);

    public abstract void onSwipeRight(float velocity_);

    protected void setOnClickAction(View view_, final String action_) {
        if (view_ != null && action_ != null) {
            view_.setOnClickListener(new View.OnClickListener() {

                public void onClick(View view_) {
                    if (actionListener != null) {
                        actionListener.onAction(action_);
                    }
                }
            });
        }
    }

    protected void doAction(String action) {
        actionListener.onAction(action);
    }

    abstract void setActionListener();

    public boolean support(Area area_) {
        return area_.getClass() == supportedAreaClass;
    }

    public interface OnActionListener {
        public void onAction(String action_);
    }

    public void dispatchTouchEvent(MotionEvent event_) {
        //DumpEvents.dumpEvent(event_);
        int action = event_.getAction() & MotionEvent.ACTION_MASK;
        int p = event_.getPointerCount();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (p == 1) {
                    onOneFingerDown();
                }
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (p == 1) {
                    onOneFingerUp();
                }
                break;

            case MotionEvent.ACTION_POINTER_DOWN:
                if (p == 2) onTwoFingerDown();
                break;

            case MotionEvent.ACTION_POINTER_UP:
                if (p == 2) onTwoFingerUp();
                break;

            case MotionEvent.ACTION_MOVE:
                //if (p == 2) onTwoFingerMove();
                break;
            default:
                break;
        }

        gestureDetector.onTouchEvent(event_);
    }

    protected abstract void onTwoFingerDown();

    protected abstract void onTwoFingerUp();

}


