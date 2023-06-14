package com.apeng.filtpick.guis.custom;

import com.apeng.filtpick.FiltPick;
import com.apeng.filtpick.guis.util.WFiltPickItemSlot;
import io.github.cottonmc.cotton.gui.SyncedGuiDescription;
import io.github.cottonmc.cotton.gui.widget.WGridPanel;
import io.github.cottonmc.cotton.gui.widget.WItemSlot;
import io.github.cottonmc.cotton.gui.widget.WLabel;
import io.github.cottonmc.cotton.gui.widget.data.HorizontalAlignment;
import io.github.cottonmc.cotton.gui.widget.data.Insets;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

public class FiltPickGuiDescription extends SyncedGuiDescription {
    public FiltPickGuiDescription(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, new SimpleInventory(27), null);
    }

    public FiltPickGuiDescription(int syncId, PlayerInventory playerInventory, Inventory inventory, @Nullable PropertyDelegate propertyDelegate) {
        super(FiltPick.FILTPICK_SCREEN_HANDLER_TYPE, syncId, playerInventory, inventory, propertyDelegate);

        //Set root panel
        WGridPanel root = new WGridPanel();
        setRootPanel(root);
        root.setInsets(Insets.ROOT_PANEL);

        //Set display title label
        WLabel displayName = new WLabel(Text.translatable("filtpick_screen_name"));
        displayName.setHorizontalAlignment(HorizontalAlignment.CENTER);
        root.add(displayName, 4, 0);

        //Set filtPickInventory slots
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                WItemSlot filtPickItemSlot = new WFiltPickItemSlot(inventory, i * 9 + j, 1, 1, false, this).setModifiable(false);
                root.add(filtPickItemSlot, j, 1 + i);
            }
        }

        //Set playerInventory slots
        root.add(this.createPlayerInventoryPanel(), 0, 4);

        //validate
        root.validate(this);
    }


}
