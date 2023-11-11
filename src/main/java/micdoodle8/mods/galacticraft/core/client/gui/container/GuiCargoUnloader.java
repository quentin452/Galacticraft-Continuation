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

public class GuiCargoUnloader extends GuiContainerGC
{
    private static final ResourceLocation unloaderTexture;
    private final TileEntityCargoUnloader cargoUnloader;
    private GuiButton buttonLoadItems;
    private GuiElementInfoRegion electricInfoRegion;
    
    public GuiCargoUnloader(final InventoryPlayer par1InventoryPlayer, final TileEntityCargoUnloader par2TileEntityAirDistributor) {
        super(new ContainerCargoLoader(par1InventoryPlayer, (IInventory)par2TileEntityAirDistributor));
        this.electricInfoRegion = new GuiElementInfoRegion((this.width - this.xSize) / 2 + 107, (this.height - this.ySize) / 2 + 101, 56, 9, new ArrayList<String>(), this.width, this.height, this);
        this.cargoUnloader = par2TileEntityAirDistributor;
        this.ySize = 201;
    }
    
    protected void actionPerformed(final GuiButton par1GuiButton) {
        switch (par1GuiButton.id) {
            case 0: {
                GalacticraftCore.packetPipeline.sendToServer(new PacketSimple(PacketSimple.EnumSimplePacket.S_UPDATE_DISABLEABLE_BUTTON, new Object[] { this.cargoUnloader.xCoord, this.cargoUnloader.yCoord, this.cargoUnloader.zCoord, 0 }));
                break;
            }
        }
    }
    
    public void initGui() {
        super.initGui();
        final List<String> electricityDesc = new ArrayList<String>();
        electricityDesc.add(GCCoreUtil.translate("gui.energyStorage.desc.0"));
        electricityDesc.add(EnumColor.YELLOW + GCCoreUtil.translate("gui.energyStorage.desc.1") + (int)Math.floor(this.cargoUnloader.getEnergyStoredGC()) + " / " + (int)Math.floor(this.cargoUnloader.getMaxEnergyStoredGC()));
        this.electricInfoRegion.tooltipStrings = electricityDesc;
        this.electricInfoRegion.xPosition = (this.width - this.xSize) / 2 + 107;
        this.electricInfoRegion.yPosition = (this.height - this.ySize) / 2 + 101;
        this.electricInfoRegion.parentWidth = this.width;
        this.electricInfoRegion.parentHeight = this.height;
        this.infoRegions.add(this.electricInfoRegion);
        final List<String> batterySlotDesc = new ArrayList<String>();
        batterySlotDesc.add(GCCoreUtil.translate("gui.batterySlot.desc.0"));
        batterySlotDesc.add(GCCoreUtil.translate("gui.batterySlot.desc.1"));
        this.infoRegions.add(new GuiElementInfoRegion((this.width - this.xSize) / 2 + 9, (this.height - this.ySize) / 2 + 26, 18, 18, batterySlotDesc, this.width, this.height, this));
        this.buttonList.add(this.buttonLoadItems = new GuiButton(0, this.width / 2 - 1, this.height / 2 - 23, 76, 20, GCCoreUtil.translate("gui.button.unloaditems.name")));
    }
    
    protected void drawGuiContainerForegroundLayer(final int par1, final int par2) {
        final int offsetX = -17;
        final int offsetY = 45;
        this.fontRendererObj.drawString(this.cargoUnloader.getInventoryName(), 60, 12, 4210752);
        this.buttonLoadItems.enabled = (this.cargoUnloader.disableCooldown == 0);
        this.buttonLoadItems.displayString = (this.cargoUnloader.getDisabled(0) ? GCCoreUtil.translate("gui.button.unloaditems.name") : GCCoreUtil.translate("gui.button.stopunloading.name"));
        this.fontRendererObj.drawString(GCCoreUtil.translate("gui.message.status.name") + ": " + this.getStatus(), 28 + offsetX, 22 + offsetY, 4210752);
        this.fontRendererObj.drawString(GCCoreUtil.translate("container.inventory"), 8, this.ySize - 90, 4210752);
    }
    
    private String getStatus() {
        if (this.cargoUnloader.noTarget) {
            return EnumColor.DARK_RED + GCCoreUtil.translate("gui.status.notargetunload.name");
        }
        if (this.cargoUnloader.targetEmpty) {
            return EnumColor.DARK_RED + GCCoreUtil.translate("gui.status.targetempty.name");
        }
        if (this.cargoUnloader.targetNoInventory) {
            return EnumColor.DARK_RED + GCCoreUtil.translate("gui.status.noinvtarget.name");
        }
        return this.cargoUnloader.getGUIstatus();
    }
    
    protected void drawGuiContainerBackgroundLayer(final float var1, final int var2, final int var3) {
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.mc.getTextureManager().bindTexture(GuiCargoUnloader.unloaderTexture);
        final int var4 = (this.width - this.xSize) / 2;
        final int var5 = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(var4, var5 + 5, 0, 0, this.xSize, this.ySize);
        final List<String> electricityDesc = new ArrayList<String>();
        electricityDesc.add(GCCoreUtil.translate("gui.energyStorage.desc.0"));
        EnergyDisplayHelper.getEnergyDisplayTooltip(this.cargoUnloader.getEnergyStoredGC(), this.cargoUnloader.getMaxEnergyStoredGC(), electricityDesc);
        this.electricInfoRegion.tooltipStrings = electricityDesc;
        if (this.cargoUnloader.getEnergyStoredGC() > 0.0f) {
            this.drawTexturedModalRect(var4 + 94, var5 + 101, 176, 0, 11, 10);
        }
        this.drawTexturedModalRect(var4 + 108, var5 + 102, 187, 0, Math.min(this.cargoUnloader.getScaledElecticalLevel(54), 54), 7);
    }
    
    static {
        unloaderTexture = new ResourceLocation(GalacticraftCore.ASSET_PREFIX, "textures/gui/cargo_loader.png");
    }
}
