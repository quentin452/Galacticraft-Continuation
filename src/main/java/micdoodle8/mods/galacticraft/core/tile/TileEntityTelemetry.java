package micdoodle8.mods.galacticraft.core.tile;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.WorldProvider;

import com.mojang.authlib.GameProfile;

import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import micdoodle8.mods.galacticraft.api.entity.ITelemetry;
import micdoodle8.mods.galacticraft.api.prefab.entity.EntitySpaceshipBase;
import micdoodle8.mods.galacticraft.api.vector.BlockVec3;
import micdoodle8.mods.galacticraft.api.vector.BlockVec3Dim;
import micdoodle8.mods.galacticraft.core.GalacticraftCore;
import micdoodle8.mods.galacticraft.core.entities.player.GCPlayerStats;
import micdoodle8.mods.galacticraft.core.network.PacketSimple;
import micdoodle8.mods.galacticraft.core.network.PacketSimple.EnumSimplePacket;
import micdoodle8.mods.galacticraft.core.util.GCLog;
import micdoodle8.mods.galacticraft.core.util.WorldUtil;

public class TileEntityTelemetry extends TileEntity {

    public Class<? extends Entity> clientClass;
    public int[] clientData = { -1 };
    public String clientName;
    public GameProfile clientGameProfile = null;

    public static HashSet<BlockVec3Dim> loadedList = new HashSet<>();
    public Entity linkedEntity;
    private UUID toUpdate = null;
    private int pulseRate = 400;
    private int lastHurttime = 0;
    private int ticks = 0;

    @Override
    public void validate() {
        super.validate();
        if (this.worldObj.isRemote) {
            loadedList.add(new BlockVec3Dim(this));
        }
    }

    @Override
    public void invalidate() {
        super.invalidate();
        if (this.worldObj.isRemote) {
            loadedList.remove(new BlockVec3Dim(this));
        }
    }

