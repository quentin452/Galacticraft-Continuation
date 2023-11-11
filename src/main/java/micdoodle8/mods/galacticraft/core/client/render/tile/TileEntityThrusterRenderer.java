package micdoodle8.mods.galacticraft.core.client.render.tile;

import net.minecraft.client.renderer.tileentity.*;
import cpw.mods.fml.relauncher.*;
import net.minecraft.util.*;
import micdoodle8.mods.galacticraft.core.tile.*;
import org.lwjgl.opengl.*;
import cpw.mods.fml.client.*;
import net.minecraft.tileentity.*;
import micdoodle8.mods.galacticraft.core.*;
import net.minecraftforge.client.model.*;

@SideOnly(Side.CLIENT)
public class TileEntityThrusterRenderer extends TileEntitySpecialRenderer
{
    public static final ResourceLocation thrusterTexture;
    public static final IModelCustom thrusterModel;
    
    public void renderModelAt(final TileEntityThruster tileEntity, final double d, final double d1, final double d2, final float f) {
        GL11.glPushMatrix();
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(TileEntityThrusterRenderer.thrusterTexture);
        GL11.glTranslatef((float)d + 0.5f, (float)d1 + 0.5f, (float)d2 + 0.5f);
        int meta = tileEntity.getBlockMetadata();
        final boolean reverseThruster = meta >= 8;
        meta &= 0x7;
        if (meta >= 1) {
            switch (meta) {
                case 1: {
                    GL11.glTranslatef(-0.475f, 0.0f, 0.0f);
                    GL11.glRotatef(0.0f, 0.0f, 1.0f, 0.0f);
                    GL11.glScalef(0.55f, 0.55f, 0.55f);
                    break;
                }
                case 2: {
                    GL11.glTranslatef(0.475f, 0.0f, 0.0f);
                    GL11.glRotatef(180.0f, 0.0f, 1.0f, 0.0f);
                    GL11.glScalef(0.55f, 0.55f, 0.55f);
                    break;
                }
                case 3: {
                    GL11.glTranslatef(0.0f, 0.0f, -0.475f);
                    GL11.glRotatef(270.0f, 0.0f, 1.0f, 0.0f);
                    GL11.glScalef(0.55f, 0.55f, 0.55f);
                    break;
                }
                case 4: {
                    GL11.glTranslatef(0.0f, 0.0f, 0.475f);
                    GL11.glRotatef(90.0f, 0.0f, 1.0f, 0.0f);
                    GL11.glScalef(0.55f, 0.55f, 0.55f);
                    break;
                }
            }
            if (!reverseThruster) {
                GL11.glRotatef(180.0f, 1.0f, 0.0f, 0.0f);
            }
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            TileEntityThrusterRenderer.thrusterModel.renderAll();
        }
        GL11.glPopMatrix();
    }
    
    public void renderTileEntityAt(final TileEntity tileEntity, final double var2, final double var4, final double var6, final float var8) {
        this.renderModelAt((TileEntityThruster)tileEntity, var2, var4, var6, var8);
    }
    
    static {
        thrusterTexture = new ResourceLocation(GalacticraftCore.ASSET_PREFIX, "textures/model/thruster.png");
        thrusterModel = AdvancedModelLoader.loadModel(new ResourceLocation(GalacticraftCore.ASSET_PREFIX, "models/thruster.obj"));
    }
}
