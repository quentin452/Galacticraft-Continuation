package micdoodle8.mods.galacticraft.core.items;

import net.minecraft.block.*;
import net.minecraft.world.*;
import net.minecraft.entity.player.*;
import net.minecraft.client.entity.*;
import micdoodle8.mods.galacticraft.core.proxy.*;
import net.minecraft.item.*;
import cpw.mods.fml.relauncher.*;

public class ItemBlockLandingPad extends ItemBlockDesc
{
    public ItemBlockLandingPad(final Block block) {
        super(block);
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
    }
    
    public String getUnlocalizedName(final ItemStack par1ItemStack) {
        String name = "";
        switch (par1ItemStack.getItemDamage()) {
            case 0: {
                name = "landingPad";
                break;
            }
            case 1: {
                name = "buggyFueler";
                break;
            }
            case 2: {
                name = "cargoPad";
                break;
            }
        }
        return this.field_150939_a.getUnlocalizedName() + "." + name;
    }
    
    public void onCreated(final ItemStack stack, final World world, final EntityPlayer player) {
        if (world.isRemote && stack.getItemDamage() == 0 && player instanceof EntityPlayerSP) {
            ClientProxyCore.playerClientHandler.onBuild(5, (EntityPlayerSP)player);
        }
    }
    
    @SideOnly(Side.CLIENT)
    public EnumRarity getRarity(final ItemStack par1ItemStack) {
        return ClientProxyCore.galacticraftItem;
    }
    
    public int getMetadata(final int damage) {
        return damage;
    }
}
