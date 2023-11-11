package micdoodle8.mods.galacticraft.core.oxygen;

import java.util.concurrent.atomic.*;
import micdoodle8.mods.galacticraft.api.vector.*;
import micdoodle8.mods.galacticraft.core.tile.*;
import net.minecraft.tileentity.*;
import net.minecraft.world.*;
import java.util.*;
import net.minecraftforge.common.util.*;
import net.minecraft.init.*;
import micdoodle8.mods.galacticraft.core.util.*;
import micdoodle8.mods.galacticraft.core.wrappers.*;
import micdoodle8.mods.galacticraft.core.tick.*;
import micdoodle8.mods.galacticraft.api.block.*;
import net.minecraft.block.material.*;
import micdoodle8.mods.galacticraft.core.blocks.*;
import net.minecraft.block.*;

public class ThreadFindSeal
{
    public AtomicBoolean sealedFinal;
    public static AtomicBoolean anylooping;
    public AtomicBoolean looping;
    private World world;
    private BlockVec3 head;
    private boolean sealed;
    private List<TileEntityOxygenSealer> sealers;
    private intBucket[] buckets;
    private int checkedSize;
    private int checkCount;
    private HashMap<BlockVec3, TileEntityOxygenSealer> sealersAround;
    private List<BlockVec3> currentLayer;
    private List<BlockVec3> airToReplace;
    private List<BlockVec3> breatheableToReplace;
    private List<BlockVec3> airToReplaceBright;
    private List<BlockVec3> breatheableToReplaceBright;
    private List<BlockVec3> ambientThermalTracked;
    private List<TileEntityOxygenSealer> otherSealers;
    private List<BlockVec3> torchesToUpdate;
    private boolean foundAmbientThermal;
    public List<BlockVec3> leakTrace;
    
    public ThreadFindSeal(final TileEntityOxygenSealer sealer) {
        this(sealer.getWorldObj(), new BlockVec3((TileEntity)sealer).translate(0, 1, 0), sealer.getFindSealChecks(), new ArrayList<TileEntityOxygenSealer>(Arrays.asList(sealer)));
    }
    
    public ThreadFindSeal(final World world, final BlockVec3 head, final int checkCount, final List<TileEntityOxygenSealer> sealers) {
        this.sealedFinal = new AtomicBoolean();
        this.looping = new AtomicBoolean();
        this.buckets = new intBucket[256];
        this.checkedSize = 0;
        this.world = world;
        this.head = head;
        this.checkCount = checkCount;
        this.sealers = sealers;
        this.foundAmbientThermal = false;
        this.checkedInit();
        this.torchesToUpdate = new LinkedList<BlockVec3>();
        this.sealersAround = TileEntityOxygenSealer.getSealersAround(world, head.x, head.y, head.z, 1048576);
        if (!sealers.isEmpty()) {
            if (checkCount > 0) {
                final Block headBlock = head.getBlockID(this.world);
                if (headBlock != null && !headBlock.isAir((IBlockAccess)world, head.x, head.y, head.z)) {
                    this.canBlockPassAirCheck(headBlock, this.head, 1);
                    this.checkCount = checkCount;
                }
            }
            this.looping.set(true);
            for (final TileEntityOxygenSealer eachSealer : sealers) {
                eachSealer.threadSeal = this;
            }
            this.check();
        }
        else {
            this.check();
        }
    }
    
