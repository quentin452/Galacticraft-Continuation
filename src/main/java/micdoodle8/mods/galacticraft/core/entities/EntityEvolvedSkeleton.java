package micdoodle8.mods.galacticraft.core.entities;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.ai.EntityAIArrowAttack;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;

import micdoodle8.mods.galacticraft.api.entity.IEntityBreathable;
import micdoodle8.mods.galacticraft.core.blocks.GCBlocks;
import micdoodle8.mods.galacticraft.core.items.GCItems;
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore;
import micdoodle8.mods.galacticraft.core.util.WorldUtil;

public class EntityEvolvedSkeleton extends EntitySkeleton implements IEntityBreathable {

    public EntityEvolvedSkeleton(World par1World) {
        super(par1World);
        this.tasks.addTask(1, new EntityAISwimming(this));
        this.tasks.addTask(2, new EntityAIRestrictSun(this));
        this.tasks.addTask(3, new EntityAIFleeSun(this, 0.25F));
        this.tasks.addTask(4, new EntityAIArrowAttack(this, 0.25F, 25, 20));
        this.tasks.addTask(5, new EntityAIWander(this, 0.25F));
        this.tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        this.tasks.addTask(6, new EntityAILookIdle(this));
        this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));
        this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, 0, true));
        this.setSize(0.7F, 2.5F);
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth)
            .setBaseValue(25);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed)
            .setBaseValue(0.35F);
    }

    @Override
    public boolean canBreath() {
        return true;
    }

    @Override
    public void attackEntityWithRangedAttack(EntityLivingBase par1EntityLivingBase, float par2) {
        final EntityArrow entityarrow = new EntityArrow(
            this.worldObj,
            this,
            par1EntityLivingBase,
            0.4F,
            17 - this.worldObj.difficultySetting.getDifficultyId() * 4);
        final int i = EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, this.getHeldItem());
        final int j = EnchantmentHelper.getEnchantmentLevel(Enchantment.punch.effectId, this.getHeldItem());
        entityarrow.setDamage(
            par2 * 2.0F + this.rand.nextGaussian() * 0.25D + this.worldObj.difficultySetting.getDifficultyId() * 0.11F);

        if (i > 0) {
            entityarrow.setDamage(entityarrow.getDamage() + i * 0.5D + 0.5D);
        }

        if (j > 0) {
            entityarrow.setKnockbackStrength(j);
        }

        if (EnchantmentHelper.getEnchantmentLevel(Enchantment.flame.effectId, this.getHeldItem()) > 0
            || this.getSkeletonType() == 1) {
            entityarrow.setFire(100);
        }

        this.playSound(
            "random.bow",
            1.0F,
            1.0F / (this.getRNG()
                .nextFloat() * 0.4F + 0.8F));
        this.worldObj.spawnEntityInWorld(entityarrow);
    }

    @Override
    protected void jump() {
        this.motionY = 0.45D / WorldUtil.getGravityFactor(this);
        if (this.motionY < 0.24D) {
            this.motionY = 0.24D;
        }

        if (this.isPotionActive(Potion.jump)) {
            this.motionY += (this.getActivePotionEffect(Potion.jump)
                .getAmplifier() + 1) * 0.1F;
        }

        if (this.isSprinting()) {
            final float f = this.rotationYaw * 0.017453292F;
            this.motionX -= MathHelper.sin(f) * 0.2F;
            this.motionZ += MathHelper.cos(f) * 0.2F;
        }

        this.isAirBorne = true;
        ForgeHooks.onLivingJump(this);
    }

    @Override
    protected void dropRareDrop(int p_70600_1_) {
        if (this.getSkeletonType() == 1) {
            this.entityDropItem(new ItemStack(Items.skull, 1, 1), 0.0F);
            return;
        }

        final int r = this.rand.nextInt(12);
        switch (r) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
                this.entityDropItem(new ItemStack(GCBlocks.oxygenPipe), 0.0F);
                break;
            case 6:
                // Oxygen tank half empty or less
                this.entityDropItem(new ItemStack(GCItems.oxTankMedium, 1, 901 + this.rand.nextInt(900)), 0.0F);
                break;
            case 7:
            case 8:
                this.dropItem(GCItems.canister, 1);
                break;
            default:
                if (ConfigManagerCore.challengeMobDropsAndSpawning) {
                    this.dropItem(Items.pumpkin_seeds, 1);
                }
                break;
        }
    }

    @Override
    protected void dropFewItems(boolean p_70628_1_, int p_70628_2_) {
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

        // Drop lapis as semi-rare drop if player hit and if dropping bones
        if (p_70628_1_ && ConfigManagerCore.challengeMobDropsAndSpawning && j > 1 && this.rand.nextInt(12) == 0) {
            this.entityDropItem(new ItemStack(Items.dye, 1, 4), 0.0F);
        }
    }
}
