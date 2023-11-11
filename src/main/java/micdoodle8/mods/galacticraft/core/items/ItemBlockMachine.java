package micdoodle8.mods.galacticraft.core.items;

import net.minecraft.block.*;
import net.minecraft.item.*;
import micdoodle8.mods.galacticraft.core.proxy.*;
import cpw.mods.fml.relauncher.*;
import micdoodle8.mods.galacticraft.core.blocks.*;
import net.minecraft.world.*;
import net.minecraft.entity.player.*;
import net.minecraft.client.entity.*;

public class ItemBlockMachine extends ItemBlockDesc
{
    public ItemBlockMachine(final Block block) {
        super(block);
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
    }
    
    public int getMetadata(final int damage) {
        return damage;
    }
    
    @SideOnly(Side.CLIENT)
    public EnumRarity getRarity(final ItemStack par1ItemStack) {
        return ClientProxyCore.galacticraftItem;
    }
    
    public String getUnlocalizedName(final ItemStack itemstack) {
        int index = 0;
        final int typenum = itemstack.getItemDamage() & 0xC;
        if (this.field_150939_a == GCBlocks.machineBase) {
            index = typenum / 4;
        }
        else if (this.field_150939_a == GCBlocks.machineTiered) {
            if (typenum == 4) {
                return "tile.machine.2";
            }
            if (typenum == 0) {
                return "tile.machine.1";
            }
            if (typenum == 12) {
                return "tile.machine.7";
            }
            if (typenum == 8) {
                return "tile.machine.8";
            }
        }
        else if (typenum == 8) {
            index = 6;
        }
        else if (typenum == 4) {
            index = 5;
        }
        else if (typenum == 0) {
            index = 4;
        }
        return this.field_150939_a.getUnlocalizedName() + "." + index;
    }
    
    public void onCreated(final ItemStack stack, final World world, final EntityPlayer player) {
        if (!world.isRemote) {
            return;
        }
        final int typenum = stack.getItemDamage() & 0xC;
        if (player instanceof EntityPlayerSP) {
            if (this.field_150939_a == GCBlocks.machineBase && typenum == 12) {
                ClientProxyCore.playerClientHandler.onBuild(1, (EntityPlayerSP)player);
            }
            else if (this.field_150939_a == GCBlocks.machineBase2 && typenum == 4) {
                ClientProxyCore.playerClientHandler.onBuild(2, (EntityPlayerSP)player);
            }
        }
    }
    
    public String getUnlocalizedName() {
        return this.field_150939_a.getUnlocalizedName() + ".0";
    }
}
