package org.bstar.autoduper.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Box;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import java.util.List;

public class DupeSequencer {
    private final MinecraftClient client = MinecraftClient.getInstance();
    private int ticks = 0;
    private int stage = 0;
    private int mountDelay = 20;

    public void tick() {
        if (client.player == null) return;

        ticks++;
        switch (stage) {
            case 0: // Select chest in hotbar
                for (int i = 0; i < 9; i++) {
                    if (client.player.getInventory().getStack(i).getItem() == Items.CHEST) {
                        client.player.getInventory().selectedSlot = i;
                        break;
                    }
                }
                ticks = 0;
                stage++;
                break;

            case 1: // Mount donkey
                Entity nearestDonkey = findNearestDonkey();
                if (nearestDonkey != null) {
                    client.player.startRiding(nearestDonkey);
                }
                ticks = 0;
                stage++;
                break;

            case 2: // Wait for mount and open inventory
                if (ticks >= 5) {
                    Entity vehicle = client.player.getVehicle();
                    if (vehicle != null) {
                        // Send packet to open inventory
                        client.player.networkHandler.sendPacket(
                                PlayerInteractEntityC2SPacket.interact(vehicle, false, Hand.MAIN_HAND)
                        );
                    }
                    ticks = 0;
                    stage++;
                }
                break;

            case 3: // Put items into donkey (skip chests)
                if (client.currentScreen != null && ticks >= 2) {
                    moveItemsToDonkey();
                    ticks = 0;
                    stage++;
                }
                break;

            case 4: // Click donkey with chest
                if (client.player.getVehicle() != null) {
                    client.player.networkHandler.sendPacket(
                            PlayerInteractEntityC2SPacket.interact(
                                    client.player.getVehicle(),
                                    true,
                                    Hand.MAIN_HAND
                            )
                    );
                }
                ticks = 0;
                stage++;
                break;

            case 5: // Take items out
                if (client.currentScreen != null && ticks >= 2) {
                    moveItemsFromDonkey();
                    ticks = 0;
                    stage++;
                }
                break;

            case 6: // Close inventory
                if (client.currentScreen != null) {
                    client.player.closeHandledScreen();
                }
                ticks = 0;
                stage++;
                break;

            case 7: // Dismount
                if (ticks >= 2) {
                    client.player.dismountVehicle();
                    ticks = 0;
                    stage = 0; // Reset to beginning
                }
                break;
        }
    }

    private Entity findNearestDonkey() {
        if (client.world == null || client.player == null) return null;

        Box searchBox = new Box(
                client.player.getX() - 5,
                client.player.getY() - 5,
                client.player.getZ() - 5,
                client.player.getX() + 5,
                client.player.getY() + 5,
                client.player.getZ() + 5
        );

        List<Entity> entities = client.world.getEntitiesByClass(Entity.class, searchBox,
                entity -> entity.getName().getString().contains("Donkey"));

        if (!entities.isEmpty()) {
            return entities.get(0);
        }
        return null;
    }

    private void moveItemsToDonkey() {
        if (client.interactionManager == null) return;

        // Loop through player inventory slots
        for (int i = 9; i < 45; i++) {
            // Skip chest slots
            if (client.player.getInventory().getStack(i).getItem() != Items.CHEST) {
                // Quick move (Shift+Click) items
                client.interactionManager.clickSlot(
                        client.player.currentScreenHandler.syncId,
                        i,
                        0,
                        SlotActionType.QUICK_MOVE,
                        client.player
                );
            }
        }
    }

    private void moveItemsFromDonkey() {
        if (client.interactionManager == null) return;

        // Loop through donkey inventory slots
        for (int i = 0; i < client.player.currentScreenHandler.slots.size(); i++) {
            if (i < 2) continue; // Skip donkey equipment slots

            // Quick move (Shift+Click) items back to player inventory
            client.interactionManager.clickSlot(
                    client.player.currentScreenHandler.syncId,
                    i,
                    0,
                    SlotActionType.QUICK_MOVE,
                    client.player
            );
        }
    }

    public void setMountDelay(int delay) {
        this.mountDelay = delay;
    }

    public void reset() {
        ticks = 0;
        stage = 0;
    }
    public int getCurrentStage() {
        return stage;
    }
}
