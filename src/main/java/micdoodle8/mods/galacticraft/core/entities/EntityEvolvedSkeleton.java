package micdoodle8.mods.galacticraft.core.entities;

import net.minecraft.entity.monster.*;
import micdoodle8.mods.galacticraft.api.entity.*;
import net.minecraft.world.*;
import net.minecraft.entity.player.*;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.projectile.*;
import net.minecraft.enchantment.*;
import net.minecraft.entity.*;
import net.minecraft.potion.*;
import net.minecraft.util.*;
import net.minecraftforge.common.*;
import net.minecraft.init.*;
import micdoodle8.mods.galacticraft.core.blocks.*;
import micdoodle8.mods.galacticraft.core.items.*;
import micdoodle8.mods.galacticraft.core.util.*;
import net.minecraft.item.*;

public class EntityEvolvedSkeleton extends EntitySkeleton implements IEntityBreathable
{
    public EntityEvolvedSkeleton(final World par1World) {
        super(par1World);
        this.tasks.addTask(1, (EntityAIBase)new EntityAISwimming((EntityLiving)this));
        this.tasks.addTask(2, (EntityAIBase)new EntityAIRestrictSun((EntityCreature)this));
        this.tasks.addTask(3, (EntityAIBase)new EntityAIFleeSun((EntityCreature)this, 0.25));
        this.tasks.addTask(4, (EntityAIBase)new EntityAIArrowAttack((IRangedAttackMob)this, 0.25, 25, 20.0f));
        this.tasks.addTask(5, (EntityAIBase)new EntityAIWander((EntityCreature)this, 0.25));
        this.tasks.addTask(6, (EntityAIBase)new EntityAIWatchClosest((EntityLiving)this, (Class)EntityPlayer.class, 8.0f));
        this.tasks.addTask(6, (EntityAIBase)new EntityAILookIdle((EntityLiving)this));
        this.targetTasks.addTask(1, (EntityAIBase)new EntityAIHurtByTarget((EntityCreature)this, false));
        this.targetTasks.addTask(2, (EntityAIBase)new EntityAINearestAttackableTarget((EntityCreature)this, (Class)EntityPlayer.class, 0, true));
        this.setSize(0.7f, 2.5f);
    }
    
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(25.0);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.3499999940395355);
    }
    
    public boolean canBreath() {
        return true;
    }
    
    public void attackEntityWithRangedAttack(final EntityLivingBase par1EntityLivingBase, final float par2) {
        final EntityArrow entityarrow = new EntityArrow(this.worldObj, (EntityLivingBase)this, par1EntityLivingBase, 0.4f, (float)(17 - this.worldObj.difficultySetting.getDifficultyId() * 4));
        final int i = EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, this.getHeldItem());
        final int j = EnchantmentHelper.getEnchantmentLevel(Enchantment.punch.effectId, this.getHeldItem());
        entityarrow.setDamage(par2 * 2.0f + this.rand.nextGaussian() * 0.25 + this.worldObj.difficultySetting.getDifficultyId() * 0.11f);
        if (i > 0) {
            entityarrow.setDamage(entityarrow.getDamage() + i * 0.5 + 0.5);
        }
        if (j > 0) {
            entityarrow.setKnockbackStrength(j);
        }
        if (EnchantmentHelper.getEnchantmentLevel(Enchantment.flame.effectId, this.getHeldItem()) > 0 || this.getSkeletonType() == 1) {
            entityarrow.setFire(100);
        }
        this.playSound("random.bow", 1.0f, 1.0f / (this.getRNG().nextFloat() * 0.4f + 0.8f));
        this.worldObj.spawnEntityInWorld((Entity)entityarrow);
    }
    
    protected void jump() {
        this.motionY = 0.45 / WorldUtil.getGravityFactor((Entity)this);
        if (this.motionY < 0.24) {
            this.motionY = 0.24;
        }
        if (this.isPotionActive(Potion.jump)) {
            this.motionY += (this.getActivePotionEffect(Potion.jump).getAmplifier() + 1) * 0.1f;
        }
        if (this.isSprinting()) {
            final float f = this.rotationYaw * 0.017453292f;
            this.motionX -= MathHelper.sin(f) * 0.2f;
            this.motionZ += MathHelper.cos(f) * 0.2f;
        }
        this.isAirBorne = true;
        ForgeHooks.onLivingJump((EntityLivingBase)this);
    }
    
    protected void dropRareDrop(final int p_70600_1_) {
        if (this.getSkeletonType() == 1) {
            this.entityDropItem(new ItemStack(Items.skull, 1, 1), 0.0f);
            return;
        }
        final int r = this.rand.nextInt(12);
        switch (r) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5: {
                this.entityDropItem(new ItemStack(GCBlocks.oxygenPipe), 0.0f);
                break;
            }
            case 6: {
                this.entityDropItem(new ItemStack(GCItems.oxTankMedium, 1, 901 + this.rand.nextInt(900)), 0.0f);
                break;
            }
            case 7:
            case 8: {
                this.dropItem(GCItems.canister, 1);
                break;
            }
            default: {
                if (ConfigManagerCore.challengeMobDropsAndSpawning) {
                    this.dropItem(Items.pumpkin_seeds, 1);
                    break;
                }
                break;
            }
        }
    }
    
    protected void dropFewItems(final boolean p_70628_1_, final int p_70628_2_) {
        final Item item = this.getDropItem();
        int j = this.rand.nextInt(3);
        if (item != null) {
            if (p_70628_2_ > 0) {
                j += this.rand.nextInt(p_70628_2_ + 1);
            }
            for (int k = 1; k < j; ++k) {
                this.dropItem(item, 1);
            }
        }
        j = this.rand.nextInt(3 + p_70628_2_);
        if (j > 1) {
            this.dropItem(Items.bone, 1);
        }
        if (p_70628_1_ && ConfigManagerCore.challengeMobDropsAndSpawning && j > 1 && this.rand.nextInt(12) == 0) {
            this.entityDropItem(new ItemStack(Items.dye, 1, 4), 0.0f);
        }
    }
}
