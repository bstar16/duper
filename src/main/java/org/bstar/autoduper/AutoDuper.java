package org.bstar.autoduper;

import net.fabricmc.api.ModInitializer;
import org.bstar.autoduper.config.ConfigManager;

public class AutoDuper implements ModInitializer {
    @Override
    public void onInitialize() {
        ConfigManager.loadConfig();
    }
}