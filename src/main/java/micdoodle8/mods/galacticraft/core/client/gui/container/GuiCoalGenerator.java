package micdoodle8.mods.galacticraft.core.client.gui.container;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import micdoodle8.mods.galacticraft.core.GalacticraftCore;
import micdoodle8.mods.galacticraft.core.energy.EnergyDisplayHelper;
import micdoodle8.mods.galacticraft.core.inventory.ContainerCoalGenerator;
import micdoodle8.mods.galacticraft.core.tile.TileEntityCoalGenerator;
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil;

@SideOnly(Side.CLIENT)
public class GuiCoalGenerator extends GuiContainer {

    private static final ResourceLocation coalGeneratorTexture = new ResourceLocation(
        GalacticraftCore.ASSET_PREFIX,
        "textures/gui/coal_generator.png");

    private final TileEntityCoalGenerator tileEntity;

    public GuiCoalGenerator(InventoryPlayer playerInventory, TileEntityCoalGenerator tileEntity) {
        super(new ContainerCoalGenerator(playerInventory, tileEntity));
        this.tileEntity = tileEntity;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        this.fontRendererObj.drawString(this.tileEntity.getInventoryName(), 55, 6, 4210752);
        String displayText = GCCoreUtil.translate("gui.status.generating.name");

        if (this.tileEntity.heatGJperTick <= 0
            || this.tileEntity.heatGJperTick < TileEntityCoalGenerator.MIN_GENERATE_GJ_PER_TICK) {
            displayText = GCCoreUtil.translate("gui.status.notGenerating.name");
        }

        this.fontRendererObj
            .drawString(displayText, 122 - this.fontRendererObj.getStringWidth(displayText) / 2, 33, 4210752);

        if (this.tileEntity.heatGJperTick < TileEntityCoalGenerator.MIN_GENERATE_GJ_PER_TICK) {
            displayText = GCCoreUtil.translate("gui.status.hullHeat.name") + ": "
                + (int) (this.tileEntity.heatGJperTick / TileEntityCoalGenerator.MIN_GENERATE_GJ_PER_TICK * 100)
                + "%";
        } else {
            displayText = EnergyDisplayHelper.getEnergyDisplayS(
                this.tileEntity.heatGJperTick - TileEntityCoalGenerator.MIN_GENERATE_GJ_PER_TICK) + "/t";
        }

        this.fontRendererObj
            .drawString(displayText, 122 - this.fontRendererObj.getStringWidth(displayText) / 2, 45, 4210752);
        this.fontRendererObj.drawString(GCCoreUtil.translate("container.inventory"), 8, this.ySize - 96 + 2, 4210752);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        this.mc.renderEngine.bindTexture(GuiCoalGenerator.coalGeneratorTexture);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        final int containerWidth = (this.width - this.xSize) / 2;
        final int containerHeight = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(containerWidth, containerHeight, 0, 0, this.xSize, this.ySize);
    }
}
