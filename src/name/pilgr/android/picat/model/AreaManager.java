package name.pilgr.android.picat.model;

import name.pilgr.android.picat.model.areas.Area;
import name.pilgr.android.picat.model.areas.WindowsArea;
import name.pilgr.android.picat.shared.EventSequence;

import java.util.List;
import java.util.Vector;

public class AreaManager {


    private boolean initialized;
    private OS os = OS.UNDEFINED;
    private List<Area> supportedAreas = new Vector<Area>();
    private Area osArea;
    private Area currentActiveArea = null;
    private AreaManagerListener listener;

    public void init(String osName_, String osVersion, String userName_) throws OsDoesNotSupportedException {
        if (osName_.contains("Windows")) {
            os = OS.WINDOWS;
            osArea = new WindowsArea(os);
        } else if (osName_.contains("Windows 7")) {
            os = OS.WINDOWS_7;
            osArea = new WindowsArea(os);
        } else {
            throw new OsDoesNotSupportedException(osName_);
        }

        initialized = true;
        if (listener != null) {
            listener.onInitialized(os);
        }
    }

    public void addSupportedArea(Area area_) {
        supportedAreas.add(area_);
    }

    public Area getCurrentActiveArea() {
        return currentActiveArea;
    }

    public void setActiveArea(String name_) {
        Area prevArea = currentActiveArea;
        boolean areaWasFound = false;

        //Find real supported area in the list
        for (Area area : supportedAreas) {
            if (area.isAppropriateByName(name_)) {
                currentActiveArea = area;
                areaWasFound = true;
                break;
            }
        }
        if (!areaWasFound) {
            listener.onAreaDoesNotFound();
            return;
        }

        if (listener != null && prevArea != currentActiveArea) {
            listener.onActiveAreaChanged(currentActiveArea);
        }
    }

    public EventSequence getEventSequence(String s) {
        //Find sequence in target area
        if (currentActiveArea != null) {
            return currentActiveArea.getEventSequence(s);
        }
        return null;
    }

    public EventSequence getOsEventSequence(String s) {
        //Find sequence in OS area
        if (osArea != null) {
            return osArea.getEventSequence(s);
        }
        return null;
    }

    public String getNameOfActiveArea() {
        return currentActiveArea.getName();
    }

    public void setOnActiveAreChangeListener(AreaManagerListener listener) {
        this.listener = listener;
    }

    public interface AreaManagerListener {
        void onActiveAreaChanged(Area area_);

        void onAreaDoesNotFound();

        void onInitialized(OS os);
    }
}
