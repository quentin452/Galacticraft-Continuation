package micdoodle8.mods.galacticraft.core.blocks;

import net.minecraft.block.*;
import net.minecraft.block.material.*;
import micdoodle8.mods.galacticraft.core.*;
import net.minecraft.creativetab.*;
import net.minecraft.client.renderer.texture.*;
import java.util.*;
import micdoodle8.mods.galacticraft.core.items.*;
import micdoodle8.mods.galacticraft.core.tile.*;
import net.minecraft.entity.*;
import net.minecraft.tileentity.*;
import net.minecraft.init.*;
import net.minecraft.world.*;
import micdoodle8.mods.galacticraft.api.vector.*;
import net.minecraft.entity.player.*;
import net.minecraft.item.*;
import micdoodle8.mods.galacticraft.core.util.*;

public class BlockFallenMeteor extends Block implements ITileEntityProvider, ItemBlockDesc.IBlockShiftDesc
{
    public BlockFallenMeteor(final String assetName) {
        super(Material.rock);
        this.setBlockBounds(0.2f, 0.2f, 0.2f, 0.8f, 0.8f, 0.8f);
        this.setHardness(40.0f);
        this.setStepSound(Block.soundTypeStone);
        this.setBlockTextureName(GalacticraftCore.TEXTURE_PREFIX + assetName);
        this.setBlockName(assetName);
    }
    
    public CreativeTabs getCreativeTabToDisplayOn() {
        return GalacticraftCore.galacticraftBlocksTab;
    }
    
    public void registerBlockIcons(final IIconRegister par1IconRegister) {
        this.blockIcon = par1IconRegister.registerIcon(GalacticraftCore.TEXTURE_PREFIX + "fallen_meteor");
    }
    
    public int getRenderType() {
        return GalacticraftCore.proxy.getBlockRender(this);
    }
    
    public boolean isOpaqueCube() {
        return false;
    }
    
    public boolean renderAsNormalBlock() {
        return false;
    }
    
    public int quantityDroppedWithBonus(final int par1, final Random par2Random) {
        return 1 + (int)(par2Random.nextFloat() + 0.75f);
    }
    
    public Item getItemDropped(final int par1, final Random par2Random, final int par3) {
        return GCItems.meteoricIronRaw;
    }
    
    public void onEntityCollidedWithBlock(final World par1World, final int par2, final int par3, final int par4, final Entity par5Entity) {
        final TileEntity tile = par1World.getTileEntity(par2, par3, par4);
        if (tile instanceof TileEntityFallenMeteor) {
            final TileEntityFallenMeteor meteor = (TileEntityFallenMeteor)tile;
            if (meteor.getHeatLevel() <= 0) {
                return;
            }
            if (par5Entity instanceof EntityLivingBase) {
                final EntityLivingBase livingEntity = (EntityLivingBase)par5Entity;
                par1World.playSoundEffect((double)(par2 + 0.5f), (double)(par3 + 0.5f), (double)(par4 + 0.5f), "random.fizz", 0.5f, 2.6f + (par1World.rand.nextFloat() - par1World.rand.nextFloat()) * 0.8f);
                for (int var5 = 0; var5 < 8; ++var5) {
                    par1World.spawnParticle("largesmoke", par2 + Math.random(), par3 + 0.2 + Math.random(), par4 + Math.random(), 0.0, 0.0, 0.0);
                }
                if (!livingEntity.isBurning()) {
                    livingEntity.setFire(2);
                }
                double var6;
                double var7;
                for (var6 = par2 + 0.5f - livingEntity.posX, var7 = livingEntity.posZ - par4; var6 * var6 + var7 * var7 < 1.0E-4; var6 = (Math.random() - Math.random()) * 0.01, var7 = (Math.random() - Math.random()) * 0.01) {}
                livingEntity.knockBack((Entity)livingEntity, 1.0f, var6, var7);
            }
        }
    }
    
    public void onBlockAdded(final World par1World, final int par2, final int par3, final int par4) {
        par1World.scheduleBlockUpdate(par2, par3, par4, (Block)this, this.tickRate(par1World));
    }
    
