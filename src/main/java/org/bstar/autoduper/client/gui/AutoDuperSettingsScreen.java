package org.bstar.autoduper.client.gui;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import org.bstar.autoduper.config.ConfigManager;
import net.minecraft.client.gui.DrawContext;

public class AutoDuperSettingsScreen extends Screen {
    private final Screen parent;
    private TextFieldWidget inventoryDelayField;
    private TextFieldWidget keyPressDelayField;

    public AutoDuperSettingsScreen(Screen parent) {
        super(Text.literal("Auto Duper Settings"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        // Inventory Delay Field
        this.inventoryDelayField = new TextFieldWidget(
                this.textRenderer,
                this.width / 2 - 100,
                this.height / 4 + 20,
                200,
                20,
                Text.literal("Inventory Delay")
        );
        inventoryDelayField.setText(String.format("%.1f", ConfigManager.getInventoryDelay() / 20.0f));
        this.addDrawableChild(inventoryDelayField);

        // Add label for Inventory Delay
        this.addDrawableChild(ButtonWidget.builder(
                        Text.literal("Inventory Delay (seconds)"),
                        button -> {})
                .dimensions(this.width / 2 - 100, this.height / 4, 200, 20)
                .build());

        // Key Press Delay Field
        this.keyPressDelayField = new TextFieldWidget(
                this.textRenderer,
                this.width / 2 - 100,
                this.height / 4 + 80,
                200,
                20,
                Text.literal("Key Press Delay")
        );
        keyPressDelayField.setText(String.format("%.1f", ConfigManager.getKeyPressDelay() / 20.0f));
        this.addDrawableChild(keyPressDelayField);

        // Add label for Key Press Delay
        this.addDrawableChild(ButtonWidget.builder(
                        Text.literal("Key Press Delay (seconds)"),
                        button -> {})
                .dimensions(this.width / 2 - 100, this.height / 4 + 60, 200, 20)
                .build());

        // Save Button
        this.addDrawableChild(ButtonWidget.builder(
                        Text.literal("Save"),
                        button -> {
                            try {
                                ConfigManager.setInventoryDelay((int)(Double.parseDouble(inventoryDelayField.getText()) * 20));
                                ConfigManager.setKeyPressDelay((int)(Double.parseDouble(keyPressDelayField.getText()) * 20));
                                ConfigManager.saveConfig();
                                this.close();
                            } catch (NumberFormatException e) {
                                // Handle invalid input
                            }
                        })
                .dimensions(this.width / 2 - 100, this.height / 4 + 120, 200, 20)
                .build());

        // Back Button
        this.addDrawableChild(ButtonWidget.builder(
                        Text.literal("Back"),
                        button -> this.close())
                .dimensions(this.width / 2 - 100, this.height / 4 + 144, 200, 20)
                .build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        context.drawText(this.textRenderer, this.title, (this.width - this.textRenderer.getWidth(this.title)) / 2, 20, 0xFFFFFF, true);
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public void close() {
        this.client.setScreen(this.parent);
    }

    @Override
    public boolean shouldPause() {
        return true;
    }
}
