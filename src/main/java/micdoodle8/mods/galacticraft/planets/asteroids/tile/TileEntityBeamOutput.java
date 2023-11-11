package micdoodle8.mods.galacticraft.planets.asteroids.tile;

import micdoodle8.mods.galacticraft.core.tile.*;
import micdoodle8.mods.galacticraft.api.power.*;
import micdoodle8.mods.miccore.*;
import cpw.mods.fml.relauncher.*;
import net.minecraft.tileentity.*;
import java.util.*;
import net.minecraft.world.chunk.*;
import micdoodle8.mods.galacticraft.api.vector.*;
import net.minecraft.world.*;
import net.minecraft.entity.player.*;
import net.minecraft.nbt.*;

public abstract class TileEntityBeamOutput extends TileEntityAdvanced implements ILaserNode
{
    public LinkedList<ILaserNode> nodeList;
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    public BlockVec3 targetVec;
    public float pitch;
    public float yaw;
    private BlockVec3 preLoadTarget;
    private BlockVec3 lastTargetVec;
    
    public TileEntityBeamOutput() {
        this.nodeList = new LinkedList<ILaserNode>();
        this.targetVec = BlockVec3.INVALID_VECTOR;
        this.preLoadTarget = null;
        this.lastTargetVec = BlockVec3.INVALID_VECTOR;
    }
    
    public void updateEntity() {
        if (this.preLoadTarget != null) {
            final TileEntity tileAtTarget = this.worldObj.getTileEntity(this.preLoadTarget.x, this.preLoadTarget.y, this.preLoadTarget.z);
            if (tileAtTarget != null && tileAtTarget instanceof ILaserNode) {
                this.setTarget((ILaserNode)tileAtTarget);
                this.preLoadTarget = null;
            }
        }
        super.updateEntity();
        if (!this.targetVec.equals((Object)this.lastTargetVec)) {
            this.markDirty();
        }
        this.lastTargetVec = this.targetVec;
        if (this.worldObj.isRemote) {
            this.updateOrientation();
        }
        else if (this.targetVec.equals((Object)BlockVec3.INVALID_VECTOR)) {
            this.initiateReflector();
        }
    }
    
    public void invalidate() {
        super.invalidate();
        this.invalidateReflector();
    }
    
    public void validate() {
        super.validate();
    }
    
    public void onChunkUnload() {
        this.invalidateReflector();
    }
    
    public void invalidateReflector() {
        for (final ILaserNode node : this.nodeList) {
            node.removeNode((ILaserNode)this);
        }
        this.nodeList.clear();
    }
    
    public void initiateReflector() {
        this.nodeList.clear();
        final int chunkXMin = this.xCoord - 15 >> 4;
        final int chunkZMin = this.zCoord - 15 >> 4;
        final int chunkXMax = this.xCoord + 15 >> 4;
        final int chunkZMax = this.zCoord + 15 >> 4;
        for (int cX = chunkXMin; cX <= chunkXMax; ++cX) {
            for (int cZ = chunkZMin; cZ <= chunkZMax; ++cZ) {
                if (this.worldObj.getChunkProvider().chunkExists(cX, cZ)) {
                    final Chunk chunk = this.worldObj.getChunkFromChunkCoords(cX, cZ);
                    for (final Object obj : chunk.chunkTileEntityMap.values()) {
                        if (obj != this && obj instanceof ILaserNode) {
                            final BlockVec3 deltaPos = new BlockVec3((TileEntity)this).subtract(new BlockVec3(((ILaserNode)obj).getTile()));
                            if (deltaPos.x >= 16 || deltaPos.y >= 16 || deltaPos.z >= 16) {
                                continue;
                            }
                            final ILaserNode laserNode = (ILaserNode)obj;
                            if (!this.canConnectTo(laserNode) || !laserNode.canConnectTo((ILaserNode)this)) {
                                continue;
                            }
                            this.addNode(laserNode);
                            laserNode.addNode((ILaserNode)this);
                        }
                    }
                }
            }
        }
        this.setTarget(this.nodeList.peekFirst());
    }
    