    @Override
    public void updateEntity() {
        if (!this.worldObj.isRemote && ++this.ticks % 2 == 0) {
            if (this.toUpdate != null) {
                this.addTrackedEntity(this.toUpdate);
                this.toUpdate = null;
            }

            String name;
            final int[] data = { -1, -1, -1, -1, -1 };
            String strUUID = "";

            if (this.linkedEntity != null) {
                // Help the Garbage Collector
                if (this.linkedEntity.isDead) {
                    this.linkedEntity = null;
                    name = "";
                    // TODO: track players after death and respawn? or not?
                } else {
                    if (this.linkedEntity instanceof EntityPlayerMP) {
                        name = "$" + this.linkedEntity.getCommandSenderName();
                    } else {
                        name = EntityList.classToStringMapping.get(this.linkedEntity.getClass());
                    }

                    if (name == null) {
                        GCLog.info(
                            "Telemetry Unit: Error finding name for " + this.linkedEntity.getClass()
                                .getSimpleName());
                        name = "";
                    }

                    final double xmotion = this.linkedEntity.motionX;
                    final double ymotion = this.linkedEntity instanceof EntityLivingBase
                        ? this.linkedEntity.motionY + 0.078D
                        : this.linkedEntity.motionY;
                    final double zmotion = this.linkedEntity.motionZ;
                    data[2] = (int) (MathHelper.sqrt_double(xmotion * xmotion + ymotion * ymotion + zmotion * zmotion)
                        * 2000D);

                    if (this.linkedEntity instanceof ITelemetry) {
                        ((ITelemetry) this.linkedEntity).transmitData(data);
                    } else if (this.linkedEntity instanceof EntityLivingBase) {
                        EntityLivingBase eLiving = (EntityLivingBase) this.linkedEntity;
                        data[0] = eLiving.hurtTime;

                        // Calculate a "pulse rate" based on motion and taking damage
                        this.pulseRate--;
                        if (eLiving.hurtTime > this.lastHurttime) {
                            this.pulseRate += 100;
                        }
                        this.lastHurttime = eLiving.hurtTime;
                        if (eLiving.ridingEntity != null) {
                            data[2] /= 4; // reduced pulse effect if riding a vehicle
                        } else if (data[2] > 1) {
                            this.pulseRate += 2;
                        }
                        this.pulseRate += Math.max(data[2] - this.pulseRate, 0) / 4;
                        if (this.pulseRate > 2000) {
                            this.pulseRate = 2000;
                        }
                        if (this.pulseRate < 400) {
                            this.pulseRate = 400;
                        }
                        data[2] = this.pulseRate / 10;

                        data[1] = (int) (eLiving.getHealth() * 100 / eLiving.getMaxHealth());
                        if (eLiving instanceof EntityPlayerMP) {
                            data[3] = ((EntityPlayerMP) eLiving).getFoodStats()
                                .getFoodLevel() * 5;
                            final GCPlayerStats stats = GCPlayerStats.get((EntityPlayerMP) eLiving);
                            data[4] = stats.airRemaining * 4096 + stats.airRemaining2;
                            final UUID uuid = eLiving.getUniqueID();
                            if (uuid != null) {
                                strUUID = uuid.toString();
                            }
                        } else if (eLiving instanceof EntityHorse) {
                            data[3] = ((EntityHorse) eLiving).getHorseType();
                            data[4] = ((EntityHorse) eLiving).getHorseVariant();
                        } else if (eLiving instanceof EntityVillager) {
                            data[3] = ((EntityVillager) eLiving).getProfession();
                            data[4] = ((EntityVillager) eLiving).getGrowingAge();
                        } else if (eLiving instanceof EntityWolf) {
                            data[3] = ((EntityWolf) eLiving).getCollarColor();
                            data[4] = ((EntityWolf) eLiving).func_70922_bv() ? 1 : 0;
                        } else if (eLiving instanceof EntitySheep) {
                            data[3] = ((EntitySheep) eLiving).getFleeceColor();
                            data[4] = ((EntitySheep) eLiving).getSheared() ? 1 : 0;
                        } else if (eLiving instanceof EntityOcelot) {
                            data[3] = ((EntityOcelot) eLiving).getTameSkin();
                        } else if (eLiving instanceof EntitySkeleton) {
                            data[3] = ((EntitySkeleton) eLiving).getSkeletonType();
                        } else if (eLiving instanceof EntityZombie) {
                            data[3] = ((EntityZombie) eLiving).isVillager() ? 1 : 0;
                            data[4] = eLiving.isChild() ? 1 : 0;
                        }
                    }
                }
            } else {
                name = "";
            }
            GalacticraftCore.packetPipeline.sendToAllAround(
                new PacketSimple(
                    EnumSimplePacket.C_UPDATE_TELEMETRY,
                    new Object[] { this.xCoord, this.yCoord, this.zCoord, name, data[0], data[1], data[2], data[3],
                        data[4], strUUID }),
                new TargetPoint(this.worldObj.provider.dimensionId, this.xCoord, this.yCoord, this.zCoord, 320D));
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        final Long msb = nbt.getLong("entityUUIDMost");
        final Long lsb = nbt.getLong("entityUUIDLeast");
        this.toUpdate = new UUID(msb, lsb);
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        if (this.linkedEntity != null && !this.linkedEntity.isDead) {
            nbt.setLong(
                "entityUUIDMost",
                this.linkedEntity.getUniqueID()
                    .getMostSignificantBits());
            nbt.setLong(
                "entityUUIDLeast",
                this.linkedEntity.getUniqueID()
                    .getLeastSignificantBits());
        }
    }

    public void addTrackedEntity(UUID uuid) {
        this.pulseRate = 400;
        this.lastHurttime = 0;
        final List<Entity> eList = this.worldObj.loadedEntityList;
        for (final Entity e : eList) {
            if (e.getUniqueID()
                .equals(uuid)) {
                this.linkedEntity = e;
                if (e instanceof EntitySpaceshipBase) {
                    ((EntitySpaceshipBase) e).addTelemetry(this);
                }
                return;
            }
        }
        // TODO Add some kind of watcher to add the entity when next loaded
        this.linkedEntity = null;
    }

    public void addTrackedEntity(Entity e) {
        this.pulseRate = 400;
        this.lastHurttime = 0;
        this.linkedEntity = e;
        if (e instanceof EntitySpaceshipBase) {
            ((EntitySpaceshipBase) e).addTelemetry(this);
        }
    }

    public void removeTrackedEntity() {
        this.pulseRate = 400;
        this.linkedEntity = null;
    }

    public static TileEntityTelemetry getNearest(TileEntity te) {
        if (te == null) {
            return null;
        }
        final BlockVec3 target = new BlockVec3(te);

        int distSq = 1025;
        BlockVec3Dim nearest = null;
        final int dim = te.getWorldObj().provider.dimensionId;
        for (final BlockVec3Dim telemeter : loadedList) {
            if (telemeter.dim != dim) {
                continue;
            }
            final int dist = telemeter.distanceSquared(target);
            if (dist < distSq) {
                distSq = dist;
                nearest = telemeter;
            }
        }

        if (nearest == null) {
            return null;
        }
        final TileEntity result = te.getWorldObj()
            .getTileEntity(nearest.x, nearest.y, nearest.z);
        if (result instanceof TileEntityTelemetry) {
            return (TileEntityTelemetry) result;
        }
        return null;
    }

    /**
     * Call this when a player wears a frequency module to check whether it has been linked with a Telemetry Unit.
     *
     * @param ItemStack The frequency module
     * @param player
     */
    public static void frequencyModulePlayer(ItemStack held, EntityPlayerMP player) {
        if (held == null) {
            return;
        }
        final NBTTagCompound fmData = held.stackTagCompound;
        if (fmData != null && fmData.hasKey("teDim")) {
            final int dim = fmData.getInteger("teDim");
            final int x = fmData.getInteger("teCoordX");
            final int y = fmData.getInteger("teCoordY");
            final int z = fmData.getInteger("teCoordZ");
            final WorldProvider wp = WorldUtil.getProviderForDimensionServer(dim);
            // TODO
            if (wp == null || wp.worldObj == null) {
                GCLog.debug("Frequency module worn: world provider is null.  This is a bug. " + dim);
            } else {
                final TileEntity te = wp.worldObj.getTileEntity(x, y, z);
                if (te instanceof TileEntityTelemetry) {
                    if (player == null) {
                        ((TileEntityTelemetry) te).removeTrackedEntity();
                    } else {
                        ((TileEntityTelemetry) te).addTrackedEntity(player.getUniqueID());
                    }
                }
            }
        }
    }

    public static void updateLinkedPlayer(EntityPlayerMP playerOld, EntityPlayerMP playerNew) {
        for (final BlockVec3Dim telemeter : loadedList) {
            final TileEntity te = telemeter.getTileEntityNoLoad();
            if (te instanceof TileEntityTelemetry && ((TileEntityTelemetry) te).linkedEntity == playerOld) {
                ((TileEntityTelemetry) te).linkedEntity = playerNew;
            }
        }
    }
}
