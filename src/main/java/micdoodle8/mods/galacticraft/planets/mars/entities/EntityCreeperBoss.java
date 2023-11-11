package micdoodle8.mods.galacticraft.planets.mars.entities;

import micdoodle8.mods.galacticraft.core.entities.EntityAIArrowAttack;
import net.minecraft.entity.monster.*;
import micdoodle8.mods.galacticraft.api.entity.*;
import net.minecraft.entity.boss.*;
import micdoodle8.mods.galacticraft.core.tile.*;
import micdoodle8.mods.galacticraft.api.vector.*;
import net.minecraft.world.*;
import micdoodle8.mods.galacticraft.core.entities.*;
import net.minecraft.entity.ai.*;
import micdoodle8.mods.galacticraft.core.*;
import cpw.mods.fml.common.network.*;
import micdoodle8.mods.galacticraft.core.network.*;
import net.minecraft.tileentity.*;
import micdoodle8.mods.galacticraft.planets.mars.tile.*;
import net.minecraftforge.common.*;
import net.minecraft.inventory.*;
import micdoodle8.mods.galacticraft.planets.mars.items.*;
import micdoodle8.mods.galacticraft.core.util.*;
import net.minecraft.item.*;
import net.minecraft.init.*;
import net.minecraft.entity.item.*;
import net.minecraft.enchantment.*;
import micdoodle8.mods.galacticraft.api.*;
import java.util.*;
import net.minecraft.entity.player.*;
import micdoodle8.mods.galacticraft.core.entities.player.*;
import micdoodle8.mods.galacticraft.api.recipe.*;
import micdoodle8.mods.galacticraft.planets.asteroids.*;
import net.minecraft.nbt.*;
import net.minecraft.entity.*;
import net.minecraft.util.*;

public class EntityCreeperBoss extends EntityMob implements IEntityBreathable, IBossDisplayData, IRangedAttackMob, IBoss
{
    protected long ticks;
    private TileEntityDungeonSpawner spawner;
    public int headsRemaining;
    public Entity targetEntity;
    public int deathTicks;
    public int entitiesWithin;
    public int entitiesWithinLast;
    private Vector3 roomCoords;
    private Vector3 roomSize;

    public EntityCreeperBoss(final World par1World) {
        super(par1World);
        this.ticks = 0L;
        this.headsRemaining = 3;
        this.deathTicks = 0;
        this.setSize(2.0f, 7.0f);
        this.isImmuneToFire = true;
        this.tasks.addTask(1, new EntityAISwimming((EntityLiving)this));
        this.tasks.addTask(2, new EntityAIArrowAttack((IRangedAttackMob)this, 1.0, 25, 20.0f));
        this.tasks.addTask(2, (EntityAIBase)new EntityAIWander((EntityCreature)this, 1.0));
        this.tasks.addTask(3, (EntityAIBase)new EntityAIWatchClosest((EntityLiving)this, (Class<EntityPlayer>)EntityPlayer.class, 8.0f));
        this.tasks.addTask(3, (EntityAIBase)new EntityAILookIdle((EntityLiving)this));
        this.targetTasks.addTask(1, (EntityAIBase)new EntityAIHurtByTarget((EntityCreature)this, false));
        this.targetTasks.addTask(2, (EntityAIBase)new EntityAINearestAttackableTarget((EntityCreature)this, (Class<EntityPlayer>)EntityPlayer.class, 0, true));
    }

