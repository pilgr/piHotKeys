package name.pilgr.android.picat.model.areas;

import name.pilgr.android.picat.model.OS;
import name.pilgr.android.picat.shared.EventSequence;

import static name.pilgr.android.picat.shared.KeyEvent.*;

public class ChromeArea extends Area {

    public static final String SWITCH_TO_NEXT_TAB = "next-tab";
    public static final String SWITCH_TO_PREVIOUS_TAB = "previous-tab";

    public ChromeArea() {
        super("Google Chrome", OS.UNDEFINED);
    }

    @Override
    public EventSequence getEventSequence(String name) {

        if (name.equalsIgnoreCase(SWITCH_TO_NEXT_TAB)) {
            return new EventSequence().
                    press(VK_CONTROL).press(VK_TAB).
                    release(VK_TAB).release(VK_CONTROL);

        } else if (name.equalsIgnoreCase(SWITCH_TO_PREVIOUS_TAB)) {
            return new EventSequence().
                    press(VK_CONTROL).press(VK_SHIFT).press(VK_TAB).
                    release(VK_TAB).release(VK_SHIFT).release(VK_CONTROL);

        } else {
            return null;
        }
    }
}
