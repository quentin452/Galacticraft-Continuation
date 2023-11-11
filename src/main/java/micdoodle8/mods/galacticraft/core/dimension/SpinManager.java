package micdoodle8.mods.galacticraft.core.dimension;

import micdoodle8.mods.galacticraft.api.vector.*;
import cpw.mods.fml.relauncher.*;
import micdoodle8.mods.galacticraft.core.blocks.*;
import net.minecraft.block.material.*;
import micdoodle8.mods.galacticraft.core.util.*;
import micdoodle8.mods.galacticraft.core.*;
import micdoodle8.mods.galacticraft.core.network.*;
import net.minecraft.world.*;
import net.minecraft.block.*;
import java.util.*;
import net.minecraft.entity.item.*;
import net.minecraft.init.*;
import net.minecraft.entity.*;
import net.minecraft.client.entity.*;
import net.minecraft.util.*;
import net.minecraft.nbt.*;
import net.minecraft.entity.player.*;

public class SpinManager
{
    private static final float GFORCE = 0.024525002f;
    private OrbitSpinSaveData savefile;
    public boolean doSpinning;
    public float angularVelocityRadians;
    public float skyAngularVelocity;
    private float angularVelocityTarget;
    private float angularVelocityAccel;
    public double spinCentreX;
    public double spinCentreZ;
    private float momentOfInertia;
    private float massCentreX;
    private float massCentreZ;
    public int ssBoundsMaxX;
    public int ssBoundsMinX;
    public int ssBoundsMaxY;
    public int ssBoundsMinY;
    public int ssBoundsMaxZ;
    public int ssBoundsMinZ;
    private LinkedList<BlockVec3> thrustersPlus;
    private LinkedList<BlockVec3> thrustersMinus;
    private BlockVec3 oneSSBlock;
    private HashSet<BlockVec3> checked;
    private float artificialG;
    public boolean thrustersFiring;
    private boolean dataNotLoaded;
    private List<Entity> loadedEntities;
    private double pPrevMotionX;
    public double pPrevMotionY;
    private double pPrevMotionZ;
    private WorldProviderSpaceStation worldProvider;
    private boolean clientSide;
    
    public SpinManager(final WorldProviderSpaceStation provider) {
        this.doSpinning = true;
        this.angularVelocityRadians = 0.0f;
        this.skyAngularVelocity = (float)(this.angularVelocityRadians * 180.0f / 3.141592653589793);
        this.angularVelocityTarget = 0.0f;
        this.angularVelocityAccel = 0.0f;
        this.thrustersPlus = new LinkedList<BlockVec3>();
        this.thrustersMinus = new LinkedList<BlockVec3>();
        this.checked = new HashSet<BlockVec3>();
        this.thrustersFiring = false;
        this.dataNotLoaded = true;
        this.loadedEntities = new LinkedList<Entity>();
        this.pPrevMotionX = 0.0;
        this.pPrevMotionY = 0.0;
        this.pPrevMotionZ = 0.0;
        this.clientSide = true;
        this.worldProvider = provider;
    }
    
    public void registerServerSide() {
        if (!this.worldProvider.worldObj.isRemote) {
            this.clientSide = false;
        }
    }
    
    public float getSpinRate() {
        return this.skyAngularVelocity;
    }
    
    public void setSpinRate(final float angle) {
        this.angularVelocityRadians = angle;
        this.skyAngularVelocity = angle * 180.0f / 3.1415927f;
        if (this.clientSide) {
            this.updateSkyProviderSpinRate();
        }
    }
    
    @SideOnly(Side.CLIENT)
    private void updateSkyProviderSpinRate() {
        this.worldProvider.setSpinDeltaPerTick(this.skyAngularVelocity);
    }
    
    public void setSpinRate(final float angle, final boolean firing) {
        this.angularVelocityRadians = angle;
        this.skyAngularVelocity = angle * 180.0f / 3.1415927f;
        this.worldProvider.setSpinDeltaPerTick(this.skyAngularVelocity);
        this.thrustersFiring = firing;
    }
    
    public void setSpinCentre(final double x, final double z) {
        this.spinCentreX = x;
        this.spinCentreZ = z;
        if (this.clientSide && ConfigManagerCore.enableDebug) {
            GCLog.info("Clientside update to spin centre: " + x + "," + z);
        }
    }
    
