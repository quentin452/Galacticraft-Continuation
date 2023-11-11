package micdoodle8.mods.galacticraft.core.entities.player;

import net.minecraft.entity.player.*;
import micdoodle8.mods.galacticraft.api.event.*;
import net.minecraftforge.common.*;
import cpw.mods.fml.common.eventhandler.*;
import net.minecraft.block.material.*;
import net.minecraft.block.*;
import net.minecraft.util.*;
import net.minecraft.client.entity.*;
import micdoodle8.mods.galacticraft.api.world.*;
import micdoodle8.mods.galacticraft.api.prefab.entity.*;
import micdoodle8.mods.galacticraft.core.entities.*;
import net.minecraft.init.*;
import micdoodle8.mods.galacticraft.core.blocks.*;
import net.minecraft.world.*;
import net.minecraft.entity.*;
import micdoodle8.mods.galacticraft.core.dimension.*;
import cpw.mods.fml.relauncher.*;
import micdoodle8.mods.galacticraft.core.util.*;

public class FreefallHandler
{
    private static double pPrevMotionX;
    public static double pPrevMotionY;
    private static double pPrevMotionZ;
    private static float jetpackBoost;
    private static double pPrevdY;
    public static boolean sneakLast;
    private GCPlayerStatsClient stats;
    
    public FreefallHandler(final GCPlayerStatsClient statsClient) {
        this.stats = statsClient;
    }
    
    public static boolean testFreefall(final EntityPlayer player) {
        final ZeroGravityEvent zeroGEvent = (ZeroGravityEvent)new ZeroGravityEvent.InFreefall((EntityLivingBase)player);
        MinecraftForge.EVENT_BUS.post((Event)zeroGEvent);
        if (zeroGEvent.isCanceled()) {
            return false;
        }
        final int playerFeetOnY = (int)(player.boundingBox.minY - 0.01);
        final int xx = MathHelper.floor_double(player.posX);
        final int zz = MathHelper.floor_double(player.posZ);
        final Block b = player.worldObj.getBlock(xx, playerFeetOnY, zz);
        if (b.getMaterial() != Material.air && !(b instanceof BlockLiquid)) {
            final double blockYmax = playerFeetOnY + b.getBlockBoundsMaxY();
            if (player.boundingBox.minY - blockYmax < 0.01 && player.boundingBox.minY - blockYmax > -0.5) {
                player.onGround = true;
                if (player.boundingBox.minY - blockYmax > 0.0) {
                    player.posY -= player.boundingBox.minY - blockYmax;
                    player.boundingBox.offset(0.0, blockYmax - player.boundingBox.minY, 0.0);
                }
                else if (b.canCollideCheck(player.worldObj.getBlockMetadata(xx, playerFeetOnY, zz), false)) {
                    final AxisAlignedBB collisionBox = b.getCollisionBoundingBoxFromPool(player.worldObj, xx, playerFeetOnY, zz);
                    if (collisionBox != null && collisionBox.intersectsWith(player.boundingBox)) {
                        player.posY -= player.boundingBox.minY - blockYmax;
                        player.boundingBox.offset(0.0, blockYmax - player.boundingBox.minY, 0.0);
                    }
                }
                return false;
            }
        }
        return true;
    }
    
