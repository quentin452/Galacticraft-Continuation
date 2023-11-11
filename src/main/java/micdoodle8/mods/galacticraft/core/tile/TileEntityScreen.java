package micdoodle8.mods.galacticraft.core.tile;

import net.minecraft.tileentity.*;
import micdoodle8.mods.galacticraft.core.client.gui.screen.*;
import cpw.mods.fml.common.*;
import micdoodle8.mods.galacticraft.core.*;
import micdoodle8.mods.galacticraft.core.network.*;
import micdoodle8.mods.galacticraft.api.vector.*;
import net.minecraftforge.common.util.*;
import net.minecraft.world.*;
import micdoodle8.mods.galacticraft.api.*;
import net.minecraft.nbt.*;
import java.util.*;
import micdoodle8.mods.galacticraft.core.tick.*;
import cpw.mods.fml.relauncher.*;

public class TileEntityScreen extends TileEntity
{
    public static float FRAMEBORDER;
    public int imageType;
    public DrawGameScreen screen;
    public boolean connectedUp;
    public boolean connectedDown;
    public boolean connectedLeft;
    public boolean connectedRight;
    public int connectionsUp;
    public int connectionsDown;
    public int connectionsLeft;
    public int connectionsRight;
    public boolean isMultiscreen;
    public int screenOffsetx;
    public int screenOffsetz;
    private int requiresUpdate;
    public boolean refreshOnUpdate;
    
    public TileEntityScreen() {
        this.screenOffsetx = 0;
        this.screenOffsetz = 0;
        this.requiresUpdate = 0;
        this.refreshOnUpdate = false;
    }
    
    public void validate() {
        super.validate();
        if (FMLCommonHandler.instance().getEffectiveSide().isClient()) {
            this.screen = new DrawGameScreen(1.0f, 1.0f, (TileEntity)this);
            GalacticraftCore.packetPipeline.sendToServer((IPacket)new PacketSimple(PacketSimple.EnumSimplePacket.S_UPDATE_VIEWSCREEN_REQUEST, new Object[] { this.worldObj.provider.dimensionId, this.xCoord, this.yCoord, this.zCoord }));
        }
    }
    
    public void updateClients() {
        int connectedFlags = 0;
        if (this.connectedUp) {
            connectedFlags += 8;
        }
        if (this.connectedDown) {
            connectedFlags += 4;
        }
        if (this.connectedLeft) {
            connectedFlags += 2;
        }
        if (this.connectedRight) {
            ++connectedFlags;
        }
        GalacticraftCore.packetPipeline.sendToDimension((IPacket)new PacketSimple(PacketSimple.EnumSimplePacket.C_UPDATE_VIEWSCREEN, new Object[] { this.xCoord, this.yCoord, this.zCoord, this.imageType, connectedFlags }), this.worldObj.provider.dimensionId);
    }
    
    public void invalidate() {
        final int meta = this.getBlockMetadata() & 0x7;
        super.invalidate();
        this.breakScreen(meta);
    }
    
