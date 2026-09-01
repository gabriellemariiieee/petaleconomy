package io.petaleconomy.commands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import io.petaleconomy.capabilities.PetalCapabilities;
import io.petaleconomy.economy.PetalAccount;
import io.petaleconomy.economy.PetalAccountManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.awt.event.ComponentListener;

public class PetalEconomyCommands {
    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(
                Commands.literal("getBalance")
                        .then(Commands.argument("target", EntityArgument.player())
                            .executes(context -> {
                                ServerPlayer targetPlayer = EntityArgument.getPlayer(context, "target");
                                PetalAccountManager accountManger = PetalAccountManager.get(targetPlayer.serverLevel());
                                PetalAccount account = accountManger.getAccountForPlayer(targetPlayer);
                                CommandSourceStack source = context.getSource();

                                if (account == null) {
                                    source.sendFailure(Component.literal("No Petal account found."));
                                    return 0;
                                }
                                targetPlayer.sendSystemMessage(Component.literal("Balance: " + account.getBalance()));

                                return 1;
                        }))
        );
        event.getDispatcher().register(
                Commands.literal(("setBalance"))
                        .then(Commands.argument("target", EntityArgument.player())
                                .then(Commands.argument("value", IntegerArgumentType.integer())
                                        .executes(context -> {
                                            ServerPlayer targetPlayer = EntityArgument.getPlayer(context, "target");
                                            int targetValue = IntegerArgumentType.getInteger(context, "value");
                                            CommandSourceStack source = context.getSource();
                                            PetalAccountManager accountManger = PetalAccountManager.get(targetPlayer.serverLevel());
                                            PetalAccount account = accountManger.getAccountForPlayer(targetPlayer);

                                            if (account == null) {
                                                source.sendFailure(Component.literal("No Petal account found."));
                                                return 0;
                                            }
                                            accountManger.set(account.getAccountID(), targetValue);
                                            targetPlayer.sendSystemMessage(Component.literal("New Balance: " + accountManger.getAccount(account.getAccountID())));
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
                                            PetalAccountManager accountManger = PetalAccountManager.get(targetPlayer.serverLevel());
                                            PetalAccount account = accountManger.getAccountForPlayer(targetPlayer);

                                            if (account == null) {
                                                source.sendFailure(Component.literal("No Petal account found."));
                                                return 0;
                                            }
                                            accountManger.deposit(account.getAccountID(), targetValue);
                                            targetPlayer.sendSystemMessage(Component.literal("Added " + targetValue + " | New Balance: " + accountManger.getAccount(account.getAccountID())));
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
                                            PetalAccountManager accountManger = PetalAccountManager.get(targetPlayer.serverLevel());
                                            PetalAccount account = accountManger.getAccountForPlayer(targetPlayer);

                                            if (account == null) {
                                                source.sendFailure(Component.literal("No Petal account found."));
                                                return 0;
                                            }
                                            accountManger.withdraw(account.getAccountID(), targetValue);
                                            targetPlayer.sendSystemMessage(Component.literal("Withdrew " + targetValue + " | New Balance: " + accountManger.getAccount(account.getAccountID())));
                                            return 1;
                                        })
                                )
                        )
        );
    }
}
