package micdoodle8.mods.galacticraft.core.entities;

import net.minecraft.entity.ai.*;
import net.minecraft.entity.player.*;
import net.minecraft.entity.*;

public class EntityAIThrowPlayer extends EntityAIBase
{
    EntitySkeletonBoss skeletonBoss;
    EntityPlayer targetPlayer;
    
    public EntityAIThrowPlayer(final EntitySkeletonBoss boss) {
        this.skeletonBoss = boss;
        this.setMutexBits(1);
    }
    
    public boolean shouldExecute() {
        final EntityPlayer player = this.skeletonBoss.worldObj.getClosestPlayerToEntity((Entity)this.skeletonBoss, 5.0);
        if (player == null) {
            return false;
        }
        this.targetPlayer = player;
        return true;
    }
    
    public void startExecuting() {
        this.skeletonBoss.setAttackTarget((EntityLivingBase)this.targetPlayer);
        double d0;
        double d2;
        for (d0 = this.skeletonBoss.posX - this.targetPlayer.posX, d2 = this.skeletonBoss.posZ - this.targetPlayer.posZ; d0 * d0 + d2 * d2 < 1.0E-4; d0 = (Math.random() - Math.random()) * 0.01, d2 = (Math.random() - Math.random()) * 0.01) {}
        this.targetPlayer.attackedAtYaw = (float)(Math.atan2(d2, d0) * 180.0 / 3.141592653589793) - this.targetPlayer.rotationYaw;
        this.targetPlayer.knockBack((Entity)this.skeletonBoss, 20.0f, d0, d2);
        super.startExecuting();
    }
}
