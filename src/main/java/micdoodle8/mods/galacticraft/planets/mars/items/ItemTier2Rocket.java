package micdoodle8.mods.galacticraft.planets.mars.items;

import micdoodle8.mods.galacticraft.api.item.*;
import net.minecraft.item.*;
import micdoodle8.mods.galacticraft.core.proxy.*;
import cpw.mods.fml.relauncher.*;
import net.minecraft.creativetab.*;
import micdoodle8.mods.galacticraft.core.*;
import net.minecraft.entity.player.*;
import net.minecraft.world.*;
import micdoodle8.mods.galacticraft.core.blocks.*;
import micdoodle8.mods.galacticraft.core.tile.*;
import micdoodle8.mods.galacticraft.api.entity.*;
import micdoodle8.mods.galacticraft.planets.mars.entities.*;
import net.minecraft.entity.*;
import net.minecraftforge.fluids.*;
import net.minecraft.tileentity.*;
import net.minecraft.block.*;
import micdoodle8.mods.galacticraft.api.prefab.entity.*;
import java.util.*;
import micdoodle8.mods.galacticraft.core.util.*;
import cpw.mods.fml.client.*;
import net.minecraft.client.renderer.texture.*;

public class ItemTier2Rocket extends Item implements IHoldableItem
{
    public ItemTier2Rocket() {
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
        this.setMaxStackSize(1);
    }
    
    @SideOnly(Side.CLIENT)
    public EnumRarity getRarity(final ItemStack par1ItemStack) {
        return ClientProxyCore.galacticraftItem;
    }
    
    @SideOnly(Side.CLIENT)
    public CreativeTabs getCreativeTab() {
        return GalacticraftCore.galacticraftItemsTab;
    }
    
    public boolean onItemUse(ItemStack par1ItemStack, final EntityPlayer par2EntityPlayer, final World par3World, final int par4, final int par5, final int par6, final int par7, final float par8, final float par9, final float par10) {
        boolean padFound = false;
        TileEntity tile = null;
        if (par3World.isRemote) {
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
        EntityAutoRocket rocket;
        if (par1ItemStack.getItemDamage() < 10) {
            rocket = (EntityAutoRocket)new EntityTier2Rocket(par3World, (double)centerX, (double)centerY, (double)centerZ, IRocketType.EnumRocketType.values()[par1ItemStack.getItemDamage()]);
        }
        else {
            rocket = (EntityAutoRocket)new EntityCargoRocket(par3World, (double)centerX, (double)centerY, (double)centerZ, IRocketType.EnumRocketType.values()[par1ItemStack.getItemDamage() - 10]);
        }
        rocket.setPosition(rocket.posX, rocket.posY + rocket.getOnPadYOffset(), rocket.posZ);
        par3World.spawnEntityInWorld((Entity)rocket);
        if (par1ItemStack.hasTagCompound() && par1ItemStack.getTagCompound().hasKey("RocketFuel")) {
            rocket.fuelTank.fill(new FluidStack(GalacticraftCore.fluidFuel, par1ItemStack.getTagCompound().getInteger("RocketFuel")), true);
        }
        if (!par2EntityPlayer.capabilities.isCreativeMode) {
            final ItemStack itemStack = par1ItemStack;
            --itemStack.stackSize;
            if (par1ItemStack.stackSize <= 0) {
                par1ItemStack = null;
            }
        }
        if (((IRocketType)rocket).getType().getPreFueled()) {
            if (rocket instanceof EntityTieredRocket) {
                ((EntityTieredRocket)rocket).fuelTank.fill(new FluidStack(GalacticraftCore.fluidFuel, rocket.getMaxFuel()), true);
            }
            else {
                ((EntityCargoRocket)rocket).fuelTank.fill(new FluidStack(GalacticraftCore.fluidFuel, rocket.getMaxFuel()), true);
            }
        }
        return true;
    }
    
    public void getSubItems(final Item par1, final CreativeTabs par2CreativeTabs, final List par3List) {
        for (int i = 0; i < IRocketType.EnumRocketType.values().length; ++i) {
            par3List.add(new ItemStack(par1, 1, i));
        }
        for (int i = 11; i < 10 + IRocketType.EnumRocketType.values().length; ++i) {
            par3List.add(new ItemStack(par1, 1, i));
        }
    }
    
    @SideOnly(Side.CLIENT)
    public void addInformation(final ItemStack par1ItemStack, final EntityPlayer player, final List par2List, final boolean b) {
        IRocketType.EnumRocketType type = null;
        if (par1ItemStack.getItemDamage() < 10) {
            type = IRocketType.EnumRocketType.values()[par1ItemStack.getItemDamage()];
        }
        else {
            type = IRocketType.EnumRocketType.values()[par1ItemStack.getItemDamage() - 10];
        }
        if (!type.getTooltip().isEmpty()) {
            par2List.add(type.getTooltip());
        }
        if (type.getPreFueled()) {
            par2List.add(EnumColor.RED + "�o" + GCCoreUtil.translate("gui.creativeOnly.desc"));
        }
        if (par1ItemStack.hasTagCompound() && par1ItemStack.getTagCompound().hasKey("RocketFuel")) {
            EntityAutoRocket rocket;
            if (par1ItemStack.getItemDamage() < 10) {
                rocket = (EntityAutoRocket)new EntityTier2Rocket((World)FMLClientHandler.instance().getWorldClient(), 0.0, 0.0, 0.0, IRocketType.EnumRocketType.values()[par1ItemStack.getItemDamage()]);
            }
            else {
                rocket = (EntityAutoRocket)new EntityCargoRocket((World)FMLClientHandler.instance().getWorldClient(), 0.0, 0.0, 0.0, IRocketType.EnumRocketType.values()[par1ItemStack.getItemDamage() - 10]);
            }
            par2List.add(GCCoreUtil.translate("gui.message.fuel.name") + ": " + par1ItemStack.getTagCompound().getInteger("RocketFuel") + " / " + rocket.fuelTank.getCapacity());
        }
        if (par1ItemStack.getItemDamage() >= 10) {
            par2List.add(EnumColor.AQUA + GCCoreUtil.translate("gui.requiresController.desc"));
        }
    }
    
    public String getUnlocalizedName(final ItemStack par1ItemStack) {
        return super.getUnlocalizedName(par1ItemStack) + ((par1ItemStack.getItemDamage() < 10) ? ".t2Rocket" : ".cargoRocket");
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
    
    @SideOnly(Side.CLIENT)
    public void registerIcons(final IIconRegister par1IconRegister) {
    }
}
