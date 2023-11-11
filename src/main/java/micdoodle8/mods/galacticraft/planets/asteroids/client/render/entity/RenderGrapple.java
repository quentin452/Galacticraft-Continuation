package micdoodle8.mods.galacticraft.planets.asteroids.client.render.entity;

import net.minecraft.client.renderer.entity.*;
import micdoodle8.mods.galacticraft.planets.asteroids.entities.*;
import org.lwjgl.opengl.*;
import net.minecraft.client.renderer.*;
import net.minecraft.entity.*;
import micdoodle8.mods.galacticraft.planets.asteroids.client.render.item.*;
import net.minecraft.entity.player.*;
import net.minecraft.util.*;

public class RenderGrapple extends Render
{
    public void doRender(final EntityGrapple grapple, final double x, final double y, final double z, final float par8, final float partialTicks) {
        GL11.glDisable(32826);
        GL11.glPushMatrix();
        final Vec3 vec3 = Vec3.createVectorHelper(0.0, -0.2, 0.0);
        final EntityPlayer shootingEntity = grapple.getShootingEntity();
        if (shootingEntity != null && grapple.getPullingEntity()) {
            final double d3 = shootingEntity.prevPosX + (shootingEntity.posX - shootingEntity.prevPosX) * partialTicks + vec3.xCoord;
            final double d4 = shootingEntity.prevPosY + (shootingEntity.posY - shootingEntity.prevPosY) * partialTicks + vec3.yCoord;
            final double d5 = shootingEntity.prevPosZ + (shootingEntity.posZ - shootingEntity.prevPosZ) * partialTicks + vec3.zCoord;
            final Tessellator tessellator = Tessellator.instance;
            GL11.glDisable(3553);
            GL11.glDisable(2896);
            tessellator.startDrawing(3);
            tessellator.setColorOpaque_F(0.79607844f, 0.79607844f, 0.7529412f);
            final byte b2 = 16;
            final double d6 = grapple.prevPosX + (grapple.posX - grapple.prevPosX) * partialTicks;
            final double d7 = grapple.prevPosY + (grapple.posY - grapple.prevPosY) * partialTicks + 0.25;
            final double d8 = grapple.prevPosZ + (grapple.posZ - grapple.prevPosZ) * partialTicks;
            final double d9 = (float)(d3 - d6);
            final double d10 = (float)(d4 - d7);
            final double d11 = (float)(d5 - d8);
            tessellator.addTranslation(0.0f, -0.2f, 0.0f);
            for (int i = 0; i <= b2; ++i) {
                final float f12 = i / (float)b2;
                tessellator.addVertex(x + d9 * f12, y + d10 * (f12 * f12 + f12) * 0.5 + 0.15, z + d11 * f12);
            }
            tessellator.draw();
            tessellator.setTranslation(0.0, 0.0, 0.0);
            GL11.glEnable(2896);
            GL11.glEnable(3553);
        }
        GL11.glTranslatef((float)x, (float)y, (float)z);
        GL11.glRotatef(grapple.prevRotationYaw + (grapple.rotationYaw - grapple.prevRotationYaw) * partialTicks - 90.0f, 0.0f, 1.0f, 0.0f);
        GL11.glRotatef(grapple.prevRotationPitch + (grapple.rotationPitch - grapple.prevRotationPitch) * partialTicks - 180.0f, 0.0f, 0.0f, 1.0f);
        GL11.glRotatef(grapple.prevRotationRoll + (grapple.rotationRoll - grapple.prevRotationRoll) * partialTicks, 1.0f, 0.0f, 0.0f);
        this.bindEntityTexture((Entity)grapple);
        ItemRendererGrappleHook.modelGrapple.renderAll();
        GL11.glPopMatrix();
    }
    
    protected ResourceLocation getEntityTexture(final EntityGrapple grapple) {
        return ItemRendererGrappleHook.grappleTexture;
    }
    
    protected ResourceLocation getEntityTexture(final Entity entity) {
        return this.getEntityTexture((EntityGrapple)entity);
    }
    
    public void doRender(final Entity entity, final double x, final double y, final double z, final float par8, final float partialTicks) {
        this.doRender((EntityGrapple)entity, x, y, z, par8, partialTicks);
    }
}
