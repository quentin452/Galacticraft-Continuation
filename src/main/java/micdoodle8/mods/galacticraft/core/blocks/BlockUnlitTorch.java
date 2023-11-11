package micdoodle8.mods.galacticraft.core.blocks;

import net.minecraft.block.*;
import micdoodle8.mods.galacticraft.api.block.*;
import net.minecraft.block.material.*;
import micdoodle8.mods.galacticraft.core.*;
import net.minecraftforge.common.util.*;
import net.minecraft.world.*;
import net.minecraft.init.*;
import micdoodle8.mods.galacticraft.api.world.*;
import micdoodle8.mods.galacticraft.core.util.*;
import net.minecraft.util.*;
import cpw.mods.fml.relauncher.*;
import java.util.*;
import net.minecraft.item.*;

public class BlockUnlitTorch extends Block implements IOxygenReliantBlock
{
    public boolean lit;
    public Block litVersion;
    public Block unlitVersion;
    public Block fallback;
    
    protected BlockUnlitTorch(final boolean lit, final String assetName) {
        super(Material.circuits);
        this.setTickRandomly(true);
        this.lit = lit;
        this.setLightLevel(lit ? 0.9375f : 0.2f);
        this.setHardness(0.0f);
        this.setStepSound(Block.soundTypeWood);
        this.setBlockTextureName(GalacticraftCore.TEXTURE_PREFIX + assetName);
        this.setBlockName(assetName);
    }
    
    public static void register(final BlockUnlitTorch unlittorch, final BlockUnlitTorch littorch, final Block vanillatorch) {
        littorch.litVersion = littorch;
        littorch.unlitVersion = unlittorch;
        littorch.fallback = vanillatorch;
        unlittorch.litVersion = littorch;
        unlittorch.unlitVersion = unlittorch;
        unlittorch.fallback = vanillatorch;
        GalacticraftCore.handler.registerTorchType(littorch, vanillatorch);
    }
    
    public Block changeState() {
        if (this.lit) {
            return this.litVersion;
        }
        return this.unlitVersion;
    }
    
    private static boolean isBlockSolidOnSide(final World world, final int x, final int y, final int z, final ForgeDirection direction, final boolean nope) {
        return world.getBlock(x, y, z).isSideSolid((IBlockAccess)world, x, y, z, direction);
    }
    
    public AxisAlignedBB getCollisionBoundingBoxFromPool(final World par1World, final int par2, final int par3, final int par4) {
        return null;
    }
    
    public boolean isOpaqueCube() {
        return false;
    }
    
    public boolean renderAsNormalBlock() {
        return false;
    }
    
    public int getRenderType() {
        return GalacticraftCore.proxy.getBlockRender(this);
    }
    
    private boolean canPlaceTorchOn(final World par1World, final int par2, final int par3, final int par4) {
        if (World.doesBlockHaveSolidTopSurface((IBlockAccess)par1World, par2, par3, par4)) {
            return true;
        }
        final Block var5 = par1World.getBlock(par2, par3, par4);
        return var5.canPlaceTorchOnTop(par1World, par2, par3, par4);
    }
    
    public boolean canPlaceBlockAt(final World par1World, final int par2, final int par3, final int par4) {
        return isBlockSolidOnSide(par1World, par2 - 1, par3, par4, ForgeDirection.EAST, true) || isBlockSolidOnSide(par1World, par2 + 1, par3, par4, ForgeDirection.WEST, true) || isBlockSolidOnSide(par1World, par2, par3, par4 - 1, ForgeDirection.SOUTH, true) || isBlockSolidOnSide(par1World, par2, par3, par4 + 1, ForgeDirection.NORTH, true) || this.canPlaceTorchOn(par1World, par2, par3 - 1, par4);
    }
    
    public int onBlockPlaced(final World par1World, final int par2, final int par3, final int par4, final int par5, final float par6, final float par7, final float par8, final int par9) {
        int var10 = par9;
        if (par5 == 1 && this.canPlaceTorchOn(par1World, par2, par3 - 1, par4)) {
            var10 = 5;
        }
        if (par5 == 2 && isBlockSolidOnSide(par1World, par2, par3, par4 + 1, ForgeDirection.NORTH, true)) {
            var10 = 4;
        }
        if (par5 == 3 && isBlockSolidOnSide(par1World, par2, par3, par4 - 1, ForgeDirection.SOUTH, true)) {
            var10 = 3;
        }
        if (par5 == 4 && isBlockSolidOnSide(par1World, par2 + 1, par3, par4, ForgeDirection.WEST, true)) {
            var10 = 2;
        }
        if (par5 == 5 && isBlockSolidOnSide(par1World, par2 - 1, par3, par4, ForgeDirection.EAST, true)) {
            var10 = 1;
        }
        return var10;
    }
    
