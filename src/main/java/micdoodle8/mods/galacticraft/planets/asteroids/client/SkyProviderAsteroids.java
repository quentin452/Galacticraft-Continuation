package micdoodle8.mods.galacticraft.planets.asteroids.client;

import net.minecraftforge.client.*;
import micdoodle8.mods.galacticraft.api.world.*;
import org.lwjgl.opengl.*;
import net.minecraft.client.multiplayer.*;
import net.minecraft.client.*;
import net.minecraft.client.renderer.*;
import cpw.mods.fml.client.*;
import java.util.*;
import micdoodle8.mods.galacticraft.core.util.*;
import net.minecraft.util.*;
import micdoodle8.mods.galacticraft.core.*;

public class SkyProviderAsteroids extends IRenderHandler
{
    private static final ResourceLocation overworldTexture;
    private static final ResourceLocation galaxyTexture;
    private static final ResourceLocation sunTexture;
    public int starGLCallList;
    public int glSkyList;
    public int glSkyList2;
    private float sunSize;
    
    public SkyProviderAsteroids(final IGalacticraftWorldProvider asteroidsProvider) {
        this.starGLCallList = GLAllocation.generateDisplayLists(3);
        this.sunSize = 17.5f * asteroidsProvider.getSolarSize();
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
        final Tessellator var23 = Tessellator.instance;
        GL11.glDisable(3553);
        GL11.glDisable(32826);
        GL11.glColor3f(1.0f, 1.0f, 1.0f);
        GL11.glDepthMask(false);
        GL11.glEnable(2912);
        GL11.glColor3f(0.0f, 0.0f, 0.0f);
        GL11.glCallList(this.glSkyList);
        GL11.glDisable(2912);
        GL11.glDisable(3008);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        RenderHelper.disableStandardItemLighting();
        GL11.glDisable(3553);
        GL11.glColor4f(0.7f, 0.7f, 0.7f, 0.7f);
        GL11.glCallList(this.starGLCallList);
        GL11.glPushMatrix();
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glRotatef(-90.0f, 0.0f, 1.0f, 0.0f);
        GL11.glRotatef(world.getCelestialAngle(partialTicks) * 360.0f, 1.0f, 0.0f, 0.0f);
        GL11.glBlendFunc(770, 771);
        GL11.glDisable(3553);
        GL11.glColor4f(0.0f, 0.0f, 0.0f, 1.0f);
        float var24 = this.sunSize / 4.2f;
        var23.startDrawingQuads();
        var23.addVertex((double)(-var24), 90.0, (double)(-var24));
        var23.addVertex((double)var24, 90.0, (double)(-var24));
        var23.addVertex((double)var24, 90.0, (double)var24);
        var23.addVertex((double)(-var24), 90.0, (double)var24);
        var23.draw();
        GL11.glEnable(3553);
        GL11.glBlendFunc(770, 1);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        var24 = this.sunSize / 1.2f;
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(SkyProviderAsteroids.sunTexture);
        var23.startDrawingQuads();
        var23.addVertexWithUV((double)(-var24), 90.0, (double)(-var24), 0.0, 0.0);
        var23.addVertexWithUV((double)var24, 90.0, (double)(-var24), 1.0, 0.0);
        var23.addVertexWithUV((double)var24, 90.0, (double)var24, 1.0, 1.0);
        var23.addVertexWithUV((double)(-var24), 90.0, (double)var24, 0.0, 1.0);
        var23.draw();
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        var24 = 0.5f;
        GL11.glScalef(0.6f, 0.6f, 0.6f);
        GL11.glRotatef(40.0f, 0.0f, 0.0f, 1.0f);
        GL11.glRotatef(200.0f, 1.0f, 0.0f, 0.0f);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(SkyProviderAsteroids.overworldTexture);
        var23.startDrawingQuads();
        var23.addVertexWithUV((double)(-var24), -100.0, (double)var24, 0.0, 1.0);
        var23.addVertexWithUV((double)var24, -100.0, (double)var24, 1.0, 1.0);
        var23.addVertexWithUV((double)var24, -100.0, (double)(-var24), 1.0, 0.0);
        var23.addVertexWithUV((double)(-var24), -100.0, (double)(-var24), 0.0, 0.0);
        var23.draw();
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glDisable(3042);
        GL11.glEnable(3008);
        GL11.glEnable(2912);
        GL11.glPopMatrix();
        GL11.glDisable(3553);
        GL11.glColor3f(0.0f, 0.0f, 0.0f);
        final double var25 = mc.thePlayer.getPosition(partialTicks).yCoord - world.getHorizon();
        GL11.glColor3f(0.2734375f, 0.2734375f, 0.2734375f);
        GL11.glEnable(3553);
        GL11.glDepthMask(true);
        GL11.glEnable(2903);
        GL11.glDisable(2912);
    }
    
    private void renderStars() {
        final Random var1 = new Random(10842L);
        final Tessellator var2 = Tessellator.instance;
        var2.startDrawingQuads();
        for (int var3 = 0; var3 < (ConfigManagerCore.moreStars ? 35000 : 6000); ++var3) {
            double var4 = var1.nextFloat() * 2.0f - 1.0f;
            double var5 = var1.nextFloat() * 2.0f - 1.0f;
            double var6 = var1.nextFloat() * 2.0f - 1.0f;
            final double var7 = 0.08f + var1.nextFloat() * 0.07f;
            double var8 = var4 * var4 + var5 * var5 + var6 * var6;
            if (var8 < 1.0 && var8 > 0.01) {
                var8 = 1.0 / Math.sqrt(var8);
                var4 *= var8;
                var5 *= var8;
                var6 *= var8;
                final double pX = var4 * (ConfigManagerCore.moreStars ? (var1.nextDouble() * 75.0 + 65.0) : 80.0);
                final double pY = var5 * (ConfigManagerCore.moreStars ? (var1.nextDouble() * 75.0 + 65.0) : 80.0);
                final double pZ = var6 * (ConfigManagerCore.moreStars ? (var1.nextDouble() * 75.0 + 65.0) : 80.0);
                final double var9 = Math.atan2(var4, var6);
                final double var10 = Math.sin(var9);
                final double var11 = Math.cos(var9);
                final double var12 = Math.atan2(Math.sqrt(var4 * var4 + var6 * var6), var5);
                final double var13 = Math.sin(var12);
                final double var14 = Math.cos(var12);
                final double var15 = var1.nextDouble() * 3.141592653589793 * 2.0;
                final double var16 = Math.sin(var15);
                final double var17 = Math.cos(var15);
                for (int i = 0; i < 4; ++i) {
                    final double i2 = ((i & 0x2) - 1) * var7;
                    final double i3 = ((i + 1 & 0x2) - 1) * var7;
                    final double var18 = i2 * var17 - i3 * var16;
                    final double var19 = i3 * var17 + i2 * var16;
                    final double var20 = -var18 * var14;
                    final double dX = var20 * var10 - var19 * var11;
                    final double dZ = var19 * var10 + var20 * var11;
                    final double dY = var18 * var13;
                    var2.addVertex(pX + dX, pY + dY, pZ + dZ);
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
        galaxyTexture = new ResourceLocation("galacticraftmars", "textures/gui/planets/galaxy.png");
        sunTexture = new ResourceLocation("textures/environment/sun.png");
    }
}
