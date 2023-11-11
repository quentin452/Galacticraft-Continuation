package micdoodle8.mods.galacticraft.planets.mars.client.gui;

import micdoodle8.mods.galacticraft.core.client.gui.container.*;
import net.minecraft.util.*;
import micdoodle8.mods.galacticraft.planets.mars.tile.*;
import net.minecraft.client.gui.*;
import micdoodle8.mods.galacticraft.core.client.gui.element.*;
import micdoodle8.mods.galacticraft.planets.mars.inventory.*;
import net.minecraft.inventory.*;
import java.util.*;
import micdoodle8.mods.galacticraft.core.*;
import micdoodle8.mods.galacticraft.core.network.*;
import micdoodle8.mods.galacticraft.core.util.*;
import org.lwjgl.opengl.*;
import micdoodle8.mods.galacticraft.core.energy.*;
import net.minecraft.entity.player.*;

public class GuiTerraformer extends GuiContainerGC implements GuiElementCheckbox.ICheckBoxCallback
{
    private static final ResourceLocation terraformerGui;
    private TileEntityTerraformer terraformer;
    private GuiButton enableTreesButton;
    private GuiButton enableGrassButton;
    private GuiElementCheckbox checkboxRenderBubble;
    private GuiElementInfoRegion electricInfoRegion;
    private GuiElementInfoRegion waterTankInfoRegion;
    
    public GuiTerraformer(final InventoryPlayer par1InventoryPlayer, final TileEntityTerraformer terraformer) {
        super((Container)new ContainerTerraformer(par1InventoryPlayer, terraformer));
        this.electricInfoRegion = new GuiElementInfoRegion(0, 0, 52, 9, (List)null, 0, 0, (GuiContainerGC)this);
        this.waterTankInfoRegion = new GuiElementInfoRegion(0, 0, 41, 28, (List)null, 0, 0, (GuiContainerGC)this);
        this.ySize = 237;
        this.terraformer = terraformer;
    }
    
    public void drawScreen(final int par1, final int par2, final float par3) {
        if (this.terraformer.disableCooldown > 0) {
            this.enableTreesButton.enabled = false;
            this.enableGrassButton.enabled = false;
        }
        else {
            this.enableTreesButton.enabled = true;
            this.enableGrassButton.enabled = true;
        }
        this.enableTreesButton.displayString = GCCoreUtil.translate(this.terraformer.treesDisabled ? "gui.button.enable.name" : "gui.button.disable.name") + " " + GCCoreUtil.translate("gui.message.trees.name");
        this.enableGrassButton.displayString = GCCoreUtil.translate(this.terraformer.grassDisabled ? "gui.button.enable.name" : "gui.button.disable.name") + " " + GCCoreUtil.translate("gui.message.grass.name");
        super.drawScreen(par1, par2, par3);
    }
    