    public void updateTick(final World par1World, final int par2, final int par3, final int par4, final Random par5Random) {
        if (par1World.getBlockMetadata(par2, par3, par4) == 0) {
            this.onBlockAdded(par1World, par2, par3, par4);
        }
        else {
            this.checkOxygen(par1World, par2, par3, par4);
        }
    }
    
    public void onBlockAdded(final World par1World, final int par2, final int par3, final int par4) {
        if (par1World.getBlockMetadata(par2, par3, par4) == 0) {
            if (isBlockSolidOnSide(par1World, par2 - 1, par3, par4, ForgeDirection.EAST, true)) {
                par1World.setBlockMetadataWithNotify(par2, par3, par4, 1, 2);
            }
            else if (isBlockSolidOnSide(par1World, par2 + 1, par3, par4, ForgeDirection.WEST, true)) {
                par1World.setBlockMetadataWithNotify(par2, par3, par4, 2, 2);
            }
            else if (isBlockSolidOnSide(par1World, par2, par3, par4 - 1, ForgeDirection.SOUTH, true)) {
                par1World.setBlockMetadataWithNotify(par2, par3, par4, 3, 2);
            }
            else if (isBlockSolidOnSide(par1World, par2, par3, par4 + 1, ForgeDirection.NORTH, true)) {
                par1World.setBlockMetadataWithNotify(par2, par3, par4, 4, 2);
            }
            else if (this.canPlaceTorchOn(par1World, par2, par3 - 1, par4)) {
                par1World.setBlockMetadataWithNotify(par2, par3, par4, 5, 2);
            }
        }
        if (this.dropTorchIfCantStay(par1World, par2, par3, par4)) {
            this.checkOxygen(par1World, par2, par3, par4);
        }
    }
    
    public void onNeighborBlockChange(final World par1World, final int par2, final int par3, final int par4, final Block par5) {
        if (this.dropTorchIfCantStay(par1World, par2, par3, par4)) {
            final int var6 = par1World.getBlockMetadata(par2, par3, par4);
            boolean var7 = false;
            if (!isBlockSolidOnSide(par1World, par2 - 1, par3, par4, ForgeDirection.EAST, true) && var6 == 1) {
                var7 = true;
            }
            if (!isBlockSolidOnSide(par1World, par2 + 1, par3, par4, ForgeDirection.WEST, true) && var6 == 2) {
                var7 = true;
            }
            if (!isBlockSolidOnSide(par1World, par2, par3, par4 - 1, ForgeDirection.SOUTH, true) && var6 == 3) {
                var7 = true;
            }
            if (!isBlockSolidOnSide(par1World, par2, par3, par4 + 1, ForgeDirection.NORTH, true) && var6 == 4) {
                var7 = true;
            }
            if (!this.canPlaceTorchOn(par1World, par2, par3 - 1, par4) && var6 == 5) {
                var7 = true;
            }
            if (var7) {
                this.dropBlockAsItem(par1World, par2, par3, par4, par1World.getBlockMetadata(par2, par3, par4), 0);
                par1World.setBlock(par2, par3, par4, Blocks.air);
            }
            else {
                this.checkOxygen(par1World, par2, par3, par4);
            }
        }
    }
    
    private void checkOxygen(final World world, final int x, final int y, final int z) {
        if (world.provider instanceof IGalacticraftWorldProvider) {
            if (OxygenUtil.checkTorchHasOxygen(world, this, x, y, z)) {
                this.onOxygenAdded(world, x, y, z);
            }
            else {
                this.onOxygenRemoved(world, x, y, z);
            }
        }
        else {
            world.setBlock(x, y, z, this.fallback, world.getBlockMetadata(x, y, z), 2);
        }
    }
    
    private boolean dropTorchIfCantStay(final World par1World, final int par2, final int par3, final int par4) {
        if (!this.canPlaceBlockAt(par1World, par2, par3, par4)) {
            if (par1World.getBlock(par2, par3, par4) == this) {
                this.dropBlockAsItem(par1World, par2, par3, par4, par1World.getBlockMetadata(par2, par3, par4), 0);
                par1World.setBlock(par2, par3, par4, Blocks.air);
            }
            return false;
        }
        return true;
    }
    
