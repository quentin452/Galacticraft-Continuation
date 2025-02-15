package micdoodle8.mods.galacticraft.planets.asteroids.items;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import micdoodle8.mods.galacticraft.api.item.IHoldableItem;
import micdoodle8.mods.galacticraft.core.GalacticraftCore;
import micdoodle8.mods.galacticraft.core.dimension.WorldProviderSpaceStation;
import micdoodle8.mods.galacticraft.core.entities.player.GCPlayerStats;
import micdoodle8.mods.galacticraft.core.proxy.ClientProxyCore;
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil;
import micdoodle8.mods.galacticraft.planets.asteroids.ConfigManagerAsteroids;
import micdoodle8.mods.galacticraft.planets.asteroids.blocks.AsteroidBlocks;
import micdoodle8.mods.galacticraft.planets.asteroids.entities.EntityAstroMiner;
import micdoodle8.mods.galacticraft.planets.asteroids.tile.TileEntityMinerBase;

public class ItemAstroMiner extends Item implements IHoldableItem {

    public ItemAstroMiner(String assetName) {
        this.setMaxDamage(0);
        this.setMaxStackSize(1);
        this.setUnlocalizedName(assetName);
        this.setTextureName("arrow");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public EnumRarity getRarity(ItemStack par1ItemStack) {
        return ClientProxyCore.galacticraftItem;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public CreativeTabs getCreativeTab() {
        return GalacticraftCore.galacticraftItemsTab;
    }

    @Override
    public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, int par4,
        int par5, int par6, int par7, float par8, float par9, float par10) {
        TileEntity tile = null;

        if (par3World.isRemote || par2EntityPlayer == null) {
            return false;
        }
        final Block id = par3World.getBlock(par4, par5, par6);

        if (id == AsteroidBlocks.minerBaseFull) {
            tile = par3World.getTileEntity(par4, par5, par6);
        }

        if (tile instanceof TileEntityMinerBase) {
            if (par3World.provider instanceof WorldProviderSpaceStation) {
                par2EntityPlayer
                    .addChatMessage(new ChatComponentText(GCCoreUtil.translate("gui.message.astroMiner7.fail")));
                return false;
            }

            if (((TileEntityMinerBase) tile).getLinkedMiner() != null) {
                par2EntityPlayer
                    .addChatMessage(new ChatComponentText(GCCoreUtil.translate("gui.message.astroMiner.fail")));
                return false;
            }

            // Gives a chance for any loaded Astro Miner to link itself
            if (((TileEntityMinerBase) tile).ticks < 15) {
                return false;
            }

            final EntityPlayerMP playerMP = (EntityPlayerMP) par2EntityPlayer;

            final int astroCount = GCPlayerStats.get(playerMP).astroMinerCount;
            if (astroCount >= ConfigManagerAsteroids.astroMinerMax && !par2EntityPlayer.capabilities.isCreativeMode) {
                par2EntityPlayer
                    .addChatMessage(new ChatComponentText(GCCoreUtil.translate("gui.message.astroMiner2.fail")));
                return false;
            }

            if (!((TileEntityMinerBase) tile).spawnMiner(playerMP)) {
                par2EntityPlayer.addChatMessage(
                    new ChatComponentText(
                        GCCoreUtil.translate("gui.message.astroMiner1.fail") + " "
                            + GCCoreUtil.translate(EntityAstroMiner.blockingBlock.toString())));
                return false;
            }

            if (!par2EntityPlayer.capabilities.isCreativeMode) {
                GCPlayerStats.get(playerMP).astroMinerCount++;
                --par1ItemStack.stackSize;
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean shouldHoldLeftHandUp(EntityPlayer player) {
        return true;
    }

    @Override
    public boolean shouldHoldRightHandUp(EntityPlayer player) {
        return true;
    }

    @Override
    public boolean shouldCrouch(EntityPlayer player) {
        return true;
    }
}
