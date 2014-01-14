// Set the Package Location
package com.mgatelabs.swftools.support.plugins;

// Get Color

import com.mgatelabs.swftools.exploit.hook.BlockEditInterface;
import com.mgatelabs.swftools.support.swf.tags.TBlock;

import javax.swing.*;
import java.awt.*;

// Get Exploit Hooks
// Get the Tags

public class BSetBackGroundColor implements Plugin, BlockPlugin {
    // Plugin Information

    public String getName() {
        return "edit Background Color";
    }

    public int getType() {
        return BLOCK;
    }

    // Menu Options
    public String getMenuName() {
        return "Background Color";
    }

    public String getMenuPath() {
        return "Edit:Tag";
    }

    // Plugin Tests
    public boolean isApplicable(int id) {
        return id == 9;
    }

    // All Work Takes Place Here
    public boolean work(BlockEditInterface bei) {
        TBlock aBlock = bei.getSelectedBlock();

        if (aBlock == null) {
            return false;
        }

        byte[] data = aBlock.getData();

        if (data != null && data.length == 3) {
            Color aColor = new Color(data[0] & 0xFF, data[1] & 0xFF, data[2] & 0xFF);

            aColor = JColorChooser.showDialog(null, "Choose a new Background Color", aColor);

            if (aColor != null) {
                data[0] = (byte) aColor.getRed();
                data[1] = (byte) aColor.getGreen();
                data[2] = (byte) aColor.getBlue();
            }
            return true;
        }
        return false;
    }
}