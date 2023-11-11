package micdoodle8.mods.galacticraft.core.client;

import net.minecraftforge.client.*;
import net.minecraft.client.*;
import micdoodle8.mods.galacticraft.core.*;
import cpw.mods.fml.client.*;
import org.lwjgl.opengl.*;
import java.util.*;
import net.minecraft.client.multiplayer.*;
import micdoodle8.mods.galacticraft.core.proxy.*;
import micdoodle8.mods.galacticraft.core.network.*;
import org.lwjgl.util.glu.*;
import net.minecraft.entity.*;
import net.minecraft.client.renderer.*;
import net.minecraft.world.*;
import java.lang.reflect.*;
import net.minecraft.util.*;
import micdoodle8.mods.galacticraft.core.util.*;
import net.minecraft.launchwrapper.*;

public class SkyProviderOverworld extends IRenderHandler
{
    private static final ResourceLocation moonTexture;
    private static final ResourceLocation sunTexture;
    private static boolean optifinePresent;
    public int starGLCallList;
    public int glSkyList;
    public int glSkyList2;
    private final ResourceLocation planetToRender;
    private final Minecraft minecraft;
    
    public SkyProviderOverworld() {
        this.starGLCallList = GLAllocation.generateDisplayLists(7);
        this.planetToRender = new ResourceLocation(GalacticraftCore.ASSET_PREFIX, "textures/gui/celestialbodies/earth.png");
        this.minecraft = FMLClientHandler.instance().getClient();
        GL11.glPushMatrix();
        final Random rand = new Random(10842L);
        GL11.glNewList(this.starGLCallList, 4864);
        this.renderStars(rand);
        GL11.glEndList();
        GL11.glNewList(this.starGLCallList + 1, 4864);
        this.renderStars(rand);
        GL11.glEndList();
        GL11.glNewList(this.starGLCallList + 2, 4864);
        this.renderStars(rand);
        GL11.glEndList();
        GL11.glNewList(this.starGLCallList + 3, 4864);
        this.renderStars(rand);
        GL11.glEndList();
        GL11.glNewList(this.starGLCallList + 4, 4864);
        this.renderStars(rand);
        GL11.glEndList();
        GL11.glPopMatrix();
        final Tessellator tessellator = Tessellator.instance;
        GL11.glNewList(this.glSkyList = this.starGLCallList + 5, 4864);
        final byte byte2 = 5;
        final int i = 53;
        float f = 16.0f;
        for (int j = -265; j <= 265; j += 5) {
            for (int l = -265; l <= 265; l += 5) {
                tessellator.startDrawingQuads();
                tessellator.addVertex((double)(j + 0), (double)f, (double)(l + 0));
                tessellator.addVertex((double)(j + 5), (double)f, (double)(l + 0));
                tessellator.addVertex((double)(j + 5), (double)f, (double)(l + 5));
                tessellator.addVertex((double)(j + 0), (double)f, (double)(l + 5));
                tessellator.draw();
            }
        }
        GL11.glEndList();
        GL11.glNewList(this.glSkyList2 = this.starGLCallList + 6, 4864);
        f = -16.0f;
        tessellator.startDrawingQuads();
        for (int k = -265; k <= 265; k += 5) {
            for (int i2 = -265; i2 <= 265; i2 += 5) {
                tessellator.addVertex((double)(k + 5), (double)f, (double)(i2 + 0));
                tessellator.addVertex((double)(k + 0), (double)f, (double)(i2 + 0));
                tessellator.addVertex((double)(k + 0), (double)f, (double)(i2 + 5));
                tessellator.addVertex((double)(k + 5), (double)f, (double)(i2 + 5));
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
        double zoom = 0.0;
        double yaw = 0.0;
        double pitch = 0.0;
        Method m = null;
        if (!SkyProviderOverworld.optifinePresent) {
            try {
                final Class<?> c = mc.entityRenderer.getClass();
                final Field cameraZoom = c.getDeclaredField(VersionUtil.getNameDynamic("cameraZoom"));
                cameraZoom.setAccessible(true);
                zoom = cameraZoom.getDouble(mc.entityRenderer);
                final Field cameraYaw = c.getDeclaredField(VersionUtil.getNameDynamic("cameraYaw"));
                cameraYaw.setAccessible(true);
                yaw = cameraYaw.getDouble(mc.entityRenderer);
                final Field cameraPitch = c.getDeclaredField(VersionUtil.getNameDynamic("cameraPitch"));
                cameraPitch.setAccessible(true);
                pitch = cameraPitch.getDouble(mc.entityRenderer);
                GL11.glMatrixMode(5889);
                GL11.glLoadIdentity();
                if (zoom != 1.0) {
                    GL11.glTranslatef((float)yaw, (float)(-pitch), 0.0f);
                    GL11.glScaled(zoom, zoom, 1.0);
                }
                Project.gluPerspective(mc.gameSettings.fovSetting, mc.displayWidth / (float)mc.displayHeight, 0.05f, 1400.0f);
                GL11.glMatrixMode(5888);
                GL11.glLoadIdentity();
                m = c.getDeclaredMethod(VersionUtil.getNameDynamic("orientCamera"), Float.TYPE);
                m.setAccessible(true);
                m.invoke(mc.entityRenderer, mc.gameSettings.fovSetting);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        float theta = MathHelper.sqrt_float(((float)mc.thePlayer.posY - 200.0f) / 1000.0f);
        final float var21 = Math.max(1.0f - theta * 4.0f, 0.0f);
        GL11.glDisable(3553);
        GL11.glDisable(32826);
        final Vec3 var22 = this.minecraft.theWorld.getSkyColor((Entity)this.minecraft.renderViewEntity, partialTicks);
        float i = (float)var22.xCoord * var21;
        float x = (float)var22.yCoord * var21;
        float var23 = (float)var22.zCoord * var21;
        if (this.minecraft.gameSettings.anaglyph) {
            final float y = (i * 30.0f + x * 59.0f + var23 * 11.0f) / 100.0f;
            final float var24 = (i * 30.0f + x * 70.0f) / 100.0f;
            final float z = (i * 30.0f + var23 * 70.0f) / 100.0f;
            i = y;
            x = var24;
            var23 = z;
        }
        GL11.glColor3f(i, x, var23);
        final Tessellator var25 = Tessellator.instance;
        GL11.glDepthMask(false);
        GL11.glEnable(2912);
        GL11.glColor3f(i, x, var23);
        if (mc.thePlayer.posY < 214.0) {
            GL11.glCallList(this.glSkyList);
        }
        GL11.glDisable(2912);
        GL11.glDisable(3008);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        RenderHelper.disableStandardItemLighting();
        final float[] costh = this.minecraft.theWorld.provider.calcSunriseSunsetColors(this.minecraft.theWorld.getCelestialAngle(partialTicks), partialTicks);
        if (costh != null) {
            final float sunsetModInv = Math.min(1.0f, Math.max(1.0f - theta * 50.0f, 0.0f));
            GL11.glDisable(3553);
            GL11.glShadeModel(7425);
            GL11.glPushMatrix();
            GL11.glRotatef(90.0f, 1.0f, 0.0f, 0.0f);
            GL11.glRotatef((MathHelper.sin(this.minecraft.theWorld.getCelestialAngleRadians(partialTicks)) < 0.0f) ? 180.0f : 0.0f, 0.0f, 0.0f, 1.0f);
            GL11.glRotatef(90.0f, 0.0f, 0.0f, 1.0f);
            float z = costh[0] * sunsetModInv;
            float var26 = costh[1] * sunsetModInv;
            float size = costh[2] * sunsetModInv;
            if (this.minecraft.gameSettings.anaglyph) {
                final float rand1 = (z * 30.0f + var26 * 59.0f + size * 11.0f) / 100.0f;
                final float r = (z * 30.0f + var26 * 70.0f) / 100.0f;
                final float rand2 = (z * 30.0f + size * 70.0f) / 100.0f;
                z = rand1;
                var26 = r;
                size = rand2;
            }
            var25.startDrawing(6);
            var25.setColorRGBA_F(z * sunsetModInv, var26 * sunsetModInv, size * sunsetModInv, costh[3]);
            var25.addVertex(0.0, 100.0, 0.0);
            final byte phi = 16;
            var25.setColorRGBA_F(costh[0] * sunsetModInv, costh[1] * sunsetModInv, costh[2] * sunsetModInv, 0.0f);
            for (int var27 = 0; var27 <= 16; ++var27) {
                final float rand2 = var27 * 3.1415927f * 2.0f / 16.0f;
                final float xx = MathHelper.sin(rand2);
                final float rand3 = MathHelper.cos(rand2);
                var25.addVertex((double)(xx * 120.0f), (double)(rand3 * 120.0f), (double)(-rand3 * 40.0f * costh[3]));
            }
            var25.draw();
            GL11.glPopMatrix();
            GL11.glShadeModel(7424);
        }
        GL11.glEnable(3553);
        GL11.glBlendFunc(770, 1);
        GL11.glPushMatrix();
        float z = 1.0f - this.minecraft.theWorld.getRainStrength(partialTicks);
        float var26 = 0.0f;
        float size = 0.0f;
        final float rand1 = 0.0f;
        GL11.glColor4f(1.0f, 1.0f, 1.0f, z);
        GL11.glTranslatef(var26, size, rand1);
        GL11.glRotatef(-90.0f, 0.0f, 1.0f, 0.0f);
        GL11.glRotatef(this.minecraft.theWorld.getCelestialAngle(partialTicks) * 360.0f, 1.0f, 0.0f, 0.0f);
        final double playerHeight = this.minecraft.thePlayer.posY;
        GL11.glBlendFunc(770, 771);
        GL11.glDisable(3553);
        final Vec3 vec = WorldUtil.getFogColorHook((World)this.minecraft.theWorld);
        final float threshold = Math.max(0.1f, (float)vec.lengthVector() - 0.1f);
        float var28 = ((float)playerHeight - 200.0f) / 1000.0f;
        var28 = MathHelper.sqrt_float(var28);
        final float bright1 = Math.min(0.9f, var28 * 3.0f);
        final float bright2 = Math.min(0.85f, var28 * 2.5f);
        final float bright3 = Math.min(0.8f, var28 * 2.0f);
        final float bright4 = Math.min(0.75f, var28 * 1.5f);
        final float bright5 = Math.min(0.7f, var28 * 0.75f);
        if (bright1 > threshold) {
            GL11.glColor4f(bright1, bright1, bright1, 1.0f);
            GL11.glCallList(this.starGLCallList);
        }
        if (bright2 > threshold) {
            GL11.glColor4f(bright2, bright2, bright2, 1.0f);
            GL11.glCallList(this.starGLCallList + 1);
        }
        if (bright3 > threshold) {
            GL11.glColor4f(bright3, bright3, bright3, 1.0f);
            GL11.glCallList(this.starGLCallList + 2);
        }
        if (bright4 > threshold) {
            GL11.glColor4f(bright4, bright4, bright4, 1.0f);
            GL11.glCallList(this.starGLCallList + 3);
        }
        if (bright5 > threshold) {
            GL11.glColor4f(bright5, bright5, bright5, 1.0f);
            GL11.glCallList(this.starGLCallList + 4);
        }
        GL11.glEnable(3553);
        GL11.glBlendFunc(770, 1);
        float r = 30.0f;
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.minecraft.renderEngine.bindTexture(SkyProviderOverworld.sunTexture);
        var25.startDrawingQuads();
        var25.addVertexWithUV((double)(-r), 100.0, (double)(-r), 0.0, 0.0);
        var25.addVertexWithUV((double)r, 100.0, (double)(-r), 1.0, 0.0);
        var25.addVertexWithUV((double)r, 100.0, (double)r, 1.0, 1.0);
        var25.addVertexWithUV((double)(-r), 100.0, (double)r, 0.0, 1.0);
        var25.draw();
        r = 40.0f;
        this.minecraft.renderEngine.bindTexture(SkyProviderOverworld.moonTexture);
        final float sinphi = (float)this.minecraft.theWorld.getMoonPhase();
        final int cosphi = (int)(sinphi % 4.0f);
        final int var29 = (int)(sinphi / 4.0f % 2.0f);
        final float yy = (cosphi + 0) / 4.0f;
        final float rand4 = (var29 + 0) / 2.0f;
        final float zz = (cosphi + 1) / 4.0f;
        final float rand5 = (var29 + 1) / 2.0f;
        var25.startDrawingQuads();
        var25.addVertexWithUV((double)(-r), -100.0, (double)r, (double)zz, (double)rand5);
        var25.addVertexWithUV((double)r, -100.0, (double)r, (double)yy, (double)rand5);
        var25.addVertexWithUV((double)r, -100.0, (double)(-r), (double)yy, (double)rand4);
        var25.addVertexWithUV((double)(-r), -100.0, (double)(-r), (double)zz, (double)rand4);
        var25.draw();
        GL11.glDisable(3553);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glDisable(3042);
        GL11.glEnable(3008);
        GL11.glEnable(2912);
        GL11.glPopMatrix();
        GL11.glDisable(3553);
        GL11.glColor3f(0.0f, 0.0f, 0.0f);
        final double var30 = playerHeight - 64.0;
        if (var30 > this.minecraft.gameSettings.renderDistanceChunks * 16) {
            theta *= 400.0f;
            final float sinth = Math.max(Math.min(theta / 100.0f - 0.2f, 0.5f), 0.0f);
            GL11.glPushMatrix();
            GL11.glEnable(3553);
            GL11.glDisable(2912);
            float scale = 850.0f * (0.25f - theta / 10000.0f);
            scale = Math.max(scale, 0.2f);
            GL11.glScalef(scale, 1.0f, scale);
            GL11.glTranslatef(0.0f, -(float)mc.thePlayer.posY, 0.0f);
            this.minecraft.renderEngine.bindTexture(this.planetToRender);
            size = 1.0f;
            GL11.glColor4f(sinth, sinth, sinth, 1.0f);
            var25.startDrawingQuads();
            float zoomIn = (1.0f - (float)var30 / 768.0f) / 5.86f;
            if (zoomIn < 0.0f) {
                zoomIn = 0.0f;
            }
            zoomIn = 0.0f;
            final float cornerB = 1.0f - zoomIn;
            var25.addVertexWithUV((double)(-size), 0.0, (double)size, (double)zoomIn, (double)cornerB);
            var25.addVertexWithUV((double)size, 0.0, (double)size, (double)cornerB, (double)cornerB);
            var25.addVertexWithUV((double)size, 0.0, (double)(-size), (double)cornerB, (double)zoomIn);
            var25.addVertexWithUV((double)(-size), 0.0, (double)(-size), (double)zoomIn, (double)zoomIn);
            var25.draw();
            GL11.glDisable(3553);
            GL11.glPopMatrix();
        }
        GL11.glColor3f(0.0f, 0.0f, 0.0f);
        GL11.glEnable(3553);
        GL11.glDepthMask(true);
        if (!SkyProviderOverworld.optifinePresent && m != null) {
            try {
                GL11.glMatrixMode(5889);
                GL11.glLoadIdentity();
                if (zoom != 1.0) {
                    GL11.glTranslatef((float)yaw, (float)(-pitch), 0.0f);
                    GL11.glScaled(zoom, zoom, 1.0);
                }
                Project.gluPerspective(mc.gameSettings.fovSetting, mc.displayWidth / (float)mc.displayHeight, 0.05f, this.minecraft.gameSettings.renderDistanceChunks * 16 * 2.0f);
                GL11.glMatrixMode(5888);
                GL11.glLoadIdentity();
                m.invoke(mc.entityRenderer, mc.gameSettings.fovSetting);
            }
            catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        GL11.glEnable(2903);
    }
    
    private void renderStars(final Random rand) {
        final Tessellator var2 = Tessellator.instance;
        var2.startDrawingQuads();
        for (int i = 0; i < (ConfigManagerCore.moreStars ? 4000 : 1200); ++i) {
            double x = rand.nextFloat() * 2.0f - 1.0f;
            double y = rand.nextFloat() * 2.0f - 1.0f;
            double z = rand.nextFloat() * 2.0f - 1.0f;
            final double size = 0.15f + rand.nextFloat() * 0.1f;
            double r = x * x + y * y + z * z;
            if (r < 1.0 && r > 0.01) {
                r = 1.0 / Math.sqrt(r);
                x *= r;
                y *= r;
                z *= r;
                final double xx = x * (ConfigManagerCore.moreStars ? (rand.nextDouble() * 100.0 + 150.0) : 100.0);
                final double zz = z * (ConfigManagerCore.moreStars ? (rand.nextDouble() * 100.0 + 150.0) : 100.0);
                if (Math.abs(xx) >= 29.0 || Math.abs(zz) >= 29.0) {
                    final double yy = y * (ConfigManagerCore.moreStars ? (rand.nextDouble() * 100.0 + 150.0) : 100.0);
                    final double theta = Math.atan2(x, z);
                    final double sinth = Math.sin(theta);
                    final double costh = Math.cos(theta);
                    final double phi = Math.atan2(Math.sqrt(x * x + z * z), y);
                    final double sinphi = Math.sin(phi);
                    final double cosphi = Math.cos(phi);
                    final double rho = rand.nextDouble() * 3.141592653589793 * 2.0;
                    final double sinrho = Math.sin(rho);
                    final double cosrho = Math.cos(rho);
                    for (int j = 0; j < 4; ++j) {
                        final double a = 0.0;
                        final double b = ((j & 0x2) - 1) * size;
                        final double c = ((j + 1 & 0x2) - 1) * size;
                        final double d = b * cosrho - c * sinrho;
                        final double e = c * cosrho + b * sinrho;
                        final double dy = d * sinphi + 0.0 * cosphi;
                        final double ff = 0.0 * sinphi - d * cosphi;
                        final double dx = ff * sinth - e * costh;
                        final double dz = e * sinth + ff * costh;
                        var2.addVertex(xx + dx, yy + dy, zz + dz);
                    }
                }
            }
        }
        var2.draw();
    }
    
    static {
        moonTexture = new ResourceLocation("textures/environment/moon_phases.png");
        sunTexture = new ResourceLocation("textures/environment/sun.png");
        SkyProviderOverworld.optifinePresent = false;
        try {
            SkyProviderOverworld.optifinePresent = (Launch.classLoader.getClassBytes("CustomColorizer") != null);
        }
        catch (Exception ex) {}
    }
}
