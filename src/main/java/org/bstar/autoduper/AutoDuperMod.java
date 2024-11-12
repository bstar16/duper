package org.bstar.autoduper;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import org.bstar.autoduper.client.AutoDuperClient;
import org.bstar.autoduper.commands.DupeCommands;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AutoDuperMod implements ModInitializer {
    public static final String MOD_ID = "autoduper";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("Initializing AutoDuper");

        // Register commands
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            DupeCommands.register(dispatcher);
        });

        // Register tick event
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player != null) {
                AutoDuperClient instance = AutoDuperClient.getInstance();
                if (instance.isDuping()) {
                    instance.onTick();
                }
            }
        });
    }
}
