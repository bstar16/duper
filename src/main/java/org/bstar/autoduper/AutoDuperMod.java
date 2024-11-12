package org.bstar.autoduper;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import org.bstar.autoduper.client.AutoDuperClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class AutoDuperMod implements ClientModInitializer {
    public static final KeyBinding DUPE_KEY = new KeyBinding(
            "key.autoduper.dupe",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_R,  // R key
            "category.autoduper.main"
    );

    @Override
    public void onInitializeClient() {
        // Register keybinding
        KeyBindingHelper.registerKeyBinding(DUPE_KEY);

        // Register tick event
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (DUPE_KEY.wasPressed()) {
                AutoDuperClient instance = AutoDuperClient.getInstance();
                if (!instance.isDuping()) {
                    instance.startDupe();
                } else {
                    instance.stopDupe();
                }
            }
        });
    }
}
