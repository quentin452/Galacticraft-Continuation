package micdoodle8.mods.galacticraft.core.entities.player;

import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.*;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraftforge.common.MinecraftForge;

import cpw.mods.fml.client.FMLClientHandler;
import micdoodle8.mods.galacticraft.api.entity.ICameraZoomEntity;
import micdoodle8.mods.galacticraft.api.vector.BlockVec3;
import micdoodle8.mods.galacticraft.api.vector.Vector3;
import micdoodle8.mods.galacticraft.api.world.IGalacticraftWorldProvider;
import micdoodle8.mods.galacticraft.api.world.IZeroGDimension;
import micdoodle8.mods.galacticraft.core.GalacticraftCore;
import micdoodle8.mods.galacticraft.core.blocks.GCBlocks;
import micdoodle8.mods.galacticraft.core.dimension.WorldProviderMoon;
import micdoodle8.mods.galacticraft.core.entities.EntityLanderBase;
import micdoodle8.mods.galacticraft.core.event.EventWakePlayer;
import micdoodle8.mods.galacticraft.core.network.PacketSimple;
import micdoodle8.mods.galacticraft.core.network.PacketSimple.EnumSimplePacket;
import micdoodle8.mods.galacticraft.core.proxy.ClientProxyCore;
import micdoodle8.mods.galacticraft.core.tick.TickHandlerClient;
import micdoodle8.mods.galacticraft.core.tile.TileEntityAdvanced;
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore;
import micdoodle8.mods.galacticraft.core.util.EnumColor;
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil;
import micdoodle8.mods.galacticraft.core.util.WorldUtil;
import micdoodle8.mods.galacticraft.core.wrappers.PlayerGearData;

public class PlayerClient implements IPlayerClient {

    private double downMot2;
    public static boolean startup;

    @Override
    public void moveEntity(EntityPlayerSP player, double par1, double par3, double par5) {
        this.updateFeet(player, par1, par5);
    }

    @Override
    public boolean wakeUpPlayer(EntityPlayerSP player, boolean par1, boolean par2, boolean par3) {
        return this.wakeUpPlayer(player, par1, par2, par3, false);
    }

    @Override
    public void onUpdate(EntityPlayerSP player) {
        final GCPlayerStatsClient stats = GCPlayerStatsClient.get(player);
        stats.tick++;

        if (stats.usingParachute && !player.capabilities.isFlying && !player.handleWaterMovement()) {
            player.motionY = -0.5D;
            player.motionX *= 0.5F;
            player.motionZ *= 0.5F;
        }
    }

    @Override
    public boolean isEntityInsideOpaqueBlock(EntityPlayerSP player, boolean vanillaInside) {
        if (vanillaInside && GCPlayerStatsClient.get(player).inFreefall) {
            GCPlayerStatsClient.get(player).inFreefall = false;
            return false;
        }
        return !(player.ridingEntity instanceof EntityLanderBase) && vanillaInside;
    }

    @Override
    public void onLivingUpdatePre(EntityPlayerSP player) {
        final GCPlayerStatsClient stats = GCPlayerStatsClient.get(player);

        if (player.worldObj.provider instanceof IGalacticraftWorldProvider) {
            if (!startup) {
                stats.inFreefallLast = stats.inFreefall;
                stats.inFreefall = FreefallHandler.testFreefall(player);
                startup = true;
            }
            if (player.worldObj.provider instanceof IZeroGDimension) {
                stats.inFreefallLast = stats.inFreefall;
                stats.inFreefall = FreefallHandler.testFreefall(player);
                this.downMot2 = stats.downMotionLast;
                stats.downMotionLast = player.motionY;
                stats.freefallHandler.preVanillaMotion(player);
            }
        }

        // if (player.boundingBox != null && stats.boundingBoxBefore == null)
        // {
        // GCLog.debug("Changed player BB from " + player.boundingBox.minY);
        // stats.boundingBoxBefore = player.boundingBox;
        // player.boundingBox.setBounds(stats.boundingBoxBefore.minX + 0.4,
        // stats.boundingBoxBefore.minY +
        // 0.9, stats.boundingBoxBefore.minZ + 0.4, stats.boundingBoxBefore.maxX - 0.4,
        // stats.boundingBoxBefore.maxY -
        // 0.9, stats.boundingBoxBefore.maxZ - 0.4);
        // GCLog.debug("Changed player BB to " + player.boundingBox.minY);
        // }
        // else if (player.boundingBox != null && stats.boundingBoxBefore != null)
        // {
        // player.boundingBox.setBB(stats.boundingBoxBefore);
        // GCLog.debug("Changed player BB to " + player.boundingBox.minY);
        // }
    }

