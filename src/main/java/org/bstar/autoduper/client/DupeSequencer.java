package org.bstar.autoduper.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.DonkeyEntity;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Box;
import java.util.List;

public class DupeSequencer {
    private final MinecraftClient client = MinecraftClient.getInstance();
    private int ticks = 0;
    private int stage = 0;
    private Entity targetDonkey = null;
    private int mountDelay = 20; // Default mount delay

    public void setMountDelay(int delay) {
        this.mountDelay = delay;
    }

    public void tick() {
        if (client.player == null || client.world == null) return;

        ticks++;

        switch (stage) {
            case 0: // Find and select chest
                selectChestInHotbar();
                stage++;
                ticks = 0;
                break;

            case 1: // Find donkey
                targetDonkey = findNearestDonkey();
                if (targetDonkey != null) {
                    stage++;
                }
                ticks = 0;
                break;

            case 2: // Mount donkey
                if (ticks >= mountDelay) {
                    if (targetDonkey != null) {
                        client.player.startRiding(targetDonkey);
                        stage++;
                        ticks = 0;
                    }
                }
                break;

            case 3: // Open inventory
                if (ticks >= 5 && client.player.getVehicle() != null) {
                    // Send packet to open inventory
                    client.player.networkHandler.sendPacket(
                            PlayerInteractEntityC2SPacket.interact(
                                    client.player.getVehicle(),
                                    false,
                                    Hand.MAIN_HAND
                            )
                    );
                    stage++;
                    ticks = 0;
                }
                break;

            case 4: // Move items to donkey
                if (client.currentScreen != null && ticks >= 2) {
                    moveItemsToDonkey();
                    stage++;
                    ticks = 0;
                }
                break;

            case 5: // Apply chest
                if (ticks >= 2 && client.player.getVehicle() != null) {
                    client.player.networkHandler.sendPacket(
                            PlayerInteractEntityC2SPacket.interactAt(
                                    client.player.getVehicle(),
                                    false,
                                    Hand.MAIN_HAND,
                                    client.player.getPos()
                            )
                    );
                    stage++;
                    ticks = 0;
                }
                break;

            case 6: // Take items out
                if (client.currentScreen != null && ticks >= 2) {
                    moveItemsFromDonkey();
                    stage++;
                    ticks = 0;
                }
                break;

            case 7: // Close inventory
                if (ticks >= 2) {
                    if (client.currentScreen != null) {
                        client.player.closeHandledScreen();
                    }
                    stage++;
                    ticks = 0;
                }
                break;

            case 8: // Dismount
                if (ticks >= 2) {
                    client.player.dismountVehicle();
                    stage = 0;
                    ticks = 0;
                    targetDonkey = null;
                }
                break;
        }
    }

    private void selectChestInHotbar() {
        for (int i = 0; i < 9; i++) {
            if (client.player.getInventory().getStack(i).getItem() == Items.CHEST) {
                client.player.getInventory().selectedSlot = i;
                break;
            }
        }
    }

    private Entity findNearestDonkey() {
        Box searchBox = new Box(
                client.player.getX() - 5,
                client.player.getY() - 5,
                client.player.getZ() - 5,
                client.player.getX() + 5,
                client.player.getY() + 5,
                client.player.getZ() + 5
        );

        List<DonkeyEntity> entities = client.world.getEntitiesByType(
                EntityType.DONKEY,
                searchBox,
                donkey -> !donkey.hasChest() && !donkey.hasPassengers()
        );

        return entities.isEmpty() ? null : entities.get(0);
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

    public int getCurrentStage() {
        return stage;
    }

    public void reset() {
        ticks = 0;
        stage = 0;
        targetDonkey = null;
    }
}
