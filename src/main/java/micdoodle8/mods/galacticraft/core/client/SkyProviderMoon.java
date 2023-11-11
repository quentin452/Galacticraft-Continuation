package micdoodle8.mods.galacticraft.core.client;

import net.minecraftforge.client.*;
import org.lwjgl.opengl.*;
import net.minecraft.client.multiplayer.*;
import net.minecraft.client.*;
import micdoodle8.mods.galacticraft.core.proxy.*;
import micdoodle8.mods.galacticraft.core.*;
import micdoodle8.mods.galacticraft.core.network.*;
import micdoodle8.mods.galacticraft.core.dimension.*;
import net.minecraft.client.renderer.*;
import cpw.mods.fml.client.*;
import java.util.*;
import micdoodle8.mods.galacticraft.core.util.*;
import net.minecraft.util.*;

public class SkyProviderMoon extends IRenderHandler
{
    private static final ResourceLocation overworldTexture;
    private static final ResourceLocation sunTexture;
    public int starGLCallList;
    public int glSkyList;
    public int glSkyList2;
    
    public SkyProviderMoon() {
        this.starGLCallList = GLAllocation.generateDisplayLists(3);
        GL11.glPushMatrix();
        GL11.glNewList(this.starGLCallList, 4864);
        this.renderStars();
        GL11.glEndList();
        GL11.glPopMatrix();
        final Tessellator tessellator = Tessellator.instance;
        GL11.glNewList(this.glSkyList = this.starGLCallList + 1, 4864);
        final byte byte2 = 64;
        final int i = 6;
        float f = 16.0f;
        for (int j = -384; j <= 384; j += 64) {
            for (int l = -384; l <= 384; l += 64) {
                tessellator.startDrawingQuads();
                tessellator.addVertex((double)(j + 0), (double)f, (double)(l + 0));
                tessellator.addVertex((double)(j + 64), (double)f, (double)(l + 0));
                tessellator.addVertex((double)(j + 64), (double)f, (double)(l + 64));
                tessellator.addVertex((double)(j + 0), (double)f, (double)(l + 64));
                tessellator.draw();
            }
        }
        GL11.glEndList();
        GL11.glNewList(this.glSkyList2 = this.starGLCallList + 2, 4864);
        f = -16.0f;
        tessellator.startDrawingQuads();
        for (int k = -384; k <= 384; k += 64) {
            for (int i2 = -384; i2 <= 384; i2 += 64) {
                tessellator.addVertex((double)(k + 64), (double)f, (double)(i2 + 0));
                tessellator.addVertex((double)(k + 0), (double)f, (double)(i2 + 0));
                tessellator.addVertex((double)(k + 0), (double)f, (double)(i2 + 64));
                tessellator.addVertex((double)(k + 64), (double)f, (double)(i2 + 64));
            }
        }
        tessellator.draw();
        GL11.glEndList();
    }
    