    @Override
    public void onLivingUpdatePost(EntityPlayerSP player) {
        final GCPlayerStatsClient stats = GCPlayerStatsClient.get(player);

        if (player.worldObj.provider instanceof IZeroGDimension) {
            stats.freefallHandler.postVanillaMotion(player);

            if (stats.inFreefall) {
                // No limb swing
                player.limbSwing -= player.limbSwingAmount;
                player.limbSwingAmount = player.prevLimbSwingAmount;
                final float adjust = Math.min(Math.abs(player.limbSwing), Math.abs(player.limbSwingAmount) / 3);
                if (player.limbSwing < 0) {
                    player.limbSwing += adjust;
                } else if (player.limbSwing > 0) {
                    player.limbSwing -= adjust;
                }
                player.limbSwingAmount *= 0.9F;
            } else if (stats.inFreefallLast && this.downMot2 < -0.008D) {
                stats.landingTicks = 5 - (int) (Math.min(this.downMot2, stats.downMotionLast) * 40);
                if (stats.landingTicks > GCPlayerStatsClient.MAX_LANDINGTICKS) {
                    if (stats.landingTicks > GCPlayerStatsClient.MAX_LANDINGTICKS + 4) {
                        stats.pjumpticks = stats.landingTicks - GCPlayerStatsClient.MAX_LANDINGTICKS - 5;
                    }
                    stats.landingTicks = GCPlayerStatsClient.MAX_LANDINGTICKS;
                }
                final float dYmax = 0.3F * stats.landingTicks / GCPlayerStatsClient.MAX_LANDINGTICKS;
                float factor = 1F;
                for (int i = 0; i <= stats.landingTicks; i++) {
                    stats.landingYOffset[i] = dYmax * MathHelper.sin(i * (float) Math.PI / stats.landingTicks) * factor;
                    factor *= 0.97F;
                }
            }

            if (stats.landingTicks > 0) {
                stats.landingTicks--;
                player.limbSwing *= 0.8F;
                player.limbSwingAmount = 0F;
            }
        } else {
            stats.inFreefall = false;
        }

        final boolean ridingThirdPersonEntity = player.ridingEntity instanceof ICameraZoomEntity
            && ((ICameraZoomEntity) player.ridingEntity).defaultThirdPerson();

        if (ridingThirdPersonEntity && !stats.lastRidingCameraZoomEntity
            && !ConfigManagerCore.disableVehicleCameraChanges) {
            FMLClientHandler.instance()
                .getClient().gameSettings.thirdPersonView = 1;
        }

        if (player.ridingEntity instanceof ICameraZoomEntity) {
            if (!ConfigManagerCore.disableVehicleCameraChanges) {
                stats.lastZoomed = true;
                TickHandlerClient.zoom(((ICameraZoomEntity) player.ridingEntity).getCameraZoom());
            }
        } else if (stats.lastZoomed && !ConfigManagerCore.disableVehicleCameraChanges) {
            stats.lastZoomed = false;
            TickHandlerClient.zoom(4.0F);
        }

        stats.lastRidingCameraZoomEntity = ridingThirdPersonEntity;

        if (stats.usingParachute) {
            player.fallDistance = 0.0F;
        }

        final PlayerGearData gearData = ClientProxyCore.playerItemData.get(player.getCommandSenderName());

        stats.usingParachute = false;

        if (gearData != null) {
            stats.usingParachute = gearData.getParachute() != null;
            if (!GalacticraftCore.isHeightConflictingModInstalled) {
                if (gearData.getMask() >= 0) {
                    player.height = 1.9375F;
                } else {
                    player.height = 1.8F;
                }
                player.boundingBox.maxY = player.boundingBox.minY + player.height;
            }
        }

        if (stats.usingParachute && player.onGround) {
            stats.setParachute(false);
            FMLClientHandler.instance()
                .getClient().gameSettings.thirdPersonView = stats.thirdPersonView;
        }

        if (!stats.lastUsingParachute && stats.usingParachute) {
            FMLClientHandler.instance()
                .getClient()
                .getSoundHandler()
                .playSound(
                    new PositionedSoundRecord(
                        new ResourceLocation(GalacticraftCore.TEXTURE_PREFIX + "player.parachute"),
                        0.95F + player.getRNG()
                            .nextFloat() * 0.1F,
                        1.0F,
                        (float) player.posX,
                        (float) player.posY,
                        (float) player.posZ));
        }

        stats.lastUsingParachute = stats.usingParachute;
        stats.lastOnGround = player.onGround;
    }

