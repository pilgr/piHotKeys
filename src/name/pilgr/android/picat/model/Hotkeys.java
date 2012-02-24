package name.pilgr.android.picat.model;

import android.content.Context;
import android.content.res.XmlResourceParser;
import name.pilgr.android.picat.utils.Log;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: pilgr
 * Date: 09.06.11
 * Time: 17:58
 * To change this template use File | Settings | File Templates.
 */
public class Hotkeys {
    private List<Application> apps = new ArrayList<Application>();
    private boolean initialized = false;
    private Application activeApp = null;
    public HashMap<Integer, Key> oskeys = new HashMap<Integer, Key>();
    public HashMap<Integer, Command> oscommands = new HashMap<Integer, Command>();

    public Hotkeys(Context ctx, int xmlResourceId) throws IOException, XmlPullParserException {
        XmlResourceParser xrp = ctx.getResources().getXml(xmlResourceId);
        initFromXml(xrp);
    }

    private void initFromXml(XmlResourceParser xrp) throws XmlPullParserException,
            IOException {
        if (xrp == null) return;

        xrp.next();
        int eventType = xrp.getEventType();
        //by the all document
        Application app = null;
        while (eventType != XmlPullParser.END_DOCUMENT) {
            // application tag
            if (eventType == XmlPullParser.START_TAG
                    && xrp.getName().equalsIgnoreCase("application")) {
                // If current tag correspond to active mobile operator
                app = new Application();
                app.id = xrp.getIdAttributeResourceValue(0);
                app.name = xrp.getAttributeValue(null, "name");
                app.procname = xrp.getAttributeValue(null, "procname");

                if (app.name == null) {
                    Log.e("[hotkeys.xml] Tag 'name' not specified for app id " + app.id);
                    app.name = "";
                }
                if (app.procname == null) {
                    Log.e("[hotkeys.xml] Tag 'procname' not specified for app id " + app.id);
                    app.procname = "";
                }

                apps.add(app);
            }
            //hotkey tag
            if (eventType == XmlPullParser.START_TAG) {
                if (xrp.getName().equalsIgnoreCase("hotkey")) {
                    //
                    Key key = new Key();
                    key.id = xrp.getIdAttributeResourceValue(0);
                    key.shortcut = xrp.getAttributeValue(null, "shortcut");
                    key.label = xrp.getAttributeValue(null, "label");
                    // piApplication shortcut
                    if (app != null) {
                        app.keys.add(key);
                        // OS specific shortcut
                    } else {
                        oskeys.put(key.id, key);
                    }
                } else if (xrp.getName().equalsIgnoreCase("command")) {
                    //
                    Command cmd = new Command(xrp.getAttributeValue(null, "text"));
                    oscommands.put(xrp.getIdAttributeResourceValue(0), cmd);
                }
                //hotkeys for hardware buttons
                else if (xrp.getName().equalsIgnoreCase("hotbtn")) {
                    Key key = new Key();
                    key.id = xrp.getIdAttributeResourceValue(0);
                    key.shortcut = xrp.getAttributeValue(null, "shortcut");
                    key.label = xrp.getAttributeValue(null, "hardware button");
                    if (app != null) {
                        app.buttons.put(key.id, key);
                    }
                }
            }
            eventType = xrp.next();
        }
        initialized = true;
    }

    public boolean isInitialized() {
        return initialized;
    }

    public Application getActiveApp() {
        if (!initialized) return null;
        return activeApp;
    }

    /**
     * Set application by new window header
     *
     * @param procName
     * @return true, if active application really has been changed
     */
    public boolean isSupportedApp(String procName) {
        if (!initialized) return false;

        //Search new application
        Application foundApp = null;
        for (Application app : apps) {
            if (procName != null && procName.equalsIgnoreCase(app.procname)) {
                foundApp = app;
            }
        }

        activeApp = foundApp;

        return foundApp != null;
    }
}
