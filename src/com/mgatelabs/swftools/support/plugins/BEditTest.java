// Set the Package Location
package com.mgatelabs.swftools.support.plugins;

// Get Color

import com.mgatelabs.swftools.exploit.hook.BlockEditInterface;

import javax.swing.*;

// Get Exploit Hooks
// Get the Tags

public class BEditTest implements Plugin, BlockPlugin {
    // Plugin Information

    public String getName() {
        return "Test Multi Class";
    }

    public int getType() {
        return BLOCK;
    }

    // Menu Options
    public String getMenuName() {
        return "Test Multi Class";
    }

    public String getMenuPath() {
        return "Edit:Text";
    }

    // Plugin Tests
    public boolean isApplicable(int id) {
        return true;
    }

    // All Work Takes Place Here
    public boolean work(BlockEditInterface bei) {
          /*
         TBlock aBlock = bei.getSelectedBlock();
      
         if (aBlock == null)
         {
            return false;
         }
      
      	// Get the Data
         byte [] data = aBlock.getData();
      	
      	// Convert To String
         byte [] tempData = Arrays.copyOfRange(data, 0, data.length-1);
      	
         String tempString = new String(tempData);
      	// Clean Up
         tempData = null;
      	
         String res = JOptionPane.showInputDialog("Metadata", tempString);
            	
         if (res != null)
         {
            tempData = res.getBytes();
         	
            data = Arrays.copyOf(tempData,tempData.length+1);
         	
            aBlock.setData(data);
         	
            return true;
         }
         */

        BEditWindow t = new BEditWindow();

        return false;
    }

    private class BEditWindow {
        public BEditWindow() {
            JOptionPane.showMessageDialog(null, "Multi Test", "", JOptionPane.ERROR_MESSAGE);
        }
    }
}