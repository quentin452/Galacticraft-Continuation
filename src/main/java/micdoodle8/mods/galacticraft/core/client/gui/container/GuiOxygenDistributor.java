package micdoodle8.mods.galacticraft.core.client.gui.container;

import net.minecraft.util.*;
import micdoodle8.mods.galacticraft.core.tile.*;
import micdoodle8.mods.galacticraft.core.client.gui.element.*;
import micdoodle8.mods.galacticraft.core.inventory.*;
import net.minecraft.inventory.*;
import java.util.*;
import micdoodle8.mods.galacticraft.core.util.*;
import org.lwjgl.opengl.*;
import micdoodle8.mods.galacticraft.core.energy.*;
import micdoodle8.mods.galacticraft.core.*;
import micdoodle8.mods.galacticraft.core.network.*;
import net.minecraft.entity.player.*;

public class GuiOxygenDistributor extends GuiContainerGC implements GuiElementCheckbox.ICheckBoxCallback
{
    private static final ResourceLocation distributorTexture;
    private final TileEntityOxygenDistributor distributor;
    private GuiElementInfoRegion oxygenInfoRegion;
    private GuiElementInfoRegion electricInfoRegion;
    private GuiElementCheckbox checkboxRenderBubble;
    
    public GuiOxygenDistributor(final InventoryPlayer par1InventoryPlayer, final TileEntityOxygenDistributor par2TileEntityAirDistributor) {
        super((Container)new ContainerOxygenDistributor(par1InventoryPlayer, par2TileEntityAirDistributor));
        this.oxygenInfoRegion = new GuiElementInfoRegion((this.width - this.xSize) / 2 + 112, (this.height - this.ySize) / 2 + 24, 56, 9, new ArrayList<String>(), this.width, this.height, this);
        this.electricInfoRegion = new GuiElementInfoRegion((this.width - this.xSize) / 2 + 112, (this.height - this.ySize) / 2 + 37, 56, 9, new ArrayList<String>(), this.width, this.height, this);
        this.distributor = par2TileEntityAirDistributor;
        this.ySize = 180;
    }
    
    public void initGui() {
        super.initGui();
        final int var5 = (this.width - this.xSize) / 2;
        final int var6 = (this.height - this.ySize) / 2;
        final List<String> batterySlotDesc = new ArrayList<String>();
        batterySlotDesc.add(GCCoreUtil.translate("gui.batterySlot.desc.0"));
        batterySlotDesc.add(GCCoreUtil.translate("gui.batterySlot.desc.1"));
        this.infoRegions.add(new GuiElementInfoRegion((this.width - this.xSize) / 2 + 46, (this.height - this.ySize) / 2 + 26, 18, 18, batterySlotDesc, this.width, this.height, this));
        final List<String> oxygenSlotDesc = new ArrayList<String>();
        oxygenSlotDesc.add(GCCoreUtil.translate("gui.oxygenSlot.desc.0"));
        oxygenSlotDesc.add(GCCoreUtil.translate("gui.oxygenSlot.desc.1"));
        this.infoRegions.add(new GuiElementInfoRegion((this.width - this.xSize) / 2 + 16, (this.height - this.ySize) / 2 + 26, 18, 18, oxygenSlotDesc, this.width, this.height, this));
        final List<String> oxygenDesc = new ArrayList<String>();
        oxygenDesc.add(GCCoreUtil.translate("gui.oxygenStorage.desc.0"));
        oxygenDesc.add(EnumColor.YELLOW + GCCoreUtil.translate("gui.oxygenStorage.desc.1") + ": " + (int)Math.floor(this.distributor.storedOxygen) + " / " + (int)Math.floor(this.distributor.maxOxygen));
        this.oxygenInfoRegion.tooltipStrings = oxygenDesc;
        this.oxygenInfoRegion.xPosition = (this.width - this.xSize) / 2 + 112;
        this.oxygenInfoRegion.yPosition = (this.height - this.ySize) / 2 + 24;
        this.oxygenInfoRegion.parentWidth = this.width;
        this.oxygenInfoRegion.parentHeight = this.height;
        this.infoRegions.add(this.oxygenInfoRegion);
        final List<String> electricityDesc = new ArrayList<String>();
        electricityDesc.add(GCCoreUtil.translate("gui.energyStorage.desc.0"));
        electricityDesc.add(EnumColor.YELLOW + GCCoreUtil.translate("gui.energyStorage.desc.1") + (int)Math.floor(this.distributor.getEnergyStoredGC()) + " / " + (int)Math.floor(this.distributor.getMaxEnergyStoredGC()));
        this.electricInfoRegion.tooltipStrings = electricityDesc;
        this.electricInfoRegion.xPosition = (this.width - this.xSize) / 2 + 112;
        this.electricInfoRegion.yPosition = (this.height - this.ySize) / 2 + 37;
        this.electricInfoRegion.parentWidth = this.width;
        this.electricInfoRegion.parentHeight = this.height;
        this.infoRegions.add(this.electricInfoRegion);
        this.checkboxRenderBubble = new GuiElementCheckbox(0, this, var5 + 85, var6 + 87, GCCoreUtil.translate("gui.message.bubbleVisible.name"));
        this.buttonList.add(this.checkboxRenderBubble);
    }
    
