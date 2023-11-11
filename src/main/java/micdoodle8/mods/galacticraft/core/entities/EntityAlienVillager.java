package micdoodle8.mods.galacticraft.core.entities;

import micdoodle8.mods.galacticraft.api.entity.*;
import net.minecraft.entity.player.*;
import net.minecraft.world.*;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.ai.*;
import net.minecraft.nbt.*;
import net.minecraft.entity.*;
import net.minecraft.util.*;
import net.minecraft.entity.monster.*;
import net.minecraft.village.*;
import net.minecraft.init.*;
import cpw.mods.fml.relauncher.*;

public class EntityAlienVillager extends EntityAgeable implements IEntityBreathable
{
    private int randomTickDivider;
    private boolean isMating;
    private boolean isPlaying;
    private Village villageObj;
    private EntityPlayer buyingPlayer;
    private MerchantRecipeList buyingList;
    private int wealth;
    private boolean isLookingForHome;
    
    public EntityAlienVillager(final World par1World) {
        super(par1World);
        this.randomTickDivider = 0;
        this.isMating = false;
        this.isPlaying = false;
        this.villageObj = null;
        this.setSize(0.6f, 2.35f);
        this.getNavigator().setBreakDoors(true);
        this.getNavigator().setAvoidsWater(true);
        this.tasks.addTask(0, (EntityAIBase)new EntityAISwimming((EntityLiving)this));
        this.tasks.addTask(2, (EntityAIBase)new EntityAIMoveIndoors((EntityCreature)this));
        this.tasks.addTask(3, (EntityAIBase)new EntityAIRestrictOpenDoor((EntityCreature)this));
        this.tasks.addTask(4, (EntityAIBase)new EntityAIOpenDoor((EntityLiving)this, true));
        this.tasks.addTask(5, (EntityAIBase)new EntityAIMoveTowardsRestriction((EntityCreature)this, 0.30000001192092896));
        this.tasks.addTask(9, (EntityAIBase)new EntityAIWatchClosest2((EntityLiving)this, (Class)EntityPlayer.class, 15.0f, 1.0f));
        this.tasks.addTask(9, (EntityAIBase)new EntityAIWatchClosest2((EntityLiving)this, (Class)EntityVillager.class, 15.0f, 0.05f));
        this.tasks.addTask(9, (EntityAIBase)new EntityAIWander((EntityCreature)this, 0.30000001192092896));
        this.tasks.addTask(10, (EntityAIBase)new EntityAIWatchClosest((EntityLiving)this, (Class)EntityLiving.class, 15.0f));
    }
    
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.5);
    }
    
    public boolean isAIEnabled() {
        return true;
    }
    
    protected void updateAITick() {
        final int randomTickDivider = this.randomTickDivider - 1;
        this.randomTickDivider = randomTickDivider;
        if (randomTickDivider <= 0) {
            this.worldObj.villageCollectionObj.addVillagerPosition(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ));
            this.randomTickDivider = 70 + this.rand.nextInt(50);
            this.villageObj = this.worldObj.villageCollectionObj.findNearestVillage(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ), 32);
            if (this.villageObj == null) {
                this.detachHome();
            }
            else {
                final ChunkCoordinates chunkcoordinates = this.villageObj.getCenter();
                this.setHomeArea(chunkcoordinates.posX, chunkcoordinates.posY, chunkcoordinates.posZ, (int)(this.villageObj.getVillageRadius() * 0.6f));
                if (this.isLookingForHome) {
                    this.isLookingForHome = false;
                    this.villageObj.setDefaultPlayerReputation(5);
                }
            }
        }
        super.updateAITick();
    }
    
    protected void entityInit() {
        super.entityInit();
        this.dataWatcher.addObject(16, (Object)0);
    }
    
    public void writeEntityToNBT(final NBTTagCompound par1NBTTagCompound) {
        super.writeEntityToNBT(par1NBTTagCompound);
        par1NBTTagCompound.setInteger("Profession", this.getProfession());
        par1NBTTagCompound.setInteger("Riches", this.wealth);
        if (this.buyingList != null) {
            par1NBTTagCompound.setTag("Offers", (NBTBase)this.buyingList.getRecipiesAsTags());
        }
    }
    
    public void readEntityFromNBT(final NBTTagCompound par1NBTTagCompound) {
        super.readEntityFromNBT(par1NBTTagCompound);
        this.setProfession(par1NBTTagCompound.getInteger("Profession"));
        this.wealth = par1NBTTagCompound.getInteger("Riches");
        if (par1NBTTagCompound.hasKey("Offers")) {
            final NBTTagCompound nbttagcompound1 = par1NBTTagCompound.getCompoundTag("Offers");
            this.buyingList = new MerchantRecipeList(nbttagcompound1);
        }
    }
    
    protected boolean canDespawn() {
        return false;
    }
    
    protected String getLivingSound() {
        return "mob.villager.idle";
    }
    
    protected String getHurtSound() {
        return "mob.villager.hit";
    }
    
    protected String getDeathSound() {
        return "mob.villager.death";
    }
    
    public void setProfession(final int par1) {
        this.dataWatcher.updateObject(16, (Object)par1);
    }
    
    public int getProfession() {
        return this.dataWatcher.getWatchableObjectInt(16);
    }
    
    public boolean isMating() {
        return this.isMating;
    }
    
    public void setMating(final boolean par1) {
        this.isMating = par1;
    }
    
    public void setPlaying(final boolean par1) {
        this.isPlaying = par1;
    }
    
    public boolean isPlaying() {
        return this.isPlaying;
    }
    
    public void setRevengeTarget(final EntityLivingBase par1EntityLiving) {
        super.setRevengeTarget(par1EntityLiving);
        if (this.villageObj != null && par1EntityLiving != null) {
            this.villageObj.addOrRenewAgressor(par1EntityLiving);
            if (par1EntityLiving instanceof EntityPlayer) {
                byte b0 = -1;
                if (this.isChild()) {
                    b0 = -3;
                }
                this.villageObj.setReputationForPlayer(((EntityPlayer)par1EntityLiving).getCommandSenderName(), (int)b0);
                if (this.isEntityAlive()) {
                    this.worldObj.setEntityState((Entity)this, (byte)13);
                }
            }
        }
    }
    
    public void onDeath(final DamageSource par1DamageSource) {
        if (this.villageObj != null) {
            final Entity entity = par1DamageSource.getEntity();
            if (entity != null) {
                if (entity instanceof EntityPlayer) {
                    this.villageObj.setReputationForPlayer(((EntityPlayer)entity).getCommandSenderName(), -2);
                }
                else if (entity instanceof IMob) {
                    this.villageObj.endMatingSeason();
                }
            }
            else if (entity == null) {
                final EntityPlayer entityplayer = this.worldObj.getClosestPlayerToEntity((Entity)this, 16.0);
                if (entityplayer != null) {
                    this.villageObj.endMatingSeason();
                }
            }
        }
        super.onDeath(par1DamageSource);
    }
    
    public void setCustomer(final EntityPlayer par1EntityPlayer) {
        this.buyingPlayer = par1EntityPlayer;
    }
    
    public EntityPlayer getCustomer() {
        return this.buyingPlayer;
    }
    
    public boolean isTrading() {
        return this.buyingPlayer != null;
    }
    
    public void useRecipe(final MerchantRecipe par1MerchantRecipe) {
        par1MerchantRecipe.incrementToolUses();
        if (par1MerchantRecipe.getItemToBuy().getItem() == Items.emerald) {
            this.wealth += par1MerchantRecipe.getItemToBuy().stackSize;
        }
    }
    
    @SideOnly(Side.CLIENT)
    public void handleHealthUpdate(final byte par1) {
        if (par1 == 12) {
            this.generateRandomParticles("heart");
        }
        else if (par1 == 13) {
            this.generateRandomParticles("angryVillager");
        }
        else if (par1 == 14) {
            this.generateRandomParticles("happyVillager");
        }
        else {
            super.handleHealthUpdate(par1);
        }
    }
    
    @SideOnly(Side.CLIENT)
    private void generateRandomParticles(final String par1Str) {
        for (int i = 0; i < 5; ++i) {
            final double d0 = this.rand.nextGaussian() * 0.02;
            final double d2 = this.rand.nextGaussian() * 0.02;
            final double d3 = this.rand.nextGaussian() * 0.02;
            this.worldObj.spawnParticle(par1Str, this.posX + this.rand.nextFloat() * this.width * 2.0f - this.width, this.posY + 1.0 + this.rand.nextFloat() * this.height, this.posZ + this.rand.nextFloat() * this.width * 2.0f - this.width, d0, d2, d3);
        }
    }
    
    public void setLookingForHome() {
        this.isLookingForHome = true;
    }
    
    public EntityAlienVillager func_90012_b(final EntityAgeable par1EntityAgeable) {
        return new EntityAlienVillager(this.worldObj);
    }
    
    public EntityAgeable createChild(final EntityAgeable par1EntityAgeable) {
        return this.func_90012_b(par1EntityAgeable);
    }
    
    public boolean canBreath() {
        return true;
    }
}
