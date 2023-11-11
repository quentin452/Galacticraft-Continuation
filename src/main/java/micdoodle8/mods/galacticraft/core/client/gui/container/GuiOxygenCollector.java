package micdoodle8.mods.galacticraft.core.client.gui.container;

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

public class GuiOxygenCollector extends GuiContainerGC
{
    private static final ResourceLocation collectorTexture;
    private final TileEntityOxygenCollector collector;
    private GuiElementInfoRegion oxygenInfoRegion;
    private GuiElementInfoRegion electricInfoRegion;
    
    public GuiOxygenCollector(final InventoryPlayer par1InventoryPlayer, final TileEntityOxygenCollector par2TileEntityAirDistributor) {
        super((Container)new ContainerOxygenCollector(par1InventoryPlayer, par2TileEntityAirDistributor));
        this.oxygenInfoRegion = new GuiElementInfoRegion((this.width - this.xSize) / 2 + 112, (this.height - this.ySize) / 2 + 24, 56, 9, new ArrayList<String>(), this.width, this.height, this);
        this.electricInfoRegion = new GuiElementInfoRegion((this.width - this.xSize) / 2 + 112, (this.height - this.ySize) / 2 + 37, 56, 9, new ArrayList<String>(), this.width, this.height, this);
        this.collector = par2TileEntityAirDistributor;
        this.ySize = 180;
    }
    
    public void initGui() {
        super.initGui();
        final List<String> batterySlotDesc = new ArrayList<String>();
        batterySlotDesc.add(GCCoreUtil.translate("gui.batterySlot.desc.0"));
        batterySlotDesc.add(GCCoreUtil.translate("gui.batterySlot.desc.1"));
        this.infoRegions.add(new GuiElementInfoRegion((this.width - this.xSize) / 2 + 31, (this.height - this.ySize) / 2 + 26, 18, 18, batterySlotDesc, this.width, this.height, this));
        this.oxygenInfoRegion.xPosition = (this.width - this.xSize) / 2 + 112;
        this.oxygenInfoRegion.yPosition = (this.height - this.ySize) / 2 + 24;
        this.oxygenInfoRegion.parentWidth = this.width;
        this.oxygenInfoRegion.parentHeight = this.height;
        this.infoRegions.add(this.oxygenInfoRegion);
        this.electricInfoRegion.xPosition = (this.width - this.xSize) / 2 + 112;
        this.electricInfoRegion.yPosition = (this.height - this.ySize) / 2 + 37;
        this.electricInfoRegion.parentWidth = this.width;
        this.electricInfoRegion.parentHeight = this.height;
        this.infoRegions.add(this.electricInfoRegion);
    }
    
    protected void drawGuiContainerForegroundLayer(final int par1, final int par2) {
        this.fontRendererObj.drawString(this.collector.getInventoryName(), 8, 10, 4210752);
        GCCoreUtil.drawStringRightAligned(GCCoreUtil.translate("gui.message.out.name") + ":", 99, 25, 4210752, this.fontRendererObj);
        GCCoreUtil.drawStringRightAligned(GCCoreUtil.translate("gui.message.in.name") + ":", 99, 37, 4210752, this.fontRendererObj);
        GCCoreUtil.drawStringCentered(GCCoreUtil.translate("gui.message.status.name") + ": " + this.getStatus(), this.xSize / 2, 50, 4210752, this.fontRendererObj);
        final String status = GCCoreUtil.translate("gui.status.collecting.name") + ": " + (int)(0.5f + Math.min(this.collector.lastOxygenCollected * 20.0f, 2000.0f)) + GCCoreUtil.translate("gui.perSecond");
        GCCoreUtil.drawStringCentered(status, this.xSize / 2, 60, 4210752, this.fontRendererObj);
        this.fontRendererObj.drawString(GCCoreUtil.translate("container.inventory"), 8, this.ySize - 90 + 2, 4210752);
    }
    
    private String getStatus() {
        final String returnValue = this.collector.getGUIstatus();
        if (returnValue.equals(EnumColor.DARK_GREEN + GCCoreUtil.translate("gui.status.active.name")) && this.collector.lastOxygenCollected <= 0.0f) {
            return EnumColor.DARK_RED + GCCoreUtil.translate("gui.status.missingleaves.name");
        }
        return returnValue;
    }
    
    protected void drawGuiContainerBackgroundLayer(final float var1, final int var2, final int var3) {
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.mc.getTextureManager().bindTexture(GuiOxygenCollector.collectorTexture);
        final int var4 = (this.width - this.xSize) / 2;
        final int var5 = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(var4, var5 + 5, 0, 0, this.xSize, 181);
        if (this.collector != null) {
            int scale = this.collector.getCappedScaledOxygenLevel(54);
            this.drawTexturedModalRect(var4 + 113, var5 + 25, 197, 7, Math.min(scale, 54), 7);
            scale = this.collector.getScaledElecticalLevel(54);
            this.drawTexturedModalRect(var4 + 113, var5 + 38, 197, 0, Math.min(scale, 54), 7);
            if (this.collector.getEnergyStoredGC() > 0.0f) {
                this.drawTexturedModalRect(var4 + 99, var5 + 37, 176, 0, 11, 10);
            }
            if (this.collector.storedOxygen > 0.0f) {
                this.drawTexturedModalRect(var4 + 100, var5 + 24, 187, 0, 10, 10);
            }
            final List<String> oxygenDesc = new ArrayList<String>();
            oxygenDesc.add(GCCoreUtil.translate("gui.oxygenStorage.desc.0"));
            oxygenDesc.add(EnumColor.YELLOW + GCCoreUtil.translate("gui.oxygenStorage.desc.1") + ": " + (int)Math.floor(this.collector.storedOxygen) + " / " + (int)Math.floor(this.collector.maxOxygen));
            this.oxygenInfoRegion.tooltipStrings = oxygenDesc;
            final List<String> electricityDesc = new ArrayList<String>();
            electricityDesc.add(GCCoreUtil.translate("gui.energyStorage.desc.0"));
            EnergyDisplayHelper.getEnergyDisplayTooltip(this.collector.getEnergyStoredGC(), this.collector.getMaxEnergyStoredGC(), electricityDesc);
            this.electricInfoRegion.tooltipStrings = electricityDesc;
        }
    }
    
    static {
        collectorTexture = new ResourceLocation(GalacticraftCore.ASSET_PREFIX, "textures/gui/oxygen.png");
    }
}