    protected void drawGuiContainerForegroundLayer(final int par1, final int par2) {
        this.fontRendererObj.drawString(this.distributor.getInventoryName(), 8, 10, 4210752);
        GCCoreUtil.drawStringRightAligned(GCCoreUtil.translate("gui.message.in.name") + ":", 99, 26, 4210752, this.fontRendererObj);
        GCCoreUtil.drawStringRightAligned(GCCoreUtil.translate("gui.message.in.name") + ":", 99, 38, 4210752, this.fontRendererObj);
        String status = GCCoreUtil.translate("gui.message.status.name") + ": " + this.getStatus();
        this.fontRendererObj.drawString(status, this.xSize / 2 - this.fontRendererObj.getStringWidth(status) / 2, 50, 4210752);
        status = GCCoreUtil.translate("gui.oxygenUse.desc") + ": " + this.distributor.oxygenPerTick * 20.0f + GCCoreUtil.translate("gui.perSecond");
        this.fontRendererObj.drawString(status, this.xSize / 2 - this.fontRendererObj.getStringWidth(status) / 2, 60, 4210752);
        this.fontRendererObj.drawString(GCCoreUtil.translate("container.inventory"), 8, this.ySize - 90 + 3, 4210752);
    }
    
    private String getStatus() {
        if (this.distributor.storedOxygen < this.distributor.oxygenPerTick) {
            return EnumColor.DARK_RED + GCCoreUtil.translate("gui.status.missingoxygen.name");
        }
        return this.distributor.getGUIstatus();
    }
    
    protected void drawGuiContainerBackgroundLayer(final float var1, final int var2, final int var3) {
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.mc.getTextureManager().bindTexture(GuiOxygenDistributor.distributorTexture);
        final int var4 = (this.width - this.xSize) / 2;
        final int var5 = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(var4, var5 + 5, 0, 0, this.xSize, 181);
        if (this.distributor != null) {
            int scale = this.distributor.getCappedScaledOxygenLevel(54);
            this.drawTexturedModalRect(var4 + 113, var5 + 25, 197, 7, Math.min(scale, 54), 7);
            scale = this.distributor.getScaledElecticalLevel(54);
            this.drawTexturedModalRect(var4 + 113, var5 + 38, 197, 0, Math.min(scale, 54), 7);
            if (this.distributor.getEnergyStoredGC() > 0.0f) {
                this.drawTexturedModalRect(var4 + 99, var5 + 37, 176, 0, 11, 10);
            }
            if (this.distributor.storedOxygen > 0.0f) {
                this.drawTexturedModalRect(var4 + 100, var5 + 24, 187, 0, 10, 10);
            }
            final List<String> oxygenDesc = new ArrayList<String>();
            oxygenDesc.add(GCCoreUtil.translate("gui.oxygenStorage.desc.0"));
            oxygenDesc.add(EnumColor.YELLOW + GCCoreUtil.translate("gui.oxygenStorage.desc.1") + ": " + (int)Math.floor(this.distributor.storedOxygen) + " / " + (int)Math.floor(this.distributor.maxOxygen));
            this.oxygenInfoRegion.tooltipStrings = oxygenDesc;
            final List<String> electricityDesc = new ArrayList<String>();
            electricityDesc.add(GCCoreUtil.translate("gui.energyStorage.desc.0"));
            EnergyDisplayHelper.getEnergyDisplayTooltip(this.distributor.getEnergyStoredGC(), this.distributor.getMaxEnergyStoredGC(), electricityDesc);
            this.electricInfoRegion.tooltipStrings = electricityDesc;
            this.checkboxRenderBubble.isSelected = this.distributor.shouldRenderBubble;
        }
    }
    
    public void onSelectionChanged(final GuiElementCheckbox checkbox, final boolean newSelected) {
        this.distributor.setBubbleVisible(newSelected);
        GalacticraftCore.packetPipeline.sendToServer(new PacketSimple(PacketSimple.EnumSimplePacket.S_ON_ADVANCED_GUI_CLICKED_INT, new Object[] { 6, this.distributor.xCoord, this.distributor.yCoord, this.distributor.zCoord, newSelected ? 1 : 0 }));
    }
    
    public boolean canPlayerEdit(final GuiElementCheckbox checkbox, final EntityPlayer player) {
        return true;
    }
    
    public boolean getInitiallySelected(final GuiElementCheckbox checkbox) {
        return this.distributor.shouldRenderBubble;
    }
    
    public void onIntruderInteraction() {
    }
    
    static {
        distributorTexture = new ResourceLocation(GalacticraftCore.ASSET_PREFIX, "textures/gui/oxygenDistributor.png");
    }
}
