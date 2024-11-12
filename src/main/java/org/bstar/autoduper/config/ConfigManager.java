package org.bstar.autoduper.config;

public class ConfigManager {
    private static int inventoryDelay = 20; // default value
    private static int keyPressDelay = 10; // default value

    public static int getInventoryDelay() {
        return inventoryDelay;
    }

    public static int getKeyPressDelay() {
        return keyPressDelay;
    }

    public static void setInventoryDelay(int delay) {
        inventoryDelay = delay;
    }

    public static void setKeyPressDelay(int delay) {
        keyPressDelay = delay;
    }

    public static void loadConfig() {
        // Load config logic here
        // For now using default values
    }

    public static void saveConfig() {
        // Save config logic here
        // For now just keeping values in memory
    }
}