    public boolean attackEntityFrom(final DamageSource damageSource, final float damage) {
        if (!damageSource.getDamageType().equals("fireball")) {
            return false;
        }
        if (this.isEntityInvulnerable()) {
            return false;
        }
        if (!super.attackEntityFrom(damageSource, damage)) {
            return false;
        }
        final Entity entity = damageSource.getEntity();
        if (this.riddenByEntity != entity && this.ridingEntity != entity) {
            if (entity != this) {
                this.entityToAttack = entity;
            }
            return true;
        }
        return true;
    }

    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(200.0 * ConfigManagerCore.dungeonBossHealthMod);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.05000000074505806);
    }

    public EntityCreeperBoss(final World world, final Vector3 vec) {
        this(world);
        this.setPosition(vec.x, vec.y, vec.z);
    }

    public void knockBack(final Entity par1Entity, final float par2, final double par3, final double par5) {
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
        this.headsRemaining = 0;
        if (this.deathTicks >= 180 && this.deathTicks <= 200) {
            final float f = (this.rand.nextFloat() - 0.5f) * 1.5f;
            final float f2 = (this.rand.nextFloat() - 0.5f) * 2.0f;
            final float f3 = (this.rand.nextFloat() - 0.5f) * 1.5f;
            this.worldObj.spawnParticle("hugeexplosion", this.posX + f, this.posY + 2.0 + f2, this.posZ + f3, 0.0, 0.0, 0.0);
        }
        if (!this.worldObj.isRemote) {
            if (this.deathTicks >= 180 && this.deathTicks % 5 == 0) {
                GalacticraftCore.packetPipeline.sendToAllAround((IPacket)new PacketSimple(PacketSimple.EnumSimplePacket.C_PLAY_SOUND_EXPLODE, new Object[0]), new NetworkRegistry.TargetPoint(this.worldObj.provider.dimensionId, this.posX, this.posY, this.posZ, 40.0));
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
                GalacticraftCore.packetPipeline.sendToAllAround((IPacket)new PacketSimple(PacketSimple.EnumSimplePacket.C_PLAY_SOUND_BOSS_DEATH, new Object[0]), new NetworkRegistry.TargetPoint(this.worldObj.provider.dimensionId, this.posX, this.posY, this.posZ, 40.0));
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
            for (final TileEntity tile : (List<TileEntity>)this.worldObj.loadedTileEntityList) {
                if (tile instanceof TileEntityTreasureChestMars) {
                    final double d3 = tile.xCoord + 0.5 - this.posX;
                    final double d4 = tile.yCoord + 0.5 - this.posY;
                    final double d5 = tile.zCoord + 0.5 - this.posZ;
                    final double dSq = d3 * d3 + d4 * d4 + d5 * d5;
                    final TileEntityTreasureChestMars chest = (TileEntityTreasureChestMars)tile;
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
                        WeightedRandomChestContent.generateChestContents(this.rand, info.getItems(this.rand), (IInventory)chest, info.getCount(this.rand));
                        chest.setInventorySlotContents(this.rand.nextInt(chest.getSizeInventory()), this.getGuaranteedLoot(this.rand));
                        break;
                    }
                }
            }
            this.entityDropItem(new ItemStack(MarsItems.key, 1, 0), 0.5f);
            super.setDead();
            if (this.spawner != null) {
                this.spawner.isBossDefeated = true;
                this.spawner.boss = null;
                this.spawner.spawned = false;
            }
        }
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
        if (this.getHealth() <= 0.0f) {
            this.headsRemaining = 0;
        }
        else if (this.getHealth() <= this.getMaxHealth() / 3.0) {
            this.headsRemaining = 1;
        }
        else if (this.getHealth() <= 2.0 * (this.getMaxHealth() / 3.0)) {
            this.headsRemaining = 2;
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
        new Vector3((Entity)this);
        if (this.roomCoords != null && this.roomSize != null) {
            final List<Entity> entitiesWithin = (List<Entity>)this.worldObj.getEntitiesWithinAABB((Class<EntityPlayer>)EntityPlayer.class, AxisAlignedBB.getBoundingBox((double)(this.roomCoords.intX() - 1), (double)(this.roomCoords.intY() - 1), (double)(this.roomCoords.intZ() - 1), (double)(this.roomCoords.intX() + this.roomSize.intX()), (double)(this.roomCoords.intY() + this.roomSize.intY()), (double)(this.roomCoords.intZ() + this.roomSize.intZ())));
            this.entitiesWithin = entitiesWithin.size();
            if (this.entitiesWithin == 0 && this.entitiesWithinLast != 0) {
                final List<EntityPlayer> entitiesWithin2 = (List<EntityPlayer>)this.worldObj.getEntitiesWithinAABB((Class<EntityPlayer>)EntityPlayer.class, AxisAlignedBB.getBoundingBox((double)(this.roomCoords.intX() - 11), (double)(this.roomCoords.intY() - 11), (double)(this.roomCoords.intZ() - 11), (double)(this.roomCoords.intX() + this.roomSize.intX() + 10), (double)(this.roomCoords.intY() + this.roomSize.intY() + 10), (double)(this.roomCoords.intZ() + this.roomSize.intZ() + 10)));
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
        super.onLivingUpdate();
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

    public ItemStack getGuaranteedLoot(final Random rand) {
        final List<ItemStack> stackList = new LinkedList<ItemStack>();
        stackList.addAll(GalacticraftRegistry.getDungeonLoot(2));
        boolean hasT3Rocket = false;
        boolean hasAstroMiner = false;
        final EntityPlayer player = this.worldObj.getClosestPlayer(this.posX, this.posY, this.posZ, 20.0);
        if (player != null) {
            final GCPlayerStats stats = GCPlayerStats.get((EntityPlayerMP)player);
            if (stats != null) {
                for (final ISchematicPage page : stats.unlockedSchematics) {
                    if (page.getPageID() == ConfigManagerAsteroids.idSchematicRocketT3) {
                        hasT3Rocket = true;
                    }
                    else {
                        if (page.getPageID() != ConfigManagerAsteroids.idSchematicRocketT3 + 1) {
                            continue;
                        }
                        hasAstroMiner = true;
                    }
                }
            }
        }
        if (hasT3Rocket && hasAstroMiner) {
            if (stackList.size() == 3) {
                stackList.remove(1 + rand.nextInt(2));
            }
            else {
                stackList.remove(2);
                stackList.remove(1);
            }
        }
        else if (hasT3Rocket) {
            stackList.remove(1);
        }
        else if (hasAstroMiner) {
            stackList.remove(2);
        }
        final int range = hasT3Rocket ? stackList.size() : 2;
        return stackList.get(rand.nextInt(range)).copy();
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

    private void func_82216_a(final int par1, final EntityLivingBase par2EntityLivingBase) {
        this.func_82209_a(par1, par2EntityLivingBase.posX, par2EntityLivingBase.posY + par2EntityLivingBase.getEyeHeight() * 0.5, par2EntityLivingBase.posZ);
    }

    private void func_82209_a(final int par1, final double par2, final double par4, final double par6) {
        this.worldObj.playAuxSFXAtEntity((EntityPlayer)null, 1014, (int)this.posX, (int)this.posY, (int)this.posZ, 0);
        final double d3 = this.func_82214_u(par1);
        final double d4 = this.func_82208_v(par1);
        final double d5 = this.func_82213_w(par1);
        final double d6 = par2 - d3;
        final double d7 = par4 - d4;
        final double d8 = par6 - d5;
        final EntityProjectileTNT entitywitherskull = new EntityProjectileTNT(this.worldObj, (EntityLivingBase)this, d6 * 0.5, d7 * 0.5, d8 * 0.5);
        entitywitherskull.posY = d4;
        entitywitherskull.posX = d3;
        entitywitherskull.posZ = d5;
        this.worldObj.spawnEntityInWorld((Entity)entitywitherskull);
    }

    private double func_82214_u(final int par1) {
        if (par1 <= 0) {
            return this.posX;
        }
        final float f = (this.renderYawOffset + 180 * (par1 - 1)) / 180.0f * 3.1415927f;
        final float f2 = MathHelper.cos(f);
        return this.posX + f2 * 1.3;
    }

    private double func_82208_v(final int par1) {
        return (par1 <= 0) ? (this.posY + 6.0) : (this.posY + 4.2);
    }

    private double func_82213_w(final int par1) {
        if (par1 <= 0) {
            return this.posZ;
        }
        final float f = (this.renderYawOffset + 180 * (par1 - 1)) / 180.0f * 3.1415927f;
        final float f2 = MathHelper.sin(f);
        return this.posZ + f2 * 1.3;
    }

    public void attackEntityWithRangedAttack(final EntityLivingBase entitylivingbase, final float f) {
        this.func_82216_a(0, entitylivingbase);
    }

    public void setRoom(final Vector3 roomCoords, final Vector3 roomSize) {
        this.roomCoords = roomCoords;
        this.roomSize = roomSize;
    }

    public void onBossSpawned(final TileEntityDungeonSpawner spawner) {
        this.spawner = spawner;
    }
}
