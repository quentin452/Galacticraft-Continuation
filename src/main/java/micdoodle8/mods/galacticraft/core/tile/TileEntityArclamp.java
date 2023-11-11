package micdoodle8.mods.galacticraft.core.tile;

import net.minecraft.tileentity.*;
import micdoodle8.mods.galacticraft.api.vector.*;
import net.minecraft.util.*;
import micdoodle8.mods.galacticraft.core.*;
import micdoodle8.mods.galacticraft.core.network.*;
import micdoodle8.mods.galacticraft.core.util.*;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.*;
import net.minecraft.pathfinding.*;
import net.minecraft.init.*;
import micdoodle8.mods.galacticraft.core.blocks.*;
import java.util.*;
import net.minecraft.block.*;
import net.minecraft.world.*;
import net.minecraft.nbt.*;

public class TileEntityArclamp extends TileEntity
{
    private int ticks;
    private int sideRear;
    public int facing;
    private HashSet<BlockVec3> airToRestore;
    private boolean isActive;
    private AxisAlignedBB thisAABB;
    private Vec3 thisPos;
    private int facingSide;
    public boolean updateClientFlag;
    
    public TileEntityArclamp() {
        this.ticks = 0;
        this.sideRear = 0;
        this.facing = 0;
        this.airToRestore = new HashSet<BlockVec3>();
        this.isActive = false;
        this.facingSide = 0;
    }
    
    public void updateEntity() {
        super.updateEntity();
        if (this.worldObj.isRemote) {
            return;
        }
        boolean initialLight = false;
        if (this.updateClientFlag) {
            GalacticraftCore.packetPipeline.sendToDimension((IPacket)new PacketSimple(PacketSimple.EnumSimplePacket.C_UPDATE_ARCLAMP_FACING, new Object[] { this.xCoord, this.yCoord, this.zCoord, this.facing }), this.worldObj.provider.dimensionId);
            this.updateClientFlag = false;
        }
        if (RedstoneUtil.isBlockReceivingRedstone(this.worldObj, this.xCoord, this.yCoord, this.zCoord)) {
            if (this.isActive) {
                this.isActive = false;
                this.revertAir();
                this.markDirty();
            }
        }
        else if (!this.isActive) {
            this.isActive = true;
            initialLight = true;
        }
        if (this.isActive) {
            if (this.thisAABB == null) {
                initialLight = true;
                final int side = this.getBlockMetadata();
                switch (side) {
                    case 0: {
                        this.sideRear = side;
                        this.facingSide = this.facing + 2;
                        this.thisAABB = AxisAlignedBB.getBoundingBox((double)(this.xCoord - 20), (double)(this.yCoord - 8), (double)(this.zCoord - 20), (double)(this.xCoord + 20), (double)(this.yCoord + 20), (double)(this.zCoord + 20));
                        break;
                    }
                    case 1: {
                        this.sideRear = side;
                        this.facingSide = this.facing + 2;
                        this.thisAABB = AxisAlignedBB.getBoundingBox((double)(this.xCoord - 20), (double)(this.yCoord - 20), (double)(this.zCoord - 20), (double)(this.xCoord + 20), (double)(this.yCoord + 8), (double)(this.zCoord + 20));
                        break;
                    }
                    case 2: {
                        this.sideRear = side;
                        this.facingSide = this.facing;
                        if (this.facing > 1) {
                            this.facingSide = 7 - this.facing;
                        }
                        this.thisAABB = AxisAlignedBB.getBoundingBox((double)(this.xCoord - 20), (double)(this.yCoord - 20), (double)(this.zCoord - 8), (double)(this.xCoord + 20), (double)(this.yCoord + 20), (double)(this.zCoord + 20));
                        break;
                    }
                    case 3: {
                        this.sideRear = side;
                        this.facingSide = this.facing;
                        if (this.facing > 1) {
                            this.facingSide += 2;
                        }
                        this.thisAABB = AxisAlignedBB.getBoundingBox((double)(this.xCoord - 20), (double)(this.yCoord - 20), (double)(this.zCoord - 20), (double)(this.xCoord + 20), (double)(this.yCoord + 20), (double)(this.zCoord + 8));
                        break;
                    }
                    case 4: {
                        this.sideRear = side;
                        this.facingSide = this.facing;
                        this.thisAABB = AxisAlignedBB.getBoundingBox((double)(this.xCoord - 8), (double)(this.yCoord - 20), (double)(this.zCoord - 20), (double)(this.xCoord + 20), (double)(this.yCoord + 20), (double)(this.zCoord + 20));
                        break;
                    }
                    case 5: {
                        this.sideRear = side;
                        this.facingSide = this.facing;
                        if (this.facing > 1) {
                            this.facingSide = 5 - this.facing;
                        }
                        this.thisAABB = AxisAlignedBB.getBoundingBox((double)(this.xCoord - 20), (double)(this.yCoord - 20), (double)(this.zCoord - 20), (double)(this.xCoord + 8), (double)(this.yCoord + 20), (double)(this.zCoord + 20));
                        break;
                    }
                    default: {
                        return;
                    }
                }
            }
            if (initialLight || this.ticks % 100 == 0) {
                this.lightArea();
            }
            if (this.worldObj.rand.nextInt(20) == 0) {
                final List<Entity> moblist = (List<Entity>)this.worldObj.getEntitiesWithinAABBExcludingEntity((Entity)null, this.thisAABB, IMob.mobSelector);
                if (!moblist.isEmpty()) {
                    for (final Entity entry : moblist) {
                        if (!(entry instanceof EntityCreature)) {
                            continue;
                        }
                        final EntityCreature e = (EntityCreature)entry;
                        final Vec3 vecNewTarget = RandomPositionGenerator.findRandomTargetBlockAwayFrom(e, 16, 7, this.thisPos);
                        if (vecNewTarget == null) {
                            continue;
                        }
                        final PathNavigate nav = e.getNavigator();
                        if (nav == null) {
                            continue;
                        }
                        Vec3 vecOldTarget = null;
                        if (nav.getPath() != null && !nav.getPath().isFinished()) {
                            vecOldTarget = nav.getPath().getPosition((Entity)e);
                        }
                        final double distanceNew = vecNewTarget.squareDistanceTo((double)this.xCoord, (double)this.yCoord, (double)this.zCoord);
                        if (distanceNew <= e.getDistanceSq((double)this.xCoord, (double)this.yCoord, (double)this.zCoord) || (vecOldTarget != null && distanceNew <= vecOldTarget.squareDistanceTo((double)this.xCoord, (double)this.yCoord, (double)this.zCoord))) {
                            continue;
                        }
                        e.getNavigator().tryMoveToXYZ(vecNewTarget.xCoord, vecNewTarget.yCoord, vecNewTarget.zCoord, 0.3);
                    }
                }
            }
        }
        ++this.ticks;
    }
    
