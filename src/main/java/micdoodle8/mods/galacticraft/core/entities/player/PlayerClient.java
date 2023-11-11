package micdoodle8.mods.galacticraft.core.entities.player;

import net.minecraft.client.entity.*;
import micdoodle8.mods.galacticraft.core.entities.*;
import net.minecraft.entity.player.*;
import micdoodle8.mods.galacticraft.api.world.*;
import micdoodle8.mods.galacticraft.api.entity.*;
import cpw.mods.fml.client.*;
import micdoodle8.mods.galacticraft.core.tick.*;
import micdoodle8.mods.galacticraft.core.wrappers.*;
import micdoodle8.mods.galacticraft.core.proxy.*;
import micdoodle8.mods.galacticraft.core.*;
import net.minecraft.client.audio.*;
import micdoodle8.mods.galacticraft.core.tile.*;
import micdoodle8.mods.galacticraft.core.dimension.*;
import micdoodle8.mods.galacticraft.core.blocks.*;
import net.minecraft.entity.*;
import micdoodle8.mods.galacticraft.api.vector.*;
import net.minecraft.world.*;
import micdoodle8.mods.galacticraft.core.event.*;
import net.minecraftforge.common.*;
import cpw.mods.fml.common.eventhandler.*;
import micdoodle8.mods.galacticraft.core.network.*;
import micdoodle8.mods.galacticraft.core.util.*;
import net.minecraft.util.*;

public class PlayerClient implements IPlayerClient
{
    private boolean saveSneak;
    private double downMot2;
    public static boolean startup;
    
    public void moveEntity(final EntityPlayerSP player, final double par1, final double par3, final double par5) {
        this.updateFeet(player, par1, par5);
    }
    
    public boolean wakeUpPlayer(final EntityPlayerSP player, final boolean par1, final boolean par2, final boolean par3) {
        return this.wakeUpPlayer(player, par1, par2, par3, false);
    }
    
    public void onUpdate(final EntityPlayerSP player) {
        final GCPlayerStatsClient value;
        final GCPlayerStatsClient stats = value = GCPlayerStatsClient.get(player);
        ++value.tick;
        if (stats.usingParachute && !player.capabilities.isFlying && !player.handleWaterMovement()) {
            player.motionY = -0.5;
            player.motionX *= 0.5;
            player.motionZ *= 0.5;
        }
    }
    
    public boolean isEntityInsideOpaqueBlock(final EntityPlayerSP player, final boolean vanillaInside) {
        if (vanillaInside && GCPlayerStatsClient.get(player).inFreefall) {
            return GCPlayerStatsClient.get(player).inFreefall = false;
        }
        return !(player.ridingEntity instanceof EntityLanderBase) && vanillaInside;
    }
    
    public void onLivingUpdatePre(final EntityPlayerSP player) {
        final GCPlayerStatsClient stats = GCPlayerStatsClient.get(player);
        if (player.worldObj.provider instanceof IGalacticraftWorldProvider) {
            if (!PlayerClient.startup) {
                stats.inFreefallLast = stats.inFreefall;
                stats.inFreefall = FreefallHandler.testFreefall((EntityPlayer)player);
                PlayerClient.startup = true;
            }
            if (player.worldObj.provider instanceof IZeroGDimension) {
                stats.inFreefallLast = stats.inFreefall;
                stats.inFreefall = FreefallHandler.testFreefall((EntityPlayer)player);
                this.downMot2 = stats.downMotionLast;
                stats.downMotionLast = player.motionY;
                stats.freefallHandler.preVanillaMotion(player);
            }
        }
    }
    