    public void initGui() {
        super.initGui();
        this.electricInfoRegion.tooltipStrings = new ArrayList();
        this.electricInfoRegion.xPosition = (this.width - this.xSize) / 2 + 44;
        this.electricInfoRegion.yPosition = (this.height - this.ySize) / 2 + 47;
        this.electricInfoRegion.parentWidth = this.width;
        this.electricInfoRegion.parentHeight = this.height;
        this.infoRegions.add(this.electricInfoRegion);
        List<String> batterySlotDesc = new ArrayList<String>();
        batterySlotDesc.add(GCCoreUtil.translate("gui.batterySlot.desc.0"));
        batterySlotDesc.add(GCCoreUtil.translate("gui.batterySlot.desc.1"));
        this.infoRegions.add(new GuiElementInfoRegion((this.width - this.xSize) / 2 + 24, (this.height - this.ySize) / 2 + 38, 18, 18, (List)batterySlotDesc, this.width, this.height, (GuiContainerGC)this));
        batterySlotDesc = new ArrayList<String>();
        batterySlotDesc.add(GCCoreUtil.translate("gui.showBubble.desc.0"));
        this.infoRegions.add(new GuiElementInfoRegion((this.width - this.xSize) / 2 + 85, (this.height - this.ySize) / 2 + 132, 85, 13, (List)batterySlotDesc, this.width, this.height, (GuiContainerGC)this));
        this.waterTankInfoRegion.tooltipStrings = new ArrayList();
        this.waterTankInfoRegion.xPosition = (this.width - this.xSize) / 2 + 55;
        this.waterTankInfoRegion.yPosition = (this.height - this.ySize) / 2 + 17;
        this.waterTankInfoRegion.parentWidth = this.width;
        this.waterTankInfoRegion.parentHeight = this.height;
        this.infoRegions.add(this.waterTankInfoRegion);
        this.buttonList.clear();
        final int var5 = (this.width - this.xSize) / 2;
        final int var6 = (this.height - this.ySize) / 2;
        this.enableTreesButton = new GuiButton(0, var5 + 98, var6 + 85, 72, 20, GCCoreUtil.translate("gui.message.enableTrees.name"));
        this.enableGrassButton = new GuiButton(1, var5 + 98, var6 + 109, 72, 20, GCCoreUtil.translate("gui.message.enableGrass.name"));
        this.buttonList.add(this.enableTreesButton);
        this.buttonList.add(this.enableGrassButton);
        this.checkboxRenderBubble = new GuiElementCheckbox(2, (GuiElementCheckbox.ICheckBoxCallback)this, var5 + 85, var6 + 132, GCCoreUtil.translate("gui.message.bubbleVisible.name"));
        this.buttonList.add(this.checkboxRenderBubble);
    }
    
    protected void actionPerformed(final GuiButton par1GuiButton) {
        if (par1GuiButton.enabled) {
            switch (par1GuiButton.id) {
                case 0: {
                    GalacticraftCore.packetPipeline.sendToServer((IPacket)new PacketSimple(PacketSimple.EnumSimplePacket.S_UPDATE_DISABLEABLE_BUTTON, new Object[] { this.terraformer.xCoord, this.terraformer.yCoord, this.terraformer.zCoord, 0 }));
                    break;
                }
                case 1: {
                    GalacticraftCore.packetPipeline.sendToServer((IPacket)new PacketSimple(PacketSimple.EnumSimplePacket.S_UPDATE_DISABLEABLE_BUTTON, new Object[] { this.terraformer.xCoord, this.terraformer.yCoord, this.terraformer.zCoord, 1 }));
                }
            }
        }
    }
    
    protected void drawGuiContainerForegroundLayer(final int par1, final int par2) {
        final String displayString = this.terraformer.getInventoryName();
        this.fontRendererObj.drawString(displayString, this.xSize / 2 - this.fontRendererObj.getStringWidth(displayString) / 2, 5, 4210752);
        this.fontRendererObj.drawString(GCCoreUtil.translate("container.inventory"), 8, 144, 4210752);
        this.fontRendererObj.drawSplitString(this.getStatus(), 105, 24, this.xSize - 105, 4210752);
    }
    
