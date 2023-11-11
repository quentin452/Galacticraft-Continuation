package micdoodle8.mods.galacticraft.core.client.gui.overlay;

import cpw.mods.fml.relauncher.*;
import net.minecraft.client.*;
import net.minecraft.util.*;
import micdoodle8.mods.galacticraft.core.util.*;
import org.lwjgl.opengl.*;
import cpw.mods.fml.client.*;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.entity.player.*;
import net.minecraft.client.renderer.*;
import net.minecraft.entity.*;
import net.minecraft.world.*;
import net.minecraft.client.entity.*;
import net.minecraft.client.gui.*;

@SideOnly(Side.CLIENT)
public class OverlayRocket extends Overlay
{
    private static Minecraft minecraft;

    public static void renderSpaceshipOverlay(final ResourceLocation guiTexture) {
        if (guiTexture == null) {
            return;
        }
        final ScaledResolution scaledresolution = ClientUtil.getScaledRes(OverlayRocket.minecraft, OverlayRocket.minecraft.displayWidth, OverlayRocket.minecraft.displayHeight);
        scaledresolution.getScaledWidth();
        final int height = scaledresolution.getScaledHeight();
        OverlayRocket.minecraft.entityRenderer.setupOverlayRendering();
        GL11.glDepthMask(true);
        GL11.glEnable(3553);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(guiTexture);
        float var1 = 0.0f;
        float var2 = (float)(height / 2 - 85);
        float var3 = 0.0f;
        float var3b = 0.0f;
        float var4 = 0.0f;
        float var5 = 1.0f;
        float var6 = 1.0f;
        float var7 = 1.0f;
        float var8 = 1.0f;
        final float sizeScale = 0.65f;
        final Tessellator var9 = Tessellator.instance;
        var9.startDrawingQuads();
        var9.addVertexWithUV((double)(var1 + 0.0f), (double)(var2 + 242.0f * sizeScale), 0.0, (double)((var3 + 0.0f) * var7), (double)((var4 + var6) * var8));
        var9.addVertexWithUV((double)(var1 + 20.0f * sizeScale), (double)(var2 + 242.0f * sizeScale), 0.0, (double)((var3 + var5) * var7), (double)((var4 + var6) * var8));
        var9.addVertexWithUV((double)(var1 + 20.0f * sizeScale), (double)(var2 + 0.0f), 0.0, (double)((var3 + var5) * var7), (double)((var4 + 0.0f) * var8));
        var9.addVertexWithUV((double)(var1 + 0.0f), (double)(var2 + 0.0f), 0.0, (double)((var3 + 0.0f) * var7), (double)((var4 + 0.0f) * var8));
        var9.draw();
        GL11.glColor3f(1.0f, 1.0f, 1.0f);
        final Render spaceshipRender = (Render) RenderManager.instance.entityRenderMap.get(OverlayRocket.minecraft.thePlayer.ridingEntity.getClass());
        final int y1 = height / 2 + 60 - (int)Math.floor(Overlay.getPlayerPositionY((EntityPlayer)OverlayRocket.minecraft.thePlayer) / 10.5f);
        var1 = 2.5f;
        var2 = (float)y1;
        var3 = 8.0f;
        var3b = 40.0f;
        var4 = 8.0f;
        var5 = 8.0f;
        var6 = 8.0f;
        var7 = 0.015625f;
        var8 = 0.03125f;
        GL11.glPushMatrix();
        final int i = OverlayRocket.minecraft.thePlayer.ridingEntity.getBrightnessForRender(1.0f);
        final int j = i % 65536;
        final int k = i / 65536;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, j / 1.0f, k / 1.0f);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glEnable(2903);
        GL11.glTranslatef(var1 + 4.0f, var2 + 6.0f, 50.0f);
        GL11.glScalef(5.0f, 5.0f, 5.0f);
        GL11.glRotatef(180.0f, 1.0f, 0.0f, 0.0f);
        GL11.glRotatef(90.0f, 0.0f, 1.0f, 0.0f);
        try {
            spaceshipRender.doRender((Entity)OverlayRocket.minecraft.thePlayer.ridingEntity.getClass().getConstructor(World.class).newInstance(OverlayRocket.minecraft.thePlayer.worldObj), 0.0, 0.0, 0.0, 0.0f, 0.0f);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        GL11.glPopMatrix();
        ResourceLocation resourcelocation = AbstractClientPlayer.locationStevePng;
        resourcelocation = AbstractClientPlayer.getLocationSkin(OverlayRocket.minecraft.thePlayer.getGameProfile().getName());
        AbstractClientPlayer.getDownloadImageSkin(resourcelocation, OverlayRocket.minecraft.thePlayer.getGameProfile().getName());
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(resourcelocation);
        GL11.glDisable(2896);
        GL11.glTranslatef(0.0f, 0.0f, 60.0f);
        var9.startDrawingQuads();
        var9.addVertexWithUV((double)(var1 + 0.0f), (double)(var2 + var6), 0.0, (double)((var3 + 0.0f) * var7), (double)((var4 + var6) * var8));
        var9.addVertexWithUV((double)(var1 + var5), (double)(var2 + var6), 0.0, (double)((var3 + var5) * var7), (double)((var4 + var6) * var8));
        var9.addVertexWithUV((double)(var1 + var5), (double)(var2 + 0.0f), 0.0, (double)((var3 + var5) * var7), (double)((var4 + 0.0f) * var8));
        var9.addVertexWithUV((double)(var1 + 0.0f), (double)(var2 + 0.0f), 0.0, (double)((var3 + 0.0f) * var7), (double)((var4 + 0.0f) * var8));
        var9.draw();
        var9.startDrawingQuads();
        var9.addVertexWithUV((double)(var1 + 0.0f), (double)(var2 + var6), 0.0, (double)((var3b + 0.0f) * var7), (double)((var4 + var6) * var8));
        var9.addVertexWithUV((double)(var1 + var5), (double)(var2 + var6), 0.0, (double)((var3b + var5) * var7), (double)((var4 + var6) * var8));
        var9.addVertexWithUV((double)(var1 + var5), (double)(var2 + 0.0f), 0.0, (double)((var3b + var5) * var7), (double)((var4 + 0.0f) * var8));
        var9.addVertexWithUV((double)(var1 + 0.0f), (double)(var2 + 0.0f), 0.0, (double)((var3b + 0.0f) * var7), (double)((var4 + 0.0f) * var8));
        var9.draw();
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glEnable(2896);
    }

    static {
        OverlayRocket.minecraft = FMLClientHandler.instance().getClient();
    }
}
