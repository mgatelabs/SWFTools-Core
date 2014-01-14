package com.mgatelabs.swftools.support.swf.objects;

import java.awt.image.BufferedImage;

public interface FImage {
    public int getVersion();

    public BufferedImage getImage();

    public int getWidth();

    public int getHeight();

    public int getID();
}