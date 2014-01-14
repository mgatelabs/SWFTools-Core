package com.mgatelabs.swftools.support.tools;

import java.io.File;

public class FolderHelper {
    public static final String APPPATH = "/M-Gate Labs/Flash Exploit/";

    public static String getAPPDATA() {
        return System.getenv().get("APPDATA");
    }

    public static File getAppPath(String filename) {
        String myAppPath = getAPPDATA() + APPPATH;
        File t = new File(myAppPath);
        t.mkdirs();
        t = new File(myAppPath + filename);
        return t;
    }
}