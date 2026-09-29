package io.blossombree.petaleconomy.gui.screens;

import io.blossombree.petaleconomy.gui.PetalButton;
import io.blossombree.petaleconomy.util.Constants;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

import java.util.function.Consumer;

public class ColorPickerScreen extends Screen {
    private final Screen parent;
    private final Consumer<Integer> onColorSelected;

    private static final int PICKER_SIZE = 120;
    private static final int HUE_WIDTH = 120;
    private static final int HUE_HEIGHT = 10;

    private int pickerX;
    private int pickerY;
    private int hueX;
    private int hueY;

    private float hue;
    private float saturation;
    private float value;

    private boolean draggingPicker;
    private boolean draggingHue;

    public ColorPickerScreen(Screen parent, int initialColor, Consumer<Integer> onColorSelected) {
        super(Component.translatable("gui.petal_economy.color_picker"));

        this.parent = parent;
        this.onColorSelected = onColorSelected;

        setFromRGB(initialColor);
    }

    @Override
    protected void init() {
        super.init();

        pickerX = leftPos();
        pickerY = topPos();

        hueX = leftPos();
        hueY = pickerY + PICKER_SIZE + 14;

        addRenderableWidget(new PetalButton(leftPos() + 35, hueY + 25, 42, 14, Component.translatable("gui.petal_economy.done"), Constants.MENU_BUTTON_NORMAL, Constants.MENU_BUTTON_SELECTED, button -> confirm()));
    }

    private int leftPos() {
        return (width - PICKER_SIZE) / 2;
    }

    private int topPos() {
        return (height - 190) / 2;
    }

    private void confirm() {
        onColorSelected.accept(getCurrentColor());
        minecraft.setScreen(parent);
    }

    private int getCurrentColor() {
        return Mth.hsvToRgb(hue, saturation, value);
    }

    private void setFromRGB(int color) {
        float red = ((color >> 16) & 0xFF) / 255.0F;
        float green = ((color >> 8) & 0xFF) / 255.0F;
        float blue = (color & 0xFF) / 255.0F;

        float max = Math.max(red, Math.max(green, blue));
        float min  = Math.min(red, Math.min(green, blue));
        float delta = max - min;

        value = max;

        if (max == 0.0F) {
            saturation = 0.0F;
        } else {
            saturation = delta / max;
        }

        if (delta == 0.0F) {
            hue = 0.0F;
        } else if (max == red) {
            hue = ((green - blue) / delta) % 6.0F;
        } else if (max == green) {
            hue = ((blue - red) / delta) + 2.0F;
        } else {
            hue = ((red - green) / delta) + 4.0F;
        }

        hue /= 6.0F;

        if (hue < 0.0F) {
            hue += 1.0F;
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics);

        super.render(guiGraphics, mouseX, mouseY, partialTick);

        guiGraphics.drawCenteredString(font, title, width / 2, pickerY - 20, 0xFFFFFF);

        drawColorPicker(guiGraphics);
        drawHueBar(guiGraphics);
        drawPreview(guiGraphics);
    }

    private void drawColorPicker(GuiGraphics guiGraphics) {
        /* Draw the saturation/value square
            Horizontally:
                0% saturation -> 100% saturation
            Vertically:
                100% value -> 0% value
         */

        int hueColor = Mth.hsvToRgb(hue, 1.0F, 1.0F);

        for (int y = 0; y < PICKER_SIZE; y++) {
            float valueAtRow = 1.0F - (float) y / (PICKER_SIZE - 1);

            int whiteColor = 0xFFFFFFFF;
            int blackColor = 0xFF000000;

            for (int x = 0; x < PICKER_SIZE; x++) {
                float saturationAtColumn = (float) x / (PICKER_SIZE - 1);

                int color = blend(whiteColor, 0xFF000000 | hueColor, saturationAtColumn);

                color = blend(0xFF000000 | color, blackColor, 1.0F - valueAtRow);

                guiGraphics.fill(pickerX + x, pickerY + y, pickerX + x + 1, pickerY + y + 1, color);
            }
        }

        //Picker border
        guiGraphics.fill(pickerX - 1, pickerY - 1, pickerX + PICKER_SIZE + 1, pickerY, 0xFFFFFFFF);
        guiGraphics.fill(pickerX - 1, pickerY, pickerX, pickerY + PICKER_SIZE, 0xFFFFFFFF);
        guiGraphics.fill(pickerX + PICKER_SIZE, pickerY, pickerX + PICKER_SIZE + 1, pickerY + PICKER_SIZE, 0xFFFFFFFF);

        //Selection cursor
        int cursorX = pickerX + Math.round(saturation * (PICKER_SIZE - 1));
        int cursorY = pickerY + Math.round((1.0F - value) * (PICKER_SIZE - 1));

        guiGraphics.pose().pushPose();

        guiGraphics.fill(cursorX - 4, cursorY - 4, cursorX + 5, cursorY + 5, 0xFF000000);
        guiGraphics.fill(cursorX - 3, cursorY - 3, cursorX + 4, cursorY + 4, 0xFFFFFFFF);

        guiGraphics.pose().popPose();
    }

