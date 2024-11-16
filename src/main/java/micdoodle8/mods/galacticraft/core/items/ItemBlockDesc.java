package micdoodle8.mods.galacticraft.core.items;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import micdoodle8.mods.galacticraft.core.blocks.BlockAdvancedTile;
import micdoodle8.mods.galacticraft.core.blocks.BlockTileGC;
import micdoodle8.mods.galacticraft.core.blocks.GCBlocks;
import micdoodle8.mods.galacticraft.core.energy.EnergyDisplayHelper;
import micdoodle8.mods.galacticraft.core.energy.tile.TileBaseElectricBlock;
import micdoodle8.mods.galacticraft.core.proxy.ClientProxyCore;
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil;

public class ItemBlockDesc extends ItemBlockGC {

    public interface IBlockShiftDesc {

        String getShiftDescription(int meta);

        boolean showDescription(int meta);
    }

    public ItemBlockDesc(Block block) {
        super(block);
    }

    @Override
    public void onCreated(ItemStack stack, World world, EntityPlayer player) {
        if (!world.isRemote) {
            return;
        }

        // The player could be a FakePlayer made by another mod e.g. LogisticsPipes
        if (player instanceof EntityPlayerSP) {
            if (this.field_150939_a == GCBlocks.fuelLoader) {
                ClientProxyCore.playerClientHandler.onBuild(4, (EntityPlayerSP) player);
            } else if (this.field_150939_a == GCBlocks.fuelLoader) {
                ClientProxyCore.playerClientHandler.onBuild(6, (EntityPlayerSP) player);
            }
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> info, boolean advanced) {
        if (this.field_150939_a instanceof IBlockShiftDesc
            && ((IBlockShiftDesc) this.field_150939_a).showDescription(stack.getItemDamage())) {
            if (GameSettings.isKeyDown(Minecraft.getMinecraft().gameSettings.keyBindSneak)) {
                info.addAll(
                    FMLClientHandler.instance()
                        .getClient().fontRenderer.listFormattedStringToWidth(
                            ((IBlockShiftDesc) this.field_150939_a).getShiftDescription(stack.getItemDamage()),
                            150));
            } else {
                if (this.field_150939_a instanceof BlockTileGC) {
                    final TileEntity te = this.field_150939_a.createTileEntity(null, stack.getItemDamage() & 12);
                    if (te instanceof TileBaseElectricBlock) {
                        final float powerDrawn = ((TileBaseElectricBlock) te).storage.getMaxExtract();
                        if (powerDrawn > 0) {
                            info.add(
                                EnumChatFormatting.GREEN + GCCoreUtil.translateWithFormat(
                                    "itemDesc.powerdraw.name",
                                    EnergyDisplayHelper.getEnergyDisplayS(powerDrawn * 20)));
                        }
                    }
                } else if (this.field_150939_a instanceof BlockAdvancedTile) {
                    final TileEntity te = this.field_150939_a
                        .createTileEntity(player.worldObj, stack.getItemDamage() & 12);
                    if (te instanceof TileBaseElectricBlock) {
                        final float powerDrawn = ((TileBaseElectricBlock) te).storage.getMaxExtract();
                        if (powerDrawn > 0) {
                            info.add(
                                EnumChatFormatting.GREEN + GCCoreUtil.translateWithFormat(
                                    "itemDesc.powerdraw.name",
                                    EnergyDisplayHelper.getEnergyDisplayS(powerDrawn * 20)));
                        }
                    }
                }
                info.add(
                    GCCoreUtil.translateWithFormat(
                        "itemDesc.shift.name",
                        GameSettings.getKeyDisplayString(
                            FMLClientHandler.instance()
                                .getClient().gameSettings.keyBindSneak.getKeyCode())));
            }
        }
    }
}
