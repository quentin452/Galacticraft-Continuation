package micdoodle8.mods.galacticraft.planets.mars;

import net.minecraft.world.gen.feature.*;
import micdoodle8.mods.galacticraft.planets.mars.entities.*;
import cpw.mods.fml.common.eventhandler.*;
import net.minecraftforge.event.entity.living.*;
import net.minecraft.potion.*;
import net.minecraft.entity.*;
import micdoodle8.mods.galacticraft.planets.mars.blocks.*;
import net.minecraft.entity.player.*;
import micdoodle8.mods.galacticraft.core.entities.player.*;
import net.minecraft.world.*;
import micdoodle8.mods.galacticraft.core.util.*;
import net.minecraft.block.*;
import micdoodle8.mods.galacticraft.core.client.render.entities.*;
import cpw.mods.fml.relauncher.*;
import micdoodle8.mods.galacticraft.api.event.wgen.*;
import micdoodle8.mods.galacticraft.planets.mars.world.gen.*;
import micdoodle8.mods.galacticraft.planets.mars.dimension.*;
import net.minecraft.client.*;
import net.minecraft.util.*;
import micdoodle8.mods.galacticraft.core.tile.*;
import org.lwjgl.opengl.*;
import net.minecraft.tileentity.*;
import micdoodle8.mods.galacticraft.core.event.*;
import micdoodle8.mods.galacticraft.api.tile.*;
import micdoodle8.mods.galacticraft.planets.mars.tile.*;
import java.util.*;

public class EventHandlerMars
{
    private WorldGenerator eggGenerator;
    
    @SubscribeEvent
    public void onLivingDeath(final LivingDeathEvent event) {
        if (event.source.damageType.equals("slimeling") && event.source instanceof EntityDamageSource) {
            final EntityDamageSource source = (EntityDamageSource)event.source;
            if (source.getEntity() instanceof EntitySlimeling && !source.getEntity().worldObj.isRemote) {
                final EntitySlimeling entitySlimeling = (EntitySlimeling)source.getEntity();
                ++entitySlimeling.kills;
            }
        }
    }
    
    @SubscribeEvent
    public void onLivingAttacked(final LivingAttackEvent event) {
        if (!event.entity.isEntityInvulnerable() && !event.entity.worldObj.isRemote && event.entityLiving.getHealth() <= 0.0f && (!event.source.isFireDamage() || !event.entityLiving.isPotionActive(Potion.fireResistance))) {
            final Entity entity = event.source.getEntity();
            if (entity instanceof EntitySlimeling) {
                final EntitySlimeling entitywolf = (EntitySlimeling)entity;
                if (entitywolf.isTamed()) {
                    event.entityLiving.recentlyHit = 100;
                    event.entityLiving.attackingPlayer = null;
                }
            }
        }
    }
    
    @SubscribeEvent
    public void onPlayerWakeUp(final EventWakePlayer event) {
        final ChunkCoordinates c = event.entityPlayer.playerLocation;
        final Block blockID = event.entityPlayer.worldObj.getBlock(c.posX, c.posY, c.posZ);
        final int metadata = event.entityPlayer.worldObj.getBlockMetadata(c.posX, c.posY, c.posZ);
        if (blockID == MarsBlocks.machine && metadata >= 4) {
            if (!event.flag1 && event.flag2 && event.flag3) {
                event.result = EntityPlayer.EnumStatus.NOT_POSSIBLE_HERE;
            }
            else if (!event.flag1 && !event.flag2 && event.flag3 && !event.entityPlayer.worldObj.isRemote) {
                event.entityPlayer.heal(5.0f);
                GCPlayerStats.get((EntityPlayerMP)event.entityPlayer).cryogenicChamberCooldown = 6000;
                final WorldServer ws = (WorldServer)event.entityPlayer.worldObj;
                ws.updateAllPlayersSleepingFlag();
                if (ws.areAllPlayersAsleep() && ws.getGameRules().getGameRuleBooleanValue("doDaylightCycle")) {
                    WorldUtil.setNextMorning(ws);
                }
            }
        }
    }
    
    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onPlayerRotate(final RenderPlayerGC.RotatePlayerEvent event) {
        final ChunkCoordinates c = event.entityPlayer.playerLocation;
        for (int x = -1; x < 2; ++x) {
            for (int z = -1; z < 2; ++z) {
                if (x * z == 0) {
                    final Block block = event.entityPlayer.worldObj.getBlock(c.posX + x, c.posY, c.posZ + z);
                    if (block == MarsBlocks.machine) {
                        final int metadata = event.entityPlayer.worldObj.getBlockMetadata(c.posX + x, c.posY, c.posZ + z);
                        if (metadata >= 4) {
                            event.shouldRotate = true;
                            event.vanillaOverride = true;
                            return;
                        }
                    }
                }
            }
        }
    }
    
