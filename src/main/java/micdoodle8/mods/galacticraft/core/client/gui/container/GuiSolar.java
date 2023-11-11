package micdoodle8.mods.galacticraft.core.client.gui.container;

import net.minecraft.util.*;
import micdoodle8.mods.galacticraft.core.tile.*;
import net.minecraft.client.gui.*;
import micdoodle8.mods.galacticraft.core.client.gui.element.*;
import net.minecraft.entity.player.*;
import micdoodle8.mods.galacticraft.core.inventory.*;
import net.minecraft.inventory.*;
import java.util.*;
import micdoodle8.mods.galacticraft.core.*;
import micdoodle8.mods.galacticraft.core.network.*;
import micdoodle8.mods.galacticraft.core.util.*;
import micdoodle8.mods.galacticraft.core.energy.*;
import org.lwjgl.opengl.*;

public class GuiSolar extends GuiContainerGC
{
    private static final ResourceLocation solarGuiTexture;
    private final TileEntitySolar solarPanel;
    private GuiButton buttonEnableSolar;
    private GuiElementInfoRegion electricInfoRegion;
    
    public GuiSolar(final InventoryPlayer par1InventoryPlayer, final TileEntitySolar solarPanel) {
        super((Container)new ContainerSolar(par1InventoryPlayer, solarPanel));
        this.electricInfoRegion = new GuiElementInfoRegion((this.width - this.xSize) / 2 + 107, (this.height - this.ySize) / 2 + 101, 56, 9, new ArrayList<String>(), this.width, this.height, this);
        this.solarPanel = solarPanel;
        this.ySize = 201;
        this.xSize = 176;
    }
    
    protected void actionPerformed(final GuiButton par1GuiButton) {
        switch (par1GuiButton.id) {
            case 0: {
                GalacticraftCore.packetPipeline.sendToServer(new PacketSimple(PacketSimple.EnumSimplePacket.S_UPDATE_DISABLEABLE_BUTTON, new Object[] { this.solarPanel.xCoord, this.solarPanel.yCoord, this.solarPanel.zCoord, 0 }));
                break;
            }
        }
    }
    
    public void initGui() {
        super.initGui();
        final List<String> electricityDesc = new ArrayList<String>();
        electricityDesc.add(GCCoreUtil.translate("gui.energyStorage.desc.0"));
        electricityDesc.add(EnumColor.YELLOW + GCCoreUtil.translate("gui.energyStorage.desc.1") + (int)Math.floor(this.solarPanel.getEnergyStoredGC()) + " / " + (int)Math.floor(this.solarPanel.getMaxEnergyStoredGC()));
        this.electricInfoRegion.tooltipStrings = electricityDesc;
        this.electricInfoRegion.xPosition = (this.width - this.xSize) / 2 + 96;
        this.electricInfoRegion.yPosition = (this.height - this.ySize) / 2 + 24;
        this.electricInfoRegion.parentWidth = this.width;
        this.electricInfoRegion.parentHeight = this.height;
        this.infoRegions.add(this.electricInfoRegion);
        final List<String> batterySlotDesc = new ArrayList<String>();
        batterySlotDesc.add(GCCoreUtil.translate("gui.batterySlot.desc.0"));
        batterySlotDesc.add(GCCoreUtil.translate("gui.batterySlot.desc.1"));
        this.infoRegions.add(new GuiElementInfoRegion((this.width - this.xSize) / 2 + 151, (this.height - this.ySize) / 2 + 82, 18, 18, batterySlotDesc, this.width, this.height, this));
        final List<String> sunGenDesc = new ArrayList<String>();
        final float sunVisible = Math.round(this.solarPanel.solarStrength / 9.0f * 1000.0f) / 10.0f;
        sunGenDesc.add((this.solarPanel.solarStrength > 0) ? (GCCoreUtil.translate("gui.status.sunVisible.name") + ": " + sunVisible + "%") : GCCoreUtil.translate("gui.status.blockedfully.name"));
        this.infoRegions.add(new GuiElementInfoRegion((this.width - this.xSize) / 2 + 47, (this.height - this.ySize) / 2 + 20, 18, 18, sunGenDesc, this.width, this.height, this));
        this.buttonList.add(this.buttonEnableSolar = new GuiButton(0, this.width / 2 - 36, this.height / 2 - 19, 72, 20, GCCoreUtil.translate("gui.button.enable.name")));
    }
    
