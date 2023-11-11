package micdoodle8.mods.galacticraft.core.client.gui.container;

import cpw.mods.fml.relauncher.*;
import net.minecraft.util.*;
import micdoodle8.mods.galacticraft.core.client.gui.element.*;
import micdoodle8.mods.galacticraft.core.tile.*;
import net.minecraft.entity.player.*;
import micdoodle8.mods.galacticraft.core.inventory.*;
import net.minecraft.inventory.*;
import java.util.*;
import micdoodle8.mods.galacticraft.core.util.*;
import org.lwjgl.opengl.*;
import micdoodle8.mods.galacticraft.core.*;

@SideOnly(Side.CLIENT)
public class GuiIngotCompressor extends GuiContainerGC
{
    private static final ResourceLocation electricFurnaceTexture;
    private GuiElementInfoRegion processInfoRegion;
    private TileEntityIngotCompressor tileEntity;
    
    public GuiIngotCompressor(final InventoryPlayer par1InventoryPlayer, final TileEntityIngotCompressor tileEntity) {
        super((Container)new ContainerIngotCompressor(par1InventoryPlayer, tileEntity));
        this.processInfoRegion = new GuiElementInfoRegion(0, 0, 52, 25, null, 0, 0, this);
        this.tileEntity = tileEntity;
        this.ySize = 192;
    }
    
    public void initGui() {
        super.initGui();
        this.processInfoRegion.tooltipStrings = new ArrayList<String>();
        this.processInfoRegion.xPosition = (this.width - this.xSize) / 2 + 77;
        this.processInfoRegion.yPosition = (this.height - this.ySize) / 2 + 30;
        this.processInfoRegion.parentWidth = this.width;
        this.processInfoRegion.parentHeight = this.height;
        this.infoRegions.add(this.processInfoRegion);
    }
    
    protected void drawGuiContainerForegroundLayer(final int par1, final int par2) {
        this.fontRendererObj.drawString(this.tileEntity.getInventoryName(), 10, 6, 4210752);
        String displayText = GCCoreUtil.translate("gui.message.fuel.name") + ":";
        this.fontRendererObj.drawString(displayText, 50 - this.fontRendererObj.getStringWidth(displayText), 79, 4210752);
        if (this.tileEntity.processTicks > 0) {
            displayText = EnumColor.BRIGHT_GREEN + GCCoreUtil.translate("gui.status.compressing.name");
        }
        else {
            displayText = EnumColor.ORANGE + GCCoreUtil.translate("gui.status.idle.name");
        }
        String str = GCCoreUtil.translate("gui.message.status.name") + ":";
        this.fontRendererObj.drawString(GCCoreUtil.translate("gui.message.status.name") + ":", 120 - this.fontRendererObj.getStringWidth(str) / 2, 70, 4210752);
        str = displayText;
        this.fontRendererObj.drawString(displayText, 120 - this.fontRendererObj.getStringWidth(str) / 2, 80, 4210752);
        this.fontRendererObj.drawString(GCCoreUtil.translate("container.inventory"), 8, this.ySize - 96 + 2, 4210752);
    }
    
    protected void drawGuiContainerBackgroundLayer(final float par1, final int par2, final int par3) {
        this.mc.renderEngine.bindTexture(GuiIngotCompressor.electricFurnaceTexture);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        final int containerWidth = (this.width - this.xSize) / 2;
        final int containerHeight = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(containerWidth, containerHeight, 0, 0, this.xSize, this.ySize);
        int process;
        if (this.tileEntity.processTicks > 0) {
            final double n = this.tileEntity.processTicks;
            final TileEntityIngotCompressor tileEntity = this.tileEntity;
            process = (int)(n / 200.0 * 100.0);
        }
        else {
            process = 0;
        }
        final List<String> processDesc = new ArrayList<String>();
        processDesc.clear();
        processDesc.add(GCCoreUtil.translate("gui.electricCompressor.desc.0") + ": " + process + "%");
        this.processInfoRegion.tooltipStrings = processDesc;
        if (this.tileEntity.processTicks > 0) {
            final int scale = (int)(this.tileEntity.processTicks / 200.0 * 54.0);
            this.drawTexturedModalRect(containerWidth + 77, containerHeight + 36, 176, 13, scale, 17);
        }
        if (this.tileEntity.furnaceBurnTime > 0) {
            final int scale = (int)(this.tileEntity.furnaceBurnTime / (double)this.tileEntity.currentItemBurnTime * 14.0);
            this.drawTexturedModalRect(containerWidth + 81, containerHeight + 27 + 14 - scale, 176, 44 - scale, 14, scale);
        }
        if (this.tileEntity.processTicks > 100) {
            this.drawTexturedModalRect(containerWidth + 101, containerHeight + 28, 176, 0, 15, 13);
        }
    }
    
    static {
        electricFurnaceTexture = new ResourceLocation(GalacticraftCore.ASSET_PREFIX, "textures/gui/ingotCompressor.png");
    }
}
