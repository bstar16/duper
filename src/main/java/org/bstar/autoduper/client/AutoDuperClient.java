package org.bstar.autoduper.client;

public class AutoDuperClient {
    private static AutoDuperClient INSTANCE;
    private boolean isDuping = false;
    private final DupeSequencer sequencer = new DupeSequencer();
    private int mountDelay = 20;

    public static AutoDuperClient getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new AutoDuperClient();
        }
        return INSTANCE;
    }

    public void startDupe() {
        isDuping = true;
        sequencer.reset();
    }

    public void stopDupe() {
        isDuping = false;
        sequencer.reset();
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

    public void setMountDelay(int delay) {
        this.mountDelay = delay;
        sequencer.setMountDelay(delay);
    }

    public int getMountDelay() {
        return mountDelay;
    }
}
