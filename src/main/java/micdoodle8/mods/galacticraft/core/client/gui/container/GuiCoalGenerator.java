package micdoodle8.mods.galacticraft.core.client.gui.container;

import net.minecraft.client.gui.inventory.*;
import cpw.mods.fml.relauncher.*;
import net.minecraft.util.*;
import micdoodle8.mods.galacticraft.core.tile.*;
import net.minecraft.entity.player.*;
import micdoodle8.mods.galacticraft.core.inventory.*;
import net.minecraft.inventory.*;
import micdoodle8.mods.galacticraft.core.util.*;
import micdoodle8.mods.galacticraft.core.energy.*;
import org.lwjgl.opengl.*;
import micdoodle8.mods.galacticraft.core.*;

@SideOnly(Side.CLIENT)
public class GuiCoalGenerator extends GuiContainer
{
    private static final ResourceLocation coalGeneratorTexture;
    private TileEntityCoalGenerator tileEntity;
    
    public GuiCoalGenerator(final InventoryPlayer playerInventory, final TileEntityCoalGenerator tileEntity) {
        super((Container)new ContainerCoalGenerator(playerInventory, tileEntity));
        this.tileEntity = tileEntity;
    }
    
    protected void drawGuiContainerForegroundLayer(final int mouseX, final int mouseY) {
        this.fontRendererObj.drawString(this.tileEntity.getInventoryName(), 55, 6, 4210752);
        String displayText = GCCoreUtil.translate("gui.status.generating.name");
        if (this.tileEntity.heatGJperTick <= 0.0f || this.tileEntity.heatGJperTick < 30.0f) {
            displayText = GCCoreUtil.translate("gui.status.notGenerating.name");
        }
        this.fontRendererObj.drawString(displayText, 122 - this.fontRendererObj.getStringWidth(displayText) / 2, 33, 4210752);
        if (this.tileEntity.heatGJperTick < 30.0f) {
            displayText = GCCoreUtil.translate("gui.status.hullHeat.name") + ": " + (int)(this.tileEntity.heatGJperTick / 30.0f * 100.0f) + "%";
        }
        else {
            displayText = EnergyDisplayHelper.getEnergyDisplayS(this.tileEntity.heatGJperTick - 30.0f) + "/t";
        }
        this.fontRendererObj.drawString(displayText, 122 - this.fontRendererObj.getStringWidth(displayText) / 2, 45, 4210752);
        this.fontRendererObj.drawString(GCCoreUtil.translate("container.inventory"), 8, this.ySize - 96 + 2, 4210752);
    }
    
    protected void drawGuiContainerBackgroundLayer(final float partialTicks, final int mouseX, final int mouseY) {
        this.mc.renderEngine.bindTexture(GuiCoalGenerator.coalGeneratorTexture);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        final int containerWidth = (this.width - this.xSize) / 2;
        final int containerHeight = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(containerWidth, containerHeight, 0, 0, this.xSize, this.ySize);
    }
    
    static {
        coalGeneratorTexture = new ResourceLocation(GalacticraftCore.ASSET_PREFIX, "textures/gui/coal_generator.png");
    }
}
