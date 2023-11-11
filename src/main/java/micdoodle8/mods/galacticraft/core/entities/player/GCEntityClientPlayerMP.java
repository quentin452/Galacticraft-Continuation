package micdoodle8.mods.galacticraft.core.entities.player;

import net.minecraft.client.*;
import net.minecraft.client.network.*;
import net.minecraft.stats.*;
import micdoodle8.mods.galacticraft.core.proxy.*;
import net.minecraft.client.entity.*;
import net.minecraft.client.gui.*;
import net.minecraft.client.audio.*;
import net.minecraft.potion.*;
import net.minecraft.world.*;
import net.minecraft.entity.player.*;
import cpw.mods.fml.common.*;
import net.minecraft.util.*;
import java.util.*;
import micdoodle8.mods.galacticraft.api.event.*;
import net.minecraft.entity.*;
import net.minecraftforge.common.*;
import cpw.mods.fml.common.eventhandler.*;
import cpw.mods.fml.relauncher.*;
import micdoodle8.mods.galacticraft.api.world.*;

public class GCEntityClientPlayerMP extends EntityClientPlayerMP
{
    boolean lastIsFlying;
    int lastLandingTicks;

    public GCEntityClientPlayerMP(final Minecraft minecraft, final World world, final Session session, final NetHandlerPlayClient netHandler, final StatFileWriter statFileWriter) {
        super(minecraft, world, session, netHandler, statFileWriter);
    }

    public void wakeUpPlayer(final boolean par1, final boolean par2, final boolean par3) {
        if (!ClientProxyCore.playerClientHandler.wakeUpPlayer((EntityPlayerSP)this, par1, par2, par3)) {
            super.wakeUpPlayer(par1, par2, par3);
        }
    }

    public boolean isEntityInsideOpaqueBlock() {
        return ClientProxyCore.playerClientHandler.isEntityInsideOpaqueBlock((EntityPlayerSP)this, super.isEntityInsideOpaqueBlock());
    }