    @Override
    public float getBedOrientationInDegrees(EntityPlayerSP player, float vanillaDegrees) {
        if (player.playerLocation != null) {
            final int x = player.playerLocation.posX;
            final int y = player.playerLocation.posY;
            final int z = player.playerLocation.posZ;

            if (!(player.worldObj.getTileEntity(x, y, z) instanceof TileEntityAdvanced)) {
                return vanillaDegrees;
            }
            // int j = player.worldObj.getBlock(x, y, z).getBedDirection(player.worldObj, x,
            // y, z);
            switch (player.worldObj.getBlockMetadata(x, y, z) - 4) {
                case 0:
                    return 90.0F;
                case 1:
                    return 270.0F;
                case 2:
                    return 180.0F;
                case 3:
                    return 0.0F;
            }
        }

        return vanillaDegrees;
    }

    private void updateFeet(EntityPlayerSP player, double motionX, double motionZ) {
        final GCPlayerStatsClient stats = GCPlayerStatsClient.get(player);
        final double motionSqrd = motionX * motionX + motionZ * motionZ;

        // If the player is on the moon, not airbourne and not riding anything
        if (motionSqrd > 0.001 && player.worldObj != null
            && player.worldObj.provider instanceof WorldProviderMoon
            && player.ridingEntity == null
            && !player.capabilities.isFlying) {
            final int iPosX = (int) Math.floor(player.posX);
            final int iPosY = (int) Math.floor(player.posY - 2);
            final int iPosZ = (int) Math.floor(player.posZ);

            // If the block below is the moon block
            // And is the correct metadata (moon turf)
            if (player.worldObj.getBlock(iPosX, iPosY, iPosZ) == GCBlocks.blockMoon
                && player.worldObj.getBlockMetadata(iPosX, iPosY, iPosZ) == 5) {
                // If it has been long enough since the last step
                if (stats.distanceSinceLastStep > 0.35) {
                    Vector3 pos = new Vector3(player);
                    // Set the footprint position to the block below and add random number to stop
                    // z-fighting
                    pos.y = MathHelper.floor_double(player.posY - 1) + player.getRNG()
                        .nextFloat() / 100.0F;

                    // Adjust footprint to left or right depending on step count
                    switch (stats.lastStep) {
                        case 0:
                            pos.translate(
                                new Vector3(
                                    Math.sin(Math.toRadians(-player.rotationYaw + 90)) * 0.25,
                                    0,
                                    Math.cos(Math.toRadians(-player.rotationYaw + 90)) * 0.25));
                            break;
                        case 1:
                            pos.translate(
                                new Vector3(
                                    Math.sin(Math.toRadians(-player.rotationYaw - 90)) * 0.25,
                                    0,
                                    Math.cos(Math.toRadians(-player.rotationYaw - 90)) * 0.25));
                            break;
                    }

                    pos = WorldUtil
                        .getFootprintPosition(player.worldObj, player.rotationYaw - 180, pos, new BlockVec3(player));

                    final long chunkKey = ChunkCoordIntPair.chunkXZ2Int(pos.intX() >> 4, pos.intZ() >> 4);
                    ClientProxyCore.footprintRenderer.addFootprint(
                        chunkKey,
                        player.worldObj.provider.dimensionId,
                        pos,
                        player.rotationYaw,
                        player.getCommandSenderName());

                    // Increment and cap step counter at 1
                    stats.lastStep++;
                    stats.lastStep %= 2;
                    stats.distanceSinceLastStep = 0;
                } else {
                    stats.distanceSinceLastStep += motionSqrd;
                }
            }
        }
    }

    public boolean wakeUpPlayer(EntityPlayerSP player, boolean par1, boolean par2, boolean par3, boolean bypass) {
        final ChunkCoordinates c = player.playerLocation;

        if (c != null) {
            final EventWakePlayer event = new EventWakePlayer(player, c.posX, c.posY, c.posZ, par1, par2, par3, bypass);
            MinecraftForge.EVENT_BUS.post(event);

            return !bypass && event.result != null && event.result != EntityPlayer.EnumStatus.OK;
        }

        return true;
    }

