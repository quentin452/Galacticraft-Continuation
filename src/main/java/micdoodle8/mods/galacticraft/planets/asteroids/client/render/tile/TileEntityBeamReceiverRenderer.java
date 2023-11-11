package micdoodle8.mods.galacticraft.planets.asteroids.client.render.tile;

import net.minecraft.client.renderer.tileentity.*;
import cpw.mods.fml.relauncher.*;
import net.minecraft.util.*;
import net.minecraftforge.client.model.*;
import micdoodle8.mods.galacticraft.planets.asteroids.tile.*;
import cpw.mods.fml.client.*;
import org.lwjgl.opengl.*;
import net.minecraftforge.common.util.*;
import micdoodle8.mods.galacticraft.core.tile.*;
import net.minecraft.tileentity.*;

@SideOnly(Side.CLIENT)
public class TileEntityBeamReceiverRenderer extends TileEntitySpecialRenderer
{
    public static final ResourceLocation receiverTexture;
    public static IModelCustom receiverModel;
    
    public TileEntityBeamReceiverRenderer() {
        TileEntityBeamReceiverRenderer.receiverModel = AdvancedModelLoader.loadModel(new ResourceLocation("galacticraftasteroids", "models/receiver.obj"));
    }
    
    public void renderModelAt(final TileEntityBeamReceiver tileEntity, final double d, final double d1, final double d2, final float f) {
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(TileEntityBeamReceiverRenderer.receiverTexture);
        GL11.glPushMatrix();
        GL11.glTranslatef((float)d + 0.5f, (float)d1, (float)d2 + 0.5f);
        GL11.glScalef(0.85f, 0.85f, 0.85f);
        switch (ForgeDirection.getOrientation(tileEntity.facing)) {
            case DOWN: {
                GL11.glTranslatef(0.7f, -0.15f, 0.0f);
                GL11.glRotatef(90.0f, 0.0f, 0.0f, 1.0f);
                break;
            }
            case UP: {
                GL11.glTranslatef(-0.7f, 1.3f, 0.0f);
                GL11.glRotatef(-90.0f, 0.0f, 0.0f, 1.0f);
                break;
            }
            case EAST: {
                GL11.glTranslatef(0.7f, -0.15f, 0.0f);
                GL11.glRotatef(180.0f, 0.0f, 1.0f, 0.0f);
                break;
            }
            case SOUTH: {
                GL11.glTranslatef(0.0f, -0.15f, 0.7f);
                GL11.glRotatef(90.0f, 0.0f, 1.0f, 0.0f);
                break;
            }
            case WEST: {
                GL11.glTranslatef(-0.7f, -0.15f, 0.0f);
                GL11.glRotatef(0.0f, 0.0f, 1.0f, 0.0f);
                break;
            }
            case NORTH: {
                GL11.glTranslatef(0.0f, -0.15f, -0.7f);
                GL11.glRotatef(270.0f, 0.0f, 1.0f, 0.0f);
                break;
            }
            default: {
                GL11.glPopMatrix();
                return;
            }
        }
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        TileEntityBeamReceiverRenderer.receiverModel.renderPart("Main");
        if (tileEntity.modeReceive == ReceiverMode.RECEIVE.ordinal()) {
            GL11.glColor3f(0.0f, 0.8f, 0.0f);
        }
        else if (tileEntity.modeReceive == ReceiverMode.EXTRACT.ordinal()) {
            GL11.glColor3f(0.6f, 0.0f, 0.0f);
        }
        else {
            GL11.glColor3f(0.1f, 0.1f, 0.1f);
        }
        GL11.glDisable(3553);
        GL11.glDisable(2884);
        TileEntityBeamReceiverRenderer.receiverModel.renderPart("Receiver");
        GL11.glEnable(3553);
        GL11.glEnable(2884);
        final float dX = 0.34772f;
        final float dY = 0.75097f;
        final float dZ = 0.0f;
        GL11.glTranslatef(dX, dY, dZ);
        if (tileEntity.modeReceive != ReceiverMode.UNDEFINED.ordinal()) {
            GL11.glRotatef((float)(-tileEntity.ticks * 50), 1.0f, 0.0f, 0.0f);
        }
        GL11.glTranslatef(-dX, -dY, -dZ);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        TileEntityBeamReceiverRenderer.receiverModel.renderPart("Ring");
        GL11.glPopMatrix();
    }
    
    public void renderTileEntityAt(final TileEntity tileEntity, final double var2, final double var4, final double var6, final float var8) {
        this.renderModelAt((TileEntityBeamReceiver)tileEntity, var2, var4, var6, var8);
    }
    
    static {
        receiverTexture = new ResourceLocation("galacticraftasteroids", "textures/model/beamReceiver.png");
    }
}