    public void check() {
        final long time1 = System.nanoTime();
        this.sealed = true;
        final TileEntity tile = this.head.getTileEntityOnSide(this.world, ForgeDirection.DOWN);
        this.foundAmbientThermal = (tile instanceof TileEntityOxygenSealer && ((TileEntityOxygenSealer)tile).thermalControlEnabled());
        this.checkedAdd(this.head.clone());
        this.currentLayer = new LinkedList<BlockVec3>();
        this.airToReplace = new LinkedList<BlockVec3>();
        this.airToReplaceBright = new LinkedList<BlockVec3>();
        this.ambientThermalTracked = new LinkedList<BlockVec3>();
        if (this.checkCount > 0) {
            this.currentLayer.add(this.head);
            if (this.head.x < -29990000 || this.head.z < -29990000 || this.head.x >= 29990000 || this.head.z >= 29990000) {
                final Block b = this.head.getBlockID_noChunkLoad(this.world);
                if (Blocks.air == b) {
                    this.airToReplace.add(this.head.clone());
                }
                else if (b == GCBlocks.brightAir) {
                    this.airToReplaceBright.add(this.head.clone());
                }
                this.doLayerNearMapEdge();
            }
            else {
                final Block headblock = this.head.getBlockIDsafe_noChunkLoad(this.world);
                if (Blocks.air == headblock) {
                    this.airToReplace.add(this.head.clone());
                }
                else if (headblock == GCBlocks.brightAir) {
                    this.airToReplaceBright.add(this.head.clone());
                }
                this.doLayer();
            }
        }
        else {
            this.sealed = false;
        }
        final long time2 = System.nanoTime();
        if (this.sealers.isEmpty()) {
            this.sealed = false;
        }
        if (this.sealed) {
            this.makeSealGood(this.foundAmbientThermal);
            this.leakTrace = null;
        }
        else {
            int checkedSave = this.checkedSize;
            this.checkedClear();
            this.breatheableToReplace = new LinkedList<BlockVec3>();
            this.breatheableToReplaceBright = new LinkedList<BlockVec3>();
            this.otherSealers = new LinkedList<TileEntityOxygenSealer>();
            this.currentLayer.clear();
            this.currentLayer.add(this.head);
            this.torchesToUpdate.clear();
            if (this.head.x < -29990000 || this.head.z < -29990000 || this.head.x >= 29990000 || this.head.z >= 29990000) {
                this.unsealNearMapEdge();
            }
            else {
                this.unseal();
            }
            if (!this.otherSealers.isEmpty()) {
                final List<TileEntityOxygenSealer> sealersSave = this.sealers;
                final List<BlockVec3> torchesSave = this.torchesToUpdate;
                final List<TileEntityOxygenSealer> sealersDone = new ArrayList<TileEntityOxygenSealer>();
                sealersDone.addAll(this.sealers);
                for (final TileEntityOxygenSealer otherSealer : this.otherSealers) {
                    if (!sealersDone.contains(otherSealer) && otherSealer.getFindSealChecks() > 0) {
                        final BlockVec3 newhead = new BlockVec3((TileEntity)otherSealer).translate(0, 1, 0);
                        this.sealed = true;
                        this.checkCount = otherSealer.getFindSealChecks();
                        (this.sealers = new LinkedList<TileEntityOxygenSealer>()).add(otherSealer);
                        if (otherSealer.thermalControlEnabled()) {
                            this.foundAmbientThermal = true;
                        }
                        this.checkedClear();
                        this.checkedAdd(newhead);
                        this.currentLayer.clear();
                        this.airToReplace.clear();
                        this.airToReplaceBright.clear();
                        this.torchesToUpdate = new LinkedList<BlockVec3>();
                        this.currentLayer.add(newhead.clone());
                        if (newhead.x < -29990000 || newhead.z < -29990000 || newhead.x >= 29990000 || newhead.z >= 29990000) {
                            this.doLayerNearMapEdge();
                        }
                        else {
                            this.doLayer();
                        }
                        if (this.sealed) {
                            if (ConfigManagerCore.enableDebug) {
                                GCLog.info("Oxygen Sealer replacing head at x" + this.head.x + " y" + (this.head.y - 1) + " z" + this.head.z);
                            }
                            if (!sealersSave.isEmpty()) {
                                final TileEntityOxygenSealer oldHead = sealersSave.get(0);
                                if (!this.sealers.contains(oldHead)) {
                                    this.sealers.add(oldHead);
                                    if (oldHead.thermalControlEnabled()) {
                                        this.foundAmbientThermal = true;
                                    }
                                }
                            }
                            this.head = newhead.clone();
                            otherSealer.threadSeal = this;
                            otherSealer.stopSealThreadCooldown = 75 + TileEntityOxygenSealer.countEntities;
                            checkedSave += this.checkedSize;
                            break;
                        }
                        sealersDone.addAll(this.sealers);
                        checkedSave += this.checkedSize;
                    }
                }
                if (!this.sealed) {
                    this.sealers = sealersSave;
                    this.torchesToUpdate = torchesSave;
                }
                else {
                    this.makeSealGood(this.foundAmbientThermal);
                }
            }
            this.checkedSize = checkedSave;
            if (!this.sealed) {
                if (this.head.getBlockID(this.world) == GCBlocks.breatheableAir) {
                    this.breatheableToReplace.add(this.head);
                }
                if (this.head.getBlockID(this.world) == GCBlocks.brightBreatheableAir) {
                    this.breatheableToReplaceBright.add(this.head);
                }
                this.makeSealBad();
            }
            else {
                this.leakTrace = null;
            }
        }
        final TileEntityOxygenSealer headSealer = this.sealersAround.get(this.head.clone().translate(0, -1, 0));
        if (headSealer != null) {
            headSealer.stopSealThreadCooldown = 75 + TileEntityOxygenSealer.countEntities;
        }
        for (final TileEntityOxygenSealer sealer : this.sealers) {
            if (sealer != headSealer && headSealer != null) {
                sealer.threadSeal = this;
                sealer.stopSealThreadCooldown = headSealer.stopSealThreadCooldown + 51;
            }
        }
        this.sealedFinal.set(this.sealed);
        this.looping.set(false);
        if (ConfigManagerCore.enableDebug) {
            final long time3 = System.nanoTime();
            final float total = (time3 - time1) / 1000000.0f;
            final float looping = (time2 - time1) / 1000000.0f;
            final float replacing = (time3 - time2) / 1000000.0f;
            GCLog.info("Oxygen Sealer Check Completed at x" + this.head.x + " y" + this.head.y + " z" + this.head.z);
            GCLog.info("   Sealed: " + this.sealed + "  ~  " + this.sealers.size() + " sealers  ~  " + (this.checkedSize - 1) + " blocks");
            GCLog.info("   Total Time taken: " + String.format("%.2f", total) + "ms  ~  " + String.format("%.2f", looping) + " + " + String.format("%.2f", replacing) + "");
        }
    }
    
