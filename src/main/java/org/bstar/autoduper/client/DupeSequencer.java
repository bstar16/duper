package org.bstar.autoduper.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.DonkeyEntity;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.client.gui.screen.ingame.HorseScreen;
import net.minecraft.screen.HorseScreenHandler;

public class DupeSequencer {
    private boolean isRunning = false;
    private int currentStage = 0;
    private int tickDelay = 0;
    private final MinecraftClient client = MinecraftClient.getInstance();
    private int mountDelay = 20;

    public void reset() {
        isRunning = false;
        currentStage = 0;
        tickDelay = 0;
    }

    public int getCurrentStage() {
        return currentStage;
    }

    public void setMountDelay(int delay) {
        this.mountDelay = delay;
    }

    public void toggle() {
        isRunning = !isRunning;
        currentStage = 0;
        tickDelay = 0;
        sendDebugMessage("Dupe " + (isRunning ? "Started" : "Stopped") + " (Stage: " + currentStage + ")");
    }

    public void tick() {
        if (!isRunning || --tickDelay > 0) return;

        switch (currentStage) {
            case 0: // Select chest in hotbar
                int chestSlot = findChestInHotbar();
                if (chestSlot != -1) {
                    sendDebugMessage("Selecting chest in hotbar");
                    client.player.getInventory().selectedSlot = chestSlot;
                    tickDelay = 2;
                    currentStage = 1;
                } else {
                    sendDebugMessage("No chest found in hotbar! Stopping.");
                    isRunning = false;
                }
                break;

            case 1: // Mount donkey
                if (!(client.player.getVehicle() instanceof DonkeyEntity)) {
                    DonkeyEntity nearestDonkey = findNearestDonkey();
                    if (nearestDonkey != null) {
                        sendDebugMessage("Mounting donkey");
                        client.interactionManager.interactEntity(client.player, nearestDonkey, Hand.MAIN_HAND);
                        tickDelay = 2;
                        currentStage = 2;
                    } else {
                        sendDebugMessage("No donkey found nearby! Stopping.");
                        isRunning = false;
                    }
                } else {
                    currentStage = 3;
                }
                break;

            case 2: // Check if mounted
                if (client.player.getVehicle() instanceof DonkeyEntity) {
                    tickDelay = mountDelay;
                    currentStage = 3;
                } else {
                    currentStage = 1;
                }
                break;

            case 3: // Open inventory
                if (client.player.getVehicle() instanceof DonkeyEntity) {
                    sendDebugMessage("Opening inventory");
                    client.player.networkHandler.sendPacket(
                        new ClientCommandC2SPacket(client.player, ClientCommandC2SPacket.Mode.OPEN_INVENTORY)
                    );
                    tickDelay = 5;
                    currentStage = 4;
                } else {
                    currentStage = 1;
                }
                break;

            case 4: // Verify inventory opened
                if (client.currentScreen instanceof HorseScreen) {
                    tickDelay = 2;
                    currentStage = 5;
                } else {
                    currentStage = 3;
                }
                break;

            case 5: // Move items to donkey
                if (client.currentScreen instanceof HorseScreen) {
                    sendDebugMessage("Moving items to donkey");
                    moveItemsToDonkey();
                    tickDelay = 2;
                    currentStage = 6;
                }
                break;

            case 6: // Apply chest
                if (client.currentScreen instanceof HorseScreen) {
                    sendDebugMessage("Applying chest");
                    DonkeyEntity donkey = (DonkeyEntity) client.player.getVehicle();
                    client.interactionManager.interactEntity(client.player, donkey, Hand.MAIN_HAND);
                    tickDelay = 2;
                    currentStage = 7;
                }
                break;

            case 7: // Remove items from donkey
                if (client.currentScreen instanceof HorseScreen) {
                    sendDebugMessage("Taking items from donkey");
                    moveItemsFromDonkey();
                    tickDelay = 2;
                    currentStage = 8;
                }
                break;

            case 8: // Close inventory
                if (client.currentScreen != null) {
                    sendDebugMessage("Closing inventory");
                    client.player.closeHandledScreen();
                    tickDelay = 2;
                    currentStage = 9;
                }
                break;

            case 9: // Dismount
                if (client.player.getVehicle() instanceof DonkeyEntity) {
                    sendDebugMessage("Dismounting donkey");
                    client.options.sneakKey.setPressed(true);
                    tickDelay = 2;
                    currentStage = 10;
                } else {
                    currentStage = 0;
                }
                break;

            case 10: // Release dismount key
                client.options.sneakKey.setPressed(false);
                tickDelay = 2;
                if (client.player.getVehicle() == null) {
                    currentStage = 0;
                } else {
                    currentStage = 9;
                }
                break;
        }
    }

