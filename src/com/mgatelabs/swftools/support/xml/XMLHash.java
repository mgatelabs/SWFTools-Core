package com.mgatelabs.swftools.support.xml;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Hashtable;

public class XMLHash {

    public XMLHash() {

    }

    ////////////////////////////////////////////////////////////////////////////

    public Hashtable read(InputStream is) {
        try {
            Hashtable hash = new Hashtable();

            XML_Reader xml = new XML_Reader();
            Document doc = xml.getXML(is);

            if (doc != null) {
                parseXML(hash, doc.getDocumentElement());
            }

            return hash;
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Hashtable read(File target) {
        Hashtable hash = new Hashtable();

        XML_Reader xml = new XML_Reader();
        Document doc = xml.getXML(target);

        if (doc != null) {
            parseXML(hash, doc.getDocumentElement());
        }

        return hash;
    }

    ////////////////////////////////////////////////////////////////////////////

    private void parseXML(Hashtable hash, Node node) {
        if (node.getNodeName().equals("hash")) {
            if (node.hasChildNodes()) {
                NodeList children = node.getChildNodes();
                for (int i = 0; i < children.getLength(); i++) {
                    Node child = children.item(i);
                    String name = child.getNodeName();
                    if (name.equals("data")) {
                        if (child.hasAttributes()) {
                            NamedNodeMap attributes = child.getAttributes();

                            String dName = attributes.getNamedItem("name").getNodeValue();
                            String dData = attributes.getNamedItem("data").getNodeValue();

                            if (attributes.getNamedItem("encode") != null) {
                                dData = decode(dData);
                            }

                            hash.put(dName, dData);
                        }
                    }
                }
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////

    public void write(File target, Hashtable table) {
        XMLNode root = new XMLNode("hash"), current = null;
        current = root;

        Enumeration myKeys = table.keys();

        int pos = 0;

        while (myKeys.hasMoreElements()) {//<data name="ftp" data="3d2toy.xml"/>
            String key = (String) myKeys.nextElement();

            String value = (String) table.get(key);


            XMLNode newNode = new XMLNode("data");
            if (needEncoding(value)) {
                newNode.addProperty("encode", "1");
                newNode.addProperty("name", key);
                newNode.addProperty("data", encode(value));
            } else {
                newNode.addProperty("name", key);
                newNode.addProperty("data", value);
            }

            if (pos == 0) {
                current.sub = newNode;
            } else {
                current.next = newNode;
            }
            current = newNode;

            pos++;
        }

        root.dump(target, "", root, true);
    }

    private boolean needEncoding(String value) {
        return (value.indexOf('"') != -1 || value.indexOf('<') != -1 || value.indexOf('>') != -1);
    }

    private String encode(String input) {
        String temp = input.replaceAll("\"", "|_QUOTE_|");
        temp = temp.replaceAll("<", "|_LB_|");
        temp = temp.replaceAll(">", "|_RB_|");
        return temp;
    }

    private String decode(String input) {
        int start = input.indexOf("|_QUOTE_|");
        while (start != -1) {
            String front = input.substring(0, start);
            input = front + "\"" + input.substring(start + 9);
            start = input.indexOf("|_QUOTE_|");
        }

        start = input.indexOf("|_LB_|");
        while (start != -1) {
            String front = input.substring(0, start);
            input = front + "<" + input.substring(start + 6);
            start = input.indexOf("|_LB_|");
        }

        start = input.indexOf("|_RB_|");
        while (start != -1) {
            String front = input.substring(0, start);
            input = front + ">" + input.substring(start + 6);
            start = input.indexOf("|_RB_|");
        }
        return input;
    }
}