    public void setSpinBox(final int mx, final int xx, final int my, final int yy, final int mz, final int zz) {
        this.ssBoundsMinX = mx;
        this.ssBoundsMaxX = xx;
        this.ssBoundsMinY = my;
        this.ssBoundsMaxY = yy;
        this.ssBoundsMinZ = mz;
        this.ssBoundsMaxZ = zz;
    }
    
    public void addThruster(final BlockVec3 thruster, final boolean positive) {
        if (positive) {
            this.thrustersPlus.add(thruster);
            this.thrustersMinus.remove(thruster);
        }
        else {
            this.thrustersPlus.remove(thruster);
            this.thrustersMinus.add(thruster);
        }
    }
    
    public void removeThruster(final BlockVec3 thruster, final boolean positive) {
        if (positive) {
            this.thrustersPlus.remove(thruster);
        }
        else {
            this.thrustersMinus.remove(thruster);
        }
    }
    
    public boolean checkSS(final BlockVec3 baseBlock, final boolean placingThruster) {
        final World worldObj = this.worldProvider.worldObj;
        if (this.oneSSBlock == null || this.oneSSBlock.getBlockID(worldObj) instanceof BlockAir) {
            if (baseBlock != null) {
                this.oneSSBlock = baseBlock.clone();
            }
            else {
                this.oneSSBlock = new BlockVec3(0, 64, 0);
            }
        }
        List<BlockVec3> currentLayer = new LinkedList<BlockVec3>();
        List<BlockVec3> nextLayer = new LinkedList<BlockVec3>();
        final List<BlockVec3> foundThrusters = new LinkedList<BlockVec3>();
        this.checked.clear();
        currentLayer.add(this.oneSSBlock.clone());
        this.checked.add(this.oneSSBlock.clone());
        Block bStart = this.oneSSBlock.getBlockID(worldObj);
        if (bStart instanceof BlockSpinThruster) {
            foundThrusters.add(this.oneSSBlock);
        }
        float thismass = 0.1f;
        float thismassCentreX = 0.1f * this.oneSSBlock.x;
        float thismassCentreY = 0.1f * this.oneSSBlock.y;
        float thismassCentreZ = 0.1f * this.oneSSBlock.z;
        float thismoment = 0.0f;
        int thisssBoundsMaxX = this.oneSSBlock.x;
        int thisssBoundsMinX = this.oneSSBlock.x;
        int thisssBoundsMaxY = this.oneSSBlock.y;
        int thisssBoundsMinY = this.oneSSBlock.y;
        int thisssBoundsMaxZ = this.oneSSBlock.z;
        int thisssBoundsMinZ = this.oneSSBlock.z;
        while (currentLayer.size() > 0) {
            for (final BlockVec3 vec : currentLayer) {
                final int bits = vec.sideDoneBits;
                if (vec.x < thisssBoundsMinX) {
                    thisssBoundsMinX = vec.x;
                }
                if (vec.y < thisssBoundsMinY) {
                    thisssBoundsMinY = vec.y;
                }
                if (vec.z < thisssBoundsMinZ) {
                    thisssBoundsMinZ = vec.z;
                }
                if (vec.x > thisssBoundsMaxX) {
                    thisssBoundsMaxX = vec.x;
                }
                if (vec.y > thisssBoundsMaxY) {
                    thisssBoundsMaxY = vec.y;
                }
                if (vec.z > thisssBoundsMaxZ) {
                    thisssBoundsMaxZ = vec.z;
                }
                for (int side = 0; side < 6; ++side) {
                    if ((bits & 1 << side) != 0x1) {
                        final BlockVec3 sideVec = vec.newVecSide(side);
                        if (!this.checked.contains(sideVec)) {
                            this.checked.add(sideVec);
                            final Block b = sideVec.getBlockID(worldObj);
                            if (b != null && !b.isAir((IBlockAccess)worldObj, sideVec.x, sideVec.y, sideVec.z)) {
                                nextLayer.add(sideVec);
                                if (bStart.isAir((IBlockAccess)worldObj, this.oneSSBlock.x, this.oneSSBlock.y, this.oneSSBlock.z)) {
                                    this.oneSSBlock = sideVec.clone();
                                    bStart = b;
                                }
                                float m = 1.0f;
                                if (!(b instanceof BlockLiquid)) {
                                    m = b.getBlockHardness(worldObj, sideVec.x, sideVec.y, sideVec.z);
                                    if (m < 0.1f) {
                                        m = 0.1f;
                                    }
                                    else if (m > 30.0f) {
                                        m = 30.0f;
                                    }
                                    if (b.getMaterial() == Material.wood) {
                                        m /= 4.0f;
                                    }
                                }
                                thismassCentreX += m * sideVec.x;
                                thismassCentreY += m * sideVec.y;
                                thismassCentreZ += m * sideVec.z;
                                thismass += m;
                                thismoment += m * (sideVec.x * sideVec.x + sideVec.z * sideVec.z);
                                if (b instanceof BlockSpinThruster && !RedstoneUtil.isBlockReceivingRedstone(worldObj, sideVec.x, sideVec.y, sideVec.z)) {
                                    foundThrusters.add(sideVec);
                                }
                            }
                        }
                    }
                }
            }
            currentLayer = nextLayer;
            nextLayer = new LinkedList<BlockVec3>();
        }
        if (!placingThruster || this.checked.contains(baseBlock)) {
            this.thrustersPlus.clear();
            this.thrustersMinus.clear();
            for (final BlockVec3 thruster : foundThrusters) {
                final int facing = thruster.getBlockMetadata((IBlockAccess)worldObj) & 0x8;
                if (facing == 0) {
                    this.thrustersPlus.add(thruster.clone());
                }
                else {
                    this.thrustersMinus.add(thruster.clone());
                }
            }
            final float mass = thismass;
            this.massCentreX = thismassCentreX / thismass + 0.5f;
            final float massCentreY = thismassCentreY / thismass + 0.5f;
            this.massCentreZ = thismassCentreZ / thismass + 0.5f;
            this.setSpinCentre(this.massCentreX, this.massCentreZ);
            this.ssBoundsMaxX = thisssBoundsMaxX + 1;
            this.ssBoundsMinX = thisssBoundsMinX;
            this.ssBoundsMaxY = thisssBoundsMaxY + 1;
            this.ssBoundsMinY = thisssBoundsMinY;
            this.ssBoundsMaxZ = thisssBoundsMaxZ + 1;
            this.ssBoundsMinZ = thisssBoundsMinZ;
            thismoment -= this.massCentreX * this.massCentreX * mass;
            thismoment -= this.massCentreZ * this.massCentreZ * mass;
            this.momentOfInertia = thismoment;
            GCLog.debug("MoI = " + this.momentOfInertia + " CoMx = " + this.massCentreX + " CoMz = " + this.massCentreZ);
            List<Object> objList = new ArrayList<Object>();
            objList.add(this.spinCentreX);
            objList.add(this.spinCentreZ);
            GalacticraftCore.packetPipeline.sendToDimension(new PacketSimple(PacketSimple.EnumSimplePacket.C_UPDATE_STATION_DATA, objList), this.worldProvider.dimensionId);
            objList = new ArrayList<Object>();
            objList.add(this.ssBoundsMinX);
            objList.add(this.ssBoundsMaxX);
            objList.add(this.ssBoundsMinY);
            objList.add(this.ssBoundsMaxY);
            objList.add(this.ssBoundsMinZ);
            objList.add(this.ssBoundsMaxZ);
            GalacticraftCore.packetPipeline.sendToDimension(new PacketSimple(PacketSimple.EnumSimplePacket.C_UPDATE_STATION_BOX, objList), this.worldProvider.dimensionId);
            this.updateSpinSpeed();
            return true;
        }
        if (foundThrusters.size() > 0) {
            if (ConfigManagerCore.enableDebug) {
                GCLog.info("Thruster placed on wrong part of space station: base at " + this.oneSSBlock.x + "," + this.oneSSBlock.y + "," + this.oneSSBlock.z + " - baseBlock was " + baseBlock.x + "," + baseBlock.y + "," + baseBlock.z + " - found " + foundThrusters.size());
            }
            return false;
        }
        if (!this.oneSSBlock.equals((Object)baseBlock)) {
            this.oneSSBlock = baseBlock.clone();
            if (this.oneSSBlock.getBlockID(worldObj).getMaterial() != Material.air) {
                return this.checkSS(baseBlock, true);
            }
        }
        return false;
    }
    
