package micdoodle8.mods.galacticraft.core.client.gui.container;

import cpw.mods.fml.relauncher.*;
import net.minecraft.util.*;
import micdoodle8.mods.galacticraft.core.tile.*;
import micdoodle8.mods.galacticraft.core.client.gui.element.*;
import net.minecraft.entity.player.*;
import micdoodle8.mods.galacticraft.core.inventory.*;
import net.minecraft.inventory.*;
import java.util.*;
import micdoodle8.mods.galacticraft.core.util.*;
import org.lwjgl.opengl.*;
import micdoodle8.mods.galacticraft.core.energy.*;
import micdoodle8.mods.galacticraft.core.*;

@SideOnly(Side.CLIENT)
public class GuiCircuitFabricator extends GuiContainerGC
{
    private static final ResourceLocation circuitFabricatorTexture;
    private TileEntityCircuitFabricator tileEntity;
    private GuiElementInfoRegion electricInfoRegion;
    private GuiElementInfoRegion processInfoRegion;
    
    public GuiCircuitFabricator(final InventoryPlayer par1InventoryPlayer, final TileEntityCircuitFabricator tileEntity) {
        super(new ContainerCircuitFabricator(par1InventoryPlayer, tileEntity));
        this.electricInfoRegion = new GuiElementInfoRegion(0, 0, 56, 9, null, 0, 0, this);
        this.processInfoRegion = new GuiElementInfoRegion(0, 0, 53, 12, null, 0, 0, this);
        this.tileEntity = tileEntity;
        this.ySize = 192;
    }
    
    public void initGui() {
        super.initGui();
        this.electricInfoRegion.tooltipStrings = new ArrayList<String>();
        this.electricInfoRegion.xPosition = (this.width - this.xSize) / 2 + 17;
        this.electricInfoRegion.yPosition = (this.height - this.ySize) / 2 + 88;
        this.electricInfoRegion.parentWidth = this.width;
        this.electricInfoRegion.parentHeight = this.height;
        this.infoRegions.add(this.electricInfoRegion);
        final List<String> batterySlotDesc = new ArrayList<String>();
        batterySlotDesc.add(GCCoreUtil.translate("gui.batterySlot.desc.0"));
        batterySlotDesc.add(GCCoreUtil.translate("gui.batterySlot.desc.1"));
        this.infoRegions.add(new GuiElementInfoRegion((this.width - this.xSize) / 2 + 5, (this.height - this.ySize) / 2 + 68, 18, 18, batterySlotDesc, this.width, this.height, this));
        this.processInfoRegion.tooltipStrings = new ArrayList<String>();
        this.processInfoRegion.xPosition = (this.width - this.xSize) / 2 + 87;
        this.processInfoRegion.yPosition = (this.height - this.ySize) / 2 + 19;
        this.processInfoRegion.parentWidth = this.width;
        this.processInfoRegion.parentHeight = this.height;
        this.infoRegions.add(this.processInfoRegion);
    }
    
    protected void drawGuiContainerForegroundLayer(final int par1, final int par2) {
        this.fontRendererObj.drawString(this.tileEntity.getInventoryName(), 10, 6, 4210752);
        String displayText;
        if (this.tileEntity.processTicks > 0) {
            displayText = EnumColor.BRIGHT_GREEN + GCCoreUtil.translate("gui.status.running.name");
        }
        else {
            displayText = EnumColor.ORANGE + GCCoreUtil.translate("gui.status.idle.name");
        }
        final String str = GCCoreUtil.translate("gui.message.status.name") + ":";
        this.fontRendererObj.drawString(str, 115 - this.fontRendererObj.getStringWidth(str) / 2, 80, 4210752);
        this.fontRendererObj.drawString(displayText, 115 - this.fontRendererObj.getStringWidth(displayText) / 2, 90, 4210752);
        this.fontRendererObj.drawString(GCCoreUtil.translate("container.inventory"), 8, this.ySize - 93, 4210752);
    }
    
    protected void drawGuiContainerBackgroundLayer(final float par1, final int par2, final int par3) {
        this.mc.renderEngine.bindTexture(GuiCircuitFabricator.circuitFabricatorTexture);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        final int containerWidth = (this.width - this.xSize) / 2;
        final int containerHeight = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(containerWidth, containerHeight, 0, 0, this.xSize, this.ySize);
        final List<String> electricityDesc = new ArrayList<String>();
        electricityDesc.add(GCCoreUtil.translate("gui.energyStorage.desc.0"));
        EnergyDisplayHelper.getEnergyDisplayTooltip(this.tileEntity.getEnergyStoredGC(), this.tileEntity.getMaxEnergyStoredGC(), electricityDesc);
        this.electricInfoRegion.tooltipStrings = electricityDesc;
        int scale;
        if (this.tileEntity.processTicks > 0) {
            scale = (int)(this.tileEntity.processTicks / 300.0 * 100.0);
        }
        else {
            scale = 0;
        }
        final List<String> processDesc = new ArrayList<String>();
        processDesc.clear();
        processDesc.add(GCCoreUtil.translate("gui.electricCompressor.desc.0") + ": " + scale + "%");
        this.processInfoRegion.tooltipStrings = processDesc;
        if (this.tileEntity.processTicks > 0) {
            scale = (int)(this.tileEntity.processTicks / 300.0 * 51.0);
            this.drawTexturedModalRect(containerWidth + 88, containerHeight + 20, 176, 17 + this.tileEntity.processTicks % 9 / 3 * 10, scale, 10);
        }
        if (this.tileEntity.getEnergyStoredGC() > 0.0f) {
            scale = this.tileEntity.getScaledElecticalLevel(54);
            this.drawTexturedModalRect(containerWidth + 116 - 98, containerHeight + 89, 176, 0, scale, 7);
            this.drawTexturedModalRect(containerWidth + 4, containerHeight + 88, 176, 7, 11, 10);
        }
    }
    
    static {
        circuitFabricatorTexture = new ResourceLocation(GalacticraftCore.ASSET_PREFIX, "textures/gui/circuitFabricator.png");
    }
}
