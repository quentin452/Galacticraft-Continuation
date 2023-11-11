package micdoodle8.mods.galacticraft.planets.mars.items;

import micdoodle8.mods.galacticraft.core.items.*;
import micdoodle8.mods.galacticraft.api.item.*;
import net.minecraft.block.*;
import micdoodle8.mods.galacticraft.planets.mars.blocks.*;
import net.minecraft.item.*;
import micdoodle8.mods.galacticraft.core.proxy.*;
import cpw.mods.fml.relauncher.*;
import net.minecraft.entity.player.*;

public class ItemBlockMachine extends ItemBlockDesc implements IHoldableItem
{
    public ItemBlockMachine(final Block block) {
        super(block);
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
    }
    
    public int getMetadata(final int damage) {
        return damage;
    }
    
    public String getUnlocalizedName(final ItemStack itemstack) {
        int index = 0;
        final int typenum = itemstack.getItemDamage() & 0xC;
        if (this.field_150939_a == MarsBlocks.machine) {
            if (typenum == 8) {
                index = 2;
            }
            else if (typenum == 4) {
                index = 1;
            }
        }
        else if (this.field_150939_a == MarsBlocks.machineT2) {
            if (typenum == 0) {
                return "tile.marsMachine.4";
            }
            if (typenum == 4) {
                return "tile.marsMachine.5";
            }
            if (typenum == 8) {
                return "tile.marsMachine.6";
            }
        }
        return this.field_150939_a.getUnlocalizedName() + "." + index;
    }
    
    @SideOnly(Side.CLIENT)
    public EnumRarity getRarity(final ItemStack par1ItemStack) {
        return ClientProxyCore.galacticraftItem;
    }
    
    public String getUnlocalizedName() {
        return this.field_150939_a.getUnlocalizedName() + ".0";
    }
    
    public boolean shouldHoldLeftHandUp(final EntityPlayer player) {
        final ItemStack currentStack = player.getCurrentEquippedItem();
        return currentStack != null && this.field_150939_a == MarsBlocks.machine && currentStack.getItemDamage() >= 4 && currentStack.getItemDamage() < 8;
    }
    
    public boolean shouldHoldRightHandUp(final EntityPlayer player) {
        final ItemStack currentStack = player.getCurrentEquippedItem();
        return currentStack != null && this.field_150939_a == MarsBlocks.machine && currentStack.getItemDamage() >= 4 && currentStack.getItemDamage() < 8;
    }
    
    public boolean shouldCrouch(final EntityPlayer player) {
        return false;
    }
}