    public void updateSpinSpeed() {
        if (this.momentOfInertia > 0.0f) {
            float netTorque = 0.0f;
            int countThrusters = 0;
            int countThrustersReverse = 0;
            for (final BlockVec3 thruster : this.thrustersPlus) {
                final float xx = thruster.x - this.massCentreX;
                final float zz = thruster.z - this.massCentreZ;
                netTorque += MathHelper.sqrt_float(xx * xx + zz * zz);
                ++countThrusters;
            }
            for (final BlockVec3 thruster : this.thrustersMinus) {
                final float xx = thruster.x - this.massCentreX;
                final float zz = thruster.z - this.massCentreZ;
                netTorque -= MathHelper.sqrt_float(xx * xx + zz * zz);
                ++countThrustersReverse;
            }
            if (countThrusters == countThrustersReverse) {
                this.angularVelocityAccel = 4.0E-6f;
                this.angularVelocityTarget = 0.0f;
            }
            else {
                countThrusters += countThrustersReverse;
                if (countThrusters > 4) {
                    countThrusters = 4;
                }
                final float maxRx = Math.max(this.ssBoundsMaxX - this.massCentreX, this.massCentreX - this.ssBoundsMinX);
                final float maxRz = Math.max(this.ssBoundsMaxZ - this.massCentreZ, this.massCentreZ - this.ssBoundsMinZ);
                final float maxR = Math.max(maxRx, maxRz);
                this.angularVelocityTarget = MathHelper.sqrt_float(0.024525002f / maxR) / 2.0f;
                final float spinCap = 0.00125f * countThrusters;
                this.angularVelocityAccel = netTorque / this.momentOfInertia / 20.0f;
                if (this.angularVelocityAccel < 0.0f) {
                    this.angularVelocityAccel = -this.angularVelocityAccel;
                    this.angularVelocityTarget = -this.angularVelocityTarget;
                    if (this.angularVelocityTarget < -spinCap) {
                        this.angularVelocityTarget = -spinCap;
                    }
                }
                else if (this.angularVelocityTarget > spinCap) {
                    this.angularVelocityTarget = spinCap;
                }
                if (ConfigManagerCore.enableDebug) {
                    GCLog.info("MaxR = " + maxR + " Angular vel = " + this.angularVelocityTarget + " Angular accel = " + this.angularVelocityAccel);
                }
            }
        }
        if (!this.clientSide) {
            if (this.savefile == null) {
                this.savefile = OrbitSpinSaveData.initWorldData(this.worldProvider.worldObj);
                this.dataNotLoaded = false;
            }
            else {
                this.writeToNBT(this.savefile.datacompound);
                this.savefile.markDirty();
            }
        }
    }
    
