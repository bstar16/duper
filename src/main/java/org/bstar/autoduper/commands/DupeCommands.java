package org.bstar.autoduper.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import org.bstar.autoduper.config.ConfigManager;

public class DupeCommands {
    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(ClientCommandManager.literal("dupe")
                .then(ClientCommandManager.literal("start")
                        .executes(context -> {
                            org.bstar.autoduper.client.AutoDuperClient.getInstance().startDupe();
                            return Command.SINGLE_SUCCESS;
                        }))
                .then(ClientCommandManager.literal("stop")
                        .executes(context -> {
                            org.bstar.autoduper.client.AutoDuperClient.getInstance().stopDupe();
                            return Command.SINGLE_SUCCESS;
                        }))
        );
    }
}