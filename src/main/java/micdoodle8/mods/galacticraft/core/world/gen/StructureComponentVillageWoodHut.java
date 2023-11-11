package micdoodle8.mods.galacticraft.core.world.gen;

import net.minecraft.nbt.*;
import java.util.*;
import net.minecraft.world.gen.structure.*;
import net.minecraft.world.*;
import micdoodle8.mods.galacticraft.core.blocks.*;
import net.minecraft.init.*;

public class StructureComponentVillageWoodHut extends StructureComponentVillage
{
    private int averageGroundLevel;
    
    public StructureComponentVillageWoodHut() {
        this.averageGroundLevel = -1;
    }
    
    public StructureComponentVillageWoodHut(final StructureComponentVillageStartPiece par1ComponentVillageStartPiece, final int par2, final Random par3Random, final StructureBoundingBox par4StructureBoundingBox, final int par5) {
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
    
    public static StructureComponentVillageWoodHut func_74908_a(final StructureComponentVillageStartPiece par0ComponentVillageStartPiece, final List<StructureComponent> par1List, final Random par2Random, final int par3, final int par4, final int par5, final int par6, final int par7) {
        final StructureBoundingBox var8 = StructureBoundingBox.getComponentToAddBoundingBox(par3, par4, par5, 0, 0, 0, 17, 9, 17, par6);
        return (StructureComponent.findIntersecting((List)par1List, var8) == null) ? new StructureComponentVillageWoodHut(par0ComponentVillageStartPiece, par7, par2Random, var8, par6) : null;
    }
    
    public boolean addComponentParts(final World par1World, final Random par2Random, final StructureBoundingBox par3StructureBoundingBox) {
        if (this.averageGroundLevel < 0) {
            this.averageGroundLevel = this.getAverageGroundLevel(par1World, par3StructureBoundingBox);
            if (this.averageGroundLevel < 0) {
                return true;
            }
            this.boundingBox.offset(0, this.averageGroundLevel - this.boundingBox.maxY + 9 - 1, 0);
        }
        this.fillWithAir(par1World, par3StructureBoundingBox, 3, 0, 3, 13, 9, 13);
        this.fillWithAir(par1World, par3StructureBoundingBox, 5, 0, 2, 11, 9, 14);
        this.fillWithAir(par1World, par3StructureBoundingBox, 2, 0, 5, 14, 9, 11);
        for (int i = 3; i <= 13; ++i) {
            for (int j = 3; j <= 13; ++j) {
                this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 3, i, 0, j, par3StructureBoundingBox);
            }
        }
        for (int i = 5; i <= 11; ++i) {
            for (int j = 2; j <= 14; ++j) {
                this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 3, i, 0, j, par3StructureBoundingBox);
            }
        }
        for (int i = 2; i <= 14; ++i) {
            for (int j = 5; j <= 11; ++j) {
                this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 3, i, 0, j, par3StructureBoundingBox);
            }
        }
        int yLevel;
        for (yLevel = 0, yLevel = -8; yLevel < 4; ++yLevel) {
            this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 4, yLevel, 2, par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 3, yLevel, 2, par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 2, yLevel, 3, par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 2, yLevel, 4, par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 1, yLevel, 5, par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 1, yLevel, 6, par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 1, yLevel, 7, par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(par1World, (yLevel <= 1) ? GCBlocks.basicBlock : Blocks.air, 4, 1, yLevel, 8, par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 1, yLevel, 9, par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 1, yLevel, 10, par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 1, yLevel, 11, par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 2, yLevel, 12, par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 2, yLevel, 13, par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 3, yLevel, 14, par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 4, yLevel, 14, par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 5, yLevel, 15, par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 6, yLevel, 15, par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 7, yLevel, 15, par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(par1World, (yLevel <= 1) ? GCBlocks.basicBlock : Blocks.air, 4, 8, yLevel, 15, par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 9, yLevel, 15, par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 10, yLevel, 15, par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 11, yLevel, 15, par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 12, yLevel, 14, par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 13, yLevel, 14, par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 14, yLevel, 13, par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 14, yLevel, 12, par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 15, yLevel, 11, par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 15, yLevel, 10, par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 15, yLevel, 9, par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(par1World, (yLevel <= 1) ? GCBlocks.basicBlock : Blocks.air, 4, 15, yLevel, 8, par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 15, yLevel, 7, par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 15, yLevel, 6, par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 15, yLevel, 5, par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 14, yLevel, 4, par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 14, yLevel, 3, par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 13, yLevel, 2, par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 12, yLevel, 2, par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 11, yLevel, 1, par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 10, yLevel, 1, par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 9, yLevel, 1, par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(par1World, (yLevel <= 1) ? GCBlocks.basicBlock : Blocks.air, 4, 8, yLevel, 1, par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 7, yLevel, 1, par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 6, yLevel, 1, par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 5, yLevel, 1, par3StructureBoundingBox);
        }
        yLevel = 4;
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 4, yLevel, 2, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 3, yLevel, 3, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 2, yLevel, 4, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 1, yLevel, 5, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 1, yLevel, 6, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 1, yLevel, 7, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 1, yLevel, 8, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 1, yLevel, 9, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 1, yLevel, 10, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 1, yLevel, 11, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 2, yLevel, 12, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 3, yLevel, 13, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 4, yLevel, 14, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 5, yLevel, 15, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 6, yLevel, 15, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 7, yLevel, 15, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 8, yLevel, 15, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 9, yLevel, 15, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 10, yLevel, 15, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 11, yLevel, 15, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 12, yLevel, 14, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 13, yLevel, 13, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 14, yLevel, 12, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 15, yLevel, 11, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 15, yLevel, 10, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 15, yLevel, 9, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 15, yLevel, 8, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 15, yLevel, 7, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 15, yLevel, 6, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 15, yLevel, 5, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 14, yLevel, 4, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 13, yLevel, 3, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 12, yLevel, 2, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 11, yLevel, 1, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 10, yLevel, 1, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 9, yLevel, 1, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 8, yLevel, 1, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 7, yLevel, 1, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 6, yLevel, 1, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 5, yLevel, 1, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.glowstoneTorch, 0, 8, yLevel, 2, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.glowstoneTorch, 0, 14, yLevel, 8, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.glowstoneTorch, 0, 8, yLevel, 14, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.glowstoneTorch, 0, 2, yLevel, 8, par3StructureBoundingBox);
        yLevel = 5;
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 5, yLevel, 2, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 4, yLevel, 2, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 3, yLevel, 3, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 2, yLevel, 4, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 2, yLevel, 5, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 1, yLevel, 6, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 1, yLevel, 7, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 1, yLevel, 8, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 1, yLevel, 9, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 1, yLevel, 10, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 2, yLevel, 11, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 2, yLevel, 12, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 3, yLevel, 13, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 4, yLevel, 14, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 5, yLevel, 14, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 6, yLevel, 15, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 7, yLevel, 15, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 8, yLevel, 15, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 9, yLevel, 15, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 10, yLevel, 15, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 11, yLevel, 14, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 12, yLevel, 14, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 13, yLevel, 13, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 14, yLevel, 12, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 14, yLevel, 11, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 15, yLevel, 10, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 15, yLevel, 9, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 15, yLevel, 8, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 15, yLevel, 7, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 15, yLevel, 6, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 14, yLevel, 5, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 14, yLevel, 4, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 13, yLevel, 3, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 12, yLevel, 2, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 11, yLevel, 2, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 10, yLevel, 1, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 9, yLevel, 1, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 8, yLevel, 1, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 7, yLevel, 1, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 6, yLevel, 1, par3StructureBoundingBox);
        yLevel = 6;
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 4, yLevel, 3, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 3, yLevel, 4, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 2, yLevel, 5, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 2, yLevel, 6, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 2, yLevel, 7, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 2, yLevel, 8, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 2, yLevel, 9, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 2, yLevel, 10, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 2, yLevel, 11, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 3, yLevel, 12, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 4, yLevel, 13, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 5, yLevel, 14, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 6, yLevel, 14, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 7, yLevel, 14, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 8, yLevel, 14, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 9, yLevel, 14, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 10, yLevel, 14, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 11, yLevel, 14, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 12, yLevel, 13, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 13, yLevel, 12, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 14, yLevel, 11, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 14, yLevel, 10, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 14, yLevel, 9, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 14, yLevel, 8, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 14, yLevel, 7, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 14, yLevel, 6, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 14, yLevel, 5, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 13, yLevel, 4, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 12, yLevel, 3, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 11, yLevel, 2, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 10, yLevel, 2, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 9, yLevel, 2, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 8, yLevel, 2, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 7, yLevel, 2, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 6, yLevel, 2, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 5, yLevel, 2, par3StructureBoundingBox);
        yLevel = 7;
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 6, yLevel, 3, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 5, yLevel, 3, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 4, yLevel, 4, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 3, yLevel, 5, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 3, yLevel, 6, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 2, yLevel, 7, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 2, yLevel, 8, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 2, yLevel, 9, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 3, yLevel, 10, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 3, yLevel, 11, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 4, yLevel, 12, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 5, yLevel, 13, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 6, yLevel, 13, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 7, yLevel, 14, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 8, yLevel, 14, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 9, yLevel, 14, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 10, yLevel, 13, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 11, yLevel, 13, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 12, yLevel, 12, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 13, yLevel, 11, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 13, yLevel, 10, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 14, yLevel, 9, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 14, yLevel, 8, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 14, yLevel, 7, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 13, yLevel, 6, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 13, yLevel, 5, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 12, yLevel, 4, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 11, yLevel, 3, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 10, yLevel, 3, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 9, yLevel, 2, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 8, yLevel, 2, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 7, yLevel, 2, par3StructureBoundingBox);
        yLevel = 8;
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 6, yLevel, 4, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 5, yLevel, 4, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 4, yLevel, 5, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 4, yLevel, 6, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 3, yLevel, 7, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 3, yLevel, 8, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 3, yLevel, 9, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 4, yLevel, 10, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 4, yLevel, 11, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 5, yLevel, 12, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 6, yLevel, 12, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 7, yLevel, 13, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 8, yLevel, 13, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 9, yLevel, 13, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 10, yLevel, 12, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 11, yLevel, 12, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 12, yLevel, 11, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 12, yLevel, 10, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 13, yLevel, 9, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 13, yLevel, 8, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 13, yLevel, 7, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 12, yLevel, 6, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 12, yLevel, 5, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 11, yLevel, 4, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 10, yLevel, 4, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 9, yLevel, 3, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 8, yLevel, 3, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 7, yLevel, 3, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 5, yLevel, 5, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 5, yLevel, 11, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 11, yLevel, 11, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 11, yLevel, 5, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 4, yLevel, 7, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 4, yLevel, 8, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 4, yLevel, 9, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 7, yLevel, 12, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 8, yLevel, 12, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 9, yLevel, 12, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 9, yLevel, 4, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 8, yLevel, 4, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 7, yLevel, 4, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 12, yLevel, 7, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 12, yLevel, 8, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, 12, yLevel, 9, par3StructureBoundingBox);
        yLevel = 9;
        for (int k = 5; k <= 11; ++k) {
            for (int l = 5; l <= 11; ++l) {
                if ((l != 5 || k != 5) && (l != 5 || k != 11) && (l != 11 || k != 5) && (l != 11 || k != 11)) {
                    if (k >= 7 && k <= 9 && l >= 7 && l <= 9) {
                        this.placeBlockAtCurrentPosition(par1World, Blocks.glass, 0, k, yLevel, l, par3StructureBoundingBox);
                    }
                    else {
                        this.placeBlockAtCurrentPosition(par1World, GCBlocks.basicBlock, 4, k, yLevel, l, par3StructureBoundingBox);
                    }
                }
            }
        }
        this.spawnVillagers(par1World, par3StructureBoundingBox, 6, 5, 6, 4);
        return true;
    }
}
