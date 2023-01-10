package com.apeng.filtpick.guis.util;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.systems.RenderSystem;
import io.github.cottonmc.cotton.gui.client.ScreenDrawing;
import io.github.cottonmc.cotton.gui.widget.WItemSlot;
import io.github.cottonmc.cotton.gui.widget.WWidget;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.tag.TagKey;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
@Deprecated
public class WFiltPickItem extends WWidget {



    private List<ItemStack> items;
    private int duration = 25;
    private int ticks = 0;
    private int current = 0;

    public WFiltPickItem(List<ItemStack> items) {
        setItems(items);
    }

    public WFiltPickItem(TagKey<? extends ItemConvertible> tag) {
        this(getRenderStacks(tag));
    }

    public WFiltPickItem(ItemStack stack) {
        this(Collections.singletonList(stack));
    }

    @Override
    public boolean canResize() {
        return true;
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void tick() {
        if (ticks++ >= duration) {
            ticks = 0;
            current = (current + 1) % items.size();
        }
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void paint(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
        RenderSystem.enableDepthTest();

        MinecraftClient mc = MinecraftClient.getInstance();
        ItemRenderer renderer = mc.getItemRenderer();
        renderer.zOffset = 100f;
        renderer.renderInGui(items.get(current), x + getWidth() / 2 - 8, y + getHeight() / 2 - 8);
        renderer.zOffset = 0f;

        ScreenDrawing.texturedRect(matrices, x, y, 18, 18, WItemSlot.SLOT_TEXTURE,
                0, 0, 18/64f, 18/64f, 0xFF_FFFFFF);
    }

    /**
     * Returns the animation duration of this {@code WItem}.
     *
     * <p>Defaults to 25 screen ticks.
     */
    public int getDuration() {
        return duration;
    }

    public WFiltPickItem setDuration(int duration) {
        this.duration = duration;
        return this;
    }

    public List<ItemStack> getItems() {
        return items;
    }

    /**
     * Sets the item list of this {@code WItem} and resets the animation state.
     *
     * @param items the new item list
     * @return this instance
     */
    public WFiltPickItem setItems(List<ItemStack> items) {
        Objects.requireNonNull(items, "stacks == null!");
        if (items.isEmpty()) throw new IllegalArgumentException("The stack list is empty!");

        this.items = items;

        // Reset the state
        current = 0;
        ticks = 0;

        return this;
    }

    /**
     * Gets the default stacks ({@link Item#getDefaultStack()} ()}) of each item in a tag.
     */
    @SuppressWarnings("unchecked")
    private static List<ItemStack> getRenderStacks(TagKey<? extends ItemConvertible> tag) {
        Registry<ItemConvertible> registry = (Registry<ItemConvertible>) Registry.REGISTRIES.get(tag.registry().getValue());
        ImmutableList.Builder<ItemStack> builder = ImmutableList.builder();

        for (RegistryEntry<ItemConvertible> item : registry.getOrCreateEntryList((TagKey<ItemConvertible>) tag)) {
            builder.add(item.value().asItem().getDefaultStack());
        }

        return builder.build();
    }

}
