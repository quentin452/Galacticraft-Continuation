package micdoodle8.mods.galacticraft.core.client.fx;

import cpw.mods.fml.relauncher.*;
import micdoodle8.mods.galacticraft.api.vector.*;
import cpw.mods.fml.client.*;
import net.minecraft.world.*;
import net.minecraft.entity.*;
import net.minecraft.client.*;
import net.minecraft.client.particle.*;

@SideOnly(Side.CLIENT)
public class EffectHandler
{
    public static void spawnParticle(final String particleID, final Vector3 position, final Vector3 motion, final Object... otherInfo) {
        final Minecraft mc = FMLClientHandler.instance().getClient();
        if (mc != null && mc.renderViewEntity != null && mc.effectRenderer != null) {
            final double dX = mc.renderViewEntity.posX - position.x;
            final double dY = mc.renderViewEntity.posY - position.y;
            final double dZ = mc.renderViewEntity.posZ - position.z;
            EntityFX particle = null;
            final double viewDistance = 64.0;
            if (particleID.equals("whiteSmokeIdle")) {
                particle = new EntityFXLaunchSmoke((World)mc.theWorld, position, motion, 1.0f, false);
            }
            else if (particleID.equals("whiteSmokeLaunched")) {
                particle = new EntityFXLaunchSmoke((World)mc.theWorld, position, motion, 1.0f, true);
            }
            else if (particleID.equals("whiteSmokeLargeIdle")) {
                particle = new EntityFXLaunchSmoke((World)mc.theWorld, position, motion, 2.5f, false);
            }
            else if (particleID.equals("whiteSmokeLargeLaunched")) {
                particle = new EntityFXLaunchSmoke((World)mc.theWorld, position, motion, 2.5f, true);
            }
            else if (particleID.equals("launchFlameIdle")) {
                particle = new EntityFXLaunchFlame((World)mc.theWorld, position, motion, false, (EntityLivingBase)otherInfo[0]);
            }
            else if (particleID.equals("launchFlameLaunched")) {
                particle = new EntityFXLaunchFlame((World)mc.theWorld, position, motion, true, (EntityLivingBase)otherInfo[0]);
            }
            else if (particleID.equals("whiteSmokeTiny")) {
                particle = new EntityFXSmokeSmall((World)mc.theWorld, position, motion);
            }
            else if (particleID.equals("distanceSmoke") && dX * dX + dY * dY + dZ * dZ < viewDistance * viewDistance * 1.7) {
                particle = (EntityFX)new EntitySmokeFX((World)mc.theWorld, position.x, position.y, position.z, motion.x, motion.y, motion.z, 2.5f);
            }
            else if (particleID.equals("oilDrip")) {
                particle = new EntityFXOilDrip((World)mc.theWorld, position.x, position.y, position.z);
            }
            if (dX * dX + dY * dY + dZ * dZ < viewDistance * viewDistance && particleID.equals("oxygen")) {
                particle = new EntityFXEntityOxygen((World)mc.theWorld, position, motion, (Vector3)otherInfo[0]);
            }
            if (particle != null) {
                particle.prevPosX = particle.posX;
                particle.prevPosY = particle.posY;
                particle.prevPosZ = particle.posZ;
                mc.effectRenderer.addEffect(particle);
            }
        }
    }
}
