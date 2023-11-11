package micdoodle8.mods.galacticraft.core.blocks;

import micdoodle8.mods.galacticraft.core.items.*;
import net.minecraft.util.*;
import net.minecraft.block.material.*;
import net.minecraft.block.*;
import micdoodle8.mods.galacticraft.api.vector.*;
import net.minecraft.creativetab.*;
import micdoodle8.mods.galacticraft.core.*;
import net.minecraft.client.renderer.texture.*;
import cpw.mods.fml.relauncher.*;
import net.minecraft.world.*;
import net.minecraft.tileentity.*;
import micdoodle8.mods.galacticraft.core.tile.*;
import java.util.*;
import net.minecraft.item.*;
import micdoodle8.mods.galacticraft.api.transmission.*;
import micdoodle8.mods.galacticraft.core.util.*;

public class BlockAluminumWire extends BlockTransmitter implements ITileEntityProvider, ItemBlockDesc.IBlockShiftDesc
{
    public static final String[] names;
    private static IIcon[] blockIcons;
    
    public BlockAluminumWire(final String assetName) {
        super(Material.cloth);
        this.setStepSound(Block.soundTypeCloth);
        this.setResistance(0.2f);
        this.setBlockBounds(0.35f, 0.35f, 0.35f, 0.65f, 0.65f, 0.65f);
        this.minVector = new Vector3(0.35, 0.35, 0.35);
        this.maxVector = new Vector3(0.65, 0.65, 0.65);
        this.setHardness(0.075f);
        this.setBlockName(assetName);
    }
    
    public CreativeTabs getCreativeTabToDisplayOn() {
        return GalacticraftCore.galacticraftBlocksTab;
    }
    
    public void registerBlockIcons(final IIconRegister par1IconRegister) {
        BlockAluminumWire.blockIcons = new IIcon[BlockAluminumWire.names.length];
        for (int i = 0; i < BlockAluminumWire.names.length; ++i) {
            BlockAluminumWire.blockIcons[i] = par1IconRegister.registerIcon(GalacticraftCore.TEXTURE_PREFIX + BlockAluminumWire.names[i]);
        }
    }
    
    public boolean isOpaqueCube() {
        return false;
    }
    
    public boolean renderAsNormalBlock() {
        return false;
    }
    
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(final int side, final int meta) {
        switch (meta) {
            case 0: {
                return BlockAluminumWire.blockIcons[0];
            }
            case 1: {
                return BlockAluminumWire.blockIcons[1];
            }
            default: {
                return BlockAluminumWire.blockIcons[0];
            }
        }
    }
    
    public int getRenderType() {
        return -1;
    }
    
    public int damageDropped(final int metadata) {
        return metadata;
    }
    
    public TileEntity createNewTileEntity(final World world, final int metadata) {
        TileEntity tile = null;
        switch (metadata) {
            case 0: {
                tile = new TileEntityAluminumWire(1);
                break;
            }
            case 1: {
                tile = new TileEntityAluminumWire(2);
                break;
            }
            default: {
                return null;
            }
        }
        return tile;
    }
    
    @SideOnly(Side.CLIENT)
    public void getSubBlocks(final Item par1, final CreativeTabs par2CreativeTabs, final List par3List) {
        par3List.add(new ItemStack(par1, 1, 0));
        par3List.add(new ItemStack(par1, 1, 1));
    }
    
    @Override
    public NetworkType getNetworkType() {
        return NetworkType.POWER;
    }
    
    public String getShiftDescription(final int meta) {
        switch (meta) {
            case 0: {
                return GCCoreUtil.translate("tile.aluminumWire.description");
            }
            case 1: {
                return GCCoreUtil.translate("tile.aluminumWireHeavy.description");
            }
            default: {
                return "";
            }
        }
    }
    
    public boolean showDescription(final int meta) {
        return true;
    }
    
    static {
        names = new String[] { "aluminumWire", "aluminumWireHeavy" };
    }
}
