package micdoodle8.mods.galacticraft.core.items;

import net.minecraft.block.*;
import micdoodle8.mods.galacticraft.core.blocks.*;
import net.minecraft.entity.player.*;
import micdoodle8.mods.galacticraft.core.util.*;
import net.minecraft.init.*;
import net.minecraft.world.*;
import net.minecraft.entity.*;
import appeng.api.*;
import appeng.api.util.*;
import net.minecraft.item.*;
import micdoodle8.mods.galacticraft.core.proxy.*;
import cpw.mods.fml.relauncher.*;

public class ItemBlockEnclosed extends ItemBlockDesc
{
    public ItemBlockEnclosed(final Block block) {
        super(block);
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
    }
    
    public String getUnlocalizedName(final ItemStack par1ItemStack) {
        String name = null;
        switch (par1ItemStack.getItemDamage()) {
            case 0: {
                name = "null";
                break;
            }
            case 1: {
                name = "oxygenPipe";
                break;
            }
            case 2: {
                name = "copperCable";
                break;
            }
            case 3: {
                name = "goldCable";
                break;
            }
            case 4: {
                name = "hvCable";
                break;
            }
            case 5: {
                name = "glassFibreCable";
                break;
            }
            case 6: {
                name = "lvCable";
                break;
            }
            case 13: {
                name = "meCable";
                break;
            }
            case 14: {
                name = "aluminumWire";
                break;
            }
            case 15: {
                name = "aluminumWireHeavy";
                break;
            }
            default: {
                try {
                    name = BlockEnclosed.getTypeFromMeta(par1ItemStack.getItemDamage()).getPipeType();
                }
                catch (Exception e) {
                    name = "null";
                }
                break;
            }
        }
        return this.field_150939_a.getUnlocalizedName() + "." + name;
    }
    
    public boolean onItemUse(final ItemStack itemstack, final EntityPlayer entityplayer, final World world, int i, int j, int k, int side, final float par8, final float par9, final float par10) {
        final int metadata = this.getMetadata(itemstack.getItemDamage());
        if (metadata != BlockEnclosed.EnumEnclosedBlock.ME_CABLE.getMetadata() || !CompatibilityManager.isAppEngLoaded()) {
            return super.onItemUse(itemstack, entityplayer, world, i, j, k, side, par8, par9, par10);
        }
        final int x = i;
        final int y = j;
        final int z = k;
        final Block block = world.getBlock(i, j, k);
        if (block == Blocks.snow_layer && (world.getBlockMetadata(i, j, k) & 0x7) < 1) {
            side = 1;
        }
        else if (block != Blocks.vine && block != Blocks.tallgrass && block != Blocks.deadbush && !block.isReplaceable((IBlockAccess)world, i, j, k)) {
            if (side == 0) {
                --j;
            }
            if (side == 1) {
                ++j;
            }
            if (side == 2) {
                --k;
            }
            if (side == 3) {
                ++k;
            }
            if (side == 4) {
                --i;
            }
            if (side == 5) {
                ++i;
            }
        }
        if (itemstack.stackSize == 0) {
            return false;
        }
        if (!entityplayer.canPlayerEdit(i, j, k, side, itemstack)) {
            return false;
        }
        if (j == 255 && this.field_150939_a.getMaterial().isSolid()) {
            return false;
        }
        if (!world.canPlaceEntityOnSide(block, i, j, k, false, side, (Entity)entityplayer, itemstack)) {
            return false;
        }
        final int j2 = this.field_150939_a.onBlockPlaced(world, i, j, k, side, par8, par9, par10, metadata);
        if (this.placeBlockAt(itemstack, entityplayer, world, i, j, k, side, par8, par9, par10, j2)) {
            world.playSoundEffect((double)(i + 0.5f), (double)(j + 0.5f), (double)(k + 0.5f), this.field_150939_a.stepSound.func_150496_b(), (this.field_150939_a.stepSound.getVolume() + 1.0f) / 2.0f, this.field_150939_a.stepSound.getPitch() * 0.8f);
            --itemstack.stackSize;
            final ItemStack itemME = AEApi.instance().definitions().parts().cableGlass().stack(AEColor.Transparent, 1);
            itemME.stackSize = 2;
            return AEApi.instance().partHelper().placeBus(itemME, x, y, z, side, entityplayer, world);
        }
        return true;
    }
    
    @SideOnly(Side.CLIENT)
    public EnumRarity getRarity(final ItemStack par1ItemStack) {
        return ClientProxyCore.galacticraftItem;
    }
    
    public int getMetadata(final int damage) {
        if (damage == 4) {
            return 0;
        }
        if (damage == 0) {
            return 4;
        }
        return damage;
    }
}
