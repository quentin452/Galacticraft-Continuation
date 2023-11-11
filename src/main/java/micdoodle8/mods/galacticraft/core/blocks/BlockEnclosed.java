package micdoodle8.mods.galacticraft.core.blocks;

import micdoodle8.mods.galacticraft.api.block.*;
import micdoodle8.mods.galacticraft.core.items.*;
import net.minecraft.util.*;
import net.minecraft.block.material.*;
import net.minecraft.block.*;
import micdoodle8.mods.galacticraft.core.*;
import net.minecraft.creativetab.*;
import java.util.*;
import net.minecraft.item.*;
import cpw.mods.fml.relauncher.*;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.world.*;
import micdoodle8.mods.galacticraft.api.transmission.tile.*;
import net.minecraft.tileentity.*;
import appeng.api.*;
import micdoodle8.mods.galacticraft.core.tile.*;
import java.lang.reflect.*;
import appeng.api.parts.*;
import net.minecraftforge.common.util.*;
import micdoodle8.mods.galacticraft.core.util.*;

public class BlockEnclosed extends BlockContainer implements IPartialSealableBlock, ITileEntityProvider, ItemBlockDesc.IBlockShiftDesc
{
    private IIcon[] enclosedIcons;
    public static Item[] pipeItemsBC;
    public static BlockContainer blockPipeBC;
    public static Method onBlockNeighbourChangeIC2;

    public static EnumEnclosedBlock getTypeFromMeta(final int metadata) {
        for (final EnumEnclosedBlock type : EnumEnclosedBlock.values()) {
            if (type.getMetadata() == metadata) {
                return type;
            }
        }
        return null;
    }

    public BlockEnclosed(final String assetName) {
        super(Material.clay);
        this.setResistance(0.2f);
        this.setHardness(0.4f);
        this.setStepSound(Block.soundTypeStone);
        this.setBlockTextureName(GalacticraftCore.TEXTURE_PREFIX + assetName);
        this.setBlockName(assetName);
    }

    @SideOnly(Side.CLIENT)
    public void getSubBlocks(final Item par1, final CreativeTabs par2CreativeTabs, final List par3List) {
        par3List.add(new ItemStack(par1, 1, EnumEnclosedBlock.ALUMINUM_WIRE.getMetadata()));
        par3List.add(new ItemStack(par1, 1, EnumEnclosedBlock.ALUMINUM_WIRE_HEAVY.getMetadata()));
        par3List.add(new ItemStack(par1, 1, EnumEnclosedBlock.OXYGEN_PIPE.getMetadata()));
        if (CompatibilityManager.isTELoaded()) {}
        if (CompatibilityManager.isIc2Loaded()) {
            par3List.add(new ItemStack(par1, 1, EnumEnclosedBlock.IC2_COPPER_CABLE.getMetadata()));
            par3List.add(new ItemStack(par1, 1, EnumEnclosedBlock.IC2_GOLD_CABLE.getMetadata()));
            par3List.add(new ItemStack(par1, 1, 4));
            par3List.add(new ItemStack(par1, 1, EnumEnclosedBlock.IC2_GLASS_FIBRE_CABLE.getMetadata()));
            par3List.add(new ItemStack(par1, 1, EnumEnclosedBlock.IC2_LV_CABLE.getMetadata()));
        }
        if (CompatibilityManager.isBCraftTransportLoaded()) {
            par3List.add(new ItemStack(par1, 1, EnumEnclosedBlock.BC_ITEM_COBBLESTONEPIPE.getMetadata()));
            par3List.add(new ItemStack(par1, 1, EnumEnclosedBlock.BC_ITEM_STONEPIPE.getMetadata()));
            par3List.add(new ItemStack(par1, 1, EnumEnclosedBlock.BC_FLUIDS_COBBLESTONEPIPE.getMetadata()));
            par3List.add(new ItemStack(par1, 1, EnumEnclosedBlock.BC_FLUIDS_STONEPIPE.getMetadata()));
            par3List.add(new ItemStack(par1, 1, EnumEnclosedBlock.BC_POWER_STONEPIPE.getMetadata()));
            par3List.add(new ItemStack(par1, 1, EnumEnclosedBlock.BC_POWER_GOLDPIPE.getMetadata()));
        }
        if (CompatibilityManager.isAppEngLoaded()) {
            par3List.add(new ItemStack(par1, 1, EnumEnclosedBlock.ME_CABLE.getMetadata()));
        }
    }

