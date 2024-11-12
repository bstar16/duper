package org.bstar.autoduper;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import org.bstar.autoduper.client.AutoDuperClient;
import org.bstar.autoduper.client.KeyBinds;
import org.bstar.autoduper.commands.DupeCommands;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.minecraft.text.Text;

public class AutoDuperMod implements ClientModInitializer {
    public static final String MOD_ID = "autoduper";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitializeClient() {
        LOGGER.info("Initializing AutoDuper");

        // Register keybindings
        KeyBinds.register();

        // Register commands
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            DupeCommands.register(dispatcher);
        });

        // Register tick event
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player != null) {
                AutoDuperClient instance = AutoDuperClient.getInstance();

                // Handle keybinding
                if (KeyBinds.dupeKey.wasPressed()) {
                    LOGGER.info("Dupe key pressed!"); // Debug log
                    if (instance.isDuping()) {
                        LOGGER.info("Stopping dupe sequence"); // Debug log
                        instance.stopDupe();
                        client.player.sendMessage(Text.literal("§c[AutoDuper] Stopped duping"), false);
                    } else {
                        LOGGER.info("Starting dupe sequence"); // Debug log
                        instance.startDupe();
                        client.player.sendMessage(Text.literal("§a[AutoDuper] Started duping"), false);
                    }
                }

                // Existing tick handling
                if (instance.isDuping()) {
                    instance.onTick();
                }
            }
        });
    }
}
