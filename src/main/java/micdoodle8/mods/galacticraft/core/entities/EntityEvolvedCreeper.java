package micdoodle8.mods.galacticraft.core.entities;

import net.minecraft.entity.monster.*;
import micdoodle8.mods.galacticraft.api.entity.*;
import java.util.*;
import net.minecraft.world.*;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.player.*;
import net.minecraft.entity.ai.*;
import net.minecraft.nbt.*;
import net.minecraft.entity.ai.attributes.*;
import net.minecraft.potion.*;
import net.minecraft.util.*;
import net.minecraftforge.common.*;
import net.minecraft.entity.*;
import net.minecraft.item.*;
import micdoodle8.mods.galacticraft.core.items.*;
import net.minecraft.init.*;
import micdoodle8.mods.galacticraft.core.util.*;

public class EntityEvolvedCreeper extends EntityCreeper implements IEntityBreathable
{
    private float sizeXBase;
    private float sizeYBase;
    private static final UUID babySpeedBoostUUID;
    private static final AttributeModifier babySpeedBoostModifier;
    
    public EntityEvolvedCreeper(final World par1World) {
        super(par1World);
        this.sizeXBase = -1.0f;
        this.tasks.taskEntries.clear();
        this.tasks.addTask(1, (EntityAIBase)new EntityAISwimming((EntityLiving)this));
        this.tasks.addTask(2, (EntityAIBase)new EntityAICreeperSwell((EntityCreeper)this));
        this.tasks.addTask(3, (EntityAIBase)new EntityAIAvoidEntity((EntityCreature)this, (Class)EntityOcelot.class, 6.0f, 0.25, 0.30000001192092896));
        this.tasks.addTask(4, (EntityAIBase)new EntityAIAttackOnCollide((EntityCreature)this, 0.25, false));
        this.tasks.addTask(5, (EntityAIBase)new EntityAIWander((EntityCreature)this, 0.20000000298023224));
        this.tasks.addTask(6, (EntityAIBase)new EntityAIWatchClosest((EntityLiving)this, (Class)EntityPlayer.class, 8.0f));
        this.tasks.addTask(6, (EntityAIBase)new EntityAILookIdle((EntityLiving)this));
        this.targetTasks.addTask(1, (EntityAIBase)new EntityAINearestAttackableTarget((EntityCreature)this, (Class)EntityPlayer.class, 0, true));
        this.targetTasks.addTask(2, (EntityAIBase)new EntityAIHurtByTarget((EntityCreature)this, false));
        this.setSize(0.7f, 2.2f);
    }
    
    protected void entityInit() {
        super.entityInit();
        this.getDataWatcher().addObject(12, (Object)0);
    }
    
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(25.0);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(1.0);
    }
    
    public void writeEntityToNBT(final NBTTagCompound nbt) {
        super.writeEntityToNBT(nbt);
        if (this.isChild()) {
            nbt.setBoolean("IsBaby", true);
        }
    }
    
    public void readEntityFromNBT(final NBTTagCompound nbt) {
        super.readEntityFromNBT(nbt);
        if (nbt.getBoolean("IsBaby")) {
            this.setChild(true);
        }
    }
    
    public boolean canBreath() {
        return true;
    }
    
    public void setChildSize(final boolean isChild) {
        this.setCreeperScale(isChild ? 0.5f : 1.0f);
    }
    
    protected final void setSize(final float sizeX, final float sizeY) {
        final boolean flag = this.sizeXBase > 0.0f && this.sizeYBase > 0.0f;
        this.sizeXBase = sizeX;
        this.sizeYBase = sizeY;
        if (!flag) {
            this.setCreeperScale(1.0f);
        }
    }
    
    protected final void setCreeperScale(final float scale) {
        super.setSize(this.sizeXBase * scale, this.sizeYBase * scale);
    }
    
    public boolean isChild() {
        return this.getDataWatcher().getWatchableObjectByte(12) == 1;
    }
    
    protected int getExperiencePoints(final EntityPlayer p_70693_1_) {
        if (this.isChild()) {
            this.experienceValue = this.experienceValue * 5 / 2;
        }
        return super.getExperiencePoints(p_70693_1_);
    }
    
    public void setChild(final boolean isChild) {
        this.getDataWatcher().updateObject(12, (Object)(byte)(isChild ? 1 : 0));
        if (this.worldObj != null && !this.worldObj.isRemote) {
            final IAttributeInstance iattributeinstance = this.getEntityAttribute(SharedMonsterAttributes.movementSpeed);
            iattributeinstance.removeModifier(EntityEvolvedCreeper.babySpeedBoostModifier);
            if (isChild) {
                iattributeinstance.applyModifier(EntityEvolvedCreeper.babySpeedBoostModifier);
            }
        }
        this.setChildSize(isChild);
    }
    
    protected void jump() {
        this.motionY = 0.45 / WorldUtil.getGravityFactor((Entity)this);
        if (this.motionY < 0.22) {
            this.motionY = 0.22;
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
    
    protected Item getDropItem() {
        if (this.isBurning()) {
            return Items.blaze_rod;
        }
        return Items.redstone;
    }
    
    protected void dropRareDrop(final int p_70600_1_) {
        switch (this.rand.nextInt(12)) {
            case 0:
            case 1:
            case 2:
            case 3: {
                this.entityDropItem(new ItemStack(VersionUtil.sand), 0.0f);
                break;
            }
            case 4:
            case 5: {
                this.entityDropItem(new ItemStack(GCItems.oxTankMedium, 1, 901 + this.rand.nextInt(900)), 0.0f);
                break;
            }
            case 6: {
                this.dropItem(GCItems.oxygenGear, 1);
                break;
            }
            case 7:
            case 8: {
                this.entityDropItem(new ItemStack(Blocks.ice), 0.0f);
                break;
            }
            default: {
                if (ConfigManagerCore.challengeMobDropsAndSpawning) {
                    this.dropItem(Items.reeds, 1);
                    break;
                }
                break;
            }
        }
    }
    
    static {
        babySpeedBoostUUID = UUID.fromString("ef67a435-32a4-4efd-b218-e7431438b109");
        babySpeedBoostModifier = new AttributeModifier(EntityEvolvedCreeper.babySpeedBoostUUID, "Baby speed boost evolved creeper", 0.5, 1);
    }
}