    private void makeSealGood(final boolean ambientThermal) {
        if (!this.airToReplace.isEmpty() || !this.airToReplaceBright.isEmpty() || !this.ambientThermalTracked.isEmpty()) {
            final List<ScheduledBlockChange> changeList = new LinkedList<ScheduledBlockChange>();
            final Block breatheableAirID = GCBlocks.breatheableAir;
            int metadata = 0;
            if (ambientThermal) {
                metadata = 1;
            }
            for (final BlockVec3 checkedVec : this.airToReplace) {
                changeList.add(new ScheduledBlockChange(checkedVec.clone(), breatheableAirID, metadata, 2));
            }
            for (final BlockVec3 checkedVec : this.airToReplaceBright) {
                changeList.add(new ScheduledBlockChange(checkedVec.clone(), GCBlocks.brightBreatheableAir, metadata, 2));
            }
            for (final BlockVec3 checkedVec : this.ambientThermalTracked) {
                changeList.add(new ScheduledBlockChange(checkedVec.clone(), checkedVec.getBlock((IBlockAccess)this.world), metadata, 3));
            }
            TickHandlerServer.scheduleNewBlockChange(this.world.provider.dimensionId, changeList);
        }
        if (!this.torchesToUpdate.isEmpty()) {
            TickHandlerServer.scheduleNewTorchUpdate(this.world.provider.dimensionId, this.torchesToUpdate);
        }
    }
    
    private void makeSealBad() {
        if (!this.breatheableToReplace.isEmpty() || !this.breatheableToReplaceBright.isEmpty()) {
            final List<ScheduledBlockChange> changeList = new LinkedList<ScheduledBlockChange>();
            for (final BlockVec3 checkedVec : this.breatheableToReplace) {
                changeList.add(new ScheduledBlockChange(checkedVec.clone(), Blocks.air, 0, 2));
            }
            for (final BlockVec3 checkedVec : this.breatheableToReplaceBright) {
                changeList.add(new ScheduledBlockChange(checkedVec.clone(), GCBlocks.brightAir, 0, 2));
            }
            TickHandlerServer.scheduleNewBlockChange(this.world.provider.dimensionId, changeList);
        }
        if (!this.torchesToUpdate.isEmpty()) {
            TickHandlerServer.scheduleNewTorchUpdate(this.world.provider.dimensionId, this.torchesToUpdate);
        }
    }
    
