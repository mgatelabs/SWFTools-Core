package com.mgatelabs.swftools.support.plugins;

import com.mgatelabs.swftools.support.xml.XMLHash;

import java.io.File;
import java.io.InputStream;
import java.util.Hashtable;

public class PluginInfo {
    public String name;
    public String fileName;
    public String className;
    public String description;
    public boolean encrypted;
    public int id;
    public int type;
    public int major;
    public int minor;
    private boolean packaged;

    public PluginInfo(String path, InputStream xmlStream, boolean packaged) {

        XMLHash reader = new XMLHash();
        Hashtable myHash = reader.read(xmlStream);
        this.packaged = packaged;

        name = (String) myHash.get("name");
        fileName = (String) myHash.get("file");
        this.className = path + (String) myHash.get("class");
        description = (String) myHash.get("description");
        encrypted = false;

        id = Integer.parseInt((String) myHash.get("id"));
        type = Integer.parseInt((String) myHash.get("type"));

        major = Integer.parseInt((String) myHash.get("major"));
        minor = Integer.parseInt((String) myHash.get("minor"));
    }

    public PluginInfo(File target, boolean packaged) {
        XMLHash reader = new XMLHash();
        Hashtable myHash = reader.read(target);
        this.packaged = packaged;

        name = (String) myHash.get("name");
        fileName = (String) myHash.get("file");
        className = (String) myHash.get("class");
        description = (String) myHash.get("description");
        encrypted = false;

        id = Integer.parseInt((String) myHash.get("id"));
        type = Integer.parseInt((String) myHash.get("type"));

        major = Integer.parseInt((String) myHash.get("major"));
        minor = Integer.parseInt((String) myHash.get("minor"));
    }

    public String toString() {
        String result = null;

        if (encrypted) {
            result = "(PRO)" + name;
        } else {
            result = "(Free)" + name;
        }

        return result;
    }

    public String getName() {
        return name;
    }

    public String getFileName() {
        return fileName;
    }

    public String getClassName() {
        return className;
    }

    public String getDescription() {
        return description;
    }

    public int getId() {
        return id;
    }

    public int getType() {
        return type;
    }

    public int getMajor() {
        return major;
    }

    public int getMinor() {
        return minor;
    }

    public boolean isPackaged() {
        return packaged;
    }
}