    public void breakScreen(final int meta) {
        final BlockVec3 vec = new BlockVec3((TileEntity)this);
        final int side = this.getRight(meta);
        final int left = this.connectionsLeft;
        final int right = this.connectionsRight;
        final int up = this.connectionsUp;
        final int down = this.connectionsDown;
        final boolean doUp = this.connectedUp;
        final boolean doDown = this.connectedDown;
        final boolean doLeft = this.connectedLeft;
        final boolean doRight = this.connectedRight;
        for (int x = -left; x <= right; ++x) {
            for (int z = -up; z <= down; ++z) {
                if (x == 0 && z == 0) {
                    this.resetToSingle();
                }
                else {
                    final BlockVec3 newVec = vec.clone().modifyPositionFromSide(ForgeDirection.getOrientation(side), x).modifyPositionFromSide(ForgeDirection.DOWN, z);
                    final TileEntity tile = newVec.getTileEntity((IBlockAccess)this.worldObj);
                    if (tile instanceof TileEntityScreen && tile.getBlockMetadata() == meta) {
                        ((TileEntityScreen)tile).resetToSingle();
                    }
                }
            }
        }
        final boolean b = false;
        this.connectedRight = b;
        this.connectedLeft = b;
        this.connectedDown = b;
        this.connectedUp = b;
        if (doUp) {
            final TileEntity tile = vec.getTileEntityOnSide(this.worldObj, 1);
            if (tile instanceof TileEntityScreen && tile.getBlockMetadata() == meta && !tile.isInvalid()) {
                if (doLeft) {
                    ((TileEntityScreen)tile).connectedLeft = true;
                }
                if (doRight) {
                    ((TileEntityScreen)tile).connectedRight = true;
                }
                ((TileEntityScreen)tile).connectedUp = true;
                ((TileEntityScreen)tile).refreshConnections(true);
            }
        }
        if (doDown) {
            final TileEntity tile = vec.getTileEntityOnSide(this.worldObj, 0);
            if (tile instanceof TileEntityScreen && tile.getBlockMetadata() == meta && !tile.isInvalid()) {
                if (doLeft) {
                    ((TileEntityScreen)tile).connectedLeft = true;
                }
                if (doRight) {
                    ((TileEntityScreen)tile).connectedRight = true;
                }
                ((TileEntityScreen)tile).connectedDown = true;
                ((TileEntityScreen)tile).refreshConnections(true);
            }
        }
        if (doLeft) {
            final TileEntity tile = vec.getTileEntityOnSide(this.worldObj, this.getLeft(meta));
            if (tile instanceof TileEntityScreen && tile.getBlockMetadata() == meta && !tile.isInvalid()) {
                if (doUp) {
                    ((TileEntityScreen)tile).connectedUp = true;
                }
                if (doDown) {
                    ((TileEntityScreen)tile).connectedDown = true;
                }
                ((TileEntityScreen)tile).connectedLeft = true;
                ((TileEntityScreen)tile).refreshConnections(true);
            }
        }
        if (doRight) {
            final TileEntity tile = vec.getTileEntityOnSide(this.worldObj, this.getRight(meta));
            if (tile instanceof TileEntityScreen && tile.getBlockMetadata() == meta && !tile.isInvalid()) {
                if (doUp) {
                    ((TileEntityScreen)tile).connectedUp = true;
                }
                if (doDown) {
                    ((TileEntityScreen)tile).connectedDown = true;
                }
                ((TileEntityScreen)tile).connectedRight = true;
                ((TileEntityScreen)tile).refreshConnections(true);
            }
        }
    }
    
    public void refreshConnections(final boolean doScreen) {
        this.log("Starting connection check");
        final int meta = this.getBlockMetadata() & 0x7;
        if (meta < 2) {
            this.resetToSingle();
            return;
        }
        TileEntity tileUp = null;
        TileEntity tileDown = null;
        TileEntity tileLeft = null;
        TileEntity tileRight = null;
        final BlockVec3 vec = new BlockVec3((TileEntity)this);
        if (this.connectedUp) {
            tileUp = vec.getTileEntityOnSide(this.worldObj, 1);
            this.connectedUp = (tileUp instanceof TileEntityScreen && tileUp.getBlockMetadata() == meta && !tileUp.isInvalid());
        }
        if (this.connectedDown) {
            tileDown = vec.getTileEntityOnSide(this.worldObj, 0);
            this.connectedDown = (tileDown instanceof TileEntityScreen && tileDown.getBlockMetadata() == meta && !tileDown.isInvalid());
        }
        if (this.connectedLeft) {
            final int side = this.getLeft(meta);
            tileLeft = vec.getTileEntityOnSide(this.worldObj, side);
            this.connectedLeft = (tileLeft instanceof TileEntityScreen && tileLeft.getBlockMetadata() == meta && !tileLeft.isInvalid());
        }
        if (this.connectedRight) {
            final int side = this.getRight(meta);
            tileRight = vec.getTileEntityOnSide(this.worldObj, side);
            this.connectedRight = (tileRight instanceof TileEntityScreen && tileRight.getBlockMetadata() == meta && !tileRight.isInvalid());
        }
        if (this.connectedUp) {
            this.connectedUp = this.tryConnectUp((TileEntityScreen)tileUp);
        }
        if (this.connectedDown) {
            this.connectedDown = this.tryConnectDown((TileEntityScreen)tileDown);
        }
        if (this.connectedLeft) {
            this.connectedLeft = this.tryConnectLeft((TileEntityScreen)tileLeft);
        }
        if (this.connectedRight) {
            this.connectedRight = this.tryConnectRight((TileEntityScreen)tileRight);
        }
        this.log("Ending connection check");
        if (doScreen) {
            this.checkScreenSize();
            this.markDirty();
        }
    }
    