    private void unseal() {
        final Block breatheableAirID = GCBlocks.breatheableAir;
        final Block breatheableAirIDBright = GCBlocks.brightBreatheableAir;
        final Block oxygenSealerID = GCBlocks.oxygenSealer;
        final Block fireBlock = (Block)Blocks.fire;
        final Block airBlock = Blocks.air;
        final Block airBlockBright = GCBlocks.brightAir;
        final List<BlockVec3> toReplaceLocal = this.breatheableToReplace;
        LinkedList nextLayer = new LinkedList();
        final World world = this.world;
        while (this.currentLayer.size() > 0) {
            for (final BlockVec3 vec : this.currentLayer) {
                int side = 0;
                final int bits = vec.sideDoneBits;
                do {
                    if ((bits & 1 << side) == 0x0 && !this.checkedContains(vec, side)) {
                        final BlockVec3 sideVec = vec.newVecSide(side);
                        final Block id = sideVec.getBlockIDsafe_noChunkLoad(world);
                        if (id == breatheableAirID) {
                            toReplaceLocal.add(sideVec);
                            nextLayer.add(sideVec);
                            this.checkedAdd(sideVec);
                        }
                        else if (id == breatheableAirIDBright) {
                            this.breatheableToReplaceBright.add(sideVec);
                            nextLayer.add(sideVec);
                            this.checkedAdd(sideVec);
                        }
                        else if (id == fireBlock) {
                            toReplaceLocal.add(sideVec);
                            nextLayer.add(sideVec);
                            this.checkedAdd(sideVec);
                        }
                        else if (id == oxygenSealerID) {
                            final TileEntityOxygenSealer sealer = this.sealersAround.get(sideVec);
                            if (sealer != null && !this.sealers.contains(sealer)) {
                                if (side != 0) {
                                    continue;
                                }
                                this.otherSealers.add(sealer);
                                this.checkedAdd(sideVec);
                            }
                            else {
                                this.checkedAdd(sideVec);
                            }
                        }
                        else if (id != null && id != airBlock && id != airBlockBright) {
                            if (!this.canBlockPassAirCheck(id, sideVec, side)) {
                                continue;
                            }
                            nextLayer.add(sideVec);
                        }
                        else {
                            if (id == null) {
                                continue;
                            }
                            this.checkedAdd(sideVec);
                        }
                    }
                } while (++side < 6);
            }
            this.currentLayer = (List<BlockVec3>)nextLayer;
            nextLayer = new LinkedList();
        }
    }
    
    private void unsealNearMapEdge() {
        final Block breatheableAirID = GCBlocks.breatheableAir;
        final Block breatheableAirIDBright = GCBlocks.brightBreatheableAir;
        final Block oxygenSealerID = GCBlocks.oxygenSealer;
        final Block fireBlock = (Block)Blocks.fire;
        LinkedList nextLayer = new LinkedList();
        while (this.currentLayer.size() > 0) {
            for (final BlockVec3 vec : this.currentLayer) {
                final int bits = vec.sideDoneBits;
                int side = 0;
                do {
                    if ((bits & 1 << side) == 0x1) {
                        continue;
                    }
                    if (!this.checkedContains(vec, side)) {
                        final BlockVec3 sideVec = vec.newVecSide(side);
                        final Block id = sideVec.getBlockID_noChunkLoad(this.world);
                        if (id == breatheableAirID) {
                            this.breatheableToReplace.add(sideVec);
                            nextLayer.add(sideVec);
                            this.checkedAdd(sideVec);
                        }
                        else if (id == breatheableAirIDBright) {
                            this.breatheableToReplaceBright.add(sideVec);
                            nextLayer.add(sideVec);
                            this.checkedAdd(sideVec);
                        }
                        else if (id == fireBlock) {
                            nextLayer.add(sideVec);
                            this.breatheableToReplace.add(sideVec);
                            this.checkedAdd(sideVec);
                        }
                        else if (id == oxygenSealerID) {
                            final TileEntityOxygenSealer sealer = this.sealersAround.get(sideVec);
                            if (sealer != null && !this.sealers.contains(sealer)) {
                                if (side == 0) {
                                    this.otherSealers.add(sealer);
                                    this.checkedAdd(sideVec);
                                }
                            }
                            else {
                                this.checkedAdd(sideVec);
                            }
                        }
                        else if (id != null && Blocks.air != id && id != GCBlocks.brightAir) {
                            if (this.canBlockPassAirCheck(id, sideVec, side)) {
                                nextLayer.add(sideVec);
                            }
                        }
                        else if (id != null) {
                            this.checkedAdd(sideVec);
                        }
                    }
                    ++side;
                } while (side < 6);
            }
            this.currentLayer = (List<BlockVec3>)nextLayer;
            nextLayer = new LinkedList();
        }
    }
    
