package micdoodle8.mods.galacticraft.core.entities;

import net.minecraft.entity.ai.*;
import net.minecraft.entity.*;
import net.minecraft.util.*;

public class EntityAIArrowAttack extends EntityAIBase
{
    private final EntityLiving entityHost;
    private final IRangedAttackMob rangedAttackEntityHost;
    private EntityLivingBase attackTarget;
    private int rangedAttackTime;
    private double entityMoveSpeed;
    private int field_96561_g;
    private int maxRangedAttackTime;
    private float field_96562_i;
    private float field_82642_h;
    
    public EntityAIArrowAttack(final IRangedAttackMob par1IRangedAttackMob, final double par2, final int par4, final float par5) {
        this(par1IRangedAttackMob, par2, par4, par4, par5);
    }
    
    public EntityAIArrowAttack(final IRangedAttackMob par1IRangedAttackMob, final double par2, final int par4, final int par5, final float par6) {
        this.rangedAttackTime = -1;
        if (!(par1IRangedAttackMob instanceof EntityLivingBase)) {
            throw new IllegalArgumentException("ArrowAttackGoal requires Mob implements RangedAttackMob");
        }
        this.rangedAttackEntityHost = par1IRangedAttackMob;
        this.entityHost = (EntityLiving)par1IRangedAttackMob;
        this.entityMoveSpeed = par2;
        this.field_96561_g = par4;
        this.maxRangedAttackTime = par5;
        this.field_96562_i = par6;
        this.field_82642_h = par6 * par6;
        this.setMutexBits(3);
    }
    
    public boolean shouldExecute() {
        final EntityLivingBase entitylivingbase = this.entityHost.getAttackTarget();
        if (entitylivingbase == null) {
            return false;
        }
        this.attackTarget = entitylivingbase;
        return true;
    }
    
    public boolean continueExecuting() {
        return this.shouldExecute() || !this.entityHost.getNavigator().noPath();
    }
    
    public void resetTask() {
        this.attackTarget = null;
        this.rangedAttackTime = -1;
    }
    
    public void updateTask() {
        final double d0 = this.entityHost.getDistanceSq(this.attackTarget.posX, this.attackTarget.boundingBox.minY, this.attackTarget.posZ);
        final boolean flag = this.entityHost.getEntitySenses().canSee((Entity)this.attackTarget);
        this.entityHost.getNavigator().tryMoveToEntityLiving((Entity)this.attackTarget, this.entityMoveSpeed);
        this.entityHost.getLookHelper().setLookPositionWithEntity((Entity)this.attackTarget, 30.0f, 30.0f);
        final int rangedAttackTime = this.rangedAttackTime - 1;
        this.rangedAttackTime = rangedAttackTime;
        if (rangedAttackTime == 0) {
            if (d0 > this.field_82642_h || !flag) {
                return;
            }
            float f2;
            final float f = f2 = MathHelper.sqrt_double(d0) / this.field_96562_i;
            if (f < 0.1f) {
                f2 = 0.1f;
            }
            if (f2 > 1.0f) {
                f2 = 1.0f;
            }
            this.rangedAttackEntityHost.attackEntityWithRangedAttack(this.attackTarget, f2);
            this.rangedAttackTime = MathHelper.floor_float(f * (this.maxRangedAttackTime - this.field_96561_g) + this.field_96561_g);
        }
        else if (this.rangedAttackTime < 0) {
            final float f = MathHelper.sqrt_double(d0) / this.field_96562_i;
            this.rangedAttackTime = MathHelper.floor_float(f * (this.maxRangedAttackTime - this.field_96561_g) + this.field_96561_g);
        }
    }
}
