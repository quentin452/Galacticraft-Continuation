package micdoodle8.mods.galacticraft.planets.mars;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayer.EnumStatus;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.potion.Potion;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.WorldServer;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import micdoodle8.mods.galacticraft.api.event.wgen.GCCoreEventPopulate;
import micdoodle8.mods.galacticraft.api.tile.IFuelDock;
import micdoodle8.mods.galacticraft.api.tile.ILandingPadAttachable;
import micdoodle8.mods.galacticraft.core.client.render.entities.RenderPlayerGC.RotatePlayerEvent;
import micdoodle8.mods.galacticraft.core.entities.player.GCPlayerStats;
import micdoodle8.mods.galacticraft.core.event.EventHandlerGC.OrientCameraEvent;
import micdoodle8.mods.galacticraft.core.event.EventLandingPadRemoval;
import micdoodle8.mods.galacticraft.core.event.EventWakePlayer;
import micdoodle8.mods.galacticraft.core.tile.TileEntityMulti;
import micdoodle8.mods.galacticraft.core.util.WorldUtil;
import micdoodle8.mods.galacticraft.planets.mars.blocks.BlockMachineMars;
import micdoodle8.mods.galacticraft.planets.mars.blocks.MarsBlocks;
import micdoodle8.mods.galacticraft.planets.mars.dimension.WorldProviderMars;
import micdoodle8.mods.galacticraft.planets.mars.entities.EntitySlimeling;
import micdoodle8.mods.galacticraft.planets.mars.tile.TileEntityCryogenicChamber;
import micdoodle8.mods.galacticraft.planets.mars.tile.TileEntityLaunchController;
import micdoodle8.mods.galacticraft.planets.mars.world.gen.WorldGenEggs;

public class EventHandlerMars {

    @SubscribeEvent
    public void onLivingDeath(LivingDeathEvent event) {
        if ("slimeling".equals(event.source.damageType) && event.source instanceof EntityDamageSource
            && event.source.getEntity() instanceof EntitySlimeling
            && !event.source.getEntity().worldObj.isRemote) {
            Entity entity = event.source.getEntity();
            if (entity instanceof EntitySlimeling) {
                ((EntitySlimeling) entity).kills++;
            }
        }
    }

    @SubscribeEvent
    public void onLivingAttacked(LivingAttackEvent event) {
        if (!event.entity.isEntityInvulnerable() && !event.entity.worldObj.isRemote
            && event.entityLiving.getHealth() <= 0.0F
            && (!event.source.isFireDamage() || !event.entityLiving.isPotionActive(Potion.fireResistance))) {
            Entity entity = event.source.getEntity();
            if (entity instanceof EntitySlimeling) {
                EntitySlimeling entitySlimeling = (EntitySlimeling) entity;
                if (entitySlimeling.isTamed()) {
                    event.entityLiving.recentlyHit = 100;
                    event.entityLiving.attackingPlayer = null;
                }
            }
        }
    }

