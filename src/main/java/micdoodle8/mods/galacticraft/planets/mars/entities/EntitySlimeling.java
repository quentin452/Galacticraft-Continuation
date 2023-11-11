package micdoodle8.mods.galacticraft.planets.mars.entities;

import micdoodle8.mods.galacticraft.api.entity.*;
import micdoodle8.mods.galacticraft.planets.mars.inventory.*;
import net.minecraft.world.*;
import net.minecraft.init.*;
import net.minecraft.item.*;
import net.minecraft.nbt.*;
import micdoodle8.mods.galacticraft.core.*;
import net.minecraft.entity.projectile.*;
import micdoodle8.mods.galacticraft.planets.mars.*;
import net.minecraft.entity.player.*;
import micdoodle8.mods.galacticraft.core.entities.player.*;
import net.minecraft.pathfinding.*;
import net.minecraft.entity.*;
import micdoodle8.mods.galacticraft.api.vector.*;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.ai.*;
import micdoodle8.mods.galacticraft.planets.mars.items.*;
import micdoodle8.mods.galacticraft.core.util.*;
import net.minecraft.potion.*;
import net.minecraft.util.*;
import net.minecraftforge.common.*;

public class EntitySlimeling extends EntityTameable implements IEntityBreathable
{
    public InventorySlimeling slimelingInventory;
    public float colorRed;
    public float colorGreen;
    public float colorBlue;
    public long ticksAlive;
    public int age;
    public final int MAX_AGE = 100000;
    public String slimelingName;
    public int favFoodID;
    public float attackDamage;
    public int kills;
    
    public EntitySlimeling(final World par1World) {
        super(par1World);
        this.slimelingInventory = new InventorySlimeling(this);
        this.age = 0;
        this.slimelingName = GCCoreUtil.translate("gui.message.unnamed.name");
        this.favFoodID = 1;
        this.attackDamage = 0.05f;
        this.setSize(0.45f, 0.7f);
        this.getNavigator().setAvoidsWater(true);
        this.tasks.addTask(1, (EntityAIBase)new EntityAISwimming((EntityLiving)this));
        this.aiSit = new EntityAISitGC(this);
        this.tasks.addTask(2, (EntityAIBase)this.aiSit);
        this.tasks.addTask(3, (EntityAIBase)new EntityAILeapAtTarget((EntityLiving)this, 0.4f));
        this.tasks.addTask(4, (EntityAIBase)new EntityAIAttackOnCollide((EntityCreature)this, 1.0, true));
        this.tasks.addTask(5, (EntityAIBase)new EntityAIFollowOwner((EntityTameable)this, 1.0, 10.0f, 2.0f));
        this.tasks.addTask(6, (EntityAIBase)new EntityAIMate((EntityAnimal)this, 1.0));
        this.tasks.addTask(7, (EntityAIBase)new EntityAIWander((EntityCreature)this, 1.0));
        this.tasks.addTask(9, (EntityAIBase)new EntityAIWatchClosest((EntityLiving)this, (Class)EntityPlayer.class, 8.0f));
        this.tasks.addTask(9, (EntityAIBase)new EntityAILookIdle((EntityLiving)this));
        this.targetTasks.addTask(1, (EntityAIBase)new EntityAIOwnerHurtByTarget((EntityTameable)this));
        this.targetTasks.addTask(2, (EntityAIBase)new EntityAIOwnerHurtTarget((EntityTameable)this));
        this.targetTasks.addTask(3, (EntityAIBase)new EntityAIHurtByTarget((EntityCreature)this, true));
        this.targetTasks.addTask(4, (EntityAIBase)new EntityAITargetNonTamed((EntityTameable)this, (Class)EntitySludgeling.class, 200, false));
        this.setTamed(false);
        switch (this.rand.nextInt(3)) {
            case 0: {
                this.colorRed = 1.0f;
                break;
            }
            case 1: {
                this.colorBlue = 1.0f;
                break;
            }
            case 2: {
                this.colorRed = 1.0f;
                this.colorGreen = 1.0f;
                break;
            }
        }
        this.setRandomFavFood();
    }
    
