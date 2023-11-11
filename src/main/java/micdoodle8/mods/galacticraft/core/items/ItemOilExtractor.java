package micdoodle8.mods.galacticraft.core.items;

import micdoodle8.mods.galacticraft.core.*;
import java.util.*;
import micdoodle8.mods.galacticraft.core.util.*;
import micdoodle8.mods.galacticraft.core.proxy.*;
import cpw.mods.fml.relauncher.*;
import net.minecraft.item.*;
import net.minecraft.world.*;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.util.*;
import micdoodle8.mods.galacticraft.api.vector.*;
import micdoodle8.mods.galacticraft.core.blocks.*;
import net.minecraft.block.*;
import net.minecraftforge.fluids.*;
import net.minecraft.entity.player.*;

public class ItemOilExtractor extends Item
{
    protected IIcon[] icons;
    
    public ItemOilExtractor(final String assetName) {
        this.icons = new IIcon[5];
        this.setMaxStackSize(1);
        this.setUnlocalizedName(assetName);
        this.setTextureName(GalacticraftCore.TEXTURE_PREFIX + assetName);
    }
    
    public void addInformation(final ItemStack item, final EntityPlayer player, final List info, final boolean advanced) {
        super.addInformation(item, player, info, advanced);
        info.add(EnumColor.RED + "[deprecated]");
        info.add(EnumColor.RED + "Use regular buckets instead!");
    }
    
    @SideOnly(Side.CLIENT)
    public EnumRarity getRarity(final ItemStack par1ItemStack) {
        return ClientProxyCore.galacticraftItem;
    }
    
    public EnumAction getItemUseAction(final ItemStack par1ItemStack) {
        return EnumAction.bow;
    }
    
    public ItemStack onItemRightClick(final ItemStack par1ItemStack, final World par2World, final EntityPlayer player) {
        if (this.getNearestOilBlock(player) != null && this.openCanister(player) != null) {
            player.setItemInUse(par1ItemStack, this.getMaxItemUseDuration(par1ItemStack));
        }
        return par1ItemStack;
    }
    
    @SideOnly(Side.CLIENT)
    public void registerIcons(final IIconRegister iconRegister) {
        this.icons = new IIcon[5];
        for (int i = 0; i < this.icons.length; ++i) {
            this.icons[i] = iconRegister.registerIcon(this.getIconString() + "_" + (i + 1));
        }
        this.itemIcon = this.icons[0];
    }
    
    public void onUsingTick(final ItemStack stack, final EntityPlayer player, final int count) {
        final Vector3 blockHit = this.getNearestOilBlock(player);
        if (blockHit != null) {
            final int x = MathHelper.floor_double(blockHit.x);
            final int y = MathHelper.floor_double(blockHit.y);
            final int z = MathHelper.floor_double(blockHit.z);
            if (this.isOilBlock(player, player.worldObj, x, y, z, false) && this.openCanister(player) != null) {
                final ItemStack canister = this.openCanister(player);
                if (canister != null && count % 5 == 0 && canister.getItemDamage() > 1) {
                    this.isOilBlock(player, player.worldObj, x, y, z, true);
                    canister.setItemDamage(Math.max(canister.getItemDamage() - 200, 1));
                }
            }
        }
    }
    
    private ItemStack openCanister(final EntityPlayer player) {
        for (final ItemStack stack : player.inventory.mainInventory) {
            if (stack != null && stack.getItem() instanceof ItemOilCanister && stack.getMaxDamage() - stack.getItemDamage() >= 0 && stack.getMaxDamage() - stack.getItemDamage() < GCItems.oilCanister.getMaxDamage() - 1) {
                return stack;
            }
        }
        return null;
    }
    
    public int getMaxItemUseDuration(final ItemStack par1ItemStack) {
        return 72000;
    }
    
    public ItemStack onEaten(final ItemStack par1ItemStack, final World par2World, final EntityPlayer player) {
        return par1ItemStack;
    }
    
