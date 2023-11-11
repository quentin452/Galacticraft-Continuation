package micdoodle8.mods.galacticraft.core.entities.player;

import net.minecraft.entity.player.*;
import micdoodle8.mods.galacticraft.core.tile.*;
import micdoodle8.mods.galacticraft.api.entity.*;
import micdoodle8.mods.galacticraft.core.dimension.*;
import micdoodle8.mods.galacticraft.core.entities.*;
import micdoodle8.mods.galacticraft.core.*;
import micdoodle8.mods.galacticraft.planets.asteroids.dimension.*;
import micdoodle8.mods.galacticraft.core.util.*;
import micdoodle8.mods.galacticraft.planets.asteroids.items.*;
import net.minecraft.item.*;
import micdoodle8.mods.galacticraft.planets.mars.items.*;
import net.minecraft.entity.*;
import micdoodle8.mods.galacticraft.core.event.*;
import net.minecraftforge.common.*;
import cpw.mods.fml.common.eventhandler.*;
import net.minecraft.util.*;

public class PlayerServer implements IPlayerServer
{
    boolean updatingRidden;
    
    public PlayerServer() {
        this.updatingRidden = false;
    }
    
    public void clonePlayer(final EntityPlayerMP player, final EntityPlayer oldPlayer, final boolean keepInv) {
        if (oldPlayer instanceof EntityPlayerMP) {
            GCPlayerStats.get(player).copyFrom(GCPlayerStats.get((EntityPlayerMP)oldPlayer), keepInv || player.worldObj.getGameRules().getGameRuleBooleanValue("keepInventory"));
            TileEntityTelemetry.updateLinkedPlayer((EntityPlayerMP)oldPlayer, player);
        }
    }
    
    public void updateRiddenPre(final EntityPlayerMP player) {
        this.updatingRidden = true;
    }
    
    public void updateRiddenPost(final EntityPlayerMP player) {
        this.updatingRidden = false;
    }
    
    public boolean mountEntity(final EntityPlayerMP player, final Entity par1Entity) {
        return this.updatingRidden && player.ridingEntity instanceof IIgnoreShift && ((IIgnoreShift)player.ridingEntity).shouldIgnoreShiftExit();
    }
    
    public void moveEntity(final EntityPlayerMP player, final double par1, final double par3, final double par5) {
        if (player.worldObj.provider instanceof WorldProviderMoon && !player.worldObj.isRemote && player.ridingEntity == null) {
            GCPlayerHandler.updateFeet(player, par1, par5);
        }
    }
    
    public boolean wakeUpPlayer(final EntityPlayerMP player, final boolean par1, final boolean par2, final boolean par3) {
        return this.wakeUpPlayer(player, par1, par2, par3, false);
    }
    
    public float attackEntityFrom(final EntityPlayerMP player, final DamageSource par1DamageSource, float par2) {
        if (player.ridingEntity instanceof EntityCelestialFake) {
            return -1.0f;
        }
        if (GalacticraftCore.isPlanetsLoaded) {
            if (par1DamageSource == DamageSource.outOfWorld) {
                if (player.worldObj.provider instanceof WorldProviderAsteroids) {
                    if (player.posY > -120.0) {
                        return -1.0f;
                    }
                    if (player.posY > -180.0) {
                        par2 /= 2.0f;
                    }
                }
            }
            else if (par1DamageSource == DamageSource.fall || par1DamageSource == DamageSourceGC.spaceshipCrash) {
                int titaniumCount = 0;
                if (player.inventory != null) {
                    for (int i = 0; i < 4; ++i) {
                        final ItemStack armorPiece = player.getCurrentArmor(i);
                        if (armorPiece != null && armorPiece.getItem() instanceof ItemArmorAsteroids) {
                            ++titaniumCount;
                        }
                    }
                }
                if (titaniumCount == 4) {
                    titaniumCount = 5;
                }
                par2 *= (float)(1.0 - 0.15 * titaniumCount);
            }
        }
        return par2;
    }
    
    public void knockBack(final EntityPlayerMP player, final Entity p_70653_1_, final float p_70653_2_, final double impulseX, final double impulseZ) {
        int deshCount = 0;
        if (player.inventory != null && GalacticraftCore.isPlanetsLoaded) {
            for (int i = 0; i < 4; ++i) {
                final ItemStack armorPiece = player.getCurrentArmor(i);
                if (armorPiece != null && armorPiece.getItem() instanceof ItemArmorMars) {
                    ++deshCount;
                }
            }
        }
        if (player.getRNG().nextDouble() >= player.getEntityAttribute(SharedMonsterAttributes.knockbackResistance).getAttributeValue()) {
            player.isAirBorne = (deshCount < 2);
            final float f1 = MathHelper.sqrt_double(impulseX * impulseX + impulseZ * impulseZ);
            final float f2 = 0.4f - deshCount * 0.05f;
            final double d1 = 2.0 - deshCount * 0.15;
            player.motionX /= d1;
            player.motionY /= d1;
            player.motionZ /= d1;
            player.motionX -= f2 * impulseX / f1;
            player.motionY += f2;
            player.motionZ -= f2 * impulseZ / f1;
            if (player.motionY > 0.4) {
                player.motionY = 0.4;
            }
        }
    }
    
    public boolean wakeUpPlayer(final EntityPlayerMP player, final boolean par1, final boolean par2, final boolean par3, final boolean bypass) {
        final ChunkCoordinates c = player.playerLocation;
        if (c != null) {
            final EventWakePlayer event = new EventWakePlayer((EntityPlayer)player, c.posX, c.posY, c.posZ, par1, par2, par3, bypass);
            MinecraftForge.EVENT_BUS.post((Event)event);
            if (bypass || event.result == null || event.result == EntityPlayer.EnumStatus.OK) {
                return false;
            }
        }
        return true;
    }
}