    public void onLivingUpdatePost(final EntityPlayerSP player) {
        final GCPlayerStatsClient stats = GCPlayerStatsClient.get(player);
        if (player.worldObj.provider instanceof IZeroGDimension) {
            stats.freefallHandler.postVanillaMotion(player);
            if (stats.inFreefall) {
                player.limbSwing -= player.limbSwingAmount;
                player.limbSwingAmount = player.prevLimbSwingAmount;
                final float adjust = Math.min(Math.abs(player.limbSwing), Math.abs(player.limbSwingAmount) / 3.0f);
                if (player.limbSwing < 0.0f) {
                    player.limbSwing += adjust;
                }
                else if (player.limbSwing > 0.0f) {
                    player.limbSwing -= adjust;
                }
                player.limbSwingAmount *= 0.9f;
            }
            else if (stats.inFreefallLast && this.downMot2 < -0.008) {
                stats.landingTicks = 5 - (int)(Math.min(this.downMot2, stats.downMotionLast) * 40.0);
                if (stats.landingTicks > 15) {
                    if (stats.landingTicks > 19) {
                        stats.pjumpticks = stats.landingTicks - 15 - 5;
                    }
                    stats.landingTicks = 15;
                }
                final float dYmax = 0.3f * stats.landingTicks / 15.0f;
                float factor = 1.0f;
                for (int i = 0; i <= stats.landingTicks; ++i) {
                    stats.landingYOffset[i] = dYmax * MathHelper.sin(i * 3.1415925f / stats.landingTicks) * factor;
                    factor *= 0.97f;
                }
            }
            if (stats.landingTicks > 0) {
                final GCPlayerStatsClient gcPlayerStatsClient = stats;
                --gcPlayerStatsClient.landingTicks;
                player.limbSwing *= 0.8f;
                player.limbSwingAmount = 0.0f;
            }
        }
        else {
            stats.inFreefall = false;
        }
        final boolean ridingThirdPersonEntity = player.ridingEntity instanceof ICameraZoomEntity && ((ICameraZoomEntity)player.ridingEntity).defaultThirdPerson();
        if (ridingThirdPersonEntity && !stats.lastRidingCameraZoomEntity && !ConfigManagerCore.disableVehicleCameraChanges) {
            FMLClientHandler.instance().getClient().gameSettings.thirdPersonView = 1;
        }
        if (player.ridingEntity != null && player.ridingEntity instanceof ICameraZoomEntity) {
            if (!ConfigManagerCore.disableVehicleCameraChanges) {
                stats.lastZoomed = true;
                TickHandlerClient.zoom(((ICameraZoomEntity)player.ridingEntity).getCameraZoom());
            }
        }
        else if (stats.lastZoomed && !ConfigManagerCore.disableVehicleCameraChanges) {
            stats.lastZoomed = false;
            TickHandlerClient.zoom(4.0f);
        }
        stats.lastRidingCameraZoomEntity = ridingThirdPersonEntity;
        if (stats.usingParachute) {
            player.fallDistance = 0.0f;
        }
        final PlayerGearData gearData = ClientProxyCore.playerItemData.get(player.getCommandSenderName());
        stats.usingParachute = false;
        if (gearData != null) {
            stats.usingParachute = (gearData.getParachute() != null);
            if (!GalacticraftCore.isHeightConflictingModInstalled) {
                if (gearData.getMask() >= 0) {
                    player.height = 1.9375f;
                }
                else {
                    player.height = 1.8f;
                }
                player.boundingBox.maxY = player.boundingBox.minY + player.height;
            }
        }
        if (stats.usingParachute && player.onGround) {
            stats.setParachute(false);
            FMLClientHandler.instance().getClient().gameSettings.thirdPersonView = stats.thirdPersonView;
        }
        if (!stats.lastUsingParachute && stats.usingParachute) {
            FMLClientHandler.instance().getClient().getSoundHandler().playSound((ISound)new PositionedSoundRecord(new ResourceLocation(GalacticraftCore.TEXTURE_PREFIX + "player.parachute"), 0.95f + player.getRNG().nextFloat() * 0.1f, 1.0f, (float)player.posX, (float)player.posY, (float)player.posZ));
        }
        stats.lastUsingParachute = stats.usingParachute;
        stats.lastOnGround = player.onGround;
    }
    
    public float getBedOrientationInDegrees(final EntityPlayerSP player, final float vanillaDegrees) {
        if (player.playerLocation != null) {
            final int x = player.playerLocation.posX;
            final int y = player.playerLocation.posY;
            final int z = player.playerLocation.posZ;
            if (!(player.worldObj.getTileEntity(x, y, z) instanceof TileEntityAdvanced)) {
                return vanillaDegrees;
            }
            switch (player.worldObj.getBlockMetadata(x, y, z) - 4) {
                case 0: {
                    return 90.0f;
                }
                case 1: {
                    return 270.0f;
                }
                case 2: {
                    return 180.0f;
                }
                case 3: {
                    return 0.0f;
                }
            }
        }
        return vanillaDegrees;
    }
    
    private void updateFeet(final EntityPlayerSP player, final double motionX, final double motionZ) {
        final GCPlayerStatsClient stats = GCPlayerStatsClient.get(player);
        final double motionSqrd = motionX * motionX + motionZ * motionZ;
        if (motionSqrd > 0.001 && player.worldObj != null && player.worldObj.provider instanceof WorldProviderMoon && player.ridingEntity == null && !player.capabilities.isFlying) {
            final int iPosX = (int)Math.floor(player.posX);
            final int iPosY = (int)Math.floor(player.posY - 2.0);
            final int iPosZ = (int)Math.floor(player.posZ);
            if (player.worldObj.getBlock(iPosX, iPosY, iPosZ) == GCBlocks.blockMoon && player.worldObj.getBlockMetadata(iPosX, iPosY, iPosZ) == 5) {
                if (stats.distanceSinceLastStep > 0.35) {
                    Vector3 pos = new Vector3((Entity)player);
                    pos.y = MathHelper.floor_double(player.posY - 1.0) + player.getRNG().nextFloat() / 100.0f;
                    switch (stats.lastStep) {
                        case 0: {
                            pos.translate(new Vector3(Math.sin(Math.toRadians(-player.rotationYaw + 90.0f)) * 0.25, 0.0, Math.cos(Math.toRadians(-player.rotationYaw + 90.0f)) * 0.25));
                            break;
                        }
                        case 1: {
                            pos.translate(new Vector3(Math.sin(Math.toRadians(-player.rotationYaw - 90.0f)) * 0.25, 0.0, Math.cos(Math.toRadians(-player.rotationYaw - 90.0f)) * 0.25));
                            break;
                        }
                    }
                    pos = WorldUtil.getFootprintPosition(player.worldObj, player.rotationYaw - 180.0f, pos, new BlockVec3((Entity)player));
                    final long chunkKey = ChunkCoordIntPair.chunkXZ2Int(pos.intX() >> 4, pos.intZ() >> 4);
                    ClientProxyCore.footprintRenderer.addFootprint(chunkKey, player.worldObj.provider.dimensionId, pos, player.rotationYaw, player.getCommandSenderName());
                    final GCPlayerStatsClient gcPlayerStatsClient = stats;
                    ++gcPlayerStatsClient.lastStep;
                    final GCPlayerStatsClient gcPlayerStatsClient2 = stats;
                    gcPlayerStatsClient2.lastStep %= 2;
                    stats.distanceSinceLastStep = 0.0;
                }
                else {
                    final GCPlayerStatsClient gcPlayerStatsClient3 = stats;
                    gcPlayerStatsClient3.distanceSinceLastStep += motionSqrd;
                }
            }
        }
    }
    
