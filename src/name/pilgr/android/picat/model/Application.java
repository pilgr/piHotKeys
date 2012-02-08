package name.pilgr.android.picat.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: pilgr
 * Date: 09.06.11
 * Time: 21:19
 * To change this template use File | Settings | File Templates.
 */
public class Application {
    public String name = "";
    public int id = 0;
    public List<Key> keys = new ArrayList<Key>();
    public HashMap<Integer, Key> buttons = new HashMap<Integer, Key>();
    public String procname = "";
}