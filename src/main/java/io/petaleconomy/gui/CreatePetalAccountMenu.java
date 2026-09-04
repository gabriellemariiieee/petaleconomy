package io.petaleconomy.gui;

import io.petaleconomy.block.ModBlocks;
import io.petaleconomy.block.entities.AutomaticPetalDispenserBlockEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class CreatePetalAccountMenu extends AbstractPetalMenu {
    public final AutomaticPetalDispenserBlockEntity blockEntity;
    private final Level level;
    private boolean primaryAccount = false;

    public CreatePetalAccountMenu(int pContainerId, Inventory inv, FriendlyByteBuf extraData) {
        this(pContainerId, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()));
    }

    public CreatePetalAccountMenu(int pContainerId, Inventory inv, BlockEntity entity) {
        super(ModMenuTypes.CREATE_PETAL_ACCOUNT_MENU.get(), pContainerId, inv.player);
        blockEntity = ((AutomaticPetalDispenserBlockEntity) entity);
        this.level = inv.player.level();

        blockEntity.setMenu(this);
        addPlayerInventory(inv);
        addCardSlot(blockEntity.getItemHandler(), 26, 52);
        addInputSlot(80);
        this.primaryAccount = blockEntity.isPrimaryAccount;
    }

    protected void createAccount() {
        //creates account and bounds card if present

    }

    protected void setAccountName() {
        //takes the account name from input and sends it to create account
    }

    protected void depositStartingBalance() {

    }

    protected void ejectStartingBalance() {

    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return stillValid(ContainerLevelAccess.create(pPlayer.level(), blockEntity.getBlockPos()), pPlayer, ModBlocks.AUTOMATIC_PETAL_DISPENSER.get());
    }
}
