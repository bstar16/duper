package org.bstar.autoduper.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import org.bstar.autoduper.client.AutoDuperClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin {
    @Inject(method = "render", at = @At("RETURN"))
    private void onRender(DrawContext context, float tickDelta, CallbackInfo ci) {
        if (AutoDuperClient.getInstance().isDuping()) {
            context.drawText(
                    MinecraftClient.getInstance().textRenderer,
                    "Duping: Stage " + AutoDuperClient.getInstance().getCurrentStage(),
                    2,
                    2,
                    0xFFFFFF,
                    true
            );
        }
    }
}
