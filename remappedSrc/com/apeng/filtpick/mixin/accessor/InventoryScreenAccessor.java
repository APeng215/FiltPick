package com.apeng.filtpick.mixin.accessor;

import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(InventoryScreen.class)
public interface InventoryScreenAccessor {
    @Accessor//RecipeBookWidget recipeBook = new RecipeBookWidget();
    RecipeBookWidget getRecipeBook();
    @Accessor
    void setMouseDown(boolean b);


}
