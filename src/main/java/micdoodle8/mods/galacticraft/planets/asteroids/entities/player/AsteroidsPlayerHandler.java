package micdoodle8.mods.galacticraft.planets.asteroids.entities.player;

import cpw.mods.fml.common.gameevent.*;
import cpw.mods.fml.common.eventhandler.*;
import net.minecraftforge.event.entity.*;
import micdoodle8.mods.galacticraft.core.entities.player.*;
import micdoodle8.mods.galacticraft.planets.asteroids.dimension.*;
import net.minecraft.entity.*;
import micdoodle8.mods.galacticraft.planets.asteroids.entities.*;
import net.minecraft.entity.player.*;

public class AsteroidsPlayerHandler
{
    @SubscribeEvent
    public void onPlayerLogin(final PlayerEvent.PlayerLoggedInEvent event) {
        if (event.player instanceof EntityPlayerMP) {
            this.onPlayerLogin((EntityPlayerMP)event.player);
        }
    }
    
    @SubscribeEvent
    public void onPlayerLogout(final PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.player instanceof EntityPlayerMP) {
            this.onPlayerLogout((EntityPlayerMP)event.player);
        }
    }
    
    @SubscribeEvent
    public void onPlayerRespawn(final PlayerEvent.PlayerRespawnEvent event) {
        if (event.player instanceof EntityPlayerMP) {
            this.onPlayerRespawn((EntityPlayerMP)event.player);
        }
    }
    
    @SubscribeEvent
    public void onEntityConstructing(final EntityEvent.EntityConstructing event) {
        if (event.entity instanceof EntityPlayerMP && GCPlayerStats.get((EntityPlayerMP)event.entity) == null) {
            GCPlayerStats.register((EntityPlayerMP)event.entity);
        }
    }
    
    private void onPlayerLogin(final EntityPlayerMP player) {
    }
    
    private void onPlayerLogout(final EntityPlayerMP player) {
    }
    
    private void onPlayerRespawn(final EntityPlayerMP player) {
    }
    
    public void onPlayerUpdate(final EntityPlayerMP player) {
        if (!player.worldObj.isRemote && player.worldObj.provider instanceof WorldProviderAsteroids) {
            final int f = 50;
            if (player.worldObj.rand.nextInt(50) == 0 && player.posY < 260.0) {
                final EntityPlayer closestPlayer = player.worldObj.getClosestPlayerToEntity((Entity)player, 100.0);
                if (closestPlayer == null || closestPlayer.getEntityId() <= player.getEntityId()) {
                    final double r = player.worldObj.rand.nextInt(60) + 30.0;
                    final double theta = 6.283185307179586 * player.worldObj.rand.nextDouble();
                    final double x = player.posX + Math.cos(theta) * r;
                    final double y = player.posY + player.worldObj.rand.nextInt(5);
                    final double z = player.posZ + Math.sin(theta) * r;
                    final double motX = (player.posX - x + (player.worldObj.rand.nextDouble() - 0.5) * 40.0) / 400.0;
                    final double motY = (player.worldObj.rand.nextDouble() - 0.5) * 0.4;
                    final double motZ = (player.posZ - z + (player.worldObj.rand.nextDouble() - 0.5) * 40.0) / 400.0;
                    final EntitySmallAsteroid smallAsteroid = new EntitySmallAsteroid(player.worldObj);
                    smallAsteroid.setPosition(x, y, z);
                    smallAsteroid.motionX = motX;
                    smallAsteroid.motionY = motY;
                    smallAsteroid.motionZ = motZ;
                    smallAsteroid.spinYaw = player.worldObj.rand.nextFloat() * 4.0f;
                    smallAsteroid.spinPitch = player.worldObj.rand.nextFloat() * 2.0f;
                    player.worldObj.spawnEntityInWorld((Entity)smallAsteroid);
                }
            }
        }
    }
}