    private void doLayer() {
        final Block breatheableAirID = GCBlocks.breatheableAir;
        final Block airID = Blocks.air;
        final Block breatheableAirIDBright = GCBlocks.brightBreatheableAir;
        final Block airIDBright = GCBlocks.brightAir;
        final Block oxygenSealerID = GCBlocks.oxygenSealer;
        LinkedList nextLayer = new LinkedList();
        final World world = this.world;
        while (this.sealed && this.currentLayer.size() > 0) {
            for (final BlockVec3 vec : this.currentLayer) {
                int side = 0;
                final int bits = vec.sideDoneBits;
                do {
                    if ((bits & 1 << side) == 0x0 && !this.checkedContains(vec, side)) {
                        final BlockVec3 sideVec = vec.newVecSide(side);
                        if (this.checkCount > 0) {
                            --this.checkCount;
                            final Block id = sideVec.getBlockIDsafe_noChunkLoad(world);
                            if (id == breatheableAirID) {
                                this.checkedAdd(sideVec);
                                nextLayer.add(sideVec);
                                this.ambientThermalTracked.add(sideVec);
                            }
                            else if (id == airID) {
                                this.checkedAdd(sideVec);
                                nextLayer.add(sideVec);
                                this.airToReplace.add(sideVec);
                            }
                            else if (id == breatheableAirIDBright) {
                                this.checkedAdd(sideVec);
                                nextLayer.add(sideVec);
                                this.ambientThermalTracked.add(sideVec);
                            }
                            else if (id == airIDBright) {
                                this.checkedAdd(sideVec);
                                nextLayer.add(sideVec);
                                this.airToReplaceBright.add(sideVec);
                            }
                            else {
                                if (id == null) {
                                    this.checkCount = 0;
                                    this.sealed = false;
                                    return;
                                }
                                if (id == oxygenSealerID) {
                                    final TileEntityOxygenSealer sealer = this.sealersAround.get(sideVec);
                                    if (sealer != null && !this.sealers.contains(sealer)) {
                                        if (side != 0) {
                                            continue;
                                        }
                                        this.checkedAdd(sideVec);
                                        this.sealers.add(sealer);
                                        if (sealer.thermalControlEnabled()) {
                                            this.foundAmbientThermal = true;
                                        }
                                        this.checkCount += sealer.getFindSealChecks();
                                    }
                                    else {
                                        this.checkedAdd(sideVec);
                                    }
                                }
                                else {
                                    if (!this.canBlockPassAirCheck(id, sideVec, side)) {
                                        continue;
                                    }
                                    nextLayer.add(sideVec);
                                }
                            }
                        }
                        else {
                            final Block id = sideVec.getBlockIDsafe_noChunkLoad(this.world);
                            if (id == null || id == airID || id == breatheableAirID || id == airIDBright || id == breatheableAirIDBright || this.canBlockPassAirCheck(id, sideVec, side)) {
                                this.sealed = false;
                                if (this.sealers.size() > 0) {
                                    vec.sideDoneBits = side << 6;
                                    this.traceLeak(vec);
                                }
                                return;
                            }
                            continue;
                        }
                    }
                } while (++side < 6);
            }
            this.currentLayer = (List<BlockVec3>)nextLayer;
            nextLayer = new LinkedList();
        }
    }
    
