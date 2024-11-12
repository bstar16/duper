package org.bstar.autoduper.client.gui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import org.bstar.autoduper.client.AutoDuperClient;

public class AutoDuperScreen extends Screen {
    private TextFieldWidget delayField;
    private final Screen parent; // Add this field

    public AutoDuperScreen(Screen parent) { // Update constructor to accept parent
        super(Text.literal("AutoDuper"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        // Add start/stop button
        this.addDrawableChild(ButtonWidget.builder(
                Text.literal(AutoDuperClient.getInstance().isDuping() ? "Stop Duping" : "Start Duping"),
                button -> {
                    if (!AutoDuperClient.getInstance().isDuping()) {
                        AutoDuperClient.getInstance().startDupe();
                        button.setMessage(Text.literal("Stop Duping"));
                    } else {
                        AutoDuperClient.getInstance().stopDupe();
                        button.setMessage(Text.literal("Start Duping"));
                    }
                }
        ).dimensions(this.width / 2 - 100, this.height / 2 - 24, 200, 20).build());

        // Add delay input field
        delayField = new TextFieldWidget(
                this.textRenderer,
                this.width / 2 - 100,
                this.height / 2 + 12,
                200,
                20,
                Text.literal("Mount Delay")
        );
        delayField.setText(String.valueOf(AutoDuperClient.getInstance().getMountDelay()));
        delayField.setMaxLength(4);
        this.addDrawableChild(delayField);

        // Add save button for delay
        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("Save Delay"),
                button -> {
                    try {
                        int delay = Integer.parseInt(delayField.getText());
                        if (delay > 0) {
                            AutoDuperClient.getInstance().setMountDelay(delay);
                            this.client.setScreen(parent); // Return to parent screen after saving
                        }
                    } catch (NumberFormatException e) {
                        // Invalid input, keep old value
                        delayField.setText(String.valueOf(AutoDuperClient.getInstance().getMountDelay()));
                    }
                }
        ).dimensions(this.width / 2 - 100, this.height / 2 + 36, 200, 20).build());

        // Add back button
        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("Back"),
                button -> this.client.setScreen(parent)
        ).dimensions(this.width / 2 - 100, this.height / 2 + 60, 200, 20).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        context.drawTextWithShadow(
                this.textRenderer,
                "Mount Delay (in ticks)",
                this.width / 2 - this.textRenderer.getWidth("Mount Delay (in ticks)") / 2,
                this.height / 2,
                0xFFFFFF
        );
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
