package micdoodle8.mods.galacticraft.core.blocks;

import net.minecraft.block.*;
import micdoodle8.mods.galacticraft.api.block.*;
import net.minecraft.block.material.*;
import micdoodle8.mods.galacticraft.core.*;
import net.minecraft.creativetab.*;
import net.minecraft.client.renderer.texture.*;
import micdoodle8.mods.galacticraft.core.items.*;
import net.minecraft.world.*;
import net.minecraft.entity.*;
import java.util.*;
import net.minecraft.item.*;
import cpw.mods.fml.relauncher.*;
import net.minecraft.util.*;

public class BlockBasic extends Block implements IDetectableResource
{
    IIcon[] iconBuffer;

    protected BlockBasic(final String assetName) {
        super(Material.rock);
        this.setHardness(1.0f);
        this.blockResistance = 15.0f;
        this.setBlockTextureName(GalacticraftCore.TEXTURE_PREFIX + assetName);
        this.setBlockName(assetName);
    }

    public CreativeTabs getCreativeTabToDisplayOn() {
        return GalacticraftCore.galacticraftBlocksTab;
    }

    public void registerBlockIcons(final IIconRegister iconRegister) {
        (this.iconBuffer = new IIcon[12])[0] = iconRegister.registerIcon(GalacticraftCore.TEXTURE_PREFIX + "deco_aluminium_2");
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

    public IIcon getIcon(final int side, final int meta) {
        switch (meta) {
            case 3: {
                switch (side) {
                    case 0: {
                        return this.iconBuffer[1];
                    }
                    case 1: {
                        return this.iconBuffer[0];
                    }
                    default: {
                        return this.iconBuffer[2];
                    }
                }
            }
            case 4: {
                return this.iconBuffer[3];
            }
            case 5: {
                return this.iconBuffer[4];
            }
            case 6: {
                return this.iconBuffer[5];
            }
            case 7: {
                return this.iconBuffer[6];
            }
            case 8: {
                return this.iconBuffer[7];
            }
            case 9: {
                return this.iconBuffer[8];
            }
            case 10: {
                return this.iconBuffer[9];
            }
            case 11: {
                return this.iconBuffer[10];
            }
            case 12: {
                return this.iconBuffer[11];
            }
            default: {
                return (meta < this.iconBuffer.length) ? this.iconBuffer[meta] : this.iconBuffer[0];
            }
        }
    }

    public Item getItemDropped(final int meta, final Random random, final int par3) {
        if (meta == 8) {
            return GCItems.basicItem;
        }
        return Item.getItemFromBlock((Block) this);
    }

    public int damageDropped(final int meta) {
        if (meta == 8) {
            return 2;
        }
        return meta;
    }

    public int getDamageValue(final World p_149643_1_, final int p_149643_2_, final int p_149643_3_, final int p_149643_4_) {
        return p_149643_1_.getBlockMetadata(p_149643_2_, p_149643_3_, p_149643_4_);
    }

    public int quantityDropped(final int meta, final int fortune, final Random random) {
        if (fortune > 0 && Item.getItemFromBlock((Block)this) != this.getItemDropped(meta, random, fortune)) {
            int j = random.nextInt(fortune + 2) - 1;
            if (j < 0) {
                j = 0;
            }
            return this.quantityDropped(random) * (j + 1);
        }
        return this.quantityDropped(random);
    }

    public float getExplosionResistance(final Entity par1Entity, final World world, final int x, final int y, final int z, final double explosionX, final double explosionY, final double explosionZ) {
        final int metadata = world.getBlockMetadata(x, y, z);
        if (metadata < 5) {
            return 2.0f;
        }
        if (metadata == 12) {
            return 8.0f;
        }
        if (metadata > 8) {
            return 6.0f;
        }
        return this.blockResistance / 5.0f;
    }

    public float getBlockHardness(final World par1World, final int par2, final int par3, final int par4) {
        final int meta = par1World.getBlockMetadata(par2, par3, par4);
        if (meta == 5 || meta == 6) {
            return 5.0f;
        }
        if (meta == 7) {
            return 6.0f;
        }
        if (meta == 8) {
            return 3.0f;
        }
        return this.blockHardness;
    }

    @SideOnly(Side.CLIENT)
    public void getSubBlocks(final Item par1, final CreativeTabs par2CreativeTabs, final List par3List) {
        for (int var4 = 3; var4 < 13; ++var4) {
            par3List.add(new ItemStack(par1, 1, var4));
        }
    }

    public boolean isValueable(final int metadata) {
        return metadata >= 5 && metadata <= 8;
    }

    public ItemStack getPickBlock(final MovingObjectPosition target, final World world, final int x, final int y, final int z) {
        final int metadata = world.getBlockMetadata(x, y, z);
        if (metadata == 8) {
            return new ItemStack(Item.getItemFromBlock(this), 1, metadata);
        }
        return super.getPickBlock(target, world, x, y, z);
    }
}
