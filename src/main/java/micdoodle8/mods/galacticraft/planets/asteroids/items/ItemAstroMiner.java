package micdoodle8.mods.galacticraft.planets.asteroids.items;

import micdoodle8.mods.galacticraft.api.item.*;
import net.minecraft.item.*;
import micdoodle8.mods.galacticraft.core.proxy.*;
import cpw.mods.fml.relauncher.*;
import net.minecraft.creativetab.*;
import micdoodle8.mods.galacticraft.core.*;
import net.minecraft.world.*;
import micdoodle8.mods.galacticraft.planets.asteroids.blocks.*;
import micdoodle8.mods.galacticraft.planets.asteroids.tile.*;
import micdoodle8.mods.galacticraft.core.dimension.*;
import micdoodle8.mods.galacticraft.core.util.*;
import net.minecraft.util.*;
import net.minecraft.entity.player.*;
import micdoodle8.mods.galacticraft.core.entities.player.*;
import micdoodle8.mods.galacticraft.planets.asteroids.*;
import micdoodle8.mods.galacticraft.planets.asteroids.entities.*;
import net.minecraft.tileentity.*;
import net.minecraft.block.*;
import java.util.*;

public class ItemAstroMiner extends Item implements IHoldableItem
{
    public ItemAstroMiner(final String assetName) {
        this.setMaxDamage(0);
        this.setMaxStackSize(1);
        this.setUnlocalizedName(assetName);
        this.setTextureName("arrow");
    }
    
    @SideOnly(Side.CLIENT)
    public EnumRarity getRarity(final ItemStack par1ItemStack) {
        return ClientProxyCore.galacticraftItem;
    }
    
    @SideOnly(Side.CLIENT)
    public CreativeTabs getCreativeTab() {
        return GalacticraftCore.galacticraftItemsTab;
    }
    
    public boolean onItemUse(final ItemStack par1ItemStack, final EntityPlayer par2EntityPlayer, final World par3World, final int par4, final int par5, final int par6, final int par7, final float par8, final float par9, final float par10) {
        TileEntity tile = null;
        if (par3World.isRemote || par2EntityPlayer == null) {
            return false;
        }
        final Block id = par3World.getBlock(par4, par5, par6);
        if (id == AsteroidBlocks.minerBaseFull) {
            tile = par3World.getTileEntity(par4, par5, par6);
        }
        if (!(tile instanceof TileEntityMinerBase)) {
            return false;
        }
        if (par3World.provider instanceof WorldProviderSpaceStation) {
            par2EntityPlayer.addChatMessage((IChatComponent)new ChatComponentText(GCCoreUtil.translate("gui.message.astroMiner7.fail")));
            return false;
        }
        if (((TileEntityMinerBase)tile).getLinkedMiner() != null) {
            par2EntityPlayer.addChatMessage((IChatComponent)new ChatComponentText(GCCoreUtil.translate("gui.message.astroMiner.fail")));
            return false;
        }
        if (((TileEntityMinerBase)tile).ticks < 15) {
            return false;
        }
        final EntityPlayerMP playerMP = (EntityPlayerMP)par2EntityPlayer;
        final int astroCount = GCPlayerStats.get(playerMP).astroMinerCount;
        if (astroCount >= ConfigManagerAsteroids.astroMinerMax && !par2EntityPlayer.capabilities.isCreativeMode) {
            par2EntityPlayer.addChatMessage((IChatComponent)new ChatComponentText(GCCoreUtil.translate("gui.message.astroMiner2.fail")));
            return false;
        }
        if (!((TileEntityMinerBase)tile).spawnMiner(playerMP)) {
            par2EntityPlayer.addChatMessage((IChatComponent)new ChatComponentText(GCCoreUtil.translate("gui.message.astroMiner1.fail") + " " + GCCoreUtil.translate(EntityAstroMiner.blockingBlock.toString())));
            return false;
        }
        if (!par2EntityPlayer.capabilities.isCreativeMode) {
            final GCPlayerStats value = GCPlayerStats.get(playerMP);
            ++value.astroMinerCount;
            --par1ItemStack.stackSize;
        }
        return true;
    }
    
    @SideOnly(Side.CLIENT)
    public void addInformation(final ItemStack par1ItemStack, final EntityPlayer player, final List par2List, final boolean b) {
    }
    
    public boolean shouldHoldLeftHandUp(final EntityPlayer player) {
        return true;
    }
    
    public boolean shouldHoldRightHandUp(final EntityPlayer player) {
        return true;
    }
    
    public boolean shouldCrouch(final EntityPlayer player) {
        return true;
    }
}
