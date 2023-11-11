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
import org.lwjgl.opengl.*;
import micdoodle8.mods.galacticraft.core.energy.*;

public class GuiFuelLoader extends GuiContainerGC
{
    private static final ResourceLocation fuelLoaderTexture;
    private final TileEntityFuelLoader fuelLoader;
    private GuiButton buttonLoadFuel;
    private GuiElementInfoRegion electricInfoRegion;
    
    public GuiFuelLoader(final InventoryPlayer par1InventoryPlayer, final TileEntityFuelLoader par2TileEntityAirDistributor) {
        super((Container)new ContainerFuelLoader(par1InventoryPlayer, par2TileEntityAirDistributor));
        this.electricInfoRegion = new GuiElementInfoRegion((this.width - this.xSize) / 2 + 112, (this.height - this.ySize) / 2 + 65, 56, 9, new ArrayList<String>(), this.width, this.height, this);
        this.fuelLoader = par2TileEntityAirDistributor;
        this.ySize = 180;
    }
    
    protected void actionPerformed(final GuiButton par1GuiButton) {
        switch (par1GuiButton.id) {
            case 0: {
                GalacticraftCore.packetPipeline.sendToServer(new PacketSimple(PacketSimple.EnumSimplePacket.S_UPDATE_DISABLEABLE_BUTTON, new Object[] { this.fuelLoader.xCoord, this.fuelLoader.yCoord, this.fuelLoader.zCoord, 0 }));
                break;
            }
        }
    }
    
    public void initGui() {
        super.initGui();
        final List<String> fuelTankDesc = new ArrayList<String>();
        fuelTankDesc.add(GCCoreUtil.translate("gui.fuelTank.desc.2"));
        fuelTankDesc.add(GCCoreUtil.translate("gui.fuelTank.desc.3"));
        this.infoRegions.add(new GuiElementInfoRegion((this.width - this.xSize) / 2 + 7, (this.height - this.ySize) / 2 + 33, 16, 38, fuelTankDesc, this.width, this.height, this));
        final List<String> batterySlotDesc = new ArrayList<String>();
        batterySlotDesc.add(GCCoreUtil.translate("gui.batterySlot.desc.0"));
        batterySlotDesc.add(GCCoreUtil.translate("gui.batterySlot.desc.1"));
        this.infoRegions.add(new GuiElementInfoRegion((this.width - this.xSize) / 2 + 50, (this.height - this.ySize) / 2 + 54, 18, 18, batterySlotDesc, this.width, this.height, this));
        final List<String> electricityDesc = new ArrayList<String>();
        electricityDesc.add(GCCoreUtil.translate("gui.energyStorage.desc.0"));
        electricityDesc.add(EnumColor.YELLOW + GCCoreUtil.translate("gui.energyStorage.desc.1") + (int)Math.floor(this.fuelLoader.getEnergyStoredGC()) + " / " + (int)Math.floor(this.fuelLoader.getMaxEnergyStoredGC()));
        this.electricInfoRegion.tooltipStrings = electricityDesc;
        this.electricInfoRegion.xPosition = (this.width - this.xSize) / 2 + 112;
        this.electricInfoRegion.yPosition = (this.height - this.ySize) / 2 + 65;
        this.electricInfoRegion.parentWidth = this.width;
        this.electricInfoRegion.parentHeight = this.height;
        this.infoRegions.add(this.electricInfoRegion);
        this.buttonList.add(this.buttonLoadFuel = new GuiButton(0, this.width / 2 + 2, this.height / 2 - 49, 76, 20, GCCoreUtil.translate("gui.button.loadfuel.name")));
    }
    
    protected void drawGuiContainerForegroundLayer(final int par1, final int par2) {
        this.fontRendererObj.drawString(this.fuelLoader.getInventoryName(), 60, 10, 4210752);
        this.buttonLoadFuel.enabled = (this.fuelLoader.disableCooldown == 0 && this.fuelLoader.fuelTank.getFluid() != null && this.fuelLoader.fuelTank.getFluid().amount != 0);
        this.buttonLoadFuel.displayString = (this.fuelLoader.getDisabled(0) ? GCCoreUtil.translate("gui.button.loadfuel.name") : GCCoreUtil.translate("gui.button.stoploading.name"));
        this.fontRendererObj.drawString(GCCoreUtil.translate("gui.message.status.name") + ": " + this.getStatus(), 28, 22, 4210752);
        this.fontRendererObj.drawString(GCCoreUtil.translate("container.inventory"), 8, this.ySize - 118 + 2 + 11, 4210752);
    }
    
    private String getStatus() {
        if (this.fuelLoader.fuelTank.getFluid() == null || this.fuelLoader.fuelTank.getFluid().amount == 0) {
            return EnumColor.DARK_RED + GCCoreUtil.translate("gui.status.nofuel.name");
        }
        return this.fuelLoader.getGUIstatus();
    }
    
    protected void drawGuiContainerBackgroundLayer(final float var1, final int var2, final int var3) {
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.mc.getTextureManager().bindTexture(GuiFuelLoader.fuelLoaderTexture);
        final int var4 = (this.width - this.xSize) / 2;
        final int var5 = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(var4, var5 + 5, 0, 0, this.xSize, 181);
        final int fuelLevel = this.fuelLoader.getScaledFuelLevel(38);
        this.drawTexturedModalRect((this.width - this.xSize) / 2 + 7, (this.height - this.ySize) / 2 + 17 + 54 - fuelLevel, 176, 38 - fuelLevel, 16, fuelLevel);
        final List<String> electricityDesc = new ArrayList<String>();
        electricityDesc.add(GCCoreUtil.translate("gui.energyStorage.desc.0"));
        EnergyDisplayHelper.getEnergyDisplayTooltip(this.fuelLoader.getEnergyStoredGC(), this.fuelLoader.getMaxEnergyStoredGC(), electricityDesc);
        this.electricInfoRegion.tooltipStrings = electricityDesc;
        if (this.fuelLoader.getEnergyStoredGC() > 0.0f) {
            this.drawTexturedModalRect(var4 + 99, var5 + 65, 192, 7, 11, 10);
        }
        this.drawTexturedModalRect(var4 + 113, var5 + 66, 192, 0, Math.min(this.fuelLoader.getScaledElecticalLevel(54), 54), 7);
    }
    
    static {
        fuelLoaderTexture = new ResourceLocation(GalacticraftCore.ASSET_PREFIX, "textures/gui/fuel_loader.png");
    }
}
