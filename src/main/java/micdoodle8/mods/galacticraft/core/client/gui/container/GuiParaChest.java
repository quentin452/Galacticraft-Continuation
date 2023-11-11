package micdoodle8.mods.galacticraft.core.client.gui.container;

import cpw.mods.fml.relauncher.*;
import net.minecraft.util.*;
import micdoodle8.mods.galacticraft.core.inventory.*;
import net.minecraft.inventory.*;
import micdoodle8.mods.galacticraft.core.util.*;
import org.lwjgl.opengl.*;
import micdoodle8.mods.galacticraft.core.entities.*;
import micdoodle8.mods.galacticraft.core.*;

@SideOnly(Side.CLIENT)
public class GuiParaChest extends GuiContainerGC
{
    private static ResourceLocation[] parachestTexture;
    private IInventory upperChestInventory;
    private IInventory lowerChestInventory;
    private int inventorySlots;
    
    public GuiParaChest(final IInventory par1IInventory, final IInventory par2IInventory) {
        super((Container)new ContainerParaChest(par1IInventory, par2IInventory));
        this.inventorySlots = 0;
        this.upperChestInventory = par1IInventory;
        this.lowerChestInventory = par2IInventory;
        this.allowUserInput = false;
        this.inventorySlots = par2IInventory.getSizeInventory();
        this.ySize = 146 + this.inventorySlots * 2;
    }
    
    protected void drawGuiContainerForegroundLayer(final int par1, final int par2) {
        this.fontRendererObj.drawString(this.lowerChestInventory.getInventoryName(), 8, 6, 4210752);
        this.fontRendererObj.drawString(this.upperChestInventory.hasCustomInventoryName() ? this.upperChestInventory.getInventoryName() : GCCoreUtil.translate(this.upperChestInventory.getInventoryName()), 8, this.ySize - 103 + ((this.inventorySlots == 3) ? 2 : 4), 4210752);
    }
    
    protected void drawGuiContainerBackgroundLayer(final float par1, final int par2, final int par3) {
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.mc.getTextureManager().bindTexture(GuiParaChest.parachestTexture[(this.inventorySlots - 3) / 18]);
        final int k = (this.width - this.xSize) / 2;
        final int l = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);
        if (this.lowerChestInventory instanceof IScaleableFuelLevel) {
            final int fuelLevel = ((IScaleableFuelLevel)this.lowerChestInventory).getScaledFuelLevel(28);
            this.drawTexturedModalRect(k + 17, l + ((this.inventorySlots == 3) ? 40 : 42) - fuelLevel + this.inventorySlots * 2, 176, 28 - fuelLevel, 34, fuelLevel);
        }
    }
    
    static {
        GuiParaChest.parachestTexture = new ResourceLocation[4];
        for (int i = 0; i < 4; ++i) {
            GuiParaChest.parachestTexture[i] = new ResourceLocation(GalacticraftCore.ASSET_PREFIX, "textures/gui/chest_" + i * 18 + ".png");
        }
    }
}