    public void onLivingUpdate() {
        ClientProxyCore.playerClientHandler.onLivingUpdatePre((EntityPlayerSP)this);
        try {
            if (this.worldObj.provider instanceof IZeroGDimension) {
                if (this.sprintingTicksLeft > 0) {
                    --this.sprintingTicksLeft;
                    if (this.sprintingTicksLeft == 0) {
                        this.setSprinting(false);
                    }
                }
                if (this.sprintToggleTimer > 0) {
                    --this.sprintToggleTimer;
                }
                this.prevTimeInPortal = this.timeInPortal;
                if (this.inPortal) {
                    if (this.mc.currentScreen != null) {
                        this.mc.displayGuiScreen((GuiScreen)null);
                    }
                    if (this.timeInPortal == 0.0f) {
                        this.mc.getSoundHandler().playSound((ISound)PositionedSoundRecord.func_147674_a(new ResourceLocation("portal.trigger"), this.rand.nextFloat() * 0.4f + 0.8f));
                    }
                    this.timeInPortal += 0.0125f;
                    if (this.timeInPortal >= 1.0f) {
                        this.timeInPortal = 1.0f;
                    }
                    this.inPortal = false;
                }
                else if (this.isPotionActive(Potion.confusion) && this.getActivePotionEffect(Potion.confusion).getDuration() > 60) {
                    this.timeInPortal += 0.006666667f;
                    if (this.timeInPortal > 1.0f) {
                        this.timeInPortal = 1.0f;
                    }
                }
                else {
                    if (this.timeInPortal > 0.0f) {
                        this.timeInPortal -= 0.05f;
                    }
                    if (this.timeInPortal < 0.0f) {
                        this.timeInPortal = 0.0f;
                    }
                }
                if (this.timeUntilPortal > 0) {
                    --this.timeUntilPortal;
                }
                final boolean flag = this.movementInput.jump;
                final float ff = 0.8f;
                final boolean flag2 = this.movementInput.moveForward >= ff;
                this.movementInput.updatePlayerMoveState();
                if (this.isUsingItem() && !this.isRiding()) {
                    final MovementInput movementInput = this.movementInput;
                    movementInput.moveStrafe *= 0.2f;
                    final MovementInput movementInput2 = this.movementInput;
                    movementInput2.moveForward *= 0.2f;
                    this.sprintToggleTimer = 0;
                }
                final GCPlayerStatsClient stats = GCPlayerStatsClient.get((EntityPlayerSP)this);
                if (stats.landingTicks > 0) {
                    this.ySize = stats.landingYOffset[stats.landingTicks];
                    final MovementInput movementInput3 = this.movementInput;
                    movementInput3.moveStrafe *= 0.5f;
                    final MovementInput movementInput4 = this.movementInput;
                    movementInput4.moveForward *= 0.5f;
                    if (this.movementInput.sneak && this.ySize < 0.2f) {
                        this.ySize = 0.2f;
                    }
                }
                else if (stats.pjumpticks > 0) {
                    this.ySize = 0.01f * stats.pjumpticks;
                }
                else if (this.movementInput.sneak && this.ySize < 0.2f && this.onGround && !stats.inFreefall) {
                    this.ySize = 0.2f;
                }
                this.func_145771_j(this.posX - this.width * 0.35, this.boundingBox.minY + 0.5, this.posZ + this.width * 0.35);
                this.func_145771_j(this.posX - this.width * 0.35, this.boundingBox.minY + 0.5, this.posZ - this.width * 0.35);
                this.func_145771_j(this.posX + this.width * 0.35, this.boundingBox.minY + 0.5, this.posZ - this.width * 0.35);
                this.func_145771_j(this.posX + this.width * 0.35, this.boundingBox.minY + 0.5, this.posZ + this.width * 0.35);
                final boolean flag3 = this.getFoodStats().getFoodLevel() > 6.0f || this.capabilities.allowFlying;
                if (this.onGround && !flag2 && this.movementInput.moveForward >= ff && !this.isSprinting() && flag3 && !this.isUsingItem() && !this.isPotionActive(Potion.blindness)) {
                    if (this.sprintToggleTimer <= 0 && !this.mc.gameSettings.keyBindSprint.getIsKeyPressed()) {
                        this.sprintToggleTimer = 7;
                    }
                    else {
                        this.setSprinting(true);
                    }
                }
                if (!this.isSprinting() && this.movementInput.moveForward >= ff && flag3 && !this.isUsingItem() && !this.isPotionActive(Potion.blindness) && this.mc.gameSettings.keyBindSprint.getIsKeyPressed()) {
                    this.setSprinting(true);
                }
                if (this.isSprinting() && (this.movementInput.moveForward < ff || this.isCollidedHorizontally || !flag3)) {
                    this.setSprinting(false);
                }
                if (this.capabilities.isFlying) {
                    if (this.movementInput.sneak) {
                        this.motionY -= 0.15;
                    }
                    if (this.movementInput.jump) {
                        this.motionY += 0.15;
                    }
                }
                if (this.worldObj.difficultySetting == EnumDifficulty.PEACEFUL && this.getHealth() < this.getMaxHealth() && this.worldObj.getGameRules().getGameRuleBooleanValue("naturalRegeneration") && this.ticksExisted % 20 * 12 == 0) {
                    this.heal(1.0f);
                }
                this.inventory.decrementAnimations();
                this.prevCameraYaw = this.cameraYaw;
                if (this.newPosRotationIncrements > 0) {
                    final double d0 = this.posX + (this.newPosX - this.posX) / this.newPosRotationIncrements;
                    final double d2 = this.posY + (this.newPosY - this.posY) / this.newPosRotationIncrements;
                    final double d3 = this.posZ + (this.newPosZ - this.posZ) / this.newPosRotationIncrements;
                    final double d4 = MathHelper.wrapAngleTo180_double(this.newRotationYaw - this.rotationYaw);
                    this.rotationYaw += (float)(d4 / this.newPosRotationIncrements);
                    this.rotationPitch += (float)((this.newRotationPitch - this.rotationPitch) / this.newPosRotationIncrements);
                    --this.newPosRotationIncrements;
                    this.setPosition(d0, d2, d3);
                    this.setRotation(this.rotationYaw, this.rotationPitch);
                }
                if (Math.abs(this.motionX) < 0.005) {
                    this.motionX = 0.0;
                }
                if (Math.abs(this.motionY) < 0.005) {
                    this.motionY = 0.0;
                }
                if (Math.abs(this.motionZ) < 0.005) {
                    this.motionZ = 0.0;
                }
                this.updateEntityActionState();
                this.rotationYawHead = this.rotationYaw;
                if (this.isMovementBlocked()) {
                    this.isJumping = false;
                    this.moveStrafing = 0.0f;
                    this.moveForward = 0.0f;
                    this.randomYawVelocity = 0.0f;
                }
                this.moveStrafing *= 0.98f;
                this.moveForward *= 0.98f;
                this.randomYawVelocity *= 0.9f;
                if (this.boundingBox.minY % 1.0 == 0.5) {
                    final AxisAlignedBB boundingBox = this.boundingBox;
                    boundingBox.minY += 9.999999747378752E-6;
                }
                this.moveEntityWithHeading(this.moveStrafing, this.moveForward);
                float f = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
                float f2 = (float)Math.atan(-this.motionY * 0.20000000298023224) * 15.0f;
                if (f > 0.1f) {
                    f = 0.1f;
                }
                if (!this.onGround || this.getHealth() <= 0.0f) {
                    f = 0.0f;
                }
                if (this.onGround || this.getHealth() <= 0.0f) {
                    f2 = 0.0f;
                }
                this.cameraYaw += (f - this.cameraYaw) * 0.4f;
                this.cameraPitch += (f2 - this.cameraPitch) * 0.8f;
                if (this.getHealth() > 0.0f) {
                    AxisAlignedBB axisalignedbb = null;
                    if (this.ridingEntity != null && !this.ridingEntity.isDead) {
                        axisalignedbb = this.boundingBox.func_111270_a(this.ridingEntity.boundingBox).expand(1.0, 0.0, 1.0);
                    }
                    else {
                        axisalignedbb = this.boundingBox.expand(1.0, 0.5, 1.0);
                    }
                    final List list = this.worldObj.getEntitiesWithinAABBExcludingEntity((Entity)this, axisalignedbb);
                    if (list != null) {
                        for (int i = 0; i < list.size(); ++i) {
                            final Entity entity = (Entity) list.get(i);
                            if (!entity.isDead) {
                                entity.onCollideWithPlayer((EntityPlayer)this);
                            }
                        }
                    }
                }
                if (this.lastIsFlying != this.capabilities.isFlying) {
                    this.lastIsFlying = this.capabilities.isFlying;
                    this.sendPlayerAbilities();
                }
            }
            else {
                super.onLivingUpdate();
            }
        }
        catch (RuntimeException e) {
            FMLLog.severe("A mod has crashed while Minecraft was doing a normal player tick update.  See details below.  GCEntityClientPlayerMP is in this because that is the player class name when Galacticraft is installed.  This is =*NOT*= a bug in Galacticraft, please report it to the mod indicated by the first lines of the crash report.", new Object[0]);
            throw e;
        }
        ClientProxyCore.playerClientHandler.onLivingUpdatePost((EntityPlayerSP)this);
    }

