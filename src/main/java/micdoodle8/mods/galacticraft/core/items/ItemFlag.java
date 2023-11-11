package micdoodle8.mods.galacticraft.core.items;

import micdoodle8.mods.galacticraft.api.item.*;
import net.minecraft.creativetab.*;
import micdoodle8.mods.galacticraft.core.*;
import net.minecraft.world.*;
import net.minecraft.entity.player.*;
import micdoodle8.mods.galacticraft.core.entities.*;
import net.minecraft.entity.*;
import net.minecraft.block.*;
import micdoodle8.mods.galacticraft.core.util.*;
import net.minecraft.item.*;
import micdoodle8.mods.galacticraft.core.proxy.*;
import cpw.mods.fml.relauncher.*;
import net.minecraft.util.*;

public class ItemFlag extends Item implements IHoldableItem
{
    public int placeProgress;
    
    public ItemFlag(final String assetName) {
        this.setMaxDamage(0);
        this.setMaxStackSize(1);
        this.setUnlocalizedName(assetName);
        this.setTextureName("arrow");
    }
    
    public CreativeTabs getCreativeTab() {
        return GalacticraftCore.galacticraftItemsTab;
    }
    
    public void onPlayerStoppedUsing(final ItemStack par1ItemStack, final World par2World, final EntityPlayer par3EntityPlayer, final int par4) {
        final int useTime = this.getMaxItemUseDuration(par1ItemStack) - par4;
        boolean placed = false;
        final MovingObjectPosition var12 = this.getMovingObjectPositionFromPlayer(par2World, par3EntityPlayer, true);
        float var13 = useTime / 20.0f;
        var13 = (var13 * var13 + var13 * 2.0f) / 3.0f;
        if (var13 > 1.0f) {
            var13 = 1.0f;
        }
        if (var13 == 1.0f && var12 != null && var12.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
            final int x = var12.blockX;
            final int y = var12.blockY;
            final int z = var12.blockZ;
            if (!par2World.isRemote) {
                final EntityFlag flag = new EntityFlag(par2World, (double)(x + 0.5f), (double)(y + 1.0f), (double)(z + 0.5f), (int)(par3EntityPlayer.rotationYaw - 90.0f));
                if (par2World.getEntitiesWithinAABB((Class)EntityFlag.class, AxisAlignedBB.getBoundingBox((double)x, (double)y, (double)z, (double)(x + 1), (double)(y + 3), (double)(z + 1))).isEmpty()) {
                    par2World.spawnEntityInWorld((Entity)flag);
                    flag.setType(par1ItemStack.getItemDamage());
                    flag.setOwner(par3EntityPlayer.getGameProfile().getName());
                    par2World.playSoundEffect((double)x, (double)y, (double)z, Block.soundTypeMetal.getBreakSound(), Block.soundTypeMetal.getVolume(), Block.soundTypeMetal.getPitch() + 2.0f);
                    placed = true;
                }
                else {
                    par3EntityPlayer.addChatMessage((IChatComponent)new ChatComponentText(GCCoreUtil.translate("gui.flag.alreadyPlaced")));
                }
            }
            if (placed) {
                final int var14 = this.getInventorySlotContainItem(par3EntityPlayer, par1ItemStack);
                if (var14 >= 0 && !par3EntityPlayer.capabilities.isCreativeMode) {
                    final ItemStack itemStack = par3EntityPlayer.inventory.mainInventory[var14];
                    if (--itemStack.stackSize <= 0) {
                        par3EntityPlayer.inventory.mainInventory[var14] = null;
                    }
                }
            }
        }
    }
    
    private int getInventorySlotContainItem(final EntityPlayer player, final ItemStack stack) {
        for (int var2 = 0; var2 < player.inventory.mainInventory.length; ++var2) {
            if (player.inventory.mainInventory[var2] != null && player.inventory.mainInventory[var2].isItemEqual(stack)) {
                return var2;
            }
        }
        return -1;
    }
    
    public ItemStack onEaten(final ItemStack par1ItemStack, final World par2World, final EntityPlayer par3EntityPlayer) {
        return par1ItemStack;
    }
    
    public int getMaxItemUseDuration(final ItemStack par1ItemStack) {
        return 72000;
    }
    
    public EnumAction getItemUseAction(final ItemStack par1ItemStack) {
        return EnumAction.none;
    }
    
    public ItemStack onItemRightClick(final ItemStack par1ItemStack, final World par2World, final EntityPlayer par3EntityPlayer) {
        par3EntityPlayer.setItemInUse(par1ItemStack, this.getMaxItemUseDuration(par1ItemStack));
        return par1ItemStack;
    }
    
    @SideOnly(Side.CLIENT)
    public EnumRarity getRarity(final ItemStack par1ItemStack) {
        return ClientProxyCore.galacticraftItem;
    }
    
    public String getUnlocalizedName(final ItemStack itemStack) {
        return "item.flag";
    }
    
    public IIcon getIconFromDamage(final int damage) {
        return super.getIconFromDamage(damage);
    }
    
    public boolean shouldHoldLeftHandUp(final EntityPlayer player) {
        return false;
    }
    
    public boolean shouldHoldRightHandUp(final EntityPlayer player) {
        return true;
    }
    
    public boolean shouldCrouch(final EntityPlayer player) {
        return false;
    }
}