    public void validate() {
        super.validate();
        this.thisPos = Vec3.createVectorHelper(this.xCoord + 0.5, this.yCoord + 0.5, this.zCoord + 0.5);
        this.ticks = 0;
        this.thisAABB = null;
        if (this.worldObj.isRemote) {
            GalacticraftCore.packetPipeline.sendToServer((IPacket)new PacketSimple(PacketSimple.EnumSimplePacket.S_REQUEST_ARCLAMP_FACING, new Object[] { this.xCoord, this.yCoord, this.zCoord }));
        }
        else {
            this.isActive = true;
        }
    }
    
    public void invalidate() {
        if (!this.worldObj.isRemote) {
            this.revertAir();
        }
        this.isActive = false;
        super.invalidate();
    }
    
    public void lightArea() {
        final Block air = Blocks.air;
        final Block breatheableAirID = GCBlocks.breatheableAir;
        final Block brightAir = GCBlocks.brightAir;
        final Block brightBreatheableAir = GCBlocks.brightBreatheableAir;
        final HashSet<BlockVec3> checked = new HashSet<BlockVec3>();
        LinkedList<BlockVec3> currentLayer = new LinkedList<BlockVec3>();
        LinkedList<BlockVec3> nextLayer = new LinkedList<BlockVec3>();
        final BlockVec3 thisvec = new BlockVec3((TileEntity)this);
        currentLayer.add(thisvec);
        final World world = this.worldObj;
        final int sideskip1 = this.sideRear;
        final int sideskip2 = this.facingSide ^ 0x1;
        for (int i = 0; i < 6; ++i) {
            if (i != sideskip1 && i != sideskip2 && i != (sideskip1 ^ 0x1) && i != (sideskip2 ^ 0x1)) {
                final BlockVec3 onEitherSide = thisvec.newVecSide(i);
                final Block b = onEitherSide.getBlockIDsafe_noChunkLoad(world);
                if (b != null && b.getLightOpacity() < 15) {
                    currentLayer.add(onEitherSide);
                }
            }
        }
        BlockVec3 inFront = new BlockVec3((TileEntity)this);
        for (int j = 0; j < 5; ++j) {
            inFront = inFront.newVecSide(this.facingSide).newVecSide(sideskip1 ^ 0x1);
            final Block b = inFront.getBlockIDsafe_noChunkLoad(world);
            if (b != null && b.getLightOpacity() < 15) {
                currentLayer.add(inFront);
            }
        }
        for (int count = 0; count < 14; ++count) {
            for (final BlockVec3 vec : currentLayer) {
                int side = 0;
                final int bits = vec.sideDoneBits;
                boolean allAir = true;
                do {
                    if ((bits & 1 << side) == 0x0) {
                        final BlockVec3 sideVec = vec.newVecSide(side);
                        if (checked.contains(sideVec)) {
                            continue;
                        }
                        checked.add(sideVec);
                        final Block b2 = sideVec.getBlockIDsafe_noChunkLoad(world);
                        if (b2 instanceof BlockAir) {
                            if (side == sideskip1 || side == sideskip2) {
                                continue;
                            }
                            nextLayer.add(sideVec);
                        }
                        else {
                            allAir = false;
                            if (b2 == null || b2.getLightOpacity((IBlockAccess)world, sideVec.x, sideVec.y, sideVec.z) != 0 || side == sideskip1 || side == sideskip2) {
                                continue;
                            }
                            nextLayer.add(sideVec);
                        }
                    }
                } while (++side < 6);
                if (!allAir) {
                    final Block id = vec.getBlockIDsafe_noChunkLoad(world);
                    if (Blocks.air == id) {
                        world.setBlock(vec.x, vec.y, vec.z, brightAir, 0, 2);
                        this.airToRestore.add(vec);
                        this.markDirty();
                    }
                    else {
                        if (id != breatheableAirID) {
                            continue;
                        }
                        world.setBlock(vec.x, vec.y, vec.z, brightBreatheableAir, 0, 2);
                        this.airToRestore.add(vec);
                        this.markDirty();
                    }
                }
            }
            currentLayer = nextLayer;
            nextLayer = new LinkedList<BlockVec3>();
            if (currentLayer.size() == 0) {
                break;
            }
        }
    }
    
