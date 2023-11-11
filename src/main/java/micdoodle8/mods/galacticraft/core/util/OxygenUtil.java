package micdoodle8.mods.galacticraft.core.util;

import cpw.mods.fml.client.*;
import net.minecraft.client.gui.inventory.*;
import net.minecraft.client.gui.*;
import cpw.mods.fml.relauncher.*;
import net.minecraft.entity.*;
import net.minecraft.util.*;
import micdoodle8.mods.galacticraft.core.blocks.*;
import net.minecraft.block.material.*;
import micdoodle8.mods.galacticraft.core.oxygen.*;
import micdoodle8.mods.galacticraft.api.block.*;
import net.minecraftforge.common.util.*;
import net.minecraft.block.*;
import net.minecraft.item.*;
import micdoodle8.mods.galacticraft.core.entities.player.*;
import micdoodle8.mods.galacticraft.api.item.*;
import net.minecraft.entity.player.*;
import micdoodle8.mods.galacticraft.core.items.*;
import net.minecraft.tileentity.*;
import micdoodle8.mods.galacticraft.core.energy.*;
import micdoodle8.mods.galacticraft.api.transmission.tile.*;
import micdoodle8.mods.galacticraft.api.transmission.*;
import mekanism.api.gas.*;
import mekanism.api.transmitters.*;
import net.minecraft.world.*;
import micdoodle8.mods.galacticraft.api.world.*;
import micdoodle8.mods.galacticraft.core.tile.*;
import micdoodle8.mods.galacticraft.api.vector.*;
import java.util.*;

public class OxygenUtil
{
    private static HashSet<BlockVec3> checked;
    
    @SideOnly(Side.CLIENT)
    public static boolean shouldDisplayTankGui(final GuiScreen gui) {
        return !FMLClientHandler.instance().getClient().gameSettings.hideGUI && (gui == null || (!(gui instanceof GuiInventory) && gui instanceof GuiChat));
    }
    
    public static boolean isAABBInBreathableAirBlock(final EntityLivingBase entity) {
        return isAABBInBreathableAirBlock(entity, false);
    }
    
    public static boolean isAABBInBreathableAirBlock(final EntityLivingBase entity, final boolean testThermal) {
        final double y = entity.posY + entity.getEyeHeight();
        final double x = entity.posX;
        final double z = entity.posZ;
        final double sx = entity.boundingBox.maxX - entity.boundingBox.minX;
        final double sy = entity.boundingBox.maxY - entity.boundingBox.minY;
        final double sz = entity.boundingBox.maxZ - entity.boundingBox.minZ;
        final double smin = Math.min(sx, Math.min(sy, sz)) / 2.0;
        return isAABBInBreathableAirBlock(entity.worldObj, AxisAlignedBB.getBoundingBox(x - smin, y - smin, z - smin, x + smin, y + smin, z + smin), testThermal);
    }
    
    public static boolean isAABBInBreathableAirBlock(final World world, final AxisAlignedBB bb) {
        return isAABBInBreathableAirBlock(world, bb, false);
    }
    
    public static boolean isAABBInBreathableAirBlock(final World world, final AxisAlignedBB bb, final boolean testThermal) {
        final double avgX = (bb.minX + bb.maxX) / 2.0;
        final double avgY = (bb.minY + bb.maxY) / 2.0;
        final double avgZ = (bb.minZ + bb.maxZ) / 2.0;
        if (testThermal) {
            return isInOxygenAndThermalBlock(world, bb.copy().contract(0.001, 0.001, 0.001));
        }
        return inOxygenBubble(world, avgX, avgY, avgZ) || isInOxygenBlock(world, bb.copy().contract(0.001, 0.001, 0.001));
    }
    
