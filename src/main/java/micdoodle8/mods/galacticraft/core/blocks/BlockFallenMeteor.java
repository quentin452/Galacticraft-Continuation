package micdoodle8.mods.galacticraft.core.blocks;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import micdoodle8.mods.galacticraft.api.vector.Vector3;
import micdoodle8.mods.galacticraft.core.GalacticraftCore;
import micdoodle8.mods.galacticraft.core.items.GCItems;
import micdoodle8.mods.galacticraft.core.items.ItemBlockDesc;
import micdoodle8.mods.galacticraft.core.tile.TileEntityFallenMeteor;
import micdoodle8.mods.galacticraft.core.util.ColorUtil;
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil;

public class BlockFallenMeteor extends Block implements ITileEntityProvider, ItemBlockDesc.IBlockShiftDesc {

    public BlockFallenMeteor(String assetName) {
        super(Material.rock);
        this.setBlockBounds(0.2F, 0.2F, 0.2F, 0.8F, 0.8F, 0.8F);
        this.setHardness(40.0F);
        this.setStepSound(Block.soundTypeStone);
        this.setBlockTextureName(GalacticraftCore.TEXTURE_PREFIX + assetName);
        this.setBlockName(assetName);
    }

    @Override
    public CreativeTabs getCreativeTabToDisplayOn() {
        return GalacticraftCore.galacticraftBlocksTab;
    }

    @Override
    public void registerBlockIcons(IIconRegister par1IconRegister) {
        this.blockIcon = par1IconRegister.registerIcon(GalacticraftCore.TEXTURE_PREFIX + "fallen_meteor");
    }

