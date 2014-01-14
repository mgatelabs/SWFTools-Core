// Set the Package Location
package com.mgatelabs.swftools.support.plugins;

// Get Color

import com.mgatelabs.swftools.exploit.hook.BlockEditInterface;
import com.mgatelabs.swftools.support.swf.tags.TBlock;

// Flash IO
// Get Exploit Hooks
// Get the Tags

public class BInsertShow implements Plugin, BlockPlugin {
    // Plugin Information

    public String getName() {
        return "Insert Show Frame";
    }

    public int getType() {
        return BLOCK;
    }

    // Menu Options
    public String getMenuName() {
        return "Show Frame";
    }

    public String getMenuPath() {
        return "Insert:Tag";
    }

    // Plugin Tests
    public boolean isApplicable(int id) {
        return true;
    }

    // All Work Takes Place Here
    public boolean work(BlockEditInterface bei) {
        // Get the Block
        int index = bei.getSelectedIndex();
        TBlock aBlock = new TBlock(1, 0);
        bei.insertBlock(index, aBlock);
        return true;
    }

}