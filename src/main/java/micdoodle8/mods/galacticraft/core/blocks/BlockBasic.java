package micdoodle8.mods.galacticraft.core.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import micdoodle8.mods.galacticraft.api.block.IDetectableResource;
import micdoodle8.mods.galacticraft.core.GalacticraftCore;
import micdoodle8.mods.galacticraft.core.items.GCItems;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

import java.util.List;
import java.util.Random;

/**
 * Metadata: 3 = Tin Decoration Block 1 4 = Tin Decoration Block 2 5 = Copper Ore 6 = Tin Ore 7 = Aluminium Ore 8 =
 * Silicon Ore 9 = Copper Block 10 = Tin Block 11 = Aluminium Block 12 = Meteoric Iron Block
 */
public class BlockBasic extends Block implements IDetectableResource {

    IIcon[] iconBuffer;

    protected BlockBasic(String assetName) {
        super(Material.rock);
        this.setHardness(1.0F);
        this.blockResistance = 15F;
        this.setBlockTextureName(GalacticraftCore.TEXTURE_PREFIX + assetName);
        this.setBlockName(assetName);
    }

    @Override
    public CreativeTabs getCreativeTabToDisplayOn() {
        return GalacticraftCore.galacticraftBlocksTab;
    }

    @Override
    public void registerBlockIcons(IIconRegister iconRegister) {
        this.iconBuffer = new IIcon[12];
        this.iconBuffer[0] = iconRegister.registerIcon(GalacticraftCore.TEXTURE_PREFIX + "deco_aluminium_2");
        this.iconBuffer[1] = iconRegister.registerIcon(GalacticraftCore.TEXTURE_PREFIX + "deco_aluminium_4");
        this.iconBuffer[2] = iconRegister.registerIcon(GalacticraftCore.TEXTURE_PREFIX + "deco_aluminium_1");
        this.iconBuffer[3] = iconRegister.registerIcon(GalacticraftCore.TEXTURE_PREFIX + "deco_aluminium_4");
        this.iconBuffer[4] = iconRegister.registerIcon(GalacticraftCore.TEXTURE_PREFIX + "oreCopper");
        this.iconBuffer[5] = iconRegister.registerIcon(GalacticraftCore.TEXTURE_PREFIX + "oreTin");
        this.iconBuffer[6] = iconRegister.registerIcon(GalacticraftCore.TEXTURE_PREFIX + "oreAluminum");
        this.iconBuffer[7] = iconRegister.registerIcon(GalacticraftCore.TEXTURE_PREFIX + "oreSilicon");
        this.iconBuffer[8] = iconRegister.registerIcon(GalacticraftCore.TEXTURE_PREFIX + "deco_copper_block");
        this.iconBuffer[9] = iconRegister.registerIcon(GalacticraftCore.TEXTURE_PREFIX + "deco_tin_block");
        this.iconBuffer[10] = iconRegister.registerIcon(GalacticraftCore.TEXTURE_PREFIX + "deco_aluminium_block");
        this.iconBuffer[11] = iconRegister.registerIcon(GalacticraftCore.TEXTURE_PREFIX + "deco_meteoriron_block");
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        IIcon result;

        switch (meta) {
            case 3:
                switch (side) {
                    case 0:
                        result = this.iconBuffer[1];
                        break;
                    case 1:
                        result = this.iconBuffer[0];
                        break;
                    default:
                        result = this.iconBuffer[2];
                        break;
                }
                break;
            case 4:
                result = this.iconBuffer[3];
                break;
            case 5:
                result = this.iconBuffer[4];
                break;
            case 6:
                result = this.iconBuffer[5];
                break;
            case 7:
                result = this.iconBuffer[6];
                break;
            case 8:
                result = this.iconBuffer[7];
                break;
            case 9:
                result = this.iconBuffer[8];
                break;
            case 10:
                result = this.iconBuffer[9];
                break;
            case 11:
                result = this.iconBuffer[10];
                break;
            case 12:
                result = this.iconBuffer[11];
                break;
            default:
                result = meta < this.iconBuffer.length ? this.iconBuffer[meta] : this.iconBuffer[0];
                break;
        }

        return result;
    }

    @Override
    public Item getItemDropped(int meta, Random random, int par3) {
        Item droppedItem;
        switch (meta) {
            case 8:
                droppedItem = GCItems.basicItem;
                break;
            default:
                droppedItem = Item.getItemFromBlock(this);
                break;
        }
        return droppedItem;
    }

    @Override
    public int damageDropped(int meta) {
        if (meta == 8) {
            return 2;
        } else {
            return meta;
        }
    }

    @Override
    public int getDamageValue(World worldIn, int x, int y, int z) {
        return worldIn.getBlockMetadata(x, y, z);
    }

    @Override
    public int quantityDropped(int meta, int fortune, Random random) {
        if (fortune <= 0 || Item.getItemFromBlock(this) == this.getItemDropped(meta, random, fortune)) {
            return this.quantityDropped(random);
        }
        int j = random.nextInt(fortune + 2) - 1;

        if (j < 0) {
            j = 0;
        }

        return this.quantityDropped(random) * (j + 1);
    }

    @Override
    public float getExplosionResistance(Entity par1Entity, World world, int x, int y, int z, double explosionX,
        double explosionY, double explosionZ) {
        final int metadata = world.getBlockMetadata(x, y, z);

        if (metadata < 5) {
            return 2.0F;
            // Decoration blocks are soft, like cauldrons or wood
        }
        if (metadata == 12) {
            return 8.0F;
            // Meteoric Iron is tougher than diamond
        }
        if (metadata > 8) {
            return 6.0F;
            // Blocks of metal are tough - like diamond blocks in vanilla
        }

        return this.blockResistance / 5.0F;
    }

    @Override
    public float getBlockHardness(World par1World, int par2, int par3, int par4) {
        final int meta = par1World.getBlockMetadata(par2, par3, par4);

        switch (meta) {
            case 5:
            case 6:
                return 5.0F;
            case 7:
                return 6.0F;
            case 8:
                return 3.0F;
            default:
                break;
        }

        return this.blockHardness;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void getSubBlocks(Item par1, CreativeTabs par2CreativeTabs, List<ItemStack> par3List) {
        for (int var4 = 3; var4 < 13; ++var4) {
            par3List.add(new ItemStack(par1, 1, var4));
        }
    }

    @Override
    public boolean isValueable(int metadata) {
        return metadata >= 5 && metadata <= 8;
    }

    @SuppressWarnings("deprecation")
    @Override
    public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z) {
        final int metadata = world.getBlockMetadata(x, y, z);
        if (metadata == 8) {
            return new ItemStack(Item.getItemFromBlock(this), 1, metadata);
        }

        return super.getPickBlock(target, world, x, y, z);
    }
}
