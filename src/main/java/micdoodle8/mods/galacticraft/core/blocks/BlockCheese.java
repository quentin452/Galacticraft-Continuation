package micdoodle8.mods.galacticraft.core.blocks;

import net.minecraft.block.*;
import micdoodle8.mods.galacticraft.core.items.*;
import net.minecraft.block.material.*;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.world.*;
import net.minecraft.util.*;
import cpw.mods.fml.relauncher.*;
import net.minecraft.entity.player.*;
import java.util.*;
import net.minecraft.item.*;
import net.minecraft.init.*;
import micdoodle8.mods.galacticraft.core.util.*;

public class BlockCheese extends Block implements ItemBlockDesc.IBlockShiftDesc
{
    IIcon[] cheeseIcons;
    
    public BlockCheese() {
        super(Material.cake);
        this.setTickRandomly(true);
        this.disableStats();
        this.setHardness(0.5f);
        this.setStepSound(Block.soundTypeCloth);
        this.setBlockName("cheeseBlock");
    }
    
    public void registerBlockIcons(final IIconRegister par1IconRegister) {
        (this.cheeseIcons = new IIcon[3])[0] = par1IconRegister.registerIcon("galacticraftmoon:cheese_1");
        this.cheeseIcons[1] = par1IconRegister.registerIcon("galacticraftmoon:cheese_2");
        this.cheeseIcons[2] = par1IconRegister.registerIcon("galacticraftmoon:cheese_3");
    }
    
    public void setBlockBoundsBasedOnState(final IBlockAccess par1IBlockAccess, final int par2, final int par3, final int par4) {
        final int var5 = par1IBlockAccess.getBlockMetadata(par2, par3, par4);
        final float var6 = 0.0625f;
        final float var7 = (1 + var5 * 2) / 16.0f;
        final float var8 = 0.5f;
        this.setBlockBounds(var7, 0.0f, 0.0625f, 0.9375f, 0.5f, 0.9375f);
    }
    
    public void setBlockBoundsForItemRender() {
        final float var1 = 0.0625f;
        final float var2 = 0.5f;
        this.setBlockBounds(0.0625f, 0.0f, 0.0625f, 0.9375f, 0.5f, 0.9375f);
    }
    
    public AxisAlignedBB getCollisionBoundingBoxFromPool(final World par1World, final int par2, final int par3, final int par4) {
        final int var5 = par1World.getBlockMetadata(par2, par3, par4);
        final float var6 = 0.0625f;
        final float var7 = (1 + var5 * 2) / 16.0f;
        final float var8 = 0.5f;
        return AxisAlignedBB.getBoundingBox((double)(par2 + var7), (double)par3, (double)(par4 + 0.0625f), (double)(par2 + 1 - 0.0625f), (double)(par3 + 0.5f - 0.0625f), (double)(par4 + 1 - 0.0625f));
    }
    
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getSelectedBoundingBoxFromPool(final World par1World, final int par2, final int par3, final int par4) {
        final int var5 = par1World.getBlockMetadata(par2, par3, par4);
        final float var6 = 0.0625f;
        final float var7 = (1 + var5 * 2) / 16.0f;
        final float var8 = 0.5f;
        return AxisAlignedBB.getBoundingBox((double)(par2 + var7), (double)par3, (double)(par4 + 0.0625f), (double)(par2 + 1 - 0.0625f), (double)(par3 + 0.5f), (double)(par4 + 1 - 0.0625f));
    }
    
    public IIcon getIcon(final int par1, final int par2) {
        return (par1 == 1) ? this.cheeseIcons[0] : ((par1 == 0) ? this.cheeseIcons[0] : ((par2 > 0 && par1 == 4) ? this.cheeseIcons[2] : this.cheeseIcons[1]));
    }
    
    public boolean renderAsNormalBlock() {
        return false;
    }
    
    public boolean isOpaqueCube() {
        return false;
    }
    
    public boolean onBlockActivated(final World par1World, final int par2, final int par3, final int par4, final EntityPlayer par5EntityPlayer, final int par6, final float par7, final float par8, final float par9) {
        this.eatCakeSlice(par1World, par2, par3, par4, par5EntityPlayer);
        return true;
    }
    
    public void onBlockClicked(final World par1World, final int par2, final int par3, final int par4, final EntityPlayer par5EntityPlayer) {
        this.eatCakeSlice(par1World, par2, par3, par4, par5EntityPlayer);
    }
    
    private void eatCakeSlice(final World par1World, final int par2, final int par3, final int par4, final EntityPlayer par5EntityPlayer) {
        if (par5EntityPlayer.canEat(false)) {
            par5EntityPlayer.getFoodStats().addStats(2, 0.1f);
            final int l = par1World.getBlockMetadata(par2, par3, par4) + 1;
            if (l >= 6) {
                par1World.setBlockToAir(par2, par3, par4);
            }
            else {
                par1World.setBlockMetadataWithNotify(par2, par3, par4, l, 2);
            }
        }
    }
    
    public boolean canPlaceBlockAt(final World par1World, final int par2, final int par3, final int par4) {
        return super.canPlaceBlockAt(par1World, par2, par3, par4) && this.canBlockStay(par1World, par2, par3, par4);
    }
    
    public void onNeighborBlockChange(final World par1World, final int par2, final int par3, final int par4, final Block par5) {
        if (!this.canBlockStay(par1World, par2, par3, par4)) {
            par1World.setBlockToAir(par2, par3, par4);
        }
    }
    
    public boolean canBlockStay(final World par1World, final int par2, final int par3, final int par4) {
        return par1World.getBlock(par2, par3 - 1, par4).getMaterial().isSolid();
    }
    
    public int quantityDropped(final Random par1Random) {
        return 0;
    }
    
    public Item getItemDropped(final int par1, final Random par2Random, final int par3) {
        return Item.getItemFromBlock(Blocks.air);
    }
    
    public String getShiftDescription(final int meta) {
        return GCCoreUtil.translate(this.getUnlocalizedName() + ".description");
    }
    
    public boolean showDescription(final int meta) {
        return true;
    }
}