    private void sendDebugMessage(String message) {
        if (client.player != null) {
            client.player.sendMessage(Text.literal("§b[AutoDuper] " + message), false);
        }
    }

    private int findChestInHotbar() {
        for (int i = 0; i < 9; i++) {
            if (client.player.getInventory().getStack(i).getItem() == Items.CHEST) {
                return i;
            }
        }
        return -1;
    }

    private DonkeyEntity findNearestDonkey() {
        double searchRadius = 5.0;
        DonkeyEntity closest = null;
        double closestDistance = searchRadius * searchRadius;

        for (Entity entity : client.world.getEntities()) {
            if (entity instanceof DonkeyEntity donkey) {
                double distance = client.player.squaredDistanceTo(entity);
                if (distance < closestDistance && !donkey.hasPassengers()) {
                    closestDistance = distance;
                    closest = donkey;
                }
            }
        }

        if (closest != null) {
            double deltaX = closest.getX() - client.player.getX();
            double deltaY = (closest.getY() + closest.getHeight()/2) - (client.player.getY() + client.player.getEyeHeight(client.player.getPose()));
            double deltaZ = closest.getZ() - client.player.getZ();
            
            double horizontalDistance = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);
            float yaw = (float) Math.toDegrees(Math.atan2(-deltaX, deltaZ));
            float pitch = (float) Math.toDegrees(-Math.atan2(deltaY, horizontalDistance));
            
            client.player.setYaw(yaw);
            client.player.setPitch(pitch);
        }
        
        return closest;
    }

    private void moveItemsToDonkey() {
        if (!(client.currentScreen instanceof HorseScreen)) return;
        
        HorseScreenHandler handler = ((HorseScreen) client.currentScreen).getScreenHandler();
        
        for (int i = 2; i < 17; i++) {
            if (handler.getSlot(i).getStack().isEmpty()) {
                for (int j = 32; j < handler.slots.size(); j++) {
                    if (!handler.getSlot(j).getStack().isEmpty() && 
                        handler.getSlot(j).getStack().getItem() != Items.CHEST) {
                        client.interactionManager.clickSlot(handler.syncId, j, 0, SlotActionType.PICKUP, client.player);
                        client.interactionManager.clickSlot(handler.syncId, i, 0, SlotActionType.PICKUP, client.player);
                        break;
                    }
                }
            }
        }
    }

    private void moveItemsFromDonkey() {
        if (!(client.currentScreen instanceof HorseScreen)) return;
        
        HorseScreenHandler handler = ((HorseScreen) client.currentScreen).getScreenHandler();
        
        for (int i = 2; i < 17; i++) {
            if (!handler.getSlot(i).getStack().isEmpty()) {
                for (int j = 32; j < handler.slots.size(); j++) {
                    if (handler.getSlot(j).getStack().isEmpty()) {
                        client.interactionManager.clickSlot(handler.syncId, i, 0, SlotActionType.PICKUP, client.player);
                        client.interactionManager.clickSlot(handler.syncId, j, 0, SlotActionType.PICKUP, client.player);
                        break;
                    }
                }
            }
        }
    }
}
