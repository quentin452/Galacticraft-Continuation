package micdoodle8.mods.galacticraft.planets.asteroids.client.render.entity;

import net.minecraft.client.renderer.entity.*;
import micdoodle8.mods.galacticraft.core.perlin.*;
import micdoodle8.mods.galacticraft.core.perlin.generator.*;
import net.minecraft.entity.*;
import micdoodle8.mods.galacticraft.planets.asteroids.entities.*;
import net.minecraft.util.*;
import org.lwjgl.opengl.*;
import cpw.mods.fml.client.*;
import net.minecraft.client.renderer.*;
import micdoodle8.mods.galacticraft.api.vector.*;
import java.util.*;
import net.minecraftforge.client.model.*;

public class RenderAstroMiner extends Render
{
    private static final float LSIZE = 0.12f;
    private static final float RETRACTIONSPEED = 0.02f;
    private RenderBlocks blockRenderer;
    private float spin;
    private float lastPartTime;
    public static ResourceLocation scanTexture;
    public static ResourceLocation modelTexture;
    public static ResourceLocation modelTextureFX;
    public static ResourceLocation modelTextureOff;
    public static IModelCustom modelObj;
    public static IModelCustom modellaser1;
    public static IModelCustom modellaser2;
    public static IModelCustom modellaser3;
    public static IModelCustom modellasergl;
    public static IModelCustom modellasergr;
    private final NoiseModule wobbleX;
    private final NoiseModule wobbleY;
    private final NoiseModule wobbleZ;
    private final NoiseModule wobbleXX;
    private final NoiseModule wobbleYY;
    private final NoiseModule wobbleZZ;
    
    public RenderAstroMiner() {
        this.blockRenderer = new RenderBlocks();
        this.shadowSize = 2.0f;
        final Random rand = new Random();
        this.wobbleX = (NoiseModule)new Gradient(rand.nextLong(), 2, 1.0f);
        this.wobbleX.amplitude = 0.5f;
        this.wobbleX.frequencyX = 0.025f;
        this.wobbleY = (NoiseModule)new Gradient(rand.nextLong(), 2, 1.0f);
        this.wobbleY.amplitude = 0.6f;
        this.wobbleY.frequencyX = 0.025f;
        this.wobbleZ = (NoiseModule)new Gradient(rand.nextLong(), 2, 1.0f);
        this.wobbleZ.amplitude = 0.1f;
        this.wobbleZ.frequencyX = 0.025f;
        this.wobbleXX = (NoiseModule)new Gradient(rand.nextLong(), 2, 1.0f);
        this.wobbleXX.amplitude = 0.1f;
        this.wobbleXX.frequencyX = 0.8f;
        this.wobbleYY = (NoiseModule)new Gradient(rand.nextLong(), 2, 1.0f);
        this.wobbleYY.amplitude = 0.15f;
        this.wobbleYY.frequencyX = 0.8f;
        this.wobbleZZ = (NoiseModule)new Gradient(rand.nextLong(), 2, 1.0f);
        this.wobbleZZ.amplitude = 0.04f;
        this.wobbleZZ.frequencyX = 0.8f;
    }
    
