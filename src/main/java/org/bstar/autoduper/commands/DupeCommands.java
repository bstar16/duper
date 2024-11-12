package org.bstar.autoduper.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.minecraft.text.Text;
import org.bstar.autoduper.client.AutoDuperClient;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;

public class DupeCommands {
    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(literal("dupe")
                .then(literal("start")
                        .executes(context -> {
                            AutoDuperClient.getInstance().startDupe();
                            return 1;
                        }))
                .then(literal("stop")
                        .executes(context -> {
                            AutoDuperClient.getInstance().stopDupe();
                            return 1;
                        }))
                .then(literal("delay")
                        .then(literal("mount")
                                .then(argument("ticks", IntegerArgumentType.integer(1))
                                        .executes(context -> {
                                            int ticks = IntegerArgumentType.getInteger(context, "ticks");
                                            AutoDuperClient.getInstance().setMountDelay(ticks);
                                            context.getSource().sendFeedback(Text.literal("Mount delay set to " + ticks + " ticks"));
                                            return 1;
                                        })))
                        .then(literal("keypress")
                                .then(argument("ticks", IntegerArgumentType.integer(1))
                                        .executes(context -> {
                                            int ticks = IntegerArgumentType.getInteger(context, "ticks");
                                            AutoDuperClient.getInstance().setKeyPressDelay(ticks);
                                            context.getSource().sendFeedback(Text.literal("Key press delay set to " + ticks + " ticks"));
                                            return 1;
                                        })))
                        .then(literal("inventory")
                                .then(argument("ticks", IntegerArgumentType.integer(1))
                                        .executes(context -> {
                                            int ticks = IntegerArgumentType.getInteger(context, "ticks");
                                            AutoDuperClient.getInstance().setInventoryDelay(ticks);
                                            context.getSource().sendFeedback(Text.literal("Inventory delay set to " + ticks + " ticks"));
                                            return 1;
                                        })))
                        .then(literal("items")
                                .then(argument("ticks", IntegerArgumentType.integer(1))
                                        .executes(context -> {
                                            int ticks = IntegerArgumentType.getInteger(context, "ticks");
                                            AutoDuperClient.getInstance().setMoveItemsDelay(ticks);
                                            context.getSource().sendFeedback(Text.literal("Move items delay set to " + ticks + " ticks"));
                                            return 1;
                                        })))
                        .then(literal("chest")
                                .then(argument("ticks", IntegerArgumentType.integer(1))
                                        .executes(context -> {
                                            int ticks = IntegerArgumentType.getInteger(context, "ticks");
                                            AutoDuperClient.getInstance().setChestApplyDelay(ticks);
                                            context.getSource().sendFeedback(Text.literal("Chest apply delay set to " + ticks + " ticks"));
                                            return 1;
                                        })))
                        .then(literal("dismount")
                                .then(argument("ticks", IntegerArgumentType.integer(1))
                                        .executes(context -> {
                                            int ticks = IntegerArgumentType.getInteger(context, "ticks");
                                            AutoDuperClient.getInstance().setDismountDelay(ticks);
                                            context.getSource().sendFeedback(Text.literal("Dismount delay set to " + ticks + " ticks"));
                                            return 1;
                                        })))));
    }
}
