package micdoodle8.mods.galacticraft.planets.asteroids.client.render.tile;

import net.minecraft.client.renderer.tileentity.*;
import cpw.mods.fml.relauncher.*;
import net.minecraft.util.*;
import net.minecraftforge.client.model.*;
import micdoodle8.mods.galacticraft.planets.asteroids.tile.*;
import org.lwjgl.opengl.*;
import cpw.mods.fml.client.*;
import net.minecraft.client.renderer.*;
import net.minecraft.tileentity.*;

@SideOnly(Side.CLIENT)
public class TileEntityMinerBaseRenderer extends TileEntitySpecialRenderer
{
    public static final ResourceLocation telepadTexture;
    public static IModelCustom telepadModel;
    
    public TileEntityMinerBaseRenderer() {
        TileEntityMinerBaseRenderer.telepadModel = AdvancedModelLoader.loadModel(new ResourceLocation("galacticraftasteroids", "models/minerbase.obj"));
    }
    
    public void renderModelAt(final TileEntityMinerBase tileEntity, final double d, final double d1, final double d2, final float f) {
        GL11.glDisable(32826);
        if (!tileEntity.isMaster) {
            return;
        }
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(TileEntityMinerBaseRenderer.telepadTexture);
        final int i = tileEntity.getWorldObj().getLightBrightnessForSkyBlocks(tileEntity.xCoord, tileEntity.yCoord + 1, tileEntity.zCoord, 0);
        final int j = i % 65536;
        final int k = i / 65536;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, j / 1.0f, k / 1.0f);
        GL11.glPushMatrix();
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glTranslatef((float)d + 1.0f, (float)d1 + 1.0f, (float)d2 + 1.0f);
        GL11.glScalef(0.05f, 0.05f, 0.05f);
        switch (tileEntity.facing) {
            case 0: {
                GL11.glRotatef(180.0f, 0.0f, 1.0f, 0.0f);
            }
            case 2: {
                GL11.glRotatef(270.0f, 0.0f, 1.0f, 0.0f);
                break;
            }
            case 3: {
                GL11.glRotatef(90.0f, 0.0f, 1.0f, 0.0f);
                break;
            }
        }
        TileEntityMinerBaseRenderer.telepadModel.renderAll();
        GL11.glPopMatrix();
    }
    
    public void renderTileEntityAt(final TileEntity tileEntity, final double var2, final double var4, final double var6, final float var8) {
        this.renderModelAt((TileEntityMinerBase)tileEntity, var2, var4, var6, var8);
    }
    
    static {
        telepadTexture = new ResourceLocation("galacticraftasteroids", "textures/model/minerbase.png");
    }
}