    public MovingObjectPosition collisionRayTrace(final World par1World, final int par2, final int par3, final int par4, final Vec3 par5Vec3, final Vec3 par6Vec3) {
        final int var7 = par1World.getBlockMetadata(par2, par3, par4) & 0x7;
        float var8 = 0.15f;
        if (var7 == 1) {
            this.setBlockBounds(0.0f, 0.2f, 0.5f - var8, var8 * 2.0f, 0.8f, 0.5f + var8);
        }
        else if (var7 == 2) {
            this.setBlockBounds(1.0f - var8 * 2.0f, 0.2f, 0.5f - var8, 1.0f, 0.8f, 0.5f + var8);
        }
        else if (var7 == 3) {
            this.setBlockBounds(0.5f - var8, 0.2f, 0.0f, 0.5f + var8, 0.8f, var8 * 2.0f);
        }
        else if (var7 == 4) {
            this.setBlockBounds(0.5f - var8, 0.2f, 1.0f - var8 * 2.0f, 0.5f + var8, 0.8f, 1.0f);
        }
        else {
            var8 = 0.1f;
            this.setBlockBounds(0.5f - var8, 0.0f, 0.5f - var8, 0.5f + var8, 0.6f, 0.5f + var8);
        }
        return super.collisionRayTrace(par1World, par2, par3, par4, par5Vec3, par6Vec3);
    }
    
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(final World par1World, final int par2, final int par3, final int par4, final Random par5Random) {
        final boolean doSmoke = par5Random.nextInt(5) == 0;
        if (this.lit || doSmoke) {
            final int var6 = par1World.getBlockMetadata(par2, par3, par4);
            final double var7 = par2 + 0.5f;
            final double var8 = par3 + 0.7f;
            final double var9 = par4 + 0.5f;
            final double var10 = 0.2199999988079071;
            final double var11 = 0.27000001072883606;
            if (var6 == 1) {
                if (doSmoke) {
                    par1World.spawnParticle("smoke", var7 - 0.27000001072883606, var8 + 0.2199999988079071, var9, 0.0, 0.0, 0.0);
                }
                if (this.lit) {
                    par1World.spawnParticle("flame", var7 - 0.27000001072883606, var8 + 0.2199999988079071, var9, 0.0, 0.0, 0.0);
                }
            }
            else if (var6 == 2) {
                if (doSmoke) {
                    par1World.spawnParticle("smoke", var7 + 0.27000001072883606, var8 + 0.2199999988079071, var9, 0.0, 0.0, 0.0);
                }
                if (this.lit) {
                    par1World.spawnParticle("flame", var7 + 0.27000001072883606, var8 + 0.2199999988079071, var9, 0.0, 0.0, 0.0);
                }
            }
            else if (var6 == 3) {
                if (doSmoke) {
                    par1World.spawnParticle("smoke", var7, var8 + 0.2199999988079071, var9 - 0.27000001072883606, 0.0, 0.0, 0.0);
                }
                if (this.lit) {
                    par1World.spawnParticle("flame", var7, var8 + 0.2199999988079071, var9 - 0.27000001072883606, 0.0, 0.0, 0.0);
                }
            }
            else if (var6 == 4) {
                if (doSmoke) {
                    par1World.spawnParticle("smoke", var7, var8 + 0.2199999988079071, var9 + 0.27000001072883606, 0.0, 0.0, 0.0);
                }
                if (this.lit) {
                    par1World.spawnParticle("flame", var7, var8 + 0.2199999988079071, var9 + 0.27000001072883606, 0.0, 0.0, 0.0);
                }
            }
            else {
                if (doSmoke) {
                    par1World.spawnParticle("smoke", var7, var8, var9, 0.0, 0.0, 0.0);
                }
                if (this.lit) {
                    par1World.spawnParticle("flame", var7, var8, var9, 0.0, 0.0, 0.0);
                }
            }
        }
    }
    
    public void onOxygenRemoved(final World world, final int x, final int y, final int z) {
        if (world.provider instanceof IGalacticraftWorldProvider) {
            world.setBlock(x, y, z, this.unlitVersion, world.getBlockMetadata(x, y, z), 2);
        }
    }
    
    public void onOxygenAdded(final World world, final int x, final int y, final int z) {
        if (world.provider instanceof IGalacticraftWorldProvider) {
            world.setBlock(x, y, z, this.litVersion, world.getBlockMetadata(x, y, z), 2);
        }
    }
    
    public ArrayList<ItemStack> getDrops(final World world, final int x, final int y, final int z, final int metadata, final int fortune) {
        final ArrayList<ItemStack> ret = new ArrayList<ItemStack>();
        ret.add(new ItemStack(this.litVersion));
        return ret;
    }
}
