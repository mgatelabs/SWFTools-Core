// Set the Package Location
package com.mgatelabs.swftools.support.plugins;

// Get Color

import com.mgatelabs.swftools.exploit.hook.BlockEditInterface;
import com.mgatelabs.swftools.support.swf.tags.TBlock;

import javax.swing.*;
import java.util.Arrays;

// Get Exploit Hooks
// Get the Tags

public class BAddHidden implements Plugin, BlockPlugin {
    // Plugin Information

    public String getName() {
        return "Add Hidden Text";
    }

    public int getType() {
        return BLOCK;
    }

    // Menu Options
    public String getMenuName() {
        return "Hidden Text";
    }

    public String getMenuPath() {
        return "Insert:Misc";
    }

    // Plugin Tests
    public boolean isApplicable(int id) {
        return true;
    }

    // All Work Takes Place Here
    public boolean work(BlockEditInterface bei) {
        int index = bei.getSelectedIndex();

        String res = JOptionPane.showInputDialog("Hidden Text", "Your Hidden Message");

        if (res != null) {
            byte[] tempData = res.getBytes();

            byte[] data = Arrays.copyOf(tempData, tempData.length + 1);

            TBlock aBlock = new TBlock(666, data.length);

            aBlock.setData(data);

            bei.insertBlock(index, aBlock);

            return true;
        }

        return false;
    }
}