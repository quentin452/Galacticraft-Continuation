package micdoodle8.mods.galacticraft.core.items;

import micdoodle8.mods.galacticraft.core.*;
import net.minecraft.creativetab.*;
import micdoodle8.mods.galacticraft.core.proxy.*;
import cpw.mods.fml.relauncher.*;
import net.minecraft.client.renderer.texture.*;
import java.util.*;
import net.minecraft.entity.player.*;
import micdoodle8.mods.galacticraft.core.util.*;
import net.minecraft.world.*;
import net.minecraft.entity.*;
import net.minecraft.item.*;
import net.minecraft.nbt.*;
import net.minecraft.util.*;

public class ItemBasic extends Item
{
    public static final String[] names;
    public static final int WAFER_BASIC = 13;
    public static final int WAFER_ADVANCED = 14;
    protected IIcon[] icons;
    
    public ItemBasic(final String assetName) {
        this.icons = new IIcon[ItemBasic.names.length];
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
        this.setUnlocalizedName(assetName);
        this.setTextureName(GalacticraftCore.TEXTURE_PREFIX + assetName);
    }
    
    public CreativeTabs getCreativeTab() {
        return GalacticraftCore.galacticraftItemsTab;
    }
    
    @SideOnly(Side.CLIENT)
    public EnumRarity getRarity(final ItemStack par1ItemStack) {
        return ClientProxyCore.galacticraftItem;
    }
    
    @SideOnly(Side.CLIENT)
    public void registerIcons(final IIconRegister iconRegister) {
        int i = 0;
        for (final String name : ItemBasic.names) {
            this.icons[i++] = iconRegister.registerIcon(this.getIconString() + "." + name);
        }
    }
    
    public String getUnlocalizedName(final ItemStack itemStack) {
        if (itemStack.getItemDamage() > 14 && itemStack.getItemDamage() < 19) {
            return this.getUnlocalizedName() + ".cannedFood";
        }
        return this.getUnlocalizedName() + "." + ItemBasic.names[itemStack.getItemDamage()];
    }
    
    public IIcon getIconFromDamage(final int damage) {
        if (this.icons.length > damage) {
            return this.icons[damage];
        }
        return super.getIconFromDamage(damage);
    }
    
    public void getSubItems(final Item par1, final CreativeTabs par2CreativeTabs, final List par3List) {
        for (int i = 0; i < ItemBasic.names.length; ++i) {
            par3List.add(new ItemStack(par1, 1, i));
        }
    }
    
    public int getMetadata(final int par1) {
        return par1;
    }
    
    @SideOnly(Side.CLIENT)
    public void addInformation(final ItemStack par1ItemStack, final EntityPlayer par2EntityPlayer, final List par3List, final boolean par4) {
        if (par1ItemStack.getItemDamage() > 14 && par1ItemStack.getItemDamage() < 19) {
            par3List.add(EnumColor.BRIGHT_GREEN + GCCoreUtil.translate(this.getUnlocalizedName() + "." + ItemBasic.names[par1ItemStack.getItemDamage()] + ".name"));
        }
        else if (par1ItemStack.getItemDamage() == 19) {
            par3List.add(EnumColor.AQUA + GCCoreUtil.translate("gui.frequencyModule.desc.0"));
            par3List.add(EnumColor.AQUA + GCCoreUtil.translate("gui.frequencyModule.desc.1"));
        }
    }
    
    public int getHealAmount(final ItemStack par1ItemStack) {
        switch (par1ItemStack.getItemDamage()) {
            case 15: {
                return 8;
            }
            case 16: {
                return 8;
            }
            case 17: {
                return 4;
            }
            case 18: {
                return 2;
            }
            default: {
                return 0;
            }
        }
    }
    
    public float getSaturationModifier(final ItemStack par1ItemStack) {
        switch (par1ItemStack.getItemDamage()) {
            case 15: {
                return 0.3f;
            }
            case 16: {
                return 0.6f;
            }
            case 17: {
                return 0.3f;
            }
            case 18: {
                return 0.3f;
            }
            default: {
                return 0.0f;
            }
        }
    }
    
    public ItemStack onEaten(final ItemStack par1ItemStack, final World par2World, final EntityPlayer par3EntityPlayer) {
        if (par1ItemStack.getItemDamage() > 14 && par1ItemStack.getItemDamage() < 19) {
            --par1ItemStack.stackSize;
            par3EntityPlayer.getFoodStats().addStats(this.getHealAmount(par1ItemStack), this.getSaturationModifier(par1ItemStack));
            par2World.playSoundAtEntity((Entity)par3EntityPlayer, "random.burp", 0.5f, par2World.rand.nextFloat() * 0.1f + 0.9f);
            if (!par2World.isRemote) {
                par3EntityPlayer.entityDropItem(new ItemStack(GCItems.canister, 1, 0), 0.0f);
            }
            return par1ItemStack;
        }
        return super.onEaten(par1ItemStack, par2World, par3EntityPlayer);
    }
    
    public int getMaxItemUseDuration(final ItemStack par1ItemStack) {
        if (par1ItemStack.getItemDamage() > 14 && par1ItemStack.getItemDamage() < 19) {
            return 32;
        }
        return super.getMaxItemUseDuration(par1ItemStack);
    }
    
    public EnumAction getItemUseAction(final ItemStack par1ItemStack) {
        if (par1ItemStack.getItemDamage() > 14 && par1ItemStack.getItemDamage() < 19) {
            return EnumAction.eat;
        }
        return super.getItemUseAction(par1ItemStack);
    }
    
    public ItemStack onItemRightClick(final ItemStack par1ItemStack, final World par2World, final EntityPlayer par3EntityPlayer) {
        if (par1ItemStack.getItemDamage() > 14 && par1ItemStack.getItemDamage() < 19 && par3EntityPlayer.canEat(false)) {
            par3EntityPlayer.setItemInUse(par1ItemStack, this.getMaxItemUseDuration(par1ItemStack));
        }
        return par1ItemStack;
    }
    
    public boolean onLeftClickEntity(final ItemStack itemStack, final EntityPlayer player, final Entity entity) {
        if (itemStack.getItemDamage() != 19) {
            return false;
        }
        if (!player.worldObj.isRemote && entity != null && !(entity instanceof EntityPlayer)) {
            if (itemStack.stackTagCompound == null) {
                itemStack.setTagCompound(new NBTTagCompound());
            }
            itemStack.stackTagCompound.setLong("linkedUUIDMost", entity.getUniqueID().getMostSignificantBits());
            itemStack.stackTagCompound.setLong("linkedUUIDLeast", entity.getUniqueID().getLeastSignificantBits());
            player.addChatMessage((IChatComponent)new ChatComponentText(GCCoreUtil.translate("gui.tracking.message")));
            return true;
        }
        return false;
    }
    
    static {
        names = new String[] { "solar_module_0", "solar_module_1", "rawSilicon", "ingotCopper", "ingotTin", "ingotAluminum", "compressedCopper", "compressedTin", "compressedAluminum", "compressedSteel", "compressedBronze", "compressedIron", "waferSolar", "waferBasic", "waferAdvanced", "dehydratedApple", "dehydratedCarrot", "dehydratedMelon", "dehydratedPotato", "frequencyModule", "ambientThermalController" };
    }
}
