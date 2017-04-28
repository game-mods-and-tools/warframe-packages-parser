import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Hashtable;
import java.lang.StringBuilder;
import java.util.Map;

class ObjectThing {
    private Hashtable < String, Object > info = new Hashtable < String, Object > ();
    private ObjectThing parent;
    private int unnamedVals = 0;

    public ObjectThing() {
        this.parent = null;
    }

    public ObjectThing(ObjectThing parent) {
        this.parent = parent;
    }

    public ObjectThing getParent() {
        return parent;
    }

    public void insert(String s, Object obj) {
        info.put(s, obj);
    }

    public String newKey() {
        return Integer.toString(unnamedVals++);
    }

    public int getKey() {
        return unnamedVals;
    }

    public String toString() {
        return toString(0);
    }

    private String toString(int spaces) {
        String indent = "";
        for (int i = 0; i < spaces; i++) {
            indent += " ";
        }
        StringBuilder sb = new StringBuilder();
        for (Map.Entry < String, Object > entry: info.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            sb.append(indent + "ENTRY: " + key);
            sb.append("\t");
            if (value instanceof ObjectThing) {
                sb.append("\n");
                sb.append(((ObjectThing) value).toString(spaces + 2));
            } else sb.append(value.toString());
            sb.append("\n");
        }
        return sb.toString();
    }
}

public class PackageScanner {

    private BufferedReader file;
    private ObjectThing rootObject = new ObjectThing();

    public PackageScanner(String filename) throws FileNotFoundException, IOException {
        file = new BufferedReader(new FileReader(new File(filename)));
        Parse();
    }

    public String Parse() throws IOException {
        int s;
        ObjectThing current = rootObject;

        StringBuilder sb = new StringBuilder();
        String key = "";
        Object value = null;
        int depth = 0;
        while ((s = file.read()) != -1) {
            switch (s) {
                case 0x3D: // equals, so we can get the key from what we already read
                    key = sb.toString();
                    sb.setLength(0);
                    break;
                case 0x7D: // close bracket
                    if (sb.length() > 0) {
                        if (current.getKey() > 0) key = current.newKey();
                        value = sb.toString().trim();
                        // System.out.printf("[%d] Insert2:\t%s\t%s\n", depth, key, value instanceof ObjectThing ? "OBJTHING" : value);
                        current.insert(key, value);
                    }
                    key = "";
                    value = null;
                    sb.setLength(0);
                    depth--;
                    current = current.getParent();
                    break;
                case 0x7B: // open bracket, this counts as the start and end of a value because it will be an object
                case 0x2C: // comma, be careful about in strings
                case 0x0A: // new line
                case 0x0D: // carriage return, these count as the end of a value
                    if (s == 0x7B) value = new ObjectThing(current);
                    else value = sb.toString().trim();
                    // key value or make key its own value
                    if (!key.equals("") || !value.equals("")) {
                        if (s == 0x2C) key = current.newKey();
                        else if (key.equals("") && value instanceof String) key = (String) value;
                        else if (key.equals("")) key = current.newKey();
                        System.out.printf("[%d] Insert1:\t%s\t%s\n", depth, key, value instanceof ObjectThing ? "OBJTHING" : value);
                        current.insert(key, value);
                    }
                    if (s == 0x7B) {
                        current = (ObjectThing) value;
                        depth++;
                    }
                    key = "";
                    value = null;
                    sb.setLength(0);
                    break;
                default: // something acceptable aka a letter
                    if (0x20 < s && s < 0x7F) {
                        sb.append((char) s);
                    }
            }

        }
        return "asd";
    }

    public String toString() {
        return rootObject.toString();
    }
}