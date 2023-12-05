package com.apeng.filtpick.mixin;


import com.apeng.filtpick.NetWorkingIDs;
import com.apeng.filtpick.guis.custom.FiltPickScreen;
import com.apeng.filtpick.mixin.accessor.InventoryScreenAccessor;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.screen.recipebook.RecipeBookProvider;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.apeng.filtpick.Config.CONFIG;

@Mixin(InventoryScreen.class)
public abstract class InventoryScreenMixin extends AbstractInventoryScreen<PlayerScreenHandler> implements RecipeBookProvider {
    private static final Identifier RECIPE_BUTTON_TEXTURE = new Identifier("textures/gui/recipe_button.png");
    private static final Identifier FILTPICK_ENTRY_BLACKLIST = new Identifier("filtpick", "gui/filtpick_entry_blacklist.png");
    private static final Identifier FILTPICK_ENTRY_WHITELIST = new Identifier("filtpick", "gui/filtpick_entry_whitelist.png");
    private static final Identifier FILTPICK_ENTRY_WHITELIST_DES_ON = new Identifier("filtpick", "gui/filtpick_entry_whitelist_des_on.png");
    private static final Identifier FILTPICK_ENTRY_BLACKLIST_DES_ON = new Identifier("filtpick", "gui/filtpick_entry_blacklist_des_on.png");

    public InventoryScreenMixin(PlayerScreenHandler screenHandler, PlayerInventory playerInventory, Text text) {
        super(screenHandler, playerInventory, text);
    }

    @Inject(method = "init()V", at = @At(value = "INVOKE", target = "net/minecraft/client/gui/screen/ingame/InventoryScreen.addDrawableChild(Lnet/minecraft/client/gui/Element;)Lnet/minecraft/client/gui/Element;"), cancellable = true)
    private void addFiltPickAddEntryButton(CallbackInfo ci) {

        ClientPlayNetworking.send(NetWorkingIDs.REQUIRE_SYN_C2S, PacketByteBufs.empty());

        int deviationOfFiltPickButton = 25;
        RecipeBookWidget recipeBook = ((InventoryScreenAccessor) this).getRecipeBook();
        TexturedButtonWidget recipeBookButton;
        TexturedButtonWidget filtPickButton;
        filtPickButton = initFiltPickButton(deviationOfFiltPickButton);
        recipeBookButton = new TexturedButtonWidget(this.x + 104, this.height / 2 - 22, 20, 18, 0, 0, 19, RECIPE_BUTTON_TEXTURE, button
                -> {
            recipeBook.toggleOpen();
            this.x = recipeBook.findLeftEdge(this.width, this.backgroundWidth);
            button.setPosition(this.x + 104, this.height / 2 - 22);
            filtPickButton.setPosition(this.x + 104 + deviationOfFiltPickButton + CONFIG.getxOffset(), this.height / 2 - 22 + CONFIG.getyOffset());
            ((InventoryScreenAccessor) this).setMouseDown(true);
        });
        this.addDrawableChild(filtPickButton);
        this.addDrawableChild(recipeBookButton);
        this.addSelectableChild(recipeBook);
        this.setInitialFocus(recipeBook);
        ci.cancel();
    }

    @NotNull
    private TexturedButtonWidget initFiltPickButton(int deviationOfFiltPickButton) {
        TexturedButtonWidget filtPickButton;
        if (FiltPickScreen.filtPickIsWhiteListMode && FiltPickScreen.filtPickIsDestructionMode) {
            filtPickButton = new TexturedButtonWidget(this.x + 104 + deviationOfFiltPickButton, this.height / 2 - 22, 20, 18, 0, 0, 19, FILTPICK_ENTRY_WHITELIST_DES_ON, button
                    -> ClientPlayNetworking.send(NetWorkingIDs.OPEN_FILTPICK_SCREEN_C2S, PacketByteBufs.empty()));
        } else if (!FiltPickScreen.filtPickIsWhiteListMode && FiltPickScreen.filtPickIsDestructionMode) {
            filtPickButton = new TexturedButtonWidget(this.x + 104 + deviationOfFiltPickButton, this.height / 2 - 22, 20, 18, 0, 0, 19, FILTPICK_ENTRY_BLACKLIST_DES_ON, button
                    -> ClientPlayNetworking.send(NetWorkingIDs.OPEN_FILTPICK_SCREEN_C2S, PacketByteBufs.empty()));
        } else if (FiltPickScreen.filtPickIsWhiteListMode) {
            filtPickButton = new TexturedButtonWidget(this.x + 104 + deviationOfFiltPickButton, this.height / 2 - 22, 20, 18, 0, 0, 19, FILTPICK_ENTRY_WHITELIST, button
                    -> ClientPlayNetworking.send(NetWorkingIDs.OPEN_FILTPICK_SCREEN_C2S, PacketByteBufs.empty()));
        } else {
            filtPickButton = new TexturedButtonWidget(this.x + 104 + deviationOfFiltPickButton, this.height / 2 - 22, 20, 18, 0, 0, 19, FILTPICK_ENTRY_BLACKLIST, button
                    -> ClientPlayNetworking.send(NetWorkingIDs.OPEN_FILTPICK_SCREEN_C2S, PacketByteBufs.empty()));
        }
        filtPickButton.setPosition(this.x + 104 + deviationOfFiltPickButton + CONFIG.getxOffset(), this.height / 2 - 22 + CONFIG.getyOffset());
        return filtPickButton;
    }

}