    @SideOnly(Side.CLIENT)
    private boolean testFreefall(final EntityPlayerSP p, final boolean flag) {
        final World world = p.worldObj;
        final WorldProvider worldProvider = world.provider;
        if (!(worldProvider instanceof IZeroGDimension)) {
            return false;
        }
        final ZeroGravityEvent zeroGEvent = (ZeroGravityEvent)new ZeroGravityEvent.InFreefall((EntityLivingBase)p);
        MinecraftForge.EVENT_BUS.post((Event)zeroGEvent);
        if (zeroGEvent.isCanceled()) {
            return false;
        }
        if (this.stats.pjumpticks > 0 || (this.stats.pWasOnGround && p.movementInput.jump)) {
            return false;
        }
        if (p.ridingEntity != null) {
            final Entity e = p.ridingEntity;
            if (e instanceof EntitySpaceshipBase) {
                return ((EntitySpaceshipBase)e).getLaunched();
            }
            if (e instanceof EntityLanderBase) {
                return false;
            }
        }
        if (!flag) {
            return false;
        }
        final float rY = p.rotationYaw % 360.0f;
        double zreach = 0.0;
        double xreach = 0.0;
        if (rY < 80.0f || rY > 280.0f) {
            zreach = 0.2;
        }
        if (rY < 170.0f && rY > 10.0f) {
            xreach = 0.2;
        }
        if (rY < 260.0f && rY > 100.0f) {
            zreach = -0.2;
        }
        if (rY < 350.0f && rY > 190.0f) {
            xreach = -0.2;
        }
        final AxisAlignedBB playerReach = p.boundingBox.addCoord(xreach, 0.0, zreach);
        boolean checkBlockWithinReach;
        if (worldProvider instanceof WorldProviderSpaceStation) {
            final SpinManager spinManager = ((WorldProviderSpaceStation)worldProvider).getSpinManager();
            checkBlockWithinReach = (playerReach.maxX >= spinManager.ssBoundsMinX && playerReach.minX <= spinManager.ssBoundsMaxX && playerReach.maxY >= spinManager.ssBoundsMinY && playerReach.minY <= spinManager.ssBoundsMaxY && playerReach.maxZ >= spinManager.ssBoundsMinZ && playerReach.minZ <= spinManager.ssBoundsMaxZ);
        }
        else {
            checkBlockWithinReach = true;
        }
        if (checkBlockWithinReach) {
            final int xm = MathHelper.floor_double(playerReach.minX);
            final int xx = MathHelper.floor_double(playerReach.maxX);
            final int ym = MathHelper.floor_double(playerReach.minY);
            final int yy = MathHelper.floor_double(playerReach.maxY);
            final int zm = MathHelper.floor_double(playerReach.minZ);
            final int zz = MathHelper.floor_double(playerReach.maxZ);
            for (int x = xm; x <= xx; ++x) {
                for (int y = ym; y <= yy; ++y) {
                    for (int z = zm; z <= zz; ++z) {
                        final Block b = world.getBlock(x, y, z);
                        if (Blocks.air != b && GCBlocks.brightAir != b) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }
    
    @SideOnly(Side.CLIENT)
    public static void setupFreefallPre(final EntityPlayerSP p) {
        final double dY = p.motionY - FreefallHandler.pPrevMotionY;
        FreefallHandler.jetpackBoost = 0.0f;
        FreefallHandler.pPrevdY = dY;
        FreefallHandler.pPrevMotionX = p.motionX;
        FreefallHandler.pPrevMotionY = p.motionY;
        FreefallHandler.pPrevMotionZ = p.motionZ;
    }
    
    @SideOnly(Side.CLIENT)
    public static void freefallMotion(final EntityPlayerSP p) {
        boolean jetpackUsed = false;
        final double dX = p.motionX - FreefallHandler.pPrevMotionX;
        final double dY = p.motionY - FreefallHandler.pPrevMotionY;
        final double dZ = p.motionZ - FreefallHandler.pPrevMotionZ;
        final double posOffsetX = -p.motionX;
        double posOffsetY = -p.motionY;
        if (posOffsetY == -WorldUtil.getGravityForEntity((Entity)p)) {
            posOffsetY = 0.0;
        }
        final double posOffsetZ = -p.motionZ;
        if (dY < 0.0 && p.motionY != 0.0) {
            p.motionY = FreefallHandler.pPrevMotionY;
        }
        else if (dY > 0.01 && GCPlayerStatsClient.get(p).inFreefallLast) {
            if (dX < 0.01 && dZ < 0.01) {
                final float pitch = p.rotationPitch / 57.29578f;
                FreefallHandler.jetpackBoost = (float)dY * MathHelper.cos(pitch) * 0.1f;
                final float factor = 1.0f + MathHelper.sin(pitch) / 5.0f;
                p.motionY -= dY * factor;
                jetpackUsed = true;
            }
            else {
                p.motionY -= dY / 2.0;
            }
        }
        p.motionX -= dX;
        p.motionZ -= dZ;
        if (p.movementInput.moveForward != 0.0f) {
            p.motionX -= p.movementInput.moveForward * MathHelper.sin(p.rotationYaw / 57.29578f) / (ConfigManagerCore.hardMode ? 600.0f : 200.0f);
            p.motionZ += p.movementInput.moveForward * MathHelper.cos(p.rotationYaw / 57.29578f) / (ConfigManagerCore.hardMode ? 600.0f : 200.0f);
        }
        if (FreefallHandler.jetpackBoost != 0.0f) {
            p.motionX -= FreefallHandler.jetpackBoost * MathHelper.sin(p.rotationYaw / 57.29578f);
            p.motionZ += FreefallHandler.jetpackBoost * MathHelper.cos(p.rotationYaw / 57.29578f);
        }
        if (p.movementInput.sneak) {
            if (!FreefallHandler.sneakLast) {
                FreefallHandler.sneakLast = true;
            }
            p.motionY -= (ConfigManagerCore.hardMode ? 0.002 : 0.0032);
        }
        else if (FreefallHandler.sneakLast) {
            FreefallHandler.sneakLast = false;
        }
        if (!jetpackUsed && p.movementInput.jump) {
            p.motionY += (ConfigManagerCore.hardMode ? 0.002 : 0.0032);
        }
        final float speedLimit = ConfigManagerCore.hardMode ? 0.9f : 0.7f;
        if (p.motionX > speedLimit) {
            p.motionX = speedLimit;
        }
        if (p.motionX < -speedLimit) {
            p.motionX = -speedLimit;
        }
        if (p.motionY > speedLimit) {
            p.motionY = speedLimit;
        }
        if (p.motionY < -speedLimit) {
            p.motionY = -speedLimit;
        }
        if (p.motionZ > speedLimit) {
            p.motionZ = speedLimit;
        }
        if (p.motionZ < -speedLimit) {
            p.motionZ = -speedLimit;
        }
        FreefallHandler.pPrevMotionX = p.motionX;
        FreefallHandler.pPrevMotionY = p.motionY;
        FreefallHandler.pPrevMotionZ = p.motionZ;
        p.moveEntity(p.motionX + posOffsetX, p.motionY + posOffsetY, p.motionZ + posOffsetZ);
    }
    
    public static void updateFreefall(final EntityPlayer p) {
        FreefallHandler.pPrevMotionX = p.motionX;
        FreefallHandler.pPrevMotionY = p.motionY;
        FreefallHandler.pPrevMotionZ = p.motionZ;
    }
    
    @SideOnly(Side.CLIENT)
    public void preVanillaMotion(final EntityPlayerSP p) {
        setupFreefallPre(p);
        this.stats.pWasOnGround = p.onGround;
    }
    
    @SideOnly(Side.CLIENT)
    public void postVanillaMotion(final EntityPlayerSP p) {
        final World world = p.worldObj;
        final WorldProvider worldProvider = world.provider;
        if (!(worldProvider instanceof IZeroGDimension)) {
            return;
        }
        final ZeroGravityEvent zeroGEvent = (ZeroGravityEvent)new ZeroGravityEvent.Motion((EntityLivingBase)p);
        MinecraftForge.EVENT_BUS.post((Event)zeroGEvent);
        if (zeroGEvent.isCanceled()) {
            return;
        }
        boolean freefall = this.stats.inFreefall;
        if (freefall) {
            p.ySize = 0.0f;
        }
        freefall = this.testFreefall(p, freefall);
        this.stats.inFreefall = freefall;
        this.stats.inFreefallFirstCheck = true;
        SpinManager spinManager = null;
        if (worldProvider instanceof WorldProviderSpaceStation) {
            spinManager = ((WorldProviderSpaceStation)worldProvider).getSpinManager();
        }
        boolean doCentrifugal = spinManager != null;
        if (freefall) {
            this.stats.pjumpticks = 0;
            p.motionX /= 0.9100000262260437;
            p.motionZ /= 0.9100000262260437;
            p.motionY /= 0.9800000190734863;
            if (spinManager != null) {
                doCentrifugal = spinManager.updatePlayerForSpin(p, 1.0f);
            }
            if (!p.capabilities.isCreativeMode) {
                freefallMotion(p);
            }
            else {
                p.capabilities.isFlying = true;
                final double dx = p.motionX - FreefallHandler.pPrevMotionX;
                final double dy = p.motionY - FreefallHandler.pPrevMotionY;
                final double dz = p.motionZ - FreefallHandler.pPrevMotionZ;
                p.motionX -= dx / 2.0;
                p.motionY -= dy / 2.0;
                p.motionZ -= dz / 2.0;
                if (p.motionX > 1.2000000476837158) {
                    p.motionX = 1.2000000476837158;
                }
                if (p.motionX < -1.2000000476837158) {
                    p.motionX = -1.2000000476837158;
                }
                if (p.motionY > 0.699999988079071) {
                    p.motionY = 0.699999988079071;
                }
                if (p.motionY < -0.699999988079071) {
                    p.motionY = -0.699999988079071;
                }
                if (p.motionZ > 1.2000000476837158) {
                    p.motionZ = 1.2000000476837158;
                }
                if (p.motionZ < -1.2000000476837158) {
                    p.motionZ = -1.2000000476837158;
                }
            }
        }
        else {
            final double dy2 = p.motionY - FreefallHandler.pPrevMotionY;
            if (p.movementInput.jump) {
                if ((p.onGround || this.stats.pWasOnGround) && !p.capabilities.isCreativeMode) {
                    if (this.stats.pjumpticks < 25) {
                        final GCPlayerStatsClient stats = this.stats;
                        ++stats.pjumpticks;
                    }
                    p.motionY -= dy2;
                }
                else {
                    p.motionY += 0.015;
                    if (this.stats.pjumpticks == 0) {
                        p.motionY -= dy2;
                    }
                }
            }
            else if (this.stats.pjumpticks > 0) {
                p.motionY += 0.0145 * this.stats.pjumpticks;
                this.stats.pjumpticks = 0;
            }
            else if (p.movementInput.sneak) {
                if (!p.onGround) {
                    p.motionY -= 0.015;
                }
                this.stats.pjumpticks = 0;
            }
        }
        if (doCentrifugal && !p.onGround) {
            spinManager.applyCentrifugalForce(p);
        }
        FreefallHandler.pPrevMotionX = p.motionX;
        FreefallHandler.pPrevMotionY = p.motionY;
        FreefallHandler.pPrevMotionZ = p.motionZ;
    }
}