    public boolean canUpdate() {
        return false;
    }
    
    public void changeChannel() {
        if (!this.worldObj.isRemote) {
            if (++this.imageType >= GalacticraftRegistry.getMaxScreenTypes()) {
                this.imageType = 0;
            }
            boolean flag = false;
            if (!this.connectedRight && this.canJoinRight()) {
                this.joinRight();
                flag = true;
            }
            else if (!this.connectedLeft && this.canJoinLeft()) {
                this.joinLeft();
                flag = true;
            }
            else if (!this.connectedUp && this.canJoinUp()) {
                this.joinUp();
                flag = true;
            }
            else if (!this.connectedDown && this.canJoinDown()) {
                this.joinDown();
                flag = true;
            }
            this.refreshConnections(true);
            this.markDirty();
            this.updateClients();
        }
    }
    
    public void readFromNBT(final NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        this.imageType = nbt.getInteger("type");
        this.connectionsDown = nbt.getInteger("connectionsDown");
        this.connectionsUp = nbt.getInteger("connectionsUp");
        this.connectionsLeft = nbt.getInteger("connectionsLeft");
        this.connectionsRight = nbt.getInteger("connectionsRight");
        this.isMultiscreen = nbt.getBoolean("multiscreen");
        this.connectedUp = (this.connectionsUp > 0);
        this.connectedDown = (this.connectionsDown > 0);
        this.connectedLeft = (this.connectionsLeft > 0);
        this.connectedRight = (this.connectionsRight > 0);
    }
    
    public void writeToNBT(final NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setInteger("type", this.imageType);
        nbt.setInteger("connectionsDown", this.connectionsDown);
        nbt.setInteger("connectionsUp", this.connectionsUp);
        nbt.setInteger("connectionsLeft", this.connectionsLeft);
        nbt.setInteger("connectionsRight", this.connectionsRight);
        nbt.setBoolean("multiscreen", this.isMultiscreen);
    }
    
