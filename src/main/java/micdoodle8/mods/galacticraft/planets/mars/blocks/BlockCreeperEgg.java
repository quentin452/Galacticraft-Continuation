package micdoodle8.mods.galacticraft.planets.mars.blocks;

import micdoodle8.mods.galacticraft.core.items.*;
import net.minecraft.client.renderer.texture.*;
import cpw.mods.fml.relauncher.*;
import net.minecraft.creativetab.*;
import micdoodle8.mods.galacticraft.core.*;
import net.minecraft.entity.player.*;
import net.minecraft.util.*;
import net.minecraft.world.*;
import micdoodle8.mods.galacticraft.core.entities.*;
import net.minecraft.entity.*;
import net.minecraft.block.*;
import micdoodle8.mods.galacticraft.planets.mars.items.*;
import net.minecraftforge.common.*;
import net.minecraft.item.*;
import micdoodle8.mods.galacticraft.core.util.*;

public class BlockCreeperEgg extends BlockDragonEgg implements ItemBlockDesc.IBlockShiftDesc
{
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(final IIconRegister iconRegister) {
        this.blockIcon = iconRegister.registerIcon("galacticraftmars:creeperEgg");
    }
    
    public boolean isOpaqueCube() {
        return false;
    }
    
    @SideOnly(Side.CLIENT)
    public CreativeTabs getCreativeTabToDisplayOn() {
        return GalacticraftCore.galacticraftBlocksTab;
    }
    
    public boolean renderAsNormalBlock() {
        return false;
    }
    
    public int getRenderType() {
        return 27;
    }
    
    public boolean onBlockActivated(final World par1World, final int par2, final int par3, final int par4, final EntityPlayer par5EntityPlayer, final int par6, final float par7, final float par8, final float par9) {
        return false;
    }
    
    public void onBlockClicked(final World par1World, final int par2, final int par3, final int par4, final EntityPlayer par5EntityPlayer) {
    }
    
    @SideOnly(Side.CLIENT)
    public ItemStack getPickBlock(final MovingObjectPosition target, final World world, final int x, final int y, final int z) {
        return null;
    }
    
    public void onBlockExploded(final World world, final int x, final int y, final int z, final Explosion explosion) {
        if (!world.isRemote) {
            final EntityEvolvedCreeper creeper = new EntityEvolvedCreeper(world);
            creeper.setPosition(x + 0.5, (double)(y + 3), z + 0.5);
            creeper.setChild(true);
            world.spawnEntityInWorld((Entity)creeper);
        }
        world.setBlockToAir(x, y, z);
        this.onBlockDestroyedByExplosion(world, x, y, z, explosion);
    }
    
    public boolean canDropFromExplosion(final Explosion explose) {
        return false;
    }
    
    public boolean canHarvestBlock(final EntityPlayer player, final int metadata) {
        final ItemStack stack = player.inventory.getCurrentItem();
        if (stack == null) {
            return player.canHarvestBlock((Block)this);
        }
        return stack.getItem() == MarsItems.deshPickSlime;
    }
    
    public float getPlayerRelativeBlockHardness(final EntityPlayer player, final World p_149737_2_, final int p_149737_3_, final int p_149737_4_, final int p_149737_5_) {
        final ItemStack stack = player.inventory.getCurrentItem();
        if (stack != null && stack.getItem() == MarsItems.deshPickSlime) {
            return 0.2f;
        }
        return ForgeHooks.blockStrength((Block)this, player, p_149737_2_, p_149737_3_, p_149737_4_, p_149737_5_);
    }
    
    @SideOnly(Side.CLIENT)
    public Item getItem(final World par1World, final int par2, final int par3, final int par4) {
        return Item.getItemFromBlock((Block)this);
    }
    
    public String getShiftDescription(final int meta) {
        return GCCoreUtil.translate(this.getUnlocalizedName() + ".description");
    }
    
    public boolean showDescription(final int meta) {
        return true;
    }
}