    public void doRender(final Entity entity, final double x, final double y, final double z, final float f, final float partialTickTime) {
        final EntityAstroMiner astroMiner = (EntityAstroMiner)entity;
        final int ais = ((EntityAstroMiner)entity).AIstate;
        final boolean active = ais > 1;
        final float time = astroMiner.ticksExisted + partialTickTime;
        final float sinOfTheTime = (MathHelper.sin(time / 4.0f) + 1.0f) / 4.0f + 0.5f;
        final float wx = active ? (this.wobbleX.getNoise(time) + this.wobbleXX.getNoise(time)) : 0.0f;
        final float wy = active ? (this.wobbleY.getNoise(time) + this.wobbleYY.getNoise(time)) : 0.0f;
        final float wz = active ? (this.wobbleZ.getNoise(time) + this.wobbleZZ.getNoise(time)) : 0.0f;
        float partTime = partialTickTime - this.lastPartTime;
        this.lastPartTime = partialTickTime;
        while (partTime < 0.0f) {
            ++partTime;
        }
        GL11.glDisable(32826);
        GL11.glPushMatrix();
        final float rotPitch = entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTickTime;
        final float rotYaw = entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partialTickTime;
        GL11.glTranslatef((float)x, (float)y + 1.4f, (float)z);
        float partBlock = 0.0f;
        switch (astroMiner.facing) {
            case 0: {
                partBlock = (float)(astroMiner.posY % 1.0);
                break;
            }
            case 1: {
                partBlock = 1.0f - (float)(astroMiner.posY % 1.0);
                break;
            }
            case 2: {
                partBlock = (float)(astroMiner.posZ % 1.0);
                break;
            }
            case 3: {
                partBlock = 1.0f - (float)(astroMiner.posZ % 1.0);
                break;
            }
            case 4: {
                partBlock = (float)(astroMiner.posX % 1.0);
                break;
            }
            case 5: {
                partBlock = 1.0f - (float)(astroMiner.posX % 1.0);
                break;
            }
            default: {
                partBlock = 0.0f;
                break;
            }
        }
        partBlock /= 0.06f;
        GL11.glRotatef(rotYaw + 180.0f, 0.0f, 1.0f, 0.0f);
        if (rotPitch != 0.0f) {
            GL11.glTranslatef(-0.65f, -0.65f, 0.0f);
            GL11.glRotatef(rotPitch / 4.0f, 1.0f, 0.0f, 0.0f);
            GL11.glTranslatef(0.65f, 0.65f, 0.0f);
        }
        GL11.glTranslatef(0.0f, -0.42f, 0.28f);
        GL11.glScalef(0.0495f, 0.0495f, 0.0495f);
        GL11.glTranslatef(wx, wy, wz);
        if (active) {
            FMLClientHandler.instance().getClient().renderEngine.bindTexture(RenderAstroMiner.modelTexture);
            RenderAstroMiner.modelObj.renderAllExcept(new String[] { "Hoverpad_Front_Left_Top", "Hoverpad_Front_Right_Top", "Hoverpad_Front_Left_Bottom", "Hoverpad_Front_Right_Bottom", "Hoverpad_Rear_Right", "Hoverpad_Rear_Left", "Hoverpad_Heavy_Right", "Hoverpad_Heavy_Left", "Hoverpad_Heavy_Rear", "Hoverpad_Front_Left_Top_Glow", "Hoverpad_Front_Right_Top_Glow", "Hoverpad_Front_Left_Bottom_Glow", "Hoverpad_Front_Right_Bottom_Glow", "Hoverpad_Rear_Right_Glow", "Hoverpad_Rear_Left_Glow", "Hoverpad_Heavy___Glow002", "Hoverpad_Heavy___Glow001", "Hoverpad_Heavy___Glow003" });
            this.renderLaserModel(astroMiner.retraction);
            final float lightMapSaveX = OpenGlHelper.lastBrightnessX;
            final float lightMapSaveY = OpenGlHelper.lastBrightnessY;
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0f, 240.0f);
            GL11.glDisable(2896);
            GL11.glColor4f(sinOfTheTime, sinOfTheTime, sinOfTheTime, 1.0f);
            RenderAstroMiner.modelObj.renderOnly(new String[] { "Hoverpad_Front_Left_Top", "Hoverpad_Front_Right_Top", "Hoverpad_Front_Left_Bottom", "Hoverpad_Front_Right_Bottom", "Hoverpad_Rear_Right", "Hoverpad_Rear_Left", "Hoverpad_Heavy_Right", "Hoverpad_Heavy_Left", "Hoverpad_Heavy_Rear" });
            FMLClientHandler.instance().getClient().renderEngine.bindTexture(RenderAstroMiner.modelTextureFX);
            GL11.glDisable(2884);
            GL11.glDisable(3008);
            GL11.glDepthMask(false);
            GL11.glBlendFunc(770, 1);
            GL11.glEnable(3042);
            GL11.glTexParameteri(3553, 10241, 9729);
            GL11.glTexParameteri(3553, 10240, 9729);
            GL11.glColor4f(sinOfTheTime, sinOfTheTime, sinOfTheTime, 0.6f);
            RenderAstroMiner.modelObj.renderOnly(new String[] { "Hoverpad_Front_Left_Top_Glow", "Hoverpad_Front_Right_Top_Glow", "Hoverpad_Front_Left_Bottom_Glow", "Hoverpad_Front_Right_Bottom_Glow", "Hoverpad_Rear_Right_Glow", "Hoverpad_Rear_Left_Glow", "Hoverpad_Heavy___Glow002", "Hoverpad_Heavy___Glow001", "Hoverpad_Heavy___Glow003" });
            if (ais < 5) {
                FMLClientHandler.instance().getClient().renderEngine.bindTexture(RenderAstroMiner.scanTexture);
                final Tessellator tess = Tessellator.instance;
                GL11.glColor4f(0.0f, 0.6f, 1.0f, 0.2f);
                tess.startDrawingQuads();
                tess.addVertexWithUV(15.600000381469727, -0.6000000238418579, -20.0, 0.0, 0.0);
                tess.addVertexWithUV(37.79999923706055, 31.399999618530273, (double)(-45.0f - partBlock), 1.0, 0.0);
                tess.addVertexWithUV(37.79999923706055, -32.599998474121094, (double)(-45.0f - partBlock), 1.0, 1.0);
                tess.addVertexWithUV(15.600000381469727, -0.699999988079071, -20.0, 0.0, 1.0);
                tess.draw();
                tess.startDrawingQuads();
                tess.addVertexWithUV(-15.600000381469727, -0.6000000238418579, -20.0, 0.0, 0.0);
                tess.addVertexWithUV(-37.79999923706055, 31.399999618530273, (double)(-45.0f - partBlock), 1.0, 0.0);
                tess.addVertexWithUV(-37.79999923706055, -32.599998474121094, (double)(-45.0f - partBlock), 1.0, 1.0);
                tess.addVertexWithUV(-15.600000381469727, -0.699999988079071, -20.0, 0.0, 1.0);
                tess.draw();
                int removeCount = 0;
                int afterglowCount = 0;
                GL11.glPopMatrix();
                GL11.glPushMatrix();
                GL11.glTranslatef((float)(x - astroMiner.posX), (float)(y - astroMiner.posY), (float)(z - astroMiner.posZ));
                for (final Integer blockTime : new ArrayList<Integer>(astroMiner.laserTimes)) {
                    if (blockTime < astroMiner.ticksExisted - 19) {
                        ++removeCount;
                    }
                    else {
                        if (blockTime >= astroMiner.ticksExisted - 3) {
                            continue;
                        }
                        ++afterglowCount;
                    }
                }
                if (removeCount > 0) {
                    astroMiner.removeLaserBlocks(removeCount);
                }
                int count = 0;
                for (final BlockVec3 blockLaser : new ArrayList<BlockVec3>(astroMiner.laserBlocks)) {
                    if (count < afterglowCount) {
                        int fade = astroMiner.ticksExisted - astroMiner.laserTimes.get(count) - 8;
                        if (fade < 0) {
                            fade = 0;
                        }
                        this.doAfterGlow(blockLaser, fade);
                    }
                    else {
                        this.doLaser(astroMiner, blockLaser);
                    }
                    ++count;
                }
                if (astroMiner.retraction > 0.0f) {
                    final EntityAstroMiner entityAstroMiner = astroMiner;
                    entityAstroMiner.retraction -= 0.02f * partTime;
                    if (astroMiner.retraction < 0.0f) {
                        astroMiner.retraction = 0.0f;
                    }
                }
                GL11.glPopMatrix();
            }
            else {
                if (astroMiner.retraction < 1.0f) {
                    final EntityAstroMiner entityAstroMiner2 = astroMiner;
                    entityAstroMiner2.retraction += 0.02f * partTime;
                    if (astroMiner.retraction > 1.0f) {
                        astroMiner.retraction = 1.0f;
                    }
                }
                GL11.glPopMatrix();
            }
            GL11.glTexParameteri(3553, 10241, 9728);
            GL11.glTexParameteri(3553, 10240, 9728);
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            GL11.glDisable(3042);
            GL11.glBlendFunc(770, 771);
            GL11.glEnable(2884);
            GL11.glEnable(3008);
            GL11.glEnable(2896);
            GL11.glDepthMask(true);
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lightMapSaveX, lightMapSaveY);
        }
        else {
            this.bindEntityTexture((Entity)astroMiner);
            RenderAstroMiner.modelObj.renderAllExcept(new String[] { "Hoverpad_Front_Left_Top_Glow", "Hoverpad_Front_Right_Top_Glow", "Hoverpad_Front_Left_Bottom_Glow", "Hoverpad_Front_Right_Bottom_Glow", "Hoverpad_Rear_Right_Glow", "Hoverpad_Rear_Left_Glow", "Hoverpad_Heavy___Glow002", "Hoverpad_Heavy___Glow001", "Hoverpad_Heavy___Glow003" });
            this.renderLaserModel(astroMiner.retraction);
            if (astroMiner.retraction < 1.0f) {
                final EntityAstroMiner entityAstroMiner3 = astroMiner;
                entityAstroMiner3.retraction += 0.02f * partTime;
                if (astroMiner.retraction > 1.0f) {
                    astroMiner.retraction = 1.0f;
                }
            }
            GL11.glPopMatrix();
        }
    }
    
    private void doAfterGlow(final BlockVec3 blockLaser, final int level) {
        GL11.glPushMatrix();
        GL11.glTranslatef((float)blockLaser.x, (float)blockLaser.y, (float)blockLaser.z);
        final Tessellator tess = Tessellator.instance;
        GL11.glColor4f(1.0f, 0.7f, 0.7f, 0.016667f * (12 - level));
        final float cA = -0.01f;
        final float cB = 1.01f;
        tess.startDrawingQuads();
        tess.addVertexWithUV((double)cA, (double)cB, (double)cA, 0.0, 1.0);
        tess.addVertexWithUV((double)cB, (double)cB, (double)cA, 1.0, 1.0);
        tess.addVertexWithUV((double)cB, (double)cB, (double)cB, 1.0, 0.0);
        tess.addVertexWithUV((double)cA, (double)cB, (double)cB, 0.0, 0.0);
        tess.draw();
        tess.startDrawingQuads();
        tess.addVertexWithUV((double)cA, (double)cA, (double)cA, 0.0, 0.0);
        tess.addVertexWithUV((double)cA, (double)cA, (double)cB, 0.0, 1.0);
        tess.addVertexWithUV((double)cB, (double)cA, (double)cB, 1.0, 1.0);
        tess.addVertexWithUV((double)cB, (double)cA, (double)cA, 1.0, 0.0);
        tess.draw();
        tess.startDrawingQuads();
        tess.addVertexWithUV((double)cA, (double)cA, (double)cA, 1.0, 0.0);
        tess.addVertexWithUV((double)cA, (double)cB, (double)cA, 0.0, 0.0);
        tess.addVertexWithUV((double)cA, (double)cB, (double)cB, 0.0, 1.0);
        tess.addVertexWithUV((double)cA, (double)cA, (double)cB, 1.0, 1.0);
        tess.draw();
        tess.startDrawingQuads();
        tess.addVertexWithUV((double)cB, (double)cA, (double)cA, 1.0, 1.0);
        tess.addVertexWithUV((double)cB, (double)cA, (double)cB, 1.0, 0.0);
        tess.addVertexWithUV((double)cB, (double)cB, (double)cB, 0.0, 0.0);
        tess.addVertexWithUV((double)cB, (double)cB, (double)cA, 0.0, 1.0);
        tess.draw();
        tess.startDrawingQuads();
        tess.addVertexWithUV((double)cA, (double)cA, (double)cA, 1.0, 0.0);
        tess.addVertexWithUV(1.0, (double)cA, (double)cA, 0.0, 0.0);
        tess.addVertexWithUV(1.0, 1.0, (double)cA, 0.0, 1.0);
        tess.addVertexWithUV((double)cA, 1.0, (double)cA, 1.0, 1.0);
        tess.draw();
        tess.startDrawingQuads();
        tess.addVertexWithUV(1.0, (double)cA, 1.0, 1.0, 1.0);
        tess.addVertexWithUV((double)cA, (double)cA, 1.0, 1.0, 0.0);
        tess.addVertexWithUV((double)cA, 1.0, 1.0, 0.0, 0.0);
        tess.addVertexWithUV(1.0, 1.0, 1.0, 0.0, 1.0);
        tess.draw();
        GL11.glPopMatrix();
    }
    
    private void doLaser(final EntityAstroMiner entity, final BlockVec3 blockLaser) {
        GL11.glPushMatrix();
        GL11.glTranslatef((float)blockLaser.x, (float)blockLaser.y, (float)blockLaser.z);
        final Tessellator tess = Tessellator.instance;
        GL11.glColor4f(1.0f, 0.7f, 0.7f, 0.2f);
        final float cA = -0.01f;
        final float cB = 1.01f;
        tess.startDrawingQuads();
        tess.addVertexWithUV((double)cA, (double)cB, (double)cA, 0.0, 1.0);
        tess.addVertexWithUV((double)cB, (double)cB, (double)cA, 1.0, 1.0);
        tess.addVertexWithUV((double)cB, (double)cB, (double)cB, 1.0, 0.0);
        tess.addVertexWithUV((double)cA, (double)cB, (double)cB, 0.0, 0.0);
        tess.draw();
        tess.startDrawingQuads();
        tess.addVertexWithUV((double)cA, (double)cA, (double)cA, 0.0, 0.0);
        tess.addVertexWithUV((double)cA, (double)cA, (double)cB, 0.0, 1.0);
        tess.addVertexWithUV((double)cB, (double)cA, (double)cB, 1.0, 1.0);
        tess.addVertexWithUV((double)cB, (double)cA, (double)cA, 1.0, 0.0);
        tess.draw();
        tess.startDrawingQuads();
        tess.addVertexWithUV((double)cA, (double)cA, (double)cA, 1.0, 0.0);
        tess.addVertexWithUV((double)cA, (double)cB, (double)cA, 0.0, 0.0);
        tess.addVertexWithUV((double)cA, (double)cB, (double)cB, 0.0, 1.0);
        tess.addVertexWithUV((double)cA, (double)cA, (double)cB, 1.0, 1.0);
        tess.draw();
        tess.startDrawingQuads();
        tess.addVertexWithUV((double)cB, (double)cA, (double)cA, 1.0, 1.0);
        tess.addVertexWithUV((double)cB, (double)cA, (double)cB, 1.0, 0.0);
        tess.addVertexWithUV((double)cB, (double)cB, (double)cB, 0.0, 0.0);
        tess.addVertexWithUV((double)cB, (double)cB, (double)cA, 0.0, 1.0);
        tess.draw();
        tess.startDrawingQuads();
        tess.addVertexWithUV((double)cA, (double)cA, (double)cA, 1.0, 0.0);
        tess.addVertexWithUV(1.0, (double)cA, (double)cA, 0.0, 0.0);
        tess.addVertexWithUV(1.0, 1.0, (double)cA, 0.0, 1.0);
        tess.addVertexWithUV((double)cA, 1.0, (double)cA, 1.0, 1.0);
        tess.draw();
        tess.startDrawingQuads();
        tess.addVertexWithUV(1.0, (double)cA, 1.0, 1.0, 1.0);
        tess.addVertexWithUV((double)cA, (double)cA, 1.0, 1.0, 0.0);
        tess.addVertexWithUV((double)cA, 1.0, 1.0, 0.0, 0.0);
        tess.addVertexWithUV(1.0, 1.0, 1.0, 0.0, 1.0);
        tess.draw();
        GL11.glColor4f(1.0f, 0.79f, 0.79f, 0.17f);
        final float bb = 1.7f;
        final float cc = 0.4f;
        final float radiansYaw = entity.rotationYaw * 0.017453292f;
        final float radiansPitch = entity.rotationPitch * 0.017453292f / 4.0f;
        float mainLaserX = bb * MathHelper.sin(radiansYaw) * MathHelper.cos(radiansPitch);
        float mainLaserY = cc + bb * MathHelper.sin(radiansPitch);
        float mainLaserZ = bb * MathHelper.cos(radiansYaw) * MathHelper.cos(radiansPitch);
        mainLaserX += (float)(entity.posX - blockLaser.x);
        mainLaserY += (float)(entity.posY - blockLaser.y);
        mainLaserZ += (float)(entity.posZ - blockLaser.z);
        final float xD = mainLaserX - 0.5f;
        final float yD = mainLaserY - 0.5f;
        final float zD = mainLaserZ - 0.5f;
        final float xDa = Math.abs(xD);
        final float yDa = Math.abs(yD);
        final float zDa = Math.abs(zD);
        if (entity.facing > 3) {
            final float xx = (xD < 0.0f) ? cA : cB;
            this.drawLaserX(mainLaserX, mainLaserY, mainLaserZ, xx, 0.5f, 0.5f);
        }
        else if (entity.facing < 2) {
            final float yy = (yD < 0.0f) ? cA : cB;
            this.drawLaserY(mainLaserX, mainLaserY, mainLaserZ, 0.5f, yy, 0.5f);
        }
        else {
            final float zz = (zD < 0.0f) ? cA : cB;
            this.drawLaserZ(mainLaserX, mainLaserY, mainLaserZ, 0.5f, 0.5f, zz);
        }
        GL11.glPopMatrix();
    }
    
    private void drawLaserX(final float x1, final float y1, final float z1, final float x2, final float y2, final float z2) {
        final Tessellator tess = Tessellator.instance;
        tess.startDrawingQuads();
        tess.addVertex((double)x1, (double)(y1 - 0.01f), (double)(z1 - 0.01f));
        tess.addVertex((double)x2, (double)(y2 - 0.12f), (double)(z2 - 0.12f));
        tess.addVertex((double)x2, (double)(y2 + 0.12f), (double)(z2 - 0.12f));
        tess.addVertex((double)x1, (double)(y1 + 0.01f), (double)(z1 - 0.01f));
        tess.draw();
        tess.startDrawingQuads();
        tess.addVertex((double)x1, (double)(y1 - 0.01f), (double)(z1 + 0.01f));
        tess.addVertex((double)x2, (double)(y2 - 0.12f), (double)(z2 + 0.12f));
        tess.addVertex((double)x2, (double)(y2 + 0.12f), (double)(z2 + 0.12f));
        tess.addVertex((double)x1, (double)(y1 + 0.01f), (double)(z1 + 0.01f));
        tess.draw();
        tess.startDrawingQuads();
        tess.addVertex((double)x1, (double)(y1 - 0.01f), (double)(z1 - 0.01f));
        tess.addVertex((double)x2, (double)(y2 - 0.12f), (double)(z2 - 0.12f));
        tess.addVertex((double)x2, (double)(y2 - 0.12f), (double)(z2 + 0.12f));
        tess.addVertex((double)x1, (double)(y1 - 0.01f), (double)(z1 + 0.01f));
        tess.draw();
        tess.startDrawingQuads();
        tess.addVertex((double)x1, (double)(y1 + 0.01f), (double)(z1 + 0.01f));
        tess.addVertex((double)x2, (double)(y2 + 0.12f), (double)(z2 + 0.12f));
        tess.addVertex((double)x2, (double)(y2 + 0.12f), (double)(z2 - 0.12f));
        tess.addVertex((double)x1, (double)(y1 + 0.01f), (double)(z1 - 0.01f));
        tess.draw();
    }
    
    private void drawLaserY(final float x1, final float y1, final float z1, final float x2, final float y2, final float z2) {
        final Tessellator tess = Tessellator.instance;
        tess.startDrawingQuads();
        tess.addVertex((double)(x1 - 0.01f), (double)y1, (double)(z1 - 0.01f));
        tess.addVertex((double)(x2 - 0.12f), (double)y2, (double)(z2 - 0.12f));
        tess.addVertex((double)(x2 + 0.12f), (double)y2, (double)(z2 - 0.12f));
        tess.addVertex((double)(x1 + 0.01f), (double)y1, (double)(z1 - 0.01f));
        tess.draw();
        tess.startDrawingQuads();
        tess.addVertex((double)(x1 - 0.01f), (double)y1, (double)(z1 + 0.01f));
        tess.addVertex((double)(x2 - 0.12f), (double)y2, (double)(z2 + 0.12f));
        tess.addVertex((double)(x2 + 0.12f), (double)y2, (double)(z2 + 0.12f));
        tess.addVertex((double)(x1 + 0.01f), (double)y1, (double)(z1 + 0.01f));
        tess.draw();
        tess.startDrawingQuads();
        tess.addVertex((double)(x1 - 0.01f), (double)y1, (double)(z1 - 0.01f));
        tess.addVertex((double)(x2 - 0.12f), (double)y2, (double)(z2 - 0.12f));
        tess.addVertex((double)(x2 - 0.12f), (double)y2, (double)(z2 + 0.12f));
        tess.addVertex((double)(x1 - 0.01f), (double)y1, (double)(z1 + 0.01f));
        tess.draw();
        tess.startDrawingQuads();
        tess.addVertex((double)(x1 + 0.01f), (double)y1, (double)(z1 + 0.01f));
        tess.addVertex((double)(x2 + 0.12f), (double)y2, (double)(z2 + 0.12f));
        tess.addVertex((double)(x2 + 0.12f), (double)y2, (double)(z2 - 0.12f));
        tess.addVertex((double)(x1 + 0.01f), (double)y1, (double)(z1 - 0.01f));
        tess.draw();
    }
    
    private void drawLaserZ(final float x1, final float y1, final float z1, final float x2, final float y2, final float z2) {
        final Tessellator tess = Tessellator.instance;
        tess.startDrawingQuads();
        tess.addVertex((double)(x1 - 0.01f), (double)(y1 - 0.01f), (double)z1);
        tess.addVertex((double)(x2 - 0.12f), (double)(y2 - 0.12f), (double)z2);
        tess.addVertex((double)(x2 - 0.12f), (double)(y2 + 0.12f), (double)z2);
        tess.addVertex((double)(x1 - 0.01f), (double)(y1 + 0.01f), (double)z1);
        tess.draw();
        tess.startDrawingQuads();
        tess.addVertex((double)(x1 + 0.01f), (double)(y1 - 0.01f), (double)z1);
        tess.addVertex((double)(x2 + 0.12f), (double)(y2 - 0.12f), (double)z2);
        tess.addVertex((double)(x2 + 0.12f), (double)(y2 + 0.12f), (double)z2);
        tess.addVertex((double)(x1 + 0.01f), (double)(y1 + 0.01f), (double)z1);
        tess.draw();
        tess.startDrawingQuads();
        tess.addVertex((double)(x1 - 0.01f), (double)(y1 - 0.01f), (double)z1);
        tess.addVertex((double)(x2 - 0.12f), (double)(y2 - 0.12f), (double)z2);
        tess.addVertex((double)(x2 + 0.12f), (double)(y2 - 0.12f), (double)z2);
        tess.addVertex((double)(x1 + 0.01f), (double)(y1 - 0.01f), (double)z1);
        tess.draw();
        tess.startDrawingQuads();
        tess.addVertex((double)x1, (double)(y1 + 0.01f), (double)(z1 + 0.01f));
        tess.addVertex((double)x2, (double)(y2 + 0.12f), (double)(z2 + 0.12f));
        tess.addVertex((double)x2, (double)(y2 + 0.12f), (double)(z2 - 0.12f));
        tess.addVertex((double)x1, (double)(y1 + 0.01f), (double)(z1 - 0.01f));
        tess.draw();
    }
    
    private void renderLaserModel(final float retraction) {
        float laserretraction = retraction / 0.8f;
        if (laserretraction > 1.0f) {
            laserretraction = 1.0f;
        }
        float guardmovement = (retraction - 0.6f) / 0.4f * 1.875f;
        if (guardmovement < 0.0f) {
            guardmovement = 0.0f;
        }
        GL11.glPushMatrix();
        float yadjust;
        float zadjust = yadjust = laserretraction * 5.0f;
        if (yadjust > 0.938f) {
            yadjust = 0.938f;
            zadjust = (zadjust - yadjust) * 2.5f + yadjust;
        }
        GL11.glTranslatef(0.0f, yadjust, zadjust);
        RenderAstroMiner.modellaser1.renderAll();
        RenderAstroMiner.modellaser2.renderAll();
        if (yadjust == 0.938f) {
            GL11.glTranslatef(0.0f, 0.0f, -zadjust + 0.938f);
        }
        RenderAstroMiner.modellaser3.renderAll();
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GL11.glTranslatef(guardmovement, 0.0f, 0.0f);
        RenderAstroMiner.modellasergl.renderAll();
        GL11.glTranslatef(-2.0f * guardmovement, 0.0f, 0.0f);
        RenderAstroMiner.modellasergr.renderAll();
        GL11.glPopMatrix();
    }
    
    protected ResourceLocation getEntityTexture(final Entity entity) {
        return RenderAstroMiner.modelTextureOff;
    }
    
    static {
        RenderAstroMiner.modelObj = AdvancedModelLoader.loadModel(new ResourceLocation("galacticraftasteroids", "models/astroMiner.obj"));
        RenderAstroMiner.modellaser1 = AdvancedModelLoader.loadModel(new ResourceLocation("galacticraftasteroids", "models/astroMinerLaserFront.obj"));
        RenderAstroMiner.modellaser2 = AdvancedModelLoader.loadModel(new ResourceLocation("galacticraftasteroids", "models/astroMinerLaserBottom.obj"));
        RenderAstroMiner.modellaser3 = AdvancedModelLoader.loadModel(new ResourceLocation("galacticraftasteroids", "models/astroMinerLaserCenter.obj"));
        RenderAstroMiner.modellasergl = AdvancedModelLoader.loadModel(new ResourceLocation("galacticraftasteroids", "models/astroMinerLeftGuard.obj"));
        RenderAstroMiner.modellasergr = AdvancedModelLoader.loadModel(new ResourceLocation("galacticraftasteroids", "models/astroMinerRightGuard.obj"));
        RenderAstroMiner.modelTexture = new ResourceLocation("galacticraftasteroids", "textures/model/astroMiner.png");
        RenderAstroMiner.modelTextureFX = new ResourceLocation("galacticraftasteroids", "textures/model/astroMinerFX.png");
        RenderAstroMiner.modelTextureOff = new ResourceLocation("galacticraftasteroids", "textures/model/astroMiner_off.png");
        RenderAstroMiner.scanTexture = new ResourceLocation("galacticraftasteroids", "textures/misc/gradient.png");
    }
}
