package com.apeng.filtpick.gui.widget;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class LegacyTexturedButton extends ImageButton {
    private final int u;
    private final int v;
    private final int hoveredVOffset;

    private final Identifier texture;

    private final int textureWidth;
    private final int textureHeight;

    public LegacyTexturedButton(int x, int y, int width, int height, int u, int v, int hoveredVOffset, Identifier texture, int textureWidth, int textureHeight, OnPress pressAction) {
        this(x, y, width, height, u, v, hoveredVOffset, texture, textureWidth, textureHeight, pressAction, CommonComponents.EMPTY);
    }

    public LegacyTexturedButton(int x, int y, int width, int height, int u, int v, int hoveredVOffset, Identifier texture, OnPress pressAction) {
        this(x, y, width, height, u, v, hoveredVOffset, texture, 256, 256, pressAction, CommonComponents.EMPTY);
    }


    public LegacyTexturedButton(int x, int y, int width, int height, int u, int v, int hoveredVOffset, Identifier texture, int textureWidth, int textureHeight, OnPress pressAction, Component message) {
        super(x, y, width, height, null, pressAction, message);

        this.u = u;
        this.v = v;
        this.hoveredVOffset = hoveredVOffset;

        this.texture = texture;

        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
    }

    @Override
    public void renderContents(GuiGraphics context, int mouseX, int mouseY, float delta) {
        int v = this.v;

        if (!this.isActive()) {
            v += this.hoveredVOffset * 2;
        } else if (this.isHoveredOrFocused()) {
            v += this.hoveredVOffset;
        }

        context.blit(
                RenderPipelines.GUI_TEXTURED,
                this.texture,
                this.getX(),
                this.getY(),
                this.u,
                v,
                this.width,
                this.height,
                this.textureWidth,
                this.textureHeight
        );
    }
}