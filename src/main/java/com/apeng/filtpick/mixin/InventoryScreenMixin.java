package com.apeng.filtpick.mixin;

import com.apeng.filtpick.FiltPick;
import com.apeng.filtpick.guis.widget.LegacyTexturedButtonWidget;
import com.apeng.filtpick.mixin.accessor.InventoryScreenAccessor;
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
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.apeng.filtpick.util.Config.CONFIG;

@Mixin(InventoryScreen.class)
public abstract class InventoryScreenMixin extends AbstractInventoryScreen<PlayerScreenHandler> implements RecipeBookProvider {

    @Unique
    private static final Identifier FILTPICK_ENTRY_TEXTURE = Identifier.of(FiltPick.ID, "gui/entry_button.png");

    @Unique
    private final static int deviationOfFiltPickButton = 23;

    @Unique
    private TexturedButtonWidget recipeBookButton;

    @Unique
    private TexturedButtonWidget filtPickEntryButton;

    public InventoryScreenMixin(PlayerScreenHandler screenHandler, PlayerInventory playerInventory, Text text) {
        super(screenHandler, playerInventory, text);
    }

    @Redirect(method = "init()V", at = @At(value = "INVOKE", target = "net/minecraft/client/gui/screen/ingame/InventoryScreen.addDrawableChild(Lnet/minecraft/client/gui/Element;)Lnet/minecraft/client/gui/Element;"))
    private Element configureRecipeBookButton(InventoryScreen instance, Element element) {
        initRecipeBookButton(((InventoryScreenAccessor)this).getRecipeBook());
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
    private void initRecipeBookButton(RecipeBookWidget recipeBook) {
        recipeBookButton = new TexturedButtonWidget(this.x + 104, this.height / 2 - 22, 20, 18, RecipeBookWidget.BUTTON_TEXTURES, button -> {
            recipeBook.toggleOpen();
            this.x = recipeBook.findLeftEdge(this.width, this.backgroundWidth);
            button.setPosition(this.x + 104, this.height / 2 - 22);
            recipeBookButton.setPosition(this.x + 104 + CONFIG.getxOffset(), this.height / 2 - 22 + CONFIG.getyOffset());
            filtPickEntryButton.setPosition(this.x + 104 + deviationOfFiltPickButton, this.height / 2 - 22);
            ((InventoryScreenAccessor) this).setMouseDown(true);
        });
    }

    @Unique
    private void initFiltPickEntryButton() {
        filtPickEntryButton = new LegacyTexturedButtonWidget(this.x + 104 + deviationOfFiltPickButton, this.height / 2 - 22, 20, 18, 0, 0, 19, FILTPICK_ENTRY_TEXTURE, button -> ClientPlayNetworking.send(new OpenFiltPickScreenC2SPacket()));
        setTooltip2EntryButton();
    }

    @Unique
    private void setTooltip2EntryButton() {
        filtPickEntryButton.setTooltip(Tooltip.of(Text.translatable("filtpick_screen_name").formatted(Formatting.YELLOW).append(": ").append(Text.translatable("entry_button_tooltip"))));
        filtPickEntryButton.setTooltipDelay(500);
    }

}