    private void doLayerNearMapEdge() {
        final Block breatheableAirID = GCBlocks.breatheableAir;
        final Block airID = Blocks.air;
        final Block breatheableAirIDBright = GCBlocks.brightBreatheableAir;
        final Block airIDBright = GCBlocks.brightAir;
        final Block oxygenSealerID = GCBlocks.oxygenSealer;
        LinkedList nextLayer = new LinkedList();
        while (this.sealed && this.currentLayer.size() > 0) {
            for (final BlockVec3 vec : this.currentLayer) {
                int side = 0;
                final int bits = vec.sideDoneBits;
                do {
                    if ((bits & 1 << side) == 0x0 && !this.checkedContains(vec, side)) {
                        final BlockVec3 sideVec = vec.newVecSide(side);
                        if (this.checkCount > 0) {
                            --this.checkCount;
                            final Block id = sideVec.getBlockID_noChunkLoad(this.world);
                            if (id == breatheableAirID) {
                                this.checkedAdd(sideVec);
                                nextLayer.add(sideVec);
                                this.ambientThermalTracked.add(sideVec);
                            }
                            else if (id == airID) {
                                this.checkedAdd(sideVec);
                                nextLayer.add(sideVec);
                                this.airToReplace.add(sideVec);
                            }
                            else if (id == breatheableAirIDBright) {
                                this.checkedAdd(sideVec);
                                nextLayer.add(sideVec);
                                this.ambientThermalTracked.add(sideVec);
                            }
                            else if (id == airIDBright) {
                                this.checkedAdd(sideVec);
                                nextLayer.add(sideVec);
                                this.airToReplaceBright.add(sideVec);
                            }
                            else {
                                if (id == null) {
                                    this.checkCount = 0;
                                    this.sealed = false;
                                    return;
                                }
                                if (id == oxygenSealerID) {
                                    final TileEntityOxygenSealer sealer = this.sealersAround.get(sideVec);
                                    if (sealer != null && !this.sealers.contains(sealer)) {
                                        if (side != 0) {
                                            continue;
                                        }
                                        this.checkedAdd(sideVec);
                                        this.sealers.add(sealer);
                                        if (sealer.thermalControlEnabled()) {
                                            this.foundAmbientThermal = true;
                                        }
                                        this.checkCount += sealer.getFindSealChecks();
                                    }
                                    else {
                                        this.checkedAdd(sideVec);
                                    }
                                }
                                else {
                                    if (!this.canBlockPassAirCheck(id, sideVec, side)) {
                                        continue;
                                    }
                                    nextLayer.add(sideVec);
                                }
                            }
                        }
                        else {
                            final Block id = sideVec.getBlockID_noChunkLoad(this.world);
                            if (id == null || id == airID || id == breatheableAirID || id == airIDBright || id == breatheableAirIDBright || this.canBlockPassAirCheck(id, sideVec, side)) {
                                this.sealed = false;
                                if (this.sealers.size() > 0) {
                                    vec.sideDoneBits = side << 6;
                                    this.traceLeak(vec);
                                }
                                return;
                            }
                            continue;
                        }
                    }
                } while (++side < 6);
            }
            this.currentLayer = (List<BlockVec3>)nextLayer;
            nextLayer = new LinkedList();
        }
    }
    
    private void checkedAdd(final BlockVec3 vec) {
        final int dx = this.head.x - vec.x;
        final int dz = this.head.z - vec.z;
        if (dx < -8191 || dx > 8192) {
            return;
        }
        if (dz < -8191 || dz > 8192) {
            return;
        }
        final intBucket bucket = this.buckets[((dx & 0xF) << 4) + (dz & 0xF)];
        bucket.add(vec.y + ((dx & 0x3FF0) + ((dz & 0x3FF0) << 10) + ((vec.sideDoneBits & 0x1C0) << 18) << 4));
    }
    
    private boolean checkedContains(final BlockVec3 vec) {
        final int dx = this.head.x - vec.x;
        final int dz = this.head.z - vec.z;
        if (dx < -8191 || dx > 8192) {
            return true;
        }
        if (dz < -8191 || dz > 8192) {
            return true;
        }
        final intBucket bucket = this.buckets[((dx & 0xF) << 4) + (dz & 0xF)];
        return bucket.contains(vec.y + ((dx & 0x3FF0) + ((dz & 0x3FF0) << 10) << 4));
    }
    
    private boolean checkedContains(final BlockVec3 vec, final int side) {
        int y = vec.y;
        int dx = this.head.x - vec.x;
        int dz = this.head.z - vec.z;
        switch (side) {
            case 0: {
                if (--y < 0) {
                    return false;
                }
                break;
            }
            case 1: {
                if (++y > 255) {
                    return false;
                }
                break;
            }
            case 2: {
                ++dz;
                break;
            }
            case 3: {
                --dz;
                break;
            }
            case 4: {
                ++dx;
                break;
            }
            case 5: {
                --dx;
                break;
            }
        }
        if (dx < -8191 || dx > 8192) {
            return true;
        }
        if (dz < -8191 || dz > 8192) {
            return true;
        }
        final intBucket bucket = this.buckets[((dx & 0xF) << 4) + (dz & 0xF)];
        return bucket.contains(y + ((dx & 0x3FF0) + ((dz & 0x3FF0) << 10) << 4));
    }
    
