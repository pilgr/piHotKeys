package name.pilgr.android.picat.model;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.XmlResourceParser;
import android.os.Environment;
import android.widget.Toast;
import name.pilgr.android.picat.utils.HotKeysHandler;
import name.pilgr.android.picat.utils.Log;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.*;
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
    public HashMap<String, Key> oskeys = new HashMap<String, Key>();
    public HashMap<String, Command> oscommands = new HashMap<String, Command>();
    private Context context;
    private final String PATH_TO_FILE = "/Android/data/name.pilgr.android.picat";
    private final String FILE_NAME = "hotkeys.xml";

    public Hotkeys(Context ctx) {
        context = ctx;
        InputStream currentStream = null;
        File hotKeys = new File(Environment.getExternalStorageDirectory() + PATH_TO_FILE + "/" + FILE_NAME);
        if (hotKeys.exists()) {
            try {
                currentStream = new FileInputStream(hotKeys);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            currentStream = getInputStreamFromAssets();
            createFileOnSDCard();
        }
        initHotKeys(currentStream);
    }

    private void initHotKeys(InputStream currentStream) {
        SAXParser parser = initSaxParser();
        HotKeysHandler handler = new HotKeysHandler();
        try {
            parser.parse(currentStream, handler);
            apps = handler.getApps();
            oskeys = handler.getOsKeys();
            oscommands = handler.getOsCommands();
            initialized = true;
        } catch (FileNotFoundException e) {
            Log.e("File not found", e);
        } catch (SAXException e) {
            Log.e("Incorrect XML file", e);
        } catch (IOException e) {
            Log.e("Can't read file from SD card", e);
        }
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

    /**
     * Gets data from assets folder
     *
     * @return - byte buffer with data
     */
    private byte[] getDataFromAssets() {
        try {
            InputStream input = getInputStreamFromAssets();
            int size = input.available();
            byte[] buffer = new byte[size];
            input.read(buffer);
            input.close();
            return buffer;
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("Can't read file from SD card", e);
            return null;
        }
    }

    /**
     * Get InputStream from assets file
     *
     * @return - input stream
     */
    private InputStream getInputStreamFromAssets() {
        AssetManager assetManager = context.getAssets();
        try {
            InputStream input = assetManager.open("hotkeys.xml");
            return input;
        } catch (IOException e) {
            Log.e("IOException", e);
        }
        return null;
    }

    private String pathToFile = null;

    /**
     * Create file on SD Card
     */
    private void createFileOnSDCard() {
        try {
            File packageName = new File(Environment.getExternalStorageDirectory() + PATH_TO_FILE);
            if (!packageName.exists()) {
                packageName.mkdirs();
            }
            File hotKeys = new File(packageName.getAbsolutePath() + "/" + FILE_NAME);
            if (!hotKeys.exists()) {
                hotKeys.createNewFile();
                FileOutputStream outputStream = new FileOutputStream(hotKeys);
                outputStream.write(getDataFromAssets());
                outputStream.flush();
                outputStream.close();
                pathToFile = hotKeys.getAbsolutePath();
            }
        } catch (FileNotFoundException e) {
            Log.e("FileNotFoundException", e);
        } catch (IOException e) {
            Log.e("IOException", e);
        }
    }

    private SAXParser initSaxParser() {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        try {
            return factory.newSAXParser();
        } catch (ParserConfigurationException e) {
            Log.e("ParserConfigurationException", e);
        } catch (SAXException e) {
            Log.e("SAXException", e);
        }
        return null;
    }
}
