package micdoodle8.mods.galacticraft.core.world.gen;

import net.minecraft.nbt.*;
import java.util.*;
import net.minecraft.world.gen.structure.*;
import net.minecraft.world.*;
import net.minecraft.init.*;
import net.minecraft.block.*;
import net.minecraft.util.*;

public class StructureComponentVillageField extends StructureComponentVillage
{
    private int averageGroundLevel;
    
    public StructureComponentVillageField() {
        this.averageGroundLevel = -1;
    }
    
    public StructureComponentVillageField(final StructureComponentVillageStartPiece par1ComponentVillageStartPiece, final int par2, final Random par3Random, final StructureBoundingBox par4StructureBoundingBox, final int par5) {
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
    
    public static StructureComponentVillageField func_74900_a(final StructureComponentVillageStartPiece par0ComponentVillageStartPiece, final List par1List, final Random par2Random, final int par3, final int par4, final int par5, final int par6, final int par7) {
        final StructureBoundingBox var8 = StructureBoundingBox.getComponentToAddBoundingBox(par3, par4, par5, 0, 0, 0, 13, 4, 9, par6);
        return (StructureComponent.findIntersecting(par1List, var8) == null) ? new StructureComponentVillageField(par0ComponentVillageStartPiece, par7, par2Random, var8, par6) : null;
    }
    
    public boolean addComponentParts(final World par1World, final Random par2Random, final StructureBoundingBox par3StructureBoundingBox) {
        if (this.averageGroundLevel < 0) {
            this.averageGroundLevel = this.getAverageGroundLevel(par1World, par3StructureBoundingBox);
            if (this.averageGroundLevel < 0) {
                return true;
            }
            this.boundingBox.offset(0, this.averageGroundLevel - this.boundingBox.maxY + 7 - 1, 0);
        }
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 0, 1, 0, 12, 4, 8, Blocks.air, Blocks.air, false);
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 1, 0, 1, 2, 0, 7, Blocks.dirt, Blocks.dirt, false);
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 4, 0, 1, 5, 0, 7, Blocks.dirt, Blocks.dirt, false);
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 7, 0, 1, 8, 0, 7, Blocks.dirt, Blocks.dirt, false);
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 10, 0, 1, 11, 0, 7, Blocks.dirt, Blocks.dirt, false);
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 0, 0, 0, 0, 0, 8, Blocks.log, Blocks.log, false);
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 6, 0, 0, 6, 0, 8, Blocks.log, Blocks.log, false);
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 12, 0, 0, 12, 0, 8, Blocks.log, Blocks.log, false);
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 1, 0, 0, 11, 0, 0, Blocks.log, Blocks.log, false);
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 1, 0, 8, 11, 0, 8, Blocks.log, Blocks.log, false);
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 3, 0, 1, 3, 0, 7, (Block)Blocks.flowing_water, (Block)Blocks.flowing_water, false);
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 9, 0, 1, 9, 0, 7, (Block)Blocks.flowing_water, (Block)Blocks.flowing_water, false);
        for (int var4 = 1; var4 <= 7; ++var4) {
            for (int i = 1; i < 12; ++i) {
                if (i % 3 != 0 && par2Random.nextInt(3) == 0) {
                    this.placeBlockAtCurrentPosition(par1World, Blocks.sapling, MathHelper.getRandomIntegerInRange(par2Random, 0, 2), i, 1, var4, par3StructureBoundingBox);
                }
            }
        }
        for (int var4 = 0; var4 < 9; ++var4) {
            for (int var5 = 0; var5 < 13; ++var5) {
                this.clearCurrentPositionBlocksUpwards(par1World, var5, 4, var4, par3StructureBoundingBox);
                this.func_151554_b(par1World, Blocks.dirt, 0, var5, -1, var4, par3StructureBoundingBox);
            }
        }
        return true;
    }
}
