package micdoodle8.mods.galacticraft.planets.mars.blocks;

import net.minecraft.block.*;
import micdoodle8.mods.galacticraft.core.items.*;
import net.minecraft.block.material.*;
import net.minecraft.client.renderer.texture.*;
import cpw.mods.fml.relauncher.*;
import net.minecraftforge.common.util.*;
import net.minecraft.world.*;
import net.minecraft.entity.player.*;
import micdoodle8.mods.galacticraft.planets.mars.tile.*;
import net.minecraft.tileentity.*;
import net.minecraft.stats.*;
import micdoodle8.mods.galacticraft.planets.mars.items.*;
import net.minecraft.enchantment.*;
import net.minecraft.entity.*;
import net.minecraft.nbt.*;
import micdoodle8.mods.galacticraft.planets.*;
import net.minecraft.creativetab.*;
import micdoodle8.mods.galacticraft.core.*;
import net.minecraft.item.*;
import java.util.*;
import net.minecraft.util.*;
import micdoodle8.mods.galacticraft.core.util.*;

public class BlockSlimelingEgg extends Block implements ITileEntityProvider, ItemBlockDesc.IBlockShiftDesc
{
    private IIcon[] icons;
    public static String[] names;
    
    public BlockSlimelingEgg() {
        super(Material.rock);
        this.setBlockBounds(0.17f, 0.0f, 0.11f, 0.83f, 0.7f, 0.89f);
    }
    
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(final IIconRegister iconRegister) {
        (this.icons = new IIcon[6])[0] = iconRegister.registerIcon("galacticraftmars:redEgg_0");
        this.icons[1] = iconRegister.registerIcon("galacticraftmars:blueEgg_0");
        this.icons[2] = iconRegister.registerIcon("galacticraftmars:yellowEgg_0");
        this.icons[3] = iconRegister.registerIcon("galacticraftmars:redEgg_1");
        this.icons[4] = iconRegister.registerIcon("galacticraftmars:blueEgg_1");
        this.icons[5] = iconRegister.registerIcon("galacticraftmars:yellowEgg_1");
        this.blockIcon = this.icons[0];
    }
    
    public boolean renderAsNormalBlock() {
        return false;
    }
    
    public boolean canBlockStay(final World par1World, final int par2, final int par3, final int par4) {
        final Block block = par1World.getBlock(par2, par3 - 1, par4);
        return block.isSideSolid((IBlockAccess)par1World, par2, par3, par4, ForgeDirection.UP);
    }
    
    private boolean beginHatch(final World world, final int x, final int y, final int z, final EntityPlayer player) {
        final int l = world.getBlockMetadata(x, y, z);
        if (l < 3) {
            world.setBlockMetadataWithNotify(x, y, z, l + 3, 2);
            final TileEntity tile = world.getTileEntity(x, y, z);
            if (tile instanceof TileEntitySlimelingEgg) {
                ((TileEntitySlimelingEgg)tile).timeToHatch = world.rand.nextInt(50) + 20;
                ((TileEntitySlimelingEgg)tile).lastTouchedPlayerUUID = (VersionUtil.mcVersion1_7_2 ? player.getCommandSenderName() : player.getUniqueID().toString());
                ((TileEntitySlimelingEgg)tile).lastTouchedPlayerName = player.getCommandSenderName();
            }
            return true;
        }
        return false;
    }
    
    public boolean removedByPlayer(final World world, final EntityPlayer player, final int x, final int y, final int z, final boolean willHarvest) {
        final ItemStack currentStack = player.getCurrentEquippedItem();
        if (currentStack != null && currentStack.getItem() instanceof ItemPickaxe) {
            return world.setBlockToAir(x, y, z);
        }
        if (player.capabilities.isCreativeMode) {
            return world.setBlockToAir(x, y, z);
        }
        this.beginHatch(world, x, y, z, player);
        return false;
    }
    
    public boolean onBlockActivated(final World world, final int x, final int y, final int z, final EntityPlayer player, final int side, final float hitX, final float hitY, final float hitZ) {
        return this.beginHatch(world, x, y, z, player);
    }
    
    public void harvestBlock(final World world, final EntityPlayer par2EntityPlayer, final int x, final int y, final int z, final int par6) {
        final ItemStack currentStack = par2EntityPlayer.getCurrentEquippedItem();
        if (currentStack != null && currentStack.getItem() instanceof ItemPickaxe) {
            par2EntityPlayer.addStat(StatList.mineBlockStatArray[Block.getIdFromBlock((Block)this)], 1);
            par2EntityPlayer.addExhaustion(0.025f);
            this.dropBlockAsItem(world, x, y, z, par6 % 3, 0);
            if (currentStack.getItem() == MarsItems.deshPickaxe && EnchantmentHelper.getSilkTouchModifier((EntityLivingBase)par2EntityPlayer)) {
                final ItemStack itemstack = new ItemStack(MarsItems.deshPickSlime, 1, currentStack.getItemDamage());
                if (currentStack.stackTagCompound != null) {
                    itemstack.stackTagCompound = (NBTTagCompound)currentStack.stackTagCompound.copy();
                }
                par2EntityPlayer.setCurrentItemOrArmor(0, itemstack);
            }
        }
    }
    
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(final int side, final int metadata) {
        return this.icons[metadata % 6];
    }
    
    public int getRenderType() {
        return GalacticraftPlanets.getBlockRenderID((Block)this);
    }
    
    @SideOnly(Side.CLIENT)
    public CreativeTabs getCreativeTabToDisplayOn() {
        return GalacticraftCore.galacticraftBlocksTab;
    }
    
    public boolean isOpaqueCube() {
        return false;
    }
    
    public Item getItemDropped(final int meta, final Random random, final int par3) {
        return Item.getItemFromBlock((Block)this);
    }
    
    public int damageDropped(final int meta) {
        return meta;
    }
    
    public int quantityDropped(final int meta, final int fortune, final Random random) {
        return 1;
    }
    
    @SideOnly(Side.CLIENT)
    public void getSubBlocks(final Item par1, final CreativeTabs par2CreativeTabs, final List par3List) {
        for (int var4 = 0; var4 < BlockSlimelingEgg.names.length; ++var4) {
            par3List.add(new ItemStack(par1, 1, var4));
        }
    }
    
    public TileEntity createNewTileEntity(final World world, final int meta) {
        return new TileEntitySlimelingEgg();
    }
    
    public ItemStack getPickBlock(final MovingObjectPosition target, final World world, final int x, final int y, final int z) {
        final int metadata = world.getBlockMetadata(x, y, z);
        if (metadata == 3) {
            return new ItemStack(Item.getItemFromBlock((Block)this), 1, 0);
        }
        if (metadata == 4) {
            return new ItemStack(Item.getItemFromBlock((Block)this), 1, 1);
        }
        if (metadata == 5) {
            return new ItemStack(Item.getItemFromBlock((Block)this), 1, 2);
        }
        return super.getPickBlock(target, world, x, y, z);
    }
    
    public String getShiftDescription(final int meta) {
        return GCCoreUtil.translate(this.getUnlocalizedName() + ".description");
    }
    
    public boolean showDescription(final int meta) {
        return true;
    }
    
    static {
        BlockSlimelingEgg.names = new String[] { "redEgg", "blueEgg", "yellowEgg" };
    }
}
