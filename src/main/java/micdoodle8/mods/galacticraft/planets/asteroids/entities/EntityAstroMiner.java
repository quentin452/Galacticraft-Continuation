package micdoodle8.mods.galacticraft.planets.asteroids.entities;

import net.minecraft.inventory.*;
import micdoodle8.mods.galacticraft.api.entity.*;
import net.minecraft.item.*;
import cpw.mods.fml.relauncher.*;
import micdoodle8.mods.galacticraft.api.vector.*;
import net.minecraft.server.gui.*;
import micdoodle8.mods.galacticraft.core.*;
import micdoodle8.mods.galacticraft.core.network.*;
import net.minecraft.entity.player.*;
import micdoodle8.mods.galacticraft.planets.asteroids.tile.*;
import net.minecraft.world.*;
import micdoodle8.mods.galacticraft.planets.asteroids.dimension.*;
import net.minecraft.tileentity.*;
import io.netty.buffer.*;
import net.minecraft.block.material.*;
import net.minecraftforge.fluids.*;
import micdoodle8.mods.galacticraft.core.blocks.*;
import net.minecraft.init.*;
import net.minecraft.block.*;
import net.minecraftforge.common.*;
import net.minecraftforge.event.world.*;
import net.minecraft.entity.item.*;
import micdoodle8.mods.galacticraft.core.items.*;
import net.minecraftforge.common.util.*;
import net.minecraft.util.*;
import net.minecraft.entity.*;
import micdoodle8.mods.galacticraft.core.entities.player.*;
import micdoodle8.mods.galacticraft.planets.asteroids.items.*;
import java.util.*;
import net.minecraft.client.entity.*;
import net.minecraft.client.audio.*;
import micdoodle8.mods.galacticraft.planets.asteroids.client.sounds.*;
import org.lwjgl.opengl.*;
import micdoodle8.mods.galacticraft.core.util.*;
import net.minecraft.nbt.*;
import micdoodle8.mods.galacticraft.planets.asteroids.blocks.*;

public class EntityAstroMiner extends Entity implements IInventory, IPacketReceiver, IEntityNoisy, IAntiGrav, ITelemetry
{
    public static final int MINE_LENGTH = 24;
    public static final int MINE_LENGTH_AST = 12;
    private static final int MAXENERGY = 12000;
    private static final int RETURNENERGY = 1000;
    private static final int RETURNDROPS = 10;
    private static final int INV_SIZE = 227;
    private static final float cLENGTH = 2.6f;
    private static final float cWIDTH = 1.8f;
    private static final float cHEIGHT = 1.7f;
    private static final double SPEEDUP = 2.5;
    public static final int AISTATE_OFFLINE = -1;
    public static final int AISTATE_STUCK = 0;
    public static final int AISTATE_ATBASE = 1;
    public static final int AISTATE_TRAVELLING = 2;
    public static final int AISTATE_MINING = 3;
    public static final int AISTATE_RETURNING = 4;
    public static final int AISTATE_DOCKING = 5;
    public static final int FAIL_BASEDESTROYED = 3;
    public static final int FAIL_OUTOFENERGY = 4;
    public static final int FAIL_RETURNPATHBLOCKED = 5;
    public static final int FAIL_ANOTHERWASLINKED = 8;
    private boolean TEMPDEBUG;
    private boolean TEMPFAST;
    public ItemStack[] cargoItems;
    public int energyLevel;
    public int mineCount;
    public float targetYaw;
    public float targetPitch;
    public int AIstate;
    public int timeInCurrentState;
    public EntityPlayerMP playerMP;
    private UUID playerUUID;
    private BlockVec3 posTarget;
    private BlockVec3 posBase;
    private BlockVec3 waypointBase;
    private LinkedList<BlockVec3> wayPoints;
    private LinkedList<BlockVec3> minePoints;
    private BlockVec3 minePointCurrent;
    private int baseFacing;
    public int facing;
    private int facingAI;
    private int lastFacing;
    private static BlockVec3[] headings;
    private static BlockVec3[] headings2;
    private final int baseSafeRadius = 32;
    private final double speedbase;
    private double speed;
    private final float rotSpeedBase;
    private float rotSpeed;
    private double speedup;
    private boolean noSpeedup;
    public float shipDamage;
    public int currentDamage;
    public int timeSinceHit;
    private boolean flagLink;
    private boolean flagCheckPlayer;
    private int turnProgress;
    private double minecartX;
    private double minecartY;
    private double minecartZ;
    private double minecartYaw;
    private double minecartPitch;
    @SideOnly(Side.CLIENT)
    private double velocityX;
    @SideOnly(Side.CLIENT)
    private double velocityY;
    @SideOnly(Side.CLIENT)
    private double velocityZ;
    private int tryBlockLimit;
    private int inventoryDrops;
    public boolean stopForTurn;
    private static ArrayList<Block> noMineList;
    public static BlockTuple blockingBlock;
    private int givenFailMessage;
    private BlockVec3 mineLast;
    private int mineCountDown;
    private int pathBlockedCount;
    public LinkedList<BlockVec3> laserBlocks;
    public LinkedList<Integer> laserTimes;
    public float retraction;
    protected IUpdatePlayerListBox soundUpdater;
    private boolean soundToStop;
    private boolean spawnedInCreative;
    
    public EntityAstroMiner(final World world, final ItemStack[] cargo, final int energy) {
        this(world);
        this.cargoItems = cargo.clone();
        this.energyLevel = energy;
    }
    
    public EntityAstroMiner(final World world) {
        super(world);
        this.TEMPDEBUG = false;
        this.TEMPFAST = false;
        this.mineCount = 0;
        this.timeInCurrentState = 0;
        this.playerMP = null;
        this.wayPoints = new LinkedList<BlockVec3>();
        this.minePoints = new LinkedList<BlockVec3>();
        this.minePointCurrent = null;
        this.speedbase = (this.TEMPFAST ? 0.16 : 0.022);
        this.speed = this.speedbase;
        this.rotSpeedBase = (this.TEMPFAST ? 8.0f : 1.5f);
        this.rotSpeed = this.rotSpeedBase;
        this.speedup = 2.5;
        this.noSpeedup = false;
        this.flagLink = false;
        this.flagCheckPlayer = false;
        this.givenFailMessage = 0;
        this.mineLast = null;
        this.mineCountDown = 0;
        this.pathBlockedCount = 0;
        this.laserBlocks = new LinkedList<BlockVec3>();
        this.laserTimes = new LinkedList<Integer>();
        this.retraction = 1.0f;
        this.soundToStop = false;
        this.spawnedInCreative = false;
        this.preventEntitySpawning = true;
        this.ignoreFrustumCheck = true;
        this.isImmuneToFire = true;
        this.renderDistanceWeight = 5.0;
        this.setSize(this.width = 2.6f, this.height = 1.8f);
        this.yOffset = 0.0f;
        this.myEntitySize = Entity.EnumEntitySize.SIZE_6;
        this.noClip = true;
        if (world != null && world.isRemote) {
            GalacticraftCore.packetPipeline.sendToServer((IPacket)new PacketDynamic((Entity)this));
        }
    }
    
    protected void entityInit() {
        this.dataWatcher.addObject(19, (Object)new Float(0.0f));
    }
    
    public int getSizeInventory() {
        return this.cargoItems.length;
    }
    
    public ItemStack getStackInSlot(final int var1) {
        return this.cargoItems[var1];
    }
    
    public ItemStack decrStackSize(final int var1, final int var2) {
        if (this.cargoItems[var1] == null) {
            return null;
        }
        if (this.cargoItems[var1].stackSize <= var2) {
            final ItemStack var3 = this.cargoItems[var1];
            this.cargoItems[var1] = null;
            return var3;
        }
        final ItemStack var3 = this.cargoItems[var1].splitStack(var2);
        if (this.cargoItems[var1].stackSize == 0) {
            this.cargoItems[var1] = null;
        }
        return var3;
    }
    
    public ItemStack getStackInSlotOnClosing(final int var1) {
        if (this.cargoItems[var1] != null) {
            final ItemStack var2 = this.cargoItems[var1];
            this.cargoItems[var1] = null;
            return var2;
        }
        return null;
    }
    
    public void setInventorySlotContents(final int var1, final ItemStack var2) {
        this.cargoItems[var1] = var2;
        if (var2 != null && var2.stackSize > this.getInventoryStackLimit()) {
            var2.stackSize = this.getInventoryStackLimit();
        }
    }
    
    public String getInventoryName() {
        return "AstroMiner";
    }
    
    public int getInventoryStackLimit() {
        return 64;
    }
    
    public boolean isUseableByPlayer(final EntityPlayer var1) {
        return !this.isDead && var1.getDistanceSqToEntity((Entity)this) <= 64.0;
    }
    
    public void markDirty() {
    }
    
    public void openInventory() {
    }
    
    public void closeInventory() {
    }
    
    public boolean isItemValidForSlot(final int i, final ItemStack itemstack) {
        return false;
    }
    