    @SubscribeEvent
    public void onPlayerWakeUp(EventWakePlayer event) {
        final ChunkCoordinates c = event.entityPlayer.playerLocation;
        final Block blockID = event.entityPlayer.worldObj.getBlock(c.posX, c.posY, c.posZ);
        final int metadata = event.entityPlayer.worldObj.getBlockMetadata(c.posX, c.posY, c.posZ);

        if (blockID == MarsBlocks.machine && metadata >= BlockMachineMars.CRYOGENIC_CHAMBER_METADATA) {
            if (!event.flag1 && event.flag2 && event.flag3) {
                event.result = EnumStatus.NOT_POSSIBLE_HERE;
            } else if (!event.flag1 && !event.flag2 && event.flag3 && !event.entityPlayer.worldObj.isRemote) {
                event.entityPlayer.heal(5.0F);
                GCPlayerStats.get((EntityPlayerMP) event.entityPlayer).cryogenicChamberCooldown = 6000;

                final WorldServer ws = (WorldServer) event.entityPlayer.worldObj;
                ws.updateAllPlayersSleepingFlag();
                if (ws.areAllPlayersAsleep() && ws.getGameRules()
                    .getGameRuleBooleanValue("doDaylightCycle")) {
                    WorldUtil.setNextMorning(ws);
                }
            }
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onPlayerRotate(RotatePlayerEvent event) {
        final ChunkCoordinates c = event.entityPlayer.playerLocation;
        for (int x = -1; x < 2; x++) {
            for (int z = -1; z < 2; z++) {
                if (x * z != 0) {
                    continue;
                }
                final Block block = event.entityPlayer.worldObj.getBlock(c.posX + x, c.posY, c.posZ + z);
                if (block == MarsBlocks.machine) {
                    final int metadata = event.entityPlayer.worldObj.getBlockMetadata(c.posX + x, c.posY, c.posZ + z);
                    if (metadata >= BlockMachineMars.CRYOGENIC_CHAMBER_METADATA) {
                        event.shouldRotate = true;
                        event.vanillaOverride = true;
                        return;
                    }
                }
            }
        }
    }

    private WorldGenerator eggGenerator;

    @SubscribeEvent
    public void onPlanetDecorated(GCCoreEventPopulate.Post event) {
        if (this.eggGenerator == null) {
            this.eggGenerator = new WorldGenEggs(MarsBlocks.rock);
        }

        if (event.worldObj.provider instanceof WorldProviderMars) {
            final int eggsPerChunk = 2;
            int x;
            int y;
            int z;

            for (int eggCount = 0; eggCount < eggsPerChunk; ++eggCount) {
                x = event.chunkX + event.rand.nextInt(16) + 8;
                y = event.rand.nextInt(128);
                z = event.chunkZ + event.rand.nextInt(16) + 8;
                this.eggGenerator.generate(event.worldObj, event.rand, x, y, z);
            }
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void orientCamera(OrientCameraEvent event) {
        final EntityPlayer entity = Minecraft.getMinecraft().thePlayer;

        if (entity != null) {
            final int x = MathHelper.floor_double(entity.posX);
            final int y = MathHelper.floor_double(entity.posY);
            final int z = MathHelper.floor_double(entity.posZ);
            TileEntity tile = Minecraft.getMinecraft().theWorld.getTileEntity(x, y - 1, z);

            if (tile instanceof TileEntityMulti) {
                tile = ((TileEntityMulti) tile).getMainBlockTile();
            }

            if (tile instanceof TileEntityCryogenicChamber) {
                entity.rotationPitch = 0;

                switch (tile.getBlockMetadata() & 3) {
                    case 0:
                        GL11.glRotatef(180, 0.0F, 1.0F, 0.0F);
                        GL11.glTranslatef(-0.4F, -0.5F, 4.1F);
                        GL11.glRotatef(270, 0.0F, 1.0F, 0.0F);
                        entity.rotationYaw = 0;
                        entity.rotationYawHead = 320;
                        break;
                    case 1:
                        GL11.glRotatef(180, 0.0F, 1.0F, 0.0F);
                        GL11.glTranslatef(0, -0.5F, 4.1F);
                        GL11.glRotatef(90, 0.0F, 1.0F, 0.0F);
                        entity.rotationYaw = 0;
                        entity.rotationYawHead = 45;
                        break;
                    case 2:
                        GL11.glRotatef(180, 0.0F, 1.0F, 0.0F);
                        GL11.glTranslatef(0, -0.5F, 4.1F);
                        GL11.glRotatef(180, 0.0F, 1.0F, 0.0F);
                        entity.rotationYaw = 0;
                        entity.rotationYawHead = 45;
                        break;
                    case 3:
                        GL11.glRotatef(180, 0.0F, 1.0F, 0.0F);
                        GL11.glTranslatef(0.0F, -0.5F, 4.1F);
                        entity.rotationYaw = 0;
                        entity.rotationYawHead = 335;
                        break;
                }
            }
        }
    }

    @SubscribeEvent
    public void onLandingPadRemoved(EventLandingPadRemoval event) {
        TileEntity tile = event.world.getTileEntity(event.x, event.y, event.z);

        if (tile instanceof IFuelDock) {
            IFuelDock dock = (IFuelDock) tile;
            for (ILandingPadAttachable connectedTile : dock.getConnectedTiles()) {
                if (connectedTile instanceof TileEntityLaunchController) {
                    TileEntityLaunchController launchController = (TileEntityLaunchController) event.world
                        .getTileEntity(
                            ((TileEntityLaunchController) connectedTile).xCoord,
                            ((TileEntityLaunchController) connectedTile).yCoord,
                            ((TileEntityLaunchController) connectedTile).zCoord);
                    if (launchController.getEnergyStoredGC() > 0.0F && launchController.launchPadRemovalDisabled
                        && !launchController.getDisabled(0)) {
                        event.allow = false;
                    }
                    break;
                }
            }
        }
    }
}
