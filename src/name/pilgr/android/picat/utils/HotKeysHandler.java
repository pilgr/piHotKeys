package name.pilgr.android.picat.utils;

import name.pilgr.android.picat.model.Application;
import name.pilgr.android.picat.model.Command;
import name.pilgr.android.picat.model.Key;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Roman Shemshei
 * Date: 29.02.12
 * Time: 18:06
 */
public class HotKeysHandler extends DefaultHandler {

    private List<Application> apps = new ArrayList<Application>();
    private HashMap<String, Key> osKeys = new HashMap<String, Key>();
    private HashMap<String, Command> osCommands = new HashMap<String, Command>();
    private Application app = null;

    @Override
    public void startDocument() throws SAXException {
        super.startDocument();
    }

    @Override
    public void endDocument() throws SAXException {
        super.endDocument();
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if (qName.equalsIgnoreCase("application")) {
            // If current tag correspond to active mobile operator
            app = new Application();
            app.id = attributes.getValue("id");
            app.name = attributes.getValue("name");
            app.procname = attributes.getValue("procname");

            if (app.name == null) {
                Log.e("[hotkeys.xml] Tag 'name' not specified for app id " + app.id);
                app.name = "";
            }
            if (app.procname == null) {
                Log.e("[hotkeys.xml] Tag 'procname' not specified for app id " + app.id);
                app.procname = "";
            }
        } else if (qName.equalsIgnoreCase("hotkey")) {
            Key key = new Key();
            key.id = attributes.getValue("id");
            key.shortcut = attributes.getValue("shortcut");
            key.label = attributes.getValue("label");
            // PiApplication shortcut
            if (app != null) {
                app.keys.add(key);
                // OS specific shortcut
            } else {
                osKeys.put(key.id, key);
            }
        } else if (qName.equalsIgnoreCase("command")) {
            Command cmd = new Command(attributes.getValue("text"));
            osCommands.put(attributes.getValue("id"), cmd);
        } else if (qName.equalsIgnoreCase("hotbtn")) {
            Key key = new Key();
            key.id = attributes.getValue("id");
            key.shortcut = attributes.getValue("shortcut");
            key.label = attributes.getValue("label");
            if (app != null) {
                app.buttons.put(key.id, key);
            }
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (qName.equalsIgnoreCase("application")) {
            apps.add(app);
            app = null;
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        super.characters(ch, start, length);
    }

    public List<Application> getApps() {
        return apps;
    }

    public HashMap<String, Key> getOsKeys() {
        return osKeys;
    }

    public HashMap<String, Command> getOsCommands() {
        return osCommands;
    }
}
