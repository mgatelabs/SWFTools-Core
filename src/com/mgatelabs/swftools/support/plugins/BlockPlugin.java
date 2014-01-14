package com.mgatelabs.swftools.support.plugins;

// Exploit Hooks

import com.mgatelabs.swftools.exploit.hook.BlockEditInterface;

// Tag Support
// Util Support

public interface BlockPlugin {
    // Menu Options
    public String getMenuName();

    public String getMenuPath();

    // Plugin Tests
    public boolean isApplicable(int id);

    // All Work Takes Place Here
    public boolean work(BlockEditInterface bei);
}