    public void updateSpin() {
        if (!this.clientSide) {
            if (this.dataNotLoaded) {
                this.savefile = OrbitSpinSaveData.initWorldData(this.worldProvider.worldObj);
                this.readFromNBT(this.savefile.datacompound);
                if (ConfigManagerCore.enableDebug) {
                    GCLog.info("Loading data from save: " + this.savefile.datacompound.getFloat("omegaSky"));
                }
                this.dataNotLoaded = false;
            }
            if (this.doSpinning) {
                boolean updateNeeded = true;
                if (this.angularVelocityTarget < this.angularVelocityRadians) {
                    float newAngle = this.angularVelocityRadians - this.angularVelocityAccel;
                    if (newAngle < this.angularVelocityTarget) {
                        newAngle = this.angularVelocityTarget;
                    }
                    this.setSpinRate(newAngle);
                    this.thrustersFiring = true;
                }
                else if (this.angularVelocityTarget > this.angularVelocityRadians) {
                    float newAngle = this.angularVelocityRadians + this.angularVelocityAccel;
                    if (newAngle > this.angularVelocityTarget) {
                        newAngle = this.angularVelocityTarget;
                    }
                    this.setSpinRate(newAngle);
                    this.thrustersFiring = true;
                }
                else if (this.thrustersFiring) {
                    this.thrustersFiring = false;
                }
                else {
                    updateNeeded = false;
                }
                if (updateNeeded) {
                    this.writeToNBT(this.savefile.datacompound);
                    this.savefile.markDirty();
                    final List<Object> objList = new ArrayList<Object>();
                    objList.add(this.angularVelocityRadians);
                    objList.add(this.thrustersFiring);
                    GalacticraftCore.packetPipeline.sendToDimension(new PacketSimple(PacketSimple.EnumSimplePacket.C_UPDATE_STATION_SPIN, objList), this.worldProvider.dimensionId);
                }
                final World worldObj = this.worldProvider.worldObj;
                this.loadedEntities.clear();
                this.loadedEntities.addAll(worldObj.loadedEntityList);
                for (final Entity e : this.loadedEntities) {
                    if ((e instanceof EntityItem || (e instanceof EntityLivingBase && !(e instanceof EntityPlayer)) || e instanceof EntityTNTPrimed || e instanceof EntityFallingBlock) && !e.onGround) {
                        boolean freefall = true;
                        Label_0689: {
                            if (e.boundingBox.maxX >= this.ssBoundsMinX && e.boundingBox.minX <= this.ssBoundsMaxX && e.boundingBox.maxY >= this.ssBoundsMinY && e.boundingBox.minY <= this.ssBoundsMaxY && e.boundingBox.maxZ >= this.ssBoundsMinZ && e.boundingBox.minZ <= this.ssBoundsMaxZ) {
                                final int xmx = MathHelper.floor_double(e.boundingBox.maxX + 0.2);
                                final int ym = MathHelper.floor_double(e.boundingBox.minY - 0.1);
                                final int yy = MathHelper.floor_double(e.boundingBox.maxY + 0.1);
                                final int zm = MathHelper.floor_double(e.boundingBox.minZ - 0.2);
                                final int zz = MathHelper.floor_double(e.boundingBox.maxZ + 0.2);
                                for (int x = MathHelper.floor_double(e.boundingBox.minX - 0.2); x <= xmx; ++x) {
                                    for (int y = ym; y <= yy; ++y) {
                                        for (int z = zm; z <= zz; ++z) {
                                            if (worldObj.blockExists(x, y, z) && Blocks.air != worldObj.getBlock(x, y, z)) {
                                                freefall = false;
                                                break Label_0689;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        if (!freefall) {
                            continue;
                        }
                        if (this.angularVelocityRadians != 0.0f) {
                            final double xx = e.posX - this.spinCentreX;
                            final double zz2 = e.posZ - this.spinCentreZ;
                            double arc = Math.sqrt(xx * xx + zz2 * zz2);
                            float angle;
                            if (xx == 0.0) {
                                angle = ((zz2 > 0.0) ? 1.5707964f : -1.5707964f);
                            }
                            else {
                                angle = (float)Math.atan(zz2 / xx);
                            }
                            if (xx < 0.0) {
                                angle += 3.1415927f;
                            }
                            angle += this.angularVelocityRadians / 3.0f;
                            arc *= this.angularVelocityRadians;
                            final double offsetX = -arc * MathHelper.sin(angle);
                            final double offsetZ = arc * MathHelper.cos(angle);
                            final Entity entity = e;
                            entity.posX += offsetX;
                            final Entity entity2 = e;
                            entity2.posZ += offsetZ;
                            final Entity entity3 = e;
                            entity3.lastTickPosX += offsetX;
                            final Entity entity4 = e;
                            entity4.lastTickPosZ += offsetZ;
                            if (!worldObj.blockExists(MathHelper.floor_double(e.posX), 64, MathHelper.floor_double(e.posZ))) {
                                e.setDead();
                            }
                            e.boundingBox.offset(offsetX, 0.0, offsetZ);
                            final Entity entity5 = e;
                            entity5.rotationYaw += this.skyAngularVelocity;
                            while (e.rotationYaw > 360.0f) {
                                final Entity entity6 = e;
                                entity6.rotationYaw -= 360.0f;
                            }
                        }
                        if (e instanceof EntityLivingBase) {
                            final Entity entity7 = e;
                            entity7.motionX /= 0.9100000262260437;
                            final Entity entity8 = e;
                            entity8.motionZ /= 0.9100000262260437;
                            if (e instanceof EntityFlying) {
                                final Entity entity9 = e;
                                entity9.motionY /= 0.9100000262260437;
                            }
                            else {
                                final Entity entity10 = e;
                                entity10.motionY /= 0.9800000190734863;
                            }
                        }
                        else if (e instanceof EntityFallingBlock) {
                            final Entity entity11 = e;
                            entity11.motionY /= 0.9800000190734863;
                        }
                        else {
                            final Entity entity12 = e;
                            entity12.motionX /= 0.9800000190734863;
                            final Entity entity13 = e;
                            entity13.motionY /= 0.9800000190734863;
                            final Entity entity14 = e;
                            entity14.motionZ /= 0.9800000190734863;
                        }
                    }
                }
            }
        }
    }
    
    @SideOnly(Side.CLIENT)
    public boolean updatePlayerForSpin(final EntityPlayerSP p, final float partialTicks) {
        final float angleDelta = partialTicks * this.angularVelocityRadians;
        if (this.doSpinning && angleDelta != 0.0f) {
            boolean doGravity = false;
            final double xx = p.posX - this.spinCentreX;
            final double zz = p.posZ - this.spinCentreZ;
            double arc = Math.sqrt(xx * xx + zz * zz);
            float angle;
            if (xx == 0.0) {
                angle = ((zz > 0.0) ? 1.5707964f : -1.5707964f);
            }
            else {
                angle = (float)Math.atan(zz / xx);
            }
            if (xx < 0.0) {
                angle += 3.1415927f;
            }
            angle += angleDelta / 3.0f;
            arc *= angleDelta;
            double offsetX = -arc * MathHelper.sin(angle);
            double offsetZ = arc * MathHelper.cos(angle);
            if (p.worldObj.getCollidingBoundingBoxes((Entity)p, p.boundingBox).size() == 0) {
                int collisions = 0;
                do {
                    final List<AxisAlignedBB> list = (List<AxisAlignedBB>)p.worldObj.getCollidingBoundingBoxes((Entity)p, p.boundingBox.addCoord(offsetX, 0.0, offsetZ));
                    collisions = list.size();
                    if (collisions > 0) {
                        if (!doGravity) {
                            p.motionX += -offsetX;
                            p.motionZ += -offsetZ;
                        }
                        offsetX /= 2.0;
                        offsetZ /= 2.0;
                        if (offsetX < 0.01 && offsetX > -0.01) {
                            offsetX = 0.0;
                        }
                        if (offsetZ < 0.01 && offsetZ > -0.01) {
                            offsetZ = 0.0;
                        }
                        doGravity = true;
                    }
                } while (collisions > 0);
                p.posX += offsetX;
                p.posZ += offsetZ;
                p.boundingBox.offset(offsetX, 0.0, offsetZ);
            }
            p.rotationYaw += this.skyAngularVelocity * partialTicks;
            p.renderYawOffset += this.skyAngularVelocity * partialTicks;
            while (p.rotationYaw > 360.0f) {
                p.rotationYaw -= 360.0f;
            }
            while (p.rotationYaw < 0.0f) {
                p.rotationYaw += 360.0f;
            }
            return doGravity;
        }
        return false;
    }
    
    @SideOnly(Side.CLIENT)
    public void applyCentrifugalForce(final EntityPlayerSP p) {
        int quadrant = 0;
        final double xd = p.posX - this.spinCentreX;
        final double zd = p.posZ - this.spinCentreZ;
        final double accel = Math.sqrt(xd * xd + zd * zd) * this.angularVelocityRadians * this.angularVelocityRadians * 4.0;
        if (xd < 0.0) {
            if (xd < -Math.abs(zd)) {
                quadrant = 2;
            }
            else {
                quadrant = ((zd < 0.0) ? 3 : 1);
            }
        }
        else if (xd > Math.abs(zd)) {
            quadrant = 0;
        }
        else {
            quadrant = ((zd < 0.0) ? 3 : 1);
        }
        switch (quadrant) {
            case 0: {
                p.motionX += accel;
                break;
            }
            case 1: {
                p.motionZ += accel;
                break;
            }
            case 2: {
                p.motionX -= accel;
                break;
            }
            default: {
                p.motionZ -= accel;
                break;
            }
        }
    }
    
    public void readFromNBT(final NBTTagCompound nbt) {
        this.doSpinning = true;
        this.angularVelocityRadians = nbt.getFloat("omegaRad");
        this.skyAngularVelocity = nbt.getFloat("omegaSky");
        this.angularVelocityTarget = nbt.getFloat("omegaTarget");
        this.angularVelocityAccel = nbt.getFloat("omegaAcc");
        final NBTTagCompound oneBlock = (NBTTagCompound)nbt.getTag("oneBlock");
        if (oneBlock != null) {
            this.oneSSBlock = BlockVec3.readFromNBT(oneBlock);
        }
        else {
            this.oneSSBlock = null;
        }
        this.checkSS(this.oneSSBlock, false);
        List<Object> objList = new ArrayList<Object>();
        objList.add(this.angularVelocityRadians);
        objList.add(this.thrustersFiring);
        GalacticraftCore.packetPipeline.sendToDimension(new PacketSimple(PacketSimple.EnumSimplePacket.C_UPDATE_STATION_SPIN, objList), this.worldProvider.dimensionId);
        objList = new ArrayList<Object>();
        objList.add(this.spinCentreX);
        objList.add(this.spinCentreZ);
        GalacticraftCore.packetPipeline.sendToDimension(new PacketSimple(PacketSimple.EnumSimplePacket.C_UPDATE_STATION_DATA, objList), this.worldProvider.dimensionId);
        objList = new ArrayList<Object>();
        objList.add(this.ssBoundsMinX);
        objList.add(this.ssBoundsMaxX);
        objList.add(this.ssBoundsMinY);
        objList.add(this.ssBoundsMaxY);
        objList.add(this.ssBoundsMinZ);
        objList.add(this.ssBoundsMaxZ);
        GalacticraftCore.packetPipeline.sendToDimension(new PacketSimple(PacketSimple.EnumSimplePacket.C_UPDATE_STATION_BOX, objList), this.worldProvider.dimensionId);
    }
    
    public void writeToNBT(final NBTTagCompound nbt) {
        nbt.setBoolean("doSpinning", this.doSpinning);
        nbt.setFloat("omegaRad", this.angularVelocityRadians);
        nbt.setFloat("omegaSky", this.skyAngularVelocity);
        nbt.setFloat("omegaTarget", this.angularVelocityTarget);
        nbt.setFloat("omegaAcc", this.angularVelocityAccel);
        if (this.oneSSBlock != null) {
            final NBTTagCompound oneBlock = new NBTTagCompound();
            this.oneSSBlock.writeToNBT(oneBlock);
            nbt.setTag("oneBlock", (NBTBase)oneBlock);
        }
    }
    
    public void sendPacketsToClient(final EntityPlayerMP player) {
        List<Object> objList = new ArrayList<Object>();
        objList.add(this.angularVelocityRadians);
        objList.add(this.thrustersFiring);
        GalacticraftCore.packetPipeline.sendTo(new PacketSimple(PacketSimple.EnumSimplePacket.C_UPDATE_STATION_SPIN, objList), player);
        objList = new ArrayList<Object>();
        objList.add(this.spinCentreX);
        objList.add(this.spinCentreZ);
        GalacticraftCore.packetPipeline.sendTo(new PacketSimple(PacketSimple.EnumSimplePacket.C_UPDATE_STATION_DATA, objList), player);
        objList = new ArrayList<Object>();
        objList.add(this.ssBoundsMinX);
        objList.add(this.ssBoundsMaxX);
        objList.add(this.ssBoundsMinY);
        objList.add(this.ssBoundsMaxY);
        objList.add(this.ssBoundsMinZ);
        objList.add(this.ssBoundsMaxZ);
        GalacticraftCore.packetPipeline.sendTo(new PacketSimple(PacketSimple.EnumSimplePacket.C_UPDATE_STATION_BOX, objList), player);
    }
}
