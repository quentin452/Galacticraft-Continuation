package micdoodle8.mods.galacticraft.core.entities;

import net.minecraft.entity.monster.*;
import micdoodle8.mods.galacticraft.api.entity.*;
import net.minecraft.world.*;
import net.minecraft.entity.player.*;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.ai.attributes.*;
import micdoodle8.mods.galacticraft.core.util.*;
import net.minecraft.potion.*;
import net.minecraft.util.*;
import net.minecraftforge.common.*;
import net.minecraft.entity.*;
import micdoodle8.mods.galacticraft.core.items.*;
import net.minecraft.init.*;
import net.minecraft.item.*;

public class EntityEvolvedZombie extends EntityZombie implements IEntityBreathable
{
    private int conversionTime;
    
    public EntityEvolvedZombie(final World par1World) {
        super(par1World);
        this.conversionTime = 0;
        this.tasks.taskEntries.clear();
        this.getNavigator().setBreakDoors(true);
        this.tasks.addTask(0, (EntityAIBase)new EntityAISwimming((EntityLiving)this));
        this.tasks.addTask(1, (EntityAIBase)new EntityAIBreakDoor((EntityLiving)this));
        this.tasks.addTask(2, (EntityAIBase)new EntityAIAttackOnCollide((EntityCreature)this, (Class)EntityPlayer.class, 0.36000001430511475, false));
        this.tasks.addTask(3, (EntityAIBase)new EntityAIAttackOnCollide((EntityCreature)this, (Class)EntityVillager.class, 0.36000001430511475, true));
        this.tasks.addTask(4, (EntityAIBase)new EntityAIMoveTowardsRestriction((EntityCreature)this, 0.36000001430511475));
        this.tasks.addTask(5, (EntityAIBase)new EntityAIMoveThroughVillage((EntityCreature)this, 0.36000001430511475, false));
        this.tasks.addTask(6, (EntityAIBase)new EntityAIWander((EntityCreature)this, 0.36000001430511475));
        this.tasks.addTask(7, (EntityAIBase)new EntityAIWatchClosest((EntityLiving)this, (Class)EntityPlayer.class, 8.0f));
        this.tasks.addTask(7, (EntityAIBase)new EntityAILookIdle((EntityLiving)this));
        this.targetTasks.addTask(1, (EntityAIBase)new EntityAIHurtByTarget((EntityCreature)this, true));
        this.targetTasks.addTask(2, (EntityAIBase)new EntityAINearestAttackableTarget((EntityCreature)this, (Class)EntityPlayer.class, 0, true));
        this.targetTasks.addTask(2, (EntityAIBase)new EntityAINearestAttackableTarget((EntityCreature)this, (Class)EntityVillager.class, 0, false));
    }
    
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(30.0);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(ConfigManagerCore.hardMode ? 1.059999942779541 : 0.9599999785423279);
        this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(ConfigManagerCore.hardMode ? 5.0 : 3.0);
        this.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(ConfigManagerCore.hardMode ? 20.0 : 16.0);
    }
    
    public boolean canBreath() {
        return true;
    }
    
    public IAttribute getReinforcementsAttribute() {
        return EntityZombie.field_110186_bp;
    }
    
    protected void jump() {
        this.motionY = 0.48 / WorldUtil.getGravityFactor((Entity)this);
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
        switch (this.rand.nextInt(16)) {
            case 0:
            case 1:
            case 2: {
                this.entityDropItem(new ItemStack(GCItems.basicItem, 1, 16), 0.0f);
                break;
            }
            case 3:
            case 4: {
                this.dropItem(GCItems.meteoricIronRaw, 1);
                break;
            }
            case 5:
            case 6: {
                this.entityDropItem(new ItemStack(GCItems.basicItem, 1, 18), 0.0f);
                break;
            }
            case 7:
            case 8: {
                this.entityDropItem(new ItemStack(GCItems.oxTankMedium, 1, 901 + this.rand.nextInt(900)), 0.0f);
                break;
            }
            case 9: {
                this.dropItem(GCItems.oxMask, 1);
                break;
            }
            case 10: {
                this.dropItem(GCItems.oxygenVent, 1);
                break;
            }
            case 11:
            case 12: {
                this.dropItem(Items.carrot, 1);
                break;
            }
            case 13:
            case 14:
            case 15: {
                if (ConfigManagerCore.challengeMobDropsAndSpawning) {
                    this.dropItem(Items.melon_seeds, 1);
                    break;
                }
                break;
            }
        }
    }
    
    protected void dropFewItems(final boolean p_70628_1_, final int p_70628_2_) {
        super.dropFewItems(p_70628_1_, p_70628_2_);
        final Item item = this.getDropItem();
        int j = this.rand.nextInt(2);
        if (item != null) {
            if (p_70628_2_ > 0) {
                j += this.rand.nextInt(p_70628_2_ + 1);
            }
            for (int k = 0; k < j; ++k) {
                this.dropItem(item, 1);
            }
        }
        if (p_70628_1_ && ConfigManagerCore.challengeMobDropsAndSpawning && j > 0 && this.rand.nextInt(6) == 0) {
            this.entityDropItem(new ItemStack(GCItems.basicItem, 1, 3), 0.0f);
        }
    }
}
