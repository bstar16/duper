package org.bstar.autoduper;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.bstar.autoduper.client.AutoDuperClient;
import org.lwjgl.glfw.GLFW;

public class AutoDuperMod implements ClientModInitializer {
    private static KeyBinding toggleKey;
    private static boolean wasPressed = false;

    @Override
    public void onInitializeClient() {
        toggleKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.autoduper.toggle",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_R,
                "category.autoduper.main"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (toggleKey.isPressed() && !wasPressed) {
                AutoDuperClient instance = AutoDuperClient.getInstance();
                if (instance.isDuping()) {
                    instance.stopDupe();
                } else {
                    instance.startDupe();
                }
            }
            wasPressed = toggleKey.isPressed();

            // Tick the client
            if (AutoDuperClient.getInstance().isDuping()) {
                AutoDuperClient.getInstance().onTick();
            }
        });
    }
}
