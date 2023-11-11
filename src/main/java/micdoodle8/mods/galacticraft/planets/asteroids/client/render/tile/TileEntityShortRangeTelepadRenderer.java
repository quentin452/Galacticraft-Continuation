package micdoodle8.mods.galacticraft.planets.asteroids.client.render.tile;

import net.minecraft.client.renderer.tileentity.*;
import cpw.mods.fml.relauncher.*;
import net.minecraft.util.*;
import net.minecraftforge.client.model.*;
import micdoodle8.mods.galacticraft.planets.asteroids.tile.*;
import cpw.mods.fml.client.*;
import org.lwjgl.opengl.*;
import net.minecraft.tileentity.*;

@SideOnly(Side.CLIENT)
public class TileEntityShortRangeTelepadRenderer extends TileEntitySpecialRenderer
{
    public static final ResourceLocation telepadTexture;
    public static final ResourceLocation telepadTexture0;
    public static IModelCustom telepadModel;
    
    public TileEntityShortRangeTelepadRenderer() {
        TileEntityShortRangeTelepadRenderer.telepadModel = AdvancedModelLoader.loadModel(new ResourceLocation("galacticraftasteroids", "models/teleporter.obj"));
    }
    
    public void renderModelAt(final TileEntityShortRangeTelepad tileEntity, final double d, final double d1, final double d2, final float f) {
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(TileEntityShortRangeTelepadRenderer.telepadTexture);
        GL11.glPushMatrix();
        GL11.glTranslatef((float)d + 0.5f, (float)d1, (float)d2 + 0.5f);
        GL11.glScalef(1.0f, 0.65f, 1.0f);
        TileEntityShortRangeTelepadRenderer.telepadModel.renderPart("Base");
        GL11.glTranslatef(0.0f, (float)Math.sin(tileEntity.ticks / 10.0f) / 15.0f - 0.25f, 0.0f);
        TileEntityShortRangeTelepadRenderer.telepadModel.renderPart("Top");
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GL11.glTranslatef((float)d + 0.5f, (float)d1 - 0.18f, (float)d2 + 0.5f);
        GL11.glScalef(1.0f, 0.65f, 1.0f);
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(TileEntityShortRangeTelepadRenderer.telepadTexture0);
        TileEntityShortRangeTelepadRenderer.telepadModel.renderPart("TopMidxNegz");
        TileEntityShortRangeTelepadRenderer.telepadModel.renderPart("TopPosxNegz");
        TileEntityShortRangeTelepadRenderer.telepadModel.renderPart("TopNegxNegz");
        TileEntityShortRangeTelepadRenderer.telepadModel.renderPart("TopMidxMidz");
        TileEntityShortRangeTelepadRenderer.telepadModel.renderPart("TopPosxMidz");
        TileEntityShortRangeTelepadRenderer.telepadModel.renderPart("TopNegxMidz");
        TileEntityShortRangeTelepadRenderer.telepadModel.renderPart("TopMidxPosz");
        TileEntityShortRangeTelepadRenderer.telepadModel.renderPart("TopPosxPosz");
        TileEntityShortRangeTelepadRenderer.telepadModel.renderPart("TopNegxPosz");
        GL11.glPopMatrix();
    }
    
    public void renderTileEntityAt(final TileEntity tileEntity, final double var2, final double var4, final double var6, final float var8) {
        this.renderModelAt((TileEntityShortRangeTelepad)tileEntity, var2, var4, var6, var8);
    }
    
    static {
        telepadTexture = new ResourceLocation("galacticraftasteroids", "textures/model/teleporter.png");
        telepadTexture0 = new ResourceLocation("galacticraftasteroids", "textures/model/teleporter0.png");
    }
}
