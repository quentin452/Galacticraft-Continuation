package micdoodle8.mods.galacticraft.core.items;

import net.minecraft.creativetab.*;
import micdoodle8.mods.galacticraft.core.*;
import net.minecraft.world.*;
import net.minecraft.nbt.*;
import net.minecraft.entity.player.*;
import java.util.*;
import cpw.mods.fml.relauncher.*;
import micdoodle8.mods.galacticraft.core.util.*;
import net.minecraft.item.*;
import micdoodle8.mods.galacticraft.core.proxy.*;
import micdoodle8.mods.galacticraft.core.entities.*;
import net.minecraft.entity.*;

public class ItemMeteorChunk extends Item
{
    public static final String[] names;
    public static final int METEOR_BURN_TIME = 900;
    
    public ItemMeteorChunk(final String assetName) {
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
        this.maxStackSize = 16;
        this.setCreativeTab(CreativeTabs.tabMaterials);
        this.setTextureName("arrow");
    }
    
    public CreativeTabs getCreativeTab() {
        return GalacticraftCore.galacticraftItemsTab;
    }
    
    public void onUpdate(final ItemStack itemstack, final World world, final Entity entity, final int par4, final boolean par5) {
        if (itemstack.getItemDamage() == 1 && !world.isRemote) {
            if (itemstack.hasTagCompound()) {
                float meteorBurnTime = itemstack.getTagCompound().getFloat("MeteorBurnTimeF");
                if (meteorBurnTime >= 0.5f) {
                    meteorBurnTime -= 0.5f;
                    itemstack.getTagCompound().setFloat("MeteorBurnTimeF", meteorBurnTime);
                }
                else {
                    itemstack.setItemDamage(0);
                    itemstack.stackTagCompound = null;
                }
            }
            else {
                itemstack.setTagCompound(new NBTTagCompound());
                itemstack.getTagCompound().setFloat("MeteorBurnTimeF", 900.0f);
            }
        }
    }
    
    public void onCreated(final ItemStack itemstack, final World world, final EntityPlayer entityPlayer) {
        super.onCreated(itemstack, world, entityPlayer);
        if (itemstack.getItemDamage() == 1) {
            if (!itemstack.hasTagCompound()) {
                itemstack.setTagCompound(new NBTTagCompound());
            }
            itemstack.getTagCompound().setFloat("MeteorBurnTimeF", 900.0f);
        }
    }
    
    @SideOnly(Side.CLIENT)
    public void getSubItems(final Item par1, final CreativeTabs par2CreativeTabs, final List par3List) {
        par3List.add(new ItemStack(par1, 1, 0));
        par3List.add(new ItemStack(par1, 1, 1));
    }
    
    @SideOnly(Side.CLIENT)
    public void addInformation(final ItemStack itemstack, final EntityPlayer par2EntityPlayer, final List par3List, final boolean par4) {
        if (itemstack.getItemDamage() > 0) {
            float burnTime = 0.0f;
            if (itemstack.hasTagCompound()) {
                final float meteorBurnTime = itemstack.getTagCompound().getFloat("MeteorBurnTimeF");
                burnTime = Math.round(meteorBurnTime / 10.0f) / 2.0f;
            }
            else {
                burnTime = 45.0f;
            }
            par3List.add(GCCoreUtil.translate("item.hotDescription.name") + " " + burnTime + GCCoreUtil.translate("gui.seconds"));
        }
    }
    
    @SideOnly(Side.CLIENT)
    public EnumRarity getRarity(final ItemStack par1ItemStack) {
        return ClientProxyCore.galacticraftItem;
    }
    
    public String getUnlocalizedName(final ItemStack itemStack) {
        return "item." + ItemMeteorChunk.names[itemStack.getItemDamage()];
    }
    
    public ItemStack onItemRightClick(final ItemStack itemStack, final World world, final EntityPlayer player) {
        if (!player.capabilities.isCreativeMode) {
            --itemStack.stackSize;
        }
        world.playSoundAtEntity((Entity)player, "random.bow", 1.0f, 1.0E-4f / (Item.itemRand.nextFloat() * 0.1f));
        if (!world.isRemote) {
            final EntityMeteorChunk meteor = new EntityMeteorChunk(world, (EntityLivingBase)player, 1.0f);
            if (itemStack.getItemDamage() > 0) {
                meteor.setFire(20);
                meteor.isHot = true;
            }
            meteor.canBePickedUp = (player.capabilities.isCreativeMode ? 2 : 1);
            world.spawnEntityInWorld((Entity)meteor);
        }
        return itemStack;
    }
    
    static {
        names = new String[] { "meteorChunk", "meteorChunkHot" };
    }
}