    public void checkScreenSize() {
        this.log("Checking screen size");
        int up = 0;
        int down = 0;
        int left = 0;
        int right = 0;
        final int meta = this.getBlockMetadata() & 0x7;
        BlockVec3 vec = new BlockVec3((TileEntity)this);
        TileEntityScreen tile = this;
        while (up < 8 && tile.connectedUp) {
            ++up;
            final TileEntity newTile = vec.getTileEntityOnSide(this.worldObj, 1);
            if (!(newTile instanceof TileEntityScreen)) {
                System.out.println("Debug - connected up to a non-screen tile");
                tile.connectedUp = false;
                tile.markDirty();
                --up;
                break;
            }
            tile = (TileEntityScreen)newTile;
            vec.translate(0, 1, 0);
        }
        vec = new BlockVec3((TileEntity)this);
        tile = this;
        while (down < 8 - up && tile.connectedDown) {
            ++down;
            final TileEntity newTile = vec.getTileEntityOnSide(this.worldObj, 0);
            if (!(newTile instanceof TileEntityScreen)) {
                System.out.println("Debug - connected down to a non-screen tile");
                tile.connectedDown = false;
                tile.markDirty();
                --down;
                break;
            }
            tile = (TileEntityScreen)newTile;
            vec.translate(0, -1, 0);
        }
        int leftside;
        TileEntity newTile2;
        for (vec = new BlockVec3((TileEntity)this), tile = this, leftside = this.getLeft(meta); left < ((up + down == 0) ? 1 : 8) && tile.connectedLeft; tile = (TileEntityScreen)newTile2, vec = vec.newVecSide(leftside)) {
            ++left;
            newTile2 = vec.getTileEntityOnSide(this.worldObj, leftside);
            if (!(newTile2 instanceof TileEntityScreen)) {
                System.out.println("Debug - connected left to a non-screen tile");
                tile.connectedLeft = false;
                tile.markDirty();
                --left;
                break;
            }
        }
        int rightside;
        TileEntity newTile3;
        for (vec = new BlockVec3((TileEntity)this), tile = this, rightside = this.getRight(meta); right < ((up + down == 0) ? 1 : 8) - left && tile.connectedRight; tile = (TileEntityScreen)newTile3, vec = vec.newVecSide(rightside)) {
            ++right;
            newTile3 = vec.getTileEntityOnSide(this.worldObj, rightside);
            if (!(newTile3 instanceof TileEntityScreen)) {
                System.out.println("Debug - connected right to a non-screen tile");
                tile.connectedRight = false;
                tile.markDirty();
                --right;
                break;
            }
        }
        this.log("Screen size check midpoint " + up + " " + down + " " + left + " " + right + " ");
        vec = new BlockVec3((TileEntity)this);
        TileEntity newtile = vec.getTileEntityOnSide(this.worldObj, 1);
        final TileEntityScreen tileUp = (newtile instanceof TileEntityScreen) ? ((TileEntityScreen)newtile) : null;
        newtile = vec.getTileEntityOnSide(this.worldObj, 0);
        final TileEntityScreen tileDown = (newtile instanceof TileEntityScreen) ? ((TileEntityScreen)newtile) : null;
        newtile = vec.getTileEntityOnSide(this.worldObj, leftside);
        final TileEntityScreen tileLeft = (newtile instanceof TileEntityScreen) ? ((TileEntityScreen)newtile) : null;
        newtile = vec.getTileEntityOnSide(this.worldObj, rightside);
        final TileEntityScreen tileRight = (newtile instanceof TileEntityScreen) ? ((TileEntityScreen)newtile) : null;
        if (left + right == 0 && up + down >= 1) {
            if (up > 0 && !tileUp.connectedUp) {
                up = 1;
                down = 0;
            }
            else {
                up = 0;
                if (tileDown != null && !tileDown.connectedDown) {
                    down = 1;
                }
                else {
                    down = 0;
                }
            }
        }
        if (up + down == 0 && left + right >= 1) {
            if (left > 0 && !tileLeft.connectedLeft) {
                if (right == 0 || tileRight == null || tileRight.connectionsLeft == 0) {
                    left = 1;
                    right = 0;
                }
                else {
                    left = 0;
                    right = 1;
                }
            }
            else {
                left = 0;
                if (tileRight != null && !tileRight.connectedRight) {
                    right = 1;
                }
                else {
                    right = 0;
                }
            }
        }
        if (up == 0) {
            this.connectedUp = false;
            if (tileUp != null) {
                tileUp.connectedDown = false;
            }
        }
        if (down == 0) {
            this.connectedDown = false;
            if (tileDown != null) {
                tileDown.connectedUp = false;
            }
        }
        if (left == 0) {
            this.connectedLeft = false;
            if (tileLeft != null) {
                tileLeft.connectedRight = false;
            }
        }
        if (right == 0) {
            this.connectedRight = false;
            if (tileRight != null) {
                tileRight.connectedLeft = false;
            }
        }
        this.log("Finished screen size check");
        this.checkWholeScreen(up, down, left, right);
    }
    
