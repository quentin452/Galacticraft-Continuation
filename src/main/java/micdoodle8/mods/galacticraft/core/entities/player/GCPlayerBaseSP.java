package micdoodle8.mods.galacticraft.core.entities.player;

import api.player.client.*;
import micdoodle8.mods.galacticraft.core.proxy.*;
import micdoodle8.mods.galacticraft.api.world.*;
import net.minecraft.util.*;
import cpw.mods.fml.common.*;
import micdoodle8.mods.galacticraft.core.util.*;
import net.minecraft.entity.*;
import net.minecraft.client.entity.*;
import net.minecraft.entity.player.*;

public class GCPlayerBaseSP extends ClientPlayerBase
{
    boolean lastIsFlying;
    int lastLandingTicks;
    
    public GCPlayerBaseSP(final ClientPlayerAPI playerAPI) {
        super(playerAPI);
    }
    
    private IPlayerClient getClientHandler() {
        return ClientProxyCore.playerClientHandler;
    }
    
    public boolean isEntityInsideOpaqueBlock() {
        return this.getClientHandler().isEntityInsideOpaqueBlock(this.player, super.isEntityInsideOpaqueBlock());
    }
    
    public void onLivingUpdate() {
        this.getClientHandler().onLivingUpdatePre(this.player);
        super.onLivingUpdate();
        this.getClientHandler().onLivingUpdatePost(this.player);
    }
    
    public void beforeUpdateEntityActionState() {
        if (this.player.worldObj.provider instanceof IZeroGDimension) {
            final GCPlayerStatsClient stats = GCPlayerStatsClient.get(this.player);
            if (stats.landingTicks > 0) {
                this.player.ySize = stats.landingYOffset[stats.landingTicks];
                final MovementInput movementInput = this.player.movementInput;
                movementInput.moveStrafe *= 0.5f;
                final MovementInput movementInput2 = this.player.movementInput;
                movementInput2.moveForward *= 0.5f;
                if (this.player.movementInput.sneak && this.player.ySize < 0.2f) {
                    this.player.ySize = 0.2f;
                }
            }
            else if (stats.pjumpticks > 0) {
                this.player.ySize = 0.01f * stats.pjumpticks;
            }
            else if (!this.player.onGround || stats.inFreefall) {
                this.player.ySize = 0.0f;
            }
        }
    }
    
    public void afterUpdateEntityActionState() {
        if (this.player.worldObj.provider instanceof IZeroGDimension) {
            this.player.setJumping(false);
            if (this.player.boundingBox.minY % 1.0 == 0.5) {
                final AxisAlignedBB boundingBox = this.player.boundingBox;
                boundingBox.minY += 9.999999747378752E-6;
            }
        }
    }
    
    public void moveEntity(final double par1, final double par3, final double par5) {
        super.moveEntity(par1, par3, par5);
        this.getClientHandler().moveEntity(this.player, par1, par3, par5);
    }
    
    public void afterMoveEntityWithHeading(final float paramFloat1, final float paramFloat2) {
        super.afterMoveEntityWithHeading(paramFloat1, paramFloat2);
        if (Loader.isModLoaded("SmartMoving") && !this.player.capabilities.isFlying) {
            final EntityPlayerSP player = this.player;
            player.motionY += 0.08;
            final EntityPlayerSP player2 = this.player;
            player2.motionY -= WorldUtil.getGravityForEntity((Entity)this.player);
        }
    }
    
    public void onUpdate() {
        this.getClientHandler().onUpdate(this.player);
        super.onUpdate();
    }
    
    public boolean isSneaking() {
        if (this.player.worldObj.provider instanceof IZeroGDimension) {
            final GCPlayerStatsClient stats = GCPlayerStatsClient.get(this.player);
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
                if (FreefallHandler.testFreefall((EntityPlayer)this.player)) {
                    return false;
                }
                if (stats.inFreefall) {
                    return false;
                }
            }
        }
        return super.isSneaking();
    }
}
