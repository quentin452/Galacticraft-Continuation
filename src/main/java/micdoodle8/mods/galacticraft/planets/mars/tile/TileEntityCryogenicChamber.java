package micdoodle8.mods.galacticraft.planets.mars.tile;

import micdoodle8.mods.galacticraft.core.tile.*;
import cpw.mods.fml.relauncher.*;
import net.minecraft.entity.player.*;
import micdoodle8.mods.galacticraft.core.*;
import micdoodle8.mods.galacticraft.planets.mars.network.*;
import micdoodle8.mods.galacticraft.core.network.*;
import micdoodle8.mods.galacticraft.core.entities.player.*;
import micdoodle8.mods.galacticraft.core.util.*;
import net.minecraft.world.biome.*;
import net.minecraft.entity.*;
import net.minecraft.util.*;
import micdoodle8.mods.galacticraft.api.vector.*;
import micdoodle8.mods.galacticraft.core.blocks.*;
import net.minecraft.tileentity.*;
import cpw.mods.fml.client.*;
import micdoodle8.mods.galacticraft.planets.mars.blocks.*;
import net.minecraft.block.*;
import net.minecraft.nbt.*;

public class TileEntityCryogenicChamber extends TileEntityMulti implements IMultiBlock
{
    public boolean isOccupied;
    
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox() {
        return AxisAlignedBB.getBoundingBox((double)(this.xCoord - 1), (double)this.yCoord, (double)(this.zCoord - 1), (double)(this.xCoord + 2), (double)(this.yCoord + 3), (double)(this.zCoord + 2));
    }
    
    public boolean onActivated(final EntityPlayer entityPlayer) {
        if (this.worldObj.isRemote) {
            return false;
        }
        final EntityPlayer.EnumStatus enumstatus = this.sleepInBedAt(entityPlayer, this.xCoord, this.yCoord, this.zCoord);
        switch (enumstatus) {
            case OK: {
                ((EntityPlayerMP)entityPlayer).playerNetServerHandler.setPlayerLocation(entityPlayer.posX, entityPlayer.posY, entityPlayer.posZ, entityPlayer.rotationYaw, entityPlayer.rotationPitch);
                GalacticraftCore.packetPipeline.sendTo((IPacket)new PacketSimpleMars(PacketSimpleMars.EnumSimplePacketMars.C_BEGIN_CRYOGENIC_SLEEP, new Object[] { this.xCoord, this.yCoord, this.zCoord }), (EntityPlayerMP)entityPlayer);
                return true;
            }
            case NOT_POSSIBLE_NOW: {
                entityPlayer.addChatMessage((IChatComponent)new ChatComponentText(GCCoreUtil.translateWithFormat("gui.cryogenic.chat.cantUse", GCPlayerStats.get((EntityPlayerMP)entityPlayer).cryogenicChamberCooldown / 20)));
                return false;
            }
            default: {
                return false;
            }
        }
    }
    
    public EntityPlayer.EnumStatus sleepInBedAt(final EntityPlayer entityPlayer, final int par1, final int par2, final int par3) {
        if (!this.worldObj.isRemote) {
            if (entityPlayer.isPlayerSleeping() || !entityPlayer.isEntityAlive()) {
                return EntityPlayer.EnumStatus.OTHER_PROBLEM;
            }
            if (this.worldObj.getBiomeGenForCoords(par1, par3) == BiomeGenBase.hell) {
                return EntityPlayer.EnumStatus.NOT_POSSIBLE_HERE;
            }
            if (GCPlayerStats.get((EntityPlayerMP)entityPlayer).cryogenicChamberCooldown > 0) {
                return EntityPlayer.EnumStatus.NOT_POSSIBLE_NOW;
            }
        }
        if (entityPlayer.isRiding()) {
            entityPlayer.mountEntity((Entity)null);
        }
        entityPlayer.setPosition((double)(this.xCoord + 0.5f), (double)(this.yCoord + 1.9f), (double)(this.zCoord + 0.5f));
        entityPlayer.sleeping = true;
        entityPlayer.sleepTimer = 0;
        entityPlayer.playerLocation = new ChunkCoordinates(this.xCoord, this.yCoord, this.zCoord);
        final double motionX = 0.0;
        entityPlayer.motionY = motionX;
        entityPlayer.motionZ = motionX;
        entityPlayer.motionX = motionX;
        if (!this.worldObj.isRemote) {
            this.worldObj.updateAllPlayersSleepingFlag();
        }
        return EntityPlayer.EnumStatus.OK;
    }
    
    public boolean canUpdate() {
        return true;
    }
    
    public void updateEntity() {
        super.updateEntity();
    }
    
    public void onCreate(final BlockVec3 placedPosition) {
        this.mainBlockPosition = placedPosition;
        this.markDirty();
        final int buildHeight = this.worldObj.getHeight() - 1;
        for (int y = 0; y < 3; ++y) {
            if (placedPosition.y + y > buildHeight) {
                return;
            }
            final BlockVec3 vecToAdd = new BlockVec3(placedPosition.x, placedPosition.y + y, placedPosition.z);
            if (!vecToAdd.equals((Object)placedPosition)) {
                ((BlockMulti)GCBlocks.fakeBlock).makeFakeBlock(this.worldObj, vecToAdd, placedPosition, 5);
            }
        }
    }
    
    public void onDestroy(final TileEntity callingBlock) {
        final BlockVec3 thisBlock = new BlockVec3((TileEntity)this);
        int fakeBlockCount = 0;
        for (int y = 0; y < 3; ++y) {
            if (y != 0) {
                if (this.worldObj.getBlock(thisBlock.x, thisBlock.y + y, thisBlock.z) == GCBlocks.fakeBlock) {
                    ++fakeBlockCount;
                }
            }
        }
        if (fakeBlockCount == 0) {
            return;
        }
        for (int y = 0; y < 3; ++y) {
            if (this.worldObj.isRemote && this.worldObj.rand.nextDouble() < 0.1) {
                FMLClientHandler.instance().getClient().effectRenderer.addBlockDestroyEffects(thisBlock.x, thisBlock.y + y, thisBlock.z, MarsBlocks.machine, Block.getIdFromBlock(MarsBlocks.machine) >> 12 & 0xFF);
            }
            this.worldObj.func_147480_a(thisBlock.x, thisBlock.y + y, thisBlock.z, true);
        }
    }
    
    public void readFromNBT(final NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        this.isOccupied = nbt.getBoolean("IsChamberOccupied");
    }
    
    public void writeToNBT(final NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setBoolean("IsChamberOccupied", this.isOccupied);
    }
}
