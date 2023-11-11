package micdoodle8.mods.galacticraft.core.world.gen;

import net.minecraft.nbt.*;
import java.util.*;
import net.minecraft.world.gen.structure.*;
import net.minecraft.world.*;
import net.minecraft.init.*;
import micdoodle8.mods.galacticraft.core.blocks.*;

public class StructureComponentVillageTorch extends StructureComponentVillage
{
    private int averageGroundLevel;
    
    public StructureComponentVillageTorch() {
        this.averageGroundLevel = -1;
    }
    
    public StructureComponentVillageTorch(final StructureComponentVillageStartPiece par1ComponentVillageStartPiece, final int par2, final Random par3Random, final StructureBoundingBox par4StructureBoundingBox, final int par5) {
        super(par1ComponentVillageStartPiece, par2);
        this.averageGroundLevel = -1;
        this.coordBaseMode = par5;
        this.boundingBox = par4StructureBoundingBox;
    }
    
    protected void func_143012_a(final NBTTagCompound nbt) {
        super.func_143012_a(nbt);
        nbt.setInteger("AvgGroundLevel", this.averageGroundLevel);
    }
    
    protected void func_143011_b(final NBTTagCompound nbt) {
        super.func_143011_b(nbt);
        this.averageGroundLevel = nbt.getInteger("AvgGroundLevel");
    }
    
    public static StructureBoundingBox func_74904_a(final StructureComponentVillageStartPiece par0ComponentVillageStartPiece, final List par1List, final Random par2Random, final int par3, final int par4, final int par5, final int par6) {
        final StructureBoundingBox var7 = StructureBoundingBox.getComponentToAddBoundingBox(par3, par4, par5, 0, 0, 0, 3, 4, 2, par6);
        return (StructureComponent.findIntersecting(par1List, var7) != null) ? null : var7;
    }
    
    public boolean addComponentParts(final World par1World, final Random par2Random, final StructureBoundingBox par3StructureBoundingBox) {
        if (this.averageGroundLevel < 0) {
            this.averageGroundLevel = this.getAverageGroundLevel(par1World, par3StructureBoundingBox);
            if (this.averageGroundLevel < 0) {
                return true;
            }
            this.boundingBox.offset(0, this.averageGroundLevel - this.boundingBox.maxY + 4 - 1, 0);
        }
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 0, 0, 0, 2, 3, 1, Blocks.air, Blocks.air, false);
        this.placeBlockAtCurrentPosition(par1World, Blocks.fence, 0, 1, 0, 0, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, Blocks.fence, 0, 1, 1, 0, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, Blocks.fence, 0, 1, 2, 0, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, Blocks.wool, 15, 1, 3, 0, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.glowstoneTorch, 0, 0, 3, 0, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.glowstoneTorch, 0, 1, 3, 1, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.glowstoneTorch, 0, 2, 3, 0, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.glowstoneTorch, 0, 1, 3, -1, par3StructureBoundingBox);
        return true;
    }
}
