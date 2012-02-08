package name.pilgr.android.picat.utils;

import android.view.GestureDetector;
import android.view.MotionEvent;
import name.pilgr.android.picat.utils.Log;

public class CommonGestureListener extends GestureDetector.SimpleOnGestureListener {
    private static final int SWIPE_MIN_DISTANCE = 75;
    private static final int SWIPE_MAX_OFF_PATH = 250;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;

    CustomGestureListener listener;

    public CommonGestureListener(CustomGestureListener listener_) {
        listener = listener_;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        try {
            /*if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
                return false;*/
            // right to left swipe
            //Swipe more by X or by Y axis?
            if (Math.abs(e1.getX() - e2.getX()) > Math.abs(e1.getY() - e2.getY())) {
                //by X
                if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE &&
                        Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    listener.swipeLeft(Math.abs(velocityX));

                } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE &&
                        Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    listener.swipeRight(Math.abs(velocityX));

                }
            } else {
                //by Y
                if (e1.getY() - e2.getY() > SWIPE_MIN_DISTANCE &&
                        Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
                    listener.swipeUp(Math.abs(velocityY));

                } else if (e2.getY() - e1.getY() > SWIPE_MIN_DISTANCE &&
                        Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
                    listener.swipeDown(Math.abs(velocityY));
                }
            }
        } catch (Exception e) {
            // nothing
        }
        return false;
    }

    @Override
    public boolean onScroll(android.view.MotionEvent e1, android.view.MotionEvent e2, float distanceX, float distanceY) {
        //Scroll horizontally
        DumpEvents.dumpEvent(e2);
        float absX, absY;
        absX = Math.abs(distanceX);
        absY = Math.abs(distanceY);
        if ( absX > absY) {
            if (distanceX > 0 ){
                listener.scrollLeft(absX);
            }else{
                listener.scrollRight(absX);
            }
        }
        //Scroll vertically
        else {
            if (distanceY > 0 ){
                listener.scrollUp(absY);
            }else{
                listener.scrollDown(absY);
            }
        }

        return false;
    }

    public interface CustomGestureListener {
        void swipeLeft(float velocity);

        void swipeRight(float velocity);

        void swipeUp(float velocity);

        void swipeDown(float velocity);

        public void scrollUp(float distance);

        public void scrollDown(float distance);

        public void scrollLeft(float distance);

        public void scrollRight(float distance);

    }

}
