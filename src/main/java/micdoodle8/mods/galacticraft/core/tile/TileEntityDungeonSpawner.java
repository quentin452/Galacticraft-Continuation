package micdoodle8.mods.galacticraft.core.tile;

import micdoodle8.mods.galacticraft.api.vector.*;
import net.minecraft.tileentity.*;
import net.minecraft.util.*;
import net.minecraft.entity.monster.*;
import net.minecraft.world.*;
import net.minecraft.entity.player.*;
import net.minecraft.entity.*;
import java.lang.reflect.*;
import java.util.*;
import micdoodle8.mods.galacticraft.core.entities.*;
import net.minecraft.nbt.*;

public class TileEntityDungeonSpawner extends TileEntityAdvanced
{
    public Class<? extends IBoss> bossClass;
    public IBoss boss;
    public boolean spawned;
    public boolean isBossDefeated;
    public boolean playerInRange;
    public boolean lastPlayerInRange;
    public boolean playerCheated;
    private Vector3 roomCoords;
    private Vector3 roomSize;
    
    public TileEntityDungeonSpawner() {
        this((Class<? extends IBoss>)EntitySkeletonBoss.class);
    }
    
    public TileEntityDungeonSpawner(final Class<? extends IBoss> bossClass) {
        this.bossClass = bossClass;
    }
    
    public void updateEntity() {
        super.updateEntity();
        if (this.roomCoords == null) {
            return;
        }
        if (!this.worldObj.isRemote) {
            final Vector3 thisVec = new Vector3((TileEntity)this);
            final List<Entity> l = (List<Entity>)this.worldObj.getEntitiesWithinAABB((Class)this.bossClass, AxisAlignedBB.getBoundingBox(thisVec.x - 15.0, thisVec.y - 15.0, thisVec.z - 15.0, thisVec.x + 15.0, thisVec.y + 15.0, thisVec.z + 15.0));
            for (final Entity e : l) {
                if (!e.isDead) {
                    (this.boss = (IBoss)e).setRoom(this.roomCoords, this.roomSize);
                    this.spawned = true;
                    this.isBossDefeated = false;
                }
            }
            List<Entity> entitiesWithin = (List<Entity>)this.worldObj.getEntitiesWithinAABB((Class)EntityMob.class, AxisAlignedBB.getBoundingBox((double)(this.roomCoords.intX() - 4), (double)(this.roomCoords.intY() - 4), (double)(this.roomCoords.intZ() - 4), (double)(this.roomCoords.intX() + this.roomSize.intX() + 3), (double)(this.roomCoords.intY() + this.roomSize.intY() + 3), (double)(this.roomCoords.intZ() + this.roomSize.intZ() + 3)));
            for (final Entity mob : entitiesWithin) {
                if (this.getDisabledCreatures().contains(mob.getClass())) {
                    mob.setDead();
                }
            }
            if (this.boss == null && !this.isBossDefeated) {
                try {
                    final Constructor<?> c = this.bossClass.getConstructor(World.class);
                    this.boss = (IBoss)c.newInstance(this.worldObj);
                    ((Entity)this.boss).setPosition(this.xCoord + 0.5, this.yCoord + 1.0, this.zCoord + 0.5);
                    this.boss.setRoom(this.roomCoords, this.roomSize);
                }
                catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
            entitiesWithin = (List<Entity>)this.worldObj.getEntitiesWithinAABB((Class)EntityPlayer.class, AxisAlignedBB.getBoundingBox((double)(this.roomCoords.intX() - 1), (double)(this.roomCoords.intY() - 1), (double)(this.roomCoords.intZ() - 1), (double)(this.roomCoords.intX() + this.roomSize.intX()), (double)(this.roomCoords.intY() + this.roomSize.intY()), (double)(this.roomCoords.intZ() + this.roomSize.intZ())));
            if (this.playerCheated && !entitiesWithin.isEmpty()) {
                this.isBossDefeated = false;
                this.spawned = false;
                this.lastPlayerInRange = false;
                this.playerCheated = false;
            }
            this.playerInRange = !entitiesWithin.isEmpty();
            if (this.playerInRange && !this.lastPlayerInRange && this.boss != null && !this.spawned && this.boss instanceof Entity) {
                this.worldObj.spawnEntityInWorld((Entity)this.boss);
                this.playSpawnSound((Entity)this.boss);
                this.spawned = true;
                this.boss.onBossSpawned(this);
                this.boss.setRoom(this.roomCoords, this.roomSize);
            }
            this.lastPlayerInRange = this.playerInRange;
        }
    }
    
    public void playSpawnSound(final Entity entity) {
    }
    
    public List<Class<? extends EntityLiving>> getDisabledCreatures() {
        final List<Class<? extends EntityLiving>> list = new ArrayList<Class<? extends EntityLiving>>();
        list.add((Class<? extends EntityLiving>)EntityEvolvedSkeleton.class);
        list.add((Class<? extends EntityLiving>)EntityEvolvedCreeper.class);
        list.add((Class<? extends EntityLiving>)EntityEvolvedZombie.class);
        list.add((Class<? extends EntityLiving>)EntityEvolvedSpider.class);
        return list;
    }
    
    public void setRoom(final Vector3 coords, final Vector3 size) {
        this.roomCoords = coords;
        this.roomSize = size;
    }
    
    public void readFromNBT(final NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        this.spawned = nbt.getBoolean("spawned");
        final boolean getBoolean = nbt.getBoolean("playerInRange");
        this.lastPlayerInRange = getBoolean;
        this.playerInRange = getBoolean;
        this.isBossDefeated = nbt.getBoolean("defeated");
        this.playerCheated = nbt.getBoolean("playerCheated");
        try {
            this.bossClass = (Class<? extends IBoss>)Class.forName(nbt.getString("bossClass"));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        this.roomCoords = new Vector3();
        this.roomCoords.x = nbt.getDouble("roomCoordsX");
        this.roomCoords.y = nbt.getDouble("roomCoordsY");
        this.roomCoords.z = nbt.getDouble("roomCoordsZ");
        this.roomSize = new Vector3();
        this.roomSize.x = nbt.getDouble("roomSizeX");
        this.roomSize.y = nbt.getDouble("roomSizeY");
        this.roomSize.z = nbt.getDouble("roomSizeZ");
    }
    
    public void writeToNBT(final NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setBoolean("spawned", this.spawned);
        nbt.setBoolean("playerInRange", this.playerInRange);
        nbt.setBoolean("defeated", this.isBossDefeated);
        nbt.setBoolean("playerCheated", this.playerCheated);
        nbt.setString("bossClass", this.bossClass.getCanonicalName());
        if (this.roomCoords != null) {
            nbt.setDouble("roomCoordsX", this.roomCoords.x);
            nbt.setDouble("roomCoordsY", this.roomCoords.y);
            nbt.setDouble("roomCoordsZ", this.roomCoords.z);
            nbt.setDouble("roomSizeX", this.roomSize.x);
            nbt.setDouble("roomSizeY", this.roomSize.y);
            nbt.setDouble("roomSizeZ", this.roomSize.z);
        }
    }
    
    public double getPacketRange() {
        return 0.0;
    }
    
    public int getPacketCooldown() {
        return 0;
    }
    
    public boolean isNetworkedTile() {
        return false;
    }
}
