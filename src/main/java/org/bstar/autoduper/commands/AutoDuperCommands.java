package org.bstar.autoduper.commands;

import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.minecraft.text.Text;
import org.bstar.autoduper.client.AutoDuperClient;

public class AutoDuperCommands {
    public static void register() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(
                    ClientCommandManager.literal("autoduper")
                            .then(ClientCommandManager.literal("start")
                                    .executes(context -> {
                                        AutoDuperClient.getInstance().startDupe();
                                        context.getSource().sendFeedback(Text.literal("Started duping"));
                                        return 1;
                                    })
                            )
                            .then(ClientCommandManager.literal("stop")
                                    .executes(context -> {
                                        AutoDuperClient.getInstance().stopDupe();
                                        context.getSource().sendFeedback(Text.literal("Stopped duping"));
                                        return 1;
                                    })
                            )
            );
        });
    }
}
