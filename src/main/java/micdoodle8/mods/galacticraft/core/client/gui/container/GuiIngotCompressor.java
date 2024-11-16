package micdoodle8.mods.galacticraft.core.client.gui.container;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import micdoodle8.mods.galacticraft.core.GalacticraftCore;
import micdoodle8.mods.galacticraft.core.client.gui.element.GuiElementInfoRegion;
import micdoodle8.mods.galacticraft.core.inventory.ContainerIngotCompressor;
import micdoodle8.mods.galacticraft.core.tile.TileEntityIngotCompressor;
import micdoodle8.mods.galacticraft.core.util.EnumColor;
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil;

@SideOnly(Side.CLIENT)
public class GuiIngotCompressor extends GuiContainerGC {

    private static final ResourceLocation electricFurnaceTexture = new ResourceLocation(
        GalacticraftCore.ASSET_PREFIX,
        "textures/gui/ingotCompressor.png");
    private final GuiElementInfoRegion processInfoRegion = new GuiElementInfoRegion(0, 0, 52, 25, null, 0, 0, this);

    private final TileEntityIngotCompressor tileEntity;

    public GuiIngotCompressor(InventoryPlayer par1InventoryPlayer, TileEntityIngotCompressor tileEntity) {
        super(new ContainerIngotCompressor(par1InventoryPlayer, tileEntity));
        this.tileEntity = tileEntity;
        this.ySize = 192;
    }

    @Override
    public void initGui() {
        super.initGui();
        this.processInfoRegion.tooltipStrings = new ArrayList<>();
        this.processInfoRegion.xPosition = (this.width - this.xSize) / 2 + 77;
        this.processInfoRegion.yPosition = (this.height - this.ySize) / 2 + 30;
        this.processInfoRegion.parentWidth = this.width;
        this.processInfoRegion.parentHeight = this.height;
        this.infoRegions.add(this.processInfoRegion);
    }

    /**
     * Draw the foreground layer for the GuiContainer (everything in front of the items)
     */
    @Override
    protected void drawGuiContainerForegroundLayer(int par1, int par2) {
        this.fontRendererObj.drawString(this.tileEntity.getInventoryName(), 10, 6, 4210752);
        String displayText = GCCoreUtil.translate("gui.message.fuel.name") + ":";
        this.fontRendererObj
            .drawString(displayText, 50 - this.fontRendererObj.getStringWidth(displayText), 79, 4210752);

        if (this.tileEntity.processTicks > 0) {
            displayText = EnumColor.BRIGHT_GREEN + GCCoreUtil.translate("gui.status.compressing.name");
        } else {
            displayText = EnumColor.ORANGE + GCCoreUtil.translate("gui.status.idle.name");
        }

        String str = GCCoreUtil.translate("gui.message.status.name") + ":";
        this.fontRendererObj.drawString(
            GCCoreUtil.translate("gui.message.status.name") + ":",
            120 - this.fontRendererObj.getStringWidth(str) / 2,
            70,
            4210752);
        str = displayText;
        this.fontRendererObj.drawString(displayText, 120 - this.fontRendererObj.getStringWidth(str) / 2, 80, 4210752);
        this.fontRendererObj.drawString(GCCoreUtil.translate("container.inventory"), 8, this.ySize - 96 + 2, 4210752);
    }

    /**
     * Draw the background layer for the GuiContainer (everything behind the items)
     */
    @Override
    protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
        this.mc.renderEngine.bindTexture(GuiIngotCompressor.electricFurnaceTexture);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        int process;
        final int containerWidth = (this.width - this.xSize) / 2;
        final int containerHeight = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(containerWidth, containerHeight, 0, 0, this.xSize, this.ySize);

        if (this.tileEntity.processTicks > 0) {
            process = (int) ((double) this.tileEntity.processTicks
                / (double) TileEntityIngotCompressor.PROCESS_TIME_REQUIRED
                * 100);
        } else {
            process = 0;
        }

        final List<String> processDesc = new ArrayList<>();
        processDesc.clear();
        processDesc.add(GCCoreUtil.translate("gui.electricCompressor.desc.0") + ": " + process + "%");
        this.processInfoRegion.tooltipStrings = processDesc;

        if (this.tileEntity.processTicks > 0) {
            final int scale = (int) ((double) this.tileEntity.processTicks
                / (double) TileEntityIngotCompressor.PROCESS_TIME_REQUIRED
                * 54);
            this.drawTexturedModalRect(containerWidth + 77, containerHeight + 36, 176, 13, scale, 17);
        }

        if (this.tileEntity.furnaceBurnTime > 0) {
            final int scale = (int) ((double) this.tileEntity.furnaceBurnTime
                / (double) this.tileEntity.currentItemBurnTime
                * 14);
            this.drawTexturedModalRect(
                containerWidth + 81,
                containerHeight + 27 + 14 - scale,
                176,
                30 + 14 - scale,
                14,
                scale);
        }

        if (this.tileEntity.processTicks > TileEntityIngotCompressor.PROCESS_TIME_REQUIRED / 2) {
            this.drawTexturedModalRect(containerWidth + 101, containerHeight + 28, 176, 0, 15, 13);
        }
    }
}
