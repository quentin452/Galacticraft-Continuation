package micdoodle8.mods.galacticraft.planets.asteroids.items;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import micdoodle8.mods.galacticraft.core.items.ItemCanisterGeneric;
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil;
import micdoodle8.mods.galacticraft.planets.asteroids.AsteroidsModule;

public class ItemCanisterLiquidNitrogen extends ItemCanisterGeneric {

    protected IIcon[] icons = new IIcon[7];

    public ItemCanisterLiquidNitrogen(String assetName) {
        super(assetName);
        this.setAllowedFluid("liquidnitrogen");
        this.setTextureName(AsteroidsModule.TEXTURE_PREFIX + assetName);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister iconRegister) {
        for (int i = 0; i < this.icons.length; i++) {
            this.icons[i] = iconRegister.registerIcon(this.getIconString() + "_" + i);
        }
    }

    @Override
    public String getUnlocalizedName(ItemStack itemStack) {
        if (itemStack.getMaxDamage() - itemStack.getItemDamage() == 0) {
            return "item.emptyGasCanister";
        }

        if (itemStack.getItemDamage() == 1) {
            return "item.canister.liquidNitrogen.full";
        }

        return "item.canister.liquidNitrogen.partial";
    }

    @Override
    public IIcon getIconFromDamage(int par1) {
        final int damage = 6 * par1 / this.getMaxDamage();

        if (this.icons.length > damage) {
            return this.icons[this.icons.length - damage - 1];
        }

        return super.getIconFromDamage(damage);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List<String> par3List,
        boolean par4) {
        if (par1ItemStack.getMaxDamage() - par1ItemStack.getItemDamage() > 0) {
            par3List.add(
                GCCoreUtil.translate("item.canister.liquidNitrogen.name") + ": "
                    + (par1ItemStack.getMaxDamage() - par1ItemStack.getItemDamage()));
        }
    }

    private Block canFreeze(Block b) {
        if (b == Blocks.water) {
            return Blocks.ice;
        }
        if (b == Blocks.lava) {
            return Blocks.obsidian;
        }
        return null;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack itemStack, World par2World, EntityPlayer par3EntityPlayer) {
        final int damage = itemStack.getItemDamage() + 125;
        if (damage > itemStack.getMaxDamage()) {
            return itemStack;
        }

        final MovingObjectPosition movingobjectposition = this
            .getMovingObjectPositionFromPlayer(par2World, par3EntityPlayer, true);

        if (movingobjectposition == null) {
            return itemStack;
        }
        if (movingobjectposition.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
            final int x = movingobjectposition.blockX;
            final int y = movingobjectposition.blockY;
            final int z = movingobjectposition.blockZ;

            if (!par2World.canMineBlock(par3EntityPlayer, x, y, z)
                || !par3EntityPlayer.canPlayerEdit(x, y, z, movingobjectposition.sideHit, itemStack)) {
                return itemStack;
            }

            // Material material = par2World.getBlock(i, j, k).getMaterial();
            final Block b = par2World.getBlock(x, y, z);
            par2World.getBlockMetadata(x, y, z);

            final Block result = this.canFreeze(b);
            if (result != null) {
                this.setNewDamage(itemStack, damage);
                par2World.playSoundEffect(
                    x + 0.5D,
                    y + 0.5D,
                    z + 0.5D,
                    "fire.ignite",
                    1.0F,
                    Item.itemRand.nextFloat() * 0.4F + 0.8F);
                par2World.setBlock(x, y, z, result, 0, 3);
            }
        }

        return itemStack;
    }
}