    @SubscribeEvent
    public void onPlanetDecorated(final GCCoreEventPopulate.Post event) {
        if (this.eggGenerator == null) {
            this.eggGenerator = new WorldGenEggs(MarsBlocks.rock);
        }
        if (event.worldObj.provider instanceof WorldProviderMars) {
            for (int eggsPerChunk = 2, eggCount = 0; eggCount < eggsPerChunk; ++eggCount) {
                final int x = event.chunkX + event.rand.nextInt(16) + 8;
                final int y = event.rand.nextInt(128);
                final int z = event.chunkZ + event.rand.nextInt(16) + 8;
                this.eggGenerator.generate(event.worldObj, event.rand, x, y, z);
            }
        }
    }
    
    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void orientCamera(final EventHandlerGC.OrientCameraEvent event) {
        final EntityPlayer entity = (EntityPlayer)Minecraft.getMinecraft().thePlayer;
        if (entity != null) {
            final int x = MathHelper.floor_double(entity.posX);
            final int y = MathHelper.floor_double(entity.posY);
            final int z = MathHelper.floor_double(entity.posZ);
            TileEntity tile = Minecraft.getMinecraft().theWorld.getTileEntity(x, y - 1, z);
            if (tile instanceof TileEntityMulti) {
                tile = ((TileEntityMulti)tile).getMainBlockTile();
            }
            if (tile instanceof TileEntityCryogenicChamber) {
                entity.rotationPitch = 0.0f;
                switch (tile.getBlockMetadata() & 0x3) {
                    case 0: {
                        GL11.glRotatef(180.0f, 0.0f, 1.0f, 0.0f);
                        GL11.glTranslatef(-0.4f, -0.5f, 4.1f);
                        GL11.glRotatef(270.0f, 0.0f, 1.0f, 0.0f);
                        entity.rotationYaw = 0.0f;
                        entity.rotationYawHead = 320.0f;
                        break;
                    }
                    case 1: {
                        GL11.glRotatef(180.0f, 0.0f, 1.0f, 0.0f);
                        GL11.glTranslatef(0.0f, -0.5f, 4.1f);
                        GL11.glRotatef(90.0f, 0.0f, 1.0f, 0.0f);
                        entity.rotationYaw = 0.0f;
                        entity.rotationYawHead = 45.0f;
                        break;
                    }
                    case 2: {
                        GL11.glRotatef(180.0f, 0.0f, 1.0f, 0.0f);
                        GL11.glTranslatef(0.0f, -0.5f, 4.1f);
                        GL11.glRotatef(180.0f, 0.0f, 1.0f, 0.0f);
                        entity.rotationYaw = 0.0f;
                        entity.rotationYawHead = 45.0f;
                        break;
                    }
                    case 3: {
                        GL11.glRotatef(180.0f, 0.0f, 1.0f, 0.0f);
                        GL11.glTranslatef(0.0f, -0.5f, 4.1f);
                        entity.rotationYaw = 0.0f;
                        entity.rotationYawHead = 335.0f;
                        break;
                    }
                }
            }
        }
    }
    
    @SubscribeEvent
    public void onLandingPadRemoved(final EventLandingPadRemoval event) {
        final TileEntity tile = event.world.getTileEntity(event.x, event.y, event.z);
        if (tile instanceof IFuelDock) {
            final IFuelDock dock = (IFuelDock)tile;
            for (final ILandingPadAttachable connectedTile : dock.getConnectedTiles()) {
                if (connectedTile instanceof TileEntityLaunchController) {
                    final TileEntityLaunchController launchController = (TileEntityLaunchController)event.world.getTileEntity(((TileEntityLaunchController)connectedTile).xCoord, ((TileEntityLaunchController)connectedTile).yCoord, ((TileEntityLaunchController)connectedTile).zCoord);
                    if (launchController.getEnergyStoredGC() > 0.0f && launchController.launchPadRemovalDisabled && !launchController.getDisabled(0)) {
                        event.allow = false;
                        return;
                    }
                    break;
                }
            }
        }
    }
}
