package micdoodle8.mods.galacticraft.planets.asteroids.items;

import net.minecraft.creativetab.*;
import micdoodle8.mods.galacticraft.core.*;
import cpw.mods.fml.relauncher.*;
import micdoodle8.mods.galacticraft.core.proxy.*;
import net.minecraft.world.*;
import net.minecraft.entity.player.*;
import net.minecraft.enchantment.*;
import net.minecraft.init.*;
import micdoodle8.mods.galacticraft.planets.asteroids.entities.*;
import net.minecraft.entity.*;
import micdoodle8.mods.galacticraft.core.util.*;
import net.minecraft.util.*;
import net.minecraft.item.*;
import net.minecraft.client.renderer.texture.*;

public class ItemGrappleHook extends ItemBow
{
    public ItemGrappleHook(final String assetName) {
        this.setUnlocalizedName(assetName);
        this.setMaxStackSize(1);
        this.setTextureName("arrow");
    }
    
    @SideOnly(Side.CLIENT)
    public CreativeTabs getCreativeTab() {
        return GalacticraftCore.galacticraftItemsTab;
    }
    
    @SideOnly(Side.CLIENT)
    public EnumRarity getRarity(final ItemStack par1ItemStack) {
        return ClientProxyCore.galacticraftItem;
    }
    
    public void onPlayerStoppedUsing(final ItemStack par1ItemStack, final World par2World, final EntityPlayer par3EntityPlayer, final int par4) {
        final boolean flag = par3EntityPlayer.capabilities.isCreativeMode || EnchantmentHelper.getEnchantmentLevel(Enchantment.infinity.effectId, par1ItemStack) > 0;
        if (flag || par3EntityPlayer.inventory.hasItem(Items.string)) {
            final EntityGrapple grapple = new EntityGrapple(par2World, par3EntityPlayer, 2.0f);
            par2World.playSoundAtEntity((Entity)par3EntityPlayer, "random.bow", 1.0f, 1.0f / (Item.itemRand.nextFloat() * 0.4f + 1.2f) + 0.5f);
            if (!par2World.isRemote) {
                par2World.spawnEntityInWorld((Entity)grapple);
            }
            par1ItemStack.damageItem(1, (EntityLivingBase)par3EntityPlayer);
            grapple.canBePickedUp = (par3EntityPlayer.capabilities.isCreativeMode ? 2 : 1);
            if (!par3EntityPlayer.capabilities.isCreativeMode) {
                par3EntityPlayer.inventory.consumeInventoryItem(Items.string);
            }
        }
        else if (par2World.isRemote) {
            par3EntityPlayer.addChatMessage((IChatComponent)new ChatComponentText(GCCoreUtil.translate("gui.message.grapple.fail")));
        }
    }
    
    public ItemStack onEaten(final ItemStack par1ItemStack, final World par2World, final EntityPlayer par3EntityPlayer) {
        return par1ItemStack;
    }
    
    public int getMaxItemUseDuration(final ItemStack par1ItemStack) {
        return 72000;
    }
    
    public EnumAction getItemUseAction(final ItemStack par1ItemStack) {
        return EnumAction.bow;
    }
    
    public ItemStack onItemRightClick(final ItemStack par1ItemStack, final World par2World, final EntityPlayer par3EntityPlayer) {
        par3EntityPlayer.setItemInUse(par1ItemStack, this.getMaxItemUseDuration(par1ItemStack));
        return par1ItemStack;
    }
    
    @SideOnly(Side.CLIENT)
    public void registerIcons(final IIconRegister iconRegister) {
        this.itemIcon = iconRegister.registerIcon("arrow");
    }
}
