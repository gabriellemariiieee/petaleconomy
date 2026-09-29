package io.blossombree.petaleconomy.gui.components;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class PlayerList extends AbstractWidget {
    private final List<PlayerEntry> entries = new ArrayList<>();
    private final Consumer<UUID> onPlayerSelected;

    private int scrollOffset = 0;

    public PlayerList(int x, int y, int width, int height, Consumer<UUID> onPlayerSelected) {
        super(x, y, width, height, Component.empty());

        this.onPlayerSelected = onPlayerSelected;
    }

    public void setPlayers(List<UUID> playerUUIDs) {
        entries.clear();

        for (UUID playerUUID : playerUUIDs) {
            entries.add(createEntry(playerUUID));
        }

        scrollOffset = 0;
        updateEntryPositions();
    }

    protected PlayerEntry createEntry(UUID playerUUID) {
        return new PlayerEntry(getX(), getY() , playerUUID, button -> onPlayerSelected.accept(playerUUID));
    }

    private void updateEntryPositions() {
        for (int i = 0; i < entries.size(); i++) {
            PlayerEntry entry = entries.get(i);

            entry.setX(getX());
            entry.setY(getY() + i * entry.getHeight() - scrollOffset);
        }
    }

    private int getContentHeight() {
        return entries.size() * PlayerEntry.HEIGHT;
    }

    private int getMaxScroll() {
        return Math.max(
                0, getContentHeight() - getHeight()
        );
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.enableScissor(getX(), getY(), getX() + getWidth(), getY() + getHeight());

        for (PlayerEntry entry : entries) {
            entry.render(guiGraphics, mouseX, mouseY, partialTick);
        }

        guiGraphics.disableScissor();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!isMouseOver(mouseX, mouseY)) {
            return false;
        }

        for (PlayerEntry entry : entries) {
            if (entry.mouseClicked(mouseX, mouseY, button)) {
                return true;
            }
        }

        return false;
    }

    //@Override
    public boolean mouseRelease(double mouseX, double mouseY, int button) {
        for (PlayerEntry entry : entries) {
            if (entry.mouseReleased(mouseX, mouseY, button)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (!isMouseOver(mouseX, mouseY)) {
            return false;
        }

        scrollOffset -= (int) (delta * PlayerEntry.HEIGHT);

        scrollOffset = Mth.clamp(scrollOffset, 0, getMaxScroll());

        updateEntryPositions();

        return true;
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput p_259858_) {

    }
}
