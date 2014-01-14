// Set the Package Location
package com.mgatelabs.swftools.support.plugins;

// Get Color

import com.mgatelabs.swftools.exploit.hook.BlockEditInterface;
import com.mgatelabs.swftools.support.swf.tags.TBlock;
import com.mgatelabs.swftools.support.swf.tags.Tag;

import javax.swing.*;

// Get Exploit Hooks
// Get the Tags

public class BEditID implements Plugin, BlockPlugin {
    // Plugin Information

    public String getName() {
        return "Edit Object ID";
    }

    public int getType() {
        return BLOCK;
    }

    // Menu Options
    public String getMenuName() {
        return "ID#";
    }

    public String getMenuPath() {
        return "Edit:Object";
    }

    // Plugin Tests
    public boolean isApplicable(int id) {
        return Tag.getObjectable(id);
    }

    // All Work Takes Place Here
    public boolean work(BlockEditInterface bei) {
        TBlock aBlock = bei.getSelectedBlock();

        if (aBlock == null) {
            return false;
        }

        if (!Tag.getObjectable(aBlock.getId())) {
            return false;
        }

        int id = aBlock.getDataID(aBlock);

        if (id != -1) {
            // Let them change it
            String res = JOptionPane.showInputDialog("Object ID", "" + id);

            if (res != null) {
                try {
                    id = Integer.parseInt(res);
                    aBlock.setDataID(aBlock, id);
                    bei.updateList();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, "Bad Input: " + e, "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }


        return false;
    }
}