    public IIcon getIcon(final ItemStack stack, final int renderPass, final EntityPlayer player, final ItemStack usingItem, final int useRemaining) {
        final int count2 = useRemaining / 2;
        switch (count2 % 5) {
            case 0: {
                if (useRemaining == 0) {
                    return this.icons[0];
                }
                return this.icons[4];
            }
            case 1: {
                return this.icons[3];
            }
            case 2: {
                return this.icons[2];
            }
            case 3: {
                return this.icons[1];
            }
            case 4: {
                return this.icons[0];
            }
            default: {
                return this.icons[0];
            }
        }
    }
    
    public void onPlayerStoppedUsing(final ItemStack par1ItemStack, final World par2World, final EntityPlayer player, final int par4) {
        if (par2World.isRemote) {
            this.itemIcon = this.icons[0];
        }
    }
    
    private boolean isOilBlock(final EntityPlayer player, final World world, final int x, final int y, final int z, final boolean doDrain) {
        final Block block = world.getBlock(x, y, z);
        if (block instanceof IFluidBlock) {
            final IFluidBlock fluidBlockHit = (IFluidBlock)block;
            boolean flag = false;
            if (block == GCBlocks.crudeOil) {
                flag = true;
            }
            else {
                final Fluid fluidHit = FluidRegistry.lookupFluidForBlock(block);
                if (fluidHit != null && fluidHit.getName().startsWith("oil")) {
                    flag = true;
                }
            }
            if (flag) {
                final FluidStack stack = fluidBlockHit.drain(world, x, y, z, doDrain);
                return stack != null && stack.amount > 0;
            }
        }
        return false;
    }
    
    private Vector3 getNearestOilBlock(final EntityPlayer par1EntityPlayer) {
        final float var4 = 1.0f;
        final float var5 = par1EntityPlayer.prevRotationPitch + (par1EntityPlayer.rotationPitch - par1EntityPlayer.prevRotationPitch) * 1.0f;
        final float var6 = par1EntityPlayer.prevRotationYaw + (par1EntityPlayer.rotationYaw - par1EntityPlayer.prevRotationYaw) * 1.0f;
        final double var7 = par1EntityPlayer.prevPosX + (par1EntityPlayer.posX - par1EntityPlayer.prevPosX) * 1.0;
        final double var8 = par1EntityPlayer.prevPosY + (par1EntityPlayer.posY - par1EntityPlayer.prevPosY) * 1.0 + 1.62 - par1EntityPlayer.yOffset;
        final double var9 = par1EntityPlayer.prevPosZ + (par1EntityPlayer.posZ - par1EntityPlayer.prevPosZ) * 1.0;
        final Vector3 var10 = new Vector3(var7, var8, var9);
        final float var11 = MathHelper.cos(-var6 * 0.017453292f - 3.1415927f);
        final float var12 = MathHelper.sin(-var6 * 0.017453292f - 3.1415927f);
        final float var13 = -MathHelper.cos(-var5 * 0.017453292f);
        final float var14 = MathHelper.sin(-var5 * 0.017453292f);
        final float var15 = var12 * var13;
        final float var16 = var11 * var13;
        double var17 = 5.0;
        if (par1EntityPlayer instanceof EntityPlayerMP) {
            var17 = ((EntityPlayerMP)par1EntityPlayer).theItemInWorldManager.getBlockReachDistance();
        }
        for (double dist = 0.0; dist <= var17; ++dist) {
            final Vector3 var18 = var10.translate(new Vector3(var15 * dist, var14 * dist, var16 * dist));
            if (this.isOilBlock(par1EntityPlayer, par1EntityPlayer.worldObj, MathHelper.floor_double(var18.x), MathHelper.floor_double(var18.y), MathHelper.floor_double(var18.z), false)) {
                return var18;
            }
        }
        return null;
    }
}