    public void onNeighborBlockChange(final World par1World, final int par2, final int par3, final int par4, final Block par5) {
        par1World.scheduleBlockUpdate(par2, par3, par4, (Block)this, this.tickRate(par1World));
    }
    
    public void updateTick(final World par1World, final int par2, final int par3, final int par4, final Random par5Random) {
        if (!par1World.isRemote) {
            this.tryToFall(par1World, par2, par3, par4);
        }
    }
    
    private void tryToFall(final World par1World, final int par2, int par3, final int par4) {
        if (canFallBelow(par1World, par2, par3 - 1, par4) && par3 >= 0) {
            final int prevHeatLevel = ((TileEntityFallenMeteor)par1World.getTileEntity(par2, par3, par4)).getHeatLevel();
            par1World.setBlock(par2, par3, par4, Blocks.air, 0, 3);
            while (canFallBelow(par1World, par2, par3 - 1, par4) && par3 > 0) {
                --par3;
            }
            if (par3 > 0) {
                par1World.setBlock(par2, par3, par4, (Block)this, 0, 3);
                ((TileEntityFallenMeteor)par1World.getTileEntity(par2, par3, par4)).setHeatLevel(prevHeatLevel);
            }
        }
    }
    
    public static boolean canFallBelow(final World par0World, final int par1, final int par2, final int par3) {
        final Block var4 = par0World.getBlock(par1, par2, par3);
        if (var4.getMaterial() == Material.air) {
            return true;
        }
        if (var4 == Blocks.fire) {
            return true;
        }
        final Material var5 = var4.getMaterial();
        return var5 == Material.water || var5 == Material.lava;
    }
    
    public int colorMultiplier(final IBlockAccess par1IBlockAccess, final int par2, final int par3, final int par4) {
        final TileEntity tile = par1IBlockAccess.getTileEntity(par2, par3, par4);
        if (tile instanceof TileEntityFallenMeteor) {
            final TileEntityFallenMeteor meteor = (TileEntityFallenMeteor)tile;
            final Vector3 col = new Vector3(198.0, 108.0, 58.0);
            col.translate((double)(200.0f - meteor.getScaledHeatLevel() * 200.0f));
            col.x = Math.min(255.0, col.x);
            col.y = Math.min(255.0, col.y);
            col.z = Math.min(255.0, col.z);
            return ColorUtil.to32BitColor(255, (byte)col.x, (byte)col.y, (byte)col.z);
        }
        return super.colorMultiplier(par1IBlockAccess, par2, par3, par4);
    }
    
    public TileEntity createNewTileEntity(final World world, final int meta) {
        return new TileEntityFallenMeteor();
    }
    
    public boolean canSilkHarvest() {
        return true;
    }
    
    public float getPlayerRelativeBlockHardness(final EntityPlayer player, final World world, final int x, final int y, final int z) {
        final int metadata = world.getBlockMetadata(x, y, z);
        final float hardness = this.getBlockHardness(world, x, y, z);
        if (hardness < 0.0f) {
            return 0.0f;
        }
        final int power = this.canHarvestBlock(this, player, metadata);
        if (power > 0) {
            return power * player.getBreakSpeed((Block)this, true, metadata, x, y, z) / hardness / 30.0f;
        }
        return player.getBreakSpeed((Block)this, false, metadata, x, y, z) / hardness / 30.0f;
    }
    
    public int canHarvestBlock(final Block block, final EntityPlayer player, final int metadata) {
        final ItemStack stack = player.inventory.getCurrentItem();
        final String tool = block.getHarvestTool(metadata);
        if (stack == null || tool == null) {
            return player.canHarvestBlock(block) ? 1 : 0;
        }
        final int toolLevel = stack.getItem().getHarvestLevel(stack, tool) - block.getHarvestLevel(metadata) + 1;
        if (toolLevel < 1) {
            return player.canHarvestBlock(block) ? 1 : 0;
        }
        return toolLevel;
    }
    
    public String getShiftDescription(final int meta) {
        return GCCoreUtil.translate(this.getUnlocalizedName() + ".description");
    }
    
    public boolean showDescription(final int meta) {
        return true;
    }
}