    private boolean checkWholeScreen(final int up, final int down, final int left, final int right) {
        if (up + down + left + right == 0 || up < 0 || down < 0 || left < 0 || right < 0) {
            this.resetToSingle();
            return true;
        }
        boolean screenWhole = true;
        boolean existingScreen = false;
        int barrierUp = up;
        int barrierDown = down;
        int barrierLeft = left;
        int barrierRight = right;
        final int meta = this.getBlockMetadata() & 0x7;
        final BlockVec3 vec = new BlockVec3((TileEntity)this);
        final ArrayList<TileEntityScreen> screenList = new ArrayList<TileEntityScreen>();
        final int side = this.getRight(meta);
        for (int x = -left; x <= right; ++x) {
            for (int z = -up; z <= down; ++z) {
                final BlockVec3 newVec = vec.clone().modifyPositionFromSide(ForgeDirection.getOrientation(side), x).modifyPositionFromSide(ForgeDirection.DOWN, z);
                final TileEntity tile = newVec.getTileEntity((IBlockAccess)this.worldObj);
                if (tile instanceof TileEntityScreen && tile.getBlockMetadata() == meta && !tile.isInvalid()) {
                    final TileEntityScreen screenTile = (TileEntityScreen)tile;
                    screenList.add(screenTile);
                    if (screenTile.isMultiscreen) {
                        if (screenTile.connectionsUp > z + up) {
                            barrierUp = -z - 1;
                            existingScreen = true;
                        }
                        if (screenTile.connectionsDown > down - z) {
                            barrierDown = z - 1;
                            existingScreen = true;
                        }
                        if (screenTile.connectionsLeft > x + left) {
                            barrierLeft = -x - 1;
                            existingScreen = true;
                        }
                        if (screenTile.connectionsRight > right - x) {
                            barrierRight = x - 1;
                            existingScreen = true;
                        }
                    }
                }
                else {
                    screenWhole = false;
                }
            }
        }
        if (!screenWhole) {
            for (final TileEntityScreen scr : screenList) {
                scr.resetToSingle();
            }
            return false;
        }
        if (existingScreen) {
            return this.checkWholeScreen(barrierUp, barrierDown, barrierLeft, barrierRight);
        }
        DrawGameScreen newScreen = null;
        boolean serverside = true;
        final TileEntity bottomLeft = vec.clone().modifyPositionFromSide(ForgeDirection.getOrientation(side), -left).modifyPositionFromSide(ForgeDirection.DOWN, down).getTileEntity((IBlockAccess)this.worldObj);
        if (this.worldObj.isRemote) {
            if (bottomLeft instanceof TileEntityScreen) {
                newScreen = ((TileEntityScreen)bottomLeft).screen;
                if (!newScreen.check(1.0f + left + right, 1.0f + up + down)) {
                    newScreen = new DrawGameScreen(1.0f + left + right, 1.0f + up + down, bottomLeft);
                }
            }
            serverside = false;
        }
        final Iterator<TileEntityScreen> it = screenList.iterator();
        for (int x2 = -left; x2 <= right; ++x2) {
            for (int z2 = -up; z2 <= down; ++z2) {
                final TileEntityScreen screenTile2 = it.next();
                screenTile2.screenOffsetx = x2 + left;
                screenTile2.screenOffsetz = z2 + up;
                screenTile2.screen = newScreen;
                screenTile2.connectionsLeft = x2 + left;
                screenTile2.connectionsRight = right - x2;
                screenTile2.connectionsUp = z2 + up;
                screenTile2.connectionsDown = down - z2;
                screenTile2.isMultiscreen = true;
                screenTile2.refreshOnUpdate = false;
                if (serverside) {
                    screenTile2.imageType = this.imageType;
                    screenTile2.markDirty();
                    screenTile2.updateClients();
                }
                screenTile2.refreshConnections(false);
            }
        }
        this.connectionsUp = up;
        this.connectionsDown = down;
        this.connectionsLeft = left;
        this.connectionsRight = right;
        return true;
    }
    
