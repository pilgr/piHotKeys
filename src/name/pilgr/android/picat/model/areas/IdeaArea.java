package name.pilgr.android.picat.model.areas;

import name.pilgr.android.picat.model.OS;
import name.pilgr.android.picat.shared.EventSequence;

import static name.pilgr.android.picat.shared.KeyEvent.*;

public class IdeaArea extends Area {

    private static final String RUN = "run-app";

    public IdeaArea() {
        super("IntelliJ IDEA", OS.UNDEFINED);
    }

    @Override
    public EventSequence getEventSequence(String name) {
        if (name.equalsIgnoreCase(RUN)) {
            return new EventSequence().press(VK_SHIFT).press(VK_F10).
                    release(VK_F10).release(VK_SHIFT);
        }
        return null;
    }
}