    @Override
    public void onBuild(int i, EntityPlayerSP player) {
        // 0 : opened GC inventory tab
        // 1,2,3 : Compressor, CF, Standard Wrench
        // 4,5,6 : Fuel loader, Launchpad, NASA Workbench
        // 7: oil found 8: placed rocket

        final GCPlayerStatsClient stats = GCPlayerStatsClient.get(player);
        int flag = stats.buildFlags;
        if (flag == -1) {
            flag = 0;
        }
        int repeatCount = flag >> 9;
        if (repeatCount <= 3) {
            repeatCount++;
        }
        if ((flag & 1 << i) != 0) {
            return;
        }
        flag |= 1 << i;
        stats.buildFlags = (flag & 511) + (repeatCount << 9);
        GalacticraftCore.packetPipeline
            .sendToServer(new PacketSimple(EnumSimplePacket.S_BUILDFLAGS_UPDATE, new Object[] { stats.buildFlags }));
        switch (i) {
            case 0:
            case 1:
            case 2:
            case 3:
                player.addChatMessage(
                    IChatComponent.Serializer.func_150699_a(
                        "[{\"text\":\"" + GCCoreUtil.translate("gui.message.help1")
                            + ": \",\"color\":\"white\"},"
                            + "{\"text\":\" "
                            + EnumColor.BRIGHT_GREEN
                            + "wiki."
                            + GalacticraftCore.PREFIX
                            + "com/wiki/1"
                            + "\","
                            + "\"color\":\"green\",\"hoverEvent\":{\"action\":\"show_text\",\"value\":"
                            + "{\"text\":\""
                            + GCCoreUtil.translate("gui.message.clicklink")
                            + "\",\"color\":\"yellow\"}},"
                            + "\"clickEvent\":{\"action\":\"open_url\",\"value\":\""
                            + "http://wiki."
                            + GalacticraftCore.PREFIX
                            + "com/wiki/1"
                            + "\"}}]"));
                player.addChatMessage(
                    new ChatComponentText(GCCoreUtil.translate("gui.message.help1a") + EnumColor.AQUA + " /gchelp"));
                break;
            case 4:
            case 5:
            case 6:
                player.addChatMessage(
                    IChatComponent.Serializer.func_150699_a(
                        "[{\"text\":\"" + GCCoreUtil.translate("gui.message.help2")
                            + ": \",\"color\":\"white\"},"
                            + "{\"text\":\" "
                            + EnumColor.BRIGHT_GREEN
                            + "wiki."
                            + GalacticraftCore.PREFIX
                            + "com/wiki/2"
                            + "\","
                            + "\"color\":\"green\",\"hoverEvent\":{\"action\":\"show_text\",\"value\":"
                            + "{\"text\":\""
                            + GCCoreUtil.translate("gui.message.clicklink")
                            + "\",\"color\":\"yellow\"}},"
                            + "\"clickEvent\":{\"action\":\"open_url\",\"value\":\""
                            + "http://wiki."
                            + GalacticraftCore.PREFIX
                            + "com/wiki/2"
                            + "\"}}]"));
                break;
            case 7:
                player.addChatMessage(
                    IChatComponent.Serializer.func_150699_a(
                        "[{\"text\":\"" + GCCoreUtil.translate("gui.message.help3")
                            + ": \",\"color\":\"white\"},"
                            + "{\"text\":\" "
                            + EnumColor.BRIGHT_GREEN
                            + "wiki."
                            + GalacticraftCore.PREFIX
                            + "com/wiki/oil"
                            + "\","
                            + "\"color\":\"green\",\"hoverEvent\":{\"action\":\"show_text\",\"value\":"
                            + "{\"text\":\""
                            + GCCoreUtil.translate("gui.message.clicklink")
                            + "\",\"color\":\"yellow\"}},"
                            + "\"clickEvent\":{\"action\":\"open_url\",\"value\":\""
                            + "http://wiki."
                            + GalacticraftCore.PREFIX
                            + "com/wiki/oil"
                            + "\"}}]"));
                break;
            case 8:
                player.addChatMessage(
                    IChatComponent.Serializer.func_150699_a(
                        "[{\"text\":\"" + GCCoreUtil.translate("gui.message.prelaunch")
                            + ": \",\"color\":\"white\"},"
                            + "{\"text\":\" "
                            + EnumColor.BRIGHT_GREEN
                            + "wiki."
                            + GalacticraftCore.PREFIX
                            + "com/wiki/pre"
                            + "\","
                            + "\"color\":\"green\",\"hoverEvent\":{\"action\":\"show_text\",\"value\":"
                            + "{\"text\":\""
                            + GCCoreUtil.translate("gui.message.clicklink")
                            + "\",\"color\":\"yellow\"}},"
                            + "\"clickEvent\":{\"action\":\"open_url\",\"value\":\""
                            + "http://wiki."
                            + GalacticraftCore.PREFIX
                            + "com/wiki/pre"
                            + "\"}}]"));
                break;
        }
    }
}
