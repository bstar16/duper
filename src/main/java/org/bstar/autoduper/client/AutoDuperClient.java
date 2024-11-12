package org.bstar.autoduper.client;

import net.minecraft.client.MinecraftClient;

public class AutoDuperClient {
    private static AutoDuperClient INSTANCE;
    private final DupeSequencer sequencer;
    private boolean isDuping = false;

    private AutoDuperClient() {
        this.sequencer = new DupeSequencer();
    }

    public static AutoDuperClient getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new AutoDuperClient();
        }
        return INSTANCE;
    }

    public void startDupe() {
        isDuping = true;
        sequencer.toggle();
    }

    public void stopDupe() {
        isDuping = false;
        sequencer.toggle();
    }

    public void onTick() {
        if (isDuping) {
            sequencer.tick();
        }
    }

    public boolean isDuping() {
        return isDuping;
    }

    public int getCurrentStage() {
        return sequencer.getCurrentStage();
    }

    // Add these delay setters
    public void setMountDelay(int delay) {
        sequencer.setMountDelay(delay);
    }

    public void setKeyPressDelay(int delay) {
        sequencer.setKeyPressDelay(delay);
    }

    public void setInventoryDelay(int delay) {
        sequencer.setInventoryDelay(delay);
    }

    public void setMoveItemsDelay(int delay) {
        sequencer.setMoveItemsDelay(delay);
    }

    public void setChestApplyDelay(int delay) {
        sequencer.setChestApplyDelay(delay);
    }

    public void setDismountDelay(int delay) {
        sequencer.setDismountDelay(delay);
    }

    // Add these delay getters
    public int getMountDelay() {
        return sequencer.getMountDelay();
    }

    public int getKeyPressDelay() {
        return sequencer.getKeyPressDelay();
    }

    public int getInventoryDelay() {
        return sequencer.getInventoryDelay();
    }

    public int getMoveItemsDelay() {
        return sequencer.getMoveItemsDelay();
    }

    public int getChestApplyDelay() {
        return sequencer.getChestApplyDelay();
    }

    public int getDismountDelay() {
        return sequencer.getDismountDelay();
    }
}
