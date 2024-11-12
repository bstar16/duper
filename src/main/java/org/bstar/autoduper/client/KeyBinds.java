package org.bstar.autoduper.client;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import org.bstar.autoduper.AutoDuperMod;

public class KeyBinds {
    public static KeyBinding dupeKey;

    public static void register() {
        dupeKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.autoduper.dupe",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_R,
                "category.autoduper.main"
        ));
        AutoDuperMod.LOGGER.info("Registered AutoDuper keybinding");
    }
}
