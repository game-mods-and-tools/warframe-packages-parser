import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Hashtable;
import java.lang.StringBuilder;
import java.util.Map;
import java.util.Iterator;

class ObjectThing {
    // hashtable to store key value things
    private Hashtable < String, Object > info = new Hashtable < String, Object > ();
    // because these objects can be nested
    private ObjectThing parent;
    // for handling unnamed things
    private int unnamedVals = 0;

    // the root so to speak
    public ObjectThing() {
        this.parent = null;
    }

    // if you make a nested one
    public ObjectThing(ObjectThing parent) {
        this.parent = parent;
    }

    // to go up the nesting
    public ObjectThing getParent() {
        return parent;
    }

    // insert into table
    public void insert(String s, Object obj) {
        info.put(s, obj);
    }

    // get a new key for an unnamed thing
    public String newKey() {
        return Integer.toString(unnamedVals++);
    }

    // see if you used any unneeded keys
    public int getKey() {
        return unnamedVals;
    }

    // tostring method wrapper
    public String toString() {
        return "{\n" + toString(1) + "\n}"; // outer wrap
    }

    // actual work
    private String toString(int spaces) {
        String indent = "";
        for (int i = 0; i < spaces; i++) {
            indent += "\t";
        }
        StringBuilder sb = new StringBuilder();
        Iterator < Map.Entry < String, Object >> it = info.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry < String, Object > entry = it.next();
            String key = entry.getKey();
            Object value = entry.getValue();
            sb.append(indent + "\"" + key.replace("\"", "\\\"") + "\": "); // escape keys just in case
            if (value instanceof ObjectThing) {
                sb.append("{\n" + ((ObjectThing) value).toString(spaces + 1) + indent + "}"); // object will be on new line and indented
            } else {
                String sValue = value.toString(); // could reuse key here tbh
                if (sValue.matches("-?\\d+(\\.\\d+)?")) sb.append(Double.valueOf(sValue)); // keep numbers numbers
                else sb.append("\"" + sValue.replace("\"", "\\\"") + "\""); // else string
            }
            if (it.hasNext()) sb.append(",");
            sb.append("\n"); // always need a nice new line
        }
        return sb.toString();
    }
}

public class PackageScanner {

    private BufferedReader file;
    private ObjectThing rootObject = new ObjectThing();

    // load file and parse asap
    public PackageScanner(String filename) throws FileNotFoundException, IOException {
        file = new BufferedReader(new FileReader(new File(filename)));
        Parse(); // hmm
    }

    public void Parse() throws IOException {
        int s; // character being read
        ObjectThing current = rootObject; // the table we're inserting into

        StringBuilder sb = new StringBuilder(); // a stringbuilder for mutable junk
        String key = ""; // the key
        Object value = null; // the value
        // int depth = 0; // how deep we are (not necessary really)
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
                    // depth--;
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
                        else if (key.equals("")) key = current.newKey();
                        // System.out.printf("[%d] Insert1:\t%s\t%s\n", depth, key, value instanceof ObjectThing ? "OBJTHING" : value);
                        current.insert(key, value);
                    }
                    if (s == 0x7B) {
                        current = (ObjectThing) value;
                        // depth++;
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
    }

    // tostring method
    public String toString() {
        return rootObject.toString();
    }
}