    public EntityLivingBase getOwner() {
        final EntityLivingBase owner = super.getOwner();
        if (owner == null) {
            final String ownerName = this.getOwnerUsername();
            if (ownerName != null) {
                return (EntityLivingBase)this.worldObj.getPlayerEntityByName(ownerName);
            }
        }
        return owner;
    }
    
    public boolean isOwner(final EntityLivingBase entityLivingBase) {
        return entityLivingBase == this.getOwner();
    }
    
    public boolean canBreatheUnderwater() {
        return true;
    }
    
    public float getSlimelingSize() {
        return this.getScale() * 2.0f;
    }
    
    public void setScaleForAge(final boolean par1) {
        this.setScale(this.getSlimelingSize());
    }
    
    public boolean isChild() {
        final float n = (float)this.getAge();
        this.getClass();
        return n / 100000.0f < 0.33f;
    }
    
    private void setRandomFavFood() {
        switch (this.rand.nextInt(10)) {
            case 0: {
                this.favFoodID = Item.getIdFromItem(Items.gold_ingot);
                break;
            }
            case 1: {
                this.favFoodID = Item.getIdFromItem(Items.flint_and_steel);
                break;
            }
            case 2: {
                this.favFoodID = Item.getIdFromItem(Items.baked_potato);
                break;
            }
            case 3: {
                this.favFoodID = Item.getIdFromItem(Items.stone_sword);
                break;
            }
            case 4: {
                this.favFoodID = Item.getIdFromItem(Items.gunpowder);
                break;
            }
            case 5: {
                this.favFoodID = Item.getIdFromItem(Items.wooden_door);
                break;
            }
            case 6: {
                this.favFoodID = Item.getIdFromItem(Items.emerald);
                break;
            }
            case 7: {
                this.favFoodID = Item.getIdFromItem(Items.cooked_fished);
                break;
            }
            case 8: {
                this.favFoodID = Item.getIdFromItem(Items.repeater);
                break;
            }
            case 9: {
                this.favFoodID = Item.getIdFromItem(Items.boat);
                break;
            }
        }
    }
    
