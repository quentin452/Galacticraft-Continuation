package micdoodle8.mods.galacticraft.core.blocks;

import net.minecraftforge.fluids.*;
import micdoodle8.mods.galacticraft.core.*;
import net.minecraft.block.material.*;
import net.minecraft.creativetab.*;
import cpw.mods.fml.relauncher.*;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.entity.player.*;
import net.minecraft.client.entity.*;
import micdoodle8.mods.galacticraft.core.proxy.*;
import java.util.*;
import net.minecraft.world.*;
import micdoodle8.mods.galacticraft.api.vector.*;
import net.minecraft.block.*;
import net.minecraftforge.common.util.*;
import micdoodle8.mods.galacticraft.core.util.*;
import net.minecraft.util.*;
import net.minecraft.entity.*;

public class BlockFluidGC extends BlockFluidClassic
{
    private IIcon stillIcon;
    private IIcon flowingIcon;
    private final String fluidName;
    private final Fluid fluid;
    
    public BlockFluidGC(final Fluid fluid, final String assetName) {
        super(fluid, (assetName.startsWith("oil") || assetName.startsWith("fuel")) ? GalacticraftCore.materialOil : Material.water);
        this.setRenderPass(1);
        this.fluidName = assetName;
        this.fluid = fluid;
        if (assetName.startsWith("oil")) {
            this.needsRandomTick = true;
        }
    }
    
    public CreativeTabs getCreativeTabToDisplayOn() {
        return GalacticraftCore.galacticraftBlocksTab;
    }
    
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(final int par1, final int par2) {
        return (par1 != 0 && par1 != 1) ? this.flowingIcon : this.stillIcon;
    }
    
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(final IIconRegister par1IconRegister) {
        this.stillIcon = par1IconRegister.registerIcon(GalacticraftCore.TEXTURE_PREFIX + this.fluidName + "_still");
        this.flowingIcon = par1IconRegister.registerIcon(GalacticraftCore.TEXTURE_PREFIX + this.fluidName + "_flow");
        this.fluid.setStillIcon(this.stillIcon);
        this.fluid.setFlowingIcon(this.flowingIcon);
    }
    
    public boolean onBlockActivated(final World world, final int x, final int y, final int z, final EntityPlayer entityPlayer, final int side, final float hitX, final float hitY, final float hitZ) {
        if (world.isRemote && this.fluidName.startsWith("oil") && entityPlayer instanceof EntityPlayerSP) {
            ClientProxyCore.playerClientHandler.onBuild(7, (EntityPlayerSP)entityPlayer);
        }
        return super.onBlockActivated(world, x, y, z, entityPlayer, side, hitX, hitY, hitZ);
    }
    
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(final World world, final int x, final int y, final int z, final Random rand) {
        super.randomDisplayTick(world, x, y, z, rand);
        if (this.fluidName.startsWith("oil") && rand.nextInt(1200) == 0) {
            world.playSound((double)(x + 0.5f), (double)(y + 0.5f), (double)(z + 0.5f), "liquid.lava", rand.nextFloat() * 0.25f + 0.75f, 1.0E-5f + rand.nextFloat() * 0.5f, false);
        }
        if (this.fluidName.equals("oil") && rand.nextInt(10) == 0 && World.doesBlockHaveSolidTopSurface((IBlockAccess)world, x, y - 1, z) && !world.getBlock(x, y - 2, z).getMaterial().blocksMovement()) {
            GalacticraftCore.proxy.spawnParticle("oilDrip", new Vector3((double)(x + rand.nextFloat()), y - 1.05, (double)(z + rand.nextFloat())), new Vector3(0.0, 0.0, 0.0), new Object[0]);
        }
    }
    
    public boolean canDisplace(final IBlockAccess world, final int x, final int y, final int z) {
        if (world.getBlock(x, y, z) instanceof BlockLiquid) {
            final int meta = world.getBlockMetadata(x, y, z);
            return meta > 1 || meta == -1;
        }
        return super.canDisplace(world, x, y, z);
    }
    
    public boolean displaceIfPossible(final World world, final int x, final int y, final int z) {
        if (world.getBlock(x, y, z) instanceof BlockLiquid) {
            final int meta = world.getBlockMetadata(x, y, z);
            return (meta > 1 || meta == -1) && super.displaceIfPossible(world, x, y, z);
        }
        return super.displaceIfPossible(world, x, y, z);
    }
    
    public IIcon getStillIcon() {
        return this.stillIcon;
    }
    
    public IIcon getFlowingIcon() {
        return this.flowingIcon;
    }
    
    public boolean isFlammable(final IBlockAccess world, final int x, final int y, final int z, final ForgeDirection face) {
        if (!(world instanceof World)) {
            return false;
        }
        if (OxygenUtil.noAtmosphericCombustion(((World)world).provider) && !OxygenUtil.isAABBInBreathableAirBlock((World)world, AxisAlignedBB.getBoundingBox((double)x, (double)y, (double)z, (double)(x + 1), (double)(y + 2), (double)(z + 1)))) {
            return false;
        }
        if (this.fluidName.startsWith("fuel")) {
            ((World)world).createExplosion((Entity)null, (double)x, (double)y, (double)z, 6.0f, true);
            return true;
        }
        return this.fluidName.startsWith("oil");
    }
    
    public boolean shouldSideBeRendered(final IBlockAccess world, final int x, final int y, final int z, final int side) {
        return super.shouldSideBeRendered(world, x, y, z, side);
    }
}