    private BlockVec3 checkedContainsTrace(final int x, final int y, final int z) {
        final int dx = this.head.x - x;
        final int dz = this.head.z - z;
        if (dx < -8191 || dx > 8192) {
            return null;
        }
        if (dz < -8191 || dz > 8192) {
            return null;
        }
        final intBucket bucket = this.buckets[((dx & 0xF) << 4) + (dz & 0xF)];
        final int side = bucket.getMSB4shifted(y + ((dx & 0x3FF0) + ((dz & 0x3FF0) << 10) << 4));
        if (side >= 0) {
            final BlockVec3 vec = new BlockVec3(x, y, z);
            vec.sideDoneBits = side;
            return vec;
        }
        return null;
    }
    
    private void checkedInit() {
        for (int i = 0; i < 256; ++i) {
            this.buckets[i] = new intBucket();
        }
    }
    
    private void checkedClear() {
        for (int i = 0; i < 256; ++i) {
            this.buckets[i].clear();
        }
        this.checkedSize = 0;
    }
    
    public List<BlockVec3> checkedAll() {
        final List<BlockVec3> list = new LinkedList<BlockVec3>();
        for (int i = 0; i < 256; ++i) {
            if (this.buckets[i].size() != 0) {
                final int ddx = i >> 4;
                final int ddz = i & 0xF;
                final int[] ints = this.buckets[i].contents();
                for (int j = 0; j < this.buckets[i].size(); ++j) {
                    int k = ints[j];
                    final int y = k & 0xFF;
                    k >>= 4;
                    int dx = (k & 0x3FF0) + ddx;
                    int dz = (k >> 10 & 0x3FF0) + ddz;
                    if (dx > 8192) {
                        dx -= 16384;
                    }
                    if (dz > 8192) {
                        dz -= 16384;
                    }
                    list.add(new BlockVec3(this.head.x + dx, y, this.head.z + dz));
                }
            }
        }
        return list;
    }
    
    private void traceLeak(BlockVec3 tracer) {
        GCLog.debug("Leak tracing test length = " + this.checkedSize);
        final ArrayList<BlockVec3> route = new ArrayList<BlockVec3>();
        final BlockVec3 start = this.head.clone().translate(0, 1, 0);
        int count = 0;
        int x = tracer.x;
        int y = tracer.y;
        int z = tracer.z;
        while (!tracer.equals((Object)start) && count < 90) {
            route.add(tracer);
            switch (tracer.sideDoneBits >> 6) {
                case 1: {
                    --y;
                    break;
                }
                case 0: {
                    ++y;
                    break;
                }
                case 3: {
                    --z;
                    break;
                }
                case 2: {
                    ++z;
                    break;
                }
                case 5: {
                    --x;
                    break;
                }
                case 4: {
                    ++x;
                    break;
                }
            }
            tracer = this.checkedContainsTrace(x, y, z);
            if (tracer == null) {
                return;
            }
            ++count;
        }
        (this.leakTrace = new ArrayList<BlockVec3>()).add(start);
        for (int j = route.size() - 1; j >= 0; --j) {
            this.leakTrace.add(route.get(j));
        }
    }
    