    @Override
    public int getRenderType() {
        return GalacticraftCore.proxy.getBlockRender(this);
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public int quantityDroppedWithBonus(int par1, Random par2Random) {
        return 1 + (int) (par2Random.nextFloat() + 0.75F);
    }

    @Override
    public Item getItemDropped(int par1, Random par2Random, int par3) {
        return GCItems.meteoricIronRaw;
    }

    @Override
    public void onEntityCollidedWithBlock(World par1World, int par2, int par3, int par4, Entity par5Entity) {
        final TileEntity tile = par1World.getTileEntity(par2, par3, par4);

        if (tile instanceof TileEntityFallenMeteor) {
            TileEntityFallenMeteor meteor = (TileEntityFallenMeteor) tile;

            if (meteor.getHeatLevel() <= 0) {
                return;
            }

            if (par5Entity instanceof EntityLivingBase) {
                EntityLivingBase livingEntity = (EntityLivingBase) par5Entity;

                par1World.playSoundEffect(
                    par2 + 0.5F,
                    par3 + 0.5F,
                    par4 + 0.5F,
                    "random.fizz",
                    0.5F,
                    2.6F + (par1World.rand.nextFloat() - par1World.rand.nextFloat()) * 0.8F);

                for (int var5 = 0; var5 < 8; ++var5) {
                    par1World.spawnParticle(
                        "largesmoke",
                        par2 + Math.random(),
                        par3 + 0.2D + Math.random(),
                        par4 + Math.random(),
                        0.0D,
                        0.0D,
                        0.0D);
                }

                if (!livingEntity.isBurning()) {
                    livingEntity.setFire(2);
                }

                double var9 = par2 + 0.5F - livingEntity.posX;
                double var7;

                for (var7 = livingEntity.posZ - par4; var9 * var9 + var7 * var7
                    < 1.0E-4D; var7 = (Math.random() - Math.random()) * 0.01D) {
                    var9 = (Math.random() - Math.random()) * 0.01D;
                }

                livingEntity.knockBack(livingEntity, 1, var9, var7);
            }
        }
    }

    @Override
    public void onBlockAdded(World par1World, int par2, int par3, int par4) {
        par1World.scheduleBlockUpdate(par2, par3, par4, this, this.tickRate(par1World));
    }

    @Override
    public void onNeighborBlockChange(World par1World, int par2, int par3, int par4, Block par5) {
        par1World.scheduleBlockUpdate(par2, par3, par4, this, this.tickRate(par1World));
    }

    @Override
    public void updateTick(World par1World, int par2, int par3, int par4, Random par5Random) {
        if (!par1World.isRemote) {
            this.tryToFall(par1World, par2, par3, par4);
        }
    }

    private void tryToFall(World par1World, int par2, int par3, int par4) {
        if (BlockFallenMeteor.canFallBelow(par1World, par2, par3 - 1, par4) && par3 >= 0) {
            final int prevHeatLevel = ((TileEntityFallenMeteor) par1World.getTileEntity(par2, par3, par4))
                .getHeatLevel();
            par1World.setBlock(par2, par3, par4, Blocks.air, 0, 3);

            while (BlockFallenMeteor.canFallBelow(par1World, par2, par3 - 1, par4) && par3 > 0) {
                --par3;
            }

            if (par3 > 0) {
                par1World.setBlock(par2, par3, par4, this, 0, 3);
                ((TileEntityFallenMeteor) par1World.getTileEntity(par2, par3, par4)).setHeatLevel(prevHeatLevel);
            }
        }
    }

    public static boolean canFallBelow(World par0World, int par1, int par2, int par3) {
        final Block var4 = par0World.getBlock(par1, par2, par3);

        if (var4.getMaterial() == Material.air || var4 == Blocks.fire) {
            return true;
        }
        final Material var5 = var4.getMaterial();
        return var5 == Material.water || var5 == Material.lava;
    }

    @Override
    public int colorMultiplier(IBlockAccess par1IBlockAccess, int par2, int par3, int par4) {
        final TileEntity tile = par1IBlockAccess.getTileEntity(par2, par3, par4);

        if (tile instanceof TileEntityFallenMeteor) {
            TileEntityFallenMeteor meteor = (TileEntityFallenMeteor) tile;

            final Vector3 col = new Vector3(198, 108, 58);
            col.translate(200 - meteor.getScaledHeatLevel() * 200);
            col.x = Math.min(255, col.x);
            col.y = Math.min(255, col.y);
            col.z = Math.min(255, col.z);

            return ColorUtil.to32BitColor(255, (byte) col.x, (byte) col.y, (byte) col.z);
        }

        return super.colorMultiplier(par1IBlockAccess, par2, par3, par4);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntityFallenMeteor();
    }

    @Override
    public boolean canSilkHarvest() {
        return true;
    }

    @Override
    public float getPlayerRelativeBlockHardness(EntityPlayer player, World world, int x, int y, int z) {
        final int metadata = world.getBlockMetadata(x, y, z);
        final float hardness = this.getBlockHardness(world, x, y, z);
        if (hardness < 0.0F) {
            return 0.0F;
        }

        final int power = this.canHarvestBlock(this, player, metadata);
        if (power > 0) {
            return power * player.getBreakSpeed(this, true, metadata, x, y, z) / hardness / 30F;
        }
        return player.getBreakSpeed(this, false, metadata, x, y, z) / hardness / 30F;
    }

    public int canHarvestBlock(Block block, EntityPlayer player, int metadata) {
        final ItemStack stack = player.inventory.getCurrentItem();
        final String tool = block.getHarvestTool(metadata);
        if (stack == null || tool == null) {
            return player.canHarvestBlock(block) ? 1 : 0;
        }

        final int toolLevel = stack.getItem()
            .getHarvestLevel(stack, tool) - block.getHarvestLevel(metadata)
            + 1;
        if (toolLevel < 1) {
            return player.canHarvestBlock(block) ? 1 : 0;
        }

        return toolLevel;
    }

    @Override
    public String getShiftDescription(int meta) {
        return GCCoreUtil.translate(this.getUnlocalizedName() + ".description");
    }

    @Override
    public boolean showDescription(int meta) {
        return true;
    }
}