    private void drawHueBar(GuiGraphics guiGraphics) {
        for (int x = 0; x < HUE_WIDTH; x++) {
            float hueAtColumn = (float) x / (HUE_WIDTH - 1);

            int color = 0xFF000000 | Mth.hsvToRgb(hueAtColumn, 1.0F, 1.0F);

            guiGraphics.fill(hueX + x, hueY, hueX + x + 1, hueY + HUE_HEIGHT, color);
        }

        //Hue cursor
        int cursorX = hueX + Math.round(hue * (HUE_WIDTH - 1));

        guiGraphics.fill(cursorX - 2, hueY - 2, cursorX + 3, hueY + HUE_HEIGHT + 2, 0xFFFFFFFF);
        guiGraphics.fill(cursorX - 1, hueY - 1, cursorX + 2, hueY + HUE_HEIGHT + 1, 0xFF000000);
    }

    private void drawPreview(GuiGraphics guiGraphics) {
        int previewX = hueX + HUE_WIDTH + 12;
        int previewY = hueY;

        guiGraphics.fill(previewX, previewY, previewX + 20, previewY + 20, 0xFF000000 | getCurrentColor());
        guiGraphics.fill(previewX - 1, previewY - 1, previewX + 21, previewY, 0xFFFFFFFF);
        guiGraphics.fill(previewX - 1, previewY + 20, previewX + 21, previewY + 21, 0xFFFFFFFF);
        guiGraphics.fill(previewX - 1, previewY, previewX, previewY + 20, 0xFFFFFFFF);
        guiGraphics.fill(previewX + 20, previewY, previewX + 21, previewY + 20, 0xFFFFFFFF);
    }

    private int blend(int first, int second, float amount) {
        amount = Mth.clamp(amount, 0.0F, 1.0F);

        int firstRed = (first >> 16) & 0xFF;
        int firstGreen = (first >> 8) & 0xFF;
        int firstBlue = first & 0xFF;

        int secondRed = (second >> 16) & 0xFF;
        int secondGreen = (second >> 8) & 0xFF;
        int secondBlue = second & 0xFF;

        int red = (int) Mth.lerp(amount, firstRed, secondRed);
        int green = (int) Mth.lerp(amount, firstGreen, secondGreen);
        int blue = (int) Mth.lerp(amount, firstBlue, secondBlue);

        return 0xFF000000 | (red << 16) | (green << 8) | blue;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            if (isInsidePicker(mouseX, mouseY)) {
                draggingPicker = true;
                updatePicker(mouseX, mouseY);
                return true;
            }

            if (isInsideHueBar(mouseX, mouseY)) {
                draggingHue = true;
                updateHue(mouseX);
                return true;
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (button == 0) {
            if (draggingPicker) {
                updatePicker(mouseX, mouseY);
                return true;
            }

            if (draggingHue) {
                updateHue(mouseX);
                return true;
            }
        }

        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0) {
            draggingPicker = false;
            draggingHue = false;
        }

        return super.mouseReleased(mouseX, mouseY, button);
    }

    private boolean isInsidePicker(double mouseX, double mouseY) {
        return mouseX >= pickerX && mouseX < pickerX + PICKER_SIZE && mouseY >= pickerY && mouseY < pickerY + PICKER_SIZE;
    }

    private boolean isInsideHueBar(double mouseX, double mouseY) {
        return mouseX >= hueX && mouseX < hueX + HUE_WIDTH && mouseY >= hueY && mouseY < hueY + HUE_HEIGHT;
    }

    private void updatePicker(double mouseX, double mouseY) {
        saturation = Mth.clamp((float) ((mouseX - pickerX) / (PICKER_SIZE - 1)), 0.0F, 1.0F);
        value = Mth.clamp(1.0F - (float) ((mouseY - pickerY) / (PICKER_SIZE - 1)), 0.0F, 1.0F);
    }

    private void updateHue(double mouseX) {
        hue = Mth.clamp((float) ((mouseX - hueX) / (HUE_WIDTH - 1)), 0.0F, 1.0F);
    }

    @Override
    public void onClose() {
        minecraft.setScreen(parent);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
