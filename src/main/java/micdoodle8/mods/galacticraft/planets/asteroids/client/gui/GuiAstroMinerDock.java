package micdoodle8.mods.galacticraft.planets.asteroids.client.gui;

import micdoodle8.mods.galacticraft.planets.asteroids.tile.*;
import net.minecraft.client.gui.*;
import micdoodle8.mods.galacticraft.core.client.gui.element.*;
import net.minecraft.entity.player.*;
import micdoodle8.mods.galacticraft.planets.asteroids.inventory.*;
import net.minecraft.inventory.*;
import java.util.*;
import micdoodle8.mods.galacticraft.planets.asteroids.entities.*;
import micdoodle8.mods.galacticraft.core.util.*;
import micdoodle8.mods.galacticraft.core.*;
import micdoodle8.mods.galacticraft.core.network.*;
import net.minecraft.util.*;
import org.lwjgl.opengl.*;
import micdoodle8.mods.galacticraft.core.energy.*;
import micdoodle8.mods.galacticraft.core.client.gui.container.*;
import net.minecraft.client.renderer.*;

public class GuiAstroMinerDock extends GuiContainerGC
{
    private static final ResourceLocation dockGui;
    private TileEntityMinerBase tile;
    private GuiButton recallButton;
    private GuiElementInfoRegion electricInfoRegion;
    private boolean extraLines;
    
    public GuiAstroMinerDock(final InventoryPlayer playerInventory, final TileEntityMinerBase dock) {
        super((Container)new ContainerAstroMinerDock(playerInventory, (IInventory)dock));
        this.electricInfoRegion = new GuiElementInfoRegion((this.width - this.xSize) / 2 + 233, (this.height - this.ySize) / 2 + 31, 10, 68, (List)new ArrayList(), this.width, this.height, (GuiContainerGC)this);
        this.xSize = 256;
        this.ySize = 221;
        this.tile = dock;
    }
    
    public void drawScreen(final int par1, final int par2, final float par3) {
        this.recallButton.enabled = true;
        if (this.tile.linkedMinerID == null) {
            this.recallButton.enabled = false;
        }
        else {
            final EntityAstroMiner miner = this.tile.linkedMiner;
            if (miner == null || miner.isDead || this.tile.linkCountDown == 0 || miner.AIstate < 2 || miner.AIstate == 5) {
                this.recallButton.enabled = false;
            }
        }
        this.recallButton.displayString = GCCoreUtil.translate("gui.button.recall.name");
        super.drawScreen(par1, par2, par3);
    }
    
    public void initGui() {
        super.initGui();
        final int xPos = (this.width - this.xSize) / 2;
        final int yPos = (this.height - this.ySize) / 2;
        final List<String> electricityDesc = new ArrayList<String>();
        electricityDesc.add(GCCoreUtil.translate("gui.energyStorage.desc.0"));
        electricityDesc.add(EnumColor.YELLOW + GCCoreUtil.translate("gui.energyStorage.desc.1") + (int)Math.floor(this.tile.getEnergyStoredGC()) + " / " + (int)Math.floor(this.tile.getMaxEnergyStoredGC()));
        this.electricInfoRegion.tooltipStrings = electricityDesc;
        this.electricInfoRegion.xPosition = xPos + 233;
        this.electricInfoRegion.yPosition = yPos + 29;
        this.electricInfoRegion.parentWidth = this.width;
        this.electricInfoRegion.parentHeight = this.height;
        this.infoRegions.add(this.electricInfoRegion);
        final List<String> batterySlotDesc = new ArrayList<String>();
        batterySlotDesc.add(GCCoreUtil.translate("gui.batterySlot.desc.0"));
        batterySlotDesc.add(GCCoreUtil.translate("gui.batterySlot.desc.1"));
        this.infoRegions.add(new GuiElementInfoRegion(xPos + 230, yPos + 108, 18, 18, (List)batterySlotDesc, this.width, this.height, (GuiContainerGC)this));
        this.buttonList.add(this.recallButton = new GuiButton(0, xPos + 173, yPos + 195, 76, 20, GCCoreUtil.translate("gui.button.recall.name")));
    }
    
    protected void mouseClicked(final int px, final int py, final int par3) {
        super.mouseClicked(px, py, par3);
    }
    
    protected void actionPerformed(final GuiButton par1GuiButton) {
        if (par1GuiButton.enabled) {
            switch (par1GuiButton.id) {
                case 0: {
                    GalacticraftCore.packetPipeline.sendToServer((IPacket)new PacketSimple(PacketSimple.EnumSimplePacket.S_UPDATE_DISABLEABLE_BUTTON, new Object[] { this.tile.xCoord, this.tile.yCoord, this.tile.zCoord, 0 }));
                    break;
                }
            }
        }
    }
    
    private String getDeltaString(final int num) {
        return (num > 0) ? ("+" + num) : ("" + num);
    }
    