    public void resetToSingle() {
        if (this.worldObj.isRemote) {
            this.screen = new DrawGameScreen(1.0f, 1.0f, (TileEntity)this);
        }
        this.screenOffsetx = 0;
        this.screenOffsetz = 0;
        this.connectionsUp = 0;
        this.connectionsDown = 0;
        this.connectionsLeft = 0;
        this.connectionsRight = 0;
        this.isMultiscreen = false;
        final boolean b = false;
        this.connectedUp = b;
        this.connectedRight = b;
        this.connectedLeft = b;
        this.connectedDown = b;
        this.refreshOnUpdate = false;
        this.markDirty();
    }
    
    private int getLeft(final int meta) {
        switch (meta) {
            case 2: {
                return 4;
            }
            case 3: {
                return 5;
            }
            case 4: {
                return 3;
            }
            case 5: {
                return 2;
            }
            default: {
                return 4;
            }
        }
    }
    
    private int getRight(final int meta) {
        switch (meta) {
            case 2: {
                return 5;
            }
            case 3: {
                return 4;
            }
            case 4: {
                return 2;
            }
            case 5: {
                return 3;
            }
            default: {
                return 5;
            }
        }
    }
    
    private boolean canJoinRight() {
        final int meta = this.getBlockMetadata();
        final TileEntity te = new BlockVec3((TileEntity)this).getTileEntityOnSide(this.worldObj, this.getRight(meta));
        if (!(te instanceof TileEntityScreen)) {
            return false;
        }
        final TileEntityScreen screenTile = (TileEntityScreen)te;
        return screenTile.getBlockMetadata() == meta && screenTile.connectionsUp == this.connectionsUp && screenTile.connectionsDown == this.connectionsDown && (this.connectionsUp + this.connectionsDown > 0 || (this.connectionsLeft <= 0 && screenTile.connectionsRight <= 0));
    }
    
    private boolean canJoinLeft() {
        final int meta = this.getBlockMetadata();
        final TileEntity te = new BlockVec3((TileEntity)this).getTileEntityOnSide(this.worldObj, this.getLeft(meta));
        if (!(te instanceof TileEntityScreen)) {
            return false;
        }
        final TileEntityScreen screenTile = (TileEntityScreen)te;
        return screenTile.getBlockMetadata() == meta && screenTile.connectionsUp == this.connectionsUp && screenTile.connectionsDown == this.connectionsDown && (this.connectionsUp + this.connectionsDown > 0 || (this.connectionsRight <= 0 && screenTile.connectionsLeft <= 0));
    }
    
    private boolean canJoinUp() {
        final int meta = this.getBlockMetadata();
        final TileEntity te = new BlockVec3((TileEntity)this).getTileEntityOnSide(this.worldObj, 1);
        if (!(te instanceof TileEntityScreen)) {
            return false;
        }
        final TileEntityScreen screenTile = (TileEntityScreen)te;
        return screenTile.getBlockMetadata() == meta && screenTile.connectionsLeft == this.connectionsLeft && screenTile.connectionsRight == this.connectionsRight && (this.connectionsLeft + this.connectionsRight > 0 || (this.connectionsDown <= 0 && screenTile.connectionsUp <= 0));
    }
    
    private boolean canJoinDown() {
        final int meta = this.getBlockMetadata();
        final TileEntity te = new BlockVec3((TileEntity)this).getTileEntityOnSide(this.worldObj, 0);
        if (!(te instanceof TileEntityScreen)) {
            return false;
        }
        final TileEntityScreen screenTile = (TileEntityScreen)te;
        return screenTile.getBlockMetadata() == meta && screenTile.connectionsLeft == this.connectionsLeft && screenTile.connectionsRight == this.connectionsRight && (this.connectionsLeft + this.connectionsRight > 0 || (this.connectionsUp <= 0 && screenTile.connectionsDown <= 0));
    }
    
