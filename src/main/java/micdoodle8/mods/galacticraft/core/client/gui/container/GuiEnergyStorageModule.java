package micdoodle8.mods.galacticraft.core.client.gui.container;

import net.minecraft.client.gui.inventory.*;
import cpw.mods.fml.relauncher.*;
import net.minecraft.util.*;
import micdoodle8.mods.galacticraft.core.tile.*;
import net.minecraft.entity.player.*;
import micdoodle8.mods.galacticraft.core.inventory.*;
import net.minecraft.inventory.*;
import micdoodle8.mods.galacticraft.core.energy.*;
import micdoodle8.mods.galacticraft.core.util.*;
import org.lwjgl.opengl.*;
import micdoodle8.mods.galacticraft.core.*;

@SideOnly(Side.CLIENT)
public class GuiEnergyStorageModule extends GuiContainer
{
    private static final ResourceLocation batteryBoxTexture;
    private TileEntityEnergyStorageModule tileEntity;
    
    public GuiEnergyStorageModule(final InventoryPlayer par1InventoryPlayer, final TileEntityEnergyStorageModule batteryBox) {
        super((Container)new ContainerEnergyStorageModule(par1InventoryPlayer, batteryBox));
        this.tileEntity = batteryBox;
    }
    
    protected void drawGuiContainerForegroundLayer(final int par1, final int par2) {
        this.fontRendererObj.drawString(this.tileEntity.getInventoryName(), this.xSize / 2 - this.fontRendererObj.getStringWidth(this.tileEntity.getInventoryName()) / 2, 6, 4210752);
        float energy = this.tileEntity.getEnergyStoredGC();
        if (energy + 49.0f > this.tileEntity.getMaxEnergyStoredGC()) {
            energy = this.tileEntity.getMaxEnergyStoredGC();
        }
        String displayStr = EnergyDisplayHelper.getEnergyDisplayS(energy);
        this.fontRendererObj.drawString(displayStr, 122 - this.fontRendererObj.getStringWidth(displayStr) / 2, 25, 4210752);
        displayStr = GCCoreUtil.translate("gui.message.of.name") + " " + EnergyDisplayHelper.getEnergyDisplayS(this.tileEntity.getMaxEnergyStoredGC());
        this.fontRendererObj.drawString(displayStr, 122 - this.fontRendererObj.getStringWidth(displayStr) / 2, 34, 4210752);
        displayStr = GCCoreUtil.translate("gui.maxOutput.desc") + ": " + EnergyDisplayHelper.getEnergyDisplayS(this.tileEntity.storage.getMaxExtract()) + "/t";
        this.fontRendererObj.drawString(displayStr, 114 - this.fontRendererObj.getStringWidth(displayStr) / 2, 64, 4210752);
        this.fontRendererObj.drawString(GCCoreUtil.translate("container.inventory"), 8, this.ySize - 96 + 2, 4210752);
    }
    
    protected void drawGuiContainerBackgroundLayer(final float par1, final int par2, final int par3) {
        this.mc.renderEngine.bindTexture(GuiEnergyStorageModule.batteryBoxTexture);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        final int containerWidth = (this.width - this.xSize) / 2;
        final int containerHeight = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(containerWidth, containerHeight, 0, 0, this.xSize, this.ySize);
        final int scale = (int)((this.tileEntity.getEnergyStoredGC() + 49.0f) / this.tileEntity.getMaxEnergyStoredGC() * 72.0f);
        this.drawTexturedModalRect(containerWidth + 87, containerHeight + 52, 176, 0, scale, 3);
    }
    
    static {
        batteryBoxTexture = new ResourceLocation(GalacticraftCore.ASSET_PREFIX, "textures/gui/energyStorageModule.png");
    }
}
