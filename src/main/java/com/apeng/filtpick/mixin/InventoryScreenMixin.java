package com.apeng.filtpick.mixin;

import com.apeng.filtpick.FiltPick;
import com.apeng.filtpick.FiltPickClient;
import com.apeng.filtpick.config.FPConfigManager;
import com.apeng.filtpick.guis.widget.LegacyTexturedButtonWidget;
import com.apeng.filtpick.network.OpenFiltPickScreenC2SPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.screen.recipebook.RecipeBookProvider;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InventoryScreen.class)
public abstract class InventoryScreenMixin extends AbstractInventoryScreen<PlayerScreenHandler> implements RecipeBookProvider {

    @Shadow private @Final RecipeBookWidget recipeBook;
    @Shadow private boolean mouseDown;

    @Unique private static final Identifier FILTPICK_ENTRY_TEXTURE = Identifier.of(FiltPick.ID, "gui/entry_button.png");
    @Unique private static final int deviationOfFiltPickButton = 23;
    @Unique private static int filtPickEntryButtonPosX;
    @Unique private static int filtPickEntryButtonPosY;
    @Unique private static int recipeButtonPosX;
    @Unique private static int recipeButtonPosY;
    @Unique private TexturedButtonWidget recipeBookButton;
    @Unique private TexturedButtonWidget filtPickEntryButton;

    public InventoryScreenMixin(PlayerScreenHandler screenHandler, PlayerInventory playerInventory, Text text) {
        super(screenHandler, playerInventory, text);
    }

    @Redirect(method = "init()V", at = @At(value = "INVOKE", target = "net/minecraft/client/gui/screen/ingame/InventoryScreen.addDrawableChild(Lnet/minecraft/client/gui/Element;)Lnet/minecraft/client/gui/Element;"))
    private Element configureRecipeBookButton(InventoryScreen instance, Element element) {
        initRecipeBookButton();
        addRecipeBookButton();
        return element;
    }

    @Inject(method = "init()V", at = @At("TAIL"))
    private void initAndAddFiltPickEntryButton(CallbackInfo ci) {
        initFiltPickEntryButton();
        addFiltPickEntryButton();
    }

    @Unique
    private void addRecipeBookButton() {
        this.addDrawableChild(recipeBookButton);
    }

    @Unique
    private void addFiltPickEntryButton() {
        this.addDrawableChild(filtPickEntryButton);
    }

    @Unique
    private void initRecipeBookButton() {
        calculateRecipeButtonPos();
        recipeBookButton = new TexturedButtonWidget(recipeButtonPosX, recipeButtonPosY, 20, 18, RecipeBookWidget.BUTTON_TEXTURES, button -> {
            recipeBook.toggleOpen();
            this.x = recipeBook.findLeftEdge(this.width, this.backgroundWidth);
            button.setPosition(this.x + 104, this.height / 2 - 22);
            calculateRecipeButtonPos();
            recipeBookButton.setPosition(recipeButtonPosX, recipeButtonPosY);
            calculateEntryButtonPos();
            filtPickEntryButton.setPosition(filtPickEntryButtonPosX, filtPickEntryButtonPosY);
            this.mouseDown = true;
        });
    }

    /**
     * Should be invoked every time before the positions are accessed.
     */
    @Unique
    private void calculateRecipeButtonPos() {
        recipeButtonPosX = this.x + 104 + FiltPickClient.CONFIG_MANAGER.getWidgetPosOffset(FPConfigManager.WidgetOffsetConfig.Key.RECIPE_BUTTON).xOffset();
        recipeButtonPosY = this.height / 2 - 22 + FiltPickClient.CONFIG_MANAGER.getWidgetPosOffset(FPConfigManager.WidgetOffsetConfig.Key.RECIPE_BUTTON).yOffset();
    }

    @Unique
    private void initFiltPickEntryButton() {
        calculateEntryButtonPos();
        filtPickEntryButton = new LegacyTexturedButtonWidget(
                filtPickEntryButtonPosX,
                filtPickEntryButtonPosY,
                20,
                18,
                0,
                0,
                19,
                FILTPICK_ENTRY_TEXTURE,
                button -> ClientPlayNetworking.send(new OpenFiltPickScreenC2SPacket())
        );
        setTooltip2EntryButton();
    }

    /**
     * Should be invoked every time before the positions are accessed.
     */
    @Unique
    private void calculateEntryButtonPos() {
        filtPickEntryButtonPosX = this.x + 104 + deviationOfFiltPickButton + FiltPickClient.CONFIG_MANAGER.getWidgetPosOffset(FPConfigManager.WidgetOffsetConfig.Key.ENTRY_BUTTON).xOffset();
        filtPickEntryButtonPosY = this.height / 2 - 22 + FiltPickClient.CONFIG_MANAGER.getWidgetPosOffset(FPConfigManager.WidgetOffsetConfig.Key.ENTRY_BUTTON).yOffset();
    }

    @Unique
    private void setTooltip2EntryButton() {
        filtPickEntryButton.setTooltip(Tooltip.of(Text.translatable("filtpick_screen_name").formatted(Formatting.YELLOW).append(": ").append(Text.translatable("entry_button_tooltip"))));
        filtPickEntryButton.setTooltipDelay(500);
    }

}