    private void joinRight() {
        final int meta = this.getBlockMetadata();
        final int side = this.getRight(meta);
        final BlockVec3 vec = new BlockVec3((TileEntity)this);
        for (int z = -this.connectionsUp; z <= this.connectionsDown; ++z) {
            final BlockVec3 newVec = vec.clone().modifyPositionFromSide(ForgeDirection.DOWN, z);
            TileEntity tile;
            if (z == 0) {
                tile = this;
            }
            else {
                tile = newVec.getTileEntity((IBlockAccess)this.worldObj);
            }
            if (tile instanceof TileEntityScreen && tile.getBlockMetadata() == meta && !tile.isInvalid()) {
                final TileEntityScreen screenTile = (TileEntityScreen)tile;
                screenTile.connectedRight = true;
                final TileEntity te2 = newVec.getTileEntityOnSide(this.worldObj, side);
                if (te2 instanceof TileEntityScreen && te2.getBlockMetadata() == meta && !te2.isInvalid()) {
                    screenTile.tryConnectRight((TileEntityScreen)te2);
                }
            }
        }
    }
    
    private void joinLeft() {
        final int meta = this.getBlockMetadata();
        final int side = this.getLeft(meta);
        final BlockVec3 vec = new BlockVec3((TileEntity)this);
        for (int z = -this.connectionsUp; z <= this.connectionsDown; ++z) {
            final BlockVec3 newVec = vec.clone().modifyPositionFromSide(ForgeDirection.DOWN, z);
            TileEntity tile;
            if (z == 0) {
                tile = this;
            }
            else {
                tile = newVec.getTileEntity((IBlockAccess)this.worldObj);
            }
            if (tile instanceof TileEntityScreen && tile.getBlockMetadata() == meta && !tile.isInvalid()) {
                final TileEntityScreen screenTile = (TileEntityScreen)tile;
                screenTile.connectedLeft = true;
                final TileEntity te2 = newVec.getTileEntityOnSide(this.worldObj, side);
                if (te2 instanceof TileEntityScreen && te2.getBlockMetadata() == meta && !te2.isInvalid()) {
                    screenTile.tryConnectLeft((TileEntityScreen)te2);
                }
            }
        }
    }
    
    private void joinUp() {
        final int meta = this.getBlockMetadata();
        final ForgeDirection side = ForgeDirection.getOrientation(this.getRight(meta));
        final BlockVec3 vec = new BlockVec3((TileEntity)this);
        for (int x = -this.connectionsLeft; x <= this.connectionsRight; ++x) {
            final BlockVec3 newVec = vec.clone().modifyPositionFromSide(side, x);
            TileEntity tile;
            if (x == 0) {
                tile = this;
            }
            else {
                tile = newVec.getTileEntity((IBlockAccess)this.worldObj);
            }
            if (tile instanceof TileEntityScreen && tile.getBlockMetadata() == meta && !tile.isInvalid()) {
                final TileEntityScreen screenTile = (TileEntityScreen)tile;
                screenTile.connectedUp = true;
                final TileEntity te2 = newVec.getTileEntityOnSide(this.worldObj, 1);
                if (te2 instanceof TileEntityScreen && te2.getBlockMetadata() == meta && !te2.isInvalid()) {
                    screenTile.tryConnectUp((TileEntityScreen)te2);
                }
            }
        }
    }
    