    public void readFromNBT(final NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        this.facing = nbt.getInteger("Facing");
        this.updateClientFlag = true;
        this.airToRestore.clear();
        final NBTTagList airBlocks = nbt.getTagList("AirBlocks", 10);
        if (airBlocks.tagCount() > 0) {
            for (int j = airBlocks.tagCount() - 1; j >= 0; --j) {
                final NBTTagCompound tag1 = airBlocks.getCompoundTagAt(j);
                if (tag1 != null) {
                    this.airToRestore.add(BlockVec3.readFromNBT(tag1));
                }
            }
        }
    }
    
    public void writeToNBT(final NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setInteger("Facing", this.facing);
        final NBTTagList airBlocks = new NBTTagList();
        for (final BlockVec3 vec : this.airToRestore) {
            final NBTTagCompound tag = new NBTTagCompound();
            vec.writeToNBT(tag);
            airBlocks.appendTag((NBTBase)tag);
        }
        nbt.setTag("AirBlocks", (NBTBase)airBlocks);
    }
    
    public void facingChanged() {
        this.facing -= 2;
        if (this.facing < 0) {
            this.facing = 1 - this.facing;
        }
        GalacticraftCore.packetPipeline.sendToDimension((IPacket)new PacketSimple(PacketSimple.EnumSimplePacket.C_UPDATE_ARCLAMP_FACING, new Object[] { this.xCoord, this.yCoord, this.zCoord, this.facing }), this.worldObj.provider.dimensionId);
        this.thisAABB = null;
        this.revertAir();
        this.markDirty();
    }
    
    private void revertAir() {
        final Block brightAir = GCBlocks.brightAir;
        final Block brightBreatheableAir = GCBlocks.brightBreatheableAir;
        for (final BlockVec3 vec : this.airToRestore) {
            final Block b = vec.getBlock((IBlockAccess)this.worldObj);
            if (b == brightAir) {
                this.worldObj.setBlock(vec.x, vec.y, vec.z, Blocks.air, 0, 2);
            }
            else {
                if (b != brightBreatheableAir) {
                    continue;
                }
                this.worldObj.setBlock(vec.x, vec.y, vec.z, GCBlocks.breatheableAir, 0, 2);
            }
        }
        this.airToRestore.clear();
    }
    
    public boolean getEnabled() {
        return !RedstoneUtil.isBlockReceivingRedstone(this.worldObj, this.xCoord, this.yCoord, this.zCoord);
    }
}