    public EntitySlimeling(final World par1World, final float red, final float green, final float blue) {
        this(par1World);
        this.colorRed = red;
        this.colorGreen = green;
        this.colorBlue = blue;
    }
    
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.30000001192092896);
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(this.getMaxHealthSlimeling());
    }
    
    public boolean isAIEnabled() {
        return true;
    }
    
    protected void updateAITick() {
        this.dataWatcher.updateObject(18, (Object)this.getHealth());
    }
    
    protected void entityInit() {
        super.entityInit();
        this.dataWatcher.addObject(18, (Object)new Float(this.getHealth()));
        this.dataWatcher.addObject(19, (Object)new Float(this.colorRed));
        this.dataWatcher.addObject(20, (Object)new Float(this.colorGreen));
        this.dataWatcher.addObject(21, (Object)new Float(this.colorBlue));
        this.dataWatcher.addObject(22, (Object)new Integer(this.age));
        this.dataWatcher.addObject(23, (Object)"");
        this.dataWatcher.addObject(24, (Object)new Integer(this.favFoodID));
        this.dataWatcher.addObject(25, (Object)new Float(this.attackDamage));
        this.dataWatcher.addObject(26, (Object)new Integer(this.kills));
        this.dataWatcher.addObject(27, (Object)new ItemStack(Blocks.stone));
        this.dataWatcher.addObject(28, (Object)"");
        this.setName(GCCoreUtil.translate("gui.message.unnamed.name"));
    }
    
    public void writeEntityToNBT(final NBTTagCompound nbt) {
        super.writeEntityToNBT(nbt);
        nbt.setTag("SlimelingInventory", (NBTBase)this.slimelingInventory.writeToNBT(new NBTTagList()));
        nbt.setFloat("SlimeRed", this.colorRed);
        nbt.setFloat("SlimeGreen", this.colorGreen);
        nbt.setFloat("SlimeBlue", this.colorBlue);
        nbt.setInteger("SlimelingAge", this.age);
        nbt.setString("SlimelingName", this.slimelingName);
        nbt.setInteger("FavFoodID", this.favFoodID);
        nbt.setFloat("SlimelingDamage", this.attackDamage);
        nbt.setInteger("SlimelingKills", this.kills);
        nbt.setString("OwnerUsername", this.getOwnerUsername());
    }
    
    public void readEntityFromNBT(final NBTTagCompound nbt) {
        super.readEntityFromNBT(nbt);
        this.slimelingInventory.readFromNBT(nbt.getTagList("SlimelingInventory", 10));
        this.colorRed = nbt.getFloat("SlimeRed");
        this.colorGreen = nbt.getFloat("SlimeGreen");
        this.colorBlue = nbt.getFloat("SlimeBlue");
        this.age = nbt.getInteger("SlimelingAge");
        this.slimelingName = nbt.getString("SlimelingName");
        this.favFoodID = nbt.getInteger("FavFoodID");
        this.attackDamage = nbt.getFloat("SlimelingDamage");
        this.kills = nbt.getInteger("SlimelingKills");
        this.setOwnerUsername(nbt.getString("OwnerUsername"));
        this.setColorRed(this.colorRed);
        this.setColorGreen(this.colorGreen);
        this.setColorBlue(this.colorBlue);
        this.setAge(this.age);
        this.setName(this.slimelingName);
        this.setKillCount(this.kills);
    }
    
    protected String getLivingSound() {
        return null;
    }
    
    protected String getHurtSound() {
        this.playSound("mob.slime.small", this.getSoundVolume(), 1.1f);
        return null;
    }
    
    protected String getDeathSound() {
        this.playSound(GalacticraftCore.TEXTURE_PREFIX + "entity.slime_death", this.getSoundVolume(), 0.8f);
        return null;
    }
    
    protected Item getDropItem() {
        return Items.slime_ball;
    }
    
    public void onLivingUpdate() {
        super.onLivingUpdate();
        if (!this.worldObj.isRemote) {
            if (this.ticksAlive <= 0L) {
                this.setColorRed(this.colorRed);
                this.setColorGreen(this.colorGreen);
                this.setColorBlue(this.colorBlue);
            }
            ++this.ticksAlive;
            if (this.ticksAlive >= Long.MAX_VALUE) {
                this.ticksAlive = 0L;
            }
            if (this.ticksAlive % 2L == 0L) {
                final int age = this.age;
                this.getClass();
                if (age < 100000) {
                    ++this.age;
                }
                final int age2 = this.age;
                this.getClass();
                this.setAge(Math.min(age2, 100000));
            }
            this.setFavoriteFood(this.favFoodID);
            this.setAttackDamage(this.attackDamage);
            this.setKillCount(this.kills);
            this.setCargoSlot(this.slimelingInventory.getStackInSlot(1));
        }
        if (!this.worldObj.isRemote) {
            this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(this.getMaxHealthSlimeling());
            if (this.getOwnerUsername().isEmpty()) {
                final EntityLivingBase owner = this.getOwner();
                if (owner != null) {
                    this.setOwnerUsername(owner.getCommandSenderName());
                }
            }
        }
    }
    
    private double getMaxHealthSlimeling() {
        if (this.isTamed()) {
            final double n = 20.001;
            final double n2 = 30.0;
            final double n3 = this.age;
            this.getClass();
            return n + n2 * (n3 / 100000.0);
        }
        return 8.0;
    }
    
    public float getEyeHeight() {
        return this.height * 0.8f;
    }
    
    public boolean attackEntityFrom(final DamageSource par1DamageSource, float par2) {
        if (this.isEntityInvulnerable()) {
            return false;
        }
        final Entity entity = par1DamageSource.getEntity();
        this.setSittingAI(false);
        if (entity != null && !(entity instanceof EntityPlayer) && !(entity instanceof EntityArrow)) {
            par2 = (par2 + 1.0f) / 2.0f;
        }
        return super.attackEntityFrom(par1DamageSource, par2);
    }
    
    public boolean attackEntityAsMob(final Entity par1Entity) {
        return par1Entity.attackEntityFrom((DamageSource)new EntityDamageSource("slimeling", (Entity)this), this.getDamage());
    }
    
    public float getDamage() {
        final int i = this.isTamed() ? 5 : 2;
        return i * this.getAttackDamage();
    }
    
    public void setTamed(final boolean par1) {
        super.setTamed(par1);
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(this.getMaxHealthSlimeling());
    }
    
    public boolean interact(final EntityPlayer par1EntityPlayer) {
        final ItemStack itemstack = par1EntityPlayer.inventory.getCurrentItem();
        if (this.isTamed()) {
            if (itemstack != null) {
                if (itemstack.getItem() == this.getFavoriteFood()) {
                    if (this.isOwner((EntityLivingBase)par1EntityPlayer)) {
                        final ItemStack itemStack = itemstack;
                        --itemStack.stackSize;
                        if (itemstack.stackSize <= 0) {
                            par1EntityPlayer.inventory.setInventorySlotContents(par1EntityPlayer.inventory.currentItem, (ItemStack)null);
                        }
                        if (this.worldObj.isRemote) {
                            MarsModuleClient.openSlimelingGui(this, 1);
                        }
                        if (this.rand.nextInt(3) == 0) {
                            this.setRandomFavFood();
                        }
                    }
                    else if (par1EntityPlayer instanceof EntityPlayerMP) {
                        final GCPlayerStats stats = GCPlayerStats.get((EntityPlayerMP)par1EntityPlayer);
                        if (stats.chatCooldown == 0) {
                            par1EntityPlayer.addChatMessage((IChatComponent)new ChatComponentText(GCCoreUtil.translate("gui.slimeling.chat.wrongPlayer")));
                            stats.chatCooldown = 100;
                        }
                    }
                }
                else if (this.worldObj.isRemote) {
                    MarsModuleClient.openSlimelingGui(this, 0);
                }
            }
            else if (this.worldObj.isRemote) {
                MarsModuleClient.openSlimelingGui(this, 0);
            }
            return true;
        }
        if (itemstack != null && itemstack.getItem() == Items.slime_ball) {
            if (!par1EntityPlayer.capabilities.isCreativeMode) {
                final ItemStack itemStack2 = itemstack;
                --itemStack2.stackSize;
            }
            if (itemstack.stackSize <= 0) {
                par1EntityPlayer.inventory.setInventorySlotContents(par1EntityPlayer.inventory.currentItem, (ItemStack)null);
            }
            if (!this.worldObj.isRemote) {
                if (this.rand.nextInt(3) == 0) {
                    this.setTamed(true);
                    this.setPathToEntity((PathEntity)null);
                    this.setAttackTarget((EntityLivingBase)null);
                    this.setSittingAI(true);
                    this.setHealth(20.0f);
                    VersionUtil.setSlimelingOwner(this, VersionUtil.mcVersion1_7_10 ? par1EntityPlayer.getUniqueID().toString() : (VersionUtil.mcVersion1_7_2 ? par1EntityPlayer.getCommandSenderName() : ""));
                    this.setOwnerUsername(par1EntityPlayer.getCommandSenderName());
                    this.playTameEffect(true);
                    this.worldObj.setEntityState((Entity)this, (byte)7);
                }
                else {
                    this.playTameEffect(false);
                    this.worldObj.setEntityState((Entity)this, (byte)6);
                }
            }
            return true;
        }
        return super.interact(par1EntityPlayer);
    }
    
    public void setSittingAI(final boolean sitting) {
        this.aiSit.setSitting(sitting);
    }
    
    public String getOwnerUsername() {
        final String s = this.dataWatcher.getWatchableObjectString(28);
        return (s == null || s.length() == 0) ? "" : s;
    }
    
    public void setOwnerUsername(final String username) {
        this.dataWatcher.updateObject(28, (Object)username);
    }
    
    public boolean isBreedingItem(final ItemStack par1ItemStack) {
        return false;
    }
    
    public EntitySlimeling spawnBabyAnimal(final EntityAgeable par1EntityAgeable) {
        if (par1EntityAgeable instanceof EntitySlimeling) {
            final EntitySlimeling otherSlimeling = (EntitySlimeling)par1EntityAgeable;
            final Vector3 colorParentA = new Vector3((double)this.getColorRed(), (double)this.getColorGreen(), (double)this.getColorBlue());
            final Vector3 colorParentB = new Vector3((double)otherSlimeling.getColorRed(), (double)otherSlimeling.getColorGreen(), (double)otherSlimeling.getColorBlue());
            final Vector3 newColor = ColorUtil.addColorsRealistically(colorParentA, colorParentB);
            newColor.x = Math.max(Math.min(newColor.x, 1.0), 0.0);
            newColor.y = Math.max(Math.min(newColor.y, 1.0), 0.0);
            newColor.z = Math.max(Math.min(newColor.z, 1.0), 0.0);
            final EntitySlimeling newSlimeling = new EntitySlimeling(this.worldObj, (float)newColor.x, (float)newColor.y, (float)newColor.z);
            String s = VersionUtil.getSlimelingOwner(this);
            if (s != null && s.trim().length() > 0) {
                VersionUtil.setSlimelingOwner(newSlimeling, s);
                newSlimeling.setOwnerUsername(this.getOwnerUsername());
                newSlimeling.setTamed(true);
            }
            else {
                s = VersionUtil.getSlimelingOwner(otherSlimeling);
                if (s != null && s.trim().length() > 0) {
                    VersionUtil.setSlimelingOwner(newSlimeling, s);
                    newSlimeling.setOwnerUsername(this.getOwnerUsername());
                    newSlimeling.setTamed(true);
                }
            }
            return newSlimeling;
        }
        return null;
    }
    
    public boolean canMateWith(final EntityAnimal par1EntityAnimal) {
        if (par1EntityAnimal == this) {
            return false;
        }
        if (!this.isTamed()) {
            return false;
        }
        if (!(par1EntityAnimal instanceof EntitySlimeling)) {
            return false;
        }
        final EntitySlimeling slimeling = (EntitySlimeling)par1EntityAnimal;
        return slimeling.isTamed() && !slimeling.isSitting() && this.isInLove() && slimeling.isInLove();
    }
    
    public boolean func_142018_a(final EntityLivingBase par1EntityLivingBase, final EntityLivingBase par2EntityLivingBase) {
        if (!(par1EntityLivingBase instanceof EntityCreeper) && !(par1EntityLivingBase instanceof EntityGhast)) {
            if (par1EntityLivingBase instanceof EntitySlimeling) {
                final EntitySlimeling slimeling = (EntitySlimeling)par1EntityLivingBase;
                if (slimeling.isTamed() && slimeling.getOwner() == par2EntityLivingBase) {
                    return false;
                }
            }
            return (!(par1EntityLivingBase instanceof EntityPlayer) || !(par2EntityLivingBase instanceof EntityPlayer) || ((EntityPlayer)par2EntityLivingBase).canAttackPlayer((EntityPlayer)par1EntityLivingBase)) && (!(par1EntityLivingBase instanceof EntityHorse) || !((EntityHorse)par1EntityLivingBase).isTame());
        }
        return false;
    }
    
    public EntityAgeable createChild(final EntityAgeable par1EntityAgeable) {
        return (EntityAgeable)this.spawnBabyAnimal(par1EntityAgeable);
    }
    
    public float getColorRed() {
        return this.dataWatcher.getWatchableObjectFloat(19);
    }
    
    public void setColorRed(final float color) {
        this.dataWatcher.updateObject(19, (Object)color);
    }
    
    public float getColorGreen() {
        return this.dataWatcher.getWatchableObjectFloat(20);
    }
    
    public void setColorGreen(final float color) {
        this.dataWatcher.updateObject(20, (Object)color);
    }
    
    public float getColorBlue() {
        return this.dataWatcher.getWatchableObjectFloat(21);
    }
    
    public void setColorBlue(final float color) {
        this.dataWatcher.updateObject(21, (Object)color);
    }
    
    public int getAge() {
        return this.dataWatcher.getWatchableObjectInt(22);
    }
    
    public void setAge(final int age) {
        this.dataWatcher.updateObject(22, (Object)age);
    }
    
    public String getName() {
        return this.dataWatcher.getWatchableObjectString(23);
    }
    
    public void setName(final String name) {
        this.dataWatcher.updateObject(23, (Object)name);
    }
    
    public Item getFavoriteFood() {
        return Item.getItemById(this.dataWatcher.getWatchableObjectInt(24));
    }
    
    public void setFavoriteFood(final int foodID) {
        this.dataWatcher.updateObject(24, (Object)foodID);
    }
    
    public float getAttackDamage() {
        return this.dataWatcher.getWatchableObjectFloat(25);
    }
    
    public void setAttackDamage(final float damage) {
        this.dataWatcher.updateObject(25, (Object)damage);
    }
    
    public int getKillCount() {
        return this.dataWatcher.getWatchableObjectInt(26);
    }
    
    public void setKillCount(final int damage) {
        this.dataWatcher.updateObject(26, (Object)damage);
    }
    
    public boolean canBreath() {
        return true;
    }
    
    public float getScale() {
        final float n = (float)this.getAge();
        this.getClass();
        return n / 100000.0f * 0.5f + 0.5f;
    }
    
    public EntityAISit getAiSit() {
        return this.aiSit;
    }
    
    public ItemStack getCargoSlot() {
        return this.dataWatcher.getWatchableObjectItemStack(27);
    }
    
    public void setCargoSlot(final ItemStack stack) {
        final ItemStack stack2 = this.dataWatcher.getWatchableObjectItemStack(27);
        if (stack != stack2) {
            this.dataWatcher.updateObject(27, (Object)stack);
            this.dataWatcher.setObjectWatched(27);
        }
    }
    
    public void onDeath(final DamageSource p_70645_1_) {
        super.onDeath(p_70645_1_);
        if (!this.worldObj.isRemote) {
            final ItemStack bag = this.getCargoSlot();
            if (bag != null && bag.getItem() == MarsItems.marsItemBasic && bag.getItemDamage() == 4) {
                this.slimelingInventory.decrStackSize(1, 64);
                this.entityDropItem(bag, 0.5f);
            }
        }
    }
    
    protected void jump() {
        this.motionY = 0.48 / WorldUtil.getGravityFactor((Entity)this);
        if (this.motionY < 0.28) {
            this.motionY = 0.28;
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
    
    public static class EntityAISitGC extends EntityAISit
    {
        private EntityTameable theEntity;
        private boolean isSitting;
        
        public EntityAISitGC(final EntityTameable theEntity) {
            super(theEntity);
            this.theEntity = theEntity;
            this.setMutexBits(5);
        }
        
        public boolean shouldExecute() {
            if (!this.theEntity.isTamed()) {
                return false;
            }
            if (this.theEntity.isInWater()) {
                return false;
            }
            final EntityLivingBase entitylivingbase = this.theEntity.getOwner();
            return entitylivingbase == null || ((this.theEntity.getDistanceSqToEntity((Entity)entitylivingbase) >= 144.0 || entitylivingbase.getAITarget() == null) && this.isSitting);
        }
        
        public void startExecuting() {
            this.theEntity.getNavigator().clearPathEntity();
            this.theEntity.setSitting(true);
        }
        
        public void resetTask() {
            this.theEntity.setSitting(false);
        }
        
        public void setSitting(final boolean isSitting) {
            this.isSitting = isSitting;
        }
    }
}
