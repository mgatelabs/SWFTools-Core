package com.mgatelabs.swftools.support.xml;

import java.io.*;
import java.util.Vector;
import java.util.zip.GZIPOutputStream;

public class XMLNode {
    public XMLNode next, sub;
    private XMLNode lastPeer;
    public Vector properties;
    public String title;

    ////////////////////////////////////////////////////////////////////////////

    public XMLNode(String title) {
        this.title = title;
        next = null;
        sub = null;
        lastPeer = null;
        properties = new Vector();
    }

    public void addChild(XMLNode aChild) {
        if (sub == null) {
            sub = aChild;

        } else {
            sub.addPeer(aChild);
        }
    }

    public void addPeer(XMLNode aPeer) {
        XMLNode temp = this;

        if (lastPeer != null) {
            temp = lastPeer;
        }

        while (temp.next != null) {
            temp = temp.next;
        }

        temp.next = aPeer;
        lastPeer = aPeer;
    }

    ////////////////////////////////////////////////////////////////////////////

    public static boolean dump(File target, String doctype, XMLNode root, boolean prettyOuput) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(target));
            bw.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
            bw.write(doctype);
            root.write(root, bw, (prettyOuput ? 0 : -1));
            bw.flush();
            bw.close();
            return true;
        } catch (Exception e) {

        }
        return false;
    }

    ////////////////////////////////////////////////////////////////////////////

    public static boolean dumpCompressedGZip(File target, String doctype, XMLNode root) {
        try {
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new GZIPOutputStream(new FileOutputStream(target))));
            bw.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
            bw.write(doctype);
            root.write(root, bw, -1);
            bw.flush();
            bw.close();
            return true;
        } catch (Exception e) {

        }
        return false;
    }

    ////////////////////////////////////////////////////////////////////////////

    public void addProperty(String name, String data) {
        properties.add(new DualString(name, data));
    }

    ////////////////////////////////////////////////////////////////////////////

    public String getProperty(String name) {
        for (int x = 0; x < properties.size(); x++) {
            DualString ds = (DualString) properties.get(x);
            if (ds.key.equals(name)) {
                return ds.value;
            }
        }
        return null;
    }

    public String getTitle() {
        return title;
    }

    ////////////////////////////////////////////////////////////////////////////

    public static void write(XMLNode rNode, BufferedWriter bw, int depth) throws Exception {

        XMLNode temp = rNode;

        while (temp != null) {

            if (depth != -1) {
                for (int x = 0; x < depth; x++) {
                    bw.write("\t");
                }
            }

            bw.write("<");
            bw.write(temp.title);

            for (int x = 0; x < temp.properties.size(); x++) {
                DualString prop = (DualString) temp.properties.elementAt(x);
                bw.write(" " + prop.key + "=\"" + prop.value + "\"");
            }

            if (temp.sub == null) {
                bw.write("/");
            }
            bw.write(">\n");

            if (temp.sub != null) {
                temp.sub.write(temp.sub, bw, ((depth == -1) ? -1 : depth + 1));

                if (depth != -1) {
                    for (int x = 0; x < depth; x++) {
                        bw.write("\t");
                    }
                }

                bw.write("</" + temp.title + ">\n");
            }

            temp = temp.next;
        }
    }

    ////////////////////////////////////////////////////////////////////////////

    private class DualString {
        public String key, value;

        public DualString(String key, String value) {
            this.key = key;
            this.value = value;
        }
    }
}