    public boolean hasCustomInventoryName() {
        return true;
    }
    
    private boolean emptyInventory(final TileEntityMinerBase minerBase) {
        final boolean doneOne = false;
        for (int i = 0; i < this.cargoItems.length; ++i) {
            final ItemStack stack = this.cargoItems[i];
            if (stack != null) {
                if (stack.stackSize == 0) {
                    this.cargoItems[i] = null;
                }
                else {
                    final int sizeprev = stack.stackSize;
                    minerBase.addToInventory(stack);
                    if (stack == null || stack.stackSize == 0) {
                        this.cargoItems[i] = null;
                        this.markDirty();
                        return true;
                    }
                    if (stack.stackSize < sizeprev) {
                        this.cargoItems[i] = stack;
                        this.markDirty();
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    public void onUpdate() {
        if (this.posY < -64.0) {
            this.kill();
            return;
        }
        if (this.getDamage() > 0.0f) {
            this.setDamage(this.getDamage() - 1.0f);
        }
        this.stopForTurn = !this.checkRotation();
        this.facing = this.getFacingFromRotation();
        this.setBoundingBoxForFacing();
        if (this.worldObj.isRemote) {
            if (this.turnProgress == 0) {
                ++this.turnProgress;
                if (this.AIstate < 2) {
                    this.posX = this.minecartX;
                    this.posY = this.minecartY;
                    this.posZ = this.minecartZ;
                }
                else {
                    final double diffX = this.minecartX - this.posX;
                    final double diffY = this.minecartY - this.posY;
                    final double diffZ = this.minecartZ - this.posZ;
                    if (Math.abs(diffX) > 1.0 || Math.abs(diffY) > 1.0 || Math.abs(diffZ) > 1.0) {
                        this.posX = this.minecartX;
                        this.posY = this.minecartY;
                        this.posZ = this.minecartZ;
                    }
                    else {
                        if (Math.abs(diffX) > Math.abs(this.motionX)) {
                            this.motionX += diffX / 10.0;
                        }
                        if (Math.abs(diffY) > Math.abs(this.motionY)) {
                            this.motionY += diffY / 10.0;
                        }
                        if (Math.abs(diffZ) > Math.abs(this.motionZ)) {
                            this.motionZ += diffZ / 10.0;
                        }
                    }
                }
            }
            this.posX += this.motionX;
            final AxisAlignedBB boundingBox = this.boundingBox;
            boundingBox.minX += this.motionX;
            final AxisAlignedBB boundingBox2 = this.boundingBox;
            boundingBox2.maxX += this.motionX;
            this.posY += this.motionY;
            final AxisAlignedBB boundingBox3 = this.boundingBox;
            boundingBox3.minY += this.motionY;
            final AxisAlignedBB boundingBox4 = this.boundingBox;
            boundingBox4.maxY += this.motionY;
            this.posZ += this.motionZ;
            final AxisAlignedBB boundingBox5 = this.boundingBox;
            boundingBox5.minZ += this.motionZ;
            final AxisAlignedBB boundingBox6 = this.boundingBox;
            boundingBox6.maxZ += this.motionZ;
            this.setRotation(this.rotationYaw, this.rotationPitch);
            if (this.AIstate == 3 && this.ticksExisted % 2 == 0) {
                this.prepareMoveClient(this.TEMPFAST ? 8 : 1, 2);
            }
            if (this.AIstate < 1) {
                this.stopRocketSound();
            }
            return;
        }
        if (this.ticksExisted % 10 == 0 || this.flagLink) {
            this.flagLink = false;
            this.checkPlayer();
            final TileEntity tileEntity = this.posBase.getTileEntity((IBlockAccess)this.worldObj);
            if (tileEntity instanceof TileEntityMinerBase && ((TileEntityMinerBase)tileEntity).isMaster && !tileEntity.isInvalid()) {
                final UUID linker = ((TileEntityMinerBase)tileEntity).getLinkedMiner();
                if (!this.getUniqueID().equals(linker)) {
                    if (linker != null) {
                        this.freeze(8);
                        return;
                    }
                    ((TileEntityMinerBase)tileEntity).linkMiner(this);
                }
                else if (((TileEntityMinerBase)tileEntity).linkedMiner != this) {
                    ((TileEntityMinerBase)tileEntity).linkMiner(this);
                }
            }
            else if (this.playerMP != null && (this.givenFailMessage & 0x8) == 0x0) {
                this.playerMP.addChatMessage((IChatComponent)new ChatComponentText(GCCoreUtil.translate("gui.message.astroMiner3.fail")));
                this.givenFailMessage += 8;
            }
        }
        else if (this.flagCheckPlayer) {
            this.checkPlayer();
        }
        if (this.playerMP == null) {
            this.motionX = 0.0;
            this.motionY = 0.0;
            this.motionZ = 0.0;
            GalacticraftCore.packetPipeline.sendToDimension((IPacket)new PacketDynamic((Entity)this), this.worldObj.provider.dimensionId);
            return;
        }
        if (this.lastFacing != this.facingAI) {
            this.lastFacing = this.facingAI;
            this.prepareMove(12, 0);
            this.prepareMove(12, 1);
            this.prepareMove(12, 2);
        }
        this.lastTickPosX = this.posX;
        this.lastTickPosY = this.posY;
        this.lastTickPosZ = this.posZ;
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        this.prevRotationPitch = this.rotationPitch;
        this.prevRotationYaw = this.rotationYaw;
        if (this.AIstate > 1) {
            if (this.energyLevel <= 0) {
                this.freeze(4);
            }
            else if (!(this.worldObj.provider instanceof WorldProviderAsteroids) && this.ticksExisted % 2 == 0) {
                --this.energyLevel;
            }
        }
        switch (this.AIstate) {
            case 0: {
                if (this.ticksExisted % 600 != 0) {
                    break;
                }
                if ((this.givenFailMessage & 0x8) > 0) {
                    this.atBase();
                    break;
                }
                this.AIstate = 4;
                if (this.energyLevel <= 0) {
                    this.energyLevel = 20;
                    break;
                }
                break;
            }
            case 1: {
                this.atBase();
                break;
            }
            case 2: {
                if (!this.moveToTarget()) {
                    this.prepareMove(this.TEMPFAST ? 8 : 2, 2);
                    break;
                }
                break;
            }
            case 3: {
                if (!this.doMining() && this.ticksExisted % 2 == 0) {
                    --this.energyLevel;
                    this.prepareMove(this.TEMPFAST ? 8 : 1, 2);
                    break;
                }
                break;
            }
            case 4: {
                this.moveToBase();
                this.prepareMove(this.TEMPFAST ? 8 : 4, 1);
                break;
            }
            case 5: {
                if (this.waypointBase == null) {
                    GCLog.severe("AstroMiner missing base position: this is a bug.");
                    this.AIstate = 0;
                    break;
                }
                this.speed = this.speedbase / 1.6;
                this.rotSpeed = this.rotSpeedBase / 1.6f;
                if (this.moveToPos(this.waypointBase, true)) {
                    this.AIstate = 1;
                    this.motionX = 0.0;
                    this.motionY = 0.0;
                    this.motionZ = 0.0;
                    this.speed = this.speedbase;
                    this.rotSpeed = this.rotSpeedBase;
                    break;
                }
                break;
            }
        }
        GalacticraftCore.packetPipeline.sendToDimension((IPacket)new PacketDynamic((Entity)this), this.worldObj.provider.dimensionId);
        this.posX += this.motionX;
        final AxisAlignedBB boundingBox7 = this.boundingBox;
        boundingBox7.minX += this.motionX;
        final AxisAlignedBB boundingBox8 = this.boundingBox;
        boundingBox8.maxX += this.motionX;
        this.posY += this.motionY;
        final AxisAlignedBB boundingBox9 = this.boundingBox;
        boundingBox9.minY += this.motionY;
        final AxisAlignedBB boundingBox10 = this.boundingBox;
        boundingBox10.maxY += this.motionY;
        this.posZ += this.motionZ;
        final AxisAlignedBB boundingBox11 = this.boundingBox;
        boundingBox11.minZ += this.motionZ;
        final AxisAlignedBB boundingBox12 = this.boundingBox;
        boundingBox12.maxZ += this.motionZ;
    }
    
    private void checkPlayer() {
        if (this.playerMP == null) {
            if (this.playerUUID != null) {
                this.playerMP = PlayerUtil.getPlayerByUUID(this.playerUUID);
            }
        }
        else if (!PlayerUtil.isPlayerOnline(this.playerMP)) {
            this.playerMP = null;
        }
    }
    
    private void freeze(final int i) {
        this.AIstate = 0;
        this.motionX = 0.0;
        this.motionY = 0.0;
        this.motionZ = 0.0;
        if (this.playerMP != null && (this.givenFailMessage & 1 << i) == 0x0) {
            this.playerMP.addChatMessage((IChatComponent)new ChatComponentText(GCCoreUtil.translate("gui.message.astroMiner" + i + ".fail")));
            this.givenFailMessage += 1 << i;
        }
    }
    
    public void decodePacketdata(final ByteBuf buffer) {
        this.AIstate = buffer.readInt();
        this.energyLevel = buffer.readInt();
        this.targetPitch = buffer.readFloat();
        this.targetYaw = buffer.readFloat();
        this.mineCount = buffer.readInt();
        final int x = buffer.readInt();
        final int y = buffer.readInt();
        final int z = buffer.readInt();
        if (this.worldObj.blockExists(x, y, z)) {
            final TileEntity tile = this.worldObj.getTileEntity(x, y, z);
            if (tile instanceof TileEntityMinerBase) {
                ((TileEntityMinerBase)tile).linkedMiner = this;
                ((TileEntityMinerBase)tile).linkCountDown = 20;
            }
        }
    }
    
    public void getNetworkedData(final ArrayList<Object> list) {
        if (this.worldObj.isRemote) {
            return;
        }
        list.add((this.playerMP == null) ? -1 : this.AIstate);
        list.add(this.energyLevel);
        list.add(this.targetPitch);
        list.add(this.targetYaw);
        list.add(this.mineCount);
        list.add(this.posBase.x);
        list.add(this.posBase.y);
        list.add(this.posBase.z);
    }
    
    public void handlePacketData(final Side side, final EntityPlayer player) {
    }
    
    public void recall() {
        if (this.AIstate > 1 && this.AIstate < 4) {
            this.AIstate = 4;
            this.pathBlockedCount = 0;
        }
    }
    
    private int getFacingFromRotation() {
        if (this.rotationPitch > 45.0f) {
            return 1;
        }
        if (this.rotationPitch < -45.0f) {
            return 0;
        }
        final float rY = this.rotationYaw % 360.0f;
        if (rY < 45.0f || rY > 315.0f) {
            return 3;
        }
        if (rY < 135.0f) {
            return 5;
        }
        if (rY < 225.0f) {
            return 2;
        }
        return 4;
    }
    
    private void atBase() {
        final TileEntity tileEntity = this.posBase.getTileEntity((IBlockAccess)this.worldObj);
        if (!(tileEntity instanceof TileEntityMinerBase) || tileEntity.isInvalid() || !((TileEntityMinerBase)tileEntity).isMaster) {
            this.freeze(3);
            return;
        }
        final TileEntityMinerBase minerBase = (TileEntityMinerBase)tileEntity;
        this.givenFailMessage &= 0x40;
        this.wayPoints.clear();
        boolean somethingTransferred = true;
        if (this.ticksExisted % 5 == 0) {
            somethingTransferred = this.emptyInventory(minerBase);
        }
        this.inventoryDrops = 0;
        if (minerBase.hasEnoughEnergyToRun && this.energyLevel < 12000) {
            this.energyLevel += 16;
            minerBase.storage.extractEnergyGC(minerBase.storage.getMaxExtract(), false);
        }
        if (this.energyLevel >= 12000 && !somethingTransferred && this.hasHoldSpace()) {
            this.energyLevel = 12000;
            if (this.findNextTarget(minerBase)) {
                this.AIstate = 2;
                this.wayPoints.add(this.waypointBase.clone());
                this.mineCount = 0;
            }
            else if (this.playerMP != null && (this.givenFailMessage & 0x40) == 0x0) {
                this.playerMP.addChatMessage((IChatComponent)new ChatComponentText(GCCoreUtil.translate("gui.message.astroMiner6.fail")));
                this.givenFailMessage += 64;
            }
        }
    }
    
    private boolean hasHoldSpace() {
        for (int i = 0; i < this.getSizeInventory(); ++i) {
            if (this.cargoItems[i] == null) {
                return true;
            }
            if (this.cargoItems[i].stackSize == 0) {
                this.cargoItems[i] = null;
                return true;
            }
        }
        return false;
    }
    
    private boolean findNextTarget(final TileEntityMinerBase minerBase) {
        if (!this.minePoints.isEmpty() && this.pathBlockedCount < 2) {
            this.posTarget = this.minePoints.getFirst().clone();
            GCLog.debug("Still mining at: " + this.posTarget.toString() + " Remaining shafts: " + this.minePoints.size());
            return true;
        }
        this.posTarget = minerBase.findNextTarget();
        this.pathBlockedCount = 0;
        if (this.posTarget == null) {
            return false;
        }
        GCLog.debug("Miner target: " + this.posTarget.toString());
        return true;
    }
    
    private boolean moveToTarget() {
        if (this.energyLevel < 1000 || this.inventoryDrops > 10) {
            this.AIstate = 4;
            this.pathBlockedCount = 0;
            return true;
        }
        if (this.posTarget == null) {
            GCLog.severe("AstroMiner missing target: this is a bug.");
            this.AIstate = 0;
            return true;
        }
        if (this.moveToPos(this.posTarget, false)) {
            this.AIstate = 3;
            this.wayPoints.add(this.posTarget.clone());
            this.setMinePoints();
            return true;
        }
        return false;
    }
    
    private void moveToBase() {
        if (this.wayPoints.size() == 0) {
            this.AIstate = 5;
            if (this.waypointBase != null) {
                this.setPosition(this.waypointBase.x, this.waypointBase.y, this.waypointBase.z);
                this.facingAI = this.baseFacing;
            }
            return;
        }
        if (this.moveToPos(this.wayPoints.getLast(), true)) {
            this.wayPoints.removeLast();
        }
    }
    
    private void setMinePoints() {
        if (this.minePoints.size() > 0) {
            return;
        }
        final BlockVec3 inFront = new BlockVec3(MathHelper.floor_double(this.posX + 0.5), MathHelper.floor_double(this.posY + 1.5), MathHelper.floor_double(this.posZ + 0.5));
        int otherEnd = (this.worldObj.provider instanceof WorldProviderAsteroids) ? 12 : 24;
        if (this.baseFacing == 2 || this.baseFacing == 4) {
            otherEnd = -otherEnd;
        }
        switch (this.baseFacing) {
            case 2:
            case 3: {
                this.minePoints.add(inFront.clone().translate(0, 0, otherEnd));
                this.minePoints.add(inFront.clone().translate(4, 0, otherEnd));
                this.minePoints.add(inFront.clone().translate(4, 0, 0));
                this.minePoints.add(inFront.clone().translate(2, 3, 0));
                this.minePoints.add(inFront.clone().translate(2, 3, otherEnd));
                this.minePoints.add(inFront.clone().translate(-2, 3, otherEnd));
                this.minePoints.add(inFront.clone().translate(-2, 3, 0));
                this.minePoints.add(inFront.clone().translate(-4, 0, 0));
                this.minePoints.add(inFront.clone().translate(-4, 0, otherEnd));
                this.minePoints.add(inFront.clone().translate(-2, -3, otherEnd));
                this.minePoints.add(inFront.clone().translate(-2, -3, 0));
                this.minePoints.add(inFront.clone().translate(2, -3, 0));
                this.minePoints.add(inFront.clone().translate(2, -3, otherEnd));
                this.minePoints.add(inFront.clone().translate(0, 0, otherEnd));
                break;
            }
            case 4:
            case 5: {
                this.minePoints.add(inFront.clone().translate(otherEnd, 0, 0));
                this.minePoints.add(inFront.clone().translate(otherEnd, 0, 4));
                this.minePoints.add(inFront.clone().translate(0, 0, 4));
                this.minePoints.add(inFront.clone().translate(0, 3, 2));
                this.minePoints.add(inFront.clone().translate(otherEnd, 3, 2));
                this.minePoints.add(inFront.clone().translate(otherEnd, 3, -2));
                this.minePoints.add(inFront.clone().translate(0, 3, -2));
                this.minePoints.add(inFront.clone().translate(0, 0, -4));
                this.minePoints.add(inFront.clone().translate(otherEnd, 0, -4));
                this.minePoints.add(inFront.clone().translate(otherEnd, -3, -2));
                this.minePoints.add(inFront.clone().translate(0, -3, -2));
                this.minePoints.add(inFront.clone().translate(0, -3, 2));
                this.minePoints.add(inFront.clone().translate(otherEnd, -3, 2));
                this.minePoints.add(inFront.clone().translate(otherEnd, 0, 0));
                break;
            }
        }
    }
    
    private boolean doMining() {
        if (this.energyLevel < 1000 || this.inventoryDrops > 10 || this.minePoints.size() == 0) {
            if (this.minePoints.size() > 0 && this.minePointCurrent != null) {
                this.minePoints.addFirst(this.minePointCurrent);
            }
            this.AIstate = 4;
            this.pathBlockedCount = 0;
            GCLog.debug("Miner going home: " + this.posBase.toString() + " " + this.minePoints.size() + " shafts still to be mined");
            return true;
        }
        if (this.moveToPos(this.minePoints.getFirst(), false)) {
            this.minePointCurrent = this.minePoints.removeFirst();
            GCLog.debug("Miner mid mining: " + this.minePointCurrent.toString() + " " + this.minePoints.size() + " shafts still to be mined");
            return true;
        }
        return false;
    }
    
    private void tryBackIn() {
        if (this.waypointBase.distanceSquared(new BlockVec3((Entity)this)) <= 9.1) {
            this.AIstate = 5;
            switch (this.baseFacing) {
                case 2: {
                    this.targetYaw = 180.0f;
                    break;
                }
                case 3: {
                    this.targetYaw = 0.0f;
                    break;
                }
                case 4: {
                    this.targetYaw = 270.0f;
                    break;
                }
                case 5: {
                    this.targetYaw = 90.0f;
                    break;
                }
            }
        }
        else {
            this.freeze(5);
        }
    }
    
    private boolean prepareMove(final int limit, int dist) {
        if (this.mineCountDown > 0) {
            --this.mineCountDown;
            return false;
        }
        final BlockVec3 inFront = new BlockVec3(MathHelper.floor_double(this.posX + 0.5), MathHelper.floor_double(this.posY + 1.5), MathHelper.floor_double(this.posZ + 0.5));
        if (dist == 2) {
            inFront.add(EntityAstroMiner.headings2[this.facingAI]);
        }
        else {
            if ((this.facingAI & 0x1) == 0x0) {
                ++dist;
            }
            if (dist > 0) {
                inFront.add(EntityAstroMiner.headings[this.facingAI].clone().scale(dist));
            }
        }
        if (!inFront.equals((Object)this.mineLast) && this.AIstate != 1) {
            this.mineCountDown = 3;
            this.mineLast = inFront;
            return false;
        }
        final int x = inFront.x;
        final int y = inFront.y;
        final int z = inFront.z;
        if (y == this.waypointBase.y && x == this.waypointBase.x - ((this.baseFacing == 5) ? 1 : 0) && z == this.waypointBase.z - ((this.baseFacing == 3) ? 1 : 0)) {
            this.tryBackIn();
            return false;
        }
        boolean wayBarred = false;
        this.tryBlockLimit = limit;
        switch (this.facingAI & 0x6) {
            case 0: {
                if (this.tryMineBlock(x, y, z)) {
                    wayBarred = true;
                }
                if (this.tryMineBlock(x + 1, y, z)) {
                    wayBarred = true;
                }
                if (this.tryMineBlock(x + 1, y, z - 1)) {
                    wayBarred = true;
                }
                if (this.tryMineBlock(x, y, z - 1)) {
                    wayBarred = true;
                }
                if (this.tryMineBlock(x, y, z - 2)) {
                    wayBarred = true;
                }
                if (this.tryMineBlock(x - 1, y, z - 2)) {
                    wayBarred = true;
                }
                if (this.tryMineBlock(x - 1, y, z - 1)) {
                    wayBarred = true;
                }
                if (this.tryMineBlock(x - 2, y, z - 1)) {
                    wayBarred = true;
                }
                if (this.tryMineBlock(x - 2, y, z)) {
                    wayBarred = true;
                }
                if (this.tryMineBlock(x - 1, y, z)) {
                    wayBarred = true;
                }
                if (this.tryMineBlock(x - 1, y, z + 1)) {
                    wayBarred = true;
                }
                if (this.tryMineBlock(x, y, z + 1)) {
                    wayBarred = true;
                    break;
                }
                break;
            }
            case 2: {
                if (this.tryMineBlock(x, y - 2, z)) {
                    wayBarred = true;
                }
                if (this.tryMineBlock(x - 1, y - 2, z)) {
                    wayBarred = true;
                }
                if (this.tryMineBlock(x, y - 1, z)) {
                    wayBarred = true;
                }
                if (this.tryMineBlock(x - 1, y - 1, z)) {
                    wayBarred = true;
                }
                if (this.tryMineBlock(x + 1, y - 1, z)) {
                    wayBarred = true;
                }
                if (this.tryMineBlock(x - 2, y - 1, z)) {
                    wayBarred = true;
                }
                if (this.tryMineBlock(x + 1, y, z)) {
                    wayBarred = true;
                }
                if (this.tryMineBlock(x - 2, y, z)) {
                    wayBarred = true;
                }
                if (this.tryMineBlock(x, y, z)) {
                    wayBarred = true;
                }
                if (this.tryMineBlock(x - 1, y, z)) {
                    wayBarred = true;
                }
                if (this.tryMineBlock(x, y + 1, z)) {
                    wayBarred = true;
                }
                if (this.tryMineBlock(x - 1, y + 1, z)) {
                    wayBarred = true;
                    break;
                }
                break;
            }
            case 4: {
                if (this.tryMineBlock(x, y - 2, z - 1)) {
                    wayBarred = true;
                }
                if (this.tryMineBlock(x, y - 1, z)) {
                    wayBarred = true;
                }
                if (this.tryMineBlock(x, y - 1, z - 1)) {
                    wayBarred = true;
                }
                if (this.tryMineBlock(x, y - 1, z + 1)) {
                    wayBarred = true;
                }
                if (this.tryMineBlock(x, y - 1, z - 2)) {
                    wayBarred = true;
                }
                if (this.tryMineBlock(x, y, z + 1)) {
                    wayBarred = true;
                }
                if (this.tryMineBlock(x, y, z - 2)) {
                    wayBarred = true;
                }
                if (this.tryMineBlock(x, y, z - 1)) {
                    wayBarred = true;
                }
                if (this.tryMineBlock(x, y - 2, z)) {
                    wayBarred = true;
                }
                if (this.tryMineBlock(x, y + 1, z - 1)) {
                    wayBarred = true;
                }
                if (this.tryMineBlock(x, y, z)) {
                    wayBarred = true;
                }
                if (this.tryMineBlock(x, y + 1, z)) {
                    wayBarred = true;
                    break;
                }
                break;
            }
        }
        if (wayBarred) {
            if (this.playerMP != null) {
                this.playerMP.addChatMessage((IChatComponent)new ChatComponentText(GCCoreUtil.translate("gui.message.astroMiner1A.fail") + " " + GCCoreUtil.translate(EntityAstroMiner.blockingBlock.toString())));
            }
            this.motionX = 0.0;
            this.motionY = 0.0;
            this.motionZ = 0.0;
            this.tryBlockLimit = 0;
            if (this.AIstate == 2) {
                this.AIstate = 4;
            }
            else if (this.AIstate == 3) {
                ++this.pathBlockedCount;
                this.AIstate = 4;
            }
            else if (this.AIstate == 4) {
                this.tryBackIn();
            }
            else {
                this.freeze(5);
            }
        }
        if (this.tryBlockLimit == limit && !this.noSpeedup) {
            this.motionX *= this.speedup;
            this.motionY *= this.speedup;
            this.motionZ *= this.speedup;
        }
        return wayBarred;
    }
    
    private boolean prepareMoveClient(final int limit, int dist) {
        final BlockVec3 inFront = new BlockVec3(MathHelper.floor_double(this.posX + 0.5), MathHelper.floor_double(this.posY + 1.5), MathHelper.floor_double(this.posZ + 0.5));
        if (dist == 2) {
            inFront.add(EntityAstroMiner.headings2[this.facing]);
        }
        else {
            if ((this.facing & 0x1) == 0x0) {
                ++dist;
            }
            if (dist > 0) {
                inFront.add(EntityAstroMiner.headings[this.facing].clone().scale(dist));
            }
        }
        if (inFront.equals((Object)this.mineLast)) {
            return false;
        }
        final int x = inFront.x;
        final int y = inFront.y;
        final int z = inFront.z;
        boolean wayBarred = false;
        this.tryBlockLimit = limit;
        switch (this.facing & 0x6) {
            case 0: {
                if (this.tryBlockClient(x, y, z)) {
                    wayBarred = true;
                }
                if (this.tryBlockClient(x + 1, y, z)) {
                    wayBarred = true;
                }
                if (this.tryBlockClient(x + 1, y, z - 1)) {
                    wayBarred = true;
                }
                if (this.tryBlockClient(x, y, z - 1)) {
                    wayBarred = true;
                }
                if (this.tryBlockClient(x, y, z - 2)) {
                    wayBarred = true;
                }
                if (this.tryBlockClient(x - 1, y, z - 2)) {
                    wayBarred = true;
                }
                if (this.tryBlockClient(x - 1, y, z - 1)) {
                    wayBarred = true;
                }
                if (this.tryBlockClient(x - 2, y, z - 1)) {
                    wayBarred = true;
                }
                if (this.tryBlockClient(x - 2, y, z)) {
                    wayBarred = true;
                }
                if (this.tryBlockClient(x - 1, y, z)) {
                    wayBarred = true;
                }
                if (this.tryBlockClient(x - 1, y, z + 1)) {
                    wayBarred = true;
                }
                if (this.tryBlockClient(x, y, z + 1)) {
                    wayBarred = true;
                    break;
                }
                break;
            }
            case 2: {
                if (this.tryBlockClient(x, y - 2, z)) {
                    wayBarred = true;
                }
                if (this.tryBlockClient(x - 1, y - 2, z)) {
                    wayBarred = true;
                }
                if (this.tryBlockClient(x, y - 1, z)) {
                    wayBarred = true;
                }
                if (this.tryBlockClient(x - 1, y - 1, z)) {
                    wayBarred = true;
                }
                if (this.tryBlockClient(x + 1, y - 1, z)) {
                    wayBarred = true;
                }
                if (this.tryBlockClient(x - 2, y - 1, z)) {
                    wayBarred = true;
                }
                if (this.tryBlockClient(x + 1, y, z)) {
                    wayBarred = true;
                }
                if (this.tryBlockClient(x - 2, y, z)) {
                    wayBarred = true;
                }
                if (this.tryBlockClient(x, y, z)) {
                    wayBarred = true;
                }
                if (this.tryBlockClient(x - 1, y, z)) {
                    wayBarred = true;
                }
                if (this.tryBlockClient(x, y + 1, z)) {
                    wayBarred = true;
                }
                if (this.tryBlockClient(x - 1, y + 1, z)) {
                    wayBarred = true;
                    break;
                }
                break;
            }
            case 4: {
                if (this.tryBlockClient(x, y - 2, z - 1)) {
                    wayBarred = true;
                }
                if (this.tryBlockClient(x, y - 1, z)) {
                    wayBarred = true;
                }
                if (this.tryBlockClient(x, y - 1, z - 1)) {
                    wayBarred = true;
                }
                if (this.tryBlockClient(x, y - 1, z + 1)) {
                    wayBarred = true;
                }
                if (this.tryBlockClient(x, y - 1, z - 2)) {
                    wayBarred = true;
                }
                if (this.tryBlockClient(x, y, z + 1)) {
                    wayBarred = true;
                }
                if (this.tryBlockClient(x, y, z - 2)) {
                    wayBarred = true;
                }
                if (this.tryBlockClient(x, y, z - 1)) {
                    wayBarred = true;
                }
                if (this.tryBlockClient(x, y - 2, z)) {
                    wayBarred = true;
                }
                if (this.tryBlockClient(x, y + 1, z - 1)) {
                    wayBarred = true;
                }
                if (this.tryBlockClient(x, y, z)) {
                    wayBarred = true;
                }
                if (this.tryBlockClient(x, y + 1, z)) {
                    wayBarred = true;
                    break;
                }
                break;
            }
        }
        if (wayBarred) {
            this.tryBlockLimit = 0;
        }
        if (this.tryBlockLimit == limit) {
            this.mineLast = inFront;
        }
        return wayBarred;
    }
    
    private boolean tryMineBlock(final int x, final int y, final int z) {
        final Block b = this.worldObj.getBlock(x, y, z);
        if (b.getMaterial() == Material.air) {
            return false;
        }
        if (EntityAstroMiner.noMineList.contains(b)) {
            EntityAstroMiner.blockingBlock.block = b;
            EntityAstroMiner.blockingBlock.meta = this.worldObj.getBlockMetadata(x, y, z);
            return true;
        }
        if (b instanceof BlockLiquid) {
            return false;
        }
        if (b instanceof IFluidBlock) {
            return false;
        }
        boolean gtFlag = false;
        if (b != GCBlocks.fallenMeteor) {
            if (b instanceof IPlantable && b != Blocks.tallgrass && b != Blocks.deadbush && b != Blocks.double_plant && b != Blocks.waterlily && !(b instanceof BlockFlower)) {
                EntityAstroMiner.blockingBlock.block = b;
                EntityAstroMiner.blockingBlock.meta = this.worldObj.getBlockMetadata(x, y, z);
                return true;
            }
            final int meta = this.worldObj.getBlockMetadata(x, y, z);
            if (b.getBlockHardness(this.worldObj, x, y, z) < 0.0f) {
                EntityAstroMiner.blockingBlock.block = b;
                EntityAstroMiner.blockingBlock.meta = meta;
                return true;
            }
            if (b.hasTileEntity(meta)) {
                if (!CompatibilityManager.isGTLoaded() || !this.gregTechCheck(b)) {
                    EntityAstroMiner.blockingBlock.block = b;
                    EntityAstroMiner.blockingBlock.meta = meta;
                    return true;
                }
                gtFlag = true;
            }
        }
        if (this.tryBlockLimit == 0) {
            return false;
        }
        final BlockEvent.BreakEvent event = ForgeHooks.onBlockBreakEvent(this.worldObj, this.playerMP.theItemInWorldManager.getGameType(), this.playerMP, x, y, z);
        if (event.isCanceled()) {
            return true;
        }
        --this.tryBlockLimit;
        final ItemStack drops = gtFlag ? this.getGTDrops(this.worldObj, x, y, z, b) : this.getPickBlock(this.worldObj, x, y, z, b);
        if (drops != null && !this.addToInventory(drops)) {
            this.dropStack(x, y, z, drops);
        }
        this.worldObj.setBlock(x, y, z, Blocks.air, 0, 3);
        return false;
    }
    
    private void dropStack(final int x, final int y, final int z, final ItemStack drops) {
        final float f = 0.7f;
        final double d0 = this.worldObj.rand.nextFloat() * f + (1.0f - f) * 0.5;
        final double d2 = this.worldObj.rand.nextFloat() * f + (1.0f - f) * 0.5;
        final double d3 = this.worldObj.rand.nextFloat() * f + (1.0f - f) * 0.5;
        final EntityItem entityitem = new EntityItem(this.worldObj, x + d0, y + d2, z + d3, drops);
        entityitem.delayBeforeCanPickup = 10;
        this.worldObj.spawnEntityInWorld((Entity)entityitem);
        ++this.inventoryDrops;
    }
    
    private boolean gregTechCheck(final Block b) {
        final Class clazz = CompatibilityManager.classGTOre;
        return clazz != null && clazz.isInstance(b);
    }
    
    private ItemStack getGTDrops(final World w, final int x, final int y, final int z, final Block b) {
        final ArrayList<ItemStack> array = (ArrayList<ItemStack>)b.getDrops(w, x, y, z, 0, 1);
        if (array != null && array.size() > 0) {
            return array.get(0);
        }
        return null;
    }
    
    private boolean tryBlockClient(final int x, final int y, final int z) {
        final BlockVec3 bv = new BlockVec3(x, y, z);
        if (this.laserBlocks.contains(bv)) {
            return false;
        }
        final Block b = this.worldObj.getBlock(x, y, z);
        if (b.getMaterial() == Material.air) {
            return false;
        }
        if (EntityAstroMiner.noMineList.contains(b)) {
            return true;
        }
        if (b instanceof BlockLiquid) {
            return false;
        }
        if (b instanceof IFluidBlock) {
            return false;
        }
        if (b instanceof IPlantable) {
            return true;
        }
        final int meta = this.worldObj.getBlockMetadata(x, y, z);
        if (b.hasTileEntity(meta) || b.getBlockHardness(this.worldObj, x, y, z) < 0.0f) {
            return true;
        }
        if (this.tryBlockLimit == 0) {
            return false;
        }
        --this.tryBlockLimit;
        this.laserBlocks.add(bv);
        this.laserTimes.add(this.ticksExisted);
        return false;
    }
    
    public void removeLaserBlocks(final int removeCount) {
        for (int i = 0; i < removeCount; ++i) {
            this.laserBlocks.removeFirst();
            this.laserTimes.removeFirst();
        }
    }
    
    private ItemStack getPickBlock(final World world, final int x, final int y, final int z, final Block b) {
        if (b == GCBlocks.fallenMeteor) {
            return new ItemStack(GCItems.meteoricIronRaw);
        }
        return VersionUtil.createStack(b, world.getBlockMetadata(x, y, z));
    }
    
    private boolean addToInventory(final ItemStack itemstack) {
        boolean flag1 = false;
        int k = 0;
        final int invSize = this.getSizeInventory();
        if (itemstack.isStackable()) {
            while (itemstack.stackSize > 0 && k < invSize) {
                final ItemStack itemstack2 = this.cargoItems[k];
                if (itemstack2 != null && itemstack2.getItem() == itemstack.getItem() && (!itemstack.getHasSubtypes() || itemstack.getItemDamage() == itemstack2.getItemDamage()) && ItemStack.areItemStackTagsEqual(itemstack, itemstack2)) {
                    final int l = itemstack2.stackSize + itemstack.stackSize;
                    if (l <= itemstack.getMaxStackSize()) {
                        itemstack.stackSize = 0;
                        itemstack2.stackSize = l;
                        flag1 = true;
                    }
                    else if (itemstack2.stackSize < itemstack.getMaxStackSize()) {
                        itemstack.stackSize -= itemstack.getMaxStackSize() - itemstack2.stackSize;
                        itemstack2.stackSize = itemstack.getMaxStackSize();
                        flag1 = true;
                    }
                }
                ++k;
            }
        }
        if (itemstack.stackSize > 0) {
            for (k = 0; k < invSize; ++k) {
                final ItemStack itemstack2 = this.cargoItems[k];
                if (itemstack2 == null) {
                    this.cargoItems[k] = itemstack.copy();
                    itemstack.stackSize = 0;
                    flag1 = true;
                    break;
                }
            }
        }
        if (flag1) {
            this.markDirty();
            ++this.mineCount;
        }
        return flag1;
    }
    
    private boolean moveToPos(final BlockVec3 pos, final boolean reverse) {
        this.noSpeedup = false;
        if (reverse != this.baseFacing < 4) {
            if (this.posZ > pos.z + 1.0E-4 || this.posZ < pos.z - 1.0E-4) {
                this.moveToPosZ(pos.z, this.stopForTurn);
                if (this.TEMPDEBUG) {
                    GCLog.debug("At " + this.posX + "," + this.posY + "," + this.posZ + "Moving Z to " + pos.toString() + (this.stopForTurn ? (" : Stop for turn " + this.rotationPitch + "," + this.rotationYaw + " | " + this.targetPitch + "," + this.targetYaw) : ""));
                }
            }
            else if (this.posY > pos.y - 0.9999 || this.posY < pos.y - 1.0001) {
                this.moveToPosY(pos.y - 1, this.stopForTurn);
                if (this.TEMPDEBUG) {
                    GCLog.debug("At " + this.posX + "," + this.posY + "," + this.posZ + "Moving Y to " + pos.toString() + (this.stopForTurn ? (" : Stop for turn " + this.rotationPitch + "," + this.rotationYaw + " | " + this.targetPitch + "," + this.targetYaw) : ""));
                }
            }
            else {
                if (this.posX <= pos.x + 1.0E-4 && this.posX >= pos.x - 1.0E-4) {
                    return true;
                }
                this.moveToPosX(pos.x, this.stopForTurn);
                if (this.TEMPDEBUG) {
                    GCLog.debug("At " + this.posX + "," + this.posY + "," + this.posZ + "Moving X to " + pos.toString() + (this.stopForTurn ? (" : Stop for turn " + this.rotationPitch + "," + this.rotationYaw + " | " + this.targetPitch + "," + this.targetYaw) : ""));
                }
            }
        }
        else if (this.posX > pos.x + 1.0E-4 || this.posX < pos.x - 1.0E-4) {
            this.moveToPosX(pos.x, this.stopForTurn);
            if (this.TEMPDEBUG) {
                GCLog.debug("At " + this.posX + "," + this.posY + "," + this.posZ + "Moving X to " + pos.toString() + (this.stopForTurn ? (" : Stop for turn " + this.rotationPitch + "," + this.rotationYaw + " | " + this.targetPitch + "," + this.targetYaw) : ""));
            }
        }
        else if (this.posY > pos.y - 0.9999 || this.posY < pos.y - 1.0001) {
            this.moveToPosY(pos.y - 1, this.stopForTurn);
            if (this.TEMPDEBUG) {
                GCLog.debug("At " + this.posX + "," + this.posY + "," + this.posZ + "Moving Y to " + pos.toString() + (this.stopForTurn ? (" : Stop for turn " + this.rotationPitch + "," + this.rotationYaw + " | " + this.targetPitch + "," + this.targetYaw) : ""));
            }
        }
        else {
            if (this.posZ <= pos.z + 1.0E-4 && this.posZ >= pos.z - 1.0E-4) {
                return true;
            }
            this.moveToPosZ(pos.z, this.stopForTurn);
            if (this.TEMPDEBUG) {
                GCLog.debug("At " + this.posX + "," + this.posY + "," + this.posZ + "Moving Z to " + pos.toString() + (this.stopForTurn ? (" : Stop for turn " + this.rotationPitch + "," + this.rotationYaw + " | " + this.targetPitch + "," + this.targetYaw) : ""));
            }
        }
        return false;
    }
    
    private void moveToPosX(final int x, final boolean stopForTurn) {
        this.targetPitch = 0.0f;
        if (this.posX > x) {
            if (this.AIstate != 5) {
                this.targetYaw = 270.0f;
            }
            this.motionX = -this.speed;
            if (this.motionX * this.speedup <= x - this.posX) {
                this.motionX = x - this.posX;
                this.noSpeedup = true;
            }
            this.facingAI = 4;
        }
        else {
            if (this.AIstate != 5) {
                this.targetYaw = 90.0f;
            }
            this.motionX = this.speed;
            if (this.motionX * this.speedup >= x - this.posX) {
                this.motionX = x - this.posX;
                this.noSpeedup = true;
            }
            this.facingAI = 5;
        }
        if (stopForTurn) {
            this.motionX = 0.0;
        }
        this.motionY = 0.0;
        this.motionZ = 0.0;
    }
    
    private void moveToPosY(final int y, final boolean stopForTurn) {
        if (this.posY > y) {
            this.targetPitch = -90.0f;
            this.motionY = -this.speed;
            if (this.motionY * this.speedup <= y - this.posY) {
                this.motionY = y - this.posY;
                this.noSpeedup = true;
            }
            this.facingAI = 0;
        }
        else {
            this.targetPitch = 90.0f;
            this.motionY = this.speed;
            if (this.motionY * this.speedup >= y - this.posY) {
                this.motionY = y - this.posY;
                this.noSpeedup = true;
            }
            this.facingAI = 1;
        }
        if (stopForTurn) {
            this.motionY = 0.0;
        }
        this.motionX = 0.0;
        this.motionZ = 0.0;
    }
    
    private void moveToPosZ(final int z, final boolean stopForTurn) {
        this.targetPitch = 0.0f;
        if (this.posZ > z) {
            if (this.AIstate != 5) {
                this.targetYaw = 180.0f;
            }
            this.motionZ = -this.speed;
            if (this.motionZ * this.speedup <= z - this.posZ) {
                this.motionZ = z - this.posZ;
                this.noSpeedup = true;
            }
            this.facingAI = 2;
        }
        else {
            if (this.AIstate != 5) {
                this.targetYaw = 0.0f;
            }
            this.motionZ = this.speed;
            if (this.motionZ * this.speedup >= z - this.posZ) {
                this.motionZ = z - this.posZ;
                this.noSpeedup = true;
            }
            this.facingAI = 3;
        }
        if (stopForTurn) {
            this.motionZ = 0.0;
        }
        this.motionY = 0.0;
        this.motionX = 0.0;
    }
    
    private boolean checkRotation() {
        boolean flag = true;
        if (this.rotationPitch > this.targetPitch + 0.001f || this.rotationPitch < this.targetPitch - 0.001f) {
            if (this.rotationPitch > this.targetPitch + 180.0f) {
                this.rotationPitch -= 360.0f;
            }
            else if (this.rotationPitch < this.targetPitch - 180.0f) {
                this.rotationPitch += 360.0f;
            }
            if (this.rotationPitch > this.targetPitch) {
                this.rotationPitch -= this.rotSpeed;
                if (this.rotationPitch < this.targetPitch) {
                    this.rotationPitch = this.targetPitch;
                }
            }
            else {
                this.rotationPitch += this.rotSpeed;
                if (this.rotationPitch > this.targetPitch) {
                    this.rotationPitch = this.targetPitch;
                }
            }
        }
        if (this.rotationYaw > this.targetYaw + 0.001f || this.rotationYaw < this.targetYaw - 0.001f) {
            if (this.rotationYaw > this.targetYaw + 180.0f) {
                this.rotationYaw -= 360.0f;
            }
            else if (this.rotationYaw < this.targetYaw - 180.0f) {
                this.rotationYaw += 360.0f;
            }
            if (this.rotationYaw > this.targetYaw) {
                this.rotationYaw -= this.rotSpeed;
                if (this.rotationYaw < this.targetYaw) {
                    this.rotationYaw = this.targetYaw;
                }
            }
            else {
                this.rotationYaw += this.rotSpeed;
                if (this.rotationYaw > this.targetYaw) {
                    this.rotationYaw = this.targetYaw;
                }
            }
            flag = false;
        }
        return flag;
    }
    
    public static boolean spawnMinerAtBase(final World world, final int x, final int y, final int z, final int facing, final BlockVec3 base, final EntityPlayerMP player) {
        if (world.isRemote) {
            return true;
        }
        final EntityAstroMiner miner = new EntityAstroMiner(world, new ItemStack[227], 0);
        miner.setPlayer(player);
        if (player.capabilities.isCreativeMode) {
            miner.spawnedInCreative = true;
        }
        miner.waypointBase = new BlockVec3(x, y, z).modifyPositionFromSide(ForgeDirection.getOrientation(facing), 1);
        miner.setPosition(miner.waypointBase.x, miner.waypointBase.y - 1, miner.waypointBase.z);
        miner.baseFacing = facing;
        miner.facingAI = facing;
        miner.lastFacing = facing;
        miner.motionX = 0.0;
        miner.motionY = 0.0;
        miner.motionZ = 0.0;
        miner.targetPitch = 0.0f;
        switch (facing) {
            case 2: {
                miner.targetYaw = 180.0f;
                break;
            }
            case 3: {
                miner.targetYaw = 0.0f;
                break;
            }
            case 4: {
                miner.targetYaw = 270.0f;
                break;
            }
            case 5: {
                miner.targetYaw = 90.0f;
                break;
            }
        }
        miner.rotationPitch = miner.targetPitch;
        miner.rotationYaw = miner.targetYaw;
        miner.setBoundingBoxForFacing();
        miner.AIstate = 1;
        miner.posBase = base;
        miner.speedup = ((world.provider instanceof WorldProviderAsteroids) ? 4.0 : 2.5);
        if (miner.prepareMove(12, 0)) {
            miner.isDead = true;
            return false;
        }
        if (miner.prepareMove(12, 1)) {
            miner.isDead = true;
            return false;
        }
        if (miner.prepareMove(12, 2)) {
            miner.isDead = true;
            return false;
        }
        world.spawnEntityInWorld((Entity)miner);
        return miner.flagLink = true;
    }
    
    public void setPlayer(final EntityPlayerMP player) {
        this.playerMP = player;
        this.playerUUID = player.getUniqueID();
    }
    
    private void setBoundingBoxForFacing() {
        float xsize = 1.8f;
        float ysize = 1.8f;
        float zsize = 1.8f;
        switch (this.facing) {
            case 0:
            case 1: {
                ysize = 2.6f;
                break;
            }
            case 2:
            case 3: {
                ysize = 1.7f;
                zsize = 2.6f;
                break;
            }
            case 4:
            case 5: {
                ysize = 1.7f;
                xsize = 2.6f;
                break;
            }
        }
        this.width = Math.max(xsize, zsize);
        this.height = ysize;
        this.boundingBox.minX = this.posX - xsize / 2.0;
        this.boundingBox.minY = this.posY + 1.0 - ysize / 2.0;
        this.boundingBox.minZ = this.posZ - zsize / 2.0;
        this.boundingBox.maxX = this.posX + xsize / 2.0;
        this.boundingBox.maxY = this.posY + 1.0 + ysize / 2.0;
        this.boundingBox.maxZ = this.posZ + zsize / 2.0;
    }
    
    public boolean attackEntityFrom(final DamageSource par1DamageSource, final float par2) {
        if (this.isDead || par1DamageSource.equals(DamageSource.cactus)) {
            return true;
        }
        if (this.worldObj.isRemote) {
            return true;
        }
        final Entity e = par1DamageSource.getEntity();
        if (e instanceof EntityPlayer && ((EntityPlayer)e).capabilities.isCreativeMode) {
            if (this.playerMP == null && !this.spawnedInCreative) {
                ((EntityPlayer)e).addChatMessage((IChatComponent)new ChatComponentText("WARNING: that Astro Miner belonged to an offline player, cannot reset player's Astro Miner count."));
            }
            this.kill();
            return true;
        }
        if (this.isEntityInvulnerable() || (e instanceof EntityLivingBase && !(e instanceof EntityPlayer))) {
            return false;
        }
        this.setBeenAttacked();
        this.shipDamage += par2 * 10.0f;
        if (e instanceof EntityPlayer) {
            this.shipDamage += par2 * 21.0f;
        }
        if (this.shipDamage > 90.0f) {
            this.kill();
            this.dropShipAsItem();
            return true;
        }
        return true;
    }
    
    public AxisAlignedBB getCollisionBox(final Entity par1Entity) {
        return par1Entity.boundingBox;
    }
    
    public AxisAlignedBB getBoundingBox() {
        return this.boundingBox;
    }
    
    public boolean canBePushed() {
        return false;
    }
    
    public boolean canBeCollidedWith() {
        return !this.isDead;
    }
    
    public void performHurtAnimation() {
    }
    
    public float getDamage() {
        return this.dataWatcher.getWatchableObjectFloat(19);
    }
    
    public void setDamage(final float p_70492_1_) {
        this.dataWatcher.updateObject(19, (Object)p_70492_1_);
    }
    
    public void setLocationAndAngles(final double x, final double y, final double z, final float rotYaw, final float rotPitch) {
        super.setLocationAndAngles(this.minecartX = x, this.minecartY = y, this.minecartZ = z, rotYaw, rotPitch);
    }
    
    @SideOnly(Side.CLIENT)
    public void setPositionAndRotation2(final double p_70056_1_, final double p_70056_3_, final double p_70056_5_, final float p_70056_7_, final float p_70056_8_, final int p_70056_9_) {
        this.minecartX = p_70056_1_;
        this.minecartY = p_70056_3_;
        this.minecartZ = p_70056_5_;
        this.minecartYaw = p_70056_7_;
        this.minecartPitch = p_70056_8_;
        this.turnProgress = 0;
        this.motionX = this.velocityX;
        this.motionY = this.velocityY;
        this.motionZ = this.velocityZ;
    }
    
    @SideOnly(Side.CLIENT)
    public void setVelocity(final double p_70016_1_, final double p_70016_3_, final double p_70016_5_) {
        this.motionX = p_70016_1_;
        this.velocityX = p_70016_1_;
        this.motionY = p_70016_3_;
        this.velocityY = p_70016_3_;
        this.motionZ = p_70016_5_;
        this.velocityZ = p_70016_5_;
        this.turnProgress = 0;
    }
    
    protected void setSize(final float p_70105_1_, final float p_70105_2_) {
        this.setBoundingBoxForFacing();
    }
    
    public void setPosition(final double p_70107_1_, final double p_70107_3_, final double p_70107_5_) {
        this.boundingBox.offset(p_70107_1_ - this.posX, p_70107_3_ - this.posY, p_70107_5_ - this.posZ);
        this.posX = p_70107_1_;
        this.posY = p_70107_3_;
        this.posZ = p_70107_5_;
    }
    
    public void setDead() {
        if (!this.worldObj.isRemote && this.playerMP != null && !this.spawnedInCreative) {
            final int astroCount = GCPlayerStats.get(this.playerMP).astroMinerCount;
            if (astroCount > 0) {
                final GCPlayerStats value = GCPlayerStats.get(this.playerMP);
                --value.astroMinerCount;
            }
        }
        super.setDead();
        if (this.posBase != null) {
            final TileEntity tileEntity = this.posBase.getTileEntity((IBlockAccess)this.worldObj);
            if (tileEntity instanceof TileEntityMinerBase) {
                ((TileEntityMinerBase)tileEntity).unlinkMiner();
            }
        }
        if (this.soundUpdater != null) {
            this.soundUpdater.update();
        }
    }
    
    public boolean isEntityInvulnerable() {
        return this.playerMP == null;
    }
    
    public List<ItemStack> getItemsDropped(final List<ItemStack> droppedItems) {
        final ItemStack rocket = new ItemStack(AsteroidsItems.astroMiner, 1, 0);
        droppedItems.add(rocket);
        for (int i = 0; i < this.cargoItems.length; ++i) {
            if (this.cargoItems[i] != null) {
                droppedItems.add(this.cargoItems[i]);
            }
            this.cargoItems[i] = null;
        }
        return droppedItems;
    }
    
    public void dropShipAsItem() {
        if (this.worldObj.isRemote) {
            return;
        }
        for (final ItemStack item : this.getItemsDropped(new ArrayList<ItemStack>())) {
            final EntityItem entityItem = this.entityDropItem(item, 0.0f);
            if (item.hasTagCompound()) {
                entityItem.getEntityItem().setTagCompound((NBTTagCompound)item.getTagCompound().copy());
            }
        }
    }
    
    @SideOnly(Side.CLIENT)
    public IUpdatePlayerListBox getSoundUpdater() {
        return this.soundUpdater;
    }
    
    @SideOnly(Side.CLIENT)
    public ISound setSoundUpdater(final EntityPlayerSP player) {
        this.soundUpdater = (IUpdatePlayerListBox)new SoundUpdaterMiner(player, this);
        return (ISound)this.soundUpdater;
    }
    
    public void stopRocketSound() {
        if (this.soundUpdater != null) {
            ((SoundUpdaterMiner)this.soundUpdater).stopRocketSound();
        }
        this.soundToStop = false;
    }
    
    public void transmitData(final int[] data) {
        data[0] = (int)this.posX;
        data[1] = (int)this.posY;
        data[2] = (int)this.posZ;
        data[3] = this.energyLevel;
        data[4] = this.AIstate;
    }
    
    public void receiveData(final int[] data, final String[] str) {
        str[0] = "";
        str[1] = "x: " + data[0];
        str[2] = "y: " + data[1];
        str[3] = "z: " + data[2];
        final int energyPerCent = data[3] / 120;
        str[4] = GCCoreUtil.translate("gui.energyStorage.desc.1") + ": " + energyPerCent + "%";
        switch (data[4]) {
            case 0: {
                str[0] = GCCoreUtil.translate("gui.message.noEnergy.name");
                break;
            }
            case 1: {
                str[0] = GCCoreUtil.translate("gui.miner.docked");
                break;
            }
            case 2: {
                str[0] = GCCoreUtil.translate("gui.miner.travelling");
                break;
            }
            case 3: {
                str[0] = GCCoreUtil.translate("gui.miner.mining");
                break;
            }
            case 4: {
                str[0] = GCCoreUtil.translate("gui.miner.returning");
                break;
            }
            case 5: {
                str[0] = GCCoreUtil.translate("gui.miner.docking");
                break;
            }
            case -1: {
                str[0] = GCCoreUtil.translate("gui.miner.offline");
                break;
            }
        }
    }
    
    public void adjustDisplay(final int[] data) {
        GL11.glScalef(0.9f, 0.9f, 0.9f);
    }
    
    protected void readEntityFromNBT(final NBTTagCompound nbt) {
        final NBTTagList var2 = nbt.getTagList("Items", 10);
        this.cargoItems = new ItemStack[227];
        if (var2 != null) {
            for (int var3 = 0; var3 < var2.tagCount(); ++var3) {
                final NBTTagCompound var4 = var2.getCompoundTagAt(var3);
                final int var5 = var4.getByte("Slot") & 0xFF;
                if (var5 < this.cargoItems.length) {
                    this.cargoItems[var5] = ItemStack.loadItemStackFromNBT(var4);
                }
            }
        }
        if (nbt.hasKey("Energy")) {
            this.energyLevel = nbt.getInteger("Energy");
        }
        if (nbt.hasKey("BaseX")) {
            this.posBase = new BlockVec3(nbt.getInteger("BaseX"), nbt.getInteger("BaseY"), nbt.getInteger("BaseZ"));
            this.flagLink = true;
        }
        if (nbt.hasKey("TargetX")) {
            this.posTarget = new BlockVec3(nbt.getInteger("TargetX"), nbt.getInteger("TargetY"), nbt.getInteger("TargetZ"));
        }
        if (nbt.hasKey("WBaseX")) {
            this.waypointBase = new BlockVec3(nbt.getInteger("WBaseX"), nbt.getInteger("WBaseY"), nbt.getInteger("WBaseZ"));
        }
        if (nbt.hasKey("BaseFacing")) {
            this.baseFacing = nbt.getInteger("BaseFacing");
        }
        if (nbt.hasKey("AIState")) {
            this.AIstate = nbt.getInteger("AIState");
        }
        if (nbt.hasKey("Facing")) {
            this.facingAI = nbt.getInteger("Facing");
        }
        this.lastFacing = -1;
        if (nbt.hasKey("WayPoints")) {
            this.wayPoints.clear();
            final NBTTagList wpList = nbt.getTagList("WayPoints", 10);
            for (int j = 0; j < wpList.tagCount(); ++j) {
                final NBTTagCompound bvTag = wpList.getCompoundTagAt(j);
                this.wayPoints.add(BlockVec3.readFromNBT(bvTag));
            }
        }
        if (nbt.hasKey("MinePoints")) {
            this.minePoints.clear();
            final NBTTagList mpList = nbt.getTagList("MinePoints", 10);
            for (int j = 0; j < mpList.tagCount(); ++j) {
                final NBTTagCompound bvTag = mpList.getCompoundTagAt(j);
                this.minePoints.add(BlockVec3.readFromNBT(bvTag));
            }
        }
        if (nbt.hasKey("MinePointCurrent")) {
            this.minePointCurrent = BlockVec3.readFromNBT(nbt.getCompoundTag("MinePointCurrent"));
        }
        else {
            this.minePointCurrent = null;
        }
        if (nbt.hasKey("playerUUIDMost", 4) && nbt.hasKey("playerUUIDLeast", 4)) {
            this.playerUUID = new UUID(nbt.getLong("playerUUIDMost"), nbt.getLong("playerUUIDLeast"));
        }
        else {
            System.out.println("[Galacticraft] Please break and replace any AstroMiner placed in the world prior to build 3.0.11.317.");
            this.playerUUID = null;
        }
        if (nbt.hasKey("speedup")) {
            this.speedup = nbt.getDouble("speedup");
        }
        else {
            this.speedup = ((WorldUtil.getProviderForDimensionServer(this.dimension) instanceof WorldProviderAsteroids) ? 4.0 : 2.5);
        }
        this.pathBlockedCount = nbt.getInteger("pathBlockedCount");
        this.spawnedInCreative = nbt.getBoolean("spawnedInCreative");
        this.flagCheckPlayer = true;
    }
    
    protected void writeEntityToNBT(final NBTTagCompound nbt) {
        final NBTTagList var2 = new NBTTagList();
        if (this.cargoItems != null) {
            for (int var3 = 0; var3 < this.cargoItems.length; ++var3) {
                if (this.cargoItems[var3] != null) {
                    final NBTTagCompound var4 = new NBTTagCompound();
                    var4.setByte("Slot", (byte)var3);
                    this.cargoItems[var3].writeToNBT(var4);
                    var2.appendTag((NBTBase)var4);
                }
            }
        }
        nbt.setTag("Items", (NBTBase)var2);
        nbt.setInteger("Energy", this.energyLevel);
        if (this.posBase != null) {
            nbt.setInteger("BaseX", this.posBase.x);
            nbt.setInteger("BaseY", this.posBase.y);
            nbt.setInteger("BaseZ", this.posBase.z);
        }
        if (this.posTarget != null) {
            nbt.setInteger("TargetX", this.posTarget.x);
            nbt.setInteger("TargetY", this.posTarget.y);
            nbt.setInteger("TargetZ", this.posTarget.z);
        }
        if (this.waypointBase != null) {
            nbt.setInteger("WBaseX", this.waypointBase.x);
            nbt.setInteger("WBaseY", this.waypointBase.y);
            nbt.setInteger("WBaseZ", this.waypointBase.z);
        }
        nbt.setInteger("BaseFacing", this.baseFacing);
        nbt.setInteger("AIState", this.AIstate);
        nbt.setInteger("Facing", this.facingAI);
        if (this.wayPoints.size() > 0) {
            final NBTTagList wpList = new NBTTagList();
            for (int j = 0; j < this.wayPoints.size(); ++j) {
                wpList.appendTag((NBTBase)this.wayPoints.get(j).writeToNBT(new NBTTagCompound()));
            }
            nbt.setTag("WayPoints", (NBTBase)wpList);
        }
        if (this.minePoints.size() > 0) {
            final NBTTagList mpList = new NBTTagList();
            for (int j = 0; j < this.minePoints.size(); ++j) {
                mpList.appendTag((NBTBase)this.minePoints.get(j).writeToNBT(new NBTTagCompound()));
            }
            nbt.setTag("MinePoints", (NBTBase)mpList);
        }
        if (this.minePointCurrent != null) {
            nbt.setTag("MinePointCurrent", (NBTBase)this.minePointCurrent.writeToNBT(new NBTTagCompound()));
        }
        if (this.playerUUID != null) {
            nbt.setLong("playerUUIDMost", this.playerUUID.getMostSignificantBits());
            nbt.setLong("playerUUIDLeast", this.playerUUID.getLeastSignificantBits());
        }
        nbt.setDouble("speedup", this.speedup);
        nbt.setInteger("pathBlockedCount", this.pathBlockedCount);
        nbt.setBoolean("spawnedInCreative", this.spawnedInCreative);
    }
    
    static {
        EntityAstroMiner.headings = new BlockVec3[] { new BlockVec3(0, -1, 0), new BlockVec3(0, 1, 0), new BlockVec3(0, 0, -1), new BlockVec3(0, 0, 1), new BlockVec3(-1, 0, 0), new BlockVec3(1, 0, 0) };
        EntityAstroMiner.headings2 = new BlockVec3[] { new BlockVec3(0, -3, 0), new BlockVec3(0, 2, 0), new BlockVec3(0, 0, -3), new BlockVec3(0, 0, 2), new BlockVec3(-3, 0, 0), new BlockVec3(2, 0, 0) };
        EntityAstroMiner.noMineList = new ArrayList<Block>();
        EntityAstroMiner.blockingBlock = new BlockTuple(Blocks.air, 0);
        EntityAstroMiner.noMineList.add(Blocks.bedrock);
        EntityAstroMiner.noMineList.add(Blocks.lava);
        EntityAstroMiner.noMineList.add(Blocks.mossy_cobblestone);
        EntityAstroMiner.noMineList.add(Blocks.end_portal);
        EntityAstroMiner.noMineList.add(Blocks.end_portal_frame);
        EntityAstroMiner.noMineList.add((Block)Blocks.portal);
        EntityAstroMiner.noMineList.add(Blocks.stonebrick);
        EntityAstroMiner.noMineList.add(Blocks.farmland);
        EntityAstroMiner.noMineList.add(Blocks.rail);
        EntityAstroMiner.noMineList.add(Blocks.lever);
        EntityAstroMiner.noMineList.add((Block)Blocks.redstone_wire);
        EntityAstroMiner.noMineList.add(AsteroidBlocks.blockWalkway);
    }
}
