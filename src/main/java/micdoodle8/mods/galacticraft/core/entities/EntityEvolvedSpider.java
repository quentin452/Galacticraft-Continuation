package micdoodle8.mods.galacticraft.core.entities;

import net.minecraft.entity.monster.*;
import micdoodle8.mods.galacticraft.api.entity.*;
import net.minecraft.world.*;
import net.minecraft.potion.*;
import micdoodle8.mods.galacticraft.core.util.*;
import net.minecraft.util.*;
import net.minecraftforge.common.*;
import net.minecraft.entity.*;
import micdoodle8.mods.galacticraft.core.items.*;
import net.minecraft.init.*;
import net.minecraft.item.*;

public class EntityEvolvedSpider extends EntitySpider implements IEntityBreathable
{
    public EntityEvolvedSpider(final World par1World) {
        super(par1World);
        this.setSize(1.5f, 1.0f);
    }
    
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(22.0);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(ConfigManagerCore.hardMode ? 1.2000000476837158 : 1.0);
        this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(ConfigManagerCore.hardMode ? 4.0 : 2.0);
    }
    
    public boolean canBreath() {
        return true;
    }
    
    protected boolean isAIEnabled() {
        return false;
    }
    
    public IEntityLivingData onSpawnWithEgg(IEntityLivingData livingData) {
        if (this.worldObj.rand.nextInt(100) == 0) {
            final EntityEvolvedSkeleton skeleton = new EntityEvolvedSkeleton(this.worldObj);
            skeleton.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, 0.0f);
            skeleton.onSpawnWithEgg((IEntityLivingData)null);
            this.worldObj.spawnEntityInWorld((Entity)skeleton);
            skeleton.mountEntity((Entity)this);
        }
        if (livingData == null) {
            livingData = (IEntityLivingData)new EntitySpider.GroupData();
            if (this.worldObj.difficultySetting == EnumDifficulty.HARD && this.worldObj.rand.nextFloat() < 0.1f * this.worldObj.func_147462_b(this.posX, this.posY, this.posZ)) {
                ((EntitySpider.GroupData)livingData).func_111104_a(this.worldObj.rand);
            }
        }
        if (livingData instanceof EntitySpider.GroupData) {
            final int i = ((EntitySpider.GroupData)livingData).field_111105_a;
            if (i > 0 && Potion.potionTypes[i] != null) {
                this.addPotionEffect(new PotionEffect(i, Integer.MAX_VALUE));
            }
        }
        return livingData;
    }
    
    protected void jump() {
        this.motionY = 0.52 / WorldUtil.getGravityFactor((Entity)this);
        if (this.motionY < 0.26) {
            this.motionY = 0.26;
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
        switch (this.rand.nextInt(14)) {
            case 0:
            case 1:
            case 2: {
                this.dropItem(GCItems.cheeseCurd, 1);
                break;
            }
            case 3:
            case 4:
            case 5: {
                this.dropItem(Items.fermented_spider_eye, 1);
                break;
            }
            case 6:
            case 7: {
                this.entityDropItem(new ItemStack(GCItems.oxTankMedium, 1, 901 + this.rand.nextInt(900)), 0.0f);
                break;
            }
            case 8: {
                this.dropItem(GCItems.oxygenGear, 1);
                break;
            }
            case 9: {
                this.dropItem(GCItems.oxygenConcentrator, 1);
                break;
            }
            default: {
                if (ConfigManagerCore.challengeMobDropsAndSpawning) {
                    this.dropItem(Items.nether_wart, 1);
                    break;
                }
                break;
            }
        }
    }
}
