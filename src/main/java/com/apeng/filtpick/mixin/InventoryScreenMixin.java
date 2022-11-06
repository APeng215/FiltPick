package com.apeng.filtpick.mixin;


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
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InventoryScreen.class)
public abstract class InventoryScreenMixin extends AbstractInventoryScreen<PlayerScreenHandler> implements RecipeBookProvider {
    private static final Identifier RECIPE_BUTTON_TEXTURE = new Identifier("textures/gui/recipe_button.png");
    public InventoryScreenMixin(PlayerScreenHandler screenHandler, PlayerInventory playerInventory, Text text) {
        super(screenHandler, playerInventory, text);
    }

        @Inject(method = "init()V",at = @At(value = "INVOKE",target = "net/minecraft/client/gui/screen/ingame/InventoryScreen.addDrawableChild(Lnet/minecraft/client/gui/Element;)Lnet/minecraft/client/gui/Element;"),cancellable = true)
    private void addFiltPickAddEntryButton(CallbackInfo ci){
        int deviationOfFiltPickButton = 25;
        RecipeBookWidget recipeBook = ((InventoryScreenAccessor)this).getRecipeBook();
        TexturedButtonWidget recipeBookButton;
        TexturedButtonWidget filtPickButton;
        filtPickButton = new TexturedButtonWidget(this.x + 104 + deviationOfFiltPickButton, this.height / 2 - 22, 20, 18, 0, 0, 19, RECIPE_BUTTON_TEXTURE, button
                -> ClientPlayNetworking.send(new Identifier("open_filtpick_screen"), PacketByteBufs.empty()));
        recipeBookButton = new TexturedButtonWidget(this.x + 104, this.height / 2 - 22, 20, 18, 0, 0, 19, RECIPE_BUTTON_TEXTURE, button
                -> {
            recipeBook.toggleOpen();
            this.x = recipeBook.findLeftEdge(this.width, this.backgroundWidth);
            ((TexturedButtonWidget)button).setPos(this.x + 104, this.height / 2 - 22);
            filtPickButton.setPos(this.x + 104 + deviationOfFiltPickButton, this.height / 2 - 22);
            ((InventoryScreenAccessor)this).setMouseDown(true);
        });
        this.addDrawableChild(filtPickButton);
        this.addDrawableChild(recipeBookButton);
        this.addSelectableChild(recipeBook);
        this.setInitialFocus(recipeBook);
        ci.cancel();



    }

}