    private boolean canBlockPassAirCheck(final Block block, final BlockVec3 vec, final int side) {
        if (block instanceof IPartialSealableBlock) {
            final IPartialSealableBlock blockPartial = (IPartialSealableBlock)block;
            if (blockPartial.isSealed(this.world, vec.x, vec.y, vec.z, ForgeDirection.getOrientation(side))) {
                --this.checkCount;
                return false;
            }
            for (int i = 0; i < 6; ++i) {
                if (i != side) {
                    if (blockPartial.isSealed(this.world, vec.x, vec.y, vec.z, ForgeDirection.getOrientation(i))) {
                        vec.setSideDone(i ^ 0x1);
                    }
                }
            }
            this.checkedAdd(vec);
            return true;
        }
        else {
            if (block instanceof BlockLeavesBase) {
                this.checkedAdd(vec);
                return true;
            }
            if (block.isOpaqueCube()) {
                this.checkedAdd(vec);
                return block instanceof BlockGravel || block.getMaterial() == Material.cloth || block instanceof BlockSponge;
            }
            if (block instanceof BlockGlass || block instanceof BlockStainedGlass) {
                this.checkedAdd(vec);
                return false;
            }
            if (OxygenPressureProtocol.nonPermeableBlocks.containsKey(block)) {
                final ArrayList<Integer> metaList = OxygenPressureProtocol.nonPermeableBlocks.get(block);
                if (metaList.contains(-1) || metaList.contains(vec.getBlockMetadata((IBlockAccess)this.world))) {
                    this.checkedAdd(vec);
                    return false;
                }
            }
            if (block instanceof BlockUnlitTorch) {
                this.torchesToUpdate.add(vec);
                this.checkedAdd(vec);
                return true;
            }
            if (block instanceof BlockSlab) {
                final boolean isTopSlab = (vec.getBlockMetadata((IBlockAccess)this.world) & 0x8) == 0x8;
                if ((side == 0 && isTopSlab) || (side == 1 && !isTopSlab)) {
                    --this.checkCount;
                    return false;
                }
                vec.setSideDone((int)(isTopSlab ? 1 : 0));
                this.checkedAdd(vec);
                return true;
            }
            else if (block instanceof BlockFarmland || block instanceof BlockEnchantmentTable || block instanceof BlockLiquid) {
                if (side == 1) {
                    --this.checkCount;
                    return false;
                }
                vec.setSideDone(0);
                this.checkedAdd(vec);
                return true;
            }
            else if (block instanceof BlockPistonBase) {
                final BlockPistonBase piston = (BlockPistonBase)block;
                final int meta = vec.getBlockMetadata((IBlockAccess)this.world);
                if (!BlockPistonBase.isExtended(meta)) {
                    this.checkedAdd(vec);
                    return false;
                }
                final int facing = BlockPistonBase.getPistonOrientation(meta);
                if (side == facing) {
                    --this.checkCount;
                    return false;
                }
                vec.setSideDone(facing ^ 0x1);
                this.checkedAdd(vec);
                return true;
            }
            else if (block.isSideSolid((IBlockAccess)this.world, vec.x, vec.y, vec.z, ForgeDirection.getOrientation(side ^ 0x1))) {
                if (block.getMaterial().blocksMovement() && block.renderAsNormalBlock()) {
                    this.checkedAdd(vec);
                    return false;
                }
                --this.checkCount;
                return false;
            }
            else {
                if (block.getMaterial() == Material.air) {
                    this.checkedAdd(vec);
                    return true;
                }
                for (int j = 0; j < 6; ++j) {
                    if (j != (side ^ 0x1)) {
                        if (block.isSideSolid((IBlockAccess)this.world, vec.x, vec.y, vec.z, ForgeDirection.getOrientation(j))) {
                            vec.setSideDone(j);
                        }
                    }
                }
                this.checkedAdd(vec);
                return true;
            }
        }
    }
    
    static {
        ThreadFindSeal.anylooping = new AtomicBoolean();
    }
    
    public class ThreadedFindSeal extends Thread
    {
        public ThreadedFindSeal() {
            super("GC Sealer Roomfinder Thread");
            ThreadFindSeal.anylooping.set(true);
            if (this.isAlive()) {
                this.interrupt();
            }
            this.start();
        }
        
        @Override
        public void run() {
            ThreadFindSeal.this.check();
            ThreadFindSeal.anylooping.set(false);
        }
    }
    
    public class intBucket
    {
        private int maxSize;
        private int size;
        private int[] table;
        
        public intBucket() {
            this.maxSize = 64;
            this.size = 0;
            this.table = new int[this.maxSize];
        }
        
        public void add(final int i) {
            if (this.contains(i)) {
                return;
            }
            if (this.size >= this.maxSize) {
                final int[] newTable = new int[this.maxSize + this.maxSize];
                System.arraycopy(this.table, 0, newTable, 0, this.maxSize);
                this.table = newTable;
                this.maxSize += this.maxSize;
            }
            this.table[this.size] = i;
            ++this.size;
            ThreadFindSeal.this.checkedSize++;
        }
        
        public boolean contains(final int test) {
            for (int i = this.size - 1; i >= 0; --i) {
                if ((this.table[i] & 0xFFFFFFF) == test) {
                    return true;
                }
            }
            return false;
        }
        
        public int getMSB4shifted(final int test) {
            for (int i = this.size - 1; i >= 0; --i) {
                if ((this.table[i] & 0xFFFFFFF) == test) {
                    return (this.table[i] & 0xF0000000) >> 22;
                }
            }
            return -1;
        }
        
        public void clear() {
            this.size = 0;
        }
        
        public int size() {
            return this.size;
        }
        
        public int[] contents() {
            return this.table;
        }
    }
}
