package micdoodle8.mods.galacticraft.core.client.gui.container;

import net.minecraft.client.gui.inventory.*;
import micdoodle8.mods.galacticraft.core.client.gui.element.*;
import java.util.*;
import net.minecraft.client.*;
import net.minecraft.inventory.*;
import net.minecraft.entity.player.*;
import cpw.mods.fml.common.*;
import net.minecraft.item.*;

public abstract class GuiContainerGC extends GuiContainer
{
    public List<GuiElementInfoRegion> infoRegions;

    public GuiContainerGC(final Container container) {
        super(container);
        this.infoRegions = new ArrayList<GuiElementInfoRegion>();
    }

    public void drawScreen(final int par1, final int par2, final float par3) {
        super.drawScreen(par1, par2, par3);
        for (int k = 0; k < this.infoRegions.size(); ++k) {
            final GuiElementInfoRegion guibutton = this.infoRegions.get(k);
            guibutton.drawRegion(par1, par2);
        }
    }

    public void setWorldAndResolution(final Minecraft par1Minecraft, final int par2, final int par3) {
        this.infoRegions.clear();
        super.setWorldAndResolution(par1Minecraft, par2, par3);
    }

    public int getTooltipOffset(final int par1, final int par2) {
        for (int i1 = 0; i1 < this.inventorySlots.inventorySlots.size(); ++i1) {
            final Slot slot = (Slot) this.inventorySlots.inventorySlots.get(i1);
            if (slot.func_111238_b() && this.func_146978_c(slot.xDisplayPosition, slot.yDisplayPosition, 16, 16, par1, par2)) {
                final ItemStack itemStack = slot.getStack();
                if (itemStack != null) {
                    final List list = itemStack.getTooltip(this.mc.thePlayer, this.mc.gameSettings.advancedItemTooltips);
                    int size = list.size();
                    if (Loader.isModLoaded("Waila")) {
                        ++size;
                    }
                    return size * 10 + 10;
                }
            }
        }
        return 0;
    }
}