    protected void drawGuiContainerForegroundLayer(final int par1, final int par2) {
        this.fontRendererObj.drawString(this.tile.getInventoryName(), 7, 6, 4210752);
        this.fontRendererObj.drawString(this.getStatus(), 177, 141, 4210752);
        if (this.extraLines) {
            this.fontRendererObj.drawString("\u0394x: " + this.getDeltaString(MathHelper.floor_double(this.tile.linkedMiner.posX) - this.tile.xCoord - 1), 186, 152, 2536735);
        }
        if (this.extraLines) {
            this.fontRendererObj.drawString("\u0394y: " + this.getDeltaString(MathHelper.floor_double(this.tile.linkedMiner.posY) - this.tile.yCoord), 186, 162, 2536735);
        }
        if (this.extraLines) {
            this.fontRendererObj.drawString("\u0394z: " + this.getDeltaString(MathHelper.floor_double(this.tile.linkedMiner.posZ) - this.tile.zCoord - 1), 186, 172, 2536735);
        }
        if (this.extraLines) {
            this.fontRendererObj.drawString(GCCoreUtil.translate("gui.miner.mined") + ": " + this.tile.linkedMiner.mineCount, 177, 183, 2536735);
        }
        this.fontRendererObj.drawString(GCCoreUtil.translate("container.inventory"), 7, this.ySize - 92, 4210752);
    }
    
    private String getStatus() {
        this.extraLines = false;
        if (this.tile.linkedMinerID == null) {
            return "";
        }
        final EntityAstroMiner miner = this.tile.linkedMiner;
        if (miner == null || miner.isDead) {
            return "";
        }
        if (this.tile.linkCountDown == 0) {
            return EnumColor.ORANGE + GCCoreUtil.translate("gui.miner.outOfRange");
        }
        switch (miner.AIstate) {
            case -1: {
                return EnumColor.ORANGE + GCCoreUtil.translate("gui.miner.offline");
            }
            case 0: {
                this.extraLines = true;
                return EnumColor.RED + GCCoreUtil.translate("gui.miner.stuck");
            }
            case 1: {
                return EnumColor.BRIGHT_GREEN + GCCoreUtil.translate("gui.miner.docked");
            }
            case 2: {
                this.extraLines = true;
                return EnumColor.BRIGHT_GREEN + GCCoreUtil.translate("gui.miner.travelling");
            }
            case 3: {
                this.extraLines = true;
                return EnumColor.BRIGHT_GREEN + GCCoreUtil.translate("gui.miner.mining");
            }
            case 4: {
                this.extraLines = true;
                return EnumColor.BRIGHT_GREEN + GCCoreUtil.translate("gui.miner.returning");
            }
            case 5: {
                return EnumColor.BRIGHT_GREEN + GCCoreUtil.translate("gui.miner.docking");
            }
            default: {
                return "";
            }
        }
    }
    
    protected void drawGuiContainerBackgroundLayer(final float var1, final int var2, final int var3) {
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        final int xPos = (this.width - this.xSize) / 2;
        final int yPos = (this.height - this.ySize) / 2;
        this.mc.getTextureManager().bindTexture(GuiAstroMinerDock.dockGui);
        this.drawTexturedModalRect(xPos, yPos, 0, 0, this.xSize, this.ySize);
        final List<String> electricityDesc = new ArrayList<String>();
        electricityDesc.add(GCCoreUtil.translate("gui.energyStorage.desc.0"));
        EnergyDisplayHelper.getEnergyDisplayTooltip(this.tile.getEnergyStoredGC(), this.tile.getMaxEnergyStoredGC(), (List)electricityDesc);
        this.electricInfoRegion.tooltipStrings = electricityDesc;
        this.mc.getTextureManager().bindTexture(GuiCargoLoader.loaderTexture);
        if (this.tile.getEnergyStoredGC() > 0.0f) {
            this.drawTexturedModalRect(xPos + 233, yPos + 17, 176, 0, 11, 10);
        }
        final int level = Math.min(this.tile.getScaledElecticalLevel(66), 66);
        this.drawColorModalRect(xPos + 234, yPos + 29 + 66 - level, 8, level, 12692004);
    }
    
    public void drawColorModalRect(final int p_73729_1_, final int p_73729_2_, final int p_73729_5_, final int p_73729_6_, final int color) {
        final float f = 0.00390625f;
        final float f2 = 0.00390625f;
        final Tessellator tessellator = Tessellator.instance;
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        tessellator.startDrawingQuads();
        tessellator.setColorOpaque_I(color);
        tessellator.addVertex((double)(p_73729_1_ + 0), (double)(p_73729_2_ + p_73729_6_), (double)this.zLevel);
        tessellator.addVertex((double)(p_73729_1_ + p_73729_5_), (double)(p_73729_2_ + p_73729_6_), (double)this.zLevel);
        tessellator.addVertex((double)(p_73729_1_ + p_73729_5_), (double)(p_73729_2_ + 0), (double)this.zLevel);
        tessellator.addVertex((double)(p_73729_1_ + 0), (double)(p_73729_2_ + 0), (double)this.zLevel);
        tessellator.draw();
        GL11.glEnable(3553);
        GL11.glDisable(3042);
    }
    
    static {
        dockGui = new ResourceLocation("galacticraftasteroids", "textures/gui/guiAstroMinerDock.png");
    }
}
