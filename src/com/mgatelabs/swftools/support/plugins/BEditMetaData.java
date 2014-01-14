// Set the Package Location
package com.mgatelabs.swftools.support.plugins;

// Get Color

import com.mgatelabs.swftools.exploit.hook.BlockEditInterface;
import com.mgatelabs.swftools.support.swf.tags.TBlock;

import javax.swing.*;
import java.util.Arrays;

// Get Exploit Hooks
// Get the Tags

public class BEditMetaData implements Plugin, BlockPlugin {
    // Plugin Information

    public String getName() {
        return "edit Metadata";
    }

    public int getType() {
        return BLOCK;
    }

    // Menu Options
    public String getMenuName() {
        return "Metadata";
    }

    public String getMenuPath() {
        return "Edit:Tag";
    }

    // Plugin Tests
    public boolean isApplicable(int id) {
        return id == 77;
    }

    // All Work Takes Place Here
    public boolean work(BlockEditInterface bei) {
        TBlock aBlock = bei.getSelectedBlock();

        if (aBlock == null) {
            return false;
        }

        // Get the Data
        byte[] data = aBlock.getData();

        // Convert To String
        byte[] tempData = Arrays.copyOfRange(data, 0, data.length - 1);

        String tempString = new String(tempData);
        // Clean Up
        tempData = null;

        String res = JOptionPane.showInputDialog("Metadata", tempString);

        if (res != null) {
            tempData = res.getBytes();

            data = Arrays.copyOf(tempData, tempData.length + 1);

            aBlock.setData(data);

            return true;
        }

        return false;
    }
}