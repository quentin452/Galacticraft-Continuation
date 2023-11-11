package micdoodle8.mods.galacticraft.core.items;

import micdoodle8.mods.galacticraft.api.item.*;
import net.minecraft.creativetab.*;
import micdoodle8.mods.galacticraft.core.*;
import net.minecraft.entity.player.*;
import net.minecraft.world.*;
import net.minecraft.client.entity.*;
import micdoodle8.mods.galacticraft.core.proxy.*;
import micdoodle8.mods.galacticraft.core.blocks.*;
import micdoodle8.mods.galacticraft.core.tile.*;
import micdoodle8.mods.galacticraft.api.entity.*;
import micdoodle8.mods.galacticraft.core.entities.*;
import net.minecraft.entity.*;
import net.minecraftforge.fluids.*;
import net.minecraft.tileentity.*;
import net.minecraft.block.*;
import java.util.*;
import net.minecraft.item.*;
import cpw.mods.fml.relauncher.*;
import micdoodle8.mods.galacticraft.core.util.*;
import cpw.mods.fml.client.*;

public class ItemTier1Rocket extends Item implements IHoldableItem
{
    public ItemTier1Rocket(final String assetName) {
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
        this.setMaxStackSize(1);
        this.setTextureName("arrow");
        this.setUnlocalizedName(assetName);
    }
    
    public CreativeTabs getCreativeTab() {
        return GalacticraftCore.galacticraftItemsTab;
    }
    
    public boolean onItemUse(ItemStack par1ItemStack, final EntityPlayer par2EntityPlayer, final World par3World, final int par4, final int par5, final int par6, final int par7, final float par8, final float par9, final float par10) {
        boolean padFound = false;
        TileEntity tile = null;
        if (par3World.isRemote && par2EntityPlayer instanceof EntityPlayerSP) {
            ClientProxyCore.playerClientHandler.onBuild(8, (EntityPlayerSP)par2EntityPlayer);
            return false;
        }
        float centerX = -1.0f;
        float centerY = -1.0f;
        float centerZ = -1.0f;
        for (int i = -1; i < 2; ++i) {
            for (int j = -1; j < 2; ++j) {
                final Block id = par3World.getBlock(par4 + i, par5, par6 + j);
                final int meta = par3World.getBlockMetadata(par4 + i, par5, par6 + j);
                if (id == GCBlocks.landingPadFull && meta == 0) {
                    padFound = true;
                    tile = par3World.getTileEntity(par4 + i, par5, par6 + j);
                    centerX = par4 + i + 0.5f;
                    centerY = par5 + 0.4f;
                    centerZ = par6 + j + 0.5f;
                    break;
                }
            }
            if (padFound) {
                break;
            }
        }
        if (!padFound) {
            return false;
        }
        if (!(tile instanceof TileEntityLandingPad)) {
            return false;
        }
        if (((TileEntityLandingPad)tile).getDockedEntity() != null) {
            return false;
        }
        final EntityTier1Rocket spaceship = new EntityTier1Rocket(par3World, (double)centerX, (double)centerY, (double)centerZ, IRocketType.EnumRocketType.values()[par1ItemStack.getItemDamage()]);
        spaceship.setPosition(spaceship.posX, spaceship.posY + spaceship.getOnPadYOffset(), spaceship.posZ);
        par3World.spawnEntityInWorld((Entity)spaceship);
        if (par1ItemStack.hasTagCompound() && par1ItemStack.getTagCompound().hasKey("RocketFuel")) {
            spaceship.fuelTank.fill(new FluidStack(GalacticraftCore.fluidFuel, par1ItemStack.getTagCompound().getInteger("RocketFuel")), true);
        }
        if (!par2EntityPlayer.capabilities.isCreativeMode) {
            final ItemStack itemStack = par1ItemStack;
            --itemStack.stackSize;
            if (par1ItemStack.stackSize <= 0) {
                par1ItemStack = null;
            }
        }
        if (spaceship.rocketType.getPreFueled()) {
            spaceship.fuelTank.fill(new FluidStack(GalacticraftCore.fluidFuel, spaceship.getMaxFuel()), true);
        }
        return true;
    }
    
    public void getSubItems(final Item par1, final CreativeTabs par2CreativeTabs, final List par3List) {
        for (int i = 0; i < IRocketType.EnumRocketType.values().length; ++i) {
            par3List.add(new ItemStack(par1, 1, i));
        }
    }
    
    @SideOnly(Side.CLIENT)
    public EnumRarity getRarity(final ItemStack par1ItemStack) {
        return ClientProxyCore.galacticraftItem;
    }
    
    @SideOnly(Side.CLIENT)
    public void addInformation(final ItemStack par1ItemStack, final EntityPlayer player, final List par2List, final boolean b) {
        final IRocketType.EnumRocketType type = IRocketType.EnumRocketType.values()[par1ItemStack.getItemDamage()];
        if (!type.getTooltip().isEmpty()) {
            par2List.add(type.getTooltip());
        }
        if (type.getPreFueled()) {
            par2List.add(EnumColor.RED + "�o" + GCCoreUtil.translate("gui.creativeOnly.desc"));
        }
        if (par1ItemStack.hasTagCompound() && par1ItemStack.getTagCompound().hasKey("RocketFuel")) {
            final EntityTier1Rocket rocket = new EntityTier1Rocket((World)FMLClientHandler.instance().getWorldClient(), 0.0, 0.0, 0.0, IRocketType.EnumRocketType.values()[par1ItemStack.getItemDamage()]);
            par2List.add(GCCoreUtil.translate("gui.message.fuel.name") + ": " + par1ItemStack.getTagCompound().getInteger("RocketFuel") + " / " + rocket.fuelTank.getCapacity());
        }
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
