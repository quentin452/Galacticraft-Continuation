package micdoodle8.mods.galacticraft.planets.asteroids.client.render.entity;

import net.minecraft.client.renderer.entity.*;
import cpw.mods.fml.relauncher.*;
import net.minecraftforge.client.model.*;
import net.minecraft.entity.*;
import micdoodle8.mods.galacticraft.api.prefab.entity.*;
import org.lwjgl.opengl.*;
import net.minecraft.util.*;
import cpw.mods.fml.client.*;
import micdoodle8.mods.galacticraft.core.util.*;
import micdoodle8.mods.galacticraft.api.vector.*;

@SideOnly(Side.CLIENT)
public class RenderTier3Rocket extends Render
{
    private ResourceLocation rocketTexture;
    protected IModelCustom rocketModelObj;
    
    public RenderTier3Rocket(final IModelCustom spaceshipModel, final String textureDomain, final String texture) {
        this.rocketModelObj = spaceshipModel;
        this.rocketTexture = new ResourceLocation(textureDomain, "textures/model/" + texture + ".png");
        this.shadowSize = 2.0f;
    }
    
    protected ResourceLocation getEntityTexture(final Entity par1Entity) {
        return this.rocketTexture;
    }
    
    public void renderSpaceship(final EntitySpaceshipBase entity, final double par2, final double par4, final double par6, final float par8, final float par9) {
        GL11.glDisable(32826);
        GL11.glPushMatrix();
        final float var24 = entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * par9 + 180.0f;
        final float var25 = entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * par9 + 45.0f;
        GL11.glTranslatef((float)par2, (float)par4 - 0.4f, (float)par6);
        GL11.glRotatef(180.0f - par8, 0.0f, 1.0f, 0.0f);
        GL11.glRotatef(-var24, 0.0f, 0.0f, 1.0f);
        final float var26 = entity.rollAmplitude / 3.0f - par9;
        float var27 = entity.shipDamage - par9;
        if (var27 < 0.0f) {
            var27 = 0.0f;
        }
        if (var26 > 0.0f) {
            final float i = entity.getLaunched() ? ((5 - MathHelper.floor_double((double)(entity.timeUntilLaunch / 85))) / 10.0f) : 0.3f;
            GL11.glRotatef(MathHelper.sin(var26) * var26 * i * par9, 1.0f, 0.0f, 0.0f);
            GL11.glRotatef(MathHelper.sin(var26) * var26 * i * par9, 1.0f, 0.0f, 1.0f);
        }
        this.bindEntityTexture((Entity)entity);
        GL11.glScalef(-1.0f, -1.0f, 1.0f);
        GL11.glScalef(0.9f, 0.9f, 0.9f);
        this.rocketModelObj.renderOnly(new String[] { "Boosters", "Rocket" });
        final Vector3 teamColor = ClientUtil.updateTeamColor(FMLClientHandler.instance().getClient().thePlayer.getCommandSenderName(), true);
        if (teamColor != null) {
            GL11.glColor3f(teamColor.floatX(), teamColor.floatY(), teamColor.floatZ());
        }
        this.rocketModelObj.renderPart("NoseCone");
        if (FMLClientHandler.instance().getClient().thePlayer.ticksExisted / 10 % 2 < 1) {
            GL11.glColor3f(1.0f, 0.0f, 0.0f);
        }
        else {
            GL11.glColor3f(0.0f, 1.0f, 0.0f);
        }
        GL11.glDisable(3553);
        GL11.glDisable(2896);
        this.rocketModelObj.renderPart("Cube");
        GL11.glEnable(3553);
        GL11.glEnable(2896);
        GL11.glColor3f(1.0f, 1.0f, 1.0f);
        GL11.glPopMatrix();
    }
    
    public void doRender(final Entity par1Entity, final double par2, final double par4, final double par6, final float par8, final float par9) {
        this.renderSpaceship((EntitySpaceshipBase)par1Entity, par2, par4, par6, par8, par9);
    }
}
