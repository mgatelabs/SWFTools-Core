package com.mgatelabs.swftools.support.loaders;

import java.io.File;
import java.io.FileInputStream;

public class BasicClassLoader extends ClassLoader {

    public Class findClass(String name, File target) {
        byte[] b = loadClassData(target);

        if (b == null) {
            return null;
        }

        return defineClass(name, b, 0, b.length);
    }

    public Class findPackagedClass(String name, String target) {
        try {
            return this.getClass().getClassLoader().loadClass(target);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    private byte[] loadClassData(File target) {
        try {
            byte[] data = new byte[(int) target.length()];

            FileInputStream fis = new FileInputStream(target);

            fis.read(data);

            fis.close();

            return data;
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }
}