    public boolean wakeUpPlayer(final EntityPlayerSP player, final boolean par1, final boolean par2, final boolean par3, final boolean bypass) {
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
    
    public void onBuild(final int i, final EntityPlayerSP player) {
        final GCPlayerStatsClient stats = GCPlayerStatsClient.get(player);
        int flag = stats.buildFlags;
        if (flag == -1) {
            flag = 0;
        }
        int repeatCount = flag >> 9;
        if (repeatCount <= 3) {
            ++repeatCount;
        }
        if ((flag & 1 << i) > 0) {
            return;
        }
        flag |= 1 << i;
        stats.buildFlags = (flag & 0x1FF) + (repeatCount << 9);
        GalacticraftCore.packetPipeline.sendToServer(new PacketSimple(PacketSimple.EnumSimplePacket.S_BUILDFLAGS_UPDATE, new Object[] { stats.buildFlags }));
        switch (i) {
            case 0:
            case 1:
            case 2:
            case 3: {
                player.addChatMessage(IChatComponent.Serializer.func_150699_a("[{\"text\":\"" + GCCoreUtil.translate("gui.message.help1") + ": \",\"color\":\"white\"},{\"text\":\" " + EnumColor.BRIGHT_GREEN + "wiki." + GalacticraftCore.PREFIX + "com/wiki/1\",\"color\":\"green\",\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\"" + GCCoreUtil.translate("gui.message.clicklink") + "\",\"color\":\"yellow\"}},\"clickEvent\":{\"action\":\"open_url\",\"value\":\"http://wiki." + GalacticraftCore.PREFIX + "com/wiki/1\"}}]"));
                player.addChatMessage((IChatComponent)new ChatComponentText(GCCoreUtil.translate("gui.message.help1a") + EnumColor.AQUA + " /gchelp"));
                break;
            }
            case 4:
            case 5:
            case 6: {
                player.addChatMessage(IChatComponent.Serializer.func_150699_a("[{\"text\":\"" + GCCoreUtil.translate("gui.message.help2") + ": \",\"color\":\"white\"},{\"text\":\" " + EnumColor.BRIGHT_GREEN + "wiki." + GalacticraftCore.PREFIX + "com/wiki/2\",\"color\":\"green\",\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\"" + GCCoreUtil.translate("gui.message.clicklink") + "\",\"color\":\"yellow\"}},\"clickEvent\":{\"action\":\"open_url\",\"value\":\"http://wiki." + GalacticraftCore.PREFIX + "com/wiki/2\"}}]"));
                break;
            }
            case 7: {
                player.addChatMessage(IChatComponent.Serializer.func_150699_a("[{\"text\":\"" + GCCoreUtil.translate("gui.message.help3") + ": \",\"color\":\"white\"},{\"text\":\" " + EnumColor.BRIGHT_GREEN + "wiki." + GalacticraftCore.PREFIX + "com/wiki/oil\",\"color\":\"green\",\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\"" + GCCoreUtil.translate("gui.message.clicklink") + "\",\"color\":\"yellow\"}},\"clickEvent\":{\"action\":\"open_url\",\"value\":\"http://wiki." + GalacticraftCore.PREFIX + "com/wiki/oil\"}}]"));
                break;
            }
            case 8: {
                player.addChatMessage(IChatComponent.Serializer.func_150699_a("[{\"text\":\"" + GCCoreUtil.translate("gui.message.prelaunch") + ": \",\"color\":\"white\"},{\"text\":\" " + EnumColor.BRIGHT_GREEN + "wiki." + GalacticraftCore.PREFIX + "com/wiki/pre\",\"color\":\"green\",\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\"" + GCCoreUtil.translate("gui.message.clicklink") + "\",\"color\":\"yellow\"}},\"clickEvent\":{\"action\":\"open_url\",\"value\":\"http://wiki." + GalacticraftCore.PREFIX + "com/wiki/pre\"}}]"));
                break;
            }
        }
    }
}