    protected void drawGuiContainerForegroundLayer(final int par1, final int par2) {
        final int offsetY = 35;
        this.buttonEnableSolar.enabled = (this.solarPanel.disableCooldown == 0);
        this.buttonEnableSolar.displayString = (this.solarPanel.getDisabled(0) ? GCCoreUtil.translate("gui.button.enable.name") : GCCoreUtil.translate("gui.button.disable.name"));
        String displayString = this.solarPanel.getInventoryName();
        this.fontRendererObj.drawString(displayString, this.xSize / 2 - this.fontRendererObj.getStringWidth(displayString) / 2, 7, 4210752);
        displayString = GCCoreUtil.translate("gui.message.status.name") + ": " + this.getStatus();
        this.fontRendererObj.drawString(displayString, this.xSize / 2 - this.fontRendererObj.getStringWidth(displayString) / 2, 22 + offsetY, 4210752);
        displayString = GCCoreUtil.translate("gui.message.generating.name") + ": " + ((this.solarPanel.generateWatts > 0) ? (EnergyDisplayHelper.getEnergyDisplayS((float)this.solarPanel.generateWatts) + "/t") : GCCoreUtil.translate("gui.status.notGenerating.name"));
        this.fontRendererObj.drawString(displayString, this.xSize / 2 - this.fontRendererObj.getStringWidth(displayString) / 2, 11 + offsetY, 4210752);
        final float boost = Math.round((this.solarPanel.getSolarBoost() - 1.0f) * 1000.0f) / 10.0f;
        displayString = GCCoreUtil.translate("gui.message.environment.name") + ": " + boost + "%";
        this.fontRendererObj.drawString(displayString, this.xSize / 2 - this.fontRendererObj.getStringWidth(displayString) / 2, 33 + offsetY, 4210752);
        this.fontRendererObj.drawString(GCCoreUtil.translate("container.inventory"), 8, this.ySize - 94, 4210752);
    }
    
    private String getStatus() {
        if (this.solarPanel.getDisabled(0)) {
            return EnumColor.ORANGE + GCCoreUtil.translate("gui.status.disabled.name");
        }
        if (!this.solarPanel.getWorldObj().isDaytime()) {
            return EnumColor.DARK_RED + GCCoreUtil.translate("gui.status.blockedfully.name");
        }
        if (this.solarPanel.getWorldObj().isRaining() || this.solarPanel.getWorldObj().isThundering()) {
            return EnumColor.DARK_RED + GCCoreUtil.translate("gui.status.raining.name");
        }
        if (this.solarPanel.solarStrength == 0) {
            return EnumColor.DARK_RED + GCCoreUtil.translate("gui.status.blockedfully.name");
        }
        if (this.solarPanel.solarStrength < 9) {
            return EnumColor.DARK_RED + GCCoreUtil.translate("gui.status.blockedpartial.name");
        }
        if (this.solarPanel.generateWatts > 0) {
            return EnumColor.DARK_GREEN + GCCoreUtil.translate("gui.status.collectingenergy.name");
        }
        return EnumColor.ORANGE + GCCoreUtil.translate("gui.status.unknown.name");
    }
    
    protected void drawGuiContainerBackgroundLayer(final float var1, final int var2, final int var3) {
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.mc.getTextureManager().bindTexture(GuiSolar.solarGuiTexture);
        final int var4 = (this.width - this.xSize) / 2;
        final int var5 = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(var4, var5, 0, 0, this.xSize, this.ySize);
        final List<String> electricityDesc = new ArrayList<String>();
        EnergyDisplayHelper.getEnergyDisplayTooltip(this.solarPanel.getEnergyStoredGC(), this.solarPanel.getMaxEnergyStoredGC(), electricityDesc);
        this.electricInfoRegion.tooltipStrings = electricityDesc;
        if (this.solarPanel.getEnergyStoredGC() > 0.0f) {
            this.drawTexturedModalRect(var4 + 83, var5 + 24, 176, 0, 11, 10);
        }
        if (this.solarPanel.solarStrength > 0) {
            this.drawTexturedModalRect(var4 + 48, var5 + 21, 176, 10, 16, 16);
        }
        this.drawTexturedModalRect(var4 + 97, var5 + 25, 187, 0, Math.min(this.solarPanel.getScaledElecticalLevel(54), 54), 7);
    }
    
    static {
        solarGuiTexture = new ResourceLocation(GalacticraftCore.ASSET_PREFIX, "textures/gui/solar.png");
    }
}
