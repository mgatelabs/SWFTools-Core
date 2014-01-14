package com.mgatelabs.swftools.support.tools;

import java.io.File;

public class FileNameHelper {

    public static File checkFile(File target, String extension) {
        //First off check the name

        String name = target.getName().toLowerCase();

        if (name.endsWith(extension)) {
            return target;
        } else {
            name = target.getName();

            int pos = name.lastIndexOf('.');

            if (pos == -1)//No .
            {
                if (target.getParent() != null) {
                    return new File(target.getParent() + File.separator + name + extension);
                } else {
                    return new File(name + extension);
                }
                //returnFile = new File();
            } else if (pos == 0) {
                //return new File(target.getParent() + File.separator + name + extension);
                return null;
            } else {
                name = name.substring(0, pos);
                if (target.getParent() != null) {
                    return new File(target.getParent() + File.separator + name + extension);
                } else {
                    return new File(name + extension);
                }
            }
        }
    }

}