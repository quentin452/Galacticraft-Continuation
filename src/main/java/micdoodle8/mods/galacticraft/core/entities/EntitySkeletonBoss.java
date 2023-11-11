package micdoodle8.mods.galacticraft.core.entities;

import net.minecraft.entity.monster.*;
import net.minecraft.entity.boss.*;
import micdoodle8.mods.galacticraft.api.entity.*;
import micdoodle8.mods.galacticraft.api.vector.*;
import net.minecraft.world.*;
import net.minecraft.entity.player.*;
import net.minecraft.entity.ai.*;
import micdoodle8.mods.galacticraft.core.*;
import cpw.mods.fml.common.network.*;
import micdoodle8.mods.galacticraft.core.network.*;
import net.minecraft.tileentity.*;
import micdoodle8.mods.galacticraft.core.tile.*;
import net.minecraftforge.common.*;
import net.minecraft.inventory.*;
import micdoodle8.mods.galacticraft.core.items.*;
import cpw.mods.fml.relauncher.*;
import micdoodle8.mods.galacticraft.core.util.*;
import net.minecraft.util.*;
import net.minecraft.entity.projectile.*;
import net.minecraft.stats.*;
import net.minecraft.item.*;
import net.minecraft.init.*;
import net.minecraft.entity.item.*;
import net.minecraft.enchantment.*;
import java.util.*;
import micdoodle8.mods.galacticraft.api.*;
import net.minecraft.nbt.*;
import net.minecraft.entity.*;
import micdoodle8.mods.galacticraft.api.world.*;

public class EntitySkeletonBoss extends EntityMob implements IEntityBreathable, IBossDisplayData, IRangedAttackMob, IBoss, IIgnoreShift
{
    protected long ticks;
    private static final ItemStack defaultHeldItem;
    private TileEntityDungeonSpawner spawner;
    public int throwTimer;
    public int postThrowDelay;
    public Entity thrownEntity;
    public Entity targetEntity;
    public int deathTicks;
    public int entitiesWithin;
    public int entitiesWithinLast;
    private Vector3 roomCoords;
    private Vector3 roomSize;
    