    public static void initialiseBC() {
        for (int i = 0; i < 6; ++i) {
            try {
                final Class<?> clazzBC = Class.forName("buildcraft.BuildCraftTransport");
                String pipeName = EnumEnclosedBlock.values()[i + 7].getPipeType();
                pipeName = pipeName.substring(0, 1).toLowerCase() + pipeName.substring(1);
                BlockEnclosed.pipeItemsBC[i] = (Item)clazzBC.getField(pipeName).get(null);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public CreativeTabs getCreativeTabToDisplayOn() {
        return GalacticraftCore.galacticraftBlocksTab;
    }

    @SideOnly(Side.CLIENT)
    public IIcon getIcon(final int par1, int meta) {
        if (meta == 4) {
            meta = 0;
        }
        return (meta >= this.enclosedIcons.length) ? this.blockIcon : this.enclosedIcons[meta];
    }

    public int damageDropped(final int meta) {
        if (meta == 0) {
            return 4;
        }
        if (meta == 4) {
            return 0;
        }
        return meta;
    }

    public void registerBlockIcons(final IIconRegister par1IconRegister) {
        this.enclosedIcons = new IIcon[16];
        for (int i = 0; i < EnumEnclosedBlock.values().length; ++i) {
            this.enclosedIcons[EnumEnclosedBlock.values()[i].getMetadata()] = par1IconRegister.registerIcon(GalacticraftCore.TEXTURE_PREFIX + EnumEnclosedBlock.values()[i].getTexture());
        }
        this.blockIcon = par1IconRegister.registerIcon(GalacticraftCore.TEXTURE_PREFIX + "" + EnumEnclosedBlock.OXYGEN_PIPE.getTexture());
    }

    public void onNeighborBlockChange(final World world, final int x, final int y, final int z, final Block block) {
        final int metadata = world.getBlockMetadata(x, y, z);
        final TileEntity tileEntity = world.getTileEntity(x, y, z);
        if (metadata == EnumEnclosedBlock.TE_CONDUIT.getMetadata()) {
            super.onNeighborBlockChange(world, x, y, z, block);
        }
        else if (metadata == EnumEnclosedBlock.OXYGEN_PIPE.getMetadata()) {
            super.onNeighborBlockChange(world, x, y, z, block);
            if (tileEntity instanceof INetworkConnection) {
                ((INetworkConnection)tileEntity).refresh();
            }
        }
        else {
            if (metadata <= 6) {
                super.onNeighborBlockChange(world, x, y, z, block);
                if (!CompatibilityManager.isIc2Loaded() || tileEntity == null) {
                    return;
                }
                try {
                    BlockEnclosed.onBlockNeighbourChangeIC2.invoke(tileEntity, new Object[0]);
                    return;
                }
                catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
            }
            if (metadata <= 12) {
                if (CompatibilityManager.isBCraftTransportLoaded() && BlockEnclosed.blockPipeBC != null) {
                    try {
                        BlockEnclosed.blockPipeBC.onNeighborBlockChange(world, x, y, z, block);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                    return;
                }
                super.onNeighborBlockChange(world, x, y, z, block);
            }
            else if (metadata <= EnumEnclosedBlock.ME_CABLE.getMetadata()) {
                super.onNeighborBlockChange(world, x, y, z, block);
                if (CompatibilityManager.isAppEngLoaded()) {
                    world.markBlockForUpdate(x, y, z);
                }
            }
            else if (metadata <= EnumEnclosedBlock.ALUMINUM_WIRE.getMetadata()) {
                super.onNeighborBlockChange(world, x, y, z, block);
                if (tileEntity instanceof IConductor) {
                    ((IConductor)tileEntity).refresh();
                }
            }
            else if (metadata <= EnumEnclosedBlock.ALUMINUM_WIRE_HEAVY.getMetadata()) {
                super.onNeighborBlockChange(world, x, y, z, block);
                if (tileEntity instanceof IConductor) {
                    ((IConductor)tileEntity).refresh();
                }
            }
        }
    }

    public TileEntity createNewTileEntity(final World world, final int metadata) {
        if (metadata != EnumEnclosedBlock.TE_CONDUIT.getMetadata()) {
            if (metadata == EnumEnclosedBlock.OXYGEN_PIPE.getMetadata()) {
                return new TileEntityOxygenPipe();
            }
            if (metadata <= 6) {
                if (!CompatibilityManager.isIc2Loaded()) {
                    return null;
                }
                try {
                    final Class<?> clazz = Class.forName("ic2.core.block.wiring.TileEntityCable");
                    final Constructor<?>[] constructors = clazz.getDeclaredConstructors();
                    Constructor<?> constructor = null;
                    final Constructor<?>[] array = constructors;
                    for (int length = array.length, i = 0; i < length; ++i) {
                        final Constructor<?> constructor2 = constructor = array[i];
                        if (constructor.getGenericParameterTypes().length == 1) {
                            break;
                        }
                    }
                    constructor.setAccessible(true);
                    return (TileEntity)constructor.newInstance((short)getTypeFromMeta(metadata).getSubMetaValue());
                }
                catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }
            if (metadata <= 12) {
                if (!CompatibilityManager.isBCraftTransportLoaded()) {
                    return null;
                }
                try {
                    return BlockEnclosed.blockPipeBC.createNewTileEntity(world, 0);
                }
                catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }
            if (metadata <= EnumEnclosedBlock.ME_CABLE.getMetadata()) {
                if (!CompatibilityManager.isAppEngLoaded()) {
                    return null;
                }
                try {
                    final IPartHelper apiPart = AEApi.instance().partHelper();
                    final Class<?> clazzApiPart = Class.forName("appeng.core.api.ApiPart");
                    final Class clazz2 = (Class)clazzApiPart.getDeclaredMethod("getCombinedInstance", String.class).invoke(apiPart, "appeng.tile.networking.TileCableBus");
                    return (TileEntity) clazz2.newInstance();
                }
                catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }
            if (metadata <= EnumEnclosedBlock.ALUMINUM_WIRE.getMetadata()) {
                return new TileEntityAluminumWire(1);
            }
            if (metadata <= EnumEnclosedBlock.ALUMINUM_WIRE_HEAVY.getMetadata()) {
                return new TileEntityAluminumWire(2);
            }
        }
        return null;
    }

    public boolean isSealed(final World world, final int x, final int y, final int z, final ForgeDirection direction) {
        return true;
    }

    public String getShiftDescription(final int meta) {
        return GCCoreUtil.translate(this.getUnlocalizedName() + ".description");
    }

    public boolean showDescription(final int meta) {
        return true;
    }

    public void onPostBlockPlaced(final World world, final int x, final int y, final int z, final int metadata) {
        if (metadata >= EnumEnclosedBlock.BC_ITEM_STONEPIPE.getMetadata() && metadata <= EnumEnclosedBlock.BC_POWER_GOLDPIPE.getMetadata()) {
            final EnumEnclosedBlock type = getTypeFromMeta(metadata);
            if (CompatibilityManager.isBCraftTransportLoaded() && type != null && type.getPipeType() != null) {
                initialiseBCPipe(world, x, y, z, metadata);
            }
        }
    }

    public static void initialiseBCPipe(final World world, final int i, final int j, final int k, final int metadata) {
        try {
            final Item pipeItem = BlockEnclosed.pipeItemsBC[metadata - 7];
            final Class<?> clazzBlockPipe = CompatibilityManager.classBCBlockGenericPipe;
            final TileEntity tilePipe = world.getTileEntity(i, j, k);
            final Class<?> clazzTilePipe = tilePipe.getClass();
            if (CompatibilityManager.methodBCBlockPipe_createPipe != null) {
                final Object pipe = CompatibilityManager.methodBCBlockPipe_createPipe.invoke(null, pipeItem);
                Method initializePipe = null;
                for (final Method m : clazzTilePipe.getMethods()) {
                    if (m.getName().equals("initialize") && m.getParameterTypes().length == 1) {
                        initializePipe = m;
                        break;
                    }
                }
                if (initializePipe != null) {
                    initializePipe.invoke(tilePipe, pipe);
                    Method l = null;
                    try {
                        l = clazzTilePipe.getMethod("sendUpdateToClient", (Class<?>[])new Class[0]);
                    }
                    catch (Exception ex) {}
                    if (l != null) {
                        l.invoke(tilePipe, new Object[0]);
                    }
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    static {
        BlockEnclosed.pipeItemsBC = new Item[6];
        BlockEnclosed.blockPipeBC = null;
        BlockEnclosed.onBlockNeighbourChangeIC2 = null;
    }

    public enum EnumEnclosedBlock
    {
        TE_CONDUIT(4, 2, (String)null, "enclosed_te_conduit"),
        OXYGEN_PIPE(1, -1, (String)null, "enclosed_oxygen_pipe"),
        IC2_COPPER_CABLE(2, 0, (String)null, "enclosed_copper_cable"),
        IC2_GOLD_CABLE(3, 3, (String)null, "enclosed_gold_cable"),
        IC2_HV_CABLE(0, 6, (String)null, "enclosed_hv_cable"),
        IC2_GLASS_FIBRE_CABLE(5, 9, (String)null, "enclosed_glassfibre_cable"),
        IC2_LV_CABLE(6, 13, (String)null, "enclosed_lv_cable"),
        BC_ITEM_STONEPIPE(7, -1, "PipeItemsStone", "enclosed_itempipe_stone"),
        BC_ITEM_COBBLESTONEPIPE(8, -1, "PipeItemsCobblestone", "enclosed_itempipe_cobblestone"),
        BC_FLUIDS_STONEPIPE(9, -1, "PipeFluidsStone", "enclosed_liquidpipe_stone"),
        BC_FLUIDS_COBBLESTONEPIPE(10, -1, "PipeFluidsCobblestone", "enclosed_liquidpipe_cobblestone"),
        BC_POWER_STONEPIPE(11, -1, "PipePowerStone", "enclosed_powerpipe_stone"),
        BC_POWER_GOLDPIPE(12, -1, "PipePowerGold", "enclosed_powerpipe_gold"),
        ME_CABLE(13, -1, (String)null, "enclosed_me_cable"),
        ALUMINUM_WIRE(14, -1, (String)null, "enclosed_aluminum_wire"),
        ALUMINUM_WIRE_HEAVY(15, -1, (String)null, "enclosed_heavy_aluminum_wire");

        int metadata;
        int subMeta;
        String pipeType;
        String texture;

        private EnumEnclosedBlock(final int metadata, final int subMeta, final String pipeTypeBC, final String texture) {
            this.metadata = metadata;
            this.subMeta = subMeta;
            this.pipeType = pipeTypeBC;
            this.texture = texture;
        }

        public int getMetadata() {
            return this.metadata;
        }

        public int getSubMetaValue() {
            return this.subMeta;
        }

        public String getPipeType() {
            return this.pipeType;
        }

        public String getTexture() {
            return this.texture;
        }
    }
}
