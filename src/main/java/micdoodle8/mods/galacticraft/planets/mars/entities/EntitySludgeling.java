package micdoodle8.mods.galacticraft.planets.mars.entities;

import net.minecraft.entity.monster.*;
import micdoodle8.mods.galacticraft.api.entity.*;
import net.minecraft.world.*;
import net.minecraft.entity.player.*;
import net.minecraft.entity.ai.*;
import micdoodle8.mods.galacticraft.core.entities.*;
import net.minecraft.util.*;
import net.minecraft.block.*;
import net.minecraft.item.*;
import net.minecraft.init.*;
import net.minecraft.entity.*;

public class EntitySludgeling extends EntityMob implements IEntityBreathable
{
    public EntitySludgeling(final World par1World) {
        super(par1World);
        this.setSize(0.3f, 0.2f);
        this.tasks.addTask(1, (EntityAIBase)new EntityAIAttackOnCollide((EntityCreature)this, 0.25, true));
        this.targetTasks.addTask(1, (EntityAIBase)new EntityAINearestAttackableTarget((EntityCreature)this, (Class)EntityPlayer.class, 0, false, true));
        this.targetTasks.addTask(2, (EntityAIBase)new EntityAINearestAttackableTarget((EntityCreature)this, (Class)EntityEvolvedZombie.class, 0, false, true));
        this.targetTasks.addTask(2, (EntityAIBase)new EntityAINearestAttackableTarget((EntityCreature)this, (Class)EntityEvolvedSkeleton.class, 0, false, true));
        this.targetTasks.addTask(2, (EntityAIBase)new EntityAINearestAttackableTarget((EntityCreature)this, (Class)EntityEvolvedSpider.class, 0, false, true));
        this.targetTasks.addTask(2, (EntityAIBase)new EntityAINearestAttackableTarget((EntityCreature)this, (Class)EntityEvolvedCreeper.class, 0, false, true));
        this.targetTasks.addTask(3, (EntityAIBase)new EntityAINearestAttackableTarget((EntityCreature)this, (Class)EntitySlimeling.class, 200, false));
    }

    public boolean canBreatheUnderwater() {
        return true;
    }

    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(7.0);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(1.0);
    }

    public boolean isAIEnabled() {
        return true;
    }

    protected boolean canTriggerWalking() {
        return false;
    }

    protected Entity findPlayerToAttack() {
        final double var1 = 8.0;
        return (Entity)this.worldObj.getClosestVulnerablePlayerToEntity((Entity)this, var1);
    }

    protected String getLivingSound() {
        return "mob.silverfish.say";
    }

    protected String getHurtSound() {
        return "mob.silverfish.hit";
    }

    protected String getDeathSound() {
        return "mob.silverfish.kill";
    }

    public EntityPlayer getClosestEntityToAttack(final double par1, final double par3, final double par5, final double par7) {
        double var9 = -1.0;
        EntityPlayer var10 = null;
        for (int var11 = 0; var11 < this.worldObj.loadedEntityList.size(); ++var11) {
            final EntityPlayer var12 = (EntityPlayer) this.worldObj.loadedEntityList.get(var11);
            final double var13 = var12.getDistanceSq(par1, par3, par5);
            if ((par7 < 0.0 || var13 < par7 * par7) && (var9 == -1.0 || var13 < var9)) {
                var9 = var13;
                var10 = var12;
            }
        }
        return var10;
    }

    protected void attackEntity(final Entity par1Entity, final float par2) {
        if (this.attackTime <= 0 && par2 < 1.2f && par1Entity.boundingBox.maxY > this.boundingBox.minY && par1Entity.boundingBox.minY < this.boundingBox.maxY) {
            this.attackTime = 20;
            par1Entity.attackEntityFrom(DamageSource.causeMobDamage((EntityLivingBase)this), par2);
        }
    }

    protected void func_145780_a(final int x, final int y, final int z, final Block block) {
        this.worldObj.playSoundAtEntity((Entity)this, "mob.silverfish.step", 1.0f, 1.0f);
    }

    protected Item getDropItem() {
        return Item.getItemFromBlock(Blocks.air);
    }

    public void onUpdate() {
        this.renderYawOffset = this.rotationYaw;
        super.onUpdate();
    }

    protected boolean isValidLightLevel() {
        return true;
    }

    public boolean getCanSpawnHere() {
        if (super.getCanSpawnHere()) {
            final EntityPlayer var1 = this.worldObj.getClosestPlayerToEntity((Entity)this, 5.0);
            return var1 == null;
        }
        return false;
    }

    public EnumCreatureAttribute getCreatureAttribute() {
        return EnumCreatureAttribute.ARTHROPOD;
    }

    public boolean canBreath() {
        return true;
    }
}
