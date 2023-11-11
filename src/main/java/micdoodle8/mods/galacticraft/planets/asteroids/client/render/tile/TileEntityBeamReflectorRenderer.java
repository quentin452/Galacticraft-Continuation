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
public class TileEntityBeamReflectorRenderer extends TileEntitySpecialRenderer
{
    public static final ResourceLocation reflectorTexture;
    public static IModelCustom reflectorModel;
    
    public TileEntityBeamReflectorRenderer() {
        TileEntityBeamReflectorRenderer.reflectorModel = AdvancedModelLoader.loadModel(new ResourceLocation("galacticraftasteroids", "models/reflector.obj"));
    }
    
    public void renderModelAt(final TileEntityBeamReflector tileEntity, final double d, final double d1, final double d2, final float f) {
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(TileEntityBeamReflectorRenderer.reflectorTexture);
        GL11.glPushMatrix();
        GL11.glTranslatef((float)d + 0.5f, (float)d1, (float)d2 + 0.5f);
        GL11.glScalef(0.5f, 0.5f, 0.5f);
        TileEntityBeamReflectorRenderer.reflectorModel.renderPart("Base");
        GL11.glRotatef(tileEntity.yaw, 0.0f, 1.0f, 0.0f);
        TileEntityBeamReflectorRenderer.reflectorModel.renderPart("Axle");
        final float dX = 0.0f;
        final float dY = 1.13228f;
        final float dZ = 0.0f;
        GL11.glTranslatef(dX, dY, dZ);
        GL11.glRotatef(tileEntity.pitch, 1.0f, 0.0f, 0.0f);
        GL11.glTranslatef(-dX, -dY, -dZ);
        TileEntityBeamReflectorRenderer.reflectorModel.renderPart("EnergyBlaster");
        GL11.glTranslatef(dX, dY, dZ);
        GL11.glRotatef((float)(tileEntity.ticks * 500), 0.0f, 0.0f, 1.0f);
        GL11.glTranslatef(-dX, -dY, -dZ);
        TileEntityBeamReflectorRenderer.reflectorModel.renderPart("Ring");
        GL11.glPopMatrix();
    }
    
    public void renderTileEntityAt(final TileEntity tileEntity, final double var2, final double var4, final double var6, final float var8) {
        this.renderModelAt((TileEntityBeamReflector)tileEntity, var2, var4, var6, var8);
    }
    
    static {
        reflectorTexture = new ResourceLocation("galacticraftasteroids", "textures/model/beamReflector.png");
    }
}
