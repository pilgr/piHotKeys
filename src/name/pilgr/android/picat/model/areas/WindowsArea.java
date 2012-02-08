package name.pilgr.android.picat.model.areas;

import name.pilgr.android.picat.model.OS;
import name.pilgr.android.picat.shared.EventSequence;

import static name.pilgr.android.picat.shared.KeyEvent.*;

public class WindowsArea extends Area {

    public static final String SWITCH_WINDOW = "switch-window";
    public static final String SCROLL_UP = "scroll-up";
    public static final String SCROLL_DOWN = "scroll-down";
    public static final String SWITCH_WINDOW_START = "switch-window-start";
    public static final String SWITCH_WINDOW_NEXT = "switch-window-next";
    public static final String SWITCH_WINDOW_PREV = "switch-window-prev";
    public static final String SWITCH_WINDOW_FINISH = "switch-window-finish";

    public WindowsArea(OS os_) {
        super("WindowsArea", os_);
    }

    @Override
    public EventSequence getEventSequence(String name) {
        if (name.equalsIgnoreCase(SWITCH_WINDOW)) {
            return new EventSequence().
                    press(VK_WINDOWS).
                    press(VK_TAB).delay(100).
                    release(VK_TAB).delay(300).
                    press(VK_TAB).delay(300).
                    release(VK_TAB).delay(100).
                    release(VK_WINDOWS);
        } else if (name.equalsIgnoreCase(SCROLL_UP)) {
            return new EventSequence().wheel(-1);

        } else if (name.equalsIgnoreCase(SCROLL_DOWN)) {
            return new EventSequence().wheel(1);

            //Event specific commands
        } else if (name.equalsIgnoreCase(SWITCH_WINDOW_START)) {
            return new EventSequence().
                    press(VK_WINDOWS).press(VK_TAB).delay(100).
                    release(VK_TAB);
        } else if (name.equalsIgnoreCase(SWITCH_WINDOW_NEXT)) {
            return new EventSequence().
                    press(VK_TAB).delay(100).
                    release(VK_TAB);
        } else if (name.equalsIgnoreCase(SWITCH_WINDOW_PREV)) {
            return new EventSequence().
                    press(VK_SHIFT).press(VK_TAB).delay(100).
                    release(VK_TAB).release(VK_SHIFT);
        } else if (name.equalsIgnoreCase(SWITCH_WINDOW_FINISH)) {
            return new EventSequence().
                    release(VK_TAB);
        } else {
            return null;
        }
    }


}
