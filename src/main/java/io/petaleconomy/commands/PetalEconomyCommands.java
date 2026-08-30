package io.petaleconomy.commands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import io.petaleconomy.capabilities.PetalCapabilities;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class PetalEconomyCommands {
    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(
                Commands.literal("getBalance")
                        .executes(context -> {
                            ServerPlayer player = context.getSource().getPlayerOrException();

                            player.getCapability(PetalCapabilities.PETAL_BALANCE).ifPresent(balance -> {
                                player.sendSystemMessage(Component.literal("Balance: " + balance.getBalance()));
                            });

                            return 1;
                        })
        );
        event.getDispatcher().register(
                Commands.literal(("setBalance"))
                        .then(Commands.argument("target", EntityArgument.player())
                                .then(Commands.argument("value", IntegerArgumentType.integer())
                                        .executes(context -> {
                                            ServerPlayer targetPlayer = EntityArgument.getPlayer(context, "target");
                                            int targetValue = IntegerArgumentType.getInteger(context, "value");
                                            CommandSourceStack source = context.getSource();

                                            targetPlayer.getCapability(PetalCapabilities.PETAL_BALANCE).ifPresent(balance -> {
                                                balance.setBalance(targetValue);
                                                targetPlayer.sendSystemMessage(Component.literal("Balance: " + balance.getBalance()));
                                            });

                                            return 1;
                                        })
                                )
                        )
        );
        event.getDispatcher().register(
                Commands.literal(("addPetals"))
                        .then(Commands.argument("target", EntityArgument.player())
                                .then(Commands.argument("value", IntegerArgumentType.integer())
                                        .executes(context -> {
                                            ServerPlayer targetPlayer = EntityArgument.getPlayer(context, "target");
                                            int targetValue = IntegerArgumentType.getInteger(context, "value");
                                            CommandSourceStack source = context.getSource();

                                            targetPlayer.getCapability(PetalCapabilities.PETAL_BALANCE).ifPresent(balance -> {
                                                balance.addPetals(targetValue);
                                                targetPlayer.sendSystemMessage(Component.literal("Balance: " + balance.getBalance()));
                                            });

                                            return 1;
                                        })
                                )
                        )
        );
        event.getDispatcher().register(
                Commands.literal(("removePetals"))
                        .then(Commands.argument("target", EntityArgument.player())
                                .then(Commands.argument("value", IntegerArgumentType.integer())
                                        .executes(context -> {
                                            ServerPlayer targetPlayer = EntityArgument.getPlayer(context, "target");
                                            int targetValue = IntegerArgumentType.getInteger(context, "value");
                                            CommandSourceStack source = context.getSource();

                                            targetPlayer.getCapability(PetalCapabilities.PETAL_BALANCE).ifPresent(balance -> {
                                                balance.removePetals(targetValue);
                                                targetPlayer.sendSystemMessage(Component.literal("Balance: " + balance.getBalance()));
                                            });

                                            return 1;
                                        })
                                )
                        )
        );
    }
}