    public void moveEntity(final double par1, final double par3, final double par5) {
        super.moveEntity(par1, par3, par5);
        ClientProxyCore.playerClientHandler.moveEntity((EntityPlayerSP)this, par1, par3, par5);
    }

    public void onUpdate() {
        ClientProxyCore.playerClientHandler.onUpdate((EntityPlayerSP)this);
        super.onUpdate();
    }

    public boolean isSneaking() {
        if (this.worldObj.provider instanceof IZeroGDimension) {
            final ZeroGravityEvent zeroGEvent = (ZeroGravityEvent)new ZeroGravityEvent.SneakOverride((EntityLivingBase)this);
            MinecraftForge.EVENT_BUS.post((Event)zeroGEvent);
            if (zeroGEvent.isCanceled()) {
                return super.isSneaking();
            }
            final GCPlayerStatsClient stats = GCPlayerStatsClient.get((EntityPlayerSP)this);
            if (stats.landingTicks > 0) {
                if (this.lastLandingTicks == 0) {
                    this.lastLandingTicks = stats.landingTicks;
                }
                return stats.landingTicks < this.lastLandingTicks;
            }
            this.lastLandingTicks = 0;
            if (stats.pjumpticks > 0) {
                return true;
            }
            if (ClientProxyCore.sneakRenderOverride) {
                if (FreefallHandler.testFreefall((EntityPlayer)this)) {
                    return false;
                }
                if (stats.inFreefall) {
                    return false;
                }
            }
        }
        return super.isSneaking();
    }

    @SideOnly(Side.CLIENT)
    public float getBedOrientationInDegrees() {
        return ClientProxyCore.playerClientHandler.getBedOrientationInDegrees((EntityPlayerSP)this, super.getBedOrientationInDegrees());
    }

    public void setInPortal() {
        if (!(this.worldObj.provider instanceof IGalacticraftWorldProvider)) {
            super.setInPortal();
        }
    }
}
