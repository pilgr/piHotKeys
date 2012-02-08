package name.pilgr.android.picat.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.*;
import name.pilgr.android.picat.ConnectivityManager;
import name.pilgr.android.picat.R;
import name.pilgr.android.picat.shared.EventSequence;
import name.pilgr.android.picat.utils.Log;

public class TouchPadFragment extends Fragment {

    private ConnectivityManager connManager;
    private GestureDetector gestureDetector;
    private View.OnTouchListener gestureListener;
    private float scale;
    //Formula to calculate sensitive (copy&past this to WolframAlpha)
    //Plot[1 + (x*0.003)^2, {x, -500, 500}]
    private static final double SENSITIVE_TO_SPEED = 0.001 * 1.5;
    private static final double SENSITIVE_BASE = 1.0f;
    private static final double SENSITIVE_THRESHOLD_MIN = 60;
    private static final double SENSITIVE_THRESHOLD_MAX = 1000;

    public TouchPadFragment(ConnectivityManager connManager_) {
        this.connManager = connManager_;

        // Gesture detection
        gestureDetector = new GestureDetector(new MyGestureDetector());
        gestureListener = new View.OnTouchListener() {
            long prevTimeStamp;
            int prevX;
            int prevY;

            public boolean onTouch(View v, MotionEvent event) {
                long t = System.currentTimeMillis();
                double dt = (t - prevTimeStamp) / 1000.0f;
                int x = pxToDp(event.getX());
                int y = pxToDp(event.getY());

                //Speed meter
                switch (event.getAction()) {
                    case MotionEvent.ACTION_MOVE:
                        //speed in dp per one sec
                        double currSpeed = Math.sqrt(Math.pow(x - prevX, 2) + Math.pow((y - prevY), 2)) / dt;
                        //Log.d("Touchpad speed = " + currSpeed);
                        int dx = makeSensetive(x - prevX, currSpeed);
                        int dy = makeSensetive(y - prevY, currSpeed);

                        //connManager.sendMessage(new EventSequence().mouseMove(dx, dy));
                }

                prevTimeStamp = t;
                prevX = x;
                prevY = y;

                gestureDetector.onTouchEvent(event);
                return true;
            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        scale = getActivity().getResources().getDisplayMetrics().density;
        View v = inflater.inflate(R.layout.touchpad_fragment, container, false);
        v.setOnTouchListener(gestureListener);
        return v;
    }


    class MyGestureDetector extends GestureDetector.SimpleOnGestureListener {
        /*@Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            int dx = pxToDp(-distanceX);
            int dy = pxToDp(-distanceY);

            *//*dx = makeSensetive(dx, currSpeedX);
            dy = makeSensetive(dy, currSpeedX);*//*


            //Log.d("dx=" + dx + " dy=" + dy);
            connManager.sendMessage(new EventSequence().mouseMove(dx, dy));
            return true;
        }*/

        public boolean onSingleTapConfirmed(MotionEvent e) {
            leftClick();
            return true;
        }

        public boolean onDoubleTap(MotionEvent e) {
            doubleLeftClick();
            return true;
        }

    }

    private void leftClick() {
        //connManager.sendMessage(new EventSequence().mousePress(1).mouseRelease(1));
    }

    private void doubleLeftClick() {
        //connManager.sendMessage(new EventSequence().mousePress(1).mouseRelease(1).mousePress(1).mouseRelease(1));
    }

    private int pxToDp(float px) {
        return (int) ((px - 0.5f) / scale);
    }

    private int makeSensetive(int value, double speed) {
        //Plot[1 + (x*0.003)^2, {x, -500, 500}]
        double m;
        if (speed > SENSITIVE_THRESHOLD_MIN) {
            m = SENSITIVE_BASE + Math.pow(Math.abs(speed) * SENSITIVE_TO_SPEED, 2);
        } else if (speed > SENSITIVE_THRESHOLD_MAX) {
            m = 4;
        } else {
            m = 1;
        }
        //Log.d("m = " + m);
        return (int) (value * m);
    }

}
