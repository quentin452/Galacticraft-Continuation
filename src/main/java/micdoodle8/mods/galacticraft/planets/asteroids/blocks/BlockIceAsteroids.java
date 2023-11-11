package micdoodle8.mods.galacticraft.planets.asteroids.blocks;

import net.minecraft.block.material.*;
import net.minecraft.creativetab.*;
import micdoodle8.mods.galacticraft.core.*;
import cpw.mods.fml.relauncher.*;
import net.minecraft.entity.player.*;
import net.minecraft.stats.*;
import net.minecraft.block.*;
import net.minecraft.enchantment.*;
import net.minecraft.entity.*;
import net.minecraft.item.*;
import net.minecraftforge.event.*;
import micdoodle8.mods.galacticraft.api.world.*;
import net.minecraft.init.*;
import java.util.*;
import net.minecraft.world.*;
import net.minecraft.client.renderer.texture.*;

public class BlockIceAsteroids extends BlockBreakable
{
    public BlockIceAsteroids(final String assetName) {
        super(assetName, Material.ice, false);
        this.slipperiness = 0.98f;
        this.setTickRandomly(true);
        this.setCreativeTab(CreativeTabs.tabBlock);
        this.setBlockTextureName("galacticraftasteroids:" + assetName);
        this.setHardness(0.5f);
        this.setBlockName(assetName);
        this.setStepSound(BlockIceAsteroids.soundTypeGlass);
    }
    
    @SideOnly(Side.CLIENT)
    public CreativeTabs getCreativeTabToDisplayOn() {
        return GalacticraftCore.galacticraftBlocksTab;
    }
    
    @SideOnly(Side.CLIENT)
    public int getRenderBlockPass() {
        return 1;
    }
    
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(final IBlockAccess world, final int x, final int y, final int z, final int side) {
        return super.shouldSideBeRendered(world, x, y, z, 1 - side);
    }
    
    public void harvestBlock(final World world, final EntityPlayer player, final int x, final int y, final int z, final int meta) {
        player.addStat(StatList.mineBlockStatArray[Block.getIdFromBlock((Block)this)], 1);
        player.addExhaustion(0.025f);
        if (this.canSilkHarvest(world, player, x, y, z, meta) && EnchantmentHelper.getSilkTouchModifier((EntityLivingBase)player)) {
            final ArrayList<ItemStack> items = new ArrayList<ItemStack>();
            final ItemStack itemstack = this.createStackedBlock(meta);
            if (itemstack != null) {
                items.add(itemstack);
            }
            ForgeEventFactory.fireBlockHarvesting((ArrayList)items, world, (Block)this, x, y, z, meta, 0, 1.0f, true, player);
            for (final ItemStack is : items) {
                this.dropBlockAsItem(world, x, y, z, is);
            }
        }
        else {
            if (world.provider.isHellWorld || world.provider instanceof IGalacticraftWorldProvider) {
                world.setBlockToAir(x, y, z);
                return;
            }
            final int i1 = EnchantmentHelper.getFortuneModifier((EntityLivingBase)player);
            this.harvesters.set(player);
            this.dropBlockAsItem(world, x, y, z, meta, i1);
            this.harvesters.set(null);
            final Material material = world.getBlock(x, y - 1, z).getMaterial();
            if (material.blocksMovement() || material.isLiquid()) {
                world.setBlock(x, y, z, (Block)Blocks.flowing_water);
            }
        }
    }
    
    public int quantityDropped(final Random rand) {
        return 0;
    }
    
    public void updateTick(final World world, final int x, final int y, final int z, final Random rand) {
        if (world.getSavedLightValue(EnumSkyBlock.Block, x, y, z) > 13) {
            if (world.provider.isHellWorld || world.provider instanceof IGalacticraftWorldProvider) {
                world.setBlockToAir(x, y, z);
                return;
            }
            this.dropBlockAsItem(world, x, y, z, world.getBlockMetadata(x, y, z), 0);
            world.setBlock(x, y, z, Blocks.water);
        }
    }
    
    public int getMobilityFlag() {
        return 0;
    }
    
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(final IIconRegister iconRegister) {
        this.blockIcon = iconRegister.registerIcon(this.getTextureName());
    }
}