    private void joinDown() {
        final int meta = this.getBlockMetadata();
        final ForgeDirection side = ForgeDirection.getOrientation(this.getRight(meta));
        final BlockVec3 vec = new BlockVec3((TileEntity)this);
        for (int x = -this.connectionsLeft; x <= this.connectionsRight; ++x) {
            final BlockVec3 newVec = vec.clone().modifyPositionFromSide(side, x);
            TileEntity tile;
            if (x == 0) {
                tile = this;
            }
            else {
                tile = newVec.getTileEntity((IBlockAccess)this.worldObj);
            }
            if (tile instanceof TileEntityScreen && tile.getBlockMetadata() == meta && !tile.isInvalid()) {
                final TileEntityScreen screenTile = (TileEntityScreen)tile;
                screenTile.connectedDown = true;
                final TileEntity te2 = newVec.getTileEntityOnSide(this.worldObj, 0);
                if (te2 instanceof TileEntityScreen && te2.getBlockMetadata() == meta && !te2.isInvalid()) {
                    screenTile.tryConnectDown((TileEntityScreen)te2);
                }
            }
        }
    }
    
    private boolean tryConnectUp(final TileEntityScreen screenTile) {
        if (screenTile.connectedDown) {
            return true;
        }
        screenTile.connectedDown = true;
        if (this.connectedLeft) {
            screenTile.connectedLeft = true;
        }
        if (this.connectedRight) {
            screenTile.connectedRight = true;
        }
        screenTile.refreshConnections(false);
        return (!(this.connectedLeft ^ screenTile.connectedLeft) && !(this.connectedRight ^ screenTile.connectedRight)) || (screenTile.connectedDown = false);
    }
    
    private boolean tryConnectDown(final TileEntityScreen screenTile) {
        if (screenTile.connectedUp) {
            return true;
        }
        screenTile.connectedUp = true;
        if (this.connectedLeft) {
            screenTile.connectedLeft = true;
        }
        if (this.connectedRight) {
            screenTile.connectedRight = true;
        }
        screenTile.refreshConnections(false);
        return (!(this.connectedLeft ^ screenTile.connectedLeft) && !(this.connectedRight ^ screenTile.connectedRight)) || (screenTile.connectedUp = false);
    }
    
    private boolean tryConnectLeft(final TileEntityScreen screenTile) {
        if (screenTile.connectedRight) {
            return true;
        }
        if ((screenTile.connectedUp && !this.connectedUp) || (screenTile.connectedDown && !this.connectedDown)) {
            return false;
        }
        screenTile.connectedRight = true;
        if (this.connectedUp) {
            screenTile.connectedUp = true;
        }
        if (this.connectedDown) {
            screenTile.connectedDown = true;
        }
        screenTile.refreshConnections(false);
        return (!(this.connectedUp ^ screenTile.connectedUp) && !(this.connectedDown ^ screenTile.connectedDown)) || (screenTile.connectedRight = false);
    }
    
    private boolean tryConnectRight(final TileEntityScreen screenTile) {
        if (screenTile.connectedLeft) {
            return true;
        }
        if ((screenTile.connectedUp && !this.connectedUp) || (screenTile.connectedDown && !this.connectedDown)) {
            return false;
        }
        screenTile.connectedLeft = true;
        if (this.connectedUp) {
            screenTile.connectedUp = true;
        }
        if (this.connectedDown) {
            screenTile.connectedDown = true;
        }
        screenTile.refreshConnections(false);
        return (!(this.connectedUp ^ screenTile.connectedUp) && !(this.connectedDown ^ screenTile.connectedDown)) || (screenTile.connectedLeft = false);
    }
    
    private void log(final String msg) {
        String connections = "";
        String strSide = "S";
        if (this.connectedUp) {
            connections = "U";
        }
        if (this.connectedDown) {
            connections += "D";
        }
        if (this.connectedLeft) {
            connections += "L";
        }
        if (this.connectedRight) {
            connections += "R";
        }
        if (this.worldObj.isRemote) {
            strSide = "C";
        }
    }
    
    @SideOnly(Side.CLIENT)
    public void refreshNextTick(final boolean b) {
        this.refreshOnUpdate = true;
        TickHandlerClient.screenConnectionsUpdateList.add(this);
    }
    
    static {
        TileEntityScreen.FRAMEBORDER = 0.098f;
    }
}
