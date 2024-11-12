package org.bstar.autoduper.mixin;

import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.HorseScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.entity.passive.AbstractDonkeyEntity;
import net.minecraft.screen.HorseScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.entity.player.PlayerInventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.bstar.autoduper.client.gui.AutoDuperScreen;
import net.minecraft.client.gui.DrawContext;

@Mixin(HorseScreen.class)
public abstract class HorseScreenMixin extends HandledScreen<HorseScreenHandler> {
    public HorseScreenMixin(HorseScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Inject(method = "render(Lnet/minecraft/client/gui/DrawContext;IIF)V", at = @At("TAIL"))
    private void injectConfigButton(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (this.handler.slots.get(0).getStack().isEmpty() && this.handler.slots.size() > 2) {
            this.addDrawableChild(ButtonWidget.builder(
                    Text.literal("AutoDuper"),
                    button -> {
                        if (this.client != null) {
                            this.client.setScreen(new AutoDuperScreen(this));
                        }
                    }
            ).dimensions(this.width / 2 - 100, this.height / 4 + 120, 200, 20).build());
        }
    }
}
