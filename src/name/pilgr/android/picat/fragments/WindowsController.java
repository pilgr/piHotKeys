package name.pilgr.android.picat.fragments;

import name.pilgr.android.picat.model.areas.WindowsArea;

public class WindowsController extends CommonController {

    public WindowsController(OnActionListener actionListener_) {
        super(WindowsArea.class, actionListener_);
    }

    @Override
    public void onSwipeRight(float velocity){

    }

    @Override
    public void onTwoFingerDown(){
        //doAction(WindowsArea.SWITCH_WINDOW);
    }
}
