package com.apeng.filtpick.mixin;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.ScreenHandlerProvider;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HandledScreen.class)
public abstract class HandledScreenMixin<T extends ScreenHandler> extends Screen implements ScreenHandlerProvider<T> {

    protected HandledScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "mouseReleased(DDI)Z", at = @At("HEAD"))
    private void makeConsistencyWithForgeReobf(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        super.mouseReleased(mouseX, mouseY, button);
    }

}
