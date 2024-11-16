package micdoodle8.mods.galacticraft.planets.asteroids.blocks;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

import micdoodle8.mods.galacticraft.core.blocks.BlockTileGC;
import micdoodle8.mods.galacticraft.planets.asteroids.AsteroidsModule;
import micdoodle8.mods.galacticraft.planets.asteroids.tile.TileEntityMinerBase;

public class BlockMinerBaseFull extends BlockTileGC {

    public BlockMinerBaseFull(String assetName) {
        super(Material.rock);
        this.blockHardness = 3.0F;
        this.setBlockName(assetName);
        this.setBlockTextureName(AsteroidsModule.TEXTURE_PREFIX + "machineframe");
        this.setStepSound(soundTypeMetal);
    }

    @Override
    public int getRenderType() {
        return -1;
    }

    @Override
    public int damageDropped(int meta) {
        return 0;
    }

    @Override
    public int quantityDropped(int meta, int fortune, Random random) {
        return 1;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public TileEntity createTileEntity(World world, int metadata) {
        return new TileEntityMinerBase();
    }

    @Override
    public int onBlockPlaced(World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int meta) {
        return 0;
        // TODO
        // return this.getMetadataFromAngle(world, x, y, z, side);
    }

    @Override
    public boolean canPlaceBlockOnSide(World world, int x, int y, int z, int side) {
        // TODO
        /*
         * if (this.getMetadataFromAngle(world, x, y, z, side) != -1) { return true; }
         */

        return true;
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block par5, int par6) {
        final TileEntity tileEntity = world.getTileEntity(x, y, z);

        if (tileEntity instanceof TileEntityMinerBase) {
            ((TileEntityMinerBase) tileEntity).onBlockRemoval();
        }

        super.breakBlock(world, x, y, z, par5, par6);
    }

    @Override
    public boolean onMachineActivated(World par1World, int x, int y, int z, EntityPlayer par5EntityPlayer, int par6,
        float par7, float par8, float par9) {
        final TileEntity tileEntity = par1World.getTileEntity(x, y, z);
        if (tileEntity instanceof TileEntityMinerBase) {
            return ((TileEntityMinerBase) tileEntity).onActivated(par5EntityPlayer);
        }
        return false;
    }

    @Override
    public Item getItemDropped(int par1, Random par2Random, int par3) {
        return Item.getItemFromBlock(AsteroidBlocks.blockMinerBase);
    }

    @Override
    public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
        final ArrayList<ItemStack> ret = new ArrayList<>();
        ret.add(new ItemStack(Item.getItemFromBlock(AsteroidBlocks.blockMinerBase), 8, 0));
        return ret;
    }

    @Override
    public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z) {
        return new ItemStack(Item.getItemFromBlock(AsteroidBlocks.blockMinerBase), 1, 0);
    }

    @Override
    public boolean onUseWrench(World par1World, int x, int y, int z, EntityPlayer par5EntityPlayer, int side,
        float hitX, float hitY, float hitZ) {
        return true;
    }
}
