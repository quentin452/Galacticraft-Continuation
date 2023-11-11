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

public class GuiCargoLoader extends GuiContainerGC
{
    public static final ResourceLocation loaderTexture;
    private final TileEntityCargoLoader cargoLoader;
    private GuiButton buttonLoadItems;
    private GuiElementInfoRegion electricInfoRegion;
    
    public GuiCargoLoader(final InventoryPlayer par1InventoryPlayer, final TileEntityCargoLoader par2TileEntityAirDistributor) {
        super(new ContainerCargoLoader(par1InventoryPlayer, (IInventory)par2TileEntityAirDistributor));
        this.electricInfoRegion = new GuiElementInfoRegion((this.width - this.xSize) / 2 + 107, (this.height - this.ySize) / 2 + 101, 56, 9, new ArrayList<String>(), this.width, this.height, this);
        this.cargoLoader = par2TileEntityAirDistributor;
        this.ySize = 201;
    }
    
    protected void actionPerformed(final GuiButton par1GuiButton) {
        switch (par1GuiButton.id) {
            case 0: {
                GalacticraftCore.packetPipeline.sendToServer(new PacketSimple(PacketSimple.EnumSimplePacket.S_UPDATE_DISABLEABLE_BUTTON, new Object[] { this.cargoLoader.xCoord, this.cargoLoader.yCoord, this.cargoLoader.zCoord, 0 }));
                break;
            }
        }
    }
    
    public void initGui() {
        super.initGui();
        final List<String> electricityDesc = new ArrayList<String>();
        electricityDesc.add(GCCoreUtil.translate("gui.energyStorage.desc.0"));
        electricityDesc.add(EnumColor.YELLOW + GCCoreUtil.translate("gui.energyStorage.desc.1") + (int)Math.floor(this.cargoLoader.getEnergyStoredGC()) + " / " + (int)Math.floor(this.cargoLoader.getMaxEnergyStoredGC()));
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
        this.buttonList.add(this.buttonLoadItems = new GuiButton(0, this.width / 2 - 1, this.height / 2 - 23, 76, 20, GCCoreUtil.translate("gui.button.loaditems.name")));
    }
    
    protected void drawGuiContainerForegroundLayer(final int par1, final int par2) {
        final int offsetX = -17;
        final int offsetY = 45;
        this.fontRendererObj.drawString(this.cargoLoader.getInventoryName(), 60, 12, 4210752);
        this.buttonLoadItems.enabled = (this.cargoLoader.disableCooldown == 0);
        this.buttonLoadItems.displayString = (this.cargoLoader.getDisabled(0) ? GCCoreUtil.translate("gui.button.loaditems.name") : GCCoreUtil.translate("gui.button.stoploading.name"));
        this.fontRendererObj.drawString(GCCoreUtil.translate("gui.message.status.name") + ": " + this.getStatus(), 28 + offsetX, 22 + offsetY, 4210752);
        this.fontRendererObj.drawString(GCCoreUtil.translate("container.inventory"), 8, this.ySize - 90, 4210752);
    }
    
    private String getStatus() {
        if (this.cargoLoader.outOfItems) {
            return EnumColor.DARK_RED + GCCoreUtil.translate("gui.status.noitems.name");
        }
        if (this.cargoLoader.noTarget) {
            return EnumColor.DARK_RED + GCCoreUtil.translate("gui.status.notargetload.name");
        }
        if (this.cargoLoader.targetNoInventory) {
            return EnumColor.DARK_RED + GCCoreUtil.translate("gui.status.noinvtarget.name");
        }
        if (this.cargoLoader.targetFull) {
            return EnumColor.DARK_RED + GCCoreUtil.translate("gui.status.targetfull.name");
        }
        return this.cargoLoader.getGUIstatus();
    }
    
    protected void drawGuiContainerBackgroundLayer(final float var1, final int var2, final int var3) {
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.mc.getTextureManager().bindTexture(GuiCargoLoader.loaderTexture);
        final int var4 = (this.width - this.xSize) / 2;
        final int var5 = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(var4, var5 + 5, 0, 0, this.xSize, this.ySize);
        final List<String> electricityDesc = new ArrayList<String>();
        electricityDesc.add(GCCoreUtil.translate("gui.energyStorage.desc.0"));
        EnergyDisplayHelper.getEnergyDisplayTooltip(this.cargoLoader.getEnergyStoredGC(), this.cargoLoader.getMaxEnergyStoredGC(), electricityDesc);
        this.electricInfoRegion.tooltipStrings = electricityDesc;
        if (this.cargoLoader.getEnergyStoredGC() > 0.0f) {
            this.drawTexturedModalRect(var4 + 94, var5 + 101, 176, 0, 11, 10);
        }
        this.drawTexturedModalRect(var4 + 108, var5 + 102, 187, 0, Math.min(this.cargoLoader.getScaledElecticalLevel(54), 54), 7);
    }
    
    static {
        loaderTexture = new ResourceLocation(GalacticraftCore.ASSET_PREFIX, "textures/gui/cargo_loader.png");
    }
}
