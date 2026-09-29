package io.blossombree.petaleconomy.gui.screens;

import com.mojang.authlib.GameProfile;
import io.blossombree.petaleconomy.gui.PetalButton;
import io.blossombree.petaleconomy.gui.components.PlayerList;
import io.blossombree.petaleconomy.gui.menus.ManageAccessMenu;
import io.blossombree.petaleconomy.network.GrantAccessPacket;
import io.blossombree.petaleconomy.network.PetalNetwork;
import io.blossombree.petaleconomy.network.RevokeAccessPacket;
import io.blossombree.petaleconomy.util.Constants;
import io.blossombree.petaleconomy.util.MethodUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import java.util.List;
import java.util.UUID;

public class ManageAccessScreen extends AbstractPetalScreen<ManageAccessMenu> {
    private EditBox searchBox;
    private PlayerList searchList, authorizedList;
    private UUID playerEntry;
    private PetalButton grantButton;
    private PetalButton revokeButton;

    @Override
    protected void init() {
        super.init();

        searchList = new PlayerList(leftPos + 7, topPos + 49, 76, 30, this::selectPlayer);
        authorizedList = new PlayerList(leftPos + 93, topPos + 40, 76, 30, this::selectAuthorizedPlayer);
        searchBox = new EditBox(font, leftPos + 7, topPos + 40, 76, 9, Component.translatable("gui.petal_economy.player_name_edit"));
        searchBox.setMaxLength(15);
        searchBox.setResponder(text -> updateSearchResults());

        grantButton = new PetalButton(leftPos + 13, topPos + 78, 42, 14, Component.translatable("gui.petal_economy.grant"), Constants.MENU_BUTTON_NORMAL, Constants.MENU_BUTTON_SELECTED, button -> {
            if (playerEntry == null) {
                return;
            }

            PetalNetwork.CHANNEL.sendToServer(new GrantAccessPacket(playerEntry));
            playerEntry = null;
            searchBox.setValue("");
            updateSearchResults();
        });

        revokeButton = new PetalButton(leftPos + 111, topPos + 78, 42, 14, Component.translatable("gui.petal_economy.remove"), Constants.MENU_BUTTON_NORMAL, Constants.MENU_BUTTON_SELECTED, button -> {
            if (playerEntry == null) {
                return;
            }
            PetalNetwork.CHANNEL.sendToServer(new RevokeAccessPacket(playerEntry));
            playerEntry = null;
            updateAuthorizedList();
        });

        addRenderableWidget(grantButton);
        addRenderableWidget(revokeButton);
        addRenderableWidget(searchBox);
        addRenderableWidget(authorizedList);
        addRenderableWidget(backButton);
    }

    public ManageAccessScreen(ManageAccessMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, Component.translatable("gui.petal_economy.manage_access"), Constants.MANAGE_ACCESS_MENU);
    }

    private void selectPlayer(UUID playerUUID) {
        playerEntry = playerUUID;

        String gamertag = MethodUtils.getPlayerName(playerUUID);
        searchBox.setValue(gamertag);
    }

    private void selectAuthorizedPlayer(UUID playerUUID) {
        playerEntry = playerUUID;
    }

    private void updateAuthorizedList() {
        authorizedList.setPlayers(menu.getAuthorizedUsers());
    }

    private List<UUID> getOnlinePlayerUUIDs() {
        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.getConnection() == null) {
            return List.of();
        }

        return minecraft.getConnection().getOnlinePlayers().stream().map(PlayerInfo::getProfile).map(GameProfile::getId).toList();
    }

    private void updateSearchResults() {
        String search = searchBox.getValue().trim().toLowerCase();

        List<UUID> results = getOnlinePlayerUUIDs().stream().filter(uuid -> {
            String gamertag = MethodUtils.getPlayerName(uuid); return gamertag.toLowerCase().contains(search);
        }).filter(uuid -> !menu.getAuthorizedUsers().contains(uuid)).filter(uuid -> !uuid.equals(Minecraft.getInstance().player.getUUID())).toList();

        searchList.setPlayers(results);
    }

    @Override
    protected void containerTick() {
        super.containerTick();

        if (searchBox.isFocused()) {
            addRenderableWidget(searchList);
        }
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        super.renderBg(guiGraphics, partialTick, mouseX, mouseY);

        drawCenteredText(guiGraphics, Component.translatable("gui.petal_economy.name").getString(), 7, 21);
        drawCenteredText(guiGraphics, Component.translatable("gui.petal_economy.player").getString(), 7, 31);
    }
}