    public static boolean isInOxygenBlock(final World world, final AxisAlignedBB bb) {
        final int i = MathHelper.floor_double(bb.minX);
        final int j = MathHelper.floor_double(bb.maxX);
        final int k = MathHelper.floor_double(bb.minY);
        final int l = MathHelper.floor_double(bb.maxY);
        final int i2 = MathHelper.floor_double(bb.minZ);
        final int j2 = MathHelper.floor_double(bb.maxZ);
        OxygenUtil.checked = new HashSet<BlockVec3>();
        if (world.checkChunksExist(i, k, i2, j, l, j2)) {
            for (int x = i; x <= j; ++x) {
                for (int y = k; y <= l; ++y) {
                    for (int z = i2; z <= j2; ++z) {
                        final Block block = world.getBlock(x, y, z);
                        if (testContactWithBreathableAir(world, block, x, y, z, 0) >= 0) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
    
    public static boolean isInOxygenAndThermalBlock(final World world, final AxisAlignedBB bb) {
        final int i = MathHelper.floor_double(bb.minX);
        final int j = MathHelper.floor_double(bb.maxX);
        final int k = MathHelper.floor_double(bb.minY);
        final int l = MathHelper.floor_double(bb.maxY);
        final int i2 = MathHelper.floor_double(bb.minZ);
        final int j2 = MathHelper.floor_double(bb.maxZ);
        OxygenUtil.checked = new HashSet<BlockVec3>();
        if (world.checkChunksExist(i, k, i2, j, l, j2)) {
            for (int x = i; x <= j; ++x) {
                for (int y = k; y <= l; ++y) {
                    for (int z = i2; z <= j2; ++z) {
                        final Block block = world.getBlock(x, y, z);
                        if (testContactWithBreathableAir(world, block, x, y, z, 0) == 1) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
    
    public static boolean checkTorchHasOxygen(final World world, final Block block, final int x, final int y, final int z) {
        if (inOxygenBubble(world, x + 0.5, y + 0.6, z + 0.5)) {
            return true;
        }
        OxygenUtil.checked = new HashSet<BlockVec3>();
        final BlockVec3 vec = new BlockVec3(x, y, z);
        for (int side = 0; side < 6; ++side) {
            final BlockVec3 sidevec = vec.newVecSide(side);
            final Block newblock = sidevec.getBlockID_noChunkLoad(world);
            if (testContactWithBreathableAir(world, newblock, sidevec.x, sidevec.y, sidevec.z, 1) >= 0) {
                return true;
            }
        }
        return false;
    }
    
    private static int testContactWithBreathableAir(final World world, final Block block, final int x, final int y, final int z, final int limitCount) {
        final BlockVec3 vec = new BlockVec3(x, y, z);
        OxygenUtil.checked.add(vec);
        if (block == GCBlocks.breatheableAir || block == GCBlocks.brightBreatheableAir) {
            return world.getBlockMetadata(x, y, z);
        }
        if (block == null || block.getMaterial() == Material.air) {
            return -1;
        }
        boolean permeableFlag = false;
        if (!(block instanceof BlockLeavesBase)) {
            if (block.isOpaqueCube()) {
                if (!(block instanceof BlockGravel) && block.getMaterial() != Material.cloth && !(block instanceof BlockSponge)) {
                    return -1;
                }
                permeableFlag = true;
            }
            else {
                if (block instanceof BlockGlass || block instanceof BlockStainedGlass) {
                    return -1;
                }
                if (block instanceof BlockLiquid) {
                    return -1;
                }
                if (OxygenPressureProtocol.nonPermeableBlocks.containsKey(block)) {
                    final ArrayList<Integer> metaList = OxygenPressureProtocol.nonPermeableBlocks.get(block);
                    if (metaList.contains(-1) || metaList.contains(world.getBlockMetadata(x, y, z))) {
                        return -1;
                    }
                }
            }
        }
        else {
            permeableFlag = true;
        }
        if (limitCount < 5) {
            for (int side = 0; side < 6; ++side) {
                if (permeableFlag || canBlockPassAirOnSide(world, block, vec, side)) {
                    final BlockVec3 sidevec = vec.newVecSide(side);
                    if (!OxygenUtil.checked.contains(sidevec)) {
                        final Block newblock = sidevec.getBlockID_noChunkLoad(world);
                        final int adjResult = testContactWithBreathableAir(world, newblock, sidevec.x, sidevec.y, sidevec.z, limitCount + 1);
                        if (adjResult >= 0) {
                            return adjResult;
                        }
                    }
                }
            }
        }
        return -1;
    }
    
    private static boolean canBlockPassAirOnSide(final World world, final Block block, final BlockVec3 vec, final int side) {
        if (block instanceof IPartialSealableBlock) {
            return !((IPartialSealableBlock)block).isSealed(world, vec.x, vec.y, vec.z, ForgeDirection.getOrientation(side));
        }
        if (block instanceof BlockSlab) {
            return (side != 0 || (vec.getBlockMetadata((IBlockAccess)world) & 0x8) != 0x8) && (side != 1 || (vec.getBlockMetadata((IBlockAccess)world) & 0x8) != 0x0);
        }
        if (block instanceof BlockFarmland || block instanceof BlockEnchantmentTable || block instanceof BlockLiquid) {
            return side != 1;
        }
        if (!(block instanceof BlockPistonBase)) {
            return !block.isSideSolid((IBlockAccess)world, vec.x, vec.y, vec.z, ForgeDirection.getOrientation(side ^ 0x1));
        }
        final BlockPistonBase piston = (BlockPistonBase)block;
        final int meta = vec.getBlockMetadata((IBlockAccess)world);
        if (BlockPistonBase.isExtended(meta)) {
            final int facing = BlockPistonBase.getPistonOrientation(meta);
            return side != facing;
        }
        return false;
    }
    
    public static int getDrainSpacing(final ItemStack tank, final ItemStack tank2) {
        final boolean tank1Valid = tank != null && tank.getItem() instanceof ItemOxygenTank && tank.getMaxDamage() - tank.getItemDamage() > 0;
        final boolean tank2Valid = tank2 != null && tank2.getItem() instanceof ItemOxygenTank && tank2.getMaxDamage() - tank2.getItemDamage() > 0;
        if (!tank1Valid && !tank2Valid) {
            return 0;
        }
        return 9;
    }
    
    public static boolean hasValidOxygenSetup(final EntityPlayerMP player) {
        boolean missingComponent = false;
        final GCPlayerStats stats = GCPlayerStats.get(player);
        if (stats.extendedInventory.getStackInSlot(0) == null || !isItemValidForPlayerTankInv(0, stats.extendedInventory.getStackInSlot(0))) {
            boolean handled = false;
            for (final ItemStack armorStack : player.inventory.armorInventory) {
                if (armorStack != null && armorStack.getItem() instanceof IBreathableArmor) {
                    final IBreathableArmor breathableArmor = (IBreathableArmor)armorStack.getItem();
                    if (breathableArmor.handleGearType(IBreathableArmor.EnumGearType.HELMET) && breathableArmor.canBreathe(armorStack, (EntityPlayer)player, IBreathableArmor.EnumGearType.HELMET)) {
                        handled = true;
                    }
                }
            }
            if (!handled) {
                missingComponent = true;
            }
        }
        if (stats.extendedInventory.getStackInSlot(1) == null || !isItemValidForPlayerTankInv(1, stats.extendedInventory.getStackInSlot(1))) {
            boolean handled = false;
            for (final ItemStack armorStack : player.inventory.armorInventory) {
                if (armorStack != null && armorStack.getItem() instanceof IBreathableArmor) {
                    final IBreathableArmor breathableArmor = (IBreathableArmor)armorStack.getItem();
                    if (breathableArmor.handleGearType(IBreathableArmor.EnumGearType.GEAR) && breathableArmor.canBreathe(armorStack, (EntityPlayer)player, IBreathableArmor.EnumGearType.GEAR)) {
                        handled = true;
                    }
                }
            }
            if (!handled) {
                missingComponent = true;
            }
        }
        if ((stats.extendedInventory.getStackInSlot(2) == null || !isItemValidForPlayerTankInv(2, stats.extendedInventory.getStackInSlot(2))) && (stats.extendedInventory.getStackInSlot(3) == null || !isItemValidForPlayerTankInv(3, stats.extendedInventory.getStackInSlot(3)))) {
            boolean handled = false;
            for (final ItemStack armorStack : player.inventory.armorInventory) {
                if (armorStack != null && armorStack.getItem() instanceof IBreathableArmor) {
                    final IBreathableArmor breathableArmor = (IBreathableArmor)armorStack.getItem();
                    if (breathableArmor.handleGearType(IBreathableArmor.EnumGearType.TANK1) && breathableArmor.canBreathe(armorStack, (EntityPlayer)player, IBreathableArmor.EnumGearType.TANK1)) {
                        handled = true;
                    }
                    if (breathableArmor.handleGearType(IBreathableArmor.EnumGearType.TANK2) && breathableArmor.canBreathe(armorStack, (EntityPlayer)player, IBreathableArmor.EnumGearType.TANK2)) {
                        handled = true;
                    }
                }
            }
            if (!handled) {
                missingComponent = true;
            }
        }
        return !missingComponent;
    }
    
    public static boolean isItemValidForPlayerTankInv(final int slotIndex, final ItemStack stack) {
        switch (slotIndex) {
            case 0: {
                return stack.getItem() instanceof ItemOxygenMask;
            }
            case 1: {
                return stack.getItem() instanceof ItemOxygenGear;
            }
            case 2: {
                return stack.getItem() instanceof ItemOxygenTank;
            }
            case 3: {
                return stack.getItem() instanceof ItemOxygenTank;
            }
            default: {
                return false;
            }
        }
    }
    
    public static TileEntity[] getAdjacentOxygenConnections(final TileEntity tile) {
        final TileEntity[] adjacentConnections = new TileEntity[ForgeDirection.VALID_DIRECTIONS.length];
        final boolean isMekLoaded = EnergyConfigHandler.isMekanismLoaded();
        final BlockVec3 thisVec = new BlockVec3(tile);
        final World world = tile.getWorldObj();
        for (final ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS) {
            final TileEntity tileEntity = thisVec.getTileEntityOnSide(world, direction);
            if (tileEntity instanceof IConnector) {
                if (((IConnector)tileEntity).canConnect(direction.getOpposite(), NetworkType.OXYGEN)) {
                    adjacentConnections[direction.ordinal()] = tileEntity;
                }
            }
            else if (isMekLoaded && tileEntity instanceof ITubeConnection && (!(tileEntity instanceof IGasTransmitter) || TransmissionType.checkTransmissionType(tileEntity, TransmissionType.GAS, tileEntity)) && ((ITubeConnection)tileEntity).canTubeConnect(direction)) {
                adjacentConnections[direction.ordinal()] = tileEntity;
            }
        }
        return adjacentConnections;
    }
    
    public static boolean noAtmosphericCombustion(final WorldProvider provider) {
        return provider instanceof IGalacticraftWorldProvider && !((IGalacticraftWorldProvider)provider).isGasPresent(IAtmosphericGas.OXYGEN) && !((IGalacticraftWorldProvider)provider).hasBreathableAtmosphere();
    }
    
    public static boolean inOxygenBubble(final World worldObj, final double avgX, final double avgY, final double avgZ) {
        for (final BlockVec3Dim blockVec : TileEntityOxygenDistributor.loadedTiles) {
            if (blockVec != null && blockVec.dim == worldObj.provider.dimensionId) {
                final TileEntity tile = worldObj.getTileEntity(blockVec.x, blockVec.y, blockVec.z);
                if (tile instanceof TileEntityOxygenDistributor && ((TileEntityOxygenDistributor)tile).inBubble(avgX, avgY, avgZ)) {
                    return true;
                }
                continue;
            }
        }
        return false;
    }
}