    public void addNode(final ILaserNode node) {
        int index = -1;
        for (int i = 0; i < this.nodeList.size(); ++i) {
            if (new BlockVec3(this.nodeList.get(i).getTile()).equals((Object)new BlockVec3(node.getTile()))) {
                index = i;
                break;
            }
        }
        if (index != -1) {
            this.nodeList.set(index, node);
            return;
        }
        if (this.nodeList.isEmpty()) {
            this.nodeList.add(node);
        }
        else {
            int nodeCompare = this.nodeList.get(0).compareTo(node, new BlockVec3((TileEntity)this));
            if (nodeCompare <= 0) {
                this.nodeList.addFirst(node);
                return;
            }
            nodeCompare = this.nodeList.get(this.nodeList.size() - 1).compareTo(node, new BlockVec3((TileEntity)this));
            if (nodeCompare >= 0) {
                this.nodeList.addLast(node);
                return;
            }
            for (index = 1; index < this.nodeList.size(); ++index) {}
            this.nodeList.add(index, node);
        }
    }
    
    public void removeNode(final ILaserNode node) {
        int index = -1;
        for (int i = 0; i < this.nodeList.size(); ++i) {
            if (new BlockVec3(this.nodeList.get(i).getTile()).equals((Object)new BlockVec3(node.getTile()))) {
                index = i;
                break;
            }
        }
        if (new BlockVec3(node.getTile()).equals((Object)this.targetVec)) {
            if (index == 0) {
                if (this.nodeList.size() > 1) {
                    this.setTarget(this.nodeList.get(index + 1));
                }
                else {
                    this.setTarget(null);
                }
            }
            else {
                this.setTarget(this.nodeList.get(index - 1));
            }
        }
        if (index != -1) {
            this.nodeList.remove(index);
        }
    }
    
    public void updateOrientation() {
        if (this.getTarget() != null) {
            final Vector3 direction = Vector3.subtract(this.getOutputPoint(false), this.getTarget().getInputPoint()).normalize();
            this.pitch = (float)(-Vector3.getAngle(new Vector3(-direction.x, -direction.y, -direction.z), new Vector3(0.0, 1.0, 0.0))) * 57.29578f + 90.0f;
            this.yaw = (float)(-(Math.atan2(direction.z, direction.x) * 57.295780181884766)) + 90.0f;
        }
    }
    
    public TileEntity getTile() {
        return (TileEntity)this;
    }
    
    public int compareTo(final ILaserNode otherNode, final BlockVec3 origin) {
        final int thisDistance = new BlockVec3((TileEntity)this).subtract(origin).getMagnitudeSquared();
        final int otherDistance = new BlockVec3(otherNode.getTile()).subtract(origin).getMagnitudeSquared();
        if (thisDistance < otherDistance) {
            return 1;
        }
        if (thisDistance > otherDistance) {
            return -1;
        }
        return 0;
    }
    
    public boolean onMachineActivated(final World world, final int x, final int y, final int z, final EntityPlayer entityPlayer, final int side, final float hitX, final float hitY, final float hitZ) {
        if (this.nodeList.size() > 1) {
            int index = -1;
            if (this.getTarget() != null) {
                for (int i = 0; i < this.nodeList.size(); ++i) {
                    if (new BlockVec3(this.nodeList.get(i).getTile()).equals((Object)new BlockVec3(this.getTarget().getTile()))) {
                        index = i;
                        break;
                    }
                }
            }
            if (index != -1) {
                index = ++index % this.nodeList.size();
                this.setTarget(this.nodeList.get(index));
                return true;
            }
            this.initiateReflector();
        }
        return false;
    }
    
    public ILaserNode getTarget() {
        if (this.targetVec.equals((Object)BlockVec3.INVALID_VECTOR)) {
            return null;
        }
        final TileEntity tileAtTarget = this.worldObj.getTileEntity(this.targetVec.x, this.targetVec.y, this.targetVec.z);
        if (tileAtTarget != null && tileAtTarget instanceof ILaserNode) {
            return (ILaserNode)tileAtTarget;
        }
        return null;
    }
    
    public void setTarget(final ILaserNode target) {
        if (target != null) {
            this.targetVec = new BlockVec3(target.getTile());
        }
        else {
            this.targetVec = BlockVec3.INVALID_VECTOR;
        }
    }
    
    public void readFromNBT(final NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        if (nbt.getBoolean("HasTarget")) {
            this.preLoadTarget = new BlockVec3(nbt.getInteger("TargetX"), nbt.getInteger("TargetY"), nbt.getInteger("TargetZ"));
        }
    }
    
    public void writeToNBT(final NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setBoolean("HasTarget", this.getTarget() != null);
        if (this.getTarget() != null) {
            nbt.setInteger("TargetX", this.getTarget().getTile().xCoord);
            nbt.setInteger("TargetY", this.getTarget().getTile().yCoord);
            nbt.setInteger("TargetZ", this.getTarget().getTile().zCoord);
        }
    }
}
