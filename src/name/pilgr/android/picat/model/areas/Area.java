package name.pilgr.android.picat.model.areas;

import name.pilgr.android.picat.model.OS;
import name.pilgr.android.picat.shared.EventSequence;

public abstract class Area {

    private OS os = OS.UNDEFINED;
    private String name;

    public Area(String name_, OS os_) {
        name = name_;
        os = os_;
    }

    /**
     * @param name_ The name of window header
     * @return true, if window header consist this traget area name
     */
    public boolean isAppropriateByName(String name_) {
        return name_.contains(name);
    }

    public abstract EventSequence getEventSequence(String name);

    public String getName() {
        return name;
    }

    public OS getOs() {
        return os;
    }

}
