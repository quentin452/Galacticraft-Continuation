package micdoodle8.mods.galacticraft.core.items;

import micdoodle8.mods.galacticraft.core.*;
import net.minecraft.creativetab.*;
import net.minecraft.item.*;
import micdoodle8.mods.galacticraft.core.proxy.*;
import cpw.mods.fml.relauncher.*;
import net.minecraft.entity.player.*;
import micdoodle8.mods.miccore.*;
import net.minecraft.entity.*;
import net.minecraft.world.*;
import net.minecraft.client.entity.*;
import net.minecraft.init.*;
import net.minecraftforge.common.util.*;
import net.minecraft.block.*;

public class ItemUniversalWrench extends Item
{
    public ItemUniversalWrench(final String assetName) {
        this.setUnlocalizedName(assetName);
        this.setMaxStackSize(1);
        this.setMaxDamage(256);
        this.setTextureName(GalacticraftCore.TEXTURE_PREFIX + assetName);
    }
    
    public CreativeTabs getCreativeTab() {
        return GalacticraftCore.galacticraftItemsTab;
    }
    
    @SideOnly(Side.CLIENT)
    public EnumRarity getRarity(final ItemStack par1ItemStack) {
        return ClientProxyCore.galacticraftItem;
    }
    
    @Annotations.RuntimeInterface(clazz = "buildcraft.api.tools.IToolWrench", modID = "BuildCraft|Core")
    public boolean canWrench(final EntityPlayer entityPlayer, final int x, final int y, final int z) {
        return true;
    }
    
    @Annotations.RuntimeInterface(clazz = "buildcraft.api.tools.IToolWrench", modID = "BuildCraft|Core")
    public void wrenchUsed(final EntityPlayer entityPlayer, final int x, final int y, final int z) {
        final ItemStack stack = entityPlayer.inventory.getCurrentItem();
        if (stack != null) {
            stack.damageItem(1, (EntityLivingBase)entityPlayer);
            if (stack.getItemDamage() >= stack.getMaxDamage()) {
                final ItemStack itemStack = stack;
                --itemStack.stackSize;
            }
            if (stack.stackSize <= 0) {
                entityPlayer.inventory.setInventorySlotContents(entityPlayer.inventory.currentItem, (ItemStack)null);
            }
        }
    }
    
    public boolean onItemUse(final ItemStack par1ItemStack, final EntityPlayer player, final World world, final int x, final int y, final int z, final int side, final float hitX, final float hitY, final float hitZ) {
        return false;
    }
    
    public boolean doesSneakBypassUse(final World world, final int x, final int y, final int z, final EntityPlayer player) {
        return true;
    }
    
    public void onCreated(final ItemStack stack, final World world, final EntityPlayer player) {
        if (world.isRemote && player instanceof EntityPlayerSP) {
            ClientProxyCore.playerClientHandler.onBuild(3, (EntityPlayerSP)player);
        }
    }
    
    public boolean onItemUseFirst(final ItemStack stack, final EntityPlayer entityPlayer, final World world, final int x, final int y, final int z, final int side, final float hitX, final float hitY, final float hitZ) {
        if (world.isRemote) {
            return false;
        }
        final Block blockID = world.getBlock(x, y, z);
        if (blockID == Blocks.furnace || blockID == Blocks.lit_furnace || blockID == Blocks.dropper || blockID == Blocks.hopper || blockID == Blocks.dispenser || blockID == Blocks.piston || blockID == Blocks.sticky_piston) {
            final int metadata = world.getBlockMetadata(x, y, z);
            int[] rotationMatrix = { 1, 2, 3, 4, 5, 0 };
            if (blockID == Blocks.furnace || blockID == Blocks.lit_furnace) {
                rotationMatrix = ForgeDirection.ROTATION_MATRIX[0];
            }
            world.setBlockMetadataWithNotify(x, y, z, ForgeDirection.getOrientation(rotationMatrix[metadata]).ordinal(), 3);
            this.wrenchUsed(entityPlayer, x, y, z);
            return true;
        }
        return false;
    }
}