    public EntitySkeletonBoss(final World par1World) {
        super(par1World);
        this.ticks = 0L;
        this.postThrowDelay = 20;
        this.deathTicks = 0;
        this.setSize(1.5f, 4.0f);
        this.isImmuneToFire = true;
        this.tasks.addTask(1, (EntityAIBase)new EntityAISwimming((EntityLiving)this));
        this.tasks.addTask(2, (EntityAIBase)new EntityAIArrowAttack((IRangedAttackMob)this, 1.0, 25, 10.0f));
        this.tasks.addTask(2, (EntityAIBase)new EntityAIWander((EntityCreature)this, 1.0));
        this.tasks.addTask(3, (EntityAIBase)new EntityAIWatchClosest((EntityLiving)this, (Class)EntityPlayer.class, 8.0f));
        this.tasks.addTask(3, (EntityAIBase)new EntityAILookIdle((EntityLiving)this));
        this.targetTasks.addTask(1, (EntityAIBase)new EntityAIHurtByTarget((EntityCreature)this, false));
        this.targetTasks.addTask(2, (EntityAIBase)new EntityAINearestAttackableTarget((EntityCreature)this, (Class)EntityPlayer.class, 0, true));
    }
    
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(150.0 * ConfigManagerCore.dungeonBossHealthMod);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(ConfigManagerCore.hardMode ? 0.4000000059604645 : 0.25);
    }
    
    public EntitySkeletonBoss(final World world, final Vector3 vec) {
        this(world);
        this.setPosition(vec.x, vec.y, vec.z);
    }
    
    public boolean isInWater() {
        return false;
    }
    
    public boolean handleWaterMovement() {
        return false;
    }
    
    public void updateRiderPosition() {
        if (this.riddenByEntity != null) {
            final double offsetX = Math.sin(this.rotationYaw * 3.141592653589793 / 180.0);
            final double offsetZ = Math.cos(this.rotationYaw * 3.141592653589793 / 180.0);
            final double offsetY = 2.0 * Math.cos((this.throwTimer + this.postThrowDelay) * 0.05f);
            this.riddenByEntity.setPosition(this.posX + offsetX, this.posY + this.getMountedYOffset() + this.riddenByEntity.getYOffset() + offsetY, this.posZ + offsetZ);
        }
    }
    
    public void knockBack(final Entity par1Entity, final float par2, final double par3, final double par5) {
    }
    
    public void onCollideWithPlayer(final EntityPlayer par1EntityPlayer) {
        if (this.riddenByEntity == null && this.postThrowDelay == 0 && this.throwTimer == 0 && par1EntityPlayer.equals((Object)this.targetEntity) && this.deathTicks == 0) {
            if (!this.worldObj.isRemote) {
                GalacticraftCore.packetPipeline.sendToAllAround(new PacketSimple(PacketSimple.EnumSimplePacket.C_PLAY_SOUND_BOSS_LAUGH, new Object[0]), new NetworkRegistry.TargetPoint(this.worldObj.provider.dimensionId, this.posX, this.posY, this.posZ, 40.0));
                par1EntityPlayer.mountEntity((Entity)this);
            }
            this.throwTimer = 40;
        }
        super.onCollideWithPlayer(par1EntityPlayer);
    }
    
    public boolean isAIEnabled() {
        return true;
    }
    
    public boolean canBePushed() {
        return false;
    }
    
    protected String getLivingSound() {
        return null;
    }
    
    protected String getHurtSound() {
        this.playSound(GalacticraftCore.TEXTURE_PREFIX + "entity.bossliving", this.getSoundVolume(), this.getSoundPitch() + 6.0f);
        return null;
    }
    
    protected String getDeathSound() {
        return null;
    }
    
    protected void onDeathUpdate() {
        ++this.deathTicks;
        if (this.deathTicks >= 180 && this.deathTicks <= 200) {
            final float f = (this.rand.nextFloat() - 0.5f) * 1.5f;
            final float f2 = (this.rand.nextFloat() - 0.5f) * 2.0f;
            final float f3 = (this.rand.nextFloat() - 0.5f) * 1.5f;
            this.worldObj.spawnParticle("hugeexplosion", this.posX + f, this.posY + 2.0 + f2, this.posZ + f3, 0.0, 0.0, 0.0);
        }
        if (!this.worldObj.isRemote) {
            if (this.deathTicks >= 180 && this.deathTicks % 5 == 0) {
                GalacticraftCore.packetPipeline.sendToAllAround(new PacketSimple(PacketSimple.EnumSimplePacket.C_PLAY_SOUND_EXPLODE, new Object[0]), new NetworkRegistry.TargetPoint(this.worldObj.provider.dimensionId, this.posX, this.posY, this.posZ, 40.0));
            }
            if (this.deathTicks > 150 && this.deathTicks % 5 == 0) {
                int i = 30;
                while (i > 0) {
                    final int j = EntityXPOrb.getXPSplit(i);
                    i -= j;
                    this.worldObj.spawnEntityInWorld((Entity)new EntityXPOrb(this.worldObj, this.posX, this.posY, this.posZ, j));
                }
            }
            if (this.deathTicks == 1) {
                GalacticraftCore.packetPipeline.sendToAllAround(new PacketSimple(PacketSimple.EnumSimplePacket.C_PLAY_SOUND_BOSS_DEATH, new Object[0]), new NetworkRegistry.TargetPoint(this.worldObj.provider.dimensionId, this.posX, this.posY, this.posZ, 40.0));
            }
        }
        this.moveEntity(0.0, 0.10000000149011612, 0.0);
        final float n = this.rotationYaw + 20.0f;
        this.rotationYaw = n;
        this.renderYawOffset = n;
        if (this.deathTicks == 200 && !this.worldObj.isRemote) {
            int i = 20;
            while (i > 0) {
                final int j = EntityXPOrb.getXPSplit(i);
                i -= j;
                this.worldObj.spawnEntityInWorld((Entity)new EntityXPOrb(this.worldObj, this.posX, this.posY, this.posZ, j));
            }
            if (!this.worldObj.isRemote) {
                for (final TileEntity tile : new ArrayList<TileEntity>(this.worldObj.loadedTileEntityList)) {
                    if (tile instanceof TileEntityTreasureChest) {
                        final double d3 = tile.xCoord + 0.5 - this.posX;
                        final double d4 = tile.yCoord + 0.5 - this.posY;
                        final double d5 = tile.zCoord + 0.5 - this.posZ;
                        final double dSq = d3 * d3 + d4 * d4 + d5 * d5;
                        final TileEntityTreasureChest chest = (TileEntityTreasureChest)tile;
                        if (dSq < 10000.0) {
                            if (!chest.locked) {
                                chest.locked = true;
                            }
                            for (int k = 0; k < chest.getSizeInventory(); ++k) {
                                chest.setInventorySlotContents(k, null);
                            }
                            final ChestGenHooks info = ChestGenHooks.getInfo("dungeonChest");
                            WeightedRandomChestContent.generateChestContents(this.rand, info.getItems(this.rand), (IInventory)chest, info.getCount(this.rand));
                            WeightedRandomChestContent.generateChestContents(this.rand, info.getItems(this.rand), (IInventory)chest, info.getCount(this.rand));
                            final ItemStack schematic = this.getGuaranteedLoot(this.rand);
                            final int slot = this.rand.nextInt(chest.getSizeInventory());
                            chest.setInventorySlotContents(slot, schematic);
                            break;
                        }
                        continue;
                    }
                }
            }
            this.entityDropItem(new ItemStack(GCItems.key, 1, 0), 0.5f);
            super.setDead();
            if (this.spawner != null) {
                this.spawner.isBossDefeated = true;
                this.spawner.boss = null;
                this.spawner.spawned = false;
            }
        }
    }
    
    @SideOnly(Side.CLIENT)
    public ItemStack getHeldItem() {
        return EntitySkeletonBoss.defaultHeldItem;
    }
    
    public EnumCreatureAttribute getCreatureAttribute() {
        return EnumCreatureAttribute.UNDEAD;
    }
    
    public void setDead() {
        if (this.spawner != null) {
            this.spawner.isBossDefeated = false;
            this.spawner.boss = null;
            this.spawner.spawned = false;
        }
        super.setDead();
    }
    
    public void onLivingUpdate() {
        if (this.ticks >= Long.MAX_VALUE) {
            this.ticks = 1L;
        }
        ++this.ticks;
        if (!this.worldObj.isRemote && this.getHealth() <= 150.0 * ConfigManagerCore.dungeonBossHealthMod / 2.0) {
            this.getEntityAttribute(SharedMonsterAttributes.movementSpeed);
        }
        final EntityPlayer player = this.worldObj.getClosestPlayer(this.posX, this.posY, this.posZ, 20.0);
        if (player != null && !player.equals((Object)this.targetEntity)) {
            if (this.getDistanceSqToEntity((Entity)player) < 400.0) {
                this.getNavigator().getPathToEntityLiving((Entity)player);
                this.targetEntity = (Entity)player;
            }
        }
        else {
            this.targetEntity = null;
        }
        if (this.throwTimer > 0) {
            --this.throwTimer;
        }
        if (this.postThrowDelay > 0) {
            --this.postThrowDelay;
        }
        new Vector3((Entity)this);
        if (this.roomCoords != null && this.roomSize != null) {
            final List<Entity> entitiesWithin = (List<Entity>)this.worldObj.getEntitiesWithinAABB((Class)EntityPlayer.class, AxisAlignedBB.getBoundingBox((double)(this.roomCoords.intX() - 1), (double)(this.roomCoords.intY() - 1), (double)(this.roomCoords.intZ() - 1), (double)(this.roomCoords.intX() + this.roomSize.intX()), (double)(this.roomCoords.intY() + this.roomSize.intY()), (double)(this.roomCoords.intZ() + this.roomSize.intZ())));
            this.entitiesWithin = entitiesWithin.size();
            if (this.entitiesWithin == 0 && this.entitiesWithinLast != 0) {
                final List<EntityPlayer> entitiesWithin2 = (List<EntityPlayer>)this.worldObj.getEntitiesWithinAABB((Class)EntityPlayer.class, AxisAlignedBB.getBoundingBox((double)(this.roomCoords.intX() - 11), (double)(this.roomCoords.intY() - 11), (double)(this.roomCoords.intZ() - 11), (double)(this.roomCoords.intX() + this.roomSize.intX() + 10), (double)(this.roomCoords.intY() + this.roomSize.intY() + 10), (double)(this.roomCoords.intZ() + this.roomSize.intZ() + 10)));
                for (final EntityPlayer p : entitiesWithin2) {
                    p.addChatMessage((IChatComponent)new ChatComponentText(GCCoreUtil.translate("gui.skeletonBoss.message")));
                }
                this.setDead();
                if (this.spawner != null) {
                    this.spawner.playerCheated = true;
                }
                return;
            }
            this.entitiesWithinLast = this.entitiesWithin;
        }
        if (this.riddenByEntity != null && this.throwTimer == 0) {
            this.postThrowDelay = 20;
            this.thrownEntity = this.riddenByEntity;
            if (!this.worldObj.isRemote) {
                this.riddenByEntity.mountEntity((Entity)null);
            }
        }
        if (this.thrownEntity != null && this.postThrowDelay == 18) {
            double d0;
            double d2;
            for (d0 = this.posX - this.thrownEntity.posX, d2 = this.posZ - this.thrownEntity.posZ; d0 * d0 + d2 * d2 < 1.0E-4; d0 = (Math.random() - Math.random()) * 0.01, d2 = (Math.random() - Math.random()) * 0.01) {}
            if (!this.worldObj.isRemote) {
                GalacticraftCore.packetPipeline.sendToAllAround(new PacketSimple(PacketSimple.EnumSimplePacket.C_PLAY_SOUND_BOW, new Object[0]), new NetworkRegistry.TargetPoint(this.worldObj.provider.dimensionId, this.posX, this.posY, this.posZ, 40.0));
            }
            ((EntityPlayer)this.thrownEntity).attackedAtYaw = (float)(Math.atan2(d2, d0) * 180.0 / 3.141592653589793) - this.rotationYaw;
            this.thrownEntity.isAirBorne = true;
            final float f = MathHelper.sqrt_double(d0 * d0 + d2 * d2);
            final float f2 = 2.4f;
            final Entity thrownEntity = this.thrownEntity;
            thrownEntity.motionX /= 2.0;
            final Entity thrownEntity2 = this.thrownEntity;
            thrownEntity2.motionY /= 2.0;
            final Entity thrownEntity3 = this.thrownEntity;
            thrownEntity3.motionZ /= 2.0;
            final Entity thrownEntity4 = this.thrownEntity;
            thrownEntity4.motionX -= d0 / f * 2.4000000953674316;
            final Entity thrownEntity5 = this.thrownEntity;
            thrownEntity5.motionY += 0.48000001907348633;
            final Entity thrownEntity6 = this.thrownEntity;
            thrownEntity6.motionZ -= d2 / f * 2.4000000953674316;
            if (this.thrownEntity.motionY > 0.4000000059604645) {
                this.thrownEntity.motionY = 0.4000000059604645;
            }
        }
        super.onLivingUpdate();
    }
    
    public void onDeath(final DamageSource par1DamageSource) {
        super.onDeath(par1DamageSource);
        if (par1DamageSource.getSourceOfDamage() instanceof EntityArrow && par1DamageSource.getEntity() instanceof EntityPlayer) {
            final EntityPlayer var2 = (EntityPlayer)par1DamageSource.getEntity();
            final double var3 = var2.posX - this.posX;
            final double var4 = var2.posZ - this.posZ;
            if (var3 * var3 + var4 * var4 >= 2500.0) {
                var2.triggerAchievement((StatBase)AchievementList.snipeSkeleton);
            }
        }
    }
    
    protected Item getDropItem() {
        return Items.arrow;
    }
    
    protected void dropFewItems(final boolean par1, final int par2) {
    }
    
    public EntityItem entityDropItem(final ItemStack par1ItemStack, final float par2) {
        final EntityItem entityitem = new EntityItem(this.worldObj, this.posX, this.posY + par2, this.posZ, par1ItemStack);
        entityitem.motionY = -2.0;
        entityitem.delayBeforeCanPickup = 10;
        if (this.captureDrops) {
            this.capturedDrops.add(entityitem);
        }
        else {
            this.worldObj.spawnEntityInWorld((Entity)entityitem);
        }
        return entityitem;
    }
    
    protected void dropRareDrop(final int par1) {
        if (par1 > 0) {
            final ItemStack var2 = new ItemStack((Item)Items.bow);
            EnchantmentHelper.addRandomEnchantment(this.rand, var2, 5);
            this.entityDropItem(var2, 0.0f);
        }
        else {
            this.dropItem((Item)Items.bow, 1);
        }
    }
    
    public boolean canBreath() {
        return true;
    }
    
    public float getExperienceToSpawn() {
        return 50.0f;
    }
    
    public double getDistanceToSpawn() {
        return 40.0;
    }
    
    public ItemStack getGuaranteedLoot(final Random rand) {
        final List<ItemStack> stackList = (List<ItemStack>)GalacticraftRegistry.getDungeonLoot(1);
        return stackList.get(rand.nextInt(stackList.size())).copy();
    }
    
    public void writeEntityToNBT(final NBTTagCompound nbt) {
        super.writeEntityToNBT(nbt);
        if (this.roomCoords != null) {
            nbt.setDouble("roomCoordsX", this.roomCoords.x);
            nbt.setDouble("roomCoordsY", this.roomCoords.y);
            nbt.setDouble("roomCoordsZ", this.roomCoords.z);
            nbt.setDouble("roomSizeX", this.roomSize.x);
            nbt.setDouble("roomSizeY", this.roomSize.y);
            nbt.setDouble("roomSizeZ", this.roomSize.z);
        }
    }
    
    public void readEntityFromNBT(final NBTTagCompound nbt) {
        super.readEntityFromNBT(nbt);
        this.roomCoords = new Vector3();
        this.roomCoords.x = nbt.getDouble("roomCoordsX");
        this.roomCoords.y = nbt.getDouble("roomCoordsY");
        this.roomCoords.z = nbt.getDouble("roomCoordsZ");
        this.roomSize = new Vector3();
        this.roomSize.x = nbt.getDouble("roomSizeX");
        this.roomSize.y = nbt.getDouble("roomSizeY");
        this.roomSize.z = nbt.getDouble("roomSizeZ");
    }
    
    public void attackEntityWithRangedAttack(final EntityLivingBase entitylivingbase, final float f) {
        if (this.riddenByEntity != null) {
            return;
        }
        Entity var1;
        if (this.worldObj.provider instanceof IGalacticraftWorldProvider) {
            var1 = (Entity)new EntityArrowGC(this.worldObj, (EntityLivingBase)this, entitylivingbase, 0.3f, 12.0f);
        }
        else {
            var1 = (Entity)new EntityArrow(this.worldObj, (EntityLivingBase)this, entitylivingbase, 1.6f, 12.0f);
        }
        this.worldObj.playSoundAtEntity((Entity)this, "random.bow", 1.0f, 1.0f / (this.getRNG().nextFloat() * 0.4f + 0.8f));
        this.worldObj.spawnEntityInWorld(var1);
    }
    
    public void setRoom(final Vector3 roomCoords, final Vector3 roomSize) {
        this.roomCoords = roomCoords;
        this.roomSize = roomSize;
    }
    
    public void onBossSpawned(final TileEntityDungeonSpawner spawner) {
        this.spawner = spawner;
    }
    
    public boolean shouldIgnoreShiftExit() {
        return true;
    }
    
    static {
        defaultHeldItem = new ItemStack((Item)Items.bow, 1);
    }
}
