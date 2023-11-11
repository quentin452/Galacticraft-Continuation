package micdoodle8.mods.galacticraft.core.items;

import net.minecraft.block.*;
import net.minecraft.item.*;
import net.minecraft.world.*;
import net.minecraft.entity.player.*;
import net.minecraft.client.entity.*;
import micdoodle8.mods.galacticraft.core.proxy.*;
import org.lwjgl.input.*;
import cpw.mods.fml.client.*;
import java.util.*;
import micdoodle8.mods.galacticraft.core.energy.tile.*;
import net.minecraft.util.*;
import micdoodle8.mods.galacticraft.core.energy.*;
import micdoodle8.mods.galacticraft.core.util.*;
import micdoodle8.mods.galacticraft.core.blocks.*;
import net.minecraft.client.settings.*;
import net.minecraft.tileentity.*;
import cpw.mods.fml.relauncher.*;

public class ItemBlockDesc extends ItemBlockGC
{
    public ItemBlockDesc(final Block block) {
        super(block);
    }
    
    public void onCreated(final ItemStack stack, final World world, final EntityPlayer player) {
        if (!world.isRemote) {
            return;
        }
        if (player instanceof EntityPlayerSP) {
            if (this.field_150939_a == GCBlocks.fuelLoader) {
                ClientProxyCore.playerClientHandler.onBuild(4, (EntityPlayerSP)player);
            }
            else if (this.field_150939_a == GCBlocks.fuelLoader) {
                ClientProxyCore.playerClientHandler.onBuild(6, (EntityPlayerSP)player);
            }
        }
    }
    
    @SideOnly(Side.CLIENT)
    public void addInformation(final ItemStack stack, final EntityPlayer player, final List info, final boolean advanced) {
        if (this.field_150939_a instanceof IBlockShiftDesc && ((IBlockShiftDesc)this.field_150939_a).showDescription(stack.getItemDamage())) {
            if (Keyboard.isKeyDown(42)) {
                info.addAll(FMLClientHandler.instance().getClient().fontRenderer.listFormattedStringToWidth(((IBlockShiftDesc)this.field_150939_a).getShiftDescription(stack.getItemDamage()), 150));
            }
            else {
                if (this.field_150939_a instanceof BlockTileGC) {
                    final TileEntity te = ((BlockTileGC)this.field_150939_a).createTileEntity((World)null, stack.getItemDamage() & 0xC);
                    if (te instanceof TileBaseElectricBlock) {
                        final float powerDrawn = ((TileBaseElectricBlock)te).storage.getMaxExtract();
                        if (powerDrawn > 0.0f) {
                            info.add(EnumChatFormatting.GREEN + GCCoreUtil.translateWithFormat("itemDesc.powerdraw.name", EnergyDisplayHelper.getEnergyDisplayS(powerDrawn * 20.0f)));
                        }
                    }
                }
                else if (this.field_150939_a instanceof BlockAdvancedTile) {
                    final TileEntity te = ((BlockAdvancedTile)this.field_150939_a).createTileEntity(player.worldObj, stack.getItemDamage() & 0xC);
                    if (te instanceof TileBaseElectricBlock) {
                        final float powerDrawn = ((TileBaseElectricBlock)te).storage.getMaxExtract();
                        if (powerDrawn > 0.0f) {
                            info.add(EnumChatFormatting.GREEN + GCCoreUtil.translateWithFormat("itemDesc.powerdraw.name", EnergyDisplayHelper.getEnergyDisplayS(powerDrawn * 20.0f)));
                        }
                    }
                }
                info.add(GCCoreUtil.translateWithFormat("itemDesc.shift.name", GameSettings.getKeyDisplayString(FMLClientHandler.instance().getClient().gameSettings.keyBindSneak.getKeyCode())));
            }
        }
    }
    
    public interface IBlockShiftDesc
    {
        String getShiftDescription(final int p0);
        
        boolean showDescription(final int p0);
    }
}