    public void render(final float partialTicks, final WorldClient world, final Minecraft mc) {
        if (!ClientProxyCore.overworldTextureRequestSent) {
            GalacticraftCore.packetPipeline.sendToServer(new PacketSimple(PacketSimple.EnumSimplePacket.S_REQUEST_OVERWORLD_IMAGE, new Object[0]));
            ClientProxyCore.overworldTextureRequestSent = true;
        }
        WorldProviderMoon gcProvider = null;
        if (world.provider instanceof WorldProviderMoon) {
            gcProvider = (WorldProviderMoon)world.provider;
        }
        GL11.glDisable(3553);
        GL11.glDisable(32826);
        GL11.glColor3f(1.0f, 1.0f, 1.0f);
        final Tessellator var23 = Tessellator.instance;
        GL11.glDepthMask(false);
        GL11.glEnable(2912);
        GL11.glColor3f(0.0f, 0.0f, 0.0f);
        GL11.glCallList(this.glSkyList);
        GL11.glDisable(2912);
        GL11.glDisable(3008);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        RenderHelper.disableStandardItemLighting();
        float var24 = 0.0f;
        if (gcProvider != null) {
            var24 = gcProvider.getStarBrightness(partialTicks);
        }
        if (var24 > 0.0f) {
            GL11.glColor4f(1.0f, 1.0f, 1.0f, var24);
            GL11.glCallList(this.starGLCallList);
        }
        GL11.glEnable(3553);
        GL11.glBlendFunc(770, 1);
        GL11.glPushMatrix();
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GL11.glRotatef(-90.0f, 0.0f, 1.0f, 0.0f);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 5.0f);
        GL11.glRotatef(world.getCelestialAngle(partialTicks) * 360.0f, 1.0f, 0.0f, 0.0f);
        GL11.glBlendFunc(770, 771);
        GL11.glDisable(3553);
        GL11.glColor4f(0.0f, 0.0f, 0.0f, 1.0f);
        float var25 = 5.714286f;
        var23.startDrawingQuads();
        var23.addVertex((double)(-var25), 99.9, (double)(-var25));
        var23.addVertex((double)var25, 99.9, (double)(-var25));
        var23.addVertex((double)var25, 99.9, (double)var25);
        var23.addVertex((double)(-var25), 99.9, (double)var25);
        var23.draw();
        GL11.glEnable(3553);
        GL11.glBlendFunc(770, 1);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        var25 = 20.0f;
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(SkyProviderMoon.sunTexture);
        var23.startDrawingQuads();
        var23.addVertexWithUV((double)(-var25), 100.0, (double)(-var25), 0.0, 0.0);
        var23.addVertexWithUV((double)var25, 100.0, (double)(-var25), 1.0, 0.0);
        var23.addVertexWithUV((double)var25, 100.0, (double)var25, 1.0, 1.0);
        var23.addVertexWithUV((double)(-var25), 100.0, (double)var25, 0.0, 1.0);
        var23.draw();
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GL11.glDisable(3042);
        var25 = 10.0f;
        final float earthRotation = (float)(world.getSpawnPoint().posZ - mc.thePlayer.posZ) * 0.01f;
        GL11.glScalef(0.6f, 0.6f, 0.6f);
        GL11.glRotatef(earthRotation, 1.0f, 0.0f, 0.0f);
        GL11.glRotatef(200.0f, 1.0f, 0.0f, 0.0f);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        if (ClientProxyCore.overworldTexturesValid) {
            GL11.glBindTexture(3553, ClientProxyCore.overworldTextureClient.getGlTextureId());
        }
        else {
            FMLClientHandler.instance().getClient().renderEngine.bindTexture(SkyProviderMoon.overworldTexture);
        }
        world.getMoonPhase();
        var23.startDrawingQuads();
        var23.addVertexWithUV((double)(-var25), -100.0, (double)var25, 0.0, 1.0);
        var23.addVertexWithUV((double)var25, -100.0, (double)var25, 1.0, 1.0);
        var23.addVertexWithUV((double)var25, -100.0, (double)(-var25), 1.0, 0.0);
        var23.addVertexWithUV((double)(-var25), -100.0, (double)(-var25), 0.0, 0.0);
        var23.draw();
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glDisable(3042);
        GL11.glEnable(3008);
        GL11.glEnable(2912);
        GL11.glPopMatrix();
        GL11.glDisable(3553);
        GL11.glColor3f(0.0f, 0.0f, 0.0f);
        final double var26 = mc.thePlayer.getPosition(partialTicks).yCoord - world.getHorizon();
        if (var26 < 0.0) {
            GL11.glPushMatrix();
            GL11.glTranslatef(0.0f, 12.0f, 0.0f);
            GL11.glCallList(this.glSkyList2);
            GL11.glPopMatrix();
            final float var27 = 1.0f;
            final float var28 = -(float)(var26 + 65.0);
            var25 = -var27;
            var23.startDrawingQuads();
            var23.setColorRGBA_I(0, 255);
            var23.addVertex((double)(-var27), (double)var28, (double)var27);
            var23.addVertex((double)var27, (double)var28, (double)var27);
            var23.addVertex((double)var27, (double)var25, (double)var27);
            var23.addVertex((double)(-var27), (double)var25, (double)var27);
            var23.addVertex((double)(-var27), (double)var25, (double)(-var27));
            var23.addVertex((double)var27, (double)var25, (double)(-var27));
            var23.addVertex((double)var27, (double)var28, (double)(-var27));
            var23.addVertex((double)(-var27), (double)var28, (double)(-var27));
            var23.addVertex((double)var27, (double)var25, (double)(-var27));
            var23.addVertex((double)var27, (double)var25, (double)var27);
            var23.addVertex((double)var27, (double)var28, (double)var27);
            var23.addVertex((double)var27, (double)var28, (double)(-var27));
            var23.addVertex((double)(-var27), (double)var28, (double)(-var27));
            var23.addVertex((double)(-var27), (double)var28, (double)var27);
            var23.addVertex((double)(-var27), (double)var25, (double)var27);
            var23.addVertex((double)(-var27), (double)var25, (double)(-var27));
            var23.addVertex((double)(-var27), (double)var25, (double)(-var27));
            var23.addVertex((double)(-var27), (double)var25, (double)var27);
            var23.addVertex((double)var27, (double)var25, (double)var27);
            var23.addVertex((double)var27, (double)var25, (double)(-var27));
            var23.draw();
        }
        GL11.glColor3f(0.2734375f, 0.2734375f, 0.2734375f);
        GL11.glPushMatrix();
        GL11.glTranslatef(0.0f, -(float)(var26 - 16.0), 0.0f);
        GL11.glCallList(this.glSkyList2);
        GL11.glPopMatrix();
        GL11.glEnable(3553);
        GL11.glDepthMask(true);
        GL11.glEnable(2903);
        GL11.glDisable(2912);
    }
    
    private void renderStars() {
        final Random var1 = new Random(10842L);
        final Tessellator var2 = Tessellator.instance;
        var2.startDrawingQuads();
        for (int var3 = 0; var3 < (ConfigManagerCore.moreStars ? 20000 : 6000); ++var3) {
            double var4 = var1.nextFloat() * 2.0f - 1.0f;
            double var5 = var1.nextFloat() * 2.0f - 1.0f;
            double var6 = var1.nextFloat() * 2.0f - 1.0f;
            final double var7 = 0.15f + var1.nextFloat() * 0.1f;
            double var8 = var4 * var4 + var5 * var5 + var6 * var6;
            if (var8 < 1.0 && var8 > 0.01) {
                var8 = 1.0 / Math.sqrt(var8);
                var4 *= var8;
                var5 *= var8;
                var6 *= var8;
                final double var9 = var4 * (ConfigManagerCore.moreStars ? (var1.nextDouble() * 100.0 + 150.0) : 100.0);
                final double var10 = var5 * (ConfigManagerCore.moreStars ? (var1.nextDouble() * 100.0 + 150.0) : 100.0);
                final double var11 = var6 * (ConfigManagerCore.moreStars ? (var1.nextDouble() * 100.0 + 150.0) : 100.0);
                final double var12 = Math.atan2(var4, var6);
                final double var13 = Math.sin(var12);
                final double var14 = Math.cos(var12);
                final double var15 = Math.atan2(Math.sqrt(var4 * var4 + var6 * var6), var5);
                final double var16 = Math.sin(var15);
                final double var17 = Math.cos(var15);
                final double var18 = var1.nextDouble() * 3.141592653589793 * 2.0;
                final double var19 = Math.sin(var18);
                final double var20 = Math.cos(var18);
                for (int var21 = 0; var21 < 4; ++var21) {
                    final double var22 = 0.0;
                    final double var23 = ((var21 & 0x2) - 1) * var7;
                    final double var24 = ((var21 + 1 & 0x2) - 1) * var7;
                    final double var25 = var23 * var20 - var24 * var19;
                    final double var26 = var24 * var20 + var23 * var19;
                    final double var27 = var25 * var16 + 0.0 * var17;
                    final double var28 = 0.0 * var16 - var25 * var17;
                    final double var29 = var28 * var13 - var26 * var14;
                    final double var30 = var26 * var13 + var28 * var14;
                    var2.addVertex(var9 + var29, var10 + var27, var11 + var30);
                }
            }
        }
        var2.draw();
    }
    
    private Vec3 getCustomSkyColor() {
        return Vec3.createVectorHelper(0.26796875, 0.1796875, 0.0);
    }
    
    public float getSkyBrightness(final float par1) {
        final float var2 = FMLClientHandler.instance().getClient().theWorld.getCelestialAngle(par1);
        float var3 = 1.0f - (MathHelper.sin(var2 * 3.1415927f * 2.0f) * 2.0f + 0.25f);
        if (var3 < 0.0f) {
            var3 = 0.0f;
        }
        if (var3 > 1.0f) {
            var3 = 1.0f;
        }
        return var3 * var3 * 1.0f;
    }
    
    static {
        overworldTexture = new ResourceLocation(GalacticraftCore.ASSET_PREFIX, "textures/gui/celestialbodies/earth.png");
        sunTexture = new ResourceLocation("textures/environment/sun.png");
    }
}