    private String getStatus() {
        if (RedstoneUtil.isBlockReceivingRedstone(this.terraformer.getWorldObj(), this.terraformer.xCoord, this.terraformer.yCoord, this.terraformer.zCoord)) {
            return EnumColor.RED + GCCoreUtil.translate("gui.status.off.name");
        }
        if (this.terraformer.getEnergyStoredGC() <= 0.0f) {
            return EnumColor.RED + GCCoreUtil.translate("gui.message.noEnergy.name");
        }
        if (this.terraformer.grassDisabled && this.terraformer.treesDisabled) {
            return EnumColor.ORANGE + GCCoreUtil.translate("gui.status.disabled.name");
        }
        if (this.terraformer.waterTank.getFluid() == null || this.terraformer.waterTank.getFluid().amount <= 0) {
            return EnumColor.RED + GCCoreUtil.translate("gui.message.noWater.name");
        }
        if (this.terraformer.getFirstBonemealStack() == null) {
            return EnumColor.RED + GCCoreUtil.translate("gui.message.noBonemeal.name");
        }
        if (!this.terraformer.grassDisabled && this.terraformer.getFirstSeedStack() == null) {
            return EnumColor.RED + GCCoreUtil.translate("gui.message.noSeeds.name");
        }
        if (!this.terraformer.treesDisabled && this.terraformer.getFirstSaplingStack() == null) {
            return EnumColor.RED + GCCoreUtil.translate("gui.message.noSaplings.name");
        }
        final double n = this.terraformer.getBubbleSize();
        this.terraformer.getClass();
        if (n < 15.0 - 0.5) {
            return EnumColor.YELLOW + GCCoreUtil.translate("gui.message.bubbleExp.name");
        }
        if (!this.terraformer.treesDisabled && this.terraformer.grassBlocksListSize <= 0) {
            return EnumColor.RED + GCCoreUtil.translate("gui.message.trees.name") + ": " + GCCoreUtil.translate("gui.message.noValidBlocks.name");
        }
        if (!this.terraformer.grassDisabled && this.terraformer.terraformableBlocksListSize <= 0) {
            return EnumColor.RED + GCCoreUtil.translate("gui.message.grass.name") + ": " + GCCoreUtil.translate("gui.message.noValidBlocks.name");
        }
        return EnumColor.BRIGHT_GREEN + GCCoreUtil.translate("gui.message.terraforming.name");
    }
    
    protected void drawGuiContainerBackgroundLayer(final float par1, final int par2, final int par3) {
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.mc.renderEngine.bindTexture(GuiTerraformer.terraformerGui);
        final int var5 = (this.width - this.xSize) / 2;
        final int var6 = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(var5, var6, 0, 0, this.xSize, this.ySize);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        final int scale = this.terraformer.getScaledElecticalLevel(54);
        this.drawTexturedModalRect(var5 + 45, var6 + 48, 176, 26, Math.min(scale, 54), 7);
        final List<String> electricityDesc = new ArrayList<String>();
        electricityDesc.add(GCCoreUtil.translate("gui.energyStorage.desc.0"));
        EnergyDisplayHelper.getEnergyDisplayTooltip(this.terraformer.getEnergyStoredGC(), this.terraformer.getMaxEnergyStoredGC(), (List)electricityDesc);
        this.electricInfoRegion.tooltipStrings = electricityDesc;
        int waterLevel = this.terraformer.getScaledWaterLevel(100);
        final List<String> processDesc = new ArrayList<String>();
        processDesc.clear();
        processDesc.add(GCCoreUtil.translate("gui.terraformer.desc.0") + ": " + waterLevel + "%");
        this.waterTankInfoRegion.tooltipStrings = processDesc;
        waterLevel = this.terraformer.getScaledWaterLevel(26);
        this.drawTexturedModalRect((this.width - this.xSize) / 2 + 56, (this.height - this.ySize) / 2 + 17 + 27 - waterLevel, 176, 26 - waterLevel, 39, waterLevel);
        this.checkboxRenderBubble.isSelected = this.terraformer.shouldRenderBubble;
    }
    
    public void onSelectionChanged(final GuiElementCheckbox checkbox, final boolean newSelected) {
        this.terraformer.shouldRenderBubble = newSelected;
        GalacticraftCore.packetPipeline.sendToServer((IPacket)new PacketSimple(PacketSimple.EnumSimplePacket.S_ON_ADVANCED_GUI_CLICKED_INT, new Object[] { 6, this.terraformer.xCoord, this.terraformer.yCoord, this.terraformer.zCoord, newSelected ? 1 : 0 }));
    }
    
    public boolean canPlayerEdit(final GuiElementCheckbox checkbox, final EntityPlayer player) {
        return true;
    }
    
    public boolean getInitiallySelected(final GuiElementCheckbox checkbox) {
        return this.terraformer.shouldRenderBubble;
    }
    
    public void onIntruderInteraction() {
    }
    
    static {
        terraformerGui = new ResourceLocation("galacticraftmars", "textures/gui/terraformer.png");
    }
}
