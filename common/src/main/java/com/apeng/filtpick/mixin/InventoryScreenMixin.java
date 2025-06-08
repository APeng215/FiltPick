package com.apeng.filtpick.mixin;

import com.apeng.filtpick.Common;
import com.apeng.filtpick.config.FiltPickClientConfig;
import com.apeng.filtpick.gui.widget.LegacyTexturedButton;
import com.apeng.filtpick.network.OpenFiltPickScreenC2SPacket;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.navigation.ScreenPosition;
import net.minecraft.client.gui.screens.inventory.AbstractRecipeBookScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.InventoryMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.time.Duration;

@Mixin(InventoryScreen.class)
public abstract class InventoryScreenMixin extends AbstractRecipeBookScreen<InventoryMenu> {

    @Shadow protected abstract ScreenPosition getRecipeBookButtonPosition();
    private static final ResourceLocation FILTPICK_ENTRY_TEXTURE = ResourceLocation.tryBuild(Common.MOD_ID, "gui/entry_button.png");
    private ImageButton filtPickEntryButton;

    public InventoryScreenMixin(InventoryMenu menu, RecipeBookComponent<?> recipeBookComponent, Inventory playerInventory, Component title) {
        super(menu, recipeBookComponent, playerInventory, title);
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void initFiltPickEntryButton(CallbackInfo ci) {
        if (!this.minecraft.player.hasInfiniteMaterials()) {
            filtPickEntryButton = new LegacyTexturedButton(
                    getFiltPickEntryButtonPositionX(),
                    getFiltPickEntryBUttonPositionY(),
                    20,
                    18,
                    0,
                    0,
                    19,
                    FILTPICK_ENTRY_TEXTURE,
                    button -> Common.getNetworkHandler().sendToServer(new OpenFiltPickScreenC2SPacket())
            );
            setTooltip4EntryButton();
            this.addRenderableWidget(filtPickEntryButton);
        }
    }

    @Override
    public void onRecipeBookButtonClick() {
        super.onRecipeBookButtonClick();
        updateFiltPickEntryButtonPosition();
    }

    private void updateFiltPickEntryButtonPosition() {
        filtPickEntryButton.setPosition(getFiltPickEntryButtonPositionX(), getFiltPickEntryBUttonPositionY());
    }

    private int getFiltPickEntryButtonPositionX() {
        return getRecipeBookButtonPosition().x() + getFiltPickEntryButtonOffsetX() + 20 + 2;
    }

    private int getFiltPickEntryBUttonPositionY() {
        return getRecipeBookButtonPosition().y() + getFiltPickEntryButtonOffsetY();
    }

    private static Integer getFiltPickEntryButtonOffsetY() {
        return Common.getClientConfig().buttonOffsets.get(FiltPickClientConfig.ButtonName.ENTRY_BUTTON).verticalOffset().get();
    }

    private static Integer getFiltPickEntryButtonOffsetX() {
        return Common.getClientConfig().buttonOffsets.get(FiltPickClientConfig.ButtonName.ENTRY_BUTTON).horizontalOffset().get();
    }

    private void setTooltip4EntryButton() {
        filtPickEntryButton.setTooltip(Tooltip.create(Component.translatable("filtpick_screen_name").withStyle(ChatFormatting.YELLOW).append(": ").append(Component.translatable("entry_button_tooltip"))));
        filtPickEntryButton.setTooltipDelay(Duration.ofMillis(500));
    }

}
